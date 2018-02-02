package svj.wedit.v6.obj.function;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.book.export.obj.*;
import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.function.params.ParameterCategory;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.WTreeObj;
import svj.wedit.v6.obj.WType;
import svj.wedit.v6.obj.book.*;
import svj.wedit.v6.obj.book.element.WBookElement;
import svj.wedit.v6.tools.BookTools;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.DialogTools;
import svj.wedit.v6.tools.Utils;

import java.awt.event.ActionEvent;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Общий механизм конвертации Произведений в различные форматы - RTF, DOC, HTML, FB2 и т.д.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.03.16 15:00
 */
public abstract class AbstractConvertFunction  extends FileWriteFunction
{
    /* Нумерация для элементов (для Глав - своя). */
    private final Map<Integer,Integer> numbers = new HashMap<Integer,Integer> ();

    /* Текстовый буфер для режима - поиск попаданий заданных символов, с сообщением в конце конвертации о местах попаданий. */
    private StringBuilder warnBuffer;

    /* Флаг - предыдущий текст был заголовком. Пустые строки не считаются.
       Только для режима отображения '* * *' вместо заголовка - чтобы сразу после одного заголовка не отображались бы звездочки. */
    private boolean previosIsTitle;

    protected BookContent                 bookContent;

    protected WEditStyle textFont, attributeFont;

    /** Режим - для выбранных Элементов (TRUE) или Для всей книги (False). */
    private final boolean multiSelect;

    /** Сборщик статистики по Титлам - каких (по уровням) и сколько раз было использовано. Для вывода в Итого. */
    private Map<Integer,Integer>  titleStat = new HashMap<Integer,Integer>();


    // ------------------------------------- abstract -----------------------------------------

    /** Взять локальные (индивидуальные) параметры конвертации. */
    protected abstract Collection<FunctionParameter> getOtherConvertParams ();

    protected abstract void processImage ( String imgFileName, ConvertParameter cp );

    // for toHTML - <BR/>
    protected abstract void processEmptyTitle ( ConvertParameter cp );

    protected abstract void processTitle ( String title, int level, ConvertParameter cp, BookNode nodeObject );

    protected abstract void processText ( TextObject textObj, ConvertParameter cp );

    protected abstract void initConvert ( ConvertParameter cp ) throws WEditException;

    protected abstract void finishConvert ( ConvertParameter cp ) throws WEditException;


    public AbstractConvertFunction ( FunctionId functionId, String functionName, String iconFile, boolean multiSelect )
    {
        setId ( functionId );
        setName ( functionName );
        setIconFileName ( iconFile );
        setParamsType ( ParameterCategory.BOOK );

        this.multiSelect = multiSelect;
    }

