package svj.wedit.v6.function.text;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.gui.tabs.TabsPanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.DialogTools;

import javax.swing.*;
import java.awt.event.ActionEvent;


/**
 * Закрыть текущий открытый текст.
 * <BR/> - Если не сохранен в обьекте - спрашиваем на сохранение.
 * <BR/> - Сохраняем если надо.
 * <BR/> - Закрываем табик - по запросу.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 18.05.2012 15:07:41
 */
public class CloseTextFunction extends Function
{
    public CloseTextFunction ()
    {
        setId ( FunctionId.CLOSE_TEXT );
        setName ( "Закрыть текст" );
        setIconFileName ( "close_red.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        TextPanel               currentTextPanel;
        String                  msg;
        int                     ic;
        TabsPanel<TextPanel>    tabsPanel;

        Log.l.debug ( "Start" );

        tabsPanel           = Par.GM.getFrame().getTextTabsPanel();
        // Взять текущую книгу - TreePanel
        currentTextPanel    = tabsPanel.getSelectedComponent();
        if ( currentTextPanel == null )
            throw new WEditException ( "Не выбран текст." );

        if ( currentTextPanel.isEdit() )
        {
            // Запросить на сохранение текста.
            msg     = Convert.concatObj ( "Текст главы '",currentTextPanel.getName(),"' был изменен, но не сохранен.\n Сохранить ?" );
            ic      = DialogTools.showConfirmDialog ( Par.GM.getFrame(), "Сохранить текст", msg );

            if ( ic == JOptionPane.YES_OPTION )
            {
                // Сохраняем только в обьекте -- TextToBookNode
                currentTextPanel.saveTextToNode();
                DialogTools.showMessage ( "Внимание", "Эпизод книги сохранен в обьекте книги." );
            }

            tabsPanel.removeTab ( currentTextPanel );   // Закрываем табик.
        }
        else
        {
            // Запросить на закрытие
            msg     = Convert.concatObj ( "Закрыть текст главы '",currentTextPanel.getName(),"' ?" );
            ic      = DialogTools.showConfirmDialog ( Par.GM.getFrame(), "Закрыть текст", msg );
            if ( ic == JOptionPane.YES_OPTION )
                tabsPanel.removeTab ( currentTextPanel );  // Закрываем табик.
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
