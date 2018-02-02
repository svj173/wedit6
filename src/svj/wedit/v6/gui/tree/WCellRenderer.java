package svj.wedit.v6.gui.tree;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.obj.WTreeObj;

import javax.swing.*;


/**
 * Интерфейс отрисовщиков объектов в дереве
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 04.08.2011 11:11:36
 */
public abstract class WCellRenderer
{
    private Icon icon;

    public abstract void init ( JLabel treeRenderer, WTreeObj obj ) throws WEditException;


    protected WCellRenderer ( Icon icon )
    {
        this.icon = icon;
    }

    public Icon getIcon ()
    {
        return icon;
    }

}
