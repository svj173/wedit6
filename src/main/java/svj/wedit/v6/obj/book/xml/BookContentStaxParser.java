package svj.wedit.v6.obj.book.xml;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.*;
import svj.wedit.v6.tools.*;
import svj.wedit.v6.xml.WEditStaxParser;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;


/**
 * Распарсить из потока книгу.
 * <BR/> Структура книги - элементы распарсиваются своим парсером.
 * <BR/>
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.08.2011 17:45:43
 */
public class BookContentStaxParser   extends WEditStaxParser
{
    private final QName name             = new QName ( BookCons.NAME );

    /**
     *
     * @param file   Это файл книги
     * @param name   Название книги -- нет, оно еще неизвестно. Это "222-3333".
     * @return Книгу в полном обьеме
     * @throws svj.wedit.v6.exception.WEditException Ош
     */
    public BookContent read ( File file, String name, String bookId ) throws WEditException
    {
        BookContent result;
        InputStream in;
        String      realName;
        Date        date;

        Log.file.debug ( "Start. fileName = '%s'; name = %s", file, name );

        try
        {
            if ( ! file.exists() )
                throw new WEditException ( null, "Отсутствует файл '", file, "'");

            in      = new FileInputStream ( file );

            result  = new BookContent ( name, file.getAbsolutePath() );
            result.setFileSize ( file.length() );
            result.setId ( bookId );
            //result.setProjectDir ( file.getParentFile() );   // Заносим директорию
            Par.CURRENT_PARSE_BOOK = result;

            // всегда добавляем фиксированные атрибуты. Если они есть в книге, то они заменятся на реальные.
            date    = new Date();
            result.addAttribute ( BookCons.ATTR_NAME_CREATE_DATE, Convert.getEnDateTime ( date ) );
            result.addAttribute ( BookCons.ATTR_NAME_LAST_CHANGE_DATE, Convert.getEnDateTime ( date ) );

            read ( in, result );

            // Взять действительное имя книги
            realName    = result.getName();
            Log.file.debug ( "real name = %s", realName );

            // валидация элементов
            try
            {
                BookStructureTools.validateBookStructure ( result.getBookStructure(), realName );

            } catch ( WEditException e )            {
                // Ошибки в описании элементов - сообщаем и подставляем дефолтную структуру.
                //result.setBookStructure ( BookStructureTools.getDefaultStructure() );
                result.getBookStructure().setDefault();
                // Как-то сказать панели что было изменение - флаг в BookContent
                DialogTools.showMessage ( Convert.concatObj ( "Ошибка структуры описания елементов книги '", realName, "'" ),
                                          Convert.concatObj ( "Ошибка : ", e.getMessage(), "\nСоздана структура 'по-умолчанию'." ) );
            }


            Log.file.info ( "Finish. fileName = '%s'", file);

        } catch ( WEditException ex )        {
            throw ex;
        } catch ( Exception e )        {
            Log.file.error ("err",e);
            throw new WEditException ( e, "Системная ошибка чтения файла опиcания\n книги '", file, "' :\n ", e );
        }

        Par.CURRENT_PARSE_BOOK = null;

        return result;
    }

