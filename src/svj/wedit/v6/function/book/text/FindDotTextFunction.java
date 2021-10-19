package svj.wedit.v6.function.book.text;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.service.search.SearchDoubleClickFunction;
import svj.wedit.v6.function.service.search.SearchObj;
import svj.wedit.v6.gui.panel.SimpleEditablePanel;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.WTreeObj;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.book.TextObject;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.obj.function.SimpleFunction;
import svj.wedit.v6.tools.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

/**
 * Искать в тексте точки, после которых идут маленькие буквы (исключая пробелы, запятые, воск и вопрос знаки).
 * <BR/> Игнорируем после точки
 * <BR/> 1) пробелы
 * <BR/> 2) запятые
 * <BR/> 3) восклицательный знак
 * <BR/> 4) вопросительный знак
 * <BR/> 5) игнорируем все
 * <BR/>
 * <BR/> Алгоритм:
 * <BR/> 1) находим точку
 * <BR/> 2) анализируем после нее буквы (и только буквы)
 * <BR/> 3) если первой буквой была маленькая - заносим в результат
 * <BR/>
 * <BR/> Результат заносим в панель внизу - Поиск
 * <BR/>
 * <BR/> Можно правила замены хранить в виде параметров и добавлять новые, удалять старые.
 * <BR/>
 * <BR/> Применяется, толкьо если все тексты закрыты.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 15.10.2021 15:57
 */
public class FindDotTextFunction extends SimpleFunction
{
    /** Счетчик изменений. */
    private int count = 0;
    private final int maxSize = 1000;

    private final String ruLowCh  = "йцукенгшщзхъэждлорпавыфячсмитьбюё";
    private final String ruHighCh = "ЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЮБЬТИМСЧЯЁ";

    private final String enLowCh  = "qwertyuioplkjhgfdsazxcvbnm";
    private final String enHighCh = "QWERTYUIOPASDFGHJKLZXCVBNM";


    public FindDotTextFunction()
    {
        setId ( FunctionId.FIND_LOW_POINT );
        setName ( "Поиск мал букв после точки" );
        setIconFileName ( "search.png" );    //

    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        BookContent             currentBookContent;
        BookNode                bookNode;
        // Реузльтат. Ключ - Полный путь Эпизода. Значение - найденные в данном эпизоде фразы, и места их рапсоложения.
        Map<String,Collection<SearchObj>> searchArray;

        Log.l.debug ( "Start" );

        count = 0;

        // Взять текущую книгу - TreePanel
        //currentBookContentPanel = Par.GM.getFrame().getCurrentBookContentPanel();
        currentBookContent  = Par.GM.getFrame().getCurrentBookContent ();
        bookNode            = currentBookContent.getBookNode();

        // Выяснить, есть ли для данной книги открытые тексты.
        // Проверка открытых эпизодов
        //BookTools.checkAllOpenText();

        // Поиск. LinkedHashMap - чтобы хранил в порядке поступления.
        searchArray = new LinkedHashMap<String,Collection<SearchObj>>();

        //StringBuilder sb = new StringBuilder(128);

        // пробегаем по всем текстам - исключаем заголовки и аннотации.
        processNode ( bookNode, searchArray );

        // Вывести на экран в сервисную часть
        createTabbedPanel ( searchArray );

        DialogTools.showMessage ( getName(), "Успешно завершилась.\nНайдено: "+searchArray.size() );
    }

    private void processNode ( BookNode bookNode, Map<String,Collection<SearchObj>> searchArray )
    {
        BookNode node;
        String   type;
        Collection<WTreeObj> childs;

        // берем тип обьекта.
        type = bookNode.getElementType ();
        if ( (type != null) && type.equalsIgnoreCase ( "hidden" ) )  return;

        find ( bookNode, searchArray );

        // Проверка на вложенные обьекты
        childs = bookNode.getChildrens ();
        for ( WTreeObj wo : childs )
        {
            node = (BookNode) wo;
            processNode ( node, searchArray );
        }
    }

    /**
     * @param bookNode  Обьект книги.
     */
    private void find ( BookNode bookNode, Map<String,Collection<SearchObj>> searchArray )
    {
        String                  str;
        Collection<TextObject>  text;
        int ic;

        // Взять текст
        text = bookNode.getText();
        if ( ( text != null ) && ( !text.isEmpty () ) )
        {
            ic = 0;
            for ( TextObject textObj : text )
            {
                str = textObj.getText();
                if (check ( str )) {
                    //sb.append(str);
                    //sb.append('\n');

                    addSearch ( bookNode, maxSize, searchArray, str, str, ic );
                    ic++;
                }
            }
        }
    }

    private boolean check ( String str )
    {
        boolean findDot = false;
        char[] chars = str.toCharArray();
        for (char ch : chars) {
            if (ch == '.') {
                findDot = true;
            } else {
                if (findDot) {
                    // точка была найдена - анализируем на символ
                    // - русский
                    if (check(ch, ruLowCh)) {
                        // это маленькая русская - конец поисков
                        return true;
                    }
                    if (check(ch, ruHighCh)) {
                        // это большая русская - скидываем режим точки
                        findDot = false;
                        continue;
                    }
                    // - английский
                    if (check(ch, enLowCh)) {
                        // это маленькая англ - конец поисков
                        return true;
                    }
                    if (check(ch, enHighCh)) {
                        // это большая англ - скидываем режим точки
                        findDot = false;
                        //continue;
                    }
                    // все остальное игнорируем
                }
            }
        }

        return false;
    }

