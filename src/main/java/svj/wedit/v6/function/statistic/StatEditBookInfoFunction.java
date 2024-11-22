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
import java.awt.*;
import java.awt.event.ActionEvent;


/**
 * Меню - Статистика / Статистика по редактированию Эпизодов.
 * <BR/> Смотреть исправления за периоды - день, неделю, месяц...
 * <BR/> Выводит список книг (по Сборникам), дату их правки, и тип правки - Редактирование, Создание.
 * <BR/>
 * <BR/> Парсим все файлы открытых Сборников на предмет получения данных - атрибуты книги last_change_date и create_date.
 * <BR/> Про неоткрытые Собрники ничего не известно, поэтому их и не трогаем.
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
        WidgetsDialog       dialog;

        // Диалог - запрашиваем диапазон дат.
        dialog = createQuestionDialog();

        dialog.showDialog ();
        if ( dialog.isOK() )
        {
            // Цикл по всем Сборникам.
            // - Парсим все файлы на предмет получения данных - атрибуты книги last_change_date и create_date

            // Собрать инфу обо всех книгах используемых Сборников.

            // todo Процесс долгий так что необходимо запускать через прогресс-бар.
            // -- extends MultiFunction, а диалог запроса Периода применять в методе beforeHandle.

            parser  = new StatEditBookInfoParser ( datePeriodWidget.getValue() );
            ProjectTools.processProjectTree ( parser );
            text    = parser.getResult();

            // Вывести Инфу в отдельном окне - как HTML текст.
            DialogTools.showHtml ( getName(), text, 5 );
        }
    }

    private WidgetsDialog createQuestionDialog ()
    {
        WidgetsDialog result;

        result = new WidgetsDialog ( "Выберите диапазон" );

        // Описание
        JTextArea textArea = new JTextArea ( " Замечание:\n При удалении или добавлении элементов книги - занесение в даты редактирования книги происходит только после ее сохранения в файл!" );
        textArea.setLineWrap ( true );
        textArea.setWrapStyleWord ( true );
        textArea.setEditable ( false );
        textArea.setPreferredSize ( new Dimension ( 250, 75 ) );  // главное - задать примерно правильную высоту.
        result.addToNorth ( textArea );

        // Виджеты
        // - выбираем первое значение.
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
        return "Показать информацию по редактированию Эпизодов за периоды - час, день, неделю, месяц...";
    }

}
