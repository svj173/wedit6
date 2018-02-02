package svj.wedit.v6.gui.widget;


import svj.wedit.v6.Par;
import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 29.08.2014 16:31
 */
public class ColorWidget   extends AbstractWidget<Color>
{
    private final JButton colorButton;

    public ColorWidget ( String titleName )
    {
        super ( titleName, false );

        colorButton = new JButton();

        ActionListener actionListener = new ActionListener()
        {
            public void actionPerformed ( ActionEvent actionEvent )
            {
                Color initialBackground = colorButton.getBackground();
                Color background = JColorChooser.showDialog ( Par.GM.getFrame(), "JColorChooser Sample", initialBackground );
                if ( background != null)    colorButton.setBackground(background);
            }
        };
        colorButton.addActionListener ( actionListener );
        add ( colorButton );
    }

    @Override
    public JComponent getGuiComponent ()
    {
        return colorButton;
    }

    @Override
    protected Color validateValue () throws WEditException
    {
        return colorButton.getBackground();
    }

    @Override
    public Color getValue ()
    {
        return colorButton.getBackground();
    }

    @Override
    public void setValue ( Color value )
    {
        colorButton.setBackground ( value );
    }

    @Override
    public void setEditable ( boolean value )
    {
        colorButton.setEnabled ( value );
    }

    @Override
    public void setValueWidth ( int width )
    {
        int         height;
        Dimension   dim;

        height  = colorButton.getHeight();
        if ( height < WCons.BUTTON_HEIGHT )  height = WCons.BUTTON_HEIGHT;
        dim     = new Dimension ( width, height );
        colorButton.setPreferredSize ( dim );
    }

}
