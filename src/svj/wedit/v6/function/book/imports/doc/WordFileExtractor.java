package svj.wedit.v6.function.book.imports.doc;



import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.HWPFOldDocument;
import org.apache.poi.hwpf.OldWordFileFormatException;
import org.apache.poi.hwpf.extractor.Word6Extractor;
import org.apache.poi.hwpf.model.FieldsDocumentPart;
import org.apache.poi.hwpf.model.PicturesTable;
import org.apache.poi.hwpf.model.StyleDescription;
import org.apache.poi.hwpf.usermodel.*;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.xml.sax.helpers.AttributesImpl;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.book.imports.IFileExtractor;
import svj.wedit.v6.function.book.imports.doc.target.IBookContentCreator;
import svj.wedit.v6.function.book.imports.doc.target.ShowTitleContentHandler;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.tools.DumpTools;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 15.12.14 13:56
 */
public class WordFileExtractor  implements IFileExtractor
{
    private static final char UNICODECHAR_NONBREAKING_HYPHEN    = '\u2011';
    private static final char UNICODECHAR_ZERO_WIDTH_SPACE      = '\u200b';

    // True if we are currently in the named style tag:
    private boolean curStrikeThrough;
    private boolean curBold;
    private boolean curItalic;


    public WordFileExtractor ()
    {
    }


    public static void main ( String[] args )
    {
        WordFileExtractor       handler;
        NPOIFSFileSystem        filesystem;
        IBookContentCreator xhtml;
        String                  fileName;

        //fileName    = "/home/svj/tmp/ch02.doc";
        fileName    = "/home/svj/Raznoe/Written/Obolon/ob09d.doc";
        //xhtml       = new XHTMLContentHandler();
        xhtml       = new ShowTitleContentHandler();
        handler     = new WordFileExtractor();

        try
        {
            filesystem  = new NPOIFSFileSystem ( new File (fileName));
            handler.parse ( filesystem, xhtml );

            System.out.println ( xhtml.getResult() );

        } catch ( Exception e )        {
            e.printStackTrace ();
        }
    }

    public void parse ( String fileName, IBookContentCreator xhtml )
            throws WEditException
    {
        NPOIFSFileSystem fileSystem;
        File file;

        try
        {
            file        = new File ( fileName );
            fileSystem  = new NPOIFSFileSystem ( file );
        } catch ( Exception e )        {
            Log.l.error ( "Create NPOIFSFileSystem error", e );
            throw new WEditException ( e, "Create NPOIFSFileSystem error", e );
        }

        parse ( fileSystem, xhtml);
    }

    public void parse ( File file, IBookContentCreator xhtml)
            throws WEditException
    {
        NPOIFSFileSystem fileSystem;

        try
        {
            fileSystem  = new NPOIFSFileSystem ( file );
        } catch ( Exception e )        {
            Log.l.error ( "Create NPOIFSFileSystem error", e );
            throw new WEditException ( e, "Create NPOIFSFileSystem error", e );
        }

        parse ( fileSystem, xhtml);
    }

    @Override
    public void processAdditional ( JComponent additionalGuiComponent )
    {
    }

    protected void parse ( NPOIFSFileSystem filesystem, IBookContentCreator xhtml )
            throws WEditException
    {
        parse ( filesystem.getRoot(), xhtml );
    }

