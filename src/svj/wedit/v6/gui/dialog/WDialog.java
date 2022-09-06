package svj.wedit.v6.gui.dialog;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.listener.CloseWindowListener;
import svj.wedit.v6.gui.listener.EltexKeyAdapter;
import svj.wedit.v6.gui.panel.WPanel;
import svj.wedit.v6.handler.CloseHandler;
import svj.wedit.v6.obj.WorkResult;
import svj.wedit.v6.tools.GuiTools;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


/**
 * Общий диалог.
 * <BR/> Содержит:
 * <BR/> - параметр возврата
 * <BR/> - панель с кнопками OK, Cancel
 * <BR/> - листенеры на кнопках
 * <BR/>
 * <BR/> объекты описания:
 * <BR/> - T - объект, который первоначально заносится в диалог (какая-то необходимая диалогу информация)
 * <BR/> - M - объект, который возвращается диалогом (результат работы)
 * <BR/>
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 27.07.2011 10:22:40
 */
public abstract class WDialog<T,M>   extends JDialog implements CloseHandler
{
    private WorkResult      returnStatus;

    /* Обработка клавиш - Enter, Esc */
    private EltexKeyAdapter keyAdapter;

    private JPanel          buttonPanel, internalButtonPanel;
    private JButton         jButtonCancel, jButtonOk;

    /* Собственно панель диалога - вокруг нее строятся все компоненты, а не вокруг контейнера диалога.
     * Т.к. опция dialog.add добавить контейнер не в rootPanel, а в glassPane. И при уходе с диалога и последующем
     * возврате - фокус передастся в rootPanel и компоненты из glassPane будут недоступны. */
    private JPanel          rootPanel;


    /* Занести исходный объект */
    public abstract void init ( T initObject ) throws WEditException;

    /* Получить результат работы диалога по функции 'ОК'. */
    public abstract M getResult () throws WEditException;


    public WDialog ( Dialog parent, String title )
    {
        super ( parent, true );

        initComponents ( title );
    }

    public WDialog ( Window parent, String title )
    {
        super ( parent, ModalityType.APPLICATION_MODAL );

        initComponents ( title );
    }

    public WDialog ( String title )
    {
        super ( Par.GM.getFrame(), true );

        initComponents ( title );
    }

