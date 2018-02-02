package svj.wedit.v6.function.book.imports.we1;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookCons;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.book.TextObject;
import svj.wedit.v6.tools.BookStructureTools;
import svj.wedit.v6.tools.StyleTools;
import svj.wedit.v6.xml.WEditStaxParser;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Загрузчик XML книги формата WE-1.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 04.04.2013 13:44
 */
public class We1BookStaxParser  extends WEditStaxParser // implements Handler
{
    //private BookContent bookContent;

    /** Хранилище древовидных обьектов для выдачи результата */
    //private DefaultMutableTreeNode book = null;

    /** Текущий древовидный обьект. */
    //private DefaultMutableTreeNode currentNode;

    /** Имя текущего тэга - используется при получении текста. */
    private String          currentTagName;

    private TextObject      currentTextObject;
    //private Properties openChapters;
    private Properties      openContent;
    //private Properties      functionParam;
    //private Hashtable functions;
    private String          chapterName;

    // Флаг что сейчас обрабатывается - блок по открытом фрагменте или блок по ифне об окне Содержания
    private boolean isOpenChapter   = false;

    // Константы
    private final String    A_NAME    = "name";
    /* Имя xml-тэга начала книги. */
    private final int   XMLBOOK     = -1946313824;  // xmlBook
    private final int   BOOK        = 3029737; // book
    private final int   NODE        = 3386882; // node
    private final int   ELEMENT_NAME = -573369874; // element_name
    private final int   TITLE_VK    = -1869999620; // title_vk
    private final int   ATTRIBUTES  = 405645655; // attributes
    private final int   ATTRIBUTE   = 13085340; // attribute
    private final int   TEXT        = 3556653; // text
    private final int	TEXT_OBJECT	= -819487087; // text_object
    private final int	END_LINE	= 1725313240; // end_line
    private final int	STR	        = 114225; // str
    private final int	STYLE	    = 109780401; // style
    private final int	OPEN_CHAPTERS	= -650540005; // open_chapters
    private final int	OPEN_CONTENT	= 1438455684; // open_content
    private final int	NAME	    = 3373707; // name
    //private final int	FUNCTIONS	= -140572773; // functions
    private final int	PARAMS	    = -995427962; // params
    private final int	FUNCTION	= 1380938712; // function
    private final int	PARAM	    = 106436749; // param
    private final int	CHAPTER	    = 739015757; // chapter
    private final int	VALUE	    = 111972721; // value

    private final int	PLACE	    = 106748167; // place
    private final int	X	= 120; // x
    private final int	Y	= 121; // y
    private final int	WIDTH	= 113126854; // width
    private final int	HEIGHT	= -1221029593; // height

    private final int	EOL	= 100610; // eol

    private final QName name             = new QName ( BookCons.NAME );
    private final QName style            = new QName ( "style" );


    //-----------------------------------------------------------------------------------
    public We1BookStaxParser ()  throws WEditException
    {
        //this.em = em;
    }

    public void read ( File file, BookContent bookContent, String codePage ) throws WEditException
    {
        InputStream in;
        String      realName;

        Log.file.debug ( "Start. fileName = '", file, "'; codePage = ", codePage );

        try
        {
            if ( ! file.exists() )
                throw new WEditException ( null, "Отсутствует файл '", file, "'");

            in          = new FileInputStream ( file );

            //   -- Имя файла потмо перепишется.
            //bookContent = new BookContent ( bookTitle, file.getAbsolutePath() );
            //bookContent.setFileSize ( file.length () );
            // Занести дефолтную структуру
            bookContent.setBookStructure ( BookStructureTools.getDefaultStructure () );
            //result.setProjectDir ( file.getParentFile() );   // Заносим директорию

            read ( in, codePage, bookContent );

            /*
            // Взять действительное имя книги
            realName    = bookContent.getName();
            Log.file.debug ( "real name = ", realName );

            // валидация элементов  -- ????
            try
            {
                BookStructureTools.validateBookStructure ( bookContent.getBookStructure(), realName );
            } catch ( WEditException e )            {
                // Ошибки в описании элементов - сообщаем и подставляем дефолтную структуру.
                bookContent.setBookStructure ( BookStructureTools.getDefaultStructure () );
                // Как-то сказать панели что было изменение - флаг в BookContent
                DialogTools.showMessage ( Convert.concatObj ( "Ошибка структуры описания елементов книги '", realName, "'" ),
                                          Convert.concatObj ( "Ошибка : ", e.getMessage (), "\nСоздана структура 'по-умолчанию'." ) );
            }
            */

            Log.file.info ( "Finish. fileName = '", file, "'");

        } catch ( WEditException ex )        {
            throw ex;
        } catch ( Exception e )        {
            Log.file.error ("err",e);
            throw new WEditException ( e, "Системная ошибка чтения и конвертации файла \n книги '", file, "' :\n ", e );
        }
    }

