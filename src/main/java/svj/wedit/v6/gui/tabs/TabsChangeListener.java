package svj.wedit.v6.gui.tabs;


import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.listener.WChangeListener;
import svj.wedit.v6.gui.panel.WPanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.tools.Convert;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;


/**
 * Обработчик смены табов на рабочей панели.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 29.07.2011 17:20:39
 */
public class TabsChangeListener   extends WChangeListener
{
    private int oldTab;

    public TabsChangeListener ( String name )
    {
        super ( name );
        oldTab = WCons.INT_EMPTY;
    }

    /* Смена таба-вкладки */
    @Override
    public void handleAction ( ChangeEvent event )  throws WEditException
    {
        Component   comp;
        String      tabName;
        JTabbedPane tabSource;
        int         selectedIndex;

        Log.l.debug ( "(%s): Start. event = %s",getName(), event );

        tabName     = "";
        try
        {
            // вытаскиваем источник события
            tabSource       = (JTabbedPane) event.getSource();
            selectedIndex   = tabSource.getSelectedIndex();

            // Это акция обнуления Индекса номера вкладки - ничего не делать
            if ( selectedIndex < 0 )
            {
                Log.l.debug ( "(%s): Finish. Its CLEAR_TAB_INDEX action. Nothing do.",getName() );
                return;
            }
            
            comp            = tabSource.getSelectedComponent();
            tabName         = tabSource.getTitleAt ( selectedIndex );

            Log.l.debug ("(%s): tab = '%s', comp = %s",getName(), tabName, comp );

            if ( ! comp.isEnabled() )
            {
                // это - добавление нового компонента в табы - ничего не делать
                reset();
                Log.l.debug ( "(%s): Component is disabled. Nothing do. Finish",getName() );
                return;
            }

            // сказать старой вкладке что она закрывается
            closeOld ( tabSource );

            // Разбираем полученный объект-вкладку (на которую произошел переход)
            // - Может у нее есть ссылка на Card панели и необходимо произвести смену?
            if ( comp instanceof WPanel )
            {
                // Проверка на класс - на всякий случай - тк в табиках содержатся только WPanel объекты
                WPanel panel = ( WPanel ) comp;
                Log.l.debug ( "(%s): WPanel = %s",getName(), panel );
                // здесь дергаем переустановку
                panel.init();
                Log.l.debug ( "(%s): finish reload",getName() );
            }

            // запомнить
            oldTab = selectedIndex;
            Log.l.debug ( "(%s): oldTab = %s",getName(), oldTab );

            /*
        } catch ( WEditException ex )             {
            // Исключение генерится в panel.reload(); но сюда почему-то не доходит (отображается в окне в EltexEventHandler)
            // - и почему-то окончание работы методы проходит нормально - выводится строка 'TabsChangeListener.handleAction : Finish'
            // - ??? может в 1.6 если в методе прописано исключение то все такие исключения выкидываются из try-catch напрямую?
            int codeError   = ex.getCode();
            String msg  = "TabsChangeListener.handleAction: error. Tab = '" + tabName + "'";
            if ( codeError == 1000 )
                Log.l.info ( msg + " - SNMP Timeout" );
            else
                Log.l.error ( msg, ex );
            // отказываемся от возврата на предыдущую панель - т.к. это может привести к тупиковым ситуациям. (svj, 2010-10-20)
            //rollback ( tabSource );
            throw ex;
            */
        } catch ( Exception e )             {
            Log.l.error ( Convert.concatObj ( "(", getName (), "): error. Tab = '", tabName, "'" ), e);
            //rollback ( tabSource );
            throw new WEditException ( e, "Системная ошибка смены текущей вкладки на '",tabName,"' :\n", e );
        }
    }
    
    private void closeOld ( JTabbedPane tabSource )
    {
        Component  comp;
        WPanel     panel;

        if ( tabSource == null ) return;
        if ( (oldTab < 0) || ( oldTab >= tabSource.getTabCount() ) ) return;

        comp = tabSource.getComponentAt ( oldTab );
        if ( (comp != null) && (comp instanceof WPanel ) )
        {
            panel = (WPanel) comp;
            panel.close();
        }
    }

     /**
      * При ошибках на вкладке - переходим на предыдущую вкладку.
      * @param tabSource  Панель с табиками
      */
     public void rollback ( JTabbedPane tabSource )
     {
         Log.l.debug ( "(%s) Start. oldTab = %d",getName(), oldTab );

         if  ( tabSource != null )
         {
             if ( oldTab >= 0  )
             {
                 Log.l.debug ( "(%s) set old tab index = %d",getName(), oldTab );
             }
             else
             {
                 Log.l.debug ( "(%s) set 0 tab",getName() );
                 oldTab = 0;
             }
             tabSource.setSelectedIndex ( oldTab );
         }

         Log.l.debug ( "(%s)  Finish",getName() );
     }

     public void reset ()
     {
         oldTab = WCons.INT_EMPTY;
     }
    
}
