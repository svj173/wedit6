package svj.wedit.v6.tools;


import svj.wedit.v6.exception.WEditException;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.12.2017 15:52
 */
public class TableTools
{
    /*
    public void insertTable ( int offset, int rowCount, int[] colWidths )
    {
            try {
                SimpleAttributeSet attrs = new SimpleAttributeSet();

                ArrayList tableSpecs = new ArrayList();
                tableSpecs.add(new DefaultStyledDocument.ElementSpec ( attrs, DefaultStyledDocument.ElementSpec.EndTagType)); //close paragraph tag

                SimpleAttributeSet tableAttrs = new SimpleAttributeSet();
                tableAttrs.addAttribute(ElementNameAttribute, ELEMENT_NAME_TABLE);
                DefaultStyledDocument.ElementSpec tableStart = new DefaultStyledDocument.ElementSpec ( tableAttrs, DefaultStyledDocument.ElementSpec.StartTagType);
                tableSpecs.add(tableStart); //start table tag

                fillRowSpecs ( tableSpecs, rowCount, colWidths );

                DefaultStyledDocument.ElementSpec tableEnd = new DefaultStyledDocument.ElementSpec ( tableAttrs, DefaultStyledDocument.ElementSpec.EndTagType);
                tableSpecs.add(tableEnd); //end table tag

                tableSpecs.add(new DefaultStyledDocument.ElementSpec ( attrs, DefaultStyledDocument.ElementSpec.StartTagType)); //open new paragraph after table

                DefaultStyledDocument.ElementSpec[] spec = new DefaultStyledDocument.ElementSpec[tableSpecs.size()];
                tableSpecs.toArray(spec);

                this.insert ( offset, spec );
            }
            catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    public void insertTable ( StyledDocument doc, int offset, int rowCount, int[] colWidths )
    {
        DefaultTableModel dtm = new DefaultTableModel() {
           // make first cell uneditable
           public boolean isCellEditable(int row, int column)
           {
              return !(column == 0);
           }
        };

        dtm.setDataVector(new Object[][]{{ "JTextArea1", "This is a testnon long linesn" },
                                         { "JTextArea2", "Hello, world!" }},
                          new Object[]{ "String","JTextArea"});

        JTable table = new JTable( dtm);
        //table.getColumn("JTextArea").setCellRenderer(new TextAreaRenderer());
        //table.getColumn("JTextArea").setCellEditor(new TextAreaEditor());

        table.setRowHeight(80);
        JScrollPane scroll = new JScrollPane(table);

        doc.insert(offset, spec);
    }
    */
    public static void insertTable ( StyledDocument doc, int pos )
            throws WEditException
    {
        String styleName;
        Style  style;

        styleName   = "table";
        /*
        // Сформировать имя стиля для данной иконки
        // new
        WEditStyle iconStyle = new WEditStyle ( StyleType.IMG, styleName );
        // - Выставить в нашем стиле выравнивание
        StyleConstants.setAlignment ( iconStyle, StyleConstants.ALIGN_CENTER );
        // - Занести в стиль иконку
        StyleConstants.setIcon ( iconStyle, icon );
        // - дополнительные атрибуты
        iconStyle.addAttribute ( StyleName.STYLE_NAME, styleName );
        iconStyle.addAttribute ( "iconFile", fileName );
        // Начальный отступ абзаца если есть. - FirstLineIndent
        StyleConstants.setFirstLineIndent ( iconStyle, 40 );

        // Вставить картинку в документ - как ранее описанный стиль.
        try
        {
            //doc.insertString ( pos, styleName, doc.getStyle(styleName) );
            doc.insertString ( pos, styleName, iconStyle );
            // Вставить обьект - обьект описания картинки - это уже при обратном парсинге - из документа в обьект
        } catch ( Exception e )             {
            throw new WEditException ( e, "Ошибка вставки иконки '", icon, "' :\n ", e );
        }
        */

        style = doc.addStyle("table", null);
        // Засунуть в Стиль созданную таблицу как обьект стиля.
        StyleConstants.setComponent ( style, getTableComponent() );

        // Вставить таблицу в документ - как ранее описанный стиль.
        try
        {
            //doc.insertString ( pos, styleName, doc.getStyle(styleName) );
            doc.insertString ( pos, styleName, style );
            // Вставить обьект - обьект описания картинки - это уже при обратном парсинге - из документа в обьект
        } catch ( Exception e )             {
            throw new WEditException ( e, "Ошибка вставки Таблицы: ", e.getMessage() );
        }
    }

    private static JScrollPane getTableComponent()
    {
        JTable table = new JTable(getModel());
        Dimension d = table.getPreferredSize();
        d.width = 300;
        table.setPreferredScrollableViewportSize(d);
        return new JScrollPane(table);
    }

