package svj.wedit.v6.function.book.export;

import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.book.export.obj.ConvertParameter;
import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.function.params.SimpleParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.Author;
import svj.wedit.v6.obj.book.*;
import svj.wedit.v6.obj.function.AbstractConvertFunction;
import svj.wedit.v6.tools.StringTools;

import java.util.ArrayList;
import java.util.Collection;

/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 07.11.2019 14:26
 */
public class ConvertToFB2 extends AbstractConvertFunction {

    private int oldLevel = -1;

    /* Текст для красной строки - &nbsp;&nbsp; */
    private final SimpleParameter redLineParam;


    public ConvertToFB2() {
        super ( FunctionId.CONVERT_TO_FB2, "Преобразовать книгу в FB2", "to_fb2.png", false );

        redLineParam = new SimpleParameter ( RED_LINE_PARAM, "<dd>&nbsp;&nbsp;&nbsp;", true );
        redLineParam.setValue ( "&nbsp;&nbsp;&nbsp;" );
        redLineParam.setRuName ( "Красная строка" );
    }

    @Override
    protected void processImage(String imgFileName, ConvertParameter cp) {

    }

    @Override
    protected void processEmptyTitle(ConvertParameter cp) {
        writeStr("<br/><br/>\n");
    }

    @Override
    protected void processTitle(String title, int level, ConvertParameter cp, BookNode nodeObject) {

        // Если предыдущий уровень равен или больше текущего - закрыть секцию (на кол-во = разнице)
        closeSection(level);

        if (level == 0) {
            // Это заголовок книги - выводим и автора
            writeStr("<section><title>");
            Author author = Par.GM.getAuthor();
            if (author != null) {
                writeStr("<p>");
                writeStr(author.getFullName());
                writeStr("</p>");
            }
        } else {
            // вывести заголовок
            writeStr(StringTools.createFirst(level, ' '));
            writeStr("<section><title>");
        }
        writeStr("<p>");
        writeStr(title);
        writeStr("</p></title>\n");

        oldLevel = level;
    }

    private void closeSection(int level) {
        int ic = oldLevel - level;
        if ( ic > 0 )  {
            for ( int i=0; i<ic+1; i++) {
                writeStr(StringTools.createFirst(level-ic,' '));
                writeStr("</section>\n");
            }
        } else if ( ic == 0 ) {
            writeStr(StringTools.createFirst(level-ic,' '));
            writeStr("</section>\n");
        }
        // else - Предыдущий уровень глубже текущего (нового). - Ничего не делаем.
    }

    @Override
    protected void processText(TextObject textObj, ConvertParameter cp) {
        //writeStr("<p>");
        String text;

        Log.l.info ( "FB2: processText = %s", textObj );

        if ( textObj instanceof EolTextObject ) {
            writeStr ( "<br/>" );
            return;
        }

        if ( textObj instanceof ImgTextObject) {
            // todo - asToDoc
            return;
        }

        if ( textObj instanceof TableTextObject) {
            // todo - asToHTML
            return;
        }

        text    = textObj.getText ();
        if ( textObj instanceof SlnTextObject) {
            writeStr ( "<p>" );
            writeStr ( getRedLineValue(cp) );
            writeStr ( text );
            writeStr ( "</p>" );
        } else {
            // простой текст.
            // - Это текст в середине абзаца - ???
            writeStr ( text );
        }
    }

    @Override
    protected void initConvert(ConvertParameter cp) throws WEditException {

        writeStr("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        writeStr("<FictionBook xmlns:l=\"http://www.w3.org/1999/xlink\" xmlns=\"http://www.gribuser"
                + ".ru/xml/fictionbook/2.0\">\n");
        writeStr("<description>\n");

        writeStr("<title-info>\n");

        //writeStr("<genre>literature_su_classics</genre><genre>mystery</genre>");

        Author author = Par.GM.getAuthor();
        if (author != null) {
            writeStr("<author>");
            writeStr("<first-name>");
            writeStr(author.getFirstName());
            writeStr("</first-name>");
            writeStr("<last-name>");
            writeStr(author.getLastName());
            writeStr("</last-name>");
            writeStr("</author>\n");
        }

        writeStr("<book-title>");
        writeStr(getBookContent().getName());
        writeStr("</book-title>\n");

        writeStr("<annotation><p>");
        writeStr(getBookContent().getAnnotation());
        writeStr("</p></annotation>\n");
        
        writeStr("<lang>ru</lang>\n");
        //writeStr("<date value='2019-11-08'>2019-11-08</date>\n");

        writeStr("</title-info>\n");

        writeStr("</description>\n");

        writeStr("<body>\n");

    }

    @Override
    protected void finishConvert(ConvertParameter cp) throws WEditException {

        closeSection(0);

        writeStr("</body>\n");

        writeStr("</FictionBook>\n");
    }

    @Override
    protected String getNewLineSymbol() {
        return null;
    }

    @Override
    public void rewrite() {
    }

    /** Взять локальные (индивидуальные) параметры конвертации. */
    @Override
    protected Collection<FunctionParameter> getOtherConvertParams ()
    {
        Collection<FunctionParameter> result;

        result = new ArrayList<FunctionParameter>();
        result.add ( redLineParam );

        return result;
    }

}
