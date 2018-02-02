package svj.wedit.v6.function.book.edit.desc;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookStructure;
import svj.wedit.v6.obj.function.SimpleFunction;

import java.awt.event.ActionEvent;


/**
 * Редактировать описание структуры книги.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 22.08.2011 21:10:23
 */
public class EditDescElementsFunction extends SimpleFunction
{
    public EditDescElementsFunction ()
    {
        setId ( FunctionId.EDIT_DESC_ALL_ELEMENTS );
        setName ( "Редактировать описание структуры книги");
        setIconFileName ( "edit.png" );
    }


    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        TreePanel<BookContent>  currentBookContentPanel;
        BookEditElementsDialog  dialog;
        BookContent             bookContent;
        BookStructure           bookStructure;

        Log.l.debug ( "Start" );

        try
        {
            // Взять текущую книгу - TreePanel
            currentBookContentPanel = Par.GM.getFrame().getCurrentBookContentPanel();
            if ( currentBookContentPanel == null )
                throw new WEditException ( "Не выбрана книга для редактирования описания ее структуры." );

            bookContent  = currentBookContentPanel.getObject();

            // Диалог - Запросить разрешение
            dialog  = new BookEditElementsDialog ( "Редактировать описание структуры книги '"+bookContent.getName()+"'", bookContent );
            dialog.showDialog();

            if ( dialog.isOK() )
            {
                // Принять изменения - сложный обьект, содержащий в себе описания элементов, описания типов элементов, атрибуты.
                bookStructure = dialog.getResult();
                Log.l.debug ( "--- new book structure = %s", bookStructure );
                //bookContent.getBookStructure().setElements ( bookElements );
                bookContent.setBookStructure ( bookStructure );
                // отмечаем что было изменение, иначе книга не сохранится.
                bookContent.setEdit ( true );
            }

        } catch ( WEditException we )        {
            throw we;
        } catch ( Exception e )        {
            Log.l.error ( "err", e );
            throw new WEditException ( e, "Системная ошибка редактирования структуры книги :\n", e );
        }

        Log.l.debug ( "Finish" );
    }

    @Override
    public void rewrite ()
    {
    }

}