    private boolean check(char sym, String symbols) {
        char[] chars = symbols.toCharArray();
        for (char ch : chars) {
            if (sym == ch) return true;
        }
        return false;
    }

    private void addSearch ( BookNode nodeObject, int maxSize, Map<String, Collection<SearchObj>> searchArray,
                             String text, String searchText, int number )
    {
        String          title;
        int             ic;
        SearchObj       searchObj;
        Collection<SearchObj> list;

        Log.l.info ( "--- [N] nodeObject = %s; find = %s", nodeObject, text );
        Log.l.info ( "--- [N] parent node = %s; searchText = %s", nodeObject.getParentNode().getName(), searchText );

        // Только при переходах на эту запись.
        //action = new StyledEditorKit.ForegroundAction ( "Search", Color.BLUE );

        // Проверка на переполнение поиска
        ic = 0;
        for ( Collection<SearchObj> sl : searchArray.values() )  ic = ic + sl.size();
        if ( ic > maxSize )  return; // как-то сообщить об этом - исключение? Запрос - Продолжить?

        title       = nodeObject.getFullPathAsTitles ();
        searchObj   = new SearchObj ( nodeObject, text, searchText, number );
        if ( searchArray.containsKey ( title) )
        {
            list  = searchArray.get ( title );
        }
        else
        {
            list = new LinkedList<SearchObj>();
            searchArray.put ( title, list );
        }
        list.add ( searchObj );
    }

    private void createTabbedPanel ( Map<String, Collection<SearchObj>> searchArray ) throws WEditException
    {
        ImageIcon icon;
        String      imgLocation, iconPath, tabName, tabId;
        TreePanel treePanel;
        JScrollPane scrollPane;
        TreeObj seachResultRoot;
        SimpleEditablePanel result;
        Function doubleClickFunction;

        Log.l.info ( "[N] searchArray = %s", DumpTools.printMap(searchArray, null) );

        // Создать дерево найденных значений
        seachResultRoot = createSearchTree ( searchArray );


        // Создать панель
        result  = new SimpleEditablePanel();
        result.setLayout ( new BorderLayout( 5,5 ) );

        // TreeObj root, T object
        treePanel   = new TreePanel ( seachResultRoot, null );
        treePanel.getTree().setRootVisible ( false );

        //scrollPane  = new JScrollPane ( treePanel );
        //result.add ( scrollPane, BorderLayout.CENTER );

        // без скрола
        result.add ( treePanel, BorderLayout.CENTER );

        // Навесить на дерево свою акцию - если это конечный узел - переход в указанную точку.
        doubleClickFunction = new SearchDoubleClickFunction();
        treePanel.setDoubleClickAction ( doubleClickFunction );

        // todo Создать панель с кнопками управления - стоит справа вертикально. Функции: Удалить табик.
        //buttonsPanel = null;
        //result.add ( buttonsPanel, BorderLayout.WEST );

        // Загрузить иконку
        iconPath    = getIcon ( Par.TOOLBAR_ICON_SIZE );
        imgLocation = FileTools.createFileName ( iconPath, Par.MODULE_HOME );
        Log.l.debug ( "imgLocation = %s", imgLocation );
        icon        = new ImageIcon ( imgLocation );
        Log.l.debug ( "search icon = %s", icon );

        //title       = Msg.getMsg (getNameKey ());
        //altTitle    = Msg.getMsg ( "simpleSearch.altTitle" );
        tabName       = "Поиск";
        tabId         = "SEARCH_PANEL";

        // T paramsPanel, String tabId, String tabName, Icon icon
        Par.GM.getFrame().getTextAdditionalPanel().addPanel ( result, tabId, tabName, icon );

        // Нашу панель сделать текущей.
        Par.GM.getFrame().getTextAdditionalPanel().setSelectedTab ( tabId );

        // Открыть дополнительную панель снизу, под текстом.
        Par.GM.getFrame().showTextAdditionalPanel();
    }

    private TreeObj createSearchTree ( Map<String, Collection<SearchObj>> searchArray )
    {
        TreeObj result, to, ts;
        Collection<SearchObj> soList;

        result = new TreeObj ();

        for ( String path : searchArray.keySet() )
        {
            to = new TreeObj();
            to.setUserObject ( new SearchObj(path) );
            result.add ( to );

            soList = searchArray.get(path);
            for ( SearchObj so : soList )
            {
                ts = new TreeObj();
                ts.setUserObject ( so );
                to.add ( ts );
            }
        }
        return result;
    }


    @Override
    public void rewrite ()     {    }


    public static void main ( String[] args )
    {
        FindDotTextFunction function;
        String[] array;
        StringBuilder sb = new StringBuilder(128);

        array = new String[5];
        array[0] = "  проверка  какого-то  текста. - да-да-да";   // 3
        array[1] = "- проверка какого-то текста. - А да";     // 1
        array[2] = "текст.-да";     // 1
        array[3] = "текст.-Да";     // 1
        array[4] = "текст. Нет";     // 1

        function = new FindDotTextFunction();

        for ( String str : array )
        {
            if (function.check ( str )) {
                sb.append(str);
                sb.append('\n');
            }
        }

        System.out.println ( "Найдено: \n" + sb );

        /*
Найдено:
  проверка  какого-то  текста. - да-да-да
текст.-да

         */
    }

}
