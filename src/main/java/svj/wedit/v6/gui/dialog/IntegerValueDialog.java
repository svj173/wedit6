package svj.wedit.v6.gui.dialog;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.widget.IntegerFieldWidget;

import javax.swing.*;


/**
 * Диалог задания нового числа.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 09.09.2022 10:36:17
 */
public class IntegerValueDialog extends WDialog<Void,Integer>
{
    private IntegerFieldWidget integerFieldWidget;

    public IntegerValueDialog (String title, int oldValue)
    {
        super ( title );

        JPanel  panel;
        int     width;

        width   = 220;

        panel = new JPanel();
        panel.setLayout ( new BoxLayout(panel, BoxLayout.PAGE_AXIS) );

        integerFieldWidget = new IntegerFieldWidget ( title, false );
        integerFieldWidget.setTitleWidth ( width );
        integerFieldWidget.setStartValue ( oldValue );
        integerFieldWidget.setMinValue(8);
        integerFieldWidget.setMaxValue(20);
        panel.add (integerFieldWidget);

        addToNorth ( panel );

        pack ();
    }

    protected void createDialogSize ()
    {
    }

    @Override
    public void init ( Void initObject ) throws WEditException
    {
    }

    @Override
    public Integer getResult () throws WEditException
    {
        return integerFieldWidget.getValue();
    }

}