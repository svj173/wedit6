package svj.wedit.v6.gui.widget;


import svj.wedit.v6.Par;
import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.tools.GuiTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.File;


/**
 * Виджет получения имен файлов.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 17.10.2013 16:30:18
 */
public class FileWidget extends AbstractWidget<String>
{
    /* Текстовое поле с отображением имени файла. */
    private final JTextField    textField;
        /* Кнопка запуска диалога выборки файла. */
    private final JButton       choseFileButton;


    public FileWidget ( String titleName, boolean hasEmpty )
    {
        super ( titleName, hasEmpty, ""  );

        Dimension size;

        textField   = new JTextField();

        // размер текстовой панели по-умолчанию.
        size        = new Dimension ( 250, WCons.BUTTON_HEIGHT );
        textField.setPreferredSize ( size );
        //setBorder ( BorderFactory.createEtchedBorder() );
        //textField.setBackground ( Color.RED );
        textField.setHorizontalAlignment ( JTextField.LEFT );
        //textField.setColumns ( maxSize );   // выкл - делает почему-то шире чем задано символов (svj, 2010-10-12)
        add ( textField );

        choseFileButton = GuiTools.createButton ( "..", "Выбрать существующий файл - для перезаписи.", null );
        choseFileButton.addActionListener ( new ActionListener ()
        {
            @Override
            public void actionPerformed ( ActionEvent event )
            {
                JFileChooser            chooser;
                int                     returnValue;
                File                    file;
                chooser     = new JFileChooser ( textField.getText() );
                //chooser = new JFileChooser ( currentDirectory );
                chooser.setDialogTitle ( "Выберите результирующий файл." );
                chooser.setMultiSelectionEnabled ( false );
                //chooser.setAcceptAllFileFilterUsed ( true );
                //chooser.setFileSelectionMode ( JFileChooser.FILES_ONLY );
                returnValue = chooser.showSaveDialog ( Par.GM.getFrame() );  // may be NULL

               if ( returnValue == JFileChooser.APPROVE_OPTION )
               {
                   // взять имя выбранной директории
                   file  = chooser.getSelectedFile();
                   //Log.l.debug ( "selected  = ", file );
                   textField.setText ( file.getAbsolutePath() );
               }
            }
        } );
        add ( choseFileButton );
    }

    public void addMouseListener ( MouseListener mouseListener )
    {
        textField.addMouseListener ( mouseListener );
    }

    @Override
    public void setEditable ( boolean value )
    {
        textField.setEditable ( value );
        choseFileButton.setEnabled ( value );
    }

    @Override
    public void setValueWidth ( int width )
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

    @Override
    public JComponent getGuiComponent ()
    {
        return textField;
    }

    @Override
    protected String validateValue () throws WEditException
    {
        String str, result, msg;

        str = textField.getText();
        if ( str != null )
        {
            str = str.trim();
            if ( str.length() == 0 ) str = null;
        }

        if ( str == null )
        {
            if ( hasEmpty() )   result = null;
            else
            {
                msg = getTitleName();
                if ( msg != null )  msg = msg + ": ";
                msg = msg + "Значение не может быть пустым";
                throw new WEditException ( msg );
            }
        }
        else
        {
            result = str;
        }
        return result;
    }

    @Override
    public String getValue () //throws WEditException
    {
        /*
        String value;
        try
        {
            value = validateValue();
        } catch ( WEditException e )         {
            value = null;
            Log.l.error ( e, "Ошибка получения параметра из виджета файла." );
        }
        return value;
        */
        return textField.getText();
    }

    @Override
    public void setValue ( String value )  //throws WEditException
    {
        /* -- Надо ли это при занесении ??? - Валидирвоать надо толкьо при получении параметров для их сохранения.
        if ( value == null )
        {
            if ( hasEmpty() )
                value = getEmptyValue();
            else
                throw new WEditException ( null, getTitleName(), ": Введено неверное значение" );
        }
        */

        textField.setText ( value );
    }

}
