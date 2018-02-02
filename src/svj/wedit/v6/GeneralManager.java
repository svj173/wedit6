package svj.wedit.v6;


import svj.wedit.v6.content.ContentFrame;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.manager.ConfigManager;
import svj.wedit.v6.manager.FunctionManager;
import svj.wedit.v6.manager.ProjectManager;
import svj.wedit.v6.manager.user.UserParamsManager;
import svj.wedit.v6.obj.Author;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.DialogTools;

import javax.swing.*;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 05.05.2011 17:52:21
 */
public class GeneralManager
{
    private FunctionManager fm;
    private ContentFrame    frame;
    private ConfigManager   config;
    private final ProjectManager  projectManager;


    public GeneralManager ()
    {
        projectManager  = new ProjectManager();
    }

    /**
     * Проверка на несохраненное редактирование во всем проекте.
     * @return TRUE - было редактирование.
     */
    /*
    public boolean isEdit ()
    {
        return false;
    }
    */

    public void setFm ( FunctionManager fm )
    {
        this.fm = fm;
    }

    public void setContent ( ContentFrame content )
    {
        frame   = content;
    }

    public FunctionManager getFm ()
    {
        return fm;
    }

    public ContentFrame getFrame ()
    {
        return frame;
    }

    public void setConfig ( ConfigManager config )
    {
        this.config = config;
    }

    public ConfigManager getConfig ()
    {
        return config;
    }

    // --------------------- Project ------------------

    public void addProject ( Project project ) throws WEditException
    {
        // установить новый проект в систему (добавить к списку открытых проектов)).
        //projectManager.addProject ( project );
        getFrame().addProject ( project );
    }

    public boolean containProject ( String projectId ) throws WEditException
    {
        return projectManager.containProject ( projectId );
    }

    public void selectProject ( String projectId )
    {
        getFrame().selectProject ( projectId );
    }

    // ------------------------ Book -----------------------------

    /* установить книгу в систему (добавить к списку открытых книг)). */
    public void addBookContent ( BookContent bookContent, Project project ) throws WEditException
    {
        getFrame().addBookContent ( bookContent, project );
    }

    public boolean containBook ( String bookId, Project project ) throws WEditException
    {
        return getFrame().containBook ( bookId, project );
    }

    public void selectBook ( String bookId, Project project )
    {
        getFrame().selectBook ( bookId, project );
    }

    public TreePanel<BookContent> selectBook ( String bookId, String projectId )
    {
        return getFrame().selectBook ( bookId, projectId );
    }

    // ----------------- Node --------------------

    /* установить книгу в систему (добавить к списку открытых книг)). */
    public void addBookText ( BookNode bookNode, BookContent bookContent, int cursor ) throws WEditException
    {
        getFrame().addBookNode ( bookNode, bookContent, cursor );
    }

    public boolean containNode ( String nodeId, BookContent bookContent  ) throws WEditException
    {
        return getFrame().containNode ( nodeId, bookContent );
    }

    /**
     * Сделать текущим табик в панели открытых текстов.
     * @param nodeId       ИД панели-табика
     * @param bookContent  Книга.
     */
    public void selectNode ( String nodeId, BookContent bookContent  )
    {
        getFrame().selectNode ( nodeId, bookContent );
    }

    public TextPanel selectNode ( String chapterId, String bookId  )
    {
        return getFrame().selectNode ( chapterId, bookId );
    }

    public void setFocus ( String chapterId, String bookId  )
    {
        getFrame().setFocus ( chapterId, bookId );
    }


    /**
     * <BR/> Корректное закрытие Редактора - НЕ аварийное. С вопросами клиенту.
     * <BR/> Алгоритм:
     * <BR/> - Если было редактирование - сообщить об этом пользователю (список). Не захочет ничего сохранять - не сохранять.
     * <BR/> - Не было редактирования - ничего не делать.
     * <BR/> todo А сохранение профилей? Списка открытых книг, положение маркера в тексте?
     */
    public void close ()
    {
        StringBuilder       editMsg;
        int                 inum;
        UserParamsManager   upm;

        // Пробегаемся по всем открытым проектам. Внутри - по всем открытым книгам. Собираем список редактированных мест.
        editMsg = getEditInfo();

        if ( editMsg.length() > 0 )
        {
            // Если есть данные в списке - Есть флаг Редактирования - запрос клиенту - Надо ли сохранять?
            inum = DialogTools.showConfirmDialog ( getFrame(), editMsg.toString(), "Сохранить все изменения", "Не сохранять" );
            if ( inum == JOptionPane.YES_OPTION )
            {
                // todo Сохраняем изменения
            }
            else
            {
                // Если сохранять НЕ надо
                // todo - Удаляем из буфера обработанные книги и проекты - чтобы когда запустим System.exit - и поднимется Shutdown - работы ему не осталось.
            }
        }

        // Сохраняем параметры пользователя
        upm = new UserParamsManager ();
        upm.close ( true );
    }

