package svj.wedit.v6.gui.widget;


import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;


import javax.swing.*;
import java.awt.*;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.08.2011 14:10:30
 */
public class BooleanWidget  extends AbstractWidget<Boolean>
{
    private JCheckBox   checkBox;

    public enum Orientation { TITLE_FIRST, TITLE_LAST }

    public BooleanWidget ( String titleName )
    {
        this ( titleName, false, Orientation.TITLE_FIRST );
    }

    public BooleanWidget ( String titleName, boolean selected, Orientation orientation )
    {
        super ( titleName, true );

        checkBox = new JCheckBox();
        checkBox.setSelected ( selected );

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
    public JComponent getGuiComponent ()
    {
        return checkBox;
    }

    @Override
    protected Boolean validateValue () throws WEditException
    {
        return getValue();
    }

    @Override
    public Boolean getValue () //throws WEditException
    {
        return checkBox.isSelected();
    }

    @Override
    public void setValue ( Boolean value ) //throws WEditException
    {
        checkBox.setSelected ( value );
    }

    @Override
    public void setEditable ( boolean value )
    {
        checkBox.setEnabled ( value );
    }

    @Override
    public void setValueWidth ( int width )
    {
        int         height;
        Dimension   dim;

        if ( checkBox != null )
        {
            height  = checkBox.getHeight();
            if ( height < WCons.BUTTON_HEIGHT )  height = WCons.BUTTON_HEIGHT;
            dim     = new Dimension ( width, height );
            checkBox.setPreferredSize ( dim );
        }
    }

}