    // Переписывается при селекте
    protected TreeObj[] getNodesToConvert ( BookContent bookContent ) throws WEditException
    {
        TreeObj[] result;

        if ( isMultiSelect () )
        {
            result = BookTools.getSelectedNodesForCut ( true );
            if ( result == null )
            {
                throw new WEditException ( "Нет выбранных элементов книги." );
            }
        }
        else
        {
            // Выбираем книгу
            // т.к. книга - то это корневой узел.
            TreeObj treeObj;
            result      = new TreeObj[ 1 ];
            treeObj     = new TreeObj ();
            treeObj.setUserObject ( bookContent.getBookNode() );
            result[0]   = treeObj;
        }

        return result;
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        ConvertDialog                   dialog;
        BookmarksParameter              bookmarksParameter;
        TreePanel<BookContent>          currentBookContentPanel;
        Collection<WBookElement>        bookElementList;
        Map<String, WType>              bookTypes;
        TreeObj[]                       selectNodes;
        BookStructure                   bookStructure;
        Collection<FunctionParameter>   otherConvertParams;

        String PARAM_NAME  = "ConvertTo";

        Log.file.debug ( "Start" );

        numbers.clear();
        warnBuffer = new StringBuilder ( 64 );

        try
        {
            otherConvertParams  = getOtherConvertParams();

            // Взять параметр функции - из файла user_profile
            // - Состав: несколкьо параметров - Заголовки, Права доступа, Индивидуальное, Общее
            bookmarksParameter  = (BookmarksParameter) getParameterFromBook ( PARAM_NAME );
            Log.file.debug ( "--- bookmarksParameter 1 = \n%s", bookmarksParameter );
            if ( bookmarksParameter == null )
            {
                Log.l.error ( "--------- Не найден параметр по имени '%s' -----------.", PARAM_NAME );
                bookmarksParameter = new BookmarksParameter ( PARAM_NAME );
                bookmarksParameter.setHasEmpty ( false );
                //setParameter ( bookmarksParameter );
                setParameterToBook ( PARAM_NAME, bookmarksParameter );
                // занести локальные параметры - во все ConvertParameter
                // -- Лишнее, т.к. здесь нет еще ConvertParameter, и никуда они не занесутся.
                /*
                if ( otherConvertParams != null )
                {
                    Log.l.info ( "<M-05>" );
                    for ( FunctionParameter p : otherConvertParams )
                    {
                        for ( ConvertParameter cp : bookmarksParameter.getList() )
                        {
                            cp.addLocale ( p.clone() );
                        }
                    }
                }
                */
            }
            else if ( otherConvertParams != null )
            {
                Log.l.info ( "<M-10>" );
                // Посмотреть, есть ли упоминания о Locale парметрах. Если нет - прописать дефолтные.
                if ( ! bookmarksParameter.hasLocaleParams() )
                {
                    Log.l.info ( "<M-15>" );
                    for ( FunctionParameter p : otherConvertParams )
                    {
                        for ( ConvertParameter cp : bookmarksParameter.getList() )
                        {
                            cp.addLocale ( p.clone() );
                        }
                    }
                }
            }
            Log.file.info ( "--- bookmarksParameter 2 = \n%s", bookmarksParameter );

            // Взять описание книги
            // - Взять текущую книгу
            currentBookContentPanel = Par.GM.getFrame().getCurrentBookContentPanel();
            if ( currentBookContentPanel == null )
                throw new WEditException ( null, "Книга не выбрана." );

            bookContent     = currentBookContentPanel.getObject();
            bookStructure   = bookContent.getBookStructure ();

            // Скинуть все несохраненные тексты в обьекты книги.
            BookTools.text2node ( bookContent );

            // Создать RTF font для текста
            textFont        = bookStructure.getTextStyle();
            //textFont        = GuiTools.createRtfFont ( bookStructure.getTextStyle () );
            //textFont.setSize ( 8 );
            attributeFont   = bookStructure.getAnnotationStyle();
            //attributeFont   = GuiTools.createRtfFont ( bookStructure.getAnnotationStyle () );
            //attributeFont.setSize ( 8 );

            // Получить обьекты для конвертации (например, если не вся книга)
            selectNodes         = getNodesToConvert ( bookContent );


            // Получить элементы из структуры книги
            bookElementList = bookStructure.getBookElements();
            // Получить типы из структуры книги
            bookTypes       = bookStructure.getTypes ();

            // Мержим структуру книги с имеющимися данными (в закладках) - вдруг расхождения.
            //  - Если ДА - отмечаем, данные сбрасываем в дефолт.
            bookmarksParameter.merge ( bookElementList, bookTypes );
            //Log.file.debug ( "--- bookmarksParameter 2 after merge = \n%s", bookmarksParameter );

            // Создаем и открываем диалог настроек конвертации
            dialog = new ConvertDialog ( Par.GM.getFrame(), getName(), bookmarksParameter, bookStructure, otherConvertParams );
            dialog.init ( bookmarksParameter );
            dialog.showDialog();

            Log.file.debug ( "--- dialog.isOK() = %b", dialog.isOK() );
            if ( dialog.isOK() )
            {
                // валидируем данные (файл, его наличие, запрос на перезапись, и т.д.) -- в самом диалоге
                // - проверяем наличие файла - если такой уже существует - запрос на перезапись
                boolean b = dialog.checkFile();
                Log.file.debug ( "--- dialog.checkFile() = %b", b );
                if ( b )
                {
                    ConvertParameter cp;

                    Log.file.debug ( "--- convert '%s' to file.", bookContent.getName() );

                    cp = dialog.getCurrentBookmark();

                    // Создать выходной файл. Занести его в Функцию.
                    createOutputFile ( cp );

                    initConvert ( cp );

                    // Конвертим книгу согласно имеющимся настройкам
                    save ( cp, selectNodes );

                    finishConvert ( cp );

                    // Показать диалог об окончании работы
                    showTotalDialog ( bookStructure );

                }
                else
                {
                    throw new WEditException ( "Не задан файл." );
                }
            }

            // в любом случае сохраняем параметр, т.к. могло быть толкьо редактирование настроек, без итоговой конвертации книги.
            bookContent.setEdit ( true );  // иначе новое значение параметра не сохранится в файле книги.

        //} catch ( FileNotFoundException fe )        {
        //    Log.file.error ( fe, "err" );
        //    throw new WEditException ( fe, "Файл не найден." );
        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка преобразования : ", e );
        } finally   {
            closeConvert ();
        }

        Log.file.debug ( "Finish. bookmarksParameter = %s", bookmarksParameter );
    }

