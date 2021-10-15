package svj.wedit.v6.tools;


import svj.wedit.v6.Par;
import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.MessageException;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.service.search.SearchObj;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.gui.tabs.TabsPanel;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.*;
import svj.wedit.v6.obj.book.*;
import svj.wedit.v6.obj.book.element.StyleName;
import svj.wedit.v6.obj.book.element.StyleType;
import svj.wedit.v6.obj.book.element.WBookElement;

import javax.swing.*;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;

import java.awt.*;
import java.io.File;
import java.util.*;


/**
 * Утилиты по работе с конкретной книгой и ее элементами.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 02.02.2012 14:12:47
 */
public class BookTools
{
    /** Создать новую книгу и сохранить ее в файле. */
    public static void saveNewBook ( Project project, TreeObj parentSector, BookTitle bookTitle ) throws WEditException
    {
        File fileBook, projectDir;

        String fileName = createFilePath ( project, parentSector, bookTitle );

        Log.l.debug ( "filePath 2 = '%s'", fileName );

        // Создать новый файл в проекте -с общими параметрами - шапкой и т.д.
        fileBook  = new File ( fileName );
        if ( fileBook.exists() )
            throw new WEditException ( null, "Файл для новой книги \n'", fileBook, "' уже существует." );

        bookTitle.getBookContent().setFileName ( fileName );

        FileTools.saveBook ( fileBook, bookTitle );
    }

    public static String createFilePath ( Project project, TreeObj parentSector, BookTitle bookTitle ) throws WEditException
    {
        StringBuilder   filePath;
        File projectDir;

        if ( project == null )
            throw new WEditException ( null, "Сборник не задан." );

        projectDir = project.getProjectDir();
        if ( projectDir == null )
            throw new WEditException ( null, "У Сборника '", project.getName(), "' отсутствует описание на корневой файл." );

        // Создать полное имя файла-директории
        // - Сложить директории по парентам Разделов, исключая корневой (т.к. он уже входит в путь Проекта).
        filePath    = new StringBuilder (128);
        TreeObjTools.createFilePath ( parentSector, filePath );
        Log.l.debug ( "filePath 1 = '%s'", filePath );

        filePath.insert ( 0, projectDir.getAbsolutePath () );

        filePath.append ( '/' );
        filePath.append ( bookTitle.getFileName () );    // короткое имя файла
        //filePath.append ( ".bookTitle" );

        return filePath.toString();
    }

    public static String createFilePath ( Project project, WTreeObj parentSector, BookTitle bookTitle ) throws WEditException
    {
        StringBuilder   filePath;
        File projectDir;

        if ( project == null )
            throw new WEditException ( null, "Сборник не задан." );

        projectDir = project.getProjectDir();
        if ( projectDir == null )
            throw new WEditException ( null, "У Сборника '", project.getName(), "' отсутствует описание на корневой файл." );

        // Создать полное имя файла-директории
        // - Сложить директории по парентам Разделов, исключая корневой (т.к. он уже входит в путь Проекта).
        filePath    = new StringBuilder (128);
        TreeObjTools.createFilePath ( parentSector, filePath );
        Log.l.debug ( "filePath 1 = '%s'", filePath );

        filePath.insert ( 0, projectDir.getAbsolutePath () );

        filePath.append ( '/' );
        filePath.append ( bookTitle.getFileName() );    // короткое имя файла
        //filePath.append ( ".bookTitle" );

        return filePath.toString();
    }

    public static String createFilePath ( Project project, WTreeObj parentSector ) throws WEditException
    {
        StringBuilder   filePath;
        File projectDir;

        if ( project == null )
            throw new WEditException ( null, "Сборник не задан." );

        projectDir = project.getProjectDir();
        if ( projectDir == null )
            throw new WEditException ( null, "У Сборника '", project.getName(), "' отсутствует описание на корневой файл." );

        // Создать полное имя файла-директории
        // - Сложить директории по парентам Разделов, исключая корневой (т.к. он уже входит в путь Проекта).
        filePath    = new StringBuilder (128);
        TreeObjTools.createFilePath ( parentSector, filePath );
        Log.l.debug ( "filePath 1 = '%s'", filePath );

        filePath.insert ( 0, projectDir.getAbsolutePath () );

        //filePath.append ( '/' );
        //filePath.append ( bookTitle.getFileName() );    // короткое имя файла
        //filePath.append ( ".bookTitle" );

        return filePath.toString();
    }

