package svj.wedit.v6.function.book.imports.txt;


import org.xml.sax.helpers.AttributesImpl;
import svj.wedit.v6.function.book.imports.doc.target.IBookContentCreator;
import svj.wedit.v6.function.book.imports.doc.target.ShowTitleObj;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Преобьразуем текстовый файл в книгу формата WEdit6.
 * <BR/> Правила.
 * <BR/> 1) Заголовки - согласно установленной пользователем схеме при первом проходе текстового файла.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.12.14 21:39
 */
public class TextContentHandler implements IBookContentCreator<BookContent>
{
    private BookContent                 bookContent;
    private Map<String,ShowTitleObj>    titleMap;
    private BookNode                    currentNode;

    @Override
    public void start ()
    {
    }

    @Override
    public void element ( String tag, String value )
    {

    }

    @Override
    public void startElement ( String tag, String aClass, String type )
    {

    }

    @Override
    public void endElement ( String tag )
    {

    }

    @Override
    public void startElement ( String tag )
    {

    }

    @Override
    public void startElement ( String tag, AttributesImpl attributes )
    {

    }

    @Override
    public void characters ( String text )
    {

    }

    @Override
    public void handleEmbeddedResource ( byte[] content, String filename, Object o, String mimeType, boolean b )
    {

    }

    @Override
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

    }

    @Override
    public void addFootnoteText ( String paragraph )
    {

    }

    @Override
    public void addCommentsText ( String paragraph )
    {

    }

    @Override
    public void addEndnoteText ( String paragraph )
    {

    }

    @Override
    public void emptyLine ()
    {

    }

    @Override
    public void endBold ()
    {

    }

    @Override
    public void startTitle ( int titleLevel )
    {

    }

    @Override
    public void endTitle ( int titleLevel )
    {

    }

    @Override
    public void init ( BookContent bookContent, Collection<ShowTitleObj> titleList )
    {
        this.bookContent    = bookContent;
        this.titleMap       = new HashMap<String,ShowTitleObj> ();
        for ( ShowTitleObj obj : titleList )
        {
            titleMap.put ( obj.getTitle(), obj );
        }

        currentNode = bookContent.getBookNode();
    }

    @Override
    public void addText ( String text )
    {
        currentNode.addText ( text, null );
    }

    @Override
    public void title ( String text )
    {
        ShowTitleObj titleObj;
        int          level;

        Log.l.debug ( "--- TXT import creator: processTitle: Start. currentText = %s", text );
        // Сравнение
        if ( titleMap.containsKey ( text ) )
        {
            titleObj    = titleMap.get ( text );
            Log.l.debug ( "--- TXT import creator: processTitle: titleObj = %s", titleObj );
            level       = titleObj.getLevel();
            if ( level > 0 )
            {
                // Обрезаем сови навороты
                if ( text.contains ( "$pu " ) )   text = text.replace ( "$pu ", "" );
                if ( text.contains ( "$pp " ) )   text = text.replace ( "$pp ", "" );
                // Создать новый элемент книги.
                currentNode = bookContent.createNextBookNode ( text, level );
                Log.l.debug ( "--- TXT import creator: processTitle: new currentNode = %s", currentNode );
            }
        }
    }

}
