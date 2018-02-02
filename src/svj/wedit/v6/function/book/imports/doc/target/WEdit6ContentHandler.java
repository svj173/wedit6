package svj.wedit.v6.function.book.imports.doc.target;


import org.xml.sax.helpers.AttributesImpl;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.book.ImgTextObject;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.FileTools;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Импорт из формата DOC.
 * <BR/> Двух видов:
 * <BR/> 1) стиля Заголовки
 * <BR/> 2) стиля Bold - если это чистая строка с пропусками строк сверху и снизу.
 * <BR/>
 * <BR/> Замечания:
 * <BR/> 1) Перенос страницы никак не ловится - нет такого символа в тексте и нет признака.
 * <BR/> 2) Если перед красной строкой стоят пробелы - то эти пробелы - первый параграф, а текст следом - второй параграф.
 * Причем зачастую если текст оканичевается на перенос строки, то в конвертер этот перенос не приходит.
 * Т.е. нельзя принудительно вставлять переносы строк в конец каждого параграфа.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 22.12.2014 15:14
 */
public class WEdit6ContentHandler implements IBookContentCreator<BookContent>
{
    private BookContent               bookContent;
    private Map<String,ShowTitleObj>  titleMap;
    private BookNode    currentNode;
    private String      currentText = null;


    public WEdit6ContentHandler ()
    {
    }

    @Override
    public void start ()
    {
    }

    @Override
    public void init ( BookContent bookContent, Collection<ShowTitleObj> titleList )
    {
        this.bookContent    = bookContent;
        this.titleMap       = new HashMap<String,ShowTitleObj>();
        for ( ShowTitleObj obj : titleList )
        {
            titleMap.put ( obj.getTitle(), obj );
        }

        currentNode = bookContent.getBookNode();
    }

    @Override
    public void title ( String text )
    {
    }

    public void element ( String tag, String value )
    {
        // ?? - tag парсим на начало - H1.. - заголовки. Div - какой-то текст
        addText ( value );
    }

    public void addText ( String text )
    {
        // Здесь текст также может содержать переносы строк - обработать.
        Log.l.debug ( "--- --- DOC import creator: add text = '%s'", text );
        //Log.l.debug ( "--- --- DOC import creator: add text = '%s'", DumpTools.printString ( text ) );
        // проверяем на больше одного переноса
        if ( multiEol(text) )
        {
            String[] sb = text.split ( "\n" );
            for ( String s : sb )
            {
                currentNode.addText ( s+"\n", null );
                //Log.l.debug ( "--- DOC import creator: add EOL text = '%s'", s );
            }
        }
        else
        {
            currentNode.addText ( text, null );
            // Ломались переносы строк при импорте Белые колокола Реаны.
            //currentNode.addText ( text+"\n", null );
        }
    }

    private boolean multiEol ( String text )
    {
        boolean result;
        int ic;

        result = false;
        ic = text.indexOf ( '\n' );
        if ( ic >= 0 )
        {
            // анализ на второй перенос строки
            ic = text.indexOf ( '\n', ic+1 );
            if ( ic >= 0 )  result = true;
        }
        Log.l.debug ( "--- DOC import creator: check multiEol. text = '%s'; result = %b", text, result );
        return result;
    }

    public void startElement ( String tag, String aClass, String type )
    {
    }

    public void endElement ( String tag )
    {
    }

    public void startElement ( String tag )
    {
    }

    public void startElement ( String tag, AttributesImpl attributes )
    {
    }

    public void characters ( String text )
    {
        addText ( text );
        currentText = text;
    }

    // какой-то обьект. например, картинка. Надо скинуть себе.
    public void handleEmbeddedResource ( byte[] content, String filename, Object o, String mimeType, boolean b )
    {
        String bookImgDir, targetFileName;
        File   bookFile, bookImgDirFile;
        //Image  image;

        if ( content != null )
        {
            Log.file.debug ( "Save picture. Start: filename = %s; mimeType = %s; image content size = %d", filename, mimeType, content.length );
            // сохранить картинку в директории данной книги
            targetFileName = null;

            try
            {
                // - создать директорию картинок данной книги
                // Сформировать директорию книги
                bookFile        = new File ( bookContent.getFileName() );
                // - создать имя директории для картинок книг
                bookImgDir      = Convert.concatObj ( bookFile.getParent(), "/image" );
                bookImgDirFile  = new File ( bookImgDir );
                FileTools.createFolder ( bookImgDirFile );

                // Сформирвоать полное имя файла картинки - /home/svj/Serg/SvjStores/zs/zs-6/image/Barracuda_01.jpg
                targetFileName  = bookImgDirFile + File.separator + filename;

                // Сохраняем в файле
                //image           = Toolkit.getDefaultToolkit ().createImage(content);
                FileTools.save ( targetFileName, content );

                // создать обьект
                currentNode.addText ( new ImgTextObject ( targetFileName ) );

            } catch ( Exception e )       {
                Log.file.error ( Convert.concatObj ( "Save picture. Error: filename = ", filename, "; mimeType = ", mimeType, "; image content size = ", content.length, "; targetFileName = ", targetFileName ), e );
            }
        }
    }

    public BookContent getResult ()
    {
        return bookContent;
    }

    @Override
    public void startTable ()
    {
    }

    @Override
    public void endTable ()
    {
    }

    @Override
    public void startTableBody ()
    {
    }

    @Override
    public void endTableBody ()
    {
    }

    @Override
    public void startTableTr ()
    {
    }

    @Override
    public void endTableTr ()
    {
    }

    @Override
    public void addMainTextboxText ( String paragraph )
    {
        addText ( paragraph );
    }

    @Override
    public void addFootnoteText ( String paragraph )
    {
        addText ( paragraph );
    }

    @Override
    public void addCommentsText ( String paragraph )
    {
        addText ( paragraph );
    }

    @Override
    public void addEndnoteText ( String paragraph )
    {
        addText ( paragraph );
    }

    @Override
    public void emptyLine ()
    {
        addText ( "\n" );
    }

    @Override
    public void endBold ()
    {
        if ( isTitle(currentText) )
            processTitle();
        else
            addText ( currentText );
    }

    private boolean isTitle ( String text )
    {
        boolean result;

        result = false;
        if ( (text != null) && (text.length() < 100 ) )
        {
            // не содержит подчеркиваний
            if ( ! text.contains ( "-----" ) )
            {
                if ( ! text.contains ( "=====" ) )
                {
                    if ( ! text.contains ( "____" ) )
                    {
                        if ( ! text.contains ( "#####" ) )  result = true;
                    }
                }
            }
        }
        return result;
    }

    private void processTitle ()
    {
        ShowTitleObj titleObj;
        int          level;

        Log.l.debug ( "--- DOC import creator: processTitle: Start. currentText = %s", currentText );
        // Сравнение
        if ( titleMap.containsKey ( currentText ) )
        {
            titleObj    = titleMap.get ( currentText );
            Log.l.debug ( "--- DOC import creator: processTitle: titleObj = %s", titleObj );
            level       = titleObj.getLevel();
            if ( level > 0 )
            {
                // Создать новый элемент книги.
                currentNode = bookContent.createNextBookNode ( currentText, level );
                Log.l.debug ( "--- DOC import creator: processTitle: new currentNode = %s", currentNode );
            }
        }
    }

    @Override
    public void startTitle ( int titleLevel )
    {
    }

    @Override
    public void endTitle ( int titleLevel )
    {
        processTitle();
    }

}
