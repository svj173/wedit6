package svj.wedit.v6.obj.book.xml;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.WType;
import svj.wedit.v6.obj.book.BookStructure;
import svj.wedit.v6.tools.BookStructureTools;
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
public class BookTypesStaxParser extends WEditStaxParser
{
    private static final QName name             = new QName ( "enName" );

    public void read ( XMLEventReader eventReader, BookStructure bookStructure ) throws WEditException
    {
        String              tagName;
        XMLEvent            event;
        StartElement        startElement;
        EndElement          endElement;
        boolean             bWork;
        BookTypeStaxParser  typeParser;
        Attribute           attr;
        WType               type;

        try
        {
            bWork       = true;
            typeParser  = new BookTypeStaxParser();

            while ( bWork )
            {
                if ( ! eventReader.hasNext() ) return;

                event = eventReader.nextEvent();

                if ( event.isStartElement() )
                {
                    startElement = event.asStartElement();
                    tagName      = startElement.getName().getLocalPart();

                    if ( tagName.equals ( WType.TYPE ) )
                    {
                        // Название типа
                        attr    = startElement.getAttributeByName ( name );
                        if ( attr == null )
                            throw new WEditException ( "Отсутствует название Типа" );
                        type    = typeParser.read ( eventReader, attr.getValue() );
                        bookStructure.addType ( type );
                        continue;
                    }

                    throw new  WEditException ( null, "Ошибка загрузки типа элемента Книги :\n Неизвестное имя стартового тега '", tagName, "'." );
                }

                if ( event.isEndElement() )
                {
                    endElement  = event.asEndElement();
                    tagName     = endElement.getName().getLocalPart();

                    if ( tagName.equals ( WType.TYPES ) )
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
            bookStructure.clearTypes();
            //throw new  WEditException ( e, "Ошибка загрузки типа Книги :\n", e );
        }
    }

}