    /**
     * Для указанной книги скинуть все измененные тексты из текстовых панелей в обьекты книги.
     * @param bookContent  Книга.
     */
    public static void text2node ( BookContent bookContent ) throws WEditException
    {
        Collection<TextPanel>   textPanels;
        boolean                 hasEdit = false;

        // textsPanel.addTabsPanel ( bookContent.getId(), tabsPanel );
        // Взять все текстовые панели данной книги
        textPanels = Par.GM.getFrame().getTextPanels ( bookContent.getId() );
        Log.l.info ( "textPanels for bookContent '%s' = %s", bookContent.getId(), textPanels );
        if ( textPanels != null )
        {
            for ( TextPanel textPanel : textPanels )
            {
                Log.l.info ( "-- textPanel = %s", textPanel );
                if ( textPanel.isEdit() )
                {
                    // Скинуть в обьект
                    textPanel.saveTextToNode();
                    hasEdit = true;
                }
            }
        }

        if ( hasEdit )
        {
            // Сообщить книге что было изменение внутри текущего узла - т.е. книгу требуется сохранить на диске.
            bookContent.setEdit ( true );
        }
    }

    /**
     * Получить узел по его полному пути. Полный путь имеет вид - 1,3,2,8 - индексы подузлов от корня и ниже.
     * -- for OLD Wedit.
     * 
     * @param book    Корневой узел
     * @param nodePath
     * @return
     */
    /*
    public static TreeNode getOldNodeByPath ( TreeNode book, String nodePath )
    {
        String[] path;
        int     i;
        Integer ii;
        String  str;
        TreeNode node, node2;

        path    = nodePath.split ( "," );
        node    = book;
        for ( i=0; i<path.length; i++ )
        {
            //logger.debug ( i + "---- path = " + path[i] );
            str = path[i].trim ();
            ii  = Integer.parseInt ( str );
            node2   = node.getChildAt ( ii );
            node    = node2;
        }
        //logger.debug ( "---- Get node = " + node );
        return node;
    }
    */


    /**
     * Ругаться если данный узел - корневой.
     * @param selectNode  Проверяемый узел.
     * @throws WEditException    Заданный узел - корневой.
     */
    public static void errorIfRoot ( DefaultMutableTreeNode selectNode )
            throws WEditException
    {
        if ( selectNode.isRoot() )
        {
            throw new WEditException ( "Выбран корневой элемент!" );
        }
    }


    public static BookContent getCurrentBookContent () throws WEditException
    {
        TreePanel<BookContent>  currentBookContentPanel;
        BookContent             bookContent;

        Log.l.debug ( "Start" );

        // Взять текущую книгу - TreePanel
        currentBookContentPanel = Par.GM.getFrame().getCurrentBookContentPanel();
        if ( currentBookContentPanel == null )
            throw new WEditException ( "Не выбран элемент книги." );

        //selectNode  = currentBookContentPanel.getCurrentObj();
        bookContent = currentBookContentPanel.getObject();

        return bookContent;
    }

    public static BookContent getBookContent ( String id ) throws WEditException
    {
        TreePanel<BookContent>  currentBookContentPanel;
        BookContent             bookContent;

        Log.l.debug ( "Start" );

        // Взять текущую книгу - TreePanel
        currentBookContentPanel = Par.GM.getFrame().getCurrentBookContentPanel();
        if ( currentBookContentPanel == null )
            throw new WEditException ( "Не выбран элемент книги." );

        //selectNode  = currentBookContentPanel.getCurrentObj();
        bookContent = currentBookContentPanel.getObject();

        return bookContent;
    }

    /**
     * Сравнивает уровни искомого обьекта (ourNode) с заданным уровнем.
     * <BR> Если они не равны:
     *  <LI> меняет внутри искомого обьекта его уровень на уровень заданного.
     *  <LI> меняет уровни у всех дочерних элементов </LI>
     *
     * @param ourNode  Анализируемый обьект
     * @param eqLevel  Требуемый уровень
     * @throws WEditException    Нет элементов требуемого уровня
     */
    public static void treatLevel ( DefaultMutableTreeNode ourNode, int eqLevel )
            throws WEditException
    {
        int ourLevel;

        // Взять уровень обьекта
        BookNode bookNode;

        bookNode    = (BookNode) ourNode.getUserObject();
        ourLevel    = bookNode.getLevel();

        //ourLevel    = getElementLevel ( ourNode );
        if ( ourLevel == eqLevel )  return;

        // Не равны. Изменить уровень нашего обьекта на eqLevel
        setNewLevel ( bookNode, eqLevel );
    }

