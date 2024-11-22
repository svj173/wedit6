package svj.wedit.v6.gui.dialog;


import svj.wedit.v6.Par;
import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.tools.Convert;

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
public class ShowHtmlDialog extends WDialog<String,Void>
{
    private JEditorPane textPane;
    private JEditorPane smallPane;
    private JPanel      topPanel;
    /** Для устанволения ширины диалога - делитель для ширины экрана. */
    private final int         widthDiv;

    private class MP extends JPanel
    {
        @Override
        public Dimension getPreferredSize()
        {
            //return new Dimension(780, super.getPreferredSize().height);
            int w;
            if ( widthDiv > 0 )
                w = Par.SCREEN_SIZE.width / widthDiv;
            else
                w = super.getPreferredSize().width;
            return new Dimension ( w, super.getPreferredSize().height );
        }
    }


    public ShowHtmlDialog ( String title, int widthDiv )
    {
        super ( title );

        JPanel          panel, panelTextPane;
        JScrollPane     scroll;

        this.widthDiv = widthDiv;

        panel       = new JPanel();

        panel.setLayout ( new BorderLayout() );
        panel.setBackground ( WCons.DARK_BLUE );


        scroll      = new JScrollPane();
        scroll.setAutoscrolls(true);
        scroll.setHorizontalScrollBarPolicy ( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );

        textPane    = new JEditorPane();
        textPane.setEditable ( false );
        textPane.setCaretPosition(0);

        /*
        textPane.addHyperlinkListener(new HyperlinkListener() {
                    @Override
                    public void hyperlinkUpdate(HyperlinkEvent hle) {
                        if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
                            System.out.println(hle.getURL());
                            Desktop desktop = Desktop.getDesktop();
                            try {
                                desktop.browse(hle.getURL().toURI());
                            } catch (Exception ex) {
                                //ex.printStackTrace();
                            }
                        }
                    }
        });
        */
        
        panelTextPane      = new MP();
        panelTextPane.setLayout ( new BorderLayout() );
        panelTextPane.add ( textPane, BorderLayout.CENTER );

        // маленькая (узкая) панелька сверху для вывода дополнительной информации
        // например, туда выводятся версии сборки ПО, можно выводить другую инфу
        smallPane    = new JEditorPane();
        smallPane.setEditable ( false );
        smallPane.setCaretPosition(0);
        //smallPane.setText("Версия сервера: \nВерсия GUI:\n");
        
        topPanel      = new JPanel();
        topPanel.setLayout ( new BorderLayout() );
        topPanel.add ( smallPane, BorderLayout.CENTER );
        topPanel.setPreferredSize(new Dimension(780, 60));
        topPanel.setVisible(false);
        
        scroll.setEnabled ( true );
        scroll.setViewportView ( panelTextPane );
        scroll.getVerticalScrollBar().setUnitIncrement(15);

        panel.add ( topPanel, BorderLayout.NORTH );
        panel.add ( scroll, BorderLayout.CENTER );

        //add ( panel, BorderLayout.CENTER );
        addToCenter ( panel );

        // оставляем только одну кнопку - с надписью - Закрыть
        //disableOkButton();
        //setCancelButtonText ( "Закрыть" );

        disableCancelButton();
        setOkButtonText ( "Закрыть" );


        //setCancelButtonText(Msg.getMessage("system.gui.dialog.button.close"));

        // Привзяываем к этой кнопке акцию по-умолчанию (по нажатию Enter)

        /*
        setOptionType(JOptionPane.OK_CANCEL_OPTION);

        JOptionPane.showConfirmDialog (UserReference.this,
                                                                   "Удалить текущего пользователя?",
                                                                   "Удалить",
                                                                   JOptionPane.YES_NO_OPTION);
        */
    }

    /*
    public void setDefaultButton (int typeButton) {

        // Вешаем на диалог - слушатель клавиатуры для Enter - когда диалог в фокусе.
        // - а то что-то слушатель в родительском диалоге не срабатывает.

        Log.l.info("[BUTTON] set type = %d", typeButton);

        ActionListener listener;

        switch (typeButton) {
            case JOptionPane.CANCEL_OPTION:
                // Cancel
                listener = new ActionListener () {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Log.l.info("[BUTTON] Cancel");
                        doCancel();
                    }
                };
                getRootPane().registerKeyboardAction ( listener, KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW );
                break;

            case JOptionPane.OK_OPTION:
                // OK
                listener = new ActionListener () {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Log.l.info("[BUTTON] OK");
                        doOk();
                    }
                };
                getRootPane().registerKeyboardAction ( listener, KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW );
                break;
        }
    }
    */

    protected void createDialogSize ()
    {
        if ( widthDiv > 0 )
        {
            int ic, width, height;

            ic = Par.SCREEN_SIZE.width / 2;
            width = ic + ic / widthDiv;
            height = Par.SCREEN_SIZE.height / 2;
            setPreferredSize ( new Dimension ( width, height ) );
            setSize ( width, height );

            pack ();
        }
    }

	/**
	 * Установить фиксированный размер диалога справки
	 * @param width
	 * @param height
	 */
	public void setDialogPreferredSize(int width, int height){
        setPreferredSize ( new Dimension(width,height) );
        setSize ( width, height );
        pack();
	}

    public void setError ( Object... errText )
    {
        textPane.setText ( Convert.concatObj ( errText ) );
        textPane.setCaretPosition(0);
    }


    /**
     * Показать диалог инициализированный html-буфером
     * 
     * @param htmlData буфер, который предварительно заполняется текстом html
     * @throws WEditException 
     */
    public void init (String htmlData) throws WEditException {
        try {
            textPane.setContentType("text/html");
            textPane.setText(htmlData);
            textPane.setCaretPosition(0);
            topPanel.setVisible(false);
            textPane.repaint();
        } catch ( Exception e ) {
            throw new WEditException(e, "Error:\n", e);
        }
    }

    /**
     * Показать диалог инициализированный txt-буфером
     * 
     * @param textData буфер, который предварительно заполняется обычным текстом 
     * @throws WEditException 
     */
    public void initTextData(String textData) throws WEditException {
        try {
            //textPane.setContentType("text/html");
            textPane.setText(textData);
            textPane.setCaretPosition(0);
            topPanel.setVisible(false);
            textPane.repaint();
        } catch (Exception e) {
            throw new WEditException(e, "Error:\n", e);
        }
    }
    
    @Override
    public Void getResult () throws WEditException
    {
        return null;
    }
    
}
