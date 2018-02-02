package svj.wedit.v6.util;


/**
 * Буфер для хранения копированных данных.
 * Самый простой вариант.
 * <BR> Возможно в будущем его можно сделать более гибким, например, несколкьо буферов,
 * и при взятии из буфера выводитcя таблица: класс обьекта, первые 100-200 символов
 * и т.д. И функции - очистить весь буфер, и т.д.
 * <BR> Можно сделать его стандалоне с инсталляцией класса из конфига, и тогда
 * пользователи сами могут прописывать свои классы.
 * <BR> А можно - как функцию. И брать из общего списка по фиксированному имени. А
 * в настройках - механизм управления содержимым буфера.
 *
 * <BR> User: Zhiganov
 * <BR> Date: 06.09.2007
 * <BR> Time: 11:30:28
 */
public class Buffer
{
    private static Object buf  = null;

    public synchronized static Object getBuffer ()
    {
        Object result = buf;
        if ( (buf != null) && (buf instanceof IClone) )
        {
            IClone cl = (IClone) buf;
            result = cl.clone();
        }
        return result;
    }

    public synchronized static void setBuffer ( Object obj )
    {
        buf = obj;
    }

    public static void clear ()
    {
        buf = null;
    }

}
