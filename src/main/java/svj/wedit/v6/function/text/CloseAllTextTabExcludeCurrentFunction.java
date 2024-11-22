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
import java.util.*;


/**
 * Закрыть все текущие открытые тексты кроме Текущей.
 * <BR/> Безусловно скидываем текст в обьект.
 * <BR/> Когда открытых вкладок слишком много, а по одной закрывать - утомительно. И текущую хочется оставить.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 05.02.2021 15:07:41
 */
public class CloseAllTextTabExcludeCurrentFunction extends Function
{
    public CloseAllTextTabExcludeCurrentFunction()
    {
        setId ( FunctionId.CLOSE_ALL_TEXT_EXCLUDE_CURRENT );
        setName ( "Закрыть все текстовые панели кроме текущей" );
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
            DialogTools.showMessage ( "Внимание", "Нет открытых панелей текстов." );
        }
        else
        {
            ic      = DialogTools.showConfirmDialog ( Par.GM.getFrame(), "Закрыть все панели кроме текущей",
                    "Закрыть все ("+(ic-1)+") панели кроме\n текущей с сохранением данных?" );

            if ( ic == JOptionPane.YES_OPTION )
            {
                // Взять текущую текстовую панель
                TextPanel currentText = Par.GM.getFrame().getCurrentTextPanel();
                if (currentText == null)  {
                    Log.l.info ( "Current tab is apsent. Nothing do." );
                    throw new WEditException ("Текущая Таб-панель не выбрана!");
                    //return;
                }

                // Пнуть панели чтобы скинули тексты в обьекты
                for ( TextPanel tp : tabsList )
                {
                    if ( tp.isEdit() )    tp.saveTextToNode();
                    if (! tp.equals(currentText)) {
                        tabsPanel.removeTab(tp);   // Закрываем табик.
                    }
                }
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
