package svj.wedit.v6.gui.dialog;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.widget.IconWidget;

import javax.swing.*;
import java.io.File;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 28.10.2012 11:13:31
 */
public class LoadImageDialog  extends WDialog<File, Icon>
{
    private IconWidget treeIconWidget;

    public LoadImageDialog ( File currentDir )
    {
        super ( "Загрузить картинку" );

        treeIconWidget = new IconWidget ( "Иконка", true, currentDir );
        //treeIconWidget.setTitleWidth ( width );

        addToNorth ( treeIconWidget );
    }
    
    /* Занести в диалог исходную директорию. */
    @Override
    public void init ( File initObject ) throws WEditException
    {
        //treeIconWidget.set
    }

    /* Выдать выбранный файл. NULL - ничего не выбрано. */
    @Override
    public Icon getResult () throws WEditException
    {
        return treeIconWidget.getValue();
    }

    public File getIconFile ()
    {
        return treeIconWidget.getIconFile();
    }

}
