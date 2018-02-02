package svj.wedit.v6.gui.widget;


import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 09.08.2011 15:02:41
 */
public abstract class AbstractWidget<T>   extends JPanel
{
    private String  titleName;

    /* Флаг, может ли данный виджет принимать пустое значение (NULL - т.е. ничего не выбрано), или нет. */
    private boolean hasEmpty;

    /* Отображение пустого значения - для списков */
    private String  emptyValue;

    /* Исходное (начальное) значение */
    protected T  startValue;

    private JLabel      title   = null;
    private Insets      insets  = null;

    /* Какой-то обьект, который связан с этим виджетом. Например, параметр этого обьекта отображен в виджете. */
    private Object      object  = null;



    public abstract JComponent getGuiComponent();

    protected abstract T validateValue() throws WEditException;

    public abstract T getValue (); //throws WEditException;

    public abstract void   setValue ( T value ); //throws WEditException;

    /* Можно или нет изменять значение виджета. */
    public abstract void   setEditable ( boolean value );
    //public abstract boolean   isEditable ();

    /* Задать ширину значения параметра виджета, в пикселях. Для форматирования вертикальных виджетов. */
    public abstract void setValueWidth ( int width );


    /* используется только внутри setAction. EltexEventHandler - универсальная для всех видов Листенеров (пока НЕ исп)
    *  переписывается в виджетах - по желанию использования */
    protected void initAction ( ActionListener listener, String cmd ) {}




    protected AbstractWidget ( String titleName, boolean hasEmpty )
    {
        //super ( new FlowLayout(FlowLayout.LEFT, 5, 1), true );   // 15, 5
        this ( titleName, hasEmpty, "" );
    }

    protected AbstractWidget ( String titleName, boolean hasEmpty, String emptyValue )
    {
        super ( new FlowLayout(FlowLayout.LEFT, 2, 0), true );    // 15, 5

        this.hasEmpty   = hasEmpty;
        this.emptyValue = emptyValue;
        this.titleName  = titleName;

        startValue      = null;

        // заголовок виджета
        if ( titleName != null )
        {
            title   = new JLabel(titleName);
            add ( title );
        }
    }

    public boolean isChangeValue () //throws WEditException
    {
        int ic;

        ic  = Utils.compareToWithNull ( getValue(), getStartValue() );
        
        return ic != 0;
    }

    /* Задать ширину титла виджета, в пикселях */
    public void setTitleWidth ( int width )
    {
        int         height;
        Dimension   dim;

        //Logger.getInstance().debug ( "AbstractWidget.setTitleWidth: Start. title = " + title );
        if ( title != null )
        {
            height  = title.getHeight();
            if ( height < WCons.BUTTON_HEIGHT )  height = WCons.BUTTON_HEIGHT;
            dim     = new Dimension ( width, height );
            title.setPreferredSize ( dim );
        }
    }

    /**
     * Сбросить виджет в исходное состояние.
     * <BR/> Если на виджете висит акцию - НЕ трогать акцию.
     * <BR/> Ошибок по Reset быть не должно - т.к. мы возвращаемся в старое (нормальное) состояние.  Поэтому исключение здесь убрано. svj, 2011-08-02
     */
    public void reset ()
    {
        try
        {
            setValue ( getStartValue() );
            // убрать сообщения об ошибке если они были.
            setError ( false );
        } catch ( Exception e )        {
            Log.l.error ( Convert.concatObj ( "AbstractWidget.reset: err. widget name = ", getName() ), e);
        }
    }

    public void disableAction ()
    {
        /*
        if ( (action != null) && (action instanceof EltexEventHandler) )
        {
            EltexEventHandler handler   = (EltexEventHandler) action;
            handler.setDisableAction();
        }
        */
    }

    public void enableAction ()
    {
        /*
        if ( (action != null) && (action instanceof EltexEventHandler) )
        {
            EltexEventHandler handler   = (EltexEventHandler) action;
            handler.setEnableAction();
        }
        */
    }

    public void setStartValue ( T startValue ) //throws WEditException
    {
        disableAction();

        this.startValue = startValue;
        setValue ( startValue );

        enableAction();
    }


    public T getStartValue ()
    {
        return startValue;
    }

    protected void setError ( boolean hasError )
    {
        JComponent comp;

        // Возможно, надо подкрашивать текст. Но если текст отсутствует, а это - ошибка - то что тогда подкрашивать?
        /*
        if ( hasError )
            setBackground ( WCons.RED_1 );   // Панель красится некрасиво - расползается и т.д.
        else
            setBackground ( UIManager.getColor("Panel.background") );    // Используем значение L&F
            */

        // Красим титл и текст   -- svj, 2011-08-02
        if ( title != null )
        {
            if ( hasError )
                title.setForeground ( WCons.RED_1 );
            else
                title.setForeground ( UIManager.getColor("Panel.foreground") );    // Используем значение L&F
        }
        // Текст
        comp    = getGuiComponent();
        if ( comp != null )
        {
            if ( hasError )
                comp.setForeground ( WCons.RED_1 );
            else
                comp.setForeground ( UIManager.getColor("Panel.foreground") );    // Используем значение L&F
        }
    }


    public void validateWidget() throws WEditException
    {
        try
        {
            validateValue();
            setError ( false );
            setToolTipText ( null );
        } catch ( WEditException ex )        {
            Log.l.error ( getTitleName ()+" Error. value = "+getValue(), ex );
            setError ( true );
            setToolTipText ( ex.getMessage() );
            throw ex;
        }
    }


    public boolean hasEmpty ()
    {
        return hasEmpty;
    }

    public String getEmptyValue ()
    {
        return emptyValue;
    }

    public String getTitleName ()
    {
        return titleName;
    }

    public Insets getInsets()
    {
        if ( insets == null )
            return super.getInsets();
        else
            return insets;
    }

    public void setInsets ( int top, int left, int bottom, int right )
    {
        insets = new Insets ( top, left, bottom, right );
    }

    public Component getTitleComponent ()
    {
        // title.getLabelFor() = null
        // label.getGraphics = null
        return title.getLabelFor();
        //title.safelyGetGraphics(title);
    }
    

    public Object getObject ()
    {
        return object;
    }

    public void setObject ( Object object )
    {
        this.object = object;
    }

    /**
     * Установить вертикальное расположение компонент. Сверху - титл, снизу - значение.
     */
    public void setVerticalAligment ()
    {
        setLayout ( new BoxLayout ( this, BoxLayout.PAGE_AXIS ));
    }

    public void setToolTip ( String toolTip )
    {
        if ( getGuiComponent () != null )  getGuiComponent ().setToolTipText ( toolTip );
    }

}
