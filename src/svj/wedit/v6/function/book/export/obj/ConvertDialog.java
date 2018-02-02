package svj.wedit.v6.function.book.export.obj;


import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.params.BooleanParameter;
import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.function.params.SimpleParameter;
import svj.wedit.v6.gui.dialog.WValidateDialog;
import svj.wedit.v6.gui.layout.VerticalLayout;
import svj.wedit.v6.gui.list.WListPanel;
import svj.wedit.v6.gui.panel.EditablePanel;
import svj.wedit.v6.gui.panel.SimpleEditablePanel;
import svj.wedit.v6.gui.renderer.INameRenderer;
import svj.wedit.v6.gui.tabs.TabsChangeListener;
import svj.wedit.v6.gui.tabs.TabsPanel;
import svj.wedit.v6.gui.widget.*;
import svj.wedit.v6.gui.widget.font.FontWidget;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.WType;
import svj.wedit.v6.obj.book.BookStructure;
import svj.wedit.v6.obj.book.element.WBookElement;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.DialogTools;
import svj.wedit.v6.tools.GuiTools;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;

/**
 * Диалог настроек элементов книги - для конвертации (RTF, HTML...)
 * <BR/> Содержит закладки на настройки.
 * <BR/> Выбирает файл.
 * <BR/> Об ошибках в параметрах - сообщает в специальном окне диалога. Либо отдельным выскакивающим диалоговым окошком.
 * <BR/> На табиках.
 * <BR/>
 * <BR/> Структура (в отдельных табиках)
 * <BR/> 1) Заголовки
 * <BR/> 2) Типы элементов
 * <BR/> 3) Разное
 * <BR/> 4) Локальные (Индивидуальное)
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 26.09.2013 13:50
 */
public class ConvertDialog extends WValidateDialog<BookmarksParameter,ConvertParameter>
{
    private final BookmarksParameter bookmarkParameter;
    /** Список вариантов конвертации (закладки) */
    private WListPanel<ConvertParameter>  listPanel;
    //private JPanel          elementsPanel, othersPanel, typesPanel;
    private EditablePanel          elementsPanel, othersPanel, typesPanel, localePanel;
    private JButton         addButton, editButton, saveButton, cancelButton, deleteButton, typeListButton, copyButton, pasteButton;
    private FileWidget      fileWidget;
    private Collection<WBookElement> bookElementList;
    private Map<String, WType> bookTypeList;

    //private Collection<FunctionParameter>  otherConvertParams = null;

    private int             titleWidth, valueWidth;

    private Collection<FunctionParameter> localeParams;



    public ConvertDialog ( Frame owner, String title, BookmarksParameter bookmarksParameter,
                           Collection<WBookElement> bookElementList, Map<String, WType> bookTypes )
    {
        super ( owner, title );

        this.bookmarkParameter  = bookmarksParameter;
        this.bookElementList    = bookElementList;
        this.bookTypeList       = bookTypes;

        titleWidth = 200;
        valueWidth = 550;  // 450

        localeParams = null;
    }

    public ConvertDialog ( Frame owner, String title, BookmarksParameter bookmarksParameter, BookStructure bookStructure,
                           Collection<FunctionParameter> localeParams )
    {
        super ( owner, title );

        this.bookmarkParameter  = bookmarksParameter;
        this.bookElementList    = bookStructure.getBookElements();
        this.bookTypeList       = bookStructure.getTypes ();

        titleWidth = 200;
        valueWidth = 550;

        this.localeParams   = localeParams;
    }

    /**
     * Валидируем данные:
     * <br/> - файл, его наличие
     * <br/> - типы - хоть один должен быть включен
     * <br/>
     * <br/>
     * <br/>
     * @return   TRUE - все ОК.
     */
    @Override
    public boolean validateData ()
    {
        boolean         result, useType;
        String          str;
        File            file;
        ComboBoxWidget  widget;
        TypeHandleType  handleType;

        result  = true;
        useType = false;

        try
        {
            // ---------------------- файл --------------------------
            str = fileWidget.getValue();
            if ( str != null )  str = str.trim();
            if ( (str == null) || str.isEmpty() )
            {
                result  = false;
                addValidateErr ( "Не задан файл для преобразования." );
            }
            else
            {
                // - его существование
                file     = new File ( str ); // имя файла здесь точно существует.
                if ( file.exists() )
                {
                    // заданный файл уже существует.
                    if ( file.isDirectory() )
                    {
                        result  = false;
                        addValidateErr ( "Заданый файл является директорией." );
                    }
                    else if ( file.isHidden() )
                    {
                        result  = false;
                        addValidateErr ( "Заданый файл является скрытым файлом." );
                    }
                }
            }

            // ------------------------- типы параметров ---------------------
            for ( Component c : typesPanel.getComponents() )
            {
                Log.l.debug ( "-- Type panel element = %s", c );
                if ( c instanceof ComboBoxWidget )
                {
                    widget      = (ComboBoxWidget) c;
                    handleType  = ConvertTools.getType ( widget.getValue().toString() );
                    if ( handleType != TypeHandleType.NOTHING ) useType = true;
                    Log.l.debug ( "---- widget : name = %s; value = %s", widget.getName(), widget.getValue() );     // StringFieldWidget
                }
            }
            if ( ! useType )
            {
                result  = false;
                addValidateErr ( "Нечего преобразовывать.\nНи один тип не выбран для преобразования." );
            }

            // - Локальные параметры
            //if ( localePanel != null )
            //{
                AbstractWidget aWidget;
                for ( Component c : localePanel.getComponents() )
                {
                    if ( c instanceof AbstractWidget )
                    {
                        aWidget = ( AbstractWidget ) c;
                        try
                        {
                            aWidget.validateWidget();
                        } catch ( Exception e )     {
                            result  = false;
                            addValidateErr ( "Ошибка виджета '"+aWidget.getTitleName()+ "' : "+e.getMessage() );
                        }
                    }
                }
            //}

            // ---------------- На выбранность закладки ---------------------
            if ( getCurrentBookmark() == null )
            {
                result  = false;
                addValidateErr ( "Нечего преобразовывать.\nСтруктура не выбрана." );
            }

            // ---------------- Локальные параметры ??? ---------------------

        } catch ( Exception e )        {
            Log.l.error ( "Ошибка валидации параметров конвертации книги.", e );
            result  = false;
            addValidateErr ( e.getMessage() );
        }

        return result;
    }

