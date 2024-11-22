package svj.wedit.v6.obj.book;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.WType;
import svj.wedit.v6.obj.XmlAvailable;
import svj.wedit.v6.obj.book.element.StyleName;
import svj.wedit.v6.obj.book.element.StyleType;
import svj.wedit.v6.obj.book.element.WBookElement;
import svj.wedit.v6.tools.*;

import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.OutputStream;
import java.util.*;
import java.util.List;


/**
 * Описание структуры книги, по уровням.
 * <BR/> Т.е. описывается каждый элемент, начиная с 0-го уровня, и его атрибуты.
 * <BR/>
 * <BR/> Атрибуты элемента:
 * <BR/> - Кол-во строк пропуска после заголовка (при отображении в редакторе)
 * <BR/> - Название элемента  (Часть, Глава, Эпизод)
 * <BR/> - Отображение в дереве
 * <BR/>    - Иконка
 * <BR/>    - Цвет
 * <BR/>    - Шрифт
 * <BR/>    - Размер шрифта
 * <BR/> - Отображение в тексте
 * <BR/>    - Надо ли выводить Название элемента
 * <BR/>    - Надо ли выводить нумерацию элемента
 * <BR/>    - Тип нумерации элемента - сквозной от начала, либо в пределах парента.
 * <BR/>    - Иконка  -- ?
 * <BR/>    - Цвет
 * <BR/>    - Шрифт
 * <BR/>    - Размер шрифта
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.08.2011 15:53:13
 */
public class BookStructure   extends XmlAvailable
{
    /* Элементы. Номер в массиве - это уровень элемента в книге. 0 - всегда сама книга. List - чтобы напрямую дергать clone. */
    private final List<WBookElement>    bookElements;
    /* Список типов элементов книги (рабочий, скрытый и т.д.). Ключ - enName, т.к. именно это имя прописано в XML файле. */
    private final Map<String,WType>     types;
    private WType defaultType;

    // Атрибуты
    private WEditStyle  textStyle, annotationStyle, labelStyle;



    public BookStructure ()
    {
        bookElements    = new LinkedList<WBookElement>();
        types           = new HashMap<String,WType> ();

        defaultType     = new WType();
        defaultType.setEnName ( "work" );   // todo Потом настраивать в редакторе типов
    }

    public BookStructure ( BookStructure structure )
    {
        //elements        = CloneTools.clone ( structure.getElements() );

        bookElements    = CloneTools.clone ( structure.getBookElements() );
        types           = CloneTools.clone ( structure.getTypes () );
        defaultType     = structure.getDefaultType().cloneObj();
    }

    public String toString()
    {
        StringBuilder result;

        result = new StringBuilder();

        result.append ( "\n[ BookStructure :\n defaultType = " );
        result.append ( getDefaultType() );
        result.append ( "\n textStyle = " );
        result.append ( getTextStyle() );
        result.append ( "\n textStyle = " );
        result.append ( getTextStyle() );
        result.append ( "\n annotationStyle = " );
        result.append ( getAnnotationStyle() );
        result.append ( "\n labelStyle = " );
        result.append ( getLabelStyle() );

        result.append ( "\n --- elements (" );
        result.append ( getBookElements().size() );
        result.append ( ") ---" );
        for ( WBookElement bookElement : getBookElements() )
        {
            result.append ( "\n   - " );
            result.append ( bookElement );
        }

        result.append ( "\n --- types (" );
        result.append ( getTypes().size() );
        result.append ( ") ---" );
        for ( WType type : getTypes().values() )
        {
            result.append ( "\n   - " );
            result.append ( type );
        }

        result.append ( "\n]" );

        return result.toString();
    }

    @Override
    public int getSize ()
    {
        // Это не используется при подсчете размера книги.
        return 0;
    }

    /* Применяется для выпадашки навешивания стиля на выделенный текст. */
    public Collection<WEditStyle> getStyles ()
    {
        Collection<WEditStyle> result;

        result  = new ArrayList<WEditStyle>(3+bookElements.size());

        // элементы заголовков
        for ( WBookElement el : bookElements )
        {
            result.add ( el.getStyle() );
        }

        result.add ( getTextStyle() );
        result.add ( getAnnotationStyle() );
        result.add ( getLabelStyle() );
        return result;
    }

    /*
    public List<BookElement> getElements ()
    {
        return elements;
    }

    public BookElement getElementDescName ( int level, String type )
    {
        BookElement be;

        be  = get ( level, type );

        return be;
    }

    public BookElement get ( int number )
    {
        if ( number >= elements.size() )
            return null;
        else
            return elements.get ( number );
    }

    // Выдать по номеру уровня и типу, если задан. Если не задан - выдать первый (для чего это?).
    public BookElement get ( int level, String type )
    {
        for ( BookElement be : getElements() )
        {
            //Log.l.debug ( "---- BookElement = ", be );
            if ( be.getElementLevel() == level )
            {
                if ( type == null )
                    return be;
                else if ( be.getType().equals ( type ))
                    return be;
            }
        }
        return null;
    }
    */

