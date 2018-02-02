package svj.wedit.v6.obj.function;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.obj.book.BookContent;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 26.10.2012 18:38:57
 */
public abstract class SimpleBookFunction extends Function
{
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
        return null;
    }

    public FunctionParameter getParameterFromBook ( String paramName ) throws WEditException
    {
        FunctionParameter result;
        BookContent bookContent;

        // Взять текущую книгу
        bookContent = Par.GM.getFrame().getCurrentBookContent();
        if ( bookContent == null )  throw new  WEditException ( "Не задана текущая книга." );

        // Взять параметр по имение + ID функции
        result  = bookContent.getBookParams().getParam ( getId(), paramName );
        return result;
    }

    public void setParameterToBook ( String paramName, FunctionParameter parameter )
    {
        BookContent bookContent;

        // Взять текущую книгу
        bookContent = Par.GM.getFrame().getCurrentBookContent();
        if ( bookContent != null )
        {
            bookContent.getBookParams().setParam ( getId(), paramName, parameter );
        }
    }
 
}
