package svj.wedit.v6.obj.book.xml;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.element.WBookElement;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.ElementCons;
import svj.wedit.v6.xml.WEditStaxParser;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


/**
 * Распарсить из потока описание элемента книги - только свою часть.
 * <BR/>
 * <BR/>
 private int     elementLevel;
 private String  name;     // (Часть, Глава, Эпизод)
 private Color   color;
 private int     fontSize;
  style - Font.PLAIN, BOLD, ITALIC, or BOLD+ITALIC. - Text
 private int     styleType;
  LEFT, RIGHT, CENTER. - Text
 private int     align;
  Крайнее смещение. Если align=LEFT - это смещение слева от заголовка. Если align=RIGHT - это смещение справа от заголовка. Для CENTER - не используется. - Text
 private int     margin;

 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.08.2011 17:45:43
 */
public class WBookElementStaxParser extends WEditStaxParser
{
    public WBookElement read ( XMLEventReader eventReader, int level ) throws WEditException
    {
        String          tagName, str;
        XMLEvent        event;
        StartElement    startElement;
        EndElement      endElement;
        boolean         bWork;
        WBookElement    bookElement;

        tagName         = null;
        bookElement     = new WBookElement ( level );

        try
        {
            bWork   = true;
            // Read the XML document

            while ( bWork )
            {
                if ( ! eventReader.hasNext() ) return bookElement;

                event = eventReader.nextEvent();

                if ( event.isStartElement() )
                {
                    startElement = event.asStartElement();
                    tagName      = startElement.getName().getLocalPart();

                    if ( tagName.equals( ElementCons.ELEMENT) )
                    {
                        // Начало элемента - уровень уже распарсен выше
                        continue;
                    }

                    if ( tagName.equals(ElementCons.NAME) )
                    {
                        str = getText ( eventReader );
                        bookElement.setName ( str );
                        continue;
                    }

                    if ( tagName.equals(ElementCons.COLOR) )
                    {
                        str = getText ( eventReader );
                        bookElement.setColor ( Convert.str2color ( str ) );
                        continue;
                    }

                    if ( tagName.equals(ElementCons.STYLE_TYPE) )
                    {
                        str = getText ( eventReader );
                        bookElement.setStyleType ( Integer.parseInt ( str ) );
                        continue;
                    }

                    if ( tagName.equals(ElementCons.FONT_SIZE) )
                    {
                        str = getText ( eventReader );
                        bookElement.setFontSize ( Integer.parseInt ( str ) );
                        continue;
                    }

                    if ( tagName.equals(ElementCons.FONT_FAMILY) )
                    {
                        str = getText ( eventReader );
                        bookElement.setFontFamily ( str );
                        continue;
                    }

                    if ( tagName.equals(ElementCons.ALIGN) )
                    {
                        str = getText ( eventReader );
                        bookElement.setAlign ( Integer.parseInt ( str ) );
                        continue;
                    }

                    if ( tagName.equals(ElementCons.MARGIN) )
                    {
                        str = getText ( eventReader );
                        bookElement.setMargin ( Integer.parseInt ( str ) );
                        continue;
                    }

                    throw new  WEditException ( null, "Ошибка загрузки элемента Книги :\n Неизвестное имя стартового тега '", tagName, "'." );
                }

                if ( event.isEndElement() )
                {
                    endElement  = event.asEndElement();
                    tagName     = endElement.getName().getLocalPart();

                    if ( tagName.equals(ElementCons.ELEMENT) )
                    {
                        bWork = false;
                    }
                }
            }

        } catch (WEditException ex) {
            throw ex;
        } catch (Exception e) {
            Log.file.error ( Convert.concatObj ( "error. level = ", level, "; tagName = ", tagName ), e);
            throw new  WEditException ( e, "Ошибка загрузки типа Книги '", level, "' :\n", e );
        }

        return bookElement;
    }

}