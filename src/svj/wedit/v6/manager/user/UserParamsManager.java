package svj.wedit.v6.manager.user;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.params.ParameterCategory;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.gui.tabs.TabsPanel;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.ConfigParam;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.obj.open.OpenParamsData;
import svj.wedit.v6.tools.*;
import svj.wedit.v6.xml.UserParamsStaxParser;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Map;


/**
 * Управляет конфиг-параметрами категории USER.
 * <BR/> При старте Редактора - загружает данные (user_params.xml) и обновляет параметры в функциях.
 * <BR/> При закрытии Редактора - сохраняет новый файл user_params.xml.
 * <BR/> Файл user_params.xml сохраняется в домашней директории пользователя в поддиректории '.wedit6' (unix).
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 10.08.2011 10:35:52
 */
public class UserParamsManager
{
    public void close ( boolean useDialog )
    {
        String              configDir, str;
        FileOutputStream    file;
        File                f;
        boolean             b;

        Log.l.info ( "Start. Save dynamic parameters");

        file = null;
        str  = null;

        try
        {
            // Взять директорию конфиг-файлов.
            configDir   = Convert.concatObj ( Par.USER_HOME_DIR, File.separator, ".wedit6", File.separator );

            // Создать директорию если она не существует
            f   = new File (configDir);
            if ( ! f.exists() )
            {
                b = f.mkdir();
                if ( ! b )  throw new WEditException ( null, "Не удалось создать директорию пользовательских конфиг-файлов '", f, "'." );
            }

            // Сохранить динамические параметры типа User.
            //  Если нет Логина - файл = user_params.xml
            //  Если есть Логин - файл = (login)/user_params.xml
            //  - в отдельную директорию - чтобы пользвоатель мог
            //    распространять на нее свои права доступа.

            // - Создать файл
            str     = configDir + ConfigParam.USER_PARAMS_FILE;

            XmlTools.setCodePage ( Par.CODE_CONFIG );

            file    = new FileOutputStream ( str );

            // Сохранить параметры - функций, информацию об открытых Проекта, Книгах, Главах.
            saveParameters ( file );

            file.flush();
            //file.close();

        } catch ( Throwable e )        {
            // Здесь все и заканчивается т.к. Редактор закрывается либо Новая книга.
            Log.l.error ( "Close function error", e);
            if ( useDialog ) DialogTools.showError ( e.getMessage(), "Ошибка сохранения пользовательских параметров" );
        } finally {
            FileTools.closeFileStream ( file, str );
        }
        Log.l.info ( "Finish. Save dynamic parameters");
    }

    /**
     * Загрузить динамические параметры.
     * Занести их в функции.
     * @return Обьект, описывающий ранее открытые проекты, книги, главы. Для их стартового открытия.
     * @param errMsg   Сообщения об ошибках.
     * @throws WEditException   Ошибки чтения из файла (с диска), либо ошибки валидации файла.
     */
    public OpenParamsData start ( StringBuilder errMsg )      throws WEditException
    {
        String          configDir, str;
        FileInputStream file;
        File            f;
        OpenParamsData  result;

        Log.l.info ( "Start. Load dynamic parameters." );

        result  = null;

        try
        {
            // Взять директорию конфиг-файлов.
            configDir   = Convert.concatObj ( Par.USER_HOME_DIR, File.separator, ".wedit6", File.separator );

            // Загрузить динамические параметры категории WEdit - wedit_params.xml  -- НЕ исп параметры этой категории
            /*
            f       = new File ( configDir + WEDIT_PARAMS );
            // Проверка - есть ли такой файл
            if ( f.exists() )
            {
                file    = new FileInputStream ( f );
                loadParameters ( file, f );
            }
            */

            // Дин параметры категории User  - идут воторыми, т.к. более весомые.
            str     = configDir + ConfigParam.USER_PARAMS_FILE;
            f       = new File ( str);
            // Проверка - есть ли такой файл
            if ( f.exists() )
            {
                file    = new FileInputStream ( f );
                // загрузить
                result  = loadParameters ( file, f, errMsg );
            }

        } catch ( WEditException we )         {
            errMsg.append ( we.getMessage() );
            errMsg.append ( "\n" );
            //throw we;
        } catch ( Exception e )         {
            Log.l.error ( "Load error", e );
            errMsg.append ( e );
            errMsg.append ( "\n" );
            //throw new WEditException ( "error.DinamicParameter.load.system", e );
        } finally   {
            Log.l.info ( "Finish. Load dynamic parameters." );
        }

        return result;
    }

