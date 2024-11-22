package svj.wedit.v6.tools;


import svj.wedit.v6.Par;
import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.WType;
import svj.wedit.v6.obj.book.*;
import svj.wedit.v6.obj.book.element.StyleName;
import svj.wedit.v6.obj.book.element.StyleType;
import svj.wedit.v6.obj.book.element.WBookElement;

import javax.swing.*;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.02.2012 16:43:24
 */
public class BookStructureTools
{
    /*
     * Выдать дефолтный список типов элементов.
   -- скрытая    (служебная - hidden)
   -- продумать - think
   -- готово     (release)
   -- draft   - черновик
   -- work   - в работе  (либо отсутствует)

     */
    public static Map<String,WType> getDefaultBookElementTypes()
    {
        Map<String,WType>   result;
        WType               type;

        result = new HashMap<String,WType> ();
        // String ruName, String enName, String descr, String iconName - иконка - то же что и дефолтная книга, толкьо другого цвета. Либо наложение на иконку книги маленьких значков -- не наглядно.
        // - work - здесь цвет = null, т.к. применяется только цвет элемента.
        type    = new WType ( "рабочая", "work", "Элемент в стадии реализации.", null, null, Font.ITALIC );   // Color.GRAY
        type.setDefaultType ( true );
        result.put ( type.getEnName(), type );
        // - draft
        type    = new WType ( "черновик", "draft", "Элемент в стадии черновых записей.",  WCons.BRAUN_1, WCons.BRAUN_1, Font.ITALIC );
        result.put ( type.getEnName(), type );
        // - release
        type    = new WType ( "написана", "release", "Элемент написан.", Color.GREEN, Color.GREEN, Font.ITALIC );
        result.put ( type.getEnName(), type );
        // - think
        type    = new WType ( "продумать", "think", "Продумать элемент.", Color.RED, Color.RED, Font.ITALIC );
        result.put ( type.getEnName(), type );
        // - hidden
        type    = new WType ( "служебный", "hidden", "Служебный элемент.", Color.BLUE, Color.BLUE, Font.ITALIC );
        result.put ( type.getEnName(), type );
        // - later (позже)
        type    = new WType ( "сделать, но потом", "later", "Элемент, который точно будет реализован, но по-позже.", Color.BLUE, Color.BLUE, Font.ITALIC );
        result.put ( type.getEnName(), type );

        return result;
    }


    public static void setDefaultTypes ( BookStructure bookStructure )
    {
        Map<String,WType> defTypes;

        if ( bookStructure == null )  return;

        bookStructure.clearTypes();
        defTypes = getDefaultBookElementTypes();
        for ( WType type : defTypes.values() )  bookStructure.addType ( type );
    }

    public static void setDefaultElements ( BookStructure bookStructure )
    {
        Collection<WBookElement> result;

        if ( bookStructure == null )  return;

        bookStructure.clearTypes();
        result = getDefaultBookElements();
        for ( WBookElement be : result )  bookStructure.addBookElement ( be );
    }

    /**
     * Дефолтная структура книги.
     * <BR/> По каждому вызову создается новый обьект - чтобы не использовать -- clone ( BookStructure ).
     * <BR/>
     * @return  Дефолтная структура книги.
     */
    public static BookStructure getDefaultStructure ()
    {
        BookStructure   result;
        WEditStyle      style;

        result = new BookStructure();

        // - без имени стиля - т.е. если у куска текста нет описанного стиля - применяем этот.
        style       = StyleTools.createStyle ( StyleType.TEXT, null, Color.BLACK, BookPar.TEXT_FONT_SIZE, "arial", false, false, 10, StyleConstants.ALIGN_JUSTIFIED );
        result.setTextStyle ( style );

        style = StyleTools.createStyle ( StyleType.ANNOTATION, StyleName.ANNOTATION, WCons.PINK_1, BookPar.TEXT_FONT_SIZE, "arial", false, true, 5, StyleConstants.ALIGN_CENTER );
        result.setAnnotationStyle ( style );

        style      = StyleTools.createStyle ( StyleType.TEXT, StyleName.LABEL, WCons.GREEN_3, BookPar.TEXT_FONT_SIZE, "arial", false, false, 5, StyleConstants.ALIGN_LEFT );
        result.setLabelStyle ( style );

        // Элементы - Книга, Часть, Глава, Подглава, Эпизод
        setDefaultElements ( result );

        // Types - work, hidden...
        setDefaultTypes ( result );

        return result;
    }

