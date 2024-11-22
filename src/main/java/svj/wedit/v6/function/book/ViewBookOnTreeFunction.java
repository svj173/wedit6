package svj.wedit.v6.function.book;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.function.Function;

import java.awt.event.ActionEvent;


/**
 * Показать на дереве книг текущую книгу.
 * <BR/> Т.е. какая панель книги текущая - она и показывается в дереве  Сборника.
 * <BR/> Иконка расположена на панели содержания книги.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 11.12.2022 10:07:41
 */
public class ViewBookOnTreeFunction extends Function
{
    public ViewBookOnTreeFunction()
    {
        setId ( FunctionId.VIEW_BOOK_FROM_SOURCE );
        setName ( "Показать книгу в дереве Сборника" );
        setIconFileName ( "view_from_source.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        Log.l.debug ( "Start. event = %s", event );

        BookContent bookContent           = Par.GM.getFrame().getCurrentBookContent();
        Log.l.info ( "bookContent = %s", bookContent );

        if ( bookContent != null )
        {
            // отобразить его в дереве
            Par.GM.getFrame().selectBookOnProject(bookContent.getId(), bookContent.getProject().getId());
            Log.l.info ( "bookId = %s", bookContent.getId() );
            Log.l.info ( "projectId = %s", bookContent.getProject().getId() );
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
