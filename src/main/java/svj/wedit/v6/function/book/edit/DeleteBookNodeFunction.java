package svj.wedit.v6.function.book.edit;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.tools.BookTools;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.DialogTools;
import svj.wedit.v6.util.Buffer;

import javax.swing.*;
import java.awt.event.ActionEvent;


/**
 * Удалить отмеченный обьект или несколько одноуровневых обьектов (функция доступна только из тулбар).
 * <BR/> Допустимость использования корневого элемента: нет.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 03.02.2012 11:06:04
 */
public class DeleteBookNodeFunction extends Function
{
    public DeleteBookNodeFunction ()
    {
        setId ( FunctionId.DELETE_ELEMENT );
        setName ( "Удалить" );
        setIconFileName ( "delete.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        TreeObj[]               selectNodes;
        TreePanel<BookContent>  currentBookContentPanel;
        JLabel                  label;
        int                     ic;
        String                  msg;
        TreeObj                 parentObj;

        // Взять отмеченные
        // Не проверяем на отсутствие выделения, т.к. если не было отмеченных то
        //  еще раньше (в NodeTools) выкинется исключение
        // Там же, в глубине, проверяется отмеченность одноуровневых элементов. И запрет корня.
        selectNodes  = BookTools.getSelectedNodesForCut(false);
        //Log.f.debug ( "selectNodes = ", WDumpTools.printArray ( selectNodes ) );

        // Проверить, может выбранный узел (сам или в составе вышестоящего узла) уже открыт в текстах, и тогда добавлять/удалять в него ничего нельзя.
        BookTools.checkOpenText ( selectNodes );

        // Стартовый диалог
        label   = createLabel ( selectNodes );
        msg     = Convert.concatObj ( "Удалить ", selectNodes.length, " элементов книги" );
        ic      = DialogTools.showConfirmDialog ( Par.GM.getFrame(), msg, label );

        if ( ic != 0 )  return;

        // Взять текущую книгу - TreePanel
        currentBookContentPanel = Par.GM.getFrame().getCurrentBookContentPanel();

        parentObj = null;
        // Удалить в дереве
        for ( TreeObj node : selectNodes )
        {
            Log.f.debug ( "--- removeNode = ", node );
            parentObj   = node.getTreeParent();
            //currentBookContentPanel.removeNode ( node );
            // удаляем и во внутреннем дереве и в gui-дереве.
            BookTools.removeNode ( currentBookContentPanel, node );
        }

        // Выделить соседа или парента -- пока только парента.
        currentBookContentPanel.selectNode ( parentObj, false );

        // Занести в буфер
        Buffer.setBuffer ( selectNodes );

        // Отметить что было изменение
        currentBookContentPanel.setEdit ( true );
        currentBookContentPanel.getObject().setEdit ( true ); // BookContent - т.к. через него флаг рисуется.

        DialogTools.showMessage ( Par.GM.getFrame(), "Удаленные элементы занесены в буфер (для возможности отката).", "Удалены "+selectNodes.length+" элементов книги" );

        Log.f.debug ( "Finish" );
    }

    private JLabel createLabel ( TreeObj[] selectNodes )
    {
        JLabel          result;
        StringBuilder   msg;
        int             ic;
        BookNode        bookNode;

        msg = new StringBuilder();

        msg.append ( "<HTML><body>\n" );
        msg.append ( "<h2>Вы действительно желаете<br/> удалить '" );
        msg.append ( selectNodes.length );
        msg.append ( "' элементов книги ?</h2>" );
        msg.append ( "Уровень : " );
        msg.append ( selectNodes[0].getLevel() );
        msg.append ( "<br/><br/>" );

        msg.append ( "<table width='80%' border='1' cellpadding='0' cellspacing='0'><tr bgcolor='#BBBBBB'><th>&nbsp;N&nbsp;</th><th>&nbsp;Название&nbsp;</th><th>&nbsp;Размер (б)&nbsp;</th><th>&nbsp;Childs&nbsp;</th></tr>\n" );

        ic          = 1;

        for ( TreeObj node : selectNodes )
        {
            bookNode = (BookNode) node.getUserObject();
            msg.append ( "<tr><td align='right'>&nbsp;" );
            msg.append ( ic );
            msg.append ( "&nbsp;</td><td><font color='green'>&nbsp;" );
            msg.append ( bookNode.getName() );
            msg.append ( "&nbsp;</td><td align='right'>&nbsp;" );
            msg.append ( bookNode.getSize() );
            msg.append ( "&nbsp;</td><td align='center'>&nbsp;" );
            msg.append ( bookNode.getChildSize() );
            msg.append ( "&nbsp;</td>" );
            msg.append ( "</tr>" );
            ic++;
        }
        msg.append ( "</table>\n<br/></body></HTML>\n" );

        result   = new JLabel ( msg.toString() );

        return result;
    }

    @Override
    public void rewrite ()
    {
    }

    @Override
    public void init () throws WEditException
    {
    }

    @Override
    public void close ()
    {
    }

    @Override
    public String getToolTipText ()
    {
        return "Удалить все отмеченные элементы книги.";
    }

}
