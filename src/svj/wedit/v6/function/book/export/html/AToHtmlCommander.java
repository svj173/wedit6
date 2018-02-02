package svj.wedit.v6.function.book.export.html;


import svj.wedit.v6.Par;
import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.book.export.obj.*;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.WTreeObj;
import svj.wedit.v6.obj.WType;
import svj.wedit.v6.obj.book.*;
import svj.wedit.v6.obj.book.element.WBookElement;
import svj.wedit.v6.obj.function.FileWriteFunction;
import svj.wedit.v6.tools.*;
import svj.wedit.v6.tree.TreeNodeManager;
import svj.wedit.v6.tree.TreeNodeProcessor;

import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;

/**
 * Абстракция конверетера в HTML - Общие методы.   -- OLD. Потмо удалить.
 * <BR/>
 * <BR/> todo Перевести на общий механизм AbstractConvertFunction - как extends
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 08.12.2016 14:07
 */
public abstract class AToHtmlCommander  extends FileWriteFunction implements TreeNodeProcessor
{
    private final String   BR    = "<br/>";

    /* Нумерация для элементов (для Глав - своя). */
    private final Map<Integer,Integer>    numbers = new HashMap<Integer,Integer>();

    /* Текстовый буфер для режима - поиск попаданий заданных символов, с сообщением в конце конвертации о местах попаданий. */
    private StringBuilder warnBuffer;

    /* Флаг - предыдущий текст был заголовком. Пустые строки не считаются.
       Только для режима отображения '* * *' вместо заголовка - чтобы сразу после одного заголовка не отображались бы звездочки. */
    private boolean previosIsTitle;

    // Обьекты для формирования оглавления.
    private FileOutputStream    fos     = null;
    private List<String>        titles  = new ArrayList<String> ();


    protected abstract BookNode[] getSelectedNodes ( BookNode bookNode )   throws WEditException;


    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        BookContent                 bookContent;
        StringBuilder               sb;
        ConvertDialog               dialog;
        BookStructure               bookStructure;
        BookmarksParameter          bookmarksParameter;
        TreePanel<BookContent>      currentBookContentPanel;
        Collection<WBookElement>    bookElementList;
        Map<String, WType>          bookTypes;

        String PARAM_NAME  = "ConvertToHtml";

        Log.file.debug ( "Start" );

        sb          = new StringBuilder ( 128 );   // текст результата завершения.
        warnBuffer  = new StringBuilder();         // сообщения о попаданиях текста.

        numbers.clear ();

