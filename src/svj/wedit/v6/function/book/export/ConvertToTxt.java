package svj.wedit.v6.function.book.export;


import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.book.export.obj.ConvertParameter;
import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.function.params.SimpleParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.book.TextObject;
import svj.wedit.v6.obj.function.AbstractConvertFunction;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Конвертер в формат TXT.
 * <BR/>
 * <BR/> Размер красной строки (в пробелах) вынести в индивидуальные настройки Конвертера ТХТ.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 21.08.2017 13:02
 */
public class ConvertToTxt extends AbstractConvertFunction
{

    //private StringBuilder errMsg = new StringBuilder();

    //private final String RED_ALIGN = ""; // "  ";

    /* Текст для красной строки. */
    private final SimpleParameter redLineParam;



    public ConvertToTxt ()
    {
        super ( FunctionId.CONVERT_TO_TXT, "Преобразовать книгу в TXT", "to_html.png", false );

        // может быть пустым, т.к. валидатор все пробелы воспринимает как пустоту.
        redLineParam = new SimpleParameter ( RED_LINE_PARAM, "   ", true );
        redLineParam.setValue ( "   " );
        redLineParam.setRuName ( "Красная строка" );
    }

    @Override
    public void rewrite ()
    {
    }

    /**
     * Какие-то другие (дополнительные-индивидуальные) параметры конвертации. Например:
     * <br/> 1) Выбор формата - DOC, DOCX
     * <br/>
     * <br/>
     * @return  Список дополнительных параметров, либо Null.
     */
    @Override
    protected Collection<FunctionParameter> getOtherConvertParams ()
    {
        Collection<FunctionParameter> result;

        result = new ArrayList<FunctionParameter> ();
        result.add ( redLineParam );

        return result;
    }

    /**
     * Обработка изображения.
     * @param imgFileName    Имя файла с изображением.
     * @param cp             Параметр (для чего он?)
     */
    @Override
    protected void processImage ( String imgFileName, ConvertParameter cp )
    {
        String str = "["+imgFileName+"]\n";
        writeStr ( str );
    }

    /*
    private void writeTxt ( String str )  //throws WEditException
    {
        try
        {
            fos.write ( str.getBytes ( WCons.CODE_PAGE ) );
        } catch ( Exception e )    {
            Log.l.error ( "TXT: str = "+str, e );
            errMsg.append ( e.getMessage() );
            errMsg.append ( WCons.END_LINE_C );
        }
    }
    */

    @Override
    protected void processEmptyTitle ( ConvertParameter cp )
    {
        writeStr ( WCons.NEW_LINE );
        writeStr ( getRedLineValue(cp) );
    }

    protected String getNewLineSymbol ()
    {
        return WCons.NEW_LINE;
    }


    @Override
    protected void processTitle ( String title, int level, ConvertParameter cp, BookNode bookNode )
    {
        Log.l.info ( "TXT: Start" );

        // Занести титл. На новой строке выводим пробелы для красной строки
        writeStr ( title+"\n\n" );
        writeStr ( getRedLineValue(cp) );

        // Текущий параграф для текстов. Создаем сразу же после титлов - для всех его текстов.
        //currentParagraph = doc.createParagraph ();

        // Занести заголовок согласно стиля.
        //currentParagraph = createParagraph ( bookNode, cp );
    }

    @Override
    protected void processText ( TextObject textObj, ConvertParameter cp )
    {
        String text;

        Log.l.info ( "TXT: processText = %s", textObj );

        text    = textObj.getText ();
        if ( text.contains ( WCons.NEW_LINE ) )
        {
            writeStr ( text );
            // конец строки. на новой строке выводим пробелы для красной строки (по идее смещение брать из стиля)
            writeStr ( getRedLineValue(cp) );
        }
        else
        {
            // - Это текст в середине абзаца - ???
            writeStr ( text );
        }
    }

    @Override
    protected void initConvert ( ConvertParameter cp )  throws WEditException
    {
    }

    @Override
    protected void finishConvert ( ConvertParameter cp ) throws WEditException
    {
        Log.l.info ( "TXT: Start." );
        /*
        try
        {
            fos.close();
        } catch ( Exception e )       {
            Log.l.error ( "error", e );
            throw new WEditException ( e, "Ошибка завершения : ", e.getMessage() );
        }

        if ( errMsg.length () > 0 )
            throw new WEditException ( null, "Ошибка преобразования : ", errMsg );
        */
    }

}
