package svj.wedit.v6;


import svj.wedit.v6.logger.Log;
import svj.wedit.v6.tools.Convert;

/**
 * Ловим фатальные ошибки. Перед падением явы по-крайней мере запишется в лог точка падения.
 * <BR/> Сюда прилетают только те исключения, которые не были пойманы в try-catch.
 * <BR/> В том числе и OutOfMemoryError, и другие критические.
 * <BR/> Если OutOfMemoryError была поймана в try-catch, то здесь она не появится.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 20.09.2016 11:28
 */
public class WeDefaultUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler
{
    @Override
    public void uncaughtException ( Thread t, Throwable te )
    {
        // Не все ошибки распечатываем - только критические
        if ( te != null )
        {
            // Здесь возможно забыли подгрузить какую-нибудь библиотеку (например, графическую).
            //if ( te instanceof NoClassDefFoundError ) return;

            //if ( te instanceof VirtualMachineError )
            if ( (te instanceof Error) || (te instanceof RuntimeException ) )
            {
                // Error - все необрабатываемые исключения (ошибки JVM), в т.ч. и VirtualMachineError.
                // VirtualMachineError : OutOfMemoryError, etc...
                Log.l.error ( "JVM error. Thread = %s; Throwable = %s\n", t, te, Convert.trace2str ( te ) );
                //return;
            }
        }
    }

}
