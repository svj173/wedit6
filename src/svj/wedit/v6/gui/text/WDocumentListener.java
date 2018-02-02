package svj.wedit.v6.gui.text;


import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.logger.Log;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Листенер событий в текстовой панели главы.
 * <BR/> Основное назначение - отслеживать изменения (устанвливать флаг - было редактирвоание).
 * А также отслеживать текущее положение курсора, чтобы выставлять правильные значения в выпадашках стилей и т.д.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 05.02.2013 15:58
 */
public class WDocumentListener implements DocumentListener
{
    private TextPanel textPanel;

    public WDocumentListener ( TextPanel textPanel )
    {
        this.textPanel = textPanel;
    }

    @Override
    public void insertUpdate ( DocumentEvent event )
    {
        Log.l.debug ( "--- event = ", event );
        textPanel.setEdit ( true );
    }

    @Override
    public void removeUpdate ( DocumentEvent event )
    {
        Log.l.debug ( "--- event = ", event );
        textPanel.setEdit ( true );
    }

    @Override
    public void changedUpdate ( DocumentEvent event )
    {
        Log.l.debug ( "--- event = ", event );
        textPanel.setEdit ( true );
    }

}
