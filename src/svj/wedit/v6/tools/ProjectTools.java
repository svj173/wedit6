package svj.wedit.v6.tools;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.project.open.ProjectStaxParser;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.gui.tabs.TabsPanel;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.ConfigParam;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.Section;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.book.BookTitle;
import svj.wedit.v6.obj.project.IProjectParser;
import svj.wedit.v6.obj.project.IProjectSectionParser;

import java.io.File;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 21.02.2012 14:48:53
 */
public class ProjectTools
{
    /**
     * Выдать дефолтный список типов книг.
     *    -- draft   - черновик
        -- work    - пишется (в работе),
        -- release - написана (релиз)

     * @return  Список. key=enName
     */
    /*
    public static Map<String,WType> getDefaultBookTitleTypes()
    {
        Map<String,WType>   result;
        WType               type;

        result = new HashMap<String,WType> ();
        // String ruName, String enName, String descr, Color color, Color iconFontColor, int styleType
        // String ruName, String enName, String descr, String iconName - иконка - то же что и дефолтная книга, толкьо другого цвета. Либо наложение на иконку книги маленьких значков -- не наглядно.
        // - work
        type    = new WType ( "рабочая", "work", "Книга в стадии реализации.", Color.GRAY, Color.GRAY, Font.ITALIC );
        result.put ( type.getEnName(), type );
        // - draft
        type    = new WType ( "черновик", "draft", "Книга в стадии черновых записей.", WCons.BRAUN_1, WCons.BRAUN_1, Font.ITALIC );
        result.put ( type.getEnName(), type );
        // - release
        type    = new WType ( "написана", "release", "Книга написана.", Color.GREEN, Color.GREEN, Font.ITALIC );
        result.put ( type.getEnName(), type );

        return result;
    }
    */

    /**
     * Какая-то обработка всех Сборников, книг в Сборниках. Функционал задается во внешнем обработчике - parser.
     * В цикле пробегаем по всему дереву - начиная от Сборников. Обьекты передаем во внешний обработчик (parser).
     * @param parser внешний обработчик
     * @throws WEditException Ошибки в работе.
     */
    public static void processTree ( IProjectParser parser ) throws WEditException
    {
        Project                             project;
        String                              str;
        TabsPanel<TreePanel<Project>>       tabsProjectsPanel;
        TabsPanel<TreePanel<BookContent>>   tabsBooksPanel;
        TabsPanel<TextPanel>                tabsTextsPanel;
        BookContent                         bookContent;
        BookNode                            bookNode;
        int                                 textCursor;

        Log.file.debug ( "Start" );

        try
        {
            parser.startDocument();

            // пробегаем по всем открытым проектам
            // - берем текущую card - либо Пусто либо Сборник
            tabsProjectsPanel   = Par.GM.getFrame().getProjectPanel().getCurrent();
            if ( tabsProjectsPanel == null )
            {
                // так не должно быть - всегда выбрана Пусто. Но почему то есть.
                parser.endDocument();
                return;
            }
            
            Log.file.debug ( "tabsProjectsPanel id = %s", tabsProjectsPanel.getId() );
            Log.file.debug ( "tabsProjectsPanel name = %s", tabsProjectsPanel.getName() );
            // 1333763765509 / project_Tabs


            // Цикл по всем табикам, каждый из которых - отдельный Сборник.
            for ( TreePanel<Project> projectPanel : tabsProjectsPanel.getPanels() )
            {
                // Взять обьект Сборника
                project = projectPanel.getObject();
                // Занести данные о нем
                parser.startProject ( project );

                // Взять табс-панель данного Сборника
                tabsBooksPanel  = Par.GM.getFrame().getBooksPanel().getTabsPanel ( project.getProjectDir().getAbsolutePath() );
                if ( tabsBooksPanel != null )
                {
                    // todo Взять текущий
                    //Par.GM.getFrame().getBooksPanel().getCurrent();
                    // Цикл по всем открытым книгам данного Сборника
                    for ( TreePanel<BookContent> bookPanel : tabsBooksPanel.getPanels() )
                    {
                        // Взять обьект Книги Сборника
                        bookContent = bookPanel.getObject();
                        // Занести данные о нем
                        parser.startBook ( bookContent );

                        // Взять табс-панель данного Сборника
                        tabsTextsPanel  = Par.GM.getFrame().getTextsPanel().getTabsPanel ( bookContent.getId() );
                        if ( tabsTextsPanel != null )
                        {
                            // Цикл по всем открытым текстам данной Книги данного Сборника
                            for ( TextPanel textPanel : tabsTextsPanel.getPanels() )
                            {
                                textCursor  = textPanel.getCurrentCursor();
                                // Взять обьект Книги Сборника
                                bookNode = textPanel.getBookNode();
                                // Занести данные о нем
                                parser.startText ( bookNode, textCursor );


                                //parser.endText ( bookContent );
                            }
                        }

                        parser.endBook ( bookContent );
                    }
                }

                parser.endProject ( project );
            }

            parser.endDocument();

        } catch ( Exception e )         {
            str = Convert.concatObj ( "Системная ошибка сохранения файла параметров пользователя '", ConfigParam.USER_PARAMS_FILE, "' :\n", e );
            Log.file.error ( str, e );
            throw new WEditException ( str, e );
        }
    }

