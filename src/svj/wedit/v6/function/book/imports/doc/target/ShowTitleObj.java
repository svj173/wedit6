package svj.wedit.v6.function.book.imports.doc.target;


import svj.wedit.v6.tools.Convert;

/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 21.12.14 21:21
 */
public class ShowTitleObj
{
    private String  title;
    private int     level;

    public ShowTitleObj ( String title )
    {
        this ( title, -1 );
    }

    public ShowTitleObj ( String title, int level )
    {
        this.title = title;
        this.level = level;
    }

    public String getTitle ()
    {
        return title;
    }

    public int getLevel ()
    {
        return level;
    }

    public String toString()
    {
        StringBuilder result;

        result = new StringBuilder ( 64 );
        result.append ( "[ ShowTitleObj : title = " );
        result.append ( getTitle() );
        result.append ( "; level = " );
        result.append ( getLevel() );
        result.append ( " ]" );

        return result.toString();
    }

    public void setLevel ( Object value )
    {
        if ( value == null )
            level = -1;
        else
            level = Convert.getInt ( value, -1 );
    }

}