    public void init ( BookmarksParameter initObject ) throws WEditException
    {
        JPanel              panel, lPanel;
        ActionEvent         actionEvent;
        ActionListener      actionListener;

        // уже занесли в конструкторе
        //bookmarkParameter = initObject;

        // главная панель диалога настроек.
        panel = new JPanel();
        panel.setLayout ( new BorderLayout ( 5, 5 ) );

        addToCenter ( panel );

        // - панель списка закладок - слева -- String name, ActionEvent actionEvent, ActionListener actionListener
        lPanel = new JPanel ();
        //lPanel.setLayout ( new BoxLayout ( lPanel, BoxLayout.PAGE_AXIS ) );
        lPanel.setLayout ( new VerticalLayout (0) );
        panel.add ( lPanel, BorderLayout.WEST );

        // Акция при смене обьекта в списке
        actionEvent     = new ActionEvent ( this, 1, ConvertAction.SELECT_NEW_BOOKMARK.toString() );
        //actionListener  = null;
        actionListener  = new ConvertActionListener ( this );

        // список Закладок
        listPanel       = new WListPanel<ConvertParameter> ( "Виды", actionEvent, actionListener );
        listPanel.setList ( bookmarkParameter.getList() );
        lPanel.add ( listPanel );

        // ------- Кнопки: Создать новую, Редактировать, Применить, Отменить, Удалить. ---------
        // String title, String toolTip, String iconPath
        // - add      - int width, ActionListener actionListener, String command
        //addButton = GuiTools.createButton ( "Создать", null, "new.png", width, actionListener, ConvertAction.CREATE.toString() );
        addButton = GuiTools.createButton ( "Создать", null, "new.png" );
        addButton.setActionCommand ( ConvertAction.CREATE.toString() );
        addButton.addActionListener ( actionListener );
        lPanel.add ( addButton );
        // - copy
        copyButton = GuiTools.createButton ( "Копировать", null, "copy.png" );
        copyButton.setActionCommand ( ConvertAction.COPY.toString() );
        copyButton.addActionListener ( actionListener );
        lPanel.add ( copyButton );
        // - copy
        pasteButton = GuiTools.createButton ( "Вставить", null, "paste.png" );
        pasteButton.setActionCommand ( ConvertAction.PASTE.toString () );
        pasteButton.addActionListener ( actionListener );
        lPanel.add ( pasteButton );
        // - edit
        // -- Блокируются кнопки: Конвертить, Редактировать, Создать
        // -- Разблокируются: Применить
        editButton = GuiTools.createButton ( "Редактировать", null, "edit.png" );
        editButton.setActionCommand ( ConvertAction.EDIT.toString() );
        editButton.addActionListener ( actionListener );
        lPanel.add ( editButton );
        // - save
        // -- Разблокирует кнопку - Конвертить
        // -- Действия: Залить данные в обьект из виджетов. Применить
        saveButton = GuiTools.createButton ( "Сохранить", null, "add.png" );
        saveButton.setActionCommand ( ConvertAction.SAVE.toString() );
        saveButton.addActionListener ( actionListener );
        saveButton.setEnabled ( false );  // исходное состояние - отключено.
        lPanel.add ( saveButton );
        // - cancel -- Отмена редактирвоания -- Не заливать данные в обьект из виджетов
        cancelButton = GuiTools.createButton ( "Отменить", null, "sync.png" );
        cancelButton.setActionCommand ( ConvertAction.CANCEL.toString() );
        cancelButton.addActionListener ( actionListener );
        cancelButton.setEnabled ( false );  // исходное состояние - отключено.
        lPanel.add ( cancelButton );
        // - delete
        deleteButton = GuiTools.createButton ( "Удалить", null, "delete.png" );
        deleteButton.setActionCommand ( ConvertAction.DELETE.toString() );
        deleteButton.addActionListener ( actionListener );
        lPanel.add ( deleteButton );

        // - работа со списками типов конвертаций    --- Надо ли это? Ведь тип samlib для разных книг будет выглядеть по разному. У кого-то Не выводить главы, у кого-то - выводить и т.д.
        /*
        typeListButton = GuiTools.createButton ( "Список", null, "edit.png" );
        typeListButton.setActionCommand ( ConvertAction.CONVERT_LIST_EDIT.toString() );
        typeListButton.addActionListener ( actionListener );
        lPanel.add ( deleteButton );
        */

        // Кнопка "Конвертить" доступна только если есть выбранная закладка.
        setOkButtonText ( "Преобразовать" );

        Font font;
        JPanel centerPanel;

        centerPanel      = new JPanel();
        centerPanel.setLayout ( new BorderLayout() );
        panel.add ( centerPanel, BorderLayout.CENTER );

        // - центр панель параметров - состоит из двух - вверху - параметры элементов, внизу - все остальные - файл и т.д.
        //cp      = new JPanel();
        //cp.setLayout ( new BoxLayout ( cp, BoxLayout.PAGE_AXIS ) );
        //centerPanel.add ( new JScrollPane ( cp ), BorderLayout.CENTER );

        TabsPanel<EditablePanel>    tabsPanel;
        EditablePanel               tabPanel;
        String                      tabsName;
        TabsChangeListener          tabsListener;

        tabsPanel = new TabsPanel<EditablePanel> ();

        tabsPanel.setTabPlacement ( JTabbedPane.TOP );
        tabsName    = initObject.getName() +"_Tabs";          // ConvertTo_Tabs
        tabsPanel.setName ( tabsName );
        tabsPanel.getAccessibleContext().setAccessibleName ( tabsName );

        centerPanel.add ( tabsPanel, BorderLayout.CENTER );


        EditablePanel sPanel;


        //font    = new Font ( "Monospaced", Font.BOLD, 12 ); // фонт титлов на группах параметров

        // Заголовки
        elementsPanel = createConvertParamsPanelAndAddToTabs ( tabsPanel, "Titles", "Заголовки" );

        // Типы элементов
        typesPanel = createConvertParamsPanelAndAddToTabs ( tabsPanel, "Types", "Типы элементов" );

        // Разное
        othersPanel = createConvertParamsPanelAndAddToTabs ( tabsPanel, "Others", "Разное" );

        // Локальные параметры - Индивидуальная панель. Берется вся панель целиком.
        //if ( initObject.hasLocaleParams() )
        //{
        //    localePanel = createConvertParamsPanelAndAddToTabs ( tabsPanel, "Locale", "Локальные" );
        //}
        localePanel = createConvertParamsPanelAndAddToTabs ( tabsPanel, "Locale", "Локальные" );

        Log.l.debug ( "(%s) ConvertDialog.init: tabsPanel = %s", getName(), tabsPanel );

        // создать листенер выборки табиков - самым последним, т.к. tabsPanel.addPanel дергает этот листенер.
        tabsListener = new TabsChangeListener ( tabsName+"_TabsListener" );
        tabsPanel.setSelectTabsListener ( tabsListener );

        //tabsPanel.setVisible ( true );
        //tabsPanel.setSelectedFirst();
        //centerPanel.revalidate ();
        //centerPanel.repaint ();

        // File - выделен в самом низу панели - чтобы был всегда виден
        // Файл
        fileWidget = new FileWidget ( "Файл", false );
        fileWidget.setName ( "file" );
        fileWidget.setEditable ( false );
        fileWidget.setTitleWidth ( titleWidth );
        fileWidget.setValueWidth ( valueWidth );
        centerPanel.add ( fileWidget, BorderLayout.SOUTH  );

        String str;
        ConvertParameter p;
        // Взять текущий выбранную закладку. Если есть - выбрать.
        str = bookmarkParameter.getCurrentBookmark();
        if ( str != null )
        {
            p = bookmarkParameter.getConvertParameter ( str );
            if ( p != null )  listPanel.setSelectedItem ( p );
        }
    }

