package svj.wedit.v6.function.project.reopen;

import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.listener.WActionListener;

import java.awt.event.ActionEvent;

/**
 * Акция на изменение максимального значения списка ранее открытых Сборников - в функции Reopen.
 * User: svj
 * Date: 16.08.2011 20:55:05
 */
public class ChangeMaxListSizeListener  extends WActionListener
{
    private ReopenProjectFunction   function;

    public ChangeMaxListSizeListener ( ReopenProjectFunction function )
    {
        super ( "ChangeMaxListSizeListener" );
        this.function   = function;
    }

    @Override
    public void handleAction ( ActionEvent event ) throws WEditException
    {
        ChangeMaxListSizeDialog dialog;

        // Открыть диалог по смене макс размера
        dialog  = new ChangeMaxListSizeDialog ( function );
        dialog.showDialog();

        if ( dialog.isOK() )
        {
            // сменить число
            function.getMaxSizeParam().setValue ( Integer.toString ( dialog.getResult() ) );
            // обновить меню
            function.createMenu();
        }
    }

}
