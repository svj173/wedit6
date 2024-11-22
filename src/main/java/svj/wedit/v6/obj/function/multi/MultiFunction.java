package svj.wedit.v6.obj.function.multi;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.progressBar.ResponseObject;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.msg.Msg;
import svj.wedit.v6.obj.WorkResult;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.Convert;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Функция с диалогом отображения процесса работы и с кнопкой "Отмена".
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 16.05.2014 13:12
 */
public abstract class MultiFunction  extends Function
{
    private boolean                 cancelMode;

    private String                  title;
    private int                     actionSize;

    private MultiActionDialog       dialog;
    //private static MultiActionDialog       dialog  = null;

    /* TRUE - закрывать диалог после завершения работы. FALSE - оставлять открытым. */
    private boolean                 needCloseDialog;

    /* Тип GUI отображения мультиакции  - показать все прогресс-бары, только один... */
    private MultiActionViewMode     viewMode    = MultiActionViewMode.ALL;

    private Dimension dialogSize;


    // --------------------------- abstract ---------------------------

    /**
     * Запускается в отдельном потоке. В него передача - publish (например, прервать работу).
     * Из него наружу - process (например, о завершении какой-то стадии работы и передача титла следующей работы. Либо проценты для прогресс-бара).
     * @return  NULL - процесс завершился без ошибок. NOT NULL - сообщение об ошибке (Лучше - обьект ResponseObject)
     */
    protected abstract ResponseObject backgroundMultiActionProcess ();


    protected MultiFunction ()
    {
        // Нельзя, т.к. в конструкторе идет обращение к Par.GM.getFrame(), которого еще нет.
        //dialog      = new MultiActionDialog (  Par.GM.getFrame(), this );
        dialogSize = new Dimension ( 600, 400 );
    }

    /**
     * Запуск механизмов по мультиакциям. Выполняется в AWT потоке.
     * @param event    Событие.
     * @throws WEditException  Непредвиденные ошибки. Все остальные ошибки отображаются в диалоге мультиакции.
     */
    public void handle ( ActionEvent event ) throws WEditException
    {
        long    startTime;

        if ( beforeHandle() )
        {
            Log.l.debug ( "MultiFunction (%s).handleMultiAction: Start. needCloseDialog = %b", getName(), isNeedCloseDialog() );

            startTime   = System.currentTimeMillis();

            dialog      = new MultiActionDialog (  Par.GM.getFrame(), this );
            Log.l.debug ( "MultiFunction (%s).handleMultiAction: Create dialog = %s", getName(), dialog );

            try
            {
                // запускаем все рабочие background процессы диалога
                dialog.startProcess ( startTime );

                // - открываем диалог - попутно он сам заносится в стек Par.DIALOG
                //if ( isUseProgressBar() )  dialog.showDialog();    -- ??? isUseProgressBar - это только для режима НЕ мультиакции.
                dialog.showDialog();

                // Здесь модальный диалог уже закрылся - ничего дальше не делаем, т.к. все уже сделано.

                afterHandle ( event );

            } catch ( Exception e )  {
                // Какая-то непредсказуемая ошибка - отрисовать ее в диалоге и не закрывать его
                // Системная ошибка на выполнение акций. Здесь у swingWorker state = done.
                // На самом деле ошибок здесь быть не должно т.к. все ошибки в процессе работы отлавливаются в самом диалоге.
                Log.l.error ( Convert.concatObj ( "MultiFunction (", getName(), ").handleMultiAction: very strong error !!!") , e );
            }

            //dialog.closeDialog();   -- само вызывается по статусу MultiActionCmd.CLOSE_DIALOG - окончание работы MultiActionSwingWorker.doInBackground
            //dialog.setVisible ( false );

            dialog = null;
            Log.l.debug ( "MultiFunction (%s).handleMultiAction: Finish", getName() );
        }
    }

    /**
     * Какие-то предварительные установки. Диалог запроса чего либо и т.д.
     * @return TRUE - продолжить работу. FALSE - больше ничего не делать (отказ от операции).
     * @throws WEditException Непредвиденные ошибки.
     */
    protected boolean beforeHandle () throws WEditException { return true; }

    public boolean isCancelMode ()
    {
        return cancelMode;
    }