    protected void parse ( DirectoryNode root, IBookContentCreator xhtml )
            throws WEditException
    {
        org.apache.poi.hwpf.extractor.WordExtractor wordExtractor;
        HWPFDocument    document;
        HeaderStories   headerFooter;
        PicturesTable   pictureTable;
        PicturesSource  pictures;
        Range[]         headers, footers;
        Range           r;
        Paragraph       pr;
        DirectoryEntry  op;
        int             ic;

        // ------------------ Создать документ ----------------------------
        try
        {
            document = new HWPFDocument (root);
        } catch ( OldWordFileFormatException e) {
            parseWord6 (root, xhtml);
            return;
        } catch ( Exception ie )        {
            Log.l.error ( "Create HWPFDocument error", ie );
            throw new WEditException ( ie, "Create HWPFDocument error", ie );
        }

        // ------------------ Создать извлекатель из документа ----------------------------
        wordExtractor   = new org.apache.poi.hwpf.extractor.WordExtractor(document);

        // ------------------ Создать документ ----------------------------
        headerFooter    = new HeaderStories (document);

        // Grab the list of pictures. As far as we can tell,
        //  the pictures should be in order, and may be directly
        //  placed or referenced from an anchor
        pictureTable    = document.getPicturesTable();
        pictures        = new PicturesSource(document);

        // Do any headers, if present
        headers = new Range[] { headerFooter.getFirstHeaderSubrange(),
                headerFooter.getEvenHeaderSubrange(), headerFooter.getOddHeaderSubrange() };
        handleHeaderFooter ( headers, "header", document, pictures, pictureTable, xhtml);

        // Do the main paragraph text
        ic = 0;
        r  = document.getRange();
        for(int i=0; i<r.numParagraphs(); i++)
        {
            pr = r.getParagraph(i);
            i += handleParagraph ( pr, 0, r, document, FieldsDocumentPart.MAIN, pictures, pictureTable, xhtml );
            ic = i;
        }
        Log.file.debug ( "WordExtractor. read count = %d", ic );

        // Do everything else
        for ( String paragraph: wordExtractor.getMainTextboxText() )
        {
            //xhtml.element ( "p", paragraph );
            xhtml.addMainTextboxText ( paragraph );
        }

        for ( String paragraph : wordExtractor.getFootnoteText() )
        {
            //xhtml.element ( "p", paragraph );
            xhtml.addFootnoteText ( paragraph );
        }

        for (String paragraph : wordExtractor.getCommentsText())
        {
            //xhtml.element ( "p", paragraph );
            xhtml.addCommentsText ( paragraph );
        }

        for (String paragraph : wordExtractor.getEndnoteText())
        {
            //xhtml.element ( "p", paragraph );
            xhtml.addEndnoteText ( paragraph );
        }

        // Do any footers, if present
        footers = new Range[] { headerFooter.getFirstFooterSubrange(),
                headerFooter.getEvenFooterSubrange(), headerFooter.getOddFooterSubrange() };
        handleHeaderFooter ( footers, "footer", document, pictures, pictureTable, xhtml);

        // Handle any pictures that we haven't output yet
        for ( Picture p = pictures.nextUnclaimed(); p != null; )
        {
            handlePictureCharacterRun ( null, p, pictures, xhtml );
            p = pictures.nextUnclaimed();
        }

        // Handle any embeded office documents
        try
        {
            op = (DirectoryEntry ) root.getEntry("ObjectPool");
            for ( Entry entry : op )
            {
                if ( entry.getName().startsWith("_") && entry instanceof DirectoryEntry )
                {
                    // todo
                    //handleEmbeddedOfficeDoc ( (DirectoryEntry) entry, xhtml );
                    System.out.println ( " -+ " + ( DirectoryEntry ) entry );
                }
            }
        } catch ( FileNotFoundException e ) {
            Log.l.error ( "Handle any embeded office documents error", e );
        }
    }

    private static int countParagraphs ( Range... ranges )
    {
        int count = 0;
        for (Range r : ranges)
        {
            if (r != null) { count += r.numParagraphs(); }
        }
        return count;
    }

    private void handleHeaderFooter ( Range[] ranges, String type, HWPFDocument document,
          PicturesSource pictures, PicturesTable pictureTable, IBookContentCreator xhtml)
          throws WEditException
    {
        if ( countParagraphs(ranges) > 0 )
        {
            Paragraph p;

            xhtml.startElement ( "div", "class", type );
            for (Range r : ranges)
            {
                if (r != null)
                {
                    for ( int i=0; i<r.numParagraphs(); i++)
                    {
                        p = r.getParagraph(i);

                        i += handleParagraph(p, 0, r, document, FieldsDocumentPart.HEADER, pictures, pictureTable, xhtml);
                     }
                }
            }
            xhtml.endElement("div");
        }
    }

