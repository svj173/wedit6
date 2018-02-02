package svj.wedit.v6.function.book.export.obj;


import svj.wedit.v6.function.params.SimpleParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.tools.Convert;

import java.util.Collection;

/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 14.11.2013 16:36
 */
public class ConvertTools
{
    public static TypeHandleType getType ( String value )
    {
        TypeHandleType result;

        result = TypeHandleType.NOTHING;
        if ( value != null )
        {
            try
            {
                result = TypeHandleType.valueOf ( value );
            } catch ( Exception e )             {
                Log.l.error ( Convert.concatObj ( "error. value = ", value ), e);
                result = TypeHandleType.NOTHING;
            }
        }
        else
        {
            result = TypeHandleType.WRITE;
        }

        return result;
    }

    /**
     * Приходит тип элемента - hidden, Null-work, ...
     * Найти его тип вывода всего элемента.
     * @param elementType
     * @return
     */
    public static TypeHandleType getType ( String elementType, Collection<SimpleParameter> types )
    {
        TypeHandleType handleType;

        if ( elementType == null ) return TypeHandleType.WRITE;  // todo hardcode -- по идее где-то должен быть мап нулевого типа на имеющиеся типы элементов.

        for ( SimpleParameter type : types )
        {
            if ( type.getName().equals ( elementType ) )
            {
                // Получить значение типа
                handleType = ConvertTools.getType ( type.getValue() );
                return handleType;
            }
        }
        return TypeHandleType.NOTHING;
    }

}
