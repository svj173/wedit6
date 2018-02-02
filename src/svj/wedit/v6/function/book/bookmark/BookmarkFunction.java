package svj.wedit.v6.function.book.bookmark;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.function.params.MultiListParameter;
import svj.wedit.v6.function.params.MultiStringParameter;
import svj.wedit.v6.function.params.ParameterCategory;
import svj.wedit.v6.gui.menu.WEMenu;
import svj.wedit.v6.gui.menu.WEMenuItem;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.function.SimpleFunction;
import svj.wedit.v6.tools.DialogTools;
import svj.wedit.v6.tools.GuiTools;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Книжные закладки.
 * <BR/> Обьект пункта меню - Хранит:
 * <BR/> А) Для возможности переходить на книгу.
 * <BR/> 1) ИД сборника     -
 * <BR/> 2) ИД книги        -
 * <BR/> 3) Текущий элемент-текст книги.   -
 * <BR/> 4) Положение курсора в текущем элементе-тексте.  -
 * <BR/> ПС: Параметры 3 и 4 периодически обновляются.
 * <BR/>
 * <BR/> Б) Просто как напоминание.
 * <BR/> 1) Текстовое название книги (в произвольном виде, т.к. набирается от руки)
 * <BR/>
 * <BR/> Применяемый параметр: массив параметров MultiStringParameter. -- todo сделать
 * <BR/>
 * <BR/> Также можно добавлять и вручную В этом случае это будет простой текст, набитый руками (как простое напоминание), без возможности автоматического перехода.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 13.11.2014 13:38
 */
public class BookmarkFunction extends SimpleFunction
{
    private final String BOOKMARK_LIST   = "BookmarkList";

    private final WEMenu bookmarkMenu;
    
    
    public BookmarkFunction ()
    {
        setId ( FunctionId.BOOKMARK );
        //setName ( "Книжные закладки" );
        setName ( "Закладки" );
        setIconFileName ( "bookmark.png" );
        setParamsType ( ParameterCategory.USER );

        bookmarkMenu  = new WEMenu ( "Книжные закладки" );
    }

    /**
     * Парсим команду. Типы команд:
     * <br/> 1) Вывести список закладок.
     * <br/> 2) Добавить текущую книгу в закладки.
     * <br/> 3) Удалить закладку.
     * <br/> 4) Очистить все.
     * <br/> todo 5) Перейти на закладку.
     * <br/>
     * @param event        Событие
     * @throws WEditException
     */
    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        String      action, str;
        int         ic;
        BookContent bookContent;
        MultiStringParameter msParam;

        action = event.getActionCommand();
        Log.l.debug ( "Bookmark: action = '%s'", action );
        Log.l.debug ( "Bookmark: source = %s", event.getSource() );
        // Вызов акции (обьекты-источники):
        // 1) тулбар над книгой -  svj.wedit.v6.gui.button.EmptyButton - [ EmptyButton : name = Work_bookPanel; functionId = BOOKMARK ]  / cmd:пусто
        // 2) из верхнего меню  -  svj.wedit.v6.gui.menu.WIconMenuItem
        // - акция Очистить список - WEMenuItem / clear