    private EditablePanel createConvertParamsPanelAndAddToTabs ( TabsPanel<EditablePanel> tabsPanel, String tabId, String tabTitle )
    {
        EditablePanel result;

        result   = new SimpleEditablePanel();
        result.setLayout ( new BoxLayout ( result, BoxLayout.PAGE_AXIS ) );
        result.setName ( tabId );
        result.setId ( tabId );
        tabsPanel.addPanel ( result, tabId, tabTitle );

        return result;
    }

    public void init2 ( BookmarksParameter initObject ) throws WEditException
    {
        JPanel              panel, lPanel, cp;
        ActionEvent         actionEvent;
        ActionListener      actionListener;
        //JButton             button;
        //int width = 300;

        // уже занесли в конструкторе
        //bookmarkParameter = initObject;

        // главная панель диалога настроек.
        panel = new JPanel();
        panel.setLayout ( new BorderLayout ( 5, 5 ) );

        addToCenter ( panel );

        // - панель списка закладок - слева -- String name, ActionEvent actionEvent, ActionListener actionListener
        lPanel = new JPanel ();
        //lPanel.setLayout ( new BoxLayout ( lPanel, BoxLayout.PAGE_AXIS ) );
        lPanel.setLayout ( new VerticalLayout (0) );
        panel.add ( lPanel, BorderLayout.WEST );

        // Акция при смене обьекта в списке
        actionEvent     = new ActionEvent ( this, 1, ConvertAction.SELECT_NEW_BOOKMARK.toString() );
        //actionListener  = null;
        actionListener  = new ConvertActionListener ( this );

        // список Закладок
        listPanel       = new WListPanel<ConvertParameter> ( "Виды", actionEvent, actionListener );
        listPanel.setList ( bookmarkParameter.getList() );
        lPanel.add ( listPanel );

        // ------- Кнопки: Создать новую, Редактировать, Применить, Отменить, Удалить. ---------
        // String title, String toolTip, String iconPath
        // - add      - int width, ActionListener actionListener, String command
        //addButton = GuiTools.createButton ( "Создать", null, "new.png", width, actionListener, ConvertAction.CREATE.toString() );
        addButton = GuiTools.createButton ( "Создать", null, "new.png" );
        addButton.setActionCommand ( ConvertAction.CREATE.toString() );
        addButton.addActionListener ( actionListener );
        lPanel.add ( addButton );
        // - copy
        copyButton = GuiTools.createButton ( "Копировать", null, "copy.png" );
        copyButton.setActionCommand ( ConvertAction.COPY.toString() );
        copyButton.addActionListener ( actionListener );
        lPanel.add ( copyButton );
        // - copy
        pasteButton = GuiTools.createButton ( "Вставить", null, "paste.png" );
        pasteButton.setActionCommand ( ConvertAction.PASTE.toString () );
        pasteButton.addActionListener ( actionListener );
        lPanel.add ( pasteButton );
        // - edit
        // -- Блокируются кнопки: Конвертить, Редактировать, Создать
        // -- Разблокируются: Применить
        editButton = GuiTools.createButton ( "Редактировать", null, "edit.png" );
        editButton.setActionCommand ( ConvertAction.EDIT.toString() );
        editButton.addActionListener ( actionListener );
        lPanel.add ( editButton );
        // - save
        // -- Разблокирует кнопку - Конвертить
        // -- Действия: Залить данные в обьект из виджетов. Применить
        saveButton = GuiTools.createButton ( "Сохранить", null, "add.png" );
        saveButton.setActionCommand ( ConvertAction.SAVE.toString() );
        saveButton.addActionListener ( actionListener );
        saveButton.setEnabled ( false );  // исходное состояние - отключено.
        lPanel.add ( saveButton );
        // - cancel -- Отмена редактирвоания -- Не заливать данные в обьект из виджетов
        cancelButton = GuiTools.createButton ( "Отменить", null, "sync.png" );
        cancelButton.setActionCommand ( ConvertAction.CANCEL.toString() );
        cancelButton.addActionListener ( actionListener );
        cancelButton.setEnabled ( false );  // исходное состояние - отключено.
        lPanel.add ( cancelButton );
        // - delete
        deleteButton = GuiTools.createButton ( "Удалить", null, "delete.png" );
        deleteButton.setActionCommand ( ConvertAction.DELETE.toString() );
        deleteButton.addActionListener ( actionListener );
        lPanel.add ( deleteButton );

        // Кнопка "Конвертить" доступна только если есть выбранная закладка.
        setOkButtonText ( "Преобразовать" );

        Font font;
        JPanel centerPanel;

        centerPanel      = new JPanel();
        centerPanel.setLayout ( new BorderLayout() );
        panel.add ( centerPanel, BorderLayout.CENTER );

        // - центр панель параметров - состоит из двух - вверху - параметры элементов, внизу - все остальные - файл и т.д.
        cp      = new JPanel();
        cp.setLayout ( new BoxLayout ( cp, BoxLayout.PAGE_AXIS ) );
        centerPanel.add ( new JScrollPane ( cp ), BorderLayout.CENTER );

        font    = new Font ( "Monospaced", Font.BOLD, 12 ); // фонт титлов на группах параметров
        /*
        elementsPanel   = new JPanel();
        elementsPanel.setLayout ( new BoxLayout ( elementsPanel, BoxLayout.PAGE_AXIS ) );
        //elementsPanel.setBorder ( BorderFactory.createTitledBorder ( "Элементы" ) );
        elementsPanel.setBorder ( BorderFactory.createTitledBorder ( null, "Элементы", TitledBorder.CENTER, TitledBorder.TOP, font, WCons.BLUE_1 ) );
        cp.add ( elementsPanel );

        typesPanel   = new JPanel();
        typesPanel.setLayout ( new BoxLayout ( typesPanel, BoxLayout.PAGE_AXIS ) );
        //typesPanel.setBorder ( BorderFactory.createTitledBorder ( "Типы элементов" ) );
        typesPanel.setBorder ( BorderFactory.createTitledBorder ( null, "Типы элементов", TitledBorder.CENTER, TitledBorder.TOP, font, WCons.BLUE_1 ) );
        cp.add ( typesPanel );

        othersPanel   = new JPanel();
        othersPanel.setLayout ( new BoxLayout ( othersPanel, BoxLayout.PAGE_AXIS ) );
        //othersPanel.setBorder ( BorderFactory.createTitledBorder ( "Параметры" ) );
        othersPanel.setBorder ( BorderFactory.createTitledBorder ( null, "Параметры", TitledBorder.CENTER, TitledBorder.TOP, font, WCons.BLUE_1 ) );
        cp.add ( othersPanel );
        */
        // File - выделен в самом низу панели - чтобы был всегда виден
        // Файл
        fileWidget = new FileWidget ( "Файл", false );
        fileWidget.setName ( "file" );
        fileWidget.setEditable ( false );
        fileWidget.setTitleWidth ( titleWidth );
        fileWidget.setValueWidth ( valueWidth );
        centerPanel.add ( fileWidget, BorderLayout.SOUTH  );

        String str;
        ConvertParameter p;
        // Взять текущий выбранную закладку. Если есть - выбрать.
        str = bookmarkParameter.getCurrentBookmark();
        if ( str != null )
        {
            p = bookmarkParameter.getConvertParameter ( str );
            if ( p != null )  listPanel.setSelectedItem ( p );
        }
        //*/
    }

