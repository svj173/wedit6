package svj.wedit.v6.gui.button;


import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.WComponent;

import javax.swing.*;


/**
 * Кнопка не имеет своего фона и граница ее НЕ рисуется.
 * <BR/> Для отображения только одной иконки.
 * <BR/>
 * <BR/> Требуется:
 * <BR/> - При наведении кнопка реагировала на это действие - появлялись границы, менялся фон (фон-2).
 * <BR/> - При нажатии кнопка реагировала на это действие - границы оставались, менялся фон (фон-3).
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 05.08.2011 17:40:40
 */
public class EmptyButton extends JButton   implements WComponent
{
    private FunctionId functionId;
    // Hit detection.
    //Shape shape;

    public EmptyButton ()
    {
        super();

        // Ставим чтобы JButton перерисовывался исключительно по paintComponent
        setContentAreaFilled ( false );
    }

    public EmptyButton ( ImageIcon icon )
    {
        super ( icon );
    }

    /* НЕ рисуем границу. Здесь тогда теряем дефолтную анимацию нажатия-отжатия кнопки. */
    /*
    protected void paintBorder ( Graphics g )
    {
    }
    */

    public void rewrite ()
    {
        // Изменение размеров иконок - в панелях содержания иконок.
        /*
        Image       imageOld;
        ImageIcon   imageIcon, tmpIcon;
        Icon        icon;
        int         width;

        icon     = getIcon();
        if ( icon != null )
        {
            // сравниваем размеры
            width   = icon.getIconWidth();
            if ( width != Par.TOOLBAR_ICON_SIZE )    // различать Панель и Тул-бар
            {
                // Перерисовываем
                if ( icon instanceof ImageIcon )
                {
                    tmpIcon     = (ImageIcon) icon;
                    imageOld    = tmpIcon.getImage();
                    imageIcon   = new ImageIcon ( imageOld.getScaledInstance ( Par.TOOLBAR_ICON_SIZE, -1, Image.SCALE_DEFAULT ) );
                    setIcon ( imageIcon );
                }
            }
        }
        */
    }

    /*
    public boolean contains ( int x, int y )
    {
        // If the button has changed size, make a new shape object.
        if ( (shape == null) || ( ! shape.getBounds().equals ( getBounds() ) ) )
            shape = new Ellipse2D.Float ( 0, 0, getWidth(), getHeight() );

        return shape.contains ( x, y );
    }
    */

    public String toString()
    {
        StringBuilder result;

        result = new StringBuilder();

        result.append ( "[ EmptyButton : name = " );
        result.append ( getName() );
        result.append ( "; functionId = " );
        result.append ( functionId );

        result.append ( " ]" );

        return result.toString();
    }

    public void setFunctionId ( FunctionId functionId )
    {
        this.functionId = functionId;
    }

}
