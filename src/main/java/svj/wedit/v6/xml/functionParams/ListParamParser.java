package svj.wedit.v6.xml.functionParams;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.function.params.OrderListParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.ConfigParam;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Парсит данные параметра OrderListParameter.
 * <BR/> Содержит список других параметров - в т.ч. и вложенный OrderListParameter.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 14.10.2013 13:24
 */
public class ListParamParser extends FunctionParamsStaxParser
{
    @Override
    public FunctionParameter parse ( XMLEventReader eventReader, String paramName, StringBuilder errMsg )
    {
        String              tagName, vParam, str;
        XMLEvent            event;
        StartElement        startElement;
        EndElement          endElement;
        OrderListParameter  fParameter;
        boolean             bwork;
        Attribute           attr;
        QName               name;

        name       = new QName ( ConfigParam.NAME );
        fParameter = new OrderListParameter ( paramName );

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
                        // Элемент списка значений одного параметра. Имя=Значение
                        attr    = startElement.getAttributeByName ( name );
                        if ( attr == null )
                        {
                            Log.l.error ( "Отсутствует имя Параметра из списка" );
                            errMsg.append ( "Отсутствует имя Параметра из списка.\n" );
                            //throw new WEditException ("Отсутствует имя Параметра");
                        }
                        else
                        {
                            str     = attr.getValue();
                            // Имя файла
                            vParam  = getText ( eventReader );
                            Log.l.debug ( "--- paramName: ", paramName, ", param1: ", str, ", param2: ", vParam );
                            // Взять функцию
                            fParameter.addItem ( str, vParam );
                        }
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
