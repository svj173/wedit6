package svj.wedit.v6.gui.widget;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.dialog.icon.ImageFileView;
import svj.wedit.v6.gui.dialog.icon.ImageFilter;
import svj.wedit.v6.gui.dialog.icon.ImagePreview;
import svj.wedit.v6.tools.GuiTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


/**
 * Виджет выборки иконок из файлов. С пре-просмотром.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.08.2011 12:11:22
 */
public class IconWidget   extends AbstractDialogWidget<Icon>
{
    /* Метка с отображением Иконки.*/
    private final JLabel iconLabel;
    private JFileChooser fc = null;
    private Icon         icon;
    private File         iconFile, currentDir;


    public IconWidget ( String titleName, boolean hasEmpty, String fileDirStr )
    {
        this ( titleName, hasEmpty, new File ( fileDirStr ) );
    }

    /**
     *
     * @param titleName  Титл
     * @param hasEmpty   TRUE - может быть пустым
     * @param fileDir  Текущая Поддиректория
     */
    public IconWidget ( String titleName, boolean hasEmpty, File fileDir )
    {
        super ( titleName, hasEmpty );

        JButton button;

        iconLabel   = new JLabel();
        add ( iconLabel );

        icon        = null;
        iconFile    = null;
        currentDir  = fileDir;

        // кнопка вызова диалога
        button  = new JButton ("..");
        button.addActionListener ( new ActionListener()
        {
            @Override
            public void actionPerformed ( ActionEvent e )
            {
                if ( fc == null )
                {
                    fc = new JFileChooser();

                    // Add a custom file filter and disable the default
                    // (Accept All) file filter.
                    fc.addChoosableFileFilter ( new ImageFilter() );
                    fc.setAcceptAllFileFilterUsed ( false );

                    // Add custom icons for file types.
                    fc.setFileView ( new ImageFileView() );
                    fc.setCurrentDirectory ( currentDir );

                    // Add the preview pane. -- Показывать выбранную картинку справа.
                    JPanel  panel = new JPanel(new BorderLayout ( 5,5 ));
                    JLabel iconSizeLabel= new JLabel();
                    iconSizeLabel.setHorizontalAlignment ( SwingConstants.CENTER );
                    ImagePreview comp = new ImagePreview ( fc, iconSizeLabel );
                    panel.add ( comp, BorderLayout.NORTH );
                    panel.add ( iconSizeLabel, BorderLayout.SOUTH );
                    fc.setAccessory ( panel );
                }

                // Show it.
                int returnVal = fc.showDialog ( getDialog(), "Выбрать картинку" );

                // Process the results.
                if ( returnVal == JFileChooser.APPROVE_OPTION )
                {
                    String filePath;
                    // Взять файл
                    iconFile = fc.getSelectedFile();
                    filePath = iconFile.getAbsolutePath();
                    /*
                    if ( ! filePath.contains ( imgSubdir ) )
                    {
                        //log.append ( "Attaching file: " + file.getName ()  + "." + newline );
                        //  Если он не находится в директории заданных картинок - занести его в эту директорию
                    }
                    */
                    // Получить картинку как результат
                    icon    = GuiTools.createImageByFile ( filePath );
                    iconLabel.setIcon ( icon );
                }
                else
                {
                    //log.append ( "Attachment cancelled by user." + newline );
                }

                // Reset the file chooser for the next time it's shown.
                fc.setSelectedFile ( null );
            }
        }
        );
        add ( button );

    }

    @Override
    public JComponent getGuiComponent ()
    {
        return iconLabel;
    }

    @Override
    protected Icon validateValue () throws WEditException
    {
        return null;
    }

    @Override
    public Icon getValue () //throws WEditException
    {
        return icon;
    }

    @Override
    public void setValue ( Icon value ) //throws WEditException
    {
    }

    @Override
    public void setEditable ( boolean value )
    {
    }

    @Override
    public void setValueWidth ( int width )
    {
        Dimension size = iconLabel.getPreferredSize ();
        iconLabel.setPreferredSize ( new Dimension ( width, size.height ) );
    }

    public File getIconFile ()
    {
        return iconFile;
    }

}
