package svj.wedit.v6.gui.tree;


import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.listener.TreeObjSelectionListener;
import svj.wedit.v6.gui.panel.EditablePanel;
import svj.wedit.v6.gui.panel.WPanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.Editable;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.TreeObjType;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.*;
import svj.wedit.v6.util.SortingNode;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.*;

import java.awt.*;
import java.util.*;


/**
 * Универсальная панель по отображению дерева (Проектов, Глав книги).
 * <BR/> - T - хранимый обьект, который отождествляется с отображаемой в дереве информацией (Например: Project - чтобы не терять инфу об авторе и т.д.)
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 29.07.2011 17:47:00
 */
public class TreePanel<T extends Editable>  extends EditablePanel  //implements Comparable<TreePanel>
//public class TreePanel<T extends EditablePanel>  extends EditablePanel  //implements Comparable<TreePanel>
{
    /* Функция, которая вызывается по двойному клику на элементе дерева. На самом деле - тройной клик - для борьбы с глюками. */
    private Function doubleClickAction;


    public enum RepaintTree { NO, NODE, ALL, ALL_WITH_ACTION }

    /* Режим перерисовки дерева по rewrite - все дерево, только текущий узел, ничего не делать... */
    private RepaintTree     repaintTreeMode;

    private JTree           tree;
    private TreeObj         currentObj;

    private WTreeRenderer   renderer;

    private WTreePopupMenu  popupTreeMenu;

    // ??? нужна ли своя модель? (TreeModel) - нужна - для правильной перерисовки дерева при изменениях имен узлов.
    // иначе при изменениях имен дефолтная модель теряет путь до объекта (svj, 2010-07-19)
    //private WTreeModel treeModel = null;
    private DefaultTreeModel treeModel = null;

    /* Листенер селекта на дереве объектов */
    private TreeObjSelectionListener treeListener;

    private boolean allowExpand;

    /* Хранимый обьект, отождествляемый с данной панель - Сборник из книг, Содержимое книги.
     * todo В WPanel это уже реализовано. Только без приведения типа. */
    private T object;

    private JScrollPane         scrollPane;


    public TreePanel ( TreeObj root, T object )  throws WEditException
    {
        currentObj      = root;
        this.object     = object;

        //repaintTreeMode = RepaintTree.NO;
        repaintTreeMode = RepaintTree.ALL;
        // Всплывающее меню - собрано много разных пунктов
        popupTreeMenu   = new WTreePopupMenu();

        allowExpand     = true;

        treeModel       = new DefaultTreeModel (root);
        //treeModel = new DefaultTreeModel(root);

        //treeModel.valueForPathChanged(new TreePath(treeModel.getRoot()), "javax.swing");
        //treeModel.reload();

        doubleClickAction  = null;

        startInit();
    }

    @Override
    public int compareTo ( WPanel o )
    {
        int result;

        result = -1;
        if ( o != null )
        {
            //TreePanel treePanel;
            result = Utils.compareToWithNull ( this.hashCode(), o.hashCode() );
            //Log.l.debug ( "--- compareTo. object = ", o.hashCode (), "; this hashCode = ", this.hashCode(), "; result = ", result );
        }
        return result;
    }

    public boolean equals ( Object obj )
    {
        if ( obj == null )  return false;
        if ( obj instanceof TreePanel )
        {
            TreePanel treePanel = (TreePanel) obj;
            return compareTo ( treePanel ) == 0;
        }
        return false;
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder ( 128 );

        result.append ( "[ TreePanel : object = '" );

        result.append ( getObject() );
        result.append ( "'; " );

        result.append ( super.toString() );
        result.append ( " ]" );

        return result.toString();
    }

    public void setSelectionMode ( int mode )
    {
        tree.getSelectionModel().setSelectionMode ( mode );
    }

    private void startInit () throws WEditException
    {
        TreeMouseAdapter    mouseListener;
        int                 mode;

        Log.l.debug ( "Start" );

        try
        {
            tree = new JTree ( treeModel );

            // Режим выборки элементов в дереве - по умолчанию
            //mode = TreeSelectionModel.SINGLE_TREE_SELECTION;        // только один обьект
            mode = TreeSelectionModel.CONTIGUOUS_TREE_SELECTION;    // несколько обьектов, которые следуют подряд, без разрывов.
            tree.getSelectionModel().setSelectionMode ( mode );

            tree.setFont ( new Font ("SansSerif", 0, 12) );
            //tree.setToolTipText ( "Основное дерево Сборника" );
            tree.setCursor ( new Cursor ( Cursor.HAND_CURSOR ) );

            tree.setMaximumSize ( new Dimension ( 72, 72 ) );
            tree.setMinimumSize ( new Dimension ( 72, 64 ) );
            //
            tree.setShowsRootHandles ( true );

            /*
            tree.setAutoscrolls ( true );
            tree.setExpandsSelectedPaths ( true );
            tree.setRootVisible ( false );          // false - не показывать рута
            tree.setScrollsOnExpand ( false );      // def = true
            */


            tree.addTreeExpansionListener ( new TreeExpansionListener()
            {
                // закрыли узел
		        @Override
                public void treeCollapsed ( TreeExpansionEvent evt )
                {
                    if ( allowExpand )
                    {
                        tree.setSelectionPath ( evt.getPath() );
                    }
                }

                // открыли узел
                @Override
                public void treeExpanded ( TreeExpansionEvent evt )
                {
                    /*
                    Log.l.debug ("treeExpanded: Start. allowExpand = " + allowExpand );
                    if ( allowExpand )
                    {
                        Log.l.debug ("treeExpanded: (before expand) currentObj = " + Par.GM.getCurrentObj() );
                        //tree.setSelectionPath ( evt.getPath() );
                    }
                    */
                }
            } );


            scrollPane = new JScrollPane ();
            scrollPane.setMinimumSize ( new Dimension ( 180, 80 ) );
            scrollPane.setPreferredSize ( new Dimension ( 120, 324 ) );
            scrollPane.setViewportView ( tree );

            //scrollPane.setBackground ( Color.WHITE );
            tree.setBackground ( WCons.TREE_BACKGROUND_COLOR );

            setLayout ( new BorderLayout() );
            add ( scrollPane, BorderLayout.CENTER );


            // Создать собственный отрисовщик для главного дерева
            renderer = new WTreeRenderer();
            tree.setCellRenderer ( renderer );
            //tree.setCellRenderer ( new WTestTreeRenderer() );  // TEST

            tree.setEditable ( false );

            mouseListener = new TreeMouseAdapter ( popupTreeMenu, this );
            tree.addMouseListener ( mouseListener );

            // привязать всплывающее меню
            // - НЕЛЬЗЯ - тк теряем возможность динамических изменений в этом меню (svj, 2010-07-21)
            //tree.setComponentPopupMenu ( popupTreeMenu );

            // Настройка - ToolTip будет появляться когда в фокусе. -- Можно создавать многострочные тул-типы.
            //ToolTipManager.sharedInstance().registerComponent ( tree );

            tree.setVisible ( true );

        //} catch ( WEditException ex )         {
        //    throw ex;
        } catch ( Exception e )         {
            Log.l.error ("init: error", e);
            throw new WEditException ( e, "Системная ошибка инициализации панели дерева объектов :\n",e);
        }

        Log.l.debug ( "init: Finish" );
    }

    public void rewriteEdit ()
    {
        // Перерисовать флаг
        if ( object != null )
            setEdit ( object.isEdit() );
    }

    // Перерисовать дерево - если задано
    @Override
    public void rewrite ()
    {
        TreeObj oldCurrentObj;

        //Log.l.debug ( "%s: Start TreePanel rewrite: repaintTreeMode = %s; currentObj = %s", getName(), repaintTreeMode, currentObj );

        // Нельзя здесь делать getCurrent, т.к. тогда перезатрется currentObj. А некоторые функции просто устанавливают его
        //   в новое значение, чтобы по rewrite оно установилось (особенно при переходах в программе - не по дереву)

        rewriteEdit();

        // считаем что если дерево изменилось, то  currentObj принадлежит старому дереву и в новом надо искать его подобие.
        // Хотя если созадли новый эпизож и выбрали его, то в старом дереве его точно не будет.
        oldCurrentObj  = currentObj;
        //Log.l.debug ( getName(), "", oldCurrentObj );

        try
        {
            // Найти такой же обьект в текущем дереве. Сменить текущий на него.
            if ( oldCurrentObj != null )
                currentObj  = TreeObjTools.getObjectInNodeById ( (TreeObj) treeModel.getRoot(), oldCurrentObj.getId() );
            //Log.l.debug (  "%s: NEW currentObj = %s", getName(), currentObj );

            if ( (oldCurrentObj != null) && (currentObj == null) ) {
                Log.l.error("%s: NOT found object for oldCurrentObj = %s; ", getName(), oldCurrentObj);
                currentObj = oldCurrentObj;
            }

            switch ( repaintTreeMode )
            {
                case NO:
                    /*
                    if ( currentObj != null )
                        selectNode ( currentObj, false );
                    else
                        tree.setSelectionRow ( 0 );
                    */
                    break;

                case NODE:
                    treeModel.nodeChanged ( currentObj );
                    break;

                case ALL:
                    // Дерево было изменено, но выделенный обьект остался тем же - Перерисовать дерево с сохранением ранее выбранного элемента - без акции.
                    //treeModel.nodeStructureChanged ( currentObj );    // НЕТ
                    // expand - не при чем
                    // - выключить все акции на дереве
                    if ( treeListener != null )  treeListener.setDisableAction();
                    allowExpand = false;  // запретить выделять обьект при его закрытии (иначе будет акция)
                    treeModel.reload();
                    //treeModel.nodeStructureChanged ( currentObj );
                    // - выделить обьект на дереве - без акции
                    //Log.l.debug ( "EltexTreePanel.rewrite: Run select currentObj" );
                    selectNode ( currentObj, true );
                    break;

                case ALL_WITH_ACTION:
                    // Дерево было изменено, и выделенный обьект изменился (например, удаление) - Перерисовать дерево с акцией.
                    allowExpand = false;
                    //treeModel.onEltexStructChanged();
                    treeModel.reload();
                    // - выделить обьект на дереве - без акции
                    //int objId  = (currentObj == null) ? -1 : currentObj.getId();
                    selectNode ( currentObj, true );
                    break;
            }

            // Безусловно переписать текущий - для синхронизации значения в дереве и текущего
            //currentObj  = getCurrentObj();
            //Log.l.debug ( "sync currentObj = ", currentObj );

            // контекстное меню - самым последним, т.к. в нем также дергается метод getCurrentObj,
            //   который ведет к явной смене параметра currentObj, что при полном смене дерева выдаст нам null, т.е. затрет исходное.
            popupTreeMenu.rewrite();

        } catch ( Exception e )         {
            Log.l.error ( Convert.concatObj ( getName(), ": error. repaintTreeMode = ", repaintTreeMode ), e);
            //throw new GuiEltexException ( "Системная ошибка перерисовки дерева объектов :\n" + e, e );
        } finally        {
            //Log.l.info ( "%s: Finish. repaint-revalidate tree. currentObj = %s", getName(), currentObj );
            allowExpand         = true;    // включить акцию на раскрывании узла - чтобы он автоматом и выбирался.
            repaintTreeMode     = RepaintTree.NO;
            //
            tree.revalidate();
            tree.repaint();
            scrollPane.revalidate();
            scrollPane.repaint();
            // включить акцию в любом случае
            if ( treeListener != null )  treeListener.setEnableAction();
            //Log.l.debug ( "EltexTreePanel.rewrite: Finish" );
        }
        //Log.l.debug ( "%s: Finish. currentObj = %s", getName(), currentObj );
    }


    public void addTreeSelectionListener ( TreeObjSelectionListener treeSelect )
    {
        treeListener = treeSelect;
        tree.addTreeSelectionListener ( treeListener );
        treeListener.setTreePanel ( this );
    }

    /* метод вызывается в основном в листенере дерева, поэтому необходимо сменить currentObj */
    public TreeObj getCurrentObj ()
    {
        currentObj = ( TreeObj ) tree.getLastSelectedPathComponent();
        return currentObj;
    }

    /**
     * Выдать узлы, отмеченные  в дереве оглавления.
     * <BR> Массив узлов формируется в порядке отмечания по времени обьектов,
     *  а не по их физическому расположению.
     * <BR> Нельзя выделять узлы разных уровней. Точнее, модуль дерева это позволяет,
     *  но в нашем случае работать с такими обьектами нельзя.
     *  Поэтому и ругаемся не в момент отмечания, а здесь при обработке.
     *
     * <BR/> Формируем массив - для удобства последующей сортировки.
     *
     * @return  Массив отмеченных обьектов. Если нет отмеченных - null
     * @throws  WEditException  Отмечены узлы разных уровней
     */
    public TreeObj[] getSelectNodes ()  throws WEditException
    {
        TreeObj[]   result;
        TreePath[]  currentSelections;
        int         i, size, level, ic;
        TreeObj     treeObj;

        result  = null;

        currentSelections = tree.getSelectionPaths();
        Log.l.debug ( "currentSelections = ", WDumpTools.printArray ( currentSelections ) );

        if ( currentSelections != null )
        {
            // Есть отмеченные
            size    = currentSelections.length;
            Log.l.debug ( "select size = ", size );
            // Сформировать массив
            level   = -1;
            result  = new TreeObj[size];
            for ( i=0; i<size; i++ )
            {
                treeObj = ( TreeObj ) ( currentSelections[i].getLastPathComponent() );
                ic      = treeObj.getLevel();

                /*
                logger.debug ( i + ". level = " + ic );
                logger.debug ( i + ". depth = " + result[i].getDepth () );
                logger.debug ( i + ". LeafCount = " + result[i].getLeafCount () );
                logger.debug ( i + ". SiblingCount = " + result[i].getSiblingCount () );
                */
                if ( level < 0 )
                    level   = ic;
                else if ( level != ic )
                    throw new WEditException ( "Отмечены узлы разных уровней." );
                result[i] = treeObj;
            }

            if ( size > 1 )
            {
                //Log.l.debug ( "result size before sorting = ", result.length );
                // Пересортировать обьекты согласно индексам и по возрастанию (true)
                SortingNode.sorting ( result, true );
                //Log.l.debug ( "result size after sorting = ", result.length );
            }
        }

        return result;
    }

    public TreeObj[] getAllSelectNodes ()  throws WEditException
    {
        TreeObj[]   result;
        TreePath[]  currentSelections;
        int         i, size;
        TreeObj     treeObj;

        result  = null;

        currentSelections = tree.getSelectionPaths();
        Log.l.debug ( "currentSelections = ", WDumpTools.printArray ( currentSelections ) );

        if ( currentSelections != null )
        {
            // Есть отмеченные
            size    = currentSelections.length;
            Log.l.debug ( "select size = ", size );
            // Сформировать массив
            result  = new TreeObj[size];
            for ( i=0; i<size; i++ )
            {
                treeObj = ( TreeObj ) ( currentSelections[i].getLastPathComponent() );
                result[i] = treeObj;
            }

            if ( size > 1 )
            {
                //Log.l.debug ( "result size before sorting = ", result.length );
                // Пересортировать обьекты согласно индексам и по возрастанию (true)
                SortingNode.sorting ( result, true );
                //Log.l.debug ( "result size after sorting = ", result.length );
            }
        }

        return result;
    }

    public TreeObj[] getSelectedNodesForCut ( boolean useRoot )  throws WEditException
    {
        TreeObj[]   result;
        TreePath[]  currentSelections;
        int         i, size, level, ic;
        TreeObj     treeObj;
        Collection<TreeObj> list;

        result  = null;

        currentSelections = tree.getSelectionPaths();
        Log.l.debug ( "currentSelections = ", WDumpTools.printArray ( currentSelections ) );

        if ( currentSelections != null )
        {
            // Есть отмеченные
            size    = currentSelections.length;
            Log.l.debug ( "select size = ", size );

            // Вычисляем самый макс уровень среди отмеченных
            ic = Integer.MAX_VALUE;
            for ( i=0; i<size; i++ )
            {
                treeObj = ( TreeObj ) ( currentSelections[i].getLastPathComponent() );
                level   = treeObj.getLevel();
                if ( level < ic )  ic = level;
            }

            if ( ! useRoot )
            {
                // Нельзя отмечать корень для операции вырезания обьектов дерева.
                if ( ic == 0 )   throw new WEditException ("Отмечен корневой элемент!");
            }

            // Вычисляем кол-во отмеченных элементов данного уровня.
            list    = new ArrayList<TreeObj>();
            for ( i=0; i<size; i++ )
            {
                treeObj = ( TreeObj ) ( currentSelections[i].getLastPathComponent() );
                level   = treeObj.getLevel();
                if ( level == ic )
                {
                    list.add ( treeObj );
                }
            }
            if ( list.isEmpty() )
                throw new WEditException ( "Нет отмеченных элементов книги." );

            // Сформировать массив
            i       = 0;
            result  = new TreeObj[list.size()];
            for ( TreeObj to : list )
            {
                result[i]   = to;
                i++;
            }

            if ( result.length > 1 )
            {
                //Log.l.debug ( "result size before sorting = ", result.length );
                // Пересортировать обьекты согласно индексам и по возрастанию (true)
                SortingNode.sorting ( result, true );
                //Log.l.debug ( "result size after sorting = ", result.length );
            }
        }

        return result;
    }

    public void setRootNode ( TreeObj rootNode )
    {
        treeModel.setRoot ( rootNode );
    }

    public void collapsNode ( TreeObj node  )
    {
        Log.l.debug ( "collapsNode: Start. new node = %s", node );

        if ( node == null ) node = getRoot();
        if ( node == null ) return;

        // Установить заданный элемент выбранным
        TreePath path    = new TreePath ( node.getPath() );
        //Log.l.debug ( "selectNode: node path = " + path );

        tree.collapsePath ( path );
        tree.setSelectionPath ( path );

        Log.l.debug ( "collapsNode: Finish" );
    }

    /**
     * Установить обьект текущим.
     * @param node               Обьект
     * @param needExpand         TRUE - необходимо раскрыть этот узел
     */
    public void selectNode ( TreeObj node, boolean needExpand  )
    {
        boolean     leaf;
        TreePath    path;

        Log.l.debug ( "%s: selectNode: Start. new node = %s", getName(), node );
        //Log.l.debug ( "selectNode: obj for select = ", currentObj );

        if ( node == null )
        {
            node = getRoot();
            if ( node == null ) return;
        }

        path    = new TreePath ( node.getPath() );
        Log.l.debug ( "%s: selectNode: path = %s", getName(), path );

        // раскрыть узел
        if ( needExpand )
        {
            // Проверяем сначала - может это не узел - не может быть раскрыт?
            leaf = node.isLeaf();
            Log.l.debug ( "%s: selectNode: node isLeaf = %s", getName(), leaf );
            if ( ! leaf )
            {
                path    = expand ( path, node );
                Log.l.debug ( "%s: selectNode: path after expand = %s", getName(), path );
            }
        }

        // выбрать объект - run action
        tree.setSelectionPath ( path );

        // Безусловно переписать текущий - для синхронизации значения в дереве и текущего
        currentObj = getCurrentObj();
        Log.l.debug ( "%s: sync currentObj = %s", getName(), currentObj );

        // Если текущий отмеченный узел вдруг выходит за рамки скролла (не попадает в видимую область) - сместить скролл, чтобы узел был виден.
        tree.scrollPathToVisible ( path );

        Log.l.debug ( "%s: selectNode: Finish", getName() );
    }

    public void selectNode ( String nodeId  )
    {
        TreeObj  obj;

        //Log.l.info ( "%s: selectNode: Start. new nodeId = %s", getName(), nodeId );

        if ( nodeId == null )  return;

        // Находим обьект в дереве по его ИД.

        obj = TreeObjTools.getObjectInNodeById ( getRoot(), nodeId );

        // Отметить обьект в дереве
        selectNode ( obj, false );

        Log.l.debug ( "%s: selectNode: Finish. currentObj = %s", getName(), currentObj );
    }

    /**
     * Отметить в дереве заданные по ИД обьекты указанным цветом.
     * @param listId       Список ИД.
     * @param selectColor  Цвет выборки обьекта в дереве.
     */
    public void selectNodes(Collection<String> listId, String selectColor) {
        if (listId == null) return;

        TreeObj obj;
        for ( String id : listId ) {
            obj = TreeObjTools.getObjectInNodeById ( getRoot(), id );

            //Log.l.info ( "[STRONG] selectNodes (%s): nodeId = %s; find obj = %s", getName(), id, obj );
            if (obj != null)
                obj.setSubType ( selectColor );
        }
    }


    /* Развернуть узел дерева */
    private TreePath expand ( TreePath path, TreeObj selectedNode )
    {
        boolean  expanded;
        TreeObj  obj;
        TreePath pathResult;

        // Установить заданный элемент выбранным
        Log.l.debug ( "-- expand: node path = %s", path );

        pathResult  = path;

        // Сначала проверяем, может данный узел уже раскрыт?
        expanded = tree.isExpanded ( path );
        Log.l.debug ( "-- expand: check expanded = %s", expanded );
        if ( expanded ) return path;

        // Раскрываем узел
        tree.expandPath ( path );

        // tree.expandPath - может и не отработать, например, при конечном узле. Поэтому анализируем. получилось ли?
        // - Если не получилось  - бегаем по парентам.
        expanded = tree.isExpanded ( path );
        Log.l.debug ( "-- expand: is expanded = %s", expanded );

        if ( ! expanded )
        {
            // узел не раскрылся - бегаем по всем парентам (сверху вниз по именам) и раскрываем их
            // - не раскрылся - значит дерево было обновлено, а этот обьект - из старого дерева. Поэтому compareTo и не понимает его.
            /*
            parentPath = path.getParentPath();
            Log.l.debug ( "-- expand: parentPath = " + parentPath );
            if ( parentPath == null ) return;
            expand ( parentPath );
            */
            // Ищем этот же обьект в нашем новом дереве
            obj = TreeObjTools.getObjectInNodeById ( (TreeObj) treeModel.getRoot(), selectedNode.getId() );
            Log.l.debug ( "-- expand: obj = %s", obj );
            // Если нашли - пытаемся его раскрыть. Если не получилось раскрыть - то уже ничего не делаем (сделали все что смогли)
            if ( obj != null )
            {
                currentObj  = obj;
                pathResult  = new TreePath ( obj.getPath() );
                tree.expandPath ( pathResult ); //expand ( new TreePath (obj.getPath()), obj ); // может быть бесконечный цикл
            }
        }
        // Отмечает узлы правильно но коряво - см выше.
        //tree.scrollPathToVisible ( path );
        return pathResult;
    }

    /**
     * Выбрать объект на дереве, но чтобы не запустились акции изменения дерева.
     * @param node объект дерева
     */
    /*
    public void selectNodeWithoutAction ( TreeObj node  )
    {
        Log.l.debug ( "selectNodeWithoutAction: Start. new node = ", node );

        if ( node == null ) node = (TreeObj) treeModel.getRoot();

        try
        {
            // Установить заданный элемент выбранным
            final TreePath    path    = new TreePath ( node.getPath() );
            //Log.l.debug ( "selectNode: node path = " + path );

            treeListener.setDisableAction();
            tree.setSelectionPath ( path );

        } catch ( Exception e )         {
            Log.l.error ( "selectNodeWithoutAction: err", e );
        } finally          {
            treeListener.setEnableAction();
        }

        Log.l.debug ( "selectNodeWithoutAction: Finish" );
    }
    */

    /**
     * Вставить узел.
     * @param newNode    Вставляемый узел
     * @param parentNode Узел, в который вставляют.
     * @param inumber    Номер индекса в parentNode, куда вставляют.
     */
    public void insertNode ( DefaultMutableTreeNode newNode, DefaultMutableTreeNode parentNode, int inumber )
    {
        //logger.debug ( "inumber = ", inumber );
        if ( inumber < 0 )  return;
        treeModel.insertNodeInto ( newNode, parentNode, inumber );

        TreePath newPath, path;
        path    = new TreePath ( parentNode.getPath() );

        newPath = path.pathByAddingChild(newNode);
        tree.makeVisible ( newPath );
        Rectangle rectangle = tree.getPathBounds ( newPath );
        if ( rectangle != null )  tree.scrollRectToVisible ( rectangle );

        selectNode ( (TreeObj)newNode, false );            // это будет по TreePanel.rewrite - ???
        //setRepaintTreeMode ( RepaintTree.NODE );
        setRepaintTreeMode ( RepaintTree.ALL_WITH_ACTION );
    }

    public void removeNode ( DefaultMutableTreeNode node )
    {
        treeModel.removeNodeFromParent ( node );
    }


    public TreeObj getRoot ()
    {
        return (TreeObj) treeModel.getRoot();
    }

    public void addRenderer ( TreeObjType type, WCellRenderer objectRenderer )
    {
        if ( (objectRenderer != null) && (type != null) )
            renderer.addRenderer ( type, objectRenderer );
    }

    public WTreeRenderer getRenderer ()
    {
        return renderer;
    }

    public void addPopupMenu ( Component component )
    {
        if ( component != null )
            popupTreeMenu.addPopupMenu ( component );
    }

    public void addPopupSeparator ()
    {
       popupTreeMenu.addSeparator();
    }

    public JTree getTree ()
    {
        return tree;
    }

    public void setCurrentObj ( TreeObj currentObj )
    {
        this.currentObj = currentObj;
    }

    public WTreePopupMenu getPopupTreeMenu ()
    {
        return popupTreeMenu;
    }

    public DefaultTreeModel getTreeModel ()
    {
        return treeModel;
    }

    /**
     * Переход на объект. Вызывается при акции от дерева.
     * <BR/> Т.е. сигнализирует о том, что произошла смена обьекта-устройства.
     * <BR/>
     * @param objType    Имя панели. В данном случае это полный тип объекта.
     * @param treeObj      Выбранный в дереве объект
     * @throws WEditException Ошибки
     */
    public void goTo ( TreeObjType objType, TreeObj treeObj )  throws WEditException
    {
        // todo
    }

    public RepaintTree getRepaintTreeMode ()
    {
        return repaintTreeMode;
    }

    public void setRepaintTreeMode ( RepaintTree repaintTreeMode )
    {
        this.repaintTreeMode = repaintTreeMode;
    }

    public T getObject ()
    {
        return object;
    }

    public void setDoubleClickAction ( Function function )
    {
        this.doubleClickAction = function;
    }

    public Function getDoubleClickAction ()
    {
        return doubleClickAction;
    }

}
