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
 * <BR/> Только для Секций - т.к. вставить в Книгу другую Книгу или Секцию нельзя!!!
 * <BR/>
 * <BR/> todo При переносе  - Прописать полный алгоритм
 * 1) Проверить выбранный обьект - должен быть один и должен быть Секцией.
 * 1) перенести старый файл в новую директорию
 * 2) в Новом обьекте изменить путь его файла
 * 3) еще что-то...
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 07.11.2020 18:06:04
 */
public class PasteProjectNodeInFunction extends PasteBookFunction
{
    public PasteProjectNodeInFunction()
    {
        setId ( FunctionId.PASTE_PROJECT_ELEMENT_IN );
        setName ( "Вставить в ..." );
        setIconFileName ( "paste.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        TreePanel<Project>          currentProjectContentPanel;
        int                         ic;
        Section selectSection;
        TreeObj[]                   selectNodes;
        TreeObj selectNode;
        DefaultMutableTreeNode[]    newNodes;
        JLabel                      label;

        // Взять обьект из буфера
        newNodes    = parseBuffer();

        // Взять отмеченный
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

        selectSection    = (Section) selectNode.getUserObject();
        Log.l.info("selectSection = " + selectSection);

        for ( int i=0; i<newNodes.length; i++ ) {
            Log.l.info(" - " + i + ": new node = " + newNodes[i].getUserObject());
        }

        // Проверить, может выбранный узел (сам или в составе вышестоящего узла) уже открыт
        // -- Это лишнее

        // Стартовый диалог
        label       = createLabel ( selectNode, newNodes, "в" );
        ic          = DialogTools.showConfirmDialog ( Par.GM.getFrame(), "Вставить " + newNodes.length + " в", label );

        if ( ic != 0 )  return;  // Отказ от вставки

        // Переместить файл книги в новую директорию.
        // - BookContent fileName - абс имя, заносится при октрытии книги
        // - BookTitle  fileName - просто имя - вычисляется от Сборника (Секции?)

        // Взять текущий проект - TreePanel - т.е. куда переносим.
        currentProjectContentPanel = Par.GM.getFrame().getCurrentProjectPanel();
        Project project = currentProjectContentPanel.getObject();

        // 0) Вычисляем полный путь нового места
        //String fileFullPath = FileTools.createFullFileName ( project, selectNode, null );
        //Log.l.info("fileFullPath = '%s'", fileFullPath);
        // fileFullPath = '/home/svj/Serg/stories/SvjStores/test/import_doc'


        // В переносимом обьекте изменить путь до файла - НЕТ, там только имена

        // ---------------- Изменения в дереве -------------

        Object node;
        String oldFullPath, newFullPath;
        Project oldProject = null;
        BookTitle bookTitle;
        Section section;
        int i = 0;

        // - для всех newNodes
        for (DefaultMutableTreeNode newNode : newNodes)
        {
            node = newNode.getUserObject();
            Log.l.info("move object = '%s'", node);

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
            // - Для книги - полный путь до файла книги. Для Секции - до диреткории секции (включительно)
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


            // todo Вставить в деревья
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
            currentProjectContentPanel.insertNode ( newNodes[i], selectNode, 0 );
            //*/

            i++;
        }

        // todo Отметить что было изменение  - лишнее, т.к. нечего уже сохранять - Или дерево Сборника ???

        //currentProjectContentPanel.setEdit ( true );
        //currentProjectContentPanel.getObject().setEdit ( true ); // BookContent - т.к. через него флаг рисуется.

        // OK. чистим буффер
        Buffer.clear();
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
