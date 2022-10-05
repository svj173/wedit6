package svj.wedit.v6.function.book.export;


import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.book.export.obj.ConvertParameter;
import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.book.TextObject;
import svj.wedit.v6.obj.function.AbstractConvertFunction;
import svj.wedit.v6.tools.FileTools;
import svj.wedit.v6.tools.StringTools;

import javax.swing.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Конвертер в формат DOC по условиям для публикации на сайте Литрес.
 * <BR/> DOC, DOCX - библиотека сама понимает тип, исходя из расширения результирующего файла.
 * <BR/>
 * <BR/> Правила (https://selfpub.ru/faq/first-step/):
 * <BR/> 1) Нет названия книги (и автора).
 * <BR/> 2) Все заголовки оформляются как Заголовок-1 и т.д. до 4 уровня включительно.
 * <BR/> 3) Заголовок 'три звездочки' - как Заголовок низшего уровня - тогда будет расположен по середине и прочее оформление.
 * <BR/> 4) Заголовки - нет крайних пробелов (справа и слева).
 * <BR/> 5) Текс - стиль - Обычный.
 * <BR/> 6) Абзац (красная стркоа) оформляется форматированием как Отступ. (1.25 см)
 * <BR/> 7) Нет - две пустые строки подряд.
 * <BR/> 8) Заглавная картинка - вставить в начало книги. Если иллюстраций несколько - их все друг за другом, разделяя пустым абзацем.
 * <BR/> 9) Возле заголовков - спереди и сзади, не должно быть пустых строк.
 * <BR/> 10) Абзац - заканчивается символом конца абзаца (его код?), а не переводом строки. -- У меня все правильно.
 * <BR/> 11) Выделение текста - курсивом или жирным.
 * <BR/> 12) Нет
 * <BR/> - Автора и названия книги
 * <BR/> - Содержания
 * <BR/> - Колонтитулы
 * <BR/> - Нумерации страниц
 * <BR/> - Разрыва страниц
 * <BR/> - Аннотации
 * <BR/> - Изображение с гиперссылками
 * <BR/> - Спецсимволы - (рубль, франк, хитрые кавычки...)
 * <BR/> - Смайлы
 * <BR/>
 * <BR/>  Учет пустых строк.
 * <BR/> todo Пользуемся функциоей isPreviosIsTitle, а также счетчиком пустых строк, чтобы не не было больше одной.
 * <BR/>
 * <BR/> https://www.tutorialspoint.com/apache_poi_word/apache_poi_word_quick_guide.htm
 * <BR/>
 * <BR/>
 * <BR/> Проблемы.
 * <BR/> Первая строка - (продолжение следует), после чего идет сплошнйо мусор.
 * <BR/>
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 09.04.2020 16:02
 */
public class ConvertToDocForLitres extends AbstractConvertFunction
{
    private static final String  TITLE_PREFIX = "Заголовок ";
    //private static final String  TITLE_PREFIX = "heading ";


    private XWPFDocument        doc;

    /**
     * Уст в TRUE - был выведен титл (в т.ч. и пустой титл).
     * В FALSE - был выведен простой текст (не пустая строка).
     */
    private boolean isTitle = true;

    /** Вывели пустую строку. Устанавливается в TRUE когда реально выведена упстая строка.
     * FALSE -  когда выведена не пустая, либо заголовок. */
    private boolean wasEmpty = false;

    /** Надо будет вывести пустую строку. Уст в TRUE - появилась пустая строка текста.
     * FALSE - вывели любой текст, в т.ч. и пустую строку. */
    private boolean needEmpty = true;

    //private XWPFParagraph       currentParagraph;

    //private String FILE_TYPE_PARAM = "fileType";
    //private final ComboBoxParameter   fileType;


