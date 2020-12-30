package svj.wedit.v6.function.project;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.gui.panel.card.CardPanel;
import svj.wedit.v6.gui.tabs.TabsPanel;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.obj.book.BookCons;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.DialogTools;
import svj.wedit.v6.tools.FileTools;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.Map;


/**
 * Сохранить все открытые книги - Акция по нажатию кнопки, т.е. диалоги переспроса допустимы. Только измененные книги. Не измененные не трогает.
 * <BR/> Выводит статистические данные - список книг, размеры.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 15.07.2011 16:36:45
 */
public class SaveAllProjectsFunction extends Function
{
    /* Флаг - выводить итоговый диалог-сообщение с описанием проделанной работы (true) или нет (false). */
    private boolean useDialog;

    public SaveAllProjectsFunction ()
    {
        setId ( FunctionId.SAVE_ALL_PROJECTS );
        setName ( "Сохранить все Сборники" );      // также сохраняет и все книги  -- только если были изменения (не будет ли потерь?)
        setMapKey ( "Ctrl/S" );
        setIconFileName ( "save.gif" );

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
        boolean             wasEdit;
        Map<String,TabsPanel<TextPanel>> textPanels;

        // - сохраняем книги с текстами - только у которых было изменение текста, но сохранений в файлы еще не было.

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
        msg.append ( "<table border='1' cellpadding='0' cellspacing='0'><tr bgcolor='#BBBBBB'><th>&nbsp;N&nbsp;</th><th>&nbsp;Проект&nbsp;</th><th>&nbsp;Название&nbsp;</th><th>Имя файла</th><th>&nbsp;Размер, Б&nbsp;</th><th>Результат</th><th>Изменения</th></tr>\n" );

        // Получить все книги
        bookPanel   = Par.GM.getFrame().getBookContentPanel();
        panels      = bookPanel.getPanels();

        ic          = 1;
        // Цикл по все card-панелям открытых книг разных проектов (в общей панели содержимого книг).
        for ( TabsPanel<TreePanel<BookContent>> p : panels.values() )
        {
            // Отмечать проекты ?
            // Цикл по всем книгам
            for ( TreePanel<BookContent> tc : p.getPanels() )
            {
                // Перерисовать флаг, чтобы взялись изменения из BookContent, установленный в цикле выше (saveTextToNode).
                tc.rewriteEdit();

                book    = tc.getObject();

                // Инфу выводим только по измененным книгам.
                wasEdit = tc.isEdit();
                if ( wasEdit )
                {
                    // В книге были изменения
                    book.addAttribute ( BookCons.ATTR_NAME_LAST_CHANGE_DATE, Convert.getEnDateTime ( new Date() ) );

                    // Номер и Название книги
                    msg.append ( "<tr><td align='right'>&nbsp;" );
                    msg.append ( ic );
                    msg.append ( "&nbsp;</td><td><font color='green'>&nbsp;" );
                    msg.append ( book.getProject().getName() );
                    msg.append ( "&nbsp;</td><td><font color='green'>&nbsp;" );
                    msg.append ( book.getName() );
                    msg.append ( "&nbsp;</font></td><td><font color='blue'>&nbsp;" );
                    msg.append ( book.getFileName() );
                    msg.append ( "&nbsp;</font></td>" );
                    ic++;

                    // - Скинуть текст из панелей в обьекты -- Уже
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
                    tc.setEdit ( false );    // todo лишнее? т.к. edit зависит только от BookContent
                }
                /*
                else
                {
                    // В книге не было несохраненных изменений - просто сообщить.
                    msg.append ( "<td align='right'>&nbsp;" );
                    msg.append ( book.getFileSize() );
                    msg.append ( "&nbsp;</td><td><font color='#FF00FF'>&nbsp;Не&nbsp;был&nbsp;изменен&nbsp;</font></td><td>&nbsp;</td>" );  // magenta=FF00FF
                }
                ic++;
                */
                msg.append ( "</tr>\n" );
            }
        }
        msg.append ( "</table>\n<br/>\n</body>\n</HTML>\n" );

        if ( useDialog )
        {
            if ( ic > 1 )
                DialogTools.showHtml ( "Сохранение " + (ic-1) + " книг.", JOptionPane.CANCEL_OPTION, msg.toString(), 2 );
            else
                DialogTools.showMessage ( "Сохранение", "Нет изменений." );
        }
    }

    public void setUseDialog ( boolean useDialog )
    {
        this.useDialog = useDialog;
    }

    @Override
    public String getToolTipText ()
    {
        return "Сохранить все Сборники.";
    }

    
    @Override
    public void rewrite ()    {    }

    @Override
    public void init () throws WEditException     {    }

    @Override
    public void close ()      {    }

}
