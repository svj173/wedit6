package svj.wedit.v6.logger;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 18.01.2011 18:01:37
 */
public class Log
{
    /** Логгер для вывода отладочных сообщений. */
    // getFormatterLogger - иначе в тексте будут игнорироваться параметры вида %s
    //public static WLogger l;
    public static Logger l = LogManager.getFormatterLogger ( "Kernel" );
    /* Логер для вывода сообщений работы Функций */
    public static Logger f = LogManager.getFormatterLogger ( "Function" );
    /* Логер для вывода сообщений работы с файлами */
    public static Logger file = LogManager.getFormatterLogger ( "File" );


    private Log ()  {  }

}
