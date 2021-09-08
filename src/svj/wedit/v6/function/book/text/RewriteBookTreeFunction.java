package svj.wedit.v6.function.book.text;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.obj.function.Function;

import java.awt.event.ActionEvent;


/**
 * Обновить дерево книги.
 * <BR/> Без изменений и перечитки из файла.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 08.09.2021 17:21:07
 */
public class RewriteBookTreeFunction extends Function
{
    public RewriteBookTreeFunction()
    {
        setId ( FunctionId.REWRITE_BOOK_TREE );
        setName ( "Обновить дерево книги");
        setIconFileName ( "reload.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        Par.GM.getFrame().getCurrentBookContentPanel().rewrite();
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
        return "Обновить дерево книги";
    }

}
