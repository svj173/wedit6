package svj.wedit.v6.function.text;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.params.SimpleParameter;
import svj.wedit.v6.gui.dialog.LoadImageDialog;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.BookTools;
import svj.wedit.v6.tools.FileTools;
import svj.wedit.v6.tools.GuiTools;

import javax.swing.*;
import javax.swing.text.StyledDocument;

import java.awt.event.ActionEvent;
import java.io.File;


/**
 * Загрузить в текст картинку.
 * <BR/> Здесь в диалоге показаны директории и их содержимого.
 * И если файлы - изображения - то справа от них будет небольшое превью.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 28.10.2012 15:07:41
 */
public class SelectImageFunction extends Function
{
    public SelectImageFunction ()
    {
        setId ( FunctionId.SELECT_IMAGE );
        setName ( "Загрузить в текст картинку." );
        setIconFileName ( "open_img.png" );
    }

    private SimpleParameter getPar ()
    {
        SimpleParameter sp;
        String      PARAM_NAME = "pathDir";

        // Если раньше уже брали изображения - взять директорию, откуда брали
        sp  = (SimpleParameter) getParameter ( PARAM_NAME );
        if ( sp == null )
        {
            sp  = new SimpleParameter ( PARAM_NAME, null ); // дефолтное значение
            sp.setHasEmpty ( false );
            setParameter ( PARAM_NAME, sp );
        }

        return sp;
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        LoadImageDialog dialog;
        File            file, bookFile, bookImgDirFile;
        //Icon            icon;
        ImageIcon            icon;
        int             pos;
        String          fileName, bookFileName, iconDir, bookImgDir, targetFileName, iconFileName;
        StyledDocument  doc;
        TextPanel       textPanel;
        BookContent     bookContent;
        File            pathDir;        // Директория, из которйо загружались картинки.

        Log.l.debug ( "Start" );

        fileName   = getPar().getValue();
        if ( fileName == null )  fileName = Par.USER_HOME_DIR;

        pathDir    = new File ( fileName );

        // открыть диалог по загрузке картинки
        dialog = new LoadImageDialog ( pathDir );
        dialog.showDialog();

        if ( dialog.isOK() )
        {
            // Иконка выбрана

            // - сам файл иконки
            file    = dialog.getIconFile();
            // - директория размещения файла иконки
            pathDir = file.getParentFile();
            // - полный путь до файла иконки
            iconFileName    = file.getAbsolutePath();

            // todo Взять размер иконки

            // ?
            // todo Взять подпись под кратинкой - Мап языков. Т.е. есть параметр - Язык - по умолч - ru
            // - список применяемых языков взять из конфиг-файла. есди нет аткого параметра - ru
            // - если выведем подпись то при парсинге обратно как узнаем что это не текст а подпись?
            //     и как узнаем какой у нее язык?

            // Сохранить новое значение параметра
            getPar().setValue ( pathDir.toString() );

            // Взять текстовую панель
            textPanel   = Par.GM.getFrame().getCurrentTextPanel();
            // Взять книгу
            bookContent = textPanel.getBookNode().getBookContent();

            bookFileName    = bookContent.getFileName();
            // Определить - эта иконка из рабочей директории книги или со стороны.
            iconDir         = pathDir.getAbsolutePath();

            // Если со стороны - скопировать в рабочую диеркторию книги в директорию image.
            if ( ! bookFileName.startsWith ( iconDir ) )
            {
                // скопировать файл
                // Сформировать директорию image книги
                bookImgDirFile = createImgDir(bookFileName, "image");
                // копируем
                targetFileName  = FileTools.copyFile ( file, bookImgDirFile.getAbsolutePath() );

                // todo - лишнее
                // Перечитываем иконку из правильной директории, чтоыб именно этот файловый путь отметился в стиле текста (а не предыдущий)
                if ( targetFileName != null )
                {
                    //icon            = GuiTools.createImageByFile ( targetFileName );
                    iconFileName    = targetFileName;
                }
            }

            // iconFileName - путь до болшой кратинки в диреткории книги

            // - иконка дял размещения в текстовой части

            // создать иконку указанного размера  - icon
            icon            = GuiTools.createSmallImageByFile ( iconFileName, 250 );

            // создать имя новой иконки
            String bigIconName = file.getName();

            // взять расширение (тип) изображения

            // todo сохранить ее в директории иконок изображений  - iconFileName
            // Сформировать директорию image_small книги
            bookImgDirFile = createImgDir(bookFileName, "image_small");
            // Создать имя файла иконки
            String smallFileName = bookImgDirFile + "/" + bigIconName;

            // взять расширение
            int index = smallFileName.lastIndexOf(".");
            String imgType = smallFileName.substring(index + 1);

            /*
            if (1==1)
                throw new WEditException ( null, "smallFileName = ", smallFileName,
                        "\nsource pathDir = ", pathDir, "\nnew iconFileName = ", iconFileName,
                        "\nimgType = ", imgType, "\n" );
            */

            // Сохранить
            FileTools.saveIcon(icon, smallFileName, imgType);

            // перечитать из рабочей директории т.к. icon внутри себя хранит имя файла
            icon            = GuiTools.createImageByFile ( smallFileName );


            Log.l.info (  "iconFileName = " + iconFileName +
                    "\nsource pathDir = " + pathDir + "\nbigIconName = ", bigIconName,
                    "\nsmallFileName = ", smallFileName );



            // Взять текущий Документ
            doc         = textPanel.getDocument();

            // Взять текущую позицию курсора
            pos         = textPanel.getCurrentCursor();

            //throw new WEditException ( null, "icon = ", icon, "\npathDir = ", pathDir, "\npos = ", pos, "\nbookContent = ", bookContent );

            // занести иконку в текст -  здесь передать расположение иконки и прочее
            BookTools.insertImg ( doc, pos, icon, smallFileName );

        }

        //DialogTools.showMessage ( "Внимание", "Функция не реализована." );

        Log.l.debug ( "Finish" );
    }

    private File createImgDir(String bookFileName, String imageDirName) {
        File bookFile        = new File ( bookFileName );
        // - создать имя директории для картинок книг
        String bookImgDir      = bookFile.getParent() + "/" + imageDirName;
        File bookImgDirFile  = new File ( bookImgDir );
        FileTools.createFolder ( bookImgDirFile );

        return bookImgDirFile;
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
        return "Загрузить в текст картинку.";
    }

}
