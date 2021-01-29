package svj.wedit.v6.obj;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookTitle;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.Utils;

import java.io.OutputStream;
import java.util.*;


/**
 * Раздел проекта.
 * <BR/> Содержит как другие секции-разделы так и книги.
 * <BR/> При отображении в дереве сначала выводятся другие разделы, потом - книги.
 * <BR/> Вывод - в алфавитном порядке.
 * <BR/>
 * <pre>
 <section name="Римейк" dirName="remake">
		<section name="Гуляковский" dirName="guliakovskii">
			<book name="Белые колокола Реаны." status="Реализована" id="Белые_колокола_Реаны._2021_01_04_16_52_13_823">reana.book</book>
			<book name="Сезон туманов." status="В работе" id="Сезон_туманов._2021_01_04_16_52_13_823">sezon_tumanov.book</book>
		</section>
		<book name="Иллиада" status="В работе" id="Иллиада_2021_01_04_16_52_13_823">illiada.book</book>
		<book name="Римеки_планы" status="В работе" id="Римеки_планы_2021_01_04_16_52_13_823">remake_plans.book</book>
		<book name="А.Азимов. Конец вечности." status="Болванка" id="А.Азимов._Конец_вечности._2021_01_04_16_52_13_823">azimov_vechnost.book</book>
 </section>
 </pre>
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
    private final List<BookTitle> bookTitles;

    // Сборник, к которому принадлежит эта Секция.
    // не final, т.к. при переносе между Сборниками он может меняться.
    private Project project;


    // Исп когда создается Секция и когда создается Сборник.
    public Section(String name, Project project)
    {
        this ( name, name, project );
    }

    public Section ( String name, String dirName, Project project )
    {
        setName ( name );

        fileName    = dirName;
        sections    = new ArrayList<Section>();
        bookTitles  = new ArrayList<BookTitle>();

        this.project = project;

        setParent ( null );
    }

    public Section clone ()
    {
        Section result;

        // clone на Проект делать не надо, т.к. во всех остальных Секциях лежит сылка на один обьект Проекта
        result = new Section ( getName (), getFileName(), getProject() );

        for ( Section section : getSections() )
        {
            result.addSection ( section.clone() );
        }

        for ( BookTitle bookTitle : getBooks() )
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

    public void addBook (int position, BookTitle bookTitle) {
        bookTitles.add ( position, bookTitle );
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

}
