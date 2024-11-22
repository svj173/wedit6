package svj.wedit.v6.function.book.edit.desc;


import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.button.WButton;
import svj.wedit.v6.gui.dialog.WDialog;
import svj.wedit.v6.gui.list.WListPanel;
import svj.wedit.v6.gui.panel.EditablePanel;
import svj.wedit.v6.gui.panel.SimpleEditablePanel;
import svj.wedit.v6.gui.panel.WPanel;
import svj.wedit.v6.gui.renderer.INameRenderer;
import svj.wedit.v6.gui.tabs.TabsChangeListener;
import svj.wedit.v6.gui.tabs.TabsPanel;
import svj.wedit.v6.gui.widget.ComboBoxWidget;
import svj.wedit.v6.gui.widget.IntegerFieldWidget;
import svj.wedit.v6.gui.widget.font.FontWidget;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.WType;
import svj.wedit.v6.obj.book.*;
import svj.wedit.v6.obj.book.element.StyleName;
import svj.wedit.v6.obj.book.element.StyleType;
import svj.wedit.v6.obj.book.element.WBookElement;
import svj.wedit.v6.tools.BookStructureTools;
import svj.wedit.v6.tools.DialogTools;
import svj.wedit.v6.tools.GuiTools;
import svj.wedit.v6.tools.StyleTools;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Map;


/**
 * Диалог по созданию или редактированию описания всех Элементов текущей  книги.
 * <BR/>  Изменения происходят непосредственно в структуре книги.
 * <BR/> При инициализации, если список элементво в книге - пустой - брать дефолтный список.
 * <BR/>
 * <BR/> Структура диалога: три табика - элементы, типы элементов, атрибуты.
 * <BR/>
 * <BR/> Атрибуты:
 * <BR/> 1) Текст
 * <BR/> 2) Аннотация.
 * <BR/> 3) Метки
 * <BR/>
 * <BR/>  Структура табика Элементов.
 * <BR/> 1) Панель слева
 * <BR/> - вверху - минимальная вложенность элементов - по книге. (Ошибка: Кол-во вложенных элементво в книге "название" равно ...)
 * <BR/> - По-середине - список элементов.
 * <BR/> - внизу - кнопки работы со списокм - удалить, добавить, копировать, вставить...
 * <BR/> 2) В центре - параметры выбранного элемента
 * <BR/>
 * <BR/> При изменении списка - переустанавливать во всех элементах их уровни.
 * <BR/>
 * <BR/> Мин размер элементов книги - при удалениях элементов - ругаться что книга(название) содержит элементов...
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.08.2011 14:54:05
 */
public class BookEditElementsDialog extends WDialog<BookStructure, BookStructure>
{
    /* Панель отображения одного (выбранного) элемента описания книги. */
    private BookElementPanel            bookElementPanel;
    private BookContent                 bookContent;
    private WListPanel<WBookElement>    elementsList;
    private FontWidget                  textFontWidget, annFontWidget, labelFontWidget;
    private ComboBoxWidget<AlignType>   textAlignWidget, annAlignWidget, labelAlignWidget;
    private IntegerFieldWidget          textMarginWidget, annMarginWidget, labelMarginWidget;

    // type
    private TypePanel           typePanel;
    private WListPanel<WType>   typeList;


