package svj.wedit.v6;


import svj.wedit.v6.content.ContentFrame;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.menu.WEMenuBar;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.manager.ConfigManager;
import svj.wedit.v6.manager.FunctionManager;
import svj.wedit.v6.manager.user.UserParamsManager;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.book.BookTitle;
import svj.wedit.v6.obj.book.xml.BookContentStaxParser;
import svj.wedit.v6.obj.open.*;
import svj.wedit.v6.tools.DialogTools;
import svj.wedit.v6.tools.ProjectTools;

import javax.swing.*;

import java.io.File;
import java.util.*;

/**
 * - Void - the result type returned by this {@code SwingWorker's}  {@code doInBackground} and {@code get} methods
 * - String - the type used for carrying out intermediate results by this  {@code SwingWorker's} {@code publish} and {@code process} methods
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 02.04.2014 11:23
 */
public class WEdit6InitWorker    extends SwingWorker<OpenParamsData,String>
{
    private final WEdit6InitDialog  dialog;
    private OpenParamsData          openParamsData;


    public WEdit6InitWorker ( WEdit6InitDialog dialog )
    {
        this.dialog = dialog;
    }

    @Override
    protected OpenParamsData doInBackground () throws Exception
    {
        init();

        return openParamsData;
    }

    private void init () throws WEditException
    {
        WEditShutdown       shutdown;
        GeneralManager      gm;
        FunctionManager     fm;
        ContentFrame        content;
        ConfigManager       config;
        UserParamsManager   upm;
        StringBuilder       errMsg;  // Сообщения об ошибках открытия.
        long                mTime, nTime;

        Log.l.debug ( "Start" );

        mTime   = System.currentTimeMillis();
        errMsg  = new StringBuilder ( 128 );

        try
        {
            // Создать пустой обьект GM
            gm  = new GeneralManager();
            // Занести
            Par.GM  = gm;

            publish ( "Старт" );
            // Загрузить конфиги -  Пользователя, Редактора -- Надо ли его хранить в памяти?
            config  = new ConfigManager();
            config.init();
            gm.setConfig ( config );
            nTime   = System.currentTimeMillis();
            publish ( "Загрузка конфига" + createWorkTime ( mTime ) );

            fm  = new FunctionManager();
            fm.init ();
            gm.setFm ( fm );
            mTime   = System.currentTimeMillis();
            publish ( "Инициализация функций" + createWorkTime ( nTime ) ); 
            // Подписать функции друг на друга.
            fm.signFunction ();
            nTime   = System.currentTimeMillis();
            publish ( "Подпись функций" + createWorkTime ( mTime ) );

            /*
            // Загрузить конфиг Редактора. Имя конфиг файла - фиксированно. Здесь хранится дефолтная структура книги.
            config  = new Config ( "config.txt", configDir );

            // Инсталлировать массив элементов книги (по умолчанию)
            em      = config.createEm();
            gm.setDefaultEm (em);
             */

            // Загрузить и инсталлировать в функции параметры пользователя -- наполнить фрейм toolBar
            upm = new UserParamsManager();
            openParamsData  = upm.start ( errMsg );
            mTime   = System.currentTimeMillis ();
            publish ( "Загрузка параметров пользователя" + createWorkTime ( nTime ));

            // todo Взять номер версии и номер билда

            // Создать JFrame обьект Content - здесь уже исп элементы и функции
            content = new ContentFrame();
            gm.setContent ( content );
            nTime   = System.currentTimeMillis();
            publish ( "Создание фрейма" + createWorkTime ( mTime ) );

            // наполнить фрейм Главным меню
            createMenu ( content, fm, content );
            mTime   = System.currentTimeMillis();
            publish ( "Создать Главное меню" + createWorkTime ( nTime ) );

            // ---- Здесь все модули и функции подняты.
            // Стартануть функции - декоратор, reopen... Reopen - не должен открывать свой проект - это просто список.
            fm.startAll ( errMsg );
            nTime   = System.currentTimeMillis();
            publish ( "Запуск функций" + createWorkTime ( mTime ) );


            // Открыть ранее открытое - по openParamsData
            openProjects ( openParamsData, errMsg );
            mTime   = System.currentTimeMillis();
            publish ( "Отрытие сборников и книг" + createWorkTime ( nTime ) );


            //shutdown    = new WEditShutdown ( function );
            shutdown    = new WEditShutdown ();
            Runtime.getRuntime().addShutdownHook ( shutdown );
            //nTime   = System.currentTimeMillis();
            publish ( "Создание Shutdown " + createWorkTime ( mTime ) );

            // Если были стартовые ошибки - вывести их отдельным диалогом.
            if ( errMsg.length () > 0 )
            {
                Log.l.error ( ">>>>>>>>>>>>>> Start WEdit6 error >>>>>>>>>>>>>> :\n%s", errMsg );
                DialogTools.showError ( errMsg, "Ошибки открытия редактора." );
            }

        } catch ( WEditException we )        {
            // Это уже фатальные ошибки открытия редактора.
            throw we;
        } catch ( Exception e )        {
            Log.l.error ( "WEdit.init:", e );
            throw new WEditException ( "init.wedit.error.throwable", e );
        }
    }

