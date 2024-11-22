package svj.wedit.v6.dialog;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.option.changeIconSize.IIconSize;
import svj.wedit.v6.gui.dialog.WDialog;
import svj.wedit.v6.gui.widget.ComboBoxWidget;
import svj.wedit.v6.tools.GuiTools;

import javax.swing.*;


/**
 * Диалог задания размеров иконок.
 * <BR/> Вариант 1: Задается произвольный размер, а иконки растягиваются или ужимаются. Отказался, т.к. иконки становятся уродливыми.
 * <BR/> Вариант 2: Фиксированная линейка размеров - из нее и выбирается. Преимущество - гарантированно наличие всех иконок.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 16.08.2011 21:36:17
 */
public class ChangeIconSizeDialog extends WDialog<Void,Integer>
{
    //private IntegerFieldWidget       iconSizeWidget;
    private ComboBoxWidget<Integer> iconSizeWidget;

    public ChangeIconSizeDialog ( IIconSize function ) throws WEditException
    {
        super ( "Сменить размер иконок" );

        JPanel  panel;
        int     width;

        width   = 220;

        panel = new JPanel();
        panel.setLayout ( new BoxLayout(panel, BoxLayout.PAGE_AXIS) );

        //iconSizeWidget = new IntegerFieldWidget ( "Размер ( в пикс)", false );
        iconSizeWidget = new ComboBoxWidget<Integer> ( "Размер ( в пикс)", GuiTools.getIconSizeList() );
        iconSizeWidget.setTitleWidth ( width );
        iconSizeWidget.setStartValue ( Integer.parseInt ( function.getIconSize() ) );
        panel.add ( iconSizeWidget );

        addToNorth ( panel );

        pack ();
    }

    protected void createDialogSize ()
    {
        /*
        int  width, height;

        width       = Par.SCREEN_SIZE.width / 4;
        height      = Par.SCREEN_SIZE.height / 4;
        setPreferredSize ( new Dimension(width,height) );
        setSize ( width, height );
        */
    }

    @Override
    public void init ( Void initObject ) throws WEditException
    {
    }

    @Override
    public Integer getResult () throws WEditException
    {
        return iconSizeWidget.getValue();
    }

}