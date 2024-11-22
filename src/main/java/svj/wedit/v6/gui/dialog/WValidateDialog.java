package svj.wedit.v6.gui.dialog;


import svj.wedit.v6.Par;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.WorkResult;
import svj.wedit.v6.tools.Convert;

import javax.swing.*;
import java.awt.*;


/**
 * Диалог, в котором прежде чем закрыть окно, предварительно осуществляется валидация данных. Если не прошла - окно не закрывается.
 * <BR/>
 * <BR/> объекты описания:
 * <BR/> - T - объект, который первоначально заносится в диалог (какая-то необходимая диалогу информация)
 * <BR/> - M - объект, который возвращается диалогом (результат работы)
 * <BR/> User: svj
 * <BR/> Date: 07.11.2011 14:48:34
 */
public abstract class WValidateDialog<T,M>  extends WDialog<T,M>
{
    /* Верхняя панель для вывода дополнительных сообщений об ошибках, которые не отображаются в ошибочном параметре.
    * Например, совокупные значения нескольких параметров (дополнительная валидация). */
    private JPanel    errorMsgPanel;
    private JTextArea errorMsgArea;
    private String    errorMsg;
    /* Признак добавления или редактирования. Раз есть валидация каких-то данных, значит они либо добавляются, либо ...
    * Здесь - не используется. Просто - как флаг. */
    //private boolean   isAdded   = true;
    private JScrollPane errorScroll;


    /* TRUE - все ОК с введенными в диалоге данными. */
    public abstract boolean validateData ();


    public WValidateDialog ( String title )
    {
        // Par.GM == null - для ситуации когда работа с диалогами происходит в Редакторе Лицензий.
        this ( Par.GM == null ? null : Par.GM.getFrame(), title );
    }

    /*
    public WValidateDialog ( String title, String iconPath )
    {
        super ( title, iconPath );

        initComponents ( isAdded );
    }
    */

    public WValidateDialog ( Dialog parent, String title )
    {
        super ( parent, title );

        initComponents ();
    }

    public WValidateDialog ( Frame parent, String title )
    {
        super ( parent, title );

        initComponents ();
    }

    private void initComponents ()
    {
        errorMsgArea = new JTextArea(3,0);
        errorMsgArea.setForeground ( Color.RED );
        errorMsgArea.setEditable ( false );

        errorMsgPanel = new JPanel ( new BorderLayout(5,5) );
        errorMsgPanel.setBorder ( BorderFactory.createEmptyBorder ( 5,10,5,10 ) );
        errorMsgPanel.add ( errorMsgArea, BorderLayout.CENTER );
        //errorMsgPanel.setVisible ( false );

        errorScroll = new JScrollPane ( errorMsgPanel );

        addToNorth ( errorScroll );

        // выключаем панель сообщений об ошибках
        unVisibleError();
    }


    public void doClose ( WorkResult retStatus )
    {
        Log.l.debug ( "WValidateDialog.doClose: Start. retStatus = ", retStatus );

        // чистим сообщения о предыдущих ошибках.
        errorMsg    = null;
        unVisibleError();

        if ( retStatus == WorkResult.OK )
        {
            // Сначала валидируем данные. Если ошибка - диалог не закрываем.
            if ( validateData() )
            {
                // -- Все ОК - закрываем диалог
                //unVisibleError();
                Log.l.debug ( "WValidateDialog.doClose: OK button without Errors." );
                super.doClose ( retStatus );
            }
            else
            {
                // Если это дополнительная валидация -  выводим на экран.
                setReturnStatus ( WorkResult.ERROR ); // добавил, чтобы при некорректных данных не сохранился статус ОК.
                setError ( getValidateErr() );
            }
        }
        else
        {
             super.doClose ( retStatus );
        }
        Log.l.debug ( "WValidateDialog.doClose: Finish" );
    }

    /* Переписали, чтобы при ошибках валидации не закрывать диалог. */
	@Override
    public void doClose ( int closeType )
    {
        WorkResult retStatus;

        Log.l.debug ( "WValidateDialog.doClose: Start. returnStatus (0 - OK) = ", closeType );
        switch ( closeType )
        {
            case JOptionPane.YES_OPTION:
                retStatus = WorkResult.OK;
                break;
            default:
            case JOptionPane.NO_OPTION:
            case JOptionPane.CANCEL_OPTION:
            case JOptionPane.CLOSED_OPTION:
                retStatus = WorkResult.CANCEL;
                break;
        }

        doClose ( retStatus );
    }

    public void visibleError()
    {
        errorScroll.setVisible ( true );

        // Попытка изменять размеры диалога при выключении выключении доп-панелей (с сообщением об ошибке)
        //       -- НЕ удачная. Размер остается тем же, только скроллинг ужимается.
        errorMsgPanel.setVisible ( true );
        errorScroll.revalidate();
        //this.repaint ();
        //revalidatePanel();

        // перерисовываем размеры диалога, т.к. добавилось (открылось) поле с сообщением об ошибке.
        pack();
    }

    /**
     * Перерисовать размеры панелей - обычно, центральных. Метод переписывается в диалогах.
     */
    protected void revalidatePanel ()     {    }

    public void unVisibleError()
    {
        //errorMsgPanel.setVisible ( false );
        errorScroll.setVisible ( false );
        errorMsgArea.setText ( "" );
        pack();
    }

    public void setError ( String text )
    {
        //Logger.getInstance().debug ( "--- EltexValidateDialog.setError: Start. text = ", text );
        if ( text != null )
        {
            errorMsgArea.setText ( text );
            visibleError();
        }
    }

    public String  getValidateErr ()
    {
        return errorMsg;
    }

    public void  setValidateErr ( String text )
    {
        errorMsg    = text;
    }

    public void  setValidateErr ( Object ... obj )
    {
        errorMsg    = Convert.concatObj ( obj );
    }

    public void  addValidateErr ( String text )
    {
        if ( errorMsg == null )
            errorMsg    = text;
        else
            errorMsg    = Convert.concatObj ( errorMsg, '\n', text );
    }

    /**
     * Отключить окно сообщений об ошибках.
     */
    public void disableErrorPanel ()
    {
        errorScroll.setVisible ( false );
    }

}
