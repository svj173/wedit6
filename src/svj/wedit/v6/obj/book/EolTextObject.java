package svj.wedit.v6.obj.book;


import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;

import java.io.OutputStream;


/**
 * Текстовый обьект единичного переноса строки.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.01.2012 13:50:12
 */
public class EolTextObject  extends TextObject
{
    public EolTextObject ()
    {
        super();
        setText ( WCons.NEW_LINE );
    }

    public EolTextObject clone ()
    {
        EolTextObject result;

        result  = new EolTextObject();
        result.setStyle ( getStyle() );

        return result;
    }

    @Override
    public void toXml ( int level, OutputStream out ) throws WEditException
    {
        try
        {
            outEmptyTag ( level+1, "eol", out );
        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML тега EOL в поток :\n", e );
        }
    }

    public TextObjectType getType()
    {
        return TextObjectType.EOL;
    }

}
