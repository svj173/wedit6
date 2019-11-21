package svj.wedit.v6.function.book.export;


import org.apache.poi.util.Units;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.book.export.obj.ConvertParameter;
import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.book.TextObject;
import svj.wedit.v6.obj.book.WEditStyle;
import svj.wedit.v6.obj.function.AbstractConvertFunction;
import svj.wedit.v6.tools.BookStructureTools;
import svj.wedit.v6.tools.FileTools;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Collection;

/**
 * Конвертер в формат DOC.
 * <BR/> DOC, DOCX - библиотека сама понимает тип, исходя из расширения результирующего файла.
 * <BR/> - Т.е дополнительный атрибут с типом файла не нужен.
 * <BR/>
 * <BR/> Доп параметры (Локальные):
 * <BR/> 1) Выбор формата - DOC, DOCX   -- здесь выбор происходит по расширению результирующего файла.
 * <BR/>
 * <BR/> Внимание: Не реализован цветной текст в середине абзаца! -- Сделано
 * <BR/>
 * <BR/> Page size. Стандартное:
 * <BR/> - А4 - ширина 21, высота - 29.7 (см)
 * <BR/> - справа, слева, сверху, снизу - 2 см.
 * <BR/>
 * <BR/> https://www.tutorialspoint.com/apache_poi_word/apache_poi_word_quick_guide.htm
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.03.16 17:02
 */
public class ConvertToDoc  extends AbstractConvertFunction
{
    //private String FILE_TYPE_PARAM = "fileType";

    private XWPFDocument        doc;
    //private XWPFParagraph       currentParagraph;

    //private final ComboBoxParameter   fileType;


    public ConvertToDoc ()
    {
        super ( FunctionId.CONVERT_TO_DOC, "Преобразовать книгу в DOC", "to_doc.png", false );

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

        p   = doc.createParagraph();
        r   = p.createRun();

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
            // узнать размеры
            image       = FileTools.createImageFromFileName ( imgFileName, "Стела." );
            //Log.l.info ( "[IMAGE] image = %s", image );
            ios         = new FileInputStream (imgFileName);
            emuWidth    = Units.toEMU (image.getIconWidth());         // 200x200 pixels
            emuHeight   = Units.toEMU ( image.getIconHeight() );
            //Log.l.info ( "[IMAGE] emuWidth = %d; emuHeight = %d", emuWidth, emuHeight );
            r.addPicture ( ios, format, imgFileName, emuWidth, emuHeight );

        } catch ( Exception e )        {
            Log.l.error ( "Set image error. imgFileName = "+imgFileName, e );
            p   = doc.createParagraph();
            r   = p.createRun();
            r.setText ( "Error for Image file '"+imgFileName+"' : "+e.getMessage() );
            r.addBreak();
        }

