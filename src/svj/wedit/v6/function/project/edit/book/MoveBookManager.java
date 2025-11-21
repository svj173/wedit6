package svj.wedit.v6.function.project.edit.book;

import svj.wedit.v6.Par;
import svj.wedit.v6.exception.MessageException;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.dialog.SimpleDialog;
import svj.wedit.v6.gui.renderer.SectionCellRenderer;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.*;

import javax.swing.*;

import java.io.File;

public class MoveBookManager {

    // текущий проект-сборник
    private TreePanel<Project> projectTreePanel;
    private TreePanel<Project>  targetTreePanel;



    public boolean dialogIsOk(String functionName) throws WEditException
    {
        projectTreePanel    = Par.GM.getFrame().getCurrentProjectPanel();

        // Сформировать таргет-дерево - показывать только папки.
        // Папки, в которые нельзя копировать (где содержится исходная книга) - не показывать.

        if ( projectTreePanel == null ) return false;

            SimpleDialog dialog;
            TreeObj root, currentBook, targetTree, targetSection;
            String srcFile, targetFile;
            JPanel panel;
            JLabel label;

            // Взять текущую книгу или Раздел
            currentBook = projectTreePanel.getCurrentObj();
            Log.l.info("MoveBook: currentBook = %s", currentBook);
            if (currentBook == null) throw new MessageException("Не выбрана книга для переноса.");

            // Выясняем, если переносим книгу, то не открыта ли она. - даже если на экране Тексты другой книги!!!
            // А если переносим Раздел, то есть ли открытые книги данного раздела? - Ругаемся.
            // Проверяем по ИД книги
            //BookTools.checkOpenText ();       -- Лишнее.


            // Переносим разделы и книги.

            root = projectTreePanel.getRoot();
            //if ( root == null )  throw new MessageException ( "Не выбрана книга для переноса." );
            //Log.l.info ( "Project tree = %s", DumpTools.printTreeSimple ( root ) );

            // Сформировать дерево из одних только Секций - без Книг
            //targetTree = createTree ( (WTreeObj) root.getWTreeObj() );
            targetTree = root.clone();

            //DialogTools.showHtml ( "Tree", "<html><pre>"+DumpTools.printTreeSimple ( root ) + "</pre><br/><br/></html>" );

            // Создать таргет-панель с деревом
            targetTreePanel = new TreePanel<Project>(targetTree, projectTreePanel.getObject());
            targetTreePanel.addRenderer(TreeObjType.SECTION, new SectionCellRenderer());
            dialog = new SimpleDialog(functionName);
            dialog.addToCenter(targetTreePanel);

            // Напоминалки
            panel = new JPanel();
            dialog.addToEast(panel);
            label = new JLabel("<html><font color=red>&nbsp;&nbsp;&nbsp;Напоминаем, что если вы переносите книгу, <br/>то она не должна быть открыта. <br/>А если переносите Раздел, то не <br/>должно быть открытых книг из этого Раздела. <br/>Иначе эти книги просто пропадут!</font></html>");
            panel.add(label);

            // todo В Диалоге выводить и книги - чтоыб можно было добавлять после указанной книги.
            // todo - отказаться от этого и делать через cut-paste

            dialog.pack();
            dialog.showDialog();

            return dialog.isOK();
    }

    public boolean moveFile ( String srcFile, String targetFile ) throws WEditException
    {
        String      srcFileName, targetFileDir;
        WTreeObj srcObj, trgObj;         // BookNode, BookTitle, Section

        Log.l.info ( "-- srcFile = %s;\n targetFile = %s", srcFile, targetFile );

        /*
        // Взять полные пути файлов
        // - Исходный файл
        srcObj = (WTreeObj) currentBook.getWTreeObj();
        // - Если это BookTitle - то имеем только fileName=b1.book. Необходимо получить и его Сектор
        // Если это Сектор - то имеем локальное имя файла сектора и парент-сектора.

        // - Результирующая директория
        trgObj = (WTreeObj) targetSection.getWTreeObj();
        Log.l.info ( "-- currentBook = %s;\n targetSection = %s", srcObj, trgObj );
        */

        // Перенести файл на новое место
        File fileSrc, fileTrg;
        fileSrc = new File ( srcFile );
        fileTrg = new File ( targetFile );
        return fileSrc.renameTo ( fileTrg );

        //throw new WEditException ( "Не реализована!\nsrc = "+ srcFile +"\ntarget = " + targetFile );
    }

    public TreePanel<Project> getTargetTreePanel() {
        return targetTreePanel;
    }

    public TreePanel<Project> getProjectTreePanel() {
        return projectTreePanel;
    }

    // todo закрыть открытые текст-книги исходников (и в подсекциях тоже)
    public void closeSourceBooks() {
        // projectTreePanel

    }

}
