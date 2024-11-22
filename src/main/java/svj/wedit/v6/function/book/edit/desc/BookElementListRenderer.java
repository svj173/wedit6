package svj.wedit.v6.function.book.edit.desc;


import svj.wedit.v6.obj.book.element.WBookElement;
import svj.wedit.v6.tools.Convert;

import javax.swing.*;
import java.awt.*;


/**
 * Отрисовка в JList
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.08.2011 23:11:00
 */
public class BookElementListRenderer extends DefaultListCellRenderer
{
    // метод, возвращающий для элемента рисующий компонент
    public Component getListCellRendererComponent ( JList list, Object data, int idx, boolean isSelected, boolean hasFocus )
    {
        // проверяем, нужного ли элемент типа
        if ( data instanceof WBookElement )
        {
            WBookElement bookElement = ( WBookElement ) data;
            String text = Convert.concatObj ( bookElement.getElementLevel(), ". ", bookElement.getName() );
            // - на будущее - прорисовываем индивидуальную иконку элемента
            //JLabel label = ( JLabel ) super.getListCellRendererComponent ( list, text, idx, isSelected, hasFocus );
            //label.setIcon(icon);
            //return label;
            return super.getListCellRendererComponent ( list, text, idx, isSelected, hasFocus );
        }
        else
            return super.getListCellRendererComponent ( list, data, idx, isSelected, hasFocus );
        //    return super.getListCellRendererComponent ( list, data, idx, isSelected, hasFocus );
    }

}
