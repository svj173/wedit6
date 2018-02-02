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
 * Смена иконок ToolBar всего фрейма  - по размерам.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 16.08.2011 21:06:20
 */
public class ChangeToolBarIconSizeFunction extends Function implements IIconSize
{
    private WEMenuItem  menuItem;
    private String PARAM_NAME = "iconSize";

    public ChangeToolBarIconSizeFunction ()
    {
        setId ( FunctionId.CHANGE_TOOL_BAR_ICON_SIZE );
        setName ( "Размер иконок tool-bar" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        ChangeIconSizeDialog dialog;

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
                Integer value = dialog.getResult();
                getIconSizeParam().setValue ( value.toString () );

                Par.TOOLBAR_ICON_SIZE = value;

                // Обновляем меню
                createMenu();

                // Обновить фрейм
                Par.GM.getFrame().getToolbar().rewrite ();
            }

        }  catch ( Exception e )        {
            Log.l.error ( "err", e );
            throw new WEditException ( e, "Ошибка смены размера иконок ToolBar '", event.getActionCommand(), "':\n", e );
        }
    }

    @Override
    public void start () throws WEditException
    {
        Par.TOOLBAR_ICON_SIZE = Integer.parseInt ( getIconSize () );
        Par.GM.getFrame().getToolbar().rewrite();

        Log.l.debug ( "Finish" );
    }

    @Override
    public String getIconSize ()
    {
        return getIconSizeParam ().getValue ();
    }

    public SimpleParameter getIconSizeParam ()
    {
        SimpleParameter sp;

        sp  = (SimpleParameter) getParameter ( PARAM_NAME );
        if ( sp == null )
        {
            sp  = new SimpleParameter ( PARAM_NAME, Par.TOOLBAR_ICON_SIZE ); // дефолтное значение
            sp.setHasEmpty ( false );
            setParameter ( PARAM_NAME, sp );
        }

        return sp;
    }

    @Override
    public JComponent getMenuObject ( String cmd )
    {
        menuItem    = (WEMenuItem) super.getMenuObject ( cmd );

        createMenu();

        return menuItem;
    }

    private void createMenu ()
    {
        menuItem.setText ( Convert.concatObj ( getName(), ": ", getIconSizeParam().getValue() ));
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