    /**
     * Загрузить динамические параметры.
     * <BR/> Используем Stax парсер который сразу же ображается к нужнйо функции и отдает ей полученные значения. Без предварительного сохранения в буфере.
     *
     * @param file  Потом Исходного xml-файла с динамическими параметрами.
     * @param f     Собственно Исходный xml-файл с динамическими параметрами - толкьо
     *                   для отображения в сообщении об Ошибке.
     * @param errMsg
     * @throws WEditException  Ошибка загрузки
     * @return Дин параметры
     */
    private OpenParamsData loadParameters ( FileInputStream file, File f, StringBuilder errMsg )
             throws WEditException
    {
        OpenParamsData          result;
        UserParamsStaxParser    parser;

        result = new OpenParamsData();

        try
        {
            // загрузить файл
            parser  = new UserParamsStaxParser();
            parser.read ( file, errMsg );

            // Прочитать информацию об открытых Проекта, Книгах, Главах.
            result  = parser.getOpenInfo();

        } catch ( Exception e )         {
            Log.file.error ( "err", e );
            // Не ломаем поднятие Редактора из-за ошибок в user_params.xml
            //throw new WEditException ( e, "Ошибка загрузки файла динамических параметров '", f, "' :\n", e );
        }

        return result;
    }

    /**
     * Сохранить в файле все параметры пользователя.
     * @param file   Файл '.wedit6/user_params.xml'
     * @throws WEditException Ошибки сохранения.
     */
    public void saveParameters ( FileOutputStream file )
            throws WEditException
    {
        String   str;

        try
        {
            // Скинуть в поток xml-заголовoк
            XmlTools.saveLine ( file, "<?xml version='1.0' encoding='" + Par.CODE_CONFIG + "'?>" );

            XmlTools.startTag ( file, ConfigParam.PARAMS );
            XmlTools.saveChar ( file, '\n' );

            // сохранить инфу об открытых сборниках, книгах, частях.
            saveOpen ( file );

            // сохранить инфу об измененных параметрах функций категории Пользователь.
            saveFunctionParams ( file );


            XmlTools.endTag ( file, ConfigParam.PARAMS );
            XmlTools.saveChar ( file, '\n' );

        } catch ( Exception e )         {
            str = Convert.concatObj ( "Системная ошибка сохранения файла параметров пользователя '", ConfigParam.USER_PARAMS_FILE, "' :\n", e );
            Log.file.error ( str, e );
            throw new WEditException ( str, e );
        }
    }

