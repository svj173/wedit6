package svj.wedit.v6.obj.book.xml;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.WType;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.ElementCons;
import svj.wedit.v6.xml.WEditStaxParser;

import javax.xml.stream.XMLEventReader;
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
         <desc>Рабочий текст (черновой)</desc>
     </type>
     <type enName="release">
         <ruName>Релиз</ruName>
         <desc>Законченный текст</desc>
     </type>
     <type enName="hidden">
         <ruName>Скрытый</ruName>
         <desc>Материал, используемый в книге</desc>
     </type>
 </types>

 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.08.2011 17:45:43
 */
public class BookTypeStaxParser extends WEditStaxParser
{
    public WType read ( XMLEventReader eventReader, String typeName ) throws WEditException
    {
        String          tagName, str;
        XMLEvent        event;
        StartElement    startElement;
        EndElement      endElement;
        boolean         bWork;
        WType           type;

        tagName = null;
        type    = new WType();
        type.setEnName ( typeName );

        try
        {
            bWork   = true;
            // Read the XML document

            while ( bWork )
            {
                if ( ! eventReader.hasNext() ) return type;

                event = eventReader.nextEvent();

                if ( event.isStartElement() )
                {
                    startElement = event.asStartElement();
                    tagName      = startElement.getName().getLocalPart();

                    if ( tagName.equals( WType.TYPE) )
                    {
                        // Начало элемента - уровень уже распарсен выше
                        continue;
                    }

                    if ( tagName.equals(WType.DESCR) )
                    {
                        str = getText ( eventReader );
                        type.setDescr ( str );
                        continue;
                    }

                    if ( tagName.equals(WType.RU_NAME) )
                    {
                        str = getText ( eventReader );
                        type.setRuName ( str );
                        continue;
                    }

                    if ( tagName.equals(WType.ICON_FONT_COLOR) )
                    {
                        str = getText ( eventReader );
                        type.setIconFontColor ( Convert.str2color ( str ) );
                        continue;
                    }

                    if ( tagName.equals(ElementCons.COLOR) )
                    {
                        str = getText ( eventReader );
                        type.setColor ( Convert.str2color ( str ) );
                        continue;
                    }

                    if ( tagName.equals(ElementCons.STYLE_TYPE) )
                    {
                        str = getText ( eventReader );
                        type.setStyleType ( Integer.parseInt( str ) );
                        continue;
                    }

                    if ( tagName.equals(ElementCons.DEFAULT_TYPE) )
                    {
                        str = getText ( eventReader );
                        type.setDefaultType ( Convert.str2boolean ( str, false ) );
                        continue;
                    }

                    throw new  WEditException ( null, "Ошибка загрузки типа Книги :\n Неизвестное имя стартового тега '", tagName, "'." );
                }

                if ( event.isEndElement() )
                {
                    endElement  = event.asEndElement();
                    tagName     = endElement.getName().getLocalPart();

                    if ( tagName.equals(WType.TYPE) )
                    {
                        bWork = false;
                    }
                }
            }

        } catch (WEditException ex) {
            throw ex;
        } catch (Exception e) {
            Log.file.error ( Convert.concatObj ( "error. typeName = ", typeName, "; tagName = ", tagName ), e);
            throw new  WEditException ( e, "Ошибка загрузки типа Книги '", typeName, "' :\n", e );
        }

        return type;
    }

}