package svj.wedit.v6.function.service.search;


import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.tools.Utils;

/**
 * Обьект, опиcывающий результаты поиска.
 * Поиск то ведется в обьектах а не в редакторе - какие тут курсоры?
 *
 * Должен хранить:
 * <LI> 1) полный путь до фрагмента - для формирования дерева;
 * <LI> 2) точку вхождения (позиция курсора);
 * <LI> 3) кол-во попаданий в данном фрагменте; -- ???
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 18.12.2013 16:42
 */
public class SearchObj     implements Comparable<SearchObj>
{
    private String path;
    private int cursor;
    /* Строка, в которйо нашли. */
    private String text;
    /* Текст, который искали. */
    private String searchText;
    /* Элемент, в котором нашли текст. */
    private BookNode bookNode;
    /** Порядковые номер найденного текста - если в одном тексте было найдено более одного раза. */
    private final int number;

    public SearchObj ( BookNode node, String text, String searchText, int number )
    {
        this.bookNode   = node;
        this.text       = text;
        this.searchText = searchText;
        this.number     = number;
    }

    public SearchObj ( String text )
    {
        this ( null, text, null, 0 );
    }

    public SearchObj ()
    {
        this ( null, null, null, 0 );
    }

    public void setText ( String text )
    {
        this.text = text;
    }


    @Override
    public int compareTo ( SearchObj o )
    {
        if ( o == null ) return 1;
        return Utils.compareToWithNull ( getText(), o.getText() );
    }

    public boolean equals ( Object obj )
    {
        boolean result;

        result = false;
        if ( (obj != null) && (obj instanceof SearchObj ) )
        {
            SearchObj so = (SearchObj) obj;
            result = compareTo ( so ) == 0;
        }
        return result;
    }

    public String getText ()
    {
        return text;
    }

    public String getSearchText ()
    {
        return searchText;
    }

    public void setSearchText ( String searchText )
    {
        this.searchText = searchText;
    }

    public BookNode getBookNode ()
    {
        return bookNode;
    }

    public void setBookNode ( BookNode bookNode )
    {
        this.bookNode = bookNode;
    }

    public int getNumber ()
    {
        return number;
    }

    @Override
    public String toString() {
        String result = "SearchObj{" +
                "path='" + path + '\'' +
                ", cursor=" + cursor +
                ", text='" + text + '\'' +
                ", searchText='" + searchText + '\'' +
                ", bookNode=" + (bookNode == null ? "Null" : bookNode.getId()) +
                ", number=" + number +
                '}';
        return result;
    }
}
