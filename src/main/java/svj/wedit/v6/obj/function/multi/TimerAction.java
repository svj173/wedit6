package svj.wedit.v6.obj.function.multi;


import svj.wedit.v6.msg.Msg;
import svj.wedit.v6.tools.Convert;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Акция привязана к секундному таймеру.
 * <BR/>
 * <BR/> Время работы отображаем в бордюрах обеих прогресс-баров.
 * <BR/>
 * <BR/> Уровень логирования INFO - только для ловли проблем с таймером.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 11.10.2013 11:19
 */
public class TimerAction     implements ActionListener
{
    /* Дата начала работы мульти-акции - для отображения суммарного времени в первом прогресс-баре. */
    private long startTime;
    //private JProgressBar totalProgressBar, processProgressBar;
    private Timer timer;
    //private MultiActionSwingWorker swingWorker;
    /* Локальный счетчик для второго прогресс-бара. Его можно сбрасывать в любое начальное значение. */
    private int ic  = 0;
    // старые значения - для того чтобы напарсно не дергать гуи, если ничего не изменилось.
    private String              processOldTitle, totalOldTitle;
    private MultiActionDialog   dialog;


    public TimerAction ( long startTime, MultiActionDialog dialog )
    {
        this.startTime          = startTime;
        this.dialog             = dialog;
        //this.totalProgressBar   = totalProgressBar;
        //this.processProgressBar = processProgressBar;
        processOldTitle         = "";
        totalOldTitle           = "";
    }

    @Override
    public void actionPerformed ( ActionEvent event )
    {
        Border border;
        String msg, timeStr;

        //Logger.getInstance().info ( "TimerAction (",dialog.getName(),").actionPerformed: Start. counter = ", ic );

        //processProgressBar.setString ( Convert.concatObj ( ic, " sec." ) );
        border = dialog.getProcessProgressBar().getBorder();
        //Logger.getInstance().info ( "--- TimerAction (",dialog.getName(),").actionPerformed: processProgressBar border = ", border );
        if ( (border != null) && (border instanceof TitledBorder ) )
        {
            //Logger.getInstance().info ( "---- TimerAction (",dialog.getName(),").actionPerformed: start change processProgressBar border title." );
            timeStr = Convert.sec2str ( ic, Convert.Format.HH_MM_SS );

            if ( ! timeStr.equals ( processOldTitle ) )
            {
                TitledBorder tb = (TitledBorder ) border;

                //tb.setTitle ( Convert.concatObj ( ic, " sec." ) );
                tb.setTitle ( timeStr );
                dialog.getProcessProgressBar().revalidate();
                dialog.getProcessProgressBar().repaint();
                processOldTitle = timeStr;
                //Logger.getInstance().info ( "----- TimerAction (",dialog.getName(),").actionPerformed: change processProgressBar border title = ", timeStr );
            }
        }

        if ( dialog.getSwingWorker().isDone() )
        {
            // закрыть диалог  -- ???
            //dialog.close();
            timer.stop();
            ic  = 0;
            //msg = "  Работа завершена ";
            msg = Msg.getMessage ( "common.message.finished_process" );
            // Отключаем движение бесконечного бегунка - если оно было.
            dialog.getProcessProgressBar().setIndeterminate ( false );
            //
            //dialog.setCancelButtonText ( "Закрыть" );
            dialog.setCancelButtonText ( Msg.getMessage ( "system.gui.dialog.button.close" ) );
        }
        else
        {
            ic++;
            msg = null;
        }

        printTotalTime ( msg, event.getWhen() );

        //Logger.getInstance().info ( "TimerAction (",dialog.getName(),").actionPerformed: Finish. counter = ", ic );
    }

    public void printTotalTime ( String msg, long when )
    {
        long         time;
        String       timeStr;
        Border       border;
        TitledBorder tb;

        //Logger.getInstance().info ( "--- TimerAction (",dialog.getName(),").printTotalTime: Start" );
        border = dialog.getTotalProgressBar().getBorder();
        //Logger.getInstance().info ( "--- TimerAction.printTotalTime: totalProgressBar border = ", border );

        if ( (border != null) && (border instanceof TitledBorder ) )
        {
            time    = (when - startTime) / 1000;
            timeStr = Convert.sec2str ( time, Convert.Format.HH_MM_SS );
            if ( msg != null )  timeStr = timeStr + msg;

            if ( ! timeStr.equals ( totalOldTitle ) )
            {
                tb = (TitledBorder ) border;

                tb.setTitle ( timeStr );

                // иначе не рисуется, когда дергается из акций передачи данных, а не по таймеру.
                dialog.getTotalProgressBar().revalidate();
                dialog.getTotalProgressBar().repaint();

                totalOldTitle = timeStr;
                //Logger.getInstance().info ( "--- TimerAction (",dialog.getName(),").printTotalTime: change totalProgressBar border title = ", timeStr );
            }
        }
        //Logger.getInstance().info ( "--- TimerAction (",dialog.getName(),").printTotalTime: Finish" );
    }

    public void setTimer ( Timer timer )
    {
        this.timer = timer;
    }

    public void setCurrentProcessCounter ( int ic )
    {
        this.ic = ic;
    }

}
