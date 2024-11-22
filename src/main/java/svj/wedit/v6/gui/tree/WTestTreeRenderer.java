package svj.wedit.v6.gui.tree;


import svj.wedit.v6.WCons;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.IName;
import svj.wedit.v6.obj.TreeObj;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;


/**
 * Рисовальщик объектов дерева. - Тестовый.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 04.08.2011 11:09:43
 */
public class WTestTreeRenderer extends DefaultTreeCellRenderer implements Cloneable
{
    /**
     * Получить компонент, который будет отображен в дереве. Обычно это Label.
     *
     * Нормальное отображение, без растягивания титла за  пределы экрана.
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
        JLabel      comp;
        TreeObj     obj;
        Object      wTreeObj;
        IName       iName;

        comp = (JLabel) super.getTreeCellRendererComponent ( tree, "", selected, expanded, leaf, row, hasFocus );

        try
        {
            obj     = ( TreeObj ) value;

            comp.setForeground ( Color.red );
            //comp.setEnabled ( false );

            wTreeObj = obj.getWTreeObj();
            if ( wTreeObj instanceof IName )
            {
                iName = (IName) wTreeObj;
                comp.setText ( iName.getName() );
            }

            //
            if ( ! selected )
            {
                comp.setBackground ( WCons.TREE_BACKGROUND_COLOR );
                this.setBackgroundNonSelectionColor ( WCons.TREE_BACKGROUND_COLOR );   // ??? - просто изменяет значение, а отрисовка то уже случилась
            }

            // todo Поначалу рисует очень длинные бордюры. Но стоит обьект раскрыть-закрыть и у него и его чилдренов бордюры становятся строго по тексту.
            //comp.setMaximumSize ( comp.getPreferredSize () );
            comp.setBorder ( BorderFactory.createEtchedBorder() );
            //comp.revalidate ();
            //comp.repaint ();


        } catch ( Exception e ) {
            Log.l.error ( "ERROR = ", e );
            comp.setForeground ( Color.RED );
            comp.setText ( "!! " + value.toString () + " !!" );
        }
        //Log.l.debug ( "+++ text = '", this.getText (), "'." );
        //Log.l.debug ( "+++++ label size = '", comp.getPreferredSize(), "'." );     // нормальные размеры

        return comp;
    }

}
