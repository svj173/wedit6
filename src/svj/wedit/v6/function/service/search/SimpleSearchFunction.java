package svj.wedit.v6.function.service.search;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.params.SimpleParameter;
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
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.DialogTools;
import svj.wedit.v6.tools.FileTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

/**
 * Простой поиск.
 *
 * <br/> В диалоге запрашивает глубину поиска:
 * <br/>  - в данном тексте
  - в данной книге
  - в данном сборнике
  - во всех открытых сборниках

 * <br/> Соответственно, формируется дерево результатов. Например, для поиска в текущем тексте дерева нет - только список найденных.
 * <br/> В тексте подсвечиваются собственным стилем, заданном в функции. Также на панели подсвечиваются в бордюре scrollBar - переходными метками.
 * <br/>
 * <br/> Сформировав результат, лезет в additionalPanel и ищет в ней табик Поиска (по ИД). Если есть - заливает в него результат. Нет - создает новый.
 * <br/> Табик имеет крестик - т.е. его можно закрыть с удалением (чтобы не занимал лишние ресурсы).
 * <br/>
 * <br/> Если иконка поиска расположена над текстовой панелью - значит поиск только в текущем тексте. - ???
 * <br/>
 * Выводит во вкладке сервисной панели результат, не превышающий максимально допустимого (если много - 1000).
 * На панели имеет кнопки - удалить вкладку (закрыть), следующий поиск.
 * Список значений имеет ссылку, по которой открывается соовтествующее окно и курсор переводится на соответствующее место.
 * Возможно - меняется стиль найденного слова.
 *
 * <BR>
 * <BR> Сброс результатов поиска на странице - в стиле метки убрать имя стиля - ?? А если нашли в заголовке?
 * <BR> Наверное - запоминать старый стиль и при замене сначала проверять кусок текста - тот ли по тексту, и его стиль совпадает
 * с новым-помеченным (вдруг было редактирование и текст "уплыл"?
 * <BR>
 * <BR> Места размещения
 * <BR> 1) Над текстом - поиск только в тексте.
 * <BR> 2) В меню - поиск. Пункты
 * <BR> - поиск в тек книге.
 * <BR> - поиск в тек сборнике
 * <BR> - поиск во всех сборниках
 * <BR>
 * <BR> User: Zhiganov
 * <BR> Date: 16.04.2013
 * <BR> Time: 11:25:16
 */
public class SimpleSearchFunction   extends SimpleFunction
{
    /**
     *  Массив для складывания найденных значений.
     * Должен хранить:
     * <LI> 1) полный путь до фрагмента - для формирвоания дерева;
     * <LI> 2) точку вхождения (позиция курсора);
     * <LI> 3) кол-во попаданий в данном фрагменте;
     * <LI> и т.д.
     * Ключ - полный путь до эпизода.
     */
    //private Map<String,Collection<SearchObj>> searchArray;

    /* Ограничение на кол-во поиска. */
    private int defMaxSize = 1000;


    public SimpleSearchFunction ()
    {
        setId ( FunctionId.SEARCH );
        setName ( "Поиск в текущей книге" );
        setMapKey ( "Ctrl/F" );
        setIconFileName ( "search.png" );
    }

    public void handle ( ActionEvent event ) throws WEditException
    {
        String  searchStr;
        BookContent bookContent;
        SimpleParameter maxSizeParam;
        int maxSize;
        Map<String,Collection<SearchObj>> searchArray;

        Log.l.debug ( "Start" );

        // Вывести диалоговое окно для ввода поискового значения
        searchStr   = DialogTools.showInput ( Par.GM.getFrame(), getName(), "Строка для поиска" );
        //logger.debug ( "searchStr = " + searchStr );
        if ( searchStr == null )    return;

        // Взять макс значение
        maxSizeParam = getSimpleParameter ( "max_size", Integer.toString ( defMaxSize ) );
        maxSize      = Convert.getInt ( maxSizeParam.getValue(), defMaxSize );

        // Поиск. LinkedHashMap - чтобы хранил в порядке поступления.
        searchArray = new LinkedHashMap<String,Collection<SearchObj>>();

        // Взять корень книги
        bookContent    = Par.GM.getFrame().getCurrentBookContent();
        processNode ( bookContent.getBookNode(), searchStr, maxSize, searchArray );

        // Вывести на экран в сервисную часть
        createTabbedPanel ( searchArray );

        // Сообщение - сколько всего найдено
        maxSize = 0;
        for ( Collection<SearchObj> list : searchArray.values() )
        {
            maxSize = maxSize + list.size();
        }
        DialogTools.showMessage ( getName(), "Найдено "+maxSize+" строк для '" + searchStr + "'." );
    }

