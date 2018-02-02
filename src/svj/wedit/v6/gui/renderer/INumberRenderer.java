package svj.wedit.v6.gui.renderer;


import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.IName;
import svj.wedit.v6.obj.INumber;
import svj.wedit.v6.tools.Convert;

import javax.swing.*;
import java.awt.*;


/**
 * Рендерер обьектов INumber.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 21.12.2014 22:33:46
 */
public class INumberRenderer extends DefaultListCellRenderer
{
    // метод, возвращающий для элемента рисующий компонент
    public Component getListCellRendererComponent ( JList list, Object data, int idx, boolean isSelected, boolean hasFocus )
    {
        //Log.l.debug ( "Start. data = %s", data );
        try
        {
            // проверяем, нужного ли обьект типа
            if ( data instanceof INumber )
            {
                INumber       attr;

                attr    = ( INumber ) data;
                data    = attr.getNumber();
                //Log.l.debug ( "-- number = %s", data );

                // используем возможности базового класса
                //label = ( JLabel ) super.getListCellRendererComponent ( list, text, idx, isSelected, hasFocus );
                //label.setIcon(icon);
                //return label;
            }
        } catch ( Exception e )        {
            Log.l.error ( Convert.concatObj ( "error. data = ", data ), e );
        }
        return super.getListCellRendererComponent ( list, data, idx, isSelected, hasFocus );
    }

}
