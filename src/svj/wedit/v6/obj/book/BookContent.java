package svj.wedit.v6.obj.book;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.IId;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.Editable;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.XmlAvailable;
import svj.wedit.v6.obj.book.element.WBookElement;
import svj.wedit.v6.tools.BookStructureTools;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.StringTools;
import svj.wedit.v6.tools.Utils;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * Обьект содержимого книги.
 * <BR/> Кроме текста книги включает в себя структуру книги и описание динамических параметров.
 * <BR/> Входит в BookTitle.
 * <BR/>
 * <BR/> ИД - это полное имя файла книги: /home/svj/Serg/SvjStores/test/test01.book
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.08.2011 15:52:32
 */
public class BookContent   extends XmlAvailable  implements IId, Editable, Comparable<BookContent>
{
    private String          name;

    private BookStructure   bookStructure;

    // Деревянная структура книги - корневой элемент.
    private BookNode        bookNode;
    // Параметры функций уровня Книги - Преобразовать книгу...
    private final BookParams      bookParams;

    /** Уникальный идентификатор.*/
    private String          id;

    /* Абсолютное имя файла книги (для последующего сохранения).
    Используется только на время работы Редактора. Заносится сюда при открытии книги. */
    private String          fileName;

    private String          annotation, synopsis;

    /* Флаг используется только при стартовой загрузке книги, когда нарушена структура описания книги и подставляется
    дефолтная - чтобы пометить книгу как измененную - для записи в файл. */
    private boolean         editMode;
    /* Ссылка на индивидуальную иконку книги. При загрузке иконки для табиков - подгоняется по размеру к 16х16,
    24х24 - к имеющеся линейке размеров. */
    //private String          bookIconPath;

    /* Запоминаем предыдущее значение размера файла - чтобы показывать динамику изменения при каждой правке-схранении. */
    private long            fileSize;

    // Аттрибуты книги в виде пар значений: ключ=значение. Ключи - есть фиксированный набор (create_date, last_change_date),
    // а также можно добавлять свои.
    // - запрет на xml-спецсимволы - анализ при добавлении нового атрибута.
    private final Map<String,String> bookAttrs  = new HashMap<String,String> ();

    /** Сборник, в который входит данная книга. */
    private Project         project;

    /** Статус книги: в работе, завершена, только началось и т.д. */
    private BookStatus      bookStatus;

    // Эпиграф
    private String[] epigraphText;
    private String epigraphAuthor;


    public BookContent ( String name, String fileName )
    {
        // убираем двойные кавычки, т.к. этот текст заносится в xml-файле в атрибут name="текст" - т.е. поломается структура.
        if ( (name != null) &&  name.contains ( "\"" ) )  name = name.replace ( '"', '\'' );
        this.name       = name;
        this.fileName   = fileName;
        //id              = Utils.createId();

        bookStructure   = null;  // дефолтная структура
        bookNode        = null;
        project         = null;
        bookParams      = new BookParams();
        editMode        = false;
        // пока фиксируем
        //bookIconPath    = "img/book_16.png";

        fileSize        = 0l; // - начальное значение заносится при загрузке книги из файла.

        bookStatus      = BookStatus.WORK;
    }

    /* "Чистый" размер книги. В размер не входят bookStructure, bookParams. */
    @Override
    public int getSize ()
    {
        // В размер не входят bookStructure, bookParams
        return getSize ( getName() ) + getSize(annotation) + getSize(synopsis) + getSize(bookNode);
    }

    @Override
    public String getId ()
    {
        String result;
        if ( StringTools.isEmpty ( id ) )
        {
            if ( fileName == null )
                result = "";
            else
                result = fileName;
        }
        else
        {
            result = id;
        }
        return result;
    }

    public void setId ( String id )
    {
        this.id = id;
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder ( 128 );

        result.append ( " [ BookContent : name = '" );
        result.append ( getName() );
        result.append ( "'; id = '" );
        result.append ( getId() );
        result.append ( "'; fileName = '" );
        result.append ( getFileName() );

        result.append ( "' ]" );

        return result.toString();
    }


