package svj.wedit.v6.tools;


import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.WEditStyle;
import svj.wedit.v6.obj.book.element.StyleName;
import svj.wedit.v6.obj.book.element.StyleType;

import javax.swing.text.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 01.02.2012 12:17:41
 */
public class StyleTools
{
    /**
     * Наполнить Документ фиксированными стилями.
     * @param doc  Документ.
     */
    public void styleContent ( StyledDocument doc )
    {
        Style style = doc.getStyle ( "base" );
        doc.setLogicalStyle ( 0, style );
        style = doc.getStyle ( "underline" );
        doc.setCharacterAttributes ( 22, 10, style, false );
        style = doc.getStyle ( "highlight" );
        doc.setCharacterAttributes ( 62, 26, style, false );

        Style logicalStyle = doc.getLogicalStyle ( 0 );
        style = doc.getStyle ( "tableParagraph" );
        doc.setParagraphAttributes ( 90, 1, style, false );
        style = doc.getStyle ( "table" );
        doc.setCharacterAttributes ( 90, 1, style, false );
        doc.setLogicalStyle ( 92, logicalStyle );

        style = doc.getStyle ( "blue" );
        doc.setCharacterAttributes ( 118, 13, style, false );
        style = doc.getStyle ( "italic" );
        doc.setCharacterAttributes ( 166, 18, style, false );
        style = doc.getStyle ( "green" );
        doc.setCharacterAttributes ( 235, 9, style, false );
        doc.setCharacterAttributes ( 248, 9, style, false );
        style = doc.getStyle ( "bold" );
        doc.setCharacterAttributes ( 263, 10, style, false );
        doc.setCharacterAttributes ( 278, 6, style, false );
    }

    public static String color2hex ( Color color )
    {
        if ( color == null )
            return null;
        else
        {
            String hex = Integer.toHexString(color.getRGB() & 0xffffff);
            if (hex.length() < 6)     hex = "000000".substring(0, 6 - hex.length()) + hex;
            //hex = "#" + hex;
            return hex;
        }
    }

    public static Object getAttribute ( AttributeSet attr, Object attrName )
    {
        Object      result, name;
        Enumeration en;

        result = null;
        if ( attr != null )
        {
            en  = attr.getAttributeNames();
            while ( en.hasMoreElements() )
            {
                name    = en.nextElement();
                if ( Utils.compareToWithNull ( name, attrName ) == 0 )
                {
                    result = attr.getAttribute(name);
                    break;
                }
            }
        }

        return result;
    }

    public static Font createFont ( AttributeSet swing )
    {
        Font    result;
        int     style, ic, size;
        String  fontFamily;

        // style
        style   = Font.PLAIN;
        if ( StyleConstants.isBold ( swing ) )
        {
            if ( StyleConstants.isItalic (swing) )
                style  = Font.BOLD + Font.ITALIC;
            else
                style  = Font.BOLD;
        }
        else if ( StyleConstants.isItalic (swing) ) style   = Font.ITALIC;

        // size
        ic = StyleConstants.getFontSize (swing);
        if ( ic <= 0 )   ic  = 10;
        size    = ic;

        // family
        fontFamily  = StyleConstants.getFontFamily ( swing );

        //result  = new Font ( family, size, style, color );
        result      = new Font ( fontFamily, style, size );

        return result;
    }

    /**
     * Создать стиль текста в текстовом Редакторе.
     * @param name  Имя элемента - глава, часть, эпизод...
     * @param type  Тип элемента - work, hidden...
     * @param color Цвет заголовка в тексте
     * @param font  Фонт заголовка в тексте
     * @param size  Размер (в пикселях) заголовка в тексте
     * @return  Созданный обьект стиля.
     */
    public static AttributeSet createTitleStyle ( String name, String type, Color color, Font font, int size )
    {
        SimpleAttributeSet result;
        String styleName;

        result  = new SimpleAttributeSet();

        styleName   = Convert.concatObj ( name, '_', type );
        result.addAttribute ( StyleName.STYLE_NAME, styleName );

        if ( color != null ) StyleConstants.setForeground ( result, color );
        if ( font != null ) StyleConstants.setFontFamily ( result, font.getFamily() );
        
        //StyleConstants.setFontSize ( result, size );
        if ( font != null ) StyleConstants.setFontSize ( result, font.getSize() );
        StyleConstants.setAlignment ( result, StyleConstants.ALIGN_LEFT );

        // Начальный отступ абзаца если есть. - FirstLineIndent
        StyleConstants.setFirstLineIndent ( result, 1 );

        return result;
    }

