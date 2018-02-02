package svj.wedit.v6.function.book.edit.newNode;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.book.edit.BookNodeDialog;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.tools.BookTools;

import java.awt.event.ActionEvent;


/**
 * Добавить новый элемент книги после указанного элемента.
 * <BR/> Файл здесь не перезаписывается. Т.е. можно откатывать (undo-redo на дереве содержимого книги.)
 * <BR/>
 * <BR/> Вводим: имя, аннотацию.
 * <BR/>
 * <BR/> Если нет описания на элемент данного уровня - требует добавить описание.
 * <BR/> Допустимость использования корневого элемента: нет.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 20.08.2011 15:16:52
 */
public class AddBookNodeAfterFunction extends Function
{
    public AddBookNodeAfterFunction ()
    {
        setId ( FunctionId.ADD_ELEMENT_AFTER );
        setName ( "Добавить элемент после...");
        setIconFileName ( "add.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        TreeObj             newNode;
        int                 inum;
        TreePanel<BookContent>  currentBookContentPanel;
        TreeObj             selectNode, parentObj;
        BookNodeDialog      dialog;
        BookNode            bookNode, parentNode;

        Log.l.debug ( "Start" );

        // Взять текущую книгу - TreePanel
        currentBookContentPanel = Par.GM.getFrame().getCurrentBookContentPanel();

        // Взять отмеченный элемент книги.
        selectNode  = currentBookContentPanel.getCurrentObj();

        // Корень НЕ допустим
        BookTools.errorIfRoot ( selectNode );
        // Взять уровень выбранного элемента. Проверить - может уже последний?
        //level       = selectNode.getLevel();
        //if ( level == 0 )  throw new WEditException ( "Выбран корневой элемент" );

         // = (BookNode) selectNode.getUserObject();

        // Взять родителя отмеченного элемента
        parentObj   = (TreeObj) selectNode.getParent();
        parentNode  = (BookNode)  parentObj.getUserObject();

        // Проверить, может выбранный узел (сам или в составе вышестоящего узла) уже открыт в текстах, и тогда добавлять/удалять в него ничего нельзя.
        BookTools.checkOpenText ( parentNode );

        // Диалог - Запросить имя нового обьекта (todo запросить и тип, согласно уровню)
        dialog  = new BookNodeDialog ( "Новый Элемент книги", parentNode );
        dialog.showDialog();

        if ( dialog.isOK() )
        {
            bookNode    = dialog.getResult();

            // Добавить к титлу два перевода текстовой строки -- чтобы кроме титла было еще и поле для текста.
            bookNode.addEol();
            bookNode.addEol();

            newNode     = new TreeObj();
            newNode.setUserObject ( bookNode );

            // Взять номер отмеченного у его родителя
            inum        = parentObj.getIndex ( selectNode );
            Log.l.debug ( "selected index by parent = %s", inum );

            inum++;
            // Добавить - внутрь отмеченного
            parentNode.addBookNode ( inum, bookNode );

            // Добавить в дерево после отмеченного - в самом конце, когда все действия прошли успешно
            //  (создание директории, перезапись project.xml и т.д.)
            currentBookContentPanel.insertNode ( newNode, parentObj, inum );

            // Отметить его в дереве - автоматом в insertNode
            //currentBookContentPanel.selectNode ( newNode );

            // Отметить что было изменение
            currentBookContentPanel.setEdit ( true );
            currentBookContentPanel.getObject().setEdit ( true ); // BookContent - т.к. через него флаг рисуется.

            // Ставим флаг что по rewrite панель дерева должна пнуть свою модель чтобы та приняла новые изменения. -- уже внутри insertNode
        }

        Log.l.debug ( "Finish" );
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
        return "Добавить новый элемент книги после указанного элемента.";
    }

}
