package svj.wedit.v6.function.book.imports.txt;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.MessageException;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.book.imports.AImportBookFunction;
import svj.wedit.v6.function.params.SimpleParameter;
import svj.wedit.v6.function.project.edit.book.create.CreateBookFunction;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.gui.widget.ComboBoxWidget;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.DialogTools;
import svj.wedit.v6.tools.FileTools;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Импортировать книгу из формата ТХТ.
 * <BR/>
 * <BR/> todo НЕ доделана?
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 14.02.2014 13:43
 */
public class ImportBookFromTxtFunction extends AImportBookFunction //ProgressBarFunction
{
    private File        file;
    private BookContent bookContent;
    private String      codePage;
    private BookNode    currentNode;
    private String      lastText = null;
    private int         emptyLineCount = 0;

    private ComboBoxWidget<String> codePageWidget;


    public ImportBookFromTxtFunction ()
    {
        super ( new TextFileExtractor(), new ShowTitleTextHandler(), new TextContentHandler(), "TXT" );
        setId ( FunctionId.IMPORT_FROM_TXT );
        setName ( "Импортировать книгу из формата ТХТ." );
        //setMapKey ( "Ctrl/S" );
        setIconFileName ( "from_txt.png" );

        Collection<String> codeList = new ArrayList<String>();
        codeList.add ( "UTF-8" );
        codeList.add ( "CP1251" );
        codeList.add ( "KOI8-R" );
        codeList.add ( "KOI8-U" );
        codeList.add ( "DOS-861" );
        codeList.add ( "IBM866" );
        codeList.add ( "ISO-8859-5" );
        // по идее - дефолтную брать системную кодировку
        codePageWidget  = new ComboBoxWidget<String> ( "Кодировка", codeList );
        codePageWidget.setVerticalAligment();
        try
        {
            codePageWidget.setStartIndex ( 0 );
        } catch ( WEditException e )         {
        }
        setAdditionalGuiComponent ( codePageWidget );
    }

    public boolean beforeHandle () throws WEditException
    {
        CreateBookFunction  createBookFunction;
        boolean             result;
        SimpleParameter     sp;
        String              fileDir, paramName;
        ComboBoxWidget<String> codePageWidget;

        // Порядок работы:
        // 1) Валидация выбранного сборника, раздела
        // 2) Запрос файла
        // 3) Запрос имени книги
        // 4) Создание книги в Сборнике
        // 5) Импорт книги
        // 6) При ошибке импорта - удалять книгу?

        // 1) Валидация выбранного сборника, раздела
        checkProject();

        // Запросить имя исходного файла
        //fc.setAccessory ( panel );
        Collection<String> codeList = new ArrayList<String>();
        codeList.add ( "UTF-8" );
        codeList.add ( "CP1251" );
        codeList.add ( "KOI8-R" );
        codeList.add ( "KOI8-U" );
        codeList.add ( "DOS-861" );
        codeList.add ( "IBM866" );
        codeList.add ( "ISO-8859-5" );
        // по идее - дефолтную брать системную кодировку
        codePageWidget  = new ComboBoxWidget<String> ( "Кодировка", codeList );
        codePageWidget.setVerticalAligment();
        codePageWidget.setStartIndex ( 0 );

        // Параметр, запоминающий директорию импортируемой книги.
        file = getFileName ( codePageWidget );
        if ( file == null ) return false; // Не стоит сообщать что была отмена.

        /*
        // Параметр, запоминающий директорию импортируемой книги.
        paramName = "fileDir";

        sp  = (SimpleParameter) getParameter ( paramName );
        if ( sp == null )
        {
            sp  = new SimpleParameter ( paramName, null ); // дефолтное значение
            sp.setHasEmpty ( false );
            setParameter ( paramName, sp );
        }

        result = false; // не продолжать работу

        // Создаем в Сборнике новый файл, меняет параметр файла в новой книге, сохраняем новую книгу в новом файле.
        // Сначала создать нвоую, пустую книгу
        // - Или взять из менеджера функций
        createBookFunction  = new CreateBookFunction();
        // -- Диалог - Запросить имя новой книги
        createBookFunction.handle ( null );
        bookContent         = createBookFunction.getBookContent();
        if ( bookContent == null ) return result; // Отмена создания новой книги.
        // Диалог выборки файла конвертации (книга в формате TXT)

        fileDir    = sp.getValue();
        if ( fileDir == null )  fileDir = Par.USER_HOME_DIR;

        file    = new File ( fileDir );
        file    = FileTools.selectFileName ( Par.GM.getFrame(), file, codePageWidget );
        Log.file.debug ( "Select TXT file = %s", file );
        if ( file == null ) return result; // Не стоит сообщать что была отмена.

        sp.setValue ( file.getParentFile().toString() );
        */

        // todo - Кодировка - В диалоге выборки файла. Название - берется из предыдущего функционала - создания новой книги.
        // - выборка кодировки файла.  -- берем из файла XML
        //codePage    = "UTF-8";
        codePage    = codePageWidget.getValue();
        // - ввести название книги
        //bookTitle   = "Тест-книга";

        bookContent = createNewBookContent();
        currentNode = bookContent.getBookNode ();

        Log.file.debug ( "Select TXT file codePage = %s; bookTitle = '%s'.", codePage, bookContent.getName() );

        //throw new WEditException ( null, "select file = ", file );
        return true;
    }

