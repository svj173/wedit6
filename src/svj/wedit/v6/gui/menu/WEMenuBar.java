package svj.wedit.v6.gui.menu;


import svj.wedit.v6.Par;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.DialogTools;
import svj.wedit.v6.tools.GuiTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Расширили JMenuBar добавив возможность поиска вложенного обьекта по его ИД.
 * <BR/> А также - добавление меню в конкретную область. Если область отсутствует - создается (рекурсивно)
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 15.07.2011 16:51:09
 */
public class WEMenuBar extends JMenuBar
{
    //private WeakReference<JMenu> helpMenu = null;
    
    public WEMenuBar ()
    {
        super();

        WEMenu menu;
        JComponent c;

        // Файл
        menu    = createFileMenu();
        add ( menu );

        // Поиск
        menu    = createSearchMenu();
        add ( menu );

        // Преобразовать
        menu    = createConvertMenu ();
        add ( menu );

        // Импортировать
        menu    = createImportMenu ();
        add ( menu );

        // Сборник
        //menu    = createProjectMenu ();
        //add ( menu );

        // Книга
        menu    = createBookMenu();
        add ( menu );

        // Настройки
        menu    = createOptionMenu();
        add ( menu );

        // Статистика
        menu    = createStatisticMenu();
        add ( menu );

        // help -- не реализовано в java
        setHelpMenu ( new JMenu("Помощь") );
    }

    private WEMenu createImportMenu ()
    {
        WEMenu          result;
        JComponent      menu;

        result  = new WEMenu ( "Импортировать" );

        // old-book
        menu    = getMenu ( FunctionId.IMPORT_FROM_WE1_BOOK );
        if ( menu != null )  result.add ( menu );

        // TXT
        menu    = getMenu ( FunctionId.IMPORT_FROM_TXT );
        if ( menu != null )  result.add ( menu );

        // DOC, RTF
        menu    = getMenu ( FunctionId.IMPORT_FROM_DOC );
        if ( menu != null )  result.add ( menu );

        //result.addSeparator();

        return result;
    }

    private WEMenu createConvertMenu ()
    {
        WEMenu          result;
        JComponent      menu;

        result  = new WEMenu ( "Преобразовать" );

        menu    = getMenu ( FunctionId.CONVERT_SELECTION_TO_RTF );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.CONVERT_SELECTION_TO_RTF_2 );
        if ( menu != null )  result.add ( menu );

        result.addSeparator();