    private void read ( InputStream in, String codePage, BookContent bookContent ) throws WEditException
    {
        String          tagName, str;
        XMLEvent        event;
        StartElement    startElement;
        EndElement      endElement;
        XMLEventReader  eventReader;
        Attribute       attr;
        XMLInputFactory inputFactory;
        BookNode        bookNode, bNode;
        int             eventType;

        try
        {
            // Read the XML document

            /*
            // Всегда создаем нулевой текст
            bookNode        = new BookNode ( bookContent.getName(), null );
            bookContent.setBookNode ( bookNode );

            // test childs
            bNode = new BookNode ( "Test-1", bookNode );
            bookNode.addBookNode ( bNode );
            bNode = new BookNode ( "Test-2", bookNode );
            bookNode.addBookNode ( bNode );
            */

            bookNode = null;

            inputFactory = XMLInputFactory.newInstance();
            if ( codePage == null )
                eventReader  = inputFactory.createXMLEventReader ( in );
            else
                eventReader  = inputFactory.createXMLEventReader ( in, codePage );
            //eventReader  = inputFactory.createXMLEventReader(in,"UTF-8");
            //eventReader  = inputFactory.createXMLEventReader(new FileReader ());

            while ( eventReader.hasNext() )
            {
                event       = eventReader.nextEvent();

                eventType   = event.getEventType ();
                //Log.file.debug ( "eventType  = ", eventType );     // 1,2,4 - startElement, endElement, characters

                switch ( eventType )
                {
                    case XMLEvent.START_ELEMENT :
                        startElement = event.asStartElement();
                        tagName      = startElement.getName().getLocalPart();
                        int tagCode;

                        currentTagName  = tagName;
                        tagCode         = currentTagName.hashCode ();

                        switch ( tagCode )
                        {
                            case XMLBOOK:
                            case TITLE_VK:
                            case ATTRIBUTES:
                            case TEXT:
                            case BOOK:
                                // Ничего не делаем
                                break;

                            case ATTRIBUTE:
                                str = getText ( eventReader );
                                // bookNode требуестя самой первой в фомате WE-1
                                /*
                                if ( bookNode == null )
                                {
                                    Log.file.debug ( "---- create new bookNode. tagName = ", tagName );
                                    bookNode        = new BookNode ( bookContent.getName(), null );
                                    bookContent.setBookNode ( bookNode );
                                }
                                */
                                //bookNode = getBookNode ( bookNode, bookContent, tagName );
                                bookNode.setAnnotation ( bookNode.getAnnotation() + "/n"+str );
                                break;

                            case ELEMENT_NAME:
                                // Анализируем на тип - скрытый. В WE-1 он встречается только для части и для Главы.
                                // - берем текст
                                str = getText ( eventReader );
                                if ( str.startsWith ( "hidden" ))
                                {
                                    // установить текущему обьекту тип - hidden.
                                    bookNode.setElementType ( "hidden" );
                                }
                                break;

                            case NODE:
                                // Взять name
                                attr    = startElement.getAttributeByName ( name );
                                if ( attr == null )
                                    str     = null;
                                else
                                {
                                    str     = attr.getValue();
                                    str     = str.replace ( '"', '\'' );
                                }
                                // Создать корневой элемент книги и сделать его текущим.
                                bNode       = new BookNode ( str, bookNode );
                                if ( bookNode == null )
                                {
                                    bookContent.setBookNode ( bNode );
                                    bookContent.setName ( str );
                                }
                                else
                                    bookNode.addBookNode ( bNode );
                                bookNode    = bNode;
                                // Добавить пустые строки
                                bookNode.addEol();
                                bookNode.addEol();
                                //if ( str != null )  bookNode.setName ( str );
                                //node            = new DefaultMutableTreeNode ( bookNode );
                                //if ( book == null )     book  = node;  // Это корень
                                //else   currentNode.add ( node );
                                //currentNode     = node;
                                break;

                            case EOL:
                            case END_LINE:
                                //currentTextObject.addText("\n");
                                //TextObject to   = new TextObject ();
                                //BookNodeObject bookNode    = (BookNodeObject) currentNode.getUserObject();
                                //to.addText("\n");
                                //bookNode = getBookNode ( bookNode, bookContent, tagName );
                                bookNode.addEol ();
                                break;

                            case STR:
                            //case TEXT_OBJECT:
                                // Создать текстовый обьект.
                                currentTextObject   = new TextObject ();
                                //bookNode    = (BookNode) currentNode.getUserObject ();
                                //bookNode = getBookNode ( bookNode, bookContent, tagName );
                                bookNode.addText ( currentTextObject );
                                // Если есть стиль - добавить
                                attr    = startElement.getAttributeByName ( name );
                                if ( attr != null )
                                {
                                    AttributeSet style   = createStyle ( attr.getValue() );
                                    currentTextObject.setStyle ( style );
                                }
                                str = getText ( eventReader );
                                currentTextObject.addText ( str );
                                break;

                            case OPEN_CHAPTERS:
                                // Создать обьект открытых глав.
                                isOpenChapter   = true;
                                //openChapters    = new Properties ();
                                break;

                            case OPEN_CONTENT:
                                // Создать обьект открытого Содержания.
                                isOpenChapter   = false;
                                openContent     = new Properties ();
                                break;

                            //case FUNCTIONS:
                            case PARAMS:
                                // Создать обьект параметров функций для книги.
                                //functions    = new Hashtable ();
                                break;

                            case FUNCTION:
                                // Создать обьект параметров функции.
                                //functionParam    = new Properties ();
                                //if ( str != null )  functions.put ( str, functionParam );
                                break;

                            case CHAPTER:
                                // Создать имя Части.
                                //chapterName    = str;
                                //openChapters.put ( "name", str );
                                break;

                            case PARAM:
                                // Создать имя параметра.
                                //chapterName    = str;
                                break;

                            //default:
                                //throw new  WEditException ( null, "Ошибка загрузки файла Книги :\n Неизвестное имя стартового тега '", tagName, "'." );
                                //break;
                        }
                        break;

                    case XMLEvent.START_DOCUMENT :
                    case XMLEvent.END_DOCUMENT :
                        // Ничего здесь не делаем
                        break;

                    case XMLEvent.END_ELEMENT :
                        endElement   = event.asEndElement();
                        tagName      = endElement.getName().getLocalPart();
                        tagCode      = tagName.hashCode();

                        switch ( tagCode )
                        {
                            case BOOK:
                                // Сформировать обьект результата
                                //bookContent.setBook ( book );
                                break;
                            case EOL:
                            case END_LINE:
                                //currentTextObject.addText("\n");
                                //TextObject to   = new TextObject ();
                                //BookNodeObject bookNode    = (BookNodeObject) currentNode.getUserObject();
                                //to.addText("\n");
                                //bookNode.addText ( new EolTextObject() );
                                break;
                            case OPEN_CHAPTERS:
                                //bookContent.setOpenChapters ( openChapters );
                                break;
                            case OPEN_CONTENT:
                                //bookContent.setOpenContent ( openContent );
                                break;
                            //case FUNCTIONS:
                            case PARAMS:
                                //bookContent.setFunctionParams ( functions );
                                break;
                            case NODE:
                                // Закончился узел. А вместе  с ним и все его вложенные узлы.
                                //  Подняться по дереву выше.
                                //currentNode = (DefaultMutableTreeNode) currentNode.getParent ();
                                //bookNode    = getBookNode ( bookNode, bookContent, tagName );
                                bookNode    = bookNode.getParentNode();
                                break;
                        }
                        currentTagName = "";
                        break;

                    case XMLEvent.CHARACTERS :
                        // Это в основном пустые строки в структуре XML документа.
                        //Log.file.debug ( "CHARACTERS event  = ", event );
                        break;

                    //default :
                    //    throw new  WEditException ( null, "Ошибка загрузки файла Книги :\n Неизвестный тип события '", eventType, "'." );
                }
            }
        } catch ( WEditException ex ) {
            throw ex;
        } catch ( Exception e ) {
            Log.file.error ("err",e);
            throw new  WEditException ( e, "Ошибка загрузки файла Книги формата WE-1 :\n", e );
        }
    }

