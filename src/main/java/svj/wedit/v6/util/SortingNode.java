package svj.wedit.v6.util;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.tools.BookTools;

import javax.swing.tree.DefaultMutableTreeNode;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 02.02.2012 14:23:39
 */
public class SortingNode
{
    public static void sorting ( DefaultMutableTreeNode[] nodes, boolean sortOrder )
            throws WEditException
    {
        DefaultMutableTreeNode  parentNode;
        int	                    i, j, cmpRes, ic;

        // Взять родителя отмеченного элемента
        parentNode  = BookTools.getParentNode ( nodes[0] );
        ic          = nodes.length;

        // Сортировать
        for ( i=0; i<ic-1; i++ )
        {
            for ( j=i+1; j<ic; j++ )
            {
                cmpRes  = compareNodes	( parentNode, nodes[i], nodes[j] );
                // сортировка по возрастанию
                if ( sortOrder		&& (cmpRes > 0) )
                {
                    swapObjects		( nodes, i, j );
                }
                // сортировка по убыванию
                if ( (! sortOrder)	&& (cmpRes < 0) )
                {
                    swapObjects		( nodes, i, j );
                }
            }
        }
    }

    private static int compareNodes ( DefaultMutableTreeNode parentNode,
                                      DefaultMutableTreeNode node1,
                                      DefaultMutableTreeNode node2 )
    {
        int result, i1, i2;

        i1  = parentNode.getIndex ( node1 );
        i2  = parentNode.getIndex ( node2 );
        result  = 0;
        if ( i1 > i2 ) result =  1;
        if ( i1 < i2 ) result = -1;
        return result;
    }


    /**
     *  Поменять местами обьекты в массиве
     * @param nodes   Исходный массив
     * @param srcIdx  Индекс первого элемента
     * @param dstIdx  Индекс второго элемента
     */
    private static void swapObjects ( DefaultMutableTreeNode[] nodes, int srcIdx, int dstIdx )
    {
        DefaultMutableTreeNode	tmpObj;
        tmpObj          = nodes[dstIdx];
        nodes[dstIdx]   = nodes[srcIdx];
        nodes[srcIdx]   = tmpObj;
    }
    
}