    /**
     * Проверяется правильность всей структуры.
     * <BR/>    - наличие обязательных параметров структуры (стили текста, метки, аннотации).
     * <BR/>    - наличие обязательных параметров в элементах (цвет и т.д.).
     * <BR/>
     * @param bookStructure   Проверяемая Структура описания элементов книги
     * @param bookName        Название книги - для сообщений об ошибках.
     * @throws svj.wedit.v6.exception.WEditException Найденные ошибки в структуре описания элементов книги.
     */
    public static void validateBookStructure ( BookStructure bookStructure, String bookName ) throws WEditException
    {
        if ( bookStructure == null )
            throw new WEditException ( null, "Структура описания элементов книги отсутствует." );

        if ( bookStructure.getTextStyle() == null )
            throw new WEditException ( null, "В структуре описания элементов книги '",bookName,"'\n отсутствует описание простого текста '", ElementCons.TEXT_STYLE, "'." );
        if ( bookStructure.getAnnotationStyle() == null )
            throw new WEditException ( null, "В структуре описания элементов книги '",bookName,"'\n отсутствует описание аннотации '", ElementCons.ANNOTATION_STYLE, "'." );
        if ( bookStructure.getLabelStyle() == null )
            throw new WEditException ( null, "В структуре описания элементов книги '",bookName,"'\n отсутствует описание меток '", ElementCons.LABEL_STYLE, "'." );

        if ( bookStructure.isWrong() )
            throw new WEditException ( null, "Были ошибки при загрузке описания элементов." );

        /*
        for ( WBookElement element : bookStructure.getBookElements() )
        {
            name   = element.getName();

            // - наличие обязательных параметров
            if ( element.getColor() == null )
                throw new WEditException ( null, "У элемента '", name, "' отсутствует цвет заголовка." );
            if ( element.getFontFamily() == null )
                throw new WEditException ( null, "У элемента '", name, "' отсутствует название шрифта." );
        }
        */
    }

    /**
     * <br/>
     * <br/> - CENTER - 0
     * <br/> - TOP    - 1
     * <br/> - LEFT   - 2
     * <br/> - BOTTOM - 3
     * <br/> - RIGHT  - 4
     * <br/>
     * @return
     */
    public static Collection<WBookElement> getDefaultBookElements ()
    {
        Collection<WBookElement> result;
        WBookElement             bookElement;

        result = new LinkedList<WBookElement>();

        // 0  - Font.MONOSPACED
        bookElement = new WBookElement ( 0 );
        bookElement.setName ( "Книга" );
        bookElement.setAlign ( StyleConstants.ALIGN_CENTER );
        bookElement.setColor ( WCons.BRAUN_1 );
        bookElement.setFontSize ( 18 );
        bookElement.setMargin ( 10 );
        bookElement.setStyleType ( Font.BOLD );
        result.add ( bookElement );

        // 1
        bookElement = new WBookElement ( 1 );
        bookElement.setName ( "Часть" );
        bookElement.setAlign ( StyleConstants.ALIGN_CENTER );
        bookElement.setColor ( Color.RED );
        bookElement.setFontSize ( 16 );
        bookElement.setMargin ( 10 );
        bookElement.setStyleType ( Font.BOLD );
        result.add ( bookElement );

        // 2
        bookElement = new WBookElement ( 2 );
        bookElement.setName ( "Глава" );
        bookElement.setAlign ( StyleConstants.ALIGN_LEFT );
        bookElement.setColor ( Color.BLUE );
        bookElement.setFontSize ( 16 );
        bookElement.setMargin ( 25 );
        bookElement.setStyleType ( Font.BOLD );
        result.add ( bookElement );

        // 3
        bookElement = new WBookElement ( 3 );
        bookElement.setName ( "Подглава" );
        bookElement.setAlign ( StyleConstants.ALIGN_LEFT );
        bookElement.setColor ( WCons.BLUE_6 );
        bookElement.setFontSize ( 14 );
        bookElement.setMargin ( 25 );
        bookElement.setStyleType ( Font.BOLD );
        result.add ( bookElement );

        // 4
        bookElement = new WBookElement ( 4 );
        bookElement.setName ( "Эпизод" );
        bookElement.setAlign ( StyleConstants.ALIGN_CENTER );
        bookElement.setColor ( Color.BLUE );
        bookElement.setFontSize ( 12 );
        bookElement.setMargin ( 25 );
        bookElement.setStyleType ( Font.PLAIN );
        result.add ( bookElement );

        return result;
    }

