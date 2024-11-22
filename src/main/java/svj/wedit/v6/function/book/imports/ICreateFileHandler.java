package svj.wedit.v6.function.book.imports;


import svj.wedit.v6.function.book.imports.doc.target.ShowTitleObj;
import svj.wedit.v6.obj.book.BookContent;

import java.util.Collection;

/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.12.14 14:27
 */
public interface ICreateFileHandler
{
    Collection<ShowTitleObj> getResult ();

    void init ( BookContent bookContent, Collection<ShowTitleObj> titleList );
}