    /**
     * Изменился обьект в списке закладок Диалога.
     * <br/> Полностью перерисовываем рабочие панели
     * @param parameter Текущий сложный параметр настроек конвертации книги.
     */
    void showWorkPanel ( ConvertParameter parameter )
    {
        JPanel  parameterPanel;
        AbstractWidget widget;

        Log.l.debug ( "Start (showWorkPanel). currentParameter = %s", parameter );

        //  --------------- Изменить панель элементов ---------------
        elementsPanel.removeAll();

        // Взять новый параметр и перерисовать
        for ( ElementConvertParameter element : parameter.getElementList() )
        {
            parameterPanel = createElementPanel ( element );
            elementsPanel.add ( parameterPanel );
        }

        elementsPanel.revalidate();
        elementsPanel.repaint ();

        //  --------------- Изменить панель типов элементов ---------------
        typesPanel.removeAll();

        // Взять новый параметр и перерисовать
        for ( SimpleParameter type : parameter.getTypes() )
        {
            //parameterPanel = createTypeElementPanel ( type );
            //typesPanel.add ( parameterPanel );
            widget = createTypeElementPanel ( type );
            typesPanel.add ( widget );
        }

        typesPanel.revalidate();
        typesPanel.repaint();

        // ----------- Изменить панель остальных параметров ---------------
        othersPanel.removeAll();
        /*
        // Для самиздат - отключить (не выводить) теги - html, title, body.
        //othersPanel.add ( new JLabel ( parameter.getName() ) );
        // - Включить/выключить заголовок - html,title,body
        parameterPanel = createOnOffHtmlTitlePanel ( parameter.getTornOffHtmlTitle() );
        othersPanel.add ( parameterPanel );
        // - Красная строка
        parameterPanel = createRedLineParamPanel ( parameter.getRedLineParam() );
        othersPanel.add ( parameterPanel );
        */
        // - Аннотация
        parameterPanel = createPrintAnnotationPanel ( parameter.getPrintAnnotation() );
        othersPanel.add ( parameterPanel );
        // - Сигнальные символы
        parameterPanel = createWarnTextPanel ( parameter.getWarnTextParam() );
        othersPanel.add ( parameterPanel );
        // - Заключительная строка
        parameterPanel = createEndTextPanel ( parameter.getEndTextParam() );
        othersPanel.add ( parameterPanel );
        // - createContentParam
        parameterPanel = createContentParamPanel ( parameter.getCreateContentParam() );
        othersPanel.add ( parameterPanel );
        // - contentWidthParam
        parameterPanel = createContentWidthParamPanel ( parameter.getContentWidthParam() );
        othersPanel.add ( parameterPanel );
        //
        othersPanel.revalidate();
        othersPanel.repaint();

        // ----------- Изменить Локальную панель ---------------
        // - Здесь необходимо взять новые параметры для данной Закладки и занести их в Панель.
        //Log.l.info ( "[Locale] localePanel = %s", localePanel );
        //if ( localePanel != null )
        //{
            localePanel.removeAll();
            if ( ! parameter.getLocaleParams().isEmpty() )
            {
                for ( FunctionParameter fp : parameter.getLocaleParams () )
                {
                    widget = GuiTools.createWidget ( fp, titleWidth, valueWidth );
                    Log.l.info ( "[Locale]\n  param = %s;\n  create widget = %s", fp, widget );
                    if ( widget == null )
                    {
                        // Создать Ошибочный виджет
                        widget = new StringFieldWidget ( fp.getName (), "Ошибка создания виджета " + fp.getClass ().getName () );
                        widget.setTitleWidth ( titleWidth );
                        widget.setValueWidth ( valueWidth );
                        widget.setEditable ( false );
                        widget.setForeground ( Color.RED );
                    }
                    localePanel.add ( widget );
                }
                // пропуск
                localePanel.add ( Box.createVerticalGlue () );      // for BoxLayout.PAGE
            }
            localePanel.revalidate();
            localePanel.repaint();
        //}

        // -------------------------- File ------------------------------------
        fileWidget.setValue ( parameter.getFileName() );
        fileWidget.setEditable ( false );  // запрещаем редактирование
    }

