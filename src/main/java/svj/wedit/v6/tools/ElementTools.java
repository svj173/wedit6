package svj.wedit.v6.tools;


import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 22.02.2012 15:58:52
 */
public class ElementTools
{
    // смещение слева (по умолчанию)
    public static final MutableAttributeSet PARAGRAPH_STYLE = new SimpleAttributeSet();

    static
    {
        StyleConstants.setFirstLineIndent ( PARAGRAPH_STYLE, 10 );
        StyleConstants.setAlignment ( PARAGRAPH_STYLE, StyleConstants.ALIGN_JUSTIFIED );
    }
    
}
