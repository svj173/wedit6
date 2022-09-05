package svj.wedit.v6.gui.dialog;


import svj.wedit.v6.Par;
import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;

import javax.swing.*;

import java.awt.*;


/**
 * Вывод HTML.
 * <BR/> Текст приходит в HTML виде, и запихивается в графический редактор JEditorPane.
 * <BR/> Не редактируемый.
 * <BR/> Есть возможность копировать текст с экрана.
 * <BR/> Есть второй дополнительный метод инициализации, при использовании которого
 * выводится дополнительная информация вверху диалога.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 23.01.2012 14:04:44
 */
public class ShowImageDialog extends WDialog<String,Void>
{
    //private JEditorPane textPane;
    //private JEditorPane smallPane;
    private JPanel      topPanel;

    /** Для устанволения ширины диалога - делитель для ширины экрана. */
    //private final int         widthDiv;


    public ShowImageDialog(String imageFileName )
    {
        super ( imageFileName );

        JPanel          panel, panelTextPane;
        JScrollPane     scroll;

        //this.widthDiv = widthDiv;

        panel       = new JPanel();

        panel.setLayout ( new BorderLayout() );
        panel.setBackground ( WCons.DARK_BLUE );


        scroll      = new JScrollPane();
        scroll.setAutoscrolls(true);
        scroll.setHorizontalScrollBarPolicy ( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );

        JLabel label = new JLabel();
        ImageIcon image = new ImageIcon(imageFileName);
        label.setIcon(image);

        scroll.setEnabled ( true );
        scroll.setViewportView ( label );
        scroll.getVerticalScrollBar().setUnitIncrement(15);

        //panel.add ( topPanel, BorderLayout.NORTH );
        panel.add ( scroll, BorderLayout.CENTER );

        //add ( panel, BorderLayout.CENTER );
        addToCenter ( panel );

        // оставляем только одну кнопку - с надписью - Закрыть
        disableOkButton();
        //disableCancelButton();
        setCancelButtonText ( "Закрыть" );

        Dimension size = createDialogSize2 (image);
        setPreferredSize(size);

        pack ();
    }

    private Dimension createDialogSize2 (ImageIcon image)
    {
            int width, height;

            width = image.getIconWidth();
            height = image.getIconHeight();

            if (width > height) {
                // ширина картинки больше высоты - уменьшаем диалог по ширине
                if (width > Par.SCREEN_SIZE.width)
                {
                    int w = Par.SCREEN_SIZE.width - 100;
                    // пропорциаонально уменьшаем ширину
                    double k = (double)width / (double)w;
                    width = w;
                    if (k > 0) {
                        double h = (double)height / k;
                        height = (int) h;
                    }
                }
            }
            else
            {
                if (height > Par.SCREEN_SIZE.height)
                {
                    // уменьшили высоту
                    int h = Par.SCREEN_SIZE.height - 100;
                    // пропорциаонально уменьшаем ширину
                    double k = (double)height / (double)h;
                    height = h;
                    if (k > 0) {
                        double w = (double)width / k;
                        width = (int) w;
                    }
                }
            }

            return new Dimension ( width, height );
    }


    @Override
    public void init(String initObject) throws WEditException {
    }

    protected void createDialogSize () {
    }
    
    @Override
    public Void getResult () throws WEditException
    {
        return null;
    }
    
}