    /**
     * <br/> Параметры элемента:
     * <BR/> 1) Межстрочный интервал, в пикселях. (10)                              -- RTF
     * <BR/> 2) Header - текст вверху слева и справа от нумерации страниц. (пусто)  -- RTF
     * <BR/> 3) Фонт простого текста. И других элементов текста.
     * <BR/> 4) Начальный номер страницы.
     * <BR/> 5) Вид форматирования текста (JUSTIFIED)
     * <BR/> 6) Красная строка (в пробелах)
     * <br/>
     * @param element  Параметр оспиания элемента заголовка.
     * @return   Панель
     */
    private JPanel createElementPanel ( ElementConvertParameter element ) // throws WEditException
    {
        JPanel              result;
        FontWidget          fontWidget;
        ComboBoxWidget<TitleViewMode>    formatWidget;
        Font                font;
        Color               color;
        TitleViewMode       formatType;

        result      = new JPanel();
        result.setLayout ( new BoxLayout ( result, BoxLayout.PAGE_AXIS ) );
        //result.setBorder ( BorderFactory.createTitledBorder ( element.getName() ) );
        font        = new Font ( "Monospaced", Font.BOLD, 10 );
        result.setBorder ( BorderFactory.createTitledBorder ( null, element.getName (), TitledBorder.CENTER, TitledBorder.TOP, font, Color.BLACK ) );

        // - Шрифт
        fontWidget  = new FontWidget ( "Шрифт" );
        result.add ( fontWidget );
        fontWidget.setName ( "ElementFont" );
        fontWidget.setEditable ( false );
        fontWidget.setTitleWidth ( titleWidth );
        fontWidget.setValueWidth ( valueWidth );
        fontWidget.setObject ( element );
        // установить color
        color       = element.getColor ();
        if ( color == null )   color = Color.BLACK;
        fontWidget.setColor ( color );
        // установить шрифт
        font        = element.getFont ();
        if ( font == null )   font = WCons.TEXT_FONT_1;
        fontWidget.setValue ( font );

        // - формат
        formatWidget    = new ComboBoxWidget<TitleViewMode> ( "Формат", TitleViewMode.values() );
        formatWidget.setName ( "ElementFormat" );
        formatWidget.setEditable ( false );
        formatWidget.setTitleWidth ( titleWidth );
        formatWidget.setValueWidth ( valueWidth );
        formatWidget.setObject ( element );
        formatWidget.setComboRenderer ( new INameRenderer () );
        formatType      = element.getFormatType();
        if ( formatType == null )   formatType = TitleViewMode.NOTHING;
        formatWidget.setValue ( formatType );
        result.add ( formatWidget );

        return result;
    }

    private AbstractWidget createTypeElementPanel ( SimpleParameter element )
    {
        ComboBoxWidget<TypeHandleType>  typeWidget;
        TypeHandleType                  type;

        typeWidget = new ComboBoxWidget<TypeHandleType> ( element.getName(), TypeHandleType.values() );
        //typeWidget.setName ( "TypeHandleType" );
        typeWidget.setComboRenderer ( new INameRenderer() );
        typeWidget.setEditable ( false );
        //typeWidget.setEnabled ( true );
        typeWidget.setTitleWidth ( titleWidth );
        typeWidget.setValueWidth ( valueWidth );
        typeWidget.setObject ( element );

        try
        {
            type = TypeHandleType.valueOf ( element.getValue() );
        } catch ( Exception e )        {
            Log.l.error ( Convert.concatObj ( "Error: element = ", element ), e );
            type = TypeHandleType.NOTHING;
        }
        typeWidget.setStartValue ( type );

        return typeWidget;
    }

    private JPanel createContentParamPanel ( BooleanParameter createContentParam )
    {
        BooleanWidget   widget;

        widget = new BooleanWidget ( "Формировать оглавление", createContentParam.getValue(), BooleanWidget.Orientation.TITLE_FIRST );
        widget.setEditable ( false );
        widget.setTitleWidth ( titleWidth );
        widget.setValueWidth ( valueWidth );
        widget.setObject ( createContentParam );

        return widget;
    }

    /*
    private JPanel createOnOffHtmlTitlePanel ( BooleanParameter tornOffHtmlTitleParam )
    {
        BooleanWidget   widget;

        widget = new BooleanWidget ( "Включить HTML-заголовок", tornOffHtmlTitleParam.getValue(), BooleanWidget.Orientation.TITLE_FIRST );
        widget.setEditable ( false );
        widget.setTitleWidth ( titleWidth );
        widget.setValueWidth ( valueWidth );
        widget.setObject ( tornOffHtmlTitleParam );

        return widget;
    }

    private JPanel createRedLineParamPanel ( SimpleParameter redLineParam )
    {
        StringFieldWidget   widget;

        widget = new StringFieldWidget ( "Красная строка", redLineParam.getValue(), redLineParam.hasEmpty() );
        widget.setEditable ( false );
        widget.setTitleWidth ( titleWidth );
        widget.setValueWidth ( valueWidth );
        widget.setObject ( redLineParam );

        return widget;
    }
    */

    private JPanel createWarnTextPanel ( SimpleParameter warnTextParam )
    {
        StringFieldWidget   widget;

        widget = new StringFieldWidget ( "Сигнальные символы", warnTextParam.getValue(), warnTextParam.hasEmpty() );
        widget.setEditable ( false );
        widget.setTitleWidth ( titleWidth );
        widget.setToolTipText ( "Сигнализирование о наличии в тексте произведения данных символов. Через запятую." );
        widget.setValueWidth ( valueWidth );
        widget.setObject ( warnTextParam );

        return widget;
    }

    private JPanel createEndTextPanel ( SimpleParameter endTextParam )
    {
        StringFieldWidget   widget;

        widget = new StringFieldWidget ( "Заключительная строка", endTextParam.getValue(), endTextParam.hasEmpty() );
        widget.setEditable ( false );
        widget.setTitleWidth ( titleWidth );
        widget.setToolTipText ( "Заключительный текст. Например: Продолжение следует." );
        widget.setValueWidth ( valueWidth );
        widget.setObject ( endTextParam );

        return widget;
    }

    private JPanel createContentWidthParamPanel ( SimpleParameter contentWidthParam )
    {
        StringFieldWidget   widget;

        widget = new StringFieldWidget ( "Кол-во столбцов в оглавлении", contentWidthParam.getValue() );
        widget.setEditable ( false );
        widget.setTitleWidth ( titleWidth );
        widget.setValueWidth ( valueWidth );
        widget.setObject ( contentWidthParam );

        return widget;
    }

    private JPanel createPrintAnnotationPanel ( BooleanParameter cp )
    {
        BooleanWidget   widget;

        widget = new BooleanWidget ( "Выводить аннотацию", cp.getValue(), BooleanWidget.Orientation.TITLE_FIRST );
        widget.setEditable ( false );
        widget.setTitleWidth ( titleWidth );
        widget.setValueWidth ( valueWidth );
        widget.setObject ( cp );

        return widget;
    }

