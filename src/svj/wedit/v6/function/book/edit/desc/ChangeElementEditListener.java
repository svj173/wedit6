package svj.wedit.v6.function.book.edit.desc;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.listener.WActionListener;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.element.WBookElement;

import java.awt.event.ActionEvent;

/**
 * Смена элемента на списке элементов  вдиалоге редактивраония элементво книги.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 13.12.2013 14:45
 */
public class ChangeElementEditListener  extends WActionListener
{
    private BookElementPanel bookElementPanel;


    public ChangeElementEditListener ( BookElementPanel bookElementPanel )
    {
        super ( ".." );

        this.bookElementPanel = bookElementPanel;
    }

    @Override
    public void handleAction ( ActionEvent event ) throws WEditException
    {
        BookEditElementsDialog  dialog;
        WBookElement            element;

        Log.l.debug ( "--- source book element = %s", bookElementPanel.getBookElement() );

        // Скинуть значения виджетов в старый элемент.
        bookElementPanel.fromWidgetsToElement();
        Log.l.debug ( "--- book element after widget = %s", bookElementPanel.getBookElement () );

        // Взять новый элемент - из сорца события
        dialog  = (BookEditElementsDialog) event.getSource();
        element = dialog.getCurrentElement();
        Log.l.debug ( "--- new book element = %s", element );

        // Занести его в панель. Скинуть значения элемента в виджеты панели.
        bookElementPanel.init ( element );
    }

    public void close ()
    {
        // Скинуть значения виджетов в старый элемент.
        bookElementPanel.fromWidgetsToElement();
    }

}