    private static void setNewLevel ( BookNode bookNode, int eqLevel ) throws WEditException
    {
        WBookElement    element;
        BookContent     bookContent;
        BookStructure   bookStructure;

        element         = null;
        bookContent     = getCurrentBookContent();
        if ( bookContent != null )
        {
            bookStructure   = bookContent.getBookStructure();
            if ( bookStructure != null )
            {
                element = bookStructure.getElement ( bookNode );
            }
        }

        if ( element == null )
        {
            // Нет элементов для заданного уровня. Ошибка.
            //throw new WEditException ( "node.level.error", new Integer(eqLevel) );
            throw new WEditException ( null, "Отсутствуют элементы уровня ",eqLevel,".\n Они будут потеряны." );
        }

        // Переписать заголовок для меню  -- НЕ нужно
        //bookNode.setContentName ( str );

        // Изменить уровни дочерних обьектов
        for ( BookNode child : bookNode.getNodes() )
        {
            setNewLevel ( child, eqLevel+1 );
        }
    }

    /**
     * Выдать уровень элемента в иерархии элементов книги.
     * @param selectNode Узел, у которого требуется определить уровень иерархии.
     * @return уровень
     */
    public static int getElementLevel ( DefaultMutableTreeNode selectNode )
    {
        BookNode bookNode;
        bookNode    = (BookNode) selectNode.getUserObject();
        return bookNode.getLevel();
    }


    /**
     * Выдать выделенные узлы.
     *
     * @return
     * @throws WEditException
     */
    public static TreeObj[] getSelectedNodes()
            throws WEditException
    {
        TreePanel<BookContent>  currentBookContentPanel;
        TreeObj[]               result;

        Log.l.debug ( "Start" );

        // Взять текущую книгу - TreePanel
        currentBookContentPanel = Par.GM.getFrame().getCurrentBookContentPanel();
        if ( currentBookContentPanel == null )
            throw new WEditException ( "Книга не открыта." );

        result  = currentBookContentPanel.getSelectNodes ();

        if ( result == null )
            throw new WEditException ( "Нет отмеченных элементов книги." );
        
        return result;
    }

    /* Из всех выделенных берем только одноуровневые - самого высокого уровня. Проверки и ругания - внутри вызовов. */
    public static TreeObj[] getSelectedNodesForCut ( boolean useRoot )
            throws WEditException
    {
        TreePanel<BookContent>  currentBookContentPanel;
        TreeObj[]               result;

        Log.l.debug ( "Start" );

        // Взять текущую книгу - TreePanel
        currentBookContentPanel = Par.GM.getFrame().getCurrentBookContentPanel ();
        if ( currentBookContentPanel == null )
            throw new WEditException ( "Книга не открыта." );

        result  = currentBookContentPanel.getSelectedNodesForCut ( useRoot );

        return result;
    }

    /**
     * Выдать выделенный узел.
     * Попутно проверяется, есть ли такая понель Оглавления и был ли выделен узел.
     *
     * @return     Отмеченный обьект-узел дерева.
     * @throws WEditException  Ошибки
     */
    public static TreeObj getSelectedNode()
            throws WEditException
    {
        TreePanel<BookContent>  currentBookContentPanel;
        TreeObj                 selectNode;

        Log.l.debug ( "Start" );

        // Взять текущую книгу - TreePanel
        currentBookContentPanel = Par.GM.getFrame().getCurrentBookContentPanel();
        if ( currentBookContentPanel == null )
            throw new WEditException ( "Книга не открыта." );

        selectNode  = currentBookContentPanel.getCurrentObj();

        if ( selectNode == null )
            throw new WEditException ( "Нет отмеченных элементов книги." );

        return selectNode;
    }