    /* todo Взять иконку, соответствующую данному уровню. Подкрасить ее соответственно типу. */
    public static Icon getIcon ( BookStructure bookStructure, BookNode bookNode )
    {
        Icon            icon;
        /*
        WBookElement    bookElement;

        bookElement = bookStructure.getElement ( bookNode.getLevel () );

        Log.l.debug ( "add tab bookNode = ", bookNode );
        Log.l.debug ( "bookElement = ", bookElement );
        if ( bookElement != null )
            icon    = bookElement.getTreeIcon();
        else
            icon    = null;
        if ( icon == null )              icon = GuiTools.createImageByFile ( bookNode.getTreeIconFilePath () );
        */
        // пока выдаем одну иконку.
        icon = GuiTools.createImageByFile ( Convert.concatObj ( "img/tree/", Par.TREE_ICON_SIZE, "/book.png" ) );

        return icon;
    }

    //


    /**
     * Сформировать стиль титла элемента - Для текстовой панели.
     * <br/> Внимание! При использовании в качестве имени стиля значений вида 2_hidden, 2hidden, hidden2 - стиль создается
     * полностью пустой - ни одного значения (цвет, смещение и т.д.) в нем нет.
     * <br/>
     * @param bookStructure Описание структуры.
     * @param bookNode      элемент книги, для заголвока которого создается гуи-стиль.
     * @return   ГУИ-стиль.
     */
    //public static AttributeSet getElementStyle ( BookStructure bookStructure, BookNode bookNode )
    public static WEditStyle getElementStyle ( BookStructure bookStructure, BookNode bookNode )
    {
        WEditStyle      result;
        String          styleName, str;
        WType           type;
        WBookElement    bookElement;
        Color           textColor;
        int             styleType;   // bold, italic...

        type    = null;

        try
        {
            bookElement = bookStructure.getElement ( bookNode.getLevel() );

            str         = bookNode.getElementType(); // ??? - TreeObjType.BOOK_NODE
            Log.l.debug ( "- Start. bookNode = %s", bookNode );
            Log.l.debug ( "--- type name 1 = %s", str );

            //if ( str == null )   bookStructure.getDefaultTypeName();
            //if ( str == null )   str = "work"; // hack !!! - значит дефолтное почему-то не отработало.
            /*
            if ( str == null )
            {
                styleName   = Integer.toString ( bookNode.getLevel() );
            }
            else
            {
                // Если тип равен типу по-умолчанию - сделать null
                if ( Utils.compareToWithNull ( str, bookStructure.getDefaultTypeName() ) == 0 )
                {
                    str         = null;
                    styleName   = Integer.toString ( bookNode.getLevel() );    // Это дефолтный тип - обнуляем его.
                }
                else
                    styleName   = Convert.concatObj ( bookNode.getLevel(), WCons.STYLE_NAME_SEPARATOR,  str );
            }
            //*/
            //styleName   = Convert.concatObj ( bookNode.getLevel(), WCons.STYLE_NAME_SEPARATOR,  str );

            // Имя стиля - это уровень элемента.
            styleName   = Integer.toString ( bookNode.getLevel() );

            // Если тип равен типу по-умолчанию - сделать null
            if ( (str != null) && ( Utils.compareToWithNull ( str, bookStructure.getDefaultTypeName() ) == 0 ) )    str         = null;

            result      = new WEditStyle ( StyleType.ELEMENT, styleName );

            result.addAttribute ( StyleName.STYLE_NAME, styleName );
            result.addAttribute ( StyleName.BOOK_NODE_NAME, bookNode.getName() );

            if ( str == null )
                type = null;
            else
                type = bookStructure.getType ( str );

            Log.l.debug ( "--- styleName = %s; typeName 2 = %s; type = %s", styleName, str, type );


            // ------------- Наполняем стиль значениями -----------------

            // Цвет - из типа
            if ( type == null )
            {
                textColor  = bookElement.getColor();
            }
            else
            {
                textColor  = type.getColor();
                result.addAttribute ( StyleName.TYPE_NAME, type.getEnName() );
            }

            if ( textColor != null ) StyleConstants.setForeground ( result, textColor );

            // FontFamily - из элемента
            str         = bookElement.getFontFamily();
            if ( str == null )  str = "Monospaced";
            StyleConstants.setFontFamily ( result, str );

            // Размер шрифта - из элемента
            StyleConstants.setFontSize ( result, bookElement.getFontSize() );

            // Выравнивание - из элемента       -- StyleConstants.ALIGN_LEFT
            StyleConstants.setAlignment ( result, bookElement.getAlign() );

            // Начальный отступ абзаца если есть. - FirstLineIndent
            switch ( bookElement.getAlign() )
            {
                case  StyleConstants.ALIGN_LEFT :
                    StyleConstants.setFirstLineIndent ( result, bookElement.getMargin() );
                    break;
                case  StyleConstants.ALIGN_RIGHT :
                    StyleConstants.setRightIndent ( result, bookElement.getMargin() );
                    break;
            }

            // Тип стиля  -- складывать только по-битно.
            if ( type != null )
                styleType = bookElement.getStyleType() | type.getStyleType();  // побитовое OR
            else
                styleType = bookElement.getStyleType();

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
            Log.l.error ( Convert.concatObj ( "Ошибка создания стиля. bookNode  = ", bookNode ), e);
        }

        Log.l.debug ( "Finish. create style for title Book\n\t book = %s/%s\n\t type = %s\n\t style = %s", bookNode.getName(), bookNode.getLevel(),type, result );
        return result;
    }

    /**
     * Уровень выделяем из имени стиля - первая лексема до разделителя "подчеркивание".
     * НЕТ, по новому, имя стиля это и есть уровень.
     * @param styleName  уровень_тип
     * @return   Номер уровня элемента.
     */
    public static int getLevel ( String styleName )
    {
        int     result;

        result = 0;
        if ( styleName == null )  return result;

        result  = Convert.getInt ( styleName, 0 );

        return result;
    }

    public static Collection<WBookElement> cloneElements ( Collection<WBookElement> bookElements )
    {
        Collection<WBookElement> result;

        result = new LinkedList<WBookElement>();
        for ( WBookElement be : bookElements )
        {
            result.add ( be.cloneObj() );
        }
        return result;
    }

    public static Collection<WType> cloneTypes ( Map<String, WType> types )
    {
        Collection<WType> result;

        result = new LinkedList<WType>();
        for ( WType be : types.values() )
        {
            result.add ( be.cloneObj() );
        }
        return result;
    }

}
