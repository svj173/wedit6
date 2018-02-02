package svj.wedit.v6.function.params;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.ConfigParam;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.StringTools;
import svj.wedit.v6.tools.Utils;

import java.io.OutputStream;


/**
 * Параметр содержит всего один логический обьект.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.10.2013 09:09:57
 */
public class BooleanParameter extends FunctionParameter<Boolean>  implements Comparable<BooleanParameter>
{
    private boolean value    = false;

    /* Дефолтное значение - на случай ошибок при занесении новых. */
    private final boolean defaultValue;


    public BooleanParameter ( String paramName, String value )
    {
        this ( paramName, Convert.getBoolean ( value, false ) );
    }

    public BooleanParameter ( String paramName, boolean defaultValue )
    {
        super ( paramName );

        this.defaultValue   = defaultValue;
        this.value          = defaultValue;
    }

    @Override
    public BooleanParameter clone ()
    {
        BooleanParameter result;

        result = new BooleanParameter ( getName(), defaultValue );

        super.mergeToOther ( result );
        mergeToOther ( result );

        return result;
    }

    public void mergeToOther ( BooleanParameter other )
    {
        other.setValue ( getValue() );
    }

    public Boolean getValue ()
    {
        return value;
    }

    public void setValue ( Boolean value )
    {
        this.value = value;
    }

    public void setValue ( String value )
    {
        //logger.debug ( "Set Value = " + value );
        if ( (value == null) || (value.isEmpty() ) )
        {
            if ( hasEmpty() )
                this.value = false;
            else
                this.value = defaultValue;
        }
        else
        {
            this.value = Convert.getBoolean ( value, false );
        }
    }

    public String toString()
    {
        StringBuilder result;
        result  = new StringBuilder ( 64 );
        result.append ( "[ BooleanParameter: " );
        result.append ( super.toString() );
        result.append ( "; value = " );
        result.append ( getValue() );
        result.append ( "; defaultValue = " );
        result.append ( defaultValue );
        result.append ( " ]" );
        return result.toString();
    }

    @Override
    public void toXml ( int level, OutputStream out ) throws WEditException
    {
        int    ic1;

        try
        {
            ic1 = level+1;

            outString ( level, "<param name=\""+getName()+"\" type=\""+ParameterType.BOOLEAN +"\">\n", out );

            outTag ( ic1, "value", Boolean.toString ( getValue() ), out );
            if ( ! StringTools.isEmpty ( getRuName() ) )  outTag ( ic1, ConfigParam.RU_NAME, getRuName(), out );

            endTag ( level, "param", out );

        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML представления boolean Параметра '", getName(), "' в поток :\n", e );
        }
    }

    public boolean equals ( Object obj )
    {
        boolean result;
        result = false;
        if ( (obj != null) && (obj instanceof BooleanParameter ))
        {
            BooleanParameter sp = (BooleanParameter ) obj;
            result  = compareTo ( sp ) == 0;
        }
        return result;
    }

    @Override
    public int compareTo ( BooleanParameter o )
    {
        if ( o == null )
            return -1;
        else
            return Utils.compareToWithNull ( getName(), o.getName() );
    }

}
