package svj.wedit.v6.function.text;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.params.SimpleParameter;
import svj.wedit.v6.gui.dialog.LoadImageDialog;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.tools.BookTools;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.FileTools;
import svj.wedit.v6.tools.GuiTools;

import javax.swing.*;
import javax.swing.text.StyledDocument;
import java.awt.event.ActionEvent;
import java.io.File;


/**
 * Загрузить в текст картинку.
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
        Icon            icon;
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
        //dialog.init ( pathDir );
        dialog.showDialog();

        if ( dialog.isOK() )
        {
            // Иконка выбрана
            icon    = dialog.getResult();
            file    = dialog.getIconFile();
            pathDir = file.getParentFile();
            iconFileName    = file.getAbsolutePath();

            // Сохранить новое значение параметра
            getPar().setValue ( pathDir.toString() );

            // Взять текстовую панель
            textPanel   = Par.GM.getFrame().getCurrentTextPanel();
            // Взять книгу
            bookContent = textPanel.getBookNode().getBookContent();

            bookFileName    = bookContent.getFileName();
            // Определить - из рабочей директории книги эта иконка или нет.
            iconDir         = pathDir.getAbsolutePath();
            // Если нет - скопировать туда.
            if ( ! bookFileName.startsWith ( iconDir ) )
            {
                // скопирвоать файл
                // Сформировать директорию книги
                bookFile        = new File ( bookFileName );
                // - создать имя директории для картинок книг
                bookImgDir      = Convert.concatObj ( bookFile.getParent(), "/image" );
                bookImgDirFile  = new File ( bookImgDir );
                FileTools.createFolder ( bookImgDirFile );
                // копируем
                targetFileName  = FileTools.copyFile ( file, bookImgDirFile.getAbsolutePath() );
                // Перечитываем иконку из правильной директории, чтоыб именно этот файловый путь отметился в стиле текста (а не предыдущий)
                if ( targetFileName != null )
                {
                    icon            = GuiTools.createImageByFile ( targetFileName );
                    iconFileName    = targetFileName;
                }
            }

            // Взять текущий Документ
            doc         = textPanel.getDocument();

            // Взять текущую позицию курсора
            //pos         = 10;
            pos         = textPanel.getCurrentCursor();


            //throw new WEditException ( null, "icon = ", icon, "\npathDir = ", pathDir, "\npos = ", pos, "\nbookContent = ", bookContent );


            /*
            Style           def, iconStyle;
            //styleName   = file.getName();
            // Сформировать и зарегистрирвоать стиль для данной иконки
            // - Исходный стиль - стиль по-умолчанию. На основе его потом формируются другие стили.
            def         = StyleContext.getDefaultStyleContext().getStyle ( StyleContext.DEFAULT_STYLE );
            // - Добавить стиль в документ - по имени стиля
            iconStyle   = doc.addStyle ( styleName, def);
            // - Выставить в нашем стиле выравнивание
            StyleConstants.setAlignment ( iconStyle, StyleConstants.ALIGN_CENTER );
            // - Занести в стиль иконку
            StyleConstants.setIcon ( iconStyle, icon );
            iconStyle.addAttribute ( "styleName", styleName );
            iconStyle.addAttribute ( "fileName", file.getAbsolutePath() );
            */

            BookTools.insertImg ( doc, pos, icon, iconFileName );
        }

        //DialogTools.showMessage ( "Внимание", "Функция не реализована." );

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
        return "Загрузить в текст картинку.";
    }

}
