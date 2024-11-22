package svj.wedit.v6.function.book.edit.desc;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.listener.WActionListener;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.WType;

import java.awt.event.ActionEvent;

/**
 * Смена типа элемента на списке типов  в диалоге редактирования типов элементов книги (hidden, work...).
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 29.08.2014 16:45
 */
public class ChangeTypeEditListener extends WActionListener
{
    private TypePanel typePanel;


    public ChangeTypeEditListener ( TypePanel typePanel )
    {
        super ( ".." );

        this.typePanel = typePanel;
    }

    @Override
    public void handleAction ( ActionEvent event ) throws WEditException
    {
        BookEditElementsDialog  dialog;
        WType type;

        Log.l.debug ( "--- source book element = %s", typePanel.getObj() );

        // Скинуть значения виджетов в старый элемент.
        typePanel.fromWidgetsToElement();
        Log.l.debug ( "--- book element after widget = %s", typePanel.getObj() );

        // Взять новый элемент - из сорца события
        dialog  = (BookEditElementsDialog) event.getSource();
        type = dialog.getCurrentType();
        Log.l.debug ( "--- new book element = %s", type );

        // Занести его в панель. Скинуть значения элемента в виджеты панели.
        typePanel.init ( type );
    }

    public void close ()
    {
        // Скинуть значения виджетов в старый элемент.
        typePanel.fromWidgetsToElement();
    }

}
