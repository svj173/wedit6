package svj.wedit.v6.obj.book;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;

import java.io.OutputStream;


/**
 * Текстовый обьект отображения картинки.
 * <BR/> Имя файла хранится в текстовм поле.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 31.05.2013 15:50:12
 */
public class ImgTextObject extends TextObject
{
    public ImgTextObject ( String fileName )
    {
        super();
        setText ( fileName );
    }

    public ImgTextObject clone ()
    {
        ImgTextObject result;

        result  = new ImgTextObject ( getText() );
        result.setStyle ( getStyle() );

        return result;
    }

    @Override
    public void toXml ( int level, OutputStream out ) throws WEditException
    {
        try
        {
            outTag ( level+1, "img", getText(), out );
        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML тега IMG в поток :\n", e );
        }
    }

    public TextObjectType getType()
    {
        return TextObjectType.IMG;
    }

}
