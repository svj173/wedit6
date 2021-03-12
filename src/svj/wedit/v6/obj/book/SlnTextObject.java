package svj.wedit.v6.obj.book;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;

import java.io.OutputStream;


/**
 * Текстовый обьект c переносом строки в конце.
 * <BR/> Начинается он также с красной строки. Т.е. это обьект - абзац.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 25.01.2013 10:40:12
 */
public class SlnTextObject extends TextObject
{
    public SlnTextObject ( String text )
    {
        super( text );
    }

    protected String validateText ( String str )
    {
        return str;
    }

    public SlnTextObject clone ()
    {
        SlnTextObject result;

        result  = new SlnTextObject ( getText() );
        result.setStyle ( getStyle() );

        return result;
    }

    @Override
    public void toXml ( int level, OutputStream out ) throws WEditException
    {
        int    ic;
        String tagName, text;

        try
        {
            tagName = "sln";

            ic      = level + 1;
            // убираем перевод строки в конце текста
            //text    = getText().trim();  -- здесь и пробелы зацепим, которые вполне могут быть - разделители от других текстов.
            text    = getText().replace ("\n","");

            //Log.file.debug ( "--- style = '", getStyle(), "'" );
            if ( hasStyle() )
            {
                outFirstTag ( ic, tagName, "style", getStyle().toString(), out );
                outText ( text, out );
                endTag ( tagName, out );
            }
            else
            {
                outTag ( ic, tagName, text, out );
            }
        } catch ( WEditException we )        {
            throw we;
        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML представления Структуры книги в поток :\n", e );
        }
    }

    public TextObjectType getType()
    {
        return TextObjectType.SLN;
    }

}