    /**
     * Начать редактирвоание атрибутов.
     * <br/> -- Блокируются кнопки: Конвертить, Редактировать, Создать
     * <br/> -- Разблокируются: Применить, Cancel
     *
     * @param parameter  Текущйи параметр
     */
    public void editBookmark ( ConvertParameter parameter )
    {
        Component[]     cList;
        AbstractWidget  widget;
        Container       container;

        // Блокируем кнопки
        getOkButton().setEnabled ( false );
        getEditButton().setEnabled ( false );
        getAddButton().setEnabled ( false );
        getDeleteButton().setEnabled ( false );

        // Разблокируем кнопки
        getSaveButton().setEnabled ( true );
        getCancelButton().setEnabled ( true );

        // Разрешаем редактирвоать виджеты
        // - Элементы
        for ( Component c : elementsPanel.getComponents() )
        {
            Log.l.debug ( "-- Element panel : %s", c );     // JPanel (BoxLayout)
            if ( c instanceof Container )
            {
                container = (Container) c;
                for ( Component cc : container.getComponents() )
                {
                    Log.l.debug ( "---- Element sub panel : %s", cc );     // StringFieldWidget
                    if ( cc instanceof AbstractWidget )
                    {
                        widget  = (AbstractWidget) cc;
                        widget.setEditable ( true );
                        Log.l.debug ( "------ widget : name = %s; value = %s", widget.getName(), widget.getValue() );     // StringFieldWidget
                    }
                }
            }
        }

        // - Типы
        cList = typesPanel.getComponents();
        for ( Component c : cList )
        {
            Log.l.debug ( "-- Type panel : ", c );       // BooleanWidget
            if ( c instanceof AbstractWidget )
            {
                widget  = (AbstractWidget) c;
                widget.setEditable ( true );
                Log.l.debug ( "---- widget : name = %s; value = %s", widget.getName(), widget.getValue() );     // StringFieldWidget
            }
        }

        // - Оcтальные параметры
        cList = othersPanel.getComponents();
        for ( Component c : cList )
        {
            if ( c instanceof AbstractWidget )
            {
                widget = (AbstractWidget) c;
                widget.setEditable ( true );
            }
        }

        // - Локальные параметры
        //if ( localePanel != null )
        //{
            cList = localePanel.getComponents();
            for ( Component c : cList )
            {
                if ( c instanceof AbstractWidget )
                {
                    widget = ( AbstractWidget ) c;
                    widget.setEditable ( true );
                }
            }
        //}

        // - Файл
        fileWidget.setEditable ( true );
    }

    public void deleteBookmarkParams ( ConvertParameter parameter )
    {
        // удалить в обьектах
        getBookmarkParameter().delete ( parameter );

        // удалить в диалоге
        listPanel.deleteItem ( parameter );

        // Если записи еще остались
        if ( listPanel.getListSize() > 0 )
        {
            // выбрать первую запись
            listPanel.setSelectedIndex ( 0 );
        }
        else
        {
            // Очистить рабочие панели
            elementsPanel.removeAll();
            othersPanel.removeAll();
            typesPanel.removeAll();
            //if ( localePanel != null )  localePanel.removeAll();
            localePanel.removeAll();
            fileWidget.setValue ( null );
        }
    }

    /**
     * Завершить редактирование
     * <br/> Скинуть данные из виджетов в параметры.
     * <br/> -- Разблокирует кнопку - Конвертить
     * <br/> -- Действия: Залить данные в обьект из виджетов.
     * <br/>
     * @param parameter Параметр текущей закладки.
     */
    public void saveBookmarkParams ( ConvertParameter parameter ) throws WEditException
    {
        Component[]         cList;
        AbstractWidget      widget;
        String              str;
        Object              object;
        Container           container;
        FunctionParameter   fParameter;
        SimpleParameter     simpleParameter;

        try
        {
            // Разблокируем кнопки
            getOkButton().setEnabled ( true );
            getEditButton().setEnabled ( true );
            getAddButton().setEnabled ( true );
            getDeleteButton().setEnabled ( true );

            // Блокируем кнопки
            getSaveButton().setEnabled ( false );
            getCancelButton().setEnabled ( false );

            // --------------- Берем параметры из виджетов ---------------

            // - Элементы
            cList = elementsPanel.getComponents();
            Log.l.debug ( "- cList size = %d", cList.length );     // JPanel (BoxLayout)
            for ( Component c : cList )
            {
                Log.l.debug ( "-- Element panel : %s", c );     // JPanel (BoxLayout)
                if ( c instanceof Container )
                {
                    container = (Container) c;
                    for ( Component cc : container.getComponents() )
                    {
                        Log.l.debug ( "---- Element sub panel : %s", cc );     // StringFieldWidget
                        if ( cc instanceof AbstractWidget )
                        {
                            widget      = (AbstractWidget) cc;
                            // запрещаем редактирование виджетов
                            widget.setEditable ( false );
                            // скинуть значения из виджетов в обьекты
                            processValueFromWidget ( widget );
                        }
                        else
                        {
                            str = Convert.concatObj ( "Обьект '%s' не является AbstractWidget.",cc );
                            Log.l.error ( null, str );
                        }
                    }
                }
            }

            // - Типы
            cList = typesPanel.getComponents();
            for ( Component c : cList )
            {
                Log.l.debug ( "-- Type panel : %s", c );       // BooleanWidget
                if ( c instanceof AbstractWidget )
                {
                    widget  = (AbstractWidget) c;
                    Log.l.debug ( "---- widget type : name = %s; value = %s", widget.getName(), widget.getValue() );
                    // запрещаем редактирование виджетов
                    widget.setEditable ( false );

                    object  = widget.getObject();
                    Log.l.debug ( "---- widget type : parameter = %s", object );
                    if ( (object != null ) && (object instanceof SimpleParameter) )
                    {
                        simpleParameter = (SimpleParameter) object;
                        simpleParameter.setValue ( widget.getValue().toString() );
                        Log.l.debug ( "---- widget type : new parameter = %s", simpleParameter );
                    }
                    else
                    {
                        str = Convert.concatObj ( "Обьект в виджете типов '%s' отсутствует или не является SimpleParameter.",c );
                        Log.l.error ( null, str );
                    }
                }
                else
                {
                    str = Convert.concatObj ( "Обьект '%s' не является AbstractWidget.",c );
                    Log.l.error ( null, str );
                }
            }

            // ----------------- Оcтальные параметры ------------------------

            cList = othersPanel.getComponents();
            for ( Component c : cList )
            {
                //Log.l.debug ( "-- Other panel : %s", c );
                if ( c instanceof AbstractWidget )
                {
                    widget = (AbstractWidget) c;
                    widget.setEditable ( false );
                    object  = widget.getObject();
                    Log.l.debug ( "---- FunctionParameter : %s", object );
                    if ( (object != null ) && (object instanceof FunctionParameter ) )
                    {
                        fParameter = (FunctionParameter) object;
                        fParameter.setValue ( widget.getValue() );
                        Log.l.debug ( "------ FunctionParameter 2 : value = '%s'; param = %s", widget.getValue(), fParameter );
                    }
                }
            }

            // ----------------- Локальные параметры ------------------------

            //if ( localePanel != null )
            //{
                cList = localePanel.getComponents ();
                for ( Component c : cList )
                {
                    //Log.l.debug ( "-- Other panel : %s", c );
                    if ( c instanceof AbstractWidget )
                    {
                        widget = ( AbstractWidget ) c;
                        widget.setEditable ( false );
                        object = widget.getObject ();
                        Log.l.debug ( "---- FunctionParameter : %s", object );
                        if ( ( object != null ) && ( object instanceof FunctionParameter ) )
                        {
                            fParameter = ( FunctionParameter ) object;
                            fParameter.setValue ( widget.getValue () );
                            Log.l.debug ( "------ FunctionParameter 2 : value = '%s'; param = %s", widget.getValue (), fParameter );
                        }
                    }
                }
            //}

            // - file
            parameter.setFileName ( fileWidget.getValue() );
            fileWidget.setEditable ( false );

        } catch ( Exception e )        {
            str = "Системная ошибка сохранения атрибутов Закладок конвертации в HTML в параметре Конвертации.";
            Log.l.error ( str, e );
            throw new WEditException ( e, str, " :\n", e );
        }
    }

