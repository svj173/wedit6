package svj.wedit.v6.function.project.create_new;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.MessageException;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionGroup;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.project.OpenProjectEvent;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.Author;
import svj.wedit.v6.obj.ConfigParam;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.Section;
import svj.wedit.v6.obj.function.SimpleFunction;
import svj.wedit.v6.tools.ProjectTools;
import svj.wedit.v6.tools.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;


/**
 * Создать новый проект.
 * <BR/> Если такой файл проекта уже существует - ругаться и не создавать.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 15.07.2011 16:36:45
 */
public class NewProjectFunction extends SimpleFunction
{
    /* Директория проекта. Именно от нее формируются относительные имена файлов. */
    private String                    projectRootDir;

    public NewProjectFunction ()
    {
        setId ( FunctionId.NEW_PROJECT );
        setName ( "Создать новый Сборник");
        setMapKey ( "Ctrl/N" );
        setIconFileName ( "new.png" );

        // установить свою группу
        setFunctionGroup ( FunctionGroup.OPEN_PROJECT );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        JFileChooser            chooser;
        int                     returnValue;
        File                    projectDir;
        NewProjectParamsPanel   paramsPanel;
        String                  projectName, cwd;
        Project                 project;
        Author                  author;
        Section                 rootSection;
        OpenProjectEvent        openProjectEvent;
        File                    projectFile;

        // Выдать диалог - открыть в этом окне, в новом, отменить.
        // Если в этом же - дернуть функцию закрытия проекта.
        try
        {
            // Взять директорию Сборников
            cwd         = ProjectTools.getProjectsFolder();
            if ( cwd == null )   cwd         = System.getProperty("user.dir");
            // Вызвать диалог выборки файла.
            chooser     = new JFileChooser ( cwd );
            //chooser = new JFileChooser ( currentDirectory );
            chooser.setDialogTitle ( "Укажите директорию для нового Сборника" );

            // Фильтр - только директории
            
            // установить файл "по-умолчанию"
            //chooser.setSelectedFile ( saveFile );

            // Добавить панельку справа (с дополнительными параметрами) - Имя проекта, автор
            paramsPanel = new NewProjectParamsPanel();
            chooser.setAccessory ( paramsPanel );

            chooser.setMultiSelectionEnabled ( false );
            chooser.setAcceptAllFileFilterUsed ( true );
            chooser.setFileSelectionMode ( JFileChooser.DIRECTORIES_ONLY );
            chooser.setFileFilter ( new javax.swing.filechooser.FileFilter()
            {
                public boolean accept ( File f )
                {
                    return f.isDirectory();
                }

                public String getDescription ()
                {
                    return "Директории";
                }
            } );

            // открываем файловый диалог
            returnValue = chooser.showOpenDialog ( Par.GM.getFrame() );  // may be NULL
            //returnValue = chooser.showSaveDialog(parent);

            if ( returnValue == JFileChooser.APPROVE_OPTION )
            {
                // взять имя выбранной директории
                projectDir  = chooser.getSelectedFile();
                Log.l.debug ( "selected Dir = %s", projectDir );
                projectRootDir  = projectDir.getAbsolutePath() + '/';
                Log.l.debug ( "projectRootDir = %s", projectRootDir );

                // Имя Сборника (русский)
                projectName = paramsPanel.getProjectName();
                Log.l.debug ( "projectName = '%s'", projectName );
                if ( (projectName == null) || projectName.isEmpty() )    
                    throw new WEditException ( "Не задано имя нового Сборника." );

                // создать обьект Project
                project     = new Project ( projectDir );
                project.setName ( projectName );
                //project.setProjectDir ( projectDir );
                author      = paramsPanel.getAuthor();
                project.setAuthor ( author );
                project.setCreateDate ( new Date() );

                checkProjectDir ( projectDir );

                // Создать корневую секцию
                rootSection = new Section ( projectName );
                //rootSection = new Section ( projectDir.getName() );
                project.setRootSection ( rootSection );

                // создать файл и записать его в директорию
                projectFile = saveProjectFile ( projectDir, project );

                // установить новый проект в систему (добавить к списку открытых проектов)).
                Par.GM.addProject ( project );

                // Сгенерить пост-событие - для ReopenProject, чтобы занес в свой список первым.
                openProjectEvent    = new OpenProjectEvent();
                openProjectEvent.setProjectFile ( projectFile );
                openProjectEvent.setProjectName ( project.getName() );
                openProjectEvent.setMode ( OpenProjectEvent.Mode.OK );
                setEventObject ( openProjectEvent );

                // todo Сохранить новый путь для Сборников.
            }

        } catch ( WEditException we )        {
            throw we;
        } catch ( Exception e )        {
            Log.l.error ( "err", e );
            throw new WEditException ( e, "Системная ошибка создания нового Сборника :\n", e );
        }
    }

    private void checkProjectDir ( File projectDir )  throws WEditException
    {
        File                projectFile;

        if ( projectDir.exists() )
        {
            // Такая директория уже сушествует.
            // - Проверяем на наличие в ней файла с именем Сборника.
            projectFile = new File ( projectDir, ConfigParam.PROJECT_FILE_NAME );
            if ( projectFile.exists () )   throw new MessageException ( "Файл с именем нового сборника '", projectFile, "' уже существует!" );
        }
        else
        {
            // Создать такую директорию.
            if ( ! projectDir.mkdir() )   throw new MessageException ( "Не удалось создать директорию '", projectDir, "'  для нового сборника!" );
        }
    }

    private File saveProjectFile ( File projectDir, Project project ) throws WEditException
    {
        FileOutputStream    out;
        File                projectFile;

        out = null;

        try
        {
            projectFile = new File ( projectDir, ConfigParam.PROJECT_FILE_NAME );

            // Если такой файл уже существует - ругаться -- НЕТ, перезаписываем.
            //if ( projectFile.exists() )
            //    throw new WEditException ( null, "Файл нового Проекта \n'", projectFile, "'\nуже существует.");

            out         = new FileOutputStream ( projectFile );

            project.outString ( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n", out );

            project.toXml ( 0, out );

            out.flush();

        } catch ( WEditException we )         {
            throw we;
        } catch ( Exception e )         {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Системная ошибка записи нового Сборника :\n", e );
        } finally {
            Utils.close ( out );
        }
        return projectFile;
    }

    @Override
    public void rewrite ()
    {
    }

}
