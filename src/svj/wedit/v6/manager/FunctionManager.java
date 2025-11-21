package svj.wedit.v6.manager;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionGroup;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.GuiCreator;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.Convert;

import java.util.*;


/**
 * Хранит все функции.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 15.07.2011 16:01:25
 */
public class FunctionManager
{
    // Hashtable - т.к. синхронизованный (SynchronizedMap, Map)    - надо ли?
    private final Map<FunctionId,Function> functions;


    public FunctionManager ()
    {
        functions   = new HashMap<FunctionId,Function>();

    }

    public Map<FunctionId, Function> getFunctions ()
    {
        return functions;
    }

    public void add ( Function function )
    {
        getFunctions().put ( function.getId(), function );
    }

    public Function get ( FunctionId functionId )
    {
        return getFunctions().get ( functionId );
    }

    /**
     * Закрыть все функции. Чтобы они навели порядок со своими
     * параметрами, перед их сохранением в динамических конфигах.
     *  Lang - взял текущий язык,
     * Reopen - последний открытый файл, и т.д.
     * А также скинули свои внутренние параметры в исходное состояние - при
     *  закрытии книги и открытии следующей книги.
     */
    public void closeAll ()
    {
        for ( Function function : getFunctions().values() )
        {
            function.close();
        }
    }

    /**
     * Подписывать функции согласно их желаниям.
     */
    public void signFunction () //throws WEditException
    {
        FunctionGroup  str;

        for ( Function function : functions.values() )
        {
            // Установить слушатель
            str = function.getListenerGroup();
            if ( str != null )
            {
                // Есть группа на которую хочет подписаться данная функция
                //cmd = function.getListenerCmd();
                addListener ( str, function );
            }
        }
    }

    /**
     * Добавить в функции заданной функциональной группы функцию в качестве листенера.
     *
     * @param functionGroup       Функциональная группа
     * @param listenerFunction    Функция, добавляемая как слушатель.
     */
    public void addListener ( FunctionGroup functionGroup, Function listenerFunction )
    {
        String      testList;
        int         ic;

        ic          = 0;     // for TEST
        testList    = "";    // for TEST

        for ( Function function : functions.values() )
        {
            if ( function.containGroup ( functionGroup ) )
            {
                // Функция принадлежит к данной группе - добавить в нее слушателя
                function.addListener ( listenerFunction );
                testList    = Convert.concatObj ( testList, "; ", function.getName() );
                ic++;
            }
        }
        Log.l.debug ( "Add listener '", listenerFunction.getName(), "' to ", ic, " functions (", testList, ").");
    }


    public void init ()
    {
        GuiCreator.initFunctions (this);
    }

    /**
     *  Стартуем функции.
     *  @param errMsg Буфер сборки сообщений об ошибках. Отображается по окончанию инсталляции Редактора.
     */
    public void startAll ( StringBuilder errMsg )
    {
        for ( Function function : functions.values() )
        {
            try
            {
                function.start();
            } catch ( WEditException e )   {
                errMsg.append ( "Ошибка запуска функции '" );
                errMsg.append ( function.getName() );
                errMsg.append ( "' : " );
                errMsg.append ( e.getMessage() );
                errMsg.append ( "\n" );
                // Функцию выключить
                function.disable();
            }
        }
    }

}