        try
        {
            // Взять параметр функции - из файла user_profile
            //bookmarksParameter  = (BookmarksParameter) getParameter ( PARAM_NAME );
            bookmarksParameter  = (BookmarksParameter) getParameterFromBook ( PARAM_NAME );
            Log.file.debug ( "--- bookmarksParameter 1 = \n%s", bookmarksParameter );
            if ( bookmarksParameter == null )
            {
                Log.l.error ( "--------- Не найден параметр по имени '%s' -----------.", PARAM_NAME );
                bookmarksParameter = new BookmarksParameter ( PARAM_NAME );
                bookmarksParameter.setHasEmpty ( false );
                //setParameter ( bookmarksParameter );
                setParameterToBook ( PARAM_NAME, bookmarksParameter );
            }
            Log.file.debug ( "--- bookmarksParameter 2 = \n%s", bookmarksParameter );

            // Взять описание книги
            // - Взять текущую книгу
            currentBookContentPanel = Par.GM.getFrame().getCurrentBookContentPanel();
            if ( currentBookContentPanel == null )
                throw new WEditException ( null, "Книга не выбрана." );

            bookContent     = currentBookContentPanel.getObject();
            bookStructure   = bookContent.getBookStructure();

            // Получить элементы из структуры книги
            bookElementList = bookStructure.getBookElements();
            Log.file.debug ( "--- bookElementList = \n%s", DumpTools.printCollection ( bookElementList ) );
            // Получить типы из структуры книги
            bookTypes       = bookStructure.getTypes ();
            Log.file.debug ( "--- bookTypes = \n%s", DumpTools.printMap ( bookTypes, WCons.NEW_LINE ) );

            // Мержим структуру книги с имеющимися данными (в закладках) - вдруг расхождения. Если ДА - отмечаем, данные сбрасываем в дефолт.
            bookmarksParameter.merge ( bookElementList, bookTypes );
            Log.file.debug ( "--- bookmarksParameter 2 after merge = \n%s", bookmarksParameter );

            // Создаем и открываем диалог
            dialog = new ConvertDialog ( Par.GM.getFrame(), getName(), bookmarksParameter, bookElementList, bookTypes );
            dialog.init ( bookmarksParameter );
            dialog.showDialog();

            Log.file.debug ( "--- dialog.isOK() = %b", dialog.isOK() );
            if ( dialog.isOK() )
            {
                // валидируем данные (файл, его наличие, запрос на перезапись, и т.д.) -- в самом диалоге
                // - проверяем наличие файла - если такой уже существует - запрос на перезапись
                //Log.file.debug ( "--- dialog.checkFile() = %b", dialog.checkFile() );
                if ( dialog.checkFile() )
                {
                    Log.file.debug ( "--- convert '%s' to file.", bookContent.getName()  );
                    // Конвертим книгу согласно имеющимся данным
                    saveHtml ( dialog.getCurrentBookmark() );

                    // Сообщение о завершении работы.
                    sb.append ( "Книга : " );
                    sb.append ( bookContent.getName() );
                    sb.append ( "\nуспешно преобразована.\n" );
                    if ( warnBuffer.length() > 0 )
                    {
                        sb.append ( "\nВнимание:\n\n" );
                        sb.append ( warnBuffer );
                    }
                    DialogTools.showMessage ( "Преобразование", sb.toString () );
                }
            }

            // в любом случае сохраняем параметр, т.к. могло быть толкьо редактирование настроек, без итоговой конвертации книги.
            bookContent.setEdit ( true );  // иначе новое значение параметра не сохранится в файле книги.

        //} catch ( FileNotFoundException fe )        {
        //    Log.file.error ( fe, "err" );
        //    throw new WEditException ( fe, "Файл не найден." );
        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка преобразования в HTML : ", e );
        }

