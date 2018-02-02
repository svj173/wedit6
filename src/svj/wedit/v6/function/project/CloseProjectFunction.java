package svj.wedit.v6.function.project;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.book.CloseBookTabFunction;
import svj.wedit.v6.gui.tabs.TabsPanel;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.function.Function;

import java.awt.event.ActionEvent;


/**
 * todo Закрыть текущий проект.
 * <BR/> Для текущего выбранного проекта.
 * <BR/> - Переспрашивает - действительно ли удалить.
 * <BR/> - Сохраняет все несохраненные файлы проекта - с переспрашиванием, надо ли сохранять?.
 * <BR/> - Закрывает все табики открытых главы книг.
 * <BR/> - Закрывает все табики открытых книг.
 * <BR/> - Закрывает табик Сборника.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 18.05.2012 14:36:45
 */
public class CloseProjectFunction extends Function
{
    private TreePanel<Project> projectTreePanel;


    public CloseProjectFunction ()
    {
        this ( null );
    }

    public CloseProjectFunction ( TreePanel<Project> projectTreePanel )
    {
        setId ( FunctionId.CLOSE_PROJECT );
        setName ( "Закрыть вкладку текущего Сборника");
        //setMapKey ( "Ctrl/O" );
        setIconFileName ( "close_red.png" );
        this.projectTreePanel = projectTreePanel;
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        Project project;
        TabsPanel<TreePanel<BookContent>> tabsBooksPanel;
        CloseBookTabFunction closeBookTabFunction;

        // Формируем список открытых книг для этого сборника

        // - Текущий сборник
        if ( projectTreePanel == null )
            projectTreePanel = Par.GM.getFrame().getCurrentProjectPanel();

        //project = Par.GM.getFrame().getCurrentProject();
        if ( projectTreePanel == null )  throw new WEditException ( "Сборник не задан!" );
        project = projectTreePanel.getObject();
        if ( project == null )  throw new WEditException ( "Сборник не задан!" );

        // Взять табс-панель данного Сборника - c перечнем открытых книг.
        tabsBooksPanel  = Par.GM.getFrame().getBooksPanel().getTabsPanel ( project.getProjectDir().getAbsolutePath() );
        //if ( tabsBooksPanel == null )  throw new MessageException ( "Нет открытых книг сборника '", project.getName(), "' !" );
        if ( tabsBooksPanel != null )
        {
            // - Берем список всех открытых книг данного Сборника -- список обьектов TreePanel book

            closeBookTabFunction = new CloseBookTabFunction ( null );
            // Закрываем табики всех открытых текстов данных книг. -- в CloseBookTabFunction (TreePanel book)
            for ( TreePanel<BookContent> bookPanel : tabsBooksPanel.getPanels() )
            {
                closeBookTabFunction.closeBookPanel ( bookPanel, true );
            }

            // Закрыть все таб-панели данной табс-панели
            tabsBooksPanel.removeAll();

            // Удалить табс-панель с табиками открытых книг Сборника
            Par.GM.getFrame().getBooksPanel().deleteTabsPanel ( tabsBooksPanel.getId() );
        }

        // теперь закрываем табик Сборника
        Par.GM.getFrame().getProjectPanel ().getCurrent ().removeTab ( projectTreePanel );
    }

    @Override
    public void rewrite ()
    {
    }

    @Override
    public void init () throws WEditException
    {
    }

    @Override
    public void close ()
    {
    }

    @Override
    public String getToolTipText ()
    {
        return "Закрыть вкладку текущего Сборника";
    }

}
