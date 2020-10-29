package svj.wedit.v6.function.book.export;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
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
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.StringTools;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Конвертировать книгу в формат FB2.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 07.11.2019 14:26
 */
public class ConvertToFB2 extends AbstractConvertFunction {

    private int oldLevel = -1;

    /* Текст для красной строки - &nbsp;&nbsp; */
    private final SimpleParameter redLineParam;

    // ----- Для пропуска пустых строк в конце главы. ---------
    /**
     * Уст в TRUE - был выведен титл (в т.ч. и пустой титл).
     * В FALSE - был выведен простой текст (не пустая строка).
     */
    private boolean isTitle = true;

    /** Вывели пустую строку. Устанавливается в TRUE когда реально выведена упстая строка.
     * FALSE -  когда выведена не пустая, либо заголовок. */
    private boolean wasEmpty = false;

    /** Надо будет вывести пустую строку. Уст в TRUE - появилась пустая строка текста.
     * FALSE - вывели любой текст, в т.ч. и пустую строку. */
    private boolean needEmpty = true;


    public ConvertToFB2(FunctionId functionId, String functionName, String iconFile, boolean multiSelect) {
        super ( functionId, functionName, iconFile, multiSelect );

        redLineParam = new SimpleParameter ( RED_LINE_PARAM, "<dd>&nbsp;&nbsp;&nbsp;", true );
        redLineParam.setValue ( "&nbsp;&nbsp;&nbsp;" );
        redLineParam.setRuName ( "Красная строка" );
    }

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
        if ( ! isTitle )
        {
            createEmptyLine();
        }
        isTitle = true;
        //writeStr("<br/><br/>\n");
    }

    @Override
    protected void processTitle(String title, int level, ConvertParameter cp, BookNode nodeObject) {

        wasEmpty = false;

        if (title != null) {
            //if ( title.contains ( WCons.NEW_LINE ) )  title = title.replace ( "\n", "" );

            title = title.trim();
            if (title.endsWith ( "\n" ) ) {
                title = title.substring ( 0, title.length()-2 );
                title = title.trim();
            }
        }

        if ( StringTools.isEmpty ( title ) && isTitle ) {
            // титл пустой. Если уже был перед этим пустой титл - этот игнорировать.
            // следовательно и тег НЕ закрываем - т.к. следующий титл не будет открываться тегом (точно?)
            return;
        }

        isTitle = true;

        // Если предыдущий уровень равен или больше текущего - закрыть секцию (на кол-во = разнице)
        closeSection(level);


        if (level == 0) {
            /*
            // Это заголовок книги - выводим и автора
            writeStr("<section><title>");
            Author author = Par.GM.getAuthor();
            if (author != null) {
                writeStr("<p>");
                writeStr(author.getFullName());
                writeStr("</p>");
            }
            */
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
        //Log.file.debug("closeSection. nodeLevel = %d", level);
        int ic = oldLevel - level;
        writeStr("\n");
        if ( ic > 0 )  {
            //for ( int i=0; i<ic+1; i++) {
            for ( int i=0; i<ic; i++) {   // Игнорируем уровень 0
                writeStr(StringTools.createFirst(level-ic,' '));
                writeStr("</section>\n");
            }
        } else if ( level == 0 ) {
            // закрывашка для уровня книги - для Литрес не используется.
            return;
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
            // пустая строка
            if ( isTitle )
            {
                return;
            }
            else  if ( needEmpty )
            {
                return;
            }
            else  if ( wasEmpty )
            {
                // Две подряд пустых строки выводить нельзя.
                return;
            }
            else
            {
                // Если же после текста будет заголовок - то вообще ни одной пустой нельзя.
                // - запоминаем что надо было вывести пустую строку
                needEmpty = true;
                return;
            }
            //writeStr ( "<br/>" );
            //return;
        }

        if ( textObj instanceof ImgTextObject) {
            // todo - asToDoc
            return;
        }

        if ( textObj instanceof TableTextObject) {
            // todo - asToHTML
            return;
        }

        text    = textObj.getText();

        if (needEmpty &&(!isTitle)) {
            // выводим пустую
            createEmptyLine();
        }

        wasEmpty = false;
        isTitle = false;
        needEmpty = false;

        if ( textObj instanceof SlnTextObject) {
            writeStr ( "<p>" );
            writeStr ( getRedLineValue(cp) );
            writeStr ( text );
            writeStr ( "</p>" );
        } else {
            // простой текст.
            // - Это текст в середине абзаца - ???
            // Но если по каким-то причинам этот текст не попадает внутрь тегов <p>...</p>, то он хоть в файле и
            // будет, но в электронной книге не отобразится. Поэтому все равно добавляем тег P.
            writeStr ( "<p>" );
            writeStr ( text );
            writeStr ( "</p>" );
        }
    }

    private void createEmptyLine ()
    {
        writeStr ( "<empty-line/>" );
        wasEmpty = true;
    }

    @Override
    protected void initConvert(ConvertParameter cp) throws WEditException {

        isTitle = true;
        needEmpty = true;
        wasEmpty = false;

        writeStr("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        writeStr("<FictionBook xmlns:l=\"http://www.w3.org/1999/xlink\" xmlns=\"http://www.gribuser"
                + ".ru/xml/fictionbook/2.0\">\n");
        writeStr("<description>\n");

        writeStr("<title-info>\n");

        // жанр книги - можно перечеслить несколько
        /*
children                Детское
child_tale              Сказки
child_verse             Детские Стихи
child_prose             Детская Проза
child_sf                Детская Фантастика
child_det               Детские Остросюжетные
child_adv               Детские Приключения
         */
        //writeStr("<genre>literature_su_classics</genre><genre>mystery</genre>");
        writeStr("<genre>child_sf</genre>\n");       // antique = Старинная Литература: Прочее

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

        if (getBookContent().getAnnotation() != null ){
            writeStr("<annotation><p>");
            writeStr(getBookContent().getAnnotation());
            writeStr("</p></annotation>\n");
        }

        // date
        if ( getBookContent().getBookAttrs().get("last_change_date") != null ) {
            writeStr("<date>"+ getBookContent().getBookAttrs().get("last_change_date")+"</date>\n");
            //writeStr("<date value='2019-11-08'>2019-11-08</date>\n");
        } else {
            writeStr("<date>2020</date>\n");
        }

        writeStr("<lang>ru</lang>\n");

        writeStr("</title-info>\n");

        writeStr("</description>\n");

        writeStr("<body>\n");

        // Устанавливаем рабочие параметры в исходное состояние.
        // А то если два раза подряд сконвертировать, то во втором файле будет ошибка.
        oldLevel = -1;
    }

    @Override
    protected void finishConvert(ConvertParameter cp, int currentLevel) throws WEditException {

        // В режиме "Конвертация выбранного" этот уровень будет уровнем выбранного элемента, а не 0.
        //closeSection(0);
        closeSection(currentLevel);

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
