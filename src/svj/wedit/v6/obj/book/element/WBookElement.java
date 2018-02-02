package svj.wedit.v6.obj.book.element;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.INumber;
import svj.wedit.v6.obj.WClone;
import svj.wedit.v6.obj.XmlAvailable;
import svj.wedit.v6.obj.book.BookCons;
import svj.wedit.v6.obj.book.WEditStyle;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.ElementCons;
import svj.wedit.v6.tools.Utils;
import svj.wedit.v6.util.INameNumber;

import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.OutputStream;


/**
 * Обьект описания элемента книги - Часть, Глава, и т.д. -- Новая реализация..
 * <BR/>
 * <BR/> Атрибуты элемента:
 * <BR/> - Уровень элемента (фиксированное значение, не подлежит изменению)
 * <BR/> - Название элемента  (Часть, Глава, Эпизод)
 * <BR/> - Цвет - для отображения в дереве и в тексте.
 * <BR/> - Размер - для текста (в пикселях)
 * <BR/> - тип    - для текста (bold, italic...)
 * <BR/> - расположение - влево, вправо, по центру
 * <BR/> - маргин - в символах (пикселях) - для крайних расположений (отступ слева или справа)
 * <BR/>
 * <BR/> Имя стиля в тексте = level+_+type
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 21.05.2013 13:14:11
 */
public class WBookElement extends XmlAvailable  implements Comparable<WBookElement>, WClone<WBookElement>, INameNumber
{
    private int     elementLevel;
    //private int     rowSpace;
    private String  name;     // (Часть, Глава, Эпизод)

    // Tree + Text
    private Color   color;

    /* для текста (в пикселях). - Text */
    private int     fontSize;
    private String  fontFamily;
    /* style - Font.PLAIN, BOLD, ITALIC, or BOLD+ITALIC. - Text */
    private int     styleType;
    /* LEFT, RIGHT, CENTER. - Text -- StyleConstants.ALIGN_LEFT */
    private int     align;
    /* Крайнее смещение. Если align=LEFT - это смещение слева от заголовка. Если align=RIGHT - это смещение справа от заголовка. Для CENTER - не используется. - Text */
    private int     margin;

    /*
    // Пока заносится из виджета редактирования  -- ???
    private Icon    treeIcon;
    */


    public WBookElement ( int level )
    {
        this.elementLevel = level;

        name        = "Unknown";
        color       = Color.RED;
        fontFamily  = "Arial";
        fontSize    = 14;
        styleType   = Font.BOLD;
        align       = StyleConstants.ALIGN_LEFT;
        margin      = 25;
    }

    public String toString()
    {
        StringBuilder result;

        result  = new StringBuilder ( 512 );
        result.append ( "[ WBookElement: name = '" );
        result.append ( getName() );
        result.append ( "'; level = " );
        result.append ( getElementLevel() );
        result.append ( "; color = " );
        result.append ( getColor() );
        result.append ( "; fontSize = " );
        result.append ( getFontSize() );
        result.append ( "; fontFamily = " );
        result.append ( getFontFamily() );
        result.append ( "; styleType = " );
        result.append ( getStyleType() );
        result.append ( "; align (L:0;C:1;R:2;L:3) = " );
        result.append ( getAlign() );
        result.append ( "; margin = " );
        result.append ( getMargin() );
        result.append ( "' ]" );

        return result.toString();
    }

    public WBookElement cloneObj ()
    {
        WBookElement result;

        result  = new WBookElement ( getElementLevel() );

        result.setName ( getName() );
        result.setColor ( getColor() );
        result.setFontSize ( getFontSize() );
        result.setStyleType ( getStyleType() );
        result.setAlign ( getAlign() );
        result.setMargin ( getMargin() );
        result.setFontFamily ( getFontFamily() );

        return result;
    }

    @Override
    public void toXml ( int outLevel, OutputStream out ) throws WEditException
    {
        int ic;

        //Log.file.debug ( "element = %s", this );

        try
        {
            ic  = outLevel + 1;

            outTitle ( outLevel, ElementCons.ELEMENT, BookCons.LEVEL, Integer.toString ( getElementLevel() ), out );

            outTag ( ic,  ElementCons.NAME,         getName(),                              out );
            outTag ( ic,  ElementCons.COLOR,        Convert.color2str ( getColor()),        out );
            outTag ( ic,  ElementCons.FONT_SIZE,    Integer.toString ( getFontSize() ),     out );
            outTag ( ic,  ElementCons.FONT_FAMILY,  getFontFamily(),                        out );
            outTag ( ic,  ElementCons.STYLE_TYPE,   Integer.toString ( getStyleType() ),    out );
            outTag ( ic,  ElementCons.ALIGN,        Integer.toString ( getAlign() ),        out );
            outTag ( ic,  ElementCons.MARGIN,       Integer.toString ( getMargin() ),       out );

            endTag ( outLevel, ElementCons.ELEMENT, out );

        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML представления Элемента Книги '", getName(), "' в поток :\n", e );
        }
    }

