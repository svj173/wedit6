package svj.wedit.v6.obj;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookTitle;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.Utils;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Раздел проекта.
 * <BR/> Содержит как другие секции-разделы так и книги.
 * <BR/> При отображении в дереве сначала выводятся другие разделы, потом - книги.
 * <BR/> Вывод - в алфавитном порядке.
 * <BR/>
 <section name="Имя раздела (здесь - название проекта)">
     <section name="">
         <section name="">
             <book name="Имя книги">book file</book>
         </section>
         <book name="Имя книги">book file</book>
     </section>

     <book name="Имя книги">book file</book>
 </section>
 
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 27.07.2011 15:48:44
 */
public class Section    extends WTreeObj implements Comparable<Section>
{
    /* Имя файла-директории (т.е. как правило - по-английски */
    private String   fileName;

    /* Вложенные в данную секцию подсекции. */
    private final List<Section>         sections;     // List - т.к. нужен get

    /* Список книг, входящих в данную секцию. */
    private final Collection<BookTitle> bookTitles;


    public Section ( String name )
    {
        this ( name, name );
    }

    public Section ( String name, String dirName )
    {
        setName ( name );

        fileName    = dirName;
        sections    = new ArrayList<Section>();
        bookTitles  = new ArrayList<BookTitle>();

        setParent ( null );
    }

    public Section clone ()
    {
        Section result;

        result = new Section ( getName (), getFileName() );

        for ( Section section : getSections () )
        {
            result.addSection ( section.clone() );
        }
        for ( BookTitle bookTitle : getBooks () )
        {
            result.addBook ( bookTitle.clone() );
        }

        // from WTreeObj
        result.setAnnotation ( getAnnotation() );
        result.setParent ( getParent() );
        result.setIndex ( getIndex() );

        return result;
    }

    @Override
    public int getSize ()
    {
        int result;

        result = 0;
        for ( Section section : getSections() )  result = result + section.getSize();
        for ( BookTitle bookTitle : getBooks() ) result = result + bookTitle.getSize();

        return result + getSize ( getName() ) + getSize ( getAnnotation() );
    }

     @Override
    public String toString ()
    {
        StringBuilder result;

        result = new StringBuilder(512);

        result.append ( "[ Section :" );
        result.append ( " name = " );
        result.append ( getName() );
        result.append ( "; fileName = " );
        result.append ( getFileName() );
        result.append ( "; parent = " );
        result.append ( getParent() );

        result.append ( " ]" );

        return result.toString();
    }

    @Override
    public TreeObjType getType ()
    {
        return TreeObjType.SECTION;
    }


    @Override
    public Collection<WTreeObj> getChildrens ()
    {
        Collection<WTreeObj>    result;
        int                     size;

        result  = null;
        size    = sections.size() + bookTitles.size();
        if ( size > 0 )
        {
            result  = new ArrayList<WTreeObj> ( size );
            result.addAll ( sections );
            result.addAll ( bookTitles );
        }

        return result;
    }

    @Override
    public void toXml ( int level, OutputStream out ) throws WEditException
    {
        String str;
        int    ic;

        try
        {
            ic  = level + 1;

            str = Convert.concatObj ( "<section name=\"", getName(), "\" dirName=\"", getFileName(), "\">\n" );
            //outTitle ( level, "section", getName(), out );
            outString ( level, str, out );

            if ( hasSections() )
            {
                for ( Section section : getSections() )  section.toXml ( ic, out );
            }

            if ( hasBooks() )
            {
                for ( BookTitle bookTitle : getBooks() )  bookTitle.toXml ( ic, out );
            }

            outString ( level, "</section>\n", out );

        } catch ( WEditException we )        {
            throw we;
        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML представления Раздела '", getName(), "' в поток :\n", e );
        }
    }

    @Override
    public String getTreeIconFilePath ()
    {
        return Convert.concatObj ( "img/tree/", Par.TREE_ICON_SIZE, "/section.png" );
    }

    public Collection<Section> getSections ()
    {
        return sections;
    }

    public Collection<BookTitle> getBooks ()
    {
        return bookTitles;
    }

    public boolean hasSections()
    {
        return ( ! sections.isEmpty() );
    }

    public boolean hasBooks()
    {
        return ( ! bookTitles.isEmpty() );
    }

    public void addSection ( Section section )
    {
        sections.add ( section );
    }

    public void addSection ( int number, Section section )
    {
        sections.add ( number, section );
    }

    public void addBook ( BookTitle bookTitle )
    {
        bookTitles.add ( bookTitle );
        bookTitle.setParent ( this );
    }

    public boolean deleteBook ( BookTitle bookTitle )
    {
        return bookTitles.remove ( bookTitle );
    }

    public boolean delete ( WTreeObj wTreeObj )
    {
        if ( wTreeObj == null )  return true;

        if ( wTreeObj instanceof BookTitle )
        {
            BookTitle bookTitle = (BookTitle)  wTreeObj;
            return deleteBook ( bookTitle );
        }
        else if ( wTreeObj instanceof Section )
        {
            Section section = ( Section ) wTreeObj;
            return deleteSection ( section );
        }
        return false;
    }

    public Section getFirstSection ()
    {
        if ( sections.isEmpty() )
            return null;
        else
            return sections.get(0);
    }

    
    @Override
    public int compareTo ( Section book )
    {
        int iName, iSect;

        if ( book == null )  return 1;

        iName   = Utils.compareToWithNull ( getName(), book.getName() );
        if ( iName == 0 )
        {
            iSect   = Utils.compareToWithNull ( getSections().size(), book.getSections().size() );
            if ( iSect == 0 )
                return Utils.compareToWithNull ( getBooks().size(), book.getBooks().size() );
            else
                return iSect;
        }
        else
            return iName;
    }

    public String getFileName ()
    {
        return fileName;
    }

    public void setFileName ( String fileName )
    {
        this.fileName = fileName;
    }

    public boolean deleteSection ( Section section )
    {
        return sections.remove ( section );
    }


    public BookTitle getBookTitle ( String fileName )
    {
        String      fn;
        BookTitle   bt;

        // Ищем здесь
        for ( BookTitle bookTitle : bookTitles )
        {
            // fn - только имя файла. fileName - полный путь файла
            fn  = bookTitle.getFileName();
            Log.l.debug ( "[BTEdit] fileName = %s; fn = %s", fileName, fn );
            if ( fileName.endsWith ( fn ) )  return  bookTitle;
        }

        // Ищем в парентах
        for ( Section section : sections )
        {
            bt = section.getBookTitle ( fileName );
            if ( bt != null )  return bt;
        }

        return null;
    }

}