    /* Выдать все по номеру уровня. */
    /*
    public Collection<BookElement> getElements ( int level )
    {
        Collection<BookElement> result;

        result  = new ArrayList<BookElement>();

        for ( BookElement be : getElements() )
        {
            if ( be.getElementLevel() == level )  result.add ( be );
        }
        return result;
    }

    public void add ( BookElement element )  //throws WEditException
    {
        // Валидируем. Уникальное - стиль отображения в Редакторе
        //if ( elements.contains ( element ))
        //    throw new WEditException ( null, "Описание Элемента '", element, "' уже существует." );
        if ( element != null )  elements.add ( element );
    }
    */

    @Override
    public void toXml ( int level, OutputStream out ) throws WEditException
    {
        int    ic, ic2;

        try
        {
            ic  = level + 1;
            ic2 = level + 2;

            outTitle ( level, BookCons.BOOK_STRUCTURE, out );

            // - mandatory - обязательные
            outTitle ( ic, ElementCons.MANDATORIES, out );
            {
                outTag ( ic2, ElementCons.TEXT_STYLE,       getTextStyle().toXml(),          out );
                outTag ( ic2, ElementCons.ANNOTATION_STYLE, getAnnotationStyle().toXml(),    out );
                outTag ( ic2, ElementCons.LABEL_STYLE,      getLabelStyle().toXml(),         out );
            }
            endTag ( ic, ElementCons.MANDATORIES, out );

            // - elements
            /*
            outTitle ( ic, ElementCons.ELEMENTS, out );
            for ( BookElement element : elements )
            {
                element.toXml ( ic2, out );
            }
            endTag ( ic, ElementCons.ELEMENTS, out );
            */

            // ---------------------------------- new ------------------------------
            // - types
            outTitle ( ic, WType.TYPES, out );
            for ( WType type : types.values() )
            {
                type.toXml ( ic2, out );
            }
            endTag ( ic, WType.TYPES, out );

            // - elements
            outTitle ( ic, ElementCons.W_ELEMENTS, out );
            for ( WBookElement element : bookElements )
            {
                element.toXml ( ic2, out );
            }
            endTag ( ic, ElementCons.W_ELEMENTS, out );

            endTag ( level, BookCons.BOOK_STRUCTURE, out );

        } catch ( WEditException we )        {
            throw we;
        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML представления Структуры книги в поток :\n", e );
        }
    }

    /*
    public void set ( int level, BookElement element )   throws WEditException
    {
        if ( element != null )
        {
            // Валидируем. Уникальное - стиль отображения в Редакторе
            if ( elements.contains ( element ))
                throw new WEditException ( null, "Описание Элемента '", element, "' уже существует." );
            // Заменить старый на новый
            elements.set ( level, element );
        }
    }
    */

    public void setTextStyle ( WEditStyle textStyle )
    {
        this.textStyle = textStyle;
    }

    public void setAnnotationStyle ( WEditStyle annotationStyle )
    {
        this.annotationStyle = annotationStyle;
    }

    public void setLabelStyle ( WEditStyle labelStyle )
    {
        this.labelStyle = labelStyle;
    }

    public WEditStyle getTextStyle ()
    {
        return textStyle;
    }

    public WEditStyle getAnnotationStyle ()
    {
        return annotationStyle;
    }

    public WEditStyle getLabelStyle ()
    {
        return labelStyle;
    }

    /**
     * Исходя из имени стиля выдаем тип - element, text, annotation.
     * @param styleName  Составное. Имя стиля + тип элемента (если это элемент) - Часть_work, h3_hidden.
     * @param style  Стиль текста, взятый из свинг-документа.
     * @return   Тип стиля текста.
     */
    public StyleType getStyleType ( String styleName, AttributeSet style )
    {
        StyleType   styleType;
        String      strLevel;
        boolean     b;

        Log.l.debug ( "getStyleType.Start. styleName = %s", styleName );
        //styleType   = StyleType.ELEMENT;
        // ищем среди элементов
        for ( WBookElement element : getBookElements() )
        {
            strLevel    = Integer.toString ( element.getElementLevel() );
            // Если имя стиля == номеру уровня элемента - нашли.
            if ( styleName.equals ( strLevel ) ) return StyleType.ELEMENT;
        }

        // аннотация
        styleType   = getAnnotationStyle().getStyleType();
        if ( styleType.getName().equals ( styleName ) )  return styleType;

        // сложный текст
        if ( styleName.equals ( StyleType.COLOR_TEXT.getName() ) )  return StyleType.COLOR_TEXT;

        // сложный текст 2 - сравниваем стиль простого текcта и наш
        b = GuiTools.compareTextStyle ( style, getTextStyle() );
        Log.l.debug ( "getStyleType.compareTextStyle. styleName = %s", styleName );
        Log.l.debug ( "getStyleType.compareTextStyle. style = %s", style );
        Log.l.debug ( "getStyleType.compareTextStyle. current text style = %s", getTextStyle() );
        Log.l.debug ( "getStyleType.compareTextStyle. compare = %b", b );
        if ( ! b ) return StyleType.COLOR_TEXT;

        //Log.l.debug ( "Finish. styleType = ", styleType );
        return StyleType.TEXT;
    }

