package svj.wedit.v6.function.book.export;


import com.lowagie.text.*;
import com.lowagie.text.rtf.RtfWriter2;
import svj.wedit.v6.Par;
import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.book.export.obj.ConvertParameter;
import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.*;
import svj.wedit.v6.obj.function.AbstractConvertFunction;
import svj.wedit.v6.tools.BookStructureTools;
import svj.wedit.v6.tools.FileTools;
import svj.wedit.v6.tools.GuiTools;

import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;

import java.util.*;

/**
 * Преобразовать книгу в файл формата RTF.
 * <BR/>
 * <BR/> Пример работы с параграфом (абзацем) - настройки параграфа-абзаца:
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
 * <BR/> 6) Красная строка (10)  - firstLineIndent
 * <BR/>
 * <BR/> 7) Выводить ли Название книги.
 * <BR/> 8) Выводить ли имя автора.
 * <BR/> 9) Дополнительное в титле. Например - Фант роман в семи книгах. -- или - Посвящается Татьяне Рушковой.
 * <BR/>
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 26.09.2013 12:56
 */
public class ConvertToRtfFunction extends AbstractConvertFunction // SimpleBookFunction
{
    // RTF fonts
    private com.lowagie.text.Font textFont2, attributeFont2;

    /**  Межстрочный интервал, в пикселях. */
    private float   leading = 10;

    private Document    document;
    //private Paragraph   currentParagraph;   -- Не пройдет, т.к. по document.add ( paragraph ); добавление в параграф заканчиваются (игнорируются после этого).


    public ConvertToRtfFunction ()
    {
        super ( FunctionId.CONVERT_SELECTION_TO_RTF_2, "Преобразовать книгу в RTF", "to_rtf.png", false );

        /*
        setId ( FunctionId.CONVERT_SELECTION_TO_RTF_2 );
        setName ( "Преобразовать книгу в RTF" );
        setIconFileName ( "to_rtf.png" );

        String PARAM_NAME = "convertToRtf";

        bookmarksParameter = new BookmarksParameter ( PARAM_NAME );
        bookmarksParameter.setHasEmpty ( false );
        setParameter ( PARAM_NAME, bookmarksParameter );
        */
    }

    /**
     * Начало конвертации.
     * @param cp
     * @throws WEditException
     */
    @Override
    protected void initConvert ( ConvertParameter cp ) throws WEditException
    {
        HeaderFooter        header;

        //Log.l.info ( "DOC: Start" );

        try
        {
            // Создать RTF font для текста
            textFont2        = GuiTools.createRtfFont ( getBookContent().getBookStructure().getTextStyle() );
            textFont2.setSize ( 8 );
            attributeFont2   = GuiTools.createRtfFont ( getBookContent().getBookStructure().getAnnotationStyle() );
            attributeFont2.setSize ( 8 );

            // Создать исходный RTF документ
            document = new Document ( PageSize.A4, 50, 50, 50, 50 );
            RtfWriter2.getInstance ( document, getFos() );

            // page
            document.setPageCount ( 1 );
            document.addCreationDate ();    // ???
            // we add some meta information to the document
            // - Взять из Автора данного сборника
            //document.addAuthor ( "Sergey Afanasiev" );
            document.addAuthor ( Par.GM.getAuthor().getFullName() );
            //document.addSubject ( "s_afa@yahoo.com" );
            document.addSubject ( Par.GM.getAuthor().getEmail () );
            // Footer
            header = createHeader ( null );
            document.setHeader ( header );
            //document.setFooter ( footer );

            document.open();

        } catch ( Throwable e )       {
            Log.l.error ( "error. cp = "+cp, e );
            throw new WEditException ( e, "Ошибка инициализации : \n", e.getMessage() );
        }
    }


    @Override
    protected Collection<FunctionParameter> getOtherConvertParams ()
    {
        return null;
    }

    @Override
    protected void processImage ( String imgFileName, ConvertParameter cp )
    {
        Paragraph paragraph;
        com.lowagie.text.Image image;
        String fullImgFileName = null;

        // todo - Только если установлен флаг - Применять картинки.

        try
        {
            paragraph   = new Paragraph ( leading );

            // заносим в абзац имя файла
            //paragraph.add ( imgFileName );
            // перевод строки
            //paragraph.add ( Chunk.NEWLINE );

            fullImgFileName = FileTools.createSmallImageFileName(bookContent, imgFileName);

            // заносим в абзац саму картинку
            image = Image.getInstance ( fullImgFileName );
            paragraph.add ( image );

            paragraph.add ( Chunk.NEWLINE );

            document.add ( paragraph );

        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new RuntimeException ( "Ошибка занесения изображения '"+ fullImgFileName+ "' : \n"+ e, e );
        }
    }

