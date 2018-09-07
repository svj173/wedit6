package svj.wedit.v6.gui.widget;


import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Vector;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 21.08.2011 17:21:11
 */
public class ComboBoxWidget<T>  extends AbstractWidget<T>
{
    private final JComboBox comboBox;
    private int         startIndex;
    //private T           oldValue;
    /* Обьект, отвечающий за данный виджет. Например: CpeFlagInfo, тогда как в выпадашке занесены состояния флага EnableEnum. */
    //private Object      object;


    public ComboBoxWidget ( String titleName, boolean hasEmpty, String emptyValue, T[] values )
    {
        super ( titleName, hasEmpty, emptyValue );
        comboBox = new JComboBox(values);
        //comboBox.setEditable ( false );
        //comboBox.setEnabled ( true );
		//comboBox.setFont( GCons.AlertJornalFilterFont );
        init ( hasEmpty, emptyValue );
        //if ( hasEmpty ) comboBox.insertItemAt ( emptyValue, 0 );
        //add ( comboBox );
        //if ( comboBox.getItemCount() > 0 )   comboBox.setSelectedIndex ( 0 );
    }

    public ComboBoxWidget ( String titleName, boolean hasEmpty, String emptyValue,  Collection<T> values )
    {
        super ( titleName, hasEmpty, emptyValue );
        comboBox = new JComboBox ( new Vector<T>(values) );
        init ( hasEmpty, emptyValue );
    }

    public ComboBoxWidget ( String titleName, Collection<T> values )
    {
        this ( titleName, false, null, values );
    }

    public ComboBoxWidget ( String titleName, T[] values )
    {
        this ( titleName, false, null, values );
    }

    private void init ( boolean hasEmpty, String emptyValue )
    {
        if ( hasEmpty ) comboBox.insertItemAt ( emptyValue, 0 );
        add ( comboBox );
        if ( comboBox.getItemCount() > 0 )   comboBox.setSelectedIndex ( 0 );
    }


    @Override
    public JComponent getGuiComponent ()
    {
        return comboBox;
    }

    public int  getSelectedIndex ()
    {
        return comboBox.getSelectedIndex();
    }

    @Override
    protected T validateValue () throws WEditException
    {
        return getValue ();
    }

    /**
     * Выдать содержимое выпадашки, но не брать обьект, олицетворяющий пустое значение.
     * @return  Список обьектов выпадашки без нулевого обьекта.
     */
    public Collection<T> getValues ()
    {
        Collection<T> result;
        //Object item;

        result = new LinkedList<T>();
        for (int i=0; i<comboBox.getItemCount(); ++i)
        {
            if ( (i == 0 ) && hasEmpty() ) continue;
            result.add ( (T) comboBox.getItemAt(i) );   // Почему-то обьект не класса Т (например, строка) все равно кидает в массив и не ругается.
            //item    = comboBox.getItemAt(i);
            //if ( (item != null) && (item instanceof T) ) result.add();
        }
        return result;
    }

    public boolean isEmpty()
    {
        return !(comboBox!=null && comboBox.getItemCount()>0);
    }

    @Override
    public T getValue ()
    {
        if ( hasEmpty() && (getSelectedIndex() == 0 ) )
            return null;   // пустышку возвращаем как null
        else
            return (T) comboBox.getSelectedItem();
    }

    @Override
    public void setValue ( T value )
    {
        if ( value == null )
        {
            // Если NULL - ничего не выбираем
            //comboBox.setSelectedIndex ( 0 );
        }
        else
        {
            //Logger.getInstance().debug ( "ComboBoxWidget.setValue: set selected value = ", value );
            comboBox.setSelectedItem ( value );
            startIndex  = comboBox.getSelectedIndex();
        }
    }

    public void setValues ( Collection<T> list )
    {
        ActionListener[] actions;

        // Сохраняем акции
        actions = comboBox.getActionListeners();
        // Очищаем акции - чтобы при изменении списка не дергались
        for ( ActionListener action : actions )
            comboBox.removeActionListener ( action );

        // очищаем список
        comboBox.removeAllItems();

        // заносим пустышку - если необходимо
        if ( hasEmpty() ) comboBox.insertItemAt ( getEmptyValue(), 0 );
        
        // заносим новые значения
        for ( T item : list )  comboBox.addItem ( item );

        // возвращаем назад акции
        for ( ActionListener action : actions )
            comboBox.addActionListener ( action );
    }

    @Override
    public void setEditable ( boolean value )
    {
        // Нельзя на comboBox вешать setEditable(true) - т.к. это на самом деле позволяет руками править текст строки в выпадашке.
        //comboBox.setEditable ( value );
        comboBox.setEnabled ( value );
        setEnabled ( value );
    }

    //@Override
    public boolean isEditable ()
    {
        return comboBox.isEnabled();
    }

    @Override
    public void setEnabled ( boolean enabled )
    {
        // Нельзя выключать Enabled у предка - это приводит к неверной расстановке компонентов в верхнем лайоуте
        //super.setEnabled ( enabled );
        comboBox.setEnabled ( enabled );
    }


    @Override
    public void setValueWidth ( int width )
    {
        int       height;
        Dimension dim;

        if ( comboBox != null )
        {
            height  = comboBox.getHeight();
            if ( height < WCons.BUTTON_HEIGHT )  height = WCons.BUTTON_HEIGHT;
            dim     = new Dimension ( width, height );
            comboBox.setPreferredSize ( dim );
        }
    }

    public void setValueHeight ( int  height )
    {
        int       width;
        Dimension dim;

        if ( comboBox != null )
        {
            width  = comboBox.getWidth();
            //if ( height < WCons.BUTTON_HEIGHT )  height = WCons.BUTTON_HEIGHT;
            dim     = new Dimension ( width, height );
            comboBox.setPreferredSize ( dim );
        }
    }

    public int getStartIndex ()
    {
        return startIndex;
    }

    public void setStartIndex ( int number ) throws WEditException
    {
        T obj;
        startIndex  = number;
        setIndex ( number );
        obj         = getValue();
        if ( obj != null )  setStartValue ( obj );
    }

    public void setIndex ( int number )
    {
        if ( number < 0 ) return;

        int size = comboBox.getItemCount();
        if ( (size > 0) && (number < size) )
            comboBox.setSelectedIndex ( number );
        //else  comboBox.setSelectedItem ( 0 );      // проверка на пустоту
    }

    public void initAction ( ActionListener listener, String cmd )
    {
        comboBox.addActionListener ( listener );
        comboBox.setActionCommand ( cmd );
    }

    public void setComboRenderer ( ListCellRenderer renderer )
    {
        comboBox.setRenderer ( renderer );
    }

    public void setMaximumRowCount(int length) {
        comboBox.setMaximumRowCount ( length );
    }
    
}