    private String createWorkTime ( long t )
    {
        StringBuilder sb;
        long          time;

        time = System.currentTimeMillis() - t;
        sb   = new StringBuilder ( 16 );
        sb.append ( "   (" );
        //sb.append ( time/1000 );
        sb.append ( time );
        sb.append ( ")" );

        return sb.toString();
    }

    protected void process ( List<String> list )
    {
        for ( String msg : list )
        {
            dialog.addText ( msg );
        }
    }

    /* Открыть старые проекты-сборники. */
    private void openProjects ( OpenParamsData opensData, StringBuilder errMsg )
    {
        String                  projectDirName, fileName;
        Project                 project;
        int                     x, y, width, height;
        BookContent             bookContent;
        BookNode                bookNode;
        BookTitle               bookTitle;
        BookContentStaxParser   bookParser;

        Log.l.debug ( "\n\nStart. opensData = %s", opensData );

        if ( opensData == null )  return;

        // frame
        // - location
        x   = opensData.getLocationX();
        y   = opensData.getLocationY();
        if ( (x >= 0) && (y >= 0) )  Par.GM.getFrame().setLocation ( x, y );
        // - size
        width    = opensData.getWidth();
        height   = opensData.getHeight();
        if ( (width > 0) && (height > 0) )
        {
            // - проверяем размер экрана, взятый из файла с текущим размером -- Par.SCREEN_SIZE
            if ( width  > Par.SCREEN_SIZE.width )   width  = Par.SCREEN_SIZE.width  - x;
            if ( height > Par.SCREEN_SIZE.height )  height = Par.SCREEN_SIZE.height - y;
            Par.GM.getFrame().setSize ( width, height );
        }

        bookParser      = new BookContentStaxParser();

        for ( ProjectInfo openProject : opensData.getOpenProjects() )
        {
            Log.l.debug ( "--- openProject = %s", openProject );
            projectDirName  = openProject.getFileName();
            publish ( "  Сборник : " + openProject.getFileName() );

            project         = ProjectTools.loadProject ( projectDirName );
            Log.l.debug ( "--- project = %s", project );
            if ( project != null )
            {
                bookTitle = null;
                // установить новый проект в систему (добавить к списку открытых проектов).
                try
                {
                    Par.GM.addProject ( project );
                    for ( BookInfo openBook : openProject.getOpenBooks() )
                    {
                        Log.l.debug ( "----- openBook = %s", openBook );
                        publish ( "    Книга : " + openBook.getFileName() );

                        // Распарсить файл книги в дерево.
                        fileName    = openBook.getFileName();
                        if ( fileName == null )
                        {
                            Log.l.error ( "У обьекта 'открытая книга' [%s] отсутствует имя файла.", openBook );
                            errMsg.append ( "У обьекта 'открытая книга' [" );
                            errMsg.append ( openBook );
                            errMsg.append ( "] отсутствует имя файла." );
                        }
                        else
                        {
                            // Здесь мы не знаем имя книги - только имя файла. Имя книги должно при парсинге xml файла перезаписаться.
                            bookContent = bookParser.read ( new File ( fileName ), "222-333", "11" );
                            bookContent.setProject ( project );

                            // Занести книгу в титл
                            bookTitle = project.getBookTitle ( fileName );
                            if ( bookTitle != null ) bookTitle.setBookContent ( bookContent );
                            Log.l.debug ( "[BTEdit] bookContent add to bookTitle = %s", bookTitle );

                            // Открыть дерево в новом табике в окне Книг.
                            // установить новый проект в систему (добавить к списку открытых проектов)).
                            Par.GM.addBookContent ( bookContent, project );

                            for ( TextInfo textInfo : openBook.getOpenTexts() )
                            {
                                Log.l.debug ( "------- textInfo = %s", textInfo );
                                // Взять из книги данную главу
                                bookNode    = bookContent.getBookNodeByFullPath ( textInfo.getFullPathName() );
                                Log.l.debug ( "--------- find bookNode = %s", bookNode );

                                // Открытые текста - по полному пути - от части к корню, через слеш.  - курсор установить
                                Par.GM.addBookText ( bookNode, bookContent, textInfo.getCursor() );
                            }
                        }
                    }
                } catch ( Exception e )                {
                    String str;
                    // Не смогли открыть старый сборник - не страшно
                    Log.l.error ( "WEdit.openProjects error: file = " + projectDirName, e );
                    errMsg.append ( "Ошибка открытия книги '" );
                    if ( bookTitle == null )
                        str = "Null";
                    else
                        str = bookTitle.getName();
                    errMsg.append ( str );
                    errMsg.append ( "'\n сборника '" );
                    errMsg.append ( project.getName() );
                    errMsg.append ( "' :\n " );
                    errMsg.append ( e );
                    errMsg.append ( "\n" );
                    publish ( errMsg.toString() );
                }
            }
        }

        //projectId = opensData.getActiveProject();

        Log.l.debug ( "Finish\n" );
    }

    private void createMenu ( ContentFrame frame, FunctionManager fm, ContentFrame content )
    {
        WEMenuBar menuBar;

        menuBar = new WEMenuBar();
        frame.setMenuBar ( menuBar );

        content.getRootPane().setJMenuBar ( menuBar );
    }

    @Override
    protected void done ()
    {
        /*
        try
        {
            get();
        } catch ( Exception ignore )      {
            Log.l.error ( ".done: error", ignore );
        }
        */
        dialog.setVisible ( false );
        Log.l.debug ( ".done: Finish." );
    }

}