    private void initComponents ( String title )
    {
        CloseWindowListener closeListener;
        ActionListener      listener;

        startInit();
        rootPanel   = new JPanel();
        rootPanel.setLayout ( new BorderLayout(5,5) );
        //getContentPane().add ( rootPanel );
        setContentPane ( rootPanel );

        //setLocationRelativeTo ( null );  // располагать диалог в центре экрана
        setDefaultCloseOperation ( WindowConstants.DISPOSE_ON_CLOSE );
        setTitle ( title );

        // Обработать закрывание диалога методом close(). Здесь это пустышка, необходимо переписывать метод close.
        closeListener   = new CloseWindowListener (this);
        addWindowListener ( closeListener );

        keyAdapter = new EltexKeyAdapter (this);

        buttonPanel = createButtonPanel();
        buttonPanel.setOpaque ( true );
        rootPanel.add ( buttonPanel, BorderLayout.SOUTH );

        // Вешаем на диалог - слушатель клавиатуры для ESC - когда диалог в фокусе.
        listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doCancel();
            }
        };
        getRootPane().registerKeyboardAction ( listener, KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW );

        // Вешаем на диалог - слушатель клавиатуры для Enter - когда диалог в фокусе.
        listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doOk();
            }
        };
        getRootPane().registerKeyboardAction ( listener, KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW );
    }

    /**
     * Установить размер диалога - в пол-экрана. Если данный размер не устраивает - переписать метод - здесь НЕ исправлять!
     */
    protected void createDialogSize ()
    {
        int  ic, width, height;

        ic          = Par.SCREEN_SIZE.width / 2;
        width       = ic + ic / 2;
        height      = Par.SCREEN_SIZE.height / 2;
        setPreferredSize ( new Dimension(width,height) );
        setSize ( width, height );

        pack();
    }

    /* Метод дергается ВСЕГДА при физическом закрытии диалога (в прикрученном CloseWindowListener).
       Желающие диалоги могут его переписать под себя. (added by svj, 2010-11-24) */
    @Override
    public void close()          {    }

    public void startInit ()
    {
        returnStatus = WorkResult.CANCEL;
    }

    public void showDialog ()
    {
        // Рассчитать размер диалога в зависимости от размера экрана
        createDialogSize();

        // центрируем
        GuiTools.setDialogScreenCenterPosition ( this );
        // чтобы фокус не пропадал с диалога, когда тыкаем мышкой мимо диалога.
        requestFocus();
        setVisible ( true );
    }

    private JPanel createButtonPanel ()
    {
        WPanel  jPanel2;

        jPanel2 = new WPanel ();
        jPanel2.setInsets (5,5,5,5);
        jPanel2.setBorder( BorderFactory.createEtchedBorder ( EtchedBorder.RAISED ) );

        // делаем вторую панель - чтобы кнопки Принять/Отменить не растягивались на всю ширину диалога.
        internalButtonPanel   = new JPanel( new FlowLayout ( FlowLayout.CENTER,5,5) );
        //bp.setBorder( BorderFactory.createEtchedBorder ( EtchedBorder.RAISED ) );
        //bp.add ( buttonPanel );

        jPanel2.setLayout(new BorderLayout(5, 5));
        //jPanel2.setLayout ( new GridLayout(1,2,5,5) );
        //jPanel2.setLayout ( new FlowLayout(50,5, FlowLayout.CENTER) );

        jButtonOk       = new JButton();
        jButtonCancel   = new JButton();

        jButtonOk.setText("Принять");
        jButtonOk.addActionListener ( new ActionListener() {
            @Override
            public void actionPerformed ( ActionEvent evt) {
                doOk();
            }
        });
        //jPanel2.add(jButtonOk, BorderLayout.WEST);
        internalButtonPanel.add ( jButtonOk );
        jButtonOk.addKeyListener ( keyAdapter );

        /*
        // Акция на нажатие кнопки Энтер - почему -то не сработало в диалоге Сохранения книг
        Action close = new AbstractAction("CLOSE")
        {
            public void actionPerformed(ActionEvent e)
            {
                Log.l.info("[B] Start action");
                doOk();
            }
        };
        jButtonOk.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "CLOSE");
        jButtonOk.getActionMap().put("CLOSE", close);
        */

        jButtonCancel.setText("Отменить");
        jButtonCancel.addActionListener ( new ActionListener() {
            @Override
            public void actionPerformed ( ActionEvent evt) {
                doCancel();
            }
        });
        //jPanel2.add(jButtonCancel, BorderLayout.EAST);
        internalButtonPanel.add ( jButtonCancel );
        jButtonCancel.addKeyListener ( keyAdapter );

        jPanel2.add ( internalButtonPanel, BorderLayout.CENTER );

        return jPanel2;
    }

    public WorkResult getReturnStatus()
    {
        return returnStatus;
    }

    public boolean isOK()
    {
        return returnStatus == WorkResult.OK;
    }

    public void doClose ( WorkResult retStatus )
    {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    /* Это int-константы из ряда JOptionPane: YES_OPTION, NO_OPTION, CANCEL_OPTION, OK_OPTION, CLOSED_OPTION. */
    @Override
    public void doClose ( int closeType )
    {
        //Log.l.info("[B] closeType = %d", closeType);
        switch ( closeType )
        {
            case JOptionPane.YES_OPTION:
                doOk();
                break;
            case JOptionPane.NO_OPTION:
            case JOptionPane.CANCEL_OPTION:
            case JOptionPane.CLOSED_OPTION:
                doCancel();
                break;
        }
        dispose();
    }


    public void doCancel ()
    {
        doClose ( WorkResult.CANCEL );
    }

    public void doOk ()
    {
        doClose ( WorkResult.OK );
    }

    public EltexKeyAdapter getKeyAdapter ()
    {
        return keyAdapter;
    }

    public JButton getOkButton ()
    {
        return jButtonOk;
    }

    public void setReturnStatus ( WorkResult returnStatus )
    {
        this.returnStatus = returnStatus;
    }

    public void setOkButtonText ( String text )
    {
        jButtonOk.setText ( text );
    }

    public void addToCenter ( Component comp )
    {
        rootPanel.add ( comp, BorderLayout.CENTER );
    }

    public void addToEast ( Component comp )
    {
        rootPanel.add ( comp, BorderLayout.EAST );
    }

    public void addToNorth ( Component comp )
    {
        rootPanel.add ( comp, BorderLayout.NORTH );
    }

    public void addToSouth ( Component comp )
    {
        rootPanel.add ( comp, BorderLayout.SOUTH );
    }

    public void addToWest ( Component comp )
    {
        rootPanel.add ( comp, BorderLayout.WEST );
    }

    public void disableOkButton ()
    {
        jButtonOk.setVisible ( false );
    }

    public void setOkButtonImage(Icon icon){
        jButtonOk.setIcon ( icon );
    }

    public void disableCancelButton ()
    {
        jButtonCancel.setVisible ( false );
    }

    public void enableCancelButton ()
    {
        jButtonCancel.setVisible ( true );
    }

    public void setCancelButtonText ( String text )
    {
        jButtonCancel.setText ( text );
    }

    public void setCancelButtonTooltip ( String text )
    {
        jButtonCancel.setToolTipText( text );
    }

    public void setOkButtonTooltip ( String text )
    {
        jButtonOk.setToolTipText( text );
    }

    public void setOkButtonEnable ( boolean enable )
    {
        jButtonOk.setEnabled ( enable );
    }

    public void addButton ( Component comp )
    {
        internalButtonPanel.add ( comp );
    }

    public void addButtonFirst ( Component comp )
    {
        internalButtonPanel.add ( comp, 0 );
    }

}
