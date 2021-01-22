package svj.wedit.v6.function.project.edit.paste;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.book.edit.paste.PasteBookFunction;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.Section;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.book.BookTitle;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.DialogTools;
import svj.wedit.v6.tools.ProjectTools;
import svj.wedit.v6.util.Buffer;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import java.awt.event.ActionEvent;


/**
 * Вставить обьект из буфера внутрь отмеченного обьекта (части) первым.
 * <BR/> Допустимость использования корневого элемента: ДА.
 * <BR/>
 * <BR/> todo Только для Секций - т.к. вставить в Книгу другую Книгу или Секцию нельзя!!!
 * <BR/>
 * <BR/> todo При переносе  - Прописать полный алгоритм
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
                throw new WEditException("Выбрано больше одного (" + selectNodes.length + ")." );
        }

        Object obj = selectNode.getUserObject();

        if ( ! (obj instanceof Section) )  {
            throw new WEditException("Выбранный обьект не является Секцией." );
        }

        selectSection    = (Section) selectNode.getUserObject();

        // Проверить, может выбранный узел (сам или в составе вышестоящего узла) уже открыт
        // -- Это лишнее

        // Стартовый диалог
        label       = createLabel ( selectNode, newNodes, "в" );
        ic          = DialogTools.showConfirmDialog ( Par.GM.getFrame(), Convert.concatObj ( "Вставить ", newNodes.length, " в" ), label );

        if ( ic != 0 )  return;  // Отказ от вставки

        // todo В переносимом обьекте изменить путь файла

        // Взять текущую книгу - TreePanel
        currentProjectContentPanel = Par.GM.getFrame().getCurrentProjectPanel();

        Object node;
        for ( int i=0; i<newNodes.length; i++ )
        {
            node = newNodes[i].getUserObject();
            if ( node instanceof BookTitle)
            {
                // Вставить Книгу в основное дерево
                selectSection.addBook ( i, (BookTitle) node );
            }
            else if ( node instanceof Section)
            {
                // Вставить Секцию в основное дерево
               selectSection.addSection ( i, (Section) node );
            }
            // Вставить в ГУИ-дерево Сборника
            currentProjectContentPanel.insertNode ( newNodes[i], selectNode, i );
        }

        // Отметить что было изменение
        currentProjectContentPanel.setEdit ( true );
        currentProjectContentPanel.getObject().setEdit ( true ); // BookContent - т.к. через него флаг рисуется.

        // OK. чистим буффер
        Buffer.clear();
    }

}
