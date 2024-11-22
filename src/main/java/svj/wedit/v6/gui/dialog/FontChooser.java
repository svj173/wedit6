package svj.wedit.v6.gui.dialog;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 23.08.2011 19:22:37
 */

import svj.wedit.v6.WCons;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class FontChooser extends JDialog
{
    private int closedOption = JOptionPane.CLOSED_OPTION;
    protected InputList fontNameInputList = new InputList ( fontNames, "Name:" );
    protected InputList fontSizeInputList = new InputList ( fontSizes, "Size:" );
    protected MutableAttributeSet attributes;
    protected JCheckBox boldCheckBox = new JCheckBox ( "Bold" );
    protected JCheckBox italicCheckBox = new JCheckBox ( "Italic" );
    protected JCheckBox underlineCheckBox = new JCheckBox ( "Underline" );
    protected JCheckBox strikethroughCheckBox = new JCheckBox ( "Strikethrough" );
    protected JCheckBox subscriptCheckBox = new JCheckBox ( "Subscript" );
    protected JCheckBox superscriptCheckBox = new JCheckBox ( "Superscript" );
    //protected ColorComboBox colorComboBox;
    protected FontLabel previewLabel;
    public static String[] fontNames;
    public static String[] fontSizes;
    private Font font;
    private final JButton colorViewLabel;


    static
    {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        fontNames = ge.getAvailableFontFamilyNames();
        fontSizes = new String[] {
                "8", "9", "10", "11", "12", "14", "16",
                "18", "20", "22", "24", "26", "28", "36", "48", "72" };
    }


    public FontChooser ( JDialog owner )
    {
        super ( owner, "Выбрать шрифт", true );

        ActionListener actionListener;
        JPanel panel, p, p1;
        JLabel lbl;
        JButton btOK;

        setLayout ( new BorderLayout() );

        panel   = new JPanel();
        add ( panel, BorderLayout.CENTER );

        //getContentPane().setLayout ( new BoxLayout ( getContentPane(), BoxLayout.Y_AXIS ) );
        panel.setLayout ( new BoxLayout ( panel, BoxLayout.Y_AXIS ) );

        setLocationRelativeTo ( null );  // располагать диалог в центре экрана
        setDefaultCloseOperation ( WindowConstants.DISPOSE_ON_CLOSE );
        setResizable ( true );

        // ---------- font --------------
        p = new JPanel ( new GridLayout ( 1, 2, 10, 2 ) );
        p.setBorder ( new TitledBorder ( new EtchedBorder(), "Font" ) );
        p.add ( fontNameInputList );
        fontNameInputList.setDisplayedMnemonic ( 'n' );
        fontNameInputList.setToolTipText ( "Font name" );

        p.add ( fontSizeInputList );
        fontSizeInputList.setDisplayedMnemonic ( 's' );
        fontSizeInputList.setToolTipText ( "Font size" );
        panel.add ( p );

        p = new JPanel ( new GridLayout ( 2, 3, 10, 5 ) );
        p.setBorder ( new TitledBorder ( new EtchedBorder (), "Effects" ) );
        boldCheckBox.setMnemonic ( 'b' );
        boldCheckBox.setToolTipText ( "Bold font" );
        p.add ( boldCheckBox );

        italicCheckBox.setMnemonic ( 'i' );
        italicCheckBox.setToolTipText ( "Italic font" );
        p.add ( italicCheckBox );

        underlineCheckBox.setMnemonic ( 'u' );
        underlineCheckBox.setToolTipText ( "Underline font" );
        p.add ( underlineCheckBox );

        strikethroughCheckBox.setMnemonic ( 'r' );
        strikethroughCheckBox.setToolTipText ( "Strikethrough font" );
        p.add ( strikethroughCheckBox );

        subscriptCheckBox.setMnemonic ( 't' );
        subscriptCheckBox.setToolTipText ( "Subscript font" );
        p.add ( subscriptCheckBox );

        superscriptCheckBox.setMnemonic ( 'p' );
        superscriptCheckBox.setToolTipText ( "Superscript font" );
        p.add ( superscriptCheckBox );
        panel.add ( p );

        panel.add ( Box.createVerticalStrut ( 5 ) );

        // ----------- color -- квадрат с выбраным цветом - она же кнокпа вызова JColorChooser ---------
        p = new JPanel ( new BorderLayout ( 10, 5 ) );
        p.setBorder ( new TitledBorder ( new EtchedBorder(), "Color" ) );
        // квадрат
        colorViewLabel = new JButton ( "  " );
        colorViewLabel.setBorder ( BorderFactory.createEtchedBorder ( Color.BLACK, Color.GRAY ) );
        //colorViewLabel.setOpaque ( false ); // default = false
        colorViewLabel.setBackground ( Color.WHITE );
        p.add ( colorViewLabel, BorderLayout.CENTER );
        // кнопка
        colorViewLabel.addActionListener ( new ActionListener()
        {
          public void actionPerformed ( ActionEvent e )
          {
              Color c;
              c = JColorChooser.showDialog ( ((Component) e.getSource()).getParent(), "Цвет шрифта", colorViewLabel.getBackground() );
              if ( c != null ) colorViewLabel.setBackground ( c );
          }
        });
        panel.add ( p );

        p = new JPanel ( new BorderLayout () );
        p.setBorder ( new TitledBorder ( new EtchedBorder (), "Preview" ) );
        previewLabel = new FontLabel ( "Предварительный просмотр выбранного шрифта" );

        p.add ( previewLabel, BorderLayout.CENTER );
        panel.add ( p );

        p       = new JPanel ( new FlowLayout () );
        p1      = new JPanel ( new GridLayout ( 1, 2, 10, 2 ) );
        btOK    = new JButton ( "OK" );
        btOK.setToolTipText ( "Сохранить результат и закончить работу" );
        actionListener = new ActionListener()
        {
            public void actionPerformed ( ActionEvent e )
            {
                setStatus ( JOptionPane.OK_OPTION );
            }
        };
        btOK.addActionListener ( actionListener );
        p1.add ( btOK );

        JButton btCancel = new JButton ( "Cancel" );
        btCancel.setToolTipText ( "Exit without save" );
        actionListener = new ActionListener()
        {
            public void actionPerformed ( ActionEvent e )
            {
                setStatus ( JOptionPane.CANCEL_OPTION );
            }
        };
        btCancel.addActionListener ( actionListener );
        p1.add ( btCancel );
        p.add ( p1 );
        panel.add ( p );

        pack ();

        ListSelectionListener listSelectListener = new ListSelectionListener()
        {
            public void valueChanged ( ListSelectionEvent e )
            {
                updatePreview ();
            }
        };
        fontNameInputList.addListSelectionListener ( listSelectListener );
        fontSizeInputList.addListSelectionListener ( listSelectListener );

        actionListener = new ActionListener()
        {
            public void actionPerformed ( ActionEvent e )
            {
                updatePreview ();
            }
        };
        boldCheckBox.addActionListener ( actionListener );
        italicCheckBox.addActionListener ( actionListener );
    }

    private void setStatus ( int option )
    {
        closedOption = option;
        //System.out.println ( "closedOption = " + closedOption );
        setVisible ( false );
        dispose();
    }

    public AttributeSet getAttributes ()
    {
        if ( attributes == null )   return null;

        StyleConstants.setFontFamily ( attributes, fontNameInputList.getSelected() );
        StyleConstants.setFontSize ( attributes, fontSizeInputList.getSelectedInt() );
        StyleConstants.setBold ( attributes, boldCheckBox.isSelected() );
        StyleConstants.setItalic ( attributes, italicCheckBox.isSelected() );
        StyleConstants.setUnderline ( attributes, underlineCheckBox.isSelected() );
        StyleConstants.setStrikeThrough ( attributes, strikethroughCheckBox.isSelected() );
        StyleConstants.setSubscript ( attributes, subscriptCheckBox.isSelected() );
        StyleConstants.setSuperscript ( attributes, superscriptCheckBox.isSelected() );
        StyleConstants.setForeground ( attributes, colorViewLabel.getBackground() );

        return attributes;
    }

    public int getOption ()
    {
        return closedOption;
    }

    public void setAttributes ( AttributeSet a )
    {
        attributes = new SimpleAttributeSet ( a );
        String name = StyleConstants.getFontFamily ( a );
        fontNameInputList.setSelected ( name );
        int size = StyleConstants.getFontSize ( a );
        fontSizeInputList.setSelectedInt ( size );
        boldCheckBox.setSelected ( StyleConstants.isBold ( a ) );
        italicCheckBox.setSelected ( StyleConstants.isItalic ( a ) );
        underlineCheckBox.setSelected ( StyleConstants.isUnderline ( a ) );
        strikethroughCheckBox.setSelected ( StyleConstants.isStrikeThrough ( a ) );
        subscriptCheckBox.setSelected ( StyleConstants.isSubscript ( a ) );
        superscriptCheckBox.setSelected ( StyleConstants.isSuperscript ( a ) );
        //colorComboBox.setSelectedItem ( StyleConstants.getForeground ( a ) );
        colorViewLabel.setBackground ( StyleConstants.getForeground ( a ) );

        updatePreview ();
    }

    public void init ( Font initFont, Color color )
    {
        int style;

        if ( initFont == null )
            this.font   = WCons.TEXT_FONT_1;
        else
            this.font   = initFont;

        if ( color == null )
            color   = Color.BLACK;

        //System.out.println ( "init. font = " + font );
        //System.out.println ( "init. color = " + color );

        fontNameInputList.setSelected ( font.getName() );
        fontSizeInputList.setSelectedInt ( font.getSize() );

        style   = font.getStyle();
        switch ( style )
        {
            case Font.PLAIN:
                boldCheckBox.setSelected ( false );
                italicCheckBox.setSelected ( false );
                break;
            case Font.BOLD:
                boldCheckBox.setSelected ( true );
                italicCheckBox.setSelected ( false );
                break;
            case Font.ITALIC:
                boldCheckBox.setSelected ( false );
                italicCheckBox.setSelected ( true );
                break;
            case Font.BOLD|Font.ITALIC:
                boldCheckBox.setSelected ( true );
                italicCheckBox.setSelected ( true );
                break;
        }

        //previewLabel.setForeground ( color );
        //colorComboBox.setSelectedItem ( color );
        colorViewLabel.setBackground ( color );

        updatePreview ();
    }

    protected void updatePreview ()
    {
        String name = fontNameInputList.getSelected ();
        int size = fontSizeInputList.getSelectedInt ();
        if ( size <= 0 )
            return;
        int style = Font.PLAIN;
        if ( boldCheckBox.isSelected() )
            style |= Font.BOLD;
        if ( italicCheckBox.isSelected () )
            style |= Font.ITALIC;

        font = new Font ( name, style, size );
        previewLabel.setFont ( font );

        //Color c = ( Color ) colorComboBox.getSelectedItem ();
        Color c = colorViewLabel.getBackground ();
        previewLabel.setForeground ( c );
        previewLabel.repaint();
    }

    public Font getResult ()
    {
        return font;
    }

    public Color getColor ()
    {
        //return ( Color ) colorComboBox.getSelectedItem ();
        return colorViewLabel.getBackground ();
    }

    /*
    public static void main ( String argv[] )
    {
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment ();
        fontNames = ge.getAvailableFontFamilyNames ();
        fontSizes = new String[] {
                "8", "9", "10", "11", "12", "14", "16",
                "18", "20", "22", "24", "26", "28", "36", "48", "72" };

        FontChooser dlg = new FontChooser ( new JFrame () );
        SimpleAttributeSet a = new SimpleAttributeSet ();
        StyleConstants.setFontFamily ( a, "Monospaced" );
        StyleConstants.setFontSize ( a, 12 );
        dlg.setAttributes ( a );
        dlg.show ();
    }
    */

}