    /* Выдать инфу обо всех отредактирвоанных но не сохраненных местах - во всех проектах. */
    private StringBuilder getEditInfo ()
    {
        StringBuilder result;

        result  = new StringBuilder();

        /* todo CardPanel
        // Цикл по панелям деревьев книг
        for ( TreePanel<BookContent> bootTreePanel : getFrame().getBookContentPanel().getPanels() )
        {
            // BookContent
            if ( bootTreePanel.isEdit() )
            {
                result.append ( Convert.concatObj ( "Изменено содержание книги '", bootTreePanel.getObject().getName(), "'.\n" ) );
            }
        }
        */
        
        // todo Цикл по Редакторам книг -- хотя в предыдущем случае также сохраняется книга - т.е. это будет один и тот же файл - иметь в виду.
        // todo - Здесь лучше изменения в Редакторе - менять флаг в дереве а не в Редакторе
        /*
        for ( EditorPanel editorPanel : getFrame().getEditorsPanel().getPanels() )
        {
            // BookContent
            if ( bootTreePanel.isEdit() )
            {
                result.add ( Convert.concatObj ( "Изменено содержание книги '", bootTreePanel.getObject().getName(), "'." ) );
            }
        }
        */

        return result;
    }

    /* todo  НЕ корректное закрытие Редактора. Без вопросов клиенту. */
    public void alarmClose ()
    {
        // Если есть работа - взять директорию куда складывать (если она не установлена заранее - по умолчанию) -- ???

        // Сохраняем параметры пользователя
        UserParamsManager upm;
        upm = new UserParamsManager();
        upm.close ( false );
    }


    public void setStatus ( Object ... msg )
    {
        setStatus ( Convert.concatObj ( msg ) );
    }

    /* Занести инфу по статусу операции.
       Для многократных изменений в одной акции - необходимо и акцию и изменения производить в
       отдельном потоке - особенность Swing* - сделано в statusPanel */
    public void setStatus ( String statusMsg )
    {
        //Logger.getInstance().debug ( "GeneralManager.setStatus: Finish. statusMsg = " + statusMsg );
        if ( statusMsg != null )
        {
            // удалить все символы возврата каретки
            statusMsg   = statusMsg.replace ( '\n', ' ' );
            statusMsg   = statusMsg.replace ( '\r', ' ' );
            getFrame().getServicePanel().setStatusText ( statusMsg );
        }
        //Logger.getInstance().debug ( "GeneralManager.setStatus: Finish" );
    }

    /* Время выполнения операции - для статус панели */
    public void setTime ( long timeMsec )
    {

    }

    /* Перерисовать весь фрейм */
    public void rewrite ()
    {
        Log.l.debug ( "Start" );

        frame.rewrite();

        Log.l.debug ( "Finish" );
    }

    /**
     * Установить Системные переменные декораторов.
     * <BR/> Это надо прописывать после каждой смены декоратора, тк декоратор при установке меняет цвета под себя.
     * <BR/>
     */
    public void setUI ()
    {
        // ----------- Таблица -----------------
        // Рисование сетки
        // Цвет сетки таблицы (для GTK+ - т.к. там он - белый)
        //UIManager.put ( "Table.gridColor", Color.gray );
    }

    public Author getAuthor ()
    {
        Author  result;
        Project project;

        // Взять текущий Сборник
        project = getFrame().getCurrentProject ();
        result  = project.getAuthor ();
        if ( result == null )
        {
            result = new Author ();
            result.setFirstName ( "Sergey" );
            result.setLastName ( "Afanasiev" );
            result.setEmail ( "s_afa@yahoo.com" );
            project.setAuthor ( result );
        }

        return result;
    }

}
