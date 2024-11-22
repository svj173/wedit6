package svj.wedit.v6.function.book;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.obj.function.Function;

import java.awt.event.ActionEvent;


/**
 * Перечитать книгу (заново).
 * <BR/> С предварительными проверками.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 07.02.2012 14:31:07
 */
public class ReloadBookFunction   extends Function
{
    public ReloadBookFunction ()
    {
        setId ( FunctionId.RELOAD_BOOK );
        setName ( "Перечитать книгу");
        setIconFileName ( "reload.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        // Берем текущую книгу. Если null - выходим, либо ругаемся.

        // todo Проверяем - может книга была изменена но не сохранена.

        // Отметить что уже нет изменений
        //currentBookContentPanel.setEdit ( false );

        throw new WEditException ( "Не реализована." );
    }

    @Override
    public void rewrite ()
    {
    }

    @Override
    public void init () throws WEditException
    {
    }

    @Override
    public void close ()
    {
    }

    @Override
    public String getToolTipText ()
    {
        return "Перечитать книгу";
    }

}
