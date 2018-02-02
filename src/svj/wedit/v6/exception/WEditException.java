package svj.wedit.v6.exception;


import svj.wedit.v6.tools.Convert;


/**
 * Исключение.
 * Здесь же отмечается, как отображать его пользователю - строка
 * в лог или фрейм на экран.  -- ??
 * <BR/> Виды информации:
 * <LI> Текст сообщения (нужен ли?) либо ключ на текст.</LI>
 * <LI> Трассировка, если есть.</LI>
 * <LI> Массив обьектов для мультиязыкового сообщения - если они требуются. </LI>
 * <LI> Флаг того что это выход по Cancel - для быстрого выхода из всех методов
 *  до конечного модуля. </LI>
 *
 * <BR/> User: Zhiganov
 * <BR/> Date: 27.08.2007
 * <BR/> Time: 15:59:46
 */
public class WEditException  extends Exception
{
    /** Сообщение об ошибке. Мультиязык. Т.е. это ключ к списку сообщений. */
    //private String  messageKey  = "";

    /** Флаг того, что это обыкновенный выход по CANCEL и делать ничего не нужно. */
    private boolean  isCancel  = false;

    /** Массив дополнительных параметров, используемых при выводе сообщения. */
    //private Object[]    errorValue  = null;

    // ---------------------------------------------------------

    /** Тип вывода сообщения - в лог или в окне. ???? - по идее - конечный модуль
     *  сам должен знать куда ему выводить.*/
    //private int     outputType  = WCons.OUTPUTTYPE_LOG;

    /** Заголовок в мультиязыке. - По идее - это должен определять конечный модуль. */
    //private String  titleKey  = "";

    /* Внутренний код ошибки. */
    private int code    = -1;
    public static final int NONE_ERROR = 1000;  // НЕ ошибка - чтобы в лог error.txt не выводить.
    /* Системная ошибка. Какая-то неучтенная ошибка, в коде системы. Требует вмешательства разработчиков. -- from 999 */
    public static final int SYSTEM_ERROR        = 4004;
    // Типы исключений. INFO - исключение для вывода информационного сообщения.
    public static final Boolean INFO     = Boolean.TRUE;
    public static final Boolean ERROR    = Boolean.FALSE;
    public static final int INTERRUPT_COMMAND   = 410;  // Прерывание выполнения команды
    public static final int NONE_SYSTEM_ERROR   = 800;  // НЕ системная ошибка. Т.е. не надо скидывать в лог уровнем ERROR.
    public static final int WARN            = 301;  // Рабочая ситуация (WARN). Используется для выходов из функции по исключению.

    /* Флаг - это информационное сообщение или ошибка. Исп в EltexEventHandler для фильтрации сообщений об ошибках и информационных сообщениях */
    private boolean isInfo = ERROR;


    public WEditException ( boolean isInfo, String mess )
    {
       super ( mess );
       this.isInfo = isInfo;
    }

    public WEditException ( boolean isInfo, Object... msg )
    {
       super ( Convert.concatObj ( msg ) );
       this.isInfo = isInfo;
    }

    public WEditException ( Throwable e, Object... msg )
    {
        super ( Convert.concatObj ( msg ), e );
    }

    public WEditException ( int code, Throwable e, Object... msg )
    {
        super ( Convert.concatObj ( msg ), e );
        setCode ( code );
    }

    public WEditException ( String msg, Exception e )
    {
        super ( msg, e );
    }

    public WEditException ( String msg )
    {
        super ( msg );
    }

    public WEditException ( boolean cancel )
    {
        isCancel = cancel;
    }

    public WEditException ( Throwable cause, StringBuilder msg )
    {
        super ( msg.toString(), cause );
    }

    public WEditException ( String msg, Throwable cause )
    {
        super ( msg, cause );
    }


    public boolean isCancel ()
    {
        return isCancel;
    }


    public int getCode ()
    {
        return code;
    }

    public boolean isInfo ()
    {
        return isInfo;
    }

    public String   toString()
    {
        StringBuilder    result;

        result  = new StringBuilder ( 128 );

        result.append ( "WEditException: " );
        result.append ( getMessage() );
        result.append ( "; Error : " );
        result.append ( getCause() );

        return result.toString();
    }

    public void setCode ( int code )
    {
        this.code = code;
    }

}
