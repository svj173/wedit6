package svj.wedit.v6.content.toolBar;


import svj.wedit.v6.Par;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.WComponent;
import svj.wedit.v6.obj.function.Function;

import javax.swing.*;
import java.awt.*;


/**
 * Панель кнопок.
 * <BR/> Динамическая. Т.е. ей можно менять положение на фрейме (горизонтальное, вертикальное), отстегивать от фрейма и т.д.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 27.10.2011 17:49:33
 */
public class BrowserToolBar extends JToolBar  implements WComponent
{
    public BrowserToolBar ()
    {
        setAlignmentX ( 0.5f );
    }

    /* К иконкам добавить текст. */
    public void setTextLabels ( boolean labelsAreEnabled )
    {
        Component c;
        int i = 0;
        ToolBarButton button;

        // цикл по всем вложенным обьектам
        while ( ( c = getComponentAtIndex ( i++ ) ) != null )
        {
            button = ( ToolBarButton ) c;
            if ( labelsAreEnabled )
                button.setText ( button.getToolTipText() );
            else
                button.setText ( null );
        }
    }

    public void addFunction ( FunctionId functionId )
    {
        ToolBarButton   button;
        Function function;
        JComponent      toolBarObj;

        function    = Par.GM.getFm().get ( functionId );
        toolBarObj  = function.getToolBarObject();
        if ( toolBarObj == null )
        {
            button      = new ToolBarButton ( function );
            button.addActionListener ( function );
            add ( button );
        }
        else
        {
            add ( toolBarObj );
        }
    }

    @Override
    public void rewrite ()
    {
        Component c;
        int i = 0;
        WComponent button;

        // цикл по всем вложенным обьектам
        while ( ( c = getComponentAtIndex ( i++ ) ) != null )
        {
            if ( c instanceof WComponent )
            {
                button = ( WComponent ) c;
                button.rewrite();
            }
        }
    }
    
}
