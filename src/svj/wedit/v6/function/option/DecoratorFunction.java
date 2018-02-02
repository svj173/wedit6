package svj.wedit.v6.function.option;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.params.SimpleParameter;
import svj.wedit.v6.gui.menu.WEMenu;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.Convert;

import javax.swing.*;
import java.awt.event.ActionEvent;


/**
 * Смена L&F всего фрейма.
 * <BR/> Списoк доступных декораций берет из операционной системы.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 15.08.2011 14:06:20
 */
public class DecoratorFunction  extends Function
{
    private WEMenu decMenu;
    private String LAF_NAME = "laf_name";


    public DecoratorFunction ()
    {
        setId ( FunctionId.DECORATOR );
    }

    private SimpleParameter getPar ()
    {
        SimpleParameter sp;

        sp  = (SimpleParameter) getParameter ( LAF_NAME );
        if ( sp == null )
        {
            sp  = new SimpleParameter ( LAF_NAME, null ); // дефолтное значение
            sp.setHasEmpty ( true );
            setParameter ( LAF_NAME, sp );
        }

        return sp;
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        String command;

        Log.l.debug ( "DecoratorFunction.handel: Start. event = %s", event );

        command = null;

        try
        {
            // Меняем декоратор
            command = event.getActionCommand();
            if ( (command != null) && ( ! command.isEmpty() ) )
            {
                UIManager.setLookAndFeel ( command );
                // Переустанавливаем наши системные цветовые настройки
                Par.GM.setUI();
                // Обновляем фрейм
                SwingUtilities.updateComponentTreeUI ( Par.GM.getFrame() );
                //
                //LookAndFeelActionListener.this.validate ();

                // Сохраняем выбранное значение
                getPar().setValue ( command );

                // Обновляем меню
                createMenu();
            }

        }  catch ( Exception e )        {
            Log.l.error ( Convert.concatObj ("error. decoratorName = '",command,"'; event = ", event), e );
            throw new WEditException ( e, "Ошибка смены декоратора '", command, "':\n", e );
        }
    }

    @Override
    public JComponent getMenuObject ( String cmd )
    {
        decMenu = new WEMenu ( "Оформление" );

        createMenu();
        
        return decMenu;
    }

    private void createMenu ()
    {
        JRadioButtonMenuItem    rbMenuItem;
        ButtonGroup             group;
        LookAndFeel             currentLaf;
        String                  currentLafClass, lafName, lafClassName;

        // Предварительно очистить
        decMenu.removeAll();

        group           = new ButtonGroup();
        currentLaf      = UIManager.getLookAndFeel();
        //currentLafName  = currentLaf.getName();
        currentLafClass  = currentLaf.getClass().getName();
        //Log.l.debug ( "currentLafClass = ", currentLafClass );
        //listener        = new LookAndFeelActionListener();

        // Получить список доступных декораций в данной ОС
        for ( UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels() )
        {
            //Logger.getInstance().debug ( "----> name = " + info.getName() + ", class = " + info.getClassName() );
            lafName         = info.getName();
            lafClassName    = info.getClassName();
            //Log.l.debug ( "lafName = ", lafName );
            //Log.l.debug ( "class = ", lafClassName );
            // Пропускаем Nimbus - тк он после себя ломает все остальные темы
            //if ( lafName.equals ( "Nimbus" )) continue;
            rbMenuItem  = new JRadioButtonMenuItem();
            rbMenuItem.setText ( lafName );
            rbMenuItem.setActionCommand ( info.getClassName() );
            decMenu.add ( rbMenuItem );
            group.add( rbMenuItem );
            if ( lafClassName.equals(currentLafClass) )       rbMenuItem.setSelected ( true );
            rbMenuItem.addActionListener ( this );
        }
    }


    @Override
    public void start () throws WEditException
    {
        String value = getPar().getValue();
        Log.l.debug ( "DecoratorFunction.start: Start. value = %s", value );
        if ( (value != null) && ( ! value.equalsIgnoreCase ( "null" )) && ( ! value.isEmpty()) )
        {
            ActionEvent event = new ActionEvent ( this, 157, value );
            handle ( event );
        }
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
