package svj.wedit.v6.function.project.reopen;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 16.08.2011 14:17:13
 */
public class ClearReopenListListener  implements ActionListener
{
    private ReopenProjectFunction function;

    public ClearReopenListListener ( ReopenProjectFunction function )
    {
        this.function   = function;
    }

    @Override
    public void actionPerformed ( ActionEvent e )
    {
        function.clearList();
        // пересобрать меню
        function.createMenu();
    }
}