    public void setCancelMode ( boolean cancelMode )
    {
        this.cancelMode = cancelMode;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle ( String title )
    {
        this.title = title;
    }

    public int getActionSize ()
    {
        return actionSize;
    }

    public void setActionSize ( int actionSize )
    {
        this.actionSize = actionSize;
    }

    public MultiActionDialog getDialog ()
    {
        return dialog;
    }

    /*
    public void setDialog ( MultiActionDialog dialog )
    {
        this.dialog = dialog;
    }
    */

    public boolean isNeedCloseDialog ()
    {
        return needCloseDialog;
    }

    public void setNeedCloseDialog ( boolean needCloseDialog )
    {
        this.needCloseDialog = needCloseDialog;
    }

    public MultiActionViewMode getViewMode ()
    {
        return viewMode;
    }

    public void setViewMode ( MultiActionViewMode viewMode )
    {
        this.viewMode = viewMode;
    }

    // переписывается
    public void clear ()     {  }

    protected  void afterHandle ( ActionEvent event ) throws WEditException {}

    /**
     * Обработка возникшей ошибки. В AWT потоке.
     * <br/> Здесь просто отображаем текст ошибки, изменяем размер диалога, чтобы ошибку было видно. Диалог не закрываем.
     * <vr/> Если необходимо что-то еще - листенер данный метод переписывает. (Например, при копировании файла - переспрос на продолжение работы в случае ошибки crc).
     * @param e  Ошибка
     */
    public void handleError ( Exception e ) //throws EltexException
    {
        //Logger.getInstance().debug ( "MultiFunction (",getName(),").handleError: Start" );
        //if ( dialog != null )
        //{
            String mess = (e!=null && e.getMessage()!=null && e.getMessage().length()>0) ? e.getMessage() : Convert.concatObj ( e );
            Log.l.debug ( "MultiFunction (%s).handleError: err_mess = %s", getName(), mess );
            dialog.setErrorMsg ( mess );
        //}
    }

    public void handleMsg ( String msg )
    {
        //Logger.getInstance().debug ( "MultiFunction (",getName(),").handleMsg: Start" );
        dialog.addMsg ( msg );
    }

    public void setMultiActionMode ( String dialogTitle, int actionSize, boolean closeDialog, MultiActionViewMode viewMode )
    {
        Log.l.debug ( "MultiFunction (%s).setMultiActionMode: Start", getName() );
        title               = dialogTitle;
        this.actionSize     = actionSize;
        this.needCloseDialog = closeDialog;
        this.viewMode       = viewMode;
        cancelMode          = false;
        //dialog              = null;
        Log.l.debug ( "MultiFunction (%s).setMultiActionMode: Finish", getName() );
    }

    /* Вызов при физическом закрытии диалога мультиакции. */
    public void close ()
    {
        Log.l.debug ( "MultiFunction (%s).close: Start", getName() );

        dialog.setReturnStatus ( WorkResult.CANCEL );
        dialog.stopProcess();

        // Сам диалог закроется далее - своим чередом.
        Log.l.debug ( "MultiFunction (%s).close: Finish", getName() );
    }


    /**
     * Начальная настройка прогресс-бара суммарного процесса.
     * <br/> Дергается в bg-потоке в bg-методе листенера.
     * @param actionsSize  Общее кол-во процессов.
     */
    protected void initTotalProgressBar ( int actionsSize )
    {
        MultiData data;

        data = new MultiData ( MultiActionCmd.INIT_TOTAL_PROGRESS_BAR );
        data.setStepSize ( actionsSize );  // здесь хранится кол-во акций

        Log.l.debug ( "MultiFunction (%s).initTotalProgressBar: dialog = %s", getName(), dialog );
        // передаем из none-awt-потока в awt-поток
        dialog.getSwingWorker().publishData ( data );
    }

    /**
     * Начальная настройка прогресс-бара текущего процесса.
     * Передача данных из фонового потока в AWT. Для отрисовок в ГУИ.
     * Пришло из bg-потока из bg-метода листенера.
     * Метод вызывается из NONE awt-потока (background-потока) в SwingWorker (awt-поток). Установить исходные значения для первого прогресс-бара. -- т.е. необходимо передать команду в awt-поток.
     *
     * @param type         Тип передаваемых данных: PACKET, DATA, UNTIME.
     * @param totalSize    Общий размер передаваемых данных - только для типа PACKET.
     * @param stepSize     Шаг передаваемых данных - только для типа PACKET.
     * @param processTitle Заголовок, который надо отрисовать в списке выполняемых задач - в текстовом поле внизу.
     */
    protected void initProcessProgressBar ( ProgressBarType type, long totalSize, int stepSize, String processTitle )
    {
        MultiData data;

        data = new MultiData ( MultiActionCmd.INIT_FIRST_PROGRESS_BAR );
        data.setProgressBarType ( type );
        data.setTotalSize ( totalSize );
        data.setStepSize ( stepSize );
        data.setProcessTitle ( processTitle );

        // передаем из none-awt-потока в awt-поток
        dialog.getSwingWorker().publishData ( data );
    }


    /**
     * Увеличить кол-во выполненых процессов на 1. Для отрисовки.
     * <br/> Дергается в bg-потоке в bg-методе листенера.
     */
    protected void incTotalProgressBar ()
    {
        MultiData data;

        data = new MultiData ( MultiActionCmd.INC_TOTAL_PROGRESS_BAR );

        // передаем из none-awt-потока в awt-поток
        dialog.getSwingWorker().publishData ( data );
    }

    /**
     * Команда - закрыть диалог.
     * <br/> Дергается в bg-потоке в bg-методе листенера.
     */
    protected void setCloseDialog ()
    {
        MultiData data;

        data = new MultiData ( MultiActionCmd.CLOSE_DIALOG );

        // передаем из none-awt-потока в awt-поток
        dialog.getSwingWorker().publishData ( data );
    }

    /**
     * Ошибка в работе - отрисовать ее в диалоге.
     * Дергается в листенерах, в методах обработки текущего процесса.
     * <br/> Дергается в bg-потоке в bg-методе листенера. Для передачи в AWT поток.
     *
     * @param ex  Ошибка
     */
    protected void processError ( Exception ex )
    {
        MultiData               data;
        MultiActionSwingWorker  swingWorker;

        try
        {
            data = new MultiData ( MultiActionCmd.ERROR );
            //data.setProcessTitle ( ex.getMessage() );
            data.setException ( ex );
            data.setError ( true );

            // передаем из none-awt-потока в awt-поток
            swingWorker = dialog.getSwingWorker();
            Log.l.debug ( "--- swingWorker = %s", swingWorker );
            if ( swingWorker != null )  swingWorker.publishData ( data );

        } catch ( Exception e )         {
            Log.l.error ( "error", e );
        }
    }

    protected void processMsg ( Object... msg )
    {
        MultiData data;

        data = new MultiData ( MultiActionCmd.MSG );
        data.setProcessTitle ( Convert.concatObj ( msg ) );

        // передаем из none-awt-потока в awt-поток
        dialog.getSwingWorker().publishData ( data );
    }

    /* Проверка прерывания ОТ_ГУИ, т.к. прерывания от сервера идут только как исключения.
       Исключение - чтобы в листенере смогли определиться - выводить сообщение ошибки от сервера или нет.
       Вызывается в background-потоке.
       */
    protected void processCancel () throws WEditException
    {
        if ( dialog!=null && dialog.getSwingWorker()!=null && dialog.getSwingWorker().isCancelled () ) {
            // генерит исключение с кодом ОТ_ГУИ, т.к. прерывания от сервера идут только как исключения.
            //throw new EltexException ( EltexException.INTERRUPT_COMMAND, "Прервано оператором." );
            throw new WEditException ( WEditException.INTERRUPT_COMMAND, null, Msg.getMessage ( "common.errors.message.command.interrupt_by_user" ) );
        }
    }

    /**
     *  Передать данные из background-потока в awt-поток - для отрисовок в гуи-компонентах.
     * <br/> Дергается в bg-потоке в bg-методе листенера - при пакетном копировании - здесь передаются данные о переданном пакете - для отрисовки.
     */
    protected void publishData ( MultiData data )
    {
        dialog.getSwingWorker().publishData ( data );
    }

    public void setCurrentProcessCounter ( int ic )
    {
        //timerListener.setCurrentProcessCounter ( ic );
        dialog.setCurrentProcessCounter ( ic );
    }

    public void cancel ()
    {
        cancelMode = true;
    }

    public Dimension getDialogSize ()
    {
        return dialogSize;
    }

    public void setDialogSize ( Dimension dialogSize )
    {
        this.dialogSize = dialogSize;
    }

}