    @Override
    public void rewrite ()     {    }

    private void createTabbedPanel ( Map<String, Collection<SearchObj>> searchArray ) throws WEditException
    {
        ImageIcon   icon;
        String      imgLocation, iconPath, tabName, tabId;
        TreePanel   treePanel;
        JScrollPane scrollPane;
        TreeObj     seachResultRoot;
        SimpleEditablePanel result;
        Function    doubleClickFunction;

        // Создать дерево найденных значений
        seachResultRoot = createSearchTree ( searchArray );


        // Создать панель
        result  = new SimpleEditablePanel();
        result.setLayout ( new BorderLayout ( 5,5 ) );

        // TreeObj root, T object
        treePanel   = new TreePanel ( seachResultRoot, null );
        treePanel.getTree().setRootVisible ( false );
        scrollPane  = new JScrollPane ( treePanel );
        //scrollPane = null;
        result.add ( scrollPane, BorderLayout.CENTER );

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
        Par.GM.getFrame().getAdditionalPanel().addPanel ( result, tabId, tabName, icon );

        // Нашу панель сделать текущей.
        Par.GM.getFrame().getAdditionalPanel().setSelectedTab ( tabId );
        // todo Открыть сервисную часть
        Par.GM.getFrame().showAdditionalPanel();
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

    /**
     *
     * @param nodeObject
     * @param searchText
     * @param maxSize      Счетчик последних найденных.
     * @param searchArray
     */
    private void processNode ( BookNode nodeObject, String searchText, int maxSize, Map<String, Collection<SearchObj>> searchArray )
    {
        BookNode node;
        String str, name, value;
        Collection<TextObject> text;
        Collection<WTreeObj> childs;

        //logger.debug ( "Start. level = " + level );

        try
        {
            // Взять элемент
            if ( nodeObject != null )
            {
                //logger.debug ( "obj class = " + obj.getClass().getName() + ", obj = " + obj );

                // ----- Заголовок -----------------------
                // Взять имя
                name    = nodeObject.getName();
                //logger.debug ( "Title: '" + name + "'" );
                if ( name.contains ( searchText ) )
                {
                    // Нашли в заголовке - добавить в массив
                    addSearch ( nodeObject, maxSize, searchArray, name, searchText, 0 );
                }

                // ------------ аннотация ---------------------
                // Взять атрибуты если они есть
                value    = nodeObject.getAnnotation();
                if ( (value != null) && ( value.contains ( searchText ) ) )
                {
                    // Нашли в annot - добавить в массив
                    addSearch ( nodeObject, maxSize, searchArray, value, searchText, 0 );
                }

                // Взять текст
                text = nodeObject.getText ();
                if ( ( text != null ) && ( !text.isEmpty () ) )
                {
                    int ic = 0;
                    //doc.insertString ( doc.getLength(), WCons.NEW_LINE + WCons.NEW_LINE, styleText );
                    for ( TextObject textObj : text )
                    {
                        str     = textObj.getText ();
                        if ( str.contains ( searchText ) )
                        {
                            // Нашли в заголовке - добавить в массив
                            addSearch ( nodeObject, maxSize, searchArray, str, searchText, ic );
                            ic++;
                        }
                    }
                }
            }

            // Проверка на вложенные обьекты
            childs = nodeObject.getChildrens ();
            for ( WTreeObj wo : childs )
            {
                node = (BookNode) wo;
                processNode ( node, searchText, maxSize, searchArray );
            }


        //} catch ( WEditException we )         {
        //    throw we;
        //} catch ( Exception e )         {
        //    throw new WEditException ( e, "Системная ошибка поиска текста '", searchStr, "' : ", e );
        } catch ( Throwable te )         {
            // Пытаемся поймать переполнение памяти, возникающее время от времени.
            if ( nodeObject == null ) name   = "error";
            else    name = nodeObject.toString();
            Log.l.fatal ( "Java System Error. Node = "+name, te );
        //} finally             {
        //    logger.debug ( "Finish. level = " + level );
        }

    }

    /**
     * Здесь позиции курсора не отмечаем, т.к. поиск идет не в текстовм редакторе а в дереве книги.
     * Поэтому здесь нам еще неизвестно, где в тексте этот текст будет располагаться.
     * @param nodeObject
     * @param maxSize
     * @param searchArray
     * @param text
     * @param searchText
     * @param number       Порядковые номер найденного текста - если в одном тексте было найдено более одного раза.
     */
    private void addSearch ( BookNode nodeObject, int maxSize, Map<String, Collection<SearchObj>> searchArray, String text, String searchText, int number )
    {
        String          title;
        int             ic;
        SearchObj       searchObj;
        Collection<SearchObj> list;

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

}
