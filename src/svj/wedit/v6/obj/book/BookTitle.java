package svj.wedit.v6.obj.book;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.TreeObjType;
import svj.wedit.v6.obj.WTreeObj;
import svj.wedit.v6.tools.BookTools;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;


/**
 * Обьект Книга в дереве проектов.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 27.07.2011 15:46:15
 */
public class BookTitle extends WTreeObj implements Comparable<BookTitle>
{
    /*        todo
    // Параметры книги
                <attribute>Create date :</attribute>
                <attribute>Type : фэнтези</attribute>
                <attribute>Forma : роман</attribute>
                <attribute>Size :</attribute>
                <attribute>Рабочие названия: Снова Мегаполис</attribute>
                <attribute>SAVE_DATE: 03.08.2011 17:36:40.500</attribute>

    // Аннотация на книгу.
    
     */

    /* Строка, которая добавляется к названию книги. Например: главы с 1 по 24. Для отображения в дереве проекта. */
    private String  additionalName;

    /* Имя файла (только имя) - для гибкости перемещения проектов в другое место. */
    private String  fileName;

    /** Уникальное ИД, которое заносится в распарсенный обьект BookContent. */
    private String  id;

    /* Собственно содержимое книги. Если NULL - книга еще не была закачана из файла.
    * Вопрос - стоит ли хранить в памяти все открытые книги или ограничиться только хранением того что отображено на экране в панелях?
    * И При каждом изменении - закачивать в файл. И парсить каждый раз при получении книги?
    * 30 книг по 1 Мб - это всего лишь 30 Мб ОЗУ. */
    private BookContent  bookContent;
    private BookStatus   bookStatus;


    public BookTitle ( String name, String fileName )
    {
        setName (name);
        this.fileName   = fileName;
        bookStatus      = BookStatus.WORK;
        id              = BookTools.createBookNodeId ( name );
    }

    public BookTitle clone ()
    {
        BookTitle result;

        result = new BookTitle ( getName(), getFileName() );
        result.setAdditionalName ( getAdditionalName() );
        result.setBookStatus ( getBookStatus() );
        result.setBookContent ( getBookContent() );  // todo clone
        result.setId ( getId() );

        // from WTreeObj
        result.setAnnotation ( getAnnotation() );
        result.setParent ( getParent() );
        result.setIndex ( getIndex() );

        return result;
    }


    public String toString()
    {
        StringBuilder result;
        result  = new StringBuilder();

        result.append ( "[ BookTitle: name = " );
        result.append ( getName() );
        result.append ( "; id = '" );
        result.append ( getId() );
        result.append ( "; additionalName = '" );
        result.append ( getAdditionalName() );
        result.append ( "'; fileName = " );
        result.append ( getFileName() );
        result.append ( "'; bookContent = " );
        result.append ( getBookContent() );

        //result.append ( super.toString() );
        result.append ( " ]" );

        return result.toString();
    }


    @Override
    public int getSize ()
    {
        int result;

        result  = getSize ( getName() );
        result  = result + getSize ( getAdditionalName() );
        result  = result + getSize ( getBookContent() );

        return result;
    }


    @Override
    public Collection<WTreeObj> getChildrens ()
    {
        return null;
    }

    @Override
    public TreeObjType getType ()
    {
        return TreeObjType.BOOK;
    }

    @Override
    public void toXml ( int level, OutputStream out ) throws WEditException
    {
        try
        {
            outBookTitleTag ( level, out );

        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML представления Книги '", getName(), "' в поток :\n", e );
        }
    }

    protected void outBookTitleTag ( int tabMargin, OutputStream out ) throws IOException
    {
        String tabs, status;

        if ( bookStatus != null )
            status  = bookStatus.getName();
        else
            status  = BookStatus.DEFAULT_STATUS.getName();

        tabs    = createTabs ( tabMargin );

        // атрибуты тега : nsme, status
        outString ( tabs+"<book name=\""+ Convert.validateXml(getName())+ "\" status=\""+ status+ "\" id=\""+id +"\">", out );
        outText ( getFileName (), out );   // имя файла - как значение тега.
        outString ( "</book>\n", out );
    }

    public String getAdditionalName ()
    {
        return additionalName;
    }

    public void setAdditionalName ( String additionalName )
    {
        this.additionalName = additionalName;
    }

    public String getFileName ()
    {
        return fileName;
    }

    public void setFileName ( String fileName )
    {
        this.fileName = fileName;
    }

    @Override
    public String getTreeIconFilePath ()
    {
        return Convert.concatObj ( "img/tree/", Par.TREE_ICON_SIZE, "/book.png" );
    }

    public String getTreeIconBookStatusFilePath ()
    {
        BookStatus status;

        status = getBookStatus();
        if ( status == null )
        {
            status = BookStatus.DEFAULT_STATUS;
            setBookStatus ( status );
        }
        return "img/tree/"+ Par.TREE_ICON_SIZE+ "/"+ status.getIcon();
    }

    public BookContent getBookContent ()
    {
        return bookContent;
    }

    public void setBookContent ( BookContent bookContent )
    {
        this.bookContent = bookContent;
        if ( bookContent != null )  bookStatus = bookContent.getBookStatus();
    }

    @Override
    public int compareTo ( BookTitle bookTitle )
    {
        int iName;

        if ( bookTitle == null )  return 1;

        iName   = Utils.compareToWithNull ( getName(), bookTitle.getName() );
        if ( iName == 0 )
            return Utils.compareToWithNull ( getFileName(), bookTitle.getFileName() );
        else
            return iName;
    }

    public BookStatus getBookStatus ()
    {
        return bookStatus;
    }

    public void setBookStatus ( BookStatus bookStatus )
    {
        this.bookStatus = bookStatus;
    }

    public void reinit ()
    {
        if ( bookContent != null )  bookStatus = bookContent.getBookStatus();
    }

    public String getId ()
    {
        return id;
    }

    public void setId ( String id )
    {
        this.id = id;
    }
}
