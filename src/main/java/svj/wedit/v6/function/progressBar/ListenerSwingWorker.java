package svj.wedit.v6.function.progressBar;


/**
 * <BR/>
 */
//public class ListenerSwingWorker

import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Обработчик с прогресс-баром ожидания результата.
 * <BR/> Выполняется в отдельном потоке.
 * <BR/>
 * <BR/> Результат - ResponseObject
 * <BR/>  - Ошибка если ответ содержит исключение.
 * <BR/>  - Response передается в getObject
 * <BR/>
 * <BR/> ResponseObject, Void - обьекты передачи для взаимодействия publish-process
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 05.04.2013 15:56
 */
public class ListenerSwingWorker extends SwingWorker<ResponseObject, Void>
{
    private ProgressBarFunction function;
    private ActionEvent         event;

    public ListenerSwingWorker ( ProgressBarFunction function, ActionEvent event )
    {
        this.function   = function;
        this.event      = event;
    }

    @Override
    protected ResponseObject doInBackground () throws Exception
    {
        ResponseObject  result;


        // индикатор типа окончания работы. Если NULL - все ОК, иначе это текст об ошибке.
        result      = new ResponseObject();
        if ( function == null ) return result;

        try
        {
            // null - т.к. нет возможности в другом потоке передать данные в сигнатуру, так что обработчик должен заранее
            //  (в конструкторе или сеттерами) занести себе необходимые для работы параметры.

            function.handleWithProgress ( event );

            result.setObject ( new Boolean(true) );

        } catch ( WEditException ex )        {
            result.setException ( ex );
        } catch ( Exception e )              {
            Log.l.error ( "err", e );
            result.setException ( new WEditException ( e ) );
        }
        return result;
    }

}
