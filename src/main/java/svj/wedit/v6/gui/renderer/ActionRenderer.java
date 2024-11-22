package svj.wedit.v6.gui.renderer;


import javax.swing.*;
import java.awt.*;


/**
 * Рендерер обьектов Акция - есть параметр 'Name'.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 30.05.2012 13:33:46
 */
public class ActionRenderer extends DefaultListCellRenderer
{
    // метод, возвращающий для элемента рисующий компонент
    public Component getListCellRendererComponent ( JList list, Object data, int idx, boolean isSelected, boolean hasFocus )
    {
        // проверяем, нужного ли обьект типа
        if ( data instanceof Action )
        {
            Object      obj;
            Action      attr;
            String      text;
            JLabel      label;

            attr    = ( Action ) data;
            obj     = attr.getValue ( Action.NAME );
            if ( obj == null )
                    text = "---";
            else
                    text = obj.toString();

            // используем возможности базового класса
            label = ( JLabel ) super.getListCellRendererComponent ( list, text, idx, isSelected, hasFocus );
            //label.setIcon(icon);
            return label;
        }
        else
            return super.getListCellRendererComponent ( list, data, idx, isSelected, hasFocus );
    }

}
