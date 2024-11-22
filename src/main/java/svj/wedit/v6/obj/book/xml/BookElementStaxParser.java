package svj.wedit.v6.obj.book.xml;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.tools.ElementCons;
import svj.wedit.v6.xml.WEditStaxParser;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.XMLEvent;


/**
 * Распарсить из потока элемент описания структуры книги - только свою часть.
 * <BR/>
 * <BR/>
 *   <bookContent name="Прикл 4">
		<bookStructure>
			<element name="0">
				<name>Книга</name>
				<type>hidden</type>
				<rowSpace>4</rowSpace>
				<treeFgColor>00ff00</treeFgColor>
				<treeFont>Monospaced-1-22</treeFont>
			</element>


+ private int     elementLevel;
+ private int     rowSpace;
+ private String  name;     // (Часть, Глава, Эпизод)
+ private String  type    = "work";
 // Tree
+ private Color   treeFgColor;
+ private Font    treeFont;
 // Editor
 private Color   textColor;
 private Font    textFont;

 private boolean    hasPrintElementName, hasPrintNumber;
 private BookNodeTypeNumeric typeNumeric;   // enum


 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.08.2011 17:45:43
 */
@Deprecated
public class BookElementStaxParser  extends WEditStaxParser
{
    // elementLevel - подсчитывает автоматом и заносит в обьект
    // todo treeIcon


    public void read ( XMLEventReader eventReader ) throws WEditException
    {
        String          tagName;
        XMLEvent        event;
        EndElement      endElement;
        boolean         bWork;

        try
        {
            bWork   = true;
            // Read the XML document

            while ( bWork )
            {
                if ( ! eventReader.hasNext() ) return;

                event = eventReader.nextEvent();

                if ( event.isStartElement() )
                {
                    /*
                    startElement = event.asStartElement();
                    tagName      = startElement.getName().getLocalPart();

                    if ( tagName.equals( ElementCons.ELEMENT) )
                    {
                        // Начало элемента - уровень уже распарсен выше
                        continue;
                    }

                    if ( tagName.equals( ElementCons.NAME) )
                    {
                        str = getText ( eventReader );
                        bookElement.setName(str);
                        continue;
                    }

                    if ( tagName.equals( ElementCons.TYPE) )
                    {
                        str = getText ( eventReader );
                        bookElement.setType (str);
                        continue;
                    }

                    if ( tagName.equals( ElementCons.ROW_SPACE) )
                    {
                        str = getText ( eventReader );
                        bookElement.setRowSpace (Integer.parseInt( str ));
                        continue;
                    }

                    if ( tagName.equals( ElementCons.TREE_FG_COLOR) )
                    {
                        str = getText ( eventReader );
                        bookElement.setTreeFgColor ( Convert.str2color ( str ) );
                        continue;
                    }

                    if ( tagName.equals( ElementCons.TYPE_NUMERIC) )
                    {
                        str = getText ( eventReader );
                        bookElement.setTypeNumeric ( str );
                        continue;
                    }

                    if ( tagName.equals( ElementCons.TREE_FONT) )
                    {
                        str = getText ( eventReader );
                        bookElement.setTreeFont ( Convert.str2font ( str ) );
                        continue;
                    }

                    if ( tagName.equals( ElementCons.TEXT_FG_COLOR) )
                    {
                        str = getText ( eventReader );
                        bookElement.setTextColor ( Convert.str2color ( str ) );
                        continue;
                    }

                    if ( tagName.equals( ElementCons.TEXT_FONT) )
                    {
                        str = getText ( eventReader );
                        bookElement.setTextFont ( Convert.str2font ( str ) );
                        continue;
                    }

                    if ( tagName.equals( ElementCons.PRINT_NAME) )
                    {
                        str = getText ( eventReader );
                        bookElement.setHasPrintElementName ( Convert.str2boolean ( str, false ) );
                        continue;
                    }

                    if ( tagName.equals( ElementCons.PRINT_NUMBER ) )
                    {
                        str = getText ( eventReader );
                        bookElement.setHasPrintNumber ( Convert.str2boolean ( str, false ) );
                        continue;
                    }

                    throw new  WEditException ( null, "Ошибка загрузки элемента Книги :\n Неизвестное имя стартового тега '", tagName, "'." );
                    */
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

        //} catch (WEditException ex) {
        //    throw ex;
        } catch (Exception e) {
            Log.file.error ("err",e);
            throw new  WEditException ( e, "Ошибка загрузки элемента Книги :\n", e );
        }
    }

}
