package svj.wedit.v6.obj.book.element;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.WClone;
import svj.wedit.v6.obj.XmlAvailable;
import svj.wedit.v6.obj.book.BookCons;
import svj.wedit.v6.obj.book.BookNodeTypeNumeric;
import svj.wedit.v6.obj.book.WEditStyle;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.ElementCons;
import svj.wedit.v6.tools.Utils;

import javax.swing.*;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.OutputStream;


/**
 * Обьект описания стилей элемента книги - Часть, Глава, и т.д. + типы.
 * <BR/>
 * <BR/> Атрибуты элемента:
 * <BR/> - Уровень элемента (фиксированное значение, не подлежит изменению)
 * <BR/> - Кол-во строк пропуска после заголовка (при отображении в редакторе)
 * <BR/> - Название элемента  (Часть, Глава, Эпизод)
 * <BR/> - Тип элемента  (рабочий, скрытый, черновик...)
 * <BR/> - Отображение в дереве
 * <BR/>    - Иконка    - выборка файла с препросмотром и автоматической закачкой файла в директорию img/tree
 * <BR/>    - Цвет      - возможность выбора
 * <BR/>    - Шрифт     - название шрифта, тип (plain,bold,italic), размер в пикселях
 * <BR/> - Отображение в тексте
 * <BR/>    - Надо ли выводить Название элемента
 * <BR/>    - Надо ли выводить нумерацию элемента
 * <BR/>    - Тип нумерации элемента - сквозной от начала, либо в пределах парента.
 * <BR/>    - Цвет
 * <BR/>    - Шрифт   - название шрифта, тип (plain,bold,italic), размер в пикселях
 * <BR/>
 * <BR/> Имя стиля = name+_+type
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.08.2011 16:14:11
 */
@Deprecated // Старая реализация.
public class BookElement   extends XmlAvailable  implements Comparable<BookElement>, WClone<BookElement>
{
    private int     elementLevel;
    private int     rowSpace;
    private String  name;     // (Часть, Глава, Эпизод)

    /* Тип элемента книги (work, hidden). Совокупностью уровня и типа можно управлять - т.е. привязывать к ним какое-то поведение (Например: не выводить на печать уровень_0+тип_скрытый) */
    private String  type    = "work";

    // Tree
    private Color   treeFgColor;
    private Font    treeFont;
    // Пока заносится из виджета редактирования
    private Icon    treeIcon;

    // Editor
    private Color   textColor;
    private Font    textFont;
    
    private boolean    hasPrintElementName, hasPrintNumber;

    private BookNodeTypeNumeric typeNumeric;


    public String toString()
    {
        StringBuilder result;
        
        result  = new StringBuilder ( 512 );
        result.append ( "[ BookElement: name = '" );
        result.append ( getName() );
        result.append ( "'; level = " );
        result.append ( getElementLevel() );
        result.append ( "; type = '" );
        result.append ( getType() );
        result.append ( "'; rowSpace = '" );
        result.append ( getRowSpace() );
        result.append ( "'; treeColor = '" );
        result.append ( getTreeFgColor() );
        result.append ( "'; treeFont = '" );
        result.append ( getTreeFont() );
        result.append ( "'; textColor = '" );
        result.append ( getTextColor() );
        result.append ( "'; textFont = '" );
        result.append ( getTextFont() );
        result.append ( "'; hasPrintElementName = '" );
        result.append ( isHasPrintElementName() );
        result.append ( "'; hasPrintNumber = '" );
        result.append ( isHasPrintNumber() );
        result.append ( "'; typeNumeric = '" );
        result.append ( getTypeNumeric() );
        result.append ( "' ]" );

        return result.toString ();
    }


    public BookElement ( int level )
    {
        this.elementLevel = level;
    }

    public BookElement ( int level, String name, String type, int rowSpace, Color textColor,
                         String textFontName, int textFontStyle, int textFontSize,
                         Color treeColor, String treeFontName, int treeFontStyle, int treeFontSize,
                         boolean printName, boolean printNumber, BookNodeTypeNumeric typeNumeric
                         )
    {
        Font font;

        this.elementLevel   = level;
        this.type           = type;
        this.name           = name;
        this.rowSpace       = rowSpace;
        this.textColor      = textColor;

        font = new Font( textFontName, textFontStyle, textFontSize );
        textFont    = font;

        this.treeFgColor      = treeColor;

        font = new Font( treeFontName, treeFontStyle, treeFontSize );
        treeFont = font;

        this.hasPrintElementName    = printName;
        this.hasPrintNumber         = printNumber;
        this.typeNumeric            = typeNumeric;
    }

    public BookElement cloneObj ()
    {
        BookElement result;

        result  = new BookElement ( getElementLevel() );

        result.setHasPrintElementName ( isHasPrintElementName() );
        result.setHasPrintNumber ( isHasPrintNumber() );
        result.setTypeNumeric ( getTypeNumeric() );
        result.setName ( getName() );
        result.setRowSpace ( getRowSpace() );
        result.setTextColor ( getTextColor() );
        result.setTextFont ( getTextFont() );
        result.setTreeFgColor ( getTreeFgColor() );
        result.setTreeFont ( getTreeFont() );
        result.setTreeIcon ( getTreeIcon() );
        result.setType ( getType() );

        return result;
    }