    public BookEditElementsDialog ( String title, BookContent bContent ) throws WEditException
    {
        super ( title );   // Редактировать описание структуры книги '...'.

        //this.bookContent    = bContent.clone();  // Правильно. Тогда и отмена пройдет.
        this.bookContent    = bContent;

        TabsPanel<EditablePanel>    tabsPanel;
        EditablePanel               tabPanel;
        String                      tabsName;
        TabsChangeListener          tabsListener;

        tabsPanel = new TabsPanel<EditablePanel>();
        tabsPanel.setTabPlacement ( JTabbedPane.TOP );
        tabsName    = bookContent.getId () +"_Tabs";
        tabsPanel.setName ( tabsName );
        tabsPanel.getAccessibleContext().setAccessibleName ( tabsName );

        tabPanel = createElementsPanel ( bookContent );
        tabsPanel.addPanel ( tabPanel, "Elements", "Элементы" );

        tabPanel = createTypesPanel ( bookContent );
        tabsPanel.addPanel ( tabPanel, "Types", "Типы элементов" );

        tabPanel = createAttrPanel ( bookContent );
        tabsPanel.addPanel ( tabPanel, "Attr", "Атрибуты" );

        // создать листенер выборки табиков
        tabsListener = new TabsChangeListener ( tabsName+"_TabsListener" );
        tabsPanel.setSelectTabsListener ( tabsListener );

        addToCenter ( tabsPanel );

        setOkButtonText ( "Принять изменения" );

        JButton button;
        button       = new JButton();
        button.setText ( "Установить 'по-умолчанию'" );
        button.setToolTipText ( "Принять стандартные значения - по-умолчанию." );
        button.addActionListener ( new ActionListener() {
            @Override
            public void actionPerformed ( ActionEvent evt) {
                try
                {
                    init ( BookStructureTools.getDefaultStructure() );
                } catch ( WEditException e )    {
                    Log.l.error ( "Error", e );
                    DialogTools.showError ( e.toString(), "Ошибка установки 'по-умолчанию'." );
                }
            }
        });

        addButtonFirst ( button );

        button       = new JButton();
        button.setText ( "Установить дефолтный размер текста (" + BookPar.TEXT_FONT_SIZE + ")" );
        button.setToolTipText ( "Принять размер текста" );
        button.addActionListener ( new ActionListener() {
            @Override
            public void actionPerformed ( ActionEvent evt) {
                    Font font, fontNew;
                    font = textFontWidget.getFont();
                    fontNew = new Font(font.getName(), font.getStyle(), BookPar.TEXT_FONT_SIZE);
                    textFontWidget.setValue(fontNew);

                    font = annFontWidget.getFont();
                    fontNew = new Font(font.getName(), font.getStyle(), BookPar.TEXT_FONT_SIZE);
                    annFontWidget.setValue(fontNew);

                    font = labelFontWidget.getFont();
                    fontNew = new Font(font.getName(), font.getStyle(), BookPar.TEXT_FONT_SIZE);
                    labelFontWidget.setValue(fontNew);
            }
        });

        addButtonFirst ( button );

        pack();
    }

    private EditablePanel createElementsPanel ( BookContent bookContent )
    {
        EditablePanel   result;
        JPanel          panel;

        result = new SimpleEditablePanel ();
        result.setLayout ( new BorderLayout(5,5) );

        bookElementPanel = new BookElementPanel();

        panel = createLeftElementsPanel ( bookContent, bookElementPanel );
        result.add ( panel, BorderLayout.WEST );

        bookElementPanel.setParentDialog ( this );
        result.add ( bookElementPanel, BorderLayout.CENTER );

        result.setName ( "Elements" );
        result.setId ( "Elements" );

        return result;
    }

    private JPanel createLeftElementsPanel ( BookContent bookContent, BookElementPanel bookElementPanel )
    {
        JPanel                   result, buttonPanel;
        Collection<WBookElement> bookElements;
        JLabel                   label;
        ActionEvent              actionEvent;
        ActionListener           actionListener;
        int                      minLevel;

        result = new JPanel();
        result.setLayout ( new BorderLayout ( 5,5 ) );

        // -------------- Мин глубина вложенности книги ---------------
        minLevel    = bookContent.getMaxLevel ();
        label       = new JLabel ( "  минимум: "+ minLevel );
        label.setBackground ( WCons.GRAY_1 );
        label.setToolTipText ( "Количество вложенных элементов в данной книге равно "+ minLevel +"." );
        result.add ( label, BorderLayout.NORTH );

        // ------------- Список элементов ------------------
        // Акция при смене элемента в списке
        actionEvent     = new ActionEvent ( this, 1, "select_new_element" );
        actionListener  = new ChangeElementEditListener ( bookElementPanel );
        elementsList    = new WListPanel<WBookElement> ( "Список описаний элементов", actionEvent, actionListener );
        bookElements    = bookContent.getBookStructure().getBookElements();
        if ( bookElements.isEmpty() )
        {
            bookContent.getBookStructure().setDefault();
            bookElements    = bookContent.getBookStructure().getBookElements();
        }
        // Заносим список полученный из описания книги. Значит, удаляя элементы из списка они сразу удалятся и в описании книги - ДА. (Т.е. не будет кнопок - Применить, Отменить. )
        Log.l.debug ( "--- createElementsPanel. source elements = %s", bookElements );
        // Клонируем - чтобы иметь возможность все отменить.
        bookElements = BookStructureTools.cloneElements ( bookElements );
        Log.l.debug ( "--- createElementsPanel. clone elements = %s", bookElements );
        elementsList.setCellRenderer ( new BookElementListRenderer() );
        elementsList.setBorder ( BorderFactory.createEtchedBorder () );
        elementsList.setList ( bookElements );
        result.add ( elementsList, BorderLayout.CENTER );

        // ------------------  кнопки ---------------------
        buttonPanel = createElementButtonPanel ( elementsList, minLevel );
        result.add ( buttonPanel, BorderLayout.SOUTH );

        // выбрать первый элемент в списке
        elementsList.setSelectedIndex ( 0 );

        return result;
    }