    private static AbstractTableModel getModel() {
        return new AbstractTableModel () {
            public int getColumnCount() { return 3; }
            public int getRowCount() { return 3; }
            public Object getValueAt(int row, int col) {
                return String.valueOf(row + 1) + (col + 1);
            }
        };
    }

    /*
class TextAreaRenderer extends JScrollPane implements TableCellRenderer
{
   JTextArea textarea;

   public TextAreaRenderer() {
      textarea = new JTextArea();
      textarea.setLineWrap(true);
      textarea.setWrapStyleWord(true);
      textarea.setBorder(new TitledBorder("This is a JTextArea"));
      getViewport().add(textarea);
   }

   public Component getTableCellRendererComponent(JTable table, Object value,
                                  boolean isSelected, boolean hasFocus,
                                  int row, int column)
   {
      if (isSelected) {
         setForeground(table.getSelectionForeground());
         setBackground(table.getSelectionBackground());
         textarea.setForeground(table.getSelectionForeground());
         textarea.setBackground(table.getSelectionBackground());
      } else {
         setForeground(table.getForeground());
         setBackground(table.getBackground());
         textarea.setForeground(table.getForeground());
         textarea.setBackground(table.getBackground());
      }

      textarea.setText((String) value);
      textarea.setCaretPosition(0);
      return this;
   }
}

class TextAreaEditor extends DefaultCellEditor {
   protected JScrollPane scrollpane;
   protected JTextArea textarea;

   public TextAreaEditor() {
      super(new JCheckBox());
      scrollpane = new JScrollPane();
      textarea = new JTextArea();
      textarea.setLineWrap(true);
      textarea.setWrapStyleWord(true);
      textarea.setBorder(new TitledBorder("This is a JTextArea"));
      scrollpane.getViewport().add(textarea);
   }

   public Component getTableCellEditorComponent(JTable table, Object value,
                                   boolean isSelected, int row, int column) {
      textarea.setText((String) value);

      return scrollpane;
   }

   public Object getCellEditorValue() {
      return textarea.getText();
   }
}
     */

    /*

        protected void fillRowSpecs(ArrayList tableSpecs, int rowCount, int[] colWidths) {
            SimpleAttributeSet rowAttrs = new SimpleAttributeSet();
            rowAttrs.addAttribute(ElementNameAttribute, ELEMENT_NAME_ROW);
            for (int i = 0; i < rowCount; i++) {
                DefaultStyledDocument.ElementSpec rowStart = new DefaultStyledDocument.ElementSpec ( rowAttrs, DefaultStyledDocument.ElementSpec.StartTagType);
                tableSpecs.add(rowStart);

                fillCellSpecs(tableSpecs, colWidths);

                DefaultStyledDocument.ElementSpec rowEnd = new DefaultStyledDocument.ElementSpec ( rowAttrs, DefaultStyledDocument.ElementSpec.EndTagType);
                tableSpecs.add(rowEnd);
            }

        }

        protected void fillCellSpecs(ArrayList tableSpecs, int[] colWidths) {
            for (int i = 0; i < colWidths.length; i++) {
                SimpleAttributeSet cellAttrs = new SimpleAttributeSet();
                cellAttrs.addAttribute(ElementNameAttribute, ELEMENT_NAME_CELL);
                cellAttrs.addAttribute(PARAM_CELL_WIDTH, new Integer(colWidths[i]));
                DefaultStyledDocument.ElementSpec cellStart = new DefaultStyledDocument.ElementSpec ( cellAttrs, DefaultStyledDocument.ElementSpec.StartTagType);
                tableSpecs.add(cellStart);

                DefaultStyledDocument.ElementSpec parStart = new DefaultStyledDocument.ElementSpec ( new SimpleAttributeSet(), DefaultStyledDocument.ElementSpec.StartTagType);
                tableSpecs.add(parStart);
                DefaultStyledDocument.ElementSpec parContent = new DefaultStyledDocument.ElementSpec ( new SimpleAttributeSet(), DefaultStyledDocument.ElementSpec.ContentType, "\n".toCharArray(), 0, 1);
                tableSpecs.add(parContent);
                DefaultStyledDocument.ElementSpec parEnd = new DefaultStyledDocument.ElementSpec ( new SimpleAttributeSet(), DefaultStyledDocument.ElementSpec.EndTagType);
                tableSpecs.add(parEnd);
                DefaultStyledDocument.ElementSpec cellEnd = new DefaultStyledDocument.ElementSpec ( cellAttrs, DefaultStyledDocument.ElementSpec.EndTagType);
                tableSpecs.add(cellEnd);
            }

        }
        */
}
