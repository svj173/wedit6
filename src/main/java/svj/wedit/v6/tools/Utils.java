package svj.wedit.v6.tools;


import svj.wedit.v6.logger.Log;

import java.io.IOException;
import java.io.OutputStream;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 27.07.2011 11:47:06
 */
public class Utils
{
    public static int compareToWithNull ( Object value1, Object value2 )
    {
        //Log.l.debug ( "---- Utils.compareToWithNull: value1 = '", value1, "', value2 = '", value2, "'." );

        if ( (value1 == null ) && (value2 == null) ) return 0;
        if ( (value1 != null ) && (value2 == null) ) return 1;
        if ( (value1 == null ) && (value2 != null) ) return -1;

        // Оба не равны null - сравниваем имена классов.
        String cl1, cl2;
        cl1 = value1.getClass().getName();
        cl2 = value2.getClass().getName();
        if ( cl1.equals ( cl2 ) )
        {
            // Классы равны
            if ( ( value1 instanceof Comparable ) && ( value2 instanceof Comparable ) )
            {
                Comparable v1, v2;
                v1  = (Comparable) value1;
                v2  = (Comparable) value2;
                return v1.compareTo ( v2 );
            }
        }

        return -1;
    }

    public static void close ( OutputStream out )
    {
        if ( out != null )
        {
            try
            {
                out.flush();   // может сгенерить исключение
                out.close();
            } catch ( IOException e )            {
                Log.file.error ( "close output error", e );
            }
        }
    }

    /*
    use BookTools
    public static String createId ()
    {
        long timeMsec;

        timeMsec    = System.currentTimeMillis();
        return Long.toString ( timeMsec );
    }
    */
    
}
