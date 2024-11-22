package svj.wedit.v6.function.book.imports.doc.target;


import org.xml.sax.helpers.AttributesImpl;
import svj.wedit.v6.obj.book.BookContent;

import java.util.Collection;

/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 15.12.14 15:14
 */
public class XHTMLContentHandler  implements IBookContentCreator<String>
{
    private final StringBuilder html = new StringBuilder ( 1024 );

    @Override
    public void start ()
    {
    }

    public void element ( String tag, String value )
    {
        html.append ( "<" );
        html.append ( tag );
        html.append ( ">" );
        html.append ( value );
        html.append ( "</" );
        html.append ( tag );
        html.append ( ">\n" );
    }

    public void startElement ( String tag, String aClass, String type )
    {
        html.append ( "<" );
        html.append ( tag );
        html.append ( " class=" );
        html.append ( aClass );
        html.append ( " type=" );
        html.append ( type );
        html.append ( ">" );
    }

    public void endElement ( String tag )
    {
        html.append ( "</" );
        html.append ( tag );
        html.append ( ">\n" );
    }

    public void startElement ( String tag )
    {
        html.append ( "<" );
        html.append ( tag );
        html.append ( ">" );
    }

    public void startTable ()
    {
        html.append ( "<table>" );
    }

    public void endTable ()
    {
        html.append ( "</table>" );
    }

    public void startTableBody ()
    {
        html.append ( "<tbody>" );
    }

    public void endTableBody ()
    {
        html.append ( "</tbody>" );
    }

    public void startTableTr ()
    {
        html.append ( "<tr>" );
    }

    public void endTableTr ()
    {
        html.append ( "</tr>" );
    }

    @Override
    public void addMainTextboxText ( String paragraph )
    {
        element ( "p", paragraph );
    }

    @Override
    public void addFootnoteText ( String paragraph )
    {
        element ( "p", paragraph );
    }

    @Override
    public void addCommentsText ( String paragraph )
    {
        element ( "p", paragraph );
    }

    @Override
    public void addEndnoteText ( String paragraph )
    {
        element ( "p", paragraph );
    }

    @Override
    public void emptyLine ()
    {
        element ( "br", "<!-- empty -->" );
    }

    @Override
    public void endBold ()
    {
        endElement ( "b" );
    }

    public void startElement ( String tag, AttributesImpl attributes )
    {
        html.append ( "<" );
        html.append ( tag );
        html.append ( ">" );
        html.append ( "<!-- " );
        html.append ( attributes );
        html.append ( " -->\n" );
    }

    public void characters ( String text )
    {
        html.append ( text );
    }

    public void handleEmbeddedResource ( byte[] content, String filename, Object o, String mimeType, boolean b )
    {
    }

    public String getResult ()
    {
        return html.toString();
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
    }

    @Override
    public void addText ( String text )
    {
    }

    @Override
    public void title ( String text )
    {
    }
}