    public ConvertToDocForLitres ()
    {
        super ( FunctionId.CONVERT_TO_DOC_LITRES, "Преобразовать книгу в DOC (Литрес)", "to_doc.png", false );

        /*
        fileType = new ComboBoxParameter ( FILE_TYPE_PARAM );
        fileType.setHasEmpty ( false );
        fileType.addListValue ( "DOC" );
        fileType.addListValue ( "DOCX" );
        fileType.setValue ( "DOC" );
        */
    }

    @Override
    public void rewrite ()
    {
    }

    /**
     * Какие-то другие (дополнительные-индивидуальные) параметры конвертации. Например:
     * <br/> 1) Выбор формата - DOC, DOCX
     * <br/>
     * <br/>
     * @return  Список дополнительных параметров, либо Null.
     */
    @Override
    protected Collection<FunctionParameter> getOtherConvertParams ()
    {
        /*
        Collection<FunctionParameter> result;

        result = new ArrayList<FunctionParameter> ();
        result.add ( fileType );

        return result;
        */
        return null;
    }

    /**
     * Обработка изображения.
     * @param imgFileName    Имя файла с изображением.
     * @param cp             Параметр (для чего он?)
     */
    @Override
    protected void processImage ( String imgFileName, ConvertParameter cp )
    {
        int             format;
        XWPFParagraph   p;
        XWPFRun         r;
        ImageIcon       image;
        InputStream     ios;
        int             emuWidth, emuHeight;
        String fullImgFileName = null;

        p   = doc.createParagraph();
        r   = p.createRun();
        //Log.l.info ( "[LR] processImage. isTitle = %b", isTitle );

        // Определить формат картинки
        if ( imgFileName.endsWith ( ".jpg" ) )
            format = XWPFDocument.PICTURE_TYPE_JPEG;
        else if ( imgFileName.endsWith ( ".jpeg" ) )
            format = XWPFDocument.PICTURE_TYPE_JPEG;
        else if ( imgFileName.endsWith ( ".png" ) )
            format = XWPFDocument.PICTURE_TYPE_PNG;
        else
            format = XWPFDocument.PICTURE_TYPE_JPEG;

        //Log.l.info ( "[IMAGE] format = %s; imgFileName = %s", format, imgFileName );

        // заносим в абзац имя файла
        r.setText ( imgFileName );
        // перевод строки
        r.addBreak();
        // заносим в абзац саму картинку
        try
        {
            // сформировать полный путь до маленького файла
            fullImgFileName = FileTools.createSmallImageFileName(bookContent, imgFileName);

            // узнать размеры
            image       = FileTools.createImageFromFileName ( fullImgFileName, "Стела." );
            //Log.l.info ( "[IMAGE] image = %s", image );
            ios         = new FileInputStream (fullImgFileName);
            emuWidth    = Units.toEMU (image.getIconWidth());         // 200x200 pixels
            emuHeight   = Units.toEMU ( image.getIconHeight() );
            //Log.l.info ( "[IMAGE] emuWidth = %d; emuHeight = %d", emuWidth, emuHeight );
            r.addPicture ( ios, format, imgFileName, emuWidth, emuHeight );

        } catch ( Exception e )        {
            Log.l.error ( "Set image error. fullImgFileName = "+fullImgFileName, e );
            p   = doc.createParagraph();
            r   = p.createRun();
            r.setText ( "Error for Image file '"+imgFileName+"' : "+e.getMessage() );
//            r.addBreak();
        }

        //r.addBreak ( BreakType.PAGE );   // // перевод страницы
//        r.addBreak ();
        //Log.l.info ( "[IMAGE] Finish. imgFileName = %s", imgFileName );
    }

    @Override
    protected void processEmptyTitle ( ConvertParameter cp )
    {
        //Log.l.info ( "[LR] processEmptyTitle. isTitle = %b", isTitle );
        if ( ! isTitle )
        {
            createEmptyLine ();
        }
        isTitle = true;
    }

