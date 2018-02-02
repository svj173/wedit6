package svj.wedit.v6.function.book.export.obj;


import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.util.INameNumber;

/**
 * варианты  работы с типами глав.
 * <BR/>
 *   - выводить
   - не выводить
   - выводить только <...> - вместо заголовка и текста.

 * <BR/> User: svj
 * <BR/> Date: 14.11.2013 14:56
 */
public enum TypeHandleType implements INameNumber
{
    NOTHING         ( 0, "не выводить" ),
    WRITE           ( 1, "выводить" ),
    PRINT_LATER     ( 2, "выводить только <...>" ),
    ;

    private int     number;
    private String  name;

    TypeHandleType ( int number, String name )
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

    public static TypeHandleType getByNumber ( String strNumber )
    {
        TypeHandleType  result;
        int             number;

        //Log.l.debug ( "Start getByNumber. strNumber = %s", strNumber );

        result = NOTHING;
        number = Convert.getInt ( strNumber, -1 );
        //Log.l.debug ( "-- getByNumber. number = %d", number );

        if ( number >= 0 )
        {
            for ( TypeHandleType tm : values() )
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
