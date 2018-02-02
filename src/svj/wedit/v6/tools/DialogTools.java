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
        ShowHtmlDialog dialog = new ShowHtmlDialog(title);
        try
        {
            dialog.init ( htmlText );
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

    public static void showError ( Component parentFrame, Object msg, String title )
    {
        JOptionPane.showMessageDialog ( parentFrame, msg, title, JOptionPane.ERROR_MESSAGE );
    }

    public static void showError ( Object msg, String title )
    {
        JOptionPane.showMessageDialog ( Par.GM.getFrame(), msg, title, JOptionPane.ERROR_MESSAGE );
    }

}
