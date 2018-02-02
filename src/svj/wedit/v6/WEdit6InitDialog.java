package svj.wedit.v6;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.dialog.WDialog;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.open.OpenParamsData;
import svj.wedit.v6.tools.GuiTools;

import javax.swing.*;
import java.awt.*;

/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 02.04.2014 11:34
 */
public class WEdit6InitDialog  extends WDialog<Void,OpenParamsData>
{
    private final JTextArea   textPanel;
    private WEdit6InitWorker  swingWorker;

    public WEdit6InitDialog ( String title )
    {
        super ( (Window ) null, title );

        textPanel = new JTextArea();
        textPanel.setEditable ( false );
        //textPanel.setRows ( 10 );

        addToCenter ( new JScrollPane ( textPanel ) );

        setPreferredSize ( new Dimension ( 500, 400 ) );
        // блокируем крестик (по нажатию ничего не происходит) - чтобы выход только по Отмена, по Ошибке или по Окончанию работы.
        setDefaultCloseOperation ( WindowConstants.DO_NOTHING_ON_CLOSE );   // почему-то не отрабатывает. возможно - надо создать свой класс диалога (лишняя сущность).

        // убрать кнопки
        disableOkButton();
        disableCancelButton();

        // Всегда первым, чтобы не терялся среди других окон.
        try
        {
            setAlwaysOnTop ( true );
        } catch ( Exception e )        {
            // Ошибки например из-за permission на опцию setWindowAlwaysOnTop
            e.printStackTrace();
        }

        //pack ();
    }

    public void showDialog ()
    {
        // Рассчитать размер диалога в зависимости от размера экрана
        createDialogSize();

        // центрируем
        GuiTools.setDialogScreenCenterPosition ( this );

        // - создаем swingWorker - T, D.  -- T - что возвращает doInBackground. D - что передается в publish-process.
        swingWorker   = new WEdit6InitWorker ( this );

        //setVisible ( true );

        // - запускаем  backgroundProcess
        swingWorker.execute();

        setVisible ( true );
    }

    /*
    public void startProcess ( long startTime )
    {
        //timerListener = new TimerAction ( startTime, this );
        //timer         = new Timer ( CCons.ONE_SECOND, timerListener );
        //timerListener.setTimer ( timer );

        // - создаем swingWorker - T, D.  -- T - что возвращает doInBackground. D - что передается в publish-process.
        swingWorker   = new WEdit6InitWorker ( this );

        //timer.start();

        // - запускаем  backgroundProcess
        swingWorker.execute();
    }
    */


    public void addText ( String msg )
    {
        textPanel.append ( msg );
        textPanel.append ( "\n" );
    }

    @Override
    public void init ( Void initObject ) throws WEditException
    {

    }

    @Override
    public OpenParamsData getResult () throws WEditException
    {
        OpenParamsData result;
        try
        {
            result = swingWorker.get();
        } catch ( Exception e )        {
            result = null;
            Log.l.error ( "error", e );
        }
        return result;
    }

    protected void createDialogSize ()
    {
        pack();
    }

}