    public WEditStyle getStyle ( String styleName )
    {
        StyleType   styleType;
        String      strLevel;

        Log.l.debug ( "Start. styleName = ", styleName );
        //styleType   = StyleType.ELEMENT;
        // ищем среди элементов
        for ( WBookElement element : getBookElements () )
        {
            strLevel    = Integer.toString ( element.getElementLevel() );
            // Если имя стиля начинается с номера уровня элемента - нашли.
            if ( styleName.startsWith ( strLevel ) ) return element.getStyle();
        }

        // аннотация
        styleType   = getAnnotationStyle().getStyleType();
        if ( styleType.getName().equals ( styleName ) )  return getAnnotationStyle();

        // сложный текст
        //if ( styleName.equals ( StyleType.COLOR_TEXT.getName() ) )  return getTextStyle();

        return getTextStyle();
    }

    public void addType ( WType type )
    {
        if ( type != null )   types.put ( type.getEnName(), type );
    }

    public Map<String, WType> getTypes ()
    {
        return types;
    }

    public void clearTypes ()
    {
        types.clear();
    }

    public List<WBookElement> getBookElements ()
    {
        return bookElements;
    }

    public boolean isWrong ()
    {
        return types.isEmpty() || bookElements.isEmpty();
    }

    /**
     * Заменяем на дефолтные:
     * - типы
     * - элементы
     * - стили текста, аннотации, метки
     */
    public void setDefault ()
    {
        WEditStyle  style;

        types.clear();
        types.putAll ( BookStructureTools.getDefaultBookElementTypes() );

        bookElements.clear();
        bookElements.addAll ( BookStructureTools.getDefaultBookElements() );

        // -- когда отладится
        // - без имени стиля - т.е. если у куска текста нет описанного стиля - применяем этот.
        style   = StyleTools.createStyle ( StyleType.TEXT, null, Color.BLACK, 10, "arial", false, false, 3, StyleConstants.ALIGN_JUSTIFIED );
        setTextStyle ( style );

        style   = StyleTools.createStyle ( StyleType.ANNOTATION, StyleName.ANNOTATION, Color.PINK, 10, "arial", false, true, 5, StyleConstants.ALIGN_JUSTIFIED );
        setAnnotationStyle ( style );

        style   = StyleTools.createStyle ( StyleType.TEXT, StyleName.LABEL, Color.GRAY, 10, "arial", false, true, 5, StyleConstants.ALIGN_JUSTIFIED );
        setLabelStyle ( style );
    }

    public void setDefaultTypes ()
    {
        types.clear();
        types.putAll ( BookStructureTools.getDefaultBookElementTypes() );
    }

    public void clear ()
    {
        clearElements();
        clearTypes ();
    }

    public void clearElements ()
    {
        bookElements.clear();
    }

    public void addBookElement ( WBookElement bookElement )
    {
        bookElements.add ( bookElement );
    }

    public WBookElement getElement ( int level )
    {
        for ( WBookElement element : getBookElements () )
        {
            if ( element.getElementLevel() == level )  return element;
        }
        return null;

        //return bookElements.get(level);
    }

    public WBookElement getElement ( BookNode bookNode )
    {
        if ( bookNode == null )
            return null;
        else
            return getElement ( bookNode.getLevel() );
    }

    public String getDefaultTypeName ()
    {
        return defaultType.getEnName();
    }

    public WType getDefaultType ()
    {
        return defaultType;
    }

    public WType getType ( String typeName )
    {
        WType result;
        result =  types.get ( typeName );
        if ( result == null )  result = defaultType;
        return result;
    }

    public void setElements ( Collection<WBookElement> bookElements )
    {
        int ic;

        clearElements();
        ic = 0;
        for ( WBookElement element : bookElements )
        {
            element.setElementLevel ( ic );
            addBookElement ( element );
            ic++;
        }
    }

    public void setTypes ( List<WType> list )
    {
        types.clear();
        for ( WType type : list )
        {
            addType ( type );
        }
        reinitDefaultType();
    }

    private void reinitDefaultType ()
    {
        for ( WType type : getTypes().values() )
        {
            if ( type.isDefaultType() )
            {
                setDefaultType ( type );
                break;
            }
        }
    }

    public void setDefaultType ( WType defaultType )
    {
        this.defaultType = defaultType;
    }


}
