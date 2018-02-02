package svj.wedit.v6.function.text;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.gui.tabs.TabsPanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.function.Function;

import java.awt.event.ActionEvent;


/**
 * Закрыть текущий открытый текст - по крестику на табике, -- либо по сочетанию клавиш Ctrl/W -- нет.
 * <BR/> Безусловно скидываем текст в обьект.
 * <BR/> Функция не заносится  в пул функций и применяется локально, в акции закрытия табика.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 31.01.2013 11:07:41
 */
public class CloseTextTabFunction extends Function
{
    private TextPanel textPanel;   // тот табик, которому принадлежит крест

    public CloseTextTabFunction ( TextPanel textPanel )
    {
        setId ( FunctionId.CLOSE_TEXT );
        setName ( "Закрыть текст" );
        setIconFileName ( "close_red.png" );
        this.textPanel = textPanel;
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        TabsPanel<TextPanel>    tabsPanel;

        Log.l.debug ( "Start. event = ", event );

        tabsPanel           = Par.GM.getFrame().getTextTabsPanel();

        if ( textPanel != null )
        {
            // Сохраняем только в обьекте -- TextToBookNode
            textPanel.saveTextToNode();

            tabsPanel.removeTab ( textPanel );   // Закрываем табик.
        }

        Log.l.debug ( "Finish" );
    }

    @Override
    public void rewrite ()
    {
    }

    @Override
    public void init () throws WEditException
    {
    }

    @Override
    public void close ()
    {
    }

    @Override
    public String getToolTipText ()
    {
        return "Закрыть вкладку текущего текста.";
    }

}
