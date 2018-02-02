package svj.wedit.v6.function.progressBar;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.obj.function.Function;

import java.awt.event.ActionEvent;

/**
 * Функция, действие которой сопровождается бегущим по экрану прогресс-баром.
 * <BR/>
 * <BR/> T - обьект ответа.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 05.04.2013 15:51
 */
public abstract class ProgressBarFunction extends Function
{
    //private T    response = null;


    public abstract boolean beforeHandle () throws WEditException;

    /* Работа, которая и будет сопровождаться прогресс-баром. */
    public abstract void handleWithProgress ( ActionEvent event ) throws WEditException;

    public abstract void afterHandle ();


    /**
     * Выполняется сложная команда. На время выполнения запускается прогресс-бар.
     * <BR/> Аналогичная функция, но только по обработке сокетной команды - DataExchangeManager.getInstance().sendWithProgressBar
     * <BR/>
     * @throws WEditException   Ошибки при выполнении команды.
     */

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        ListenerSwingWorker worker;
        WaitingObjectDialog waitingDialog;
        ResponseObject      ro;

        if ( beforeHandle() )
        {
            // В Воркер передаем сами себя. Воркер сам дернет у функции метод handleWithProgress.
            worker  = new ListenerSwingWorker ( this, event );

            waitingDialog = new WaitingObjectDialog ( worker, -1, "Процесс..." );
            waitingDialog.start();

            // Ошибки worker
            ro    = waitingDialog.getResultMsg();
            //Logger.getInstance().debug ( "--- GuiTools.handleWithProgressBar: ro = ", ro );
            if ( ro.isError() )
            {
                // Ошибка в работе - передать специальному обработчику
                //if ( exceptListener != null ) exceptListener.handleReload();
                throw ro.getException();
            }
            else
            {
                // Все ОК
                // Лишнее, т.к. пока всегда возвращает true - если не было ошибок.
                //response  =  (T) ro.getObject();
                afterHandle();
            }
        }
    }

    /*
    public T getResponse ()
    {
        return response;
    }
    */

}
