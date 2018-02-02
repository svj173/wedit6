package svj.wedit.v6.function.params;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.tools.Utils;

import java.io.OutputStream;
import java.util.Collection;
import java.util.TreeSet;


/**
 * Параметр содержит произвольное кол-во именнованных строковых аттрибутов.
 * <BR/> Name - собственно имя данного сложного параметра.
 * <BR/>
 * <BR/> Применяется в MultiListParameter - реализовать для Bookmark.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 29.11.2017 14:59:57
 */
public class ComboBoxParameter extends FunctionParameter<String>  implements Comparable<ComboBoxParameter>
{
    /** Текущее значение. */
    private String  value;
    /** Может ли содержать пустое значение. */
    private boolean hasEmpty;
    /** Если допустимо пустое значение, то это Текст его названия в выпадашке. Например: --- все --- */
    private String  emptyValue;
    /** Список всех возможных значений. TreeSet - для сортировки при отображении выпадашки. */
    private final Collection<String> valueList = new TreeSet<String>();


    public ComboBoxParameter ( String paramName )
    {
        super ( paramName );
    }


    @Override
    public ComboBoxParameter clone ()
    {
        ComboBoxParameter result;

        result = new ComboBoxParameter ( getName() );

        super.mergeToOther ( result );
        mergeToOther ( result );

        return result;
    }

    public void mergeToOther ( ComboBoxParameter other )
    {
        other.setValue ( getValue() );
        other.setHasEmpty ( hasEmpty() );
        other.setEmptyValue ( getEmptyValue() );

        other.clearListValue();
        for ( String str : getValueList() )
        {
            other.addListValue ( str );
        }
    }


    @Override
    public String getValue ()
    {
        return value;
    }

    @Override
    public void setValue ( String value )
    {
        this.value = value;
    }

    public void setListValue ( String value )
    {
        valueList.clear ();
        if ( value != null )  valueList.add ( value );
    }

    public void clearListValue ()
    {
        valueList.clear ();
    }

    public void addListValue ( String value )
    {
        if ( value != null )   valueList.add ( value );
    }

    public String toString()
    {
        StringBuilder result;
        result  = new StringBuilder ( 128 );
        result.append ( "[ ComboBoxParameter: " );
        result.append ( super.toString() );
        result.append ( "; value = " );
        result.append ( getValue() );
        result.append ( "; hasEmpty = " );
        result.append ( hasEmpty() );
        result.append ( "; emptyValue = " );
        result.append ( getEmptyValue() );
        result.append ( "; valueList = " );
        result.append ( getValueList() );
        result.append ( " ]" );
        return result.toString ();
    }

    public Collection<String> getValueList ()
    {
        return valueList;
    }

    @Override
    public void toXml ( int level, OutputStream out ) throws WEditException
    {
        int    ic1, ic2;
        String str;

        try
        {
            ic1 = level+1;

            outString ( level, "<param name=\""+getName()+"\" type=\""+ParameterType.COMBO_BOX +"\">\n", out );

            if ( value == null )
                str = "";
            else
                str = value;
            outTag ( ic1, "value", str, out );

            outTag ( ic1, "hasEmpty", hasEmpty(), out );

            if ( emptyValue == null )
                str = "";
            else
                str = value;
            outTag ( ic1, "emptyValue", str, out );

            ic2 = ic1+1;
            outString ( ic1, "<list>\n", out );
            for ( String entry : getValueList () )
            {
                // <item>value_from_list</item>
                outTag ( ic2, "item", entry, out );
            }
            endTag ( ic1, "list", out );

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
        if ( (obj != null) && (obj instanceof ComboBoxParameter ))
        {
            ComboBoxParameter sp = (ComboBoxParameter ) obj;
            result  = compareTo ( sp ) == 0;
        }
        return result;
    }

    @Override
    public int compareTo ( ComboBoxParameter o )
    {
        if ( o == null )
            return -1;
        else
            return Utils.compareToWithNull ( getName(), o.getName() );
    }

    public boolean isHasEmpty ()
    {
        return hasEmpty;
    }

    @Override
    public void setHasEmpty ( boolean hasEmpty )
    {
        this.hasEmpty = hasEmpty;
    }

    public String getEmptyValue ()
    {
        return emptyValue;
    }

    public void setEmptyValue ( String emptyValue )
    {
        this.emptyValue = emptyValue;
    }

}
