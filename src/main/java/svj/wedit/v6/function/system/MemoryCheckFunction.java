package svj.wedit.v6.function.system;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 16.04.2013 10:56
 */

import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.params.SimpleParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.function.SimpleFunction;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.DialogTools;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Выводить данные об используемой программой памяти.
 * <BR> Выводит в панель статуса. В мегабайт.
 * <BR> Формат: Используемая of Доступная/Макс_возможная.
 * <BR>
 * <BR> Также сохраняет и все книги  -- только если были изменения.
 * <BR> -- Отключил т.к. какой-то косяк при одновременнйо работе ручного сохранения и авто - главы (глава) перемешиваются построчно. 2015-09-23
 * <BR>
 * <BR> User: Zhiganov
 * <BR> Date: 26.11.2007
 * <BR> Time: 16:14:32
 */
public class MemoryCheckFunction  extends SimpleFunction implements Runnable
{
    private boolean     running = false;

    /**
     * Ссылка на swing обьект, который располагается на панели статуса
     *  и отображает состояние выделенной Редактору памяти.
     */
    private JLabel memory;

    /* Таймер по умолчанию, в мсек. */
    private final int     sleepTimeDef   = 60000;    // 60 sec

    /* Таймер. Берется из параметра. */
    private int     sleep;

    /* Поток таймера. Необходим для прерывания при закрытии Редактора. */
    private Thread  thread   = null;

    private final   String  PAR_TIMER   = "memory_time";


    public MemoryCheckFunction ()
    {
        setId ( FunctionId.MEMORY_CHECK );
        setName ( "Данные об используемой оперативной памяти. Автосохранение книг. Период в минутах." );
    }

    @Override
    public String getToolTipText ()
    {
        //return "Данные об используемой оперативной памяти. Автосохранение книг. Период в минутах.";
        return getName();
    }

    public void rewrite ()
    {
        // Изменили время - изменить в самом процессе
        SimpleParameter param;
        int             it;

        // Взять значение таймера
        param   = (SimpleParameter) getParameter ( PAR_TIMER );
        if ( param != null )
        {
            it  = Convert.getInt ( param.getValue(), -1 );
            if ( it > 0 )  sleep    = it * 60000;  // пересчет минут в миллисекунды.
        }

        param   = getSimpleParameter ( PAR_TIMER, Integer.toString (sleep / 60000) );
        try
        {
            it  = Integer.parseInt ( param.getValue() );
            if ( it <= 0 )
            {
                sleep = sleepTimeDef;
                param.setValue ( Integer.toString (sleep / 60000) );
            }
            else
            {
                sleep = it * 60000;
            }
        } catch (Exception e )      {
            Log.l.error ( "err", e );
            sleep = sleepTimeDef;
            param.setValue ( Integer.toString (sleepTimeDef / 60000) );
        }
    }

    public void start ()
    {
        SimpleParameter param;
        Integer         it;

        sleep   = sleepTimeDef;
        // Взять значение таймера
        param   = (SimpleParameter) getParameter ( PAR_TIMER );
        if ( param != null )
        {
            it  = Convert.getInt ( param.getValue(), -1 );
            if ( it > 0 )    sleep    = it * 60000;
        }

        // Получить доступ к метке отображения используемой памяти
        memory  = Par.GM.getFrame().getServicePanel().getMemoryLabel();

        if ( ! running )
        {
            thread = new Thread ( this, "MemoryThread" );
            thread.setDaemon ( true );
            thread.start ();
            running = true;
        }
    }

    public void close ()
    {
        running = false;
        // Остановить таймер если запущен Shutdown - проверить
        if ( (thread != null) && Par.SHUTDOWN_STARTED )     thread.interrupt();
    }

    public void run ()
    {
        String  msg;
        Runtime runtime;
        long    allocatedMemory, freeMemory, useMemory, maxMemory, M;
        //SaveAllProjectsFunction  function;

        M           = 1048576;       // Для пересчета байт в Мб.
        //function    = new SaveAllProjectsFunction();
        // ставим флаг  - работаем без применения диалогов
        //function.setUseDialog ( false );

        while ( running )
        {
            runtime         = Runtime.getRuntime();
            maxMemory       = runtime.maxMemory();       // Всего памяти можно использовать
            allocatedMemory = runtime.totalMemory();     // Выделено памяти сейчас
            freeMemory      = runtime.freeMemory();      // Свободно памяти сейчас
            useMemory       = allocatedMemory - freeMemory;   // Используется памяти сейчас
            maxMemory       = maxMemory / M;
            useMemory       = useMemory / M;
            allocatedMemory = allocatedMemory / M;
            msg             = useMemory + "M of " + allocatedMemory + "M/" + maxMemory + "M";
            memory.setText ( msg );
            //logger.debug ( "Memory: " + msg );

            /* -- Отключил т.к. какой-то косяк при одновременнйо работе ручного сохранения и авто - главы (глава) перемешиваются построчно.
            // Автосохранение сохранение книг
            Log.l.info ( "Process autosave edit books..." );
            try
            {
                function.handle ( null );
            } catch ( Exception e )    {
                Log.l.info ( "Process autosave edit books error.", e );
            }
            */

            Thread.yield();
            try {
                Thread.sleep ( sleep );
            } catch ( InterruptedException e ) {
                Log.f.info ( "Interrupt memory checker" );
                running = false;
            }
        }
    }

    /**
     * Изменение времени для таймера.
     */
    public void handle ( ActionEvent  event )     throws WEditException
    {
        String          period;
        SimpleParameter sp;

        //Log.l.debug ("Start");

        try
        {
            // Меняем размер иконки
            // Выводим диалог  - Component parentFrame, String title, Object msg
            period  = DialogTools.showInput ( Par.GM.getFrame(), "Период опроса.", "Период опроса ОЗУ и автосохранения книг, в минутах." );

            if ( period != null )
            {
                sp  = getSimpleParameter ( PAR_TIMER, Integer.toString (sleep / 60000) );
                // Сохраняем выбранное значение
                sp.setValue ( period );

                // Обновляем меню этой функции
                rewrite();
            }
        }  catch ( Exception e )        {
            Log.l.error ( "err", e );
            throw new WEditException ( e, "Ошибка смены периода опроса ОЗУ и автосохранения '", event.getActionCommand(), "':\n", e );
        }
    }

}
