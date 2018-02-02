package svj.wedit.v6.function.option.changeIconSize;


import svj.wedit.v6.Par;
import svj.wedit.v6.dialog.ChangeIconSizeDialog;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.params.SimpleParameter;
import svj.wedit.v6.gui.menu.WEMenuItem;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.Convert;

import javax.swing.*;
import java.awt.event.ActionEvent;


/**
 * Смена размера иконок, расположенных над панелями.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 29.05.2012 09:06:20
 */
public class ChangePanelIconSizeFunction extends Function implements IIconSize
{
    private WEMenuItem  menuItem;
    private String      PARAM_NAME = "iconSize";

    public ChangePanelIconSizeFunction ()
    {
        setId ( FunctionId.CHANGE_PANEL_ICON_SIZE );
        setName ( "Размер иконок панелей" );
    }

    public SimpleParameter getIconSizeParam ()
    {
        SimpleParameter sp;

        sp  = (SimpleParameter) getParameter ( PARAM_NAME );
        if ( sp == null )
        {
            sp  = new SimpleParameter ( PARAM_NAME, Par.PANEL_ICON_SIZE ); // дефолтное значение
            sp.setHasEmpty ( false );
            setParameter ( PARAM_NAME, sp );
        }

        return sp;
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        ChangeIconSizeDialog dialog;
        Integer value;

        Log.l.debug ("Start");

        try
        {
            // Меняем размер иконки
            // Выводим диалог
            dialog  = new ChangeIconSizeDialog ( this );
            dialog.showDialog ();

            if ( dialog.isOK() )
            {
                // Сохраняем выбранное значение
                value = dialog.getResult();
                getIconSizeParam ().setValue ( value.toString () );

                Par.PANEL_ICON_SIZE = value;

                // Обновляем наше меню
                createMenu();

                // Обновить фрейм
                //Par.GM.rewrite();
            }

        }  catch ( Exception e )        {
            Log.l.error ( "err", e );
            throw new WEditException ( e, "Ошибка смены размера иконок панелей '", event.getActionCommand(), "':\n", e );
        }
    }

    @Override
    public void start () throws WEditException
    {
        Par.PANEL_ICON_SIZE = Integer.parseInt ( getIconSizeParam ().getValue() );
        //Par.GM.getFrame().rewrite();
        Par.NEED_REWRITE = true;

        Log.l.debug ( "Finish" );
    }

    @Override
    public String getIconSize ()
    {
        return getIconSizeParam().getValue().toString();
    }


    @Override
    public JComponent getMenuObject ( String cmd )
    {
        menuItem    = (WEMenuItem) super.getMenuObject ( cmd );

        menuItem.setText ( Convert.concatObj ( getName(), ": ", getIconSizeParam().getValue () ));

        return menuItem;
    }

    private void createMenu ()
    {
        menuItem.setText ( Convert.concatObj ( getName(), ": ", getIconSizeParam().getValue () ));
    }

    @Override
    public void rewrite ()
    {
    }

    @Override
    public void init () throws WEditException
    {
    }

    @Override
    public void close ()
    {
    }

    @Override
    public String getToolTipText ()
    {
        return null;
    }

}
