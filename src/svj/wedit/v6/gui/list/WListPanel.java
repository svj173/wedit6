package svj.wedit.v6.gui.list;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.WComponent;
import svj.wedit.v6.gui.panel.WPanel;
import svj.wedit.v6.gui.renderer.INameRenderer;
import svj.wedit.v6.logger.Log;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;


/**
 * Универсальная панель для работы со списками объектов.
 * <BR/> Работает с любыми объектами.
 * <BR/> Режим селекта - одиночный.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.08.2011 22:03:57
 */
public class WListPanel <T>   extends WPanel implements WComponent
{
    private JList               objList;
    private WListModel<T>       model;
    private JScrollPane         listScrollPane;
    private ListListener        listListener;


    /**
     * Панель работы со списками обьектов.
     * @param name             Имя панели.
     * @param actionEvent      Событие, которое применяет actionListener при дергании.
     * @param actionListener   Акция, которая дернется при смене обьекта в списке.
     */
    public  WListPanel ( String name, ActionEvent actionEvent, ActionListener actionListener )
    {
        super ();

        setLayout ( new BorderLayout ( 5,5 ) );

        listListener = new ListListener ( name, actionEvent, actionListener );

        objList = new JList();
        objList.setBackground ( new Color(212, 208, 200) );
        objList.setBorder ( new LineBorder ( new Color ( 102, 204, 0 ), 1, true ) );
        objList.setFont ( new Font ( "Monospaced", 1, 12 ) );
        objList.setSelectionMode ( ListSelectionModel.SINGLE_SELECTION );
        objList.setToolTipText ( "Список" );
        objList.setCellRenderer ( new INameRenderer() );
        objList.addListSelectionListener ( listListener );

        model = new WListModel<T>();
        objList.setModel(model);

        listScrollPane = new JScrollPane();
        listScrollPane.setViewportView ( objList );

        // задаем мин ширину - иначе поле списка будет ужиматься по ширине по макс названию.
        setPreferredSize ( new Dimension ( 166, 150 ) );

        add ( listScrollPane, BorderLayout.CENTER );
    }

    public void addItem ( T item )
    {
        try
        {
            if ( listListener != null ) listListener.setDisableAction();
            model.addItem ( item );
        } catch ( Exception e )         {
            Log.l.error ( "EltexListPanel.addItem: err", e );
            //throw new EltexException ("Системная ошибка установки нового списка объектов : " + e, e );
            //model.setList ( Collections.<T>emptyList() );
            //Par.GM.setStatus ( "Ошибка добавления элемента в список : ", e.toString() );
        } finally {
            if ( listListener != null ) listListener.setEnableAction();
        }
    }

    public void insertItem ( T item, int index )
    {
        try
        {
            if ( listListener != null ) listListener.setDisableAction();
            model.insertItem ( item, index );
        } catch ( Exception e )         {
            Log.l.error ( "EltexListPanel.insertItem: err", e );
            //throw new EltexException ("Системная ошибка установки нового списка объектов : " + e, e );
            //model.setList ( Collections.<T>emptyList() );
            //Par.GM.setStatus ( "Ошибка добавления элемента в список : ", e.toString() );
        } finally {
            if ( listListener != null ) listListener.setEnableAction();
        }
    }

    public void beforeOpenPanel () throws WEditException
    {
        Log.l.debug ( "Start" );
        // очистить лист от старых значений  -- надо ли?
        // - нет акции
        // - при смене - есть акция, которая отваливается по currentNte = -1
        model.removeAllElements();
        Log.l.debug ( "Finish" );
    }

    @Override
    public void rewrite ()
    {
        Log.l.debug ( "Start. list size = ", getListSize () );
        Log.l.debug ( "current index = ", objList.getSelectedIndex () );

        // перерисовать список

        objList.repaint();
        listScrollPane.revalidate ();
        repaint ();

        Log.l.debug ( "Finish" );
    }

    public int getListSize ()
    {
        return model.getSize();
    }

    public java.util.List<T> getObjectList ()
    {
        return model.getObjectList();
    }

    public JList getJList ()
    {
        return objList;
    }

    public WListModel<T> getModel ()
    {
        return model;
    }

    public T getSelectedItem ()
    {
        T                   result;
        ListModel           listModel;
        WListModel<T>   eltexModel;

        result      = null;
        listModel   = objList.getModel();

        if ( listModel instanceof WListModel )
        {
            eltexModel  = (WListModel<T>) listModel;
            result      = eltexModel.getElementAt ( objList.getSelectedIndex() );
        }

        return result;
    }

    public void setSelectedItem ( T item )
    {
        int ic;

        if ( item != null )
        {
            objList.setSelectedValue ( item, true );
        }
        else
        {
            // Если есть в списке данные - выбрать первый
            ic  = model.getSize();
            if ( ic > 0 ) objList.setSelectedIndex ( 0 );
        }
    }

    // -1 если ничего не выбрано
    public int getSelectedIndex ()
    {
        return objList.getSelectedIndex();
    }

    public void setSelectedIndex ( int index )
    {
        int ic;

        ic  = model.getSize();
        if ( ic > index ) objList.setSelectedIndex ( index );
    }


    public String getParameterValue ()
    {
        T obj;

        //Log.l.debug ( "EltexListPanel.getParameter: current index = " + objList.getSelectedIndex() );

        obj = getSelectedItem();
        if ( obj == null )
            return null;
        else
            return obj.toString();
    }

    public void setList ( Collection<T> list )
    {
        try
        {
            if ( listListener != null ) listListener.setDisableAction();
            model.setList ( list );
        } catch ( Exception e )         {
            Log.l.error ( "err", e );
            //throw new WEditException ("Системная ошибка установки нового списка объектов : " + e, e );
        } finally {
            if ( listListener != null ) listListener.setEnableAction();
        }
    }

    public void setCellRenderer ( ListCellRenderer renderer )
    {
        objList.setCellRenderer ( renderer );
    }

    public void deleteItem ( T item )
    {
        try
        {
            if ( listListener != null ) listListener.setDisableAction();
            model.removeElement ( item );
        } catch ( Exception e )         {
            Log.l.error ( "deleteItem: error. item = "+item, e );
            //throw new EltexException ("Системная ошибка установки нового списка объектов : " + e, e );
            //model.setList ( Collections.<T>emptyList() );
            //Par.GM.setStatus ( "Ошибка удаления элемента из списка : ", e.toString() );
        } finally {
            if ( listListener != null ) listListener.setEnableAction();
        }
    }

    public void deleteItems ( Collection<T> items )
    {
        try
        {
            if ( listListener != null ) listListener.setDisableAction();
            model.removeElements ( items );
        } catch ( Exception e )         {
            Log.l.error ( "deleteItems: error. items = "+items, e );
            //throw new EltexException ("Системная ошибка установки нового списка объектов : " + e, e );
            //model.setList ( Collections.<T>emptyList() );
            //Par.GM.setStatus ( "Ошибка удаления элемента из списка : ", e.toString() );
        } finally {
            if ( listListener != null ) listListener.setEnableAction();
        }
    }

}
