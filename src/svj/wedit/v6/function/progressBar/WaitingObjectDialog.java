package svj.wedit.v6.function.progressBar;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 05.04.2013 16:07
 */

import svj.wedit.v6.Par;
import svj.wedit.v6.exception.MessageException;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.GuiTools;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Диалог отображения ожидания. (WAIT-режим)
 * <BR/> Отсутствует возможность закрыть его по крестику (прервать процесс).
 * <BR/> Бесконечный бегунок, в середине которого отображаются секунды.
 * <BR/> Закрывается по окончанию процесса.
 * <BR/> При ошибках в работе - предварительно выводится окно с сообщением.
 * <BR/> Результат работы - обьектом. Если ошибка работы - в обьекте содержится исключение.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 27.04.2012 13:42:30
 */
public class WaitingObjectDialog extends JDialog
{
    public final static int ONE_SECOND = 1000;

    /* Генератор секунд ожидания */
    private Timer           timer;
    /* Счетчик секунд ожидания */
    private int             ic;
    private JProgressBar    progressBar;
    /* Обработчик, окончания работы которого мы и ожидаем. Может быть null. */
    private SwingWorker     worker;
    /* Макс время. Может отсутствовать (меньше 0). */
    private final int       maxTimeout;


    public WaitingObjectDialog ( SwingWorker swingWorker, int maxTimeout, Object... title )     throws WEditException
    {
        super ( Par.GM.getFrame(), Convert.concatObj ( title ), true );

        Border border;

        Log.l.debug ( "WaitingDialog:: Start." );

        this.maxTimeout = maxTimeout;

        worker  = swingWorker;

        setLayout ( new BorderLayout() );
        setSize ( 500, 80 );

        // убрать служебные - Убрать вниз, Изменить размер, Закрыть
        // убирает всю рамку окна (титл и кнопки - Убрать вниз, Изменить размер, Закрыть. Остаются только созданные компоненты
        //setUndecorated(true);
        // убирается кнопка Изменения размера
        setResizable ( false );

        // блокируем крестик (по нажатию ничего не происходит)
        setDefaultCloseOperation ( WindowConstants.DO_NOTHING_ON_CLOSE );

        progressBar = new JProgressBar ( 0, 2000 );

        // рисуем бесконечный бегунок вправо-влево
        progressBar.setIndeterminate ( true );

        progressBar.setValue ( 0 );
        progressBar.setStringPainted ( true );

        // пустой бордюр - т.е. просто отступы по краям.
        //border = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        // бордюр вокруг бегунка - c текстом
        border = BorderFactory.createTitledBorder ( "Ожидание..." );

        progressBar.setBorder ( border );

        add ( progressBar, BorderLayout.CENTER );

        ic      = 0;
        // Create a timer.
        timer   = new Timer (ONE_SECOND, new ActionListener()
        {
            @Override
            public void actionPerformed ( ActionEvent evt )
            {
                //progressBar.setValue(ic);
                if ( WaitingObjectDialog.this.maxTimeout < 0 )
                {
                    progressBar.setString ( Convert.concatObj ( ic, " sec." ) );
                }
                else
                {
                    progressBar.setString ( Convert.concatObj ( ic, '/', WaitingObjectDialog.this.maxTimeout, " sec." ) );
                }
                ic++;
                if ( worker != null )
                {
                    if ( worker.isDone() )
                    {
                        // закрыть диалог
                        close();
                    }
                }
            }
        });

        Log.l.debug ( "WaitingDialog:: Finish" );
    }

    public void close ()
    {
        Toolkit.getDefaultToolkit().beep();
        setCursor(null); //turn off the wait cursor
        timer.stop();

        /*
        try
        {
            // при ошибках в работе - предварительно вывести окно с сообщением
            // - НЕТ. Про ошибки каждый обработчик сам сообщит. Тем более что NOT NULL - это не всегда ошибка. svj, 2011-10-20
            Object result = worker.get();
            if ( result != null ) JOptionPane.showMessageDialog ( this, result.toString(), "Ошибка", JOptionPane.ERROR_MESSAGE );

        } catch ( Exception e )        {
            Log.l.error ( "WaitingDialog.close:", e );
        }
        */

        setVisible(false);
        dispose();
    }

    public void start ()   throws WEditException
    {
        start ( null );
    }

    public void start ( Container container )   throws WEditException
    {
        try
        {
            GuiTools.setDialogScreenCenterPosition ( this, container );

            if ( worker != null ) worker.execute();
            setCursor ( Cursor.getPredefinedCursor ( Cursor.WAIT_CURSOR ) );
            timer.start();

        } catch ( Exception e )      {
            Log.l.error ( Convert.concatObj ( "WaitingDialog (", getName (), ").start:" ), e);
            close();
            if ( e instanceof WEditException )
                throw (WEditException) e;
            else
                throw new WEditException ( e, "Ошибка выполнения команды :\n ", e );
        }

        setVisible ( true );
    }

    public ResponseObject getResultMsg ()
    {
        Object          obj;
        ResponseObject  result;

        result  = new ResponseObject();
        try
        {
            if ( worker != null )
            {
                obj = worker.get(); // должен вернуть ResponseObject
                if ( obj != null )
                {
                    if ( obj instanceof ResponseObject )
                    {
                        result = (ResponseObject) obj;
                    }
                }
                else
                {
                    result.setException ( new MessageException ( "Получен NULL ответ.") );
                }
            }
        } catch ( Exception e )     {
            result  = new ResponseObject();
            result.setException ( new WEditException(e) );
            Log.l.error ( Convert.concatObj ( "WaitingObjectDialog (", getName (), ").getResultMsg: " ), e);
        }

        return result;
    }

}