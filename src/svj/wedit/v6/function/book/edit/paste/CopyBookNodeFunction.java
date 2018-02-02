package svj.wedit.v6.function.book.edit.paste;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.obj.function.SimpleFunction;
import svj.wedit.v6.tools.BookTools;
import svj.wedit.v6.util.Buffer;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;


/**
 * Копировать выбранные узлы книги во внутреннйи буфер.
 * <BR/> Если узлов несколько - они долны быть одноуровневыми.  -- НЕТ, копируем только один узел. Если отмечено несколько - берем последний отмеченный.
 * <BR/> Допустимость использования корневого элемента: ДА.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 02.02.2012 14:06:04
 */
public class CopyBookNodeFunction extends SimpleFunction
{
    public CopyBookNodeFunction ()
    {
        setId ( FunctionId.COPY_ELEMENT );
        setName ( "Копировать");
        setIconFileName ( "copy.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        DefaultMutableTreeNode selectNode, copyNode;

        // Взять отмеченный
        selectNode  = BookTools.getSelectedNode();
        // Создать копию обьекта
        copyNode    = BookTools.createClone ( selectNode );
        // Занести в буфер
        Buffer.setBuffer ( copyNode );
    }

    @Override
    public void rewrite ()
    {
    }

}