    /**
     * Сохранить информацию об открытых проектах, книгах, главах, курсорах в главах.
     *
     * Связи корявые. Сделать по другому.
     *
     * @param file  Файл для записи
     * @throws svj.wedit.v6.exception.WEditException Ошибки записи в файл.
     */
    private void saveOpen ( FileOutputStream file )   throws WEditException
    {
        UserAllOpenParser                   parser;
        String                              str;
        Project                             project;
        TabsPanel<TreePanel<Project>>       tabsProjectsPanel;
        TabsPanel<TreePanel<BookContent>>   tabsBooksPanel;
        TabsPanel<TextPanel>                tabsTextsPanel;
        TextPanel                           textPanel;
        BookNode                            bookNode;
        TreePanel<Project>                  projectPanel;
        TreePanel<BookContent>              bookPanel;
        BookContent                         bookContent;

        Log.file.debug ( "Start" );

        try
        {
            XmlTools.startTag ( file, ConfigParam.OPEN, 1 );

            // ------------- Информация о размерах текущего фрейма --------------
            saveFrameInfo ( file, Par.GM.getFrame(), 2 );

            // ---------------- Динамические параметры функций --------------------
            //XmlTools.saveChar ( file, '\n' );
            XmlTools.startTag ( file, ConfigParam.PROJECTS, 2 );

            // Список открытых проектов и т.д.
            //XmlTools.saveWord ( file, text );
            // Собрать инфу обо всем открытом.
            parser  = new UserAllOpenParser ( file, 3 );   // перевести на SwingWorker - быстрее работать.
            ProjectTools.processTree ( parser );
            //text    = parser.getResult();

            XmlTools.endTag ( file, ConfigParam.PROJECTS, 2 );

            // ------------- Инфа об активном сборнике, книге, главе ---------------------

            tabsProjectsPanel   = Par.GM.getFrame().getProjectPanel().getCurrent();
            if ( tabsProjectsPanel != null )
            {
                XmlTools.startTag ( file, ConfigParam.ACTIVE, 2 );

                // Открытые компоненты
                projectPanel = tabsProjectsPanel.getSelectedComponent();
                if ( projectPanel != null )
                {
                    project      = projectPanel.getObject();
                    XmlTools.createTag ( file, ConfigParam.ACTIVE_PROJECT, project.getId(), 3 );
                    // Взять табс-панель данного Сборника
                    tabsBooksPanel  = Par.GM.getFrame().getBooksPanel().getTabsPanel ( project.getProjectDir().getAbsolutePath() );
                    if ( tabsBooksPanel != null )
                    {
                        bookPanel   = tabsBooksPanel.getSelectedComponent();
                        if ( bookPanel != null )
                        {
                            bookContent = bookPanel.getObject();
                            XmlTools.createTag ( file, ConfigParam.ACTIVE_BOOK, bookContent.getId(), 3 );

                            tabsTextsPanel  = Par.GM.getFrame().getTextsPanel().getTabsPanel ( bookContent.getId() );
                            if ( tabsTextsPanel != null )
                            {
                                textPanel = tabsTextsPanel.getSelectedComponent();
                                // null - вообще нет открытых панелей
                                if ( textPanel != null )
                                {
                                    bookNode   = textPanel.getBookNode();
                                    //XmlTools.createTag ( file, ConfigParam.ACTIVE_CHAPTER, bookNode.getId(), 3 ); ID нельзя т.к. у него суффикс (после имени главы) каждый раз генерится свой.
                                    XmlTools.createTag ( file, ConfigParam.ACTIVE_CHAPTER, bookNode.getFullPath(), 3 );
                                }
                            }
                        }
                    }
                }

                XmlTools.endTag ( file, ConfigParam.ACTIVE, 2 );
            }


            //XmlTools.saveWord ( file, "\n\n" );
            XmlTools.endTag ( file, ConfigParam.OPEN, 1 );
            XmlTools.saveWord ( file, "\n" );

        } catch ( Exception e )         {
            str = Convert.concatObj ( "Системная ошибка сохранения файла параметров пользователя '", ConfigParam.USER_PARAMS_FILE, "' :\n", e );
            Log.file.error ( str, e );
            throw new WEditException ( str, e );
        }
    }

    /*
    <open>
        <frame>
            <x>0</x>
            <y>38</y>
            <width>675</width>
            <height>851</height>
        </frame>
        <projects>
            <currentProject>2,1,14,</currentBook>    <!-- Индексы в деревьях. Текущие открываются в конце единым циклом. -->
            <project name="Сборник-2">
                <projectFile>/home/svj/projects/SVJ/WEdit-6/test/pr-02/project.xml</projectFile>
                <!--currentBook>2,1,14,</currentBook-->
                <book name="Сказка о мышонке">
                    <!--currentElement>11,2,20,</currentElement-->
                    <element name="11,2,20," cursorPlace="5412" />
                    <element name="1,2,0," cursorPlace="3317" />
                </book>
            </project>
        </projects>
    </open>
     */

