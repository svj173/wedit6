package svj.wedit.v6.function.book.tree;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.gui.widget.ComboBoxWidget;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.WType;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.BookTools;
import svj.wedit.v6.tools.DialogTools;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Map;


/**
 * Групповое редактирование типов Элементов (hidden, work...)
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 06.06.2018 11:46:04
 */
public class GroupEditNodeTypeFunction extends Function
{
    public GroupEditNodeTypeFunction ()
    {
        setId ( FunctionId.EDIT_NODE_TYPE );
        setName ( "Заменить типы элементов" );
        setIconFileName ( "edit.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        TreeObj[]               selectNodes;
        TreePanel<BookContent>  currentBookContentPanel;
        JLabel                  label;
        JPanel                  panel;
        int                     ic;
        BookNode                bookNode;
        String                  type;
        ComboBoxWidget<String>  typeWidget;

        // Взять отмеченные
        // Не проверяем на отсутствие выделения, т.к. если не было отмеченных то
        //  еще раньше (в NodeTools) выкинется исключение
        // Там же, в глубине, проверяется отмеченность одноуровневых элементов. И проверка на корень.
        selectNodes  = BookTools.getSelectedNodesForCut(false);
        //Log.f.debug ( "selectNodes = ", WDumpTools.printArray ( selectNodes ) );

        // Проверить, может выбранный узел (сам или в составе вышестоящего узла) уже открыт в текстах, и тогда добавлять/удалять в него ничего нельзя.
        BookTools.checkOpenText ( selectNodes );

        // Взять текущую книгу - TreePanel
        currentBookContentPanel = Par.GM.getFrame().getCurrentBookContentPanel();

        // диалог
        //label   = createLabel ( selectNodes );
        typeWidget  = createTypeWidget ( currentBookContentPanel.getObject() );
        panel       = creatPanel ( typeWidget );
        // Получаем выбранный тип
        ic      = DialogTools.showConfirmDialog ( Par.GM.getFrame(),
                                                  "Заменить типы у "+ selectNodes.length+ " элементов книги.", panel );

        if ( ic != 0 )  return;     // Отмена

        // Заменяем всем элементам их типы на выбранный.
        //type = "hidden2";
        type = typeWidget.getValue();
        for ( TreeObj to : selectNodes )
        {
            bookNode    = ( BookNode ) to.getUserObject();
            bookNode.setElementType ( type );
        }

        // Отметить что было изменение
        currentBookContentPanel.setEdit ( true );
        currentBookContentPanel.getObject().setEdit ( true ); // BookContent - т.к. через него флаг рисуется.

        Log.f.debug ( "Finish" );

        //throw new WEditException ( "ic = "+ic+"; select = "+ typeWidget.getValue() );
    }

    private ComboBoxWidget<String> createTypeWidget ( BookContent bookContent )
    {
        Map<String, WType>      types;
        ComboBoxWidget<String>  typeWidget;

        // Взять список типов
        types       = bookContent.getBookStructure().getTypes();
        typeWidget  = new ComboBoxWidget<String> ( "Тип", true, "---", types.keySet() );

        return typeWidget;
    }

    private JPanel creatPanel ( ComboBoxWidget<String>  typeWidget )
    {
        JPanel              panel;

        panel = new JPanel();

        panel.add ( typeWidget );

        return panel;
    }

    private JLabel createLabel ( TreeObj[] selectNodes )
    {
        JLabel          result;
        StringBuilder   msg;
        int             ic;
        BookNode        bookNode;

        msg = new StringBuilder();

        msg.append ( "<HTML><body>\n" );
        msg.append ( "<h2>Вы действительно желаете<br/> вырезать '" );
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
        return "Вырезать все отмеченные элементы книги. Сохранить в промежуточном буфере.";
    }

}
