package svj.wedit.v6.gui.widget;


import svj.wedit.v6.WCons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Виджет флага.
 * <BR/> T - обьект, ассоциируемый с данным флагом.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 12.08.2014 17:47:13
 */
public class CheckBoxWidget<T>  extends AbstractWidget<Boolean>
{
    private JCheckBox   checkBox;
    private T           object;

    public enum Orientation { TITLE_FIRST, TITLE_LAST }

    public CheckBoxWidget ( String titleName )
    {
        this ( titleName, false, Orientation.TITLE_FIRST );
    }

    public CheckBoxWidget ( String titleName, boolean selected )
    {
        this ( titleName, selected, Orientation.TITLE_FIRST );
    }

    public CheckBoxWidget ( String titleName, boolean selected, Orientation orientation )
    {
        super ( titleName, true );

        checkBox    = new JCheckBox();
        checkBox.setSelected ( selected );
        startValue  = selected;

        switch ( orientation )
        {
            case TITLE_FIRST:
                add ( checkBox );
                break;
            case TITLE_LAST:
                add ( checkBox, 0 );  // значит checkBox ставим первым
                break;
        }
    }

    @Override
    public String toString ()
    {
        StringBuilder   result;

        result  = new StringBuilder ( 128 );

        result.append ( "[ CheckBoxWidget : title = " );
        result.append ( getTitleName() );
        result.append ( "; checkBoxValue = " );
        result.append ( checkBox.isSelected() );
        result.append ( "; object = " );
        result.append ( getObject() );

        result.append ( " ]" );

        return result.toString();
    }

    @Override
    public JComponent getGuiComponent ()
    {
        return checkBox;
    }

    @Override
    protected Boolean validateValue ()
    {
        return true;
    }

    @Override
    public Boolean getValue ()
    {
        return checkBox.isSelected();
    }

    public boolean isSelected ()
    {
        return checkBox.isSelected();
    }

    @Override
    public void setValue ( Boolean value )
    {
        checkBox.setSelected ( value );
    }

    public void setSelected ( boolean value )
    {
        checkBox.setSelected ( value );
    }

    @Override
    public void setEditable ( boolean value )
    {
        checkBox.setEnabled ( value );
        //checkBox.setEditable ( value );
        setEnabled ( value );
    }

    public boolean isEditable ()
    {
        return checkBox.isEnabled();
    }

    @Override
    public void setValueWidth ( int width )
    {
        int         height;
        Dimension dim;

        if ( checkBox != null )
        {
            height  = checkBox.getHeight();
            if ( height < WCons.BUTTON_HEIGHT )  height = WCons.BUTTON_HEIGHT;
            dim     = new Dimension ( width, height );
            checkBox.setPreferredSize ( dim );
            checkBox.setMinimumSize( dim );
            checkBox.setMaximumSize( dim );
        }
    }

    public void setMyToolTipText ( String text )
    {
        super.setToolTipText(text);
        this.getGuiComponent().setToolTipText(text);
    }

    @Override
    public void initAction ( ActionListener listener, String cmd )
    {
        checkBox.setActionCommand ( cmd );
        checkBox.addActionListener ( listener );
    }

}