    public static WEditStyle createStyle ( StyleType styleType, String styleName, Color color, Font font, Integer firstLineMargin, int alignType )
    {
        Log.l.debug ( "Start. styleType = %s; styleName = %s; color = %s; font = %s; firstLineMargin = %s; alignType = %d", styleType, styleName, color, font, firstLineMargin, alignType );
        if ( firstLineMargin == null )  firstLineMargin = 0;
        return  createStyle ( styleType, styleName, color, font.getSize(), font.getFamily(), font.isBold(), font.isItalic(), firstLineMargin, alignType );
    }

    public static WEditStyle createStyle ( StyleType styleType, String styleName, Color color, int size, String family,
                                           boolean hasBold, boolean hasItalic, int firstLineMargin, int alignType )
    {
        WEditStyle result;

        result  = new WEditStyle ( styleType, styleName );

        if ( styleName != null )  result.addAttribute ( StyleName.STYLE_NAME, styleName );

        if ( color != null ) StyleConstants.setForeground ( result, color );

        StyleConstants.setFontFamily ( result, family );
        StyleConstants.setFontSize ( result, size );

        if ( hasBold )   StyleConstants.setBold ( result, true );
        if ( hasItalic ) StyleConstants.setItalic ( result, true );

        // Начальный отступ абзаца если есть. - FirstLineIndent
        StyleConstants.setFirstLineIndent ( result, firstLineMargin );

        StyleConstants.setAlignment ( result, alignType );

        return result;
    }

    public static SimpleAttributeSet createStyle ( String styleText )
    {
        SimpleAttributeSet result;
        Properties prop;

        prop    = createProperties(styleText);
        result  = createStyleFromProperties(prop);

        return result;
    }


    /**
     * Создание стиля из текстовых свойств стиля.
     *
     * @param sattr Проперти для стиля.
     * @param styleType   Тип стиля данного элемента.
     * @param styleName   Название стиля.
     * @return      Новый стиль
     */
    private static WEditStyle createStyleFromProperties ( Properties sattr, StyleType styleType, String styleName )
    {
        WEditStyle result;
        int     ic;
        float   fc;
        Color   color;
        String  str;

        //logger.debug ( "attr = " + sattr );

        result  = new WEditStyle ( styleType, styleName );

        str     = sattr.getProperty ( StyleName.STYLE_NAME );
        if ( str != null )
        {
            result.addAttribute ( StyleName.STYLE_NAME, str );
        }

        str     = sattr.getProperty ( "family" );
        if ( str != null )
            StyleConstants.setFontFamily ( result, str );

        str     = sattr.getProperty ( "size" );
        if ( str != null )
        {
            ic  = NumberTools.getInt(str,10);
            StyleConstants.setFontSize ( result, ic );
        }

        str     = sattr.getProperty ( "foreground" );
        if ( str != null )
        {
            // java.awt.Color[r=255/g=0/b=51]
            color   = createColor ( str );
            //logger.debug ( "colorName = " + str + ", color = " + color );
            StyleConstants.setForeground ( result, color );
        }
        str     = sattr.getProperty ( "color" );
        if ( str != null )
        {
            // java.awt.Color[r=255/g=0/b=51]
            // red
            color   = createColor ( str );
            //logger.debug ( "colorName = " + str + ", color = " + color );
            StyleConstants.setForeground ( result, color );
        }

        str     = sattr.getProperty ( "Alignment" );
        if ( str != null )
        {
            ic  = NumberTools.getInt(str,StyleConstants.ALIGN_LEFT);
            StyleConstants.setAlignment ( result, ic );
        }

        // Начальный отступ абзаца если есть.
        str     = sattr.getProperty ( "FirstLineIndent" );
        if ( str != null )
        {
            fc  = NumberTools.getFloat(str,1);
            StyleConstants.setFirstLineIndent ( result, fc );
        }

        str     = sattr.getProperty ( "italic" );
        //if ( str != null && str.equalsIgnoreCase ( WCons.YES ))
        if ( str != null && str.equalsIgnoreCase ( "true" ))
        {
            StyleConstants.setItalic ( result, true );
        }

        str     = sattr.getProperty ( "bold" );
        //if ( str != null && str.equalsIgnoreCase ( WCons.YES ))
        if ( str != null && str.equalsIgnoreCase ( "true" ))
        {
            StyleConstants.setBold ( result, true );
        }

        str     = sattr.getProperty ( "underline" );
        //if ( str != null && str.equalsIgnoreCase ( WCons.YES ))
        if ( str != null && str.equalsIgnoreCase ( "true" ))
        {
            StyleConstants.setUnderline ( result, true );
        }


        // Левый отступ всего текста если есть.
        str     = sattr.getProperty ( "left_margin" );
        if ( str != null )
        {
            ic = Integer.parseInt ( str );
            StyleConstants.setLeftIndent ( result, ic );
        }

        // Правый отступ абзаца если есть.
        str     = sattr.getProperty ( "right_margin" );
        if ( str != null )
        {
            ic = Integer.parseInt ( str );
            StyleConstants.setRightIndent ( result, ic );
        }

        return result;
    }

