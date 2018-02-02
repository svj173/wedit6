package svj.wedit.v6.gui.panel;


import javax.swing.*;
import java.awt.*;


/**
 * Пустая панель. Отображается когда для выбранного обьекта дерева нет еще открытых табиков.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 25.03.2012 13:17:52
 */
public class EmptyEditablePanel  extends EditablePanel
{
    public EmptyEditablePanel ()
    {
        setLayout ( new FlowLayout() );

        add ( new JLabel ("Нет открытых данных.") );
    }

    @Override
    public void rewrite ()
    {
    }

}
