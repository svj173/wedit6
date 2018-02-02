package svj.wedit.v6.function.text;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.function.SimpleFunction;

import java.awt.event.ActionEvent;


/**
 * Открыть текст для редактирования.
 * <BR/> Полный текст должен находится в обьекте BookContent.
 * <BR/> Если на момент открытия там пусто - подгружаем.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 18.10.2011 15:07:41
 */
public class OpenTextFunction  extends SimpleFunction
{
    public OpenTextFunction ()
    {
        setId ( FunctionId.OPEN_TEXT );
        setName ( "Открыть текст" );
        setIconFileName ( "open.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        TreePanel<BookContent>  currentBookPanel;
        TreeObj                 selectNode;
        BookContent             bookContent;
        Object                  wObj;
        BookNode                bookNode;
        String                  nodeId;
        boolean                 hasOpen;

        Log.l.debug ( "Start" );

        // Взять текущую книгу - TreePanel
        currentBookPanel = Par.GM.getFrame().getCurrentBookContentPanel();
        if ( currentBookPanel == null )
            throw new WEditException ( "Не выбрана книга." );

        bookContent = currentBookPanel.getObject();

        // Взять выбранный элемент книги - тот, который хотим открыть
        selectNode  = currentBookPanel.getCurrentObj();
        Log.l.debug ( "selectNode = %s", selectNode );

        wObj        = selectNode.getWTreeObj();     // BookNode
        Log.l.debug ( "wObj = %s", wObj );

        bookNode    = (BookNode) wObj;
        nodeId      = bookNode.getId();
        Log.l.debug ( "nodeId = %s", nodeId );

        // Определить - может такой Сборник уже загружен и открыт
        hasOpen = Par.GM.containNode ( nodeId, bookContent );
        Log.l.debug ( "hasOpen = %s", hasOpen );
        if ( hasOpen )
        {
            // уже есть открытый - сделать текущим выбранный
            Par.GM.selectNode ( nodeId, bookContent );
        }
        else
        {
            Par.GM.addBookText ( bookNode, bookContent, 0 );
        }

        Log.l.debug ( "Finish" );
    }

    @Override
    public void rewrite ()    { }

}
