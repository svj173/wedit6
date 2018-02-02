package svj.wedit.v6.function.text;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.dialog.WidgetsDialog;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.gui.widget.IntegerFieldWidget;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.GuiTools;
import svj.wedit.v6.tools.TableTools;

import javax.swing.*;
import javax.swing.text.StyledDocument;
import java.awt.event.ActionEvent;
import java.io.File;


/**
 * Вставить таблицу в текст.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.12.2017 15:34:41
 */
public class InsertTableFunction extends Function
{
    public InsertTableFunction ()
    {
        setId ( FunctionId.INSERT_TABLE );
        setName ( "Вставить таблицу." );
        setIconFileName ( "table.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        WidgetsDialog   dialog;
        File            file, bookFile, bookImgDirFile;
        Icon            icon;
        int             pos;
        String          bookFileName, iconDir, bookImgDir, targetFileName, iconFileName;
        StyledDocument  doc;
        TextPanel       textPanel;
        BookContent     bookContent;

        Log.l.debug ( "Start" );

        // открыть диалог по запросу параметров таблицы: кол-во колонок, столбцов, бордюр - виден-невиден.

        dialog = createRequestDialog();
        dialog.showDialog();

        if ( dialog.isOK() )
        {
            String from, to;
            int cols, rows;
            // Берем заданные строки
            cols    = (Integer) dialog.getValue ( "Cols" );
            rows    = (Integer) dialog.getValue ( "Rows" );

            // Взять текстовую панель
            textPanel   = Par.GM.getFrame().getCurrentTextPanel();
            // Взять книгу
            bookContent = textPanel.getBookNode().getBookContent();

            bookFileName    = bookContent.getFileName();

            // Взять текущий Документ
            doc         = textPanel.getDocument();

            // Взять текущую позицию курсора
            //pos         = 10;
            pos         = textPanel.getCurrentCursor();

            // Создать таблицу и вставить
            TableTools.insertTable ( doc, pos );
        }

        //DialogTools.showMessage ( "Внимание", "Функция не реализована." );

        Log.l.debug ( "Finish" );
    }

    private WidgetsDialog createRequestDialog () throws WEditException
    {
        WidgetsDialog      dialog;
        int                titleSize, ic;
        IntegerFieldWidget widget;

        widget      = null;

        dialog      = new WidgetsDialog ( "Таблица." );

        widget = new IntegerFieldWidget ( "Колонки", false );
        widget.setName ( "Cols" );
        widget.setValue ( 1 );
        dialog.addWidget ( widget );

        widget = new IntegerFieldWidget ( "Столбцы", false );         // На что
        widget.setName ( "Rows" );
        widget.setValue ( 1 );
        dialog.addWidget ( widget );

        titleSize   = 7;    // макс длина заголовков виджетов
        // перевести размер в символах в размер в пикселях
        ic = GuiTools.getFontSize ( widget.getGuiComponent().getFont(), titleSize );
        //Log.l.debug ( "--- titleSize 2 = %d; ic = %d", titleSize, ic );

        dialog.setTitleWidth ( ic );
        dialog.pack();

        return dialog;
    }


    @Override
    public void rewrite ()
    {
    }

    @Override
    public void init () throws WEditException
    {
    }

    @Override
    public void close ()
    {
    }

    @Override
    public String getToolTipText ()
    {
        return "Загрузить в текст картинку.";
    }

}
