package svj.wedit.v6.obj.book.xml;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.*;
import svj.wedit.v6.obj.book.element.StyleType;
import svj.wedit.v6.tools.BookTools;
import svj.wedit.v6.tools.ElementCons;
import svj.wedit.v6.tools.StringTools;
import svj.wedit.v6.tools.StyleTools;
import svj.wedit.v6.xml.WEditStaxParser;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.*;
import java.io.FileInputStream;


/**
 * Распарсить из потока текст книги.
 * <BR/> Структура книги - элементы распарсиваются своим парсером
 * <BR/>

 Сама книга - как дерево
  - node - главы книги, подглавы и т.д.
  - ab   - абзац, который заканчивается переводом строки.
  - str  - кусок текста. Применяется когда текст в абзаце разбивается на участки разных стилей - цветов. Может содержать атрибут style.
  - eol  - безусловный перевод строки.
  - pf   - параграф. Это абзац, который содержит стиль (не обязательно) и описание на левое и правое смещения (alignStyle - обязательно).
             Например: Эпиграф.

 <node name="Звездный странник. Книга 5. Мегаполис-2">

     <node name="Вступление">
         <ab>Пролетев несколько метров, Сергей пулей влетел в мягкий сугроб, вскольз плечом больно ударившись обо что-то
             твердое. И тот час тело обожгло ледянным холодом жесткого снега, накрывшего его с головой. Судорожно замахав
             руками, он тут же попытался выбраться, но не смог - голова сильно кружилась, неимоверно тошнило, и от всего
             этого он все никак не мог сориентироваться - где же здесь верх, а где - низ.
         </ab>
         <str>Стуча зубами, Сергей наконец-то ухватился за что-то - явно колесо машины, и потянулся, стараясь выбраться
             из сугроба.
         </str>
         <eol/>
         <str style="color:red">Пробираясь на ощупь в рыхлом снеге по промятому им при падении тоннелю, и машинально прикидывая про себя -
             в какой мир он попал на этот раз, он высунул голову наружу и на мгновение замер.
         </str>
     </node>

     <node name="Подземка">
     </node>
 </node>

 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.08.2011 17:45:43
 */
public class BookNodeStaxParser extends WEditStaxParser
{
    private static final QName  name             = new QName("name");
    private static final QName  style            = new QName("style");


