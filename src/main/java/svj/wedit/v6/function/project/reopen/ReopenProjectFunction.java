package svj.wedit.v6.function.project.reopen;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionGroup;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.params.OrderListParameter;
import svj.wedit.v6.function.params.SimpleParameter;
import svj.wedit.v6.function.project.OpenProjectEvent;
import svj.wedit.v6.gui.menu.WEMenu;
import svj.wedit.v6.gui.menu.WEMenuItem;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.obj.WPair;
import svj.wedit.v6.tools.Convert;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 15.07.2011 16:36:45
 */
public class ReopenProjectFunction extends Function
{
    // Имена параметров
    private final String PAR_PROJECT_LIST   = "ProjectList";
    private final String PAR_MAXSIZE        = "ListMaxSize";

    //private final SimpleParameter maxSize;
    /* WPair  - Title/File */
    //private final OrderListParameter projectList;

    private final WEMenu reopenMenu;


    public ReopenProjectFunction ()
    {
        setId ( FunctionId.REOPEN_PROJECT );
        setName ( "Открыть известный");
        //setMapKey ( "Ctrl/R" );
        //setIconFileName ( "reopen.png" );

        // установить свою группу
        setFunctionGroup ( FunctionGroup.REOPEN_PROJECT );

        // установить  группу функций для прослушивания - от кого хотим получать события
        setListenerGroup ( FunctionGroup.OPEN_PROJECT );

        reopenMenu  = new WEMenu ( "Открыть известный" );
    }

    public SimpleParameter getMaxSizeParam ()
    {
        SimpleParameter sp;

        sp  = (SimpleParameter) getParameter ( PAR_MAXSIZE );
        if ( sp == null )
        {
            sp  = new SimpleParameter ( PAR_MAXSIZE, 15 ); // дефолтное значение
            sp.setHasEmpty ( false );
            setParameter ( PAR_MAXSIZE, sp );
        }

        return sp;
    }

    private OrderListParameter getOrderListParam ()
    {
        OrderListParameter sp;

        sp  = (OrderListParameter) getParameter ( PAR_PROJECT_LIST );
        if ( sp == null )
        {
            sp  = new OrderListParameter ( PAR_PROJECT_LIST ); // дефолтное значение
            sp.setHasEmpty ( true );
            setParameter ( PAR_PROJECT_LIST, sp );
        }

        return sp;
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        Object              source;
        OpenProjectEvent    openProjectEvent;

        Log.l.debug ( "Start. event = %s", event );

        // Определить - это пришло из меню или от функций группы открытия Проекта?
        source  = event.getSource();
        if ( source instanceof String )
        {
            // Это акция от Старта Редактора  -- ничего не делаем, т.к. открытием проектов по старту Редактора занимаются другие.
            //setEventObject ( source.toString() );
        }
        else if ( source instanceof WEMenuItem )
        {
            // Акция от меню
            // Выдать диалог - открыть в этом окне, в новом, отменить.
            // Если в этом же - дернуть функцию закрытия проекта.
            // -- Пока открываем в новом окне.
            // Генерим событие для функции OpenProject - где source - это имя файла.
            setEventObject ( event.getActionCommand() );
        }
        else if ( source instanceof OpenProjectEvent )
        {
            // Акция от Функций
            openProjectEvent    = (OpenProjectEvent) source;
            Log.l.debug ( "openProjectEvent = %s", openProjectEvent );
            switch ( openProjectEvent.getMode() )
            {
                case OK:
                    // Проект успешно открыт (добавлен, создан). Добавить (перенести) его в списке первым.
                    addProject ( openProjectEvent.getFile(), openProjectEvent.getProjectName() );
                    break;
                case NONE_EXIST:
                    // Проект не найден. Если он есть в списке - удалить.
                    deleteProject ( openProjectEvent.getFile() );
                    break;
            }
            // Обновить свое меню
            createMenu();
        }
        else
        {
            // ошибка
            throw new WEditException ( null, "Неизвестный источник акции '", source, "'" );
        }
    }

    @Override
    public void start () throws WEditException
    {
        WPair<String, String>   wp;
        ActionEvent             event;

        Log.l.debug ( "Start" );

        // Взять из списка проектов первый проект и дернуть функцию - открыть проект. - т.е. сгенерить событие
        wp  = getOrderListParam().getItem ( 0 );
        Log.l.debug ( "first wp = ", wp );

        if ( wp != null )
        {
            event   = new ActionEvent ( wp.getParam2(), 1, wp.getParam2() );
            // дергаем полный цикл - чтобы отработались подписки
            actionPerformed ( event );
        }
        Log.l.debug ( "Finish" );
    }


    private void addProject ( File file, String projectName )
    {
        WPair<String, String>   wp;
        int                     size;

        // Если есть такой - удалить из списка
        wp  = findItem ( file.getAbsolutePath() );
        if ( wp != null ) getOrderListParam().delete ( wp );

        // Добавить первым
        getOrderListParam().setFirstItem ( projectName, file.getAbsolutePath () );

        // Проверяем макс размер. Если больше удаляем лишнее.
        size = Integer.parseInt ( getMaxSizeParam().getValue() );
        if ( getOrderListParam().size () > size )
        {
            // обрезать
            getOrderListParam().trimToSize ( size );
        }
    }

    private WPair<String, String> findItem ( String filePath )
    {
        String  fileName;

        for ( WPair<String, String> wp : getOrderListParam().getList () )
        {
            fileName    = wp.getParam2();
            if ( fileName.equals( filePath) )   return wp;
        }

        return null;
    }

    private void deleteProject ( File file )
    {
        WPair<String, String> wp;

        // Если есть такой - удалить из списка
        wp  = findItem ( file.getAbsolutePath() );
        if ( wp != null ) getOrderListParam().delete ( wp );
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
        return null;
    }

    @Override
    public JComponent getMenuObject ( String cmd )
    {
        //reopenMenu  = new WEMenu ( "Открыть известный" );

        createMenu();

        return reopenMenu;
    }

    public void createMenu ()
    {
        WEMenuItem      menuItem;
        ActionListener  listener;

        // Предварительно очистить
        reopenMenu.removeAll();

        // Подменю - список ранее открытых проектов + пункт - Очистить список
        // - Список берется из конфига пользователя.
        // - Взять свою часть

        // Get list parameter
        for ( WPair<String, String> wp : getOrderListParam().getList () )
        {
            menuItem    = new WEMenuItem ( wp.getParam1() ); // title
            menuItem.setToolTipText ( wp.getParam2() );      // file
            menuItem.addActionListener ( this );
            menuItem.setActionCommand ( wp.getParam2() );
            reopenMenu.add ( menuItem );
        }

        reopenMenu.addSeparator();

        menuItem    = new WEMenuItem ( Convert.concatObj("Макс. размер списка : ", getMaxSizeParam().getValue () ) );
        listener    = new ChangeMaxListSizeListener ( this );
        menuItem.addActionListener ( listener );
        reopenMenu.add ( menuItem );

        menuItem    = new WEMenuItem ( "Очистить список" );
        listener    = new ClearReopenListListener ( this );
        menuItem.addActionListener ( listener );
        reopenMenu.add ( menuItem );

        menuItem    = new WEMenuItem ( "Редактировать список" );
        //listener    = new ClearReopenListListener ( this );
        //menuItem.addActionListener ( listener );
        reopenMenu.add ( menuItem );
    }

    public void clearList ()
    {
        getOrderListParam().clearList ();
    }

}
