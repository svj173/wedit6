package svj.wedit.v6.gui.text;


import svj.wedit.v6.logger.Log;

import javax.swing.text.*;
import java.awt.*;
import java.io.Serializable;

/**
 * Мой текстовый курсор. В два раза толще стандартного текстового курсора.
 * <BR/>
 * <BR/> Создать свой класс Caret с необходимой функциональностью; для экземпляра этого своего класса вызвать install на требуемом JTextField'е. В качестве основы можешь унаследоваться от DefaultCaret.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 12.05.2016 10:53
 */
public class TextCaret extends DefaultCaret implements Serializable
{
    /** Ширина курсора в пикселях. */
    private int width = 1;

    private Color bg;

    public TextCaret ( int width )
    {
        if ( width <= 0 )
            this.width = 1;
        else
            this.width = width;
    }


    //
    // Get the background color of the component
    //
    public void install ( JTextComponent c )
    {
        super.install(c);

        bg = Color.WHITE;
        /*
        // Курсор перестает быть видимым
        try
        {
            Document doc = c.getDocument();
            if (doc instanceof StyledDocument )
            {
                StyledDocument sDoc = (StyledDocument)doc;
                Element elem = sDoc.getCharacterElement( 0 );
                AttributeSet attr = elem.getAttributes();
                bg = sDoc.getBackground(attr);
            }

            if (bg == null) {
                bg = c.getBackground();
            }

        } catch ( Exception e )    {
            Log.l.error ( "TextCaret Error. JTextComponent = "+c, e );
            bg = Color.WHITE;
        }
        //*/
    }

    public void paint ( Graphics g )
    {
        if ( isVisible() )
        {
            int start, xRight, yDown;
            try
            {
                Rectangle r = getComponent().modelToView ( getDot() );

                // хвостики вверху и внизу - до setXORMode
                start   = ( r.x <= 0 ) ?  0: r.x-1;
                xRight  = r.x + width;
                yDown   = r.y + r.height - 1;

                g.setXORMode ( bg );
                for ( int i=0; i<width; i++ )
                    g.drawLine(r.x+i, r.y, r.x+i, r.y + r.height - 1);

                // Хвостики. Вверху
                g.drawLine ( start, r.y, start, r.y );
                g.drawLine ( xRight, r.y, xRight, r.y );
                // внизу
                g.drawLine ( start, yDown, start, yDown );
                g.drawLine ( xRight, yDown, xRight, yDown );

                g.setPaintMode();

            } catch ( Exception e ) {
                // can't render I guess
                //System.err.println("Can't render cursor");
                Log.l.error ( "TextCaret Error. width = "+width, e );
            }
        }
    }

}
