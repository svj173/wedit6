package svj.wedit.v6;


import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.tools.Convert;

import java.awt.*;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Изменяемые параметры Редактора.
 *
 * <BR/> User: Zhiganov
 * <BR/> Date: 24.08.2007
 * <BR/> Time: 14:36:48
 */
public class Par
{
    /** Флаг что редактор поднят (true). */
    public static boolean WEDIT_STARTED = false;
    public static boolean NEED_REWRITE  = true;

    public static GeneralManager GM = null;

    /** Рабочая директория Редактора (т.е. где он находится). */
    public static String MODULE_HOME    = "";

    /** Системный Логин пользователя. Если нет - пустая строка (не NULL).     */
    public static String USER_LOGIN     = "";

    /* Домашняя директория пользователя. */
    public static String USER_HOME_DIR  = "";

    /* Вид кодировки конфиг файлов Редактора*/
    public static String CODE_CONFIG    = "UTF-8";

    /* Вид кодировки книги*/
    public static String CODE_BOOK      = "UTF-8";

    // - Пул процессов для любых SwingWorker - применяем при загрузках страниц (таблиц)
    // переиспользует неактивные потоки и подчищает потоки, которые были неактивные некоторое время - т.е. закрывает, хоть и не мгновенно завершенные процессы.
    // Стандартный запуск SwingWorker.execute() - запускает в другом запускальщике, который НЕ закрывает последние 10 процессов.
    public static ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    /* Файл книги. */
    //public static File BookFile    = null;

    /** Флаг сигнализирует о том был ли запущен Shutdown для закрытия редактора или нет. */
    public static boolean SHUTDOWN_STARTED = false;

    /** Версия Редактора: дата. January 29 2009. Заменяется при компиляции. Вид: January 10 2009
     * todo Лучше брать из build-файла, который заносится в jar */
    public static String    VERSION_DATE    = Convert.getRussianDateTime ( new Date() );
    /** Версия Редактора */
    public static String    VERSION_NUMBER    = "76";

    /* Размер экрана пользователя */
    public static Dimension SCREEN_SIZE;

    // Размеры иконок в пикселях. Можно изменять.
    /* Иконки над панелью. */
    public static int PANEL_ICON_SIZE   = 16;
    /* Иконки в тул-бар. */
    public static int TOOLBAR_ICON_SIZE = 24;
    public static int MENU_ICON_SIZE    = 16;
    public static int TREE_ICON_SIZE    = 16;
    public static int TABS_ICON_SIZE    = 16;
    /* Размеры иконок в рабочих кнопках. */
    public static int BUTTONS_ICON_SIZE = 24;

    // Книга которую распарсивают - для более вразумительных сообщений об ошибках.
    public static BookContent CURRENT_PARSE_BOOK = null;

    /**
     * Префикс для имен конвертируемых файлов. Предназанчен для работ с разных компьютеров,
     * <br/> где Сборники находятся в одной директории (т.е. структура Сборников одинакова),
     * <br/> но эта директория в разных компьютерах - разная.
     * <br/> Пример:
     * <br/> 1) имя файла с компа-1: /home/svj/Serg/Project/Story/release_books/fb2/zs1.fb2
     * <br/> 2) имя файла с компа-1: /home/svj/Serg/stories/release_books/fb2/zs1.fb2
     * <br/> 3) общее относительное имя файла: release_books/fb2/zs1.fb2
     * <br/> 4) Вычисляемый на компе Префикс (CONVERT_FILE_PREFIX):
     * <br/> - 1-комп: /home/svj/Serg/Project/Story/
     * <br/> - 2-комп: /home/svj/Serg/stories/
     */
    public static String CONVERT_FILE_PREFIX = null;


}
