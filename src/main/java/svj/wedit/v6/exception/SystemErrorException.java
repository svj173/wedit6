package svj.wedit.v6.exception;

/**
 * Системная ошибка.
 * <BR/> Какая-то неучтенная ошибка, в коде системы. Требует вмешательства разработчиков. -- from 999
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 13.04.2012 13:58:24
 */
public class SystemErrorException extends WEditException
{
    public SystemErrorException ( Throwable cause, Object... mess )
    {
        super ( cause,  mess );
        //super ( cause, Convert.concatObj ( "Системная ошибка. ", mess ) );
        setCode ( SYSTEM_ERROR );
    }

}
