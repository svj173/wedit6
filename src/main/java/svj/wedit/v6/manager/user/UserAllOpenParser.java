package svj.wedit.v6.manager.user;


import svj.wedit.v6.Par;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.ConfigParam;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.project.IProjectParser;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.XmlTools;

import java.io.FileOutputStream;


/**
 * Пробегает по всем открытым панелям и заносит инфу в файл user_params.xml обо всем открытом.
 * <BR/> В т.ч. - положение курсора и текущий выбранный обьект - чтобы открыть те же самые.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 07.04.2012 14:18:19
 */
public class UserAllOpenParser  implements IProjectParser<String>
{
    //private final   StringBuilder text    = new StringBuilder();
    private int     level;
    private FileOutputStream file;

    public UserAllOpenParser ( FileOutputStream file, int level )
    {
        this.file   = file;
        this.level  = level;
    }

    @Override
    public String getResult ()
    {
        //return text.toString();
        return null;
    }

    @Override
    public void startDocument ()
    {
    }

    @Override
    public void endDocument ()
    {
    }

    @Override
    public void startProject ( Project project )
    {
        try
        {
            XmlTools.startTag ( getFile(), ConfigParam.PROJECT, ConfigParam.FILE, project.getProjectDir().getAbsolutePath(), getLevel() );

        } catch ( Exception e )        {
            Log.l.error ( Convert.concatObj ( "Project '", project, "' error" ), e);
            saveErrorMsg ( ConfigParam.PROJECT+"_start", e );
        }
    }

    @Override
    public void endProject ( Project project )
    {
        try
        {
            XmlTools.endTag ( getFile(), ConfigParam.PROJECT, getLevel() );
        } catch ( Exception e )        {
            Log.l.error ( Convert.concatObj (  "Project '", project, "' error" ), e);
            saveErrorMsg ( ConfigParam.PROJECT+"_end", e );
        }
    }

    @Override
    public void startBook ( BookContent bookContent )
    {
        try
        {
            //XmlTools.createTag ( getFile(), ConfigParam.BOOK, bookContent.getFileName(), getLevel()+1 );
            XmlTools.startTag ( getFile(), ConfigParam.BOOK, "name", bookContent.getFileName(), getLevel()+1 );
        } catch ( Exception e )         {
            Log.l.error ( Convert.concatObj ( "Book '", bookContent, "' start error" ), e);
            saveErrorMsg ( ConfigParam.BOOK+"_start", e );
        }
    }

    @Override
    public void endBook ( BookContent bookContent )
    {
        try
        {
            XmlTools.endTag ( getFile(), ConfigParam.BOOK, getLevel()+1 );
        } catch ( Exception e ) {
            Log.l.error ( Convert.concatObj ( "Book '", bookContent, "' end error" ), e);
            saveErrorMsg ( ConfigParam.BOOK+"_end", e );
        }
    }

    /*
    Инфа о книге
    - полный путь
    - курсор
     */
    @Override
    public void startText ( BookNode bookNode, int textCursor )
    {
        try
        {
            //XmlTools.createTag ( getFile(), ConfigParam.BOOK, bookContent.getFileName(), getLevel()+1 );
            XmlTools.startTag ( getFile(), ConfigParam.CHAPTER, "name", bookNode.getFullPath(), getLevel()+2 );

            XmlTools.createTag ( getFile(), ConfigParam.TEXT_CURSOR, Integer.toString ( textCursor ), getLevel()+3 );

            XmlTools.endTag ( getFile(), ConfigParam.CHAPTER, getLevel()+2 );

        } catch ( Exception e )         {
            Log.l.error ( Convert.concatObj ( "Text '", bookNode, "' error" ), e);
            saveErrorMsg ( ConfigParam.BOOK+"_start", e );
        }
    }

    private void saveErrorMsg ( String tagName, Exception e )
    {
        try
        {
            String str = Convert.concatObj ( "<",tagName,">Error:",e, "</",tagName,">" );
            getFile().write ( str.getBytes( Par.CODE_BOOK ) );
        } catch ( Exception ee )            {
            Log.l.error ( Convert.concatObj ( "Save error for Object '", tagName, "' error" ), ee);
        }
    }

    private int getLevel ()
    {
        return level;
    }

    private FileOutputStream getFile ()
    {
        return file;
    }

}