    private void closeConvert ()
    {
        Utils.close ( getFos() );
    }

    private void createOutputFile ( ConvertParameter cp ) throws WEditException
    {
        FileOutputStream fos;
        String fileName = null; // Взять из параметрво анстроек.

        Log.l.info ( "TXT: Start" );

        try
        {
            fileName    = cp.getFileName();
            fos         = new FileOutputStream ( fileName );
            setFos ( fos );

            Log.l.info ( "TXT: Create TXT file. fileName = %s", fileName );

        //} catch ( FileNotFoundException fe )        {
        //    Log.file.error ( "err", fe );
        //    throw new WEditException ( fe, "Файл не найден." );
        } catch ( Throwable e )       {
            Log.l.error ( "error. fileName = "+fileName, e );
            throw new WEditException ( e, "Ошибка создания результирующего файла (",fileName,") : \n", e.getMessage() );
        }
    }

    private void save ( ConvertParameter cp, TreeObj[] selectNodes ) throws WEditException
    {
        BookNode bookNode;
        String              fileName, str;

        Log.file.debug ( "Start. convert parameter = %s", cp );

        try
        {
            // Взять из параметра директорию, куда сохраняли последний раз.
            fileName    = cp.getFileName();
            if ( fileName == null )  fileName = Par.USER_HOME_DIR;

            Log.file.debug ( "--- convert to file '%s'.", fileName );

            // - Взять текущую книгу
            //bookContent = Par.GM.getFrame().getCurrentBookContentPanel().getObject();

            // Взять корневой узел книги
            bookNode    = bookContent.getBookNode();

            /// ------- for INIT
            /*
            // html head -- если флаг включен  -- Вынести в HtmlConvert
            //tornOnHeader   = false;
            tornOnHeader   = cp.getTornOffHtmlTitle().getValue();
            if ( tornOnHeader )
                writeStr ( fos, "<HTML>\n<HEAD>\n<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n<TITLE>"+bookNode.getName()+"</TITLE>\n</HEAD>\n\n<BODY>" );
            // Имя файла и дата - как коментарий
            writeStr ( fos, "\n<!--\nfile : " + fileName + "\ndate : " + new Date () + "\n-->\n" );
            */

            // Сформировать оглавление если задано -- пробегаем по всей книге, формируем оглавление в виде html, скидываем его в результирующий файл.
            // todo -- лучше не заморачиваться и Оглавление кидать всегда в конец.
            if ( cp.isCreateContent() )
                createContent ( bookNode, cp.getContentWidthParam().getValue(), getFos() );

            previosIsTitle = false;

            // Скинуть рекурсивно
            for ( TreeObj treeObj : selectNodes )
            {
                bookNode = (BookNode) treeObj.getWTreeObj();
                convertNode ( cp, bookNode, 0 );
            }

            // Заключительная строка
            str = cp.getEndTextParam().getValue ();
            if ( str != null )  writeStr ( str );

            // Заключительные html-теги - если разрешено.
            //if ( tornOnHeader )   writeStr ( fos, "\n\n<BR/>\n</BODY>\n</HTML>\n\n" );

            Log.file.debug ( "Finish. convert to file '%s'.", fileName );

        } catch ( WEditException we )        {
            Log.file.error ( "err", we );
            throw we;
        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка преобразования : ", e );
        }
    }

