package svj.wedit.v6.gui.tree;


import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.tools.GuiTools;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * Менеджер правой кнопкой мыши на дереве.
 * <BR/> Основные задачи:
 * <BR/> - Выяснить, на каком объекте дерева произошло событие
 * <BR/> - Если это объект дерева - сделать его активным (выбранным) -- ??? (потянутся акции. может - делать потом?)
 *      Активным делать если только выбрана акция контекстного меню, которая требует этого (добавить объект, синхронизировать узел и тд)
 * <BR/> - Сформировать контекстное меню для данного объекта
 * <BR/> - Выдать меню
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 04.08.2011 15:39:45
 */
public class TreeMouseAdapter   extends MouseAdapter
{
    /* Меню, высвечиваемое на объекте дерева */
    private WTreePopupMenu  popup;

    /* Меню, высвечиваемое на чистом участке окна дерева. */
    private JPopupMenu      popupDefault;
    private TreePanel       treePanel;

    public TreeMouseAdapter ( WTreePopupMenu popupTreeMenu, TreePanel treePanel )
    {
        this.treePanel  = treePanel;
        popup           = popupTreeMenu;
        popupDefault    = GuiTools.createDefaultTreePopupMenu ( treePanel );
    }

        @Override
    public void mouseClicked ( MouseEvent me )
    {
        Function function;

        //Log.l.debug ( "TreeMouseAdapter.mouseClicked: Start. me = %s", me );
        //Log.l.debug ( "TreeMouseAdapter.mouseClicked: Start. me source = %s", me.getSource() );  // JTree

        // Проверяем состояние модуля
        //if ( Par.STATE == GCons.SystemState.EDIT ) return;
        if ( SwingUtilities.isLeftMouseButton ( me ) )
        {
            // 3 клика - т.к. 2 - плохо - первый клик - на раскрытие узла. Т.е. иногда срабатывает эта функция на простое открытие.
            if ( me.getClickCount() == 3 )
            {
                // двойной клик левой кнопки - акция.
                function    = treePanel.getDoubleClickAction();
                if ( function != null )
                {
                    ActionEvent event = new ActionEvent ( me.getSource(), 12, "mouse" );
                    function.actionPerformed ( event );
                }
            }
        }

        // проверяем, что это правая кнопка. и показываем наше всплывающее меню
        else if ( SwingUtilities.isRightMouseButton(me) )
        {
            Component component;

            //if (me.isPopupTrigger())     popup.show ( me.getComponent(), me.getX(), me.getY() );

            //Logger.getInstance().debug ( "TreeMouseAdapter.mouseClicked: its Right click" );
            //Logger.getInstance().debug ( "TreeMouseAdapter.mouseClicked: me.isPopupTrigger = " + me.isPopupTrigger() );

            component = me.getComponent();
            //Logger.getInstance().debug ( "TreeMouseAdapter.mouseClicked: component = " + component );

            if ( component instanceof JTree )
            {
                JTree tree;
                int x, y;
                TreePath treePath;
                TreeObj obj;

                x = me.getX();
                y = me.getY();
                //Logger.getInstance().debug ( "TreeMouseAdapter.mouseClicked: x/y = " + x + "/" + y );

                tree = (JTree) component;
                //component = tree.getComponentAt ( x, y );  // its always tree
                // получаем объект дерева на котором произошел щелчок. если же рядом с объектом (на одной строчке) то null
                treePath = tree.getPathForLocation ( x, y );
                //Logger.getInstance().debug ( "TreeMouseAdapter.mouseClicked: tree component = " + treePath );
                if ( treePath != null )
                {
                    try
                    {
                        // avp added  - сначала делаем этот объект активным (выбор объекта в дереве)
                        tree.setSelectionPath ( treePath );

                        // получаем объект
                        obj = (TreeObj) treePath.getLastPathComponent();

                        // формируем для него контекстное меню - используем обработчик данного объекта,
                        // - и также добавляем к нему от других обработчиков - вдруг у них тоже что есть для данного типа объекта
                        // - (например: на LTE системные функции - удалить, редактировать; на NODE - функции от LTE - синхронизация узла, или от PON - поиск NTE)
                        //JPopupMenu popup;
                        //popup = new JPopupMenu();
                        //popup.add ( obj.toString() );
                        // Нет - работаем с уже сформированным меню. просто в зависимости от выбранного объекта пункты меню сами себя включают и выключают
                        popup.init ( obj );
                        popup.rewrite();
                    } catch ( Exception e )                    {
                        Log.l.error ( "err", e );
                        return;
                    }
                    //
                    popup.show ( me.getComponent(), x, y );
                }
                else
                {
                    // щелкнули на чистом месте окна дерева - вывести стандартное для дерева меню
                    popupDefault.show ( me.getComponent(), x, y );
                }
            }
        }
        //Logger.getInstance().debug ( "TreeMouseAdapter.mouseClicked: Finish" );
    }

}