    /**
     * Выдать родителя заданного обьекта. Если его нет - ругаться.
     * @param selectNode   Заданный обьект
     * @return             Родитель заданного обьекта
     * @throws WEditException    У заданного обьекта отсутствует родитель (т.е. он корневой).
     */
    public static DefaultMutableTreeNode getParentNode ( DefaultMutableTreeNode selectNode )
            throws WEditException
    {
        DefaultMutableTreeNode parentNode;
        parentNode  = ( DefaultMutableTreeNode ) selectNode.getParent ();
        // Если корень - поругаться т.к. перед корнем мы добавить ничего не можем.
        if ( parentNode == null )
            throw new WEditException ( "У заданного обьекта отсутствует родитель (т.е. он корневой)." );
        return parentNode;
    }

    /**
     * Создать копию узла.
     *
     * @param node    Исходный узел.
     * @param copy    TRUE - значит копирвоание обьекта и значит необходимо ему сгенерить уникальный ИД.
     * @return        Новый узел - копия исходного.
     */
    public static DefaultMutableTreeNode createClone ( DefaultMutableTreeNode node, boolean copy )
    {
        DefaultMutableTreeNode  result, child, cloneChild;
        Enumeration             en;
        BookNode                bookNode;


        result = ( DefaultMutableTreeNode ) node.clone ();
        //result  = new DefaultMutableTreeNode ();
        //result.setParent ( null );
        bookNode = ( BookNode ) node.getUserObject ();
        BookNode cloneBook = bookNode.clone();
        if (copy) cloneBook.setId(BookTools.createBookNodeId(cloneBook.getName()));
        result.setUserObject ( cloneBook );

        // скопировать чилдренов
        // ??? - порядок следования сохраняется?
        en = node.children ();
        while ( en.hasMoreElements () )
        {
            //
            child = ( DefaultMutableTreeNode ) en.nextElement ();
            cloneChild = createClone ( child, copy );
            //cloneChild  = (DefaultMutableTreeNode) child.clone ();
            result.add ( cloneChild );
        }

        return result;
    }

    public static TreeObj createClone ( TreeObj node )
    {
        TreeObj         result, child, cloneChild;
        Enumeration     en;
        BookNode        bookNode;

        result   = node.clone();
        //result  = new DefaultMutableTreeNode ();
        //result.setParent ( null );
        bookNode = ( BookNode ) node.getUserObject();
        result.setUserObject ( bookNode.clone () );

        // скопировать чилдренов
        // ??? - порядок следования сохраняется?
        en = node.children();
        while ( en.hasMoreElements() )
        {
            //
            child = ( TreeObj ) en.nextElement();
            cloneChild = createClone ( child );
            //cloneChild  = (DefaultMutableTreeNode) child.clone ();
            result.add ( cloneChild );
        }

        return result;
    }


    /**
     * Удалить элемент книги из gui-дерева и из внутреннего дерева.
     * @param bookContentPanel Панель дерева книги.
     * @param node Удаляемый обьект - элемент книги.
     */
    public static void removeNode ( TreePanel<BookContent> bookContentPanel, DefaultMutableTreeNode node )
    {
        BookNode bookNode, parentNode;

        // удаляем во внутреннем дереве
        bookNode    = (BookNode) node.getUserObject();
        parentNode  = bookNode.getParentNode ();
        parentNode.delete ( bookNode );

        // удаляем в gui-дереве
        //node.removeFromParent();
        bookContentPanel.removeNode ( node );
    }

    public static void removeBook ( TreePanel<Project> bookContentPanel, DefaultMutableTreeNode node )
    {
        WTreeObj book, parent;
        Object obj;

        // удаляем во внутреннем дереве
        book    = (WTreeObj) node.getUserObject();
        parent  = book.getParent();

        if (parent instanceof Section) {
            Section section = (Section) parent;
            section.delete(book);
        }

        // удаляем в gui-дереве
        //node.removeFromParent();
        bookContentPanel.removeNode ( node );
    }

    /**
     * Прoверить, содержится ли bookNode внутри node.
     * Либо части bookNode входят в node.
     * <BR/> Алгоритмы
     * <BR/> 1) Содержатся внутри - если полный путь node совпадает с началом пути bookNode.
     * <BR/> 2) Части входят в node - если полный путь bookNode совпадает с началом пути node.
     * <BR/>
     * @param bookNode  Исходная часть книги (которую хочем открыть).
     * @param node      Открытая часть книги.
     * @throws svj.wedit.v6.exception.WEditException  Содержится.
     */
    public static void checkContainNode ( BookNode bookNode, BookNode node )
            throws WEditException
    {
        String fullPath1, fullPath2;

        if ( (bookNode == null) || ( node == null) ) return;

        fullPath1   = bookNode.getFullPath();
        fullPath2   = node.getFullPath();

        if ( fullPath1.startsWith ( fullPath2 ) )
            throw new WEditException ( true, "Глава '",bookNode.getName(),"', уже открыта в составе '", node.getName(), "'."); // true - чтобы не скидывать в errors.txt

        if ( fullPath2.startsWith ( fullPath1 ) )
            throw new WEditException ( true, "Части главы '",bookNode.getName(),"', уже открыты в составе '", node.getName(), "'.");
    }