    private void saveFrameInfo ( FileOutputStream file, JFrame frame, int level )  throws Exception
    {
        int ic;

        XmlTools.startTag ( file, "frame", level );

        ic  = level + 1;
        XmlTools.createTag ( file, "x", Integer.toString ( frame.getX() ), ic );
        XmlTools.createTag ( file, "y", Integer.toString ( frame.getY() ), ic );
        XmlTools.createTag ( file, "width", Integer.toString ( frame.getWidth() ), ic );
        XmlTools.createTag ( file, "height", Integer.toString ( frame.getHeight() ), ic );

        XmlTools.endTag ( file, "frame", level );
    }

    private void saveFunctionParams ( FileOutputStream file )  throws WEditException
    {
        Collection<svj.wedit.v6.function.params.FunctionParameter>   parameters;
        //Iterator                        itp;
        //FunctionParameter               param;
        String                          str;
        ParameterCategory   paramsType;
        Map<String, svj.wedit.v6.function.params.FunctionParameter>  params;

        try
        {
            XmlTools.startTag ( file, ConfigParam.FUNCTIONS, 1 );
            XmlTools.saveChar ( file, '\n' );

            // Перебор по всем функция
            for ( Function function : Par.GM.getFm().getFunctions().values() )
            {
                Log.file.debug ( "-- function = %s : %s", function.getName(), function.getId() );

                // Проверить тип параметров. Должен быть USER (или отсутствовать - по-умолчанию)
                paramsType  = function.getParamsType();
                Log.file.debug ( "---- paramsType = %s", paramsType );
                if ( (paramsType != null) && (paramsType != ParameterCategory.USER) ) continue;

                // Взять параметры заданного типа
                params      = function.getParams();
                if ( params == null )  continue;

                parameters  = params.values();
                // Выяснить размер - может и не надо ничего делать
                if ( parameters.size() == 0 ) continue;

                // Занести заголовок функции - <function name= >
                XmlTools.startTag ( file, ConfigParam.FUNCTION, ConfigParam.NAME, function.getId().toString(), 2 );
                XmlTools.saveChar ( file, '\n' );

                // Перебор по всем параметрам
                for ( svj.wedit.v6.function.params.FunctionParameter param : parameters )
                {
                    //param   = (FunctionParameter) itp.next();
                    Log.file.debug ( "---- param = ", param );
                    try
                    {
                        param.save ( file, 3 );
                    } catch ( Exception e )     {
                        // todo Не совсем правильное рещение - здесь ломается структура xml-файла, т.к. стартовые теги созданы, а конечные - будут пропущены - при этой ошибке.
                        // -- т.е. param.save должен корреткно обрабатывать ошибку.
                        Log.file.error ( "Param save error", e );
                        XmlTools.saveLine ( file, "<!-- Save Parameter error : " + e.toString() + " -->");
                    }

                    XmlTools.saveChar ( file, '\n' );
                }
                // Занести окончание функции - </function>
                XmlTools.endTag ( file, ConfigParam.FUNCTION, 2 );
                XmlTools.saveWord ( file, "\n\n" );
            }

            XmlTools.endTag ( file, ConfigParam.FUNCTIONS, 1 );
            XmlTools.saveWord ( file, "\n\n" );

        } catch ( Exception e )         {
            str = Convert.concatObj ( "Системная ошибка сохранения файла параметров пользователя '", ConfigParam.USER_PARAMS_FILE, "' :\n", e );
            Log.file.error ( str, e );
            throw new WEditException ( str, e );
        }
    }

}
