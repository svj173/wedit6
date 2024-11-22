package svj.wedit.v6.function.book.edit;


import svj.wedit.v6.Par;
import svj.wedit.v6.gui.menu.WEMenuItem;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.obj.TreeObj;


/**
 * Контекстное меню с запретом для корня дерева.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 17.08.2011 17:53:04
 */
public class BookElementPopupMenu extends WEMenuItem
{
    private TreeObj obj;

    /* Допустимо ли использование корня. */
    private boolean canRoot;


    public BookElementPopupMenu ( Function function, boolean canRoot )
    {
        super ( function.getId().toString(), function.getIcon ( Par.MENU_ICON_SIZE ) );

        this.canRoot = canRoot;

        addActionListener ( function );
        setText ( function.getName() );
    }

    @Override
    public void init ( TreeObj obj )
    {
        this.obj = obj;

        if ( obj.getLevel() == 0 )
        {
            if ( canRoot)
                setEnabled ( true );
            else
                setEnabled ( false );
        }
    }

    @Override
    public TreeObj getObj ()
    {
        return obj;
    }

}
