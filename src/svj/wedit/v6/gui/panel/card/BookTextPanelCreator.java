package svj.wedit.v6.gui.panel.card;


import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.gui.tabs.TabsPanel;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 28.09.2011 16:35:56
 */
public class BookTextPanelCreator implements CardPanelCreator<TabsPanel<TextPanel>>
{

    @Override
    public TabsPanel<TextPanel> create ( Object pars )
    {
        return new TabsPanel<TextPanel>();
    }

}