    private int handleParagraph ( Paragraph p, int parentTableLevel, Range r, HWPFDocument document,
                                  FieldsDocumentPart docPart, PicturesSource pictures, PicturesTable pictureTable,
                                  IBookContentCreator xhtml )
            throws WEditException
    {
        int                 count;
        TagAndStyle         tas;
        String              text, id, str;
        StyleDescription    style;
        CharacterRun        cr;
        Field               field;
        Picture             picture;
        AttributesImpl      attributes;

        Log.l.debug ( "--- DOC import: Start. Paragraph. isBackward = %b; isWordWrapped = %b; ", p.isBackward(), p.isWordWrapped() );
        try
        {
            // Note - a poi bug means we can't currently properly recurse
            //  into nested tables, so currently we don't
            if ( p.isInTable () && ( p.getTableLevel () > parentTableLevel ) && ( parentTableLevel == 0 ) )
            {
                count = handleTable ( r.getTable ( p ), p, document, docPart, pictures, pictureTable, xhtml );
                return count;
            }

            if ( p.isInList() )
            {
                // todo List
                Log.l.debug ( "--- DOC import: it is List" );
            }

            // Проверка параграфа на пустоту
            text = p.text();
            Log.l.debug ( "--- DOC import: paragr text = %s", text );
            if ( text.replaceAll ( "[\\r\\n\\s]+", "" ).trim().isEmpty() )
            {
                // пустая строка
                //xhtml.element ( "br", "<!-- empty -->" );
                xhtml.emptyLine();
                return 0;
            }


            // собрать инфу о стиле - в обьекте TagAndStyle
            if ( document.getStyleSheet().numStyles() > p.getStyleIndex() )
            {
                // номер стиля данного параграфа входит в массив известных стилей.
                // - берем из документа стиль по его номеру
                style = document.getStyleSheet().getStyleDescription ( p.getStyleIndex() );
                Log.l.debug ( "--- DOC import: paragr style = %s", style );
                if ( style != null && style.getName() != null && style.getName().length () > 0 )
                    tas = buildParagraphTagAndStyle ( style.getName(), ( parentTableLevel > 0 ) );
                else
                    tas = new TagAndStyle ( "p", null );
            }
            else
            {
                // стиль данного параграфа неизвестен
                tas = new TagAndStyle ( "p", null );
            }
            Log.l.debug ( "--- DOC import: tas = %s", tas );

            if ( tas.getTag().equals ( "p" ) )
            {
                // конец строки т.к. иногда полный параграф идет без переноса строки, хотя и оформляется в html как полный параграф - т.е. в коцне - перенос строки.
                // - Белые колокола Реаны
                xhtml.emptyLine();
            }

            // стиль
            if ( tas.isHeading() )
            {
                xhtml.startTitle ( tas.getTitleLevel() );
            }
            else if ( tas.getStyleClass() != null )
            {
                // есть имя стиля
                xhtml.startElement ( tas.getTag(), "class", tas.getStyleClass() );
            }
            else
            {
                xhtml.startElement ( tas.getTag() );
            }

            // Надо парсить строки, выискивая одиночную строку с пустыми строками сверху и снизу и считать это заголовком.
            for ( int j = 0; j < p.numCharacterRuns (); j++ )
            {
                // first symbol = 13, 19, 32
                cr = p.getCharacterRun (j);

                // ищем перевод страницы  -- не нашли в самом тексте никаких спецсимволов. Возможно, переводы строк организованы как-то внутренне
                /*
                r - X..Run - получено из X..Paragraph
r.getCTR().toString().contains("<w:br w:type=\"page\"/>")
                 */
                //str = cr.text();
                /*
                if ( str.length() == 1 )
                {
                    //Log.file.debug ( "--- word 1 CR = %d", (int)str.getBytes ( "UTF-8" )[0] );
                    Log.file.debug ( "--- word 1 CR = %s", DumpTools.printString ( str ) );
                }
                */
                // проверять все символы и если есть код меньше 30 - печатать  стркоу
                //if ( DumpTools.hasLittelCode ( str, 30 ))  Log.file.debug ( "--- word 1 CR = %s", DumpTools.printString ( str ) );

                // FIELD_BEGIN_MARK:
                if ( cr.text().getBytes ( "UTF-8" )[0] == 0x13 )
                {
                    field = document.getFields ().getFieldByStartOffset ( docPart, cr.getStartOffset() );
                    // 58 is an embedded document
                    // 56 is a document link
                    if ( field != null && ( field.getType() == 58 || field.getType() == 56 ) )
                    {
                        // Embedded Object: add a <div class="embedded" id="_X"/> so consumer can see where
                        // in the main text each embedded document occurred:
                        id = "_" + field.getMarkSeparatorCharacterRun ( r ).getPicOffset ();
                        attributes = new AttributesImpl ();
                        attributes.addAttribute ( "", "class", "class", "CDATA", "embedded" );
                        attributes.addAttribute ( "", "id", "id", "CDATA", id );
                        xhtml.startElement ( "div", attributes );
                        xhtml.endElement ( "div" );
                    }
                }

                if ( cr.text().equals ( "\u0013" ) )
                {
                    j += handleSpecialCharacterRuns ( p, j, tas.isHeading(), pictures, xhtml );
                }
                else if ( cr.text().startsWith ( "\u0008" ) )
                {
                    // Floating Picture(s)
                    for ( int pn = 0; pn < cr.text ().length (); pn++ )
                    {
                        // Assume they're in the order from the unclaimed list...
                        picture = pictures.nextUnclaimed ();

                        // Output
                        handlePictureCharacterRun ( cr, picture, pictures, xhtml );
                    }
                }
                else if ( pictureTable.hasPicture ( cr ) )
                {
                    // Inline Picture
                    picture = pictures.getFor ( cr );
                    handlePictureCharacterRun ( cr, picture, pictures, xhtml );
                }
                else
                {
                    handleCharacterRun ( cr, tas.isHeading(), xhtml );
                }
            }

            // Close any still open style tags
            if ( curStrikeThrough )
            {
                xhtml.endElement ( "s" );
                curStrikeThrough = false;
            }
            if ( curItalic )
            {
                xhtml.endElement ( "i" );
                curItalic = false;
            }
            if ( curBold )
            {
                //xhtml.endElement ( "b" );
                xhtml.endBold ();
                curBold = false;
            }

            if ( tas.isHeading() )
                xhtml.endTitle ( tas.getTitleLevel() );
            else
                xhtml.endElement ( tas.getTag() );

        } catch ( Exception e )         {
            Log.l.error ( "error", e );
            throw new WEditException ( e, "" );
        }

        return 0;
    }

