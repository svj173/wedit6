package svj.wedit.v6.xml.functionParams;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.function.params.ParameterType;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.ConfigParam;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.xml.WEditStaxParser;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Парсер параметров функций - у каждого типа параметра - свой парсер.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 14.10.2013 13:09
 */
public abstract class FunctionParamsStaxParser   extends WEditStaxParser
{
    private static final Map<ParameterType,FunctionParamsStaxParser> paramsParser = new HashMap<ParameterType,FunctionParamsStaxParser> ();

    static
    {
        paramsParser.put ( ParameterType.SIMPLE,                new SimpleParamParser()     );   // простой параметр. дальше идет один тег - value
        paramsParser.put ( ParameterType.BOOLEAN,               new BooleanParamParser()    );   // простой параметр. дальше идет один тег - value
        paramsParser.put ( ParameterType.LIST_ITEM,             new ListParamParser()       );
        paramsParser.put ( ParameterType.STRING_LIST,           new StringListParamParser() );
        paramsParser.put ( ParameterType.CONVERT,               new ConvertParamParser()    );
        paramsParser.put ( ParameterType.CONVERT_BOOKMARKS,     new ConvertBookmarksParser());
        paramsParser.put ( ParameterType.ELEMENT_CONVERT,       new ElementConvertParser()  );
        paramsParser.put ( ParameterType.MULTI_STRING,          new MultiStringParser()     );
        paramsParser.put ( ParameterType.MULTI_LIST,            new MultiListParser()       );
        paramsParser.put ( ParameterType.COMBO_BOX,             new ComboBoxParser()        );
    }


    public abstract FunctionParameter parse ( XMLEventReader eventReader, String paramName, StringBuilder errMsg );


    /**
     * Получить параметр (индивидуальный xml-парсинг по типу параметра).
     * Применяется при начальной загрузке Редактора.
     *
     *
     * @param paramName   Имя параметра
     * @param paramType   Тип параметра
     * @param eventReader Потоковый ридер.
     * @param errMsg      Массив дял сообщений об ошибках.
     * @return            Обьект параметра.
     */
    public static FunctionParameter parseFunctionParameter ( String paramName, String paramType, XMLEventReader eventReader, StringBuilder errMsg )
    {
        FunctionParameter        result;
        FunctionParamsStaxParser functionParser;

        try
        {
            // Взять парсер согласно типа
            functionParser  = FunctionParamsStaxParser.createFunctionParamsParser ( paramType );

            // Распарсить параметр из потока - may be NULL - при ошибках и т.д.
            result          = functionParser.parse ( eventReader, paramName, errMsg );

        } catch ( Exception e )         {
            result = null;
            errMsg.append ( "Ошибка получения параметра '" );
            errMsg.append ( paramName );
            errMsg.append ( "' c типом '" );
            errMsg.append ( paramType );
            errMsg.append ( "' : " );
            errMsg.append ( e );
            errMsg.append ( "\n" );
        }

        return result;
    }

    public static FunctionParamsStaxParser createFunctionParamsParser ( String paramType )   throws WEditException
    {
        FunctionParamsStaxParser result;
        ParameterType type;

        // Взять парсер согласно типа
        try
        {
            type = ParameterType.valueOf ( paramType );     // .toUpperCase()
        } catch ( Exception e )         {
            Log.l.error ( Convert.concatObj ( "Ошибка определения типа параметра функции. paramType = ", paramType ), e);
            throw new WEditException ( e, "Ошибка определения типа параметра функции. paramType = ", paramType );
        }

        result  = paramsParser.get ( type );

        if ( result == null )
        {
            Log.l.error ( "Для типа параметра '%s' не задан парсер.", paramType );
            throw new WEditException ( null, "Для типа параметра '", paramType, "' не задан парсер."  );
        }

        return result;
    }

    /**
     * Прочитать параметр-закладку из потока xml-файла.
     * @param functionParameter Вышестоящий параметр. В котором хранятся все параметры-закладки.
     * @param startElement      Обьект тега.
     * @param eventReader       Поток.
     * @param errMsg            Буфер для сообщений об ошибках.
     */
    public FunctionParameter processParam ( FunctionParameter functionParameter, StartElement startElement, XMLEventReader eventReader, StringBuilder errMsg )
    {
        String              paramName, paramType;
        FunctionParameter   param;
        Attribute           attr;
        QName               name, type;

        name    = new QName ( ConfigParam.NAME );
        type    = new QName ( ConfigParam.TYPE );
        param   = null;

        // Имя параметра
        attr    = startElement.getAttributeByName ( name );

        if ( attr == null )
        {
            Log.l.error ( "Ошибка инициализации параметра функции '%s'. Отсутствует имя Параметра.",functionParameter.getName() );
            errMsg.append ( "Ошибка инициализации параметра функции '" ).append ( functionParameter.getName() ).append ( "'. Отсутствует имя Параметра.\n" );
            //throw new WEditException ("Отсутствует имя Параметра");
            return null;
        }

        paramName   = attr.getValue().trim();
        if ( paramName.isEmpty () )
        {
            Log.l.error ( "Ошибка инициализации параметра функции '%s'. Пустое значение в имени Параметра. paramName = %s",functionParameter.getName(), paramName );
            errMsg.append ( "Ошибка инициализации параметра функции '" ).append ( functionParameter.getName() ).append ( "'. Пустое значение в имени Параметра '" );
            errMsg.append ( paramName );
            errMsg.append ( "'.\n" );
            return null;
        }

        // Тип параметра
        attr        = startElement.getAttributeByName ( type );
        if ( attr == null )
        {
            Log.l.error ( "Отсутствует тип Параметра" );
            errMsg.append ( "Отсутствует тип Параметра.\n" );
        }
        else
        {
            // Взять функцию
            if ( Par.GM == null )
            {
                errMsg.append ( "Отсутствует Par.GM. Параметр = '" );
                errMsg.append ( functionParameter.getName() );
            }
            else
            {
                paramType   = attr.getValue();
                // Получить параметр (индивидуальный xml-парсинг по типу параметра)
                Log.file.info ("--- paramType = %s", paramType );
                param       = FunctionParamsStaxParser.parseFunctionParameter ( paramName, paramType, eventReader, errMsg );
            }
        }

        return param;
    }

}
