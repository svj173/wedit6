package svj.wedit.v6.function.book.edit.desc;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.list.WListPanel;
import svj.wedit.v6.gui.listener.WActionListener;
import svj.wedit.v6.obj.book.element.WBookElement;
import svj.wedit.v6.tools.DialogTools;

import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Работы по изменению списка элементов в диалоге редактирования элементов книги.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 13.12.2013 17:01
 */
public class ProcessElementListListener extends WActionListener
{
    private WListPanel<WBookElement> elementsList;
    private int minLevel;
    private WBookElement    copyElement;


    public ProcessElementListListener ( WListPanel<WBookElement> elementsList, int minLevel )
    {
        super ( ".." );

        this.elementsList   = elementsList;
        this.minLevel       = minLevel;

        copyElement         = null;
    }

    @Override
    public void handleAction ( ActionEvent event ) throws WEditException
    {
        String cmd;

        cmd = event.getActionCommand ();

        if ( cmd.equals ( "ADD" ) )
        {
            handleAdd();
            return;
        }

        if ( cmd.equals ( "DELETE" ) )
        {
            handleDelete ();
            return;
        }

        if ( cmd.equals ( "COPY" ) )
        {
            handleCopy ();
            return;
        }

        if ( cmd.equals ( "PASTE" ) )
        {
            handlePaste ();
        }
    }

    private void handleAdd ()  throws WEditException
    {
        String          elementName;
        WBookElement    bookElement;

        // Вывести диалог по вводу имени элемента
        elementName = DialogTools.showInput ( elementsList, "Добавить элемент", "Введите имя нового элемента" );
        if ( elementName != null )
        {
            // Создать новый элемент
            bookElement = new WBookElement ( -1 );
            bookElement.setName ( elementName );
            // Добавить элемент в список
            elementsList.addItem ( bookElement );
            // Перелопатить весь список чтобы проставить реальные значения уровня.
            changeLevels ( elementsList.getObjectList() );
        }
    }

    private void handleDelete ()  throws WEditException
    {
        int             listSize;
        WBookElement    bookElement;

        // взять текущий элемент
        bookElement = elementsList.getSelectedItem();
        if ( bookElement == null )
            throw new WEditException ( "Не выбран элемент для удаления." );

        // Сначала проверяем - не станет ли размер списка меньше чем реальный размер.
        listSize = elementsList.getListSize();
        if ( (listSize - 1) < minLevel )
            throw new WEditException ( null, "Удалять нельзя!\nКоличество вложенных элементов книги равно '",minLevel,"',\nа новый размер элементов будет ", (listSize - 1), '.' );

        elementsList.deleteItem ( bookElement );
        changeLevels ( elementsList.getObjectList () );
    }

    private void handleCopy ()  throws WEditException
    {
        WBookElement    bookElement;

        // взять текущий элемент
        bookElement = elementsList.getSelectedItem();
        if ( bookElement == null )
            throw new WEditException ( "Не выбран элемент для копирования." );

        copyElement = bookElement.cloneObj();
    }

    private void handlePaste ()  throws WEditException
    {
        int index;

        if ( copyElement == null )
            throw new WEditException ( "Нет скопированного элемента." );

        // Взять номер позиции текущего элемента.
        index       = elementsList.getSelectedIndex();
        elementsList.insertItem ( copyElement, index );
        copyElement = null;
    }

    private void changeLevels ( List<WBookElement> list )
    {
        int ic = 0;
        for ( WBookElement element : list )
        {
            element.setElementLevel ( ic );
            ic++;
        }
    }

}
