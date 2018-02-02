package svj.wedit.v6.gui.tree.comparator;


import svj.wedit.v6.obj.Section;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.WTreeObj;

import java.util.Comparator;

/**
 * Выстраивает чилдрены в дереве.
 * <br/> Сортировщик работает только для обьектов типа Node (внутри них сортирует подузлы и устройства). Для устройств - сортировка слотов задается в самом устройстве.
 * <br/> Сортировка чилдренов, варианты сравнения: node-node; node-device; device-device.
 * <br/> Правила сортировки:
 * <br/> - node-device - device всегда больше узла - т.е. ставится после.
 * <br/> - остальные имеют собственные компараторы - две шткуи.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 06.01.2015 16:13
 */
public class TreeComparator   implements Comparator<TreeObj>
{
    /** Обьект, внутри которого данный компаратор сортирует его чилдренов. Необходим доступ к типам сортировки. */
    //private final EltexTreeNode     treeNode;
    private final NodeComparator    nodeComparator      = new NodeComparator();
    private final DeviceComparator  deviceComparator    = new DeviceComparator();

    //public TreeComparator ( EltexTreeNode treeNode )
    public TreeComparator ()
    {
        //this.treeNode = treeNode;
    }

    /**
     * Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.
     * <p>
     * @param treeObj1   Первый сравниваемый обьект.
     * @param treeObj2   Второй сравниваемый обьект.
     * @return    -1 - если первый меньше второго; 0 - равны; +1 - первый больше второго.
     */
    @Override
    public int compare ( TreeObj treeObj1, TreeObj treeObj2 )
    {
        WTreeObj w1, w2;
        boolean  o1Node, o2Node;


        if ( treeObj1 == null )  return -1;
        if ( treeObj2 == null )  return 1;

        w1       = (WTreeObj) treeObj1.getWTreeObj();
        w2       = (WTreeObj) treeObj2.getWTreeObj();
        o1Node   = w1 instanceof Section;
        o2Node   = w2 instanceof Section;

        if ( o1Node )
        {
            // первый - узел
            if ( o2Node )
            {
                // второй - тоже узел
                // - берем компаратор для сравнения узлов - согласно заданному режиму сравнения
                //nodeComparator.setCompareType ( treeNode.getNodeComparatorType() );
                return nodeComparator.compare ( w1, w2 );
            }
            else
            {
                // второй - не узел
                // - безусловно возвращаем -1 - т.к. устройство должны находиться всегда после узлов.
                return -1;
            }
        }
        else
        {
            // первый - не узел
            if ( o2Node )
            {
                // второй - узел
                // - безусловно возвращаем 1 - т.к. устройство должны находиться всегда после узлов. - т.е. первое-устройство меньше второго-узла.
                return 1;
            }
            else
            {
                // второй - тоже не узел
                // - берем компаратор для сравнения устройств - согласно заданному режиму сравнения
                //deviceComparator.setCompareType ( treeNode.getDeviceComparatorType() );
                return deviceComparator.compare ( w1, w2 );
            }
        }

        //return -1;
    }

    public void init ( NodeCompareType nodeComparatorType, WCompareType deviceComparatorType )
    {
        nodeComparator.setCompareType   ( nodeComparatorType );
        deviceComparator.setCompareType ( deviceComparatorType );
    }

}
