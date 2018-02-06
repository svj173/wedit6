package svj.wedit.v6.obj.book.xml;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookCons;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.xml.WEditStaxParser;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;


/**
 * Распарсить из потока книгу, но брать из книги толкьо атрибуты.
 * <BR/> Применяется в функции показа предаткируемых книг за указанный период.
 * <BR/>
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 06.02.2018 14:41:43
 */
public class BookContentOnlyAttrStaxParser extends WEditStaxParser
{
    //private final QName name             = new QName ( BookCons.NAME );

    public void read ( InputStream in, BookContent bookContent ) throws WEditException
    {
        String                  tagName;
        XMLEvent                event;
        StartElement            startElement;
        XMLEventReader          eventReader;
        XMLInputFactory         inputFactory;
        BookAttrStaxParser      attrParser;
        BookNode                bookNode;
        int                     eventType;

        try
        {
            attrParser      = new BookAttrStaxParser();

            // Всегда создаем нулевой текст
            bookNode        = new BookNode ( bookContent.getName(), null );
            bookContent.setBookNode ( bookNode );

            inputFactory = XMLInputFactory.newInstance();
            eventReader  = inputFactory.createXMLEventReader(in);
            //eventReader  = inputFactory.createXMLEventReader(in,"UTF-8");
            //eventReader  = inputFactory.createXMLEventReader(new FileReader ());

            while ( eventReader.hasNext() )
            {
                event = eventReader.nextEvent();

                eventType   = event.getEventType ();
                Log.file.debug ( "eventType  = %d", eventType );

                switch ( eventType )
                {
                    case XMLEvent.START_ELEMENT :  // 1
                        startElement = event.asStartElement();
                        tagName      = startElement.getName().getLocalPart();
                        Log.file.debug ( "--- tagName  = %s", tagName );

                        /*
                        if ( tagName.equals( BookCons.BOOK_CONTENT) )
                        {
                            // Начало документа
                            // - Имя книги
                            attr    = startElement.getAttributeByName ( name );
                            if ( attr == null )
                                throw new WEditException ("Отсутствует название книги");
                            str     = attr.getValue();
                            bookContent.setName ( str );
                            bookNode.setName ( str );
                            continue;
                        }
                        */

                        if ( tagName.equals( BookCons.BOOK_ATTRIBUTIES) )
                        {
                            Log.file.debug ( "--- Start BOOK_ATTRIBUTIES" );
                            attrParser.read ( eventReader, bookContent );
                            Log.file.debug ( "--- Finish BOOK_ATTRIBUTIES" );
                            //break; // больше парсить ничего не нужно.
                            return;
                        }

                        //throw new  WEditException ( null, "Ошибка загрузки файла Книги '", bookContent.getName(), "' :\n Неизвестное имя стартового тега '", tagName, "'. \nold str = ", str );
                }
            }

        } catch (WEditException ex) {
            throw ex;
        } catch (Exception e) {
            Log.file.error ("err",e);
            throw new  WEditException ( e, "Ошибка загрузки файла Книги '", bookContent.getFileName(),"' :\n", e );
        }
    }

}
