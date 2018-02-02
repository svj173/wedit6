package svj.wedit.v6.tools;


import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 31.01.2013 9:22
 */
public final class RGBGrayFilter extends RGBImageFilter
{

    /**
     * Overrides default constructor; prevents instantiation.
     */
    private RGBGrayFilter() {
        canFilterIndexColorModel = true;
    }


    /**
     * Returns an icon with a disabled appearance. This method is used
     * to generate a disabled icon when one has not been specified.
     *
     * @param component the component that will display the icon, may be null.
     * @param icon the icon to generate disabled icon from.
     * @return disabled icon, or null if a suitable icon can not be generated.
     */
    public static Icon getDisabledIcon(JComponent component, Icon icon) {
        if (   (icon == null)
            || (component == null)
            || (icon.getIconWidth() == 0)
            || (icon.getIconHeight() == 0)) {
            return null;
        }
        Image img;
        if (icon instanceof ImageIcon) {
            img = ((ImageIcon) icon).getImage();
        } else {
            img = new BufferedImage(
                    icon.getIconWidth(),
                    icon.getIconHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            icon.paintIcon(component, img.getGraphics(), 0, 0);
        }
        if (   !RGBGrayFilter.isHiResGrayFilterEnabled()
            || (Boolean.FALSE.equals(component.getClientProperty(GuiTools.HI_RES_DISABLED_ICON_CLIENT_KEY)))) {
            return new ImageIcon(GrayFilter.createDisabledImage(img));
        }

        ImageProducer producer =
            new FilteredImageSource(img.getSource(), new RGBGrayFilter());

        return new ImageIcon(component.createImage(producer));
    }


    /**
     * Converts a single input pixel in the default RGB ColorModel to a single
     * gray pixel.
     *
     * @param x    the horizontal pixel coordinate
     * @param y    the vertical pixel coordinate
     * @param rgb  the integer pixel representation in the default RGB color model
     * @return a gray pixel in the default RGB color model.
     *
     * @see ColorModel#getRGBdefault
     * @see #filterRGBPixels
     */
    @Override
    public int filterRGB(int x, int y, int rgb) {
        // Find the average of red, green, and blue.
        float avg = (((rgb >> 16) & 0xff) / 255f +
                     ((rgb >>  8) & 0xff) / 255f +
                      (rgb        & 0xff) / 255f) / 3;
        // Pull out the alpha channel.
        float alpha = (((rgb >> 24) & 0xff) / 255f);

        // Calculate the average.
        // Sun's formula: Math.min(1.0f, (1f - avg) / (100.0f / 35.0f) + avg);
        // The following formula uses less operations and hence is faster.
        avg = Math.min(1.0f, 0.35f + 0.65f * avg);
        // Convert back into RGB.
       return (int) (alpha * 255f) << 24 |
              (int) (avg   * 255f) << 16 |
              (int) (avg   * 255f) << 8  |
              (int) (avg   * 255f);
    }

    /**
     * Checks and answers whether the new high-resolution gray filter
     * is enabled or disabled. It is enabled by default.
     *
     * @return true if the high-resolution gray filter is enabled, false if disabled
     *
     * @since 2.1
     */
    public static boolean isHiResGrayFilterEnabled()
    {
        return !Boolean.FALSE.equals( UIManager.get ( GuiTools.HI_RES_GRAY_FILTER_ENABLED_KEY ));
    }


}