    private void createEmptyLine ()
    {
        XWPFParagraph   p;
        XWPFRun         r;

        //Log.l.info ( "[LR] createEmptyLine. isTitle = %b", isTitle );

        p   = doc.createParagraph();
        r   = p.createRun();
//        r.addBreak();

        wasEmpty = true;
    }

    protected String getNewLineSymbol ()
    {
        return WCons.SP;
    }


    /**
     *
     * @param title
     * @param level   0 - это уровень названия Книги
     * @param cp
     * @param bookNode
     */
    @Override
    protected void processTitle ( String title, int level, ConvertParameter cp, BookNode bookNode )
    {
        XWPFParagraph   result;
        String styleName;

        //Log.l.info ( "DOC_LITRES: title = %s", title );

        wasEmpty = false;

        if (title != null) {
            //if ( title.contains ( WCons.NEW_LINE ) )  title = title.replace ( "\n", "" );

            title = title.trim();
            if (title.endsWith ( "\n" ) ) {
                title = title.substring ( 0, title.length()-2 );
                title = title.trim();
            }
        }

        if ( StringTools.isEmpty ( title ) && isTitle ) {
            // титл пустой. Если уже был перед этим пустой титл - этот игнорировать.
            return;
        }

        isTitle = true;

        // Взять стиль Заголовка    -- "Heading1" -- CTStyle ctStyle = CTStyle.Factory.newInstance();
        // - в файле Windows имя стиля прописано как 'heading 1'
        styleName = TITLE_PREFIX+ level;  // книга = 0, А название книги не отмечаем как заголовок.

        /*
        // Применяется если предаврительно закачали стили из реального windows.docx файла.
        if ( doc.getStyles().getStyleWithName(styleName) == null) {
            throw new RuntimeException("Отсутствует стиль '" + styleName + "'.");
        }
        */

        result = doc.createParagraph();
        result.setStyle(styleName);

        // Красная строка - для заголовка не нужна
        //result.setFirstLineIndent ( (int) StyleConstants.getFirstLineIndent ( style ) );


        // Создать сам заголовок.
        XWPFRun chunk   = result.createRun();
        chunk.setText(title);
        //Log.l.info ( "[LR] processTitle. title = %s; isTitle = %b", title, isTitle );

    }

    @Override
    protected void processText ( TextObject textObj, ConvertParameter cp )
    {
        boolean emptyStr;
        ParagraphAlignment pAlignment;
        String text;
        XWPFParagraph       currentParagraph;

        //Log.l.info ( "DOC: processText = %s", textObj );

        // убрать символ переноса строки.
        text    = textObj.getText();
        if ( text.contains ( WCons.NEW_LINE ) )
        {
            text = text.replace ( "\n", "" );
        }

        emptyStr = StringTools.isEmpty ( text );

        //Log.l.info ( "[LR] processText-1. text = %s; isTitle = %b; needEmpty = %b; wasEmpty = %b",
        //             text, isTitle, needEmpty, wasEmpty );

        // Если это пустая строка и взведен флаг isTitle - ничего не выводить.
        if ( emptyStr )
        {
            if ( isTitle )
            {
                return;
            }
            else  if ( needEmpty )
            {
                return;
            }
            else  if ( wasEmpty )
            {
                // Две подряд пустых строки выводить нельзя.
                return;
            }
            else
            {
                // Если же после текста будет заголовок - то вообще ни одной пустой нельзя.
                // - запоминаем что надо было вывести пустую строку
                needEmpty = true;
                return;
            }
        }
        else
        {
            // Не пустая строка
            // - Если был флаг что надо было вывести пустую стркоу - выводим ее
            // - если конечно перед эти не было титла
            if (needEmpty &&(!isTitle)) {
                // выводим пустую
                createEmptyLine();
            }

            wasEmpty = false;
            isTitle = false;
            needEmpty = false;

            // Создать новый параграф
            currentParagraph = doc.createParagraph ();
            // Красная строка
            currentParagraph.setStyle ( "Основной текст" );    // в пикселях?
            currentParagraph.setFirstLineIndent ( 200 );    // в пикселях?

            //pAlignment = createParagraphAlignment ( style );
            pAlignment = ParagraphAlignment.BOTH;
            currentParagraph.setAlignment ( pAlignment );

            XWPFRun chunk = currentParagraph.createRun ();
            chunk.setText ( text );
            //Log.l.info ( "[LR] processText. text = %s; isTitle = %b", text, isTitle );
        }
    }

