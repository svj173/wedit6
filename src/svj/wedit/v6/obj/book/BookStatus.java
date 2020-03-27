package svj.wedit.v6.obj.book;


import svj.wedit.v6.obj.IName;

import java.util.Collection;
import java.util.TreeSet;

/**
 * Статус книги. Редактируется пользjвателем - к списку могут добавляться свои статусы.
 * <BR/>
 * <BR/> Есть дефолтный набор статусов: реализована, очередная редакция, в работе, болванка, наполнение материалами
 * <BR/> Есть дефолтный статус - на случай, если при парсинге книги такие статусы не будут найдены.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 29.01.2015 11:55
 */
public class BookStatus  implements Comparable<BookStatus>, IName
{
    public static final BookStatus DEFAULT_STATUS = BookStatus.WORK;

    public static final BookStatus RELEASE_FB2      = new BookStatus ( "Реализована (FB-2)", "Завершено создание "
            + "книги. Перечитано в FB2",
            "book_release_fb2.png", true );        // ярко-зеленый с надписью - FB2
    public static final BookStatus RELEASE      = new BookStatus ( "Реализована", "Завершено создание книги.", "book_release_2.png", true );        // ярко-зеленый
    public static final BookStatus RELEASE_2    = new BookStatus ( "Очередная редакция", "Завершено создание книги, но продолжает правиться.", "book_release.png", true );    // светло-зеленый
    public static final BookStatus TEMPLATE     = new BookStatus ( "Болванка", "Самое начало создания книги, исходная заготовка.", "book_template.png", true );     // светло-желтый
    public static final BookStatus ADDED        = new BookStatus ( "Наполнение материалами", "Книга пока только наполняется необходимыми материалами.", "book_added.png", true );  // св-голубой
    public static final BookStatus WORK         = new BookStatus ( "В работе", "Активное создание книги.", "book.png", true );       // исходный - белый

    public static final Collection<BookStatus> statusList = new TreeSet<BookStatus> ();   // для сортировки
    static
    {
        statusList.add ( RELEASE_FB2 );
        statusList.add ( RELEASE );
        statusList.add ( RELEASE_2 );
        statusList.add ( TEMPLATE );
        statusList.add ( ADDED );
        statusList.add ( WORK );
    }

    private String name, descr;
    /** Короткие имена файлов иконок. Расположены в директории img/tree/16-24 */
    private String icon;
    /** TRUE - данный статус нельзя редактировать, удалять - т.е. он из набора фиксированных статусов. */
    private boolean fix;

    public BookStatus ( String name, String descr, String icon, boolean fix )
    {
        this.name   = name;
        this.descr  = descr;
        this.icon   = icon;
        this.fix    = fix;
    }

    @Override
    public String getName ()
    {
        return name;
    }

    public String getDescr ()
    {
        return descr;
    }

    public String getIcon ()
    {
        return icon;
    }

    public boolean isFix ()
    {
        return fix;
    }

    @Override
    public int compareTo ( BookStatus status )
    {
        if ( status == null ) return 1;
        return getName().compareTo ( status.getName() );
    }

    public boolean equals ( Object obj )
    {
        boolean result;

        result = false;
        if ( ( obj != null) && (obj instanceof BookStatus) )
        {
            result = compareTo ( (BookStatus) obj ) == 0;
        }
        return result;
    }

    public static BookStatus getStatus ( String name )
    {
        BookStatus result;

        result = null;
        if ( name != null )
        {
            for ( BookStatus status : statusList )
            {
                if ( status.getName().equals ( name ) )
                {
                    result = status;
                    break;
                }
            }
        }
        if ( result == null )  result = BookStatus.DEFAULT_STATUS;
        return result;
    }

    public static Collection<BookStatus> getStatusList ()
    {
        return statusList;
    }

}
