package svj.wedit.v6.book;


import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.*;
import svj.wedit.v6.obj.book.element.StyleName;
import svj.wedit.v6.obj.book.element.StyleType;
import svj.wedit.v6.tools.BookStructureTools;
import svj.wedit.v6.tools.BookTools;
import svj.wedit.v6.tools.GuiTools;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


/**
 * Преобразовать древовидный обьект книги в текстовый документ для редактирования.
 * <BR/>
 * <BR/> При скидывании в текст редактора:
 -- вставлять пустые строки после титла, согласно заданному значению (либо 2).
 -- По окончании всего текста титла - также вставлять пустые строки (3-4)
 -- После аннотации также вставлять пустые строки (2)
 * <BR/>
 * <BR/>
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 28.10.2011 11:22:24
 */
public class BookNodeToText
{
    public BookNodeToText ()
    {
    }

    public void process ( TextPanel textPanel, BookNode bookNode, BookContent bookContent, int cursor )
        throws WEditException
    {
        StyledDocument      sd;
        StringBuilder       errorMessage;

        Log.l.debug ( "Start" );

        try
        {
            sd      = textPanel.getDocument();

            // - Очистить текстовую панель данной части.
            sd.remove ( 0, sd.getLength() );

            errorMessage = new StringBuilder ( 256 );
            node2editDocument ( bookNode, bookContent, sd, errorMessage, 0 );

            textPanel.revalidate();
            textPanel.repaint();

            // Курсор - в начало текста
            textPanel.setCurrentCursor ( cursor );

            if ( errorMessage.length() > 0 )
                throw new WEditException ( null, "Copy NODE to DOC error.\n Chapter '", bookNode.getName(), "'\n Error : \n", errorMessage );

            // Скинуть флаг, что были изменения в тексте.
            textPanel.setEdit ( false );

        } catch ( WEditException we )        {
            throw we;
        } catch ( Exception e )        {
            Log.l.error ( "err", e );
            throw new WEditException ( e, "Ошибка преобразования обьекта в текст :\n", e );
        }

        Log.l.debug ( "Finish" );
    }

