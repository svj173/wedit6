package svj.wedit.v6.gui.renderer;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.tree.WCellRenderer;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.TreeObjType;
import svj.wedit.v6.obj.WTreeObj;
import svj.wedit.v6.obj.book.BookStatus;
import svj.wedit.v6.obj.book.BookTitle;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.GuiTools;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;


/**
 * Рендерер Разделов в дереве Сборника. Также отрисовывает книги в Разделах.
 * <BR/> Разделы здесь могут иметь свои подтипы, и, следовательно, отрисовываться по другому (Сказки, Эссе, Фэнтези).
 * <BR/> Книги также могут отрисовываться соглсано своим статусам: реализована, очередная редакция, в работе, болванка, наполнение материалами 
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 04.08.2011 17:33:46
 */
public class SectionCellRenderer extends WCellRenderer
{
    // иконки для Разделов разных подтипов данного обьекта - пока только одна
    private Map<String,Icon> icons;

    public SectionCellRenderer ()
    {
        super ( null );
        icons = new HashMap<String,Icon>();
    }

    // Добавить себя в пул рендереров.
    @Override
    public void init ( JLabel treeRenderer, WTreeObj obj ) throws WEditException
    {
        Icon        icon;
        TreeObjType type;

        // TreeObjType.SECTION   ;  TreeObjType.BOOK
        Log.l.debug ( "SectionCellRenderer.init: Start. obj type = %s", obj.getType () );
        //Logger.getInstance().debug ("MxaCellRenderer.init: obj class = " + obj.getClass() );
        try
        {
            //Icon icon = GuiTools.createImageIcon ( iconPath, "Иконка объекта MXA в дереве" );
            // проверяем подтип/версию подтипа
            icon = getIcon();
            //Logger.getInstance().debug (".init: icon = " + icon );
            if ( icon == null )
            {
                type = obj.getType();
                switch ( type )
                {
                    case SECTION :
                        // Раздел Сборника - иконку берем по типу обьекта (Раздел)
                        icon = icons.get ( type.toString() );
                        //Logger.getInstance().debug (".init: icon 2 = " + icon );
                        if ( icon == null )
                        {
                            icon = GuiTools.createImageByFile ( obj.getTreeIconFilePath() );
                            //Logger.getInstance().debug (".init: icon 3 = " + icon );
                            if ( icon != null ) icons.put ( obj.getType().toString(), icon );
                        }
                        break;

                    case BOOK :
                        // Иконка согласно статуса книги - берем по короткому имени файла иконки статуса книги.
                        BookTitle  bookTitle;
                        BookStatus status;
                        String     statusFile;
                        bookTitle   = (BookTitle) obj;
                        status      = bookTitle.getBookStatus();
                        if ( status == null )  status = BookStatus.WORK;
                        statusFile  = status.getIcon();
                        icon        = icons.get ( statusFile );
                        if ( icon == null )
                        {
                            icon        = GuiTools.createImageByFile ( bookTitle.getTreeIconBookStatusFilePath() );
                            if ( icon != null ) icons.put ( statusFile, icon );
                        }
                        break;
                }
            }

            if ( icon != null ) treeRenderer.setIcon ( icon );

            treeRenderer.setText ( obj.getName() );

            //Logger.getInstance().debug ("init: Finish" );

        //} catch ( WEditException we ) {
        //    throw we;
        } catch ( Exception ex ) {
            Log.l.error ( Convert.concatObj ( "ERROR. Obj = ", obj ), ex);
            throw new WEditException ( ex, "Section : ", ex );
        }
    }
    
}
