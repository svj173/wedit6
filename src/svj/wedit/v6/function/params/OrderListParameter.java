package svj.wedit.v6.function.params;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.WPair;
import svj.wedit.v6.tools.Convert;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Параметр содержит ordering список.
 * <BR/> Применяется в Reopen.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 10.08.2011 13:17:02
 */
public class OrderListParameter   extends FunctionParameter<List<WPair<String,String>>>
{
    /* Параметры - projectFileAbsPath, title */
    private final List<WPair<String,String>> list;


    public OrderListParameter ( String name )
    {
        super ( name );
        list    = new ArrayList<WPair<String,String>>();
    }


    @Override
    public OrderListParameter clone ()
    {
        OrderListParameter result;

        result = new OrderListParameter ( getName() );

        super.mergeToOther ( result );
        mergeToOther ( result );

        return result;
    }

    public void mergeToOther ( OrderListParameter other )
    {
        for ( WPair<String,String> entry : getList() )
        {
            other.addItem ( entry.getParam1(), entry.getParam2() );
        }
    }


    public void addItem ( String param1, String param2 )
    {
        WPair<String,String> item;

        item    = new WPair<String,String> ( param1, param2 );
        list.add ( item );
    }
    
    public void setItem ( String title, String fileName )
    {
        WPair<String,String> item;

        item    = getItem ( title );
        // Если есть такой
        if ( item != null )
        {
            //  - изменяем fileName
            item.setParam2 ( fileName );
        }
        else
        {
            // - заносим новый
            item    = new WPair<String,String> ( title, fileName );
            list.add ( item );
        }
    }

    public WPair<String, String> getItem ( String title )
    {
        if ( title == null ) return null;
        
        for ( WPair<String,String> wp : list )
        {
            if ( wp.getParam1().equals ( title ) ) return wp;
        }
        return null;
    }

    public WPair<String, String> getItem ( int number )
    {
        if ( number >= list.size() ) return null;

        return list.get ( number );
    }

    public void setFirstItem ( String title , String fileName )
    {
        WPair<String,String> item;

        item    = new WPair<String,String> ( title, fileName );
        list.add ( 0, item );
    }

    public List<WPair<String, String>> getList ()
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
            outString ( level, "<"+tagName+" name=\""+getName()+"\" type=\""+ ParameterType.LIST_ITEM+ "\">\n", out );
            outString ( ic1, "<list>\n", out );

            for ( WPair<String,String> wp : list )
            {
                // name - имя проекта. value - имя файла проекта.
                outTag ( ic2, "item", wp.getParam1(), wp.getParam2(), out );
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

        Log.l.info ( "[STRONG] (%s) toXml: list = %s", getName(), list );

        try
        {
            ic1 = level+1;
            ic2 = ic1+1;

            // <param name="BookList" type="list_item">
            outString ( level, Convert.concatObj ( "<param name=\"",getName(),"\" type=\"",ParameterType.LIST_ITEM,"\">\n" ), out );
            outString ( ic1, "<list>\n", out );

            for ( WPair<String,String> wp : list )
            {
                // name - имя проекта. value - имя файла проекта.
                outTag ( ic2, "item", wp.getParam1(), wp.getParam2(), out );
            }
            outString ( ic1, "</list>\n", out );
            outString ( level, "</param>\n", out );

        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML представления Параметра '", getName(), "' в поток :\n", e );
        }
    }

    public void getFromXml ( InputStream in ) throws WEditException
    {
    }

    public boolean delete ( WPair<String, String> wp )
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
    public List<WPair<String, String>> getValue ()
    {
        return getList();
    }

    @Override
    public void setValue ( List<WPair<String, String>> value )
    {
        if ( value != null )
        {
            list.clear ();
            list.addAll ( value );
        }
    }

}
