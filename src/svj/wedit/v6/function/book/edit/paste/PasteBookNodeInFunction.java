package svj.wedit.v6.function.book.edit.paste;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.tree.TreePanel;
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
 * Вставить обьект из буфера внутрь отмеченного обьекта (части) первым.
 * <BR> Если в обьекте из буфера есть подобьекты, уровень которых при изменении ошибочен (т.е. нет элементов
 *  такого уровня, что приведет к потере) - то НЕ ругаться, а запросить создание описания элементов, либо создать их автоматом и сообщить об этом.
 * <BR/> Эта проверка - в BookTools.treatLevel
 * <BR/> Допустимость использования корневого элемента: ДА.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 03.02.2012 11:06:04
 */
public class PasteBookNodeInFunction extends PasteBookFunction
{
    public PasteBookNodeInFunction ()
    {
        setId ( FunctionId.PASTE_ELEMENT_IN );
        setName ( "Вставить в ..." );
        setIconFileName ( "paste.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        DefaultMutableTreeNode      selectNode;
        TreePanel<BookContent>      currentBookContentPanel;
        int                         ic;
        BookNode                    bookNode;
        DefaultMutableTreeNode[]    newNodes;
        JLabel                      label;

        // Взять обьект из буфера
        newNodes    = parseBuffer();

        // Взять отмеченный
        selectNode  = BookTools.getSelectedNode();
        bookNode    = (BookNode) selectNode.getUserObject();

        // Проверить, может выбранный узел (сам или в составе вышестоящего узла) уже открыт в текстах, и тогда добавлять/удалять в него ничего нельзя.
        BookTools.checkOpenText ( bookNode );

        // Стартовый диалог
        label       = createLabel ( selectNode, newNodes, "в" );
        ic          = DialogTools.showConfirmDialog ( Par.GM.getFrame(), Convert.concatObj ( "Вставить ", newNodes.length, " в" ), label );

        if ( ic != 0 )  return;

        // Взять текущую книгу - TreePanel
        currentBookContentPanel = Par.GM.getFrame().getCurrentBookContentPanel();

        for ( int i=0; i<newNodes.length; i++ )
        {
            // Если уровень обьекта изменился - обработать
            ic          = BookTools.getElementLevel ( selectNode );
            BookTools.treatLevel ( newNodes[i], ic+1 );
            // Вставить в дерево
            bookNode.addBookNode ( i, (BookNode) newNodes[i].getUserObject() );
            // Вставить
            currentBookContentPanel.insertNode ( newNodes[i], selectNode, i );
        }

        // Отметить что было изменение
        currentBookContentPanel.setEdit ( true );
        currentBookContentPanel.getObject().setEdit ( true ); // BookContent - т.к. через него флаг рисуется.

        // OK. чистим буффер
        Buffer.clear();
    }

}
