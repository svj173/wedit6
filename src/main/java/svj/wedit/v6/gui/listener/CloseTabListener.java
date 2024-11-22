package svj.wedit.v6.gui.listener;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.obj.function.Function;

import java.awt.event.ActionEvent;

/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 31.01.2013 9:49
 */
public class CloseTabListener extends WActionListener
{
    private Function closeFunction;

    public CloseTabListener ( Function closeFunction )
    {
        super ( "CloseTabListener" );

        this.closeFunction = closeFunction;
    }

    @Override
    public void handleAction ( ActionEvent event ) throws WEditException
    {
        if ( closeFunction != null )  closeFunction.handle ( event );
    }

}
