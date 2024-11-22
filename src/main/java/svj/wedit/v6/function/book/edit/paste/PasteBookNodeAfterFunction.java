package svj.wedit.v6.function.book.edit.paste;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.tools.BookTools;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.DialogTools;
import svj.wedit.v6.util.Buffer;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;


/**
 * Вставить обьект из буфера после отмеченного обьекта (части).
 * <BR/> Допустимость использования корневого элемента: НЕТ.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 03.02.2012 11:06:04
 */
public class PasteBookNodeAfterFunction extends PasteBookFunction
{
    public PasteBookNodeAfterFunction ()
    {
        setId ( FunctionId.PASTE_ELEMENT_AFTER );
        setName ( "Вставить после..." );
        setIconFileName ( "paste.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        DefaultMutableTreeNode  selectNode, parentObj ;
        TreePanel<BookContent>  currentBookContentPanel;
        int                     ic, inum;
        BookNode                    bookNode, parentNode;
        DefaultMutableTreeNode[]    newNodes;
        JLabel                      label;

        // Взять обьект из буфера
        newNodes    = parseBuffer ();

        // Взять отмеченный
        selectNode  = BookTools.getSelectedNode();

        // Корень НЕ допустим
        BookTools.errorIfRoot ( selectNode );
        
        // Стартовый диалог
        label       = createLabel ( selectNode, newNodes, "после" );
        ic          = DialogTools.showConfirmDialog ( Par.GM.getFrame(), Convert.concatObj ( "Вставить ", newNodes.length, " после" ), label );

        if ( ic != 0 )  return;


        // Взять родителя отмеченного элемента
        //parentNode  = BookTools.getParentNode ( selectNode );
        // Взять родителя отмеченного элемента
        parentObj   = ( TreeObj ) selectNode.getParent();
        parentNode  = (BookNode)  parentObj.getUserObject();

        // Проверить, может выбранный узел (сам или в составе вышестоящего узла) уже открыт в текстах, и тогда добавлять/удалять в него ничего нельзя.
        BookTools.checkOpenText ( parentNode );

        // Взять номер отмеченного у его родителя
        inum        = parentObj.getIndex ( selectNode );
        Log.l.debug ( "selected index by parent = ", inum );

        // Взять текущую книгу - TreePanel
        currentBookContentPanel = Par.GM.getFrame().getCurrentBookContentPanel();

        for ( DefaultMutableTreeNode pasteNode : newNodes )
        {
            // Если уровень обьекта изменился - обработать
            ic          = BookTools.getElementLevel ( selectNode );
            BookTools.treatLevel ( pasteNode, ic );

            bookNode    = ( BookNode ) pasteNode.getUserObject();

            inum++;
            // Добавить - после отмеченного
            parentNode.addBookNode ( inum, bookNode );

            // Добавить в дерево после отмеченного - в самом конце, когда все действия прошли успешно
            //  (создание директории, перезапись project.xml и т.д.)
            currentBookContentPanel.insertNode ( pasteNode, parentObj, inum );
        }

        // Отметить что было изменение
        currentBookContentPanel.setEdit ( true );
        currentBookContentPanel.getObject().setEdit ( true ); // BookContent - т.к. через него флаг рисуется.

        // OK. чистим буффер
        Buffer.clear();
    }

}
