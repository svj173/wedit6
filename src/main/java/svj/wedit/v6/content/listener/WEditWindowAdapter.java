package svj.wedit.v6.content.listener;


import svj.wedit.v6.Par;
import svj.wedit.v6.logger.Log;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


/**
 * Отработка нажатия пользователем крестика на окне фрейма Общего содержимого книги.
 * <BR/> Корректное закрытие Редактора - НЕ аварийное.
 * <BR/> Алгоритм:
 * <BR/> - Если было редактирование - сообщить об этом пользователю (список). Не захочет ничего сохранять - не сохранять.
 * <BR/> - Не было редактирования - ничего не делать.
 * <BR/> todo А сохранение профилей? Списка открытых книг, положение маркера в тексте?
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 05.05.2011 17:45:28
 */
public class WEditWindowAdapter  extends WindowAdapter
{

    public WEditWindowAdapter ()
    {
        super();
    }

    public void windowClosing ( WindowEvent e )
    {
        Log.l.debug ( "Press Krestik. Close WEDIT started" );

        Par.GM.close();

        System.exit ( 0 );
        // Здесь дальше запускается функция SHUTDOWN
        
        Log.l.debug ( "Press Krestik. Close WEDIT finished" );
    }

}
