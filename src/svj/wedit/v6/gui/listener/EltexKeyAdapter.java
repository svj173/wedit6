package svj.wedit.v6.gui.listener;


import svj.wedit.v6.handler.CloseHandler;
import svj.wedit.v6.logger.Log;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


/**
 * Отрабатывает нажатие клавиш ESC, ENTER в диалоговых окнах.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 27.07.2011 10:24:46
 */
public class EltexKeyAdapter    extends KeyAdapter
{
    private CloseHandler dialog;

    public EltexKeyAdapter ( CloseHandler dialog )
    {
        this.dialog = dialog;
    }

    //public void keyTyped( KeyEvent e) {}       // клавиша нажата и отпущена
    //public void keyReleased(KeyEvent e) {}     // отжата

    /* клавиша нажата */
        @Override
    public void keyPressed ( KeyEvent event )
    {
        int keyCode = event.getKeyCode ();

        switch ( keyCode )
        {
            case KeyEvent.VK_ESCAPE:
                Log.l.debug ( "press ESC" );
                dialog.doClose ( JOptionPane.CANCEL_OPTION );
                break;
            case KeyEvent.VK_ENTER:
                Log.l.debug ( "press ENTER" );
                dialog.doClose ( JOptionPane.OK_OPTION );
                break;
        }
    }

}
