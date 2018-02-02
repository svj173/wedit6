package svj.wedit.v6.tools;


import svj.wedit.v6.WCons;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.WTreeObj;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.book.TextObject;

import javax.swing.text.AttributeSet;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 30.05.2012 14:29:00
 */
public class DumpTools
{
    /**
     * Перечислить содержимое  массива через символ CH.
     * В конце строки символа CH - нет
     */
    public static String printArray ( Object[] array, char ch )
    {
        String result = "Null";
        StringBuilder sb = new StringBuilder ( 128 );
        if ( ( array == null ) || ( array.length == 0 ) ) return result;

        for ( Object anArray : array )
        {
            //result = result + array[i] + ch + " ";
            sb.append ( anArray );
            sb.append ( ch );
            sb.append ( " " );
        }
        // удалить последнюю запятую (символ CH)
        result = sb.toString();
        result = result.substring ( 0, result.length() - 2 );
        return result;
    }

    public static String printMap ( Map array, String separator )
    {
        StringBuilder sb;

        sb = new StringBuilder ( 128 );
        if ( array == null  ) return "Null";
        if ( array.isEmpty()  ) return "Empty";

        for ( Object key : array.keySet() )
        {
            sb.append ( separator );
            sb.append ( key );
            sb.append ( "\t: " );
            sb.append ( array.get ( key ) );
        }
        return sb.toString();
    }

    public static String printCollection ( Collection array )
    {
        String result;
        StringBuilder sb = new StringBuilder ( 128 );
        if ( array == null  ) return "Null";
        if ( array.isEmpty()  ) return "Empty";

        int ic = 1;
        for ( Object anArray : array )
        {
            sb.append ( WCons.NEW_LINE );
            sb.append ( "- " );
            sb.append ( ic );
            sb.append ( "). " );
            sb.append ( anArray );
            ic++;
        }
        return sb.toString();
    }

    public static String printCollection ( Collection array, char ch )
    {
        String result;
        StringBuilder sb = new StringBuilder ( 128 );
        if ( array == null  ) return "Null";
        if ( array.isEmpty()  ) return "Empty";

        for ( Object anArray : array )
        {
            //result = result + array[i] + ch + " ";
            sb.append ( anArray );
            sb.append ( ch );
            sb.append ( " " );
        }
        // удалить последнюю запятую (символ CH)
        result = sb.toString();
        result = result.substring ( 0, result.length() - 2 );
        return result;
    }

    public static String printCollectionAsClass ( Collection array, char ch )
    {
        String result;
        StringBuilder sb = new StringBuilder ( 128 );
        if ( array == null  ) return "Null";
        if ( array.isEmpty()  ) return "Empty";

        for ( Object anArray : array )
        {
            //result = result + array[i] + ch + " ";
            sb.append ( anArray.getClass().getSimpleName() );
            sb.append ( '/' );
            sb.append ( anArray.hashCode() );
            sb.append ( ch );
            sb.append ( " " );
        }
        // удалить последнюю запятую (символ CH)
        result = sb.toString();
        result = result.substring ( 0, result.length() - 2 );
        return result;
    }


    public static StringBuilder printAttributeSet ( AttributeSet attr )
    {
        StringBuilder   result;
        Enumeration     en;
        Object          name, value;

        result = new StringBuilder(512);
        result.append ( "\n[ AttributeSet :" );

        if ( attr == null )
        {
            result.append ( "\n NULL" );
        }
        else
        {
            en  = attr.getAttributeNames ();
            while ( en.hasMoreElements() )
            {
                name    = en.nextElement();
                //Log.l.debug ( "attr = ", name );
                //Log.l.debug ( "attr class = ", name.getClass().getName() );  // StyleConstants
                result.append ( "\n  " );
                result.append ( name );
                result.append ( " = " );
                value   = attr.getAttribute(name);
                result.append ( value );
                result.append ( "\t\t " );
                result.append ( name.getClass().getSimpleName() );
                result.append ( '/' );
                if ( value == null )
                    result.append ( "Null" );
                else
                    result.append ( value.getClass().getSimpleName() );
            }
        }
        result.append ( "\n ]" );
        return result;
    }

