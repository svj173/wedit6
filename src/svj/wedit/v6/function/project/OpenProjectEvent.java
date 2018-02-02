package svj.wedit.v6.function.project;


import java.io.File;


/**
 * Обьект события об открытии проекта.
 * <BR/> Для передачи в Reopen.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 12.08.2011 13:56:50
 */
public class OpenProjectEvent
{
    public enum Mode { NONE_EXIST, OK }

    private File    file;
    private Mode    mode;
    private String  projectName;


    public void setMode ( Mode mode )
    {
        this.mode = mode;
    }

    public void setProjectFile ( File file )
    {
        this.file = file;
    }

    public String toString()
    {
        StringBuilder result;

        result  = new StringBuilder ( 64 );
        result.append ( "[ OpenProjectEvent: mode = " );
        result.append ( getMode () );
        result.append ( "; projectName = '" );
        result.append ( getProjectName() );
        result.append ( "'; file = '" );
        result.append ( getFile() );
        result.append ( "' ] " );

        return result.toString ();
    }

    public String getProjectName ()
    {
        return projectName;
    }

    public void setProjectName ( String projectName )
    {
        this.projectName = projectName;
    }

    public File getFile ()
    {
        return file;
    }

    public Mode getMode ()
    {
        return mode;
    }
    
}
