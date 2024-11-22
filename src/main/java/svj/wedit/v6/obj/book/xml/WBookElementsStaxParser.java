package svj.wedit.v6.obj.book.xml;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookCons;
import svj.wedit.v6.obj.book.BookStructure;
import svj.wedit.v6.obj.book.element.WBookElement;
import svj.wedit.v6.tools.ElementCons;
import svj.wedit.v6.xml.WEditStaxParser;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


/**
 * Распарсить из потока тип описания структуры книги - только свою часть.
 * <BR/>
 * <BR/>
 <types>
     <type enName="work">
         <ruName>Рабочий</ruName>
         <descr>Рабочий текст (черновой)</descr>
         <iconName>book_work.png</iconName>
     </type>
     <type name="release">
         <ruName>Релиз</ruName>
         <desc>Законченный текст</desc>
     </type>
     <type name="hidden">
         <ruName>Скрытый</ruName>
         <desc>Материал, используемый в книге</desc>
     </type>
 </types>

 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.08.2011 17:45:43
 */
public class WBookElementsStaxParser extends WEditStaxParser
{
    private static final QName level             = new QName ( BookCons.LEVEL );

    public void read ( XMLEventReader eventReader, BookStructure bookStructure ) throws WEditException
    {
        String              tagName;
        XMLEvent            event;
        StartElement        startElement;
        EndElement          endElement;
        boolean             bWork;
        WBookElementStaxParser  parser;
        Attribute           attr;
        WBookElement        bookElement;

        try
        {
            bWork   = true;
            parser  = new WBookElementStaxParser();

            while ( bWork )
            {
                if ( ! eventReader.hasNext() ) return;

                event = eventReader.nextEvent();

                if ( event.isStartElement() )
                {
                    startElement = event.asStartElement();
                    tagName      = startElement.getName().getLocalPart();

                    if ( tagName.equals ( ElementCons.ELEMENT ) )
                    {
                        // Название типа
                        attr    = startElement.getAttributeByName ( level );
                        if ( attr == null )
                            throw new WEditException ( "Отсутствует уровень элемента" );
                        bookElement    = parser.read ( eventReader, Integer.parseInt ( attr.getValue() ) );
                        bookStructure.addBookElement ( bookElement );
                        continue;
                    }

                    throw new  WEditException ( null, "Ошибка загрузки элементов Книги :\n Неизвестное имя стартового тега '", tagName, "'." );
                }

                if ( event.isEndElement() )
                {
                    endElement  = event.asEndElement();
                    tagName     = endElement.getName().getLocalPart();

                    if ( tagName.equals ( ElementCons.W_ELEMENTS ) )
                    {
                        bWork = false;
                    }
                }
            }
        /*
        } catch ( WEditException ex ) {
            // Наверх НЕ ругаться, а подставить дефолтные типы.
            Log.file.error ("err",ex);
            BookStructureTools.setDefaultTypes ( bookStructure );
            //throw ex;
        */
        } catch ( Exception e ) {
            // Наверх НЕ ругаться, а подставить дефолтные типы.
            Log.file.error ("err",e);
            //BookStructureTools.setDefaultTypes ( bookStructure );
            bookStructure.clearElements();
            //throw new  WEditException ( e, "Ошибка загрузки типа Книги :\n", e );
        }
    }

}
