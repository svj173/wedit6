package svj.wedit.v6.content;


import svj.wedit.v6.Par;
import svj.wedit.v6.WCons;
import svj.wedit.v6.book.BookNodeToText;
import svj.wedit.v6.content.listener.WEditWindowAdapter;
import svj.wedit.v6.content.panel.ServicePanel;
import svj.wedit.v6.content.toolBar.BrowserToolBar;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.book.CloseBookTabFunction;
import svj.wedit.v6.function.book.edit.BookElementPopupMenu;
import svj.wedit.v6.function.project.CloseProjectFunction;
import svj.wedit.v6.function.project.edit.book.BookPopupMenu;
import svj.wedit.v6.function.project.edit.section.AddSectionPopupMenu;
import svj.wedit.v6.function.text.CloseTextTabFunction;
import svj.wedit.v6.gui.WComponent;
import svj.wedit.v6.gui.menu.WEMenuBar;
import svj.wedit.v6.gui.menu.WEMenuItem;
import svj.wedit.v6.gui.panel.EditablePanel;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.gui.panel.WorkPanel;
import svj.wedit.v6.gui.panel.card.CardPanel;
import svj.wedit.v6.gui.renderer.BookNodeCellRenderer;
import svj.wedit.v6.gui.renderer.SectionCellRenderer;
import svj.wedit.v6.gui.tabs.TabsChangeListener;
import svj.wedit.v6.gui.tabs.TabsPanel;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.gui.tree.WCellRenderer;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.TreeObjType;
import svj.wedit.v6.obj.WTreeObj;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;


/**
 * Главный фрейм содержимого проекта.
 * <BR/> Содержит дерево проекта и вкладки деревьев оглавлений открытых книг.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 05.05.2011 10:15:13
 */
public class ContentFrame    extends JFrame   implements WComponent
{
    /** Флаг, было ли редактирoвание или нет - лишнее. */
    //private boolean editBook    = false;

    /* Флаг редактирования - общий для всех открытых книг. Формируется из опроса книг. */
    private JLabel editFlag;

    private WEMenuBar       menuBar;
    private ServicePanel    servicePanel;

    /** Левая панель. Выводит дерево Сборников. */
    private WorkPanel<TreePanel<Project>>       projectsPanel;

    /** Средняя панель. В ней выводится дерево содержимого книги. */
    private WorkPanel<TreePanel<BookContent>>   booksPanel;

    /** Крайняя правая панель. В ней выводятся тексты выбранных Элементво книги. */
    private WorkPanel<TextPanel>                textsPanel;

    /* Верхняя панель иконок. */
    private BrowserToolBar toolbar;

    /* Нижняя, выдвигающаяся панель - дополнительная - Результаты поиска и т.д. Табики можно закрывать.
       Функции, например "поиска", ищут таб-панель своего имени, если находят - заливают в нее результат работы, не находят - создают норвую панель. */
    private TabsPanel<EditablePanel>  additionalPanel;
    /* РАзделитель панели Поиска. Для возможности управлять им извне - показывать панели, скрывать. */
    private JSplitPane  vertSplitPane;