    public static StringBuilder printTreeObj ( TreeObj treeObj )
    {
        StringBuilder   result;
        Enumeration     en;
        Object          child;
        Object          wo;
        WTreeObj        bookNode;

        result = new StringBuilder(512);
        result.append ( "\n[ TreeObj :" );

        if ( treeObj == null )
        {
            result.append ( "\n NULL" );
        }
        else
        {
            result.append ( "\n treeObj = " );
            result.append ( treeObj );
            // text
            //result.append ( "\n   text = " );
            wo  = treeObj.getWTreeObj();
            if ( wo != null )
            {
                if ( wo instanceof WTreeObj )
                {
                    bookNode = (WTreeObj) wo;
                    /*
                    for ( TextObject to : bookNode.getText() )
                    {
                        result.append ( "\n\t to = " );
                        result.append ( to );
                    }
                    */
                    result.append ( "\n\t to = " );
                    result.append ( bookNode.getName() );
                }
                else
                {
                    result.append ( "\n\t to (" );
                    result.append ( wo.getClass().getSimpleName() );
                    result.append ( ") = " );
                    result.append ( wo );
                }
            }

            // childs
            result.append ( "\n   childs = " );
            en  = treeObj.children ();
            while ( en.hasMoreElements() )
            {
                child    = en.nextElement();
                //Log.l.debug ( "attr = ", name );
                //Log.l.debug ( "attr class = ", name.getClass().getName() );  // StyleConstants
                result.append ( "\n  " );
                result.append ( child );
            }
        }
        result.append ( "\n ]" );
        return result;
    }

    /* Распечатать дерево. Только имена (и типы) и иерархические отступы слева. */
    public static String printTreeSimple ( TreeObj rootTree )
    {
        StringBuilder   result;
        String          level = "";

        if ( rootTree == null ) return "";

        result = new StringBuilder(1024);

        printTreeSimple ( result, rootTree, "" );
        /*
        result.append ( '\n' );
        result.append ( level );
        result.append ( rootTree.getName() );
        result.append ( " [" );
        result.append ( rootTree.getId() );
        result.append ( ':' );
        result.append ( rootTree.getType() );
        result.append ( ']' );
        result.append ( "; parentId = " );
        result.append ( rootTree.getParentId() );

        level = level + "\t";
        for ( TreeObj to : rootTree.getChildrens() )
        {
            printTreeSimple ( result, to, level );
        }
        */

        //Logger.getInstance().debug ( "GuiTools.createObjectList: OBJ_LIST = \n" + result );
        return result.toString();
    }

    private static void printTreeSimple ( StringBuilder buffer, TreeObj treeObj, String level )
    {
        if ( treeObj == null ) return;

        buffer.append ( '\n' );
        buffer.append ( level );
        buffer.append ( treeObj.getName() );
        buffer.append ( " [" );
        //buffer.append ( treeObj.getId() );
        //buffer.append ( ':' );
        buffer.append ( treeObj.getType() );
        buffer.append ( ']' );
        //buffer.append ( "; parentId = " );
        //buffer.append ( treeObj.getParentId() );

        level = level + "\t";
        //for ( TreeObj to : treeObj.getChildrens() )
        Enumeration     en;
        Object          child;
        en  = treeObj.children ();
        while ( en.hasMoreElements() )
        {
            child    = en.nextElement();
            if ( child instanceof TreeObj )
                printTreeSimple ( buffer, (TreeObj) child, level );
        }
    }


    public static StringBuilder printBookNode ( BookNode bookNode )
    {
        StringBuilder           result;
        Collection<WTreeObj>    childs;

        result = new StringBuilder(512);
        result.append ( "\n[ BookNode :" );

        if ( bookNode == null )
        {
            result.append ( "\n NULL" );
        }
        else
        {
            result.append ( "\n bookNode = " );
            result.append ( bookNode );
            // text
            result.append ( "\n   text = " );
            for ( TextObject to : bookNode.getText() )
            {
                result.append ( "\n\t to = " );
                result.append ( to );
            }

            // childs
            result.append ( "\n   childs = " );
            childs  = bookNode.getChildrens();
            for ( WTreeObj child : childs )
            {
                result.append ( "\n  " );
                result.append ( child );
            }
        }
        result.append ( "\n ]" );
        return result;
    }

    /* Распечать строку в виде кода символов. */
    public static StringBuilder printString ( String str )
    {
        StringBuilder   result;
        byte[]          bytes;

        result  = new StringBuilder (512);

        result.append ( "\nString: '");
        if ( str == null )
        {
            result.append("NULL");
        }
        else
        {
            result.append ( str );
            result.append ( "'\n" );

            result.append ( "size : " );
            result.append ( str.length() );
            result.append ( "'\n" );

            bytes   = str.getBytes();

            for ( byte aByte : bytes )
            {
                result.append ( aByte );
                result.append ( ", " );
            }
        }

        return result;
    }

    public static boolean hasLittelCode ( String str, int code )
    {
        boolean result;
        byte[]  bytes;

        result = false;
        if ( str != null )
        {
            bytes   = str.getBytes();

            for ( byte aByte : bytes )
            {
                if ( aByte <= code )
                {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

}
