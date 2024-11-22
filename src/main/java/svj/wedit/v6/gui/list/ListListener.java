package svj.wedit.v6.gui.list;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.listener.WListSelectionListener;
import svj.wedit.v6.logger.Log;

import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Слушатель на списке.
 * <BR/> Отрабатывает изменения, дергая для этого actionListener - тот уже сам знает что надо делать.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.08.2011 22:06:43
 */
public class ListListener extends WListSelectionListener
{
    private ActionEvent     actionEvent;
    private ActionListener  actionListener;

    public ListListener ( String name, ActionEvent actionEvent, ActionListener actionListener )
    {
        super ( name );
        this.actionEvent    = actionEvent;
        this.actionListener = actionListener;
    }

    @Override
    public void handleAction ( ListSelectionEvent event ) throws WEditException
    {
        Log.l.debug ( "ListListener.handleAction: Start" );

        // getSource - ?? - JList or WListPanel

        if ( event.getValueIsAdjusting() )
        {
            // проверка необходима, иначе на каждый клик в списке будет генериться две акции
            // - это 'выход' из предыдущего значения
            //Log.l.debug ("ListListener.valueChanged ("+getName()+"): Finish - it is Adjusting");
        }
        else
        {
            // Здесь конечная точка исключений - actionListener.actionPerformed. Там выскакивает информационное окно с сообщением об ошибке.
            if ( (actionEvent != null ) && (actionListener != null) )
            {
                actionListener.actionPerformed ( actionEvent );
            }
            // Могут и отсутствовать. svj, 2011-03-22
            /*
            else
            {
                JOptionPane.showMessageDialog ( Par.FRAME, "Системная ошибка работы со списком : Отсутствует обработчик или объект события обработчика",
                                                "Ошибка",  JOptionPane.ERROR_MESSAGE );
            }
            */
        }

        Log.l.debug ("ListListener.handleAction: Finish");
    }

}

