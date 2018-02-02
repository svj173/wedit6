package svj.wedit.v6.function.book.export;


import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.rtf.RtfWriter2;
import svj.wedit.v6.Par;
import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.params.SimpleParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.function.FileWriteFunction;
import svj.wedit.v6.tools.BookTools;
import svj.wedit.v6.tools.DialogTools;
import svj.wedit.v6.tools.FileTools;
import svj.wedit.v6.tools.StringTools;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Enumeration;


/**
 * Преобразовать содержание в RTF.
 * <BR/> Контекстное меню на элементе книги в дереве.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 14.11.2013 16:00:23
 */
public class ConvertContentToRtfFunction extends FileWriteFunction
{
    //private Font textFont, attributeFont;

    /**  Межстрочный интервал, в пикселях. */
    private float   leading = 10;
    //private BookStructure bookStructure;
    //private String PARAM_NAME = "fileName";


    public ConvertContentToRtfFunction ()
    {
        setId ( FunctionId.CONVERT_CONTENT_TO_RTF );
        setName ( "Преобразовать содержание выделенных глав в RTF");
        setIconFileName ( "to_rtf.png" );
    }


    private SimpleParameter getPar ()
    {
        SimpleParameter sp;
        String PARAM_NAME = "fileName";

        sp  = (SimpleParameter) getParameter ( PARAM_NAME );
        if ( sp == null )
        {
            sp  = new SimpleParameter ( PARAM_NAME, null ); // дефолтное значение
            sp.setHasEmpty ( false );
            setParameterToBook ( PARAM_NAME, sp );
        }

        return sp;
    }


    public void handle ( ActionEvent event ) throws WEditException
    {
        TreeObj[]           selectNodes;
        File                file;
        FileOutputStream    fos;
        Document            document;
        HeaderFooter        header;
        BookContent         bookContent;
        String              fileName;

        try
        {
            // Взять выделенный элемент - но только верхнего уровня.
            // Там же, в глубине, проверяется отмеченность одноуровневых элементов. Без проверки на корень.
            selectNodes  = BookTools.getSelectedNodesForCut ( true );

            // Взять из параметра директорию, куда сохраняли последний раз.
            fileName    = getPar().getValue ();
            if ( fileName == null )  fileName = Par.USER_HOME_DIR;

            // Запросить имя сохраняемого файла
            file    = new File ( fileName );
            file    = FileTools.selectFileName ( Par.GM.getFrame (), file );
            if ( file == null ) return; // Не стоит сообщать что была отмена.

            // TODO Если файл уже существует - запросить разрешение на перезапись

            fos     = new FileOutputStream ( file );

            // Создать исходный RTF документ
            document = new Document ( PageSize.A4, 50, 50, 50, 50 );
            RtfWriter2.getInstance ( document, fos );

            // page
            document.setPageCount ( 1 );
            document.addCreationDate();    // ???
            // we add some meta information to the document
            document.addAuthor ( "Sergey Afanasiev" );
            document.addSubject ( "s_afa@yahoo.com" );
            // Header
            header = new HeaderFooter ( null, null );
            header.setAlignment ( Element.ALIGN_CENTER );
            document.setHeader ( header );
            //document.setFooter ( footer );

            document.open();

            // Взять описание книги
            // - Взять текущую книгу
            bookContent     = Par.GM.getFrame().getCurrentBookContentPanel().getObject();
            //bookStructure   = bookContent.getBookStructure();

            // Создать RTF font для текста  - не исп.
            /*
            textFont        = GuiTools.createRtfFont ( bookStructure.getTextStyle () );
            textFont.setSize ( 8 );
            attributeFont   = GuiTools.createRtfFont ( bookStructure.getAnnotationStyle () );
            attributeFont.setSize ( 8 );
            */

            // Скинуть рекурсивно
            for ( TreeObj treeObj : selectNodes )
            {
                nodeTitle2rtf ( treeObj, document, 0 );
            }

            document.close();

            // Сохранить новое значение параметра
            getPar().setValue ( file.toString() );
            bookContent.setEdit ( true );  // иначе новое значение параметра не сохранится в файле книги.

            DialogTools.showMessage ( "Преобразование", "Файл : " + file );

        } catch ( FileNotFoundException fe )        {
            Log.file.error ( "err", fe );
            throw new WEditException ( fe, "Файл не найден." );
        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка преобразования : ", e );
        }
    }