    private JPanel createLeftTypesPanel ( BookContent bookContent, TypePanel typePanel )
    {
        JPanel                   result, buttonPanel;
        Map<String,WType> types;
        Collection<WType> list;
        JLabel                   label;
        ActionEvent              actionEvent;
        ActionListener           actionListener;
        int                      minLevel;

        result = new JPanel();
        result.setLayout ( new BorderLayout ( 5,5 ) );

        // -------------- Мин глубина вложенности книги ---------------
        /*
        minLevel    = bookContent.getMaxLevel ();
        label       = new JLabel ( "  минимум: "+ minLevel );
        label.setBackground ( WCons.GRAY_1 );
        label.setToolTipText ( "Количество вложенных элементов в данной книге равно "+ minLevel +"." );
        result.add ( label, BorderLayout.NORTH );
        */

        // ------------- Список типов ------------------
        // Акция при смене элемента в списке
        actionEvent     = new ActionEvent ( this, 1, "select_new_type" );
        actionListener  = new ChangeTypeEditListener ( typePanel );
        typeList    = new WListPanel<WType> ( "Список типов элементов", actionEvent, actionListener );
        types    = bookContent.getBookStructure().getTypes ();
        if ( types.isEmpty() )
        {
            bookContent.getBookStructure().setDefaultTypes ();
            types    = bookContent.getBookStructure().getTypes ();
        }
        // Заносим список полученный из описания книги. Значит, удаляя элементы из списка они сразу удалятся и в описании книги - ДА. (Т.е. не будет кнопок - Применить, Отменить. )
        Log.l.debug ( "--- createLeftTypesPanel. source types = %s", types );
        // Клонируем - чтобы иметь возможность все отменить.
        list = BookStructureTools.cloneTypes ( types );
        Log.l.debug ( "--- createLeftTypesPanel. clone types = %s", list );
        typeList.setCellRenderer ( new ElementTypeListRenderer() );
        typeList.setBorder ( BorderFactory.createEtchedBorder() );
        typeList.setList ( list );
        result.add ( typeList, BorderLayout.CENTER );

        // ------------------  кнопки ---------------------
        buttonPanel = createTypeButtonPanel ( typeList );
        result.add ( buttonPanel, BorderLayout.SOUTH );

        // выбрать первый элемент в списке
        typeList.setSelectedIndex ( 0 );

        return result;
    }

    private EditablePanel createTypesPanel ( BookContent bookContent )
    {
        EditablePanel   result;
        JPanel          panel;

        result = new SimpleEditablePanel ();
        result.setLayout ( new BorderLayout(5,5) );

        typePanel = new TypePanel();

        panel = createLeftTypesPanel ( bookContent, typePanel );
        result.add ( panel, BorderLayout.WEST );

        //typePanel.setParentDialog ( this );
        result.add ( typePanel, BorderLayout.CENTER );

        result.setName ( "Types" );
        result.setId ( "Types" );

        return result;
    }

