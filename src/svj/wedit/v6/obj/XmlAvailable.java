package svj.wedit.v6.obj;


import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.tools.Convert;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 05.07.2011 15:23:01
 */
public abstract class XmlAvailable
{
    /* Преобразовать в XML для сохранения. И скинуть в поток.  */
    public abstract void toXml ( int level, OutputStream out )  throws WEditException;

    /* Выдать 'чистый' (без учета тегов и стилей) размер элемента xml. */
    public abstract int getSize();
    //public abstract void getFromXml ( InputStream in ) throws WEditException;


    // public - для записи в поток и другими средствами.  -- Пишем чего-то без всякой валидации
    public void outString ( String text, OutputStream out ) throws IOException
    {
        //if ( text != null )
        out.write ( text.getBytes(WCons.CODE_PAGE) );    // todo Charset charset -- Проверить в Windows
    }

    // В конец поставить перевод строки
    public void outLine ( String text, OutputStream out ) throws IOException
    {
        //if ( text != null )
        out.write ( text.getBytes(WCons.CODE_PAGE) );
        out.write ( '\n' );
    }

    protected void outString ( int tabMargin, String text, OutputStream out ) throws IOException
    {
        String tabs;

        tabs    = createTabs ( tabMargin );
        outString ( Convert.concatObj ( tabs, text ), out );
    }

    /* Записать текст. Здесь валидируем символы - <&>. */
    public void outText ( Object text, OutputStream out ) throws IOException
    {
        String str;
        if ( text != null )
        {
            str = Convert.validateXml ( text.toString() );
            out.write ( str.getBytes(WCons.CODE_PAGE) );
        }
    }

    public void outText ( int tabMargin, String text, OutputStream out ) throws IOException
    {
        String str;
        String tabs;

        tabs    = createTabs ( tabMargin );
        if ( text != null )
        {
            str = Convert.concatObj ( tabs, Convert.validateXml ( text ) );
            out.write ( str.getBytes(WCons.CODE_PAGE) );
        }
    }

    protected void endTag ( String tag, OutputStream out ) throws IOException
    {
        outString ( Convert.concatObj ( "</", tag, ">\n" ), out );
    }

    protected void endTag ( int tabMargin, String tag, OutputStream out ) throws IOException
    {
        String tabs;

        tabs    = createTabs ( tabMargin );
        outString ( Convert.concatObj ( tabs, "</", tag, ">\n" ), out );
    }

    /* Записать полный тег - с началом и концом. Без атрибутов тега. */
    protected void outTag ( int tabMargin, String tag, Object text, OutputStream out ) throws WEditException
    {
        String tabs;

        try
        {
            tabs    = createTabs ( tabMargin );
            outString ( Convert.concatObj ( tabs, '<', tag, ">" ), out );
            outText ( text, out );
            outString ( Convert.concatObj ( "</", tag, ">\n" ), out );
        } catch ( Exception e )        {
            Log.file.error ( Convert.concatObj ( "err: tag = '", tag, "'; text = ", text ), e);
            throw new WEditException ( e, "Ошибка записи тега '", tag, "'; text = ", text );
        }
    }

    protected void outEmptyTag ( int tabMargin, String tag, OutputStream out ) throws IOException
    {
        String tabs;

        tabs    = createTabs ( tabMargin );
        outString ( Convert.concatObj ( tabs, '<', tag, "/>\n" ), out );
    }

    /* Начало и конец тега. С атрибутом name и с содержимым внутри тега. */
    protected void outTag ( int tabMargin, String tag, String name, String text, OutputStream out ) throws IOException
    {
        String tabs;

        tabs    = createTabs ( tabMargin );

        outString ( Convert.concatObj ( tabs, '<', tag, " name=\"", Convert.validateXml(name), "\">" ), out );
        outText ( text, out );
        outString ( Convert.concatObj ( "</", tag, ">\n" ), out );
    }

    protected void outTitle ( int tabMargin, String tag, String name, OutputStream out ) throws IOException
    {
        String tabs;

        tabs    = createTabs ( tabMargin );
        outString ( Convert.concatObj ( tabs, '<', tag, " name=\"", Convert.validateXml(name), "\">\n" ), out );
    }

    protected void outTitle ( int tabMargin, String tag, String attrName, String attrValue, OutputStream out ) throws IOException
    {
        String tabs;

        tabs    = createTabs ( tabMargin );
        outString ( Convert.concatObj ( tabs, '<', tag, " ", attrName, "=\"", Convert.validateXml(attrValue), "\">\n" ), out );
    }

    protected void outTitle ( int tabMargin, String tag, Properties attr, OutputStream out ) throws IOException
    {
        String tabs;
        StringBuilder sb;

        // Сфоримрроватьб строку из атрибутов
        sb = new StringBuilder ( 64 );
        if ( attr != null )
        {
            for ( String name : attr.stringPropertyNames()  )
            {
                sb.append ( WCons.SEP_SPACE );
                sb.append ( name );
                sb.append ( "=\"" );
                sb.append (  Convert.validateXml(attr.getProperty ( name )) );
                sb.append ( "\"" );
                //sb.append (  );
            }
        }

        tabs    = createTabs ( tabMargin );
        outString ( Convert.concatObj ( tabs, '<', tag, sb.toString(), "\">\n" ), out );
    }

    /* Вывести тег с атрибутами, без переноса строки в конце. */
    protected void outFirstTag ( int tabMargin, String tag, String attrName, String attrValue, OutputStream out ) throws IOException
    {
        String tabs;

        tabs    = createTabs ( tabMargin );
        outString (  tabs + '<' + tag + " " + attrName + "=\"" + Convert.validateXml(attrValue) + "\">", out );
    }

    /* Только начало тега, без атрибутов, но с левым смещением. */
    protected void outTitle ( int tabMargin, String tag, OutputStream out ) throws IOException
    {
        String tabs;

        tabs    = createTabs ( tabMargin );
        outString ( Convert.concatObj ( tabs, '<', tag, ">\n" ), out );
    }

    protected String createTabs ( int tabMargin )
    {
        StringBuilder result;

        result  = new StringBuilder(64);

        if ( tabMargin > 0 )
        {
            if ( tabMargin < 5 )
            {
                // Быстрая реализация - без циклов.
                switch ( tabMargin )
                {
                    case 1:
                        result.append ( '\t' );
                        break;
                    case 2:
                        result.append ( "\t\t" );
                        break;
                    case 3:
                        result.append ( "\t\t\t" );
                        break;
                    case 4:
                        result.append ( "\t\t\t\t" );
                        break;
                }
            }
            else
            {
                for ( int i=0; i<tabMargin; i++ ) result.append ( WCons.TAB );
            }
        }

        return result.toString();
    }

    protected int getSize ( String text )
    {
        if ( text == null )
            return 0;
        else
            return text.length();
    }

    protected int getSize ( XmlAvailable xmlObj )
    {
        if ( xmlObj == null )
            return 0;
        else
            return xmlObj.getSize();
    }

}
