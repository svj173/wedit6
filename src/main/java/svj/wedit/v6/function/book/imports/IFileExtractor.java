package svj.wedit.v6.function.book.imports;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.book.imports.doc.target.IBookContentCreator;

import javax.swing.*;
import java.io.File;

/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.12.14 14:22
 */
public interface IFileExtractor
{
    void parse ( File file, IBookContentCreator fileHandler ) throws WEditException;

    void processAdditional ( JComponent additionalGuiComponent );
}