    private BookNode getBookNode ( BookNode bookNode, BookContent bookContent, String tagName )
    {
        BookNode result;

        //Log.file.debug ( "--- bookNode for tagName = ", tagName, " : ", bookNode );
        if ( bookNode == null )
        {
            Log.file.debug ( "---- create new bookNode 2. tagName = ", tagName );
            result        = new BookNode ( bookContent.getName(), null );
            bookContent.setBookNode ( result );
        }
        else
        {
            result = bookNode;
        }

        return result;
    }

    /**
     * Метод вызывается, когда появляется какой-нибудь текст.
     * Как текст внутри тэга, так и текст между тэгами.
     * Например: < ab>Text1< /ab>   < ab>.... - в этом случае метод TEXT
     * вызовется два раза - как для Text1 так и для пробелов (либо переход на
     * новую строку) между < /ab>   < ab>
     *
     * @param  text  Текст. Внутри него возможно появление переносов строк,
     *  проставленные вручную - удалить.
     * @throws Exception
     */
    /*
    public void text ( String text ) throws Exception
    {
        String  str, menuTitle;
        int tagCode, ic;
        BookNode bookNode;
        AttributeSet style;

        // Убрать переносы строк
        str = text.replace ( '\n', ' ' );
        str = str.replace ( '\r', ' ' );
        // Заменить символы  &lt; &gt; на <>
        str = str.replace ( "&lt;", "<" );
        str = str.replace ( "&gt;", ">" );
        str = str.replace ( "&amp;", "&" );
        // Убрать первые и последние пробелы
        str = str.trim();

        tagCode         = currentTagName.hashCode ();

        switch ( tagCode )
        {
            case ELEMENT_NAME:
                bookNode    = (BookNodeObject) currentNode.getUserObject ();
                bookNode.setElementName ( str );
                menuTitle     = em.createContentName ( bookNode.getName (), str );
                bookNode.setContentName ( menuTitle );
                break;
            case TITLE_VK:
                try
                {
                    ic      = Integer.parseInt ( str );
                } catch ( Exception e )                {
                    ic      = 1;
                }
                bookNode    = (BookNodeObject) currentNode.getUserObject ();
                bookNode.setTitleVk(ic);
                break;
            case ATTRIBUTE:
                bookNode    = (BookNodeObject) currentNode.getUserObject ();
                bookNode.addAttribute ( str );
                break;

            case STR:
                currentTextObject.setText ( str );
                break;
            case STYLE:  // НЕ используется уже
                style   = createStyle ( str );
                currentTextObject.setStyle ( style );
                break;
            case NAME:
                chapterName = str;
                break;
            case PARAM:
                functionParam.put ( chapterName, str );
                break;
            case VALUE:
                functionParam.put ( chapterName, str );
                break;

            // Параметры открытых фрагментов и окна Содержания
            case PLACE:
            case X:
            case Y:
            case HEIGHT:
            case WIDTH:
                //logger.debug ( "currentTagName = " + currentTagName + ", text = " + str );
                if ( isOpenChapter )
                    openChapters.put ( currentTagName, str );
                else
                    openContent.put ( currentTagName, str );
                break;
        }

    }
    */
    /**
     * Создать обьект стиля из его текстового описания.
     * @param str Текстовое представление стиля.
     * @return Стиль как обьект.
     */
    private AttributeSet createStyle ( String str )
    {
        SimpleAttributeSet result;
        result  = StyleTools.createStyle ( str );
        return result;
    }

    /* Генерим хэш-код названия тэга */
    public static void main ( String[] args )
    {
        String str, title;
        int hashCode;

        str         = "eol";
        title       = str.toUpperCase();
        hashCode    = str.hashCode();

        //     private final int XMLBOOK = -1946313824;  // xmlBook
        System.out.println ( "private final int\t" + title + "\t= " + hashCode + "; // " + str );
    }

}