    public static void processProjectTree ( IProjectSectionParser parser ) throws WEditException
    {
        Project                             project;
        String                              str;
        TabsPanel<TreePanel<Project>>       tabsProjectsPanel;
        TabsPanel<TreePanel<BookContent>>   tabsBooksPanel;
        TabsPanel<TextPanel>                tabsTextsPanel;
        BookContent                         bookContent;
        BookNode                            bookNode;
        int                                 booksCount, ic;
        Section section;

        Log.file.debug ( "Start" );

        try
        {
            parser.startDocument();

            // пробегаем по всем открытым проектам
            // - берем текущую card - либо Пусто либо Сборник
            tabsProjectsPanel   = Par.GM.getFrame().getProjectPanel().getCurrent();
            if ( tabsProjectsPanel == null )
            {
                // так не должно быть - всегда выбрана Пусто. Но почему то есть.
                parser.endDocument();
                return;
            }

            Log.file.debug ( "tabsProjectsPanel id = %s", tabsProjectsPanel.getId() );
            Log.file.debug ( "tabsProjectsPanel name = %s", tabsProjectsPanel.getName() );
            // 1333763765509 / project_Tabs


            // Цикл по всем табикам, каждый из которых - отдельный Сборник.
            for ( TreePanel<Project> projectPanel : tabsProjectsPanel.getPanels() )
            {
                // Взять обьект Сборника
                project = projectPanel.getObject();

                booksCount = 0;
                // Занести данные о нем
                parser.startProject ( project );

                // Взять книги данного Сборника
                section = project.getRootSection();

                booksCount = booksCount + processSection ( parser, section, 1 );

                parser.endProject ( project, booksCount );
            }

            parser.endDocument();

        } catch ( Exception e )         {
            str = Convert.concatObj ( "Системная ошибка :\n", e );
            Log.file.error ( str, e );
            throw new WEditException ( str, e );
        }
    }

    private static int processSection ( IProjectSectionParser parser, Section section, int level )
    {
        int ic, ic2, bookCount;

        bookCount = 0;
        parser.startSection ( section, level );

        ic = level + 1;
        for ( Section s : section.getSections() )
        {
            bookCount   = bookCount + processSection ( parser, s, ic );
        }

        //ic2 = level + 2;
        for ( BookTitle book : section.getBooks() )
        {
            parser.startBook ( book, ic );
            // .....
            parser.endBook ( book, ic );
            bookCount++;
        }

        parser.endSection ( section, level );

        return bookCount;
    }

    /**
     * Загрузить проект из файла
     * @param file
     * @return Загруженный из файла проект либо NULL.
     * @throws WEditException
     */
    public static Project loadProject ( File file ) throws WEditException
    {
        Project             result;
        ProjectStaxParser   xmlParser;

        // загрузить и распарсить файл
        xmlParser   = new ProjectStaxParser();
        result      = xmlParser.read ( file );

        return result;
    }

    /**
     * Загрузить проект из файла по абс имени.
     * <br/> Не ругаемся, т.к. это применяется при стартовой загрузке проектов из списка открытых проектов.
     * @param projectAbsDir  Абсолютная директория расположения данного проекта.
     * @return   Загруженный из файла проект либо NULL.
     */
    public static Project loadProject ( String projectAbsDir )
    {
        Project             result;
        ProjectStaxParser   xmlParser;
        File                file;

        result  = null;
        file    = new File ( projectAbsDir, ConfigParam.PROJECT_FILE_NAME );
        // Проверить - есть ли такой файл
        if ( file.exists() )
        {
            // загрузить и распарсить файл
            xmlParser   = new ProjectStaxParser();
            try
            {
                result      = xmlParser.read ( file );
            } catch ( WEditException e )            {
                result  = null;
                Log.l.error ( Convert.concatObj ( "load project error. file = ", projectAbsDir ), e);
            }
        }
        else
        {
            Log.l.error ( "Project file = %s not exist.", projectAbsDir );
        }

        return result;
    }

    public static String getProjectsFolder ()
    {
        Project project;

        project = Par.GM.getFrame().getCurrentProject ();
        if ( project == null )
            return null;
        else
            return project.getProjectDir().getParent();
    }

}
