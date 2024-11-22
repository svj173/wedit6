package svj.wedit.v6.tools;


import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.WClone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 26.01.2012 15:21:15
 */
public class CloneTools
{
    public static <T extends WClone<T>> List<T> clone ( List<T> list )
    {
        List<T> result;

        if ( list == null ) return null;

        result  = new ArrayList<T>(list.size());

        for ( T obj : list )
               result.add ( obj.cloneObj() );

        return result;
    }

    public static <K, T extends WClone<T>> Map<K,T> clone ( Map<K,T> list )
    {
        Map<K,T>    result;
        T           obj;

        if ( list == null ) return null;

        result  = new HashMap<K,T> (list.size());

        try
        {
            for ( K key : list.keySet() )
            {
                obj = list.get ( key );
                result.put ( key, obj.cloneObj() );
            }
        } catch ( Exception e )  {
            Log.l.error ( "err", e );
        }

        return result;
    }

}
