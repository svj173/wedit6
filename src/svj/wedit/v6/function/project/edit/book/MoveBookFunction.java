package svj.wedit.v6.function.project.edit.book;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.MessageException;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.project.AbstractSaveProjectFunction;
import svj.wedit.v6.gui.dialog.SimpleDialog;
import svj.wedit.v6.gui.renderer.SectionCellRenderer;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.*;
import svj.wedit.v6.obj.book.BookTitle;
import svj.wedit.v6.tools.BookTools;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collection;
import java.util.Enumeration;

/**
 * Раздел "Сборник" - переместить книгу в пределах Сборников.
 * <BR/> Открывается диалог, в котором отображается дерево Сборника - куда перенести.
 * <BR/> Такой подход гарантирует, что выкушенная из Сборника книга будет занесена в другое место, не потеряется и не забудется в буфере обмена.
 * <BR/>
 *
 * project.xml
 * 		<section name="Дневники" dirName="dnevniki">
 			<section name="Маша" dirName="masha">
 				<book name="Маша Жиганова" status="В работе">masha.book</book>
 			</section>
 			<book name="Автобиография" status="В работе">mybiogr.book</book>
 			<book name="Дневник" status="В работе">dnevniki.book</book>

 Дневник
 <bookContent name="Дневник">
 	<id>/home/svj/Serg/Stories/SvjStores/dnevniki/dnevniki.book</id>

 Расписать алгоритм
 1) Выводится диалог
 - Определяется - кто (сектор или книга) и куда (только сектор)
 - Если книга открыта или открыты книги выбранного сектора (и подсекторов) - закрыть в гуи, с сохранением изменений.
 2) Выбранный обьект клонируется
 3) Этот обьект удаляется из его парента
 4) Добавляется в новый парент
 5) У него изменяются все пути книг

 * <BR/>
 * <BR/>  Сделать:
 * <BR/> +1) ID - генерить уникальный.
 * <BR/> +2) Имя файла - заносить в bookContent при парсинге файла.
 * <BR/> +3) BookTitle - также генерить уникальную ИД книги, которая будет заноситься в BookContent
 * <BR/>
 * <BR/> CutProjectNodeFunction - НЕ используем - это CUT-PASTE, поэтому можно переносить в другие Сборники и пр.
 * <BR/> Минус - можно выкусить книгу и забыть.
 * <BR/>
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 28.12.2015 15:51
 */
