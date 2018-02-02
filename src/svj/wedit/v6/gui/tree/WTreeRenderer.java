package svj.wedit.v6.gui.tree;


import svj.wedit.v6.WCons;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.TreeObjType;
import svj.wedit.v6.obj.WTreeObj;
import svj.wedit.v6.tools.Convert;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;


/**
 * Рисовальщик объектов дерева.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 04.08.2011 11:09:43
 */
public class WTreeRenderer extends DefaultTreeCellRenderer implements Cloneable
{
    /* Ключ - тип объекта. System - это Unknow объект */
    private Map<TreeObjType, WCellRenderer> objectRenderers;

    //private ImageIcon okIcon, errorIcon, wrongIcon;


    public WTreeRenderer ()
    {
        objectRenderers = new HashMap<TreeObjType, WCellRenderer> ();

        /*
        try
        {
            okIcon      = GuiTools.createImage  ( "/org/eltex/ems/web/gui/images/connected.png", "Device OK" );
        } catch ( WEditException e )        {
            okIcon      = null;
        }
        try
        {
            errorIcon      = GuiTools.createImage  ( "/org/eltex/ems/web/gui/images/not-avalaible.png", "Device Error" );
        } catch ( WEditException e )        {
            errorIcon      = null;
        }
        try
        {
            wrongIcon      = GuiTools.createImage  ( "/org/eltex/ems/web/gui/images/device-warning.png", "Device Wrong" );
        } catch ( WEditException e )        {
            wrongIcon      = null;
        }
        */
    }

    public WTreeRenderer ( Map<TreeObjType, WCellRenderer> renderers )
    {
        objectRenderers = renderers;
    }

        @Override
    public WTreeRenderer clone() //throws CloneNotSupportedException
    {
        return new WTreeRenderer(objectRenderers);
    }

    public void addRenderer ( TreeObjType type, WCellRenderer renderer )
    {
        objectRenderers.put ( type, renderer );
    }

    /**
     * Получить компонент, который будет отображен в дереве. Обычно это Label.
     * 
     * @param tree      Дерево с рисуемым узлом
     * @param value     Рисуемый узел
     * @param selected  true - данный узел выбран на дереве
     * @param expanded  true - данный узел раскрыт и показаны его дочерние компоненты
     * @param leaf      true - это конечный элемент дерева (?)
     * @param row       номер строки для данного узла
     * @param hasFocus  true - значит текущий узел содержит фокус ввода
     * @return Отображаемый компонент
     */
    @Override
    public Component getTreeCellRendererComponent ( JTree tree, Object value, boolean selected, boolean expanded,
                                                    boolean leaf, int row, boolean hasFocus )
    {
        JLabel          comp;
        TreeObj         obj;
        TreeObjType     type;
        WCellRenderer   renderer;
        WTreeObj        wTreeObj;
        Object          object;

        comp = (JLabel) super.getTreeCellRendererComponent ( tree, "", selected, expanded, leaf, row, hasFocus );
        //comp = this;
        try
        {
            obj      = ( TreeObj ) value;
            type     = obj.getType();
            object   = obj.getWTreeObj();

            if ( object instanceof WTreeObj )
            {
                wTreeObj = (WTreeObj) object;

                // Вот этой строкой решили проблему растягивания титлов.
                comp.setText ( wTreeObj.getName() );

                renderer = objectRenderers.get(type);
                //if ( renderer == null )  renderer = objectRenderers.get(EmsConst.SYSTEM_OBJECT_TYPE);
                if ( renderer == null )
                {
                    // установить ош значения
                    //result = new JPanel();
                    comp.setForeground ( Color.red );
                    comp.setEnabled ( false );
                    comp.setToolTipText ( "Не найден отрисовщик для типа объекта дерева '"+type+"'." );
                }
                else
                {
                    // Сильно удлинняет название
                    renderer.init ( comp, wTreeObj );
                }

                //
                if ( ! selected )
                {
                    comp.setBackground ( WCons.TREE_BACKGROUND_COLOR );
                    this.setBackgroundNonSelectionColor ( WCons.TREE_BACKGROUND_COLOR );   // ??? - просто изменяет значение, а отрисовка то уже случилась
                }

                /*
                // Поначалу рисует очень длинные бордюры. Но стоит обьект раскрыть-закрыть и у него и его чилдренов бордюры становятся строго по тексту.
                comp.setMaximumSize ( comp.getPreferredSize () );
                comp.setBorder ( BorderFactory.createEtchedBorder () );
                comp.revalidate ();
                comp.repaint ();
                */
            }
            else
            {
                if ( object != null )  comp.setText ( object.toString() );
            }

        } catch ( Exception e ) {
            Log.l.error ( Convert.concatObj ( "ERROR. value = ", value ), e );
            comp.setForeground ( Color.RED );
            comp.setText ( "!! " + value.toString() + " !!" );
            comp.setToolTipText ( e.toString() );
        }
        //Log.l.debug ( "+++ text = '", this.getText (), "'." );
        //Log.l.debug ( "+++++ label size = '", comp.getPreferredSize(), "'." );     // нормальные размеры

        return comp;
    }

}
