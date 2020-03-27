package svj.wedit.v6.gui.panel;


import svj.wedit.v6.WCons;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.panel.card.CardPanel;
import svj.wedit.v6.gui.tabs.TabsPanel;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.Editable;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.function.Function;

import javax.swing.*;

import java.awt.*;
import java.util.Map;


/**
 * Рабочая панель (Сборники, Книги, Тексты)
 * <BR/> Включает в себя
 * <BR/> - Титл от парента. Индикатор - к чему относится данная рабочая панель.
 * <BR/> - Панель с кнопками
 * <BR/> - Панель с деревом
 * <BR/> - (возможно) статус-панель внизу
 * <BR/>
 * <BR/> T - то из чего состоит данная TabsPanel.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 05.08.2011 16:31:00
 */
public class WorkPanel<T extends EditablePanel> extends RewritePanel
{
    /* Иконки над рабочей панелью. */
    private final IconsPanel iconPanel;
    private final CardPanel<TabsPanel<T>> cardPanel;
    /* Титл парента. Отображается сверху над панелью дерева или текста. Для дерева Сборников здесь отображается не парент (которого нет) а текущий Сборник. */
    private final JTextField titleField;

    /** Если задан, то именно этот текст всегда будет отображаться над панелью. Если нет - будет вычисляться текущий обьект - Сборник, Книга... */
    private String fixTitle = null;


    public WorkPanel ( String name, CardPanel parentPanel, CardPanel<TabsPanel<T>> cp, String fixTitle )
    {
        super();
        
        setName ( name );
        setTitle ( name );

        cardPanel   = cp;
        //cardPanel.startInit();
        cardPanel.setName ( name );
        //cardPanel.setOpaque ( false );
        cardPanel.setParentPanel ( parentPanel );
        cardPanel.setVisible ( true );

        this.fixTitle = fixTitle;

        // --------------- Иконки над деревом проектов ---------------------------
        iconPanel = new IconsPanel ( name );
        iconPanel.setName ( name );

        titleField  = new JTextField ("");
        titleField.setBackground ( WCons.LIGHT_YELLOW );
        titleField.setEditable ( false );

        JPanel panel;
        panel   = new JPanel();
        panel.setLayout ( new BorderLayout(0,0) );
        panel.add ( titleField, BorderLayout.NORTH );
        panel.add ( iconPanel, BorderLayout.CENTER );

        setLayout ( new BorderLayout(0,0) );
        add ( panel, BorderLayout.NORTH );
        add ( cardPanel, BorderLayout.CENTER );
    }

    @Override
    public void rewrite ()
    {
        CardPanel   cp;
        Component   ep;

        Log.l.debug ( "(%s) Start",getName() );

        iconPanel.rewrite();
        cardPanel.rewrite();

        if ( fixTitle == null )
        {
            // Выяснить титл парента и занести - titleField
            cp = cardPanel.getParentPanel();
            //Log.l.debug ( "(%s) --- parent cardPanel = %s",getName(), cardPanel );
            if ( cp == null )
            {
                titleField.setText ( "" );
            }
            else
            {
                ep = cp.getCurrentTabPanel();
                // По идее - сюда же выводить и имя файла (директории - для проекта).
                if ( ep != null ) {
                    // Если брать титл из табика, то он там убдет урезанный - если так отображен на вкладках.
                    // Берем полный титл.
                    if ( ep instanceof TreePanel)  {
                        TreePanel treePanel = (TreePanel) ep;
                        Editable eb = treePanel.getObject();
                        if ( eb instanceof BookContent) {
                            BookContent bookContent = (BookContent) eb;
                            titleField.setText ( bookContent.getName() );
                        }
                    } else {
                        // так небывает
                        titleField.setText ( ep.getName() ); // ep.getName() getTitle
                    }
                }
            }
        }
        else
        {
            titleField.setText ( fixTitle );
        }

        Log.l.debug ( "(%s) Finish",getName() );
    }

    public Function getFunction ( FunctionId functionId )
    {
        return iconPanel.getFunction ( functionId );
    }

    public void addIconFunction ( Function function )
    {
        iconPanel.addFunction ( function );
    }

    public CardPanel<TabsPanel<T>> getCardPanel ()
    {
        return cardPanel;
    }

    public TabsPanel<T> getTabsPanel ( String tabsId )
    {
        return cardPanel.get(tabsId);
    }

    public void deleteTabsPanel ( String tabsId )
    {
        cardPanel.delete ( tabsId );
    }

    public void addTabsPanel ( String tabsId, TabsPanel<T> tabsPanel )
    {
        cardPanel.add ( tabsId, tabsPanel );
    }

    public void setCurrent ( String tabsId )
    {
        cardPanel.setCurrent ( tabsId );
    }

    public TabsPanel<T> getCurrent ()
    {
        return cardPanel.getCurrent();
    }

    public Map<String, TabsPanel<T>> getPanels ()
    {
        return cardPanel.getPanels();
    }

    /*
    public Collection<T> getObjects ()
    {
        Map<String, TabsPanel<T>> maps;
        Collection<T> result;

        result = new LinkedList<T>();
        maps = getPanels();
        for ( TabsPanel<T> panel : maps.values() )
        {
            result.add ( panel.get)
        }
        return result;
    }
    */
}
