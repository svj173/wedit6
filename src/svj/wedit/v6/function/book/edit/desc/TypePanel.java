package svj.wedit.v6.function.book.edit.desc;


import svj.wedit.v6.gui.InitObjectComponent;
import svj.wedit.v6.gui.panel.WPanel;
import svj.wedit.v6.gui.widget.CheckBoxWidget;
import svj.wedit.v6.gui.widget.ColorWidget;
import svj.wedit.v6.gui.widget.StringFieldWidget;
import svj.wedit.v6.gui.widget.font.StyleTypeWidget;
import svj.wedit.v6.obj.WType;

import javax.swing.*;
import java.awt.*;

/**
 * Панель для отображения свойств типа элемента книги WBookElement.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 29.08.2014 16:17
 */
public class TypePanel   extends WPanel implements InitObjectComponent<WType>
{
    private final StringFieldWidget   ruNameWidget, enNameWidget, descrWidget;
    /*  - цвет - для дерева и текста (заменяет цвет элемента) */
    private final ColorWidget titleColorWidget;

    /* style - Font.PLAIN, BOLD, ITALIC, or BOLD+ITALIC. - накладывается на тип стиля элемента */
    private final StyleTypeWidget styleTypeWidget;

    // todo Тип по-умолчанию (при отсутствии типа).  CheckBox - если установлен то у остальных этот признак стирается. - только один тип может быть дефолтным.
    private final CheckBoxWidget defaultTypeWidget;

    /* Текущйи тип. */
    private WType elementType;


    public TypePanel ()
    {
        JPanel panel;
        int    width;

        width   = 220;  // todo по идее - сначала пересчитать длину всех заголовков.
        setLayout ( new BorderLayout (5,5) );

        panel = new JPanel();
        panel.setLayout ( new BoxLayout(panel, BoxLayout.PAGE_AXIS) );

        ruNameWidget = new StringFieldWidget ( "Название типа (ru)", false, 128, 320 );
        ruNameWidget.setTitleWidth ( width );
        panel.add ( ruNameWidget );

        enNameWidget = new StringFieldWidget ( "Название типа (en)", false, 128, 320 );
        enNameWidget.setTitleWidth ( width );
        panel.add ( enNameWidget );

        descrWidget = new StringFieldWidget ( "Описание", false, 128, 320 );
        descrWidget.setTitleWidth ( width );
        panel.add ( descrWidget );

        titleColorWidget = new ColorWidget ( "Цвет заголовка" );
        titleColorWidget.setTitleWidth ( width );
        panel.add ( titleColorWidget );

        styleTypeWidget = new StyleTypeWidget ( "Стиль" );
        styleTypeWidget.setTitleWidth ( width );
        styleTypeWidget.setToolTipText ( "Bold, Italic, etc..." );
        panel.add ( styleTypeWidget );

        defaultTypeWidget = new CheckBoxWidget ( "Тип по-умолчанию" );
        defaultTypeWidget.setTitleWidth ( width );
        defaultTypeWidget.setToolTipText ( "Может быть только один. Установив здесь, в других типах этот флаг сбросится." );
        panel.add ( defaultTypeWidget );


        add ( panel, BorderLayout.NORTH );

        // пустышка
        add ( new JLabel(" "), BorderLayout.CENTER );
    }

    @Override
    public void init ( WType obj )
    {
        elementType = obj;
        if ( elementType != null )
        {
            ruNameWidget.setValue ( obj.getRuName() );
            enNameWidget.setValue ( obj.getEnName() );
            descrWidget.setValue ( obj.getDescr() );
            titleColorWidget.setValue ( obj.getColor() );
            styleTypeWidget.setValue ( obj.getStyleType() );
            defaultTypeWidget.setValue ( obj.isDefaultType() );
        }
    }

    @Override
    public WType getObj ()
    {
        return elementType;
    }

    public void fromWidgetsToElement ()
    {
        if ( elementType != null )
        {
            elementType.setRuName ( ruNameWidget.getValue() );
            elementType.setEnName ( enNameWidget.getValue() );
            elementType.setDescr ( descrWidget.getValue() );
            elementType.setColor ( titleColorWidget.getValue() );
            elementType.setStyleType ( styleTypeWidget.getValue() );
            elementType.setDefaultType ( defaultTypeWidget.getValue() );
        }
    }

}