    private void nodeTitle2rtf ( TreeObj treeObj, Document document, int level )    throws WEditException
    {
        BookNode    bookNode;
        String      phase;
        Enumeration en;
        Paragraph   paragraph;
        TreeObj     to;

        bookNode  = null;
        phase     = "start";
        try
        {
                // Взять обьект
                bookNode = ( BookNode ) treeObj.getUserObject();

                // ------------- Заголовок -------------
                phase       = "title";
                // Занести заголовок согласно стиля.
                paragraph   = createParagraph ( bookNode, level );
                document.add ( paragraph );
                // Занести аннотацию согласно стиля.
                paragraph   = createAnnotation ( bookNode, level );
                if ( paragraph != null )  document.add ( paragraph );


                // ------------- Дочерние элементы -------------
                phase   = "children";
                // Проверка на вложенные обьекты
                en = treeObj.children();
                while ( en.hasMoreElements() )
                {
                    to = ( TreeObj ) en.nextElement ();
                    nodeTitle2rtf ( to, document, level + 1 );
                }

        } catch ( WEditException we )         {
            throw we;
        } catch ( Exception e )         {
            throw new WEditException ( e, "System error. nodeObject = '", bookNode, "'. Phase = ", phase, "\n Error : \n", e.toString() );
        }
    }

    private Paragraph createParagraph ( BookNode bookNode, int level )
    {
        Paragraph   result;
        String      str;
        Font        titleFont;   // RTF font
        Chunk       chunk;

        // Взять стиль Заголовка
        //style   = bookElement.getStyle();
        //style   = BookStructureTools.getElementStyle ( bookStructure, bookNode );
        //titleFont   = GuiTools.createRtfFont ( style );


        // 10 - size
        titleFont  = new com.lowagie.text.Font ( com.lowagie.text.Font.COURIER, 10, Font.BOLD, Color.BLACK );
        // абзац
        result = new Paragraph (leading);
        // Выравнивание
        result.setAlignment ( Paragraph.ALIGN_LEFT );
        // Красная строка (10)
        //result.setFirstLineIndent ( ( int ) StyleConstants.getFirstLineIndent ( attributeFont ) );
        result.setFirstLineIndent ( level * 20 );

        // титл
        str = bookNode.getName();
        if ( str.contains ( WCons.NEW_LINE ) )  str = str.replace ( "\n", "" );
        chunk   = new Chunk ( str, titleFont );
        result.add ( chunk );

        return result;
    }

    private Paragraph createAnnotation ( BookNode bookNode, int level )
    {
        Paragraph   result;
        String      str;
        Font        titleFont;   // RTF font
        Chunk       chunk;

        result  = null;

        // аннотация
        str     = bookNode.getAnnotation();
        if ( ! StringTools.isEmpty ( str ) )
        {
            // 10 - size
            titleFont  = new com.lowagie.text.Font ( com.lowagie.text.Font.COURIER, 9, Font.ITALIC, Color.BLACK );
            // абзац
            result = new Paragraph (leading);
            // Выравнивание
            result.setAlignment ( Paragraph.ALIGN_LEFT );
            // Красная строка (10)
            result.setFirstLineIndent ( level * 20 + 5 );

            //if ( str.contains ( WCons.NEW_LINE ) )  str = str.replace ( "\n", "" );
            titleFont.setStyle ( Font.ITALIC );
            chunk = new Chunk ( str, titleFont );
            result.add ( chunk );
        }

        return result;
    }

    @Override
    public void rewrite ()
    {
    }

}