        Log.file.debug ( "Finish. bookmarksParameter = %s", bookmarksParameter );
    }

    private void saveHtml ( ConvertParameter cp ) throws WEditException
    {
        BookContent         bookContent;
        BookNode            bookNode;
        String              fileName, str;
        boolean             tornOnHeader;
        BookNode[]          selectNodes;

        Log.file.debug ( "Start. convert parameter = %s", cp );
        fos = null;

        try
        {
            // Взять из параметра директорию, куда сохраняли последний раз.
            fileName    = cp.getFileName();
            if ( fileName == null )  fileName = Par.USER_HOME_DIR;

            Log.file.debug ( "--- convert to file '%s'.", fileName );

            fos         = new FileOutputStream ( fileName );

            // - Взять текущую книгу
            bookContent = Par.GM.getFrame().getCurrentBookContentPanel().getObject();

            // Взять корневой узел книги
            bookNode    = bookContent.getBookNode();
        /*
            // html head -- если флаг включен
            //tornOnHeader   = false;
            tornOnHeader   = cp.getTornOffHtmlTitle().getValue();
            if ( tornOnHeader )
                writeStr ( fos, "<HTML>\n<HEAD>\n<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n<TITLE>"+bookNode.getName()+"</TITLE>\n</HEAD>\n\n<BODY>" );

            // Имя файла и дата - как коментарий
            writeStr ( fos, "\n<!--\nfile : "+fileName+"\ndate : " + new Date() + "\n-->\n" );
        */
            previosIsTitle = false;

            // Взять выделенный элемент - но только верхнего уровня.
            // Там же, в глубине, проверяется отмеченность одноуровневых элементов. Без проверки на корень.
            selectNodes  = getSelectedNodes ( bookNode );

            // Сформировать оглавление если задано -- пробегаем по всей книге, формируем оглавление в виде html, скидываем его в результирующий файл.
            if ( cp.isCreateContent() )
            {
                //createContent ( selectNodes, cp.getContentWidthParam().getValue(), fos );       // selected
                //createContent ( bookNode, ConvertParameter cp, FileOutputStream fos ) // book
                createContent ( selectNodes, cp, fos );
            }

            // Скинуть рекурсивно
            for ( BookNode node : selectNodes )
            {
                //node2html ( cp, ( BookNode ) node.getUserObject(), fos, 0 );  // selected
                node2html ( cp, node, fos, 0 );   // book
            }

            // Скинуть рекурсивно
            //node2html ( cp, bookNode, fos, 0 );

            // Заключительная строка
            str = cp.getEndTextParam().getValue();
            if ( str != null )  writeStr ( fos, str );

            // Заключительные html-теги - если разрешено.
        //    if ( tornOnHeader )   writeStr ( fos, "\n\n<BR/>\n</BODY>\n</HTML>\n\n" );

            Log.file.debug ( "Finish. convert to file '%s'.", fileName );

        } catch ( WEditException we )        {
            Log.file.error ( "err", we );
            throw we;
        } catch ( FileNotFoundException fe )        {
            Log.file.error ( "err", fe );
            throw new WEditException ( fe, "Файл не найден." );
        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка преобразования : ", e );
        } finally {
            Utils.close ( fos );
        }
    }

    /**
     * Последние пробелы НЕ скидывать в файл. Хранить - вдруг их придется удалять?
     *
     * @param cp                Сложный параметр, описывающий принятый набор параметров конвертации книги.
     * @param nodeObject        Узел (обьект) книги
     * @param fos               Результирующий файл
     * @param level             Уровень вложенности анализа
     */
    private void node2html ( ConvertParameter cp, BookNode nodeObject, FileOutputStream fos, int level )
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

            elementParam    = cp.getElementParam ( nodeLevel );
            Log.file.debug ( "---- nodeLevel = %d; elementParam = '%s'", nodeLevel, elementParam );
            if ( elementParam == null )  throw new WEditException ( null, "Не найден элемент описания для уровня '", nodeLevel, "'." );

            // Проверить - игнорировать этот элемент?
            // if ( cp.ignoreElement(elementType) )  return;
            handleType = ConvertTools.getType ( elementType, cp.getTypes () );
            switch ( handleType )
            {
                case NOTHING:
                    return;
                case PRINT_LATER:
                    writeStr ( fos, "<...>" );
                    return;
            }


            // ------------- Заголовок -------------
            phase       = "title";

            titleViewMode   = cp.getTitleViewType ( nodeLevel );
            Log.file.debug ( "------ title = %s; titleViewMode = '%s'", nodeObject.getName(), titleViewMode );
            if ( titleViewMode == null )  titleViewMode = TitleViewMode.NOTHING;

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
                // -- Использовать стиль (При сборке оглавления - отличается)
                title           = createHtmlTitle ( title, level, cp );
                previosIsTitle  = true;
            }
            else
            {
                title = BR;
                previosIsTitle  = false;
            }
            writeStr ( fos, title );
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
                        // Скопировать картинку в директорию расположения html-файла
                        FileTools.copyFileToDir ( textObj.getText (), cp.getFileName () );
                        // Разместить в тексте тег ссылки на картинку (по идее в обьекте может быть и подпись для картинки).
                        writeStr ( fos, "<center><IMG src='"+ textObj.getText()+ "' /></center>\n" );
                        // Иначе текст следующего заголовка пойдет прямо от иконки.
                        writeStr ( fos, BR );
                        // Сбрасываем флаг
                        previosIsTitle = false;
                    }
                    else
                    {
                        checkTextForTitle ( textObj );
                        str             = createHtmlText ( textObj, cp );
                        // проверка на наличие сигнальных символов, например ==.
                        checkText ( cp, str, nodeObject.getFullPathAsTitles() );
                        writeStr ( fos, str );
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
                    node2html ( cp, bo, fos, level + 1 );
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

    /**
     * Создать Оглавление. Если  режим в несколько столбцов - в виде таблицы.
     * <BR> Ссылки:
     * <BR> - В Оглавлении -- LI A HREF=#p001 text /A /LI
     * <BR> - В тексте     -- A NAME=p001 B text /B /A
     *  @param bookNode             Корневой узел.
     * @param cp     Параметр конвертации, содержит все атрибуты для конвертации - задаются в диалоге..
     * @param fos              Результирующиий файл-поток, куда скидывать данные.
     */
    private void createContent ( BookNode[] bookNode, ConvertParameter cp, FileOutputStream fos )   throws WEditException
    {
        TreeNodeManager manager;

        // Создать менеджер
        manager = new TreeNodeManager ( bookNode, cp, "content", this );

        // Пройти всю книгу и скинуть заголовки в массив, вместе с параметрами - уровень (отступ), локальный линк (который потом добавится к даннйо главе)
        manager.handle();

        // Занести в выходной поток.
    }

    /**
     * Здесь мы имеем полный массив заголовков и теперь можем распределить их по колонкам.
     * @param cp
     */
    @Override
    public void finished ( ConvertParameter cp )  throws WEditException
    {
        String str, t2;
        int ic, contentWidth, number;

        // Получить кол-во колонок для оглавления
        contentWidth = Convert.getInt ( cp.getContentWidthParam ().getValue (), 1 );

        Log.file.info ( "titles (%d) = %s", titles.size (), DumpTools.printCollection ( titles ) );
        Log.file.info ( "contentWidth = %d", contentWidth );
        try
        {
            // разбиваем на колонки

            // - выясняем сколько заголовков пойдет в одну колонку - это и есть смещение +1
            ic  = titles.size() / contentWidth;
            Log.file.info ( "ic = %d", ic );

            str = "<center><b>Содержание</b><br/>\n<TABLE border=1>\n";
            fos.write ( str.getBytes ( Par.CODE_BOOK ) );

            /* цикл по заголовкам
            ic = 0;  // счетчик колонок
            for ( String title : titles )
            {
                if ( ic >= contentWidth )
                {
                    // новая строка
                    ic = 0;
                    str = "<TR>\n";
                    fos.write ( str.getBytes ( Par.CODE_BOOK ) );
                }

                if
            }
            */

            //*
            // цикл по строкам таблицы - колонкам
            ic++;   // чтобы захватить последниюю строку оглавления
            number = 0;
            for ( int i=0; i<ic; i++ )
            {
                //
                str = "<TR>\n";
                fos.write ( str.getBytes ( Par.CODE_BOOK ) );
                for ( int i2=0; i2<contentWidth; i2++ )
                {
                    // берем заголовок
                    //number  = i * (contentWidth - 1) + i2;
                    //number = i + (ic + 1) * i2;
                    number = i + ic * i2;
                    Log.file.info ( "-- i = %d; i2 = %d; number = %d", i, i2, number );
                    if ( number < titles.size() )
                        t2 = titles.get ( number );
                    else
                        t2 = WCons.SP;
                    str = "<TD>"+t2+"</TD>\n";
                    fos.write ( str.getBytes ( Par.CODE_BOOK ) );
                    //number++;
                }
                str = "</TR>\n";
                fos.write ( str.getBytes ( Par.CODE_BOOK ) );
            }
            //*/

            str = "</TABLE></center>\n";
            fos.write ( str.getBytes ( Par.CODE_BOOK ) );

        } catch ( Exception e )        {
            Log.file.error ( "error", e );
            throw new WEditException ( e, "Ошибка: ", e.getMessage() );
        }

        //Log.l.info ( "-- contentWidth = %s; titles = \n%s", contentWidth, DumpTools.printCollection ( titles ) );
    }

    @Override
    public void title ( TitleViewMode titleViewMode, String title, String elementName, String number, int level, ConvertParameter cp )
    {
        if ( level == 0 )  return;  //игнорируем заголовок книги

        // Правильное формирование заголвока - только титл, только номер... + Смещение слева.
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
                title   = title;
                break;
            case ONLY_TITLE_WITH_NUMBER:
                // выводить только тип заголовка (например, глава) с нумерацией. Например: "Глава 1."
                title   = elementName + " " + number + ".";
                break;
            case TITLE_WITH_NUMBER_AND_NAME:
                // выводить тип заголовка (например, глава) с нумерацией и с названием титла. Например: "Глава 1. Введение"
                title   = elementName + " " + number + ". " + title;
                break;
            case TITLE_WO_NUMBER_AND_NAME:
                // выводить тип заголовка (например, глава) без нумерации и с названием титла. Например: "Глава. Введение"
                title   = elementName + ". " + title;
                break;
            case NUMBER_AND_POINT_WITH_NAME:
                // выводить номер заголовка с точкой и название титла. Например: "1. Введение"
                title   = number + ". " + title;
                break;
            case NUMBER_AND_BR_WITH_NAME:
                // выводить номер заголовка со скобкой и название титла. Например: "1) Введение"
                title   = number + ") " + title;
                break;
        }

        if ( title != null )
        {
            // -- Использовать стиль (При сборке оглавления - отличается)
            //title           = createHtmlTitle ( title, level, cp );
            String s = "";
            for ( int i=0; i<level; i++)  s = s + WCons.HTML_SP;
            //previosIsTitle  = true;
            titles.add ( s + title );
        }

    }

    @Override
    public void text ( Collection<TextObject> text )
    {
        // игнорируем для сбора оглавления
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
                text = text.trim ();
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

    /**
     * Создать html заголовок.
     *
     * Преобразование обьекта Font в html.style:
     * тег < font
     * - color=
     * - size=
     * - имя_шрифта=
     *
     * @param str   Текст заголовка.
     * @param cp    Закладка (структура параметров), отвечающая за текущие параметры преобразования.
     * @return      Заголовок в виде html текста.
     */
    private String createHtmlTitle ( String str, int level, ConvertParameter cp )
    {
        StringBuilder result;

        result  = new StringBuilder (64);
        result.append ( "<CENTER><H" ).append ( level + 2 ).append ( ">" );
        result.append ( str );
        result.append ( "</H" ).append ( level + 2 ).append ( "></CENTER>\n" );

        return result.toString();
    }

    // Применять стиль  - не нужно. Сильно загромоздит файл. Для текста - применяем стиль по умолчанию. -- Сделать потом.
    private String createHtmlText ( TextObject textObj, ConvertParameter cp )
    {
        StringBuilder result;
        String        text;
        AttributeSet style;
        float         f1;
        int           ic;

        result  = new StringBuilder (256);
        if ( textObj != null)
        {
            if ( textObj instanceof EolTextObject )
            {
                // Перенос строки
                result.append ( BR );
                //result.append ( "\n&nbsp;&nbsp;&nbsp;" );
            }
            else
            {
                style    = textObj.getStyle();
                Log.file.info ( "ToHTML: style = %s", style );
                text     = textObj.getText();
                if ( text == null )
                {
                    //Log.file.error ( null, "Text is NULL. nodeObject = ", nodeObject, "; text = ", text, "; nodeObject = ",
                    //                 nodeObject, "; nodeLevel = ", nodeLevel, "; elementType = ", elementType );
                    text = " ";    // именно пробел, т.к. почему-то именно пробелы пропадают в null.
                }

                if ( textObj instanceof SlnTextObject )
                {
                    // - красная строка
                    result.append ( "\n" );
                    if ( style == null )
                    {
                        // Взять стиль текста
                        style = Par.GM.getFrame().getCurrentBookContentPanel().getObject().getBookStructure().getTextStyle ();
                    } // иначе это color_text
                    ic = createFirstLineIndent ( style );
                    Log.file.info ( "ToHTML: style 2 = %s; firstLine = %d", style, ic );
                //    result.append ( cp.getRedLineParam().getValue() );
                    if ( StyleConstants.isUnderline ( style ) ) result.append ( "<U>" );
                    if ( StyleConstants.isItalic ( style ) ) result.append ( "<I>" );
                    if ( StyleConstants.isBold ( style ) ) result.append ( "<B>" );
                    // - текст
                    result.append ( text );
                    if ( StyleConstants.isBold ( style ) ) result.append ( "</B>" );
                    if ( StyleConstants.isItalic ( style ) ) result.append ( "</I>" );
                    if ( StyleConstants.isUnderline ( style ) ) result.append ( "</U>" );
                    result.append ( BR );
                }
                else
                {
                    result.append ( text );
                }
            }
        }
        return result.toString();
    }

    private int createFirstLineIndent ( AttributeSet style )
    {
        float         f1;
        int           result, ic, size;

        result = 0;
        if ( style != null )
        {
            // - красная стркоа в пикселях.
            f1   = StyleConstants.getFirstLineIndent ( style );
            size = StyleConstants.getFontSize ( style );
            Log.file.info ( "ToHTML: firstLine = %s; size = %d", f1, size );
            ic = (int) (f1 / size);
            if ( ic == 0 )  ic = 1;
            result = ic * 2;
        }
        return result;
    }

    @Override
    public void rewrite ()
    {
    }


}
