package svj.wedit.v6.function.text;


import svj.wedit.v6.logger.Log;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyledEditorKit;
import java.awt.event.ActionEvent;


/**
 * Акция по навешиванию на выбранный текст в редакторе заданного стиля.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 26.10.2012 12:21:26
 */
public class TextAction   extends StyledEditorKit.StyledTextAction
{
    private AttributeSet currentStyle;

    /**
     * Creates a new StyledTextAction from a string action name.
     *
     * @param nm the name of the action
     */
    public TextAction ( String nm )
    {
        super ( nm );
    }

    public void setStyle ( AttributeSet style, ActionEvent e )
    {
        currentStyle = style;
        actionPerformed (e);
    }

    // На самом деле стиль приходит в событии
    public void actionPerformed ( ActionEvent e )
    {
        JEditorPane editor;

        Log.l.debug ( "Start. Text action event = %s", e );
        /*
        JComboBox   comboBox;
        Object      obj;
        comboBox = (JComboBox) e.getSource();
        Log.l.debug ( "comboBox = %s", comboBox );
        obj      = comboBox.getSelectedItem ();
        Log.l.debug ( "Selected obj = %s", obj );
        */

        editor = getEditor(e);
        //if ( (editor != null) && (obj != null) && (obj instanceof AttributeSet) )
        if ( (editor != null) && (currentStyle != null) )
        {
            Log.l.debug ( "set new style = %s", currentStyle );

            // полностью заменяем стиль.
            // -- WEditStyle: styleType = TEXT; styleName=text; либо  styleName отсутствует или null

            setCharacterAttributes ( editor, currentStyle, true );   // true - replace existing attributes - т.е. удалит все старые атрибуты и занесет новые.

            //setParagraphAttributes ( editor, currentStyle, true );   // true - replace existing attributes
        }
    }

}
