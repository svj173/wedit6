package svj.wedit.v6.function.text;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.gui.renderer.AttributeSetRenderer;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.gui.widget.ComboBoxWidget;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.WEditStyle;
import svj.wedit.v6.obj.book.element.StyleName;
import svj.wedit.v6.obj.function.SimpleFunction;
import svj.wedit.v6.tools.DumpTools;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.LinkedList;


/**
 * Функция позволяет задать выделенному тексту стиль элемента (заголовок, аннотация, текст и т.д.)
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 29.05.2012 13:07:16
 */
public class SelectElementFunction  extends SimpleFunction
{
    private  final ComboBoxWidget<WEditStyle> styleWidget;
    private  final TextAction textAction;
    /* Флаг смены книги - тогда обновляем выпадашку стилей. */
    private  BookContent      oldBookContent;



    /*
    Изначально создаем выпадашку-пустышку, а внутри виджета меняем список в зависимости от выбранной книги.
    Смена данных в выпадашке - по rewrite;
     */
    public SelectElementFunction ()
    {
        setId ( FunctionId.TEXT_SELECT_ELEMENT );
        setName ( "Задать стиль тексту." );

        styleWidget = new ComboBoxWidget<WEditStyle> ( null, true, "-- элемент --", new LinkedList<WEditStyle>() );
        styleWidget.initAction ( this, "Element" );
        styleWidget.setComboRenderer ( new AttributeSetRenderer() );
        //styleWidget.setMaximumRowCount(10);
        styleWidget.setVisible ( true );
        
        textAction  = new TextAction ( "textAction" );

        oldBookContent = null;
    }

    /* Акция из выпадашки - выбран элемент из списка элементов - в выпадашке вверху над текстовой панелью - навесить данный стиль на выбранный текст.
    *  В event.source - JEditorPane
    */
    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        WEditStyle  style;

        Log.l.debug ( "Start. Select event = %s", event );
        // Event = java.awt.event.ActionEvent[
        // - ACTION_PERFORMED,
        // - cmd = Element,when=1398309922893,modifiers=Button1] on
        // - source = javax.swing.JComboBox[ selectedItemReminder=-- элемент --]

        style = styleWidget.getValue();

        Log.l.debug ( "SelectElement: set new style = %s", DumpTools.printAttributeSet ( style ) );
        // делать ограничение - только не Книга?
        textAction.setStyle ( style, event );

        // todo Снять выделение в тексте

        // Переключить выпадашку стилей в первую позицию - нейтральную
        styleWidget.setIndex ( 0 );
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
        TreePanel<BookContent>      bookPanel;
        BookContent                 bookContent;
        Collection<WEditStyle>      styles;

        Log.l.debug ( "Start" );

        // Взять текущую книгу - TreePanel
        bookPanel       = Par.GM.getFrame().getCurrentBookContentPanel();
        if ( bookPanel == null )  return;

        bookContent     = bookPanel.getObject();
        if ( (oldBookContent == null) || ( ! oldBookContent.equals ( bookContent ) ) )
        {
            oldBookContent = bookContent;
            // Обновить выпадашку
            //updateWidgets ( bookContent );
            styles          = bookContent.getBookStructure().getStyles();
            Log.l.debug ( "SelectElement: get new styles = %s", styles );

            // наполнить выпадашку новыми значениями.
            //Log.l.debug ( "styles for element widget = ", styles );
            styleWidget.setValues ( styles );
            styleWidget.setMaximumRowCount ( styles.size()+1 );
            styleWidget.repaint();
            styleWidget.revalidate();
            styleWidget.getGuiComponent().repaint();
            styleWidget.getGuiComponent().revalidate();
        }

        /*
        // Опросить текстовую панель - какой стиль сейчас под маркером.
        style   = getCurrentStyle();
        // Выбрать данный стиль в выпадашке.
        styleWidget.setValue ( style ); // null - выбрать первый в списке.
        */
    }

    /*
    public void setCurrentStyle ( WEditStyle style )
    {
        styleWidget.setValue ( style ); // null - выбрать первый в списке.
    }
    */

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
        Log.l.debug ( "----- current text style attr class = %s", attr.getClass().getName() ); // javax.swing.text.StyledEditorKit$1 - subclass

        // Получить атрибут styleName
        obj     = attr.getAttribute ( StyleName.STYLE_NAME );
        Log.l.debug ( "----- style name = ", obj );

        if ( obj != null )
        {
            // StyleType styleType, String styleName
            result = new WEditStyle ( null, obj.toString() );
        }
        else
        {
            // неизвестный стиль - значит это какой-то текст - берем стиль текста
            result  = Par.GM.getFrame().getCurrentBookContentPanel().getObject().getBookStructure().getTextStyle();
        }

        return result;
    }

    /**
     * Установить в выпадашке стиль, соовтетсвующий стилю в тексте (по его имени). -- дергается по клику мыши (там сначала определяется имя стиля в текстовой панели).
     * @param styleName   Имя стиля в тексте на текущей позиции курсора.
     */
    public void setCurrentStyle ( String styleName )
    {
        TreePanel<BookContent> bookPanel;
        BookContent            bookContent;
        Collection<WEditStyle> styles;
        WEditStyle             style;
        boolean                b;

        if ( styleName != null )
        {
            // ------------------------- Взять список стилей ------------------------
            // По идее - список надо брать из выпадашки.
            styles          = styleWidget.getValues();
            //styles          = bookContent.getBookStructure().getStyles();
            //Log.l.debug ( "----- get styles from widget = %s", styles );

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
        }
        else
        {
            style = null;
        }

        /*
        if ( style == null )
        {
            // Ищем стиль по-умолчанию дял текущей книги.
            // - Взять текущую книгу - TreePanel
            bookPanel       = Par.GM.getFrame().getCurrentBookContentPanel();
            if ( bookPanel == null )  return;

            bookContent     = bookPanel.getObject();

            style = bookContent.getBookStructure().getTextStyle();
        }
        //Log.l.debug ( "----- result style = ", style );

        // Выбрать данный стиль в выпадашке.
        styleWidget.setValue ( style ); // null - выбрать первый в списке.
        */
        if ( style == null )
            styleWidget.setIndex ( 0 );
        else
            styleWidget.setValue ( style );
    }

}