        menu    = getMenu ( FunctionId.CONVERT_TO_HTML );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.CONVERT_SELECTED_TO_HTML );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.CONVERT_TO_DOC );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.CONVERT_TO_TXT );
        if ( menu != null )  result.add ( menu );

        result.addSeparator();

        menu    = getMenu ( FunctionId.CONVERT_CONTENT_TO_RTF );
        if ( menu != null )  result.add ( menu );

        return result;
    }

    /* Верхнее меню - "Книга". */
    private WEMenu createBookMenu ()
    {
        WEMenu          result;
        JComponent      menu;

        result  = new WEMenu ( "Текущая книга" );

        // ----------------------- Описание Элементов Книги  -----------------------

        menu    = getMenu ( FunctionId.EDIT_DESC_ELEMENT );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.EDIT_DESC_ALL_ELEMENTS );
        if ( menu != null )  result.add ( menu );

        result.addSeparator();

        // ----------------------- Элементы Книги ---------------------------

        // - Добавить
        menu    = getMenu ( FunctionId.ADD_ELEMENT_IN );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.ADD_ELEMENT_AFTER );
        if ( menu != null )  result.add ( menu );

        result.addSeparator();

        // Paste

        menu    = getMenu ( FunctionId.COPY_ELEMENT );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.CUT_ELEMENT );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.PASTE_ELEMENT_IN );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.PASTE_ELEMENT_AFTER );
        if ( menu != null )  result.add ( menu );

        result.addSeparator();

        menu    = getMenu ( FunctionId.EDIT_ELEMENT );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.EDIT_BOOK_PARAMS );
        if ( menu != null )  result.add ( menu );

        result.addSeparator();

        menu    = getMenu ( FunctionId.DELETE_ELEMENT );
        if ( menu != null )  result.add ( menu );

        result.addSeparator();

        // ----------------------- Поиск -----------------------

        menu    = getMenu ( FunctionId.SEARCH );
        if ( menu != null )  result.add ( menu );


        result.addSeparator();

        // ----------------------- Работа с Текстом -----------------------

        // Открыть Редактор текста для выбранного элемента книги
        menu    = getMenu ( FunctionId.OPEN_TEXT );
        if ( menu != null )  result.add ( menu );

        result.addSeparator();

        // Закладки
        menu    = getMenu ( FunctionId.BOOKMARK );
        if ( menu != null )  result.add ( menu );

        result.addSeparator();

        // ----------------------- Сервис с текущей книгой -----------------------

        // Убрать лидирующие пробелы - первые и последние в строках.
        menu    = getMenu ( FunctionId.TRIM_BOOK_TEXT );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.REPLACE_BOOK_TEXT );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.BLOCK_REPLACE );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.SET_ALL_TEXT_AS_SIMPLE );
        if ( menu != null )  result.add ( menu );

        return result;
    }

    private WEMenu createOptionMenu ()
    {
        WEMenu          result;
        JComponent      menu;

        result  = new WEMenu ( "Настройки" );

        menu    = getMenu ( FunctionId.DECORATOR );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.CHANGE_TOOL_BAR_ICON_SIZE );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.CHANGE_PANEL_ICON_SIZE );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.CHANGE_MENU_ICON_SIZE );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.MEMORY_CHECK );
        if ( menu != null )  result.add ( menu );

        // todo
        //menu    = getMenu ( FunctionId.CHANGE_TREE_ICON_SIZE );
        //if ( menu != null )  result.add ( menu );

        return result;
    }

    private WEMenu createStatisticMenu ()
    {
        WEMenu          result;
        JComponent      menu;

        result  = new WEMenu ( "Статистика" );

        // Все открытые. Подробно - ИД, файл, размер, позиция курсора.
        menu    = getMenu ( FunctionId.STAT_OPEN );
        if ( menu != null )  result.add ( menu );

        // Список всех книг - по Сборникам, без доп данных. (Парсятся проекты)
        menu    = getMenu ( FunctionId.STAT_BOOK );
        if ( menu != null )  result.add ( menu );

        // Статистика по редактированию Эпизодов. Смотреть исправления за - день, неделю, месяц...
        menu    = getMenu ( FunctionId.STAT_BOOK_EDIT );
        if ( menu != null )  result.add ( menu );

        return result;
    }

    /**
     * Глобальный поиск, замены - в рамках Сборника, а то и всех открытых Сборников.
     * @return
     */
    private WEMenu createSearchMenu ()
    {
        WEMenu          result;
        JComponent      menu;

        result  = new WEMenu ( "Поиск" );

        //menu    = getMenu ( FunctionId.SEARCH );
        //if ( menu != null )  result.add ( menu );

        return result;
    }

    private WEMenu createFileMenu ()
    {
        WEMenu          result;
        JComponent      menu, subMenu;

        result  = new WEMenu ( "Файл" );

        // ------------------------- Сборник ------------------------

        menu    = getMenu ( FunctionId.SYNC_PROJECT );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.ARCHIVE_PROJECT );
        if ( menu != null )  result.add ( menu );

        result.addSeparator();

        menu    = getMenu ( FunctionId.NEW_PROJECT );
        if ( menu != null )  result.add ( menu );

        result.addSeparator();

        menu    = getMenu ( FunctionId.OPEN_PROJECT );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.REOPEN_PROJECT );
        if ( menu != null )  result.add ( menu );

        result.addSeparator();

        menu    = getMenu ( FunctionId.SAVE_ALL_PROJECTS );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.SAVE_ABSOLUTE_ALL_PROJECTS );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.ZIP_ALL_PROJECTS );
        if ( menu != null )  result.add ( menu );

        result.addSeparator();

        // --------------------------- Раздел (сборника?) ---------------------

        /*
        menu    = new WEMenu("Добавить");
        result.add ( menu );
        subMenu = getMenu ( FunctionId.ADD_SECTION_AFTER );
        if ( subMenu != null ) menu.add ( subMenu );     // Добавить Раздел после выбранного
        subMenu = getMenu ( FunctionId.ADD_SECTION_IN );
        if ( subMenu != null ) menu.add ( subMenu );     // Добавить Раздел в выбранный первым
        */
        menu    = getMenu ( FunctionId.ADD_SECTION_AFTER );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.ADD_SECTION_IN );
        if ( menu != null )  result.add ( menu );


        menu    = getMenu ( FunctionId.EDIT_SECTION );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.DELETE_SECTION );
        if ( menu != null )  result.add ( menu );

        result.addSeparator();

        // ------------------------- Книга --------------------------------

        menu    = getMenu ( FunctionId.CREATE_BOOK );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.OPEN_BOOK );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.EDIT_BOOK_TITLE );
        if ( menu != null )  result.add ( menu );

        menu    = getMenu ( FunctionId.DELETE_BOOK );
        if ( menu != null )  result.add ( menu );


        return result;
    }

    private JComponent getMenu ( FunctionId projectId )
    {
        JComponent  menu;
        Function    f;

        f       = Par.GM.getFm().get ( projectId );
        if ( f != null )
            menu    = f.getMenuObject("menu");
        else
        {
            menu = null;
            Log.l.error ( "---------------- Отсутствует функция '%s'.", projectId );
        }

        return menu;
    }

    public WEMenuItem getMenuItem ( String id )
    {
        String idc;
        int ncomponents = this.getComponentCount();
        Component[] component = this.getComponents();
        for (int i = 0 ; i < ncomponents ; i++)
        {
            Component comp = component[i];
            if (comp instanceof WEMenuItem)
            {
                WEMenuItem mi = (WEMenuItem) comp;
                idc = mi.getId();
                if ( idc.equals ( id ) )   return mi;
            }
        }
        return null;
    }

    public WEMenu getMenu ( String id )
    {
        String idc;
        Component comp;
        int ncomponents = this.getComponentCount();
        Component[] component = this.getComponents();
        for ( int i = 0; i < ncomponents; i++ )
        {
            comp = component[i];
            if (comp instanceof WEMenu)
            {
                WEMenu mi = (WEMenu) comp;
                idc = mi.getId();
                if ( idc.equals ( id ) )   return mi;
            }
        }
        return null;
    }

    public void rewrite ()
    {
        GuiTools.rewriteComponents ( this );
        /*
        Component comp;
        int ncomponents = this.getComponentCount();
        Component[] component = this.getComponents();
        for ( int i = 0; i < ncomponents; i++ )
        {
            comp = component[i];
            if (comp instanceof WEMenu)
            {
                WEMenu mi = (WEMenu) comp;
                mi.rewrite();
            }
        }
        */
    }

    // НЕ исп.
    public WEMenuBar clone ( String id )
    {
        Component   comp;
        WEMenuBar   result;
        WEMenu      mi, newmi;
        JSeparator  sep1, sep2;
        JComboBox   box1, box2;

        result  = new WEMenuBar ();
        int ncomponents = this.getComponentCount();
        Component[] component = this.getComponents();
        for ( int i = 0; i < ncomponents; i++ )
        {
            comp = component[i];
            if (comp instanceof WEMenu)
            {
                mi      = (WEMenu) comp;
                newmi   = mi.clone(id);
                result.add ( newmi );
                continue;
            }
            if (comp instanceof JSeparator)
            {
                sep1    = (JSeparator) comp;
                sep2 = new JSeparator (sep1.getOrientation());
                result.add ( sep2 );
                continue;
            }
            if ( comp instanceof JComboBox )
            {
                box1    = (JComboBox) comp;
                box2    = new JComboBox (box1.getModel());
                result.add ( box2 );
                //continue;
            }
        }
        return result;
    }


    /* Попытка создать help меню, расположенное справа. Не получилось. (Взято от SUN). */
    public void setHelpMenu ( JMenu menu )
    {
        JMenu       helpMenu;
        JMenuItem   menuItem;

        // пропуск по горизонтали. т.е. сдвинуть данное меню максимально вправо.
        add ( Box.createHorizontalGlue() );

        helpMenu = new JMenu ( "Помощь" );
        add ( helpMenu );

        menuItem = new JMenuItem("О Редакторе"); // Инструкция пользователя с функцией - скинуть в файл (PDF).
        menuItem.setComponentOrientation ( ComponentOrientation.RIGHT_TO_LEFT );
        helpMenu.add ( menuItem );

        // Об авторе
        menuItem = new JMenuItem("Об авторе");
        menuItem.setComponentOrientation ( ComponentOrientation.RIGHT_TO_LEFT );
        menuItem.addActionListener ( new ActionListener ()
        {
            @Override
            public void actionPerformed ( ActionEvent event )
            {
                DialogTools.showMessage ( "Об авторе", "Сергей Афанасьев\n- s_afa@yahoo.com\n- http://samlib.ru/a/afanasxew_s/" );
            }
        } );
        helpMenu.add ( menuItem );
        /*
        JComponent  jmenu;
        jmenu    = getMenu ( FunctionId.CHANGE_TOOL_BAR_ICON_SIZE );
        if ( jmenu != null )
        {
            jmenu.setComponentOrientation ( ComponentOrientation.RIGHT_TO_LEFT );
            menu.add ( jmenu );
        }
        */
    }

    /**
     * Gets the help menu for the menu bar.  This method is not yet
     * implemented and will throw an exception.
     * Gets the help menu for the menu bar.
     *
     * @return the <code>JMenu</code> that delivers help to the user
     */
    /*
    public JMenu getHelpMenu ()
    {
        //throw new Error ( "getHelpMenu() not yet implemented." );
        JMenu help = null;
        if ( helpMenu != null )
        {
            JMenu tmphelp = helpMenu.get ();
            if ( tmphelp != null && tmphelp.getParent () == this )
                help = tmphelp;
        }

        if ( helpMenu != null && help == null )
            helpMenu = null;
        return help;
    }
    */

    /*
    public void addMenu ( JComponent newMenu )
    {
        if ( newMenu == null )  return;

        if ( newMenu instanceof WEMenu )
        {
            addMenu ( (WEMenu) newMenu );
        }
        else if ( newMenu instanceof WEMenuItem )
        {
            addMenuItem ( (WEMenuItem) newMenu );
        }
        else
        {
            add (newMenu);
        }
    }

    public void addMenu ( WEMenu newMenu )
    {
        String  menuPath;
        WEMenu  menu;

        Log.l.debug ( "--- Start. newMenu = ", newMenu );
        if ( newMenu == null )  return;

        //  взять из меню - полный путь до точки расположения этого меню (другое меню). Взять номер группы в точке (меню).
        menuPath    = newMenu.getMenuPath();
        Log.l.debug ( "--- menuPath = ", menuPath );

        menu    = getMenuByPath ( menuPath );
        Log.l.debug ( "menu = ", menu );
        if ( menu != null ) menu.add ( newMenu, newMenu.getGroup() );
    }

    public void addMenuItem ( WEMenuItem menuItem )
    {
        String  menuPath;
        WEMenu  menu;

        Log.l.debug ( "--- Start. newMenu = ", menuItem );
        if ( menuItem == null )  return;

        //  взять из меню - полный путь до точки расположения этого меню (другое меню). Взять номер группы в точке (меню).
        menuPath    = menuItem.getMenuPath();
        Log.l.debug ( "--- menuPath = ", menuPath );

        menu        = getMenuByPath ( menuPath );
        Log.l.debug ( "menu = ", menu );
        if ( menu != null ) menu.add ( menuItem, menuItem.getGroup() );
    }

    private WEMenu getMenuByPath ( String menuPath )
    {
        int         i;
        WEMenu      menu, menu1;

        Log.l.debug ( "Start. menuPath = ", menuPath );

        if ( menuPath == null )  return null;

        // Разобрать строку пути Меню
        String[] smenu    = menuPath.split ( "/" );
        if ( smenu.length == 0 )
        {
            // TODO По идее, такие ошибки надо запоминать где-то и потом вывести полный список пользователю в красном окне.
            Log.l.error ( "Menu path is NULL" );
            return null;
        }
        Log.l.debug ( "menuPath size = ", smenu.length );
    
        // Если элементы пути отсутствуют - создать  -- todo рекурсивно

        // - Если отсутствует корневой элемент меню - создать
        menu    = getMenu ( smenu[0] );
        Log.l.debug ( "parent menu = ", menu );
        if ( menu == null )
        {
            menu    = new WEMenu ( smenu[0] );
            //menu.setMenuPath ( smenu[0] );
            add ( menu );
        }

        for ( i=1; i<smenu.length; i++ )
        {
            menu1    = menu.getMenu ( smenu[i] );
            Log.l.debug ( i, ". name = ", smenu[i], ", menu = '", menu1, "'" );
            if ( menu1 == null )
            {
                menu1 = new WEMenu ( smenu[i] );
                menu.add ( menu1 );
                Log.l.debug ( i, ". Create new menu. name = ", smenu[i], ", menuPath = ", menuPath );
            }
            menu    = menu1;
        }

        return menu;
    }
    */

}

