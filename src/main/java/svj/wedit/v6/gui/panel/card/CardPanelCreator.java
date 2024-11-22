package svj.wedit.v6.gui.panel.card;


import svj.wedit.v6.gui.panel.WPanel;


/**
 * Фабрика создания пустых панелей для CardPanel.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 28.09.2011 16:34:16
 */
public interface CardPanelCreator<T extends WPanel>
{
    public T create ( Object pars );
}