    public void read ( InputStream in, BookContent bookContent ) throws WEditException
    {
        String                  tagName, str;
        XMLEvent                event;
        StartElement            startElement;
        XMLEventReader          eventReader;
        Attribute               attr;
        XMLInputFactory         inputFactory;
        BookStructure           bookStructure;
        BookStructureStaxParser structureParser;
        BookNodeStaxParser      nodeParser;
        BookAttrStaxParser      attrParser;
        EpigraphStaxParser      epigraphParser;
        BookFunctionParamsParser      fpParser;
        BookNode                bookNode;
        int                     eventType;

        try
        {
            structureParser = new BookStructureStaxParser();
            nodeParser      = new BookNodeStaxParser();
            attrParser      = new BookAttrStaxParser();
            fpParser        = new BookFunctionParamsParser();
            epigraphParser  = new EpigraphStaxParser();

            // Всегда создаем нулевой текст
            bookNode        = new BookNode ( bookContent.getName(), null );
            bookContent.setBookNode ( bookNode );

            //bookNode = null;
            str     = null;

            inputFactory = XMLInputFactory.newInstance();
            eventReader  = inputFactory.createXMLEventReader(in);
            //eventReader  = inputFactory.createXMLEventReader(in,"UTF-8");
            //eventReader  = inputFactory.createXMLEventReader(new FileReader ());

            while ( eventReader.hasNext() )
            {
                event = eventReader.nextEvent();

                /**
                 * Returns an integer code for this event.
                 * @see #START_ELEMENT            - 1
                 * @see #END_ELEMENT              - 2
                 * @see #CHARACTERS               - 4
                 * @see #ATTRIBUTE                - 10
                 * @see #NAMESPACE
                 * @see #PROCESSING_INSTRUCTION
                 * @see #COMMENT
                 * @see #START_DOCUMENT           - 7
                 * @see #END_DOCUMENT             - 8
                 * @see #DTD
                 */
                eventType   = event.getEventType ();
                Log.file.debug ( "eventType  = %d", eventType );

                switch ( eventType )
                {
                    case XMLEvent.START_ELEMENT :  // 1
                        startElement = event.asStartElement();
                        tagName      = startElement.getName().getLocalPart();
                        Log.file.debug ( "--- tagName  = %s", tagName );

                        if ( tagName.equals( BookCons.BOOK_CONTENT) )
                        {
                            // Начало документа
                            attr    = startElement.getAttributeByName ( name );
                            if ( attr == null )
                                throw new WEditException ("Отсутствует название книги");
                            str     = attr.getValue();
                            bookContent.setName ( str );
                            bookNode.setName ( str );
                            continue;
                        }

                        if ( tagName.equals( BookCons.BOOK_STRUCTURE) )
                        {
                            Log.file.debug ( "--- Start BOOK_STRUCTURE" );
                            bookStructure    = new BookStructure();
                            bookContent.setBookStructure ( bookStructure );
                            structureParser.read ( eventReader, bookStructure );
                            // проверяем правильность данных
                            if ( bookStructure.isWrong() )  bookStructure.setDefault();
                            Log.file.debug ( "--- Finish BOOK_STRUCTURE" );
                            continue;
                        }

                        if ( tagName.equals( BookCons.BOOK_ATTRIBUTIES) )
                        {
                            Log.file.debug ( "--- Start BOOK_ATTRIBUTIES" );
                            attrParser.read ( eventReader, bookContent );
                            Log.file.debug ( "--- Finish BOOK_ATTRIBUTIES" );
                            continue;
                        }

                        if ( tagName.equals( BookCons.BOOK_NODE) )
                        {
                            nodeParser.read ( eventReader, bookNode );
                            continue;
                        }

                        if ( tagName.equals( BookCons.ANNOTATION) )
                        {
                            // читаем текст
                            str = getText ( eventReader );
                            bookContent.addAnnotation ( str );
                            continue;
                        }

                        if ( tagName.equals( BookCons.ID ) )
                        {
                            // читаем текст
                            str = getText ( eventReader );
                            if ( StringTools.isEmpty ( str ) )  str = BookTools.createBookNodeId ( bookContent.getName() );
                            bookContent.setId ( str );
                            continue;
                        }

                        if ( tagName.equals( BookCons.SYNOPSIS) )
                        {
                            // читаем текст
                            str = getText ( eventReader );
                            bookContent.setSynopsis ( str );
                            continue;
                        }

                        if ( tagName.equals( BookCons.EPIGRAPH) )
                        {
                            // вызываем парсер эпиграфа  - epigraphParser
                            epigraphParser.read ( eventReader, bookContent );
                            continue;
                        }

                        if ( tagName.equals( "status" ) )
                        {
                            // читаем название статуса книги
                            str = getText ( eventReader );
                            bookContent.setBookStatus ( BookStatus.getStatus(str) );
                            continue;
                        }

                        if ( tagName.equals( BookCons.BOOK_FUNCTION_PARAMS ) )
                        {
                            // Параметры функций для данной книги: парсим своим парсером
                            fpParser.read ( eventReader, bookContent );
                            continue;
                        }

                        if ( tagName.equals( BookCons.PARAMS) )
                        {
                            // блок параметров функций уровня Книги - необходим свой парсер. Возможно - для каждой функции - свой, т.к. сильно индивидуальная структура данных.
                            // - Либо для каждого типа параметра - свой парсер.
                            // ---------- НЕТ - это уже не нужно, т.к. значения функций сохраняются в профиле пользователя '.wedit6/user_params.xml'.
                            // -- НУЖНО, т.к. значения функций Конвертации индивидуальны для каждой книги.
                            // -- Реализовано в BOOK_FUNCTION_PARAMS
                            continue;
                        }

                        throw new  WEditException ( null, "Ошибка загрузки файла Книги '", bookContent.getName(), "' :\n Неизвестное имя стартового тега '", tagName, "'. \nold str = ", str );
                        //break;

                    case XMLEvent.START_DOCUMENT :   // 7
                    case XMLEvent.END_DOCUMENT :     // 8
                    case XMLEvent.END_ELEMENT :      // 2
                        // Ничего здесь не делаем
                        break;

                    case XMLEvent.CHARACTERS :       // 4
                        // Это в основном пустые строки в структуре XML документа.
                        Log.file.debug ( "---- CHARACTERS (4): file = %s; event  = %s", bookContent.getFileName(), event );
                        break;

                    default :
                        throw new  WEditException ( null, "Ошибка загрузки файла Книги '", bookContent.getName(), "' :\n Неизвестный тип события '", eventType, "'." );
                }
            }

        } catch (WEditException ex) {
            throw ex;
        } catch (Exception e) {
            Log.file.error ("err",e);
            throw new  WEditException ( e, "Ошибка загрузки файла Книги '", bookContent.getFileName(),"' :\n", e );
        }
    }

    /* Генерим хэш-код названия тэга */
    public static void main ( String[] args )
    {
        String                  str, title, fileName;
        int                     hashCode;
        BookContentStaxParser   parser;
        BookContent             bookContent;
        File                    file;

        str         = "synopsis";
        title       = str.toUpperCase();
        hashCode    = str.hashCode();

        System.out.println ( "private final int\t" + title + "\t= " + hashCode + "; // " + str );

        try
        {
            Par.MODULE_HOME = "/home/svj/projects/SVJ/WEdit-6/project";
            System.setProperty ( "module.home", Par.MODULE_HOME );

            //Log.init ( Par.MODULE_HOME + "/conf/logger.txt" );
            //Log.init ( "/home/svj/programm/ems_server/conf/logger.cfg" );
            
            parser      = new BookContentStaxParser();

            //fileName    = "/home/svj/projects/SVJ/WEdit-6/project/conf/book/we6_book.xml";
            fileName    = "/home/svj/projects/SVJ/WEdit-6/test/test_sb/proza/srok_avansom_ISH.book";
            file        = new File ( fileName );

            bookContent = parser.read ( file, "test_book", "12" );
            //System.out.println ( "bookContent :\n" + bookContent );
            System.out.println ( "bookContent :\n" );

            bookContent.toXml ( 0, System.out );

        } catch ( Exception e )         {
            e.printStackTrace();
        }
    }

}
