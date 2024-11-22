package svj.wedit.v6.obj;


/**
 * Фиксированные Периоды дат.
 * <BR/> Для виджетов выборки диапазонов дат.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 02.02.2018 16:31
 */
public enum DatePeriod implements IName
{
    // in sec
    LAST_2_HOURS    ("Последние 2 часа",    60*60*2),
    LAST_4_HOURS    ("Последние 4 часа",    60*60*4),
    LAST_6_HOURS    ("Последние 6 часов",   60*60*6),
    LAST_12_HOURS   ("Последние 12 часов",  60*60*12),
    LAST_DAY        ("Последний день",      60*60*24),
    LAST_2_DAYS     ("Последние 2 дня",     60*60*24*2),
    LAST_4_DAYS     ("Последние 4 дня",     60*60*24*4),
    LAST_WEEK       ("Последняя неделя",    60*60*24*7),
    LAST_TWO_WEEKS  ("Последние 2 недели",  60*60*24*14),
    LAST_MONTH      ("Последний месяц",     60*60*24*28);

    private String title;
    private int    time;

    private DatePeriod ( String title, int time)
    {
        this.title = title;
        this.time  = time;
    }

    public String getName ()
    {
        return title;
    }

    public String getTitle ()
    {
        return title;
    }

    public int getTime ()
    {
        return time;
    }

}
