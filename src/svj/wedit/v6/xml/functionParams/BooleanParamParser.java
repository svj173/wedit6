package svj.wedit.v6.xml.functionParams;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.params.BooleanParameter;
import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.ConfigParam;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Парсер параметра BooleanParameter -- тип BOOLEAN.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.10.2013 09:23
 */
public class BooleanParamParser extends FunctionParamsStaxParser
{
    @Override
    public FunctionParameter parse ( XMLEventReader eventReader, String paramName, StringBuilder errMsg )
    {
        String              tagName;
        XMLEvent            event;
        StartElement        startElement;
        EndElement          endElement;
        String              str;
        FunctionParameter   fParameter;
        boolean             bwork;

        fParameter = null;

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

                    if ( tagName.equals( ConfigParam.VALUE) )
                    {
                        str = getText ( eventReader );
                        fParameter = new BooleanParameter ( paramName, str );
                    }
                    else if ( tagName.equals( ConfigParam.RU_NAME ) )
                    {
                        str = getText ( eventReader );
                        if ( fParameter != null )  fParameter.setRuName ( str );
                    }
                    else
                    {
                        errMsg.append ( "Неизвестный стартовый тег простого параметра : " );
                        errMsg.append ( tagName );
                        errMsg.append ( "\n" );
                    }
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
            errMsg.append ( "Ошибка чтения boolean параметра : " );
            errMsg.append ( ex.getMessage() );
            errMsg.append ( "\n" );
            Log.file.error ( "err", ex );
        } catch ( Exception e ) {
            errMsg.append ( "Ошибка чтения boolean параметра : " );
            errMsg.append ( e );
            errMsg.append ( "\n" );
            Log.file.error ( "err", e );
        } catch ( NoSuchMethodError ne ) {
            errMsg.append ( "Ошибка чтения boolean параметра - нет такого метода : " );
            errMsg.append ( ne );
            errMsg.append ( "\n" );
            Log.file.error ( "err", ne );
        }

        return fParameter;
    }

}
