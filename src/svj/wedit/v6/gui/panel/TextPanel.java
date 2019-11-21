package svj.wedit.v6.gui.panel;


import com.inet.jortho.PopupListener;
import com.inet.jortho.SpellChecker;
import svj.wedit.v6.Par;
import svj.wedit.v6.book.TextToBookNode;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.text.WDocumentListener;
import svj.wedit.v6.gui.text.WKeyListener;
import svj.wedit.v6.gui.text.WMouseListener;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.tools.Utils;
import svj.wedit.v6.undo.CompoundUndoMan;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;


/**
 * Панель для редактирования текста.
 * <BR/> Метки найденных позици и прочее - в бордюрах скроллинга.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 28.10.2011 11:30:45
 */
public class TextPanel extends EditablePanel //implements Comparable<WPanel>
{
    private StyledDocument      doc;
    private JTextPane           textPane;
    /* Обьект с которым связан текст данной главы. */
    private BookNode            bookNode;
    private CompoundUndoMan     undo;
    private JScrollPane         scrollPane;

    private DefaultHighlighter highlighter;
    private DefaultHighlighter.DefaultHighlightPainter painter;


    public TextPanel ( BookNode bookNode )
    {
        super();

        this.bookNode   = bookNode;

        setLayout ( new GridLayout ( 1, 0 ) );

        //Create the text pane and configure it.
        textPane = new JTextPane();
        textPane.setCaretPosition ( 0 );
        textPane.setMargin ( new Insets ( 5, 5, 5, 5 ) );
        textPane.setFocusable ( true );
        // запретить разрыв слов при переносе
        //textPane.wrapStyleWord ( false );
        textPane.setAutoscrolls(true);        // enable synthetic drag events

        // Жирный курсор
        // 1)
        // - Эта процедура меняет вид курсора мыши, когда он ползает по панели текста. Текстовый курсор - без изменений.
        //Cursor cursor;
        //cursor = new Cursor ( Cursor.HAND_CURSOR );
        //textPane.setCursor ( cursor );
        // 2)
        //int ic = textPane.getCaret().getDot();
        //textPane.getCaret().setDot ( ic+1 );
        //textPane.getCaret().setDot ( ic+1 );

        doc         = textPane.getStyledDocument();
        //styledDoc   = textPane.getStyledDocument();  //styledDoc.
        //doc         = ( AbstractDocument ) styledDoc;

        undo        = new CompoundUndoMan ( doc );

        // Ловим события по изменению текста в текстовой панели - для взведения флага что было Редактирование.
        // - Ловит события: insert, change, remove.
        // туда же скинуть панель виджетов над текстовым окном
        doc.addDocumentListener ( new WDocumentListener ( this ) );

        // Ловим щелчок мышки на тексте - чтобы изменять выпадашки стилей, цветов, элементов - для информации.
        textPane.addMouseListener ( new WMouseListener() );



        // Ловим движения курсора по стрелкам - также для анализа стилей под курсором.
        textPane.addKeyListener ( new WKeyListener() );

        scrollPane = new JScrollPane ( textPane );
        scrollPane.setPreferredSize ( new Dimension ( 200, 200 ) );

        add ( scrollPane, BorderLayout.CENTER );

        // изменить цветь курсор
        textPane.setCaretColor ( Color.BLACK );

        /*
        highlighter = new DefaultHighlighter();
        painter     = new DefaultHighlighter.DefaultHighlightPainter (Color.RED);
        textPane.setHighlighter( highlighter);
        */

        // подсветка всех остальных вхождений для выделеных символов
        // - удаление подсветки - highlighter.removeHighlight(h);
        textPane.addCaretListener ( new CaretListener ()
        {
            public void caretUpdate ( CaretEvent evt )
            {
                if ( evt.getDot() == evt.getMark() ) return;  // т.е. ничего в тексте не выделено.

                JTextPane txtPane = ( JTextPane ) evt.getSource ();
                DefaultHighlighter highlighter = ( DefaultHighlighter ) txtPane.getHighlighter();
                highlighter.removeAllHighlights();

                int ic = evt.getDot() - evt.getMark();
                //Log.l.error ( "+++ ic = " + ic );
                if ( ic == 1 || ic == -1 ) return;  // выделен только один символ - все очищаем.

                DefaultHighlighter.DefaultHighlightPainter hPainter = new DefaultHighlighter.DefaultHighlightPainter ( new Color ( 0xFFAA00 ) );   // оранжевый
                String selText = txtPane.getSelectedText();
                String contText = "";// = jTextPane1.getText();

                DefaultStyledDocument document = ( DefaultStyledDocument ) txtPane.getDocument();

                try
                {
                    contText = document.getText ( 0, document.getLength() );
                } catch ( Exception ex )      {
                    Log.l.error ( "", ex );
                }

                int index = 0;

                while ( ( index = contText.indexOf ( selText, index ) ) > -1 )
                {

                    try
                    {
                        highlighter.addHighlight ( index, selText.length() + index, hPainter );
                        index = index + selText.length ();
                    } catch ( Exception ex )            {
                        Log.l.error ( "", ex );
                        //System.out.println(index);
                    }
                }
            }
        } );

        // изменить курсор в текстовом редакторе
        /*
        Caret caret = textPane.getCaret();
        // object, name, oldValue, newValue
        PropertyChangeEvent prop = new PropertyChangeEvent (null, "caretWidth", null, 3);
        DefaultCaret defaultCaret = (DefaultCaret) caret;
        defaultCaret.handler.propertyChange(prop);
        */

        //caret   = textPane.getCaret();    // стандартный текстовый курсор. мигание = 500.
        //Log.l.info ( "-- caret blink = %d", caret.getBlinkRate() );
        /*
        // Свой курсор. Шире в два раза и с хвостиками вверху и внизу.
        // - Косяк с вводом текста. Почему-то после каждой буквы курсор передвигается вправо через одну позицию (символ).
        caret = new TextCaret (2);
        caret.setBlinkRate ( 500 ); // мигание курсора (пауза), в мсек
        caret.install ( textPane );
        textPane.setCaret ( caret );
        //*/

        //KeyStroke keySave = KeyStroke.getKeyStroke(KeyEvent.VK_F12, Event.ACTION_EVENT);


        // F12 - Изменяет курсор в текстовм поле, чтобы его легко найти.
        KeyStroke keySave = KeyStroke.getKeyStroke("F12");
        Action hardCaret = new AbstractAction("HardCaret") {
            public void actionPerformed(ActionEvent e) {
                Log.l.info("Set new caret blink");
                Caret caret;
                //caret = textPane.getCaret();
                //caret.setBlinkRate(50);

                /*
                try {
                    Caret caretOld = textPane.getCaret();

                    caret = new TextCaret(2);
                    caret.setBlinkRate ( 500 ); // мигание курсора (пауза), в мсек
                    caret.install ( textPane );
                    textPane.setCaret ( caret );

                    // пауза не рабоатет - замирает курсор
                    Thread.currentThread().sleep(3000);
                    
                    caret.deinstall(textPane);
                    textPane.setCaret ( caretOld );

                } catch (InterruptedException e1) {
                    Log.l.info("Set new caret error", e1);
                }
                */

                /*
                caret = new TextCaret(4);
                caret.setBlinkRate ( 500 ); // мигание курсора (пауза), в мсек
                caret.install ( textPane );
                textPane.setCaret ( caret );
                */

                // увеличили толщину курсора - но проблемы со вставками текста - курсор убегает вправо за следующий
                // символ.
                /*
                textPane.putClientProperty("caretWidth",4);
                caret = textPane.getCaret();
                caret.install ( textPane );
                // А как вернуть обратный режим?
                */
            }
        };
        getActionMap().put("hardCaret", hardCaret);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keySave, "hardCaret");


