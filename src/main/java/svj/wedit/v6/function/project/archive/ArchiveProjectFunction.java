package svj.wedit.v6.function.project.archive;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.obj.function.Function;

import java.awt.event.ActionEvent;


/**
 * Архивировать проект.
 * <BR/> В диалоге запрашивает директорию, куда архивировать, и имя архивного файла (предварительно предлагается свой вариант, с учетом версии).
 * <BR/> В обьекте Project - должны быть параметры:
 * <BR/> - Дата последней архивации
 * <BR/> - Файл архивации (абс путь)
 * <BR/> - Версия архивации.
 * <BR/>
 * <BR/> В имени архивного файла использовать
 * <BR/> - Имя проекта (на англ)
 * <BR/> - Версия
 * <BR/> - Дата
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 18.08.2011 10:15:11
 */
public class ArchiveProjectFunction extends Function
{
    public ArchiveProjectFunction ()
    {
        setId ( FunctionId.ARCHIVE_PROJECT );
        setName ( "Архивировать Сборник");
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        throw new WEditException ( "Функция еще не реализована." );
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
        return "Архивировать Сборник, учитывая версию архивации.";
    }

}
