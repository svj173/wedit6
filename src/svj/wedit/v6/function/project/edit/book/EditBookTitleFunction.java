package svj.wedit.v6.function.project.edit.book;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.project.AbstractSaveProjectFunction;
import svj.wedit.v6.function.project.edit.book.create.CreateBookDialog;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookTitle;
import svj.wedit.v6.tools.BookTools;
import svj.wedit.v6.tools.DialogTools;

import java.awt.event.ActionEvent;


/**
 * Редактировать выбранную книгу в дереве Сборника.
 * <BR/> Допускается изменять только Русское название Книги. Имя файла - нельзя.
 * <BR/> Перезаписывает проект в project.xml  - т.е. без возможности отката.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 05.04.2013 10:52:24
 */
public class EditBookTitleFunction extends AbstractSaveProjectFunction
{
    public EditBookTitleFunction ()
    {
        setId ( FunctionId.EDIT_BOOK_TITLE );
        //setName ( "Редактировать титл книги" );
        setName ( "Свойства книги");
        setIconFileName ( "edit.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        TreePanel<Project>  currentProjectPanel;
        TreeObj             selectNode;
        CreateBookDialog    dialog;
        Project             project;
        String              bookTitleName, titleFromBookContent, fileName;
        BookTitle           bookTitle, newBookTitle;
        BookContent         bookContent;

        Log.l.debug ( "Start" );

        // Взять текущий проект - TreePanel
        currentProjectPanel = Par.GM.getFrame().getCurrentProjectPanel();
        if ( currentProjectPanel == null ) throw  new WEditException ( "Текущий Сборник не установлен." );

        // Взять текущую книгу
        selectNode  = currentProjectPanel.getCurrentObj();
        if ( selectNode == null ) throw  new WEditException ( "Книга не выбрана." );

        bookTitle   = (BookTitle) selectNode.getWTreeObj();
        Log.l.debug ( "[BTEdit] bookTitle = %s", bookTitle );

        // Анализируем наличие текста книги. Если есть, также возьмем титл и оттуда - вдруг они Разные? Чтобы иметь возможность для синхронизации.
        // Не всегда  bookContent вложен в bookTitle -- лучше искать выделенную в дереве книгу. -- Это сложно сделать. Проще в bookTitle хранить и полный путь до файла.
        titleFromBookContent    = null;
        //fileName                = null;
        bookContent             = bookTitle.getBookContent();
        Log.l.debug ( "bookContent = %s", bookContent );
        if ( bookContent != null )
        {
            titleFromBookContent = bookContent.getName();
            //fileName             = bookContent.getFileName();
        }
        Log.l.debug ( "titleFromBookContent = %s", titleFromBookContent );

        fileName = BookTools.createFilePath ( currentProjectPanel.getObject(), bookTitle.getParent(), bookTitle );

        // Диалог
        dialog      = new CreateBookDialog ( getName(), false, titleFromBookContent, fileName );
        dialog.init ( bookTitle );
        dialog.showDialog ();
        if ( dialog.isOK() )
        {
            if ( dialog.isChange() )
            {
                newBookTitle    = dialog.getResult();
                // Взять имя
                bookTitleName   = newBookTitle.getName();
                // Сравнить имена
                //if ( bookTitleName.equals ( bookTitle.getName() ) )
                //    throw new WEditException ( null, "Титл Книги не изменился." );

                // Изменяем имя
                bookTitle.setName ( bookTitleName );

                // Изменяем статус
                bookTitle.setBookStatus ( bookTitle.getBookStatus() );

                // Взять текущий проект
                project = currentProjectPanel.getObject();

                // Сохранить обновление проекта - в файле project.xml
                saveProjectFile ( project );

                // Обновить название/статус в дереве Сборника
                currentProjectPanel.getTreeModel().nodeChanged ( selectNode );
            }
            else
            {
                DialogTools.showMessage ( getName(), "Нет изменений." );
            }
        }

        Log.l.debug ( "Finish" );
    }

}
