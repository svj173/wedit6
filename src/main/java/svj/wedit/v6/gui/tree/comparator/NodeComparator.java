package svj.wedit.v6.gui.tree.comparator;


import svj.wedit.v6.obj.WTreeObj;

import java.util.Comparator;

/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 06.01.2015 17:47
 */
public class NodeComparator implements Comparator<WTreeObj>
{
    private NodeCompareType compareType = NodeCompareType.ABC;

    public NodeComparator ()
    {
    }

    @Override
    public int compare ( WTreeObj o1, WTreeObj o2 )
    {
        int result;

        switch ( compareType )
        {
            case ABC: // name ASC
                //result = o1.getName().toLowerCase().compareTo ( o2.getName().toLowerCase() );
                result = o1.getName().compareToIgnoreCase ( o2.getName() );
                break;

            case ABC_REVERSE: // name DESC
                //result = - o1.getName().toLowerCase().compareTo ( o2.getName().toLowerCase() );
                result = - o1.getName().compareToIgnoreCase ( o2.getName() );
                break;

            default:
                result = 1;
                break;
        }
        return result;
    }

    /*
    public int getCompareType ()
    {
        return compareType;
    }
    */

    public void setCompareType ( NodeCompareType compareType )
    {
        this.compareType = compareType;
    }

}
