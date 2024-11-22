package svj.wedit.v6.function.project.sync;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.obj.function.Function;

import java.awt.event.ActionEvent;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 18.08.2011 9:15:11
 */
public class SyncProjectFunction   extends Function
{
    public SyncProjectFunction ()
    {
        setId ( FunctionId.SYNC_PROJECT );
        setName ( "Синхронизовать Сборник");
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        throw new WEditException ( "Функция еще не реализована." );
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
        return "Перечитать директорию Сборника, сравнить с описанием, предложить изменения.";
    }

}
