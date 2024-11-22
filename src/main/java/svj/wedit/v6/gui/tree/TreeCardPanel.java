package svj.wedit.v6.gui.tree;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.panel.card.CardPanel;
import svj.wedit.v6.obj.Editable;
import svj.wedit.v6.obj.TreeObj;


/**
 * Панель, которая жестко связана с набором card-панелей (содержащих деревья).
 * <BR/> Т.е. при выборке этой панели, в cardPanel тоже должна смениться текущая панель.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 28.09.2011 14:54:03
 */
public class TreeCardPanel<T extends Editable>  extends TreePanel<T>
{
    /* Набор панелей, который связан с этой панелью (дочерние панели). */
    private CardPanel   cardPanel;

    /* ИД данной панели. */
    private String      cardId;

    
    public TreeCardPanel ( TreeObj root, T object, CardPanel cardPanel, String cardId ) throws WEditException
    {
        super ( root, object );

        this.cardPanel  = cardPanel;
        this.cardId     = cardId;
    }

    /**
     * Выбрана данная панель. Сменить панель у связанного с ней набора card-панелей.
     */
    public void init() throws WEditException
    {
        /*
        String cardId;

        Log.l.debug ( "Start" );
        Log.l.debug ( "cardPanel = ", cardPanel );
        cardId  = getCardId();
        Log.l.debug ( "cardId = ", cardId );
        //Log.l.debug ( "id = ", getId() );

        // Если есть такая кард-панель - перейти на нее. Если нет - отобразить пустую.
        if ( cardPanel.containsTabsPanel ( cardId ) )
            cardPanel.showPanel ( cardId );
        else
            cardPanel.showPanel ( "empty" );

        Log.l.debug ( "Finish" );
        */
    }

    public String getCardId ()
    {
        return cardId;
    }

    public CardPanel getCardPanel ()
    {
        return cardPanel;
    }

}
