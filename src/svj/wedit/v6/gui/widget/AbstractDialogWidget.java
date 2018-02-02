package svj.wedit.v6.gui.widget;


import javax.swing.*;


/**
 * Виджеты, которые содержатся в диалогах и сами открывают свои вннутренние диалоги.
 * <BR/> Следовательно им необходимо знать о родительсокм диалоге, иначе свой диалог не будет активным.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.08.2011 12:15:27
 */
public abstract class AbstractDialogWidget<T>  extends AbstractWidget<T>
{
    private JDialog dialog;

    protected AbstractDialogWidget ( String titleName, boolean hasEmpty )
    {
        super ( titleName, hasEmpty );
    }

    public JDialog getDialog ()
    {
        return dialog;
    }

    public void setDialog ( JDialog dialog )
    {
        this.dialog = dialog;
    }
    
}
