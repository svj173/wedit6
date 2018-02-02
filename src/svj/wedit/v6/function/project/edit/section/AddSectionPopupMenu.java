package svj.wedit.v6.function.project.edit.section;


import svj.wedit.v6.Par;
import svj.wedit.v6.gui.menu.WEMenuItem;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.obj.Section;
import svj.wedit.v6.obj.TreeObj;


/**
 * Добавить новый раздел после выбранного. Запрет для:
 * <BR/> - Корня
 * <BR/> - Книг
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 17.08.2011 17:53:04
 */
public class AddSectionPopupMenu  extends WEMenuItem
{
    private TreeObj obj;

    /* Допустимо ли использование корня (всегда Раздел). */
    private boolean canRoot;

    public AddSectionPopupMenu ( Function function, boolean canRoot )
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
        else
        {
            // проверка на раздел
            if ( obj.getWTreeObj() instanceof Section )
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