    private int handleTable ( Table table, Paragraph p, HWPFDocument document, FieldsDocumentPart docPart,
                              PicturesSource pictures, PicturesTable pictureTable, IBookContentCreator xhtml )
            throws WEditException
    {
        int ic;
        TableRow row;
        TableCell cell;
        Paragraph cellP;

        //xhtml.startElement ( "table" );
        xhtml.startTable();
        //xhtml.startElement ( "tbody" );
        xhtml.startTableBody();

        try
        {
            for ( int rn = 0; rn < table.numRows(); rn++ )
            {
                row = table.getRow ( rn );
                //xhtml.startElement ( "tr" );
                xhtml.startTableTr();
                for ( int cn = 0; cn < row.numCells (); cn++ )
                {
                    cell = row.getCell ( cn );
                    xhtml.startElement ( "td" );

                    // параграфы внутри ячеек
                    for ( int pn = 0; pn < cell.numParagraphs (); pn++ )
                    {
                        cellP = cell.getParagraph ( pn );
                        handleParagraph ( cellP, p.getTableLevel (), cell, document, docPart, pictures, pictureTable, xhtml );
                    }
                    xhtml.endElement ( "td" );
                }
                //xhtml.endElement ( "tr" );
                xhtml.endTableTr ();
            }
        } catch ( Exception e )        {
            Log.l.error ( "error", e );
            throw new WEditException ( e, "handleTable error" );
        }

        xhtml.endTableBody ();
        //xhtml.endElement ( "table" );
        xhtml.endTable ();

        ic = table.numParagraphs() - 1;

        return ic;
    }

