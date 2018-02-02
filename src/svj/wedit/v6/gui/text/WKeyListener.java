package svj.wedit.v6.gui.text;


import svj.wedit.v6.Par;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.text.InfoElementTypeFunction;
import svj.wedit.v6.function.text.SelectElementFunction;
import svj.wedit.v6.obj.function.Function;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Ловим щелчок мышки на тексте - чтобы изменять выпадашки стилей, цветов, элементов - для информации.
 *
 - text
  --- WKeyListener.mouseClicked: charAttr = LeafElement(content) 72,78
  --- WKeyListener.mouseClicked: inputAttr =
  --- WKeyListener.mouseClicked: paragraphAttr = BranchElement(paragraph) 72,78
  --- WKeyListener.mouseClicked: logicalStyle = NamedStyle:default {bold=false,name=default,foreground=sun.swing.PrintColorUIResource[r=51,g=51,b=51],
         italic=false,FONT_ATTRIBUTE_KEY=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12],size=12,family=Dialog,}

 - подглава
  --- WKeyListener.mouseClicked: charAttr = LeafElement(content) 39,49
  --- WKeyListener.mouseClicked: inputAttr = Alignment=0 FirstLineIndent=1.0 styleName=Подглава_work foreground=java.awt.Color[r=0,g=128,b=0] size=14 family=Dialog
  --- WKeyListener.mouseClicked: paragraphAttr = BranchElement(paragraph) 39,50
  --- WKeyListener.mouseClicked: logicalStyle = NamedStyle:default {bold=false,name=default,foreground=sun.swing.PrintColorUIResource[r=51,g=51,b=51],
         italic=false,FONT_ATTRIBUTE_KEY=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12],size=12,family=Dialog,}

 Юзаем inputAttr

 Имя стиля
 1) глава: Глава_work
 2) текст: null

 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 07.02.2013 16:28
 */
public class WKeyListener implements KeyListener
{
    // keyPressed + keyReleased
    @Override
    public void keyTyped ( KeyEvent event )
    {

    }

    private void processKey ( KeyEvent event )
    {
        Function        function;

        /*
        Object          source;
        JTextPane       textPane;
        AttributeSet    charAttr, inputAttr, paragraphAttr;
        Style           logicalStyle;
        String          styleName;

        //Log.l.debug ( "--- WKeyListener.mouseClicked: event = ", e );
        //Log.l.debug ( "- WKeyListener.mouseClicked: point = ", event.getPoint() );
        source      = event.getSource();   // JTextPane
        Log.l.debug ( "- WKeyListener.processKey: event source = %s", source.getClass().getSimpleName() );

        //processMouseEvent ();
        textPane    = ( JTextPane ) source;
        charAttr    = textPane.getCharacterAttributes();
        Log.l.debug ( "--- WKeyListener.processKey: charAttr = %s", charAttr );
        inputAttr   = textPane.getInputAttributes();
        Log.l.debug ( "--- WKeyListener.processKey: inputAttr = %s", inputAttr );
        paragraphAttr = textPane.getParagraphAttributes ();
        Log.l.debug ( "--- WKeyListener.processKey: paragraphAttr = %s", paragraphAttr );
        logicalStyle = textPane.getLogicalStyle();
        Log.l.debug ( "--- WKeyListener.processKey: logicalStyle = %s", logicalStyle );

        // inputAttr
        styleName  = (String) inputAttr.getAttribute ( StyleName.STYLE_NAME );
        Log.l.debug ( "----- styleName = %s", styleName );
        */

        // Взять функцию отображения стиля и изменить в выпадашке занчение.
        function = Par.GM.getFrame().getTextsPanel().getFunction ( FunctionId.TEXT_INFO_ELEMENT );
        //Log.l.debug ( "----- function (InfoElementTypeFunction) = %s", styleName );
        if ( ( function != null ) && ( function instanceof InfoElementTypeFunction ) )
        {
            InfoElementTypeFunction elementInfoFunction = ( InfoElementTypeFunction ) function;
            //elementInfoFunction.setCurrentStyle ( styleName );
            // сам определит какой стиль под курсором
            elementInfoFunction.rewrite();
        }

        // Выпадашка установки нового стиля - всегда должна находится в исходном состоянии - т.е. ничего не выбрано.
        function = Par.GM.getFrame().getTextsPanel().getFunction ( FunctionId.TEXT_SELECT_ELEMENT );
        //Log.l.debug ( "----- function (SelectElementFunction) = %s", styleName );
        if ( ( function != null ) && ( function instanceof SelectElementFunction ) )
        {
            SelectElementFunction elementSelectFunction = ( SelectElementFunction ) function;
            //elementFunction.setCurrentStyle ( styleName );
            elementSelectFunction.setCurrentStyle ( null ); // reset
        }
    }

    @Override
    public void keyPressed ( KeyEvent event )
    {
        //Log.l.debug ( "--- WKeyListener.keyPressed: event = %s", e );
    }

    @Override
    public void keyReleased ( KeyEvent event )
    {
        //Log.l.debug ( "--- WKeyListener.keyReleased: event = %s", e );
        processKey ( event );
    }

}