    /* Преобразовать в XML для сохранения. И скинуть в поток. level = 0 - самое начало. */
    @Override
    public void toXml ( int level, OutputStream out ) throws WEditException
    {
        int    ic;

        try
        {
            ic  = level + 1;

            outTitle ( level, "bookContent", getName(), out );

            outTag ( ic, "id", getId (), out );
            outTag ( ic, "annotation", getAnnotation(), out );

            outTag ( ic, "synopsis",   getSynopsis(), out );

            outTag ( ic, "status",   getBookStatus().getName(), out );

            // todo эпиграф. текст - как массив строк, выделенных тегами P
            // - требуется свой парсер для Эпиграфа

            // bookStructure
            if ( hasBookStructure() ) getBookStructure().toXml ( ic, out );

            // book attributies
            if ( hasBookAttributies() ) attributiesToXml ( ic, out );

            // all node
            if ( hasBookNode() )    getBookNode().toXml ( ic, out );

            // параметры функций уровня книги - в самих этих функциях мап - название_книги=значение_параметра.
            // Либо функция изначально свои значения хранит в книге.
            if ( hasBookParams() ) getBookParams().toXml ( ic, out );

            endTag ( level, "bookContent", out );

        } catch ( WEditException we )        {
            throw we;
        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML представления Книги '", getName(), "' в поток :\n", e );
        }
    }

    private void attributiesToXml ( int level, OutputStream out )  throws WEditException
    {
        int    ic, ic2;

        try
        {
            ic  = level + 1;
            ic2 = level + 2;

            outTitle ( level, BookCons.BOOK_ATTRIBUTIES, out );

            // - attrs
            for ( Map.Entry<String,String> entry: getBookAttrs().entrySet() )
            {
                // int tabMargin, String tag, String name, String text, OutputStream out
                outTag ( ic, BookCons.BOOK_ATTRIBUTE, entry.getKey(), entry.getValue(), out );
            }

            endTag ( level, BookCons.BOOK_ATTRIBUTIES, out );

        //} catch ( WEditException we )        {
        //    throw we;
        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML представления аттрибутов книги в поток :\n", e );
        }
    }

    private boolean hasBookAttributies ()
    {
        return ! getBookAttrs().isEmpty();
    }

    public boolean hasBookStructure ()
    {
        return bookStructure != null;
    }

    public boolean hasBookNode ()
    {
        return bookNode != null;
    }

    public boolean hasBookParams ()
    {
        return ! bookParams.isEmpty();
    }

    public String getName ()
    {
        return name;
    }

    public void setName ( String name )
    {
        this.name = name;
    }

    public BookStructure getBookStructure ()
    {
        return bookStructure;
    }

    public void setBookStructure ( BookStructure bookStructure )
    {
        this.bookStructure = bookStructure;
        //setEdit ( false );
    }

    public BookParams getBookParams ()
    {
        return bookParams;
    }

    /*
    public void setBookParams ( BookParams bookParams )
    {
        this.bookParams = bookParams;
    }
    */
    public BookNode getBookNode ()
    {
        return bookNode;
    }

    public void setBookNode ( BookNode bookNode )
    {
        this.bookNode = bookNode;
        this.bookNode.setBookContent ( this );
    }

    /**
     *
     * @param styleName  уровень_тип
     * @return           элемент
     */
    public WBookElement getElement ( String styleName )
    {
        if ( styleName == null )  return null;

        int level = BookStructureTools.getLevel ( styleName );

        return bookStructure.getElement ( level );
    }

    /*
    public BookElement getBookElement ( BookNode bookNode )
    {
        return bookStructure.get ( bookNode.getLevel(), bookNode.getElementType() );
    }
    */

    public String getFileName ()
    {
        return fileName;
    }

    public void setFileName ( String fileName )
    {
        this.fileName = fileName;
    }

    public String getAnnotation ()
    {
        return annotation;
    }

    public void setAnnotation ( String annotation )
    {
        this.annotation = annotation;
    }

    public void addAnnotation ( String text )
    {
        if ( annotation == null )
            annotation  = text;
        else
            annotation = getAnnotation() + '\n' + text;
    }

    public String getSynopsis ()
    {
        return synopsis;
    }

    public void setSynopsis ( String synopsis )
    {
        this.synopsis = synopsis;
    }

    @Override
    public void setEdit ( boolean editMode )
    {
        this.editMode = editMode;
    }

    @Override
    public boolean isEdit ()
    {
        return editMode;
    }

    // 0,2,3,1  - индексы от корня
    /*
    public BookNode  ( String fullPathName )
    {
        String[] path;
        BookNode node;

        // WCons.SEP;
        // Разделить путь на имена
        path    = fullPathName.split ( ""+WCons.COMMA );

        node    = bookNode;
        // Пробегаем от корня
        for ( int i = path.length-1; i>0; i-- )
        {
            Log.l.debug ( "--- ", i, ") ",path[i] );
            if ( node.getName().equals ( path[i] ) )
        }
        return node;
    }
    */

