package svj.wedit.v6.function.book.imports;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.MessageException;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.book.imports.doc.target.IBookContentCreator;
import svj.wedit.v6.function.book.imports.doc.target.ShowTitleObj;
import svj.wedit.v6.function.params.SimpleParameter;
import svj.wedit.v6.function.project.edit.book.create.CreateBookFunction;
import svj.wedit.v6.gui.dialog.WidgetsDialog;
import svj.wedit.v6.gui.renderer.INameNumberRenderer;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.gui.widget.AbstractWidget;
import svj.wedit.v6.gui.widget.ComboBoxWidget;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.element.WBookElement;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Общий функционал импортеров книг.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.12.2014 11:37
 */
public abstract class AImportBookFunction extends Function
{
    private IFileExtractor      fileExtractor;
    private IBookContentCreator showTitleHandler;
    private IBookContentCreator createFileHandler;
    private String fileFormat;  // DOC, TXT...
    /* Дополнительный гуи-компонет в выборку файла. Для ТХТ - задает кодировку текста.*/
    private JComponent additionalGuiComponent   = null;


    protected AImportBookFunction ( IFileExtractor fileExtractor, IBookContentCreator showTitleHandler,
                                    IBookContentCreator createFileHandler, String fileFormat )
    {
        this.fileExtractor      = fileExtractor;
        this.showTitleHandler   = showTitleHandler;
        this.createFileHandler  = createFileHandler;
        this.fileFormat         = fileFormat;
    }

    /**
     * В цикле построчно читаем файл.
     * Одиночные (пустые строки сверху и снизу) короткие строки принимаем за названия глав.
     * @param event  Swing событие.
     * @throws svj.wedit.v6.exception.WEditException
     */
    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        //WordFileExtractor docExtractor;
        //ShowTitleContentHandler showTitleTarget;
        //WEdit6ContentHandler createBookTarget;
        Collection<ShowTitleObj> titleList;
        WidgetsDialog            dialog;
        File                     file;
        BookContent              bookContent;

        file            = null;
        //docExtractor    = new WordFileExtractor();

