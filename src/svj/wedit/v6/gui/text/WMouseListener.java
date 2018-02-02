package svj.wedit.v6.gui.text;


import svj.wedit.v6.Par;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.text.InfoElementTypeFunction;
import svj.wedit.v6.function.text.SelectElementFunction;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.DialogTools;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

/**
 * Ловим щелчок мышки на тексте - чтобы изменять выпадашки стилей, цветов, элементов - для информации.
 *
 - text
  --- MouseListener.mouseClicked: charAttr = LeafElement(content) 72,78
  --- MouseListener.mouseClicked: inputAttr =
  --- MouseListener.mouseClicked: paragraphAttr = BranchElement(paragraph) 72,78
  --- MouseListener.mouseClicked: logicalStyle = NamedStyle:default {bold=false,name=default,foreground=sun.swing.PrintColorUIResource[r=51,g=51,b=51],
         italic=false,FONT_ATTRIBUTE_KEY=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12],size=12,family=Dialog,}

 - подглава
  --- MouseListener.mouseClicked: charAttr = LeafElement(content) 39,49
  --- MouseListener.mouseClicked: inputAttr = Alignment=0 FirstLineIndent=1.0 styleName=Подглава_work foreground=java.awt.Color[r=0,g=128,b=0] size=14 family=Dialog
  --- MouseListener.mouseClicked: paragraphAttr = BranchElement(paragraph) 39,50
  --- MouseListener.mouseClicked: logicalStyle = NamedStyle:default {bold=false,name=default,foreground=sun.swing.PrintColorUIResource[r=51,g=51,b=51],
         italic=false,FONT_ATTRIBUTE_KEY=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12],size=12,family=Dialog,}

 Юзаем inputAttr

 Имя стиля
 1) глава: Глава_work
 2) текст: null

 source = JTextPane


 Синонимы: кавалерия — конница, смелый — храбрый, идти — шагать.
 Омонимы:  кран, ключ, бор.
 Антонимы: правда — ложь, добрый — злой, говорить — молчать.

 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 07.02.2013 16:28
 */
public class WMouseListener implements MouseListener
{

    // mousePressed + mouseReleased
    @Override
    public void mouseClicked ( MouseEvent event )
    {
        Object          source;
        JTextPane       textPane;
        AttributeSet    charAttr, inputAttr, paragraphAttr;
        Style           logicalStyle;
        String          styleName;

        Log.l.debug ( "--- MouseListener.mouseClicked: event = ", event.getSource() );

        if ( event.isConsumed() ) return;

        /*
        //Log.l.debug ( "--- MouseListener.mouseClicked: event = ", e );
        Log.l.debug ( "- MouseListener.mouseClicked: point = %s", event.getPoint() );
        source      = event.getSource();
        Log.l.debug ( "- MouseListener.mouseClicked: event source = %s", source.getClass().getSimpleName() );

        //processMouseEvent ();
        textPane    = ( JTextPane ) source;
        charAttr    = textPane.getCharacterAttributes();
        Log.l.debug ( "--- MouseListener.mouseClicked: charAttr = %s", charAttr );
        inputAttr   = textPane.getInputAttributes();
        Log.l.debug ( "--- MouseListener.mouseClicked: inputAttr = %s", inputAttr );
        paragraphAttr = textPane.getParagraphAttributes ();
        Log.l.debug ( "--- MouseListener.mouseClicked: paragraphAttr = %s", paragraphAttr );
        logicalStyle = textPane.getLogicalStyle();
        Log.l.debug ( "--- MouseListener.mouseClicked: logicalStyle = %s", logicalStyle );

        // inputAttr
        styleName  = (String) inputAttr.getAttribute ( StyleName.STYLE_NAME );
        Log.l.debug ( "----- styleName = %s", styleName );
        */
        // Взять функцию отображения стиля и изменить в выпадашке занчение.
        Function function;

        function = Par.GM.getFrame().getTextsPanel().getFunction ( FunctionId.TEXT_INFO_ELEMENT );
        if ( ( function != null ) && ( function instanceof InfoElementTypeFunction ) )
        {
            InfoElementTypeFunction elementFunction = ( InfoElementTypeFunction ) function;
            //elementFunction.setCurrentStyle ( styleName );
            elementFunction.rewrite();
        }

        //*
        function = Par.GM.getFrame().getTextsPanel().getFunction ( FunctionId.TEXT_SELECT_ELEMENT );
        Log.l.debug ( "----- function (SelectElementFunction) = %s", function );
        if ( ( function != null ) && ( function instanceof SelectElementFunction ) )
        {
            SelectElementFunction elementFunction = ( SelectElementFunction ) function;
            //elementFunction.setCurrentStyle ( styleName );
            elementFunction.setCurrentStyle ( null ); // reset
        }

        // ловим правую кнопку мыши - выводим меню.
        if ( SwingUtilities.isRightMouseButton(event) )
        {
            int x, y;
            JMenuItem menuItem;
            EditorKit editor;
            StyledDocument doc;
            Position pos;
            Element element;
            //JEditorPane editorPane;
            Point p;
            String str;


            x = event.getX();
            y = event.getY();
            //menuItem = new JMenuItem ( "тест" );

            p = new Point ( x,y );

            // Выделить слово на котором стоим
            source      = event.getSource();
            textPane    = ( JTextPane ) source;
            charAttr    = textPane.getCharacterAttributes();
            editor      = textPane.getEditorKit ();
            doc      = textPane.getStyledDocument ();
            //editor.get
            pos = doc.getStartPosition();    // всегда 0
            //element = doc.getCharacterElement ( x );

            //textPane.getAccessibleAt(p); // getCaretPosition,
            //textPane.getCaretPosition(); // номер от начала текста

            // todo переместить курсор в эту точку
            textPane.getCaret().setMagicCaretPosition ( p );

            str = textPane.getSelectedText();
            //if ( str == null )  str = "Null";

            if ( str != null )
            {
                // Открыть меню
                JPopupMenu pm = new JPopupMenu ();
                //pm.add ( menuItem );

                try
                {
                    /*
                    menuItem = new JMenuItem ( textPane.getToolTipText ( event ) );
                    pm.add ( menuItem );
                    //menuItem = new JMenuItem ( textPane.getToolTipLocation ( event ).toString () );
                    //pm.add ( menuItem );
                    menuItem = new JMenuItem ( "X:"+x+"; Y:"+y );
                    pm.add ( menuItem );
                    if ( pos != null )
                    {
                        menuItem = new JMenuItem ( "pos: "+pos );
                        pm.add ( menuItem );
                    }
                    */
                    String title;
                    if ( str.length() > 10 )
                        title = str.substring ( 0, 9 ) + "...";
                    else
                        title = str;
                    menuItem = new JMenuItem ( title );
                    menuItem.setBackground ( Color.LIGHT_GRAY );
                    pm.add ( menuItem );

                    // todo Список испарвлений орфографии

                    // todo Список синонимов

                    // todo Список омонимов

                    // todo функция по перекодировке текста - русский-английский
                    menuItem = createEncodeMenu ( textPane, str );
                    pm.add ( menuItem );

                } catch ( Exception e )            {
                    Log.l.error ( "--- MouseListener.mouseClicked: event = ", e );
                }

                pm.show ( event.getComponent(), x, y );
            }
        }

        //*/
    }

