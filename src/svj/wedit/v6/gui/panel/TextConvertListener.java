package svj.wedit.v6.gui.panel;

import svj.wedit.v6.Par;
import svj.wedit.v6.tools.DialogTools;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Изменение текста на другую раскрадку.
 * Например, набили русский текст в английской раскладке.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 17.06.2019 16:30
 */
public class TextConvertListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent event) {
        //Log.l.info("Convert source = %s", event.getSource());   // JMenuItem

        // получить JTextPane
        JTextPane textPane = Par.GM.getFrame().getCurrentTextPanel().getTextPane();

        // взять выделенный текст
        String text = textPane.getSelectedText();
        if ( text == null )  return;   // Ничего не выбрано.

        String title;
        if ( text.length() > 10 )
            title = text.substring ( 0, 9 ) + "...";
        else
            title = text;

        // вывести меню с предложением замены. Результат: -1 - крестик; 0 - первая кнопка; 1 - 2-я; 2 - 3-я
        int ic = DialogTools
                .showConfirmDialog ( null, title+"\n\nВыбрать раскладку клавиатуры:",  "RU", "EN" );

        // перекодировать
        // - строим строки двух раскладок
        String en = " ~!@#$%^&*()_+|QWERTYUIOP{}ASDFGHJKL:\"ZXCVBNM<>?`1234567890-=\\qwertyuiop[]asdfghjkl;\'zxcvbnm,./";
        String ru = " Ё!\"№;%:?*()_+/ЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ,ё1234567890-=\\йцукенгшщзхъфывапролджэячсмитьбю.";
        // - создаем мап - исходя из того из какой раскладки в какую.
        Map<Character, Character> map = new HashMap<Character, Character>( en.length () );
        int index = 0;
        switch (ic) {
            case -1:
                // крестик - Отказ
                break;
            case 0:
                // Из англ в русскую
                for ( Character ch : en.toCharArray() )
                {
                    map.put ( ch, ru.charAt ( index ) );
                    index++;
                }
                break;
            case 1:
                // Из русской в анг
                for ( Character ch : ru.toCharArray() )
                {
                    map.put ( ch, en.charAt ( index ) );
                    index++;
                }
                break;
        }

        map.put('\n', '\n');
        map.put('\r', '\r');

        //Log.l.info ( "map = " + map );

        if ( map.size() > 0 ) {
            // - переконвертить
            Character cNew;
            StringBuilder newStr = new StringBuilder();
            for (Character c : text.toCharArray()) {
                cNew = map.get(c);
                if (cNew == null)
                    cNew = '_';
                newStr.append(cNew);
            }
            //Log.l.info ( "newStr = " + newStr );

            // заменить текст на новый
            textPane.replaceSelection(newStr.toString());
        }
    }

}
