package svj.wedit.v6.function.project.open;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionGroup;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.project.OpenProjectEvent;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.tools.ProjectTools;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 15.07.2011 16:36:45
 */
public class OpenProjectFunction extends Function
{
    public OpenProjectFunction ()
    {
        setId ( FunctionId.OPEN_PROJECT );
        setName ( "Открыть Сборник");
        setMapKey ( "Ctrl/O" );
        setIconFileName ( "open.png" );

        // установить свою группу
        setFunctionGroup ( FunctionGroup.OPEN_PROJECT );

        // установить  группу функций для прослушивания - от кого хотим получать события
        setListenerGroup ( FunctionGroup.REOPEN_PROJECT );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        JFileChooser    chooser;
        int             returnValue;
        File            file;
        boolean         fromReopen;
        String          str, cmd;
        OpenProjectEvent openProjectEvent;

        Log.l.debug ( "Start. event = %s", event );
        cmd = event.getActionCommand();
        Log.l.debug ( "cmd = %s; listGroup = %s", cmd, getListenerGroup() );

        openProjectEvent    = new OpenProjectEvent();

        // Выдать диалог - открыть в этом окне, в новом, отменить.
        // Если в этом же - дернуть функцию закрытия проекта.
        // cmd
        // - REOPEN_PROJECT - from Reopen
        // - menu           - from MENU
        try
        {
            // - определяется из cmd
            fromReopen  = cmd.equals ( FunctionGroup.REOPEN_PROJECT.toString() );

            if ( fromReopen )
            {
                // This from Reopen. Get file from CMD
                //str     = cmd.substring ( sf.length() );
                str     = (String) event.getSource();
                Log.l.debug ( "event source = %s", str );
                file    = new File ( str );
                // Проверить - есть ли такой файл
                if ( ! file.exists() )
                {
                    // Нет такого файла - сгенерить событие чтобы Реопен убрал его из своего списка
                    openProjectEvent.setProjectFile ( file );
                    openProjectEvent.setMode ( OpenProjectEvent.Mode.NONE_EXIST );
                    setEventObject ( openProjectEvent );
                    // Закончить работу
                    throw new WEditException ( null, "Файл Сборника '", file, "' не существует." );
                }
                else
                {
                    openProject ( file );
                }
            }
            else
            {
                // Вызвать диалог выборки файла.
                chooser = new JFileChooser();
                //chooser = new JFileChooser ( currentDirectory );
                chooser.setDialogTitle ( "Укажите файл существующего Сборника." );

                // установить файл "по-умолчанию"
                //chooser.setSelectedFile ( saveFile );

                // Добавить панельку справа (с дополнительными параметрами)
                // chooser.setAccessory ( paramsPanel );

                // открываем файловый диалог
                //returnValue = chooser.showSaveDialog ( Par.GM.getFrame() );  // may be NULL
                returnValue = chooser.showOpenDialog ( Par.GM.getFrame() );  // may be NULL
                //returnValue = chooser.showSaveDialog(parent);

                if ( returnValue == JFileChooser.APPROVE_OPTION )
                {
                    // взять имя выбранного файла
                    file = chooser.getSelectedFile();
                    openProject ( file );
                }
            }

        } catch ( WEditException we )        {
            throw we;
        } catch ( Exception e )        {
            Log.l.error ( "err", e );
            /*
            openProjectEvent.setProjectFile ( file );
            openProjectEvent.setMode ( OpenProjectEvent.Mode.NONE_EXIST );
            setEventObject ( openProjectEvent );
            */
            throw new WEditException ( e, "Системная ошибка открытия Сборника :\n", e );
        }
    }

    private void openProject ( File file )  throws WEditException
    {
        Project             project;
        OpenProjectEvent    openProjectEvent;
        String              projectId;

        Log.l.debug ( "selected saveFile = %s", file );

        // Определить - может такой Сборник уже загружен и открыт
        projectId   = file.getParentFile().getAbsolutePath();
        if ( Par.GM.containProject ( projectId ))
        {
            // уже есть открытый - сделать текущим выбранным
            Par.GM.selectProject ( projectId );
        }
        else
        {
            // загрузить и распарсить файл
            project     = ProjectTools.loadProject ( file );

            // установить новый проект в систему (добавить к списку открытых проектов)).
            Par.GM.addProject ( project );

            // Сгенерить пост-событие - для ReopenProject, чтобы занес в свой список первым.
            openProjectEvent    = new OpenProjectEvent();
            openProjectEvent.setProjectFile ( file );
            openProjectEvent.setMode ( OpenProjectEvent.Mode.OK );
            openProjectEvent.setProjectName ( project.getName() );
            setEventObject ( openProjectEvent );
        }
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
        return "Открыть Сборник";
    }

}
