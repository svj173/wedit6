package svj.wedit.v6.gui.renderer;


import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.IName;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.util.INameNumber;

import javax.swing.*;
import java.awt.*;


/**
 * Рендерер обьектов IName.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 11.10.2013 16:33:46
 */
public class INameNumberRenderer extends DefaultListCellRenderer
{
    // метод, возвращающий для элемента рисующий компонент
    public Component getListCellRendererComponent ( JList list, Object data, int idx, boolean isSelected, boolean hasFocus )
    {
        Log.l.debug ( "Start. data = %s", data );
        try
        {
            // проверяем, нужного ли обьект типа
            if ( data instanceof INameNumber )
            {
                INameNumber       attr;

                attr    = ( INameNumber ) data;
                data    = attr.getName() + "/" + attr.getNumber();
                Log.l.debug ( "-- name = %s", data );

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