        if ( action.isEmpty() )
        {
            // Акция с тулбара над книгами -- Значит это только: Добавить текущую книгу в закладки.
            // - Взять текущую книгу
            bookContent = Par.GM.getFrame().getCurrentBookContent();
            Log.l.debug ( "Bookmark: current bookContent = '%s'", bookContent );
            if ( bookContent != null )
            {
                str     = bookContent.getName();
                Log.l.debug ( "Bookmark: add from toolbar name = '%s'", str );
                if ( noneContent(str) )
                {
                    msParam = createParam ( bookContent );
                    getOrderListParam().addItem ( msParam );   // title, link
                    rewriteMenu();
                    DialogTools.showMessage ( "Закладки", "Книга '"+str+"' \nдобавлена в закладки." );
                }
                else
                {
                    DialogTools.showMessage ( "Закладки", "Книга '"+str+"' \nуже содержится в закладках." );
                }
            }
        }
        else
        {
            if ( action.equals ( "add" ))
            {
                // ручной ввод
                str = DialogTools.showInput ( null, "Добавить в закладки", "Введите имя книги" );
                Log.l.debug ( "Bookmark: add name = '%s'", str );
                if ( str != null )
                {
                    msParam = new MultiStringParameter ( str );
                    getOrderListParam().addItem ( msParam );   // title, link
                    rewriteMenu();
                }
            }
            else if ( action.equals ( "clear" ))
            {
                ic = DialogTools.showConfirmDialog ( null, "Закладки", "Очистить закладки ?" );
                //Log.l.debug ( "Bookmark: add name = '%s'", str );
                if ( ic == 0 )
                {
                    getOrderListParam().clearList();
                    rewriteMenu();
                }
            }
            else if ( action.equals ( "edit" ))
            {
                throw new WEditException ( "Не реализовано!" );
            }
            else if ( action.equals ( "goto" ))
            {
                //throw new WEditException ( null, "Не реализовано! event.getSource() = ", event.getSource() );
                // взять текущий элемент списка - из сорцов события.
                gotoBookmark ( (WEMenuItem) event.getSource() );
            }
        }
    }

    /*
                        <param name="BookmarkList" type="MULTI_LIST">
                                <param name="Мольер" type="MULTI_STRING">
                                        <item name="bookId">/home/svj/Serg/Stories/SvjStores/stories/molier.book</item>
                                        <item name="projectId">/home/svj/Serg/Stories/SvjStores</item>
                                        <item name="projectName">SvjStores</item>
                                        <item name="textId">0,</item>
                                        <item name="cursor">1030</item>
                                </param>
                        </param>

Text
- FullPath = 0,
- ID = kadavr_1413518326259
     */
    private void gotoBookmark ( WEMenuItem source )  throws WEditException
    {
        String               projectId, projectName, bookId, textPath;
        MultiStringParameter currentParam;
        boolean              b;

        currentParam    = (MultiStringParameter) source.getObject();

        // взять ИД проекта
        projectId   = currentParam.getValue ( "projectId" );
        projectName = currentParam.getValue ( "projectName" );
        bookId      = currentParam.getValue ( "bookId" );
        textPath    = currentParam.getValue ( "textPath" );
        if ( projectId != null )
        {
            // Смотрим, может такой Сборник уже открыт.
            b = Par.GM.getFrame().containProject ( projectId );
            //throw new WEditException ( null, "projectId = ", bookId, "; hasProject = ", b );
            if ( b )
            {
                // Есть такой Сборник среди открытых.
                Par.GM.getFrame().selectProject ( projectId );
                Par.GM.getFrame().selectBook ( bookId, projectId );
                // todo
                //Par.GM.getFrame().selectNode ( textPath );
            }
            else
            {
                // Нет такого Сборника среди открытых.
                throw new WEditException ( null, "Сборника '", projectName, "' нет среди открытых.\nДанный функционал не реализован." );
            }
        }

        //throw new WEditException ( null, "Не реализовано! current :\n", currentParam );
    }

    private boolean noneContent ( String bookName )
    {
        MultiStringParameter param;

        for ( FunctionParameter wp : getOrderListParam().getList() )
        {
            param       = (MultiStringParameter) wp;
            if ( param.getName().equals ( bookName ) )  return false;
        }
        return true;
    }

    /*
                        <param name="BookmarkList" type="MULTI_LIST">
                                <param name="Мольер" type="MULTI_STRING">
                                        <item name="bookId">/home/svj/Serg/Stories/SvjStores/stories/molier.book</item>
                                        <item name="projectId">/home/svj/Serg/Stories/SvjStores</item>
                                        <item name="projectName">SvjStores</item>
                                        <item name="textId">0,</item>
                                        <item name="cursor">1030</item>
                                </param>
                        </param>

     */
    private MultiStringParameter createParam ( BookContent bookContent )
    {
        MultiStringParameter result;
        Project              project;

        result  = new MultiStringParameter ( bookContent.getName() );

        // bookId
        result.addValue ( "bookId", bookContent.getId() );
        // Project
        project = bookContent.getProject();
        if ( project != null )
        {
            result.addValue ( "projectId", project.getId() );
            result.addValue ( "projectName", project.getName () );
        }
        // Open text
        TextPanel tp = Par.GM.getFrame().getCurrentTextPanel ();
        if ( tp != null )
        {
            // nodeId - это локальный ИД, т.к. при генерации исп текущее время. Надо сохранять по пути.
            result.addValue ( "textPath", tp.getBookNode().getFullPath() );
            //result.addValue ( "textId", tp.getBookNode().getId () );
            result.addValue ( "cursor", Integer.toString ( tp.getCurrentCursor() ) );
        }

        return result;
    }

    @Override
    public void rewrite ()
    {
        String iconPath = getIcon ( Par.MENU_ICON_SIZE );
        if ( iconPath != null )
        {
            Icon icon;
            icon = GuiTools.createImageByFile ( iconPath );
            bookmarkMenu.setIcon ( icon );
        }
    }

    @Override
    public JComponent getMenuObject ( String cmd )
    {
        rewriteMenu();

        return bookmarkMenu;
    }

    private void rewriteMenu ()
    {
        WEMenuItem           menuItem;
        MultiStringParameter param;
        String               str;
        //ActionListener listener;

        // Предварительно очистить
        bookmarkMenu.removeAll();

        // Подменю - список ранее открытых проектов + пункт - Очистить список
        // - Список берется из конфига пользователя.
        // - Взять свою часть

        // Get list parameter
        for ( FunctionParameter wp : getOrderListParam().getList() )
        {
            param       = (MultiStringParameter) wp;
            menuItem    = new WEMenuItem ( param.getName() ); // title
            str         = param.getValue ( "projectName" );
            if ( str != null )  menuItem.setToolTipText ( str );      // file
            menuItem.addActionListener ( this );
            //menuItem.setActionCommand ( wp.getParam2() );
            menuItem.setActionCommand ( "goto" );
            menuItem.setObject ( param );
            bookmarkMenu.add ( menuItem );
        }

        bookmarkMenu.addSeparator();

        //menuItem    = new WEMenuItem ( Convert.concatObj ( "Макс. размер списка : ", getMaxSizeParam().getValue () ) );
        //listener    = new ChangeMaxListSizeListener ( this );
        //menuItem.addActionListener ( listener );
        //bookmarkMenu.add ( menuItem );

        menuItem    = new WEMenuItem ( "Очистить список" );
        menuItem.addActionListener ( this );
        menuItem.setActionCommand ( "clear" );
        bookmarkMenu.add ( menuItem );

        // add
        menuItem    = new WEMenuItem ( "Добавить" );
        menuItem.addActionListener ( this );
        menuItem.setActionCommand ( "add" );
        bookmarkMenu.add ( menuItem );

        // edit для возможности удаления, либо удалять через Правую кнопку мыши (ПКМ)
        menuItem    = new WEMenuItem ( "Редактировать список" );
        menuItem.addActionListener ( this );
        menuItem.setActionCommand ( "edit" );
        bookmarkMenu.add ( menuItem );

        // goto  -- двойной клик на выбраннйо позиции
        /*
        menuItem    = new WEMenuItem ( "Перейти" );
        menuItem.addActionListener ( this );
        menuItem.setActionCommand ( "goto" );
        bookmarkMenu.add ( menuItem );
        */
    }

    private MultiListParameter getOrderListParam ()
    {
        FunctionParameter   sp;
        MultiListParameter  result;

        sp  = getParameter ( BOOKMARK_LIST );
        if ( sp == null )
        {
            result = null;
        }
        else
        {
            if ( sp instanceof MultiListParameter )
                result = (MultiListParameter) sp;
            else
                result = null;
        }

        if ( result == null )
        {
            //sp  = new OrderListParameter ( BOOKMARK_LIST ); // дефолтное значение
            result  = new MultiListParameter ( BOOKMARK_LIST ); // дефолтное значение
            result.setHasEmpty ( true );
            setParameter ( BOOKMARK_LIST, result );
        }

        return result;
    }

    @Override
    public String getToolTipText ()
    {
        return "Добавить текущую книгу в Закладки.";
    }

}