    /* Проверяем на вхождение bookNode в node или ее частях. Т.е. смотрим, открыт ли bookNode в каком-нибудь тексте. */
    public static void checkContainInNode ( BookNode bookNode, BookNode node )
            throws WEditException
    {
        String fullPath1, fullPath2;

        if ( (bookNode == null) || ( node == null) ) return;

        fullPath1   = bookNode.getFullPath();
        fullPath2   = node.getFullPath ();

        if ( fullPath1.startsWith ( fullPath2 ) )
            throw new WEditException ( true, "Глава '",bookNode.getName(),"', уже открыта в составе '", node.getName(), "'.");
    }

    /**
     * Проверить, может выбранный узел (сам или в составе вышестоящего узла) уже открыт в текстах.
     * @param bookNode  Анализируемый на открытость узел.
     */
    public static void checkOpenText ( BookNode bookNode )  throws WEditException
    {
        TabsPanel<TextPanel>    tabsPanel;
        BookNode                node;

        // Берем все открытые текстовые панели. Ищем в них наш узел, или его родителей.
        tabsPanel   = Par.GM.getFrame().getTextTabsPanel();
        if ( tabsPanel != null )
        {
            for ( TextPanel tp : tabsPanel.getPanels() )
            {
                node    = tp.getBookNode();
                // - bookNode открыта в составе других частей? Если ДА - исключение.
                BookTools.checkContainInNode ( bookNode, node );
            }
        }
    }

    /**
     * Проверить, есть ли открытые тексты для текущей книги.
     * Если есть - генерим исключение.
     */
    public static void checkAllOpenText ()  throws WEditException
    {
        TabsPanel<TextPanel>    tabsPanel;

        // Берем все открытые текстовые панели. Ищем в них наш узел, или его родителей.
        tabsPanel   = Par.GM.getFrame().getTextTabsPanel();
        if ((tabsPanel != null) && (tabsPanel.isNotEmpty()))
        {
            throw new MessageException( "У книги '", Par.GM.getFrame().getCurrentBookContent().getName()
                    + "' есть открытые тексты.\nНеобходимо их все закрыть."
                    + "\n\n" + tabsPanel );
        }
    }

    public static void checkOpenText ( TreeObj[] treeNodes )  throws WEditException
    {
        BookNode                bookNode;

        for ( TreeObj treeNode : treeNodes )
        {
            bookNode    = (BookNode) treeNode.getUserObject();
            checkOpenText ( bookNode );
        }
    }

    public static void insertImg ( StyledDocument doc, int pos, Icon icon, String fileName ) throws WEditException
    {
        String styleName;

        // Сформировать имя стиля для данной иконки
        styleName   = "image";
        // new
        WEditStyle iconStyle = new WEditStyle ( StyleType.IMG, styleName );
        // - Выставить в нашем стиле выравнивание
        StyleConstants.setAlignment ( iconStyle, StyleConstants.ALIGN_CENTER );
        // - Занести в стиль иконку
        StyleConstants.setIcon ( iconStyle, icon );
        // - дополнительные атрибуты
        iconStyle.addAttribute ( StyleName.STYLE_NAME, styleName );
        iconStyle.addAttribute ( "iconFile", fileName );
        // Начальный отступ абзаца если есть. - FirstLineIndent
        StyleConstants.setFirstLineIndent ( iconStyle, 40 );

        // Вставить картинку в документ - как ранее описанный стиль.
        try
        {
            //doc.insertString ( pos, styleName, doc.getStyle(styleName) );
            doc.insertString ( pos, styleName, iconStyle );
            // Вставить обьект - обьект описания картинки - это уже при обратном парсинге - из документа в обьект
        } catch ( Exception e )             {
            throw new WEditException ( e, "Ошибка вставки иконки '", icon, "' :\n ", e );
        }
    }