    private JMenuItem createEncodeMenu ( final JTextPane textPane, final String str )
    {
        JMenuItem menuItem = new JMenuItem("Перераскладка");
        menuItem.setComponentOrientation ( ComponentOrientation.RIGHT_TO_LEFT );
        menuItem.addActionListener ( new ActionListener ()
        {
            @Override
            public void actionPerformed ( ActionEvent event )
            {
                int ic = DialogTools.showConfirmDialog ( null, "Выбрать раскладку клавиатуры.", "RU", "EN" );

                // перекодировать
                // - строим строки двух раскладок
                String en = "~!@#$%^&*()_+|QWERTYUIOP{}ASDFGHJKL:\"ZXCVBNM<>?`1234567890-=\\qwertyuiop[]asdfghjkl;\'zxcvbnm,./";
                String ru = "Ё!\"№;%:?*()_+/ЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ,ё1234567890-=\\йцукенгшщзхъфывапролджэячсмитьбю.";
                // - создаем мап - исходя из того из какой раскладки в какую.
                Map<Character, Character> map = new HashMap<Character, Character> ( en.length () );
                int index = 0;
                if ( ic == 0 )
                {
                    // Из англ в русскую
                    for ( Character ch : en.toCharArray() )
                    {
                        map.put ( ch, ru.charAt ( index ) );
                        index++;
                    }
                }
                else
                {
                    // Из русской в анг
                    for ( Character ch : ru.toCharArray() )
                    {
                        map.put ( ch, en.charAt ( index ) );
                        index++;
                    }
                }
                //Log.l.info ( "map = " + map );

                // - переконвертить
                Character cNew;
                StringBuilder newStr = new StringBuilder();
                for ( Character c : str.toCharArray() )
                {
                    cNew    = map.get(c);
                    if ( cNew == null )  cNew = '_';
                    newStr.append ( cNew );
                }
                //Log.l.info ( "newStr = " + newStr );

                // заменить текст на новый
                textPane.replaceSelection ( newStr.toString() );
            }
        } );

        return menuItem;
    }

    @Override
    public void mousePressed ( MouseEvent e )
    {
        //Log.l.debug ( "--- MouseListener.mousePressed: event = ", e );
    }

    @Override
    public void mouseReleased ( MouseEvent e )
    {
        //Log.l.debug ( "--- MouseListener.mouseReleased: event = ", e );
    }

    @Override
    public void mouseEntered ( MouseEvent e )
    {
        //Log.l.debug ( "--- MouseListener.mouseEntered: event = ", e );
    }

    @Override
    public void mouseExited ( MouseEvent e )
    {
        //Log.l.debug ( "--- MouseListener.mouseExited: event = ", e );
    }

}