    @Override
    public int getSize ()
    {
        // В размер это все не входит
        return 0;
    }

    @Override
    public int compareTo ( WBookElement element )
    {
        int result, ic;

        if ( element == null ) return 1;

        // по идее, имени достаточно - уникальное должно быть
        ic  = Utils.compareToWithNull ( getName(), element.getName() );
        if ( ic == 0 )
        {
            ic  = Utils.compareToWithNull ( getFontSize(), element.getFontSize() );
            if ( ic == 0 )
                result  = Utils.compareToWithNull ( getStyleType(), element.getStyleType() );
            else
                result = ic;
        }
        else
        {
            result = ic;
        }
        return result;
    }

    
    public void setElementLevel ( int elementLevel )
    {
        this.elementLevel = elementLevel;
    }

    public int getElementLevel ()
    {
        return elementLevel;
    }

    public String getName ()
    {
        return name;
    }

    public void setName ( String name )
    {
        this.name = name;
    }

    public Color getColor ()
    {
        return color;
    }

    public void setColor ( Color color )
    {
        this.color = color;
    }

    public int getFontSize ()
    {
        return fontSize;
    }

    public void setFontSize ( int fontSize )
    {
        this.fontSize = fontSize;
    }

    public int getStyleType ()
    {
        return styleType;
    }

    public void setStyleType ( int styleType )
    {
        this.styleType = styleType;
    }

    public int getAlign ()
    {
        return align;
    }

    public void setAlign ( int align )
    {
        this.align = align;
    }

    public int getMargin ()
    {
        return margin;
    }

    public void setMargin ( int margin )
    {
        this.margin = margin;
    }

    public String getFontFamily ()
    {
        return fontFamily;
    }

    public Font getFont ()
    {
        return new Font ( getFontFamily(), getStyleType(), getFontSize() );
    }

    public void setFontFamily ( String fontFamily )
    {
        this.fontFamily = fontFamily;
    }

    // -- Применяется для выпадашки навешивания стиля на выделенный текст.
    public WEditStyle getStyle ()
    {
        WEditStyle      result;
        String          styleName, str;
        Color           textColor;
        int             styleType;   // bold, italic...

        try
        {
            styleName   = getStyleName();

            result      = new WEditStyle ( StyleType.ELEMENT, styleName );

            result.addAttribute ( StyleName.STYLE_NAME, styleName );

            // ------------- Наполняем стиль значениями -----------------

            // Цвет
            textColor  = getColor();
            if ( textColor != null ) StyleConstants.setForeground ( result, textColor );

            // FontFamily - из элемента
            str         = getFontFamily();
            if ( str == null )  str = "Monospaced";
            StyleConstants.setFontFamily ( result, str );

            // Размер шрифта - из элемента
            StyleConstants.setFontSize ( result, getFontSize() );

            // Выравнивание - из элемента       -- StyleConstants.ALIGN_LEFT
            StyleConstants.setAlignment ( result, getAlign() );

            // Начальный отступ абзаца если есть. - FirstLineIndent
            switch ( getAlign() )
            {
                case  StyleConstants.ALIGN_LEFT :
                    StyleConstants.setFirstLineIndent ( result, getMargin() );
                    break;
                case  StyleConstants.ALIGN_RIGHT :
                    StyleConstants.setRightIndent ( result, getMargin() );
                    break;
            }

            // Тип стиля
            styleType = getStyleType();

            switch ( styleType )
            {
                case Font.BOLD :
                    StyleConstants.setBold ( result, true );
                    break;
                case Font.ITALIC :
                    StyleConstants.setItalic ( result, true );
                    break;
                default:
                    StyleConstants.setBold ( result, true );
                    StyleConstants.setItalic ( result, true );
                    break;
            }

        } catch ( Exception e )     {
            result      = new WEditStyle ( StyleType.TEXT, "errorText" );
            Log.l.error ( Convert.concatObj ( "Ошибка создания стиля для элемента уровня ", getElementLevel() ), e);
        }

        Log.l.debug ( "Finish. create style for title Book\n\t element level = ", getElementLevel(), "\n\t create style = ", result );
        return result;
    }

    private String getStyleName ()
    {
        return Integer.toString ( getElementLevel() );
    }

    public void setFont ( Font font )
    {
        if ( font != null )
        {
            setFontFamily ( font.getFamily() );
            setStyleType ( font.getStyle () );
            setFontSize ( font.getSize() );
        }
    }

    @Override
    public int getNumber ()
    {
        return getElementLevel();
    }
}