    public static String createBookNodeId ( String bookNodeName )
    {
        return createBookNodeId(bookNodeName, 0);
    }

    public static String createBookNodeId ( String bookNodeName, int number )
    {
        String result;
        if ( bookNodeName == null )
            result = "bookNodeName";
        else
        {
            // В имени заменяем все пробелы на подчеркивание - для цельности ИД.
            // А другие HTML-символы? Их тоже надо убрать из ИД - кавычки, треугольные скобки? - ДА. ниже.
            result = bookNodeName.replace ( " ", "_" );
        }
        //result  = Convert.concatObj ( result, '_', System.currentTimeMillis() );    // old

        // Преобразовать дату в удобочитаемый вид, а не непонятный набор циферок.
        // - не применяет наносекунды
        //result  = result + WCons.PP + Convert.getFullDate ( new Date() );

        // применяем наносекунды - т.к. при конвертации книги одинаковые названия эпизодов имеют одинаковое время в миллисек.
        // что приводит к одинаковым ИД
        result  = result + WCons.PP + System.nanoTime();

        // убрать все специфические XML символы.
        result  = Convert.replaceXml ( result );

        if (number > 0) {
            result = result + WCons.PP + number;
        }
        return result;
    }

    /**
     * Перейти на страницу текста, которая указана в обьекте.
     * @param so    Обьект со страницы Поиска
     * @throws WEditException  Проблемы перехода
     */
    public static void goToSearchObj(SearchObj so) throws WEditException
    {
        if ( so.getBookNode() == null )  return;   // выходим т.к. это - узел дерева, а не конечный элемент поиска.

        BookNode    bookNode;
        String      nodeId, text, searchText;
        boolean     hasOpen;
        BookContent bookContent;
        TextPanel   textPanel;
        JTextPane   textPane;
        int         start, end;

        bookNode    = so.getBookNode();
        nodeId      = bookNode.getId();
        Log.l.info ( "[N] nodeId = %s", nodeId );

        bookContent = bookNode.getBookContent();

        // Перейти на обьект bookNode. Если он не открыт - открыть.
        // - Определить - может такой Сборник уже загружен и открыт
        hasOpen = Par.GM.containNode ( nodeId, bookContent );
        Log.l.info ( "[N] hasOpen = %b", hasOpen );
        if ( hasOpen )
        {
            // уже есть открытый - сделать текущим выбранным
            Par.GM.selectNode ( nodeId, bookContent );
        }
        else
        {
            Par.GM.addBookText ( bookNode, bookContent, 0 );
        }

        // Найти в нем требуемый текст, выделить, перевести на него курсор.
        // - Взять текущий текст-panel
        textPanel   = Par.GM.getFrame().getCurrentTextPanel();
        // - Найти в нем искомую фразу. Запомнить позиции начала и конца.
        textPane    = textPanel.getTextPane ();
        text        = textPane.getText();
        //Log.l.debug ( "--- current text : \n%s\n", text );
        searchText  = so.getSearchText();

        start       = search ( text, searchText, so.getNumber() );

        //Log.l.debug ( "--- searchText = '%s'; start = %d", searchText, start );
        if ( start >= 0 )
        {
            end = start + searchText.length();
            // - Переместить курсор на начало выделенного текста. Передвинуть скроллинг - если надо.
            textPanel.setCurrentCursor ( start );
            // - Выделить данную позицию
            textPane.select ( start, end );
            //textPanel.set
            //textPane.repaint();

            // изменяем цвет текста выделения
            textPane.setSelectedTextColor ( Color.WHITE );
            // изменяем цвет фона выделения
            textPane.setSelectionColor ( Color.GREEN );
        }
    }

    /**
     *  Найти текст.
     * @param text         где ищем.
     * @param searchText   что ищем.
     * @param number       номер поиска текста - вдруг еще здесь встречается?
     * @return             Номер найденной позиции текста, во всем тексте. Годится для применения курсора в редакторе текстов.
     */
    public static int search ( String text, String searchText, int number )
    {
        int start;

        if ( number > 0 )
        {
            start = 0;
            // проделать заданное число поисков
            for ( int i = 0; i<=number; i++ )
            {
                start  = text.indexOf ( searchText, start );
                start++;   // сдвигает поиск вперед
            }
            start--;       // возвращаем правильную позицию
        }
        else
        {
            start = text.indexOf ( searchText );    // находит правильно
        }

        return start;
    }


}
