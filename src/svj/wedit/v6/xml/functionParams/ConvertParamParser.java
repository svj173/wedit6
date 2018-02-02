package svj.wedit.v6.xml.functionParams;


import svj.wedit.v6.function.book.export.obj.ConvertParameter;
import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.ConfigParam;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Парсер параметра-закладки.
 * <BR/> Тип параметра - CONVERT - конвертация книги.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 14.10.2013 13:24
 */
public class ConvertParamParser extends FunctionParamsStaxParser
{
    private enum ParamType { ELEMENT, TYPE, OTHER, LOCALE }

    @Override
    public FunctionParameter parse ( XMLEventReader eventReader, String paramName, StringBuilder errMsg )
    {
        String              tagName;
        XMLEvent            event;
        StartElement        startElement;
        EndElement          endElement;
        String              str;
        ConvertParameter    fParameter;
        FunctionParameter   param;
        boolean             bwork;
        ParamType           useType;

        fParameter  = new ConvertParameter ( paramName );
        useType     = ParamType.OTHER;

        try
        {
            bwork = true;

            while ( bwork && eventReader.hasNext() )
            {
                event = eventReader.nextEvent();

                if ( event.isStartElement() )
                {
                    startElement = event.asStartElement();
                    tagName      = startElement.getName().getLocalPart();

                    if ( tagName.equals( ConfigParam.FILE) )
                    {
                        // имя файла в который последний произошло конвертирование книги
                        str = getText ( eventReader );
                        fParameter.setFileName ( str );
                    }
                    else if ( tagName.equals( ConfigParam.PARAM ) )
                    {
                        // Параметр - это или тип или элемент.
                        param = processParam ( fParameter, startElement, eventReader, errMsg );
                        Log.file.debug ("--- currentFunctionId = %s; param = %s", fParameter.getName(), param );
                        if ( param != null )
                        {
                            switch ( useType )
                            {
                                case ELEMENT:
                                    fParameter.addElement ( param );
                                    break;
                                case TYPE:
                                    fParameter.addType ( param );
                                    break;
                                case OTHER:
                                    fParameter.addOtherParam ( param.getName(), param );
                                    break;
                                case LOCALE:
                                    fParameter.addLocale ( param );
                                    break;
                            }
                        }
                    }
                    else if ( tagName.equals( "types" ) )
                    {
                        // Начался список параметров описания типов
                        useType = ParamType.TYPE;
                    }
                    else if ( tagName.equals( "elements" ) )
                    {
                        // Начался список параметров описания элементов
                        useType = ParamType.ELEMENT;
                    }
                    else if ( tagName.equals( "others" ) )
                    {
                        // Начался список параметров описания элементов
                        useType = ParamType.OTHER;
                    }
                    else if ( tagName.equals( "locale" ) )
                    {
                        // Начался список Локальных параметров
                        useType = ParamType.LOCALE;
                    }
                    else
                    {
                        errMsg.append ( "Неизвестный стартовый тег простого параметра : " );
                        errMsg.append ( tagName );
                        errMsg.append ( "\n" );
                    }
                }
                else

                if ( event.isEndElement() )
                {
                    endElement  = event.asEndElement();
                    tagName     = endElement.getName().getLocalPart();

                    if ( tagName.equals( ConfigParam.PARAM) )
                    {
                        // Тег закрытия опиcания параметра - завершить работы
                        bwork = false;
                    }
                }
            }

        } catch ( Exception e ) {
            errMsg.append ( "Ошибка чтения параметра : " );
            errMsg.append ( e );
            errMsg.append ( "\n" );
            Log.file.error ( "err", e );
        }

        return fParameter;
    }

}