        //r.addBreak ( BreakType.PAGE );   // // перевод страницы
        r.addBreak ();
        //Log.l.info ( "[IMAGE] Finish. imgFileName = %s", imgFileName );
    }

    @Override
    protected void processEmptyTitle ( ConvertParameter cp )
    {
        XWPFParagraph   p;
        XWPFRun         r;

        p   = doc.createParagraph();
        r   = p.createRun();
        r.addBreak();
    }

    protected String getNewLineSymbol ()
    {
        processEmptyTitle ( null );
        return WCons.SP;
    }



    @Override
    protected void processTitle ( String title, int level, ConvertParameter cp, BookNode bookNode )
    {
        //Log.l.info ( "DOC: Start" );

        // Занести титл.

        // Занести заголовок согласно стиля.
        //currentParagraph = createParagraph ( bookNode, cp, title );
        createParagraph ( bookNode, cp, title );
    }

    @Override
    protected void processText ( TextObject textObj, ConvertParameter cp )
    {
        AttributeSet style;
        boolean sln;
        XWPFRun tempRun;
        int pos;
        ParagraphAlignment pAlignment;
        String text;
        XWPFParagraph       currentParagraph;

        //Log.l.info ( "DOC: processText = %s", textObj );

        sln = false;
        pos = -1;
        style = textObj.getStyle();
        if ( style == null )
        {
            // это Text - взять стиль для текста по-умолчанию   -- bookStructure
            style   = getBookContent().getBookStructure().getTextStyle();
            sln     = true;
        }

        // Временно - ставим размер шрифта для текста - 8.
        if ( style instanceof MutableAttributeSet )
            StyleConstants.setFontSize ( (MutableAttributeSet) style, 8 );

        // Если это Абзац  - создать абзац.
        //if ( ( currentParagraph == null ) || ( textObj instanceof SlnTextObject ) )
        //if ( currentParagraph == null )
        //{
            // Создать новый параграф
            currentParagraph = doc.createParagraph ();
            // Красная строка
            //currentParagraph.setSpacingBefore ( ( int ) StyleConstants.getFirstLineIndent ( style ) );
            //currentParagraph.setFirstLineIndent ( ( int ) StyleConstants.getFirstLineIndent ( style ) );
            currentParagraph.setFirstLineIndent ( 200 );    // в пикселях?

            pAlignment = createParagraphAlignment ( style );
            currentParagraph.setAlignment ( pAlignment );

            /*
            int topMargin, leftMargin, hangingIndent;
            topMargin  = 15;
            leftMargin = 15;
            hangingIndent = 25;
            //currentParagraph.setAlignment(alignment);
            // Setting the space before and after the paragraph
            currentParagraph.setSpacingAfter(10);
            currentParagraph.setSpacingBefore(topMargin);
            currentParagraph.setIndentationLeft(leftMargin);
            currentParagraph.setIndentationRight(50);
            currentParagraph.setIndentationHanging ( hangingIndent);
            */
       // }

        //if ( sln ) pos = 0;
        //tempRun = createChunk ( currentParagraph, textObj.getStyle(), textObj.getText(), pos );

        // убрать символ переноса строки.
        text    = textObj.getText ();
        if ( text.contains ( WCons.NEW_LINE ) )
        {
            text = text.replace ( "\n", "" );
            // создать параграф
            tempRun = createChunk ( currentParagraph, style, text );
        }
        else
        {
            // todo - Это текст в середине абзаца - ???
            tempRun = createChunk ( currentParagraph, style, text );
        }

        //if ( sln )    tempRun.addBreak();
    }

    /**
     public static final int ALIGN_LEFT = 0;
     public static final int ALIGN_CENTER = 1;
     public static final int ALIGN_RIGHT = 2;
     public static final int ALIGN_JUSTIFIED = 3;
     *
     * @param style
     * @return
     */
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

    @Override
    protected void initConvert ( ConvertParameter cp )  throws WEditException
    {
        XWPFParagraph paragraph;
        XWPFRun run;
        
        //Log.l.info ( "DOC: Start" );

        try
        {
            doc = new XWPFDocument ();

            //Log.l.info ( "DOC: Create DOC = %s", doc );

            // ------------------------- Нумерация страниц -------------------------

            XWPFHeaderFooterPolicy headerFooterPolicy = doc.getHeaderFooterPolicy();
            if (headerFooterPolicy == null) headerFooterPolicy = doc.createHeaderFooterPolicy();
            // -------- Верхний колонтитул . ----
            // create header start
            XWPFHeader header = headerFooterPolicy.createHeader( XWPFHeaderFooterPolicy.DEFAULT);

            paragraph = header.getParagraphArray(0);
            if (paragraph == null) paragraph = header.createParagraph();
            paragraph.setAlignment ( ParagraphAlignment.CENTER );

            //run = paragraph.createRun();
            //run.setText(" - ");
            // текущий номер страницы
            paragraph.getCTP().addNewFldSimple().setInstr("PAGE \\* MERGEFORMAT");
            run = paragraph.createRun();
            run.setText ( " / " );
            // всего страниц
            paragraph.getCTP().addNewFldSimple().setInstr("NUMPAGES \\* MERGEFORMAT");

            // ------------------------- Формат и Размер страницы ----------------
            CTDocument1 document = doc.getDocument();
            CTBody body = document.getBody();
            if ( ! body.isSetSectPr() )         body.addNewSectPr();
            CTSectPr section = body.getSectPr();
            if ( ! section.isSetPgSz() )        section.addNewPgSz();
            // Почемуто этих классов нет в Jar 3.17 - Но он есть в ooxml-schemas-1.1.jar
            //org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz pageSize = section.getPgSz();
            CTPageSz pageSize = section.getPgSz();
            //pageSize.setOrient ( STPageOrientation.LANDSCAPE );
            // Размер всей страницы. А4 = 11900 х 16900
            //pageSize.setH ( BigInteger.valueOf( 16900 ) );
            //pageSize.setW ( BigInteger.valueOf( 11900 ) );

            //Object obj = (Object) section.getPgSz();

            // ------------------- Край страницы ---------------------

            if ( ! body.isSetSectPr () )    body.addNewSectPr();
            CTSectPr sectPr = body.getSectPr();
            CTPageMar pageMar = sectPr.addNewPgMar();
            // 720L - 2 см. 1440L - 4 см.
            pageMar.setLeft( BigInteger.valueOf( 720L) );
            pageMar.setTop (BigInteger.valueOf(1440L) );
            pageMar.setRight ( BigInteger.valueOf(720L) );
            pageMar.setBottom ( BigInteger.valueOf(1440L) );

        } catch ( Throwable e )       {
            Log.l.error ( "error. cp = "+cp, e );
            throw new WEditException ( e, "Ошибка инициализации : \n", e.getMessage() );
        }
    }

    @Override
    protected void finishConvert ( ConvertParameter cp ) throws WEditException
    {
        //Log.l.info ( "DOC: Start." );
        try
        {
            doc.write ( getFos() );
            getFos().close();
        } catch ( Exception e )       {
            Log.l.error ( "error", e );
            throw new WEditException ( e, "Ошибка завершения : ", e.getMessage() );
        }
    }

    private XWPFParagraph createParagraph ( BookNode bookNode, ConvertParameter cp, String title )
    {
        XWPFParagraph   result;
        XWPFRun chank;
        String str, type, name;
        int align, i;
        WEditStyle style;

        result = doc.createParagraph ();

        // Взять стиль Заголовка
        style       = BookStructureTools.getElementStyle ( getBookContent().getBookStructure(), bookNode );

        // Красная строка
        result.setFirstLineIndent ( ( int ) StyleConstants.getFirstLineIndent ( style ) );

        // Взять сам заголовок. Вставляем переводы строк - иначе заголовок будет прижат к предыдущему тексту.
        //name    = bookNode.getName ();
        chank   = createChunk ( result, style, "\n\n" + title+"\n" );

        // аннотация
        if ( cp.isPrintAnnotation() )
        {
            str = bookNode.getAnnotation();
            if ( str != null )
            {
                //chunk   = new Chunk ( str, attributeFont );
                //result.add ( chunk );
                //result.add ( "\n" );
                result = doc.createParagraph ();
                // Красная строка для аннотации
                result.setFirstLineIndent ( ( int ) StyleConstants.getFirstLineIndent ( attributeFont ) );

                chank = createChunk ( result, attributeFont, str + "\n\n" );
            }
        }

        // Вставить переводы строк после заголовка
        //ic      = nodeObject.getTitleVkSize ();
     //   chunk   = new Chunk ( "\n", textFont );
     //   for ( i=0; i<2; i++ )   result.add ( chunk );

        return result;
    }

    private XWPFRun createChunk ( XWPFParagraph paragraph, AttributeSet style, String text )
    {
        XWPFRun chunk;

        //Log.l.debug ( "createChunk Start. style = %s; text = %s", style, text );

        if ( style == null )
        {
            // это Text - взять стиль для текста по-умолчанию   -- bookStructure
            style = getBookContent().getBookStructure().getTextStyle();
        }

        chunk   = paragraph.createRun();
        //chunk.setColor ( StyleTools.color2hex ( StyleConstants.getForeground ( style ) ) );
        chunk.setColor ( "000000" );  // black only
        chunk.setFontFamily ( StyleConstants.getFontFamily ( style ) );
        //chank.setUnderline ();
        chunk.setItalic ( StyleConstants.isItalic ( style ) );
        chunk.setBold ( StyleConstants.isBold ( style ) );
        chunk.setFontSize ( StyleConstants.getFontSize ( style ) );
        //chunk.setStrike ( StyleConstants.isStrikeThrough ( style ) );
        //chank.setSubscript ();        enum VerticalAlign
        chunk.setText ( text );

        /*
        if ( pos < 0 )
            chunk.setText ( text );
        else
            chunk.setText ( text, pos );
        */
        //chunk.addBreak();   // конец строки ?

        //chunk.setTextPosition ( 100 );

        return chunk;
    }

    /*
    private String getFileTypeValue ( ConvertParameter cp )
    {
        String result = WCons.SP;
        FunctionParameter fp;
        fp = cp.getLocaleParam ( FILE_TYPE_PARAM );
        if ( fp != null )
        {   if ( fp.getValue() != null )
            result = fp.getValue().toString();
        }
        return result;
    }
    */

