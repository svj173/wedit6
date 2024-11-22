package svj.wedit.v6.obj;


import svj.wedit.v6.tools.Utils;


/**
 * Пара объектов. используется где необходимо передавать два объекта как один объект
 * (чтобы не использовать Object[] или List с последующим приведением типов)
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 04.08.2011 15:16:30
 */
public class WPair<T,M>  implements Comparable<WPair<T,M>>
{
    private T param1;
    private M param2;

    public WPair ( T param1, M param2 )
    {
        this.param1   = param1;
        this.param2   = param2;
    }

    public T getParam1 ()
    {
        return param1;
    }

    public M getParam2 ()
    {
        return param2;
    }

    public String toString()
    {
        StringBuilder result;

        result = new StringBuilder (64);
        result.append ( "param1 = " );
        result.append ( param1 );
        result.append ( ", param2 = " );
        result.append ( param2 );

        return result.toString();
    }

    public void setParam1 ( T param1 )
    {
        this.param1 = param1;
    }

    public void setParam2 ( M param2 )
    {
        this.param2 = param2;
    }

    @Override
    public int compareTo ( WPair<T, M> obj )
    {
        int iFile, iTitle;
        if ( obj == null )  return -1;

        iTitle   = Utils.compareToWithNull ( getParam1(), obj.getParam1() );
        if ( iTitle == 0 )
        {
            return Utils.compareToWithNull ( getParam2(), obj.getParam2() );
        }
        else
            return iTitle;
    }

    public boolean equals ( Object obj )
    {
        if ( (obj != null) && ( obj instanceof WPair) )
        {
            WPair wp;
            wp  = (WPair) obj;
            return wp.compareTo ( this ) == 0;
        }
        else
            return false;
    }
    
}
