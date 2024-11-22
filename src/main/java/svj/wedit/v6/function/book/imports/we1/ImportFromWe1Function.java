package svj.wedit.v6.function.book.imports.we1;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.params.SimpleParameter;
import svj.wedit.v6.function.progressBar.ProgressBarFunction;
import svj.wedit.v6.function.project.edit.book.create.CreateBookFunction;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.tools.DialogTools;
import svj.wedit.v6.tools.FileTools;

import java.awt.event.ActionEvent;
import java.io.File;


/**
 * Конвертировать из формата we-1.
 * <BR/> Временная функция.
 * <BR/>
 * <BR/> Аттрибут Name
 * <BR/> - Перекодируем все символы " в '
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 03.04.2013 14:36:45
 */
public class ImportFromWe1Function extends ProgressBarFunction
{
    private File                file;
    private BookContent         bookContent;
    private String              codePage;


    public ImportFromWe1Function ()
    {
        setId ( FunctionId.IMPORT_FROM_WE1_BOOK );
        setName ( "Импортировать книгу из формата we-1." );
        //setMapKey ( "Ctrl/S" );
        setIconFileName ( "convert_we1.png" );
    }


    @Override
    public boolean beforeHandle () throws WEditException
    {
        CreateBookFunction  createBookFunction;
        boolean             result;
        SimpleParameter     sp;
        String              fileName, paramName;

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
        createBookFunction.handle ( null );
        bookContent         = createBookFunction.getBookContent();
        if ( bookContent == null ) return result; // Отмена создания новой книги.

        // Диалог выборки файла конвертации (книга в формате we-1)
        fileName    = sp.getValue();
        if ( fileName == null )  fileName = Par.USER_HOME_DIR;

        // Запросить имя сохраняемого файла
        file    = new File ( fileName );
        file    = FileTools.selectFileName ( Par.GM.getFrame(), file );
        Log.file.debug ( "Select WE-1 file = ", file );
        if ( file == null ) return result; // Не стоит сообщать что была отмена.

        sp.setValue ( file.getParentFile().toString() );

        // todo - Кодировка - В диалоге выборки файла. Название - берется из предыдущего функционала - создания новой книги.
        // - выборка кодировки файла.  -- берем из файла XML
        //codePage    = "UTF-8";
        codePage    = null;
        // - ввести название книги
        //bookTitle   = "Тест-книга";
        Log.file.debug ( "Select WE-1 file codePage = ", codePage, "; bookTitle = '", bookContent.getName(), "'." );

        //throw new WEditException ( null, "select file = ", file );
        return true;
    }

    @Override
    public void handleWithProgress ( ActionEvent event ) throws WEditException
    {
        We1BookStaxParser   we1Parser;

        // Берем текущее место в Сборнике и вставляем туда новую книгу


        // Используем свой парсер - получаем книгу - с применением указанной кодировки.
        // - параметры функций игнорируем
        we1Parser   = new We1BookStaxParser();
        we1Parser.read ( file, bookContent, codePage );
        bookContent.setEdit ( true );
        Log.file.debug ( "Load WE-1 file = ", bookContent );

        // Сохраняем новую книгу в новом файле. -- Обязательно!!! Т.к. потом открытие книги - загрузкой из файла.
        FileTools.saveBook ( bookContent );
    }

    @Override
    public void afterHandle ()
    {
        DialogTools.showMessage ( "Импорт из WE-1", "Файл успешно сконвертирован." );
    }


    @Override
    public String getToolTipText ()
    {
        return null;
    }

    
    @Override
    public void rewrite ()    {    }

    @Override
    public void init () throws WEditException     {    }

    @Override
    public void close ()      {    }

}
