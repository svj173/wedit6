package svj.wedit.v6.function.book.edit;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.dialog.WDialog;
import svj.wedit.v6.obj.book.BookContent;

/**
 * Редактор атрибутов книги.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.04.2014 14:31
 */
public class BookAttributeDialog  extends WDialog<BookContent, Void>
{
    public BookAttributeDialog ( String title )
    {
        super ( title );
    }

    @Override
    public void init ( BookContent initObject ) throws WEditException
    {

    }

    @Override
    public Void getResult () throws WEditException
    {
        return null;
    }
}
