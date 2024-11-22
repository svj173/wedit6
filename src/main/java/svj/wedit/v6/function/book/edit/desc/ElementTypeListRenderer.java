package svj.wedit.v6.function.book.edit.desc;


import svj.wedit.v6.obj.WType;

import javax.swing.*;
import java.awt.*;


/**
 * Отрисовка в JList
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.08.2014 23:11:00
 */
public class ElementTypeListRenderer extends DefaultListCellRenderer
{
    // метод, возвращающий для элемента рисующий компонент
    public Component getListCellRendererComponent ( JList list, Object data, int idx, boolean isSelected, boolean hasFocus )
    {
        // проверяем, нужного ли элемент типа
        if ( data instanceof WType )
        {
            WType type = ( WType ) data;
            String text = type.getRuName();
            // - на будущее - прорисовываем индивидуальную иконку элемента
            //JLabel label = ( JLabel ) super.getListCellRendererComponent ( list, text, idx, isSelected, hasFocus );
            //label.setIcon(icon);
            //return label;
            return super.getListCellRendererComponent ( list, text, idx, isSelected, hasFocus );
        }
        else
            return super.getListCellRendererComponent ( list, data, idx, isSelected, hasFocus );
    }

}
