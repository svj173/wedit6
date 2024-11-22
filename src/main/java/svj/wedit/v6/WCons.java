package svj.wedit.v6;


import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.07.2011 16:36:39
 */
public interface WCons
{
    /* Окончание в имени файла книги. */
    String  BOOK_FILE_NAME_SUFFIX  = "book";

    String  DEFAULT_ICON_PATH  = "img/menu/unknow.png";

    String  CODE_PAGE   = "UTF-8";
    String  YES         = "yes";
    String  NO          = "no";
    String  NEW_LINE    = "\n"; // только String !!!
    String  END_LINE    = "\n"; // только String !!!
    char    END_LINE_C  = '\n';
    char    TAB         = '\t';
    char    SEP         = '/';
    char    COMMA       = ',';
    String  SP          = "";
    String  PP          = "_";

    String  HTML_SP     = "&nbsp;";

    // --------------------- Виды строковых разделителей ------------------
    char SEP_COLON      = ':';
    char SEP_SEMICOLON  = ';';
    char SEP_QUOTE      = '\'';
    char SEP_COMMA      = ',';
    char SEP_SPACE      = ' ';
    char SEP_POINT      = '.';

    String IMG_B_ADD       = "add.png";
    String IMG_B_DELETE    = "delete.png";
    String IMG_B_CREATE    = "new.png";
    String IMG_B_COPY      = "new.png";
    String IMG_B_PASTE     = "new.png";

    /* Высота GUI контрола - для стандартизации */
    int BUTTON_HEIGHT = 27;
    
    int INT_EMPTY = -1;

    // Результат работы диалогового окна
    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 1;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 0;


    Color TREE_BACKGROUND_COLOR = Color.WHITE;
    
    /* Светло-красный. Исп в подсветке критических алертов. */
    Color RED_1 = Color.decode ( "#FF0033" );
    /* Оранжевый. Исп в подсветке мажорных алертов. */
    Color RED_2 = Color.decode ( "#FF9933" );
    /* Жёлтый. Исп в подсветке мажорных алертов. */
    Color RED_3 = Color.decode ( "#FFFF00" );

    /* Серо-синий. Исп в заголовке контекстного меню. */
    Color BLUE_1 = Color.decode ( "#7090AE" );
    /* Светло-голубой - как в выбранном табике */
    Color BLUE_2 = Color.decode ( "#C8DDF2" );
    /* Светло-голубой - как в выбранной строке таблицы */
    Color BLUE_3 = Color.decode ( "#B8CFE5" );
    /* Ярко-голубой */
    Color BLUE_4 = new Color ( 0x12A4F8 );

    /* Ярко-синий для событий типа WARNING*/
    Color BLUE_5 = new Color ( 0x00c0ff );

    Color BLUE_6 = new Color ( 0x008000 );   

    /* Темно-синий. Цвет текста. Либо фона под белые буквы. */
    Color   DARK_BLUE               = new Color ( 0x004080 );

    /* Серый для событий типа Info */
    Color GRAY_1 = new Color ( 0xc4c4c4 );

    /* Красно-коричневый. */
    Color BRAUN_1 = new Color ( 0x940404 );

    /* Темно-розовый. Цвет анотации в главе книги. */
    Color PINK_1 = new Color ( 0x990099 );

    /* Блекло-серый - для через-строчной окраски в таблицах. */
    Color GRAY_2 = new Color ( 0xE0E0E0 );
    //Color GRAY_2 = Color.LIGHT_GRAY;  // 192, 192, 192   ->C0C0C0

    /* Серо-зеленый. Цвет шапки таблицы. */
    Color GREEN_1 = Color.decode ( "#70AE70" );
    /* Ярко зелёный - Clear События*/
    Color GREEN_2 = Color.decode ( "#00ff00" );
    /* Темно-зеленый. Цвет метки в тексте. */
    Color GREEN_3 = Color.decode ( "#009900" );

    /* фон апплет панели c текстом - светло серо сине зеленый */
    Color APPLET_FON = new Color ( 0x98D1DC );

    Color LIGHT_YELLOW = new Color ( 0xFFFFCE );   // #ffffce


    /* Строка редактирования в таблице*/
    Color TABLE_ROW_EDIT_COLOR = Color.YELLOW;

    /* Цвет нечетной строки в таблице */
    Color TABLE_ODD_ROW_COLOR = GRAY_2;
    /* Цвет четной строки в таблице */
    Color TABLE_EVEN_ROW_COLOR = Color.WHITE;

    /* Цвет нижних вкладок. */
    Color BOTTOM_TABS  = new Color ( 0xEBAE8B );

    Border TABLE_ROW_EDIT_BORDER = new LineBorder ( TABLE_ROW_EDIT_COLOR, 2 );


    /* Цвет текстового поля для отображения процесса работы - светлофиолетовый */
    Color TEXT_AREA = Color.decode("#AAAAFF");

    /* Светло-серый - Цвет фона кнопок по умолчанию */
    Color DEFAULT_BUTTON = Color.decode ( "#EEEEEE" );

    Font    TEXT_FONT_1             = new Font("Monospaced", Font.BOLD, 14 );
    Font    ERROR_FONT              = new Font("Monospaced", Font.BOLD, 14 );

    /* курсоры */
    Cursor WORK_CURSOR = new Cursor ( Cursor.DEFAULT_CURSOR );
    Cursor WAIT_CURSOR = new Cursor ( Cursor.WAIT_CURSOR );
    Cursor HAND_CURSOR = new Cursor ( Cursor.HAND_CURSOR );

    String JPEG = "jpeg";
    String JPG  = "jpg";
    String GIF  = "gif";
    String TIFF = "tiff";
    String TIF  = "tif";
    String PNG  = "png";

    // мин ширина рабочей панели
    int WORK_PANEL_MIN_WIDTH = 480;

    /* Разделитель в имени стиля заголовка. Отделяет номер уровня от типа. Пример: 1_hidden. */
    String STYLE_NAME_SEPARATOR = "";

    // Временные интервалы
    int ONE_SECOND = 1000;

}
