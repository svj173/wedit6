package svj.wedit.v6.gui.dialog.icon;


import svj.wedit.v6.WCons;
import svj.wedit.v6.tools.FileTools;
import svj.wedit.v6.tools.GuiTools;

import javax.swing.*;
import javax.swing.filechooser.FileView;
import java.io.File;


/**
 * Показывет иконкой тип картинки - jpg, gif...
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.08.2011 13:10:01
 */
public class ImageFileView extends FileView
{
    ImageIcon jpgIcon = GuiTools.createImageByFile("img/imgType/jpgIcon.gif");
    ImageIcon gifIcon = GuiTools.createImageByFile("img/imgType/gifIcon.gif");
    ImageIcon tiffIcon = GuiTools.createImageByFile("img/imgType/tiffIcon.gif");
    ImageIcon pngIcon = GuiTools.createImageByFile("img/imgType/pngIcon.png");

    public String getName( File f) {
        return null; //let the L&F FileView figure this out
    }

    public String getDescription(File f) {
        return null; //let the L&F FileView figure this out
    }

    public Boolean isTraversable(File f) {
        return null; //let the L&F FileView figure this out
    }

    public String getTypeDescription(File f) {
        String extension = FileTools.getExtension(f);
        String type = null;

        if (extension != null) {
            if (extension.equals( WCons.JPEG) ||
                extension.equals(WCons.JPG)) {
                type = "JPEG Image";
            } else if (extension.equals(WCons.GIF)){
                type = "GIF Image";
            } else if (extension.equals(WCons.TIFF) ||
                       extension.equals(WCons.TIF)) {
                type = "TIFF Image";
            } else if (extension.equals(WCons.PNG)){
                type = "PNG Image";
            }
        }
        return type;
    }

    public Icon getIcon(File f)
    {
        String extension = FileTools.getExtension(f);
        Icon icon = null;

        if (extension != null) {
            if (extension.equals(WCons.JPEG) ||
                extension.equals(WCons.JPG)) {
                icon = jpgIcon;
            } else if (extension.equals(WCons.GIF)) {
                icon = gifIcon;
            } else if (extension.equals(WCons.TIFF) ||
                       extension.equals(WCons.TIF)) {
                icon = tiffIcon;
            } else if (extension.equals(WCons.PNG)) {
                icon = pngIcon;
            }
        }
        return icon;
    }

}
