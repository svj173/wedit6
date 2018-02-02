package svj.wedit.v6.function.text;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.text.action.RightMarginAction;
import svj.wedit.v6.gui.renderer.ActionRenderer;
import svj.wedit.v6.gui.widget.ComboBoxWidget;
import svj.wedit.v6.obj.function.SimpleFunction;
import svj.wedit.v6.obj.book.WEditStyle;

import javax.swing.*;
import javax.swing.text.StyledEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.LinkedList;


/**
 * Функция позволяет задать выделенному тексту стиль - цвет, шрифт, размер и т.д.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 03.11.2012 15:07:16
 */
public class SelectStyleFunction extends SimpleFunction
{
    private  final ComboBoxWidget<Action> alignWidget;


    public SelectStyleFunction ()
    {
        Action action;

        setId ( FunctionId.TEXT_SELECT_STYLE );
        setName ( "Задать стиль." );

        Collection<Action> actionVector = new LinkedList<Action> ();

        // ------------ Правый отступ  ------------

        action  = new RightMarginAction ( "1",1 );
        action.putValue(Action.NAME, "1");
        actionVector.add ( action );

        action  = new RightMarginAction ( "3",3 );
        action.putValue(Action.NAME, "3");
        actionVector.add ( action );

        action  = new RightMarginAction ( "5",5 );
        action.putValue(Action.NAME, "5");
        actionVector.add ( action );

        // ------------

        action  = new StyledEditorKit.BoldAction();
        action.putValue(Action.NAME, "Bold");
        actionVector.add ( action );

        action  = new StyledEditorKit.ItalicAction();
        action.putValue(Action.NAME, "Italic");
        actionVector.add ( action );

        action  = new StyledEditorKit.UnderlineAction();
        action.putValue(Action.NAME, "Underline");
        actionVector.add ( action );

        // --------  Размер букв ------------

        action  = new StyledEditorKit.FontSizeAction("12", 12);
        action.putValue(Action.NAME, "12");
        actionVector.add ( action );

        action  = new StyledEditorKit.FontSizeAction("14", 14);
        action.putValue(Action.NAME, "14");
        actionVector.add ( action );

        action  = new StyledEditorKit.FontSizeAction("18", 18);
        action.putValue(Action.NAME, "18");
        actionVector.add ( action );

        // ------------ Шрифты ------------

        String[] sh = new String[] { "Serif", "SansSerif", "Dialog", "Courier", "Monospaced"};
        for ( String s : sh )
        {
            action  = new StyledEditorKit.FontFamilyAction ( s, s );
            action.putValue ( Action.NAME, s );
            actionVector.add ( action );
        }
        /*
        action  = new StyledEditorKit.FontFamilyAction("Serif", "Serif");
        action.putValue(Action.NAME, "Serif");
        actionVector.add ( action );

        action  = new StyledEditorKit.FontFamilyAction("SansSerif", "SansSerif");
        action.putValue(Action.NAME, "SansSerif");
        actionVector.add ( action );
        */

        // ------------ Цвет букв ------------

        action  = new StyledEditorKit.ForegroundAction("Red", Color.red);
        action.putValue(Action.NAME, "Red");
        actionVector.add ( action );

        action  = new StyledEditorKit.ForegroundAction("Green", Color.green );
        action.putValue(Action.NAME, "Green");
        actionVector.add ( action );

        action  = new StyledEditorKit.ForegroundAction("Blue", Color.blue );
        action.putValue(Action.NAME, "Blue");
        actionVector.add ( action );

        action  = new StyledEditorKit.ForegroundAction("Black", Color.black );
        action.putValue(Action.NAME, "Black");
        actionVector.add ( action );

        alignWidget = new ComboBoxWidget<Action> ( null, true, "- стиль -", actionVector );
        alignWidget.initAction ( this, "Style" );
        alignWidget.setComboRenderer ( new ActionRenderer() );
        alignWidget.setVisible ( true );
    }

    /* выбран элемент из списка элементов - навесить данный стиль на выбранный текст. */
    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        Action action;

        action = alignWidget.getValue();
        action.actionPerformed ( event );
        // todo изменить имя стиля на color_text
    }

    public JComponent getGuiComponent ( int height )
    {
        //Log.l.debug ( "Start. height = ", height );

        //styleWidget.setValueHeight ( height );

        return alignWidget;
    }

    /* Искать в текущем тексте тип его стиля. */
    @Override
    public void rewrite ()
    {
        /*
        TreePanel<BookContent>      bookPanel;
        BookContent                 bookContent;
        Collection<WEditStyle>      styles;
        WEditStyle                  style;

        Log.l.debug ( "Start" );

        // Взять текущую книгу - TreePanel
        bookPanel       = Par.GM.getFrame().getCurrentBookContentPanel();
        if ( bookPanel == null )  return;

        bookContent     = bookPanel.getObject();
        styles          = bookContent.getBookStructure().getStyles();

        // наполнить выпадашку новыми значениями.
        Log.l.debug ( "styles for element widget = ", styles );
        styleWidget.setValues ( styles );
        styleWidget.repaint();
        styleWidget.revalidate();
        styleWidget.getGuiComponent().repaint();
        styleWidget.getGuiComponent().revalidate();


        // Опросить текстовую панель - какой стиль сейчас под маркером.
        style   = getCurrentStyle ();
        // Выбрать данный стиль в выпадашке.
        styleWidget.setValue ( style ); // null - выбрать первый в списке.
        */
    }

    private WEditStyle getCurrentStyle ()
    {
        // todo
        /*
        Chapter chapter;
        AbstractDocument doc;
        int ic;
        Position pos;
        EditorKit editor;
        StyledEditorKit se;
        AttributeSet attr;
        SimpleAttributeSet sattr;
        Object      obj;

        // Получить соотвествующую часть
        chapter = getGm().getChapterManager().getChapter ( cmd );
        // Получить документ
        doc     = chapter.getDocument ();
        //pos     = doc.getStartPosition ();    // = 0
        editor  = chapter.getEditorKit ();
        se  = (StyledEditorKit) editor;
        logger.debug ( "editor = " + se );
        // Взяли атрибуты текущей точки текста
        attr    = se.getInputAttributes ();
        logger.debug ( "attr = " + attr );
        // Получить атрибут styleName
        obj = attr.getAttribute ( WCons.STYLE_NAME );
        logger.debug ( "obj = " + obj );

        sattr   = (SimpleAttributeSet) attr;
        Enumeration en  = sattr.getAttributeNames ();
        while ( en.hasMoreElements () )
        {
            obj = en.nextElement ();
            logger.debug ( " - attr name = " + obj + ", class = " + obj.getClass ().getName () );
        }
        */
        return null;
    }

}
