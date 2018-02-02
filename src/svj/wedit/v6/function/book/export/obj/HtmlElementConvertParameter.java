package svj.wedit.v6.function.book.export.obj;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.params.ParameterType;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.tools.Convert;

import java.awt.*;
import java.io.OutputStream;

/**
 * Параметр хранения настроек конвертации книги в формат HTML.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 11.10.2013 16:24
 */
public class HtmlElementConvertParameter extends  ElementConvertParameter
{
    public HtmlElementConvertParameter clone ()
    {
        HtmlElementConvertParameter result;

        result = new HtmlElementConvertParameter ();
        result.setName ( getName() );

        super.mergeToOther ( result );
        //mergeToOther ( result );

        return result;
    }

    @Override
    public void toXml ( int level, OutputStream out ) throws WEditException
    {
        int     ic1, ic2;
        String  str;
        Font    font;
        Color   color;
        TitleViewMode formatType;

        try
        {
            ic1 = level+1;
            ic2 = ic1+1;

            outString ( level, Convert.concatObj ( "<param name=\"", getName(), "\" type=\"", ParameterType.ELEMENT_CONVERT, "\">\n" ), out );

            // - level
            outTag ( ic1, "level", Integer.toString ( getLevel() ), out );

            // - font - текстовое предсатвление : Monospaced-bold-14
            font  = getFont();
            Log.file.debug ( "Element level = %s; font = %s", getLevel(), font );
            if ( font != null )  outTag ( ic1, "font", Convert.font2str ( font ), out );

            // - color
            color  = getColor();
            Log.file.debug ( "Element level = %s; color = %s", getLevel(), color );
            if ( color != null )  outTag ( ic1, "color", Convert.color2str ( color ), out );

            // - format
            formatType  = getFormatType();
            Log.file.debug ( "Element level = %s; format = %s", getLevel(), formatType );
            if ( formatType != null )  outTag ( ic1, "format", formatType.getNumber(), out );


            outString ( level, "</param>\n", out );

        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML представления Параметра '", getName(), "' в поток :\n", e );
        }
    }

    @Override
    public Object getValue ()
    {
        return null;
    }


}
