package svj.wedit.v6.xml.functionParams;


import svj.wedit.v6.function.params.ComboBoxParameter;
import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.ConfigParam;
import svj.wedit.v6.tools.Convert;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Парсер параметра, хранившего список допутсимых значений, и текущее значение (из данного списка).
 * <BR/> Тип параметра - COMBO_BOX.
 * <BR/>
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 29.11.2017 16:24
 */
public class ComboBoxParser extends FunctionParamsStaxParser
{
    @Override
    public FunctionParameter parse ( XMLEventReader eventReader, String paramName, StringBuilder errMsg )
    {
        String               tagName, name;
        XMLEvent             event;
        StartElement         startElement;
        EndElement           endElement;
        String               str;
        ComboBoxParameter    fParameter;
        boolean              bwork;
        QName                nameAttr;
        Attribute            attr;

        nameAttr    = new QName ( ConfigParam.NAME );

        fParameter  = new ComboBoxParameter ( paramName );

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
                        str  = getText ( eventReader );
                        fParameter.addListValue ( str );
                        continue;
                    }

                    if ( tagName.equals( "value") )
                    {
                        str  = getText ( eventReader );
                        fParameter.setValue ( str );
                        continue;
                    }

                    if ( tagName.equals( "hasEmpty") )
                    {
                        str  = getText ( eventReader );
                        fParameter.setHasEmpty ( Convert.getBoolean ( str, false ) );
                        continue;
                    }

                    if ( tagName.equals( "emptyValue") )
                    {
                        str  = getText ( eventReader );
                        fParameter.setEmptyValue ( str );
                        continue;
                    }

                    if ( tagName.equals( "list") )
                    {
                        continue;
                    }

                    errMsg.append ( "Неизвестный стартовый тег параметра : " );
                    errMsg.append ( tagName );
                    errMsg.append ( "\n" );
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
