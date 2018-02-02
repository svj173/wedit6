package svj.wedit.v6.obj.open;


import java.util.ArrayList;
import java.util.Collection;


/**
 * Информация об открытом Сборнике. Берется при запуске Редактора из файла user_params.xml.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 21.02.2012 14:41:21
 */
public class ProjectInfo
{
    private final String fileName;
    private final Collection<BookInfo> openBooks;


    public String toString()
    {
        StringBuilder result;
        
        result  = new StringBuilder();

        result.append ( "[ ProjectInfo: fileName = '" );
        result.append ( getFileName() );
        result.append ( "'; openBooks size =" );
        result.append ( getOpenBooks().size() );
        result.append ( " ]" );

        return result.toString();
    }

    public ProjectInfo ( String fileName )
    {
        this.fileName = fileName;
        openBooks     = new ArrayList<BookInfo>();
    }

    public String getFileName ()
    {
        return fileName;
    }

    public void addBook ( BookInfo bookInfo )
    {
        openBooks.add ( bookInfo );
    }

    public Collection<BookInfo> getOpenBooks ()
    {
        return openBooks;
    }
    
}
