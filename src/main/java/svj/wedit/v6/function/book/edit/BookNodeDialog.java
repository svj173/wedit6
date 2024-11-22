package svj.wedit.v6.function.book.edit;


import svj.wedit.v6.exception.MessageException;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.dialog.WDialog;
import svj.wedit.v6.gui.widget.ComboBoxWidget;
import svj.wedit.v6.gui.widget.StringFieldWidget;
import svj.wedit.v6.gui.widget.TextWidget;
import svj.wedit.v6.obj.WType;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;

import javax.swing.*;
import java.awt.*;
import java.util.Map;


/**
 * Диалог по созданию или редактированию собственно Элемента книги.
 * <BR/> Со всеми атрибутами элемента.
 * <BR/>
 * <BR/> Атрибуты элемента:
 * <BR/> - Имя
 * <BR/> - Аннотация
 * <BR/>
 * <BR/> Возможность редактировать описание элемента этого уровня.
 * <BR/> При добавлении, если в списке описания элементов нет элемента такого уровня - принудительно заставить описать.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 20.08.2011 14:54:05
 */
public class BookNodeDialog extends WDialog<BookNode, BookNode>
{
    private BookNode parentBookNode;

    private StringFieldWidget       nameWidget;
    private TextWidget              annotationWidget;
    private ComboBoxWidget<String>  typeWidget;
    //private BookElementDialog       elementDialog;


    public BookNodeDialog ( String title, BookNode parentBookNode ) throws WEditException
    {
        super ( title );

        JPanel              panel;
        int                 width, guiWidth;
        BookContent         bookContent;
        Map<String, WType>  types;

        if ( parentBookNode == null )
            throw new MessageException ( "Запрещено редактировать корневой элемент\n (Есть свой редактор, с редактированием атрибутов книги и т.д.)" );

        this.parentBookNode = parentBookNode;
        width       = 120;   // Ширина заголовков виджетов
        guiWidth    = 320;   // Ширина ГУИ компонент

        panel   = new JPanel();
        panel.setLayout ( new BoxLayout(panel, BoxLayout.PAGE_AXIS) );

        // boolean hasEmpty, int maxSize, int width, String titleName
        nameWidget = new StringFieldWidget ( "Название", false, 128, guiWidth );
        nameWidget.setTitleWidth ( width );
        //nameWidget.setValue ( "none empty" );
        panel.add ( nameWidget );

        // - type
        //bookContent     = Par.GM.getFrame().getCurrentBookContent();
        bookContent = parentBookNode.getBookContent();
        // Взять список типов
        types       = bookContent.getBookStructure().getTypes();
        //typeWidget = new StringFieldWidget ( "Тип", true, 128, 320 );
        // String titleName, boolean hasEmpty, String emptyValue,  Collection<T> values
        typeWidget = new ComboBoxWidget<String> ( "Тип", true, "---", types.keySet() );
        typeWidget.setTitleWidth ( width );
        //nameWidget.setValue ( "none empty" );
        panel.add ( typeWidget );

        // String titleName, boolean hasEmpty, int rows
        //annotationWidget = new StringFieldWidget ( "Аннотация", true, 128, 320 );
        annotationWidget = new TextWidget ( "Аннотация", true, 4 );
        annotationWidget.getGuiComponent().setPreferredSize ( new Dimension ( guiWidth, annotationWidget.getHeight() ) );
        annotationWidget.setTitleWidth ( width );
        panel.add ( annotationWidget );

        /*
        bookContent     = Par.GM.getFrame().getCurrentBookContent();
        bookElement     = bookContent.getBookElement ( parentBookNode.getLevel() + 1 );
        elementDialog   = new BookElementDialog ( "Элемент", bookElement );
        panel.add ( elementDialog );
        */
        addToNorth ( panel );
    }

    protected void createDialogSize ()
    {
        /*
        int  width, height;

        width       = Par.SCREEN_SIZE.width / 3;
        height      = Par.SCREEN_SIZE.height / 2;
        setPreferredSize ( new Dimension (width,height) );
        setSize ( width, height );
        */
        pack();
    }

    @Override
    public void doClose ( int closeType )
    {
    }

    @Override
    public void init ( BookNode bookNode ) throws WEditException
    {
        //this.parentBookNode = bookNode;

        // Занести значения в виджеты при редактировании
        nameWidget.setValue ( bookNode.getName() );

        typeWidget.setValue ( bookNode.getElementType() );

        annotationWidget.setValue ( bookNode.getAnnotation() );
    }

    @Override
    public BookNode getResult () throws WEditException
    {
        BookNode bookNode;

        bookNode = new BookNode ( nameWidget.getValue(), parentBookNode );
        bookNode.setElementType ( typeWidget.getValue() );
        bookNode.setAnnotation ( annotationWidget.getValue() );

        return bookNode;
    }

}
