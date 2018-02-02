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
 * <BR/> Date: 12.04.2013 15:31
 */
public class UndoFunction extends SimpleFunction
{
    public UndoFunction ()
    {
        setId ( FunctionId.UNDO_TEXT );
        setName ( "Undo" );
        setMapKey ( "Ctrl/Z" );
        setIconFileName ( "undo.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        TextPanel textPanel;

        // Взять текущий текст.
        textPanel   = Par.GM.getFrame().getCurrentTextPanel();
        // Дернуть у него Undo
        if ( textPanel != null )  textPanel.doUndo();
    }

    @Override
    public String getToolTipText ()
    {
        return "Undo";
    }

    @Override
    public void rewrite ()     {    }

}