    @Override
    protected void processEmptyTitle ( ConvertParameter cp )
    {
    }

    @Override
    protected void processTitle ( String title, int level, ConvertParameter cp, BookNode bookNode )
    {
        Paragraph paragraph;

        // ------------- Заголовок -------------
        try
        {
            // Занести заголовок согласно стиля и типа вывода, заданного в Конвертере.
            paragraph   = createTitleParagraph ( bookNode, cp, title );

            // Аннотация
            if ( cp.isPrintAnnotation() )
            {
                String str = bookNode.getAnnotation();
                if ( str != null )
                {
                    //chunk   = new Chunk ( str, attributeFont2 );
                    //paragraph.add ( chunk );
                    //paragraph.add ( "\n" );

                    document.add ( paragraph );

                    paragraph   = new Paragraph ( leading, str+"\n", attributeFont2 );
                    // - Красная строка для аннотации - надо создавать как параграф
                    paragraph.setFirstLineIndent ( ( int ) StyleConstants.getFirstLineIndent ( attributeFont ) );
                    //paragraph.add ( "\n" );
                    //paragraph.add ( Chunk.NEWLINE );
                    // - annotations align
                    int align   = StyleConstants.getAlignment ( attributeFont );
                    paragraph.setAlignment ( align );
                }
            }

            // Вставить переводы строк после заголовка
            //ic      = nodeObject.getTitleVkSize ();
            Chunk chunk   = new Chunk ( "\n", textFont2 );
            for ( int i=0; i<2; i++ )   paragraph.add ( chunk );

            document.add ( paragraph );

        //} catch ( WEditException we )         {
        //    throw we;
        } catch ( Exception e )         {
            Log.file.error ( "err", e );
            throw new RuntimeException ( "Ошибка создания заголовка '"+ bookNode.getName()+ "'\n Error : \n"+ e, e );
        }
    }

    @Override
    protected void processText ( TextObject textObj, ConvertParameter cp )
    {
        String                  str;
        AttributeSet            style;
        Font                    tfont;   // RTF font
        Chunk                   chunk;
        Paragraph               paragraph;

        /*
        // Один параграф на весь текст Элемента книги.
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
        */


        if ( textObj == null )  return;

        try
        {
            if ( textObj instanceof ImgTextObject )
            {
                // картинка  -- вызовется ранее, в processImage
            }
            else if ( textObj instanceof SlnTextObject )
            {
                // абзац
                paragraph = new Paragraph (leading);
                // Выравнивание
                paragraph.setAlignment ( Paragraph.ALIGN_JUSTIFIED_ALL );
                // Красная строка (10)
                paragraph.setFirstLineIndent ( ( int ) StyleConstants.getFirstLineIndent ( attributeFont ) );
                str     = textObj.getText();
                style   = textObj.getStyle();
                if ( style != null ) tfont  = GuiTools.createRtfFont ( style );
                else    tfont   = textFont2;
                //str =  + WCons.NEW_LINE;

                if ( str.contains ( WCons.NEW_LINE ) )  str = str.replace ( "\n", "" );

                chunk   = new Chunk ( str, tfont );
                //document.add ( paragraph );
                paragraph.add ( chunk );
                //paragraph.add ( Chunk.NEWLINE );
                document.add ( paragraph );
            }
            else if ( textObj instanceof EolTextObject )
            {
                // пустая строка
                paragraph = new Paragraph (leading, "", textFont2 );
                //paragraph.add ( Chunk.NEWLINE );
                document.add ( paragraph );
            }
            else
            {
                // просто текст - добавляем к текущему параграфу. -- Не пройдет, т.к. document.add ( paragraph ); добавление в параграф заканичваются.
                /*
                if ( currentParagraph == null )
                {
                    currentParagraph = new Paragraph (leading);
                    document.add ( currentParagraph );
                }
                */
                paragraph = new Paragraph (leading);
                str     = textObj.getText();
                style   = textObj.getStyle();
                if ( style != null ) tfont  = GuiTools.createRtfFont ( style );
                else    tfont   = textFont2;
                //str =  + WCons.NEW_LINE;
                chunk   = new Chunk ( str, tfont );
                paragraph.add ( chunk );
                document.add ( paragraph );
            }
        } catch ( Exception e )        {
            Log.l.error ( "error", e );
            throw new RuntimeException ( "Ошибка обработки текста\n "+textObj+"\n: "+ e.getMessage(), e );
        }
    }