    /**
     * Создание стиля из текстовых свойств стиля.
     *
     * @param sattr
     * @return
     */
    private static SimpleAttributeSet createStyleFromProperties ( Properties sattr )
    {
        SimpleAttributeSet result;
        int ic;
        float   fc;
        Color color;
        String  str;

        result  = new SimpleAttributeSet ();

        //logger.debug ( "attr = " + sattr );
        str     = sattr.getProperty ( "styleName" );
        if ( str != null )
            result.addAttribute ( "styleName" , str );

        str     = sattr.getProperty ( "family" );
        if ( str != null )
            StyleConstants.setFontFamily ( result, str );

        str     = sattr.getProperty ( "size" );
        if ( str != null )
        {
            ic  = NumberTools.getInt(str,10);
            StyleConstants.setFontSize ( result, ic );
        }

        str     = sattr.getProperty ( "foreground" );
        if ( str != null )
        {
            // java.awt.Color[r=255/g=0/b=51]
            color   = createColor ( str );
            //logger.debug ( "colorName = " + str + ", color = " + color );
            StyleConstants.setForeground ( result, color );
        }

        str     = sattr.getProperty ( "Alignment" );
        if ( str != null )
        {
            ic  = NumberTools.getInt(str,StyleConstants.ALIGN_LEFT);
            StyleConstants.setAlignment ( result, ic );
        }

        // Начальный отступ абзаца если есть.
        str     = sattr.getProperty ( "FirstLineIndent" );
        if ( str != null )
        {
            fc  = NumberTools.getFloat(str,1);
            StyleConstants.setFirstLineIndent ( result, fc );
        }

        // TODO
        /*
        str     = sattr.getProperty ( "italic" );
        if ( str != null && str.equalsIgnoreCase ( WCons.YES ))
        {
            StyleConstants.setItalic ( result, true );
        }

        str     = sattr.getProperty ( "bold" );
        if ( str != null && str.equalsIgnoreCase ( WCons.YES ))
        {
            StyleConstants.setBold ( result, true );
        }
        */


        // Левый отступ всего текста если есть.
        str     = sattr.getProperty ( "left_margin" );
        if ( str != null )
        {
            ic = Integer.parseInt ( str );
            StyleConstants.setLeftIndent ( result, ic );
        }

        // Правый отступ абзаца если есть.
        str     = sattr.getProperty ( "right_margin" );
        if ( str != null )
        {
            ic = Integer.parseInt ( str );
            StyleConstants.setRightIndent ( result, ic );
        }

        return result;
    }


