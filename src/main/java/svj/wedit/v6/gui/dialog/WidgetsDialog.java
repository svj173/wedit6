package svj.wedit.v6.gui.dialog;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.widget.AbstractWidget;
import svj.wedit.v6.tools.Utils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Диалог, который содержит в себе одни виджеты. С валидацией.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 21.12.2014 21:51
 */
public class WidgetsDialog extends WValidateDialog<Void,Void>
{
    private final Collection<AbstractWidget> widgets;
    private JPanel panel;

    public WidgetsDialog ( String title )
    {
        super ( title );

        widgets = new ArrayList<AbstractWidget>();

        panel = new JPanel();
        panel.setLayout ( new BoxLayout ( panel, BoxLayout.PAGE_AXIS ) );
        addToCenter ( new JScrollPane ( panel ) );
    }

    @Override
    public boolean validateData ()
    {
        boolean result;

        result  = true;

        for ( AbstractWidget widget : widgets )
        {
            try
            {
                widget.validateWidget();
            } catch ( Exception e )    {
                result  = false;
                addValidateErr ( e.getMessage() );
            }
        }

        return result;
    }

    public void addWidget ( AbstractWidget widget )
    {
        widgets.add ( widget );
        panel.add ( widget );
    }

    public void setTitleWidth ( int titleWidth )
    {
        for ( AbstractWidget widget : widgets )
        {
            widget.setTitleWidth ( titleWidth );
        }
        //panel.revalidate();
        //panel.repaint();
    }

    public void setValueWidth ( int valueWidth )
    {
        for ( AbstractWidget widget : widgets )
        {
            widget.setValueWidth ( valueWidth );
        }
    }

    protected void createDialogSize ()
    {
    }

    @Override
    public void init ( Void initObject ) throws WEditException
    {
        // не исп
    }

    @Override
    public Void getResult () throws WEditException
    {
        return null;  // не исп
    }

    public Collection<AbstractWidget> getWidgets ()
    {
        return widgets;
    }

    public AbstractWidget getWidgetByName ( String widgetName )
    {
        for ( AbstractWidget widget : widgets )
        {
            if ( Utils.compareToWithNull ( widget.getName(), widgetName ) == 0 ) return widget;
        }
        return null;
    }

    public Object getValue ( String widgetName )
    {
        AbstractWidget widget;
        widget = getWidgetByName (widgetName);
        if ( widget == null )
            return null;
        else
            return widget.getValue();
    }

}
