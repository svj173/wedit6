package svj.wedit.v6.gui.widget.font;


import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.widget.AbstractDialogWidget;

import javax.swing.*;
import java.awt.*;

/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 01.09.2014 16:20
 */
public class StyleTypeWidget   extends AbstractDialogWidget<Integer>
{
    private final JPanel panel;
    private JCheckBox boldCheckBox = new JCheckBox ( "Bold" );
    private JCheckBox italicCheckBox = new JCheckBox ( "Italic" );
    private JCheckBox underlineCheckBox = new JCheckBox ( "Underline" );

    public StyleTypeWidget ( String titleName )

    {
        super ( titleName, false );

        panel = new JPanel ( new GridLayout ( 1, 3, 10, 5 ) );
        //p = new JPanel ();
        //p.setBorder ( new TitledBorder ( new EtchedBorder(), "Effects" ) );

        boldCheckBox.setMnemonic ( 'b' );
        boldCheckBox.setToolTipText ( "Bold font" );
        panel.add ( boldCheckBox );

        italicCheckBox.setMnemonic ( 'i' );
        italicCheckBox.setToolTipText ( "Italic font" );
        panel.add ( italicCheckBox );

        /*
        underlineCheckBox.setMnemonic ( 'u' );
        underlineCheckBox.setToolTipText ( "Underline font" );
        panel.add ( underlineCheckBox );
        */

        add ( panel );
    }

    @Override
    public JComponent getGuiComponent ()
    {
        return panel;
    }

    @Override
    protected Integer validateValue () throws WEditException
    {
        return getValue();
    }

    @Override
    public Integer getValue ()
    {
        int result;

        result = Font.PLAIN;
        if ( boldCheckBox.isSelected() )     result = result + Font.BOLD;
        if ( italicCheckBox.isSelected() )   result = result + Font.ITALIC;

        return result;
    }

    @Override
    public void setValue ( Integer value )
    {
        switch ( value )
        {
            case Font.PLAIN:
                boldCheckBox.setSelected ( false );
                italicCheckBox.setSelected ( false );
                break;
            case Font.BOLD:
                boldCheckBox.setSelected ( true );
                italicCheckBox.setSelected ( false );
                break;
            case Font.ITALIC:
                boldCheckBox.setSelected ( false );
                italicCheckBox.setSelected ( true );
                break;
            case Font.BOLD|Font.ITALIC:
                boldCheckBox.setSelected ( true );
                italicCheckBox.setSelected ( true );
                break;
        }
    }

    @Override
    public void setEditable ( boolean value )
    {
        panel.setEnabled ( value );
    }

    @Override
    public void setValueWidth ( int width )
    {
        int         height;
        Dimension   dim;

        height  = panel.getHeight();
        if ( height < WCons.BUTTON_HEIGHT )  height = WCons.BUTTON_HEIGHT;
        dim     = new Dimension ( width, height );
        panel.setPreferredSize ( dim );
    }

}