    /**
     * Последние пробелы НЕ скидывать в файл. Хранить - вдруг их придется удалять?
     *
     * @param cp                Сложный параметр, описывающий принятый набор параметров конвертации книги.
     * @param nodeObject        Узел (обьект) книги для ковертации.
     * @param level             Уровень вложенности анализа
     */
    private void convertNode ( ConvertParameter cp, BookNode nodeObject, int level )
            throws WEditException
    {
        BookNode                bo;
        String                  phase, str, elementType, title;
        Collection<TextObject>  text;
        Collection<WTreeObj>    childs;
        int                     nodeLevel;
        TitleViewMode           titleViewMode;
        ElementConvertParameter elementParam;
        TypeHandleType          handleType;

        phase       = "start";     // шаг процесса - для отладки
        try
        {
            nodeLevel   = nodeObject.getLevel();
            // Взять тип заголовка элемента - может быть NULL (т.е. work)
            elementType = nodeObject.getElementType();
            //if ( elementType == null )  elementType = ""; // work
            Log.file.debug ( "---- elementType = '%s'; book title = %s", elementType, nodeObject.getName() );

            elementParam    = cp.getElementParam(nodeLevel);
            if ( elementParam == null )  throw new WEditException ( null, "Не найден элемент описания уровня ", nodeLevel );

            // Проверить - игнорировать этот элемент?
            // if ( cp.ignoreElement(elementType) )  return;
            handleType = ConvertTools.getType ( elementType, cp.getTypes() );
            switch ( handleType )
            {
                case NOTHING:
                    return;
                case PRINT_LATER:
//                    writeStr ( fos, "<...>" );
                    return;
            }


            // ------------- Заголовок -------------
            phase       = "title";

            titleViewMode   = cp.getTitleViewType ( nodeLevel );
            Log.file.debug ( "------ title = %s; titleViewMode = '%s'", nodeObject.getName(), titleViewMode );

            switch ( titleViewMode )
            {
                default:
                case NOTHING:
                    // не выводить заголовок
                    title = null;
                    break;
                case TREE_STARS:
                    // три звездочки
                    // - Не выводить если перед этим также выводился заголовок, а из текста - только пустые строки.
                    // -- т.е. ситуация когда два заголовка подряд. Учесть что пустые строки между заголовками считаются выводом текста.
                    if ( previosIsTitle )
                        title = null;
                    else
                        title = "* * *";
                    break;
                case ONLY_NAME:
                    // выводить только название титла. Например: "Введение"
                    title   = nodeObject.getName();
                    break;
                case ONLY_TITLE_WITH_NUMBER:
                    // выводить только тип заголовка (например, глава) с нумерацией. Например: "Глава 1."
                    title   = elementParam.getName() + " " + getNumber(nodeLevel) + ".";
                    break;
                case TITLE_WITH_NUMBER_AND_NAME:
                    // выводить тип заголовка (например, глава) с нумерацией и с названием титла. Например: "Глава 1. Введение"
                    title   = elementParam.getName() + " " + getNumber(nodeLevel) + ". " + nodeObject.getName();
                    break;
                case TITLE_WO_NUMBER_AND_NAME:
                    // выводить тип заголовка (например, глава) без нумерации и с названием титла. Например: "Глава. Введение"
                    title   = elementParam.getName() + ". " + nodeObject.getName();
                    break;
                case NUMBER_AND_POINT_WITH_NAME:
                    // выводить номер заголовка с точкой и название титла. Например: "1. Введение"
                    title   = getNumber ( nodeLevel ) + ". " + nodeObject.getName();
                    break;
                case NUMBER_AND_BR_WITH_NAME:
                    // выводить номер заголовка со скобкой и название титла. Например: "1) Введение"
                    title   = getNumber ( nodeLevel ) + ") " + nodeObject.getName();
                    break;
            }

            if ( title != null )
            {
                // Заголовок отображается.
                // - Использовать стиль (При сборке оглавления - отличается)
                processTitle ( title, level, cp, nodeObject );
                previosIsTitle  = true;
                // - Собираем статистику по кол-ву заголовков согласно уровням
                addTitleStat ( level );
            }
            else
            {
                processEmptyTitle ( cp );
                previosIsTitle  = false;
            }
            //writeStr ( fos, title );
            Log.file.debug ( "------ save title = '%s'; previosIsTitle = %b", title, previosIsTitle );


            // ------------- Текст -------------
            phase   = "text";
            text    = nodeObject.getText();
            if ( ( text != null ) && ( ! text.isEmpty() ) )
            {
                Log.file.debug ( "------ write text lines = %d", text.size() );
                // По идее - до и перед оглавлением необходимо собирать пустые строки, а выводить столько пустых, сколько задано.  -- ???
                for ( TextObject textObj : text )
                {
                    if ( textObj instanceof ImgTextObject )
                    {
                        // ---------------- IMAGE -------------------
                        processImage ( textObj.getText(), cp ); // Имя файла
                        // Сбрасываем флаг
                        previosIsTitle = false;
                    }
                    else
                    {
                        checkTextForTitle ( textObj );
                        processText ( textObj, cp );
                        // проверка на наличие сигнальных символов, например ==.
                        checkText ( cp, textObj.getText(), nodeObject.getFullPathAsTitles() );
                        //writeStr ( fos, str );
                    }
                }
            }
            Log.file.debug ( "------ after text previosIsTitle = %b", previosIsTitle );

            // ------------- Дочерние элементы -------------
            phase   = "children";
            // Проверка на вложенные обьекты
            childs  = nodeObject.getChildrens();
            Log.file.debug ( "------ childs = '%s'", childs );
            if ( childs != null )
            {
                for ( WTreeObj obj : childs )
                {
                    bo  = ( BookNode ) obj;
                    convertNode ( cp, bo, level + 1 );
                }
            }

        } catch ( WEditException we )         {
            throw we;
        } catch ( Exception e )         {
            str = Convert.concatObj ( "Системная ошибка. nodeObject = '", nodeObject, "'. Phase = ", phase, "\n Error : \n", e );
            Log.file.error ( str, e );
            throw new WEditException ( e, str );
        }
    }

