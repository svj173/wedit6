package svj.wedit.v6.function.book.edit.desc;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.InitObjectComponent;
import svj.wedit.v6.gui.panel.WPanel;
import svj.wedit.v6.gui.renderer.INameRenderer;
import svj.wedit.v6.gui.widget.ComboBoxWidget;
import svj.wedit.v6.gui.widget.IntegerFieldWidget;
import svj.wedit.v6.gui.widget.StringFieldWidget;
import svj.wedit.v6.gui.widget.font.FontWidget;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.element.WBookElement;

import javax.swing.*;
import java.awt.*;


/**
 * Панель для отображения свойств описания элемента книги WBookElement.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.08.2011 17:35:06
 */
public class BookElementPanel  extends WPanel  implements InitObjectComponent<WBookElement>
{
    /* Крайнее смещение, в пробелах. Если align=LEFT - это смещение слева от заголовка. Если align=RIGHT - это смещение справа от заголовка. Для CENTER - не используется. - Text */
    private IntegerFieldWidget          marginWidget;
    private StringFieldWidget           nameWidget;
    private FontWidget                  fontWidget;
    private ComboBoxWidget<AlignType>   alignWidget;   // String titleName, boolean hasEmpty, String emptyValue, T[] values

    private JDialog parentDialog;
    private WBookElement bookElement;


    public BookElementPanel ()
    {
        JPanel  panel;
        int     width;

        width   = 220;
        setLayout ( new BorderLayout(5,5) );

        panel = new JPanel();
        panel.setLayout ( new BoxLayout(panel, BoxLayout.PAGE_AXIS) );

        nameWidget = new StringFieldWidget ( "Название элемента", false, 128, 320 );
        nameWidget.setTitleWidth ( width );
        panel.add ( nameWidget );

        fontWidget = new FontWidget ( "Шрифт элемента" );
        fontWidget.setTitleWidth ( width );
        panel.add ( fontWidget );

        alignWidget = new ComboBoxWidget<AlignType> ( "Положение", false, "", AlignType.values() );
        alignWidget.setTitleWidth ( width );
        alignWidget.setComboRenderer ( new INameRenderer() );
        panel.add ( alignWidget );

        marginWidget = new IntegerFieldWidget ( "Смещение", false );
        marginWidget.setTitleWidth ( width );
        marginWidget.setToolTipText ( "Если align=LEFT - это смещение (в пробелах) слева от заголовка. Если align=RIGHT - это смещение справа от заголовка." );
        //marginWidget.setEditable ( false );
        panel.add ( marginWidget );

        add ( panel, BorderLayout.NORTH );

        // пустышка
        add ( new JLabel(" "), BorderLayout.CENTER );
    }

    @Override
    public void init ( WBookElement element )
    {
        int ic;

        bookElement = element;

        if ( bookElement == null ) return;
        try
        {
            nameWidget.setValue ( bookElement.getName() );

            fontWidget.setColor ( bookElement.getColor() );
            fontWidget.setValue ( bookElement.getFont() );

            ic = bookElement.getAlign();
            alignWidget.setValue ( AlignType.getByNumber(ic) );

            marginWidget.setValue ( bookElement.getMargin() );

        } catch ( Exception e )        {
            // пока непонятно что делать
            Log.l.error ( "err", e );
        }
    }

    @Override
    public WBookElement getObj ()
    {
        return null;
    }

    public void setParentDialog ( JDialog dialog )
    {
        parentDialog    = dialog;
    }

    public WBookElement getResult () throws WEditException
    {
        /*
        BookElement bookElement;

        bookElement = new BookElement ( levelWidget.getValue() );

        // common
        bookElement.setName ( nameWidget.getValue() );
        bookElement.setType ( typeWidget.getValue() );
        bookElement.setRowSpace ( rowSpaceWidget.getValue() );

        // tree
        bookElement.setTreeFgColor ( treeFontWidget.getColor() );
        bookElement.setTreeFont ( treeFontWidget.getValue() );
        bookElement.setTreeIcon ( treeIconWidget.getValue() );

        // editor
        bookElement.setTextFont ( textFontWidget.getValue() );
        bookElement.setTextColor ( textFontWidget.getColor() );

        // print
        bookElement.setHasPrintElementName ( hasPrintElementNameWidget.getValue() );
        bookElement.setHasPrintNumber ( hasPrintNumberWidget.getValue() );
        bookElement.setTypeNumeric ( typeNumericWidget.getValue() );

        return bookElement;
        */
        return null;
    }

    public JDialog getParentDialog ()
    {
        return parentDialog;
    }

    public static void main ( String[] args )
    {
        BookElementPanel    panel;
        JFrame              frame;

        frame = new JFrame();
        panel   = new BookElementPanel();
        frame.add ( panel );
        frame.pack();
        frame.setVisible ( true );

        //System.exit(1);
    }

    public void fromWidgetsToElement ()
    {
        AlignType align;

        if ( bookElement != null )
        {
            bookElement.setName ( nameWidget.getValue() );

            bookElement.setFont ( fontWidget.getValue() );
            bookElement.setColor ( fontWidget.getColor() );

            align = alignWidget.getValue();
            bookElement.setAlign ( align.getNumber() );

            bookElement.setMargin ( marginWidget.getValue() );
        }
    }

    public WBookElement getBookElement ()
    {
        return bookElement;
    }

}
