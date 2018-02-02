package svj.wedit.v6.function.book.export;


import com.lowagie.text.*;
import com.lowagie.text.rtf.RtfWriter2;
import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.params.ParameterCategory;
import svj.wedit.v6.function.params.SimpleParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.book.BookStructure;
import svj.wedit.v6.obj.book.TextObject;
import svj.wedit.v6.obj.function.SimpleBookFunction;
import svj.wedit.v6.tools.*;

import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Enumeration;

/**
 * Преобразовать выделенные элементы книги в файл формата RTF.
 * <BR/> Если выделены разноуровневые элементы, то работа ведется только с элементами самого высокого уровня.
 * <BR/>
 //paragraph   = new Paragraph ( floatLeading, name, title );
Paragraph p = (Paragraph)phrase;
setAlignment(p.alignment);
setLeading(phrase.getLeading(), p.multipliedLeading);
setIndentationLeft(p.getIndentationLeft());
setIndentationRight(p.getIndentationRight());
setFirstLineIndent(p.getFirstLineIndent());
setSpacingAfter(p.spacingAfter());
setSpacingBefore(p.spacingBefore());
setExtraParagraphSpace(p.getExtraParagraphSpace());
 * <BR/>
 * <BR/>  Параметры, которые необходимо задавать (в диалоге) - берутся из описания элемента.
 * <BR/>
 * <BR/> 1) Межстрочный интервал, в пикселях. (10)
 * <BR/> 2) Header - текст вверху слева и справа от нумерации страниц. (пусто)
 * <BR/> 3) Фонт простого текста. И других элементов текста.
 * <BR/> 4) Начальный номер страницы.
 * <BR/> 5) Вид форматирования текста (JUSTIFIED)
 * <BR/> 6) Красная строка (10)
 * <BR/>
 * <BR/> Если стоит флаг - Были изменения - Сохранять автоматом, т.к. в файл идут данные из обьектов.
 * Либо сообщать что необходимо сначала сохранить.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 28.02.2013 14:56
 */
public class SaveAsRTFSelectFunction extends SimpleBookFunction
{
    /* Параметр для хранения имени файла. */
    private Font textFont, attributeFont;

    /**  Межстрочный интервал, в пикселях. */
    private float   leading = 10;
    private BookStructure bookStructure;

    private String PARAM_NAME = "fileName";


