package svj.wedit.v6.gui.listener;


import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


/**
 * Листенер добавляется в окна (фрейм, диалог) - дергается при событиях окна - закрывание (по крестику) и т.д.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 27.07.2011 10:54:17
 */
public class WEditWindowListener implements WindowListener
{
    @Override
    public void windowOpened ( WindowEvent e )
    {
        //Logger.getInstance().debug ( "-- EltexWindowListener.windowOpened" );
    }

    /* Окно (фрейм, диалог) начало закрываться */
    @Override
    public void windowClosing ( WindowEvent e )
    {
        //Logger.getInstance().debug ( "-- EltexWindowListener.windowClosing" );
    }

    /* Окно (фрейм, диалог) закрылось. Почему-то вызывается два раза. */
    @Override
    public void windowClosed ( WindowEvent e )
    {
        //Logger.getInstance().debug ( "-- EltexWindowListener.windowClosed" );
    }

    @Override
    public void windowIconified ( WindowEvent e )
    {
        //Logger.getInstance().debug ( "-- EltexWindowListener.windowIconified" );
    }

    @Override
    public void windowDeiconified ( WindowEvent e )
    {
        //Logger.getInstance().debug ( "-- EltexWindowListener.windowDeiconified" );
    }

    @Override
    public void windowActivated ( WindowEvent e )
    {
        //Logger.getInstance().debug ( "-- EltexWindowListener.windowActivated" );
    }

    /* Окно (фрейм, диалог) окончательно завершило свою работу - после Closed */
    @Override
    public void windowDeactivated ( WindowEvent e )
    {
        //Logger.getInstance().debug ( "-- EltexWindowListener.windowDeactivated" );
    }

}
