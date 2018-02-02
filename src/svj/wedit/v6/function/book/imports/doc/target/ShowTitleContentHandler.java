package svj.wedit.v6.function.book.imports.doc.target;


import org.xml.sax.helpers.AttributesImpl;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookContent;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Собирает толкьо титлы.
 * <BR/> Двух видов:
 * <BR/> 1) стиля Заголовки
 * <BR/> 2) стиля Bold - если это чистая строка с пропусками строк сверху и снизу.
 * <BR/> Пропуск строки сверху игнорируем, т.к. если стоит перевод страницы, то в тексте он никак не указывается,
 * и получается что титл следующей страницы идет сразу после текста. 2015-01-27
 * <BR/> Проверку пустой строки после - пока не делаем.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 15.12.14 15:14
 */
public class ShowTitleContentHandler implements IBookContentCreator<Collection<ShowTitleObj>>
{
    private int     emptyLineCount = 0;
    private String  currentText = null;
    private final Collection<ShowTitleObj>  titleList = new ArrayList<ShowTitleObj> ();

    @Override
    public void start ()
    {
        titleList.clear();
    }

    @Override
    public void addText ( String text )
    {
    }

    @Override
    public void title ( String text )
    {
    }

    public void element ( String tag, String value )
    {
        //clearEmptyCounter();
    }

    public void startElement ( String tag, String aClass, String type )
    {
        //clearEmptyCounter();
    }

    public void endElement ( String tag )
    {
        //clearEmptyCounter();
    }

    public void startElement ( String tag )
    {
        //clearEmptyCounter();
    }

    public void startElement ( String tag, AttributesImpl attributes )
    {
        //clearEmptyCounter();
    }

    public void characters ( String text )
    {
        if ( currentText != null )  clearEmptyCounter();
        currentText = text;
    }

    public void handleEmbeddedResource ( byte[] content, String filename, Object o, String mimeType, boolean b )
    {
        //clearEmptyCounter();
    }

    public Collection<ShowTitleObj> getResult ()
    {
        return titleList;
    }

    @Override
    public void startTable ()
    {
        //clearEmptyCounter();
    }

    @Override
    public void endTable ()
    {
        //clearEmptyCounter();
    }

    @Override
    public void startTableBody ()
    {
        //clearEmptyCounter();
    }

    @Override
    public void endTableBody ()
    {
    }

    @Override
    public void startTableTr ()
    {
        //clearEmptyCounter();
    }

    @Override
    public void endTableTr ()
    {
        //clearEmptyCounter();
    }

    @Override
    public void addMainTextboxText ( String paragraph )
    {
        currentText = paragraph;

        //clearEmptyCounter();
    }

    @Override
    public void addFootnoteText ( String paragraph )
    {
        currentText = paragraph;

        //clearEmptyCounter();
    }

    @Override
    public void addCommentsText ( String paragraph )
    {
        currentText = paragraph;
    }

    @Override
    public void addEndnoteText ( String paragraph )
    {
        currentText = paragraph;
        //clearEmptyCounter();
    }

    @Override
    public void emptyLine ()
    {
        emptyLineCount++;
        currentText = null;
    }

    @Override
    public void endBold ()
    {
        Log.file.debug ( "--- show title END_BOLD: emptyLineCount = %d; currentText = %s", emptyLineCount, currentText );
        // убираем проверку на пустую строку сверху титла (перед титлом).
        //if ( emptyLineCount > 0 )
        //{
            // это заголовок - берем текст
            if ( (currentText != null) && (currentText.length() < 100) )  titleList.add ( new ShowTitleObj (currentText) );
            clearEmptyCounter();
        //}
    }

    @Override
    public void startTitle ( int titleLevel )
    {
    }

    @Override
    public void endTitle ( int titleLevel )
    {
        titleList.add ( new ShowTitleObj ( currentText, titleLevel) );
        clearEmptyCounter();
    }

    @Override
    public void init ( BookContent bookContent, Collection<ShowTitleObj> titleList )
    {
    }

    private void clearEmptyCounter()
    {
        emptyLineCount = 0;
    }

}
