package svj.wedit.v6.function.text;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.gui.widget.StringFieldWidget;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.WEditStyle;
import svj.wedit.v6.obj.book.element.StyleName;
import svj.wedit.v6.obj.book.element.StyleType;
import svj.wedit.v6.obj.function.SimpleFunction;
import svj.wedit.v6.tools.DumpTools;
import svj.wedit.v6.tools.StyleTools;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import java.awt.event.ActionEvent;


/**
 * Функция позволяет задать выделенному тексту стиль элемента (заголовок, аннотация, текст и т.д.)
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 29.05.2012 13:07:16
 */
public class InfoElementTypeFunction extends SimpleFunction
{
    /* Выпадашка - индикатор текущего стиля. */
    private  final StringFieldWidget styleWidget;


    /*
    Изначально создаем выпадашку-пустышку, а внутри виджета меняем список в зависимости от выбранной книги.
    Смена данных в выпадашке - по rewrite;
     */
    public InfoElementTypeFunction ()
    {
        setId ( FunctionId.TEXT_INFO_ELEMENT );
        setName ( "Показать стиль текста." );

        //styleWidget = new ComboBoxWidget<WEditStyle> ( null, true, "-- элемент --", new LinkedList<WEditStyle>() );
        styleWidget = new StringFieldWidget ( null, "" );
        //styleWidget.initAction ( this, "Element" );
        //styleWidget.setComboRenderer ( new AttributeSetRenderer() );
        styleWidget.setVisible ( true );
        styleWidget.setEditable ( false );
        styleWidget.setValueWidth ( 100 );
    }

    /* Акция из выпадашки - выбран элемент из списка элементов - в выпадашке вверху над текстовой панелью - навесить данный стиль на выбранный текст. */
    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
    }

    public JComponent getGuiComponent ( int height )
    {
        Log.l.debug ( "Start. height = %s", height );

        //styleWidget.setValueHeight ( height );

        return styleWidget;
    }

    // Здесь выясняется, какой стиль находится под текущим курсором и отмечается в выпадашке.
    @Override
    public void rewrite ()
    {
        WEditStyle style;
        String     styleName;

        Log.l.debug ( "InfoElement: Start." );

        styleWidget.repaint();
        styleWidget.revalidate();
        styleWidget.getGuiComponent().repaint();
        styleWidget.getGuiComponent().revalidate();


        // Опросить текстовую панель - какой стиль сейчас под маркером.
        style   = getCurrentStyle();
        Log.l.debug ( "InfoElement: current text style = %s", style );
        if ( style == null )
            styleName = "text";
        else
            styleName = style.getStyleName();

        // Выбрать данный стиль в выпадашке.
        styleWidget.setValue ( styleName ); // null - выбрать первый в списке.
    }

    /**
     * Узнать стиль текущего текста (где курсор) в текущей текстовой панели.
     * @return Стиль согласно его имени в градации WEdit6, иначе стиль рабочего текста (если имя стиля не задано).
     */
    private WEditStyle getCurrentStyle ()
    {
        TextPanel       textPanel;
        StyledDocument  doc;
        StyledEditorKit editor;
        AttributeSet    attr;
        Object          obj;
        WEditStyle      result;

        textPanel   = Par.GM.getFrame().getCurrentTextPanel();
        if ( textPanel == null )  return null;
        doc         = textPanel.getDocument();
        if ( doc == null )  return null;

        editor  = (StyledEditorKit) textPanel.getEditor();

        // Взяли атрибуты текущей точки текста
        attr    = editor.getInputAttributes();
        Log.l.debug ( "----- current text style attr = %s", attr );
        Log.l.debug ( "----- current text style attr (dump) = %s", DumpTools.printAttributeSet ( attr ) );
        Log.l.debug ( "----- current text style attr class = %s", attr.getClass().getName() ); // javax.swing.text.StyledEditorKit$1 - subclass

        // Получить атрибут styleName -- здесь StyledEditorKit$1 если не находит такой атрибут - выдает корневой. Это нам не подходит.
        //obj     = attr.getAttribute ( StyleName.STYLE_NAME );
        obj     = StyleTools.getAttribute ( attr, StyleName.STYLE_NAME );
        Log.l.debug ( "----- style name = %s", obj );

        if ( obj != null )
        {
            // StyleType styleType, String styleName
            result = new WEditStyle ( null, obj.toString() );
        }
        else
        {
            // неизвестный стиль - значит это какой-то текст - берем стиль текста текущей книги - если она есть.
            result = new WEditStyle ( StyleType.TEXT, StyleType.TEXT.getName() );
            TreePanel<BookContent> currentBookContentPanel = Par.GM.getFrame().getCurrentBookContentPanel();
            if ( currentBookContentPanel != null )
            {
                BookContent bookContent = currentBookContentPanel.getObject();
                if ( bookContent != null )
                {
                    result  = bookContent.getBookStructure().getTextStyle();
                }
            }
        }

        return result;
    }

    /**
     * Установить в выпадашке стиль, соовтетсвующий стилю в тексте (по его имени). -- дергается по клику мыши (там сначала определяется имя стиля в текстовой панели).
     * @param styleName   Имя стиля в тексте на текущей позиции курсора.
     */
    public void setCurrentStyle ( String styleName )
    {
        //Log.l.debug ( "Start" );

        /*
        TreePanel<BookContent> bookPanel;
        BookContent                 bookContent;
        Collection<WEditStyle> styles;
        WEditStyle                  style;
        boolean                     b;

        // - Взять текущую книгу - TreePanel
        bookPanel       = Par.GM.getFrame().getCurrentBookContentPanel();
        if ( bookPanel == null )  return;

        bookContent     = bookPanel.getObject();

        // ------------------------- Взять список стилей ------------------------
        // По идее - список надо брать из выпадашки.
        styles          = styleWidget.getValues();
        //styles          = bookContent.getBookStructure().getStyles();
        Log.l.debug ( "----- get styles from widget = ", styles );

        // искать стиль по его имени
        style           = null;
        for ( WEditStyle st : styles )
        {
            //Log.l.debug ( "--- style = ", st );
            b = st.isThisStyle ( styleName );
            //Log.l.debug ( "----- eq with styleName = ", styleName, "; b = ", b );
            if ( b )
            {
                style = st;
                break;
            }
        }

        if ( style == null )  style = bookContent.getBookStructure().getTextStyle();
        //Log.l.debug ( "----- result style = ", style );

        // Выбрать данный стиль в выпадашке.
        styleWidget.setValue ( style ); // null - выбрать первый в списке.
        */

        //if ( (styleName == null) || styleName.isEmpty() )   styleName = "text";

        styleWidget.setValue ( styleName );
    }


}
