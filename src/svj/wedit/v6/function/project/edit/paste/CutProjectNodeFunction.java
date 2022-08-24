package svj.wedit.v6.function.project.edit.paste;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.Section;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.book.BookTitle;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.*;
import svj.wedit.v6.util.Buffer;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import java.awt.event.ActionEvent;


/**
 * Дерево Сборника.
 * <BR/> Вырезать отмеченный обьект или несколько одноуровневых обьектов.
 * <BR/> Допустимость использования корневого элемента дерева: НЕТ.
 * <BR/> Только однородные для верхнего уровня!
 * <BR/>
 * <BR/> todo Большой недостаток - можнов ырезать книгу-сектор, и забыть про это.
 * <BR/> Лучше - расширенный диалог - слева - кого, справа - куда
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 03.02.2012 11:06:04
 */
@Deprecated
public class CutProjectNodeFunction extends Function
{
    public CutProjectNodeFunction()
    {
        setId ( FunctionId.CUT_PROJECT_ELEMENT );
        setName ( "Вырезать" );
        setIconFileName ( "cut.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        TreeObj[]           selectNodes;
        TreePanel<Project>  projectTreePanel;
        JLabel              label;
        int                 ic;

        // 1) Взять отмеченные
        // Не проверяем на отсутствие выделения, т.к. если не было отмеченных то
        //  еще раньше (в NodeTools) выкинется исключение
        // Там же, в глубине, проверяется отмеченность одноуровневых элементов. И проверка на корень.
        selectNodes  = ProjectTools.getSelectedNodesForCut(false);
        Log.f.info ( "selectNodes = %s", WDumpTools.printArray ( selectNodes ) );

        // 2) Проверить, может выбранный узел (сам или в составе вышестоящего узла) уже открыт
        // в текстах, и тогда добавлять/удалять в него ничего нельзя.
        ProjectTools.checkOpenBooks ( selectNodes );

        // 3) Стартовый диалог
        label   = createLabel ( selectNodes );
        ic      = DialogTools.showConfirmDialog ( Par.GM.getFrame(),  "Вырезать " + selectNodes.length
                + " обьектов", label );

        if ( ic != 0 )  return;

        // 4) Создать копию обьектов - необходимо при переносе элементов книг из одной книги в другую.
        // Для CUT не надо создавать clone - т.к. ИД обьектов останется уникальным.

        Log.f.info ( "new Clone Nodes = \n%s", DumpTools.printArray ( selectNodes, '\n' ) );

        // 5) Занести в буфер
        Buffer.setBuffer ( selectNodes );

        // 6) Удалить в дереве    todo - После успешного Paste
        // - Взять панель дерева текущего Сборника
        projectTreePanel = Par.GM.getFrame().getCurrentProjectPanel();

        for ( DefaultMutableTreeNode node : selectNodes )
        {
            Log.f.info ( "--- removeNode = %s", node );
            //currentBookContentPanel.removeNode ( node );
            // удаляем и во внутреннем дереве и в gui-дереве.
            BookTools.removeBook ( projectTreePanel, node );
        }

        // Отметить что было изменение  -- НЕ надо. Там ведь все уже поменяно. И по Save ничего не произойдет.
        projectTreePanel.setEdit ( true );
        projectTreePanel.getObject().setEdit ( true ); // BookContent - т.к. через него флаг рисуется.
        //Log.f.debug ( "Finish" );
    }

    private JLabel createLabel ( TreeObj[] selectNodes )
    {
        JLabel          result;
        StringBuilder   msg;
        int             ic;

        msg = new StringBuilder();

        msg.append ( "<HTML><body>\n" );
        msg.append ( "<h2>Вы действительно желаете<br/> вырезать '" );
        msg.append ( selectNodes.length );
        msg.append ( "' обьектов ?</h2>" );
        msg.append ( "Уровень : " );
        msg.append ( selectNodes[0].getLevel() );
        msg.append ( "<br/><br/>" );

        msg.append ( "<table width='80%' border='1' cellpadding='0' cellspacing='0'><tr bgcolor='#BBBBBB'><th>&nbsp;N&nbsp;</th><th>&nbsp;Название&nbsp;</th><th>&nbsp;Размер (б)&nbsp;</th><th>&nbsp;Childs&nbsp;</th></tr>\n" );

        ic          = 1;

        String name;
        int size, chidSize;
        Object obj;
        Section section;
        BookTitle bookTitle;

        for ( TreeObj node : selectNodes )
        {
            obj = node.getUserObject();
            if (obj instanceof Section)
            {
                section = (Section) obj;
                name = section.getName();
                size = 0;
                chidSize = section.getSize();
            }
            else
            {
                bookTitle = (BookTitle) obj;
                name = bookTitle.getName();
                chidSize = 0;
                size = bookTitle.getSize();
            }
            msg.append ( "<tr><td align='right'>&nbsp;" );
            msg.append ( ic );
            msg.append ( "&nbsp;</td><td><font color='green'>&nbsp;" );
            msg.append ( name );
            msg.append ( "&nbsp;</td><td align='right'>&nbsp;" );
            msg.append ( size );
            msg.append ( "&nbsp;</td><td align='center'>&nbsp;" );
            msg.append ( chidSize );
            msg.append ( "&nbsp;</td>" );
            msg.append ( "</tr>" );
            ic++;
        }
        msg.append ( "</table>\n<br/></body></HTML>\n" );

        result   = new JLabel ( msg.toString() );

        return result;
    }

    @Override
    public void rewrite ()
    {
    }

    @Override
    public void init () throws WEditException
    {
    }

    @Override
    public void close ()
    {
    }

    @Override
    public String getToolTipText ()
    {
        return "Вырезать все отмеченные элементы книги. Сохранить в промежуточном буфере.";
    }

}
