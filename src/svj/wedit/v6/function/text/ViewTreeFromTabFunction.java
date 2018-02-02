package svj.wedit.v6.function.text;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.function.Function;

import java.awt.event.ActionEvent;


/**
 * Показать на дереве элементов книги текущий элемент редактирования.
 * <BR/> Т.е. какая текстовая панель текущая - тот главный элемент и показывается в дереве.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 11.12.2013 15:07:41
 */
public class ViewTreeFromTabFunction extends Function
{
    public ViewTreeFromTabFunction ()
    {
        setId ( FunctionId.VIEW_FROM_SOURCE );
        setName ( "Показать элемент книги в дереве Содержания." );
        setIconFileName ( "view_from_source.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        TextPanel textPanel;
        BookNode  bookNode;
        TreePanel<BookContent> bookTreePanel;

        Log.l.debug ( "Start. event = %s", event );

        textPanel           = Par.GM.getFrame().getCurrentTextPanel();

        if ( textPanel != null )
        {
            // взять обьект
            bookNode        = textPanel.getBookNode();
            // отобразить его в дереве
            bookTreePanel   = Par.GM.getFrame().getCurrentBookContentPanel();
            bookTreePanel.selectNode ( bookNode.getId() );
        }

        Log.l.debug ( "Finish" );
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
        return "Показать в дереве элементов текущий редактируемый элемент книги.";
    }

}
