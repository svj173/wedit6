package svj.wedit.v6.function.service.search;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.function.SimpleFunction;
import svj.wedit.v6.tools.BookTools;

import javax.swing.*;

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

        if ( event == null ) return;
        if ( event.getSource() == null ) return;
        if ( !(event.getSource() instanceof JTree ) ) return;


        try
        {
            tree    = (JTree) event.getSource();
            obj     = tree.getLastSelectedPathComponent();
            if ( !(obj instanceof TreeObj) ) return;

            treeObj = (TreeObj) obj;
            obj     = treeObj.getUserObject();
            if ( obj == null ) return;
            if ( !(obj instanceof SearchObj) ) return;

            so = (SearchObj) obj;

            BookTools.goToSearchObj(so);

        } catch ( WEditException we )         {
            throw we;
        } catch ( Exception e )         {
            Log.l.error ( "error", e );
            throw new WEditException ( e, "Ошибка перехода на страницу с найденным текстом :\n", e );
        }
    }

    @Override
    public void rewrite ()    { }

}