    public void read ( XMLEventReader eventReader, BookNode rootBookNode ) throws WEditException
    {
        String                  tagName, str;
        XMLEvent                event;
        StartElement            startElement;
        Attribute               attr;
        EndElement              endElement;
        boolean                 bWork;
        TextObject              to;
        BookNode                bookNode, bNode;
        int                     level, eventType;
        WEditStyle              attrStyle;
        Characters              characters;

        Log.file.debug ( "NodeParser.Start: rootBookNode = %s", rootBookNode );

        try
        {
            bWork       = true;
            bookNode    = rootBookNode;
            level       = 0;
            tagName     = null;

            // Read the XML document

            while ( bWork )
            {
                if ( ! eventReader.hasNext() ) return;

                event       = eventReader.nextEvent();

                eventType   = event.getEventType();
                //Log.file.debug ( "eventType  = ", eventType );

                switch ( eventType )
                {
                    case XMLEvent.START_ELEMENT :
                        startElement = event.asStartElement();
                        tagName      = startElement.getName().getLocalPart();

                        if ( tagName.equals( ElementCons.BOOK_NODE) )
                        {
                            //bookStructure    = new BookStructure();
                            // Начало нового элемента. Вставить в текущий.
                            attr    = startElement.getAttributeByName ( name );
                            if ( attr == null )
                                throw new WEditException ("Отсутствует название главы");
                            str     = attr.getValue();
                            level++;
                            //bNode       = new BookNode ( str, level, bookNode );       // уровень - от 1 делаем пока
                            bNode       = new BookNode ( str, bookNode );       
                            bookNode.addBookNode ( bNode );
                            bookNode    = bNode;
                            continue;
                        }


                        if ( tagName.equals(ElementCons.STR) )
                        {
                            str     = getText ( eventReader );
                            //Log.file.debug ( "str = '", str, "'" );
                            to      = new TextObject ( str );
                            // Если есть стиль - добавить
                            attr    = startElement.getAttributeByName ( style );
                            if ( attr != null )
                            {
                                //Log.file.debug ( "--- style text = '", attr.getValue(), "'" );
                                //attrStyle   = createStyle ( attr.getValue() );
                                attrStyle   = StyleTools.createStyle ( attr.getValue(), StyleType.COLOR_TEXT, StyleType.COLOR_TEXT.getName() );
                                //Log.file.debug ( "--- style = '", style, "'" );
                                to.setStyle ( attrStyle );
                            }
                            if ( bookNode == null )
                                rootBookNode.addText ( to );  // Чтобы текст кот случайно находится вне структур - не потерялся.
                            else
                                bookNode.addText ( to );
                            continue;
                        }

                        if ( tagName.equals(ElementCons.SLN) )
                        {
                            str     = getText ( eventReader );
                            if ( str == null )  str = "";    // для ситуации sln/sln - отк+закр теги без текста
                            //Log.file.debug ( "str = '", str, "'" );
                            // Если у текста нет в конце перевода строки - добавляем
                            if ( str.indexOf ( '\n' ) < 0 )  str = str + "\n";
                            to      = new SlnTextObject ( str );
                            // Если есть стиль - добавить
                            attr    = startElement.getAttributeByName ( style );
                            if ( attr != null )
                            {
                                //Log.file.debug ( "--- style text = '", attr.getValue(), "'" );
                                //attrStyle   = createStyle ( attr.getValue() );
                                attrStyle   = StyleTools.createStyle ( attr.getValue(), StyleType.COLOR_TEXT, StyleType.COLOR_TEXT.getName() );
                                //Log.file.debug ( "--- style = '", style, "'" );
                                to.setStyle ( attrStyle );
                            }
                            if ( bookNode == null )
                                rootBookNode.addText ( to );  // Чтобы текст кот случайно находится вне структур - не потерялся.
                            else
                                bookNode.addText ( to );
                            continue;
                        }

                        if ( tagName.equals(ElementCons.NODE_TYPE) )
                        {
                            str = getText ( eventReader );
                            bookNode.setElementType ( str );
                            continue;
                        }

                        if ( tagName.equals(ElementCons.ANNOTATION) )
                        {
                            str = getText ( eventReader );
                            Log.file.debug ( "--- ANNOTATION = '%s'", str );
                            bookNode.addAnnotation ( str );
                            continue;
                        }

                        if ( tagName.equals( BookCons.ID ) )
                        {
                            // читаем текст
                            str = getText ( eventReader );
                            if ( StringTools.isEmpty ( str ) ) {
                                //Log.file.debug ( "[STRONG] id is NULL. title = '%s'", str, bookNode.getName() );
                                str = BookTools.createBookNodeId ( bookNode.getName() );
                            }
                            bookNode.setId ( str );
                            continue;
                        }

                        if ( tagName.equals(ElementCons.TEXT) )
                        {
                            // это просто указатель на начало текста.
                            continue;
                        }

                        if ( tagName.equals(ElementCons.EOL) )
                        {
                            to   = new EolTextObject();
                            //to.addText("\n");
                            if ( bookNode == null )
                                rootBookNode.addText ( to );  // Чтобы текст кот случайно находится вне структур - не потерялся.
                            else
                                bookNode.addText ( to );
                            continue;
                        }

                        if ( tagName.equals(ElementCons.IMG) )
                        {
                            str = getText ( eventReader );
                            to  = new ImgTextObject ( str );
                            //to.addText("\n");
                            if ( bookNode == null )
                                rootBookNode.addText ( to );  // Чтобы текст кот случайно находится вне структур - не потерялся.
                            else
                                bookNode.addText ( to );
                            continue;
                        }

                        throw new  WEditException ( null, "Ошибка загрузки файла описания Структуры книги :\n Неизвестное имя стартового тега '", tagName, "'." );
                        //break;

                    case XMLEvent.CHARACTERS:
                        // Если в теге текст разделен переносами строк - то каждая такая строка генерит отдельное событие CHARACTERS
                        characters = ( Characters ) event;
                        if ( (!characters.isIgnorableWhiteSpace()) && (!characters.isWhiteSpace() ) )
                        {
                            str = characters.getData();
                            Log.file.debug ( "--- NodeParser: CHARACTERS. tagName = %s; str = '%s'", tagName, str );
                            if ( (tagName != null) && tagName.equals(ElementCons.ANNOTATION) )
                            {
                                // Аннотация еще продолжается.
                                Log.file.debug ( "--- ANNOTATION continue = '%s'", str );
                                bookNode.addAnnotation ( str );
                            }
                            else
                            {
                                if ( str.length() != 0 )
                                {
                                    if ( bookNode != null )
                                    {
                                        bookNode.addText ( new TextObject ( str ) );
                                        // добавляем признак перевода строки
                                        bookNode.addText ( new EolTextObject () );
                                    }
                                }
                            }
                        }
                        break;

                    case XMLEvent.END_ELEMENT :
                        endElement  = event.asEndElement();
                        tagName     = endElement.getName().getLocalPart();

                        if ( tagName.equals(ElementCons.BOOK_TAG) )
                        {
                            // Конец структуры текста книги
                            bWork = false;
                            continue;
                        }

                        if ( tagName.equals(ElementCons.BOOK_NODE) )
                        {
                            // Конец главы текста книги - поднимаем уровень
                            level--;
                            bookNode    = bookNode.getParentNode();
                        }
                        break;
                }
            }

        } catch ( WEditException ex ) {
            throw ex;
        } catch ( Exception e ) {
            Log.file.error ( "err", e );
            throw new  WEditException ( e, "Ошибка загрузки файла описания Структуры книги '",rootBookNode.getName(),"' :\n", e );
        } finally      {
            Log.file.debug ( "NodeParser.Finish: rootBookNode = %s", rootBookNode );
        }
    }

    /**
     * Создать обьект стиля из его текстового описания.
     * @ param str Текстовое представление стиля.
     * @return Стиль как обьект.
     */
    /*
    private AttributeSet createStyle ( String str )
    {
        SimpleAttributeSet result;
        result  = StyleTools.createStyle ( str, StyleType.TEXT, StyleName.TEXT );
        return result;
    }
    */

    public static void main ( String[] args )
    {
        BookNodeStaxParser      parser;
        XMLEventReader          eventReader;
        Attribute               attr;
        XMLInputFactory         inputFactory;
        FileInputStream         in;
        String                  fileName;

        try
        {
            parser          = new BookNodeStaxParser();

            fileName        = "/home/svj/projects/SVJ/WEdit-6/test/test_sb/proza/srok_avansom_ISH.book";
            in              = new FileInputStream ( fileName );
            inputFactory    = XMLInputFactory.newInstance();
            eventReader     = inputFactory.createXMLEventReader(in);

            parser.read ( eventReader, new BookNode("123",null) );

        } catch ( Exception e )        {
            e.printStackTrace();
        }
    }
    
}
