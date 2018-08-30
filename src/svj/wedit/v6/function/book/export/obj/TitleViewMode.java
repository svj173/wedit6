package svj.wedit.v6.function.book.export.obj;


import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.util.INameNumber;

/**
 * варианты  формата отображения текста заголовка при конвертации книги.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 12.11.2013 14:56
 */
public enum TitleViewMode   implements INameNumber
{
    NOTHING                     ( 0, "не выводить ничего (пустая строка вместо заголовка)" ),
    ONLY_TITLE_WITH_NUMBER      ( 1, "выводить только тип заголовка (например, глава) с нумерацией" ),
    TITLE_WITH_NUMBER_AND_NAME  ( 2, "выводить тип заголовка (например, глава) с нумерацией и с названием титла" ),
    TITLE_WO_NUMBER_AND_NAME    ( 3, "выводить тип заголовка (например, глава) без нумерации и с названием титла" ),
    ONLY_NAME                   ( 4, "выводить только название титла" ),
    TREE_STARS                  ( 5, "вместо заголовка выводить три звездочки" ),
    NUMBER_AND_POINT_WITH_NAME  ( 6, "выводить номер заголовка с точкой (например, 1.) и название титла" ),
    NUMBER_AND_BR_WITH_NAME     ( 7, "выводить номер заголовка со скобкой (например, 1) ) и название титла" ),
    NUMBER_AND_POINT_ONLY       ( 8, "выводить номер заголовка с точкой" )
    ;

    private int     number;
    private String  name;

    TitleViewMode ( int number, String name )
    {
        this.number = number;
        this.name   = name;
    }

    @Override
    public String getName ()
    {
        return name;
    }

    @Override
    public int getNumber ()
    {
        return number;
    }

    public static TitleViewMode getByNumber ( String strNumber )
    {
        TitleViewMode   result;
        int             number;

        //Log.l.debug ( "Start getByNumber. strNumber = %s", strNumber );

        result = NOTHING;
        number = Convert.getInt ( strNumber, -1 );
        //Log.l.debug ( "-- getByNumber. number = %d", number );

        if ( number >= 0 )
        {
            for ( TitleViewMode tm : values() )
            {
                if ( tm.getNumber() == number )
                {
                    result = tm;
                    break;
                }
            }
        }

        //Log.l.debug ( "Finish getByNumber. result = %s", result );
        return result;
    }

}
