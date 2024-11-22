package svj.wedit.v6.function.text.action;


import javax.swing.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import java.awt.event.ActionEvent;


/**
 * Акция установки правого смещения для параграфа. Смещение - в пикселях.
 * <BR/> Возможно, брать у выбранного текста размер шрифта и переводить символы смещения в пиксели.
 * Трудность - у выбранного текста могут быть разные размеры шрифта.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 14.07.2011 15:10:14
 */
public class RightMarginAction  extends StyledEditorKit.StyledTextAction
{
    private int margin;

    /**
     * Creates a new StyledTextAction from a string action name.
     *
     * @param actionName the name of the action
     */
    public RightMarginAction ( String actionName, int margin )
    {
        super ( actionName );
        this.margin = margin;
    }

    @Override
    public void actionPerformed ( ActionEvent event )
    {
        //System.out.println ( "--- event = " + event );
        JEditorPane editor = getEditor ( event );
        //System.out.println ( "--- editor = " + editor );
        if ( editor != null )
        {
            int size = this.margin;
            //if ( ( event != null ) && ( event.getSource() == editor ) )
            if (  event != null )
            {
                String s = event.getActionCommand();
                //System.out.println ( "--- s = '" + s + "'" );
                try
                {
                    size = Integer.parseInt ( s, 10 );
                } catch ( NumberFormatException nfe )                 {
                    nfe.printStackTrace();
                }
            }
            //System.out.println ( "--- size = '" + size + "'" );
            if ( size != 0 )
            {
                MutableAttributeSet attr = new SimpleAttributeSet ();
                StyleConstants.setFirstLineIndent ( attr, size );
                //setCharacterAttributes ( editor, attr, false );
                setParagraphAttributes ( editor, attr, false);
            }
            else
            {
                UIManager.getLookAndFeel().provideErrorFeedback ( editor );
            }
        }
    }

}
