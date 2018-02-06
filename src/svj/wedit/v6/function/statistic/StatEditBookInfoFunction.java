package svj.wedit.v6.function.statistic;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.dialog.WidgetsDialog;
import svj.wedit.v6.gui.renderer.INameRenderer;
import svj.wedit.v6.gui.widget.ComboBoxWidget;
import svj.wedit.v6.obj.DatePeriod;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.DialogTools;
import svj.wedit.v6.tools.ProjectTools;

import javax.swing.*;
import java.awt.event.ActionEvent;


/**
 * Меню - Статистика / Статистика по редактированию Эпизодов.
 * <BR/> Смотреть исправления за периоды - день, неделю, месяц...
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 02.02.2018 16:12:54
 */
public class StatEditBookInfoFunction extends Function
{
    private final ComboBoxWidget<DatePeriod> datePeriodWidget;

    public StatEditBookInfoFunction ()
    {
        setId ( FunctionId.STAT_BOOK_EDIT );
        setName ( "Редактирование" );
        setIconFileName ( "edit-group.png" );

        datePeriodWidget = new ComboBoxWidget<DatePeriod> ( "Период", false, null, DatePeriod.values() );
        datePeriodWidget.setComboRenderer ( new INameRenderer() );
        datePeriodWidget.setTitleWidth ( 150 );
        //datePeriodWidget.setValueWidth (  );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        StatEditBookInfoParser   parser;
        String              text;
        JLabel              label;
        JScrollPane         scrollPane;
        WidgetsDialog       dialog;
        DatePeriod          datePeriod = null;

        text = "Test";

        // Диалог - запрашиваем диапазон дат.
        dialog = createQuestionDialog();

        dialog.showDialog ();
        if ( dialog.isOK() )
        {
            //throw new WEditException ( "datePeriod = "+datePeriod+ "; \nstartDate = "+startDate+"; \nendDate = "+endDate );

            // Цикл по всем Сборникам.
            // - Парсим все файлы на предмет получения данных - атрибуты книги last_change_date и create_date

            // Собрать инфу обо всех книгах используемых Сборников.
            // todo Процесс долгий так что необходимо запускать через прогресс-бар.
            parser  = new StatEditBookInfoParser ( datePeriodWidget.getValue() );
            ProjectTools.processProjectTree ( parser );
            text    = parser.getResult();

            // Вывести ее в отдельном окне - как Component или HTML
            DialogTools.showHtml ( getName(), text, 5 );
        }
    }

    private WidgetsDialog createQuestionDialog ()
    {
        WidgetsDialog result;

        result = new WidgetsDialog ( "Выберите диапазон" );

        // выбираем первое значение.
        datePeriodWidget.setIndex ( 0 );

        result.addWidget ( datePeriodWidget );

        result.pack();

        return result;
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
        return "Показать информацию по редактированию Эпизодов за периоды - день, неделю, месяц...";
    }

}
