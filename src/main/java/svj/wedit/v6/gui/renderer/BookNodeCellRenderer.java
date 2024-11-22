package svj.wedit.v6.gui.renderer;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.tree.WCellRenderer;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.TreeObjType;
import svj.wedit.v6.obj.WTreeObj;
import svj.wedit.v6.obj.WType;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.book.element.WBookElement;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.GuiTools;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;


/**
 * Рендерер Элементов книги в дереве Содержимого книги.
 * <BR/> Элементы здесь могут иметь свои подтипы, и, следовательно, отрисовываться по другому (Часть, Глава, Скрытая часть...).
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 05.04.2013 15:23:46
 */
public class BookNodeCellRenderer extends WCellRenderer
{
    // иконки для Элементов разных подтипов данного обьекта - пока только одна
    private Map<TreeObjType,Icon> icons;

    public BookNodeCellRenderer ()
    {
        super ( null );
        icons = new HashMap<TreeObjType,Icon>();
    }

    // Добавить себя в пул рендереров.
    @Override
    public void init ( JLabel treeRenderer, WTreeObj obj ) throws WEditException
    {
        Icon        icon;
        BookNode    bookNode;
        BookContent bookContent;
        WBookElement bookElement;

        //Logger.getInstance().debug ( "MesCellRenderer.init: Start. obj = " + obj );
        //Log.l.debug ( "--- init: obj class = ", obj.getClass().getSimpleName() );     // BookNode

        if ( obj instanceof  BookNode )
        {
            try
            {
                //Icon icon = GuiTools.createImageIcon ( iconPath, "Иконка объекта в дереве" );
                // проверяем подтип/версию подтипа
                icon = getIcon();
                //Logger.getInstance().debug ("MesCellRenderer.init: icon = " + icon );
                if ( icon == null )
                {
                    icon = icons.get ( obj.getType() );
                    //Logger.getInstance().debug ("MesCellRenderer.init: icon 2 = " + icon );
                    if ( icon == null )
                    {
                        icon = GuiTools.createImageByFile ( obj.getTreeIconFilePath() );
                        if ( icon != null ) icons.put ( obj.getType(), icon );
                    }
                }

                if ( icon != null ) treeRenderer.setIcon ( icon );

                treeRenderer.setText ( obj.getName() );

                bookNode    = (BookNode) obj;
                // Вытаскиваем цвет, стиль для данного элемента
                bookContent = bookNode.getBookContent();
                if ( bookContent != null )
                {
                    bookElement = bookContent.getBookStructure().getElement ( bookNode );
                    if ( bookElement != null )
                    {
                        Color color;
                        Font  font, resultFont, currentFont;
                        WType type;
                        int styleType;

                        type    = bookContent.getBookStructure().getType ( bookNode.getElementType() );
                        // В дереве.
                        // - Цвет - берем из типа
                        // - название фонта - берем из элемента
                        // - тип стиля - берем из типа и из элемента (сумма по | ).

                        //font    = bookElement.getTreeFont();
                        //bookElement.getTreeIcon ();
                        color   = bookElement.getColor();

                        treeRenderer.setForeground ( color );

                        // Нет setXXX - перестраивать заново весь фонт?
                        currentFont  = treeRenderer.getFont();

                        styleType   = bookElement.getStyleType() | type.getStyleType();
                        // String name, int style, int size
                        //resultFont  = new Font ( font.getName(), font.getStyle(), currentFont.getSize() );
                        resultFont  = new Font ( bookElement.getFontFamily(), styleType, currentFont.getSize() );
                        treeRenderer.setFont ( resultFont );
                    }
                }

                //Logger.getInstance().debug ("MxaCellRenderer.init: Finish" );

            //} catch ( WEditException we ) {
            //    throw we;
            } catch ( Exception ex ) {
                Log.l.error ( Convert.concatObj ( "ERROR. Obj = ", obj ), ex);
                throw new WEditException ( ex, "Section : ", ex );
            }
        }
    }
    
}
