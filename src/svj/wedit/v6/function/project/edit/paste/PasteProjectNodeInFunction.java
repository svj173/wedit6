package svj.wedit.v6.function.project.edit.paste;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.book.edit.paste.PasteBookFunction;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.*;
import svj.wedit.v6.obj.book.BookTitle;
import svj.wedit.v6.tools.DialogTools;
import svj.wedit.v6.tools.FileTools;
import svj.wedit.v6.tools.ProjectTools;
import svj.wedit.v6.util.Buffer;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import java.awt.event.ActionEvent;


/**
 * Вставить обьект из буфера внутрь отмеченного обьекта (части) первым.
 * <BR/> Допустимость использования корневого элемента: ДА.
 * <BR/>
 * <BR/> Выбрать можно только Секцию - т.к. вставить в Книгу другую Книгу или Секцию нельзя!!!
 * <BR/>
 * <BR/> todo При переносе  - Прописать полный алгоритм
 * 1) Проверить выбранный обьект - должен быть один и должен быть Секцией.
 * 1) перенести старый файл в новую директорию
 * 2) в Новом обьекте изменить путь его файла
 * 3) еще что-то...
 * <BR/>
 * <BR/> todo Проверки:
 * <BR/> 1) Вырезка толкьо книги (книг)
 * <BR/> 2) Вырезка сектора (секторов), содержащих в себе сектора, книги.
 * <BR/> 3) Вставка всего этого в и после (Сектор)
 * <BR/> 4) Вставка всего этого в книгу
 * <BR/>
 * <BR/> todo С возможностью отката операции
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 07.11.2020 18:06:04
 */