    /**
     * Отмена редактирования.
     * <br/> Скинуть значения из параметров виджеты.
     * <br/> -- Разблокирует кнопку - Конвертить
     * <br/>
     * @param parameter
     * @throws WEditException
     */
    public void cancelBookmarkParams ( ConvertParameter parameter ) throws WEditException
    {
        Component[]         cList;
        AbstractWidget      widget;
        String              str;
        Object              object;
        Container           container;
        FunctionParameter   fParameter;
        SimpleParameter     simpleParameter;

        try
        {
            // Разблокируем кнопки
            getOkButton().setEnabled ( true );
            getEditButton().setEnabled ( true );
            getAddButton().setEnabled ( true );
            getDeleteButton().setEnabled ( true );

            // Блокируем кнопки
            getSaveButton().setEnabled ( false );
            getCancelButton().setEnabled ( false );

            /* todo -- потом сделать
            // --------------- Берем параметры из обьектов ---------------

            // - Элементы
            cList = elementsPanel.getComponents();
            Log.l.debug ( "- cList size = %d", cList.length );     // JPanel (BoxLayout)
            for ( Component c : cList )
            {
                Log.l.debug ( "-- Element panel : %s", c );     // JPanel (BoxLayout)
                if ( c instanceof Container )
                {
                    container = (Container) c;
                    for ( Component cc : container.getComponents() )
                    {
                        Log.l.debug ( "---- Element sub panel : %s", cc );     // StringFieldWidget
                        if ( cc instanceof AbstractWidget )
                        {
                            widget      = (AbstractWidget) cc;
                            // запрещаем редактирование виджетов
                            widget.setEditable ( false );
                            // скинуть значения из обьектов в виджеты
                            processValueToWidget ( widget );
                        }
                        else
                        {
                            str = Convert.concatObj ( "Обьект '%s' не является AbstractWidget.",cc );
                            Log.l.error ( null, str );
                        }
                    }
                }
            }

            // - Типы
            cList = typesPanel.getComponents();
            for ( Component c : cList )
            {
                Log.l.debug ( "-- Type panel : %s", c );       // BooleanWidget
                if ( c instanceof AbstractWidget )
                {
                    widget  = (AbstractWidget) c;
                    Log.l.debug ( "---- widget type : name = %s; value = %s", widget.getName(), widget.getValue() );
                    // запрещаем редактирование виджетов
                    widget.setEditable ( false );

                    object  = widget.getObject();
                    Log.l.debug ( "---- widget type : parameter = %s", object );
                    if ( (object != null ) && (object instanceof SimpleParameter) )
                    {
                        simpleParameter = (SimpleParameter) object;
                        simpleParameter.setValue ( widget.getValue().toString() );
                        Log.l.debug ( "---- widget type : new parameter = %s", simpleParameter );
                    }
                    else
                    {
                        str = Convert.concatObj ( "Обьект в виджете типов '%s' отсутствует или не является SimpleParameter.",c );
                        Log.l.error ( null, str );
                    }
                }
                else
                {
                    str = Convert.concatObj ( "Обьект '%s' не является AbstractWidget.",c );
                    Log.l.error ( null, str );
                }
            }

            // ----------------- Оcтальные параметры ------------------------

            cList = othersPanel.getComponents();
            for ( Component c : cList )
            {
                Log.l.debug ( "-- Other panel : %s", c );
                if ( c instanceof AbstractWidget )
                {
                    widget = (AbstractWidget) c;
                    widget.setEditable ( false );
                    object  = widget.getObject();
                    if ( (object != null ) && (object instanceof FunctionParameter ) )
                    {
                        fParameter = (FunctionParameter) object;
                        fParameter.setValue ( widget.getValue() );
                    }
                }
            }

            // - file
            parameter.setFileName ( fileWidget.getValue() );
            fileWidget.setEditable ( false );
            */

        } catch ( Exception e )        {
            str = "Системная ошибка сохранения атрибутов Закладок конвертации в HTML в параметре Конвертации.";
            Log.l.error ( str, e );
            throw new WEditException ( e, str, " :\n", e );
        }
    }

    private void processValueFromWidget ( AbstractWidget widget )
    {
        String          nameWidget, str;
        Object          object;
        BooleanParameter   booleanParameter;
        SimpleParameter     simpleParameter;
        ElementConvertParameter     elementParameter;
        FontWidget                  fontWidget;
        ComboBoxWidget<TitleViewMode>    formatWidget;

        try
        {
            nameWidget  = widget.getName();
            /*
               Имена
               1) Font_1 - level - шрифт для элемента - обьект Font
                */
            Log.l.debug ( "------ widget : name = %s; value = %s", nameWidget, widget.getValue () );     // StringFieldWidget
            object  = widget.getObject();
            if ( nameWidget != null )
            {
                if ( nameWidget.equals ( "ElementFont" ) )
                {
                    elementParameter = (ElementConvertParameter) object;
                    elementParameter.setFont ( widget.getValue() );
                    fontWidget       = (FontWidget) widget;
                    elementParameter.setColor ( fontWidget.getColor() );
                }
                else if ( nameWidget.equals ( "ElementFormat" ) )
                {
                    elementParameter = (ElementConvertParameter) object;
                    formatWidget     = (ComboBoxWidget<TitleViewMode>) widget;
                    elementParameter.setFormatType ( formatWidget.getValue () );
                }

                /*
                if ( object != null )
                {
                    if ( object instanceof SimpleParameter )
                    {
                        simpleParameter = (SimpleParameter) object;
                        simpleParameter.setValue ( widget.getValue().toString() );
                    }
                    else if ( object instanceof ElementConvertParameter )
                    {
                        elementParameter = (ElementConvertParameter) object;
                        elementParameter.setFont ( widget.getValue() );
                    }
                    else
                    {
                        str = Convert.concatObj ( "Обьект в виджете элементов '",widget,"' не является параметром." );
                        Log.l.error ( null, str );
                    }
                }
                else
                {
                    str = Convert.concatObj ( "Обьект в виджете элементов '",widget,"' отсутствует." );
                    Log.l.error ( null, str );
                }
                */
            }
        } catch ( Exception e )        {
            Log.l.error ( Convert.concatObj (  "widget = ", widget ), e);
        }
    }

