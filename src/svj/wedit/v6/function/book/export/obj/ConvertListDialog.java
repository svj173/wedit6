package svj.wedit.v6.function.book.export.obj;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.dialog.WValidateDialog;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * Редактор по работе со списками вариантов типов конвертаций (samlib, book_ru, ...).
 * <BR/> Получает список параметров типов и возвращает новый список.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 30.10.2015 16:38
 */
public class ConvertListDialog  extends WValidateDialog<Collection<ConvertParameter>,Collection<ConvertParameter>>
{
    public ConvertListDialog ( Dialog parent, String title )
    {
        super ( parent, title );

        addToCenter ( new JLabel ("Не реализовано!") );
    }

    @Override
    public boolean validateData ()
    {
        // Чтобы у всех параметров были заданы файлы.
        return false;
    }

    /**
     * Занести исходны список параметров.
     * @param initObject
     * @throws WEditException
     */
    @Override
    public void init ( Collection<ConvertParameter> initObject ) throws WEditException
    {
    }

    /**
     * Выдать новый список параметров. Для сохранения в функции и т.д.
     * @return
     * @throws WEditException
     */
    @Override
    public Collection<ConvertParameter> getResult () throws WEditException
    {
        return null;
    }

}