class FontLabel extends JLabel
{
    public FontLabel ( String text )
    {
        super ( text, JLabel.CENTER );
        setBackground ( Color.white );
        setForeground ( Color.black );
        setOpaque ( true );
        setBorder ( new LineBorder ( Color.black ) );
        setPreferredSize ( new Dimension ( 120, 40 ) );
    }
}

class ColorComboBox extends JComboBox
{

    public ColorComboBox ()
    {
        int[] values = new int[] { 0, 64, 128, 192, 255 };
        for ( int r = 0; r < values.length; r++ )
            for ( int g = 0; g < values.length; g++ )
                for ( int b = 0; b < values.length; b++ )
                {
                    Color c = new Color ( values[ r ], values[ g ], values[ b ] );
                    addItem ( c );
                }
        setRenderer ( new ColorComboRenderer1() );
    }

    class ColorComboRenderer1 extends JPanel implements ListCellRenderer
    {
        protected Color m_c = Color.black;

        public ColorComboRenderer1 ()
        {
            super ();
            setBorder ( new CompoundBorder ( new MatteBorder ( 2, 10, 2, 10, Color.white ), new LineBorder ( Color.black ) ) );
        }

        public Component getListCellRendererComponent ( JList list, Object obj, int row, boolean sel, boolean hasFocus )
        {
            if ( obj instanceof Color )
                m_c = ( Color ) obj;
            return this;
        }

        public void paint ( Graphics g )
        {
            setBackground ( m_c );
            super.paint ( g );
        }

    }

}
