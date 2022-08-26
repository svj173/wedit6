package svj.wedit.v6.obj;


import svj.wedit.v6.gui.IId;
import svj.wedit.v6.gui.tree.comparator.NodeCompareType;
import svj.wedit.v6.gui.tree.comparator.TreeComparator;
import svj.wedit.v6.gui.tree.comparator.WCompareType;
import svj.wedit.v6.tools.Utils;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Comparator;


/**
 * GUI обьект дерева.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 04.08.2011 11:35:53
 */
public class TreeObj extends DefaultMutableTreeNode implements Comparable<TreeObj>
{
    private String      parentId;
    //private WTreeObj    parent;      // ???

    //private final Collection<WTreeObj> childrens;
    //private String type;
    private String      id;

    /** Сортировщик. Различает сортировку узлов и сортировку устройств. */
    private final TreeComparator comparator             = new TreeComparator();

    /** Тип сортировки для вложенных узлов. */
    private NodeCompareType     nodeComparatorType      = NodeCompareType.ABC;
    /** Тип сортировки для вложенных устройств. */
    private WCompareType        deviceComparatorType    = WCompareType.ABC;

    private String subType = null;


    @Override
    public String toString ()
    {
        StringBuilder result;

        result = new StringBuilder(512);

        result.append ( "[ TreeObj :" );
        result.append ( " name = " );
        result.append ( getName() );
        result.append ( "; type = " );
        result.append ( getType() );
        result.append ( "; id = " );
        result.append ( getId() );
        result.append ( "; parentId = " );
        result.append ( getParentId() );
        result.append ( "; subType = " );
        result.append ( getSubType() );
        result.append ( "; childs = " );
        result.append ( getChildCount() );
        result.append ( "; wtree = " );
        result.append ( getWTreeObj() );

        result.append ( " ]" );

        return result.toString();
    }

    public  TreeObj clone()
    {
        TreeObj result;

        //result  = new TreeObj();
        result  = (TreeObj) super.clone();
        result.setId ( getId() );
        result.setParentId ( getParentId() );
        result.setSubType ( getSubType() );
        //result.setParent ( getWTreeObj() );      // clone?
        //result.setParent ( getParent() );
        // userObject ? -- отдельно идет в BookTools

        return result;
    }

    /*
    // Прим сортировщика - но это только для Дерева книг. Главы нельзя сортировать.
    @Override
    public void insert ( final MutableTreeNode newChild, final int childIndex )
    {
        // добавить новый обьект
        super.insert ( newChild, childIndex );
        // пересортировать обьекты согласно установленным типам сортировок - отдельно для узлов и отдельно для устройств.
        Collections.sort ( this.children, getComparator() );
    }

    @Override
    public int compareTo ( TreeObj other )
    {
        if ( other == null ) return 1;

        return getComparator().compare ( this, other );
    }
    */

    //* old
    // +1 - текущий больше внешнего; -1 - текущий меньше внешнего.
    public int compareTo ( TreeObj object )
    {
        Comparable obj1, obj2;

        if ( object == null ) return 1;
        obj1    = (Comparable) getUserObject();
        obj2    = (Comparable) object.getUserObject();

        return Utils.compareToWithNull ( obj1, obj2 );
    }
    //*/

    @Override
    public boolean equals ( Object obj )
    {
        boolean     result;
        Comparable  obj1, obj2;
        TreeObj     treeObj;

        if ( obj instanceof TreeObj )
        {
            treeObj = (TreeObj) obj;
            obj1    = (Comparable) getUserObject();
            obj2    = (Comparable) treeObj.getUserObject();

            result = Utils.compareToWithNull ( obj1, obj2 ) == 0;
        }
        else
            result = false;

        return result;
    }

    public String getName ()
    {
        WTreeObj wo;
        Object   obj;
        String   result;

        result  = null;
        obj     = getUserObject();
        if ( ( obj != null) && (obj instanceof WTreeObj))
        {
            wo      = (WTreeObj) getUserObject();
            result  = wo.getName();
        }
        return result;
    }

    public TreeObjType getType ()
    {
        WTreeObj wo;
        Object   obj;
        obj = getWTreeObj();
        if ( (obj != null) && (obj instanceof WTreeObj) )
        {
            wo = (WTreeObj) obj;
            return wo.getType();
        }
        else
            return TreeObjType.UNKNOW;
    }

    public Object getWTreeObj()
    {
        //WTreeObj wo;
        //wo  = (WTreeObj) getUserObject();
        //return wo;
        return getUserObject();
    }

    // Т.к. ИД здесь не используется - берем ИД обьекта.
    public String getId ()
    {
        Object obj;

        obj = getUserObject ();
        if ( (obj != null) && (obj instanceof IId ))
        {
            IId wObj = (IId) obj;
            id = wObj.getId();
        }
        return id;
    }

    public String getParentId ()
    {
        return parentId;
    }

    public TreeObj getTreeParent ()
    {
        return (TreeObj) getParent();
    }

    public void setParentId ( String parentId )
    {
        this.parentId = parentId;
    }

    /*
    public void setParent ( WTreeObj parent )
    {
        this.parent = parent;
    }
    */

    public void setId ( String id )
    {
        this.id = id;
    }

    public Comparator<? super TreeObj> getComparator ()
    {
        comparator.init ( getNodeComparatorType(), getDeviceComparatorType() );
        return comparator;
    }

    public NodeCompareType getNodeComparatorType ()
    {
        return nodeComparatorType;
    }

    public void setNodeComparatorType ( int nodeComparatorType )
    {
        this.nodeComparatorType = NodeCompareType.getByNumber ( nodeComparatorType );
    }

    public WCompareType getDeviceComparatorType ()
    {
        return deviceComparatorType;
    }

    public void setDeviceComparatorType ( int wComparatorType )
    {
        this.deviceComparatorType = WCompareType.getByNumber ( wComparatorType );
    }

    public String getSubType ()
    {
        return subType;
    }

    public void setSubType ( String subType )
    {
        this.subType = subType;
    }
}
