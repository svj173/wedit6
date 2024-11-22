package svj.wedit.v6.function.book.export.obj;


import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.obj.book.element.WBookElement;

import java.awt.*;

/**
 * Параметр, описывающий атрибуты конвертации заголовка (титл).
 * <BR/>
 * <BR/> toXML   : HtmlElementConvertParameter
 * <BR/> fromXML : ElementConvertParser
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 10.10.2013 15:38
 */
public abstract class ElementConvertParameter extends FunctionParameter
{
    //private String  type;  // Тип элемента - для xml парсинг.
    private int     level   = -1;
    private Font    font    = null;
    private Color   color   = null;
    private TitleViewMode formatType;




    public void mergeToOther ( ElementConvertParameter other )
    {
        super.mergeToOther ( other );

        other.setLevel ( getLevel() );
        other.setFont ( getFont() );
        other.setColor ( getColor() );
        other.setFormatType ( getFormatType() );
    }

    @Override
    public void setValue ( Object value )
    {
        WBookElement bookElement;

        if ( (value != null) && (value instanceof WBookElement) )
        {
            bookElement = (WBookElement) value;
            setColor ( bookElement.getColor() );
            setFont ( bookElement.getFontFamily() );
            // name descr category empty
            setName ( bookElement.getName() );
            setDesc ( null );
        }
    }

    public int getLevel ()
    {
        return level;
    }

    public void setLevel ( int level )
    {
        this.level = level;
    }

    public void setFont ( Font font )
    {
        this.font = font;
    }

    public void setFont ( Object obj )
    {
        if ( (obj != null) && (obj instanceof Font) )
            font = (Font) obj;
    }

    public Font getFont ()
    {
        return font;
    }

    public void setColor ( Color color )
    {
        this.color = color;
    }

    public Color getColor ()
    {
        return color;
    }

    public TitleViewMode getFormatType ()
    {
        return formatType;
    }

    public void setFormatType ( TitleViewMode formatType )
    {
        this.formatType = formatType;
    }

    public void setFormatType ( String number )
    {
        TitleViewMode formatType;

        if ( number == null )
            formatType = TitleViewMode.NOTHING;
        else
            formatType = TitleViewMode.getByNumber ( number );

        setFormatType ( formatType );
    }

}
