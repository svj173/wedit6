package svj.wedit.v6.gui.menu;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.WMenuComponent;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.tools.GuiTools;

import javax.swing.*;
import java.awt.*;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 15.07.2011 16:52:35
 */
public class WEMenu  extends JMenu  implements WMenuComponent
{
    /** ИД. Название меню. */
    private String  id;

    /* это объект, на котором щелкнули правой кнопкой мыши. Может быть и НЕ текущим. */
    private TreeObj selectObj   = null;

    /* Для привязки пункта меню к акции. Чтобы можно было заносить в меню один пункт на несколько объектов
    (а не отдельные пункты от каждого объекта) */
    private String actionCommand = "";

    private boolean allowed = true;

    
    /* Именно здесь идет дополнительный рассчет с анализом состояние GUI модуля  - если требуется */
    //protected abstract boolean isAllowed ();
    public boolean isAllowed ()
    {
        return allowed;
    }


    public WEMenu ( String name, String toolTip, String iconPath ) throws WEditException
    {
        super ( name );

        Icon icon;

        if ( iconPath != null )
        {
            icon   = GuiTools.createImageIcon ( iconPath, toolTip );
            setIcon ( icon );
        }
        setText ( name );
        if ( toolTip != null ) setToolTipText ( toolTip );
        setEnabled ( false );
    }

    public WEMenu ( String id )
    {
        super(id);
        this.id     = id;
    }


    @Override
    public void setEnabled ( boolean enabled )
    {
        // ставим true только если оба условия TRUE, во всех остальных случаях - FALSE
        if ( enabled && isAllowed() )
            super.setEnabled(true);
        else
            super.setEnabled(false);
    }


    /**
     *  obj - это объект, на котором щелкнули правой кнопкой мыши. Может быть и НЕ текущим.
     */
    @Override
    public void init ( TreeObj obj )
    {
        selectObj = obj;
    }

    @Override
    public TreeObj getObj ()
    {
        return selectObj;
    }

    public String getActionCommand ()
    {
        return actionCommand;
    }

    public void setActionCommand ( String actionCommand )
    {
        this.actionCommand = actionCommand;
    }

    public String toString()
    {
        StringBuilder result;
        result  = new StringBuilder ( 64 );
        result.append ( "WEMenu: id = " );
        result.append ( getId() );
        //result.append ( ", params = " );
        //result.append ( params );
        return result.toString();
        //return getText();
    }

    public String getId ()
    {
        return id;
    }

    public WEMenu getMenu ( String id )
    {
        String      idc;
        Component   comp;
        int         ic;
        WEMenu      mi;

        //logger.debug ( "==== MENU = " + id );
        if (id == null) {
            throw new IllegalArgumentException("ID is NULL.");
        }
        ic  = this.getItemCount ();
        //logger.debug ( "getItemCount = " + ic );
        //ic  = this.getMenuComponentCount ();
        //logger.debug ( "getMenuComponentCount = " + ic );

        //MenuElement[] me    = this.getSubElements ();
        //logger.debug ( "MenuElement size = " + me.length );
        for ( int i = 0; i < ic; i++ )
        {
            comp    = getItem ( i );
            //logger.debug ( " - " + i + ". comp = " + comp );
            if (comp instanceof WEMenu)
            {
                mi  = (WEMenu) comp;
                idc = mi.getId();
                //logger.debug ( "      id = " + id + ", idc = " + idc );
                if ( idc.equals ( id ) )   return mi;
            }
        }

        return null;
    }

    public void rewrite ()
    {
        Component comp;
        int ic;

        Log.l.debug ( "WEMenu.rewrite (%s) : enabled = %b",getText(), isAllowed() );
        super.setEnabled ( isAllowed() );
        super.setVisible( isAllowed() ); // avp added : для тестов

        // todo если изменился размер иконки?

        //logger.debug ( "id = " + id + ", key = " + key );
        // Переписать текущее название
        //setText ( Msg.getMsg(key) );
        // Переписать вложенные компоненты (подменю)
        ic  = this.getItemCount ();
        //logger.debug ( "getItemCount = " + ic );
        for ( int i = 0; i < ic; i++ )
        {
            comp    = getItem ( i );
            //logger.debug ( " - " + i + ". comp = " + comp );
            if (comp instanceof WEMenu)
            {
                WEMenu mi = (WEMenu) comp;
                mi.rewrite ();
                continue;
            }
            if (comp instanceof WEMenuItem)
            {
                WEMenuItem item = (WEMenuItem) comp;
                item.rewrite ();
                //continue;
            }
        }
    }

    public WEMenu clone ( String frameId )
    {
        Component comp;
        int ic;
        WEMenu  newmi, mi, mi2;
        WEMenuItem  item, item2;

        newmi   = new WEMenu ( id );
        //newmi.setKey ( key );
        //logger.debug ( "id = " + id + ", key = " + key );
        // Скопировать вложенные компоненты (подменю)
        ic  = this.getItemCount ();
        //logger.debug ( "getItemCount = " + ic );
        for ( int i = 0; i < ic; i++ )
        {
            comp    = getItem ( i );
            //logger.debug ( " - " + i + ". comp = " + comp );
            //logger.debug ( "   -- " + i + ". class = " + comp.getClass().getClass ().getName () );
            //Class[] cl = comp.getClass().getClasses () ;
            //for ( int i2=0; i2<cl.length; i2++)
            //    logger.debug ( "   -- " + i2 + ". class2 = " + cl[i2].getName () );
            if (comp instanceof WEMenu)
            {
                mi  = (WEMenu) comp;
                mi2 = mi.clone ( frameId );
                newmi.add(mi2);
                continue;
            }
            if (comp instanceof WEMenuItem)
            {
                item    = (WEMenuItem) comp;
                item2   = item.clone ( frameId );
                newmi.add (item2);
                //continue;
            }
        }
        return newmi;
    }

}
