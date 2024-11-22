package svj.wedit.v6.gui.listener;


import svj.wedit.v6.logger.Log;
import svj.wedit.v6.tools.Convert;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 * Общий класс листенеров ListSelectionListener.
 * <BR/> Висят на jList.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.08.2011 22:09:04
 */
public abstract class WListSelectionListener extends WEventHandler<ListSelectionEvent> implements ListSelectionListener
{
    //private boolean allowAction = true;

    public WListSelectionListener ( String name )
    {
        super ( name );
    }

    @Override
    public void valueChanged ( ListSelectionEvent e )
    {
        Log.l.debug ("(",getName(),"): Start");

        if ( e.getValueIsAdjusting() )
        {
            // проверка необходима, иначе на каждый клик в списке NTE будет генериться две акции
            // - это 'выход' из предыдущего значения
            Log.l.debug ("(",getName(),"): Finish - it is Adjusting");
        }
        else
        {
            handle(e);

            Log.l.debug ("(",getName(),"): Finish - handle");
        }
    }

    @Override
    protected String getCmd ( ListSelectionEvent e )
    {
        return Convert.concatObj ( "firstIndex:",e.getFirstIndex(), "; lastIndex:", e.getLastIndex() );
    }

}
