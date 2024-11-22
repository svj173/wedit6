package svj.wedit.v6.function.params;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.tools.Convert;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Параметр содержит ordering список других параметров. В т.ч. и другие MultListParameter параметры.
 * <BR/> Применяется в Bookmark.
 * <BR/>
 * <BR/> Пример:
 * <BR/>
 <pre>
 <param name="BookmarkList" type="MULTI_LIST">
        <param name="BookAttr" type="MULTI_STRING">
                 <item name="bookTitle">Школа</item>
                 <item name="projectId">/home/svj/Serg/Stories/Cookies/project.xml</item>
                 <item name="bookId">school</item>
                 <item name="textId">...</item>
                 <item name="cursor">254</item>
        </param>
        <param name="BookmarkList" type="LIST_ITEM">
            ....
        </param>
 </param>
 </pre>
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 21.01.2015 12:17:02
 */
public class MultiListParameter extends FunctionParameter
{
    /* Параметры - projectFileAbsPath, title */
    //private final Collection<FunctionParameter> list;
    private final List<FunctionParameter> list;


    public MultiListParameter ( String name )
    {
        super ( name );
        list    = new ArrayList<FunctionParameter> ();
        //list    = new TreeSet<FunctionParameter> ();
    }

    @Override
    public MultiListParameter clone ()
    {
        MultiListParameter result;

        result = new MultiListParameter ( getName() );

        super.mergeToOther ( result );
        mergeToOther ( result );

        return result;
    }

    public void mergeToOther ( MultiListParameter other )
    {
        other.clearList();
        for ( FunctionParameter fp : getList () )
        {
            other.addItem ( fp.clone() );
        }
    }

    public String toString()
    {
        StringBuilder result;

        result = new StringBuilder();

        result.append ( "[ MultiListParameter : name = " );
        result.append ( getName() );
        result.append ( "; list = " );
        result.append ( getList() );

        result.append ( " ]" );

        return result.toString();
    }

    public void addItem ( FunctionParameter param )
    {
        list.add ( param );
    }
    
    public FunctionParameter getItem ( String name )
    {
        if ( name == null ) return null;
        
        for ( FunctionParameter wp : list )
        {
            if ( wp.getName ().equals ( name ) ) return wp;
        }
        return null;
    }

    public FunctionParameter getItem ( int number )
    {
        if ( number >= list.size() ) return null;

        return list.get ( number );
    }

    public void setFirstItem ( FunctionParameter param )
    {
        list.add ( 0, param );
    }

    public List<FunctionParameter> getList ()
    {
        return list;
    }

    @Override
    public void toXml ( int level, OutputStream out ) throws WEditException
    {
        int    ic1, ic2;

        try
        {
            ic1 = level+1;
            ic2 = ic1+1;

            // <param name="BookList" type="list_item">
            outString ( level, Convert.concatObj ( "<param name=\"",getName(),"\" type=\"",ParameterType.MULTI_LIST,"\">\n" ), out );
            //outString ( ic1, "<list>\n", out );

            for ( FunctionParameter wp : list )
            {
                //outTag ( ic2, "item", wp.getParam1(), wp.getParam2(), out );
                wp.toXml ( ic1, out );
            }
            //outString ( ic1, "</list>\n", out );
            outString ( level, "</param>\n", out );

        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML представления Параметра '", getName(), "' в поток :\n", e );
        }
    }

    public void getFromXml ( InputStream in ) throws WEditException
    {
    }

    public boolean delete ( FunctionParameter wp )
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

    public void trimToSize ( int newSize )
    {
        // todo - это не работает. Сделать правильно.
        //list.ensureCapacity ( newSize );
    }

    @Override
    public Object getValue ()
    {
        return list;
    }

    @Override
    public void setValue ( Object value )
    {
        FunctionParameter fp;

        if ( value != null )
        {
            if ( value instanceof List)
            {
                List list = (List) value;
                for ( Object obj : list )
                {
                    if ( (obj != null) && (obj instanceof FunctionParameter))
                    {
                        fp = (FunctionParameter) obj;
                        addItem ( fp );
                    }
                }
            }
            else if ( value instanceof FunctionParameter )
            {
                fp = (FunctionParameter) value;
                addItem ( fp );
            }
        }
    }

}
