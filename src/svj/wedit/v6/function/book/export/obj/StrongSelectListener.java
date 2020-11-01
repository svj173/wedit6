package svj.wedit.v6.function.book.export.obj;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.listener.WActionListener;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.obj.TreeObj;

import java.awt.event.ActionEvent;

/**
 * Акция по работе с отметкой Неизменяемых элементов (у которых к титлу не добавляются 'Глава' и прочее).
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 03.07.2018 19:05
 */
public class StrongSelectListener extends WActionListener
{
    private final TreePanel treePanel;
    private final String    strongColor;

    public StrongSelectListener ( TreePanel treePanel, String strongColor )
    {
        super ( "StrongSelectListener" );
        this.treePanel      = treePanel;
        this.strongColor    = strongColor;
    }

    @Override
    public void handleAction ( ActionEvent event ) throws WEditException
    {
        String      cmd, str;
        TreeObj[]   selected;

        // Взять из дерева отмеченные обьекты
        selected = treePanel.getAllSelectNodes ();
        if ( selected == null )  return;

        // Взять команду
        cmd = event.getActionCommand();

        if ( cmd.equals ( "select" ) )
        {
            // отметить
            str = strongColor;
        }
        else
        {
            // очистить
            str = null;
        }

        for ( TreeObj to : selected )   to.setSubType ( str );
    }

}
