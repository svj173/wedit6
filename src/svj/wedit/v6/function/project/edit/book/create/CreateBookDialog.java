package svj.wedit.v6.function.project.edit.book.create;


import svj.wedit.v6.Par;
import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.dialog.WValidateDialog;
import svj.wedit.v6.gui.renderer.INameRenderer;
import svj.wedit.v6.gui.widget.ComboBoxWidget;
import svj.wedit.v6.gui.widget.StringFieldWidget;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.Section;
import svj.wedit.v6.obj.book.BookStatus;
import svj.wedit.v6.obj.book.BookTitle;
import svj.wedit.v6.tools.GuiTools;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Диалог по созданию Книги Сборника.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 18.08.2011 12:54:05
 */
public class CreateBookDialog extends WValidateDialog<BookTitle, BookTitle>
{
    private final StringFieldWidget   nameWidget, fileNameWidget;
    private final ComboBoxWidget<BookStatus> statusWidget;
    private final Project project;
    private final Section section;


    public CreateBookDialog(Project project, Section section, String title, boolean editFileName,
                            final String titleFromBookContent, String fileName)
            throws WEditException
    {
        super ( Par.GM.getFrame(), title );

        this.project = project;
        this.section = section;

        JPanel  panel;
        int     width;

        width   = 220;

        panel   = new JPanel();
        panel.setLayout ( new BoxLayout(panel, BoxLayout.PAGE_AXIS) );

        nameWidget = new StringFieldWidget ( "Название книги", false, 128, 320 );
        nameWidget.setTitleWidth ( width );
        panel.add ( nameWidget );

        fileNameWidget = new StringFieldWidget ( "Имя файла (без расширения)", false, 128, 320 );
        fileNameWidget.setTitleWidth ( width );
        fileNameWidget.setEditable ( editFileName );
        panel.add ( fileNameWidget );

        // Кнопка: "Синхронизировать с книгой (титл из книги)"
        if ( titleFromBookContent != null )
        {
            JButton button;
            button = GuiTools.createButton ( "Синхронизировать с книгой ("+titleFromBookContent+")", null, "sync.png" );
            button.setToolTipText ( "Взять название из названия книги (указано в скобках)." );
            button.addActionListener ( new ActionListener ()
            {
                @Override
                public void actionPerformed ( ActionEvent event )
                {
                    nameWidget.setValue ( titleFromBookContent );
                }
            } );
            panel.add ( button );
        }

        statusWidget = new ComboBoxWidget<BookStatus> ( "Статус", BookStatus.getStatusList() );
        statusWidget.setTitleWidth ( width );
        //statusWidget.setValueWidth ( valueWidth );
        statusWidget.setComboRenderer ( new INameRenderer () );
        panel.add ( statusWidget );

        // fileName  -- todo пока всегда Null
        if ( fileName == null )
        {
            //fileWidget = null;
        }
        else
        {
            // Выводим полный путь до файла книги. Без возможности редактирования.
            StringFieldWidget fileWidget = new StringFieldWidget ( "Файл (полный путь)", false, 128, 320 );
            fileWidget.setTitleWidth ( width );
            fileWidget.setEditable ( false );
            fileWidget.setValue ( fileName );
            panel.add ( fileWidget );
        }

        addToCenter ( panel );
    }

    @Override
    public boolean validateData ()
    {
        boolean result;
        String  str;

        result  = true;
        str     = nameWidget.getValue();
        str     = str.trim();
        if ( str.isEmpty() )
        {
            result  = false;
            addValidateErr ( "Не задано название книги." );
        }
        else
        {
            str = fileNameWidget.getValue();
            str = str.trim();
            if ( str.isEmpty() )
            {
                result  = false;
                addValidateErr ( "Не задано имя файла книги." );
            }
        }

        return result;
    }

    protected void createDialogSize ()
    {
        /*
        int  width, height;

        width       = Par.SCREEN_SIZE.width / 3;
        height      = Par.SCREEN_SIZE.height / 3;
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
    public void init ( BookTitle initBookTitle ) throws WEditException
    {
        nameWidget.setValue ( initBookTitle.getName() );
        fileNameWidget.setValue ( initBookTitle.getFileName() );
        statusWidget.setValue ( initBookTitle.getBookStatus() );
        /*
        if ( fileWidget != null )
        {
            BookContent bc = initBookTitle.getBookContent ();
            if ( bc == null )
                fileWidget.setValue ( "???" );
            else
                fileWidget.setValue ( bc.getFileName() );
        }
        */
    }

    public boolean isChange ()
    {
        return nameWidget.isChangeValue() || statusWidget.isChangeValue();
    }

    @Override
    public BookTitle getResult () throws WEditException
    {
        BookTitle   bookTitle;
        String      fileName;

        fileName    = fileNameWidget.getValue();
        if ( ! fileName.endsWith ( "."+ WCons.BOOK_FILE_NAME_SUFFIX ))  fileName = fileName + "." + WCons.BOOK_FILE_NAME_SUFFIX;
        bookTitle = new BookTitle ( project, section, nameWidget.getValue(), fileName );
        bookTitle.setBookStatus ( statusWidget.getValue() );

        return bookTitle;
    }

}