/*

public static void createSimpleTable() throws Exception {
    XWPFDocument doc = new XWPFDocument();

    XWPFTable table = doc.createTable(3, 3);

    table.getRow(1).getCell(1).setText("EXAMPLE OF TABLE");

    // table cells have a list of paragraphs; there is an initial
    // paragraph created when the cell is created. If you create a
    // paragraph in the document to put in the cell, it will also
    // appear in the document following the table, which is probably
    // not the desired result.
    XWPFParagraph p1 = table.getRow(0).getCell(0).getParagraphs().get(0);

    XWPFRun r1 = p1.createRun();
    r1.setBold(true);
    r1.setText("The quick brown fox");
    r1.setItalic(true);
    r1.setFontFamily("Courier");
    r1.setUnderline(UnderlinePatterns.DOT_DOT_DASH);
    r1.setTextPosition(100);

    table.getRow(2).getCell(2).setText("only text");

    FileOutputStream out = new FileOutputStream("simpleTable.docx");
    doc.write(out);
    out.close();
}

public static void main(String[] args) throws Exception
{
    // The path to the documents directory.
    String dataDir = Utils.getDataDir(ApacheInsertImage.class);

    XWPFDocument doc = new XWPFDocument();
    XWPFParagraph p = doc.createParagraph();

    String imgFile = dataDir + "aspose.jpg";
    XWPFRun r = p.createRun();

    int format = XWPFDocument.PICTURE_TYPE_JPEG;
    r.setText(imgFile);
    r.addBreak();
    r.addPicture(new FileInputStream(imgFile), format, imgFile, Units.toEMU(200), Units.toEMU(200)); // 200x200 pixels
    r.addBreak(BreakType.PAGE);

    FileOutputStream out = new FileOutputStream(dataDir + "Apache_ImagesInDoc.docx");
    doc.write(out);
    out.close();

    System.out.println("Process Completed Successfully");
}

public static void main(String[] args) throws Exception
{
    // The path to the documents directory.
    String dataDir = Utils.getDataDir(ApacheCreateTable.class);

    XWPFDocument document = new XWPFDocument();

    // New 2x2 table
    XWPFTable tableOne = document.createTable();
    XWPFTableRow tableOneRowOne = tableOne.getRow(0);
    tableOneRowOne.getCell(0).setText("Hello");
    tableOneRowOne.addNewTableCell().setText("World");

    XWPFTableRow tableOneRowTwo = tableOne.createRow();
    tableOneRowTwo.getCell(0).setText("This is");
    tableOneRowTwo.getCell(1).setText("a table");

    // Add a break between the tables
    document.createParagraph().createRun().addBreak();

    // New 3x3 table
    XWPFTable tableTwo = document.createTable();
    XWPFTableRow tableTwoRowOne = tableTwo.getRow(0);
    tableTwoRowOne.getCell(0).setText("col one, row one");
    tableTwoRowOne.addNewTableCell().setText("col two, row one");
    tableTwoRowOne.addNewTableCell().setText("col three, row one");

    XWPFTableRow tableTwoRowTwo = tableTwo.createRow();
    tableTwoRowTwo.getCell(0).setText("col one, row two");
    tableTwoRowTwo.getCell(1).setText("col two, row two");
    tableTwoRowTwo.getCell(2).setText("col three, row two");

    XWPFTableRow tableTwoRowThree = tableTwo.createRow();
    tableTwoRowThree.getCell(0).setText("col one, row three");
    tableTwoRowThree.getCell(1).setText("col two, row three");
    tableTwoRowThree.getCell(2).setText("col three, row three");

    FileOutputStream outStream = new FileOutputStream(dataDir + "Apache_CreateTable.doc");
    document.write(outStream);
    outStream.close();
}


XWPFHeaderFooterPolicy.getFirstPageHeader(): Provides the header of first page.
XWPFHeaderFooterPolicy.getDefaultHeader(): Provides the default header of DOCX file given to each and every page.
XWPFHeaderFooterPolicy.getFirstPageFooter(): Provides the footer of first page.
XWPFHeaderFooterPolicy.getDefaultFooter(): Provides the default footer of DOCX file given to each and every page.

Find the sample demo for getDefaultHeader and getDefaultFooter.
ReadDOCXHeaderFooter.java

package com.concretepage;
import java.io.FileInputStream;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
public class ReadDOCXHeaderFooter {
   public static void main(String[] args) {
     try {
	 FileInputStream fis = new FileInputStream("D:/docx/read-test.docx");
	 XWPFDocument xdoc=new XWPFDocument(OPCPackage.open(fis));
	 XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(xdoc);
	 //read header
	 XWPFHeader header = policy.getDefaultHeader();
	 System.out.println(header.getText());
	 //read footer
	 XWPFFooter footer = policy.getDefaultFooter();
	 System.out.println(footer.getText());
     } catch(Exception ex) {
	ex.printStackTrace();
     }
  }
}

        -- нумерация страниц

import java.io.*;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;

public class CreateWordHeaderFooter
{
 public static void main(String[] args) throws Exception {

  XWPFDocument doc= new XWPFDocument();

  // the body content
  XWPFParagraph paragraph = doc.createParagraph();
  XWPFRun run=paragraph.createRun();
  run.setText("The Body:");

  paragraph = doc.createParagraph();
  run=paragraph.createRun();
  run.setText("Lorem ipsum.... page 1");

  paragraph = doc.createParagraph();
  run=paragraph.createRun();
  run.addBreak(BreakType.PAGE);
  run.setText("Lorem ipsum.... page 2");

  paragraph = doc.createParagraph();
  run=paragraph.createRun();
  run.addBreak(BreakType.PAGE);
  run.setText("Lorem ipsum.... page 3");

  // create header-footer
  XWPFHeaderFooterPolicy headerFooterPolicy = doc.getHeaderFooterPolicy();
  if (headerFooterPolicy == null) headerFooterPolicy = doc.createHeaderFooterPolicy();

  // create header start
  XWPFHeader header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);

  paragraph = header.getParagraphArray(0);
  if (paragraph == null) paragraph = header.createParagraph();
  paragraph.setAlignment(ParagraphAlignment.LEFT);

  run = paragraph.createRun();
  run.setText("The Header:");

  // create footer start
  XWPFFooter footer = headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);

  paragraph = footer.getParagraphArray(0);
  if (paragraph == null) paragraph = footer.createParagraph();
  paragraph.setAlignment(ParagraphAlignment.CENTER);

  // Внизу пишем : Page 3 of 3
  run = paragraph.createRun();
  run.setText("Page ");
  paragraph.getCTP().addNewFldSimple().setInstr("PAGE \\* MERGEFORMAT");     // ??? что это?
  run = paragraph.createRun();
  run.setText(" of ");
  paragraph.getCTP().addNewFldSimple().setInstr("NUMPAGES \\* MERGEFORMAT");

  doc.write(new FileOutputStream("CreateWordHeaderFooter.docx"));

 }

}


-- page number

        XWPFDocument document = new XWPFDocument();

        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
        XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(document, sectPr);

        //write header content
        CTP ctpHeader = CTP.Factory.newInstance();
        CTR ctrHeader = ctpHeader.addNewR();
        CTText ctHeader = ctrHeader.addNewT();
        String headerText = "FISHER SCIENTIFIC COMPANY L.L.C. DISTRIBUTION AGREEMENT";
        ctHeader.setStringValue(headerText);

        //write page number header
        CTP ctpHeaderPage = CTP.Factory.newInstance();
        CTPPr ctppr = ctpHeaderPage.addNewPPr();
        CTString pst = ctppr.addNewPStyle();
        pst.setVal("style21");
        CTJc ctjc = ctppr.addNewJc();
        ctjc.setVal(STJc.RIGHT);
        ctppr.addNewRPr();
        CTR ctr = ctpHeaderPage.addNewR();
        ctr.addNewRPr();
        CTFldChar fch = ctr.addNewFldChar();
        fch.setFldCharType(STFldCharType.BEGIN);

        ctr = ctpHeaderPage.addNewR();
        ctr.addNewInstrText().setStringValue(" PAGE ");

        ctpHeaderPage.addNewR().addNewFldChar().setFldCharType(STFldCharType.SEPARATE);

        ctpHeaderPage.addNewR().addNewT().setStringValue("1");

        ctpHeaderPage.addNewR().addNewFldChar().setFldCharType(STFldCharType.END);

        XWPFParagraph headerParagraph = new XWPFParagraph(ctpHeader, document);
        XWPFParagraph headerParagraphPage = new XWPFParagraph(ctpHeader, document);
        XWPFParagraph[] parsHeader = new XWPFParagraph[2];
        parsHeader[0] = headerParagraph;
        parsHeader[1] = headerParagraphPage;
        policy.createHeader(XWPFHeaderFooterPolicy.DEFAULT, parsHeader);


-- Set Page orientation

XWPFDocument doc = new XWPFDocument();

CTDocument1 document = doc.getDocument();
CTBody body = document.getBody();

if (!body.isSetSectPr()) {
     body.addNewSectPr();
}
CTSectPr section = body.getSectPr();

if(!section.isSetPgSz()) {
    section.addNewPgSz();
}
CTPageSz pageSize = section.getPgSz();

pageSize.setOrient(STPageOrientation.LANDSCAPE);


-- Set Page size

import org.apache.poi.openxml4j.opc.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

public class DocxPageLayout {
    public static void main(String[] args) throws Exception {
        OPCPackage opc = OPCPackage.open("example.docx", PackageAccess.READ);
        XWPFDocument doc = new XWPFDocument(opc);
        opc.close();

        CTSectPr sectPr = doc.getDocument().getBody().getSectPr();
        if (sectPr == null) return;
        CTPageSz pageSize = sectPr.getPgSz();
        if (pageSize == null) return;

        double width_cm = Math.round(pageSize.getW().doubleValue()/20d/72d*2.54d*100d)/100d;
        double height_cm = Math.round(pageSize.getH().doubleValue()/20d/72d*2.54d*100d)/100d;

        System.out.println("width: "+width_cm+" cm; height: "+height_cm+" cm");
    }
}


-- Style

public void testAddStylesToDocument() throws IOException {
    XWPFDocument docOut = new XWPFDocument();
    XWPFStyles styles = docOut.createStyles();

    String strStyleId = "headline1";
    CTStyle ctStyle = CTStyle.Factory.newInstance();

    ctStyle.setStyleId(strStyleId);
    XWPFStyle s = new XWPFStyle(ctStyle);
    styles.addStyle(s);

    assertTrue(styles.styleExist(strStyleId));

    XWPFDocument docIn = XWPFTestDataSamples
            .writeOutAndReadBack(docOut);

    styles = docIn.getStyles();
    assertTrue(styles.styleExist(strStyleId));
}

-- Рамка вокруг абзаца

public class ApplyingBorder {

   public static void main(String[] args)throws Exception {

      //Blank Document
      XWPFDocument document = new XWPFDocument();

      //Write the Document in file system
      FileOutputStream out = new FileOutputStream(new File("applyingborder.docx"));

      //create paragraph
      XWPFParagraph paragraph = document.createParagraph();

      //Set bottom border to paragraph
      paragraph.setBorderBottom(Borders.BASIC_BLACK_DASHES);

      //Set left border to paragraph
      paragraph.setBorderLeft(Borders.BASIC_BLACK_DASHES);

      //Set right border to paragraph
      paragraph.setBorderRight(Borders.BASIC_BLACK_DASHES);

      //Set top border to paragraph
      paragraph.setBorderTop(Borders.BASIC_BLACK_DASHES);

      XWPFRun run = paragraph.createRun();
         run.setText("At tutorialspoint.com, we strive hard to " +
         "provide quality tutorials for self-learning " +
         "purpose in the domains of Academics, Information " +
         "Technology, Management and Computer Programming " +
         "Languages.");

      document.write(out);
      out.close();
      System.out.println("applyingborder.docx written successully");
   }
}

Font Style

The following code is used to set different styles of font −

import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class FontStyle {

   public static void main(String[] args)throws Exception {

      //Blank Document
      XWPFDocument document = new XWPFDocument();

      //Write the Document in file system
      FileOutputStream out = new FileOutputStream(new File("fontstyle.docx"));

      //create paragraph
      XWPFParagraph paragraph = document.createParagraph();

      //Set Bold an Italic
      XWPFRun paragraphOneRunOne = paragraph.createRun();
      paragraphOneRunOne.setBold(true);
      paragraphOneRunOne.setItalic(true);
      paragraphOneRunOne.setText("Font Style");
      paragraphOneRunOne.addBreak();

      //Set text Position
      XWPFRun paragraphOneRunTwo = paragraph.createRun();
      paragraphOneRunTwo.setText("Font Style two");
      paragraphOneRunTwo.setTextPosition(100);

      //Set Strike through and Font Size and Subscript
      XWPFRun paragraphOneRunThree = paragraph.createRun();
      paragraphOneRunThree.setStrike(true);
      paragraphOneRunThree.setFontSize(20);
      paragraphOneRunThree.setSubscript(VerticalAlign.SUBSCRIPT);
      paragraphOneRunThree.setText(" Different Font Styles");

      document.write(out);
      out.close();
      System.out.println("fontstyle.docx written successully");
   }
}

-- Отступ дял абзаца
documentTitle.setSpacingBefore(100);    // It lefe me 100pt space between each line of the text


-- Край страницы
    CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
    CTPageMar pageMar = sectPr.addNewPgMar();
    pageMar.setLeft(BigInteger.valueOf(720L));
    pageMar.setTop(BigInteger.valueOf(1440L));
    pageMar.setRight(BigInteger.valueOf(720L));
    pageMar.setBottom(BigInteger.valueOf(1440L));



 */
}
