package svj.wedit.v6.gui.listener;


import svj.wedit.v6.logger.Log;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;


/**
 * Общий класс листенеров ChangeListener.
 * <BR/> Вешается на смене табов.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 04.08.2011 16:09:01
 */
public abstract class WChangeListener extends WEventHandler<ChangeEvent> implements ChangeListener
{
    public WChangeListener ( String name )
    {
        super( name );
    }

    @Override
    public void stateChanged ( ChangeEvent event )
    {
        Log.l.debug ( "(%s): Start. event = %s",getName(), event );

        handle ( event );

        Log.l.debug ( "(%s): Finish", getName() );
    }

    @Override
    protected String getCmd ( ChangeEvent e )
    {
        String result;
        Object source;

        source = e.getSource();
        //Log.l.debug ("(",getName(),"): Start. source = ", source );
        result = source.toString();
        if ( source instanceof JTabbedPane )
        {
            JTabbedPane jTabbedPane;
            Component   component;

            // Получить имя таба
            jTabbedPane = (JTabbedPane) source;
            //Log.l.debug ("(",getName(),"): jTabbedPane = ", jTabbedPane );
            component   = jTabbedPane.getSelectedComponent();
            //Log.l.debug ("(",getName(),"): component = ", component );
            if ( component != null )
                result = component.toString();
        }

        Log.l.debug ("(%s): tab name = %s",getName(), result );
        return result;
    }

}
