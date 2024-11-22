package svj.wedit.v6.obj.book.xml;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.WType;
import svj.wedit.v6.obj.book.BookCons;
import svj.wedit.v6.obj.book.BookStructure;
import svj.wedit.v6.obj.book.WEditStyle;
import svj.wedit.v6.obj.book.element.StyleName;
import svj.wedit.v6.obj.book.element.StyleType;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.ElementCons;
import svj.wedit.v6.tools.StyleTools;
import svj.wedit.v6.xml.WEditStaxParser;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


/**
 * Распарсить из потока структуру книги.
 * <BR/> Структура книги - элементы распарсиваются своим парсером
 * <BR/>
 *   <bookContent name="Прикл 4">
		<bookStructure>
			<element level="0">
				<name>Книга</name>
				<rowSpace>4</rowSpace>
				<treeFgColor>00ff00</treeFgColor>
				<treeFont>Monospaced-1-22</treeFont>
			</element>

 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.08.2011 17:45:43
 */
public class BookStructureStaxParser   extends WEditStaxParser
{
    //private static final QName  level             = new QName("level");


    public void read ( XMLEventReader eventReader, BookStructure bookStructure ) throws WEditException
    {
        String                  tagName, str;
        XMLEvent                event;
        StartElement            startElement;
        EndElement              endElement;
        WBookElementsStaxParser bookElementParser;
        BookTypesStaxParser     typesParser;
        boolean                 bWork;
        WEditStyle              style;

        tagName = null;

        try
        {
            bWork   = true;

            typesParser         = new BookTypesStaxParser();
            bookElementParser   = new WBookElementsStaxParser();

            // Read the XML document

            while ( bWork )
            {
                if ( ! eventReader.hasNext() ) return;

                event = eventReader.nextEvent();

                if ( event.isStartElement() )
                {
                    startElement = event.asStartElement();
                    tagName      = startElement.getName().getLocalPart();
                    Log.file.debug ( "----- BookStructure. tagName = ", tagName );

                    if ( tagName.equals( BookCons.BOOK_STRUCTURE) )
                    {
                        //bookStructure    = new BookStructure();
                        continue;
                    }

                    /* not use
                    if ( tagName.equals( ElementCons.ELEMENTS) )
                    {
                        //старый тег - просто пропускаем
                        continue;
                    }

                    if ( tagName.equals( ElementCons.ELEMENT) )
                    {
                        // Начало блока - здесь просто проупскаем все теги, относящиеся к старому функционалу. Иначе ниже будет ругаться.
                        // здесь читаем поток до тега окончания элемента
                        elementParser.read ( eventReader );
                        continue;
                    }
                    */

                    if ( tagName.equals( ElementCons.MANDATORIES) )
                    {
                        continue;
                    }

                    if ( tagName.equals ( ElementCons.TEXT_STYLE) )
                    {
                        str     = getText ( eventReader );
                        style   = StyleTools.createStyle ( str, StyleType.TEXT, StyleName.TEXT );
                        bookStructure.setTextStyle ( style );
                        continue;
                    }

                    if ( tagName.equals ( ElementCons.ANNOTATION_STYLE) )
                    {
                        str     = getText ( eventReader );
                        style   = StyleTools.createStyle ( str, StyleType.ANNOTATION, StyleName.ANNOTATION );
                        style.addAttribute ( StyleName.STYLE_NAME, StyleName.ANNOTATION );
                        bookStructure.setAnnotationStyle ( style );
                        continue;
                    }

                    if ( tagName.equals ( ElementCons.LABEL_STYLE) )
                    {
                        str     = getText ( eventReader );
                        //bookStructure.setLabelStyle ( StyleTools.createStyle ( str, StyleType.TEXT) );
                        style   = StyleTools.createStyle ( str, StyleType.TEXT, StyleName.LABEL );
                        style.addAttribute ( StyleName.STYLE_NAME, StyleName.LABEL );
                        bookStructure.setLabelStyle ( style );
                        continue;
                    }

                    // -------- new ----------------------------------
                    if ( tagName.equals( ElementCons.W_ELEMENTS) )
                    {
                        bookElementParser.read ( eventReader, bookStructure );
                        continue;
                    }

                    if ( tagName.equals( WType.TYPES) )
                    {
                        typesParser.read ( eventReader, bookStructure );
                        continue;
                    }


                    throw new  WEditException ( null, "Ошибка загрузки файла описания Структуры книги :\n Неизвестное имя стартового тега '", tagName, "'." );
                }

                if ( event.isEndElement() )
                {
                    endElement  = event.asEndElement();
                    tagName     = endElement.getName().getLocalPart();

                    if ( tagName.equals(BookCons.BOOK_STRUCTURE) )
                    {
                        bWork = false;
                    }
                }
            }

        } catch ( WEditException ex ) {
            Log.file.error ( Convert.concatObj ( "error. tagName = ", tagName ), ex);
            // Заменяем на дефолтную - выше. Здесь - обнуляем.
            //bookStructure.setDefault();
            bookStructure.clear();
            //throw ex;
        } catch ( Exception e ) {
            Log.file.error ("err",e);
            //throw new  WEditException ( e, "Ошибка загрузки файла описания Структуры книги :\n", e );
            // Заменяем на дефолтную
            //bookStructure.setDefault();
            bookStructure.clear();
        }
    }

}
