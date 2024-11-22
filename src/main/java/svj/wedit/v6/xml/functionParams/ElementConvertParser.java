package svj.wedit.v6.xml.functionParams;


import svj.wedit.v6.function.book.export.obj.ElementConvertParameter;
import svj.wedit.v6.function.book.export.obj.HtmlElementConvertParameter;
import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.ConfigParam;
import svj.wedit.v6.tools.Convert;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 14.10.2013 13:24
 */
public class ElementConvertParser extends FunctionParamsStaxParser
{
    @Override
    public FunctionParameter parse ( XMLEventReader eventReader, String paramName, StringBuilder errMsg )
    {
        String                  tagName;
        XMLEvent                event;
        StartElement            startElement;
        EndElement              endElement;
        String                  str;
        ElementConvertParameter fParameter;
        boolean                 bwork;

        fParameter  = new HtmlElementConvertParameter();
        fParameter.setName ( paramName );

        try
        {
            bwork       = true;

            while ( bwork && eventReader.hasNext() )
            {
                event = eventReader.nextEvent();

                if ( event.isStartElement() )
                {
                    startElement = event.asStartElement ();
                    tagName      = startElement.getName().getLocalPart();

                    if ( tagName.equals( "font" ) )
                    {
                        str = getText ( eventReader );
                        fParameter.setFont ( Convert.str2font ( str) );
                    }
                    else if ( tagName.equals( "level" ) )
                    {
                        str = getText ( eventReader );
                        fParameter.setLevel ( Convert.getInt ( str, 1 ) );
                    }
                    else if ( tagName.equals( "color" ) )
                    {
                        str = getText ( eventReader );
                        fParameter.setColor ( Convert.str2color ( str ) );
                    }
                    else if ( tagName.equals( "format" ) )
                    {
                        // номер режима
                        str = getText ( eventReader );
                        fParameter.setFormatType ( str );
                    }
                    else
                    {
                        errMsg.append ( paramName );
                        errMsg.append ( ": Неизвестный стартовый тег простого параметра : " );
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
