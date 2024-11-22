package svj.wedit.v6.gui.widget.font;


import svj.wedit.v6.gui.dialog.FontChooser;
import svj.wedit.v6.tools.GuiTools;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 30.09.2011 16:42:07
 */
public class SelectFontActionListener  implements ActionListener
{
    private FontWidget fontWidget;


    public SelectFontActionListener ( FontWidget fontWidget )
    {
        this.fontWidget = fontWidget;
    }

    @Override
    public void actionPerformed ( ActionEvent e )
    {
        FontChooser dialog;
        int                 selectOption;
        SimpleAttributeSet a;
        JDialog parentDialog;
        Font font;
        Color color;

        //dialog = new FontChooserWithPoleDialog( Par.GM.getFrame() );
        parentDialog = getFontWidget().getDialog();
        //System.out.println ( "---------- parentDialog = " + parentDialog );
        dialog       = new FontChooser ( parentDialog );

        // Заносим наш фонт и цвет
        dialog.init ( getFontWidget().getFont(), getFontWidget().getColor() );
        // центрируем
        GuiTools.setDialogScreenCenterPosition ( dialog );
        dialog.setVisible ( true );

        // Почему-то нет паузы и акция пролетает насквозь и уже заканчивается, а диалог все еще открыт.  Это из-за modal=false
        selectOption = dialog.getOption();
        //System.out.println ( "---------- selectOption = " + selectOption );
        if ( selectOption == JOptionPane.YES_OPTION )
        {
            font  = dialog.getResult();
            color = dialog.getColor();
            //System.out.println ( "font = " + font );
            // Перерисовать в диалоге
            getFontWidget().setValue ( font );
            getFontWidget().setColor ( color );
            getFontWidget().getTextField().setFont ( font );
            getFontWidget().getTextField().setForeground ( color );
            getFontWidget().getTextField().repaint();
        }

        //dialog.dispose();
        //System.out.println ( "Finish. Font chooser action" );
    }

    public FontWidget getFontWidget ()
    {
        return fontWidget;
    }
}
