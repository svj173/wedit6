package svj.wedit.v6.function.project.edit.book.create;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.MessageException;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.project.AbstractSaveProjectFunction;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.Section;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.book.BookCons;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.book.BookTitle;
import svj.wedit.v6.tools.BookStructureTools;
import svj.wedit.v6.tools.BookTools;
import svj.wedit.v6.tools.Convert;

import java.awt.event.ActionEvent;
import java.util.Date;


/**
 * Создать новую книгу и Добавить в отмеченный обьект (только в Узел).
 * <BR/> Первым по списку -- или по алфавиту, или самим выбирать место ???.
 * <BR/> - Создает файл имя.book
 * <BR/> - Перезаписывает проект в project.xml  - т.е. без возможности отката.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 18.08.2011 11:52:24
 */
public class CreateBookFunction extends AbstractSaveProjectFunction
{
    private BookContent         bookContent;

    public CreateBookFunction ()
    {
        setId ( FunctionId.CREATE_BOOK );
        setName ( "Создать книгу");
        setIconFileName ( "new.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        TreeObj             newNode;
        int                 inum;
        TreePanel<Project>  currentProjectPanel;
        TreeObj             selectNode;
        CreateBookDialog    dialog;
        Section             parentSection;
        Project             project;
        BookTitle           bookTitle;
        BookNode            bookNode, rootNode, bookCommon;
        Object              object;

        Log.l.debug ( "Start" );

        // Взять текущий проект - TreePanel
        currentProjectPanel = Par.GM.getFrame().getCurrentProjectPanel();
        if ( currentProjectPanel == null )  throw new MessageException ( "Отсутствует текущий Сборник." );

        // Взять в дереве сборника выбранный Раздел
        selectNode  = currentProjectPanel.getCurrentObj ();
        if ( selectNode == null )  throw new MessageException ( "Раздел Сборника, в котором будет \nсоздана новая книга, не выбран." );
        object   = selectNode.getWTreeObj ();
        if ( object == null )  throw new MessageException ( "Не выбрана папка Сборника,\n в которой будет создана новая книга." );
        if ( ! (object instanceof Section) )  throw new MessageException ( "Необходимо выбрать папку Сборника,\n в которой будет создана новая книга." );

        bookContent = null;
        // Диалог - Запросить имя нового обьекта
        dialog  = new CreateBookDialog ( "Создать книгу", true, null, null );
        dialog.showDialog ();
        if ( dialog.isOK() )
        {
            bookTitle   = dialog.getResult();

            // fileName = null;
            bookContent = new BookContent ( bookTitle.getName(), null );
            bookContent.setId ( BookTools.createBookNodeId ( bookTitle.getName() ) );     // уникальный ИД
            bookContent.setBookStructure ( BookStructureTools.getDefaultStructure() );
            bookContent.setBookStatus ( bookTitle.getBookStatus() );
            bookTitle.setBookContent ( bookContent );

            Log.l.debug ( "New BookStructure = %s", bookContent.getBookStructure() );

            // атрибут - дата создания
            bookContent.addAttribute ( BookCons.ATTR_NAME_CREATE_DATE, Convert.getEnDateTime ( new Date() ) );

            // Создать служебные главы.
            rootNode    = new BookNode ( bookContent.getName(), null );
            bookContent.setBookNode ( rootNode );
            //rootNode    = bookContent.getBookNode();
            // - Служебное
            bookCommon    = new BookNode ( "Служебное", rootNode );
            rootNode.addBookNode ( bookCommon );
            // - Общее
            bookNode    = new BookNode ( "Общее", bookCommon );
            bookCommon.addBookNode ( bookNode );
            // - Действующие лица
            bookNode    = new BookNode ( "Действующие лица", bookCommon );
            bookCommon.addBookNode ( bookNode );
            // - Дописать
            bookNode    = new BookNode ( "Дописать", bookCommon );
            bookCommon.addBookNode ( bookNode );
            // - Не вошедшее
            bookNode    = new BookNode ( "Не вошедшее", bookCommon );
            bookCommon.addBookNode ( bookNode );


            newNode = new TreeObj();
            newNode.setUserObject ( bookTitle );

            inum    = 0;

            // Добавить в сектора проекта - внутрь отмеченного

            parentSection   = (Section) selectNode.getWTreeObj();
            parentSection.addBook ( bookTitle );

            // Взять текущий проект
            project         = currentProjectPanel.getObject();
            bookContent.setProject ( project );

            // Сохранить новую Книгу - новый файл
            BookTools.saveNewBook ( project, selectNode, bookTitle );

            // Сохранить обновление проекта - в файле project.xml
            saveProjectFile ( project );

            // Добавить в дерево - в отмеченный раздел первым.
            currentProjectPanel.insertNode ( newNode, selectNode, inum );
        }

        Log.l.debug ( "Finish" );
    }

    public BookContent getBookContent ()
    {
        return bookContent;
    }

}
