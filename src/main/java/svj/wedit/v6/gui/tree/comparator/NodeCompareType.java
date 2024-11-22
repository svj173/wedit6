package svj.wedit.v6.gui.tree.comparator;


import svj.wedit.v6.obj.INumber;

/**
 * Типы сортировок узлов в дереве.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 12.01.2015 11:05
 */
public enum NodeCompareType implements INumber
{
    ABC (0), ABC_REVERSE(1)
    ;

    private int number;

    NodeCompareType ( int number )
    {
        this.number = number;
    }

    @Override
    public int getNumber ()
    {
        return number;
    }

    public static NodeCompareType getByNumber ( int number )
    {
        for ( NodeCompareType type : values() )
        {
            if ( type.getNumber() == number )  return type;
        }
        return NodeCompareType.ABC;
    }

}