        // Навесить Орфографию.
        SpellChecker.register( textPane );

        // В выпадающее меню Орфографии добавляем свое меню по переконвертирвоке текста.
        // - раньше эта функция находилась в WMouseListener
        for ( MouseListener mouseListener : textPane.getMouseListeners() ) {
            // Ищем  PopupListener
            if ( mouseListener instanceof PopupListener) {
                PopupListener popupListener = (PopupListener) mouseListener;
                // достаем параметр menu и добавляем к нему свое меню.
                Field f = null;
                try {
                    f = PopupListener.class.getDeclaredField("menu");
                    f.setAccessible(true);
                    JPopupMenu menu = (JPopupMenu) f.get(popupListener);
                    JMenuItem item = new JMenuItem("Конвертация");
                    item.addActionListener( new TextConvertListener() );
                    menu.add(item);
                } catch (Exception e) {
                    Log.l.error ( "Add text popup menu error.", e );
                }
            }
        }
    }

    protected void finalize() throws Throwable {
        // отключить проверку.
        SpellChecker.unregister( textPane );
    }

    @Override
    public String toString()
    {
        StringBuilder result; 
        result = new StringBuilder ( 128 );
        result.append ( "[ TextPanel: bookNode = '" );
        result.append ( getBookNode () );
        result.append ( super.toString() );
        result.append ( " ]" );

        return result.toString();
    }

    public StyledDocument getDocument ()
    {
        return   doc;
    }

    public  int getCurrentCursor ()
    {
        return textPane.getCaretPosition ();
    }

    public EditorKit getEditor ()
    {
        return textPane.getEditorKit ();
    }

    public JTextPane getTextPane ()
    {
        return textPane;
    }

    /**
     *
     * we-6
     TextPanel - Применяить сдвиг скролла для текущйе позиции курсора.
     см - JList
     public void ensureIndexIsVisible(int index) {
             Rectangle cellBounds = getCellBounds(index, index);
             if (cellBounds != null) {
                 scrollRectToVisible(cellBounds);
             }
         }
     - вычисляют актуальный прямоугольник для видимости, а потом сдвигает под него скролл.

     *
     * @param point  Позиция курсора в тексте - номер символа в тексте.
     */
    public  void setCurrentCursor ( int point )
    {
        textPane.requestFocusInWindow ();

        if ( point <= textPane.getDocument().getLength() )
        {
            textPane.setCaretPosition ( point );

            //textPane.grabFocus ();
            //textPane.moveCaretPosition (point);

            // НЕ передвигаем скролл, т.к. это Стартовая инициализация - в фоновом потоке, а при старте скролл НЕ передвигается. Двигаем скролл в rewrite
            /*
            try
            {
                // Get the rectangle of the where the text would be visible...  -- Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
                Rectangle viewRect = textPane.modelToView ( point );
                if ( viewRect == null )  throw new WEditException ( null, "Text panel = ", getBookNode().getFullName(), ". Rectangle for point '", point, "' is null." );
                // Scroll to make the rectangle visible
                textPane.scrollRectToVisible ( viewRect );

                // Подсветить немного текста, чтобы сразу бросалось в глаза положение курсора.
                int start;
                if ( point > 5 )
                    start = point - 5;
                else
                    start = 0;
                textPane.select ( start, point );

            } catch ( WEditException we )        {
                Log.l.error ( "Error.", we );
            } catch ( Exception e )        {
                Log.l.error ( "Error. Text panel = "+getBookNode().getFullName()+". Set text point = " + point, e );
            }
            */

            //scrollPane.getViewport().setViewPosition(o);

            //textPane.repaint();
            //scrollPane.revalidate();
            //scrollPane.repaint();

            //textPane.scrollToReference ( "+++" );  // for html URL ?
        }
    }

    @Override
    public void rewrite ()
    {
        // Передвигаем скролл к курсору, т.к. при стартовой инициализации скролл не двигается.
        Rectangle viewRect = null;
        try
        {
            viewRect = textPane.modelToView ( getCurrentCursor() );
        } catch ( Exception e )     {
            Log.l.error ( "Get Rectangle error. Text panel = "+getBookNode().getFullName()+". For cursor point = " + getCurrentCursor(), e );
        }
        //Log.l.error ( getBookNode().getFullName() + "; cursor = "+getCurrentCursor()+"; viewRect = " + viewRect );
        if ( viewRect != null )
        {
            // Scroll to make the rectangle visible
            textPane.scrollRectToVisible ( viewRect );
        }
    }

    public BookNode getBookNode ()
    {
        return bookNode;
    }

    /* Скинуть текст из области редактирования в обьект книги. */
    public void saveTextToNode () throws WEditException
    {
        BookContent             bookContent;
        TextToBookNode          textToBook;
        TreePanel<BookContent>  bookTreePanel;

        Log.l.debug ( "Start. bookNode = %s", getBookNode() );

        if ( isEdit() )
        {
            Log.l.debug ( "IS Edit - run save process." );

            // Получить обьект Книги для данного элемента книги.
            bookContent = getBookNode().getBookContent();

            textToBook  = new TextToBookNode();
            textToBook.process ( this, bookContent );

            // Взвести флаг редактирования у панели данной книги.
            bookContent.setEdit ( true );

            // Установить режим перерисовки дерева - перерисовать без акции - для панели Дерева книги
            // - т.к. пункт дерева был изменен (и содержимое пункта - подгалвы - тоже). expand=true
            bookTreePanel   = Par.GM.getFrame().getCurrentBookContentPanel();
            bookTreePanel.setRepaintTreeMode ( TreePanel.RepaintTree.ALL );
        }

        // Скинуть флаг редактирования у панели текста.
        setEdit ( false );

        Log.l.debug ( "Finish. bookNode = %s", getBookNode() );
    }

    /* Залить текст из обьекта книги в область редактирования. */
    public void loadTextFromNode () throws WEditException
    {
        // todo
    }

    /**
     * Сравниваем по BookNode
     * @param o
     * @return  0 - равны.
     */
    @Override
    public int compareTo ( WPanel o )
    {
        int result;

        result = -1;
        if ( (o != null) && ( o instanceof TextPanel) )
        {
            TextPanel textPanel = (TextPanel) o;
            result = Utils.compareToWithNull ( getBookNode(), textPanel.getBookNode() );
            //Log.l.debug ( "--- compareTo. object = ", o.hashCode (), "; this hashCode = ", this.hashCode(), "; result = ", result );
        }
        return result;
    }

    public boolean equals ( Object obj )
    {
        if ( obj == null )  return false;
        if ( obj instanceof TextPanel )
        {
            TextPanel textPanel = (TextPanel) obj;
            return compareTo ( textPanel ) == 0;
        }
        return false;
    }

    /**
     * Perform an undo action, if possible.
     * Дергаются из акции.
     */
    public void doUndo ()
    {
        if ( undo.canUndo() )
        {
            undo.undo();
            //parse();
        }
    }

    /**
     * Perform a redo action, if possible.
     */
    public void doRedo ()
    {
        if ( undo.canRedo() )
        {
            undo.redo();
            //parse();
        }
    }

    public void activeFocus ()
    {
        //textPane.grabFocus();
        textPane.requestFocusInWindow();
        //textPane.scrollRectToVisible (  );
        //textPane.moveCaretPosition ( textPane.getCaretPosition() );
        //textPane.repaint();
        //textPane.scrollToReference ( "+++" );  // for html URL
    }

}
