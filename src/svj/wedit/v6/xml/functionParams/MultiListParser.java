package svj.wedit.v6.xml.functionParams;


import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.function.params.MultiListParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.ConfigParam;
import svj.wedit.v6.tools.Convert;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.Reader;
import java.io.StringReader;

/**
 * Парсит данные параметра OrderListParameter.
 * <BR/> Содержит список других параметров - в т.ч. и вложенный OrderListParameter.
 * <BR/>
 * <BR/> Пример:
 * <BR/>
 <pre>
 <param name="BookmarkList" type="MULTI_LIST">
        <param name="BookAttr" type="MULTI_STRING">
                 <item name="bookTitle">Школа</item>
                 <item name="projectId">/home/svj/Serg/Stories/Cookies/project.xml</item>
                 <item name="bookId">school</item>
                 <item name="textId">...</item>
                 <item name="cursor">254</item>
        </param>
        <param name="BookmarkList" type="LIST_ITEM">
            ....
        </param>
 </param>
 </pre>
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 21.01.2015 13:24
 */
public class MultiListParser extends FunctionParamsStaxParser
{
    @Override
    public FunctionParameter parse ( XMLEventReader eventReader, String paramName, StringBuilder errMsg )
    {
        String              tagName, vParam, paramType, str;
        XMLEvent            event;
        StartElement        startElement;
        EndElement          endElement;
        MultiListParameter  fParameter;
        boolean             bwork;
        Attribute           attr;
        FunctionParameter   fp;

        fParameter = new MultiListParameter ( paramName );

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

                    if ( tagName.equals( ConfigParam.PARAM) )
                    {
                        attr    = startElement.getAttributeByName ( NAME );
                        if ( attr == null )
                        {
                            str = Convert.concatObj ( paramName, " : Ошибка инициализации параметра для мультипараметра '",paramName,"'. Отсутствует имя Параметра." );
                            Log.l.error ( str );
                            errMsg.append ( str );
                            errMsg.append ( "\n" );
                            continue;
                        }
                        paramName   = attr.getValue();

                        attr    = startElement.getAttributeByName ( TYPE );
                        if ( attr == null )
                        {
                            str = Convert.concatObj ( paramName, " : Отсутствует тип Параметра для подпараметра '", tagName, "'." );
                            Log.l.error ( str );
                            errMsg.append ( str );
                            errMsg.append ( "\n" );
                        }
                        else
                        {
                            // занести параметр
                            paramType   = attr.getValue();
                            // Получить параметр (индивидуальный xml-парсинг по типу параметра)
                            fp  = FunctionParamsStaxParser.parseFunctionParameter ( paramName, paramType, eventReader, errMsg );
                            Log.l.debug ("--- MultiListParser: paramName = %s; add fp = %s", paramName, fp );
                            if ( fp != null )  fParameter.addItem ( fp );
                        }
                        continue;
                    }

                    // Ошибочные теги
                    errMsg.append ( paramName );
                    errMsg.append ( " : Неизвестный стартовый тег параметра-списка '" );
                    errMsg.append ( tagName );
                    errMsg.append ( "'.\n" );
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

            /*
        } catch ( WEditException ex ) {
            errMsg.append ( "Ошибка чтения параметра-списка : " );
            errMsg.append ( ex.getMessage() );
            errMsg.append ( "\n" );
            Log.file.error ( "err", ex );
            */
        } catch ( Exception e ) {
            errMsg.append ( "Ошибка чтения мульти-параметра '" );
            errMsg.append ( paramName );
            errMsg.append ( "' : " );
            errMsg.append ( e );
            errMsg.append ( "\n" );
            Log.file.error ( "err", e );
        }

        return fParameter;
    }

    public static void main ( String[] args )
    {
        MultiListParser parser;
        StringBuilder text, errMsg;
        XMLEventReader xmlReader;
        XMLInputFactory  inputFactory;
        Reader reader;
        FunctionParameter   fp;

        errMsg  = new StringBuilder ( 512 );

        text    = new StringBuilder ( 512 );
        text.append ( "<param name=\"BookmarkList\" type=\"MULTI_LIST\">\n" );
        text.append ( "\t<param name=\"BookAttr\" type=\"MULTI_STRING\">\n" );
        text.append ( "\t\t<item name=\"bookTitle\">Школа</item>\n" );
        text.append ( "\t\t<item name=\"projectId\">/home/svj/Serg/Stories/Cookies/project.xml</item>\n" );
        text.append ( "\t\t<item name=\"bookId\">school</item>\n" );
        text.append ( "\t\t<item name=\"textId\">...</item>\n" );
        text.append ( "\t\t<item name=\"cursor\">254</item>\n" );
        text.append ( "\t</param>\n" );
        text.append ( "</param>\n" );

        parser = new MultiListParser();

        try
        {
            reader = new StringReader ( text.toString() );

            inputFactory    = XMLInputFactory.newInstance();
            xmlReader       = inputFactory.createXMLEventReader ( reader );

            fp  = parser.parse ( xmlReader, "TEST_01", errMsg );
            System.out.println ( "Result = " + fp );
            System.out.println ( "ErrMsg = " + errMsg );

        } catch ( Exception e )        {
            e.printStackTrace ();
        }
    }

}