    private EditablePanel createAttrPanel ( BookContent bookContent )
    {
        EditablePanel   result;
        JPanel          panel, p;
        int             width;
        WEditStyle      textStyle, annStyle, labelStyle;
        Border          border;

        result = new SimpleEditablePanel();

        // ------------- Список атрибутов ------------------

        width   = 220;
        result.setLayout ( new BorderLayout ( 5, 5 ) );

        panel = new JPanel();
        panel.setLayout ( new BoxLayout ( panel, BoxLayout.PAGE_AXIS ) );

        // ---------------------------- TEXT -----------------------------------
        p = new JPanel();
        p.setLayout ( new BoxLayout ( p, BoxLayout.PAGE_AXIS ) );

        textStyle   = bookContent.getBookStructure().getTextStyle();
        Log.l.debug ( "--- textStyle = %s", textStyle );

        textFontWidget = new FontWidget ( "Шрифт" );
        textFontWidget.setTitleWidth ( width );
        textFontWidget.setColor ( textStyle.getColor() );
        textFontWidget.setValue ( textStyle.getFont() );
        p.add ( textFontWidget );

        textAlignWidget = new ComboBoxWidget<AlignType> ( "Положение", false, "", AlignType.values() );
        textAlignWidget.setTitleWidth ( width );
        textAlignWidget.setComboRenderer ( new INameRenderer() );
        textAlignWidget.setValue ( AlignType.getByNumber ( StyleConstants.getAlignment ( textStyle ) ) );
        p.add ( textAlignWidget );

        textMarginWidget = new IntegerFieldWidget ( "Смещение", true );
        textMarginWidget.setTitleWidth ( width );
        textMarginWidget.setToolTipText ( "Если align=LEFT - это смещение (в пробелах) слева от заголовка. Если align=RIGHT - это смещение справа от заголовка." );
        textMarginWidget.setValue ( ( int ) StyleConstants.getFirstLineIndent ( textStyle ) );
        p.add ( textMarginWidget );

        border  = GuiTools.createTitleBorder ( "Текст", WCons.GREEN_1, TitledBorder.CENTER );
        p.setBorder ( border );
        panel.add ( p );

        // ---------------------------- ANNOT -----------------------------------
        p = new JPanel();
        p.setLayout ( new BoxLayout ( p, BoxLayout.PAGE_AXIS ) );

        annStyle   = bookContent.getBookStructure().getAnnotationStyle();
        Log.l.debug ( "--- annStyle = %s", annStyle );

        annFontWidget = new FontWidget ( "Шрифт" );
        annFontWidget.setTitleWidth ( width );
        annFontWidget.setColor ( annStyle.getColor() );
        annFontWidget.setValue ( annStyle.getFont() );
        p.add ( annFontWidget );

        annAlignWidget = new ComboBoxWidget<AlignType> ( "Положение", false, "", AlignType.values() );
        annAlignWidget.setTitleWidth ( width );
        annAlignWidget.setComboRenderer ( new INameRenderer() );
        annAlignWidget.setValue ( AlignType.getByNumber ( StyleConstants.getAlignment ( annStyle ) ) );
        p.add ( annAlignWidget );

        annMarginWidget = new IntegerFieldWidget ( "Смещение", true );
        annMarginWidget.setTitleWidth ( width );
        annMarginWidget.setToolTipText ( "Если align=LEFT - это смещение (в пробелах) слева от заголовка. Если align=RIGHT - это смещение справа от заголовка." );
        annMarginWidget.setValue ( (int) StyleConstants.getFirstLineIndent ( annStyle )  );
        p.add ( annMarginWidget );

        border  = GuiTools.createTitleBorder ( "Аннотация", WCons.GREEN_1, TitledBorder.CENTER );
        p.setBorder ( border );
        panel.add ( p );

        // ---------------------------- Label -----------------------------------
        p = new JPanel();
        p.setLayout ( new BoxLayout ( p, BoxLayout.PAGE_AXIS ) );

        labelStyle   = bookContent.getBookStructure().getLabelStyle();
        Log.l.debug ( "--- labelStyle = %s", labelStyle );

        labelFontWidget = new FontWidget ( "Шрифт" );
        labelFontWidget.setTitleWidth ( width );
        labelFontWidget.setColor ( labelStyle.getColor() );
        labelFontWidget.setValue ( labelStyle.getFont() );
        p.add ( labelFontWidget );

        labelAlignWidget = new ComboBoxWidget<AlignType> ( "Положение", false, "", AlignType.values() );
        labelAlignWidget.setTitleWidth ( width );
        labelAlignWidget.setComboRenderer ( new INameRenderer() );
        labelAlignWidget.setValue ( AlignType.getByNumber ( StyleConstants.getAlignment ( labelStyle ) ) );
        p.add ( labelAlignWidget );

        labelMarginWidget = new IntegerFieldWidget ( "Смещение", true );
        labelMarginWidget.setTitleWidth ( width );
        labelMarginWidget.setToolTipText ( "Если align=LEFT - это смещение (в пробелах) слева от заголовка. Если align=RIGHT - это смещение справа от заголовка." );
        labelMarginWidget.setValue ( (int) StyleConstants.getFirstLineIndent ( labelStyle )  );
        p.add ( labelMarginWidget );

        border  = GuiTools.createTitleBorder ( "Метка", WCons.GREEN_1, TitledBorder.CENTER );
        p.setBorder ( border );
        panel.add ( p );


        // ------------------------------------------------------------
        result.add ( panel, BorderLayout.NORTH );

        // пустышка
        result.add ( new JLabel ( " " ), BorderLayout.CENTER );

        //result.add ( new JLabel("Атрибуты") );
        result.setName ( "Attr" );
        result.setId ( "Attr" );

        return result;
    }


