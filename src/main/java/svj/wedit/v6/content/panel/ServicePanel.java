package svj.wedit.v6.content.panel;


import svj.wedit.v6.gui.panel.RewritePanel;

import javax.swing.*;
import java.awt.*;


/**
 * Панель для отображения разных мелких состояний.
 * Располагается внизу, узкой полоской.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 27.07.2011 18:00:50
 */
public class ServicePanel extends RewritePanel
{
    private JLabel  memoryLabel, statusMsg, javaVersion;


    public ServicePanel ()
    {
        //Border border;

        //border = BorderFactory.createEtchedBorder(Color.white,new Color(178, 178, 178));

        //setLayout ( new BorderLayout ());
        setLayout ( new BoxLayout(this, BoxLayout.LINE_AXIS) );

        // java version
        javaVersion = new JLabel ( "java: "+System.getProperty("java.version") );
        //javaVersion.setFont ( new Font ("Monospaced", Font.BOLD, 14) );
        javaVersion.setForeground ( Color.GREEN );
        javaVersion.setToolTipText ( "Версия Java." );
        add ( javaVersion );

        // статус
        statusMsg = new JLabel();
        //statusMsg.setBorder(border);
        add ( statusMsg );

        // Сдвигаем метку ОЗУ крайне вправо
        add ( Box.createHorizontalGlue() );

        // memory
        memoryLabel = new JLabel();
        //memoryLabel.setBorder(border);
        memoryLabel.setToolTipText ( "Память: используется / выделено / максимально_возможная." );
        add ( memoryLabel );
    }


    public JLabel getMemoryLabel ()
    {
        return memoryLabel;
    }


    public void addComponent ( JComponent jc )
    {
       add ( jc, 0 );
    }

    public void setStatusText ( String text )
    {
        statusMsg.setText ( text );
    }

    //public void setJavaVersion ()

    @Override
    public void rewrite () //throws WEditException
    {
    }
    
}
