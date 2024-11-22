package svj.wedit.v6.gui.panel;


import svj.wedit.v6.obj.Editable;

import javax.swing.*;
import java.awt.*;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 09.08.2011 15:27:58
 */
public abstract class EditablePanel   extends RewritePanel  implements Editable//, Comparable<EditablePanel>
{
    /* Флаг, было редактирование панели (без сохранения изменений) или нет. */
    private boolean edit;

    private JLabel tabTitleLabel    = null;
    private Color  defaultColor     = Color.BLACK;  // исходный цвет текста титла в табике


    @Override
    public String toString()
    {
        StringBuilder result;
        String str;

        result = new StringBuilder ( 128 );
        result.append ( "; edit = " );
        result.append ( isEdit() );

        if ( tabTitleLabel == null )
            str = "Null";
        else
            str = tabTitleLabel.getText();
        result.append ( "; tabTitleLabel = " );
        result.append ( str );

        result.append ( "; defaultColor = " );
        result.append ( defaultColor );
        result.append ( ";; " );
        result.append ( super.toString() );
        result.append ( " " );

        return result.toString();
    }

    public boolean isEdit ()
    {
        return edit;
    }

    public void setEdit ( boolean edit )
    {
        this.edit = edit;

        if ( tabTitleLabel != null )
        {
            if ( edit )
                tabTitleLabel.setForeground ( Color.RED );
            else
                tabTitleLabel.setForeground ( defaultColor );
        }
    }

    public void setTabTitleLabel ( JLabel tabTitleLabel )
    {
        this.tabTitleLabel  = tabTitleLabel;
        if ( tabTitleLabel != null )
            defaultColor        = tabTitleLabel.getForeground();
    }


    /*
    public int compareTo ( EditablePanel p )
    {
        int result;

        if ( p == null )
            result = -1;
        else
            result = Utils.compareToWithNull ( getName(), p.getName() );

        Log.l.debug ( "--- EditablePanel.compareTo: this = ", this );
        Log.l.debug ( "--- EditablePanel.compareTo: p = ", p );
        Log.l.debug ( "----- EditablePanel.compareTo: result = ", result );
        return result;
    }

    public boolean equals ( Object o )
    {
        if ( (o != null) && ( o instanceof EditablePanel) )
        {
            EditablePanel p = (EditablePanel) o;
            return compareTo ( p ) == 0;
        }
        else
        {
            return false;
        }
    }
    //*/
}
