package svj.wedit.v6.gui.dialog.icon;


import svj.wedit.v6.tools.Convert;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;


/**
 * Панель просмотра выбранных картинок.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.08.2011 13:16:37
 */
public class ImagePreview extends JComponent implements PropertyChangeListener
{
    ImageIcon   thumbnail   = null;
    File        file        = null;
    JLabel      iconSizeLabel;

    public ImagePreview ( JFileChooser fc, JLabel iconSizeLabel )
    {
        setPreferredSize ( new Dimension ( 100, 50 ) );
        setBorder ( BorderFactory.createEtchedBorder() );
        
        fc.addPropertyChangeListener ( this );

        this.iconSizeLabel  = iconSizeLabel;
    }

    public void propertyChange ( PropertyChangeEvent e )
    {
        boolean update;
        String  prop;

        update  = false;
        prop    = e.getPropertyName();

        if ( JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals ( prop ) )
        {
            // If the directory changed, don't show an image.
            file    = null;
            update  = true;
        }
        else if ( JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals ( prop ) )
        {
            // If a file became selected, find out which one.
            file    = ( File ) e.getNewValue();
            update  = true;
        }

        //Update the preview accordingly.
        if ( update )
        {
            thumbnail = null;
            if ( isShowing() )
            {
                loadImage();
                repaint();
            }
        }
    }

    public void paintComponent ( Graphics g )
    {
        if ( thumbnail == null )   loadImage();

        if ( thumbnail != null )
        {
            int x = getWidth()  / 2 - thumbnail.getIconWidth() / 2;
            int y = getHeight() / 2 - thumbnail.getIconHeight() / 2;

            if ( y < 0 )  y = 0;
            if ( x < 5 )  x = 5;

            thumbnail.paintIcon ( this, g, x, y );
        }
    }

    public void loadImage ()
    {
        if ( file == null )
        {
            thumbnail = null;
            return;
        }

        //Don't use createImageIcon (which is a wrapper for getResource)
        //because the image we're trying to load is probably not one
        //of this program's own resources.
        ImageIcon tmpIcon = new ImageIcon ( file.getPath() );
        if ( tmpIcon != null )
        {
            // вычислить размер картинки
            iconSizeLabel.setText ( Convert.concatObj ( "w:", tmpIcon.getIconWidth(), "; h:", tmpIcon.getIconHeight()) );
            if ( tmpIcon.getIconWidth() > 90 )
            {
                // Уменьшить картинку
                Image image = tmpIcon.getImage ();
                int k = tmpIcon.getIconWidth() / 90;
                int height = tmpIcon.getIconHeight() / k;
                //thumbnail = new ImageIcon ( image.getScaledInstance ( 90, height, Image.SCALE_DEFAULT ) );
                setPreferredSize ( new Dimension ( 90, height ) );
                thumbnail = new ImageIcon ( image.getScaledInstance ( 90, height, Image.SCALE_REPLICATE ) );
            }
            else
            {
                //no need to miniaturize
                thumbnail = tmpIcon;
            }
        }
    }

}