    private JPanel createElementButtonPanel ( WListPanel<WBookElement> elementsList, int minLevel )
    {
        WPanel          result;
        JPanel          buttonPanel;
        WButton         button;
        int             buttonWidth;
        ActionListener  listener;

        buttonWidth = 150;

        result = new WPanel ( 5,5,5,5 );
        result.setBorder ( BorderFactory.createEtchedBorder() );

        buttonPanel = new JPanel();
        result.add ( buttonPanel, BorderLayout.NORTH );

        // listener
        listener = new ProcessElementListListener ( elementsList, minLevel );

        //buttonPanel.setLayout ( new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS) );
        buttonPanel.setLayout ( new GridLayout ( 4, 1, 5, 5 ) );

        button  = GuiTools.createButton ( "Добавить", "Добавить новый, последним", WCons.IMG_B_ADD, buttonWidth, listener, "ADD" );
        buttonPanel.add ( button );

        button  = GuiTools.createButton ( "Удалить", "Удалить", WCons.IMG_B_DELETE, buttonWidth, listener, "DELETE" );
        buttonPanel.add ( button );

        button  = GuiTools.createButton ( "Копировать", "Копировать в буфер", WCons.IMG_B_COPY, buttonWidth, listener, "COPY" );
        buttonPanel.add ( button );

        button  = GuiTools.createButton ( "Вставить", "Вставить из буфера последним", WCons.IMG_B_PASTE, buttonWidth, listener, "PASTE" );
        buttonPanel.add ( button );

        // большая пустышка
        result.add ( new JLabel(" "), BorderLayout.CENTER );

        return result;
    }

    private JPanel createTypeButtonPanel ( WListPanel<WType> tList )
    {
        WPanel          result;
        JPanel          buttonPanel;
        WButton         button;
        int             buttonWidth;
        ActionListener  listener;

        buttonWidth = 150;

        result = new WPanel ( 5,5,5,5 );
        result.setBorder ( BorderFactory.createEtchedBorder() );

        buttonPanel = new JPanel();
        result.add ( buttonPanel, BorderLayout.NORTH );

        // listener
        listener = new ProcessTypeListListener ( tList );

        //buttonPanel.setLayout ( new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS) );
        buttonPanel.setLayout ( new GridLayout ( 4, 1, 5, 5 ) );

        button  = GuiTools.createButton ( "Добавить", "Добавить новый, последним", WCons.IMG_B_ADD, buttonWidth, listener, "ADD" );
        buttonPanel.add ( button );

        button  = GuiTools.createButton ( "Удалить", "Удалить", WCons.IMG_B_DELETE, buttonWidth, listener, "DELETE" );
        buttonPanel.add ( button );

        button  = GuiTools.createButton ( "Копировать", "Копировать в буфер", WCons.IMG_B_COPY, buttonWidth, listener, "COPY" );
        buttonPanel.add ( button );

        button  = GuiTools.createButton ( "Вставить", "Вставить из буфера последним", WCons.IMG_B_PASTE, buttonWidth, listener, "PASTE" );
        buttonPanel.add ( button );

        // большая пустышка
        result.add ( new JLabel(" "), BorderLayout.CENTER );

        return result;
    }

