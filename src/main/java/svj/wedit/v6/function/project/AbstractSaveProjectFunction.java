package svj.wedit.v6.function.project;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.ConfigParam;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.tools.Utils;

import java.io.File;
import java.io.FileOutputStream;


/**
 * Функция с механизмом сохранения проекта (файла project.xml)
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 18.08.2011 9:19:02
 */
public abstract class AbstractSaveProjectFunction  extends Function
{
    protected File saveProjectFile ( Project project ) throws WEditException
    {
        FileOutputStream    out;
        File                projectFile;

        if ( project == null )
            throw new WEditException ( null, "НЕ задан Сборник для сохранения." );

        out = null;

        try
        {
            projectFile = new File ( project.getProjectDir(), ConfigParam.PROJECT_FILE_NAME );

            out         = new FileOutputStream ( projectFile );

            project.outString ( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n", out );

            project.toXml ( 0, out );

            out.flush();

        } catch ( WEditException we )         {
            throw we;
        } catch ( Exception e )         {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Системная ошибка записи Сборника '", project.getName(), "' :\n", e );
        } finally {
            Utils.close ( out );
        }
        return projectFile;
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
        return null;
    }

}
