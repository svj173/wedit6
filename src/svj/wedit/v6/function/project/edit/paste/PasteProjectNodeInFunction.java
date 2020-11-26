package svj.wedit.v6.function.project.edit.paste;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.book.edit.paste.PasteBookFunction;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.Section;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.tools.BookTools;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.DialogTools;
import svj.wedit.v6.tools.ProjectTools;
import svj.wedit.v6.util.Buffer;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import java.awt.event.ActionEvent;


/**
 * Вставить обьект из буфера внутрь отмеченного обьекта (части) первым.
 * <BR> Если в обьекте из буфера есть подобьекты, уровень которых при изменении ошибочен (т.е. нет элементов
 *  такого уровня, что приведет к потере) - то НЕ ругаться, а запросить создание описания элементов, либо создать их автоматом и сообщить об этом.
 * <BR/> Эта проверка - в todo BookTools.treatLevel
 * <BR/> Допустимость использования корневого элемента: ДА.
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
                throw new WEditException("Выбрано больше одного.");

            case 1:
                selectNode = selectNodes[0];
                break;

            default: // больше 1
                throw new WEditException("Выбрано больше одного.");
        }

        selectSection    = (Section) selectNode.getUserObject();

        // Проверить, может выбранный узел (сам или в составе вышестоящего узла) уже открыт
        // -- Это лишнее

        // Стартовый диалог
        label       = createLabel ( selectNode, newNodes, "в" );
        ic          = DialogTools.showConfirmDialog ( Par.GM.getFrame(), Convert.concatObj ( "Вставить ", newNodes.length, " в" ), label );

        if ( ic != 0 )  return;

        // Взять текущую книгу - TreePanel
        currentProjectContentPanel = Par.GM.getFrame().getCurrentProjectPanel();

        for ( int i=0; i<newNodes.length; i++ )
        {
            // todo Вставить в дерево
            selectSection.addBookNode ( i, (BookNode) newNodes[i].getUserObject() );
            // todo Вставить в ГУИ-дерево Сборника
            currentProjectContentPanel.insertNode ( newNodes[i], selectNode, i );
        }

        // Отметить что было изменение
        currentProjectContentPanel.setEdit ( true );
        currentProjectContentPanel.getObject().setEdit ( true ); // BookContent - т.к. через него флаг рисуется.

        // OK. чистим буффер
        Buffer.clear();
    }

}
