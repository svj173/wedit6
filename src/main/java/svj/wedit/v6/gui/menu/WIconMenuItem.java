package svj.wedit.v6.gui.menu;


import svj.wedit.v6.gui.icon.IIconSizeGetter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.GuiTools;

import javax.swing.*;

/**
 * Меню с возможностью изменения размера экрана.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 15.03.2013 12:15
 */
public class WIconMenuItem extends WEMenuItem
{
    private Function function;
    private IIconSizeGetter iconSizeGetter;

    public WIconMenuItem ( Function function, IIconSizeGetter iconSizeGetter )
    {
        super ( function.getId().toString() );

        this.function       = function;
        this.iconSizeGetter = iconSizeGetter;

        addActionListener ( function );
        setText ( function.getName() );

        rewrite();
    }

    public void rewrite ()
    {
        Icon    icon;
        String  iconPath;

        // Изменить размер иконки
        //this.key = key;
        iconPath    = function.getIcon ( iconSizeGetter.getIconSize() );
        if ( iconPath != null )
        {
            try
            {
                icon   = GuiTools.createImageByFile ( iconPath );
                setIcon ( icon );
            } catch ( Exception e )            {
                Log.l.error ( Convert.concatObj ( "Ошибка получения иконки для пункта меню '", getId(), "'" ), e);
            }
        }
    }

}
