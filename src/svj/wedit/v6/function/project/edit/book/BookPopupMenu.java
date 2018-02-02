package svj.wedit.v6.function.project.edit.book;


import svj.wedit.v6.Par;
import svj.wedit.v6.gui.menu.WEMenuItem;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.obj.book.BookTitle;
import svj.wedit.v6.obj.TreeObj;


/**
 * Операции с книгами. Запрет для:
 * <BR/> - Разделов
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 17.08.2011 17:53:04
 */
public class BookPopupMenu extends WEMenuItem
{
    private TreeObj obj;

    public BookPopupMenu ( Function function )
    {
        super ( function.getId().toString(), function.getIcon ( Par.MENU_ICON_SIZE ) );

        addActionListener ( function );
        setText ( function.getName() );
    }

    @Override
    public void init ( TreeObj obj )
    {
        this.obj = obj;

        // проверка на раздел
        if ( obj.getWTreeObj() instanceof BookTitle )
            setEnabled ( true );
        else
            setEnabled ( false );
    }

    @Override
    public TreeObj getObj ()
    {
        return obj;
    }

}
