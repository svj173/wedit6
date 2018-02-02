package svj.wedit.v6.gui.img;


import svj.wedit.v6.gui.panel.EditablePanel;

import javax.swing.*;
import java.awt.*;


/**
 * Иконка для табика. Основное назначение - при изменение статуса редатирования панели - менять цвет иконки - зеленый, красный.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 25.03.2012 16:39:13
 */
public class TabIcon  implements Icon
{
    private EditablePanel editPanel;
    private Image         greenImage, redImage;

    public TabIcon ( EditablePanel editPanel, Image greenImage, Image redImage )
    {
        this.editPanel  = editPanel;
        this.greenImage = greenImage;
        this.redImage   = redImage;
    }

    @Override
    public void paintIcon ( Component c, Graphics g, int x, int y )
    {
        if ( editPanel.isEdit() )
            g.drawImage ( redImage, x, y, c );
        else
            g.drawImage ( greenImage, x, y, c );
    }

    @Override
    public int getIconWidth ()
    {
        int greenWidth, redWidth;

        if ( greenImage != null )
            greenWidth   = greenImage.getWidth(null);
        else
            greenWidth  = 0;

        if ( redImage != null )
            redWidth   = redImage.getWidth(null);
        else
            redWidth  = 0;

        return Math.max ( redWidth, greenWidth );
    }

    @Override
    public int getIconHeight ()
    {
        int greenHeight, redHeight;

        if ( greenImage != null )
            greenHeight   = greenImage.getHeight(null);
        else
            greenHeight  = 0;

        if ( redImage != null )
            redHeight   = redImage.getHeight(null);
        else
            redHeight  = 0;

        return Math.max ( redHeight, greenHeight );
    }

}
