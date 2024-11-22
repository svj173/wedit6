package svj.wedit.v6.obj.book.xml;


import svj.wedit.v6.exception.SystemErrorException;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookCons;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.xml.WEditStaxParser;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


/**
 * Распарсить из потока структуру атрибутов книги.
 * <BR/> Структура книги - элементы распарсиваются своим парсером
 * <BR/>
		<attributies>
			<attribute name="create_date">2014-08-10 12:12</attribute>
			<attribute name="last_change_date">2014-08-20 12:12</attribute>

 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.08.2011 17:45:43
 */
public class BookAttrStaxParser extends WEditStaxParser
{
    private static final QName  NAME             = new QName("name");


    public void read ( XMLEventReader eventReader, BookContent bookContent ) throws WEditException
    {
        String          tagName, attrName, attrValue;
        XMLEvent        event;
        StartElement    startElement;
        Attribute       attr;
        EndElement      endElement;
        boolean         bWork;

        tagName = null;

        try
        {
            bWork   = true;

            while ( bWork )
            {
                if ( ! eventReader.hasNext() ) return;

                event = eventReader.nextEvent();

                if ( event.isStartElement() )
                {
                    startElement = event.asStartElement();
                    tagName      = startElement.getName().getLocalPart();
                    Log.file.debug ( "----- BookStructure. tagName = %s", tagName );

                    if ( tagName.equals( BookCons.BOOK_ATTRIBUTE) )
                    {
                        // Название атрибута
                        attr    = startElement.getAttributeByName ( NAME );
                        if ( attr == null )
                            throw new WEditException ("Отсутствует имя атрибута.");
                        attrName     = attr.getValue();
                        attrValue    = getText ( eventReader );
                        bookContent.addAttribute ( attrName, attrValue );
                        continue;
                    }

                    throw new  WEditException ( null, "Ошибка загрузки атрибута книги '", bookContent.getName(), "' :\n Неизвестное имя стартового тега '", tagName, "'." );
                }

                if ( event.isEndElement() )
                {
                    endElement  = event.asEndElement();
                    tagName     = endElement.getName().getLocalPart();

                    if ( tagName.equals(BookCons.BOOK_ATTRIBUTIES) )
                    {
                        bWork = false;
                    }
                }
            }

        } catch ( WEditException ex ) {
            Log.file.error ( Convert.concatObj ( "error. tagName = ", tagName, "; book = ", bookContent ), ex);
            throw ex;
        } catch ( Exception e ) {
            Log.file.error ("err",e);
            throw new SystemErrorException ( e, "Системная ошибка чтения атрибутов книги '", bookContent.getName(), "' :\n", e );
        }
    }

}
