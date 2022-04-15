package svj.wedit.v6.tools;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.dialog.ShowHtmlDialog;
import svj.wedit.v6.logger.Log;

import javax.swing.*;
import java.awt.*;


/**
 * Генератор внутренних диалоговых окон.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 27.07.2011 11:00:37
 */
public class DialogTools
{
    /**
     * Запрос Подтверждения
     * @param parentFrame       Родительский фрейм
     * @param msg               Сообщение диалога
     * @param buttonNameOk      Текст для первой кнопки
     * @param buttonNameCancel  Текст для второй кнопки
     * @return  Номер кнопки (от 0) либо -1 если диалог закрылся крестиком.
     */
    public static int showConfirmDialog ( Component parentFrame, Object msg, String buttonNameOk, String buttonNameCancel )
    {
        int result;
        Object[] options = { buttonNameOk, buttonNameCancel };
        result = JOptionPane.showOptionDialog ( parentFrame,
            msg,
            "Подтверждение",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            options,
            options[0] );
        return result;
    }

    public static int showConfirmDialog ( Component parentFrame, Object msg, String buttonName1,
                                          String buttonName2, String buttonName3 )
    {
        int result;
        Object[] options = { buttonName1, buttonName2, buttonName3 };
        result = JOptionPane.showOptionDialog ( parentFrame,
            msg,
            "Подтверждение",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            options,
            options[0] );
        return result;
    }

    public static int showConfirmDialog ( Component parentFrame, String title, Object msg )
    {
        int result;
        Object[] options = { "Принять", "Отменить" };
        result = JOptionPane.showOptionDialog ( parentFrame,
            msg,
            title,
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            options,
            options[0] );
        return result;
    }

    /**/
    public static void showMessage  ( Component parentFrame, Object msg, String title )
    {
        JOptionPane.showMessageDialog ( parentFrame, msg, title, JOptionPane.INFORMATION_MESSAGE );
    }

    public static void showMessage ( String title, Object msg )
    {
        JOptionPane.showMessageDialog ( Par.GM.getFrame(), msg, title, JOptionPane.INFORMATION_MESSAGE );
        //JOptionPane.showMessageDialog ( null, msg, title, JOptionPane.INFORMATION_MESSAGE );
    }

    public static void showHtml ( String title, String htmlText )
    {
        showHtml ( title, htmlText, 2 );
    }

    public static void showHtml ( String title, String htmlText, int widthDiv )
    {
        showHtml ( title, JOptionPane.OK_OPTION, htmlText, widthDiv );
    }

    public static void showHtml ( String title, int buttonType, String htmlText, int widthDiv )
    {
        ShowHtmlDialog dialog = new ShowHtmlDialog ( title, widthDiv );
        try
        {
            dialog.init ( htmlText );
            dialog.setDefaultButton(buttonType);
        } catch ( WEditException e )         {
            Log.l.error ( "err", e );
        }
        dialog.showDialog ();
        //JOptionPane.showMessageDialog ( Par.GM.getFrame(), msg, title, JOptionPane.INFORMATION_MESSAGE );
    }

    /**
     *
     * @param title
     * @param msg
     * @return  NULL если cancel.
     */
    public static String showInput ( Component parentFrame, String title, Object msg )
    {
        return JOptionPane.showInputDialog ( parentFrame, msg, title, JOptionPane.INFORMATION_MESSAGE );
    }

    /*
    todo Виснет диалог, если ткнуть мышкой мимо него. Что-то в JOptionPane.
    Причем клавиша Enter остается в силе - кнопка ОК реагирует на это событие, на мышь - нет.
    Уходит фокус?? попробовать свой диалог, и переписать repaint
    -- nameField.requestFocusInWindow();
    -- dialog.requestFocus();
    public static void showMessage2 ( String title, Object msg )
    {
        JDialog     dialog;
        Component   c;

        //dialog = new JDialog ( Par.GM.getFrame(), title, true );
        dialog = new JDialog ( (Frame)null, title, true );

        if ( msg instanceof Component )
            c = (Component) msg;
        else
            c = new JLabel(msg.toString ());
        dialog.getContentPane().add ( c );
        dialog.pack();

        dialog.setVisible ( true );

        dialog.dispose();

        //JOptionPane.showMessageDialog ( Par.GM.getFrame(), msg, title, JOptionPane.INFORMATION_MESSAGE );
    }
    //*/

    public static void showError ( Object msg, String title )
    {
        //JOptionPane.showMessageDialog ( parentFrame, msg, title, JOptionPane.ERROR_MESSAGE );
        showError ( null, msg, title );
    }

    public static void showError ( Component parentFrame, Object msg, String title )
    {
        Object object;
        Log.l.info("[M] msg = %s");   // ловим ошибку с длинным текстом который почемуто не обрезается.
        // Анализ на очень длинные строки.
        if ( msg instanceof String )
        {
            String str = msg.toString ();
            Log.l.info("[M] msg is String/ length = %d", str.length());
            if ( str.length() > 80 )
            {
                Log.l.info("[M] msg > 80");
                JTextArea textArea = new JTextArea ( str );
                textArea.setWrapStyleWord ( true );
                textArea.setLineWrap ( true );
                Dimension size = new Dimension ( Par.SCREEN_SIZE.width / 2, (str.length() / 80 + 1)*15 );
                Log.l.info("[M] new size = %s", size);
                textArea.setPreferredSize ( size );
                object = textArea;
                //str = StringTools.recut ( str, 75, "\n" );
            }
            else
            {
                Log.l.info("[M] msg < 80");
                object = str;
            }
        }
        else
        {
            Log.l.info("[M] msg Not String");
            object = msg;
        }
        if ( parentFrame == null )  parentFrame = Par.GM.getFrame();
        JOptionPane.showMessageDialog ( parentFrame, object, title, JOptionPane.ERROR_MESSAGE );
    }

}