    private void addTitleStat ( int level )
    {
        Integer value;

        value = titleStat.get ( level );
        if ( value == null )  value = 0;

        value++;
        titleStat.put ( level, value );
    }

    private void checkTextForTitle ( TextObject textObj )
    {
        String  text;

        if ( textObj != null)
        {
            text     = textObj.getText();
            // Сбрасываем флаг только если был полноценный текст.
            if ( text != null )
            {
                text = text.trim();
                if ( ! text.isEmpty() )  previosIsTitle = false;
            }
        }
    }

    private void checkText ( ConvertParameter cp, String text, String episodeTitle )
    {
        String      str;
        String[]    st;

        if ( text == null || text.isEmpty() )  return;

        // Взять сигнальные строки
        str = cp.getWarnTextParam().getValue();

        if ( str != null )
        {
            // разбить на лексемы - через запятую.
            st = str.split ( "," );
            for ( String s : st )
            {
                s = s.trim();
                if ( text.contains(s) )
                {
                    warnBuffer.append ( "'" );
                    warnBuffer.append ( s );
                    warnBuffer.append ( "'\t" );
                    //warnBuffer.append ( "Текст " );
                    warnBuffer.append ( " - " );
                    warnBuffer.append ( episodeTitle );
                    warnBuffer.append ( "\n" );
                }
            }
        }
    }