    private void handleCharacterRun(CharacterRun cr, boolean skipStyling, IBookContentCreator xhtml)
          throws WEditException
    {
       // Skip trailing newlines
       if(!isRendered(cr) || cr.text().equals("\r"))
          return;

        // todo cr - можно получить назхвания и размер шрифта.

       if(!skipStyling)
       {
         if (cr.isBold() != curBold) {
           // Enforce nesting -- must close s and i tags
           if (curStrikeThrough) {
             xhtml.endElement("s");
             curStrikeThrough = false;
           }
           if (curItalic) {
             xhtml.endElement("i");
             curItalic = false;
           }
           if (cr.isBold()) {
             xhtml.startElement("b");
           } else {
             xhtml.endElement("b");
           }
           curBold = cr.isBold();
         }

         if (cr.isItalic() != curItalic) {
           // Enforce nesting -- must close s tag
           if (curStrikeThrough) {
             xhtml.endElement("s");
             curStrikeThrough = false;
           }
           if (cr.isItalic()) {
             xhtml.startElement("i");
           } else {
             xhtml.endElement("i");
           }
           curItalic = cr.isItalic();
         }

         if (cr.isStrikeThrough() != curStrikeThrough) {
           if (cr.isStrikeThrough()) {
             xhtml.startElement("s");
           } else {
             xhtml.endElement("s");
           }
           curStrikeThrough = cr.isStrikeThrough();
         }
       }

       // Clean up the text
       String text = cr.text();
       text = text.replace('\r', '\n');
       if(text.endsWith("\u0007")) {
          // Strip the table cell end marker
          text = text.substring(0, text.length()-1);
       }

       // Copied from POI's org/apache/poi/hwpf/converter/AbstractWordConverter.processCharacters:

       // Non-breaking hyphens are returned as char 30
       text = text.replace((char) 30, UNICODECHAR_NONBREAKING_HYPHEN);

       // Non-required hyphens to zero-width space
       text = text.replace((char) 31, UNICODECHAR_ZERO_WIDTH_SPACE);

       // Control characters as line break
       text = text.replaceAll("[\u0000-\u001f]", "\n");
       xhtml.characters(text);
    }

    /**
     * Can be \13..text..\15 or \13..control..\14..text..\15 .
     * Nesting is allowed
     */
    private int handleSpecialCharacterRuns ( Paragraph p, int index, boolean skipStyling,
          PicturesSource pictures, IBookContentCreator xhtml )
            throws WEditException
    {
       List<CharacterRun> controls = new ArrayList<CharacterRun>();
       List<CharacterRun> texts = new ArrayList<CharacterRun>();
       boolean has14 = false;

       // Split it into before and after the 14
       int i;
       for(i=index+1; i<p.numCharacterRuns(); i++) {
          CharacterRun cr = p.getCharacterRun(i);
          if(cr.text().equals("\u0013")) {
             // Nested, oh joy...
             int increment = handleSpecialCharacterRuns(p, i+1, skipStyling, pictures, xhtml);
             i += increment;
          } else if(cr.text().equals("\u0014")) {
             has14 = true;
          } else if(cr.text().equals("\u0015")) {
             if(!has14) {
                texts = controls;
                controls = new ArrayList<CharacterRun>();
             }
             break;
          } else {
             if(has14) {
                texts.add(cr);
             } else {
                controls.add(cr);
             }
          }
       }

       // Do we need to do something special with this?
       if(controls.size() > 0)
       {
          String text = controls.get(0).text();
          for(int j=1; j<controls.size(); j++)
          {
             text += controls.get(j).text();
          }

          if((text.startsWith("HYPERLINK") || text.startsWith(" HYPERLINK"))
                 && text.indexOf('"') > -1)
          {
             String url = text.substring(
                   text.indexOf('"') + 1,
                   text.lastIndexOf('"')
             );
             xhtml.startElement("a", "href", url);
             for(CharacterRun cr : texts)
             {
                handleCharacterRun ( cr, skipStyling, xhtml );
             }
             xhtml.endElement("a");
          }
          else
          {
             // Just output the text ones
             for(CharacterRun cr : texts)
             {
                if(pictures.hasPicture(cr))
                {
                   Picture picture = pictures.getFor(cr);
                   handlePictureCharacterRun ( cr, picture, pictures, xhtml );
                }
                else
                {
                   handleCharacterRun ( cr, skipStyling, xhtml );
                }
             }
          }
       }
       else
       {
          // We only had text
          // Output as-is
          for(CharacterRun cr : texts)
          {
             handleCharacterRun(cr, skipStyling, xhtml);
          }
       }

       // Tell them how many to skip over
       return i-index;
    }

