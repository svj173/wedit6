package svj.wedit.v6.gui.tree.comparator;


import svj.wedit.v6.obj.INumber;

/**
 * Типы сортировок обьектов в дереве.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 12.01.2015 11:05
 */
public enum WCompareType implements INumber
{
    ABC (0), ABC_REVERSE(1)
    //ID(2), IP(3), FULL_TYPE(4), LAST_CONNECT_DATE (5)
    ;

    private int number;

    WCompareType ( int number )
    {
        this.number = number;
    }

    @Override
    public int getNumber ()
    {
        return number;
    }

    public static WCompareType getByNumber ( int number )
    {
        for ( WCompareType type : values() )
        {
            if ( type.getNumber() == number )  return type;
        }
        return WCompareType.ABC;
    }

}
