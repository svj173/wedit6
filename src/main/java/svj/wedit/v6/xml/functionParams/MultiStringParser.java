package svj.wedit.v6.xml.functionParams;


import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.function.params.MultiStringParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.ConfigParam;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Парсер много-атрибутного параметра.
 * <BR/> Тип параметра -.
 * <BR/>
 * <BR/> <param name="book_ispoved" type="MULTI_STRING">
 * <BR/>    <attribute name="ProjectId">svjStories_12345</attribute>
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 13.11.2014 16:24
 */
public class MultiStringParser extends FunctionParamsStaxParser
{
    @Override
    public FunctionParameter parse ( XMLEventReader eventReader, String paramName, StringBuilder errMsg )
    {
        String               tagName, name;
        XMLEvent             event;
        StartElement         startElement;
        EndElement           endElement;
        String               str;
        MultiStringParameter fParameter;
        boolean              bwork;
        QName                nameAttr;
        Attribute            attr;

        nameAttr    = new QName ( ConfigParam.NAME );

        fParameter  = new MultiStringParameter ( paramName );

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

                    if ( tagName.equals( "item") )
                    {
                        // name
                        attr        = startElement.getAttributeByName ( nameAttr );
                        name = attr.getValue();
                        // имя файла в который последний произошло конвертирование книги
                        str  = getText ( eventReader );
                        fParameter.addValue ( name, str );
                    }
                    else
                    {
                        errMsg.append ( "Неизвестный стартовый тег сложного параметра : " );
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
            errMsg.append ( "Ошибка чтения сложного параметра : " );
            errMsg.append ( e );
            errMsg.append ( "\n" );
            Log.file.error ( "err", e );
        }

        return fParameter;
    }

}
