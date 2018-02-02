package svj.wedit.v6.msg;


import svj.wedit.v6.logger.Log;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;


/**
 * Мультиязыковость.
 * <BR/>                 
 * <BR/> User: svj
 * <BR/> Date: 07.02.2006
 * <BR/> Time: 17:43:18
 */
public class Msg
{
    /* Хранит мессаги по типам языка (ключи) - 'ru', 'de' и т.д.  Как обьекты Properties. */
    private static Map<String,Properties> msg = new HashMap<String,Properties> ();

    /** Соглашение по умолчанию. */
    private static String  currentLocale   = "ru";
    private static String  defaultLocale   = "ru";


    public static void setMsg ( Hashtable msg )
    {
        Msg.msg = msg;
    }

    public static void setCurrent ( String locale )
    {
        currentLocale   = locale;
        Log.l.debug ( "SET New Lang = " + currentLocale );
    }

    public static void setDefault ()
    {
        currentLocale   = defaultLocale;
        Log.l.debug ( "SET Default Lang = " + currentLocale );
    }

    public static String getCurrent ()
    {
        return currentLocale;
    }

    public static String getDefault ()
    {
        return defaultLocale;
    }

    public synchronized static String getMessage ( String name )
    {
        String      result;
        Properties  lang;

        if ( name == null ) return null;
        result  = null;
        try
        {
            lang    = (Properties) msg.get ( currentLocale );
            if ( lang == null )
            {
                //throw new Exception ( "Language '" + currentLocale + "' is absent." );
                //LogWriter.l.error ( "Language '" + currentLocale + "' is absent." );
                return null;
            }
            //LogWriter.l.debug ( "lang = " + lang + ", name = " + name );
            result  = lang.getProperty ( name );
            // если сообщение не найдено - то брать по умолчанию (en)
            if ( result == null )
            {
                lang    = (Properties) msg.get ( defaultLocale );
                if ( lang == null )
                {
                    //throw new Exception ( "Language '" + defaultLocale + "' is absent." );
                    //LogWriter.l.error ( "Language '" + defaultLocale + "' is absent." );
                    return null;
                }
                result  = lang.getProperty ( name );
            }

            // Заменить символы \n,\t,\r на соответсвующий символ
            if ( result != null )
            {
                result = changeSpecSmb ( result );
            }
        } catch ( Exception e )         {
            Log.l.error ( "Error. name = " + name, e );
        }
        return result;
    }

    /**
     * Заменить символы \n,\t,\r на соответствующий символ.
     * 
     * @param str Исходная строка
     * @return Результат
     */
    private static String changeSpecSmb ( String str )
    {
        String result;

        if ( str == null ) return str;

        result = str.replace ( "\\n", "\n" );
        result = result.replace ( "\\t", "\t" );
        result = result.replace ( "\\r", "\r" );

        return result;
    }

    /**
     * Выдать сообщение с подменой спецсимволов вида {1} на их значения из массива.
     * @param name   Имя ключа
     * @param value  Массив значений
     * @return       Сгруппированная итоговая строка
     */
    public synchronized static String getMessage ( String name, Object[] value )
    {
        String result, str;
        Object  obj;
        result  = getMessage ( name );
        //LogWriter.l.debug ( "result-f = " + result );
        if ( result == null )   return name;
        for ( int i=0; i<value.length; i++ )
        {
            obj     = value[i];
            if ( obj == null )  str = "null";
            else    str = obj.toString ();
            result  = result.replace ( "{" + (i+1) + "}", str );
            //LogWriter.l.debug ( "result-" + i + " = " + result + ", value = " + value[i].toString() );
        }

        // Заменить символы \n,\t,\r на соответсвующий символ
        if ( result != null )
        {
            result = changeSpecSmb ( result );
        }
        
        return result;
    }

    /**
     * Выдать мультиязыковый текст для заданного ключа сообщения.
     * <BR/> Особенность: если текст не найден - выдать сам ключ вместо текста.
     * @param name Имя ключа
     * @return     Текст либо имя ключа
     */
    public synchronized static String getMsg ( String name )
    {
        String result;
        result  = getMessage ( name );
        if ( result == null )   return name;
        return result;
    }

    /**
     * Выдать сообщение в требуемом языке.
     * @param name        Код сообщения
     * @param language    Язык сообщения
     * @param defaultMsg  Сообщение по умолчанию на случай отсутствия.
     * @return  msg
     */
    public synchronized static String getMessage ( String name, String language, String defaultMsg )
    {
        String result;
        try
        {
            Properties   lang    = (Properties) msg.get ( language );
            if ( lang == null ) throw new Exception ( "Language '" + language + "' is absent." );
            result  = lang.getProperty ( name );
            if ( result == null )   result  = defaultMsg;
        } catch ( Exception e )        {
            result  = defaultMsg;
        }
        return result;
    }

    public synchronized static String getMessage ( String name, String defMsg )
    {
        String result;
        try {
            result  = getMessage ( name );
        } catch ( Exception e ) {
            result  = defMsg;
        }
        if ( result == null )   result  = defMsg;
        return result;
    }

    /**
     * Добавить записи к имеющимся.
     * @param prop    Массив записей
     * @param locale  Идентификатор языка
     */
    public synchronized static void add ( Properties prop, String locale )
    {
        msg.put ( locale, prop );
    }

    public static String view()
    {
        return "CurrentLang = " + currentLocale + ", messages: " + msg;
    }

}
