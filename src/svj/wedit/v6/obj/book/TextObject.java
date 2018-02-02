package svj.wedit.v6.obj.book;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.XmlAvailable;
import svj.wedit.v6.obj.book.element.StyleType;

import javax.swing.text.AttributeSet;
import java.io.OutputStream;


/**
 * Описывает кусок текста книги. Содержит собственно текст и стиль,
 *  наложенный на этот текст. Если стиль равен null - значит используется
 *  стиль для текста по умолчанию. Данный обьект может содержать всего
 *  один символ - перевод строки.
 * <BR/> Т.е. это однотипный текст - либо до перевода строки, либо до текста другого типа (стиля),
 * либо сам перевод строки, который всегда один (как обьект).
 * Текста с переводом строки быть НЕ может.
 * <BR/>
 * <BR/>
 <str>7ч</str>
 <eol/>
 <str style="color=red">разгадка отношений – почему Толстяк толкьо после Сергея - Кэрол - в 7-й части.</str>
 <eol/>

 * <BR/>
 * <BR/> User: Zhiganov
 * <BR/> Date: 04.10.2007
 * <BR/> Time: 16:41:05
 */
public class TextObject   extends XmlAvailable
{
    private String          text;
    private AttributeSet    style;


    public TextObject ()
    {
        this ( null );
    }

    public TextObject ( String text )
    {
        style       = null;
        setText ( text );
    }

    public TextObject ( String text, AttributeSet style )
    {
        this.text  = text;
        this.style = style;
    }

    public TextObject clone ()
    {
        TextObject result;

        //Log.file.debug ( "-- text type = ", this.getClass().getSimpleName() );
        //Log.file.debug ( "---- text before clone = ", getText() );
        result  = new TextObject ( getText(), getStyle() );
        //result.setStyle ( getStyle() );
        //Log.file.debug ( "---- text after clone = ", result.getText() );

        return result;
    }

    @Override
    public void toXml ( int level, OutputStream out ) throws WEditException
    {
        int    ic;
        String tagName;
        WEditStyle wStyle;

        try
        {
            tagName = "str";

            ic  = level + 1;

            //Log.file.debug ( "--- style = '", getStyle(), "'" );
            if ( hasStyle() )
            {
                // Есть свой стиль, отличный от текстового. Поменить как color_text
                wStyle  = new WEditStyle ( getStyle(), StyleType.COLOR_TEXT );
                outFirstTag ( ic, tagName, "style", wStyle.toXml(), out );
                outText ( getText(), out );
                endTag ( tagName, out );
            }
            else
            {
                outTag ( ic, tagName, getText(), out );
            }
        //} catch ( WEditException we )        {
        //    throw we;
        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML представления Структуры книги в поток :\n", e );
        }
    }

    @Override
    public int getSize ()
    {
        if ( text == null )
            return 0;
        else
            return text.length();
    }

    public boolean hasStyle ()
    {
        AttributeSet style;
        String str;
        boolean result;

        result  = false;
        style = getStyle();
        if (style != null)
        {
            str = style.toString();
            // если это не только одни скобки типа '{}'.
            if ( str.length() > 3 )  result = true;
        }
        return  result;
    }

    /*
    public int getTextSize ()
    {
        return text.length();
    }
    */

    public String getText ()
    {
        return text;
    }

    /* Заносится из парсера файла и из парсера текстового редактора. */
    public void setText ( String str )
    {
        text = validateText ( str );
    }

    /* Из текстового редактора - преобразование в обьект. Чтобы не терялись пробелы текста. */
    public void setTextWoValidate ( String str )
    {
        text = str;
    }

    protected String validateText ( String str )
    {
        // Удаляем крайние пробелы и крайние символы перевода строки - Для простого текста и чистого перевода строки. Для SLN - ничего не делаем
        // - если текст - только перевод строки то по trim эти символы уничтожаются и остается пустая строка.
        // todo пока убрал, т.к. пробелы пропадают - 2013-09-11 - надо разнести создание из xml-файла и создание из текстового редактора - либо вообще никогда не тримить.
        //if ( (str != null) && ( ! str.equals ( WCons.NEW_LINE )) )  str   = str.trim();
        return str;
    }

    public AttributeSet getStyle ()
    {
        return style;
    }

    public void setStyle ( AttributeSet style )
    {
        this.style = style;
    }

    public String toString()
    {
        StringBuilder result;
        result  = new StringBuilder ( 512 );
        result.append ( "text = '" );
        result.append ( text );
        result.append ( "', style = " );
        result.append ( style );
        return result.toString ();
    }

    /**
     * Добавить текст в конец имеющемуся.
     * @param str  Текст (может быть и перевод строки)
     */
    public void addText ( String str )
    {
        if ( text == null ) text    = str;
        else    text    = text + str;
    }

    /**
     * Удалить лидирующие пробелы. Но переносы строк оставить.
     */
    public void trim ()
    {
        if ( text != null )
        {
            int ic;
            // Запоминаем кол-во переносов строк - в конце строки.
            ic      = text.indexOf ( '\n' );
            text    = text.trim();
            if ( ic >= 0 )  text = text + '\n';
        }
    }

    public int replace ( String fromStr, String toStr )
    {
        int ic = 0;
        if ( text != null )
        {
            // todo как вести подсчет кол-ву изменений - простым способом без цикла поиска в строке?
            text = text.replace ( fromStr, toStr );
            //text.replace ( fromStr, toStr );
        }
        return ic;
    }

}