    public SaveAsRTFSelectFunction ()
    {
        setId ( FunctionId.CONVERT_SELECTION_TO_RTF );
        setName ( "Преобразовать выделенное в RTF" );
        setIconFileName ( "to_rtf.png" );
        // хранение параметров функции - имя результирующего файла - в самой книге.
        setParamsType ( ParameterCategory.BOOK );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        TreeObj[]           selectNodes;
        File                file;
        FileOutputStream    fos;
        Document            document;
        HeaderFooter        header;
        BookContent         bookContent;
        SimpleParameter     sp;
        String              fileName;

        // Взять выделенный элемент - но только верхнего уровня.
        // Там же, в глубине, проверяется отмеченность одноуровневых элементов. Без проверки на корень.
        selectNodes  = BookTools.getSelectedNodesForCut(true);

        try
        {
            // Если стоит флаг - Были изменения - Сохранять автоматом, т.к. в файл идут данные из обьектов.
            // - Взять текущую книгу
            bookContent     = Par.GM.getFrame().getCurrentBookContentPanel().getObject();
            // Если были изменения в текстах
            // - скинуть тексты из редакторов в обьекты -
            // Здесь требуется TextPanel - из нее берется документ.
            // - скинуть в файл - ? - зачем
            //FileTools.saveBook ( bookContent );
            // Пока просто ругаемся.
            //throw new WEditException ( "Есть измененные тексты!\n Необходимо предварительно их сохранить,\n т.к. в RTF файл данные берутся из обьекта Книги." );
            BookTools.text2node ( bookContent );

            // Берем параметр который хранит имя файловой директории, в которую последний раз конвертировали тексты.
            // -- Лучше и имя файла (И так заносим имя. Значит файловый диалог его обрезает).
            sp  = (SimpleParameter) getParameterFromBook ( PARAM_NAME );
            if ( sp == null )
            {
                sp  = new SimpleParameter ( PARAM_NAME, null );
                sp.setHasEmpty ( false );
                setParameterToBook ( PARAM_NAME, sp );
            }

            // Взять из параметра директорию, куда сохраняли в последний раз.
            fileName    = sp.getValue ();
            if ( fileName == null )  fileName = Par.USER_HOME_DIR;

            // Запросить имя сохраняемого файла
            file    = new File ( fileName );
            file    = FileTools.selectFileName ( Par.GM.getFrame(), file );
            if ( file == null ) return; // Не стоит сообщать что была Oтмена преобразования.

            // todo Взять имя автора и его e-mail - из текущего Сборника

            fos     = new FileOutputStream ( file );

            // Создать исходный RTF документ
            document = new Document ( PageSize.A4, 50, 50, 50, 50 );
            RtfWriter2.getInstance ( document, fos );

            // page
            document.setPageCount ( 1 );
            document.addCreationDate ();    // ???
            // we add some meta information to the document  -- todo - Это надо прописывать в Сборниках, как обязательные параметры Сборника.
            document.addAuthor ( "Sergey Afanasiev" );
            document.addSubject ( "s_afa@yahoo.com" );
            // Footer
            header = createHeader ( null );
            document.setHeader ( header );
            //document.setFooter ( footer );

            document.open();

            // Взять описание книги
            bookStructure   = bookContent.getBookStructure();

            // Создать RTF font для текста
            textFont        = GuiTools.createRtfFont ( bookStructure.getTextStyle() );
            textFont.setSize ( 8 );
            attributeFont   = GuiTools.createRtfFont ( bookStructure.getAnnotationStyle() );
            attributeFont.setSize ( 8 );

            // Скинуть рекурсивно
            for ( TreeObj treeObj : selectNodes )
            {
                node2rtf ( treeObj, document, 0 );
            }

            document.close();

            // Сохранить новое значение параметра
            Log.file.debug ( "-- Parameter before = %s", sp );
            sp.setValue ( file.toString() );
            bookContent.setEdit ( true );  // иначе новое значение параметра не сохранится в файле книги.
            Log.file.debug ( "-- Parameter after = %s", sp );

            DialogTools.showMessage ( "Преобразование", "Создан файл : " + file );

        } catch ( FileNotFoundException fe )        {
            Log.file.error ( "err", fe );
            throw new WEditException ( fe, "Файл не найден." );
        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка преобразования : ", e );
        }
    }

