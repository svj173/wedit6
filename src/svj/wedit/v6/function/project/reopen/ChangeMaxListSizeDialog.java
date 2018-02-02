package svj.wedit.v6.function.project.reopen;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.dialog.WDialog;
import svj.wedit.v6.gui.widget.IntegerFieldWidget;

import javax.swing.*;
import java.awt.*;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 16.08.2011 13:36:17
 */
public class ChangeMaxListSizeDialog extends WDialog<Void,Integer>
{
    private ReopenProjectFunction   function;
    private IntegerFieldWidget      maxSizeWidget;

    public ChangeMaxListSizeDialog ( ReopenProjectFunction function ) throws WEditException
    {
        super ( "Сменить макс. размер списка" );

        JPanel  panel;
        int     width;

        this.function   = function;
        
        width   = 220;

        panel = new JPanel();
        panel.setLayout ( new BoxLayout(panel, BoxLayout.PAGE_AXIS) );

        // boolean hasEmpty, int maxSize, int width, String titleName
        maxSizeWidget = new IntegerFieldWidget ( "Макс. размер", false );
        maxSizeWidget.setTitleWidth ( width );
        maxSizeWidget.setStartValue ( Integer.parseInt ( function.getMaxSizeParam().getValue().toString() ) );
        panel.add ( maxSizeWidget );

        addToNorth ( panel );
    }

    protected void createDialogSize ()
    {
        int  width, height;

        width       = Par.SCREEN_SIZE.width / 4;
        height      = Par.SCREEN_SIZE.height / 4;
        setPreferredSize ( new Dimension(width,height) );
        setSize ( width, height );

        pack();
    }
    
    @Override
    public void init ( Void initObject ) throws WEditException
    {
    }

    @Override
    public Integer getResult () throws WEditException
    {
        return maxSizeWidget.getValue();
    }

}