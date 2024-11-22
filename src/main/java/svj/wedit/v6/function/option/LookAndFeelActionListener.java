package svj.wedit.v6.function.option;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.listener.WActionListener;
import svj.wedit.v6.logger.Log;

import javax.swing.*;
import java.awt.event.ActionEvent;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 15.08.2011 14:10:30
 */
public class LookAndFeelActionListener extends WActionListener
{
    public LookAndFeelActionListener ()
    {
        super ( "Menu_LookAndFeel" );
    }

    @Override
    public void handleAction ( ActionEvent event ) throws WEditException
    {
        Log.l.debug ("LookAndFeelActionListener.handleAction: Start");

        try
        {
            // Меняем декоратор
            UIManager.setLookAndFeel ( event.getActionCommand() );
            // Переустанавливаем наши системные цветовые настройки
            Par.GM.setUI();
            // Обновляем фрейм
            SwingUtilities.updateComponentTreeUI ( Par.GM.getFrame() );
            //
            //LookAndFeelActionListener.this.validate ();

        }  catch ( Exception e )        {
            Log.l.error ( "err", e );
            throw new WEditException ( e, "Ошибка смены декоратора '", event.getActionCommand(), "':\n", e );
        }
    }

}
