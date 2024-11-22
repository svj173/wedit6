package svj.wedit.v6.gui.dialog;


import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import static java.awt.font.TextAttribute.*;


/**
 * Диалог где можно выбрать:
 * <BR/> - Название шрифта из списка
 * <BR/> - Размер шрифта
 * <BR/> - Тип шрифта (plain, bold...)
 * <BR/> - Цвет шрифта - с возможность выборки - как простой ComboBox (в цвете)
 * <BR/> Есть поле показа примера результата. - Как HTML + Font
 * <BR/>
 * <BR/> Результат - Font
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 22.08.2011 9:33:28
 */
public class FontChooserWithPoleDialog extends JDialog
{
    protected int Closed_Option = JOptionPane.CLOSED_OPTION;
    protected InputList fontNameInputList = new InputList ( fontNames, "Name:" );
    protected InputList fontSizeInputList = new InputList ( fontSizes, "Size:" );
    protected MutableAttributeSet attributes;
    protected JCheckBox boldCheckBox = new JCheckBox ( "Bold" );
    protected JCheckBox italicCheckBox = new JCheckBox ( "Italic" );
    protected JCheckBox underlineCheckBox = new JCheckBox ( "Underline" );
    protected JCheckBox strikethroughCheckBox = new JCheckBox ( "Strikethrough" );
    protected JCheckBox subscriptCheckBox = new JCheckBox ( "Subscript" );
    protected JCheckBox superscriptCheckBox = new JCheckBox ( "Superscript" );
    protected ColorComboBox colorComboBox;
    protected FontLabel previewLabel;

    public static String[] fontNames;
    public static String[] fontSizes;

    private static final String PREVIEW_TEXT = "Preview Font";

    private Font font;


    public FontChooserWithPoleDialog ( JFrame owner )
    {
        super ( owner, "Font Chooser", false );
        getContentPane ().setLayout (
                new BoxLayout ( getContentPane (), BoxLayout.Y_AXIS ) );

        JPanel p = new JPanel ( new GridLayout ( 1, 2, 10, 2 ) );
        p.setBorder ( new TitledBorder ( new EtchedBorder (), "Font" ) );
        p.add ( fontNameInputList );
        fontNameInputList.setDisplayedMnemonic ( 'n' );
        fontNameInputList.setToolTipText ( "Font name" );

        p.add ( fontSizeInputList );
        fontSizeInputList.setDisplayedMnemonic ( 's' );
        fontSizeInputList.setToolTipText ( "Font size" );
        getContentPane ().add ( p );

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
        getContentPane ().add ( p );

        getContentPane ().add ( Box.createVerticalStrut ( 5 ) );
        p = new JPanel ();
        p.setLayout ( new BoxLayout ( p, BoxLayout.X_AXIS ) );
        p.add ( Box.createHorizontalStrut ( 10 ) );
        JLabel lbl = new JLabel ( "Color:" );
        lbl.setDisplayedMnemonic ( 'c' );
        p.add ( lbl );
        p.add ( Box.createHorizontalStrut ( 20 ) );
        colorComboBox = new ColorComboBox ();
        lbl.setLabelFor ( colorComboBox );
        colorComboBox.setToolTipText ( "Font color" );
        ToolTipManager.sharedInstance ().registerComponent ( colorComboBox );
        p.add ( colorComboBox );
        p.add ( Box.createHorizontalStrut ( 10 ) );
        getContentPane ().add ( p );

        p = new JPanel ( new BorderLayout () );
        p.setBorder ( new TitledBorder ( new EtchedBorder (), "Preview" ) );
        previewLabel = new FontLabel ( PREVIEW_TEXT );

        p.add ( previewLabel, BorderLayout.CENTER );
        getContentPane ().add ( p );

        p = new JPanel ( new FlowLayout () );
        JPanel p1 = new JPanel ( new GridLayout ( 1, 2, 10, 2 ) );
        JButton btOK = new JButton ( "OK" );
        btOK.setToolTipText ( "Save and exit" );
        getRootPane ().setDefaultButton ( btOK );
        ActionListener actionListener = new ActionListener()
        {
            public void actionPerformed ( ActionEvent e )
            {
                Closed_Option = JOptionPane.OK_OPTION;
                dispose ();
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
                Closed_Option = JOptionPane.CANCEL_OPTION;
                dispose ();
            }
        };
        btCancel.addActionListener ( actionListener );
        p1.add ( btCancel );
        p.add ( p1 );
        getContentPane().add ( p );

        pack();
        setResizable ( false );

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
        colorComboBox.addActionListener ( actionListener );
        underlineCheckBox.addActionListener ( actionListener );
        strikethroughCheckBox.addActionListener ( actionListener );
        subscriptCheckBox.addActionListener ( actionListener );
        superscriptCheckBox.addActionListener ( actionListener );
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
        colorComboBox.setSelectedItem ( StyleConstants.getForeground ( a ) );
        updatePreview ();
    }

