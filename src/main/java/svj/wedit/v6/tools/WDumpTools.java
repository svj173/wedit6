package svj.wedit.v6.tools;


import svj.wedit.v6.gui.panel.WorkPanel;
import svj.wedit.v6.gui.panel.card.CardPanel;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.TreeObjType;

import java.util.Map;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 04.08.2011 15:24:03
 */
public class WDumpTools
{
    /* Распечатать обьект без вложений (только сам обьект) <EditablePanel> */
    public static StringBuilder printCardPanel ( WorkPanel workPanel )
    {
        String          str;
        StringBuilder   result;
        CardPanel cardPanel;
        Map<String,Object> cards;
        Object obj;

        result = new StringBuilder(512);

        result.append("\nWorkPanel: [");
        if ( workPanel == null)
        {
            result.append("\n NULL");
        }
        else
        {
            result.append("\n\tname = ");
            result.append(workPanel.getName());
            result.append("\n\ttitle = ");
            result.append(workPanel.getTitle());
            result.append("\n\tId = ");
            result.append(workPanel.getId());
            result.append("\n\tParentId = ");
            result.append(workPanel.getParentId());

            cardPanel   = workPanel.getCardPanel ();
            if ( cardPanel == null )
            {
                result.append("\n\tcardPanel = Null");
            }
            else
            {
                cards   = cardPanel.getPanels();
                result.append ( "\n\t - card current = ");
                result.append ( cardPanel.getCurrent() );
                for ( String key : cards.keySet() )
                {
                    result.append ( "\n\t-- key = " );
                    result.append ( key );
                    result.append ( "; panel = " );
                    result.append ( cards.get(key) );
                }
            }

            //result.append("\n\tParent = ");
            //result.append(workPanel.getParent());
            /*
            result.append("\n\tType = '");
            type = workPanel.getType();
            if ( type == null )
                str = "Null";
            else
                str = type.toString();
            result.append( str );
            result.append('\'');

            result.append("\tchildrens size = ");
            result.append(workPanel.getChildCount());
            */
        }
        result.append("\n]");

        return result;
    }

    /* Распечатать обьект без вложений (только сам обьект) */
    public static StringBuilder printTreeObj ( TreeObj obj )
    {
        TreeObjType     type;
        String          str;
        StringBuilder   result;

        result = new StringBuilder(512);

        if ( obj == null)
        {
            result.append("\nTreeObj: NULL");
        }
        else
        {
            result.append("\nTreeObj:\n\tName = ");
            result.append(obj.getName());
            result.append("\n\tId = ");
            result.append(obj.getId());
            result.append("\n\tParentId = ");
            result.append(obj.getParentId());
            result.append("\n\tParent = ");
            result.append(obj.getParent());
            result.append("\n\tType = '");
            type = obj.getType();
            if ( type == null )
                str = "Null";
            else
                str = type.toString();
            result.append( str );
            result.append('\'');

            result.append("\tchildrens size = ");
            result.append(obj.getChildCount());

        }

        return result;
    }

    public static String printArray ( Object[] array )
    {
        if ( array == null )    return "Null";
        StringBuilder    result  = new StringBuilder ( 512 );

        for ( Object anArray : array )
        {
            result.append ( anArray );
            result.append ( "; " );
        }
        return result.toString();
    }


}
