package svj.wedit.v6.function.project.edit.book;


import svj.wedit.v6.exception.MessageException;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.dialog.WValidateDialog;
import svj.wedit.v6.gui.renderer.INameRenderer;
import svj.wedit.v6.gui.widget.ComboBoxWidget;
import svj.wedit.v6.gui.widget.StringFieldWidget;
import svj.wedit.v6.gui.widget.TextWidget;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookStatus;
import svj.wedit.v6.tools.XmlTools;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


/**
 * Диалог по редактированию атрибутов Книги.
 * <BR/> 1) Заголовок (название книги).
 * <BR/> -2) Дата создания.
 * <BR/> -3) Автор - изначально берется Из сборника.
 * <BR/> -4) e-mail  - изначально берется Из сборника.
 * <BR/> 5) web
 * <BR/> 6) Аннотация на книгу.
 * <BR/> 7) Синопсис.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 04.02.2014 12:54:05
 */
public class EditBookParamsDialog extends WValidateDialog<BookContent, BookContent>
{
    private final StringFieldWidget   titleWidget;
    private final ComboBoxWidget<BookStatus> statusWidget;
    private final TextWidget          annotationWidget, synopsisWidget;
    private final JPanel              attrsPanel;
    private final Collection<StringFieldWidget> attrsWidgetList;

    public EditBookParamsDialog () throws WEditException
    {
        super ( "Редактировать аттрибуты книги" );

        JPanel  panel, p;
        int     titleWidth, valueWidth;

        Log.l.debug ( "Create edit book params dialog." );

        titleWidth   = 110;
        valueWidth   = 220;

        panel = new JPanel();
        panel.setLayout ( new BoxLayout(panel, BoxLayout.PAGE_AXIS) );

        // кнопки: добавить атрибут, удалить выделенные атрибуты
        p = new JPanel ();
        p.add ( new JButton("Добавить атрибут") );
        p.add ( new JButton("Удалить атрибуты") );
        panel.add ( p );

        // boolean hasEmpty, int maxSize, int width, String titleName
        titleWidget = new StringFieldWidget ( "Название", false, 128, 320 );
        titleWidget.setTitleWidth ( titleWidth );
        titleWidget.setValueWidth ( valueWidth );
        //nameWidget.setValue ( "none empty" );
        panel.add ( titleWidget );

        annotationWidget = new TextWidget ( "Аннотация", 3 );
        annotationWidget.setTitleWidth ( titleWidth );
        annotationWidget.setValueWidth ( valueWidth );
        panel.add ( annotationWidget );

        synopsisWidget = new TextWidget ( "Синопсис", 10 );
        synopsisWidget.setTitleWidth ( titleWidth );
        synopsisWidget.setValueWidth ( valueWidth );
        panel.add ( synopsisWidget );

        statusWidget = new ComboBoxWidget<BookStatus> ( "Статус", BookStatus.getStatusList() );
        statusWidget.setTitleWidth ( titleWidth );
        statusWidget.setValueWidth ( valueWidth );
        statusWidget.setComboRenderer ( new INameRenderer() );
        panel.add ( statusWidget );

        attrsPanel  = new JPanel();
        attrsPanel.setLayout ( new BoxLayout(attrsPanel, BoxLayout.PAGE_AXIS) );
        attrsPanel.setBorder ( BorderFactory.createTitledBorder ( "Аттрибуты" ) );
        panel.add ( attrsPanel );

        attrsWidgetList = new ArrayList<StringFieldWidget>();

        //addToNorth ( panel );
        addToCenter ( panel );

        //pack();
    }

    @Override
    public boolean validateData ()
    {
        boolean result;
        String  str;

        result  = true;

        str     = getBookTitle();
        if ( str.isEmpty() )
        {
            result  = false;
            addValidateErr ( "Не задано название книги." );
        }
        else
        {
            if ( XmlTools.checkXmlSymbols ( str ) )
            {
                result  = false;
                addValidateErr ( "Название книги содержит недопустимые XML символы." );
            }
        }

        str     = getAnnotation ();
        if ( XmlTools.checkXmlSymbols ( str ) )
        {
            result  = false;
            addValidateErr ( "Аннотация книги содержит недопустимые XML символы." );
        }

        str     = getSynopsis ();
        if ( XmlTools.checkXmlSymbols ( str ) )
        {
            result  = false;
            addValidateErr ( "Синопсис книги содержит недопустимые XML символы." );
        }

        // валидация на отсутствие xml в введенных значениях
        result  = validateAttributies ( result );

        return result;
    }

    private boolean validateAttributies ( boolean b )
    {
        boolean result;
        String  text;

        result = b;
        for ( StringFieldWidget widget : getAttrsWidgetList() )
        {
            text = widget.getValue();
            if ( XmlTools.checkXmlSymbols ( text ) )
            {
                result  = false;
                addValidateErr ( "Атрибут '"+widget.getTitleName()+"' содержит недопустимые XML символы." );
            }
        }
        return result;
    }

    protected void createDialogSize ()
    {
    }

    @Override
    public void doClose ( int closeType )
    {
    }

    @Override
    public void init ( BookContent bookContent ) throws WEditException
    {
        StringFieldWidget attrWidget;
        int     titleWidth, valueWidth;

        if ( bookContent == null )  throw new MessageException ( "Книга не задана." );

        setTitle ( "Редактировать аттрибуты книги '"+bookContent.getName() +"'" );

        titleWidget.setValue ( bookContent.getName() );
        annotationWidget.setValue ( bookContent.getAnnotation() );
        synopsisWidget.setValue ( bookContent.getSynopsis() );
        statusWidget.setValue ( bookContent.getBookStatus() );

        // attrs

        titleWidth   = 170;
        valueWidth   = 220;

        attrsWidgetList.clear();
        attrsPanel.removeAll();
        for ( Map.Entry<String,String> entry : bookContent.getBookAttrs().entrySet() )
        {
            attrWidget = new StringFieldWidget ( entry.getKey(), false, 128, 320 );
            attrWidget.setTitleWidth ( titleWidth );
            attrWidget.setValueWidth ( valueWidth );
            attrWidget.setValue ( entry.getValue() );
            getAttrsPanel().add ( attrWidget );
            getAttrsWidgetList().add ( attrWidget );
        }

        pack();
    }

    // НЕ исп.
    @Override
    public BookContent getResult () throws WEditException
    {
        return null;
    }

    public String getBookTitle ()
    {
        return titleWidget.getValue().trim();
    }

    public String getAnnotation ()
    {
        return annotationWidget.getValue().trim();
    }

    public String getSynopsis ()
    {
        return synopsisWidget.getValue().trim();
    }

    public BookStatus getBookStatus ()
    {
        return statusWidget.getValue ();
    }

    public boolean isChange ()
    {
        return titleWidget.isChangeValue() || annotationWidget.isChangeValue() || synopsisWidget.isChangeValue() || statusWidget.isChangeValue() || attrsChanged();
    }

    public boolean isStatusChange ()
    {
        return statusWidget.isChangeValue();
    }

    /**
     *
     * @return TRUE - были изменения в атрибутах.
     */
    private boolean attrsChanged ()
    {
        boolean result;

        result = false;
        for ( StringFieldWidget widget : getAttrsWidgetList() )
        {
            if ( widget.isChangeValue() )
            {
                result  = true;
                break;
            }
        }
        return result;
    }

    public Collection<StringFieldWidget> getAttrsWidgetList ()
    {
        return attrsWidgetList;
    }

    public JPanel getAttrsPanel ()
    {
        return attrsPanel;
    }

}
