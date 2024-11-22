package svj.wedit.v6.gui.renderer;


import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.WEditStyle;
import svj.wedit.v6.obj.book.element.StyleName;
import svj.wedit.v6.tools.DumpTools;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;



/**
 * Рендерер Стилей.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 30.05.2012 13:33:46
 */
public class AttributeSetRenderer extends DefaultListCellRenderer
{
    // метод, возвращающий для элемента рисующий компонент
    public Component getListCellRendererComponent ( JList list, Object data, int idx, boolean isSelected, boolean hasFocus )
    {
        // проверяем, нужного ли обьект типа
        if ( data instanceof WEditStyle )
        {
            Object      obj;
            WEditStyle  attr;
            String      text;
            Color       color;
            JLabel      label;

            /*
            -- Ключи - это обьекты Object (styleName). Стандартные значения - StyleConstants (foreground)
[ AttributeSet :                                           key
  foreground = java.awt.Color[r=148,g=4,b=4]   -- StyleConstants$ColorConstants
  FirstLineIndent = 1.0                        -- StyleConstants$ParagraphConstants
  Alignment = 0                                -- StyleConstants$ParagraphConstants
  family = Dialog                              -- StyleConstants$FontConstants
  size = 18                                    -- StyleConstants$FontConstants
  styleName = Книга_work                       -- String
 ]
             */
            attr    = ( WEditStyle ) data;
            //Log.l.debug ( "attr params = ", DumpTools.printAttributeSet(attr) );
            //obj     = attr.getAttribute ( StyleName.STYLE_NAME );
            text    = attr.getStyleName();
            //Log.l.debug ( "attr styleName = ", text );
            if ( text == null )  text    = "unknow";

            // color - foreground
            //attr    = ( AttributeSet ) data;
            obj     = attr.getAttribute ( StyleConstants.Foreground );
            //Log.l.debug ( "foreground = ", obj );
            if ( obj == null )
                color    = Color.BLACK;
            else
                color    = (Color) obj;

            // используем возможности базового класса
            label = ( JLabel ) super.getListCellRendererComponent ( list, text, idx, isSelected, hasFocus );
            label.setForeground ( color );
            //label.setIcon(icon);
            return label;
        }
        else
            return super.getListCellRendererComponent ( list, data, idx, isSelected, hasFocus );
    }

}
