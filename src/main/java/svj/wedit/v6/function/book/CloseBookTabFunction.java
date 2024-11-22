package svj.wedit.v6.function.book;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.tabs.TabsPanel;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.FileTools;

import java.awt.event.ActionEvent;


/**
 * Закрыть текущую открытую книгу - по крестику на табике, либо по сочетанию клавиш Ctrl/W -- НЕТ. иначе запутаемся с закрытием текстов, книг, сборников.
 * <BR/> Безусловно скидываем текст из дерева в файл книги. -- НЕТ
 * <BR/> Функция не заносится  в пул функций и применяется локально, в акции закрытия табика.
 * <BR/>
 * <BR/> При закрытии табика книги необходимо:
 * <BR/> - взять все открытые тексты этой книги и скинуть в них тексты в обьекты
 * <BR/> - удалить табики текстов: из рабочего массива.
 * <BR/> - удалить панель табиков текстов из cardLayout
 * <BR/> - закрыть таб-панель книги
 * <BR/> - сказать обьекту книги что было редактирование (безусловно, либо анализом состояний, либо не заморачиваться на флаг Редактирования и сохранять всех и всегда)
 * <BR/>
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 31.01.2013 11:07:41
 */
public class CloseBookTabFunction extends Function
{
    private TreePanel<BookContent> bookPanel;

    public CloseBookTabFunction ( TreePanel<BookContent> bookPanel )
    {
        setId ( FunctionId.CLOSE_BOOK );
        setName ( "Закрыть книгу" );
        setIconFileName ( "close_red.png" );
        this.bookPanel = bookPanel;
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        TabsPanel<TreePanel<BookContent>>    tabsBookPanel;
        //TabsPanel<TextPanel>    tabsTextPanel;
        //Collection<TextPanel>   textPanels;

        Log.l.debug ( "Start" );

        // Взять тот табик, которому принадлежит крест - bookPanel

        // Взять ту табс-панель с табиками открытых книг, которой принадлежит данный табик - bookPanel
        tabsBookPanel           = Par.GM.getFrame().getCurrentBookTabsPanel();

        closeBookPanel ( bookPanel, true );

        // - закрыть таб-панель книги
        // -- Возможно здесь bookPanel.getId() совпадает с ИД, прописанном в мапе панелей.
        tabsBookPanel.removeTab ( bookPanel );   // Закрываем табик.

        // - сказать обьекту книги что было редактирование -- ЕЕ уже нет нигде - табик книги то удалили.
        //bookContent.setEdit ( true );

        Log.l.debug ( "Finish" );
    }

    public void closeBookPanel ( TreePanel<BookContent> bookPanel, boolean needSave )
            throws WEditException
    {
        BookContent bookContent;

        if ( bookPanel != null )
        {
            // Взять книгу -- Это книга того табика, на чей крестик мы нажали.
            bookContent = bookPanel.getObject();

            // Закрываем все тексты, связанные с этой книгой. Там же предварительно скидываем тексты из Редактирования в Обьекты - только у тех кто был изменен.
            Par.GM.getFrame().closeTextsPanel ( bookContent.getId () );

            // Скинуть книгу в файл - если было изменение
            if ( needSave && bookContent.isEdit() )     FileTools.saveBook ( bookContent );
        }
    }

    @Override
    public void rewrite ()
    {
    }

    @Override
    public void init () throws WEditException
    {
    }

    @Override
    public void close ()
    {
    }

    @Override
    public String getToolTipText ()
    {
        return "Закрыть вкладку текущей книги.";
    }

}
