package svj.wedit.v6.gui.listener;


import svj.wedit.v6.handler.CloseHandler;
import svj.wedit.v6.logger.Log;

import java.awt.event.WindowEvent;


/**
 * Акция закрывания. Например, для корректной обработки закрытия диалога по крестику.
 * <BR/> PS: При лояльном выходе из диалога - по кнопке диалога Выход - закрытие (эта функция) будет дергаться два
 * раза - лояльно по обработке кнопки Выход и еще раз - по физическом закрытии диалога..
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 27.07.2011 10:52:36
 */
public class CloseWindowListener extends WEditWindowListener
{
    private CloseHandler closeHandler;

    public CloseWindowListener ( CloseHandler closeHandler )
    {
        this.closeHandler = closeHandler;
    }

    /* Окно (фрейм, диалог) начало закрываться - Обработать закрывание диалога.
     * Этот метод вызывается при закрытии окна диалога. */
    @Override
    public void windowClosing ( WindowEvent e )
    {
        Log.l.debug ( "CloseWindowListener.windowClosing: Start" );
        if ( closeHandler != null )  closeHandler.close();
        Log.l.debug ( "CloseWindowListener.windowClosing: Finish" );
    }

}