    /**
     * Исходная строка описания цвета:
     * <BR/> Варианты:
     * <BR/> - java.awt.Color[r=255/g=0/b=51]
     * <BR/>
     * @param color   Строковое опсиание цвета.
     * @return        Цвет как обьект.
     */
    private static Color createColor ( String color )
    {
        Color           result;
        String          str;
        BufferedReader  br;
        Properties      prop;
        int             r,g,b;

        result  = Color.BLACK;

        if ( color == null )  return result;

        if ( color.startsWith ( "java.awt.Color[" ) )
        {
            prop   = new Properties();

            str = color.replace ("java.awt.Color[","");
            str = str.replace('/', '\n' );
            str = str.trim();
            str = str.substring(0, str.length()-1);

            br  = StringTools.createReader(str);
            try {
                prop.load(br);
            } catch ( IOException e) {
                e.printStackTrace();
            }
            //System.out.println("Prop = " + prop );

            str = prop.getProperty("r");
            r   = NumberTools.getInt (str, 0);
            str = prop.getProperty("g");
            g   = NumberTools.getInt (str, 0);
            str = prop.getProperty("b");
            b   = NumberTools.getInt (str, 0);


            result  = new Color(r,g,b);
        }
        else
        {
            //Это названия цветов
            if ( color.equals ( "red" ) )           result = Color.RED;
            else if ( color.equals ( "green" ) )    result = Color.GREEN;
            else if ( color.equals ( "black" ) )    result = Color.BLACK;
            else if ( color.equals ( "blue" ) )     result = Color.BLUE;
            // todo
        }

        return result;
    }

    /**
     * <br/> Приходит строка вида (styleText):
     * <br/> [ WEditStyle : styleType = TEXT; styleName = null; AttributeSet = family=Monospaced Alignment=0 FirstLineIndent=0.0 foreground=java.awt.Color[r=0,g=0,b=0] size=12  ]
     * <br/>
     * <br/> Необходимо распарсить все параметры.
     * <br/>
     * @param styleText
     * @param styleType
     * @param styleName
     * @return
     */
    public static WEditStyle createStyle ( String styleText, StyleType styleType, String styleName )
    {
        WEditStyle  result;
        Properties  prop;

        Log.file.debug ( "--- createStyle. source style text = %s", styleText );
        prop    = createProperties ( styleText );
        Log.file.debug ( "--- createStyle. create style prop = %s", prop );
        result  = createStyleFromProperties ( prop, styleType, styleName );
        Log.file.debug ( "--- createStyle. created style = %s", result );

        return result;
    }

    private static Properties createProperties ( String styleText )
    {
        BufferedReader  br;
        Properties      result;
        String          str;

        result   = new Properties();

        if ( styleText == null ) return result;

        // Парсим текст на пары

        try
        {
            // - for Color
            str = styleText.replace(",g", "/g" );
            str = str.replace(",b", "/b" );

            str = str.replace(';', '\n' );

            // Удаляем крайние фигурные скобки - если они есть
            if ( str.startsWith ( "{" ))  str = str.substring(1,str.length()-1);

            br  = StringTools.createReader(str);
            result.load(br);

        } catch ( Exception e ) {
            Log.l.error ( Convert.concatObj ( "Style parser error. Style = '", styleText, "'." ), e);
        }

        return result;
    }

//==========================================================

    public static void main ( String[] args )
    {
        SimpleAttributeSet style;
        String inputText =
            "family=arial;FirstLineIndent=3.0;Alignment=0;styleName=unknow;size=10;foreground=java.awt.Color[r=255,g=0,b=51];";

        try
        {
            //System.out.println("Prop = " + result );

            style   = StyleTools.createStyle(inputText, StyleType.TEXT, StyleName.TEXT );
            System.out.println("Create style = " + style );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
