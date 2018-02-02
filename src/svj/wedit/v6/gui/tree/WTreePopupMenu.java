package svj.wedit.v6.gui.tree;


import svj.wedit.v6.WCons;
import svj.wedit.v6.gui.InitObjectComponent;
import svj.wedit.v6.gui.WComponent;
import svj.wedit.v6.gui.WMenuComponent;
import svj.wedit.v6.gui.menu.WEMenu;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.WTreeObj;
import svj.wedit.v6.tools.Convert;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Контекстное меню дерева. Собирается на основе данных от обработчиков объектов.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 04.08.2011 11:33:40
 */
public class WTreePopupMenu extends JPopupMenu implements InitObjectComponent<TreeObj>, WComponent
{
    //private final static String TITLE_FIRST = "   Объект:  ";
    private List<WMenuComponent> menus;
    private JLabel  title, titleFirst;
    private TreeObj currentObj;


    public WTreePopupMenu ()
    {
        //super ( "Tree_PopupMenu" );  // title высвечивается не на всех платформах и LAF. Поэтому делаем титл через отдельный пункт
        super ();
        menus = new ArrayList<WMenuComponent> ();

        JPanel titlePanel = new JPanel(new BorderLayout ());
        titlePanel.setBorder ( BorderFactory.createEtchedBorder() );
        titlePanel.setBackground ( WCons.BLUE_1 );

        titleFirst = new JLabel();

        titleFirst.setForeground ( Color.WHITE );
        titlePanel.add ( titleFirst, BorderLayout.WEST );

        title = new JLabel ();
        title.setForeground ( Color.WHITE );
        titlePanel.add ( title, BorderLayout.CENTER );

        add ( titlePanel );
    }

    /**
     * Проинсталировать имеющиеся пункты меню. Т.е. отключить те, которые не подходят к данному обьекту.
     * @param obj   объект дерева, на котором ткнулись правой кнопкой мышки (может быть не выбранным)
     */
    @Override
    public void init ( TreeObj obj )
    {
        Object   object;
        WTreeObj treeObj;

        currentObj = obj;

        if ( obj != null )
        {
            object = currentObj.getWTreeObj();
            if ( object instanceof WTreeObj )
            {
                treeObj = (WTreeObj) object;
                titleFirst.setText ( Convert.concatObj ( "   ",treeObj.getType().getRuName(), " :  " ) );
                title.setText ( treeObj.getName() );
            }
        }
        else
        {
            titleFirst.setText ( "" );
            title.setText ( "" );
        }

        for ( WMenuComponent menuItem : menus )
        {
            menuItem.init ( obj );
        }
    }

    @Override
    public void rewrite ()
    {
        /*
         * для объектов, которые недоступны текущей роли, можно выключить всё меню, прямо тут,
         * чтобы не реализовывать это в каждом MenuItem.
         */
        if ( currentObj != null )
        {
            boolean     nodeAllow;
            Component   comp;
            JMenu       menu;
            
            //nodeAllow = RoleTools.checkContainsNodeFromChildToRoot ( Par.USER.getRole (), CurrentObj );
            //nodeAllow = true;

            try
            {
                int size = getComponentCount ();
                for ( int i = 0; i < size; i++ )
                {
                    comp = getComponent ( i );
                    if ( comp instanceof JMenu )
                    {
                        // instanceof - тк здесь присутствует Separator и могут и др
                        menu = ( JMenu ) comp;
                        menu.setEnabled ( true );
                    }
                }
            } catch ( Exception ex )             {
                Log.l.error ( "WTreePopupMenu.rewrite: ", ex );
            }
        }
        else
        {
            // нет текущего объекта - безусловно выключить меню.
            Log.l.info ( "Current obj is null. Disable popup menu." );
            setEnabled ( false );
            setVisible ( false );
        }

        //Log.l.debug ( "EltexPopupMenu.rewrite: Start. menus = " + menus );
        for ( WMenuComponent menuItem : menus )
        {
            menuItem.rewrite ();
        }
    }

    @Override
    public TreeObj getObj ()
    {
        return null;
    }

    public void addPopupMenu ( Component component )
    {
        //Log.l.debug ( "EltexPopupMenu.addPopupMenu: Start. component = " + component );

        if ( component == null ) return;

        add ( component );

        // добавить в свой массив только объекты типа WMenuComponent - для возможности задавать им init(obj), rewrite.
        parseComponent ( component );

        //Log.l.debug ( "EltexPopupMenu.addPopupMenu: Finish. menus = " + menus );
    }

    /**
     * Добавить в свой массив только объекты типа WMenuComponent - для возможности задавать им init(obj), rewrite.
     * @param component   Добавляемая в меню компонента. Может иметь вложения, которые также необходимо проверить.
     */
    private void parseComponent ( Component component )
    {
        WMenuComponent  menuItem;
        JMenu           menu;
        int             ic;

        //Log.l.debug ( "EltexPopupMenu.parseComponent: Start. component = " + component );

        // Парсим тип добавляемого компонента
        if ( component instanceof JMenu )
        {
            // лезем по дереву на предмет вытаскивания всех EltexMenuItem объектов
            menu = (JMenu) component;
            //Log.l.debug ( "EltexPopupMenu.parseComponent: menu. size = " + menu.getComponentCount() );
            //Log.l.debug ( "EltexPopupMenu.parseComponent: menu. item size = " + menu.getItemCount() );

            if ( menu instanceof WMenuComponent )
            {
                menuItem = (WMenuComponent) menu;
                menus.add(menuItem);
            }

            Component comp;
            ic  = menu.getItemCount ();
            //logger.debug ( "getItemCount = " + ic );
            for ( int i = 0; i < ic; i++ )
            {
                comp    = menu.getItem ( i );
                parseComponent ( comp );
            }

        }
        else if ( component instanceof WMenuComponent )
        {
            menuItem = (WMenuComponent) component;
            //Log.l.debug ( "--- EltexPopupMenu.parseComponent: add menuItem = " + menuItem );
            menus.add ( menuItem );
        }

        //Log.l.debug ( "EltexPopupMenu.parseComponent: Finish" );
    }

    /**
     * Выяснить, содержится ли в меню пункт меню с указанной акцией.
     * <BR/> Здесь поиск осуществляется только в меню самого первого уровня, без учета вложенности пунктов.
     * @param action  Акция
     * @return        True - есть такой пункт.
     */
    public boolean containsMenuItemByAction ( String action )
    {
        WEMenu   menu;
        String      cmd;
        boolean     result;

        result = false;
        if ( action != null )
        {
            for ( WMenuComponent menuItem : menus )
            {
                if ( menuItem instanceof WEMenu )
                {
                    menu    = ( WEMenu ) menuItem;
                    cmd     = menu.getActionCommand();
                    if ( (cmd != null) && cmd.equals(action))
                    {
                        result = true;
                        break;
                    }
                }
            }
        }

        return result;
    }

}