    public String getNumber ( int nodeLevel )
    {
        String   result;
        Integer  i1;

        i1      = numbers.get ( nodeLevel );
        if ( i1 != null )
            i1 = i1 + 1;
        else
            i1 = 1;

        numbers.put ( nodeLevel, i1 );
        result  = i1.toString();
        return result;
    }

    private void showTotalDialog ( BookStructure bookStructure )
    {
        StringBuilder  responseBuffer;

        // Сообщение о завершении работы.
        responseBuffer = new StringBuilder ( 128 );   // текст результата завершения. сообщения о попаданиях текста.

        responseBuffer.append ( "Книга : '" );
        responseBuffer.append ( bookContent.getName() );
        responseBuffer.append ( "'" );

        responseBuffer.append ( "\nуспешно преобразована.\n" );

        // Статистика по титлам
        WBookElement bookElement;
        if ( ! titleStat.isEmpty() )
        {
            responseBuffer.append ( "\nКоличество:" );
            for ( Map.Entry<Integer,Integer> entry : titleStat.entrySet() )
            {
                // Уровень 0 игнорируем - это Книга.
                if ( entry.getKey() != 0 )
                {
                    responseBuffer.append ( "\n- " );
                    // Взять название согласно уровня
                    bookElement = bookStructure.getElement ( entry.getKey() );
                    if ( bookElement != null )
                    {
                        responseBuffer.append ( entry.getKey() );
                        responseBuffer.append ( ") " );
                        responseBuffer.append ( bookElement.getName() );
                        responseBuffer.append ( " -- " );
                        responseBuffer.append ( entry.getValue() );
                    }
                    else
                    {
                        responseBuffer.append ( "Неизвестный уровень: " );
                        responseBuffer.append ( entry.getKey() );
                    }
                }
            }
        }

        // Сообщение о Наличии в тексте сигнальных символов.
        if ( warnBuffer.length() > 0 )
        {
            responseBuffer.append ( "\n\nВнимание:\n" );
            responseBuffer.append ( warnBuffer );
        }

        DialogTools.showMessage ( "Преобразование", responseBuffer.toString() );
    }


    /**
     * todo Создать Оглавление. Если  режим в несколько столбцов - в виде таблицы.
     * <BR> Ссылки:
     * <BR> - В Оглавлении -- LI A HREF=#p001 text /A /LI
     * <BR> - В тексте     -- A NAME=p001 B text /B /A
     *
     * лучше не заморачиваться и кидать всегда в конец.  - взять из TreeNodeManager
     *
     * @param bookNode             Корневой узел.
     * @param contentWidth     Ширина Оглавления в столбцах таблицы.
     * @param fos
     */
    private void createContent ( BookNode bookNode, String contentWidth, FileOutputStream fos )
    {
        // Пройти всю книгу и скинуть заголовки в массив, вместе с параметрами - уровень (отступ), локальный линк (который потом добавится к даннйо главе)

        // Занести в выходной поток.
    }

    public BookContent getBookContent ()
    {
        return bookContent;
    }

    public boolean isMultiSelect ()
    {
        return multiSelect;
    }

}
