package svj.wedit.v6.function.book.export;

import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.book.export.obj.ConvertParameter;
import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.Author;
import svj.wedit.v6.obj.book.*;
import svj.wedit.v6.obj.function.AbstractConvertFunction;
import svj.wedit.v6.tools.StringTools;

import java.util.*;

/**
 * Конвертировать книгу в формат FB2.
 * <BR/>
 * <BR/> Формат для Литрес:
 * <BR/> 1) Не применять красную строку (абзацы там обворачиваются тегами P)
 * <BR/> 2) После тега BODY не должно быть пустой строки (уточнить)
 * <BR/> 3) в тег DATE заносить только год.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 07.11.2019 14:26
 */
public class ConvertToFB2 extends AbstractConvertFunction {

    /* Начальный уровень секций, с которого реально начинается вывод Заголовков.
     * Используется при закрытии книги. */
    private int startLevel = 1000;

    // Уровень последней закрытой секции.
    private int oldLevel = -1;

    // Текст для красной строки - здесь не нужен

    // ----- Параметры - Для пропуска пустых строк в конце главы. ---------

    /**
     * Уст в TRUE - был выведен титл (в т.ч. и пустой титл).
     * В FALSE - был выведен простой текст (не пустая строка).
     */
    private boolean isTitle = true;

    /** Вывели пустую строку. Устанавливается в TRUE когда реально выведена пустая строка.
     * FALSE -  когда выведена не пустая, либо заголовок. */
    private boolean wasEmpty = false;

    /** Флаг - Надо вывести пустую строку или не надо. Установили в TRUE - появилась пустая строка текста.
     * FALSE - вывели любой текст, в т.ч. и пустую строку. */
    private boolean needEmpty = true;

    private final static String END_LINE = "\r\n";

    //private TextObjectType oldTextType = TextObjectType.EOL;


    public ConvertToFB2(FunctionId functionId, String functionName, String iconFile, boolean multiSelect) {
        super ( functionId, functionName, iconFile, multiSelect );
    }

    public ConvertToFB2() {
        super ( FunctionId.CONVERT_TO_FB2, "Преобразовать книгу в FB2", "to_fb2.png", false );
    }

    @Override
    protected void processImage(String imgFileName, ConvertParameter cp) {
        // todo
    }

    @Override
    protected void processEmptyTitle(ConvertParameter cp) {
        if ( ! isTitle )
        {
            createEmptyLine();
        }
        isTitle = true;
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
        // - перед тем как вывести текущий титл
        //Log.file.info("current level = %d; oldLevel = %d; title = '%s'", level, oldLevel, title);
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
            // вывести Начало секции и ее заголовок
            writeStr(StringTools.createFirst(level, ' '));
            writeStr("<section>" + END_LINE + "<title>" + END_LINE);
            writeStr("<p>");
            writeStr(title);
            writeStr("</p>" + END_LINE + "</title>" + END_LINE);
            // Вывести эпиграф Эпизода - если он есть
            //writeEpigraph(nodeObject, level);
        }
        //writeStr("<p>");
        //writeStr(title);
        //writeStr("</p></title>\n");

        oldLevel = level;

