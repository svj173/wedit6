package svj.wedit.v6.gui.panel.card;


import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.panel.EditablePanel;
import svj.wedit.v6.gui.panel.EmptyEditablePanel;
import svj.wedit.v6.gui.panel.WPanel;
import svj.wedit.v6.gui.tabs.TabsPanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.tools.Utils;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Содержит набор табс-панелей.
 * <BR/> Табики книг и табики текста глава.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 22.09.2011 15:58:29
 */
//public class CardPanel<T extends TabsPanel<WPanel>>  extends WPanel
public class CardPanel<T extends TabsPanel>  extends WPanel
//public class CardPanel<T extends TabsPanel<EditablePanel>>  extends WPanel
//public class CardPanel<TabsPanel<T extends EditablePanel>>  extends WPanel
//public class CardPanel<T extends EditablePanel>  extends WPanel
{
    public static final String EMPTY    = "empty";

    private final Map<String, T> panels;
    //private Map<String, <TabsPanel<T>>> panels;

    private String currentPanelId;
    private String previosPanelId;
    //private CardPanelCreator<T> creator;

    private CardLayout cardLayout;

    private CardPanel parentPanel;


    public CardPanel ( CardLayout cardLayout )
    {
        //this.creator    = creator;
        setCardLayout ( cardLayout );
        panels          = new HashMap<String,T>();
        startInit();
        addEmpty();
    }

    public CardPanel (  )
    {
        CardLayout cardLayout;

        cardLayout  = new CardLayout();
        panels      = new HashMap<String,T>();
        setCardLayout ( cardLayout );
        startInit();
        addEmpty();
    }

    public String toString()
    {
        StringBuilder result = new StringBuilder(512);

        result.append ( "[ CardPanel: currentPanelId = '" );
        result.append ( currentPanelId );
        result.append ( "'; panels = <<" );
        result.append ( panels );
        result.append ( " >> ]" );

        return result.toString();
    }

    /* Инициализация объекта. */
    private void startInit ()
    {
        //Logger.getInstance().debug("WorkPanel.init: Start");
        
        currentPanelId  = null;
        previosPanelId  = "";
        // top, left, bottom, right
        setInsets ( 10,5,10,15 );
        setMaximumSize ( new Dimension ( WCons.WORK_PANEL_MIN_WIDTH, 300 ) );

        //Logger.getInstance().debug("WorkPanel.init: Finish");
    }

    /* Обновить экран, НЕ учитывая смену языка.
      *   bookCard.rewrite  (textCard.rewrite)
  - id  = parent.getCurrentId
  - if ( id = nowId )
     - ничего не делать
    else
    {
      if ( есть такая Ид в списке )
        showCard ( id )
        взять новую текущую TabsPanel и выбрать в ней первый табик - ?
      else
        выбрать emptyCard
    }

       *  */
    public void rewrite ()
    {
        String      nowId, id;
        T           tabsPanel;
        int         ic;
        CardPanel   parentPanel;
        boolean     b;
        
        Log.l.debug ( "(%s) Start",getName() );

        //id  = null;
        // Взять настоящую ИД табс-панели.
        parentPanel = getParentPanel();
        //Log.l.debug ( "(%s) parentPanel = %s",getName(), parentPanel );
        if ( parentPanel == null )
            return;   // нет родителя - не к чему цепляться
        else
            id  = parentPanel.getCurrentId();
        Log.l.debug ( "(%s) parent current id = '%s'",getName(), id );

        if ( id == null )
        {
            // У родителя ничего не установлено - скидываемся в Пусто.
            setCurrent ( EMPTY );
            showPanel ( EMPTY );
        }
        else
        {
            // Взять ИД табс-панели, что сейчас текущая для CardPanel - для последующего сравнения
            tabsPanel   = getCurrent();
            if ( tabsPanel != null )
                nowId  = tabsPanel.getId();
            else
                nowId  = null;

            // сравниваем ИД
            ic  = Utils.compareToWithNull ( nowId, id );
            Log.l.debug ( "(%s) compare id:%s and nowId:%s = %d",getName(), id, nowId, ic );

            if ( ic == 0 )
            {
                // текущий остался - ничего не делаем
            }
            else
            {
                // смотрим - есть ли в нашем списке табсПанель с таким ИД
                b = containsTabsPanel ( id );
                Log.l.debug ( "(%s) contains id = %b",getName(), b );
                if ( b )
                {
                    Log.l.debug ( "(%s) set new id = %s",getName(), id );
                    // есть такая у нас - делаем текущей
                    setCurrent ( id );
                    showPanel ( id );   // run rewriteAll
                }
                else
                {
                    Log.l.debug ( "(%s) set empty",getName() );
                    // У нас ничего нет для этой панели - скидываемся в Пусто.
                    setCurrent ( EMPTY );
                    showPanel ( EMPTY );
                }
            }
        }

        Log.l.debug ( "(%s) RUN repaint",getName() );
        repaint();

        Log.l.debug ( "(%s) Finish",getName() );
    }

    /* Обновить все панели, учитывая смену языка.  */
    public void rewriteAll () //throws WEditException
    {
        Collection<T> list = panels.values();
        for ( T  panel : list )
        {
            panel.rewrite ();
        }
    }

    /**
     * Взять текущую панель (TabsPanel - панель со вкладками)
     * @return  Текущая панель либо NULL
     */
    public T getCurrent ()
    {
        T  panel   = null;
        if ( currentPanelId != null )
            panel   = panels.get ( currentPanelId );

        //if ( panel == null )   panel   = panels.get ( "empty" );

        return panel;
    }

    public T get ( String panelId )
    {
        return panels.get ( panelId );
    }

    public void delete ( String panelId )
    {
        panels.remove ( panelId );
    }

    /**
     * Взять текущую панель-вкладку.
     * @return  Текущая панель либо NULL
     */
    public WPanel getCurrentTabPanel ()
    {
        T        tabsPanel;
        WPanel   result;

        tabsPanel   = getCurrent();
        if ( tabsPanel == null )
            result = null;
        else
            result = tabsPanel.getSelectedComponent();

        return result;
    }

    /* Выдать ИД текущей таб-панели. */
    public String getCurrentId ()
    {
        WPanel   result;

        result = getCurrentTabPanel();
        if ( result == null )
            return null;
        else
            return result.getId();
    }

    public void add ( String key, T cmp )
    {
        add ( cmp, key );
        //Logger.getInstance().debug ( "WorkPanel.add: Start. key = '" + key + "', panel = " + cmp );
        panels.put ( key, cmp );
    }

    /**
     * Создать пустую Кард-панель - с одним единственным табиком - пусто.
     */
    private void addEmpty ()
    {
        TabsPanel<EditablePanel>    tabsPanel;
        EmptyEditablePanel          emptyPanel;

        emptyPanel  = new EmptyEditablePanel();
        tabsPanel   = new TabsPanel<EditablePanel>();
        // T paramsPanel, String tabId, String tabName
        tabsPanel.addPanel ( emptyPanel, EMPTY, "Пусто" );

        add ( EMPTY, tabsPanel );
    }

    public void setCurrent ( String name )
    {
        previosPanelId = currentPanelId;
        // Если еще нет такой карты - переключиться на пустую
        if ( containsTabsPanel ( name ))
            currentPanelId = name;
        else
            currentPanelId = EMPTY;

        // нельзя убирать - Сборник не отображается
        getCardLayout().show ( this, currentPanelId );
    }

    public String getCurrentPanelName ()
    {
        return currentPanelId;
    }

    public String getPrevios ()
    {
        return previosPanelId;
    }

    public boolean hasPanels ( String name )
    {
        return panels.containsKey ( name );
    }

    /**
     * Вызывается при смене одной рабочей панели с табиками на другую.
     * Задача - сообщить бывшему активному табику, что он закрывается.
     * Дергается у предыдущей рабочей панели.
     */
    public void close()
    {
        WPanel   panel;
        T        tabsPanel;

        // взять текущую вкладку и дернуть у нее close
        panel = getCurrentTabPanel();
        //Logger.getInstance().debug ( "-- WorkPanel.close: previosPanel = " + previosPanel + ", currentPanel = " + currentPanel );
        //Logger.getInstance().debug ( "-- WorkPanel.close: old panel = " + panel );
        if ( panel != null )   panel.close();

        // Обнулить номер индекса
        tabsPanel   = getCurrent();
        if ( tabsPanel != null )   tabsPanel.clear();
    }

    public void setCardLayout ( CardLayout cardLayout )
    {
        this.cardLayout = cardLayout;
        setLayout ( cardLayout );
    }

    public CardLayout getCardLayout ()
    {
        return cardLayout;
    }

    public void setCurrentFirst () throws WEditException
    {
        // todo - здесь первая панель уже вытащена наружу. надо определить ее имя - для currentPanel
        //currentPanel = panels.get
        getCardLayout().first ( this );
        //throw new WEditException ( "Функция смены панели 'setFirst' НЕ реализована !!!" );
    }

    public void showPanel ( String cardId )
    {
        setCurrent ( cardId );
        rewriteAll();

        getCardLayout().show ( this, cardId );
    }

    public Map<String, T> getPanels ()
    {
        return panels;
    }

    public boolean containsTabsPanel ( String cardId )
    {
        return panels.containsKey ( cardId );
    }

    public CardPanel getParentPanel ()
    {
        return parentPanel;
    }

    public void setParentPanel ( CardPanel parentPanel )
    {
        this.parentPanel = parentPanel;
    }
    
}
