package svj.wedit.v6.exception;


import svj.wedit.v6.tools.Convert;

import java.util.ArrayList;
import java.util.List;


/**
 * Сообщение об ошибке с возможностью хранения списка потомков.
 *
 * @author <a href="mailto:svj173@yahoo.com">Sergey Zhiganov</a>
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.07.2011 17:32:06
 */
public class Issue
{
    private  String errorMessage;

    /** Заголовок (принадлежность) сообщения. - ??? */
    private  String title;

    /** Список вложенных Issue обьектов. */
    private List<Issue> children;

    private Throwable   systemError = null;


    /**
     * Создание сообщения об ошибке
     * @param errorMessage сообщение об ошибке
     */
    public Issue ( String errorMessage )
    {
        this ( errorMessage, null );
    }

    public Issue()
    {
        this ( null, null );
    }

    /**
     * Создание сообщения об ошибке
     * @param errorMessage сообщение об ошибке
     * Метод не поднимать до public. Используйте IssueTracker.
     */
    public Issue ( String errorMessage, List<Issue> children )
    {
        this.errorMessage = errorMessage;
        //this.children = Collections.unmodifiableList( new ArrayList( children ) );
        if ( children != null )
            this.children = new ArrayList<Issue> ( children );
        else
            this.children = new ArrayList<Issue>();
    }

    /**
     * Возвращает сообщение об ошибке
     */
    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage ( String msg )
    {
        errorMessage    = msg;
    }


    public String getTitle ()
    {
        return title;
    }

    public void setTitle ( String title )
    {
        this.title = title;
    }

    /**
     * Возвращает список потомков-сообщений об ошибках в виде немодифицированного
     * списка
     * @return список <code>Issue</code>
     */
    public List getChildren()
    {
        return children;
    }

    /**
     * Возвращает признак наличия потомков-сообщений об ошибках
     * @return true - если есть потомки
     */
    public boolean hasChildren()
    {
        return !children.isEmpty();
    }

    public String toString()
    {
        return errorMessage;
    }

    /**
     * @return строка для отображения, содержит описания вложенных issue
     */
    public String toDisplayValue()
    {
        Issue           issue;
        StringBuilder   message;

        message = new StringBuilder( errorMessage );

        for ( Object aChildren : children )
        {
            issue = (Issue) aChildren;
            message.append("\n").append(issue.toDisplayValue());
        }

        return message.toString();
    }

    public void clear()
    {
        children.clear();
    }

    public void add ( Issue newIssue )
    {
        children.add( newIssue );
    }

    public void add ( String errorMessage )
    {
        add ( new Issue( errorMessage ) );
    }

    public void add ( Object ... msg )
    {
        add ( new Issue( Convert.concatObj(msg) ) );
    }


    public Throwable getSystemError ()
    {
        return systemError;
    }

    public void setSystemError ( Throwable systemError )
    {
        this.systemError = systemError;
    }
    
}
