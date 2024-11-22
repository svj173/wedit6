package svj.wedit.v6.function.book.undo;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.obj.function.SimpleFunction;

import java.awt.event.ActionEvent;

/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 12.04.2013 15:41
 */
public class RedoFunction extends SimpleFunction
{
    public RedoFunction ()
    {
        setId ( FunctionId.REDO_TEXT );
        setName ( "Redo" );
        setIconFileName ( "redo.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        TextPanel textPanel;

        // Взять текущий текст.
        textPanel   = Par.GM.getFrame().getCurrentTextPanel();
        // Дернуть у него Redo
        if ( textPanel != null )  textPanel.doRedo();
    }

    @Override
    public String getToolTipText ()
    {
        return "Redo";
    }

    @Override
    public void rewrite ()     {    }

}
