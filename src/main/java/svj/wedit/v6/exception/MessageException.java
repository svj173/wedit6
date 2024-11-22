package svj.wedit.v6.exception;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 05.04.2013 11:11
 */
public class MessageException extends WEditException
{
    public MessageException ( Object... mess )
    {
        super ( null, mess );
        //setCode ( UNKNOW );
    }

}