        try
        {
            // проверка что выбран сборник и раздел, в котором будет создана новая книга
            checkProject();

            // выбрать DOC-файл для экспорта
            // Параметр, запоминающий директорию импортируемой книги.
            file = getFileName();
            if ( file == null ) return; // Не стоит сообщать что была отмена.

            // Очищаем обработчики от данных предыдущих парсингов.
            showTitleHandler.start();
            createFileHandler.start();

            // для ТХТ здесь получена кодирвока файла - надо как-то передать
            fileExtractor.processAdditional ( additionalGuiComponent );

            // первый проход - собрали инфу о заголовках.
            //showTitleTarget = new ShowTitleContentHandler();
            //docExtractor.parse ( file, showTitleTarget );
            fileExtractor.parse ( file, showTitleHandler );

            // отображаем инфу о заголовках с возм редактирования пользователем уровня заголовков (либо откл)
            titleList   = (Collection<ShowTitleObj>) showTitleHandler.getResult();
            dialog      = createShowTitleDialog ( titleList );
            dialog.showDialog();

            if ( dialog.isOK() )
            {
                // второй проход - собтвенно создаем файл.
                // - здесь создаем  bookContent
                bookContent = createNewBookContent();
                if ( bookContent != null )
                {
                    Log.file.debug ( "--- %s import: create bookContent = %s", fileFormat, bookContent );
                    // взять измененные результаты по заголовкам.
                    handleTitleList ( dialog.getWidgets() );
                    Log.file.debug ( "--- %s import: titleList = %s", fileFormat, titleList );
                    // запустить вторйо проход парсера файла
                    //createBookTarget = new WEdit6ContentHandler ( bookContent, titleList );
                    createFileHandler.init ( bookContent, titleList );
                    //Log.file.debug ( "--- DOC import: create createBookTarget = %s", createFileHandler );
                    //docExtractor.parse ( file, createBookTarget );
                    fileExtractor.parse ( file, createFileHandler );
                    //docExtractor.parse ( getFileName(), createBookTarget );
                    //bookContent.setEdit ( true );
                    // Сохранить новую книгу в файле.
                    FileTools.saveBook ( bookContent );
                    //DialogTools.showMessage ( file.toString(), "Успешно проимпортирована." );
                    DialogTools.showMessage ( "Импорт книги из формата " +fileFormat, "Файл '" + file + "'\nуспешно сконвертирован." );
                }
            }

        } catch ( Exception e )        {
            Log.file.debug ( Convert.concatObj ( "Ошибка импорта книги '", file, "' из формата ",fileFormat,"." ), e );
            // todo Удалить bookContent
            //Par.GM.getFrame ().
            throw new WEditException ( e, "Ошибка импорта книги '",file,"' из формата ",fileFormat," :\n", e );
        }
    }

    private void handleTitleList ( Collection<AbstractWidget> widgets )
    {
        Object       obj;
        ShowTitleObj titleObj;
        WBookElement bookElement;
        int          level;

        for ( AbstractWidget widget : widgets )
        {
            // Взять виджет
            // Взять новое значение уровня.
            obj = widget.getObject();
            if ( (obj != null) && (obj instanceof ShowTitleObj) )
            {
                // Занести в  обьект виджета.
                titleObj    = (ShowTitleObj) obj;
                bookElement = (WBookElement) widget.getValue();
                if ( bookElement == null )
                    level = -1;
                else
                    level = bookElement.getElementLevel();
                titleObj.setLevel ( level );
            }
        }
    }

    private BookContent createNewBookContent ()  throws WEditException
    {
        BookContent         bookContent;
        CreateBookFunction createBookFunction;

        createBookFunction  = new CreateBookFunction();
        // -- Диалог - Запросить имя новой книги -- Создать книгу.
        createBookFunction.handle ( null );
        bookContent         = createBookFunction.getBookContent();
        // убрать Служебные главы
        bookContent.clear();
        return bookContent;
    }

    private File getFileName ()
    {
        File                file;
        SimpleParameter sp;
        String              fileDir, paramName;

        paramName = "fileDir";

        sp  = (SimpleParameter) getParameter ( paramName );
        if ( sp == null )
        {
            sp  = new SimpleParameter ( paramName, null ); // дефолтное значение
            sp.setHasEmpty ( false );
            setParameter ( paramName, sp );
        }

        fileDir    = sp.getValue();
        if ( fileDir == null )  fileDir = Par.USER_HOME_DIR;

        file    = new File ( fileDir );
        file    = FileTools.selectFileName ( Par.GM.getFrame(), file, additionalGuiComponent );
        Log.file.debug ( "Select %s file = %s", fileFormat, file );

        if ( file != null )
        {
            sp.setValue ( file.getParentFile().toString() );
        }

        return file;
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

    private WidgetsDialog createShowTitleDialog ( Collection<ShowTitleObj> titleList ) throws WEditException
    {
        WidgetsDialog                   dialog;
        Collection<WBookElement>        elements, cloneList;
        ComboBoxWidget<WBookElement>    widget;
        String                          title;
        int                             level, titleSize, ic;

        if ( (titleList == null) || titleList.isEmpty() )  throw new MessageException ( "Заголовки отсутствуют" );

        titleSize   = 0;
        widget      = null;

        // Получаем список уровней-элементов книги.
        cloneList    = BookStructureTools.getDefaultBookElements ();

        // удаляем элемент типа "Книга"
        elements = new ArrayList<WBookElement>();
        ic = 0;
        for ( WBookElement be : cloneList )
        {
            if ( ic != 0  ) elements.add ( be );
            ic++;
        }

        /*
        // сначала вычисляем макс размер титла виджетов.
        for ( ShowTitleObj obj : titleList )
        {
            title   = obj.getTitle();
            if ( title.length() > 40 )  title = title.substring ( 0, 36 ) + "...";
            level   = obj.getLevel();
            if ( level >= 0 )  title = title + " [" + level + "]";
            if ( titleSize < title.length() )  titleSize = title.length();
            //Log.l.debug ( "--- titleSize = %d; title = %s", titleSize, title );
        }
        */

        dialog      = new WidgetsDialog ( "Предварительные Заголовки книги." );

        for ( ShowTitleObj obj : titleList )
        {
            title   = obj.getTitle();
            if ( title.length() > 40 )  title = title.substring ( 0, 36 ) + "...";
            level   = obj.getLevel();
            if ( level >= 0 )  title = title + " [" + level + "]";
            cloneList   = BookStructureTools.cloneElements ( elements );
            widget      = new ComboBoxWidget<WBookElement> ( title, true, "-", cloneList );
            if ( level >= 0 )
                widget.setStartIndex ( level+1 ); // +1 - т.к. применяется emptyValue
            else
                widget.setStartIndex ( 23 );
            //widget.setTitleWidth ( titleSize );
            widget.setComboRenderer ( new INameNumberRenderer () );
            widget.setObject ( obj );
            dialog.addWidget ( widget );
            if ( titleSize < title.length() )  titleSize = title.length();
            Log.l.debug ( "--- titleSize = %d; title = %s", titleSize, title );
        }

        if ( widget != null )
        {
            // перевести размер в символах в размер в пикселях
            ic = GuiTools.getFontSize ( widget.getGuiComponent ().getFont (), titleSize );
        }
        else
        {
            ic= 300;
        }
        Log.l.debug ( "--- titleSize 2 = %d; ic = %d", titleSize, ic );

        dialog.setTitleWidth ( ic );
        dialog.pack();

        return dialog;
    }

    /*
    public void afterHandle ()
    {
        DialogTools.showMessage ( "Импорт книги из формата DOC", "Файл '"+file+"'\nуспешно сконвертирован." );
    }
    */

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

    public void setAdditionalGuiComponent ( JComponent additionalGuiComponent )
    {
        this.additionalGuiComponent = additionalGuiComponent;
    }
}
