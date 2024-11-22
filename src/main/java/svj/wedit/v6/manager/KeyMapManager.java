package svj.wedit.v6.manager;


import svj.wedit.v6.exception.Issue;
import svj.wedit.v6.gui.menu.WEMenuItem;
import svj.wedit.v6.obj.function.Function;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Hashtable;


/**
 * Отслеживает совпадения мапинга клавиш в функциях
 * и сообщает об этом пользователю в понятной для него
 * форме на этапе инициализации функций (нефатальная ошибка).
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.07.2011 17:30:22
 */
public class KeyMapManager
{
    private static KeyMapManager manager = new KeyMapManager ();

    private Hashtable<String,Function> buffer  = new Hashtable<String, Function> ();


    public static KeyMapManager getInstance ()
    {
        return manager;
    }

    private KeyMapManager ()    {}

    public void treatKeyMap ( WEMenuItem item, String mapKey, Function function )
    {
        String[]    keys;
        int         mod;
        KeyStroke   keyStroke;
        Function function2;

        try
        {
            // Проверка на похожесть. Если уже есть такой - выйти с сообщением.
            if ( buffer.contains ( mapKey ) )
            {
                //
                function2   = buffer.get (mapKey);
                function.addError ( "Клавиши мапирования: Есть уже такие '", mapKey,"' в функции '", function2.getName(), "'." );
            }
            else
            {
                buffer.put ( mapKey, function );
                keys    = mapKey.split ("/");
                if ( keys.length == 1 )
                {
                    // Всего Один символ
                    keyStroke = KeyStroke.getKeyStroke (  mapKey.charAt ( 0 ) );
                    item.setAccelerator( keyStroke );
                }
                else if ( keys.length == 2 )
                {
                    // Два символа
                    mod = ActionEvent.ALT_MASK;
                    if ( keys[0].equalsIgnoreCase ( "ctrl") ) mod = ActionEvent.CTRL_MASK;
                    keyStroke = KeyStroke.getKeyStroke (  keys[1].charAt(0), mod );
                    item.setAccelerator( keyStroke );
                }
                else
                {
                    //logger.error ( "Unknow key map = '" + str + "'." );
                    function.addError ( "Клавиши мапирования: Не удалось распознать '", mapKey, "'." );
                }
            }

        } catch ( Exception e )        {
            Issue issue   = new Issue();
            //logger.error ( "Key map error for '" + str + "'.", e );
            issue.add ( "Клавиши мапирования: Системная ошибка при обработке сочетания '", mapKey, "'." );
            issue.setSystemError ( e );
            function.addIssue ( issue );
        }
    }
    
}
