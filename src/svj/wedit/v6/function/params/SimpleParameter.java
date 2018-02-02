package svj.wedit.v6.function.params;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.ConfigParam;
import svj.wedit.v6.tools.StringTools;
import svj.wedit.v6.tools.Utils;

import java.io.OutputStream;


/**
 * Параметр содержит всего один обьект - строковый.
 * <BR/> Т.е. это на самом деле StringParameter.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.07.2011 16:59:57
 */
public class SimpleParameter extends FunctionParameter<String>  implements Comparable<SimpleParameter>
{
    /* Строка - т.к. при чтении профиля пользователя, все данные этого параметра - отображаются в виде строки. И также и заносятся в параметр. */
    private String value    = null;

    /* Дефолтное значение - на случай ошибок при занесении новых. */
    private final String defaultValue;


    public SimpleParameter ( String paramName, int defaultValue )
    {
        this ( paramName, Integer.toString ( defaultValue ) );
    }

    public SimpleParameter ( String paramName, String defaultValue )
    {
        super ( paramName );

        this.defaultValue   = defaultValue;
        this.value          = defaultValue;
    }

    public SimpleParameter ( String paramName, String defaultValue, boolean hasEmpty )
    {
        this ( paramName, defaultValue );
        setHasEmpty ( hasEmpty );
    }


    @Override
    public SimpleParameter clone ()
    {
        SimpleParameter result;

        result = new SimpleParameter( getName(), defaultValue );
        result.setValue ( getValue() );

        super.mergeToOther ( result );

        return result;
    }

    public String getValue ()
    {
        return value;
    }

    public void    addValue ( String text )
    {
        if ( value == null )
            value = text;
        else
            value = value + text;
    }

    public void setValue ( String value )
    {
        //logger.debug ( "Set Value = " + value );
        if ( (value == null) || (value.isEmpty() ) )
        {
            if ( hasEmpty() )
                this.value = value;
            else
                this.value = defaultValue;
        }
        else
        {
            this.value = value;
        }
    }

    public String toString()
    {
        StringBuilder result;
        result  = new StringBuilder ( 64 );
        result.append ( "[ SimpleParameter: " );
        result.append ( super.toString() );
        //result.append ( "]; type = " );
        //result.append ( type );
        result.append ( "; value = " );
        result.append ( value );
        result.append ( "; defaultValue = " );
        result.append ( defaultValue );
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

            // <param name="BookList" type="list_item">
            outString ( level, "<param name=\""+getName()+"\" type=\""+ParameterType.SIMPLE +"\">\n", out );
            if ( value == null )
                str = "";
            else
                str = value;
            outTag ( ic1, "value", str, out );
            outTag ( ic1, "hasEmpty", hasEmpty(), out );
            if ( ! StringTools.isEmpty ( getRuName() ) )  outTag ( ic1, ConfigParam.RU_NAME, getRuName(), out );

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
        if ( (obj != null) && (obj instanceof SimpleParameter ))
        {
            SimpleParameter sp = (SimpleParameter) obj;
            result  = compareTo ( sp ) == 0;
        }
        return result;
    }

    @Override
    public int compareTo ( SimpleParameter o )
    {
        if ( o == null )
            return -1;
        else
            return Utils.compareToWithNull ( getName(), o.getName() );
    }

    public String getDefaultValue ()
    {
        return defaultValue;
    }
}
