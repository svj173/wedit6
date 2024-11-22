package svj.wedit.v6.function.book.imports.txt;


import org.xml.sax.helpers.AttributesImpl;
import svj.wedit.v6.function.book.imports.doc.target.IBookContentCreator;
import svj.wedit.v6.function.book.imports.doc.target.ShowTitleObj;
import svj.wedit.v6.obj.book.BookContent;

import java.util.ArrayList;
import java.util.Collection;

/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.12.14 21:38
 */
public class ShowTitleTextHandler implements IBookContentCreator<Collection<ShowTitleObj>>
{
    private final Collection<ShowTitleObj> titleList    = new ArrayList<ShowTitleObj> ();


    @Override
    public void start ()
    {
        titleList.clear();
    }

    @Override
    public void title ( String text )
    {
        titleList.add ( new ShowTitleObj ( text, 1 ) );
    }

    @Override
    public Collection<ShowTitleObj> getResult ()
    {
        return titleList;
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
    public void characters ( String text )   { }

    @Override
    public void handleEmbeddedResource ( byte[] content, String filename, Object o, String mimeType, boolean b )
    {
    }

    @Override
    public void startTable ()  { }

    @Override
    public void endTable ()   { }

    @Override
    public void startTableBody ()   { }

    @Override
    public void endTableBody ()  {}

    @Override
    public void startTableTr ()  {}

    @Override
    public void endTableTr () {}

    @Override
    public void addMainTextboxText ( String paragraph )  {}

    @Override
    public void addFootnoteText ( String paragraph )  {}

    @Override
    public void addCommentsText ( String paragraph ) {}

    @Override
    public void addEndnoteText ( String paragraph ) {}

    @Override
    public void emptyLine () {  }

    @Override
    public void endBold ()   {}

    @Override
    public void startTitle ( int titleLevel ) {}

    @Override
    public void endTitle ( int titleLevel ){}

    @Override
    public void init ( BookContent bookContent, Collection<ShowTitleObj> titleList ){}

    @Override
    public void addText ( String text ) { }

}
