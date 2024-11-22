package svj.wedit.v6.tools;


import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.Section;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.WTreeObj;
import svj.wedit.v6.obj.book.BookTitle;

import javax.swing.tree.TreeNode;
import java.util.Collection;
import java.util.Enumeration;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 04.08.2011 15:43:39
 */
public class TreeObjTools
{
    /**
     * Выдать индексы родительских узлов, считая от 0 (от корня).
     *
     * @param node   Искомый узел.
     * @return  Полный путь - имеет вид '1,3,2,8' - индексы подузлов.
     */
    public static String getFullPath ( TreeNode node )
    {
        StringBuilder   result;
        TreeNode        tn, tn2;
        int             ic;

        result  = new StringBuilder ( 32 );
        tn      = node;
        while ( tn != null  )
        {
            tn2 = tn.getParent ();
            if ( tn2 == null )  break;
            ic  = tn2.getIndex ( tn );
            result.insert ( 0, ""+ic+',' );
            tn  = tn2;
        }
        return result.toString();
    }

    /**
     * Получить узел (обьект) по его полному пути. Полный путь имеет вид - 1,3,2,8 - индексы подузлов.
     *
     * @param book    Узел, в котором ищут (и в глубь).
     * @param nodePath  Полный путь.
     * @return  Найденный обьект либо NULL.
     */
    public static TreeNode getNodeByPath ( TreeNode book, String nodePath )
    {
        String[]    path;
        int         i;
        Integer     ii;
        String      str;
        TreeNode    node, node2;

        path    = nodePath.split ( "," );
        node    = book;
        for ( i=0; i<path.length; i++ )
        {
            //logger.debug ( i + "---- path = " + path[i] );
            str     = path[i].trim ();
            ii      = Integer.parseInt ( str );
            node2   = node.getChildAt ( ii );
            node    = node2;
        }
        //logger.debug ( "---- Get node = " + node );
        return node;
    }


    /* Найти обьект по ИД от заданого узла и вглубь. */
    public static TreeObj getObjectInNodeById ( TreeObj object, String id )
    {
        boolean                 b;
        TreeObj                 obj, findObj;
        Enumeration<TreeNode>   childs;

        if ( (object == null) || (id == null) )     return null;

        b = Utils.compareToWithNull ( object.getId(), id ) == 0;
        //Logger.getInstance().debug ( "-- TreeObjTools.getObjectInNodeById: b = " + b );
        if ( b ) return object;

        childs = object.children();
        while ( childs.hasMoreElements() )
        {
            obj     = (TreeObj) childs.nextElement();
            findObj = getObjectInNodeById ( obj, id );
            if ( findObj != null ) return findObj;
        }

        return null;
    }

    /* Найти обьект по подтипу от заданого узла и вглубь. */
    public static void getObjectsInNodeBySubtype ( TreeObj object, String subtype, Collection<TreeObj> result )
    {
        boolean                 b;
        TreeObj                 obj;
        Enumeration<TreeNode>   childs;

        if ( (object == null) || (subtype == null) )     return;

        b = Utils.compareToWithNull ( object.getSubType(), subtype ) == 0;
        //Logger.getInstance().debug ( "-- TreeObjTools.getObjectInNodeById: b = " + b );
        if ( b ) result.add ( object );

        childs = object.children();
        while ( childs.hasMoreElements() )
        {
            obj     = (TreeObj) childs.nextElement();
            getObjectsInNodeBySubtype ( obj, subtype, result );
            //if ( findObj != null ) result.add ( findObj );
        }
    }

    /**
     * На основе деревянного обьекта (паренты) создать gui-дерево.
     * @param wTreeObj  исходный обьект
     * @return   GUI дерево.
     */
    public static TreeObj createTree ( WTreeObj wTreeObj )
    {
        TreeObj                 root;
        Collection<WTreeObj>    childs;

        root    = new TreeObj();

        if ( wTreeObj == null )     return root;

        root.setUserObject ( wTreeObj );
        //root.setType ( wTreeObj.getType() );

        childs      = wTreeObj.getChildrens ();
        if ( childs != null )
        {
            for ( WTreeObj wo : childs )
            {
                createTree ( root, wo );
            }
        }

        return root;
    }

    public static void createTree ( TreeObj parent, WTreeObj wTreeObj )
    {
        TreeObj                 treeObj;
        Collection<WTreeObj>    childs;

        if ( wTreeObj == null )     return;

        treeObj     = new TreeObj();
        treeObj.setUserObject ( wTreeObj );
        //treeObj.setType ( wTreeObj.getType() );

        parent.add ( treeObj );

        childs      = wTreeObj.getChildrens();
        if ( childs != null )
        {
            for ( WTreeObj wo : childs )
            {
                createTree ( treeObj, wo );
            }
        }
    }

    /*
    /home/svj/projects/SVJ/WEdit-6/test/Miniatur
     */
    public static void createFilePath ( TreeObj treeObj, StringBuilder filePath )
    {
        TreeObj parent;
        Object  wo;
        Section section;
        BookTitle bookTitle;

        Log.l.debug ( "--- treeObj = '", treeObj, "'" );

        if ( treeObj == null )          return;
        if ( treeObj.getLevel() == 0 )  return;

        wo  = treeObj.getWTreeObj();
        if ( wo instanceof Section )
        {
            section = (Section) wo;
            filePath.insert ( 0, section.getFileName() );
            filePath.insert ( 0, '/' );
        }
        else if ( wo instanceof BookTitle)
        {
            bookTitle = (BookTitle) wo;
            filePath.append ( '/' );
            filePath.append ( bookTitle.getFileName() );
        }

        parent = (TreeObj) treeObj.getParent();

        if ( parent != null )
        {
            createFilePath ( parent, filePath );
        }
    }

    public static void createFilePath ( WTreeObj treeObj, StringBuilder filePath )
    {
        WTreeObj parent;
        Section section;

        Log.l.debug ( "--- treeObj = '%s'", treeObj );

        if ( treeObj == null )          return;
        if ( treeObj.getLevel() == 0 )  return;

        if ( treeObj instanceof Section )
        {
            section = (Section) treeObj;
            filePath.insert ( 0, section.getFileName() );
            filePath.insert ( 0, '/' );
        }
        else if ( treeObj instanceof BookTitle )
        {
            BookTitle bookTitle = (BookTitle) treeObj;
            filePath.append ( '/' );
            filePath.append ( bookTitle.getFileName() );
        }

        // Пробегаем по секциям вверх до корня, пока не закончатся.
        parent = treeObj.getParent();

        if ( parent != null )
        {
            createFilePath ( parent, filePath );
        }
    }

}