    protected void createDialogSize ()
    {
        /*
        int  width, height;

        width       = Par.SCREEN_SIZE.width / 2 + Par.SCREEN_SIZE.width / 4;
        height      = Par.SCREEN_SIZE.height / 2 + Par.SCREEN_SIZE.height / 4;
        setPreferredSize ( new Dimension (width,height) );
        setSize ( width, height );

        pack();
        */
    }

    @Override
    public void doClose ( int closeType )
    {
    }

    @Override
    public void init ( BookStructure bookStructure ) throws WEditException
    {
        WEditStyle style;

        elementsList.setList ( bookStructure.getBookElements() );

        // todo Необходимо переопределить список в  ProcessTypeListListener
        typeList.setList ( BookStructureTools.cloneTypes ( bookStructure.getTypes() ) );

        // text
        style   = bookStructure.getTextStyle();
        //Log.l.debug ( "--- textStyle = %s", style );
        textFontWidget.setColor ( style.getColor() );
        textFontWidget.setValue ( style.getFont() );
        textAlignWidget.setValue ( AlignType.getByNumber ( StyleConstants.getAlignment ( style ) ) );
        textMarginWidget.setValue ( ( int ) StyleConstants.getFirstLineIndent ( style ) );

        // annot
        style   = bookStructure.getAnnotationStyle();
        //Log.l.debug ( "--- annStyle = %s", style );
        annFontWidget.setColor ( style.getColor() );
        annFontWidget.setValue ( style.getFont() );
        annAlignWidget.setValue ( AlignType.getByNumber ( StyleConstants.getAlignment ( style ) ) );
        annMarginWidget.setValue ( (int) StyleConstants.getFirstLineIndent ( style )  );

        // label
        style   = bookStructure.getLabelStyle();
        //Log.l.debug ( "--- labelStyle = %s", style );
        labelFontWidget.setColor ( style.getColor () );
        labelFontWidget.setValue ( style.getFont () );
        labelAlignWidget.setValue ( AlignType.getByNumber ( StyleConstants.getAlignment ( style ) ) );
        labelMarginWidget.setValue ( (int) StyleConstants.getFirstLineIndent ( style )  );
    }

    @Override
    public BookStructure getResult() throws WEditException
    {
        BookStructure   result;
        WEditStyle      style;

        // Скидываем в обьекты последние гуи-изменения (т.е. страница, на которой нажали Применить.
        // - Elements
        bookElementPanel.fromWidgetsToElement();
        // - Types
        typePanel.fromWidgetsToElement();

        //result = new BookStructure();
        result = bookContent.getBookStructure();

        // элементы
        result.setElements ( elementsList.getObjectList() );
        Log.l.debug ( "--- createElements. new elements = %s", result.getBookElements () );

        // типы
        result.setTypes ( typeList.getObjectList() );
        // Переустановить ТИП по-умочанию. -- in setTypes
        //result.reinitDefaultType();

        // атрибуты
        // StyleType styleType, String styleName, Color color, Font font, int firstLineMargin, int alignType
        // - text
        style   = StyleTools.createStyle ( StyleType.TEXT, null, textFontWidget.getColor(), textFontWidget.getFont(), textMarginWidget.getValue(),
                                           textAlignWidget.getValue().getNumber() );
        result.setTextStyle ( style );

        // - ann
        style   = StyleTools.createStyle ( StyleType.ANNOTATION, StyleName.ANNOTATION, annFontWidget.getColor(), annFontWidget.getFont(), annMarginWidget.getValue(),
                                           annAlignWidget.getValue().getNumber() );
        result.setAnnotationStyle ( style );

        // - label
        style   = StyleTools.createStyle ( StyleType.TEXT, StyleName.LABEL, labelFontWidget.getColor(), labelFontWidget.getFont(), labelMarginWidget.getValue(),
                                           labelAlignWidget.getValue().getNumber() );
        result.setLabelStyle ( style );

        return result;
    }

    public WBookElement getCurrentElement ()
    {
        return elementsList.getSelectedItem();
    }

    public WType getCurrentType ()
    {
        return typeList.getSelectedItem();
    }

}
