package svj.wedit.v6.gui.renderer;


import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.IName;
import svj.wedit.v6.tools.Convert;

import javax.swing.*;
import java.awt.*;


/**
 * Рендерер обьектов IName.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 11.10.2013 16:33:46
 */
public class INameRenderer extends DefaultListCellRenderer
{
    // метод, возвращающий для элемента рисующий компонент
    public Component getListCellRendererComponent ( JList list, Object data, int idx, boolean isSelected, boolean hasFocus )
    {
        String      value;
        Component   result;

        Log.l.debug ( "getListCellRendererComponent: Start. data: class = %s; value = %s", data.getClass().getSimpleName(), data );
        value = data.toString();
        try
        {
            // проверяем, нужного ли обьект типа
            if ( data instanceof IName )
            {
                IName       attr;

                attr    = ( IName ) data;
                value   = attr.getName();
                //Log.l.debug ( "-- name = %s", data );

            }
        } catch ( Exception e )        {
            Log.l.error ( Convert.concatObj ( "error. data = ", data ), e );
        }
        Log.l.debug ( "--- getListCellRendererComponent: value = %s", value );
        result = super.getListCellRendererComponent ( list, value, idx, isSelected, hasFocus );
        return result;
    }

}
