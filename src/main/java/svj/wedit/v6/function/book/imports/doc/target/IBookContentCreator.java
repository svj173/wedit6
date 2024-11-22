package svj.wedit.v6.function.book.imports.doc.target;


import org.xml.sax.helpers.AttributesImpl;
import svj.wedit.v6.obj.book.BookContent;

import java.util.Collection;

/**
 * Интерфейс создателей книг при парсинге из различных форматов файлов.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 17.12.14 13:29
 */
public interface IBookContentCreator<R>
{
    void start ();  // начало работы - очистка массивов и т.д.
    void element ( String tag, String value );
    void startElement ( String tag, String aClass, String type );
    void endElement ( String tag );
    void startElement ( String tag );
    void startElement ( String tag, AttributesImpl attributes );
    void characters ( String text );
    void handleEmbeddedResource ( byte[] content, String filename, Object o, String mimeType, boolean b );

    R getResult ();

    void startTable ();
    void endTable ();
    void startTableBody ();
    void endTableBody ();
    void startTableTr ();
    void endTableTr ();

    void addMainTextboxText ( String paragraph );

    void addFootnoteText ( String paragraph );

    void addCommentsText ( String paragraph );

    void addEndnoteText ( String paragraph );

    void emptyLine ();

    void endBold ();

    void startTitle ( int titleLevel );

    void endTitle ( int titleLevel );

    void init ( BookContent bookContent, Collection<ShowTitleObj> titleList );

    void addText ( String text );

    void title ( String text );
}
