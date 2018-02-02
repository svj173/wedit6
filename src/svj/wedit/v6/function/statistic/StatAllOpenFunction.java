package svj.wedit.v6.function.statistic;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.DialogTools;
import svj.wedit.v6.tools.ProjectTools;

import javax.swing.*;
import java.awt.event.ActionEvent;


/**
 * меню - Статистика / Инфа обо всех открытых
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 07.04.2012 8:36:54
 */
public class StatAllOpenFunction extends Function
{
    public StatAllOpenFunction ()
    {
        setId ( FunctionId.STAT_OPEN );
        setName ( "Все открытые");
        //setMapKey ( "Ctrl/O" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        StatAllOpenParser parser;
        String text;
        JLabel label;
        JScrollPane scrollPane;

        // Собрать инфу обо всем открытом.
        parser  = new StatAllOpenParser();   // перевести на SwingWorker - быстрее работать.
        ProjectTools.processTree ( parser );
        text    = parser.getResult();

        // Вывести ее в отдельном окне - как Component или HTML
        label       = new JLabel(text);
        scrollPane  = new JScrollPane (label);
        scrollPane.setHorizontalScrollBarPolicy ( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
        scrollPane.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        DialogTools.showMessage ( getName(), scrollPane );
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
        return "Показать информацию обо всех открытых частях.";
    }

}
