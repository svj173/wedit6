package svj.wedit.v6.function.text;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.gui.tabs.TabsPanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.DialogTools;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;


/**
 * Закрыть все текущие открытые тексты.
 * <BR/> Безусловно скидываем текст в обьект.
 * <BR/> Когда открытых вкладок слишком много, а по одной закрывать - утомительно.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 08.05.2013 16:07:41
 */
public class CloseAllTextTabFunction extends Function
{
    public CloseAllTextTabFunction ()
    {
        setId ( FunctionId.CLOSE_ALL_TEXT );
        setName ( "Закрыть все текстовые панели" );
        setIconFileName ( "close_all.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        int                     ic;
        TabsPanel<TextPanel>    tabsPanel;
        Collection<TextPanel>   tabsList;

        Log.l.debug ( "Start" );

        tabsPanel   = Par.GM.getFrame().getTextTabsPanel();
        // Взять общее кол-во
        tabsList    = tabsPanel.getPanels();
        ic          = tabsList.size ();

        if ( ic == 0 )
        {
            //
            DialogTools.showMessage ( "Внимание", "Нет открытых панелей текстов." );
        }
        else
        {
            ic      = DialogTools.showConfirmDialog ( Par.GM.getFrame(), "Закрыть все панели", "Закрыть все ("+ic+") панели с сохранением данных?" );

            if ( ic == JOptionPane.YES_OPTION )
            {
                // Пнуть панели чтобы скинули ттексты в обьекты
                for ( TextPanel tp : tabsList )
                {
                    if ( tp.isEdit() )    tp.saveTextToNode();
                }
                // закрыть
                tabsPanel.removeAll();
            }
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
        return getName();
    }

}
