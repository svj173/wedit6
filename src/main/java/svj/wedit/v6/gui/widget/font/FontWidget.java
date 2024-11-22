package svj.wedit.v6.gui.widget.font;


import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.widget.AbstractDialogWidget;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;


/**
 * Виджет строковых значений.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 09.08.2011 15:00:18
 */
public class FontWidget extends AbstractDialogWidget<Font>
{
    private static final String PREVIEW_TEXT  = "Вариант текста.";
    private final JTextField  textField;

    private Font  font;
    private Color color;
    private JButton chuseButton;



    public FontWidget ( String titleName )
    {
        super ( titleName, false  );


        // Только для демонстрации
        textField   = new JTextField();

        //setBorder ( BorderFactory.createEtchedBorder() );
        //textField.setBackground ( Color.RED );

        textField.setHorizontalAlignment ( JTextField.LEFT );
        //textField.setColumns ( maxSize );   // выкл - делает почему-то шире чем задано символов (svj, 2010-10-12)
        textField.setText ( PREVIEW_TEXT );
        textField.setEditable ( false );
        add ( textField );

        // кнопка вызова диалога
        chuseButton = new JButton ("..");
        chuseButton.addActionListener ( new SelectFontActionListener ( this ) );
        add ( chuseButton );
    }

    public void addMouseListener ( MouseListener mouseListener )
    {
        textField.addMouseListener ( mouseListener );
    }

    @Override
    public void setEditable ( boolean value )
    {
        //textField.setEditable ( value );
        chuseButton.setEnabled ( value );
    }

    @Override
    public void setValueWidth ( int width )
    {
        int         height;
        Dimension   dim;

        if ( textField != null )
        {
            height  = textField.getHeight();
            if ( height < WCons.BUTTON_HEIGHT )  height = WCons.BUTTON_HEIGHT;
            dim     = new Dimension ( width, height );
            textField.setPreferredSize ( dim );
        }
    }

    @Override
    public JComponent getGuiComponent ()
    {
        return textField;
    }

    @Override
    protected Font validateValue () throws WEditException
    {
        // todo - првоерить на уникальность шрифта
        /*
        String str, result, msg;

        str = textField.getText();
        if ( str != null )
        {
            str = str.trim();
            if ( str.length() == 0 ) str = null;
        }

        if ( str == null )
        {
            if ( hasEmpty() )   result = null;
            else
            {
                msg = getTitleName();
                if ( msg != null )  msg = msg + ": ";
                msg = msg + "Значение не может быть пустым";
                throw new WEditException ( msg );
            }
        }
        else
        {
            if ( str.length() > maxSize )
            {
                msg = getTitleName();
                if ( msg != null )  msg = msg + ": ";
                msg = Convert.concatObj ( msg, "Введено символов '", str.length(), "', что больше максимально допустимого '", maxSize, "'" );
                throw new WEditException ( msg );
            }
            result = str;
        }
        */
        return font;
    }

    @Override
    public Font getValue () //throws WEditException
    {
        /*
        Font result;

        try
        {
            result = validateValue();
        } catch ( Exception e )         {
            result = null;
            Log.l.error ( e, "Ошибка получения Font параметра из виджета." );
        }
        */
        return getFont();
    }

    public Font getFont ()
    {
        return font;
    }

    public Color getColor ()
    {
        return color;
    }

    public void setColor ( Color color )
    {
        this.color = color;
        textField.setForeground ( color );
    }

    @Override
    public void setValue ( Font value )  //throws WEditException
    {
        if ( value == null )
        {
            //throw new WEditException ( null, getTitleName(), ": Font не может быть пустым" );
            value = WCons.TEXT_FONT_1;
        }

        //System.out.println ( "setValue. font = " + font );

        font  = value;
        textField.setFont ( font );
        textField.repaint();
    }

    public JTextField getTextField ()
    {
        return textField;
    }

}
