package svj.wedit.v6.function.text;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.renderer.ActionRenderer;
import svj.wedit.v6.gui.widget.ComboBoxWidget;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.function.SimpleFunction;
import svj.wedit.v6.obj.book.WEditStyle;
import svj.wedit.v6.obj.book.element.StyleName;
import svj.wedit.v6.obj.book.element.StyleType;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.LinkedList;


/**
 * Функция позволяет задать выделенному тексту форматирование - влево, вправо, формат.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 29.05.2012 13:07:16
 */
public class SelectAlignFunction extends SimpleFunction
{
    private  final ComboBoxWidget<Action> alignWidget;


    public SelectAlignFunction ()
    {
        Action action;

        setId ( FunctionId.TEXT_SELECT_ALIGN );
        setName ( "Задать выравнивание." );

        Collection<Action> actionVector = new LinkedList<Action> ();

        action  = new StyledEditorKit.AlignmentAction("Left", StyleConstants.ALIGN_LEFT );
        action.putValue(Action.NAME, "Left");
        actionVector.add ( action );

        action  = new StyledEditorKit.AlignmentAction("Center", StyleConstants.ALIGN_CENTER );
        action.putValue(Action.NAME, "Center");
        actionVector.add ( action );

        action  = new StyledEditorKit.AlignmentAction("Right", StyleConstants.ALIGN_RIGHT );
        action.putValue(Action.NAME, "Right");
        actionVector.add ( action );

        action  = new StyledEditorKit.AlignmentAction("Justify", StyleConstants.ALIGN_JUSTIFIED ); 
        action.putValue(Action.NAME, "Justify");
        actionVector.add ( action );

        alignWidget = new ComboBoxWidget<Action> ( null, true, "- формат -", actionVector );
        alignWidget.initAction ( this, "Align" );
        alignWidget.setComboRenderer ( new ActionRenderer() );
        alignWidget.setVisible ( true );
    }

    /* выбран элемент из списка элементов - навесить данный стиль на выбранный текст. */
    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        Object obj, o;
        Action action;
        int    ic, elementIndex, iEnd;
        AttributeSet attr;
        JEditorPane editor;
        Document doc;
        SimpleAttributeSet sStyle;

        obj = alignWidget.getValue();
        if ( obj instanceof Action )
        {
            action  = (Action) obj;
            action.actionPerformed ( event );

            // todo Изменить имя стиля у выделенного текста -- Не получается. Нет нашего стиля в обьекте Document
            // - Взять текущую текстовую панель
            editor  = Par.GM.getFrame().getTextTabsPanel().getSelectedComponent().getTextPane();
            doc     = Par.GM.getFrame().getTextTabsPanel().getSelectedComponent().getDocument();
            ic              = editor.getSelectionStart ();
            iEnd            = editor.getSelectionEnd ();
            Log.l.debug ( "ic start selected = %d", ic );
            elementIndex    = doc.getDefaultRootElement().getElementIndex ( ic );
            Log.l.debug ( "elementIndex for start selected = %d", elementIndex );
            attr            = doc.getDefaultRootElement().getElement ( elementIndex ).getAttributes();
            //attr.
            Log.l.debug ( "attribute = %s", attr );
            // Создать новый стиль на основе имеющегося.
            //sStyle = new SimpleAttributeSet ( attr );
            sStyle = new WEditStyle ( attr, StyleType.COLOR_TEXT, StyleName.STYLE_NAME );
            sStyle.addAttribute ( StyleName.STYLE_NAME, StyleType.COLOR_TEXT.getName() );
            StyledDocument sd      = (StyledDocument) doc;
            //sd.setParagraphAttributes ( ic, iEnd - ic, sStyle, true ); // false  - имеющиеся атрибуты стиля НЕ затирать.
            sd.setCharacterAttributes ( ic, iEnd - ic, sStyle, true ); // false  - имеющиеся атрибуты стиля НЕ затирать.

            /*
            o = JTextComponent.getFocusedComponent();
            //o = event.getSource();   // JComboBox
            JEditorPane editor = (JEditorPane) o;
            if (editor != null)
            {
                ic              = editor.getSelectionStart();
                Log.l.debug ( "ic start selected = ", ic );
                elementIndex    = editor.getDocument().getDefaultRootElement().getElementIndex ( ic );
                Log.l.debug ( "elementIndex for start selected = ", elementIndex );
                attr            = editor.getDocument().getDefaultRootElement().getElement ( elementIndex ).getAttributes();
                //attr.
                Log.l.debug ( "attribute = ", attr );
            }
            */
        }
    }

    public JComponent getGuiComponent ( int height )
    {
        Log.l.debug ( "Start. height = ", height );

        //styleWidget.setValueHeight ( height );

        return alignWidget;
    }

    /* Искать в текущем тексте тип его форматирвоания и отметить в выпадашке. */
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

    // todo Для изменения выпадашки в связи с изменением положения курсора текста
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