    private void processValueToWidget ( AbstractWidget widget )
    {
        String          nameWidget, str;
        Object          object;
        BooleanParameter   booleanParameter;
        SimpleParameter     simpleParameter;
        ElementConvertParameter     elementParameter;
        FontWidget                  fontWidget;
        ComboBoxWidget<TitleViewMode>    formatWidget;

        try
        {
            nameWidget  = widget.getName();
            /*
               Имена
               1) Font_1 - level - шрифт для элемента - обьект Font
                */
            Log.l.debug ( "------ widget : name = %s; value = %s", nameWidget, widget.getValue() );     // StringFieldWidget
            // Взять параметр, который отвечает за данный виджет.
            object  = widget.getObject();

            if ( nameWidget != null )
            {
                if ( nameWidget.equals ( "ElementFont" ) )
                {
                    elementParameter = (ElementConvertParameter) object;
                    elementParameter.setFont ( widget.getValue() );
                    fontWidget       = (FontWidget) widget;
                    elementParameter.setColor ( fontWidget.getColor() );
                }
                else if ( nameWidget.equals ( "ElementFormat" ) )
                {
                    elementParameter = (ElementConvertParameter) object;
                    formatWidget     = (ComboBoxWidget<TitleViewMode>) widget;
                    elementParameter.setFormatType ( formatWidget.getValue () );
                }

                /*
                if ( object != null )
                {
                    if ( object instanceof SimpleParameter )
                    {
                        simpleParameter = (SimpleParameter) object;
                        simpleParameter.setValue ( widget.getValue().toString() );
                    }
                    else if ( object instanceof ElementConvertParameter )
                    {
                        elementParameter = (ElementConvertParameter) object;
                        elementParameter.setFont ( widget.getValue() );
                    }
                    else
                    {
                        str = Convert.concatObj ( "Обьект в виджете элементов '",widget,"' не является параметром." );
                        Log.l.error ( null, str );
                    }
                }
                else
                {
                    str = Convert.concatObj ( "Обьект в виджете элементов '",widget,"' отсутствует." );
                    Log.l.error ( null, str );
                }
                */
            }
        } catch ( Exception e )        {
            Log.l.error ( Convert.concatObj (  "widget = ", widget ), e);
        }
    }


    // Выдать текущий обьект..
    @Override
    public ConvertParameter getResult () throws WEditException
    {
        return listPanel.getSelectedItem();
    }

    public void addNewBookmark ( String name )
    {
        ConvertParameter cp;

        if ( name == null )  return;

        name = name.trim();
        if ( name.isEmpty() )  return;

        Log.l.info ( "[CREATE] localeParams = %s", localeParams );
        cp = createDefaultCp ( name );
        Log.l.info ( "[CREATE] cp = %s", cp );
        getBookmarkParameter().addNew ( cp );
        listPanel.addItem ( cp );
        listPanel.rewrite();
        // выбрать новый обьект
        listPanel.setSelectedItem ( cp );
    }

    public void addBookmark ( ConvertParameter cp )
    {
        if ( cp == null )  return;

        getBookmarkParameter().addNew ( cp );
        listPanel.addItem ( cp );
        listPanel.rewrite();
        // выбрать новый обьект
        listPanel.setSelectedItem ( cp );
    }

    private ConvertParameter createDefaultCp ( String name )
    {
        ConvertParameter                    cp;
        Collection<ElementConvertParameter> elementList;
        Collection<SimpleParameter>         typeList;
        ElementConvertParameter             element;
        SimpleParameter                     spType;

        cp = new ConvertParameter ( name );
        // Наполнить данными из контента книги

        elementList     = new LinkedList<ElementConvertParameter> ();
        for ( WBookElement bookElement : bookElementList )
        {
            element = bookmarkParameter.createElement ( bookElement );
            elementList.add ( element );
        }
        cp.setElementList ( elementList );

        typeList     = new TreeSet<SimpleParameter> ();
        // Цикл по всем реальным типам элементов
        for ( WType type : bookTypeList.values() )
        {
            spType = bookmarkParameter.createType ( type );
            //if ( type.getEnName().equals ( "hidden" ) )
            typeList.add ( spType );
        }
        cp.setTypeList ( typeList );

        // Locale
        if ( localeParams != null )  cp.setLocale ( localeParams );

        return cp;
    }

    public java.util.List<ConvertParameter> getBookmarkList ()
    {
        return listPanel.getObjectList ();
    }

    public ConvertParameter getCurrentBookmark ()
    {
        ConvertParameter result;

        result = listPanel.getSelectedItem();
        if ( result != null )
            getBookmarkParameter().setCurrentBookmark ( result.getName() );

        return result;
    }

    public BookmarksParameter getBookmarkParameter ()
    {
        //if ( bookmarkParameter == null )  bookmarkParameter = new BookmarksParameter ( "empty" );
        return bookmarkParameter;
    }

    public JButton getAddButton ()
    {
        return addButton;
    }

    public JButton getEditButton ()
    {
        return editButton;
    }

    public JButton getSaveButton ()
    {
        return saveButton;
    }

    public JButton getCancelButton ()
    {
        return cancelButton;
    }

    public JButton getDeleteButton ()
    {
        return deleteButton;
    }

    /**
     * проверяем наличие файла - если такой уже существует - запрос на перезапись.
     * Здесь имя файла уже провалидирвоалось - и на директорию, и т.д.
     * @return TRUE  - все ОК, можно работать дальше.
     */
    public boolean checkFile ()
    {
        boolean result;
        String  fileName;
        File    file;
        int     ic;

        Log.l.debug ( "Start.checkFile" );

        result = true;  // заранее отмечаем - можно псиать в этот файл - есть он или его нет.

        try
        {
            fileName = fileWidget.getValue();
            if ( fileName != null )
            {
                file     = new File ( fileName ); // имя файла здесь точно существует.
                // Проверяем - вдруг такой файл уже есть?
                if ( file.exists() )
                {
                    // Запрос
                    ic      = DialogTools.showConfirmDialog ( this, "Файл", "Файл '"+fileName+"' уже существует.\nПерезаписать ?" );
                    result  = ( ic == 0 );
                }
            }
        } catch ( Exception e )        {
            result = false;
            Log.l.error ( "Ошибка проверки существования файла.", e );
        }

        Log.l.debug ( "Finish.checkFile: result = %b", result );
        return result;
    }

}
