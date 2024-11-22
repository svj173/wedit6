package svj.wedit.v6.obj.open;


import java.util.ArrayList;
import java.util.Collection;


/**
 * Информация о ранее открытой книге.
 * <BR/> Используется при старте Редактора.
 * <BR/> Берется из файла user_params.xml
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 21.02.2012 14:41:21
 */
public class BookInfo
{
    private final String fileName;
    private final Collection<TextInfo> openTexts;


    public BookInfo ( String fileName )
    {
        this.fileName = fileName;
        openTexts     = new ArrayList<TextInfo>();
    }

    public String toString()
    {
        StringBuilder result;
        result  = new StringBuilder();

        result.append ( "[ BookInfo: fileName = '" );
        result.append ( getFileName() );
        result.append ( "'; openTexts size =" );
        result.append ( getOpenTexts().size() );
        result.append ( " ]" );

        return result.toString();
    }

    public String getFileName ()
    {
        return fileName;
    }

    public void addText ( TextInfo textInfo )
    {
        openTexts.add ( textInfo );
    }

    public Collection<TextInfo> getOpenTexts ()
    {
        return openTexts;
    }

}