    private void node2rtf ( TreeObj treeObj, Document document, int level )    throws WEditException
    {
        BookNode                bookNode;
        String                  phase, str;
        Collection<TextObject>  text;
        Enumeration             en;
        AttributeSet            style;
        Paragraph               paragraph;
        Font                    tfont;   // RTF font
        Chunk                   chunk;
        TreeObj                 to;

        bookNode  = null;
        phase     = "start";
        try
        {
                // Взять обьект
                bookNode = ( BookNode ) treeObj.getUserObject();

                // ------------- Заголовок -------------
                phase       = "title";
                // Занести заголовок согласно стиля.
                paragraph   = createParagraph ( bookNode );
                document.add ( paragraph );

                // ------------- Текст -------------
                phase   = "text";
                text    = bookNode.getText();
                if ( ( text != null ) && ( !text.isEmpty() ) )
                {
                    paragraph   = new Paragraph (leading);
                    // Выравнивание
                    paragraph.setAlignment ( Paragraph.ALIGN_JUSTIFIED_ALL );
                    // Красная строка
                    paragraph.setFirstLineIndent ( 10 );
                    //doc.insertString ( doc.getLength(), WCons.NEW_LINE + WCons.NEW_LINE, styleText );
                    for ( TextObject textObj : text )
                    {
                        str     = textObj.getText();
                        style   = textObj.getStyle();
                        if ( style != null ) tfont  = GuiTools.createRtfFont ( style );
                        else    tfont   = textFont;
                        //str =  + WCons.NEW_LINE;
                        chunk   = new Chunk ( str, tfont );
                        //document.add ( paragraph );
                        paragraph.add ( chunk );
                    }
                    document.add ( paragraph );
                }

                // ------------- Дочерние элементы -------------
                phase   = "children";
                // Проверка на вложенные обьекты
                en = treeObj.children();
                while ( en.hasMoreElements() )
                {
                    to = ( TreeObj ) en.nextElement ();
                    node2rtf ( to, document, level + 1 );
                }

        } catch ( WEditException we )         {
            throw we;
        } catch ( Exception e )         {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "System error. nodeObject = '", bookNode, "'. Phase = ", phase, "\n Error : \n", e.toString() );
        }
    }

    private Paragraph createParagraph ( BookNode bookNode )
    {
        Paragraph   result;
        String str, type, name;
        int align, i;
        AttributeSet style;
        Chunk chunk;
        Font titleFont;   // RTF font
        //BookElement bookElement;

        // Взять тип элемента
        //type    = nodeObject.getElementName (); // часть, глава, эпизод...
        //type    = bookNode.getElementType();   // ???

        // Взять сам элемент описания
        //bookElement = bookStructure.get ( bookNode.getLevel(), type );
        //element = em.getElement ( type );

        // Взять стиль Заголовка
        //style   = bookElement.getStyle();
        style   = BookStructureTools.getElementStyle ( bookStructure, bookNode );
        titleFont   = GuiTools.createRtfFont ( style );
        // Взять сам заголовок
        name    = bookNode.getName();

        result   = new Paragraph ( leading, name, titleFont );
        result.add ( "\n" );
        //result   = new Paragraph ( name, title );

        // align
        align   = StyleConstants.getAlignment ( style );
        result.setAlignment ( align );

        // атрибуты
        str    = bookNode.getAnnotation();
        if ( str != null )
        {
            chunk   = new Chunk ( str, attributeFont );
            result.add ( chunk );
            result.add ( "\n" );
        }

        /*
        attrs   = bookNode.getAttribute ();
        for ( i=0; i<attrs.size (); i++ )
        {
            str = (String) attrs.get(i);
            chunk   = new Chunk ( str, attributeFont );
            result.add ( chunk );
            result.add ( "\n" );
        }
        */

        // Вставить переводы строк после заголовка
        //ic      = nodeObject.getTitleVkSize ();
        chunk   = new Chunk ( "\n", textFont );
        for ( i=0; i<2; i++ )   result.add ( chunk );

        return result;
    }


    /**
     * Создать колонтитл - с текстом справа и слева от номера страницы.
     * <br/> Вынести в общую функцию конвертации в RTF.
     * <br/>
     * @param rootBookNode
     * @return
     */
    public HeaderFooter createHeader ( BookNode rootBookNode )
    {
        HeaderFooter    header;
        /*
        String          leftMsg, rightMsg, str, sep, fileName, author, email;
        File file;

        file    = Par.BookFile;
        sep     = ";  ";
        book    = (BookNodeObject) gm.getContent().getContentPanel().getRootNode().getUserObject ();

        str     = BookTools.getAttrIntContent ( book, WCons.VERSION, null );
        if ( str == null )    str   = "";
        else                  str   = "V:" + str;
        // Создать левую половинку: имя файла, версия, дата печати
        if ( file == null )
            fileName    = "-";
        else
            fileName    = file.getName();
        leftMsg  = fileName  + sep + str + sep
                + DateTools.getCurrentRUDate () + "    - ";

        // Создать правую половинку: Имя автора, e-mail
        // - author
        author      = BookTools.getAttrContent ( book, WCons.AUTHOR, "" );
        email       = BookTools.getAttrContent ( book, WCons.EMAIL, "" );
        rightMsg    = " -    " + author + sep  + email;

        header = new HeaderFooter ( new Phrase (leftMsg), new Phrase (rightMsg) );
        */
        header = new HeaderFooter ( null, null );
        header.setAlignment ( Element.ALIGN_CENTER );
        //footer.setAlignment ( Element.ALIGN_CENTER );
        return header;
    }

    @Override
    public void rewrite ()    {    }

}
