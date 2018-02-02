package svj.wedit.v6.function.book.edit.desc;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.list.WListPanel;
import svj.wedit.v6.gui.listener.WActionListener;
import svj.wedit.v6.obj.WType;
import svj.wedit.v6.tools.DialogTools;

import java.awt.event.ActionEvent;

/**
 * Работы по изменению списка элементов в диалоге редактирования элементов книги.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 29.08.2014 17:01
 */
public class ProcessTypeListListener extends WActionListener
{
    private WListPanel<WType> typesList;
    private WType copyType;


    public ProcessTypeListListener ( WListPanel<WType> list )
    {
        super ( ".." );

        this.typesList = list;

        copyType = null;
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
        String          typeName;
        WType    type;

        // Вывести диалог по вводу имени элемента
        typeName = DialogTools.showInput ( typesList, "Добавить тип", "Введите имя нового типа" );
        if ( typeName != null )
        {
            // Создать новый элемент
            type = new WType ();
            type.setRuName ( typeName );
            // Добавить элемент в список
            typesList.addItem ( type );
        }
    }

    private void handleDelete ()  throws WEditException
    {
        WType    type;

        // взять текущий элемент
        type = typesList.getSelectedItem();
        if ( type == null )
            throw new WEditException ( "Не выбран тип для удаления." );

        typesList.deleteItem ( type );
    }

    private void handleCopy ()  throws WEditException
    {
        WType    type;

        // взять текущий элемент
        type = typesList.getSelectedItem();
        if ( type == null )
            throw new WEditException ( "Не выбран тип для копирования." );

        copyType = type.cloneObj();
    }

    private void handlePaste ()  throws WEditException
    {
        int index;

        if ( copyType == null )
            throw new WEditException ( "Нет скопированного типа." );

        // Взять номер позиции текущего элемента.
        index       = typesList.getSelectedIndex();
        typesList.insertItem ( copyType, index );
        copyType = null;
    }

}