@Deprecated
public class PasteProjectNodeInFunction extends PasteBookFunction
{
    public PasteProjectNodeInFunction()
    {
        setId ( FunctionId.PASTE_PROJECT_ELEMENT_IN );
        setName ( "Вставить Секцию или Книгу в ..." );
        setIconFileName ( "paste.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        TreePanel<Project>          currentProjectContentPanel;
        int                         ic;
        Section selectSection;
        TreeObj selectNode;
        DefaultMutableTreeNode[]    newNodes;
        JLabel                      label;

        // 1) Взять обьект из буфера
        newNodes    = parseBuffer();
        for ( int i=0; i<newNodes.length; i++ ) {
            Log.l.info(" - " + i + ": new node = " + newNodes[i].getUserObject());
        }

        // 2) Взять отмеченный обьект. Проверить на кол-во - можно только один (пока что). И что это - Секция
        selectNode = getSelectNode();

        // 3) Валидация
        // - Для вырезанной книги - выбрна может быть толкьо секция
        // - Для


        // Проверить, может выбранный узел (сам или в составе вышестоящего узла) уже открыт
        // -- Это лишнее

        // Диалог запроса на разрешение переноса
        label       = createLabel ( selectNode, newNodes, "в" );
        ic          = DialogTools.showConfirmDialog ( Par.GM.getFrame(), "Вставить " + newNodes.length + " в", label );

        if ( ic != 0 )  return;  // Отказ от вставки

        // Переместить файл книги в новую директорию.
        // - BookContent fileName - абс имя, заносится при создании книги
        // - BookTitle  fileName - просто имя - вычисляется от Сборника (Секции?)

        // Взять текущий проект - TreePanel - т.е. куда переносим.
        currentProjectContentPanel = Par.GM.getFrame().getCurrentProjectPanel();
        Project project = currentProjectContentPanel.getObject();


        // В переносимом обьекте изменить путь до файла - НЕТ, там только имена

        // ---------------- Изменения в дереве -------------

        Object node;
        Project oldProject = null;
        int i = 0;

        // - для всех newNodes
        for (DefaultMutableTreeNode newNode : newNodes)
        {
            node = newNode.getUserObject();
            Log.l.info("move object = '%s'", node);

            // - Переместить файлы
            moveFiles (oldProject, node, project, selectNode);


            // - Вставить обьекты в ГУИ деревья (из исходных деревьев было уже удалено при Cut)
            addToTree (newNode, selectNode, node, currentProjectContentPanel);
        }

        // Отметить что было изменение  - лишнее, т.к. нечего уже сохранять - Или дерево Сборника ???
        currentProjectContentPanel.setEdit ( true );
        //currentProjectContentPanel.getObject().setEdit ( true ); // BookContent - т.к. через него флаг рисуется.

        // Все OK. чистим буффер - т.к. там всего один обьект
        Buffer.clear();
    }

    private void addToTree(DefaultMutableTreeNode newNode, TreeObj selectNode, Object node,
                           TreePanel<Project> currentProjectContentPanel)
    {
        Section selectSection    = (Section) selectNode.getUserObject();
        Log.l.info("selectSection = " + selectSection);

        //*
        if ( node instanceof BookTitle)
        {
            // Вставить Книгу в основное дерево
            selectSection.addBook ( 0, (BookTitle) node );
        }
        else if ( node instanceof Section)
        {
            // Вставить Секцию в основное дерево
           selectSection.addSection ( 0, (Section) node );
        }
        // Вставить в ГУИ-дерево Сборника
        currentProjectContentPanel.insertNode ( newNode, selectNode, 0 );
    }

    private void moveFiles(Project oldProject, Object node, Project project, TreeObj selectNode) throws WEditException
    {
        String oldFullPath, newFullPath;
        BookTitle bookTitle;
        Section section;

        // 0) Берем старый проект - т.к. можем переносить между Проектами.
        // - может, в диалоге взять Сборник переносимых обьектов и сохранить его?
        if (oldProject == null) {
            if (node instanceof BookTitle) {
                bookTitle = (BookTitle) node;
                oldProject = bookTitle.getProject();
            } else if (node instanceof Section) {
                section = (Section) node;
                oldProject = section.getProject();
            }
        }
        Log.l.info("oldProject = '%s'", oldProject);

        // 1) вычисляем старые полные пути до их файлов.
        // - Для книги - полный путь до файла книги. Для Секции - до директории секции (включительно)
        oldFullPath = FileTools.createFullFileName(oldProject, (WTreeObj) node);
        Log.l.info("oldFullPath = '%s'", oldFullPath);

        // 2) вычисляем новые полные пути для них.
        newFullPath = FileTools.createFullFileName(project, selectNode, (WTreeObj) node);
        Log.l.info("newFullPath = '%s'", newFullPath);

        // 3) перемещаем эти файлы на новое место - Отладка (секторы со вложениями и книги)
        // - Для сектора - директорию сектора со всеми файлами
        // - Для книги - только ее файл
        // - есть File.renameTo - но не для всех платформ работает. Сделать жесткое копирвоание а потмо удаление.
        FileTools.moveFile(oldFullPath, newFullPath);
    }

    private TreeObj getSelectNode() throws WEditException {
        TreeObj[]                   selectNodes;
        TreeObj selectNode;

        selectNodes  = ProjectTools.getSelectedNodesForCut(false);
        if (selectNodes == null)
            throw new WEditException("Ничего не выбрано.");

        switch (selectNodes.length) {
            case 0:
                throw new WEditException("Ничего не выбрано.");

            case 1:
                selectNode = selectNodes[0];
                break;

            default: // больше 1
                throw new WEditException("Выбрано больше одного обьекта: " + selectNodes.length );
        }

        Object obj = selectNode.getUserObject();

        if ( ! (obj instanceof Section) )  {
            throw new WEditException("Выбранный обьект не является Секцией." );
        }

        return selectNode;
    }

    @Override
    protected String getChildSize(DefaultMutableTreeNode node) {
        return "-";
    }

    @Override
    protected int getSize(DefaultMutableTreeNode node) {
        int result = -1;

        if ( node == null )  return result;

        if ( node.getUserObject() instanceof Section )
        {
            Section section = (Section) node.getUserObject();
            result = section.getSize();
        }

        return result;
    }

    @Override
    protected String getName(DefaultMutableTreeNode node) {
        String result = "???";

        if ( node == null )  return result;

        if ( node.getUserObject() instanceof Section )
        {
            Section section = (Section) node.getUserObject();
            result = section.getName();
        }

        return result;
    }

}
