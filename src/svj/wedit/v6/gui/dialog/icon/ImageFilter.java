package svj.wedit.v6.gui.dialog.icon;


import svj.wedit.v6.WCons;
import svj.wedit.v6.tools.FileTools;

import javax.swing.filechooser.FileFilter;
import java.io.File;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.08.2011 13:05:03
 */
public class ImageFilter extends FileFilter
{
    //Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept ( File f )
    {
        if (f.isDirectory())     return true;

        String extension = FileTools.getExtension(f);
        if (extension != null)
        {
            if (extension.equals(WCons.TIFF) ||
                extension.equals(WCons.TIF) ||
                extension.equals(WCons.GIF) ||
                extension.equals(WCons.JPEG) ||
                extension.equals( WCons.JPG) ||
                extension.equals(WCons.PNG))
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        return false;
    }

    //The description of this filter
    public String getDescription()
    {
        return "Только Images";
    }

}

