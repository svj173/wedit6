package svj.wedit.v6.gui.panel;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.tools.Utils;

import javax.swing.*;
import java.awt.*;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 29.07.2011 17:14:20
 */
public class WPanel   extends JPanel  implements Comparable<WPanel>
{
    /* Крайние отступы этой панели от других панелей. ( top, left, bottom, right) */
    private Insets insets;

    /* Имя (ru) панели. Особенно для таб-панели. */
    private String title;

    /* ID (en) данной панели. Особенно для card-панели. */
    private String id;

    /* ID (en) родительской панели. Особенно для tabs-панели - для привязки к родительской таб-панели. */
    private String parentId;

    /** Какой-то обьект, связанный с этой панелью. */
    private Object object;



    public WPanel ()
    {
        this ( 0, 0, 0, 0 );
    }

    public WPanel ( int top, int left, int bottom, int right )
    {
        super();
        // (int top, int left, int bottom, int right)
        insets  = new Insets ( top, left, bottom, right );
        title   = null;
        id      = "";
    }

    /**
     * Закрытие панели. Используется при смене одной вкладки на другую, чтобы сообщить предыдущей, что она закрывается.
     * <BR/> Исключения здесь НЕ допустимы.
     */
    public void close () {}


    public void init () throws WEditException {}

    public String getTitle ()
    {
        return title;
    }

    public void setTitle ( String title )
    {
        this.title = title;
    }

    @Override
    public Insets getInsets()
    {
        return insets;
    }

    public void setInsets ( Insets ins )
    {
        insets  = ins;
    }

    public void setInsets ( int top, int left, int bottom, int right )
    {
        insets  = new Insets ( top, left, bottom, right );
    }

    public String getId ()
    {
        return id;
    }

    public void setId ( String id )
    {
        this.id = id;
    }

    public String getParentId ()
    {
        return parentId;
    }

    public void setParentId ( String parentId )
    {
        this.parentId = parentId;
    }

    @Override
    public String toString()
    {
        StringBuilder result;

        result = new StringBuilder ( 128 );
        result.append ( "[ WPanel: Name = '" );
        result.append ( getName() );
        result.append ( "'; id = '" );
        result.append ( getId() );
        result.append ( "'; parentId = '" );
        result.append ( getParentId() );
        result.append ( "'; title = '" );
        result.append ( getTitle() );
        //result.append ( "', insets = " );
        //result.append ( insets );
        result.append ( "' ]" );

        return result.toString();
    }

    @Override
    public int compareTo ( WPanel panel )
    {
        int ic;

        if ( panel == null )
            ic = 1;
        else
        {
            ic = Utils.compareToWithNull ( getId(), panel.getId() );
            //Log.l.debug ( "---- (%s) Panel 1 id = %s; Panel 2 id = %s; compareTo = %d", getName(), getId(), panel.getId(), ic );
        }
        return ic;
    }

    public boolean equals ( Object obj )
    {
        boolean result;

        result = false;
        if ( (obj != null) && (obj instanceof WPanel) )
        {
            result = compareTo ( (WPanel) obj ) == 0;
        }
        return result;
    }

    public Object getObject ()
    {
        return object;
    }

    public void setObject ( Object object )
    {
        this.object = object;
    }
}