    /**
     * В цикле построчно читаем файл.
     * Одиночные (пустые строки сверху и снизу) короткие строки принимаем за названия глав.
     * @param event
     * @throws WEditException
     */
    public void handleWithProgress ( ActionEvent event ) throws WEditException
    {
        BufferedReader      reader;
        InputStreamReader   iReader;
        String              str;
        boolean             b;

        try
        {
            iReader     = new InputStreamReader ( new FileInputStream(file), codePage );
            reader      = new BufferedReader ( iReader );

            //sb = new StringBuilder ( 1024 );
            while ( reader.ready() )
            {
                str = reader.readLine();
                // todo Надо парсить строки, выискивая одиночную строку с пустыми строками сверху и снизу и считать это заголовком.
                //Log.file.debug ( str );
                if ( str.isEmpty() )
                {
                    // Пустая строка - возможно - lastText - это заголовок.
                    b = processTitle ( null );
                    if ( ! b )
                    {
                        // Это не заголовок
                        emptyLineCount++;
                        //currentText = null;
                        addText( lastText+"\n");
                    }
                    addText("\n");
                    lastText = "\n";
                }
                else
                {
                    addText( lastText+"\n");
                    lastText = str;
                    /*
                    // какой-то текст
                    if ( emptyLineCount > 0 )
                    {
                        // это заголовок - берем текст
                        if ( isTitle(str) )
                        {
                            //titleList.add ( new ShowTitleObj (currentText) );
                            // Создать новый элемент книги.
                            //currentNode = bookContent.createNextBookNode ( str, 1 );
                            currentNode = new BookNode ( str, bookContent.getBookNode() );
                            bookContent.getBookNode().addBookNode ( currentNode );
                            Log.l.debug ( "--- DOC import creator: processTitle: new currentNode = %s", currentNode );
                            // добавить EOL
                            addText("\n");
                        }
                        else
                        {
                            // текст
                            addText ( str+"\n" );
                            //addText ( str );
                        }
                        clearEmptyCounter();
                        //addText("\n");
                    }
                    else
                    {
                        // это простой текст - заносим в текущий элемент книги.
                        //clearEmptyCounter();
                        addText ( str+"\n" );
                    }
                    */
                }
                //sb.append ( str );
                //sb.append ( '\n' );
            }

            // Сохранить новую книгу в файле.
            FileTools.saveBook ( bookContent );

        } catch ( Exception e )        {
            Log.file.debug ( Convert.concatObj ( "Ошибка импорта книги '",file,"' из формата ТХТ. " ), e );
            throw new WEditException ( e, "Ошибка импорта книги '",file,"' из формата ТХТ :\n", e );
        }
    }

    private boolean processTitle ( String text )
    {
        boolean result;

        result = false;

        if ( emptyLineCount > 0 )
        {
            // это заголовок - берем текст
            if ( isTitle(text) )
            {
                //titleList.add ( new ShowTitleObj (currentText) );
                // Создать новый элемент книги.
                //currentNode = bookContent.createNextBookNode ( str, 1 );
                currentNode = new BookNode ( text, bookContent.getBookNode() );
                bookContent.getBookNode().addBookNode ( currentNode );
                Log.l.debug ( "--- DOC import creator: processTitle: new currentNode = %s", currentNode );
                // добавить EOL
                //addText("\n");
                clearEmptyCounter();
                result = true;
            }
        }

        return result;
    }

    private boolean isTitle ( String text )
    {
        boolean result;

        result = false;
        if ( (text != null) && (text.length() < 60 ) )
        {
            // не содержит подчеркиваний
            if ( ! text.contains ( "-----" ) )
            {
                if ( ! text.contains ( "=====" ) )
                {
                    if ( ! text.contains ( "____" ) )
                    {
                        if ( ! text.contains ( "#####" ) )  result = true;
                    }
                }
            }
        }
        return result;
    }

    private void checkProject () throws WEditException
    {
        TreePanel<Project> currentProjectPanel;
        TreeObj selectNode;

        // Взять текущий проект - TreePanel
        currentProjectPanel = Par.GM.getFrame().getCurrentProjectPanel();
        if ( currentProjectPanel == null )  throw new MessageException ( "Отсутствует текущий Сборник." );

        // Взять в дереве сборника выбранный Раздел
        selectNode  = currentProjectPanel.getCurrentObj();
        if ( selectNode == null )  throw new MessageException ( "Раздел Сборника, в котором будет \nсоздана новая книга, не выбран." );
    }

    private BookContent createNewBookContent ()  throws WEditException
    {
        BookContent         bookContent;
        CreateBookFunction  createBookFunction;

        createBookFunction  = new CreateBookFunction();
        // -- Диалог - Запросить имя новой книги -- Создать книгу.
        createBookFunction.handle ( null );
        bookContent         = createBookFunction.getBookContent();
        return bookContent;
    }

    private File getFileName ( JComponent additionalGuiComp )
    {
        File                file;
        SimpleParameter     sp;
        String              fileDir, paramName;

        paramName = "fileDir";

        sp  = (SimpleParameter) getParameter ( paramName );
        if ( sp == null )
        {
            sp  = new SimpleParameter ( paramName, null ); // дефолтное значение
            sp.setHasEmpty ( false );
            setParameter ( paramName, sp );
        }

        fileDir    = sp.getValue ();
        if ( fileDir == null )  fileDir = Par.USER_HOME_DIR;

        file    = new File ( fileDir );
        file    = FileTools.selectFileName ( Par.GM.getFrame(), file, additionalGuiComp );
        Log.file.debug ( "Select file = %s", file );

        if ( file != null )
        {
            sp.setValue ( file.getParentFile().toString() );
        }

        return file;
    }

    private void addText ( String text )
    {
        currentNode.addText ( text, null );
        //Log.l.debug ( "--- --- DOC import creator: text = %s", text );
    }

    private void clearEmptyCounter()
    {
        emptyLineCount = 0;
    }

    public void afterHandle ()
    {
        DialogTools.showMessage ( "Импорт книги из формата ТХТ", "Файл '"+file+"'\nуспешно сконвертирован." );
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

}