    public BookNode getBookNodeByFullPath ( String nodePath )
    {
        String[]    path;
        int         i, ic;
        String      str;
        BookNode    node, node2;

        path    = nodePath.split ( "," );
        node    = bookNode;
        for ( i=0; i<path.length; i++ )
        {
            //logger.debug ( i + "---- path = " + path[i] );
            // адрес узла - чистим крайние пробелы
            str     = path[i].trim();
            if ( (str == null) || str.isEmpty() ) break;
            ic      = Integer.parseInt ( str );
            node2   = node.getChildAt ( ic );
            if ( node2 == null ) break;
            node    = node2;
        }
        //logger.debug ( "---- Get node = " + node );
        return node;
    }

    public String getBookIconPath ( int iconSize )
    {
        return Convert.concatObj ( "img/editor/book_",iconSize,".png" );   // это дефолтное значение. У каждой книги - свое.
    }

    public long getFileSize ()
    {
        return fileSize;
    }

    public void setFileSize ( long fileSize )
    {
        this.fileSize = fileSize;
    }

    /**
     * Выдать глубину вложенности элементво книги.
     * <br/> Необходимо пробежаться по всей книге.
     * <br/>
     * @return Глубина вложенности.
     */
    public int getMaxLevel ()
    {
        // + 1 - т.к. счет от 0.
        return getMaxLevel ( bookNode ) + 1;
    }

    public int getMaxLevel ( BookNode node )
    {
        int         result, ic;

        result = node.getLevel();
        for ( BookNode node2 : node.getNodes() )
        {
            ic = getMaxLevel ( node2 );
            if ( ic > result ) result = ic;
        }
        return result;
    }

    @Override
    public int compareTo ( BookContent o )
    {
        if ( o == null )
            return -1;
        else
            return Utils.compareToWithNull ( getId(), o.getId() );
    }

    public boolean equals ( Object o )
    {
        if ( o == null ) return false;
        if ( o instanceof  BookContent)
            return compareTo ( (BookContent) o ) == 0;
        else
            return false;
    }

    public Map<String, String> getBookAttrs ()
    {
        return bookAttrs;
    }

    public void addAttribute ( String attrName, String attrValue )
    {
        bookAttrs.put ( attrName, attrValue );
    }

    /**
     * Создать последний элемент заданного уровня.
     * <br/> Здесь сначала необходимо найти подходящего парента, и уж потом привязаться к нему.
     * <br/>
     * @param bookNodeTitle    Заголовок нового элемента.
     * @param level            Уровень нового элемента.
     * @return                 Новый элеимент книги.
     */
    public BookNode createNextBookNode ( String bookNodeTitle, int level )
    {
        BookNode parentNode, result, bo;
        int      ic;

        ic = level - 1;
        Log.file.debug ( "--- DOC import: level = %s; parentLevel = %s", level, ic );
        // ищем парента для указанного уровня.
        parentNode  = getBookNode();
        if ( ic > 0 )
        {
            //parentNode  = getLastBookNode ( getBookNode(), level -1 );
            for ( int i = 0 ; i<ic; i++ )
            {
                // Берем последнего среди всех данного уровня.
                bo  = parentNode.getLastNode();
                if ( bo  == null )
                {
                    // нет узла - разрыв. Необходимо создать.
                    bo  = new BookNode ( "N-"+i, parentNode );
                    parentNode.addBookNode ( bo );
                }
                parentNode = bo;
                if ( parentNode.getLevel() == ic )    break;
            }
        }

        Log.file.debug ( "--- DOC import: bookNodeTitle = %s; parentNode = %s", bookNodeTitle, parentNode );
        result  = new BookNode ( bookNodeTitle, parentNode );
        parentNode.addBookNode ( result );

        return result;
    }

    /**
     * убрать Служебные главы
     */
    public void clear ()
    {
       if ( bookNode != null )  bookNode.clear();
    }

    public void setProject ( Project project )
    {
        this.project = project;
    }

    public Project getProject ()
    {
        return project;
    }

    public BookStatus getBookStatus ()
    {
        return bookStatus;
    }

    public void setBookStatus ( BookStatus bookStatus )
    {
        this.bookStatus = bookStatus;
    }

    /**
     * Эпиграф.
     * @param text Текст эпиграфа может быть многострочным. Его необходимо разбить на массив строк.
     */
    public void setEpigraphText(String text) {
        if (text == null) return;

        String[] strs = text.split("\n");
        epigraphText = strs;
    }

    public void setEpigraphAuthor(String text) {
        if (text == null) return;
        epigraphAuthor = text;
    }

}