    /**
     public static final int ALIGN_LEFT = 0;
     public static final int ALIGN_CENTER = 1;
     public static final int ALIGN_RIGHT = 2;
     public static final int ALIGN_JUSTIFIED = 3;
     *
     * @ param style
     * @return
     */
    /*
    private ParagraphAlignment createParagraphAlignment ( AttributeSet style )
    {
        ParagraphAlignment result;

        switch ( StyleConstants.getAlignment ( style ) )
        {
            default:
            case StyleConstants.ALIGN_LEFT : // 0
                result = ParagraphAlignment.LEFT;
                break;

            case StyleConstants.ALIGN_CENTER : // 1
                result = ParagraphAlignment.CENTER;
                break;

            case StyleConstants.ALIGN_RIGHT : // 2
                result = ParagraphAlignment.RIGHT;
                break;

            case StyleConstants.ALIGN_JUSTIFIED : // 3
                result = ParagraphAlignment.BOTH;   // ???
                break;
        }
        return result;
    }
*/
    @Override
    protected void initConvert ( ConvertParameter cp )  throws WEditException
    {
        //Log.l.info ( "DOC: Start" );

        try
        {
            // error: ClassNotFoundException: org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
            doc = new XWPFDocument ();

            isTitle = true;
            needEmpty = true;
            wasEmpty = false;

            //Log.l.info ( "DOC: Create DOC = %s", doc );

            /*
            String titleName;
            XWPFStyle st;
            XWPFStyles styles;


            // Попытка закачать реальный windows.docx и взять его стили
            // -- Не помогло для litres-конвертера - не берет мой docx.
            String windowsFile;
            //windowsFile = "/home/svj/Serg/Stories/release_books/Litres/gn_okean_w.docx";
            windowsFile = "/home/svj/Serg/Stories_OLD/release_books/Litres/sudak_xxx_win7.docx";

            XWPFDocument windowsDoc = new XWPFDocument(new FileInputStream(windowsFile) );
            styles = windowsDoc.createStyles();
            titleName = TITLE_PREFIX + "1";
            st = styles.getStyleWithName(titleName);
            Log.l.info ( "DOC: Title style (%s) = %s", titleName, st );      // null
            if (st == null) {
                // Заголовок 1 -- ????
                throw new WEditException( "В эталонном файле стилей Windows заголовков\n ("
                        +windowsFile + ")\n отсутствует стиль '" + titleName + "'."  );
            }
            // Занести Windows стили заголовков в наш документ.
            XWPFStyles newStyles = doc.createStyles();
            newStyles.setStyles(windowsDoc.getStyle());

            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyles ctStyles = windowsDoc.getStyle();
            for ( org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle stl : ctStyles.getStyleList() ) {
                Log.l.info ( "-- DOCX: Title style name = %s", stl.getName() );
            }
            */

        } catch ( Throwable e )       {
            Log.l.error ( "error. cp = "+cp, e );
            throw new WEditException ( e, "Ошибка инициализации : \n", e.getMessage() );
        }
    }

    @Override
    protected void finishConvert(ConvertParameter cp, int currentLevel) throws WEditException
    {
        //Log.l.info ( "DOC: Start." );
        try
        {
            doc.write ( getFos() );
            getFos().flush();
            getFos().close();
        } catch ( Exception e )       {
            Log.l.error ( "error", e );
            throw new WEditException ( e, "Ошибка завершения : ", e.getMessage() );
        }
    }

}
