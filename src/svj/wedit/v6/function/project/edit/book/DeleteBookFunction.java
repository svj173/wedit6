package svj.wedit.v6.function.project.edit.book;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.MessageException;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.project.AbstractSaveProjectFunction;
import svj.wedit.v6.gui.tabs.TabsPanel;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.Section;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookTitle;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.DialogTools;
import svj.wedit.v6.tools.FileTools;

import javax.swing.*;
import java.awt.event.ActionEvent;


/**
 * Удалить книгу из Сборника.
 * <BR/> Преспрашивает
 * <BR/> Удаляет:
 * <BR/> - Запись из проекта
 * <BR/> - Саму книгу.
 * <BR/> Пока без возможности отката. (Переводить файл книги в tmp?)
 * <BR/>
 * <BR/> При удалении книги необходимо:
 * <BR/> - удалить табики текстов: из рабочего массива.
 * <BR/> - удалить панель табиков текстов из cardLayout
 * <BR/> - закрыть таб-панель книги
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 05.04.2013 10:42:24
 */
public class DeleteBookFunction extends AbstractSaveProjectFunction
{
    public DeleteBookFunction ()
    {
        setId ( FunctionId.DELETE_BOOK );
        setName ( "Удалить книгу");
        setIconFileName ( "delete.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        int                 inum;
        TreePanel<Project>  currentProjectPanel;
        TreeObj             selectNode, parentNode;
        Project             project;
        BookTitle           bookTitle;
        String              msg, bookFileName;
        StringBuilder       dirBookName;
        Section             section;
        boolean             b;
        TreePanel<BookContent>  bookTreePanel;
        TabsPanel<TreePanel<BookContent>> tabsTextPanel;

        Log.l.debug ( "Start" );

        // Взять текущий проект - TreePanel
        currentProjectPanel = Par.GM.getFrame().getCurrentProjectPanel();

        selectNode  = currentProjectPanel.getCurrentObj();    // BOOK
        parentNode  = selectNode.getTreeParent ();         // Section
        Log.l.debug ( "book for delete = ", selectNode );
        Log.l.debug ( "parent for delete = ", parentNode );

        bookTitle   = (BookTitle) selectNode.getWTreeObj();
        section     = (Section) parentNode.getWTreeObj();

        // Диалог - Запросить подтверждение
        msg     = Convert.concatObj ( "Удалить книгу '", bookTitle.getName(), "' ?" );

        inum    = DialogTools.showConfirmDialog ( Par.GM.getFrame(), msg, "Удалить", "Отменить" );
        if ( inum == JOptionPane.YES_OPTION )
        {
            // Удалить из сектора проекта
            b               = section.deleteBook ( bookTitle );
            if ( ! b )
                throw new MessageException ( "Не удалось удалить Книгу из Раздела\n '", section.getName(), "'." );

            // Взять текущий проект
            project         = currentProjectPanel.getObject();

            // Сформировать имя файла книги
            dirBookName     = FileTools.createNodeFilePath ( project, selectNode ); // только диреткория - по ближайшей секции
            bookFileName    = Convert.concatObj ( dirBookName, '/', bookTitle.getFileName() );
            Log.l.debug ( "file for delete = ", bookFileName );

            // Удалить файл книги
            if ( ! FileTools.deleteFile ( bookFileName ) )
                throw new MessageException ( "Не удалось удалить файл Книги\n '", bookFileName, "'." );

            // Сохранить изменения проекта - в файле project.xml
            saveProjectFile ( project );

            BookContent bookContent;
            bookContent = bookTitle.getBookContent();
            if ( bookContent != null )
            {
                // Эта книга открыта и используестя в Редакторе - закрыть все применяемые табики - Закрыть табик книги. Закрыть и удалить таб-панель текстов книги.
                // - Закрываем все тексты, связанные с этой книгой. -- лучше всего оперировать ИД и закрывать все что связано с этим ИД.
                Par.GM.getFrame().closeTextsPanel ( bookContent.getId() );
                // todo Закрыть табик книги. -- прогоняем по всем таб-книгам и сравниваем на ИД?
                // Удалить табс-панель с табиками открытых книг Сборника - удаляем из массива панелей.
                Par.GM.getFrame().deleteBook ( bookContent.getId (), false );
                /*
                tabsTextPanel   = Par.GM.getFrame().getBooksPanel().getTabsPanel ( bookContent.getId() );
                tabsTextPanel.removeTab ( bookContent.getId() );
                Par.GM.getFrame().getBooksPanel().deleteTabsPanel ( bookContent.getId() );    // tabsId ?
                // принимаем gui-изменения
                Par.GM.getFrame().getBooksPanel().revalidate ();
                // Закрыть все таб-панели данной табс-панели книги
                //tabsBooksPanel.removeAll();
                */
            }

            // Удалить из дерева - в самом конце, когда все действия прошли успешно (создание директории, перезапись project.xml и т.д.)
            currentProjectPanel.removeNode ( selectNode );
        }

        Log.l.debug ( "Finish" );
    }

}
