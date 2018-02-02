package svj.wedit.v6.function.params;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.tools.Utils;

import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Параметр содержит произвольное кол-во именнованных строковых аттрибутов.
 * <BR/> Name - собственно имя данного сложного параметра.
 * <BR/>
 * <BR/> Применяется в MultiListParameter - реализовать для Bookmark.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 13.11.2014 14:59:57
 */
public class MultiStringParameter extends FunctionParameter<Map<String,String>>  implements Comparable<MultiStringParameter>
{
    /* Строка - т.к. при чтении профиля пользователя, все данные этого параметра - отображаются в виде строки. И также и заносятся в параметр. */
    private final Map<String,String> attribs    = new LinkedHashMap<String,String> ();   // Linked - чтобы еще и порядок соблюдался.


    public MultiStringParameter ( String paramName )
    {
        super ( paramName );
    }


    @Override
    public MultiStringParameter clone ()
    {
        MultiStringParameter result;

        result = new MultiStringParameter ( getName() );

        super.mergeToOther ( result );
        mergeToOther ( result );

        return result;
    }

    public void mergeToOther ( MultiStringParameter other )
    {
        for ( Map.Entry<String,String> entry : attribs.entrySet() )
        {
            other.addValue ( entry.getKey(), entry.getValue() );
        }
    }


    @Override
    public Map<String,String> getValue ()
    {
        return attribs;
    }

    @Override
    public void setValue ( Map<String, String> value )
    {
        attribs.clear ();
        if ( value != null )  attribs.putAll ( value );
    }

    public void addValue ( String name, String value )
    {
        if ( name != null && value != null )   attribs.put ( name, value );
    }

    public String getValue ( String name )
    {
        String result;
        if ( name != null )
            result = attribs.get ( name );
        else
            result = null;
        return result;
    }


    public String toString()
    {
        StringBuilder result;
        result  = new StringBuilder ( 128 );
        result.append ( "[ MultiStringParameter: " );
        result.append ( super.toString() );
        result.append ( "; value = " );
        result.append ( getValue() );
        result.append ( " ]" );
        return result.toString ();
    }

    @Override
    public void toXml ( int level, OutputStream out ) throws WEditException
    {
        int    ic1;
        String str;

        try
        {
            ic1 = level+1;

            //<param name="book_ispoved" type="MULTI_STRING">
            //  <attribute name="ProjectId">svjStories_12345</attribute>
            outString ( level, "<param name=\""+getName()+"\" type=\""+ParameterType.MULTI_STRING +"\">\n", out );

            for ( Map.Entry<String,String> entry : attribs.entrySet() )
            {
                // <param name="key">value</param>
                outTag ( ic1, "item", entry.getKey(), entry.getValue(), out );
            }

            /*
            if ( value == null )
                str = "";
            else
                str = value.toString();
            outTag ( ic1, "value", str, out );
            */

            endTag ( level, "param", out );

        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML представления Параметра '", getName(), "' в поток :\n", e );
        }
    }

    public boolean equals ( Object obj )
    {
        boolean result;
        result = false;
        if ( (obj != null) && (obj instanceof MultiStringParameter ))
        {
            MultiStringParameter sp = (MultiStringParameter ) obj;
            result  = compareTo ( sp ) == 0;
        }
        return result;
    }

    @Override
    public int compareTo ( MultiStringParameter o )
    {
        if ( o == null )
            return -1;
        else
            return Utils.compareToWithNull ( getName(), o.getName() );
    }

}