public class MoveBookFunction  extends AbstractSaveProjectFunction
{
    public MoveBookFunction ()
    {
        setId ( FunctionId.MOVE_BOOK );
        setName ( "Переместить Книгу или Сектор (x)" );
        setIconFileName ( "move_book.png" );
        //setToolTip ();
        //setParamsType ( ParameterCategory.BOOK );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        MoveBookManager manager;
        TreePanel<Project>  targetTreePanel;

        manager = new MoveBookManager();

        // Сформировать таргет-дерево - показывать только папки.
        // Папки, в которые нельзя копировать (где содержится исходная книга) - не показывать.


        if ( manager.dialogIsOk(getName()) )
        {
            TreeObj root, currentBook, targetTree, targetSection;
            String srcFile, targetFile;

            // todo закрыть открытые текст-книги исходников (и в подсекциях тоже)
            manager.closeSourceBooks();

            targetTreePanel = manager.getTargetTreePanel();

                // Нажата Принять.
                // - Берем таргет-секцию. (куда переносим)
                targetSection   = targetTreePanel.getCurrentObj();
                if ( targetSection == null )  throw new MessageException ( "Не выбран раздел для переноса." );

                // todo Остались Проблемы:
                // -1) При переносе книги - в дереве старое исчезает, а новое не появляется.
                // -2) Не проверял перенос Разделов

            // Взять текущую книгу или Раздел
            TreePanel<Project> projectTreePanel = manager.getProjectTreePanel();
            currentBook = projectTreePanel.getCurrentObj();

                // Формируем полные пути файлов.
                srcFile     = createSrcFileName ( currentBook, projectTreePanel );
                targetFile  = createTargetFileName ( targetSection, projectTreePanel, currentBook );
                //throw new WEditException ( "srcFile : " + srcFile + "; targetFile = " + targetFile );

                // Переносим книгу currentBook в  targetSection
                // - Переносим файл (имя.book или Раздел) в новую директорию  - физически.
                if ( manager.moveFile ( srcFile, targetFile ) )
                {
                    // Файл успешно перемещен

                    // todo Может применять cut-paste и перемещать и в другие Проекты?
                    // - в буфере вешать флаг - было коипрвоание, не было копирования из буфера - чтобы при закрытии
                    // напоминать что сотался подвешенный обьект.

                    // 1) Изменяем Структуру проекта
                    Section  section, parentSec;
                    WTreeObj currentObj, newObj, parent;

                    section     = (Section) targetSection.getWTreeObj();
                    //BookTitle bookTitle     = (BookTitle) currentBook.getWTreeObj();
                    currentObj  = (WTreeObj) currentBook.getWTreeObj();
                    newObj      = currentObj.clone();
                    // - удалить старое
                    parent      = currentObj.getParent ();
                    if ( parent != null && parent instanceof Section )
                    {
                        parentSec = (Section) parent;
                        parentSec.delete ( currentObj );
                    }
                    // - занести новое
                    if ( currentObj instanceof BookTitle )
                        section.addBook ( (BookTitle) currentObj );
                    else if ( currentObj instanceof Section )
                        section.addSection ( ( Section ) currentObj );

                    // - Сохранить обновление проекта - в файле project.xml
                    saveProjectFile ( projectTreePanel.getObject() );

                    // - Изменяем данные в ГУИ-дереве Сборника
                    // - Удалить старое в дереве
                    projectTreePanel.removeNode ( currentBook );

                    // todo Меняем имена файлов в открытых проектах.
                    // Искать среди открытых книг такой BookContent. Если нашли - изменить в нем полный путь файла.
                    changeFileName ();

                    // todo Может у старого обьекта просто поменять парент? И доабвить чилдреном новому паренту?

                    // - Добавить в дерево - в отмеченный раздел первым.
                    //TreeObj newNode = new TreeObj();
                    //newNode.setUserObject ( bookTitle );
                    //projectTreePanel.insertNode ( newNode, selectNode, 0 );
                    // todo - Почему-то не добавляет в дерево (только удаляет)
                    currentBook.setParent ( null );
                    currentBook.setParentId ( null );

                    TreeObj newNode     = new TreeObj();
                    newNode.setUserObject ( newObj );

                    //projectTreePanel.insertNode ( currentBook, targetSection, 0 );   // здесь же и выборка в дереве нового обьекта
                    //projectTreePanel.getTreeModel().insertNodeInto ( currentBook, targetSection, 0 );
                    projectTreePanel.getTreeModel().insertNodeInto ( newNode, targetSection, 0 );
                    projectTreePanel.setRepaintTreeMode ( TreePanel.RepaintTree.ALL_WITH_ACTION );

                    // todo А если книга открыта и есть отрытые страницы - как с ними? Особенно если перенос в другой Сборник.
                    // - Здесь надо также менять и BookStructure.fileName
                }
                else
                {
                    // Ошибка переноса
                    throw new WEditException ( "Ошибка переноса файлов.\n  srcFile : " + srcFile + "\n  targetFile = " + targetFile );
                }
            }
    }

    private void changeFileName ()
    {
        // todo Если перенесли Раздел
        //     Здесь надо проверять есть ли октрытые книги из раздела или его подразделов и им всем поменять имена файлов.
        //Par.GM.getFrame().containBook ( bookId, project );


    }

