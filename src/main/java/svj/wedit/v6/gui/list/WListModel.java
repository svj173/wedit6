package svj.wedit.v6.gui.list;


import svj.wedit.v6.logger.Log;

import javax.swing.*;
import java.util.Collection;
import java.util.Vector;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.08.2011 22:05:39
 */
public class WListModel<T> extends AbstractListModel
{
    private Vector<T> list = new Vector<T> ();

    @Override
    public int getSize ()
    {
        return list.size();
    }

    @Override
    public T getElementAt ( int index )
    {
        T result = null;

        if ( index < 0 ) return result;
        if ( index >= list.size() ) return result;

        if ( list != null )
        {
            try {
                result = list.get(index);
            } catch (Exception e) {
                Log.l.error ( "EltexListModel.getElementAt: get index '"+index+"' error. List size = " + list.size(), e );
                result = null;
            }
        }
        return result;
    }

    public void setList ( Collection<T> newList )
    {
        list.clear ();
        list.addAll ( newList );

        // подергать все системные листенеры модели - иначе наши изменения не отобразятся на экране
		int listSize = list.size()-1;
		if (listSize < 0) listSize = 0; // avp: дополнительная проверка, иначе на пустой список получаем Exception
        fireIntervalAdded ( this, 0, listSize );
    }

    public void addList ( Collection<T> newList )
    {
        int ic;

        if ( newList != null )   list.addAll ( newList );

        // доп проверка, т.к. к пустому списку может прийти еще пустой список.
        ic = list.size() - 1;
        if ( ic < 0 ) ic = 0;
        // подергать все системные листенеры модели - иначе наши изменения не отобразятся на экране
        // - либо не с 0 по последний а с последнего по последний?
        fireIntervalAdded ( this, 0, ic );
    }

    public void addItem ( T item )
    {
        if ( item != null )
        {
            list.add ( item );

            // подергать все системные листенеры модели - иначе наши изменения не отобразятся на экране
            // - либо не с 0 по последний а с последнего по последний?
            fireIntervalAdded ( this, 0, list.size()-1 );
        }
    }

    public void insertItem ( T item, int index )
    {
        if ( item != null )
        {
            if ( list.size() < index )
                list.insertElementAt ( item, index );
            else
                list.add ( item );    // добавить в конец.

            // подергать все системные листенеры модели - иначе наши изменения не отобразятся на экране
            // - либо не с 0 по последний а с последнего по последний?
            fireIntervalAdded ( this, 0, list.size()-1 );
        }
    }


    public void removeAllElements ()
    {
        list.clear ();
    }

    public void removeElement ( T item )
    {
        int ic;
        list.remove ( item );
        ic = list.size() - 1;
        if ( ic < 0 ) ic = 0;
        fireIntervalRemoved ( this, 0, ic );
    }

    public void removeElements ( Collection<T> items )
    {
        int ic;
        list.removeAll ( items );
        ic = list.size() - 1;
        if ( ic < 0 ) ic = 0;
        fireIntervalRemoved ( this, 0, ic );
    }

    public Vector<T> getObjectList ()
    {
        return list;
    }

}
