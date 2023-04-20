package svj.wedit.v6.function.params;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;

import java.io.OutputStream;
import java.util.*;


/**
 * Параметр содержит строковый список.
 * <BR/> Применяется в SelectToRTF.
 * <BR/> Хранит список директорий, куда конвертится текст - для применения функции на разных компьютерах.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 20.04.2023
 */
public class StringListParameter extends FunctionParameter<Collection<String>>
{
    private final Set<String> list;


    public StringListParameter(String name )
    {
        super ( name );
        list    = new TreeSet<>();
    }


    @Override
    public StringListParameter clone ()
    {
        StringListParameter result;

        result = new StringListParameter( getName() );

        super.mergeToOther ( result );
        mergeToOther ( result );

        return result;
    }

    public void mergeToOther ( StringListParameter other )
    {
        for (String str : getList() )
        {
            other.addItem (str);
        }
    }


    public boolean isEmpty ()
    {
        return list.isEmpty();
    }

    public void addItem ( String item)
    {
        list.add ( item );
    }

    public Collection<String> getList ()
    {
        return list;
    }

    public void toXml ( int level, String tagName, OutputStream out ) throws WEditException
    {
        int    ic1, ic2;

        try
        {
            ic1 = level+1;
            ic2 = ic1+1;

            // <param name="BookList" type="list_item">
            outString ( level, "<"+tagName+" name=\""+getName()+"\" type=\""+ ParameterType.STRING_LIST+ "\">\n", out );
            outString ( ic1, "<list>\n", out );

            for ( String item : list )
            {
                // name - имя проекта. value - имя файла проекта.
                outTag ( ic2, "item", item, out );
            }
            outString ( ic1, "</list>\n", out );
            outString ( level, "</"+tagName+">\n", out );

        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML представления Параметра '", getName(), "' в поток :\n", e );
        }
    }


    @Override
    public void toXml ( int level, OutputStream out ) throws WEditException
    {
        int    ic1, ic2;

        Log.l.debug ( "[STRONG] toXml: name = %s; list = %s", getName(), list );

        try
        {
            ic1 = level+1;
            ic2 = ic1+1;

            outString ( level, "<param name=\""+getName()+"\" type=\""+ParameterType.STRING_LIST+"\">\n", out );
            outString ( ic1, "<list>\n", out );

            for ( String item : list )
            {
                //Log.l.debug ( "[STRONG] (%s) toXml: list = %s", getName(), wp );
                outTag ( ic2, "item", item, out );
            }
            outString ( ic1, "</list>\n", out );
            outString ( level, "</param>\n", out );

        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML представления Параметра '", getName(), "' в поток :\n", e );
        }
    }


    public boolean delete ( String wp )
    {
        return list.remove ( wp );
    }

    public void clearList ()
    {
        list.clear();
    }

    public int size ()
    {
        return list.size();
    }


    @Override
    public Collection<String> getValue ()
    {
        return getList();
    }

    @Override
    public void setValue ( Collection<String> value )
    {
        if ( value != null )
        {
            list.clear ();
            list.addAll ( value );
        }
    }

}
