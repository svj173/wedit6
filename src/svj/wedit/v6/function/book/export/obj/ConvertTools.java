package svj.wedit.v6.function.book.export.obj;


import svj.wedit.v6.function.params.SimpleParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.WType;
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

        Log.l.debug ("getType: elementType = %s; types = %s", elementType, types );

        if ( elementType == null ) return TypeHandleType.WRITE;
        // todo hardcode -- по идее где-то должен быть мап нулевого типа на имеющиеся типы элементов.

        for ( SimpleParameter type : types )
        {
            // elementType = release; type = служебный -- в разных языках.
            // todo найти в списках типов Элемент по его англ имени

            Log.l.debug("getType: elementType = %s; type = %s", elementType, type.getName() );
            if ( type.getName().equals ( elementType ) )
            {
                // Тип анализируемого элемента присутсвует в списке разрешенных.
                // Получить значение типа
                handleType = ConvertTools.getType ( type.getValue() );
                Log.l.debug(" handleType = %s; elementType = %s; type = %s", handleType, elementType, type.getName() );
                return handleType;
            }
        }
        return TypeHandleType.NOTHING;
    }

    public static TypeHandleType getType ( WType elementType, Collection<SimpleParameter> types )
    {
        TypeHandleType handleType;
        boolean b;

        // Могут прийти: elementType = release; type = служебный -- в разных языках.
        // todo найти в списках типов Элемент по его англ имени

        Log.l.debug("getType: elementType = %s; types = %s", elementType, types );

        // todo hardcode -- по идее где-то должен быть мап нулевого типа элемента (по-умолчанию) на имеющиеся типы
        // элементов.
        if ( elementType == null ) return TypeHandleType.WRITE;

        String ruName = elementType.getRuName();
        if ( ruName == null )  ruName = "рабочая";

        for ( SimpleParameter type : types )
        {

            Log.l.debug("-- getType: type = %s", type.getName() );

            b = false;
            // Сначала проверяем на руское имя.
            //if ( elementType.getRuName().equals(type.getName()) )   {
            if ( ruName.equals(type.getName()) )
            {
                // OK
                b = true;
            }
            else
            {
                // Проверяем на англ имя
                b = elementType.getEnName().equals(type.getName());
            }

            if ( b )
            {
                // Тип анализируемого элемента присутсвует в списке разрешенных.
                // Получить значение типа
                handleType = ConvertTools.getType ( type.getValue() );
                Log.l.debug("-- handleType = %s; elementType = %s; type = %s",handleType,elementType,type.getName() );
                return handleType;
            }
        }
        return TypeHandleType.NOTHING;
    }

}
