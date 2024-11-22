package svj.wedit.v6.function.statistic;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.DialogTools;
import svj.wedit.v6.tools.ProjectTools;

import javax.swing.*;
import java.awt.event.ActionEvent;


/**
 * Меню - Статистика / Инфа обо всех книгах
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 05.12.2014 14:12:54
 */
public class StatAllBookFunction extends Function
{
    public StatAllBookFunction ()
    {
        setId ( FunctionId.STAT_BOOK );
        setName ( "Список книг");
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        StatAllBookParser   parser;
        String              text;
        JLabel              label;
        JScrollPane         scrollPane;

        // Собрать инфу обо всех книгах используемых Сборников.
        parser  = new StatAllBookParser();   // перевести на SwingWorker - быстрее работать.
        ProjectTools.processProjectTree ( parser );
        text    = parser.getResult();

        // Вывести ее в отдельном окне - как Component или HTML
        /*
        label       = new JLabel(text);
        scrollPane  = new JScrollPane (label);
        scrollPane.setHorizontalScrollBarPolicy ( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
        scrollPane.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        */
        DialogTools.showHtml ( getName(), text );
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
        return "Показать информацию обо всех книгах используемых Сборников.";
    }

}
