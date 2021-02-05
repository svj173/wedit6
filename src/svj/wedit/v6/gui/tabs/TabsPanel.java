package svj.wedit.v6.gui.tabs;


import svj.wedit.v6.exception.MessageException;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.WComponent;
import svj.wedit.v6.gui.panel.EditablePanel;
import svj.wedit.v6.gui.panel.WPanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.DumpTools;
import svj.wedit.v6.tools.GuiTools;
import svj.wedit.v6.tools.Utils;

import javax.swing.*;
import javax.swing.border.MatteBorder;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Панель с табиками
 * <BR/> ИД панели с табиками. Необходим для прописки в CardPanel - для уникальности переходов.
 * <BR/>
 * <BR/> Внимание:
 * <BR/> 1) Не любит когда таб-панели названы одинаково. В этом случае будет отображаться только одна панель, а ее содержимое будет изменяться.
 * Кол-во же панелей будет иметь парвильное значение (т.е. одна панель отображаться не будет).
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 29.07.2011 17:12:09
 */
public class TabsPanel<T extends EditablePanel> extends WPanel  implements WComponent
//public class TabsPanel<T extends JPanel> extends WPanel  implements WComponent
{
    //private WTabbedPane tabbedPane;   // управление табиком - не получилось
    private final JTabbedPane tabbedPane;

    /* Набор панелей-табиков. Ключ
      - имя табика. Необходим для получения доступа к табику из другого табика. -- ???
      - уникальный ИД. Для Сборника-Книги - имя файла. Для Главы - полный путь.
      Пример ключа:
      1) Разное_1391411769437   - табики открытых текстов
      */
    private final ConcurrentHashMap<String, T> panels;


    public TabsPanel ()
    {
        setId ( Long.toString ( System.currentTimeMillis() ) );
        
        //setBorder ( BorderFactory.createEtchedBorder() );
        setBorder ( new MatteBorder (null) );

        // setInsets ( int top, int left, int bottom, int right )
        //setInsets ( 10, 5, 5, 5 );

        setLayout ( new BorderLayout() );

        // создаем упорядоченный список в порядке поступления
        panels      = new ConcurrentHashMap<String,T>();

        //tabbedPane  = new WTabbedPane();
        tabbedPane  = new JTabbedPane();
        tabbedPane.setName("tabbed");
        //Icon icon = null;
        //tabbedPane.setBorder(new MatteBorder (15,15,15,15,icon));
        tabbedPane.getAccessibleContext().setAccessibleName("tabs");
        add ( tabbedPane, BorderLayout.CENTER );
    }

    @Override
    public String toString()
    {
        StringBuilder result;

        result = new StringBuilder ( 1024 );
        result.append ( "[ TabsPanel: name = '" );
        result.append ( getName() );
        result.append ( "'; panels size = " );
        result.append ( panels );
        result.append ( ";;" );
        result.append ( super.toString() );
        //result.append ( "', insets = " );
        //result.append ( insets );
        result.append ( " ]" );

        return result.toString();
    }


    /* Закрыть все открытые табики. */
    public void removeAll ()
    {
        panels.clear();
        tabbedPane.removeAll();
        // сделать чтобы обновилась инфа внутри таб-обьекта, а то табик удалили, а данные о нем остались.
        tabbedPane.revalidate();
    }

    /**
     * Удаляем табик.
     * <br/> Проблема в том что здесь panelId, это tabPanel.getId(). И это значение не всегда является ключом в мапе panels.
     * <br/> Например, для текстов, ключ в мапе, это название текста+лонг-время, а ID - это путь до текста, от корня, в виде 0,2,6...
     * <br/>
     * @param tabPanel   Таб, который необходимо удалить из табс-панели.
     *
     */
    public void removeTab ( T tabPanel ) throws MessageException
    {
        String  removeKey;
        T       panel;
        int     ic;

        if ( tabPanel == null )  throw new MessageException ( "Нельзя удалить NULL таб." );

        //Log.l.debug ( "[removeTab] Start. panelId:", tabPanel.getId(), "; tabPanel = ", tabPanel.getClass().getSimpleName(), "; tabPanel hashCode = ", tabPanel.hashCode() );
        Log.l.debug ( "[removeTab] panels = %s", DumpTools.printCollectionAsClass ( panels.values (), '\n' ) );

        removeKey   = null;
        // Найти в мапе ключ для удаляемого обьекта
        for ( String key : panels.keySet() )
        {
            panel   = panels.get ( key );
            ic      = Utils.compareToWithNull ( tabPanel, panel );
            if ( ic == 0 )
            {
                removeKey   = key;
                break;
            }
        }
        Log.l.info ( "Tab key for gui remove = %s", removeKey );
        if ( removeKey != null )
        {
            panels.remove ( removeKey );
            tabbedPane.remove ( tabPanel );
            // сделать чтобы обновилась инфа внутри таб-обьекта, а то табик удалили, а данные о нем остались.
            tabbedPane.revalidate();
        }
        else
        {
            // Удалить таб почему-то не смогли
            throw new MessageException ( "Не удалось удалить таб '", tabPanel.getName(), "'." );
        }

        Log.l.info("[removeTab] Finish. remove tab");
        //Log.l.debug ( "[removeTab] after removed. panelId:", panelId, "; panels = ", DumpTools.printCollectionAsClass ( panels.values (), '\n' ) );
    }

    /*
    public void removeTab ( String idTabPanel ) throws MessageException
    {
        String  removeKey;
        T       panel;
        int     ic;

        if ( idTabPanel == null )  throw new MessageException ( "Нельзя удалить NULL таб." );

        Log.l.debug ( "[removeTab] Start. panelId:", idTabPanel );
        Log.l.debug ( "[removeTab] panels = ", DumpTools.printCollectionAsClass ( panels.values (), '\n' ) );

        panel       = null;
        removeKey   = null;
        // Найти в мапе ключ для удаляемого обьекта
        for ( String key : panels.keySet() )
        {
            panel   = panels.get ( key );
            ic      = Utils.compareToWithNull ( idTabPanel, panel.getId() );
            if ( ic == 0 )
            {
                removeKey   = key;
                break;
            }
        }
        Log.l.debug ( "---  key:", removeKey );
        if ( removeKey != null )
        {
            panels.remove ( removeKey );
            tabbedPane.remove ( panel );
            // сделать чтобы обновилась инфа внутри таб-обьекта, а то табик удалили, а данные о нем остались.
            tabbedPane.revalidate();
        }
        else
        {
            // Удалить таб почему-то не смогли
            throw new MessageException ( "Не удалось удалить таб '", idTabPanel, "'." );
        }

        //Log.l.debug ( "[removeTab] after removed. panelId:", panelId, "; panels = ", DumpTools.printCollectionAsClass ( panels.values (), '\n' ) );
    }
    */

    /**
     * Установить тип расположения табиков на панели.
     * <br/>Варианты:<ul>
     * <li><code>JTabbedPane.TOP</code>
     * <li><code>JTabbedPane.BOTTOM</code>
     * <li><code>JTabbedPane.LEFT</code>
     * <li><code>JTabbedPane.RIGHT</code>
     * </ul>
     * The default value, if not set, is <code>SwingConstants.TOP</code>.
     *
     * @param type   тип расположения табиков на панели
     */
    public void setTabPlacement ( int type )
    {
        tabbedPane.setTabPlacement ( type );
    }

    /* Вынесен отдельно - чтобы при наполнении табиками листенер не мешался. Вызывается только после окончательного наполнения табами. */
    public void setSelectTabsListener ( TabsChangeListener tabsListener )
    {
        // листенер вызывается и при добавлении нового компонента (таба)
        tabbedPane.addChangeListener ( tabsListener );
    }

    public JTabbedPane getTabbedPane ()
    {
        return tabbedPane;
    }

    private T getFirst ()
    {
        T comp;

        Collection<T> values = panels.values();
        comp = values.iterator().next();

        return comp;
    }

    public void setSelectedFirst () throws WEditException
    {
        T panel;

        Log.l.debug ( "TabsPanel(",getName(),").setSelectedFirst: Start" );

        tabbedPane.setSelectedIndex(0);

        // reload
        panel   = getFirst();
        Log.l.debug ( "TabsPanel(",getName(),").setSelectedFirst: first panel = ", panel );
        if ( panel != null )
        {
            //panel.reload();
            //panel.reloadWithException();
        }
        Log.l.debug ( "TabsPanel(",getName(),").setSelectedFirst: Finish" );
    }

    public T getSelectedComponent ()
    {
        Component comp = tabbedPane.getSelectedComponent();
        /*
        if ( comp instanceof T )
            return (WPanel) comp;
        else
            return null;
        */
        return (T) comp;
    }

    /**
     * Добавить новый табик в пул табиков.
     * <br/>
     * @param paramsPanel    Добавляемая панель. Обязательно должна иметь уникальный ID (setId) и Имя (setName).
     * @param tabId          ИД панели. Для книги - название книги.
     * @param tabName        Название табика.
     */
    public void addPanel ( T paramsPanel, String tabId, String tabName )
    {

        //Log.l.debug ( "(%s) TabsPanel.addPanel: Start. add tab-panel. tabId = '%s'; tabName = '%s'.", getName(), tabId, tabName );
        //Log.l.debug ( "--- TabsPanel(",getName(),").addPanel: panels = ", panels );

        if ( (paramsPanel == null) || (tabId == null) || (tabName == null) )  return;

        // Проверяем - может уже есть такой -- еще одна проверка
        if ( panels.containsKey ( tabId ) )
        {
            //Log.l.debug ( "--- (%s) TabsPanel.addPanel: already has tabId '%s'", getName(), tabId );
            // уже есть перейти на нее
            tabbedPane.setSelectedComponent ( panels.get(tabId) );
            //tabbedPane.get
            //tabbedPane.setSelectedIndex ( ic);
        }
        else
        {
            //Log.l.debug ( "--- (%s) TabsPanel.addPanel: add tabId '%s'", getName(), tabId );
            // добавить новую вкладку и перейти на нее.
            panels.put ( tabId, paramsPanel );
            tabbedPane.addTab ( tabName, paramsPanel );
            // -- создать с иконкой - крестиком
            //Icon tabIcon;
            //tabIcon = GuiTools.createTabIcon ( paramsPanel );
            //tabbedPane.addTab ( tabName, tabIcon, paramsPanel );
        }
    }

    public JLabel addPanel ( T paramsPanel, String tabId, String tabName, Icon icon, Function closeFunction )
    {
        JLabel tabTitleLabel;

        tabTitleLabel = null;

        Log.l.debug ( "TabsPanel(%s).addPanel: Start. add tab-panel с id '%s'; tabName = %s", getName(), tabId, tabName );
        Log.l.debug ( "--- TabsPanel(%s).addPanel: panels = %s", getName(), DumpTools.printMap ( panels, "\n  - ") );

        if ( (paramsPanel == null) || (tabId == null) || (tabName == null) )  return tabTitleLabel;

        // Проверяем - может уже есть такой -- еще одна проверка
        if ( panels.containsKey ( tabId ) )
        {
            Log.l.debug ( "--- TabsPanel(%s).addPanel: already has tabId '%s'",getName(), tabId );
            // уже есть перейти на нее
            tabbedPane.setSelectedComponent ( panels.get ( tabId ) );
            //tabbedPane.get
            //tabbedPane.setSelectedIndex ( ic);
        }
        else
        {
            // добавить новую вкладку и перейти на нее.
            Log.l.debug ( "--- TabsPanel(%s).addPanel: add as new tabId '%s'",getName(), tabId );
            panels.put ( tabId, paramsPanel );
            //tabbedPane.addTab ( tabName, paramsPanel );
            // Обрезаем длинное название
            if ( tabName.length() > 15 )  tabName = tabName.substring ( 0, 15 ) + "...";
            // -- создать с иконкой - крестиком - и добавить
            tabTitleLabel   = GuiTools.addClosableTab ( tabbedPane, paramsPanel, tabName, icon, closeFunction );
        }
        return tabTitleLabel;
    }

    /*
    public int getTabIndex ( Component component ) {
        Component c;
        for(int i = 0; i < getTabbedPane().getTabCount(); i++) {
            c = getTabbedPane().getComponentAt(i);
            if ( (c != null) && (c instanceof EditablePanel) ) {
                return i;
            }
        }
        return -1;
    }
    */
    
    /* Добавить новый табик в пул табиков - с иконкой. */
    public void addPanel ( T paramsPanel, String tabId, String tabName, Icon icon )  throws WEditException
    {
        Log.l.debug ( "TabsPanel(",getName(),").addPanel: add tab-panel '", paramsPanel, "' с id '", tabId, "'" );
        try
        {
            // Ищем, вдруг панель с заданным ИД уже существует?
            if ( panels.containsKey ( tabId ) )
            {
                T panel;
                // Взять панель и занести в нее новые данные. -- НЕТ, сложно слишком.
                panel   = panels.get ( tabId );
                // Удалить старую
                panels.remove ( tabId );
                tabbedPane.remove ( panel );
                // Добавить как новую
                panels.put ( tabId, paramsPanel );
                tabbedPane.addTab ( tabName, icon, paramsPanel );
            }
            else
            {
                // создать новую панель
                panels.put ( tabId, paramsPanel );
                tabbedPane.addTab ( tabName, icon, paramsPanel );
            }
        } catch ( Exception e )         {
            Log.l.error ( "err", e );
            throw new WEditException ( e, "Системная ошибка добавления таб-панели\n '", paramsPanel, "' с именем '", tabName, "' в табс панель :\n", e );
        }
    }

    /* Добавить новый табик в пул табиков. В титле будет отображаться только иконка. */
    public void addPanel ( T paramsPanel, String tabId, Icon tabName )  throws WEditException
    {
        Log.l.debug ( "TabsPanel(",getName(),").addPanel: add tab-panel '", paramsPanel, "' с id '", tabId, "'" );
        try
        {
            panels.put ( tabId, paramsPanel );
            tabbedPane.add ( paramsPanel, tabName );

        } catch ( Exception e )         {
            Log.l.error ( "err", e );
            throw new WEditException ( e, "Системная ошибка добавления таб-панели\n '", paramsPanel, "' с id '", tabId, "' в табс панель :\n ", e );
        }
    }

    public void clear ()
    {
        // скидываем начальный индекс в -1. акции здесь нет т.к. листенер вешается после создания таб-панели и наполнения ее табиками.
        Log.l.debug ( "-- TabsPanel(",getName(),").clear: Start. index = ", tabbedPane.getSelectedIndex() );
        tabbedPane.getModel().clearSelection();
        Log.l.debug ( "-- TabsPanel(",getName(),").clear: Finish. index = ", tabbedPane.getSelectedIndex() );
    }

    public T getPanel ( String tabId )
    {
        return panels.get ( tabId );
    }

    public Collection<T> getPanels ()
    {
        return panels.values ();
    }

    public Map<String,T> getPanelsMap ()
    {
        return panels;
    }


    /**
     * - select - getCurrent.rewrite
     * - edit   - цикл вглубь до самых текстов - для определения наличия изменений
     */
    @Override
    public void rewrite ()
    {
        T comp = getSelectedComponent();
        if ( comp != null && comp instanceof WComponent)
        {
            WComponent wp = (WComponent) comp;
            wp.rewrite();
        }
        /*
        // только для смены языка
        for ( RewritePanel panel : panels.values() )
        {
            panel.rewrite ();
        }
        */
    }

    /* Выдать ИД текущей табс-панели. */
    /*
    public String getCurrentId()
    {
        WPanel panel;
        panel   = getSelectedComponent();
        if ( panel != null )
            return panel.getId();
        else
            return null;
    }
    */

    public boolean contain ( String tabId )
    {
        //Log.l.debug ( "[containNode] tabId:%s" );
        //Log.l.debug ( "[containNode] tabId:%s; panels = %s",tabId, panels );
        return panels.containsKey ( tabId );
    }

    public T setSelectedTab ( String tabId )
    {
        T panel = null;

        Log.l.debug ( "[selectText] tabId:%s",tabId  );
        if ( tabId == null )  return null;

        // Найти индекс таба по ИД
        if ( contain(tabId) )
        {
            panel   = panels.get(tabId);
            Log.l.debug ( "[selectText] contains tabId:%s; text panel = %s", tabId, panel );
            tabbedPane.setSelectedComponent ( panel );
            //panel.requestFocusInWindow();
        }
        return panel;
    }
    
    public void setFocus ( String tabId )
    {
        T panel;

        Log.l.debug ( "[setFocus] text tabId:%s",tabId  );
        if ( tabId == null )  return;

        // Найти индекс таба по ИД
        if ( contain(tabId) )
        {
            panel   = panels.get(tabId);
            Log.l.debug ( "[setFocus] contains tabId:%s; text panel = %s", tabId, panel );
            panel.requestFocusInWindow();
        }
    }

    public void setSelectedPanel ( T panel )
    {
        tabbedPane.setSelectedComponent ( panel );
    }

}
