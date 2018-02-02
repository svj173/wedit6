package svj.wedit.v6.gui.listener;


import svj.wedit.v6.logger.Log;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;


/**
 * Общий класс листенеров TreeSelectionListener - акция при выборке объекта в дереве.
 * <BR/> Вешается на дерево.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 04.08.2011 12:12:23
 */
public abstract class WTreeSelectionListener extends WEventHandler<TreeSelectionEvent> implements TreeSelectionListener
{

    public WTreeSelectionListener ( String name )
    {
        super( name );
    }

    @Override
    public void valueChanged ( TreeSelectionEvent e )
    {
        Log.l.debug ("Listener (", getName(), "): Start");

        handle ( e );

        Log.l.debug ("Listener (", getName(), "): Finish");
    }

    @Override
    protected String getCmd ( TreeSelectionEvent e )
    {
        return e.getPath().toString();
    }

}
