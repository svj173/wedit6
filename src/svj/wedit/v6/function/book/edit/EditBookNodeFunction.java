package svj.wedit.v6.function.book.edit;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.MessageException;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.BookTools;

import java.awt.event.ActionEvent;


/**
 * Редактировать элемент книги.
 * <BR/> Файл здесь не перезаписывается. Т.е. можно откатывать (undo-redo на дереве содержимого книги.)
 * <BR/>
 * <BR/> Вводим: имя, аннотацию, тип.
 * <BR/>
 * <BR/> Допустимость использования корневого элемента: ДА.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 20.08.2011 15:16:52
 */
public class EditBookNodeFunction extends Function
{
    public EditBookNodeFunction ()
    {
        setId ( FunctionId.EDIT_ELEMENT );
        //setName ( "Редактировать атрибуты элемента книги");
        setName ( "Свойства");
        setIconFileName ( "edit.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        TreePanel<BookContent>  currentBookContentPanel;
        TreeObj                 selectNode;
        BookNodeDialog          dialog;
        BookNode                bookNode, editNode;

        Log.l.debug ( "Start" );

        // Взять текущую книгу - TreePanel
        currentBookContentPanel = Par.GM.getFrame().getCurrentBookContentPanel();

        // Взять отмеченный элемент книги.
        selectNode  = currentBookContentPanel.getCurrentObj();
        bookNode    = (BookNode) selectNode.getUserObject();

        // Диалог
        if ( bookNode.getParentNode() == null )
        {
            // - Выбрана книга. Редактируем атрибуты книги
            Function function = Par.GM.getFm().get ( FunctionId.EDIT_BOOK_PARAMS );
            if ( function != null )
                function.handle ( event );
            else
                throw new MessageException ( "Запрещено редактировать корневой элемент\n (Отсутствует редактор атрибутов книги)." );
        }
        else
        {
            // - Редактируем атрибуты элемента книги: Название, тип, аннотация.

            // Проверить, может выбранный узел (сам или в составе вышестоящего узла) уже открыт в текстах,
            //   и тогда изменять/добавлять/удалять в него ничего нельзя - т.к. это присутствует на экране.
            BookTools.checkOpenText ( bookNode );

            // Диалог редактирования атрибутов элемента книги.
            //dialog  = new BookNodeDialog ( "Редактируем параметры '"+bookNode.getName()+"'", bookNode.getParentNode() );
            dialog  = new BookNodeDialog ( getName() + " '"+bookNode.getName()+"'.", bookNode.getParentNode() );
            dialog.init ( bookNode );
            dialog.showDialog();
            if ( dialog.isOK() )
            {
                editNode    = dialog.getResult();
                bookNode.merge ( editNode );

                // Отметить что было изменение
                currentBookContentPanel.setEdit ( true );
                currentBookContentPanel.getObject().setEdit ( true ); // BookContent - т.к. через него флаг рисуется.
            }
        }

        Log.l.debug ( "Finish" );
    }

    @Override
    public void rewrite ()      {    }

    @Override
    public void init () throws WEditException    {    }

    @Override
    public void close ()    {    }

    @Override
    public String getToolTipText ()
    {
        return "Редактировать параметры элемента книги : наименование, аннотация.";
    }

}
