package svj.wedit.v6.function.option;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.params.SimpleParameter;
import svj.wedit.v6.gui.dialog.IntegerValueDialog;
import svj.wedit.v6.gui.menu.WEMenuItem;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookPar;
import svj.wedit.v6.obj.function.Function;

import javax.swing.*;

import java.awt.event.ActionEvent;


/**
 * Смена дефолтных размеров фонта текста - текст, аннотация, цветнйо текст.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 09.09.2022 10:06:20
 */
public class ChangeTextSizeFunction extends Function
{
    private WEMenuItem  menuItem;
    private String PARAM_NAME = "textSize";

    public ChangeTextSizeFunction()
    {
        setId ( FunctionId.CHANGE_TEXT_SIZE );
        setName ( "Размер текста" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        IntegerValueDialog dialog;

        Log.l.debug ("Start");

        try
        {
            // Меняем размер иконки
            // Выводим диалог
            dialog  = new IntegerValueDialog( "Изменить дефолтный размер текста", BookPar.TEXT_FONT_SIZE);
            dialog.showDialog ();

            if ( dialog.isOK() )
            {
                // Сохраняем выбранное значение
                Integer value = dialog.getResult();

                BookPar.TEXT_FONT_SIZE = value;

                // Обновляем меню
                createMenu();

                // Обновить фрейм
                Par.GM.getFrame().getToolbar().rewrite ();
            }

        }  catch ( Exception e )        {
            Log.l.error ( "err", e );
            throw new WEditException ( e, "Ошибка смены размера текста '", event.getActionCommand(), "':\n", e );
        }
    }

    @Override
    public void start () throws WEditException
    {
        BookPar.TEXT_FONT_SIZE = Integer.parseInt ( getTextSize() );
        Par.GM.getFrame().getToolbar().rewrite();

        Log.l.debug ( "Finish" );
    }

    public String getTextSize ()
    {
        return getTextSizeParam().getValue ();
    }

    public SimpleParameter getTextSizeParam ()
    {
        SimpleParameter sp;

        sp  = (SimpleParameter) getParameter ( PARAM_NAME );
        if ( sp == null )
        {
            sp  = new SimpleParameter ( PARAM_NAME, BookPar.TEXT_FONT_SIZE ); // дефолтное значение
            sp.setHasEmpty ( false );
            setParameter ( PARAM_NAME, sp );
        }

        return sp;
    }

    @Override
    public JComponent getMenuObject ( String cmd )
    {
        menuItem    = (WEMenuItem) super.getMenuObject ( cmd );

        createMenu();

        return menuItem;
    }

    private void createMenu ()
    {
        menuItem.setText ( getName() + ": " + getTextSizeParam().getValue());
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
        return "Смена размеров для текста, аннотации, цветного текста.";
    }

}