    private void handlePictureCharacterRun ( CharacterRun cr, Picture picture, PicturesSource pictures, IBookContentCreator xhtml )
            throws WEditException
    {
        if ( ! isRendered ( cr ) || picture == null )
        {
            // Oh dear, we've run out...
            // Probably caused by multiple \u0008 images referencing the same real image
            return;
        }

        String          extension, filename, mimeType;
        int             pictureNumber;
        AttributesImpl  attr;

        // Which one is it?
        extension       = picture.suggestFileExtension();
        pictureNumber   = pictures.pictureNumber ( picture );

        // Make up a name for the picture
        // There isn't one in the file, but we need to be able to reference
        //  the picture from the img tag and the embedded resource
        filename = "image" + pictureNumber + ( extension.length() > 0 ? "." + extension : "" );

        // Grab the mime type for the picture
        mimeType = picture.getMimeType();

        // filename = image1.jpg; mimeType = image/jpeg; hasOutput = false
        Log.file.debug ( "---- Has picture: filename = %s; mimeType = %s; hasOutput = %b", filename, mimeType, pictures.hasOutput ( picture ) );

        // Output the img tag
        attr = new AttributesImpl ();
        attr.addAttribute ( "", "src", "src", "CDATA", "embedded:" + filename );
        attr.addAttribute ( "", "alt", "alt", "CDATA", filename );
        xhtml.startElement ( "img", attr );
        xhtml.endElement ( "img" );

        // Have we already output this one?
        // (Only expose each individual image once)
        if ( ! pictures.hasOutput ( picture ) )
        {
            Log.file.debug ( "---- Has picture content" );
            //TikaInputStream stream = TikaInputStream.get(picture.getContent());
            //handleEmbeddedResource (stream, filename, null, mimeType, xhtml, false);
            xhtml.handleEmbeddedResource ( picture.getContent(), filename, null, mimeType, false );
            pictures.recordOutput ( picture );
        }
    }

    /**
     * Outputs a section of text if the given text is non-empty.
     *
     * @param xhtml XHTML content handler
     * @param section the class of the &lt;div/&gt; section emitted
     * @param text text to be emitted, if any
     * @throws org.xml.sax.SAXException if an error occurs
     */
    private void addTextIfAny( IBookContentCreator xhtml, String section, String text )
            //throws SAXException
    {
        if (text != null && text.length() > 0)
        {
            xhtml.startElement("div", "class", section);
            xhtml.element("p", text);
            xhtml.endElement("div");
        }
    }

    protected void parseWord6 ( NPOIFSFileSystem filesystem, IBookContentCreator xhtml )
            throws WEditException
    {
        parseWord6 ( filesystem.getRoot(), xhtml );
    }

    protected void parseWord6 ( DirectoryNode root, IBookContentCreator xhtml )
            throws WEditException
    {
        HWPFOldDocument doc;
        Word6Extractor  extractor;
        String[]        pText;

        try
        {
            doc         = new HWPFOldDocument (root);
        } catch ( Exception e )         {
            Log.l.error ( "error", e );
            throw new WEditException ( e, "Create HWPFOldDocument :\n", e );
        }
        extractor   = new Word6Extractor (doc);

        pText = extractor.getParagraphText();
        Log.l.debug ( "pText = %s", DumpTools.printArray ( pText, ';' ) );

        for(String p :  pText )
        {
            xhtml.element ( "p", p );
        }
    }

    private static final Map<String,TagAndStyle> fixedParagraphStyles = new HashMap<String,TagAndStyle>();
    private static final TagAndStyle defaultParagraphStyle = new TagAndStyle("p", null, -1 );
    static {
        fixedParagraphStyles.put("Default", defaultParagraphStyle);
        fixedParagraphStyles.put("Normal", defaultParagraphStyle);
        fixedParagraphStyles.put("heading", new TagAndStyle("h1", null, 1 ));
        fixedParagraphStyles.put("Heading", new TagAndStyle("h1", null, 1 ));
        fixedParagraphStyles.put("Title", new TagAndStyle("h1", "title", 1 ));
        fixedParagraphStyles.put("Subtitle", new TagAndStyle("h2", "subtitle", 2 ));
        fixedParagraphStyles.put("HTML Preformatted", new TagAndStyle("pre", null, -1 ));
    }

