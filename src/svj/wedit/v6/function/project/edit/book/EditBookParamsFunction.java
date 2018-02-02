package svj.wedit.v6.function.project.edit.book;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.project.AbstractSaveProjectFunction;
import svj.wedit.v6.gui.widget.StringFieldWidget;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookTitle;

import java.awt.event.ActionEvent;


/**
 * Редактировать аттрибуты текущей книги.
 * <BR/> 1) Заголовок.
 * <BR/> 2) Дата создания.
 * <BR/> 3) Автор - изначально берется Из сборника.
 * <BR/> 4) e-mail
 * <BR/> 5) web
 * <BR/> 6) Аннотация на книгу.
 * <BR/> 7) Синопсис.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 05.04.2013 10:52:24
 */
public class EditBookParamsFunction extends AbstractSaveProjectFunction
{
    public EditBookParamsFunction ()
    {
        setId ( FunctionId.EDIT_BOOK_PARAMS );
        setName ( "Редактировать аттрибуты текущей книги");
        setIconFileName ( "edit.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        EditBookParamsDialog    dialog;
        BookContent             bookContent;

        //Log.l.debug ( "Start" );

        bookContent = Par.GM.getFrame().getCurrentBookContent();
        Log.l.debug ( "bookContent = %s", bookContent );
        if ( bookContent == null ) throw  new WEditException ( "Книга не выбрана." );

        // Диалог
        dialog      = new EditBookParamsDialog();
        dialog.init ( bookContent );
        dialog.showDialog();

        if ( dialog.isOK() && dialog.isChange() )
        {
            // были изменения
            bookContent.setName ( dialog.getBookTitle() );
            bookContent.setAnnotation ( dialog.getAnnotation() );
            bookContent.setSynopsis ( dialog.getSynopsis() );
            bookContent.setBookStatus ( dialog.getBookStatus() );

            //  attributies
            for ( StringFieldWidget attrWidget : dialog.getAttrsWidgetList() )
            {
                Log.l.debug ( "--- Edit book params: bookContent = %s; attr name = %s; attr value = %s", bookContent.getName(), attrWidget.getTitleName(), attrWidget.getValue() );
                bookContent.addAttribute ( attrWidget.getTitleName(), attrWidget.getValue() );
            }

            bookContent.setEdit ( true );

            // Если бы изменен статус книги - Сохранить данное изменение в Сборнике - в файле project.xml
            if ( dialog.isStatusChange() )
            {
                Project project = bookContent.getProject();
                saveProjectFile ( project );
                // пнуть bookTitle, чтобы он обновил свой статус - для отрисовки в проекте.
                BookTitle bookTitle = project.getBookTitle ( bookContent.getFileName() );
                //Log.l.debug ( "--- Edit book params. Change Project: bookContent = %s; bookTitle = %s", bookContent.getName(), bookTitle );
                if ( bookTitle != null )
                {
                    bookTitle.reinit();
                    // пнуть дерево
                    Par.GM.getFrame().getCurrentProjectPanel().rewrite();
                }
            }
        }

        //Log.l.debug ( "Finish" );
    }

}
