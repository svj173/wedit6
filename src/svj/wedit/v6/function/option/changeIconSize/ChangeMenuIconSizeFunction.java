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
 * Смена размеров иконок во всех меню - контекстных, главное меню и т.д.
 * <BR/> todo Не отлажена !!!
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 02.03.2013 14:06:20
 */
public class ChangeMenuIconSizeFunction extends Function implements IIconSize
{
    private WEMenuItem  menuItem;
    private String      PARAM_NAME = "iconSize";


    public ChangeMenuIconSizeFunction ()
    {
        setId ( FunctionId.CHANGE_MENU_ICON_SIZE );
        setName ( "Размер иконок в меню." );
    }

    /*
    private SimpleParameter getPar ()
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
    */

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        ChangeIconSizeDialog dialog;
        Integer                 value;
        SimpleParameter         sp;

        Log.l.debug ("Start");

        try
        {
            // Меняем размер иконки
            // Выводим диалог
            dialog  = new ChangeIconSizeDialog ( this );
            dialog.showDialog ();

            if ( dialog.isOK() )
            {
                //sp  = getPar();
                sp  = getSimpleParameter ( PARAM_NAME, Integer.toString (Par.TOOLBAR_ICON_SIZE) );
                // Сохраняем выбранное значение
                value = dialog.getResult();
                sp.setValue ( value.toString() );

                Par.MENU_ICON_SIZE = value;

                // Обновляем меню этой функции
                createMenu();

                // Обновить фрейм
                //Par.GM.rewrite();
                Par.NEED_REWRITE = true;
            }
            else
            {
                Par.NEED_REWRITE = false;
            }

        }  catch ( Exception e )        {
            Log.l.error ( "err", e );
            throw new WEditException ( e, "Ошибка смены размера иконок ToolBar '", event.getActionCommand(), "':\n", e );
        }
    }

    @Override
    public void start () throws WEditException
    {
        SimpleParameter         sp;

        //Log.l.debug ( "Start. sp = ", sp );
        //sp  = getPar();
        sp  = getSimpleParameter ( PARAM_NAME, Integer.toString (Par.TOOLBAR_ICON_SIZE) );

        Par.MENU_ICON_SIZE  = Integer.parseInt ( sp.getValue() );
        //Par.GM.getFrame().rewrite();
        Par.NEED_REWRITE    = true;

        Log.l.debug ( "Finish" );
    }

    @Override
    public String getIconSize ()
    {
        SimpleParameter         sp;

        sp  = getSimpleParameter ( PARAM_NAME, Integer.toString (Par.TOOLBAR_ICON_SIZE) );
        return sp.getValue();
        //return getPar().getValue();
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
        SimpleParameter         sp;

        sp  = getSimpleParameter ( PARAM_NAME, Integer.toString (Par.TOOLBAR_ICON_SIZE) );
        menuItem.setText ( Convert.concatObj ( getName(), ": ", sp.getValue() ));
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
