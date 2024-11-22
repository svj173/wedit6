package svj.wedit.v6.function.book.imports.txt;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.book.imports.IFileExtractor;
import svj.wedit.v6.function.book.imports.doc.target.IBookContentCreator;
import svj.wedit.v6.gui.widget.ComboBoxWidget;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.tools.Convert;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.12.14 21:36
 */
public class TextFileExtractor implements IFileExtractor
{
    private String  codePage;
    private String  textLine1 = null;
    private String  textLine2 = null;
    private IBookContentCreator fileHandler;

    @Override
    public void parse ( File file, IBookContentCreator fileHandler ) throws WEditException
    {
        // Надо как-то получить тип Кодировки. -- processAdditional

        BufferedReader      reader;
        InputStreamReader   iReader;
        String              str;
        boolean             b;

        this.fileHandler = fileHandler;

        try
        {
            iReader     = new InputStreamReader ( new FileInputStream (file), codePage );
            reader      = new BufferedReader ( iReader );

            while ( reader.ready() )
            {
                str = reader.readLine();    // здесь обрезаются переносы строк.
                str = str.trim();           // удаляет переносы строк
                Log.file.debug ( "--- Import from TXT: read line = %s; textLine1 = %s; textLine2 = %s", str, textLine1, textLine2 );
                if ( str.isEmpty() )
                {
                    b = processTitle();
                    if ( b )
                    {
                        // это был заголовок - значит предыдущие две строки и эта были отработаны
                        textLine1 = null;
                        textLine2 = null;
                    }
                    else
                    {
                        //не заголовок - сдвигаем на одну позицию
                        sdvig ( str );
                    }
                }
                else
                {
                    // Это какой-то текст - просто сдвигаем
                    sdvig ( str );
                }
            }

            // Сбрасываем оставшиеся две строки
            close();

        } catch ( Exception e )        {
            Log.file.debug ( Convert.concatObj ( "Ошибка импорта книги '", file, "' из формата ТХТ. " ), e );
            throw new WEditException ( e, "Ошибка импорта книги '",file,"' из формата ТХТ :\n", e );
        }

    }

    /**
     * Пришла пустая строка. Проверяем что перед этим был короткий текст, а выше него - пустая строка.
     * @return   TRUE - был заголовок. Отработали.
     */
    private boolean processTitle ()
    {
        boolean result;

        result = false;

        if ( ( textLine1 == null ) || textLine1.isEmpty() )
        {
            if ( isTitle(textLine2) )
            {
                // Обрабатываем заголовок
                if ( textLine1 != null ) fileHandler.addText ( "\n" );
                fileHandler.title ( textLine2 );
                fileHandler.addText ( "\n" );
                textLine1 = null;
                textLine2 = null;
                result    = true;
            }
        }

        return result;
    }

    private void close ()
    {
        fileHandler.addText ( textLine1+"\n" );
        fileHandler.addText ( textLine2+"\n" );
    }

    private void sdvig ( String str )
    {
        if ( textLine1 != null) fileHandler.addText ( textLine1+"\n" );
        textLine1 = textLine2;
        textLine2 = str;
    }

    @Override
    public void processAdditional ( JComponent additionalGuiComponent )
    {
        ComboBoxWidget<String> codeWidget;

        codeWidget  = (ComboBoxWidget<String>) additionalGuiComponent;
        codePage    = codeWidget.getValue();
    }

    private boolean isTitle ( String text )
    {
        boolean result;

        result = false;
        if ( (text != null) && ( ! text.isEmpty() ) && (text.length() < 60 ) )
        {
            // не содержит подчеркиваний
            if ( ! text.contains ( "-----" ) )
            {
                if ( ! text.contains ( "=====" ) )
                {
                    if ( ! text.contains ( "____" ) )
                    {
                        if ( ! text.contains ( "#####" ) )  result = true;
                    }
                }
            }
        }
        Log.file.debug ( "--- Import TXT: isTitle. result = %b; text = '%s'", result, text );
        return result;
    }

}
