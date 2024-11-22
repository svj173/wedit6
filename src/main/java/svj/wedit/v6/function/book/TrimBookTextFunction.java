package svj.wedit.v6.function.book;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.MessageException;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.gui.tabs.TabsPanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.book.TextObject;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.BookTools;
import svj.wedit.v6.tools.DialogTools;

import javax.swing.*;
import java.awt.event.ActionEvent;


/**
 * Для текущей книги Убрать лидирующие пробелы - первые и последние в строках.
 * <BR/>
 * <BR/> Сервис. Функция (trim)
 - удалить последний пробел в строке.
 - удалить первый и последний пробелы в строке.
 =? - удаление - на уровне файла? или для октрытйо книги? И если у книги какие-то главы открыты? - скинуть текст в Книгу - если были изменения.
 Очистить пробелы - перегрузить текст - для всех открытых глав.
 Либо потребовать чтобы закрыли все открытые тексты.

 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 07.10.2015 14:16:52
 */
public class TrimBookTextFunction extends Function
{
    public TrimBookTextFunction ()
    {
        setId ( FunctionId.TRIM_BOOK_TEXT );
        setName ( "Убрать лидирующие пробелы");
        setIconFileName ( "trim_text.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        BookContent             currentBookContent;
        BookNode                bookNode;
        TabsPanel<TextPanel>    tabsPanel;
        BookNode                node;
        int                     ic;

        Log.l.debug ( "Start" );

        // Взять текущую книгу - TreePanel
        currentBookContent  = Par.GM.getFrame().getCurrentBookContent ();
        bookNode            = currentBookContent.getBookNode();

        // Диалог запроса.
        ic = DialogTools.showConfirmDialog ( Par.GM.getFrame(), getName(), getName()+" ?" );
        if ( ic == JOptionPane.YES_OPTION )
        {
            // Выяснить, есть ли для данной книги открытые тексты.
            // - Берем все тексты
            tabsPanel = Par.GM.getFrame().getTextTabsPanel();
            if ( tabsPanel != null )
            {
                for ( TextPanel tp : tabsPanel.getPanels() )
                {
                    node = tp.getBookNode ();
                    // - node открыта в составе bookNode? Если ДА - исключение.
                    try
                    {
                        BookTools.checkContainInNode ( node, bookNode );
                    } catch ( WEditException e )   {
                        // Есть открытые главы. Предлагем их предварительно закрыть.
                        throw new MessageException ( "У книги '", currentBookContent.getName() + "' есть открытые тексты.\nНеобходимо их все закрыть." );
                    }
                }
            }

            // Все ОК. Рекурсивный прогон по текстам.
            trimText ( bookNode );

            // Отметить что было изменение - всегда, т.к. пока нет возможности выяснить, были ли вообще изменения, и каков их обьем.
            currentBookContent.setEdit ( true );
        }

        Log.l.debug ( "Finish" );
    }

    private void trimText ( BookNode bookNode )
    {
        if ( bookNode == null ) return;

        for ( TextObject to : bookNode.getText() )
        {
            to.trim();
        }

        for ( BookNode node : bookNode.getNodes() )
        {
            trimText ( node );
        }
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
        return "Убрать лидирующие пробелы текста - спереди и сзади.";
    }

}