        // вычисляем начальный уровень секций, с которого реально начинается вывод Заголовков.
        if (startLevel > oldLevel)  startLevel = oldLevel;
    }

    private void writeEpigraph(int level) {
        // Это заголовок книги - выводим Эпиграф если есть
        // todo Автор в файле есть но в гуи-книге почему-то не отображается. Разобраться. (test_002.fb2)

        if (bookContent.getEpigraphText() != null) {
            String sp = StringTools.createFirst(level, ' ');
            writeStr(sp);
            writeStr("<epigraph>" + END_LINE);
            for (String str: bookContent.getEpigraphText())  {
                if (StringTools.isEmpty(str)) {
                    writeStr(sp);
                    writeStr("  <empty-line/>" + END_LINE);
                }
                else {
                    writeStr(sp);
                    writeStr("  <p>");
                    writeStr(str);
                    writeStr("</p>" + END_LINE);
                }
            }

            if (! StringTools.isEmpty(bookContent.getEpigraphAuthor())) {
                writeStr(sp);
                writeStr("  <text-author>");
                writeStr(bookContent.getEpigraphAuthor());
                writeStr("</text-author>" + END_LINE);
            }
            writeStr(sp);
            writeStr("</epigraph>" + END_LINE);
        }
    }

    /**
     * Закрыть секцию (на кол-во = разнице) - перед тем как вывести текущий титл.
     *
     * Здесь учитываем уровень пердыдыущей закрытой секции, и если он был гораздо меньше, то применяем недостающие
     * закрывающие теги, подтягивающие структуру книги до последней секции.
     *
     * @param level  Уровень текущего (нового) титла, который только собираемся вывести в документ.
     */
    private void closeSection(int level) {
        //Log.file.info("[S] closeSection. nodeLevel = %d; oldLevel = %d", level, oldLevel);

        // закрывашка для уровня книги - для Литрес не используется.
        // - НЕТ. используем. В ситуации когда три части с текстом без эпизодов - конец не обрабатывается.
        // но если стркоу ниже убрать - то тоже все ломается.
        // todo Нужен другой алгоритм closeSection. Например сразу формирвоать и сохранять в стеке тег закрытия и его
        // уровень. Если эпизоды будут углубляться вглубь, то и стек будет наполняться. А по закрытию эпизода
        // из стека все извчлечется до текушего уровня.
        if ( level == 0 ) return;

        /*
        Варианты
        1) for ( int i=0; i<ic+1; i++) {
        - Не срабатывает когда не выводим титлы Книги и Части.
        В этом случае в самом конце работы появляется лишний /section
        - Работает для структур Книга-Глава, когда не выводится только титл Книги

        2) for ( int i=0; i<ic; i++) {


        А если у Глава есть подглавы, но они также не выводятся.
        И тогда в самом конце у нас oldLevel и currentLevel равны 5, а азкрыть надо до уровня 2 (Глава)
        НЕТ. oldLevel отмечается толкьо для тех титлов, которые выводятся.
         */

        int ic = oldLevel - level;
        //Log.file.info("[S] closeSection. ic = %d", ic);
    //    writeStr(END_LINE);
        if ( ic > 0 )  {
            //Log.file.info("[S] closeSection. 1");
            // т.е. закрываем эпизод, более верхний чем предыдущий
            // (например: Часть, а до этого была ПодГлава. Значит надо закрыть Подглаву, Главу, Часть-предыдущую)
            for ( int i=0; i<ic+1; i++) {
                //for ( int i=0; i<ic; i++) {   // Игнорируем уровень 0 - Для структуры: Часть, Глава
                //Log.file.info("[S] -- closeSection. i = %d", i);
                writeStr(StringTools.createFirst(level-ic,' '));
                writeStr("</section>" + END_LINE);
            }
        } else if ( ic == 0 ) {
            //Log.file.info("[S] closeSection. 1");
            // Новый эпизод того же уровня что и предыдущий.
            writeStr(StringTools.createFirst(level-ic,' '));
            writeStr("</section>" + END_LINE);
        }
        // else - Предыдущий уровень выше текущего (нового). - Ничего не делаем. Т.к. углубляемся вниз.
    }

    @Override
    protected void processText(TextObject textObj, ConvertParameter cp) {
        //writeStr("<p>");
        String text;

        Log.l.debug ( "FB2: processText = %s", textObj );

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
            // выводим пустую строку
            createEmptyLine();
        }

        wasEmpty = false;
        isTitle = false;
        needEmpty = false;

        /*
Условия для борьбы с разрывами строк:
1) флаг - предыдущий текст -sln, str
2) str
- если предыдущий - sln - выводим с новой строки, но перенос строк в конце не ставим   - т.е. только открытый тег P
- если предыдущий - str - выводим текст без переносов строк - в начале и в конце
3) sln
- если предыдущий - sln - обычная работа
- если предыдущий - str - добавляем текст без начального переноса строк, но в конце ставим перенос

Проблема
1) Ставя стартовый тег <p> мы можем забыть поставить закрывашку (например, дальше пойдут типы EOL, IMG и пр)

Может, проще прогонять весь текст и анализировать подобные ситуации (str

Вторйо вариант - может лучше разобраться, почему конвертер из BookNode так работает.
Почему он полсе SLN воспринимает абзац как две разных сущности?

         */
