package svj.wedit.v6.xml.functionParams;


import svj.wedit.v6.function.book.export.obj.BookmarksParameter;
import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.ConfigParam;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Парсер параметра - список закладок и их натсройки.
 * <BR/> Тип параметр - CONVERT_BOOKMARKS
 * <BR/> paramName = ConvertTo
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 14.10.2013 13:24
 */
public class ConvertBookmarksParser extends FunctionParamsStaxParser
{
    @Override
    public FunctionParameter parse ( XMLEventReader eventReader, String paramName, StringBuilder errMsg )
    {
        //errMsg.append ( "Функция 'ConvertBookmarksParser' не реализована.\n" );

        String              tagName;
        XMLEvent            event;
        StartElement        startElement;
        EndElement          endElement;
        String              str, paramType;
        BookmarksParameter  result;
        FunctionParameter   param;
        boolean             bwork;
        Attribute           attr;

        QName name         = new QName(ConfigParam.NAME);
        QName type         = new QName(ConfigParam.TYPE);

        result = new BookmarksParameter ( paramName );        // paramName = ConvertTo

        try
        {
            bwork = true;

            while ( bwork && eventReader.hasNext() )
            {
                event = eventReader.nextEvent();

                if ( event.isStartElement() )
                {
                    startElement = event.asStartElement ();
                    tagName      = startElement.getName().getLocalPart();

                    if ( tagName.equals(ConfigParam.PARAM) )
                    {
                        // Закладка - распарсить и добавить в список.
                        param = processParam ( result, startElement, eventReader, errMsg );
                        Log.l.debug ("--- currentFunctionId = ", result.getName(), "; param = ", param );
                        if ( param != null )  result.setParameter ( paramName, param );
                    }
                    else if ( tagName.equals("selected") )
                    {
                        str = getText ( eventReader );
                        result.setCurrentBookmark ( str );
                    }
                    else if ( tagName.equals("bookmarks") )
                    {
                        // разбираем блок закладок
                    }
                    else
                    {
                        errMsg.append ( "BookmarksParameter: Неизвестный стартовый тег : " );
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

        } catch ( Exception e ) {
            errMsg.append ( "Ошибка чтения параметра : " );
            errMsg.append ( e );
            errMsg.append ( "\n" );
            Log.file.error ( "err", e );
        }

        return result;
    }

}