    public AttributeSet getAttributes ()
    {
        if ( attributes == null )
            return null;
        StyleConstants.setFontFamily ( attributes, fontNameInputList
                .getSelected () );
        StyleConstants.setFontSize ( attributes, fontSizeInputList
                .getSelectedInt () );
        StyleConstants.setBold ( attributes, boldCheckBox.isSelected () );
        StyleConstants.setItalic ( attributes, italicCheckBox.isSelected () );
        StyleConstants.setUnderline ( attributes, underlineCheckBox.isSelected () );
        StyleConstants.setStrikeThrough ( attributes, strikethroughCheckBox
                .isSelected () );
        StyleConstants.setSubscript ( attributes, subscriptCheckBox.isSelected () );
        StyleConstants.setSuperscript ( attributes, superscriptCheckBox
                .isSelected () );
        StyleConstants.setForeground ( attributes, ( Color ) colorComboBox.getSelectedItem () );
        return attributes;
    }

    public int getOption ()
    {
        return Closed_Option;
    }

    protected void updatePreview ()
    {
        StringBuilder previewText = new StringBuilder ( PREVIEW_TEXT );
        String name = fontNameInputList.getSelected ();
        int size = fontSizeInputList.getSelectedInt ();
        if ( size <= 0 )
            return;

        Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object> ();

        attributes.put ( FAMILY, name );
        attributes.put ( SIZE, ( float ) size );

        // Using HTML to force JLabel manage natively unsupported attributes
        if ( underlineCheckBox.isSelected () || strikethroughCheckBox.isSelected () )
        {
            previewText.insert ( 0, "<html>" );
            previewText.append ( "</html>" );
        }

        if ( underlineCheckBox.isSelected () )
        {
            attributes.put ( UNDERLINE, UNDERLINE_LOW_ONE_PIXEL );
            previewText.insert ( 6, "<u>" );
            previewText.insert ( previewText.length () - 7, "</u>" );
        }
        if ( strikethroughCheckBox.isSelected () )
        {
            attributes.put ( STRIKETHROUGH, STRIKETHROUGH_ON );
            previewText.insert ( 6, "<strike>" );
            previewText.insert ( previewText.length () - 7, "</strike>" );
        }


        if ( boldCheckBox.isSelected () )
            attributes.put ( WEIGHT, WEIGHT_BOLD );
        if ( italicCheckBox.isSelected () )
            attributes.put ( POSTURE, POSTURE_OBLIQUE );

        if ( subscriptCheckBox.isSelected () )
        {
            attributes.put ( SUPERSCRIPT, SUPERSCRIPT_SUB );
        }
        if ( superscriptCheckBox.isSelected () )
            attributes.put ( SUPERSCRIPT, SUPERSCRIPT_SUPER );

        superscriptCheckBox.setEnabled ( !subscriptCheckBox.isSelected () );
        subscriptCheckBox.setEnabled ( !superscriptCheckBox.isSelected () );


        //Font fn = new Font ( attributes );
        font = new Font ( attributes );

        previewLabel.setText ( previewText.toString () );
        previewLabel.setFont ( font );

        // цвет отдельно
        Color c = ( Color ) colorComboBox.getSelectedItem ();
        previewLabel.setForeground ( c );
        previewLabel.repaint ();
    }

    public Font getResult ()
    {
        return font;
    }
    
    public Color getColor ()
    {
        return ( Color ) colorComboBox.getSelectedItem ();
    }

    public static void main ( String argv[] )
    {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment ();
        fontNames = ge.getAvailableFontFamilyNames ();
        fontSizes = new String[] {
                "8", "9", "10", "11", "12", "14", "16",
                "18", "20", "22", "24", "26", "28", "36", "48", "72" };

        FontChooserWithPoleDialog dlg = new FontChooserWithPoleDialog ( new JFrame () );
        SimpleAttributeSet a = new SimpleAttributeSet ();
        StyleConstants.setFontFamily ( a, "Monospaced" );
        StyleConstants.setFontSize ( a, 12 );
        dlg.setAttributes ( a );
        dlg.setVisible ( true );
    }

}

/*
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
        int[] values = new int[] { 0, 128, 192, 255 };
        for ( int r = 0; r < values.length; r++ )
            for ( int g = 0; g < values.length; g++ )
                for ( int b = 0; b < values.length; b++ )
                {
                    Color c = new Color ( values[ r ], values[ g ], values[ b ] );
                    addItem ( c );
                }
        setRenderer ( new ColorComboRenderer1 () );

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
*/