    /**
     * Given a style name, return what tag should be used, and
     *  what style should be applied to it.
     *
     *  -- styleName = Верхний колонтитул
     -- styleName = Normal
     -- styleName = Заголовок 3
     -- styleName = Стандартный HTML

     */
    public static TagAndStyle buildParagraphTagAndStyle ( String styleName, boolean isTable )
    {
        TagAndStyle tagAndStyle;

        //System.out.println ( "-- styleName = " + styleName );

        tagAndStyle = fixedParagraphStyles.get ( styleName );
        if ( tagAndStyle != null )   return tagAndStyle;

        if ( styleName.equals ( "Table Contents" ) && isTable )   return defaultParagraphStyle;

        String tag = "p";
        String styleClass = null;
        int num = -1;

        if ( styleName.startsWith ( "heading" ) || styleName.startsWith ( "Heading" ) || styleName.startsWith ( "Заголовок" ) )
        {
            // Стиль - Заголовок.
            // "Heading 3" or "Heading2" or "heading 4"
            // выделяем номер заголовка
            try
            {
                num = Integer.parseInt ( styleName.substring ( styleName.length() - 1 ) );
            } catch ( NumberFormatException e ) {
                Log.l.error ( "error", e );
                num = 1;
            }
            // Turn it into a H1 - H6 (H7+ isn't valid!)
            tag = "h" + Math.min ( num, 6 );
        }
        else
        {
            styleClass = styleName.replace ( ' ', '_' );
            styleClass = styleClass.substring ( 0, 1 ).toLowerCase ( Locale.ROOT ) +
                    styleClass.substring ( 1 );
        }

        return new TagAndStyle ( tag, styleClass, num );
    }

    public static class TagAndStyle
    {
        private String  tag;
        private String  styleClass;
        private int     titleLevel;

        public TagAndStyle ( String tag, String styleClass )
        {
            this ( tag, styleClass, -1 );
        }

        public TagAndStyle ( String tag, String styleClass, int num )
        {
            this.tag = tag;
            this.styleClass = styleClass;
            titleLevel = num;
        }

        public String getTag ()
        {
            return tag;
        }

        public String getStyleClass ()
        {
            return styleClass;
        }

        public boolean isHeading ()
        {
            return tag.length () == 2 && tag.startsWith ( "h" );
        }

        public int getTitleLevel ()
        {
            return titleLevel;
        }

        public String toString()
        {
            StringBuilder result;

            result = new StringBuilder ( 64 );
            result.append ( "[ TagAndStyle : tag = " );
            result.append ( getTag() );
            result.append ( "; level = " );
            result.append ( getTitleLevel () );
            result.append ( "; styleClass = " );
            result.append ( getStyleClass () );
            result.append ( " ]" );

            return result.toString();
        }
    }

    /**
     * Determines if character run should be included in the extraction.
     *
     * @param cr character run.
     * @return true if character run should be included in extraction.
     */
    private boolean isRendered(final CharacterRun cr) {
 	   return cr == null || !cr.isMarkedDeleted();
    }


    /**
     * Provides access to the pictures both by offset, iteration
     *  over the un-claimed, and peeking forward
     */
    private static class PicturesSource {
       private PicturesTable picturesTable;
       private Set<Picture> output = new HashSet<Picture>();
       private Map<Integer,Picture> lookup;
       private List<Picture> nonU1based;
       private List<Picture> all;
       private int pn = 0;

       private PicturesSource(HWPFDocument doc) {
          picturesTable = doc.getPicturesTable();
          all = picturesTable.getAllPictures();

          // Build the Offset-Picture lookup map
          lookup = new HashMap<Integer, Picture>();
          for(Picture p : all) {
             lookup.put(p.getStartOffset(), p);
          }

          // Work out which Pictures aren't referenced by
          //  a \u0001 in the main text
          // These are \u0008 escher floating ones, ones
          //  found outside the normal text, and who
          //  knows what else...
          nonU1based = new ArrayList<Picture>();
          nonU1based.addAll(all);
          Range r = doc.getRange();
          for(int i=0; i<r.numCharacterRuns(); i++) {
             CharacterRun cr = r.getCharacterRun(i);
             if(picturesTable.hasPicture(cr)) {
                Picture p = getFor(cr);
                int at = nonU1based.indexOf(p);
                nonU1based.set(at, null);
             }
          }
       }

       private boolean hasPicture(CharacterRun cr) {
          return picturesTable.hasPicture(cr);
       }

       private void recordOutput(Picture picture) {
          output.add(picture);
       }
       private boolean hasOutput(Picture picture) {
          return output.contains(picture);
       }

       private int pictureNumber(Picture picture) {
          return all.indexOf(picture) + 1;
       }

       private Picture getFor(CharacterRun cr) {
          return lookup.get(cr.getPicOffset());
       }

       /**
        * Return the next unclaimed one, used towards
        *  the end
        */
       private Picture nextUnclaimed() {
          Picture p = null;
          while(pn < nonU1based.size()) {
             p = nonU1based.get(pn);
             pn++;
             if(p != null) return p;
          }
          return null;
       }
    }
}