    @Override
    public void toXml ( int outLevel, OutputStream out ) throws WEditException
    {
        int ic;

        Log.file.debug ( "element = ", this );

        try
        {
            ic  = outLevel + 1;

            outTitle ( outLevel, ElementCons.ELEMENT, BookCons.LEVEL, Integer.toString ( getElementLevel() ), out );

            outTag ( ic,  ElementCons.NAME,         getName(),                              out );
            outTag ( ic,  ElementCons.TYPE,         getType(),                              out );
            outTag ( ic,  ElementCons.ROW_SPACE,    Integer.toString ( getRowSpace() ),     out );
            outTag ( ic,  ElementCons.TREE_FG_COLOR,Convert.color2str ( getTreeFgColor()),  out );
            outTag ( ic,  ElementCons.TREE_FONT,    Convert.font2str ( getTreeFont()),      out );
            outTag ( ic,  ElementCons.TEXT_FG_COLOR,Convert.color2str ( getTextColor()),    out );
            outTag ( ic,  ElementCons.TEXT_FONT,    Convert.font2str ( getTextFont()),      out );
            outTag ( ic,  ElementCons.PRINT_NAME,   Boolean.toString ( isHasPrintElementName()),    out );
            outTag ( ic,  ElementCons.PRINT_NUMBER, Boolean.toString ( isHasPrintNumber()),         out );
            outTag ( ic,  ElementCons.TYPE_NUMERIC, getTypeNumeric().toString(),                    out );

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
    public int compareTo ( BookElement element )
    {
        int result, ic;

        if ( element == null ) return -1;

        ic  = Utils.compareToWithNull ( getName(), element.getName() );
        if ( ic == 0 )
        {
            ic  = Utils.compareToWithNull ( getTextFont(), element.getTextFont() );
            if ( ic == 0 )
                result  = Utils.compareToWithNull ( getTextColor(), element.getTextColor() );
            else
                result = ic;
        }
        else
        {
            result = ic;
        }
        return result;
    }

    
    public String getType ()
    {
        return type;
    }

    public void setType ( String type )
    {
        this.type = type;
    }

    public void setElementLevel ( int elementLevel )
    {
        this.elementLevel = elementLevel;
    }

    public int getElementLevel ()
    {
        return elementLevel;
    }

    public int getRowSpace ()
    {
        return rowSpace;
    }

    public void setRowSpace ( int rowSpace )
    {
        this.rowSpace = rowSpace;
    }

    public String getName ()
    {
        return name;
    }

    public void setName ( String name )
    {
        this.name = name;
    }

    public Color getTreeFgColor ()
    {
        return treeFgColor;
    }

    public void setTreeFgColor ( Color treeFgColor )
    {
        this.treeFgColor = treeFgColor;
    }

    public Font getTreeFont ()
    {
        return treeFont;
    }

    public void setTreeFont ( Font treeFont )
    {
        this.treeFont = treeFont;
    }

    public Icon getTreeIcon ()
    {
        return treeIcon;
    }

    public void setTreeIcon ( Icon treeIcon )
    {
        this.treeIcon = treeIcon;
    }

    public Color getTextColor ()
    {
        return textColor;
    }

    public void setTextColor ( Color textColor )
    {
        this.textColor = textColor;
    }

    public Font getTextFont ()
    {
        return textFont;
    }

    public void setTextFont ( Font textFont )
    {
        this.textFont = textFont;
    }

    public boolean isHasPrintElementName ()
    {
        return hasPrintElementName;
    }

    public void setHasPrintElementName ( boolean hasPrintElementName )
    {
        this.hasPrintElementName = hasPrintElementName;
    }

    public boolean isHasPrintNumber ()
    {
        return hasPrintNumber;
    }

    public void setHasPrintNumber ( boolean hasPrintNumber )
    {
        this.hasPrintNumber = hasPrintNumber;
    }

    public BookNodeTypeNumeric getTypeNumeric ()
    {
        return typeNumeric;
    }

    public void setTypeNumeric ( BookNodeTypeNumeric typeNumeric )
    {
        this.typeNumeric = typeNumeric;
    }

    public void setTypeNumeric ( String value )
    {
        try
        {
            typeNumeric = BookNodeTypeNumeric.valueOf ( value );
        } catch ( IllegalArgumentException e )        {
            typeNumeric = BookNodeTypeNumeric.ALL_BOOK;
            Log.l.error ( Convert.concatObj ( "err for '", value, "'." ), e);
        }
    }

    public StyleType getStyleType ()
    {
        return StyleType.ELEMENT;
    }

    public String getStyleName()
    {
        return Convert.concatObj ( name, '_', type );
    }

    public WEditStyle getStyle ()
    {
        WEditStyle result;

        result  = new WEditStyle ( StyleType.ELEMENT, getStyleName() );

        result.addAttribute ( StyleName.STYLE_NAME, getStyleName() );

        if ( textColor != null ) StyleConstants.setForeground ( result, textColor );
        if ( textFont != null ) StyleConstants.setFontFamily ( result, textFont.getFamily() );

        //StyleConstants.setFontSize ( result, size );
        if ( textFont != null ) StyleConstants.setFontSize ( result, textFont.getSize() );
        StyleConstants.setAlignment ( result, StyleConstants.ALIGN_LEFT );

        // Начальный отступ абзаца если есть. - FirstLineIndent
        StyleConstants.setFirstLineIndent ( result, 1 );

        return result;
    }

}