    protected String getNewLineSymbol ()
    {
        Paragraph paragraph = new Paragraph (leading, "", textFont2 );
        //paragraph.add ( Chunk.NEWLINE );
        try
        {
            document.add ( paragraph );
        } catch ( Exception e ) {
            Log.l.error ( "error", e );
        }
        return WCons.SP;
    }


    @Override
    protected void finishConvert(ConvertParameter cp, int currentLevel) throws WEditException
    {
        Log.l.info ( "DOC: Start." );
        try
        {
            document.close();
        } catch ( Exception e )       {
            Log.l.error ( "error", e );
            throw new WEditException ( e, "Ошибка завершения конвертации: ", e.getMessage() );
        }
    }

    /*
    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        TreeObj[]           selectNodes;
        BookContent         bookContent;
        StringBuilder       sb, warnBuffer;
        ConvertDialog       dialog;

        sb          = new StringBuilder ( 128 );
        warnBuffer  = new StringBuilder();         // сообщения о попаданиях текста.

        // Взять выделенный элемент - но только верхнего уровня.
        // Там же, в глубине, проверяется отмеченность одноуровневых элементов. Без проверки на корень.
        selectNodes = BookTools.getSelectedNodesForCut ( true );

        try
        {
            // Взять описание книги
            // - Взять текущую книгу
            bookContent     = Par.GM.getFrame().getCurrentBookContentPanel().getObject();
            bookStructure   = bookContent.getBookStructure();

            // todo Получить структуру книги
            bookStructure   = bookContent.getBookStructure();

            // todo Мержим структуру книги с имеющимися данными (в закладках) - вдруг расхождения. Если ДА - отмечаем, данные сбрасываем в дефолт.

            // todo Создаем и открываем диалог
            //convertParameter    = null;
            dialog = new ConvertDialog ( Par.GM.getFrame(), getName(), bookmarksParameter, bookStructure.getBookElements(), bookStructure.getTypes() );
            dialog.init ( bookmarksParameter );
            dialog.showDialog();

            if ( dialog.isOK() )
            {
                // todo Если ОК - валидируем данные (файл, его наличие, запрос на перезапись, и т.д.)

                if ( dialog.checkFile() )
                {
                    Log.file.debug ( "--- convert '%s' to file.", bookContent.getName()  );

                    // todo Конвертим книгу согласно имеющимся данным
                //    warnBuffer = saveHtml ( dialog.getCurrentBookmark() );

                    // Сообщение о завершении работы.
                    sb.append ( "Книга : " );
                    sb.append ( bookContent.getName() );
                    sb.append ( "\nуспешно преобразована.\n" );
                    sb.append ( "\n\n" );

                    // Скинуть рекурсивно
                    for ( TreeObj treeObj : selectNodes )
                    {
                        //node2rtf ( treeObj, document, 0 );
                        sb.append ( "-- " );
                        sb.append ( treeObj.getName() );
                    }
                    if ( warnBuffer.length() > 0 )
                    {
                        sb.append ( "\nВнимание:\n\n" );
                        sb.append ( warnBuffer );
                    }

                    DialogTools.showMessage ( "Преобразование", sb.toString() );
                }

            }

            // в любом случае сохраняем параметр, т.к. могло быть толкьо редактирование настроек, без итоговой конвертации книги.
            bookContent.setEdit ( true );  // иначе новое значение параметра не сохранится в файле книги.

        //} catch ( FileNotFoundException fe )        {
        //    Log.file.error ( fe, "err" );
        //    throw new WEditException ( fe, "Файл не найден." );
        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка преобразования в RTF : ", e );
        }
    }
    */

    @Override
    public void rewrite ()
    {
        // не исп
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

        header = new HeaderFooter ( null, null );
        header.setAlignment ( Element.ALIGN_CENTER );

        return header;
    }

    private Paragraph createTitleParagraph ( BookNode bookNode, ConvertParameter cp, String title )
    {
        Paragraph    result;
        int          align;
        AttributeSet style;
        Font         titleFont;   // RTF font

        // Взять стиль Заголовка
        style       = BookStructureTools.getElementStyle ( getBookContent().getBookStructure(), bookNode );
        titleFont   = GuiTools.createRtfFont ( style );

        // в каком виде выводится заголовок - уже проделано в  AbstractConvertFunction.convertNode  -- результат: title
        result   = new Paragraph ( leading, title, titleFont );
        result.add ( "\n" );

        // align
        align   = StyleConstants.getAlignment ( style );
        result.setAlignment ( align );

        return result;
    }

}
