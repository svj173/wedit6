package svj.wedit.v6.function.project.edit.book.open;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.project.AbstractSaveProjectFunction;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookTitle;
import svj.wedit.v6.obj.book.xml.BookContentStaxParser;
import svj.wedit.v6.tools.FileTools;

import java.awt.event.ActionEvent;
import java.io.File;


/**
 * Открыть выбранную книгу.
 * <BR/> Вычисляется файл книги. Загружается. Открывается в среднем окне в виде дерева в отдельном табе.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 18.08.2011 16:49:15
 */
public class OpenBookFunction extends AbstractSaveProjectFunction
{
    public OpenBookFunction ()
    {
        setId ( FunctionId.OPEN_BOOK );
        setName ( "Открыть книгу" );
        setIconFileName ( "open.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        TreePanel<Project>      currentProjectPanel;
        TreeObj                 selectNode, sectionNode;
        Project                 project;
        BookTitle               bookTitle;
        StringBuilder           filePath;
        BookContentStaxParser   bookParser;
        BookContent             bookContent;
        File                    file;
        String                  bookId;
        Object                  wTreeObj;

        Log.l.debug ( "Start" );

        // Взять текущий проект - TreePanel
        currentProjectPanel = Par.GM.getFrame().getCurrentProjectPanel();
        project             = currentProjectPanel.getObject();

        // Взять текущую книгу
        selectNode      = currentProjectPanel.getCurrentObj();
        wTreeObj        = selectNode.getWTreeObj();

        if ( wTreeObj instanceof BookTitle )
        {
            bookTitle       = ( BookTitle ) wTreeObj;
            Log.l.debug ( "[BTEdit] bookTitle openBook = ", bookTitle );

            sectionNode     = (TreeObj) selectNode.getParent();


            // Сформировать полное имя файла.
            filePath        = FileTools.createNodeFilePath ( project, sectionNode );
            filePath.append ( '/' );
            filePath.append ( bookTitle.getFileName() );
            Log.l.debug ( "[BTEdit] bookTitle filePath = '", filePath, "'" );

            file            = new File ( filePath.toString() );

            // Определить - может такой Сборник уже загружен и открыт
            bookId   = file.getAbsolutePath();
            if ( Par.GM.containBook ( bookId, project ))
            {
                // уже есть открытый - сделать его текущим выбранным
                Par.GM.selectBook ( bookId, project );
            }
            else
            {
                // Распарсить файл в дерево.
                bookParser      = new BookContentStaxParser();
                bookContent     = bookParser.read ( file, bookTitle.getName(), bookTitle.getId() );
                bookContent.setProject ( project );

                // Занести книгу в титл
                bookTitle.setBookContent ( bookContent );
                Log.l.debug ( "[BTEdit] bookContent add to bookTitle = ", bookContent );

                // Открыть дерево в новом табике в окне Книг.
                // установить новый проект в систему (добавить к списку открытых проектов)).
                Par.GM.addBookContent ( bookContent, project );
            }
        }

        Log.l.debug ( "Finish" );
    }

}
