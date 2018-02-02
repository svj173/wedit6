package svj.wedit.v6.obj;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.ElementCons;
import svj.wedit.v6.tools.Utils;

import java.awt.*;
import java.io.OutputStream;

/**
 * Тип обьекта - книги (в работе, завершена...), элементов книги (черновик, рабочий, скрытый...).
 * <BR/> Иконка - только имя, т.к. иконки книг - расположены только в одной директории - img/tree, с разбивкой по размерам
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 08.05.2013 10:44
 */
public class WType   extends XmlAvailable  implements Comparable<WType>, WClone<WType>
{
    public static final String TYPES            = "types";
    public static final String TYPE             = "type";
    public static final String EN_NAME          = "enName";
    public static final String RU_NAME          = "ruName";
    public static final String DESCR            = "descr";
    public static final String ICON_FONT_COLOR  = "iconFontColor";
    //public static final String ICON_NAME    = "iconName";       // ??? - надо ли?

    private String ruName, enName, descr;  // iconName
    //  - цвет - для дерева и текста (заменяет цвет элемента)
    private Color color;
    // - цвет иконки - для подсветки стандартной иконки элемента в дереве. - т.е. смена фона в зависимости от типа. -- Пока НЕ исп.
    private Color iconFontColor;
    /* style - Font.PLAIN, BOLD, ITALIC, or BOLD+ITALIC. - накладывается на тип стиля элемента */
    private int     styleType;
    private boolean defaultType;


    public WType ( String ruName, String enName, String descr, Color color, Color iconFontColor, int styleType )
    {
        this.ruName     = ruName;
        this.enName     = enName;
        this.descr      = descr;
        this.color          = color;
        this.iconFontColor  = iconFontColor;
        this.styleType      = styleType;
        defaultType     = false;
    }

    public WType ()
    {
        ruName  = enName = descr = null;
        color   = iconFontColor = null;
        styleType   = Font.PLAIN;
        defaultType = false;
    }

    public String toString()
    {
        StringBuilder result;

        result  = new StringBuilder ( 512 );
        result.append ( "[ WType: enName = '" );
        result.append ( getEnName() );
        result.append ( "'; ruName = " );
        result.append ( getRuName() );
        result.append ( "; color = " );
        result.append ( getColor() );
        result.append ( "; descr = " );
        result.append ( getDescr() );
        result.append ( "; iconFontColor = " );
        result.append ( getIconFontColor() );
        result.append ( "; styleType = " );
        result.append ( getStyleType() );
        result.append ( "; defaultType = " );
        result.append ( isDefaultType() );
        result.append ( "' ]" );

        return result.toString();
    }

    @Override
    public void toXml ( int outLevel, OutputStream out ) throws WEditException
    {
        int     ic;
        Color   color;

        Log.file.debug ( "element = %s", this );

        try
        {
            ic  = outLevel + 1;

            outTitle ( outLevel, TYPE, EN_NAME, getEnName(), out );

            outTag ( ic, RU_NAME,   getRuName(),   out );
            outTag ( ic, DESCR,     getDescr(),    out );

            color   = getColor();
            if ( color != null )  outTag ( ic, ElementCons.COLOR, Convert.color2str ( color ), out );

            color   = getIconFontColor();
            if ( color != null )  outTag ( ic, ICON_FONT_COLOR,   Convert.color2str ( color ), out );

            outTag ( ic, ElementCons.STYLE_TYPE,    Integer.toString ( getStyleType() ), out );    // bold, italic...
            //outTag ( ic,  ICON_NAME, getIconName(), out );

            outTag ( ic,  ElementCons.DEFAULT_TYPE, isDefaultType(), out );

            endTag ( outLevel, TYPE, out );

        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML представления типа Книги '", getEnName(), "' в поток :\n", e );
        }
    }

    /* Сравнение толкьо по англ имени. */
    @Override
    public int compareTo ( WType type )
    {
        int result;

        if ( type == null ) return -1;

        result  = Utils.compareToWithNull ( getRuName(), type.getRuName() );

        return result;
    }

    @Override
    public WType cloneObj ()
    {
        WType result;

        result = new WType ( getRuName(), getEnName(), getDescr(), getColor(), getIconFontColor(), getStyleType() );
        result.setDefaultType ( isDefaultType() );

        return result;
    }

    @Override
    public int getSize ()
    {
        // В размер книги это все не входит
        return 0;
    }

    public String getRuName ()
    {
        return ruName;
    }

    public void setRuName ( String ruName )
    {
        this.ruName = ruName;
    }

    public String getEnName ()
    {
        return enName;
    }

    public void setEnName ( String enName )
    {
        this.enName = enName;
    }

    public String getDescr ()
    {
        return descr;
    }

    public void setDescr ( String descr )
    {
        this.descr = descr;
    }

    public Color getColor ()
    {
        return color;
    }

    public void setColor ( Color color )
    {
        this.color = color;
    }

    public Color getIconFontColor ()
    {
        return iconFontColor;
    }

    public void setIconFontColor ( Color iconFontColor )
    {
        this.iconFontColor = iconFontColor;
    }

    public int getStyleType ()
    {
        return styleType;
    }

    public void setStyleType ( int styleType )
    {
        this.styleType = styleType;
    }

    public void setDefaultType ( boolean aDefault )
    {
        this.defaultType = aDefault;
    }

    public boolean isDefaultType ()
    {
        return defaultType;
    }
}
