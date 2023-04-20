package svj.wedit.v6.xml.functionParams;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.function.params.StringListParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.ConfigParam;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.*;

/**
 * Парсит данные параметра StringListParameter.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 20.04.2023
 */
public class StringListParamParser extends FunctionParamsStaxParser
{
    @Override
    public FunctionParameter parse ( XMLEventReader eventReader, String paramName, StringBuilder errMsg )
    {
        String              tagName, str;
        XMLEvent            event;
        StartElement        startElement;
        EndElement          endElement;
        StringListParameter fParameter;
        boolean             bwork;
        QName               name;

        fParameter = new StringListParameter ( paramName );

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

                    if ( tagName.equals(ConfigParam.LIST) )
                    {
                        continue;
                    }

                    if ( tagName.equals(ConfigParam.ITEM) )
                    {
                        str  = getText ( eventReader );
                        Log.l.debug ( "--- paramName: {}; value = {}", paramName, str);
                        // Взять функцию
                        fParameter.addItem ( str );
                        continue;
                    }

                    // Ошибочные теги
                    errMsg.append ( "Неизвестный стартовый тег параметра-списка : " );
                    errMsg.append ( tagName );
                    errMsg.append ( "\n" );
                }

                else if ( event.isEndElement() )
                {
                    endElement  = event.asEndElement();
                    tagName     = endElement.getName().getLocalPart();

                    if ( tagName.equals(ConfigParam.PARAM) )
                    {
                        // Тег закрытия опиcания параметра - завершить работы
                        bwork = false;
                    }
                }
            }

        } catch ( WEditException ex ) {
            errMsg.append ( "Ошибка чтения параметра-списка : " );
            errMsg.append ( ex.getMessage() );
            errMsg.append ( "\n" );
            Log.file.error ( "err", ex );
        } catch ( Exception e ) {
            errMsg.append ( "Ошибка чтения простого параметра : " );
            errMsg.append ( e );
            errMsg.append ( "\n" );
            Log.file.error ( "err", e );
        }

        return fParameter;
    }

}
