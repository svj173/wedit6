package svj.wedit.v6.function.service.search;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.function.SimpleFunction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Акция двойного клика в дереве результатов поиска.
 * <BR/> Необходимо перейти на соответствующую страницу (если не была открыта - открыть), передвинуть курсор к найденному слову,
 * <BR/> Найденное слово подсвечивается светло-зеленым.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 20.12.2013 14:58
 */
public class SearchDoubleClickFunction extends SimpleFunction
{
    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        JTree       tree;
        Object      obj;
        TreeObj     treeObj;
        SearchObj   so;
        BookNode    bookNode;
        String      nodeId, text, searchText;
        boolean     hasOpen;
        BookContent bookContent;
        TextPanel   textPanel;
        JTextPane   textPane;
        int         start, end;

        if ( event == null ) return;
        if ( event.getSource() == null ) return;
        if ( !(event.getSource() instanceof JTree ) ) return;


        try
        {
            tree    = (JTree) event.getSource();
            obj     = tree.getLastSelectedPathComponent();
            if ( (obj == null) || !(obj instanceof TreeObj) ) return;

            treeObj = (TreeObj) obj;
            obj     = treeObj.getUserObject();
            if ( obj == null ) return;
            if ( !(obj instanceof SearchObj) ) return;

            so = (SearchObj) obj;
            if ( so.getBookNode() == null )  return;   // выходим т.к. это - узел дерева, а не конечный элемент поиска.

            bookNode    = so.getBookNode();
            nodeId      = bookNode.getId();
            Log.l.debug ( "nodeId = ", nodeId );

            bookContent = bookNode.getBookContent();

            // Перейти на обьект bookNode. Если он не открыт - открыть.
            // - Определить - может такой Сборник уже загружен и открыт
            hasOpen = Par.GM.containNode ( nodeId, bookContent );
            Log.l.debug ( "hasOpen = ", hasOpen );
            if ( hasOpen )
            {
                // уже есть открытый - сделать текущим выбранным
                Par.GM.selectNode ( nodeId, bookContent );
            }
            else
            {
                Par.GM.addBookText ( bookNode, bookContent, 0 );
            }

            // Найти в нем требуемый текст, выделить, перевести на него курсор.
            // - Взять текущий текст-panel
            textPanel   = Par.GM.getFrame().getCurrentTextPanel();
            // - Найти в нем искомую фразу. Запомнить позиции начала и конца.
            textPane    = textPanel.getTextPane ();
            text        = textPane.getText();
            //Log.l.debug ( "--- current text : \n%s\n", text );
            searchText  = so.getSearchText();

            start       = search ( text, searchText, so.getNumber() );

            //Log.l.debug ( "--- searchText = '%s'; start = %d", searchText, start );
            if ( start >= 0 )
            {
                end = start + searchText.length();
                // - Переместить курсор на начало выделенного текста. Передвинуть скроллинг - если надо.
                textPanel.setCurrentCursor ( start );
                // - Выделить данную позицию
                textPane.select ( start, end );
                //textPanel.set
                //textPane.repaint();

                // изменяем цвет текста выделения
                textPane.setSelectedTextColor ( Color.WHITE );
                // изменяем цвет фона выделения
                textPane.setSelectionColor ( Color.GREEN );
            }

        } catch ( WEditException we )         {
            throw we;
        } catch ( Exception e )         {
            Log.l.error ( "error", e );
            throw new WEditException ( e, "Ошибка перехода на страницу с найденным текстом :\n", e );
        }
    }

    /**
     *  Найти текст.
     * @param text         где ищем.
     * @param searchText   что ищем.
     * @param number       номер поиска текста - вдруг еще здесь встречается?
     * @return             Номер найденной позиции текста, во всем тексте. Годится для применения курсора в редакторе текстов.
     */
    private int search ( String text, String searchText, int number )
    {
        int start;

        if ( number > 0 )
        {
            start = 0;
            // проделать заданное число поисков
            for ( int i = 0; i<=number; i++ )
            {
                start  = text.indexOf ( searchText, start );
                start++;   // сдвигает поиск вперед
            }
            start--;       // возвращаем правильную позицию
        }
        else
        {
            start = text.indexOf ( searchText );    // находит правильно
        }

        return start;
    }

    @Override
    public void rewrite ()    { }

}
