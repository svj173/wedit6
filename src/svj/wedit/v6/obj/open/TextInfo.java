package svj.wedit.v6.obj.open;


import svj.wedit.v6.tools.Convert;


/**
 * Информация об открытом тексте.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 21.02.2012 14:41:21
 */
public class TextInfo
{
    private final String fullPathName;
    private int cursor;


    public TextInfo ( String fullPathName )
    {
        this.fullPathName = fullPathName;
    }

    public String toString()
    {
        StringBuilder result;
        
        result  = new StringBuilder();

        result.append ( "[ TextInfo: fileName = '" );
        result.append ( getFullPathName() );
        result.append ( "'; cursor =" );
        result.append ( getCursor() );
        result.append ( " ]" );

        return result.toString();
    }

    public String getFullPathName ()
    {
        return fullPathName;
    }

    public int getCursor ()
    {
        return cursor;
    }

    public void setCursor ( String strCursor )
    {
        this.cursor = Convert.getInt ( strCursor, 0 );
    }

}
