package svj.wedit.v6.function.project.edit.paste;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.book.edit.paste.PasteBookFunction;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
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
 * Вставить обьект из буфера после отмеченного обьекта (части).
 * <BR/> Допустимость использования корневого элемента: НЕТ.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 07.11.2020 18:06:04
 */
public class PasteProjectNodeAfterFunction extends PasteBookFunction
{
    public PasteProjectNodeAfterFunction()
    {
        setId ( FunctionId.PASTE_PROJECT_ELEMENT_AFTER );
        setName ( "Вставить после..." );
        setIconFileName ( "paste.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        DefaultMutableTreeNode  parentObj ;
        TreePanel<Project>  currentProjectContentPanel;
        int                     ic, inum;
        DefaultMutableTreeNode[]    newNodes;
        JLabel                      label;
        TreeObj[]                   selectNodes;
        TreeObj selectNode = null;
        Section   parentNode;

        // Взять обьект из буфера
        newNodes    = parseBuffer();

        // Взять отмеченный - Книгу или Раздел -- Обязательно один, а не много
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


        // Корень НЕ допустим
        BookTools.errorIfRoot ( selectNode );

        // Проверка на Книгу - Нельзя добавлять в книгу
        if ( !(selectNode.getUserObject() instanceof Section))
            throw new WEditException("Должен быть выбран Раздел.");
        
        // Стартовый диалог
        label       = createLabel ( selectNode, newNodes, "после" );
        ic          = DialogTools.showConfirmDialog ( Par.GM.getFrame(), Convert.concatObj ( "Вставить ", newNodes.length, " после" ), label );

        if ( ic != 0 )  return;

        // Взять родителя отмеченного элемента
        //parentNode  = BookTools.getParentNode ( selectNode );
        // Взять родителя отмеченного элемента
        parentObj   = ( TreeObj ) selectNode.getParent();
        parentNode  = (Section)  parentObj.getUserObject();

        // Проверить, может выбранный узел уже открыт -- Это лишнее

        // Взять номер отмеченного у его родителя
        inum        = parentObj.getIndex ( selectNode );
        Log.l.debug ( "selected index by parent = %d", inum );

        // Взять панель текущего Проекта - TreePanel
        currentProjectContentPanel = Par.GM.getFrame().getCurrentProjectPanel();

        for ( DefaultMutableTreeNode pasteNode : newNodes )
        {
            // todo Обьекты для Вставления - Могут быть как Книги так и Разделы

            // todo Добавить в parentNode

            // todo Section -- BookTitle - надо различать их?
            
            bookNode    = ( BookNode ) pasteNode.getUserObject();

            inum++;
            // Добавить - после отмеченного
            parentNode.addBookNode ( inum, bookNode );

            // todo Добавить в дерево после отмеченного - в самом конце, когда все действия прошли успешно
            //  (создание директории, перезапись project.xml и т.д.)
            currentProjectContentPanel.insertNode ( pasteNode, parentObj, inum );
        }

        // todo Отметить что было изменение
        currentProjectContentPanel.setEdit ( true );
        //currentProjectContentPanel.getObject().setEdit ( true );

        // OK. чистим буффер
        Buffer.clear();
    }

}