    public ContentFrame () throws HeadlessException, WEditException
    {
        // Title - WEdit6. номер версии + номер билда.
        //super ( "Editor. " + Par.VERSION_DATE + "/" + Par.VERSION_NUMBER );
        super ( "WEdit6. " + ContentFrame.class.getPackage().getImplementationVersion() );

        JSplitPane  projectSplitPane;
        Dimension   d;
        int         x, y;
        JPanel      booksPanel;

        getContentPane().setLayout ( new BorderLayout() );

        // Установить точку вывода
        setLocation ( 10, 10 );
        setDefaultCloseOperation ( WindowConstants.EXIT_ON_CLOSE );

        // иконка
        Image icon = GuiTools.createImage ( "img/editor/we6_title.png" );
        if ( icon != null )  setIconImage ( icon );

        // Вешаем листенер на крестик фрейма
        addWindowListener ( new WEditWindowAdapter() );

        // Установить размер - если размер не был взят из сохраняемых параметров при загрузке книги.
        d   = Toolkit.getDefaultToolkit().getScreenSize();
        x   = d.width / 2;
        y   = d.height - 100;
        //Dimension size  = new Dimension ( x,y );
        setSize ( x,y );
        //getContentPane().setPreferredSize ( size );



        // split - отделяет дерево Сборников от всех остальных - по горизонтали.
        projectSplitPane = new JSplitPane();
        //splitPane.setDividerSize(2);
        projectSplitPane.setOneTouchExpandable ( true );

        additionalPanel = new TabsPanel<EditablePanel>();
        additionalPanel.getAccessibleContext().setAccessibleName ( "Bottom_Tabs" );
        // Другой цвет на всю нижнюю панель - чтобы отличалась от верхней.
        additionalPanel.setBackground ( WCons.BOTTOM_TABS );

        // Отделяет все рабочие панели - Сборники, Книги. тексты - от служебной панели (Поиск и т.д.) - по вертикали.
        //vertSplitPane = new JSplitPane();
        vertSplitPane  = new JSplitPane ( JSplitPane.VERTICAL_SPLIT, projectSplitPane, additionalPanel );
        vertSplitPane.setOneTouchExpandable ( true );
        //add ( vertSplitPane, BorderLayout.CENTER );
        getContentPane().add ( vertSplitPane, BorderLayout.CENTER );

        // ------------- project --------------------
        CardPanel<TabsPanel<TreePanel<Project>>> cardPanel = new CardPanel<TabsPanel<TreePanel<Project>>>();
        projectsPanel   = new WorkPanel<TreePanel<Project>>( "Work_projectPanel", null, cardPanel, "Книжные сборники" );
        projectsPanel.setId ( "Work_projectPanel" );
        // - Добавить функции в тул-бар Сборника
        projectsPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.OPEN_PROJECT ) );
        projectsPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.MOVE_BOOK ) );
        projectsPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.CLOSE_PROJECT ) );

        booksPanel              = createBooksAndTextPanel ();

        projectSplitPane.setLeftComponent ( projectsPanel );

        projectSplitPane.setRightComponent ( booksPanel );

        servicePanel = new ServicePanel();
        //add ( servicePanel, BorderLayout.SOUTH );
        getContentPane().add ( servicePanel, BorderLayout.SOUTH );

        editFlag = new JLabel ( " " );
        Border border = BorderFactory.createEtchedBorder(Color.white,new Color(178, 178, 178));
        editFlag.setBorder ( border );
        editFlag.setOpaque(true);

        // toolBar
        createToolBar();

        //vertSplitPane.setDividerLocation ( 0.2 );   // до pack - не влияет на расположение -- по-середине.
        pack();

        // сдвинуть вправо разделитель панелей  - только после упаковки  (pack)
        projectSplitPane.setDividerLocation ( 220 );
        // нижнюю панель показать на чуть-чуть.  0.7 - сильно вверху; 0.2 - еще выше
        // -- Почему-то дробные (проценты) не хотят работать - юзаем пикселя.
        vertSplitPane.setDividerLocation ( Par.SCREEN_SIZE.height );
    }

    private void createToolBar ()
    {
        toolbar = new BrowserToolBar();    // IconsPanel ???
        getContentPane().add ( toolbar, BorderLayout.NORTH );

        // Наполнить функциями
        toolbar.addFunction ( FunctionId.SAVE_ALL_PROJECTS );
        toolbar.addFunction ( FunctionId.SAVE_ABSOLUTE_ALL_PROJECTS );
        toolbar.addFunction ( FunctionId.ZIP_ALL_PROJECTS );
    }

    private JPanel createBooksAndTextPanel () throws WEditException
    {
        JPanel      result;
        JSplitPane  splitPane;

        result   = new JPanel();
        result.setLayout ( new BorderLayout ( 0,0) );

        splitPane = new JSplitPane();
        //splitPane.setDividerSize(2);     // ширина разделителя между панелями - в пикс.
        splitPane.setOneTouchExpandable ( true );

        result.add ( splitPane, BorderLayout.CENTER );

        // ---------------- создать book content Panel -----------------
        booksPanel   = new WorkPanel<TreePanel<BookContent>>( "Work_bookPanel", projectsPanel.getCardPanel(),
                                                              new CardPanel<TabsPanel<TreePanel<BookContent>>>(), null );
        booksPanel.setId ( "Work_bookPanel" );
        // наполнить ее сверху (тул-бар)  акциями-иконками
        booksPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.RELOAD_BOOK ) );
        booksPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.CUT_ELEMENT ) );
        booksPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.DELETE_ELEMENT ) );
        booksPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.BOOKMARK ) );       // добавить текущую книгу в Закладки
        // Групповое редактирование типов Элементов (hidden, work...)
        booksPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.EDIT_NODE_TYPE ) );

        splitPane.setLeftComponent ( booksPanel );

        // ----------------- создать textsPanel ------------------
        textsPanel   = new WorkPanel<TextPanel>( "Work_textPanel", booksPanel.getCardPanel(),
                                                 new CardPanel<TabsPanel<TextPanel>>(), null );
        textsPanel.setId ( "Work_textPanel" );
        // наполнить ее сверху (тул-бар)  акциями-иконками
        textsPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.SAVE_TEXT ) );
        textsPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.CLOSE_TEXT ) );
        textsPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.CLOSE_ALL_TEXT ) );
        textsPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.UNDO_TEXT ) );
        textsPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.REDO_TEXT ) );
        textsPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.TEXT_INFO_ELEMENT ) );
        textsPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.TEXT_SELECT_ELEMENT ) );
        textsPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.TEXT_SELECT_ALIGN ) );
        textsPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.TEXT_SELECT_STYLE ) );
        textsPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.SELECT_IMAGE ) );
        textsPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.VIEW_FROM_SOURCE ) );
        textsPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.INSERT_TABLE ) );

        splitPane.setRightComponent ( textsPanel );

        splitPane.setDividerLocation ( 220 );

        return result;
    }

    /**
     * Навесить на табс-панель имя и листенер.
     * CardLayout + Tab панели. Таб - имя открытого элемента книги. ( имя открытой книги,  имя открытого проекта.)
     * @param tabsPanel  табс-панель
     * @param prefix     Префикс, который добавляется к имени табс-панели.
     * @return  Панель   табс-панель
     */
    private TabsPanel createTabsPanel ( TabsPanel tabsPanel, String prefix )
    {
        TabsChangeListener  tabsListener;

        tabsPanel.setTabPlacement ( JTabbedPane.TOP );
        tabsPanel.setName ( prefix+"_Tabs" );
        tabsPanel.getAccessibleContext().setAccessibleName ( prefix+"_Tabs" );

        // создать листенер выборки табиков
        tabsListener = new TabsChangeListener ( prefix+"_TabsListener" );
        tabsPanel.setSelectTabsListener ( tabsListener );

        return tabsPanel;
    }

    public void init ()
    {
        Log.l.debug ( "ContentFrame.init: Start." );

        // Повесить на крестик фрейма функцию закрытия
        addWindowListener ( new WEditWindowAdapter() );

        // Перерисовать фрейм согласно установленному языку
        rewrite();

        // Перерисовать меню согласно установленному языку
        rewriteMenu();

        Log.l.debug ( "ContentFrame.init: Finish." );
    }

    /**
     * Перерисовать согласно установленному языку -- по идее, вес это выхватывается в rewrite
     */
    public void rewriteMenu()
    {
        // todo Взять меню
        //WEMenuBar   menu    = (WEMenuBar) getRootPane().getJMenuBar();
        //menu.rewrite();
        // Оглавление
        //contentPanel.rewrite ();
    }

    public void addProject ( Project project ) throws WEditException
    {
        TreeObj                         root;
        TreePanel<Project>              projectPanel;
        SectionCellRenderer             renderer;
        WTreeObj                        wTreeObj;
        TabsPanel<TreePanel<Project>>   tabsPanel;

        Log.l.debug ( "-+- Start. project = %s", project );

        // Создать из разделов проекта дерево.
        wTreeObj        = project.getRootSection();
        root            = TreeObjTools.createTree ( wTreeObj );
        projectPanel    = new TreePanel<Project> ( root, project );
        //projectPanel.setParentId ( project.getId() );
        projectPanel.setId ( project.getProjectDir().toString() );
        projectPanel.setName ( project.getName() );
        projectPanel.setEdit ( false );
        Log.l.debug ( "projectPanel = %s", projectPanel );

        // Создать рендереры - для дерева Проектов
        renderer    = new SectionCellRenderer();
        projectPanel.addRenderer ( TreeObjType.SECTION, renderer );
        renderer    = new SectionCellRenderer();
        projectPanel.addRenderer ( TreeObjType.BOOK, renderer );

        // Создать контекстное меню Проектов
        createProjectPopUpMenu ( projectPanel );

        // Взять из текущего card - tabsPanel по ИД
        tabsPanel   = projectsPanel.getTabsPanel ( "project" );
        Log.l.debug ( "--- project tabsPanel = %s", tabsPanel );
        if ( tabsPanel == null )
        {
            Log.l.debug ( "--- Create new book tabsPanel" );
            // создать
            tabsPanel = new TabsPanel<TreePanel<Project>>();
            //tabsPanel.setParentId ( "project" );
            // наполнить таб панель именем и листенером
            createTabsPanel ( tabsPanel, "project" );
            projectsPanel.addTabsPanel ( "project", tabsPanel );
        }

        // Добавить новую вкладку
        //tabsPanel.addPanel ( projectPanel, project.getId(), project.getName() );
        //projectsCardPanel.revalidate();
        // Добавить новую вкладку
        //tabsPanel.addPanel ( bookPanel, bookContent.getId(), bookContent.getName() );
        JLabel  tabTitleLabel;
        Icon    icon;
        String  iconPath;

        // Иконка для вкладки. По идее - необходим размер иконки. -- Пока применяется только в табиках дерева книг. У остальных табиках (Сборник, Тексты) - свое формирование.
        //iconPath        = bookContent.getBookIconPath ( Par.TABS_ICON_SIZE );
        //icon            = GuiTools.createImageByFile ( iconPath );
        icon = null;
        tabTitleLabel   = tabsPanel.addPanel ( projectPanel, project.getId(), project.getName(), icon, new CloseProjectFunction (projectPanel) );
        projectPanel.setTabTitleLabel ( tabTitleLabel );

        // Выбрать новый проект
        projectsPanel.setCurrent ( "project" );

        Log.l.debug ( "--- projectsPanel = %s", WDumpTools.printCardPanel ( projectsPanel ) );
        Log.l.debug ( "-+- Finish. project = %s", project );
    }

    /**
     * Добавить панель содержания книги к указанному проекту.
     * <BR/> Табс-Панель содержаний этого проекта может и не быть текущей.
     * <BR/>
     * @param bookContent   Книга как обьект.
     * @param project       Сборник, к которому принадлежит данная книга.
     * @throws WEditException  err
     */
    public void addBookContent ( BookContent bookContent, Project project ) throws WEditException
    {
        TreeObj                     root;
        TreePanel<BookContent>      bookPanel;
        WCellRenderer               renderer;
        WTreeObj                    wTreeObj;
        TabsPanel<TreePanel<BookContent>>  tabsPanel;

        // Создать из разделов проекта дерево.
        wTreeObj     = bookContent.getBookNode();
        root         = TreeObjTools.createTree ( wTreeObj );

        // Создать таб-панель - которая добавится в tabsPanel
        //bookPanel    = new TreeCardPanel<BookContent> ( root, bookContent, bookTextCardPanel, bookContent.getId() );
        bookPanel    = new TreePanel<BookContent> ( root, bookContent );
        bookPanel.setId ( bookContent.getId() );
        // Имя равно имени обьекта, ради которого эта панель открыта. Для отображениии в титле над другой (связной) панелью.
        bookPanel.setName ( bookContent.getName() );

        // Создать рендереры - для дерева Проектов
        //renderer    = new SectionCellRenderer();
        //bookPanel.addRenderer ( TreeObjType.SECTION, renderer );
        //renderer    = new SectionCellRenderer();
        renderer    = new BookNodeCellRenderer();
        bookPanel.addRenderer ( TreeObjType.BOOK_NODE, renderer );

        // Создать контекстное меню Содержимого книги
        createBookPopUpMenu ( bookPanel );

        // Взять из текущего card - tabsPanel по ИД
        tabsPanel   = booksPanel.getTabsPanel ( project.getId() );
        Log.l.debug ( "book for project id = %s", project.getId() );
        //Log.l.debug ( "get book tabsPanel = ", tabsPanel );
        if ( tabsPanel == null )
        {
            Log.l.debug ( "Create new book tabsPanel" );
            // создать
            tabsPanel = new TabsPanel<TreePanel<BookContent>>();
            tabsPanel.setParentId ( project.getId() );
            // наполнить таб панель именем и листенером
            createTabsPanel ( tabsPanel, "BookContents_"+project.getName() );
            booksPanel.addTabsPanel ( project.getId(), tabsPanel );
        }

        bookPanel.setEdit ( bookContent.isEdit() );

        // Добавить новую вкладку
        //tabsPanel.addPanel ( bookPanel, bookContent.getId(), bookContent.getName() );
        JLabel  tabTitleLabel;
        Icon    icon;
        String  iconPath;

        // Иконка для вкладки. По идее - необходим размер иконки. -- Пока применяется только в табиках дерева книг. У остальных табиках (Сборник, Тексты) - свое формирование.
        iconPath        = bookContent.getBookIconPath ( Par.TABS_ICON_SIZE );
        icon            = GuiTools.createImageByFile ( iconPath );
        tabTitleLabel   = tabsPanel.addPanel ( bookPanel, bookContent.getId(), bookContent.getName(), icon, new CloseBookTabFunction(bookPanel) );
        bookPanel.setTabTitleLabel ( tabTitleLabel );
    }


    /*
    public boolean containProject ( String projectId )
    {
        return projectsCardPanel.contain ( projectId );
    }

    public void selectProject ( String projectId )
    {
        projectsCardPanel.setSelectedTab ( projectId );
    }
    */

    /**
     *
     * @param projectId  ID Сборника. Например:  /home/svj/Serg/Stories/SvjStores
     * @return   TRUE - есть такой Сборник среди открытых.
     */
    public boolean containProject ( String projectId )
    {
        TabsPanel<TreePanel<Project>>  tabsPanel;

        // Взять из текущего card - tabsPanel по ИД
        tabsPanel   = projectsPanel.getTabsPanel ( "project" );

        return (tabsPanel != null) && tabsPanel.contain ( projectId );
    }


    public boolean containBook ( String bookId, Project project )
    {
        TabsPanel<TreePanel<BookContent>>  tabsPanel;

        // Взять из текущего card - tabsPanel по ИД
        tabsPanel   = booksPanel.getTabsPanel ( project.getId() );

        return (tabsPanel != null) && tabsPanel.contain ( bookId );
    }

    public void selectProject ( String projectId )
    {
        TabsPanel<TreePanel<Project>>  tabsPanel;

        // Взять из текущего card - tabsPanel по ИД
        tabsPanel   = projectsPanel.getTabsPanel ( "project" );
        if ( (tabsPanel != null) && (tabsPanel.contain ( projectId )) )
        {
            tabsPanel.setSelectedTab ( projectId );
        }
        else
        {
            Log.l.error ( "[selectProject] tabsPanel not found. projectId:%s", projectId );
        }
    }

    public void selectBook ( String bookId, Project project )
    {
        TabsPanel<TreePanel<BookContent>>  tabsPanel;

        // Взять из текущего card - tabsPanel по ИД
        tabsPanel   = booksPanel.getTabsPanel ( project.getId() );
        if ( (tabsPanel != null) && (tabsPanel.contain ( bookId )) )
        {
            tabsPanel.setSelectedTab ( bookId );
        }
        else
        {
            Log.l.error ( "[selectBook] tabsPanel not found. bookId:%s; project:%s", bookId, project );
        }
    }

    public TreePanel<BookContent> selectBook ( String bookId, String projectId )
    {
        TabsPanel<TreePanel<BookContent>>  tabsPanel;
        TreePanel<BookContent>  treePanel;

        treePanel = null;
        // Взять из текущего card - tabsPanel по ИД
        tabsPanel   = booksPanel.getTabsPanel ( projectId );
        if ( (tabsPanel != null) && (tabsPanel.contain ( bookId )) )
        {
            treePanel   = tabsPanel.setSelectedTab ( bookId );
            //bookContent = treePanel.getObject ();
        }
        else
        {
            Log.l.error ( "[selectBook] tabsPanel not found. bookId:%s; projectId:%s", bookId, projectId );
        }
        return treePanel;
    }

    public void selectNode ( String nodeId, BookContent bookContent )
    {
        TabsPanel<TextPanel>    tabsPanel;
        String bookId;

        // Взять из текущего card - tabsPanel по ИД
        bookId = bookContent.getId();  // ID книги - это ее имя файла.
        Log.l.debug ( "--- bookId = %s; nodeId for select = %s", bookId, nodeId );
        tabsPanel   = textsPanel.getTabsPanel ( bookId );
        if ( (tabsPanel != null) && (tabsPanel.contain ( nodeId )) )
        {
            tabsPanel.setSelectedTab ( nodeId );
        }
        else
        {
            Log.l.error ( "[selectNode] tabsPanel not found. nodeId:%s; bookContent:%s", nodeId, bookContent );
        }
    }

    public Collection<TextPanel> getTextPanels ( String bookContentId )
    {
        TabsPanel<TextPanel>    tabsPanel;
        Collection<TextPanel>   result;

        Log.l.debug ( "--- bookContentId = %s", bookContentId );
        tabsPanel   = textsPanel.getTabsPanel ( bookContentId );
        if ( tabsPanel != null )
        {
            result = tabsPanel.getPanels ();
        }
        else
        {
            //Log.l.error ( "[selectNode] tabsPanel not found. nodeId:%s; bookContent:%s", nodeId, bookContent );
            result = null;
        }
        return result;
    }

    /*
    public void selectNode ( String chapterId, String bookId )
    {
        TabsPanel<TextPanel>    tabsPanel;
        TextPanel               textPanel;

        // Взять из текущего card - tabsPanel по ИД
        tabsPanel   = textsPanel.getTabsPanel ( bookId );
        Log.l.debug ( "[selectNode] tabsPanel for bookId:%s; tabsPanel=%s", bookId, tabsPanel );
        if ( tabsPanel != null)
        {
            // Взять панель по ее полному пути
            textPanel   = tabsPanel.getPanelByPath ( chapterId );
            Log.l.debug ( "[selectNode] tabsPanel for chapterPath:%s; textPanel=%s", chapterId, textPanel );
            if ( textPanel != null )
            {
                tabsPanel.setSelectedPanel ( textPanel );
            }
            else
            {
                Log.l.error ( "[selectNode] tabsPanel hasnot nodeId:%s",chapterId  );
                Log.l.error ( "[selectNode] tabsPanel hasnot nodeId. panels = %s",tabsPanel.getPanelsMap()  );
            }
        }
        else
        {
            Log.l.error ( "[selectNode] tabsPanel not found for bookId:%s",bookId  );
        }
    }
    */

    public TextPanel selectNode ( String chapterId, String bookId )
    {
        TabsPanel<TextPanel>    tabsPanel;
        TextPanel               textPanel;

        textPanel = null;
        // Взять из текущего card - tabsPanel по ИД
        tabsPanel   = textsPanel.getTabsPanel ( bookId );
        Log.l.debug ( "[selectNode] tabsPanel for bookId:%s; tabsPanel=%s", bookId, tabsPanel );
        if ( tabsPanel != null)
        {
            if ( tabsPanel.contain ( chapterId ) )
            {
                textPanel = tabsPanel.setSelectedTab ( chapterId );
            }
            else
            {
                Log.l.error ( "[selectNode] tabsPanel hasnot nodeId:%s",chapterId  );
                Log.l.error ( "[selectNode] tabsPanel hasnot nodeId. panels = %s",tabsPanel.getPanelsMap()  );
            }
        }
        else
        {
            Log.l.error ( "[selectNode] tabsPanel not found for bookId:%s",bookId  );
        }

        return textPanel;
    }

    public void setFocus ( String nodeId, String bookId )
    {
        TabsPanel<TextPanel>    tabsPanel;

        // Взять из текущего card - tabsPanel по ИД
        tabsPanel   = textsPanel.getTabsPanel ( bookId );
        if ( (tabsPanel != null) && (tabsPanel.contain ( nodeId )) )
        {
            tabsPanel.setFocus ( nodeId );
            int cur = tabsPanel.getSelectedComponent().getCurrentCursor();
            Log.l.debug ( "[setFocus] tabsPanel. cur:%d; nodeId:%s", cur, nodeId );
        }
        else
        {
            Log.l.error ( "[setFocus] tabsPanel not found. bookId:%s; nodeId:%s", bookId, nodeId );
        }
    }


    /**
     * Создать новую панель с текстом главы и добавить ее в табики.
     * @param bookNode     Обьект текста главы.
     * @param bookContent  Обьект книги.
     * @param cursor       Положение курсора в тексте.
     * @throws WEditException   Ошибки добавления.
     */
    public void addBookNode ( BookNode bookNode, BookContent bookContent, int cursor ) throws WEditException
    {
        TextPanel               textPanel;
        TabsPanel<TextPanel>    tabsPanel;
        BookNodeToText          nodeToText;
        BookNode                node;
        Icon                    icon;

        Log.l.debug ( "Start. bookNode = %s; bookContent = %s; cursor = %d", bookNode, bookContent, cursor );

        if ( (bookNode == null) || (bookContent == null) ) return;

        // Взять из текущего card - tabsPanel по ИД - здесь содержатся все открытые тексты данной книги.
        tabsPanel   = textsPanel.getTabsPanel ( bookContent.getId() );
        Log.l.debug ( "tabsPanel for book = %s", tabsPanel );

        // Проверить, может данная часть уже открыта ?
        // - Или подчаcти данной части уже открыты в составе других частей?
        if ( tabsPanel != null )
        {
            for ( TextPanel tp : tabsPanel.getPanels() )
            {
                node    = tp.getBookNode();
                // - открыта в составе других частей? Если ДА - исключение.
                BookTools.checkContainNode ( bookNode, node );
            }
        }

        // Создать из разделов проекта дерево.
        textPanel   = new TextPanel ( bookNode );
        textPanel.setId ( bookNode.getFullPath() );
        textPanel.setName ( bookNode.getName() );

        // Наполнить панель текстом   - BookNodeToText
        nodeToText  = new BookNodeToText();
        nodeToText.process ( textPanel, bookNode, bookContent, cursor );

        // Создать контекстное меню для текста Главы
        //createBookPopUpMenu ( textPanel );

        // Добавить в табс-панель нашу панель как табик
        if ( tabsPanel == null )
        {
            // создать новую табс-панель
            tabsPanel = new TabsPanel<TextPanel>();
            tabsPanel.setParentId ( bookContent.getId() );
            // наполнить таб панель именем и листенером
            createTabsPanel ( tabsPanel, "BookNodes_"+bookContent.getId() );
            textsPanel.addTabsPanel ( bookContent.getId(), tabsPanel );
        }

        // Взять иконку, соответсвующую  bookNode
        icon    = BookStructureTools.getIcon ( bookContent.getBookStructure(), bookNode );
        Log.l.debug ( "bookElement icon = %s", icon );
        Log.l.debug ( "object icon path = %s", bookNode.getTreeIconFilePath() );

        // Добавить в панель табиков новую вкладку   - C иконками закрытия и типа эпизода книги.
        JLabel tabTitleLabel;
        tabTitleLabel   = tabsPanel.addPanel ( textPanel, bookNode.getId(), bookNode.getName(), icon, new CloseTextTabFunction(textPanel) );
        textPanel.setTabTitleLabel ( tabTitleLabel );
    }

    public boolean containNode ( String nodeId, BookContent bookContent )
    {
        TabsPanel<TextPanel>    tabsPanel;
        boolean                 result;

        // Взять из текущего card - tabsPanel по ИД
        tabsPanel   = textsPanel.getTabsPanel ( bookContent.getId() );
        Log.l.debug ( "[containNode] nodeId:%s; tabsPanel = %s",nodeId, tabsPanel );

        result      = (tabsPanel != null) && tabsPanel.contain ( nodeId );
        Log.l.debug ( "[containNode] nodeId:%s; result = %s",nodeId, result );

        return result;
    }

    /**
     * Контекстное меню на дереве элементов книги.
     * @param bookPanel  Панель дерева.
     */
    private void createBookPopUpMenu ( TreePanel<BookContent> bookPanel )
    {
        WEMenuItem  menuItem;
        Function function;

        function    = Par.GM.getFm().get ( FunctionId.ADD_ELEMENT_IN );
        menuItem    = new BookElementPopupMenu ( function, true );
        bookPanel.addPopupMenu ( menuItem );
    
        function    = Par.GM.getFm().get ( FunctionId.ADD_ELEMENT_AFTER );
        menuItem    = new BookElementPopupMenu ( function, true );
        bookPanel.addPopupMenu ( menuItem );

        // -------------- separator -------------
        bookPanel.addPopupSeparator();

        // Paste
        function    = Par.GM.getFm().get ( FunctionId.COPY_ELEMENT );
        menuItem    = new BookElementPopupMenu ( function, true );
        bookPanel.addPopupMenu ( menuItem );

        function    = Par.GM.getFm().get ( FunctionId.CUT_ELEMENT );
        menuItem    = new BookElementPopupMenu ( function, true );
        bookPanel.addPopupMenu ( menuItem );

        function    = Par.GM.getFm().get ( FunctionId.PASTE_ELEMENT_IN );
        menuItem    = new BookElementPopupMenu ( function, true );
        bookPanel.addPopupMenu ( menuItem );

        function    = Par.GM.getFm().get ( FunctionId.PASTE_ELEMENT_AFTER );
        menuItem    = new BookElementPopupMenu ( function, true );
        bookPanel.addPopupMenu ( menuItem );

        // -------------- separator -------------
        bookPanel.addPopupSeparator ();

        function    = Par.GM.getFm().get ( FunctionId.EDIT_ELEMENT );
        menuItem    = new BookElementPopupMenu ( function, true );
        bookPanel.addPopupMenu ( menuItem );

        // -------------- separator -------------
        bookPanel.addPopupSeparator();

        function    = Par.GM.getFm().get ( FunctionId.DELETE_ELEMENT );
        menuItem    = new BookElementPopupMenu ( function, true );
        bookPanel.addPopupMenu ( menuItem );

        // -------------- separator -------------
        bookPanel.addPopupSeparator();

        function    = Par.GM.getFm().get ( FunctionId.EDIT_DESC_ELEMENT );
        menuItem    = new BookElementPopupMenu ( function, true );
        bookPanel.addPopupMenu ( menuItem );

        // -------------- separator -------------
        bookPanel.addPopupSeparator();

        function    = Par.GM.getFm().get ( FunctionId.CONVERT_CONTENT_TO_RTF );
        menuItem    = new BookElementPopupMenu ( function, true );
        bookPanel.addPopupMenu ( menuItem );

        // -------------- separator -------------
        bookPanel.addPopupSeparator();

        // Открыть текст. Запрет для корня - ???. - НЕТ, книгу тоже можно открывать.
        function    = Par.GM.getFm().get ( FunctionId.OPEN_TEXT );
        menuItem    = new BookElementPopupMenu ( function, true );
        bookPanel.addPopupMenu ( menuItem );
        bookPanel.setDoubleClickAction ( function );

    }

    private void createProjectPopUpMenu ( TreePanel<Project> projectPanel )
    {
        WEMenuItem  menuItem;
        Function    function;

        function    = Par.GM.getFm().get ( FunctionId.ADD_SECTION_AFTER );
        menuItem    = new AddSectionPopupMenu ( function, false );
        //menuItem.addActionListener ( function );
        //menuItem.setText ( function.getName() );
        projectPanel.addPopupMenu ( menuItem );

        function    = Par.GM.getFm().get ( FunctionId.ADD_SECTION_IN );
        menuItem    = new AddSectionPopupMenu ( function, true );
        projectPanel.addPopupMenu ( menuItem );

        function    = Par.GM.getFm().get ( FunctionId.EDIT_SECTION );
        menuItem    = new AddSectionPopupMenu ( function, true );
        projectPanel.addPopupMenu ( menuItem );

        function    = Par.GM.getFm().get ( FunctionId.DELETE_SECTION );
        menuItem    = new AddSectionPopupMenu ( function, false );
        projectPanel.addPopupMenu ( menuItem );

        // -------------- separator -------------
        projectPanel.addPopupSeparator();

        function    = Par.GM.getFm().get ( FunctionId.CREATE_BOOK );
        menuItem    = new AddSectionPopupMenu ( function, true );
        projectPanel.addPopupMenu ( menuItem );

        function    = Par.GM.getFm().get ( FunctionId.EDIT_BOOK_TITLE );
        menuItem    = new BookPopupMenu ( function );
        projectPanel.addPopupMenu ( menuItem );

        function    = Par.GM.getFm().get ( FunctionId.OPEN_BOOK );
        menuItem    = new BookPopupMenu ( function );
        projectPanel.addPopupMenu ( menuItem );
        projectPanel.setDoubleClickAction ( function );

        function    = Par.GM.getFm().get ( FunctionId.DELETE_BOOK );
        menuItem    = new BookPopupMenu ( function );
        projectPanel.addPopupMenu ( menuItem );

        function    = Par.GM.getFm().get ( FunctionId.MOVE_BOOK );
        menuItem    = new BookPopupMenu ( function );
        projectPanel.addPopupMenu ( menuItem );

        projectPanel.addPopupSeparator();

        // imports
        function    = Par.GM.getFm().get ( FunctionId.IMPORT_FROM_WE1_BOOK );
        menuItem    = new AddSectionPopupMenu ( function, true );
        projectPanel.addPopupMenu ( menuItem );

        function    = Par.GM.getFm().get ( FunctionId.IMPORT_FROM_TXT );
        menuItem    = new AddSectionPopupMenu ( function, true );
        projectPanel.addPopupMenu ( menuItem );
    }

    @Override
    public void rewrite ()
    {
        Log.l.debug ( "ContentFrame.rewrite: Start. Par.NEED_REWRITE = %b; Par.WEDIT_STARTED = %b", Par.NEED_REWRITE, Par.WEDIT_STARTED );

        if ( Par.NEED_REWRITE && Par.WEDIT_STARTED )
        {
            GuiTools.rewriteComponents ( this );

            menuBar.rewrite();
            //rewriteMenu();

            // перерисовка рабочих панелей
            projectsPanel.rewrite();
            booksPanel.rewrite();
            textsPanel.rewrite();

            servicePanel.rewrite();

            toolbar.rewrite();

            additionalPanel.rewrite();

            // true - всегда взводим флаг что необходима перерисовка. Функции где это не требуется должны сбрасывать этот флаг.
            //Par.NEED_REWRITE = false;
        }

        if ( ! Par.NEED_REWRITE )  Par.NEED_REWRITE = true;


        Log.l.debug ( "ContentFrame.rewrite: Finish" );
    }

    public WorkPanel<TreePanel<Project>> getProjectPanel()
    {
        return projectsPanel;
    }

    public WorkPanel<TreePanel<BookContent>> getBooksPanel()
    {
        return booksPanel;
    }

    public WorkPanel<TextPanel> getTextsPanel ()
    {
        return textsPanel;
    }

    public TreePanel<Project> getCurrentProjectPanel()
    {
        TabsPanel<TreePanel<Project>>  tabsPanel;

        // Взять из текущего card - tabsPanel по ИД
        tabsPanel   = projectsPanel.getCurrent();

        if ( tabsPanel == null )
            return null;
        else
            return tabsPanel.getSelectedComponent();
    }

    public Project getCurrentProject()
    {
        TabsPanel<TreePanel<Project>>  tabsPanel;

        // Взять из текущего card - tabsPanel по ИД
        tabsPanel   = projectsPanel.getCurrent();

        if ( tabsPanel == null )
            return null;
        else
            return tabsPanel.getSelectedComponent().getObject ();
    }

    public Collection<Project> getProjects()
    {
        Collection<Project> result;
        Collection<TreePanel<Project>> list;
        Map<String, TabsPanel<TreePanel<Project>>> maps;
        Project project;

        result = new LinkedList<Project>();

        maps   = projectsPanel.getPanels();
        for ( TabsPanel<TreePanel<Project>> tabsPanel : maps.values () )
        {
            list    = tabsPanel.getPanels();
            for ( TreePanel<Project> treePanel : list )
            {
                project = treePanel.getObject();
                result.add ( project );
            }
        }

        return result;
    }

    /**
     * Взять текущую книгу.
     * @return Тек книга (содержание).
     */
    public TreePanel<BookContent> getCurrentBookContentPanel()
    {
        TabsPanel<TreePanel<BookContent>> tabsPanel;

        // Взять текущий
        tabsPanel   = booksPanel.getCurrent();
        if ( tabsPanel == null )
            return null;
        else
            return tabsPanel.getSelectedComponent();
    }

    /**
     * Выдать ту книгу, содержимое которой отображено в панели Книг, а не ту, на которой стоит курсор в дереве Разделов и Книг.
     * @return
     */
    public BookContent getCurrentBookContent()
    {
        TabsPanel<TreePanel<BookContent>> tabsPanel;

        // Взять текущий
        tabsPanel   = booksPanel.getCurrent();
        if ( tabsPanel == null )
            return null;
        else
            return tabsPanel.getSelectedComponent().getObject ();
    }

    public TextPanel getCurrentTextPanel()
    {
        TabsPanel<TextPanel> tabsPanel;

        // Взять текущий
        tabsPanel   = textsPanel.getCurrent();
        if ( tabsPanel == null )
            return null;
        else
            return tabsPanel.getSelectedComponent ();
    }

    public TabsPanel<TextPanel> getTextTabsPanel()
    {
        return textsPanel.getCurrent();
    }

    public TabsPanel<TreePanel<Project>> getCurrentProjectTabsPanel ()
    {
        return projectsPanel.getCurrent();
    }

    public TabsPanel<TreePanel<BookContent>> getCurrentBookTabsPanel ()
    {
        return booksPanel.getCurrent();
    }

    public CardPanel<TabsPanel<TreePanel<BookContent>>> getBookContentPanel()
    {
        return booksPanel.getCardPanel();
    }


    /**
     * Закрыть табс-панель открытых текстов, принадлежащую данной книги. Предварительно скинуть тексты в обьекты.
     * <BR/> - взять все открытые тексты этой книги и скинуть в них тексты в обьекты
     * <BR/> - удалить табики текстов: из рабочего массива.
     * <BR/> - удалить панель табиков текстов из cardLayout
     *
     * @param bookContentId  ID книги.
     */
    public void closeTextsPanel ( String bookContentId ) throws WEditException
    {
        TabsPanel<TextPanel>    tabsTextPanel;
        Collection<TextPanel>   textPanels, deletePanels;
        WorkPanel<TextPanel>    workTextPanel;

        workTextPanel   = Par.GM.getFrame().getTextsPanel();
        tabsTextPanel   = workTextPanel.getTabsPanel ( bookContentId );
        //Log.l.debug ( "close book = ", bookContent );
        Log.l.debug ( "bookContentId:%s. text tabs for book = %s", bookContentId, tabsTextPanel );

        if ( tabsTextPanel != null )
        {
            // даем команду скинуть тексты в обьекты.
            textPanels  = tabsTextPanel.getPanels();
            if ( textPanels != null )
            {
                deletePanels    = new LinkedList<TextPanel> ();
                for ( TextPanel textPanel : textPanels )          // ConcurrentModificationException - после удаления табика
                {
                    Log.l.debug ( "bookContentId:%s  --- textPanel = %s",bookContentId, textPanel );
                    // Скинуть текст в обьект - толкьо если были изменения. Там же установить флаг на bookContent
                    textPanel.saveTextToNode();
                    // здесь, в итераторе, удалять табики нельзя. Только вне итератора.
                    deletePanels.add ( textPanel );
                    //tabsTextPanel.removeTab ( textPanel, textPanel.getId() );
                }

                // Удаляем табики
                for ( TextPanel textPanel : deletePanels )
                {
                    Log.l.debug ( "bookContentId:%s  --- textPanel for delete = %s", bookContentId, textPanel );
                    tabsTextPanel.removeTab ( textPanel );
                }
            }

            // удаляем всю панель табиков
            workTextPanel.deleteTabsPanel ( bookContentId );

            // принимаем gui-изменения
            workTextPanel.revalidate();
        }
    }

    /**
     * Удаление файла книги и всех ее табиков  - дерево содержания, открытые тексты.
     * @param bookContentId     ID книги - ее полное имя файла.
     * @throws WEditException   Ошибки удаления.
     */
    public void deleteBook ( String bookContentId, boolean needSave ) throws WEditException
    {
        Project                             project;
        CloseBookTabFunction                closeFunction;
        TabsPanel<TreePanel<BookContent>>   tabsBookPanel;
        WorkPanel<TreePanel<BookContent>>   workBookPanel;
        TreePanel<BookContent>              treeBookPanel;

        project         = Par.GM.getFrame().getCurrentProject();
        workBookPanel   = Par.GM.getFrame().getBooksPanel();
        tabsBookPanel   = workBookPanel.getTabsPanel ( project.getId() );

        treeBookPanel   = tabsBookPanel.getPanel ( bookContentId );
        Log.l.debug ( "--- deleteBook: bookContentId = %s; treeBookPanel = %s", bookContentId, treeBookPanel );

        if ( treeBookPanel != null )
        {
            closeFunction = new CloseBookTabFunction ( treeBookPanel );
            closeFunction.closeBookPanel ( treeBookPanel, needSave );
            tabsBookPanel.removeTab ( treeBookPanel );   // Закрываем табик.
        }
    }

    public void setMenuBar ( WEMenuBar menuBar )
    {
        this.menuBar = menuBar;
    }

    public BrowserToolBar getToolbar ()
    {
        return toolbar;
    }

    public ServicePanel getServicePanel ()
    {
        return servicePanel;
    }

    public TabsPanel<EditablePanel> getAdditionalPanel ()
    {
        return additionalPanel;
    }

    public void showAdditionalPanel ()
    {
        vertSplitPane.setDividerLocation ( 0.8 );  // нет еще прошлого значения - открываем слегка снизу
    }

}
