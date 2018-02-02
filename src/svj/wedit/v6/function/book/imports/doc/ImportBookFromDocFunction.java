package svj.wedit.v6.function.book.imports.doc;


import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.book.imports.AImportBookFunction;
import svj.wedit.v6.function.book.imports.doc.target.ShowTitleContentHandler;
import svj.wedit.v6.function.book.imports.doc.target.WEdit6ContentHandler;

/**
 * Импортировать книгу из формата DOC.
 * <BR/>
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 17.12.2014 13:20
 */
public class ImportBookFromDocFunction extends AImportBookFunction
{
    public ImportBookFromDocFunction ()
    {
        super ( new WordFileExtractor(), new ShowTitleContentHandler(), new WEdit6ContentHandler(), "DOC" );
        setId ( FunctionId.IMPORT_FROM_DOC );
        setName ( "Импортировать книгу из формата DOC." );
        //setMapKey ( "Ctrl/S" );
        setIconFileName ( "from_doc.png" );
    }

}