/*
        String startTag, endTag;
        TextObjectType currentType = textObj.getType();

        switch (currentType)
        {
            case STR:
                switch (oldTextType) {
                    case SLN:
                        startTag = "<p>";
                        endTag = "";
                        break;
                    case STR:
                        startTag = "";
                        endTag = "";
                        break;
                }
                break;

            case SLN:
                switch (oldTextType) {
                    case SLN:
                        startTag = "<p>";
                        endTag = "";
                        break;
                    case STR:
                        startTag = "";
                        endTag = "";
                        break;
                }
                break;

            default:

        }
*/
        /*
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
        */

        // Не исп красную строку в любом случае - чтобы случайно не потерять данные, либо не поломать структуру файла.
        //if (text.contains("\n"))  throw new RuntimeException("Text: '" + text + "' contains EOL");
        text = text.replace("\n", "");
        text = text.replace("\r", "");
        if (! StringTools.isEmpty(text)) {
            writeStr("<p>");
            writeStr(text);
            writeStr("</p>" + END_LINE);
        }

    }

    private void createEmptyLine ()
    {
        writeStr ( "<empty-line/>" + END_LINE );
        wasEmpty = true;
    }

    @Override
    protected void initConvert(ConvertParameter cp) throws WEditException {

        isTitle = true;
        needEmpty = true;
        wasEmpty = false;

        writeStr("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + END_LINE);
        writeStr("<FictionBook xmlns:l=\"http://www.w3.org/1999/xlink\" xmlns=\"http://www.gribuser"
                + ".ru/xml/fictionbook/2.0\">" + END_LINE);
        writeStr("<description>" + END_LINE);

        writeStr("<title-info>" + END_LINE);

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
        writeStr("<genre>child_sf</genre>" + END_LINE);       // antique = Старинная Литература: Прочее

        Author author = Par.GM.getAuthor();
        if (author != null) {
            writeStr("<author>" + END_LINE);
            writeStr("<first-name>");
            writeStr(author.getFirstName());
            writeStr("</first-name>" + END_LINE);
            writeStr("<last-name>");
            writeStr(author.getLastName());
            writeStr("</last-name>" + END_LINE);
            writeStr("</author>" + END_LINE);
        }

        writeStr("<book-title>");
        writeStr(getBookContent().getName());
        writeStr("</book-title>"  + END_LINE);

        if (getBookContent().getAnnotation() != null ){
            writeStr("<annotation>" + END_LINE + "<p>");
            writeStr(getBookContent().getAnnotation());
            writeStr("</p>" + END_LINE + "</annotation>" + END_LINE);
        }

        // date - дата написания - только год
        String dateStr = getBookContent().getBookAttrs().get("last_change_date");
        if ( dateStr != null ) {
            writeStr("<date>"+ getYear(dateStr) +"</date>" + END_LINE);
        } else {
            writeStr("<date>2020</date>" + END_LINE);
        }

        writeStr("<lang>ru</lang>" + END_LINE);

        writeStr("</title-info>" + END_LINE);

        writeStr("</description>" + END_LINE);

        writeStr("<body>" + END_LINE);
        //writeStr("<body>");

        // Вывести эпиграф книги - если он есть
        writeEpigraph(0);

        // Устанавливаем рабочие параметры в исходное состояние.
        // А то если два раза подряд сконвертировать, то во втором файле будет ошибка.
        oldLevel = -1;
        startLevel = 1000;
    }

    /**
     * Выделить год.
     * @param dateStr Строковое представление даты, в виде 2019-11-28
     * @return  Год в виде четырех цифр. Например, 2019.
     */
    private String getYear (String dateStr) {
        if (dateStr.length() > 4)
            return dateStr.substring(0,4);
        else
            return "2022";
    }

    /**
     * Конец конвертации. Закрыть все Элементы.
     *
     * @param cp               Не исп.
     * @param currentLevel     Уровень закрываемого элемента. Вычисляется как самый максимальный
     *                         из выбранных для конвертации Элементов.
     *                         Не учитывает, что верхние Элементы могут не выводить свои титлы, что является ошибкой.
     *                         В режиме Книги это 0, а в режиме "Конвертация
     *                         выбранного" этот уровень будет уровнем выбранного элемента, а не 0.
     * @throws WEditException  Проблемы вывода в документ.
     */
    @Override
    protected void finishConvert(ConvertParameter cp, int currentLevel) throws WEditException {

        // currentLevel = 0
        //Log.file.info("finishConvert: currentLevel = %d", currentLevel);

        /*

        Анализировать oldLevel - уровень самого последнего закрытого элемента

         */

        // В режиме "Конвертация выбранного" этот уровень будет уровнем выбранного элемента, а не 0.

        // +1 - иначе будет закрыт и уровень книги = 0. А мы его в титле не выводим - игнорируем,
        // поэтому и не закрываем.
        //if (currentLevel == 0)  currentLevel = 1;

        //closeSection(currentLevel);
        closeSection(startLevel);

        writeStr("</body>" + END_LINE);

        writeStr("</FictionBook>" + END_LINE);
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
        /*
        Collection<FunctionParameter> result;

        result = new ArrayList<FunctionParameter>();
        result.add ( redLineParam );

        return result;
        */
        return null;
    }

}
