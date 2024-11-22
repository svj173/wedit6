package svj.wedit.v6.function.project.create_new;


import svj.wedit.v6.Par;
import svj.wedit.v6.WCons;
import svj.wedit.v6.obj.Author;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 27.07.2011 14:02:27
 */
public class NewProjectParamsPanel  extends JPanel
{
    private JTextField projectNameField, authorName, authorLastName, authorEmail;

    public NewProjectParamsPanel ()
    {
        super();

        Border border;
        JLabel          label;
        Dimension size;

        size    = new Dimension ( 200, WCons.BUTTON_HEIGHT );
        
        border  = new LineBorder ( Color.GRAY, 1, true );
        //border = new LineBorder (new Color (102, 204, 0), 1, false);
        //border = BorderFactory.createTitledBorder ( "Параметры" );
        setBorder ( border );

        setLayout ( new BoxLayout ( this, BoxLayout.PAGE_AXIS ) );

        // пропуск
        add ( Box.createVerticalStrut(5) );

        label   = new JLabel ( "Название Сборника" );
        add ( label );
        projectNameField = createTextField();
        add ( projectNameField );

        // пропуск
        add ( Box.createVerticalStrut(5) );
        label   = new JLabel ( "Имя" );
        add ( label );
        authorName = createTextField();
        authorName.setText ( Par.USER_LOGIN );
        add ( authorName );

        // пропуск
        add ( Box.createVerticalStrut(5) );
        label   = new JLabel ( "Фамилия" );
        add ( label );
        authorLastName = createTextField();
        add ( authorLastName );

        // пропуск
        add ( Box.createVerticalStrut(5) );
        label   = new JLabel ( "E-mail" );
        add ( label );
        authorEmail = createTextField();
        add ( authorEmail );

        // пропуск
        add ( Box.createVerticalStrut(50) );

        // нижняя пустышка
        add ( Box.createVerticalGlue() );
    }

    private JTextField createTextField ()
    {
        JTextField textField;
        Dimension size;

        size = new Dimension ( 200, WCons.BUTTON_HEIGHT );

        textField = new JTextField();
        textField.setColumns ( 20 );
        textField.setPreferredSize ( size );
        textField.setSize ( size );
        textField.setMinimumSize ( size );
        textField.setHorizontalAlignment ( JTextField.LEFT );

        return textField;
    }

    public String getProjectName ()
    {
        return projectNameField.getText();
    }

    public Author getAuthor ()
    {
        Author author;

        author  = new Author();
        author.setFirstName ( authorName.getText() );
        author.setLastName ( authorLastName.getText() );
        author.setEmail ( authorEmail.getText() );

        return author;
    }

}
