package svj.wedit.v6.function.book.edit.newNode;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.book.edit.BookNodeDialog;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.function.SimpleFunction;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.tools.BookTools;

import java.awt.event.ActionEvent;


/**
 * Добавить новый элемент книги в указанный элемент первым.
 * <BR/> Файл здесь не перезаписывается. Т.е. можно откатывать (undo-redo на дереве содержимого книги.)
 * <BR/>
 * <BR/> Вводим: имя, аннотацию.
 * <BR/>
 * <BR/> Если нет описания на элемент данного уровня - требует добавить описание.
 * <BR/> Допустимость использования корневого элемента: ДА.
 * <BR/>
 * <BR/> Если выбранный элемент есть среди открытых - ругаемся.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 20.08.2011 15:16:52
 */
public class AddBookNodeInFunction extends SimpleFunction
{
    public AddBookNodeInFunction ()
    {
        setId ( FunctionId.ADD_ELEMENT_IN );
        setName ( "Добавить элемент в...");
        setIconFileName ( "add.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        TreeObj             newNode;
        TreePanel<BookContent>  currentBookContentPanel;
        TreeObj             selectNode;
        BookNodeDialog      dialog;
        BookNode            bookNode, currentNode;

        Log.l.debug ( "Start" );

        // Взять текущую книгу - TreePanel
        currentBookContentPanel = Par.GM.getFrame().getCurrentBookContentPanel();

        selectNode  = currentBookContentPanel.getCurrentObj();

        // Взять обьект отмеченного элемента
        currentNode  = (BookNode) selectNode.getUserObject();

        // Проверить, может выбранный узел (сам или в составе вышестоящего узла) уже открыт в текстах, и тогда добавлять/удалять в него ничего нельзя.
        BookTools.checkOpenText ( currentNode );

        // todo  Проверить - может уже последний в описании? И тогда создать новое Описание -- лучше ругаться.

        // Взять уровень выбранного элемента.
        //level       = selectNode.getLevel();

        // НЕТ - Корень допустим
        //if ( level == 0 )  throw new WEditException ( "Выбран корневой элемент" );


        // Диалог - Запросить имя нового обьекта
        dialog  = new BookNodeDialog ( "Новый Элемент книги", currentNode );
        //dialog.init ( level );
        dialog.showDialog();
        if ( dialog.isOK() )
        {
            bookNode    = dialog.getResult();

            // Добавить к титлу два перевода текстовой строки -- чтобы кроме титла было еще и поле для текста.
            bookNode.addEol();
            bookNode.addEol();

            newNode     = new TreeObj();
            newNode.setUserObject ( bookNode );

            // Добавить - внутрь отмеченного
            currentNode.addBookNode ( 0, bookNode );

            // Добавить в дерево после отмеченного - в самом конце, когда все действия прошли успешно
            //  (создание директории, перезапись project.xml и т.д.)
            currentBookContentPanel.insertNode ( newNode, selectNode, 0 );
            // Либо перерисовать дерево
            //TreePanel.RepaintTree = TreePanel.RepaintTree.NODE;

            // Отметить что было изменение
            currentBookContentPanel.setEdit ( true );
            currentBookContentPanel.getObject().setEdit ( true ); // BookContent - т.к. через него флаг рисуется.
            currentBookContentPanel.rewrite();
        }

        Log.l.debug ( "Finish" );
    }

    @Override
    public void rewrite ()
    {
    }

}
