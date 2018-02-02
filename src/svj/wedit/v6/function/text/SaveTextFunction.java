package svj.wedit.v6.function.text;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.gui.tabs.TabsPanel;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.obj.book.BookContent;

import java.awt.event.ActionEvent;


/**
 * Скинуть текущий открытый текст в обьект узла дерева.
 * <BR/> Внимание: в файле ничего не сохраняется!
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 01.06.2012 11:07:41
 */
public class SaveTextFunction extends Function
{
    public SaveTextFunction ()
    {
        setId ( FunctionId.SAVE_TEXT );
        setName ( "Сохранить текст" );
        setIconFileName ( "paste.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        TextPanel               textPanel;
        TabsPanel<TextPanel>    tabsPanel;
        TreePanel<BookContent>  treeBookPanel;

        Log.l.debug ( "Start" );

        // Взять текущую книгу
        treeBookPanel   = Par.GM.getFrame().getCurrentBookContentPanel();
        if ( treeBookPanel == null )
            throw new WEditException ( "Не выбрана книга." );

        // Взять текущий текст - TextPanel
        tabsPanel   = Par.GM.getFrame().getTextTabsPanel();
        textPanel   = tabsPanel.getSelectedComponent();
        if ( textPanel == null )
            throw new WEditException ( "Не выбран текст." );

        // Сохраняем -- TextToBookNode - process ( TextPanel textPanel, BookContent bookContent )
        textPanel.saveTextToNode();

        // перерисовываем дерево книги
        treeBookPanel.rewrite();

        Log.l.debug ( "Finish" );
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
        return "Скинуть в обьект текущий текст. Без сохранения в файле.";
    }

}
