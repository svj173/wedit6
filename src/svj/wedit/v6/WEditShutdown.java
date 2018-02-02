package svj.wedit.v6;


import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.project.SaveAllProjectsFunction;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.function.Function;

/**
 * Аварийный останов Редактора по системному вызову - Ctrl/C, сброс питания, вызову System.exit().
 * Запросы пользвоателя и вывод ему сообщений здесь уже не проходят,
 * поэтому система старается всячески сохранить книгу. Если нет файла,
 * куда скидывать книгу - скидывает в темпорари - задается в параметрах
 * функции Shutdown.
 * <BR/>
 * <BR/> Это некорректный выход из Редактора
 * <BR/>
 * <BR/> Алгоритм:
 * <BR/> - Если было редактивроание - стараться аварийно скинуть куда-нибудь файл. Если нет прописи имени файла - в TMP.
 * <BR/> - Не было редактирования - ничего не делать.
 * <BR/>
 * <BR/> User: Zhiganov
 * <BR/> Date: 29.08.2007
 * <BR/> Time: 16:39:21
 */
public class WEditShutdown extends Thread
{
    public WEditShutdown ()
    {
        Log.l.info ( "Create Shutdown function" );
        setName ( "Shutdown" );
    }

    /**
     * Запуск процесса аварийного останова Редактора
     */
    public void run ()
    {
        Function function;
        //SaveAbsoluteAllProjectsFunction saveAllFunction;
        SaveAllProjectsFunction saveAllFunction;

        Log.l.info ( "\n\n\t-------------- Start shutdown process. Waiting for real work is finished. --------------------" );

        Par.SHUTDOWN_STARTED    = true;  // флаг для всех рабочих процессов

        // Запустить функцию Shutdown - закрытие Редактора.
        try
        {
            // взять функцию закрытия всех книг всех проектов - SaveAllProjectsFunction
            //function = Par.GM.getFm().get ( FunctionId.SAVE_ABSOLUTE_ALL_PROJECTS );       // Переключил на Сохранение только измененых книг.
            function = Par.GM.getFm().get ( FunctionId.SAVE_ALL_PROJECTS );
            //if ( (function != null) && (function instanceof SaveAbsoluteAllProjectsFunction) )
            if ( (function != null) && (function instanceof SaveAllProjectsFunction ) )
            {
                //saveAllFunction = (SaveAbsoluteAllProjectsFunction) function;
                saveAllFunction = (SaveAllProjectsFunction) function;
                // ставим флаг  - работаем без применения диалогов
                saveAllFunction.setUseDialog ( false );
                Log.l.info ( "Process shutdown function..." );
                saveAllFunction.handle ( null );
            }
        } catch ( Exception e )             {
            Log.l.error ( "Function Shutdown error", e );
        }

        Par.GM.alarmClose();

        Log.l.info ( "Finish shutdown\n----------------------------------------- FINISH ------------------------------------------\n" );
    }

}