    private String createSrcFileName ( TreeObj currentBook, TreePanel<Project> projectTreePanel )  throws WEditException
    {
        String result;
        Object obj;

        try
        {
            obj = currentBook.getWTreeObj();
            if ( obj instanceof BookTitle )
            {
                BookTitle bookTitle = (BookTitle) obj;
                //result = bookTitle.getFileName();
                // bookTitle.getParent() == null   Section
                //throw new WEditException ( "bookTitle parent : " + bookTitle.getParent() );
                result = BookTools.createFilePath ( projectTreePanel.getObject(), bookTitle.getParent(), bookTitle );
                //StringBuilder filePath    = new StringBuilder (128);
                //TreeObjTools.createFilePath ( bookTitle, filePath );
                //result = filePath.toString ();
            }
            else if ( obj instanceof Section )
            {
                Section section = (Section) obj;
                //throw new WEditException ( "section parent : " + section.getParent() );
                result = BookTools.createFilePath ( projectTreePanel.getObject (), section );
            }
            else
            {
                throw new WEditException ( "Неизвестный тип исходного обьекта : " + currentBook );
            }

        } catch ( Exception e )         {
            Log.l.error ( "error. currentBook = "+currentBook, e );
            throw new WEditException ( "Ошибка создания полного пути исходного файла :\n"+ e, e );
        }

        return result;
    }

    private String createTargetFileName ( TreeObj targetSection, TreePanel<Project> projectTreePanel, TreeObj currentBook )  throws WEditException
    {
        String result;
        Object obj;

        try
        {
            obj = targetSection.getWTreeObj();
            if ( obj instanceof Section )
            {
                Section section = (Section ) obj;
                result = BookTools.createFilePath ( projectTreePanel.getObject(), section );

                obj = currentBook.getWTreeObj();
                BookTitle bookTitle = (BookTitle) obj;
                result = result + "/" + bookTitle.getFileName();
            }
            else
            {
                throw new WEditException ( "Неизвестный тип исходного обьекта : " + targetSection );
            }

        } catch ( Exception e )         {
            Log.l.error ( "error. targetSection = "+targetSection, e );
            throw new WEditException ( "Ошибка создания полного пути результирующего файла :\n"+ e, e );
        }

        return result;
    }

    private TreeObj createTree ( WTreeObj wTreeObj )
    {
        TreeObj                 root;
        Collection<WTreeObj>    childs;

        root    = new TreeObj();

        if ( wTreeObj == null )     return root;

        root.setUserObject ( wTreeObj );
        //root.setType ( wTreeObj.getType() );

        childs      = wTreeObj.getChildrens();
        if ( childs != null )
        {
            for ( WTreeObj wo : childs )
            {
                createTree ( root, wo );
            }
        }

        return root;
    }

    private void createTree ( TreeObj parent, WTreeObj wTreeObj )
    {
        TreeObj                 treeObj;
        Collection<WTreeObj> childs;

        if ( wTreeObj == null )     return;
        if ( wTreeObj.getType() != TreeObjType.SECTION )     return;

        treeObj     = new TreeObj();
        treeObj.setUserObject ( wTreeObj );
        //treeObj.setType ( wTreeObj.getType() );

        parent.add ( treeObj );

        childs      = wTreeObj.getChildrens ();
        if ( childs != null )
        {
            for ( WTreeObj wo : childs )
            {
                createTree ( treeObj, wo );
            }
        }
    }

    private void createSectionTree ( TreeObj targetTree, TreeObj treeObj )
    {
        Enumeration en;
        Object          child;
        TreeObj to, tt;

        en  = treeObj.children();
        while ( en.hasMoreElements() )
        {
            child    = en.nextElement();
            if ( child instanceof TreeObj )
            {
                to = (TreeObj) child;
                if ( to.getType() == TreeObjType.SECTION )
                {
                    tt = new TreeObj();
                    tt.setUserObject ( to.getUserObject() );
                    targetTree.add ( tt );
                    createSectionTree ( tt, to );
                }
            }
        }
    }

    @Override
    public void rewrite ()
    {
        /* Resolver
        TreePanel<Project> projectTreePanel;
        projectTreePanel    = Par.GM.getFrame().getCurrentProjectPanel();
        if ( projectTreePanel == null )
            setEnabled ( false );
        else
            setEnabled ( true );
        */
    }

}
