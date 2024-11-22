package svj.wedit.v6.function.project;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.gui.panel.card.CardPanel;
import svj.wedit.v6.gui.tabs.TabsPanel;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.DialogTools;
import svj.wedit.v6.tools.FileTools;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Map;


/**
 * Безусловно сохранить все открытые книги - без анализов флага eidt и без диалогов переспроса.
 * <BR/> Выводит статистические данные - список книг, размеры.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 18.03.2013 13:36:45
 */
public class SaveAbsoluteAllProjectsFunction extends Function
{
    /* Флаг - выводить диалог с описанием проделанной работы (true) или нет (false). */
    private boolean useDialog;

    public SaveAbsoluteAllProjectsFunction ()
    {
        setId ( FunctionId.SAVE_ABSOLUTE_ALL_PROJECTS );
        setName ( "Безусловно cохранить все проекты" );
        //setMapKey ( "Ctrl/S" );
        setIconFileName ( "save_all.gif" );

        useDialog = true;
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        CardPanel<TabsPanel<TreePanel<BookContent>>>    bookPanel;
        Map<String, TabsPanel<TreePanel<BookContent>>>  panels;
        BookContent         book;
        StringBuilder       msg;
        long                fileSize;
        int                 ic;
        JLabel              label;
        Map<String,TabsPanel<TextPanel>> textPanels;

        // - сохраняем книги с текстами - только у которых было изменение текста, но сохранений в файлы еще не было.
        // Выдать диалог - закрывать открытые: 1) в этом окне, 2) в новом, 3) отменить.
        // Если в этом же - дернуть функцию закрытия проекта.

        // Выдать команду - скинуть все открытые тексты в обьекты.
        // - Взять спиcок всех открытых текстовых таб-панелей.
        textPanels  = Par.GM.getFrame().getTextsPanel().getPanels();
        for ( TabsPanel<TextPanel> tb : textPanels.values() )
        {
            for ( TextPanel tp : tb.getPanels() )
            {
                tp.saveTextToNode();
            }
        }


        msg = new StringBuilder(512);
        msg.append ( "<HTML><body>\n" );

        // Получить все книги
        bookPanel   = Par.GM.getFrame().getBookContentPanel();
        panels      = bookPanel.getPanels();

        msg.append ( "<table border='1' cellpadding='0' cellspacing='0'><tr bgcolor='#BBBBBB'><th>&nbsp;N&nbsp;</th><th>&nbsp;Название&nbsp;</th><th>Имя файла</th><th>&nbsp;Размер, Б&nbsp;</th><th>Результат</th><th>Изменения</th></tr>\n" );

        ic          = 1;
        // Цикл по все card-панелям открытых книг разных проектов (в общей панели содержимого книг).
        for ( TabsPanel<TreePanel<BookContent>> p : panels.values() )
        {
            // Отмечать проекты ?
            // Цикл по всем книгам
            for ( TreePanel<BookContent> tc : p.getPanels() )
            {
                book    = tc.getObject();
                // Номер и Название книги
                msg.append ( "<tr><td align='right'>&nbsp;" );
                msg.append ( ic );
                msg.append ( "&nbsp;</td><td><font color='green'>&nbsp;" );
                msg.append ( book.getName() );
                msg.append ( "&nbsp;</font></td><td><font color='blue'>&nbsp;" );
                msg.append ( book.getFileName() );
                msg.append ( "&nbsp;</font></td>" );

                // - сохранить
                try
                {
                    fileSize    = FileTools.saveBook ( book );
                    msg.append ( "<td align='right'>&nbsp;" );
                    msg.append ( fileSize );
                    msg.append ( "&nbsp;</td><td><font color='green'>&nbsp;Сохранен&nbsp;</font></td>" );
                    // изменения -
                    msg.append ( "<td align='right'>&nbsp;" );
                    msg.append ( fileSize - book.getFileSize() );
                    msg.append ( "&nbsp;</td>" );
                    book.setFileSize ( fileSize );

                } catch ( Exception e )                {
                    msg.append ( "<td>&nbsp;</td><td><font color='red'>&nbsp; Ошибка : " );
                    msg.append ( e.getMessage() );
                    msg.append ( "&nbsp;</font></td><td>&nbsp;</td>" );
                }
                // Отключить флаг, что были изменения - все уже сохранили в файле.
                book.setEdit ( false );
                tc.setEdit ( false );    // лишнее? т.к. edit зависит только от BookContent
                ic++;
                msg.append ( "</tr>\n" );
            }
        }
        msg.append ( "</table>\n<br/>\n</body>\n</HTML>\n" );

        if ( useDialog )
        {
            label   = new JLabel ( msg.toString() );
            DialogTools.showMessage ( Par.GM.getFrame(), label, Convert.concatObj ( "Сохранение ", (ic-1), " книг" ) );
            //DialogTools.showMessage2 ( Convert.concatObj ( "Сохранение ", (ic-1), " книг" ), label );
        }
    }

    public void setUseDialog ( boolean useDialog )
    {
        this.useDialog = useDialog;
    }

    @Override
    public String getToolTipText ()
    {
        return "Безусловно сохранить все Сборники, без учета флага edit (Ctrl/S).";
    }

    
    @Override
    public void rewrite ()    {    }

    @Override
    public void init () throws WEditException     {    }

    @Override
    public void close ()      {    }

}
