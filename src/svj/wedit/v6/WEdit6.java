package svj.wedit.v6;


import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.open.OpenParamsData;

import java.awt.*;

/**
 * Класс отвечает за запуск GUI воплощения данного редактора.
 *
 * <BR/> User: Zhiganov
 * <BR/> Date: 23.08.2007
 * <BR/> Time: 17:02:56
 */
public class WEdit6 implements Runnable
{

    public WEdit6 ()
    {
        Par.SCREEN_SIZE     = Toolkit.getDefaultToolkit().getScreenSize();
        // System.getProperty("java.version")
    }

    /**
     * Инициализация Редактора.
     * Парсит конфиг-файлы и создает все необходимые обьекты.
     *
     * Это главный AWT поток.
     *
     * Здесь должен открыться диалог загрузки, чтобы показывать что и сколько времени подгружается.
     *
     */
    @Override
    public void run ()
    {
        WEdit6InitDialog initDialog;
        OpenParamsData   openParams;

        try
        {
            // Установить имя потока, в котoром будет крутиться EmsGUI приложение
            Thread.currentThread().setName ( "WEdit" );
            Log.l.debug ( "WEdit.run: Start" );

            // запустить диалог инициализации Редактора. В отдельном потоке.
            // - здесь при иницализации разных панелей дергаются акции, которые вызывают перерисовывание фрейма, которое не применяется - т.к. не AWT поток.
            initDialog = new WEdit6InitDialog ( "Инициализация редактора" );
            initDialog.showDialog();

            openParams    = initDialog.getResult();

            Log.l.debug ( "WEdit.run: run init WEdit6." );

            // ------------- Открыть самые активные тексты -------------------
            openActive ( openParams );

            // Флаг что редактор поднят
            Par.WEDIT_STARTED = true;
            Par.NEED_REWRITE  = true;

            Par.GM.getFrame().init();
            Par.GM.getFrame().setVisible ( true );

            Log.l.debug ( "WEdit.run: Finish" );

        } catch ( Exception e )         {
            System.err.println ( "WEdit.run() Error = " + e.getMessage() );
            e.printStackTrace();
        }
    }

    private void openActive ( OpenParamsData opensData )
    {
        String                  projectId, bookId, chapterPath, chapterId, str;
        BookContent             bookContent;
        TreePanel<BookContent>  treePanel;
        BookNode                bookNode;
        TextPanel               textPanel;

        Log.l.debug ( "Start.openActive: opensData = %s", opensData );
        if ( opensData == null )  return;

        // Взять ИД активного Сборника
        projectId = opensData.getActiveProject();
        Log.l.debug ( "openActive: projectId = %s", projectId );
        if ( projectId != null )
        {
            // Выбрать в ГУИ проект по ID сборника
            Par.GM.selectProject ( projectId );
            // Взять активную книгу (ИД) в этом сборнике
            bookId = opensData.getActiveBook();     // пример: /home/svj/Serg/Stories/SvjStores/test/test01.book
            Log.l.debug ( "openActive: bookId = %s", bookId );
            if ( bookId != null )
            {
                // Выбрать в гуи книгу по ID (имя файла) книги и ИД сборника. Взять панель дерева книги.
                treePanel   = Par.GM.selectBook ( bookId, projectId );
                // Взять активный текст книги - в виде полного пути. Пример: 1,2,
                chapterPath = opensData.getActiveChapter();
                if ( (chapterPath != null) && ( treePanel != null) )
                {
                    Log.l.debug ( "openActive: chapterPath = %s", chapterPath );    // 1,2,
                    bookContent = treePanel.getObject();
                    bookNode    = bookContent.getBookNodeByFullPath ( chapterPath );
                    if ( bookNode != null )
                    {
                        chapterId   = bookNode.getId();
                        // kadavr_1413518326259 - это чисто местный, локальный ИД, т.к. числа в нем при каждом открытии меняются. Т.е. его нельзя сохранять в параметрах.
                        Log.l.debug ( "openActive: chapterId = %s", chapterId );
                        // Выбрать таб эпизода в книге. Выдать обьект эпизода.
                        textPanel   = Par.GM.selectNode ( chapterId, bookId );
                        // Установить фокус в тексте. Курсор сохраняется и устанавливается в рамках самого текста.
                        //Par.GM.setFocus ( chapterId, bookId );
                        if ( textPanel != null ) textPanel.activeFocus();
                        {
                            // Выбрать в дереве книги данный эпизод
                            //str = textPanel.getBookNode().getId();    -- kadavr_1413518326259
                            treePanel.selectNode ( chapterId );
                        }
                    }
                }
            }
        }
    }

    public static void main ( String[] args )
    {
        String  str;
        WEdit6  edit;

        System.setErr ( System.out );
        //if ( args.length > 0 )  configFile  = args[0];

        // Заносим обработчик непойманных ошибок
        Thread.setDefaultUncaughtExceptionHandler ( new WeDefaultUncaughtExceptionHandler() );

        try
        {
            Par.MODULE_HOME     = System.getProperty ( "module.home" );

            Thread.currentThread().setName ( "main" );

            // ----------- Инициализируем логгер   ----------------------
            //str = System.getProperty ( "log4j" );
            //str = FileTools.createFileName ( Par.MODULE_HOME, "conf/logger.txt" );
            //System.out.println ( "log_file = '"+str+"'" );
            //Log.init ( str );
            Log.l.info ( "\n----------------------------------------------------------------------------------" );

            // - Определить домашнюю директорию пользователя
            // Попытка выяснить что за операционная система - по параметру хранения логина пользователя.
            // - Linux   - параметр USER
            // - Windows - параметр USERNAME

            // USER_LOGIN  - только для изменяемых параметров Редактора (dynamic)
            str = System.getenv ( "USERNAME" ); // for Windows
            Log.l.debug ( "USERNAME = %s", str );
            if ( str != null )
            {
                Par.USER_LOGIN  = str;
            }
            else
            {
                str = System.getenv ( "USER" ); // for Linux
                if ( str != null )  Par.USER_LOGIN  = str;
            }
            Log.l.debug ( "User = '%s'", str );

            // HOME - домашняя директория пользователя. Именно в ней будет лежать конфиг пользователя. В директории '.wedit6'
            str = System.getenv ( "HOME" ); // for Windows
            Log.l.debug ( "HOME = %s", str );
            if ( str != null )  Par.USER_HOME_DIR  = str;

            // Создать и инициализировать Редактор
            edit = new WEdit6();

            // Запустить в отдельной самостоятельной нити
            javax.swing.SwingUtilities.invokeLater ( edit );
            //System.out.println ( "WEdit-6. finish" );

        } catch ( Throwable e ) {
            // Обработать  ошибку и закрыть Редактор..
            str = "WEdit-6.main ERROR: " + e.getMessage();
            System.err.println ( str );
            e.printStackTrace();
            System.exit ( 12 );
        }
    }

}
