package svj.wedit.v6.obj.function.multi;


import svj.wedit.v6.exception.SystemErrorException;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.progressBar.ResponseObject;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.tools.Convert;

import javax.swing.*;
import java.util.List;

/**
 * Background поток для работы мульти-акций.
 * <BR/> Основная задача - передавать данные на отрисовку бегунков.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 28.08.2013 10:45
 */
public class MultiActionSwingWorker  extends SwingWorker<ResponseObject,MultiData>
{
    private final MultiFunction multiFunction;


    public MultiActionSwingWorker ( MultiFunction multiFunction )
    {
        this.multiFunction = multiFunction;
    }

    @Override
    protected ResponseObject doInBackground () throws Exception
    {
        ResponseObject result;

        // Имя здесь не меняем т.к. у каждой акции multiListener.backgroundMultiActionProcess - свое имя. Там и меняем.
        //Thread.currentThread().setName ( "MultiActionWorker_" + multiListener.getName() );
        Log.l.debug ( "MultiActionSwingWorker (%s).doInBackground: Start.", multiFunction.getName() );

        try
        {
            result  = multiFunction.backgroundMultiActionProcess();

            // Сообщить в диалог что работа закончена успешно
            MultiData data = new MultiData ( MultiActionCmd.CLOSE_DIALOG );
            publish ( data );

        } catch ( Exception e )        {
            Log.l.error ( Convert.concatObj ( "MultiActionSwingWorker (", multiFunction.getName(),").doInBackground: error"), e );
            // Исключение не должно выходить наружу - только как обьект ResponseObject -- и текст отображаться в диалоге.
            result = new ResponseObject();
            if ( e instanceof WEditException )
                result.setException ( (WEditException ) e );
            else
                result.setException ( new SystemErrorException ( e ) );
        }

        Log.l.debug ( "MultiActionSwingWorker (%s).doInBackground: Finish. result = %s", multiFunction.getName(), result );
        return result;
    }

    /**
    Метод вызывается когда doInBackground завершит свою работу.
     */
    @Override
    protected void done ()
    {
        /*
        ResponseObject result;

        result = null;
        try
        {
            result = get();
        } catch ( Exception ignore )      {
            Log.l.error ( ignore, "MultiActionSwingWorker (", multiListener.getName (), ").done: error" );
        }
        Log.l.debug ( "MultiActionSwingWorker (",multiListener.getName(),").done: Finish. result = ", result );
        */
        Log.l.debug ( "MultiActionSwingWorker (%s).done: Finish.", multiFunction.getName() );
    }

    /**
     * Получить - в потоке AWT - данные - из background-потока -- для начальных настроек, отрисовки бегунка.
     * <br/> Здесь же должен отслеживать ситуации с ошибками ГУИ-стороны и прерыванием работы от сервера.
     * <br/>
     * @param list  Массив данных о размере переданных байт файла либо инфа об ошибке.
     */
    protected void process ( List<MultiData> list )
    {
        boolean     error, sendData;

        Log.l.debug ( "MultiActionSwingWorker (%s).process: Start. isCancelled() = %b; data = %s", multiFunction.getName(), isCancelled(), list );

        if ( isCancelled() )  return;   // не рисовать если было прерывание задачи

        error       = false;
        sendData    = false;

        // размер уже переданной информации
        for ( MultiData data : list )
        {
            // Проверка на глобальную ошибку
            if ( data.isError() )
            {
                // Рисовать в прогресс-баре слово 'Ошибка' ???
                multiFunction.handleError ( data.getException() );
                error = true;
                break;
            }
            else
            {
                switch ( data.getCmd() )
                {
                    case INIT_FIRST_PROGRESS_BAR:
                        if ( multiFunction.getDialog()!=null)
                            multiFunction.getDialog().setupProcessProgressBar ( data );
                        break;
                    case INIT_TOTAL_PROGRESS_BAR:
                        if ( multiFunction.getDialog()!=null)
                            multiFunction.getDialog().setupTotalProgressBar ( data );
                        break;
                    case INC_TOTAL_PROGRESS_BAR:
                        if ( multiFunction.getDialog()!=null)
                            multiFunction.getDialog().paintIncTotalProgressBar();
                        break;
                    case ERROR:
                        multiFunction.handleError ( data.getException() );
                        break;
                    case MSG:
                        // Просто какое-то сообщение
                        multiFunction.handleMsg ( data.getProcessTitle() );
                        break;
                    case CLOSE_DIALOG:
                        if ( multiFunction.getDialog()!=null)
                            multiFunction.getDialog().closeDialog();
                        break;
                    case SEND_DATA:
                        // подсчитываем размер переданных данных
                        //count = count + data.getSendSize();
                        if ( multiFunction.getDialog()!=null)
                            multiFunction.getDialog().addData ( data.getSendSize(), data.getPacketNumber() );
                        sendData    = true;
                        break;
                }
            }
        }

        if ( (! error) && sendData )
        {
            // гуи-обработка прогресс-бара. условие - если были переданы данные.
            if ( multiFunction.getDialog()!=null)
                multiFunction.getDialog().paintFirstProgressBarData();
        }

        Log.l.debug ( "MultiActionSwingWorker (%s).process: Finish. error = %b; sendData = %b", multiFunction.getName(), error, sendData );
    }

    public void publishData ( MultiData... chunks )
    {
        super.publish ( chunks );
    }

    public String toString()
    {
        StringBuilder result;

        result = new StringBuilder();
        result.append ( "[ MultiActionSwingWorker: state = " );
        result.append ( getState() );
        result.append ( "; action = " );
        result.append ( multiFunction.getName () );
        result.append ( "; progress = " );
        result.append ( getProgress() );
        result.append ( " ]" );

        return result.toString();
    }

}
