package svj.wedit.v6.function.book.edit.paste;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.IName;
import svj.wedit.v6.obj.XmlAvailable;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.function.SimpleFunction;
import svj.wedit.v6.util.Buffer;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;


/**
 * Основа для всех функций вставления обьектов - Элемент книги, Книга, Параметры конвертации.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 14.02.2012 14:38:23
 */
public abstract class PasteBookFunction extends SimpleFunction
{
    protected DefaultMutableTreeNode[] parseBuffer (  ) throws WEditException
    {
        DefaultMutableTreeNode[] result;
        Object  obj;

        obj = Buffer.getBuffer();

        if ( obj == null )
        {
            // Нет ничего - поругаться и выйти
            throw new WEditException ( "В буфере ничего нет!" );
        }
        else if ( obj instanceof DefaultMutableTreeNode )
        {
            result      = new DefaultMutableTreeNode[1];
            result[0]   = (DefaultMutableTreeNode) obj;
        }
        else if ( obj instanceof DefaultMutableTreeNode[] )
        {
            result      = (DefaultMutableTreeNode[]) obj;
        }
        else
        {
            // Иначе - ошибка. В буфере - не элемент дерева оглавления.
            // - поругаться и выйти
            throw new WEditException ( "В буфере - не элемент дерева оглавления!" );
        }
        return result;
    }

    protected JLabel createLabel ( DefaultMutableTreeNode selectNode, DefaultMutableTreeNode[] pasteNodes, String mode )
    {
        JLabel          result;
        StringBuilder   msg;
        int             ic;

        Log.l.info("selectNode = %s", selectNode);

        msg = new StringBuilder();

        // Вы действительно желаете вставить след элементы? (перечислить) В обьект ""?
        msg.append ( "<HTML><body>\n" );
        msg.append ( "<h2>Вы действительно желаете<br/> вставить обьекты " );
        msg.append ( mode );
        msg.append ( " в '" );
        msg.append ( getName(selectNode) );
        msg.append ( "' ?</h2>" );

        msg.append ( "Уровень : " );
        msg.append ( selectNode.getLevel() );
        msg.append ( "<br/><br/>" );

        msg.append ( "<table width='80%' border='1' cellpadding='0' cellspacing='0'><tr bgcolor='#BBBBBB'><th>&nbsp;N&nbsp;</th><th>&nbsp;Название&nbsp;</th><th>&nbsp;Размер (б)&nbsp;</th><th>&nbsp;Childs&nbsp;</th></tr>\n" );

        ic          = 1;

        // todo - node?
        for ( DefaultMutableTreeNode node : pasteNodes )
        {
            Log.l.info("- move Node = %s", node);
            msg.append ( "<tr><td align='right'>&nbsp;" );
            msg.append ( ic );
            msg.append ( "&nbsp;</td><td><font color='green'>&nbsp;" );
            msg.append ( getName(node) );
            msg.append ( "&nbsp;</td><td align='right'>&nbsp;" );
            msg.append ( getSize(node) );
            msg.append ( "&nbsp;</td><td align='center'>&nbsp;" );
            //msg.append ( "-" );
            msg.append ( getChildSize(node) );
            msg.append ( "&nbsp;</td>" );
            msg.append ( "</tr>" );
            ic++;
            /*
            bookNode = (BookNode) node.getUserObject();
            msg.append ( "<tr><td align='right'>&nbsp;" );
            msg.append ( ic );
            msg.append ( "&nbsp;</td><td><font color='green'>&nbsp;" );
            msg.append ( bookNode.getName() );
            msg.append ( "&nbsp;</td><td align='right'>&nbsp;" );
            msg.append ( bookNode.getSize() );
            msg.append ( "&nbsp;</td><td align='center'>&nbsp;" );
            msg.append ( bookNode.getChildSize() );
            msg.append ( "&nbsp;</td>" );
            msg.append ( "</tr>" );

             */
        }
        msg.append ( "</table>\n<br/></body></HTML>\n" );

        result   = new JLabel ( msg.toString() );

        return result;
    }

    protected String getChildSize(DefaultMutableTreeNode node)
    {
        String result = "-";

        if ( node == null )  return result;

        if ( node.getUserObject() instanceof BookNode )
        {
            BookNode bookNode = (BookNode) node.getUserObject();
            result = Integer.toString(bookNode.getChildSize());
        }

        return result;
    }

    protected int getSize(DefaultMutableTreeNode node)
    {
        int result = -1;

        if ( node == null )  return result;

        if ( node.getUserObject() instanceof XmlAvailable)
        {
            XmlAvailable bookNode = (XmlAvailable) node.getUserObject();
            result = bookNode.getSize();
        }

        return result;
    }

    protected String getName(DefaultMutableTreeNode node)
    {
        String result = "???";

        if ( node == null )  return result;

        Object userObject = node.getUserObject();
        if ( userObject instanceof IName)
        {
            IName bookNode = (IName) node.getUserObject();
            result = bookNode.getName();
        }

        return result;

    }

    @Override
    public void rewrite ()
    {
    }

}
