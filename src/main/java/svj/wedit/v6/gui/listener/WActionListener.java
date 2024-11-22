package svj.wedit.v6.gui.listener;


import svj.wedit.v6.logger.Log;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Общий класс WEdit листенеров ActionListener.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 15.08.2011 14:12:17
 */
public abstract class WActionListener extends WEventHandler<ActionEvent> implements ActionListener
{

    public WActionListener ( String name )
    {
        super ( name );
    }

    @Override
    public void actionPerformed ( ActionEvent e )
    {
        Log.l.debug ("WActionListener.actionPerformed (",getName(),"): Start [",e.getActionCommand(),"]");

        handle(e);

        Log.l.debug ("WActionListener.actionPerformed (",getName(),"): Finish [",e.getActionCommand(),"]");
    }

    @Override
    protected String getCmd ( ActionEvent e )
    {
        return e.getActionCommand();
    }

}