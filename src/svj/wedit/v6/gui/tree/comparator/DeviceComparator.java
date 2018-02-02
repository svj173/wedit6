package svj.wedit.v6.gui.tree.comparator;


import svj.wedit.v6.obj.WTreeObj;

import java.util.Comparator;

/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 06.01.2015 17:47
 */
public class DeviceComparator implements Comparator<WTreeObj>
{
    private WCompareType compareType;


    public DeviceComparator ()
    {
        compareType = WCompareType.ABC;
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

            /*
            case LAST_CONNECT_DATE:
                result = CommonTools.compareToWithNull ( o1.getLastConnectDate(), o2.getLastConnectDate() );
                break;

            case FULL_TYPE:
                result = CommonTools.compareToWithNull ( o1.getFullType(), o2.getFullType() );
                break;

            case ID:
                result = CommonTools.compareToWithNull ( o1.getId(), o2.getId() );
                break;

            case IP:  // На самом деле необходимо сравнивать не как строку, иначе 198.22 будет больше чем 198.112
                String ip1, ip2;
                ip1     = getIp ( o1 );
                ip2     = getIp ( o2 );
                result  = CommonTools.compareToWithNull ( ip1, ip2 );
                break;
            */

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
    public void setCompareType ( WCompareType compareType )
    {
        this.compareType = compareType;
    }

}
