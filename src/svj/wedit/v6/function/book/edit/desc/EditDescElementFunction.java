package svj.wedit.v6.function.book.edit.desc;


import svj.wedit.v6.exception.MessageException;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.obj.function.SimpleFunction;

import java.awt.event.ActionEvent;


/**
 * Редактировать описание элемента.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 22.08.2011 21:10:23
 */
public class EditDescElementFunction extends SimpleFunction
{
    public EditDescElementFunction ()
    {
        setId ( FunctionId.EDIT_DESC_ELEMENT );
        setName ( "Редактировать описание элемента");
        setIconFileName ( "edit.png" );
    }


    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        /*
        int                     level;
        TreePanel<BookContent>  currentBookContentPanel;
        TreeObj                 selectNode;
        BookElementDialog       dialog;
        BookContent             bookContent;
        WBookElement bookElement, bookElement2;

        Log.l.debug ( "Start" );

        // Взять текущую книгу - TreePanel
        currentBookContentPanel = Par.GM.getFrame().getCurrentBookContentPanel();
        if ( currentBookContentPanel == null )
            throw new WEditException ( "Не выбран элемент книги, \nкоторой требуется редактировать." );

        selectNode  = currentBookContentPanel.getCurrentObj();
        bookContent = currentBookContentPanel.getObject();

        //parentNode  = (BookNode) selectNode.getObject();
        // Взять родителя отмеченного элемента
        //parentNode  = (BookNode) selectNode.getUserObject();

        // Взять уровень выбранного элемента. Проверить - может уже последний?
        level       = selectNode.getLevel();

        // взять описание для уровня - если нет такого - создается.
        bookElement = bookContent.getBookElement ( level );

        // Корень допустим
        //if ( level == 0 )  throw new WEditException ( "Выбран корневой элемент" );


        // Диалог - Запросить имя нового обьекта
        dialog  = new BookElementDialog ( "Редактировать описание Элемента книги", bookElement );
        //dialog.init ( level );
        dialog.showDialog();
        if ( dialog.isOK() )
        {
            bookElement2     = dialog.getResult();
            bookContent.getBookStructure ().set ( level, bookElement2 );

            // Отметить что было изменение
            currentBookContentPanel.setEdit ( true );
        }
        */
        //Log.l.debug ( "Finish" );

        throw new MessageException ( "Не реализовано" );
    }

    @Override
    public void rewrite ()
    {
    }

}
