package svj.wedit.v6.obj.book;


import svj.wedit.v6.obj.book.element.StyleType;
import svj.wedit.v6.tools.StyleTools;
import svj.wedit.v6.tools.Utils;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.Enumeration;


/**
 * Стиль текста, применяемый в редакторе.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 01.06.2012 13:22:41
 */
public class WEditStyle  extends SimpleAttributeSet implements Comparable<WEditStyle>
{
    private StyleType   styleType;   // подтип стиля. Например: элемент, метка в тексте (у метки имя стиля = текст). Если не задан - простой текст.
    private String      styleName;   // имя стиля: текст, аннотация, номерЭлемента

    public WEditStyle ( StyleType styleType, String styleName )
    {
        super();
        this.styleType = styleType;
        this.styleName = styleName;
    }

    public WEditStyle ( AttributeSet style, StyleType styleType, String styleName )
    {
        super ( style );
        this.styleType = styleType;
        this.styleName = styleName;
    }

    public WEditStyle ( AttributeSet style, StyleType styleType )
    {
        super ( style );
        this.styleType = styleType;
        this.styleName = styleType.getName();
    }

    // По-моему раньше здесь скидывалось в книгу в структуре по-умолчанию, и потом парсилось из этой структуры. Т.е. toString не был прописан.

    /**
     * Данное отображение НЕ менять! Т.к. оно в таком виже сохраняется в XML и потом парсится при загрузке.
     * По другорму нельзя т.к. применяется AttributeSet.toString()
     * @return
     */
    public String toString()
    {
        StringBuilder result;

        result  = new StringBuilder ( 128 );
        result.append ( "[ WEditStyle: styleType = " );
        result.append ( styleType );
        result.append ( "; styleName=" );
        result.append ( styleName );
        result.append ( "; AttributeSet: " );
        result.append ( super.toString() );
        result.append ( " ]" );
        return result.toString();
    }

    /**
     * Преобразовать в формат для сохранение в файле xml. Для удобства парсинга. Не исп возврат каретки, т.к. в XML он теряется.
     * <br/>
     * <br/> Примеры стилей:
     * <br/> 1) styleType=COLOR_TEXT;styleName=color_text;FirstLineIndent=10.0;styleName=color_text;
     *  resolver=NamedStyle:default {italic=false,size=12,foreground=sun.swing.PrintColorUIResource[r=51,g=51,b=51],FONT_ATTRIBUTE_KEY=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12],name=default,bold=false,family=Dialog,};Alignment=2;
     *
     * <br/> - здесь параметр  resolver нам - лишний, игнорируем его
     * <br/>
     * @return  XML образ обьекта
     */
    public String toXml()
    {
        StringBuilder   result;
        Enumeration     en;
        Object          objName, objKey;

        result  = new StringBuilder ( 128 );
        result.append ( "styleType=" );
        result.append ( styleType );

        if ( styleName != null )
        {
            result.append ( ";styleName=" );
            result.append ( styleName );
        }

        en = getAttributeNames();
        while ( en.hasMoreElements() )
        {
            objName = en.nextElement();
            objKey  = getAttribute ( objName );
            if ( objKey != null )
            {
                // игнорируем resolver
                if ( ! objName.toString().equals ( "resolver" ) )
                {
                    result.append ( ';' );
                    result.append ( objName );
                    result.append ( '=' );
                    result.append ( objKey );
                }
            }
        }

        result.append ( ';' );

        return result.toString();
    }

    public StyleType getStyleType ()
    {
        return styleType;
    }

    public String getStyleName ()
    {
        return styleName;
    }

    public void setStyleName ( String styleName )
    {
        this.styleName = styleName;
    }

    /**
     * Правильный ли это стиль.
     * @param compositeStyleName  Составное имя стиля = имя-стиля_имя-типа
     * @return
     */
    public boolean isThisStyle ( String compositeStyleName )
    {
        return Utils.compareToWithNull ( getStyleName(), compositeStyleName ) == 0;
    }

    @Override
    public int compareTo ( WEditStyle o )
    {
        int result;

        result = 1;
        if ( o != null )
        {
            result = Utils.compareToWithNull ( getStyleName(), o.getStyleName() );
            /*
            if (  == 0 )
            {
                // совпали имена стилей. например, элемент/ для текста поидее надо сранвивать цвет и шрифт.
            }
            */
        }
        return result;
    }

    @Override
    public boolean equals ( Object obj )
    {
        boolean result;
        if ( (obj != null) && (obj instanceof WEditStyle) )
        {
            WEditStyle wStyle = (WEditStyle) obj;
            result = compareTo ( wStyle ) == 0;
        }
        else
        {
            result = false;
        }
        return result;
    }

    public Color getColor ()
    {
        return StyleConstants.getForeground ( this );
    }

    public Font getFont ()
    {
        return StyleTools.createFont ( this );
    }

}
