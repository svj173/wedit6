package svj.wedit.v6.gui.widget;

import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;

import javax.swing.*;
import java.awt.*;

/**
 * Виджет целочисленного поля. В нем же происходит валидация введенных значений (проверка на целое).
 * <BR/>
 * User: svj
 * Date: 16.08.2011 21:07:15
 */
public class IntegerFieldWidget  extends AbstractWidget<Integer>
{
    /* Текстовое поле для ввода числа */
    private JTextField      textField;
    /* если не NULL - максимально допустимое значение (верхняя граница) */
    private Integer         maxValue;
    /* если не NULL - минимально допустимое значение (нижняя граница) */
    private Integer         minValue;

    public IntegerFieldWidget ( String titleName, boolean hasEmpty )
    {
        super ( titleName, hasEmpty );
        init();
    }

    private void init ()
    {
        textField = new  JTextField();
        textField.setPreferredSize ( new Dimension ( 80, WCons.BUTTON_HEIGHT) );
        textField.setHorizontalAlignment ( JTextField.RIGHT );
        add ( textField );

        maxValue    = null;
        minValue    = null;
    }

    @Override
    public JComponent getGuiComponent ()
    {
        return textField; 
    }

    @Override
    protected Integer validateValue () throws WEditException
    {
        Integer result;
        String  str;

        str = textField.getText();
        if ( str != null )
        {
            str = str.trim();
            if ( str.length() == 0 ) str = null;
        }

        if ( str == null )
        {
            if ( hasEmpty() )   result = null;  // EmsConst.INT_EMPTY
            else throw new WEditException ( null, getTitleName(), ": Значение не может быть пустым" );
        }
        else
        {
            try
            {
                result = Integer.parseInt(str);
            } catch ( NumberFormatException e )             {
                throw new WEditException ( null, getTitleName(), ": Введено неверное значение" );
            }
        }
        // проверка на верхнюю и нижнюю границы
        if ( result != null )
        {
            if ( (maxValue != null) && (maxValue < result ) )
                throw new WEditException ( null, getTitleName(), ": Введеное значение '", result, "' больше максимально допустимого '", maxValue, "'" );
            if ( (minValue != null) && (minValue > result ) )
                throw new WEditException ( null, getTitleName(), ": Введеное значение '", result, "' меньше минимально допустимого '", minValue, "'" );
        }
        return result;
    }

    @Override
    public Integer getValue ()  //throws WEditException
    {
        Integer result;

        try
        {
            result = validateValue();
        } catch ( Exception e )         {
            result = null;
            Log.l.error ( "Ошибка получения целочисленного параметра из виджета.", e );
        }
        return result;
    }

    @Override
    public void setValue ( Integer value ) //throws WEditException
    {
        String strValue;
        if ( value == null )
        {
            //if ( hasEmpty() )  strValue = getEmptyValue();
            //else  throw new WEditException ( null, getTitleName(), ": Введено неверное значение" );
            strValue = "";
        }
        else
            strValue = value.toString();

        if ( strValue == null )  strValue = "";
        textField.setText ( strValue );

        try
        {
            validateValue();
        } catch ( WEditException e )         {
            textField.setText ( "-2" );
        }
    }

    @Override
    public void setEditable (boolean value)
    {
        textField.setEditable ( value );
    }

    @Override
    public void setValueWidth (int width)
    {
        int         height;
        Dimension   dim;

        if ( textField != null )
        {
            height  = textField.getHeight();
            if ( height < WCons.BUTTON_HEIGHT )  height = WCons.BUTTON_HEIGHT;
            dim     = new Dimension ( width, height );
            textField.setPreferredSize ( dim );
        }
    }

    public Integer getMaxValue ()
    {
        return maxValue;
    }

    public void setMaxValue ( Integer maxValue )
    {
        this.maxValue = maxValue;
    }

    public Integer getMinValue ()
    {
        return minValue;
    }

    public void setMinValue ( Integer minValue )
    {
        this.minValue = minValue;
    }
}
