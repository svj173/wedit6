package svj.wedit.v6.content.toolBar;


import svj.wedit.v6.Par;
import svj.wedit.v6.gui.WComponent;
import svj.wedit.v6.obj.function.Function;

import javax.swing.*;
import java.awt.*;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 27.10.2011 17:50:33
 */
public class ToolBarButton extends JButton  implements WComponent
{
    private static final Insets margins = new Insets ( 0, 0, 0, 0 );

    private Function function;

    /*
    public ToolBarButton ( Icon icon )
    {
        super ( icon );
        setMargin ( margins );
        setVerticalTextPosition ( BOTTOM );
        setHorizontalTextPosition ( CENTER );
    }
    */

    public ToolBarButton ( Function function )
    {
        super();

        this.function   = function;

        // Загрузить картинку
        ImageIcon   imageIcon;
        imageIcon   = new ImageIcon ( function.getIcon ( Par.TOOLBAR_ICON_SIZE ) );
        setIcon ( imageIcon );

        setMargin ( margins );
        setVerticalTextPosition ( BOTTOM );
        setHorizontalTextPosition ( CENTER );

        // Переделать иконку по заданный в системе размер
        rewrite();
    }

    public void rewrite ()
    {
        ImageIcon   imageIcon;
        Icon        icon;
        int         width;

        icon     = getIcon();
        if ( icon != null )
        {
            // сравниваем размеры
            width   = icon.getIconWidth();
            if ( width != Par.TOOLBAR_ICON_SIZE )
            {
                // Перерисовываем
                if ( icon instanceof ImageIcon )
                {
                    //Image       imageOld;
                    //tmpIcon     = (ImageIcon) icon;
                    //imageOld    = tmpIcon.getImage();
                    //imageIcon   = new ImageIcon ( imageOld.getScaledInstance ( Par.TOOLBAR_ICON_SIZE, -1, Image.SCALE_DEFAULT ) );
                    imageIcon   = new ImageIcon ( function.getIcon ( Par.TOOLBAR_ICON_SIZE ) );
                    setIcon ( imageIcon );
                }
            }
        }
    }

    /*
    public ToolBarButton ( String imageFile, String text )
    {
        this ( new ImageIcon ( imageFile ) );
        setText ( text );
    }
    */

}