    private void node2editDocument ( BookNode bookNode, BookContent bookContent, StyledDocument doc, StringBuilder errorMessage, int level )
            throws WEditException
    {
        WEditStyle          style, styleText;
        String              str, name;
        Icon                icon;
        int                 p0, p1, align;
        boolean             hasText, replace;

        hasText = false;

        //logger.debug ( "Start. level = " + level );
        //logger.debug ( "doc = " + doc + ", document = " + document + ", level = " + level );

        try
        {
            // Взять стиль текста по умолчанию.
            styleText = bookContent.getBookStructure().getTextStyle();
            Log.l.debug ( "- %d) styleText = %s",level, styleText );

            replace = true;
            // replace = false - не заменять старые атрибуты, а просто добавить новые (merge). true - удалить все старые.

            // --------------------------- Заголовок ------------------------------------

            // Взять имя
            name    = bookNode.getName();
            Log.l.debug ( "- %d) Title = %s", level, name );

            // Убрать в имени последний символ Возврата каретки
            name    = parseTitle ( name );

            // Взять стиль Заголовка  -  styleName = name_type
            style   = BookStructureTools.getElementStyle ( bookContent.getBookStructure(), bookNode );
            Log.l.debug ( "- %d) title name = '%s'; title style = %s", level, name, style );
            // Занести заголовок в конец документа.  -- без ВК
            p0 = doc.getLength();
            doc.insertString ( doc.getLength(), name, style );

            // Навесить атрибуты параграфа - выравнивание, смещение и т.д.
            p1 = doc.getLength();
            align = StyleConstants.getAlignment ( style );
            if ( align != StyleConstants.ALIGN_LEFT )
            {
                // replace = false - не заментяь старые атрибуты а просто добавить новые.
                doc.setParagraphAttributes ( p0+1, p1 - p0, style, replace );
            }

            // ВК - которое убрали из заголовка
            // - добавляем пустую строку, чтобы в пустом элементе, после заголовка было хоть что-то от стиля текста, а не заголовка.
            doc.insertString ( doc.getLength(), "\n\n", styleText );

            // ------------------------------- аннотация --------------------------------------
            // Взять аннотацию, если она есть todo - аннотация может быть много-строчной.
            str = bookNode.getAnnotation();
            Log.l.debug ( "- %d) annotation = %s", level, str );
            if ( ( str != null ) && ( !str.isEmpty() ) )
            {
                //logger.debug ( "Add atribute" );
                // Взять стиль
                style = bookContent.getBookStructure().getAnnotationStyle();
                Log.l.debug ( "- %d) annotation style = %s", level, style );
                p0 = doc.getLength();
                doc.insertString ( doc.getLength(), str, style );
                p1 = doc.getLength();
                doc.setParagraphAttributes ( p0+1, p1 - p0, style, replace );
                // После аннотации также вставлять пустые строки (2) -- почему-то необходимо. иначе текст начнется сразу же в той же строке что и аннотация. где-то у аннотации при сохранении убираются ВК?
                doc.insertString ( doc.getLength(), "\n\n", styleText );
            }

            // ---------------- row space ------------------
            /*
            // Пока НЕ используем заданный пропуск между заголовком и текстом - какой есть, такой и применяем.
            ic = element.getRowSpace();
            Log.l.debug ( "-",level, ") ", "row space = ", ic );
            for ( i = 0; i < ic; i++ )
            {
                //logger.debug ( " -- VK" );
                doc.insertString ( doc.getLength(), WCons.NEW_LINE, styleText );
            }
            */

            AttributeSet aStyle;
            // ------------------- текст ---------------------
            for ( TextObject textObj : bookNode.getText() )
            {
                if ( textObj instanceof ImgTextObject )
                {
                    // ---------------- IMAGE -------------------
                    icon    = GuiTools.createImageByFile ( textObj.getText() );
                    BookTools.insertImg ( doc, doc.getLength(), icon, textObj.getText() );
                    hasText = true;  // Иначе следующий заголовок пойдет прямо от иконки.
                }
                else
                {
                    // ---------------- TEXT -------------------
                    str = textObj.getText();
                    // todo Здесь теряется часть текста - если внутри текста есть переводы строк  - Ошибка парсера ???.
                    Log.l.debug ( "--- %d) -- text = %s",level, str );
                    aStyle = textObj.getStyle();
                    Log.l.debug ( "--- %d) -- text style = %s",level, aStyle );
                    if ( aStyle == null )
                    {
                        style = styleText;
                    }
                    else
                    {
                        // Прописать что это сложный текст
                        if ( aStyle instanceof WEditStyle )
                        {
                            style = (WEditStyle) aStyle;
                        }
                        else if ( aStyle instanceof SimpleAttributeSet )
                        {
                            style = new WEditStyle ( aStyle, StyleType.TEXT );
                            style.addAttribute ( StyleName.STYLE_NAME, StyleType.COLOR_TEXT.getName() );
                        }
                        else
                        {
                            style = new WEditStyle ( aStyle, StyleType.TEXT, StyleType.COLOR_TEXT.getName() );
                        }
                    }
                    Log.l.debug ( "--- %d) -- create text style = %s", level, style );

                    // запоминаем начальную позицию курсора
                    p0 = doc.getLength();
                    doc.insertString ( doc.getLength(), str, style );

                    // наложение стиля параграфа - левый отступ, выравнивания...
                    // это параграф - обновить текст на экране (иначе не отобразится)
                    // конечная позиция курсора.
                    p1 = doc.getLength();
                    // обновляем вcтавленный текст. false - предыдущие атрибуты текста НЕ затираем.
                    //doc.setParagraphAttributes ( p0, p1 - p0, ElementTools.PARAGRAPH_STYLE, false );
                    doc.setParagraphAttributes ( p0+1, p1 - p0, style, replace );

                    //hasText = true;
                    hasText = false;
                }
            }

            //-- По окончании всего текста титла - также вставлять пустые строки (3-4) -- ???
            // - При иконке иначе следующий заголовок пойдет прямо от иконки
            if ( hasText )  doc.insertString ( doc.getLength(), "\n\n\n", styleText );

            // Проверка на вложенные обьекты
            for ( BookNode node : bookNode.getNodes() )
            {
                Log.l.debug ( "-- %d) child node = %s", level, node );
                node2editDocument ( node, bookContent, doc, errorMessage, level + 1 );
            }

        } catch ( WEditException we )         {
            throw we;
        } catch ( Exception e )         {
            if ( bookNode == null ) name   = "error";
            else    name = bookNode.toString();
            createError ( errorMessage, name, "System Error", e.toString() );
            Log.l.error ( "Java System Error", e );
            // !!! НЕ искл - т.к. сообщение об ошибке - через errorMessage
            //throw new WEditException ( "node2editDocument error : '" + e.getMessage() + "'.", e );
        } catch ( Throwable te )         {
            // Пытаемся поймать переполнение памяти, возникающее время от времени.
            if ( bookNode == null ) name   = "error";
            else    name = bookNode.toString ();
            createError ( errorMessage, name, "Java System Error", te.toString () );
            Log.l.fatal ( "Java System Error", te );
            // !!! НЕ искл - т.к. сообщение об ошибке - через errorMessage
        }
    }

    /* Убрать в имени последний символ Возврата каретки */
    private String parseTitle ( String name )
    {
        String result;

        if ( name == null )  name = "??";

        if ( name.endsWith ( "\n" ) )
            result  = name.substring ( 0, name.length() - 1 );
        else if ( name.endsWith ( "\r\n" ) )
            result  = name.substring ( 0, name.length() - 2 );
        else
            result  = name;

        return result;
    }

    private void createError ( StringBuilder result, String chapterTitle, String errorMess, String text )
    {
        result.append ( "Chapter : '" );
        result.append ( chapterTitle );
        result.append ( "'. " );
        result.append ( errorMess );
        result.append ( ". '" );
        result.append ( text );
        result.append ( "'." );
        result.append ( WCons.NEW_LINE );
    }

}
