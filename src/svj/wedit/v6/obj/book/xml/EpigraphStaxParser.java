package svj.wedit.v6.obj.book.xml;


import svj.wedit.v6.exception.SystemErrorException;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookCons;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.xml.WEditStaxParser;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


/**
 * Распарсить из потока структуру параметров Эпиграфа книги.
 * <pre>
 * <epigraph>
 *   <p>Кажется, знаешь о себе все, так нет.
 *   Находятся люди, которые знают о тебе больше.</p>
 *   <text-author>В. Андреев</text-author>
 * </epigraph>
 *
 * </pre>
 *
 * <BR/> Для пустой строки применяется <empty-line/>
 * <BR/>
 * <BR/> text-author - тоже допускается множественное исползование, но мы пока делаем толкьо одного.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 11.01.2022 15:45:43
 */
public class EpigraphStaxParser extends WEditStaxParser
{
    public void read ( XMLEventReader eventReader, BookContent bookContent ) throws WEditException
    {
        String          tagName;
        XMLEvent        event;
        StartElement    startElement;
        EndElement      endElement;
        boolean         bWork;

        String str;
        tagName     = null;

        try
        {
            bWork   = true;
            int ic = 0;

            while ( bWork )
            {
                if ( ! eventReader.hasNext() ) return;

                event = eventReader.nextEvent();
                ic++;
                if (ic > 10000) throw new WEditException("Слишком много циклов при парсинге Эпиграфа книги: " + ic);

                if ( event.isStartElement() )
                {
                    startElement = event.asStartElement();
                    tagName      = startElement.getName().getLocalPart();
                    Log.file.debug ( "EpigraphStaxParser. tagName = %s", tagName );

                    if ( tagName.equals( "text-author") )
                    {
                        str = getText ( eventReader );
                        bookContent.setEpigraphAuthor ( str );
                        continue;
                    }

                    if ( tagName.equals( "p") )
                    {
                        str = getText ( eventReader );
                        bookContent.addEpigraphText ( str );
                        continue;
                    }

                    if ( tagName.equals( "empty-line") )
                    {
                        bookContent.addEpigraphText ( "" );
                        continue;
                    }

                    throw new  WEditException ( null, "Ошибка загрузки параметров Эпиграфа уровня книги '", bookContent.getName(), "' :\n Неизвестное имя стартового тега '", tagName, "'." );
                }

                if ( event.isEndElement() )
                {
                    endElement  = event.asEndElement();
                    tagName     = endElement.getName().getLocalPart();

                    if ( tagName.equals(BookCons.EPIGRAPH) )
                    {
                        // конец работы парсера
                        bWork = false;
                    }
                }
            }

        } catch ( WEditException ex ) {
            Log.file.error (  "error. tagName = " + tagName + "; book = " + bookContent, ex);
            throw ex;
        } catch ( Exception e ) {
            Log.file.error ("err",e);
            throw new SystemErrorException ( e, "Системная ошибка чтения Эпиграфа уровня книги '", bookContent.getName(), "' :\n", e );
        }
    }

}
