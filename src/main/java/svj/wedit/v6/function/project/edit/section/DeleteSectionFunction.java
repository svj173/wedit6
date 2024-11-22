package svj.wedit.v6.function.project.edit.section;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.project.AbstractSaveProjectFunction;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.Section;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.DialogTools;
import svj.wedit.v6.tools.FileTools;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 18.08.2011 9:49:15
 */
public class DeleteSectionFunction  extends AbstractSaveProjectFunction
{
    public DeleteSectionFunction ()
    {
        setId ( FunctionId.DELETE_SECTION );
        setName ( "Удалить Раздел");
        setIconFileName ( "delete.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        int                 inum, level;
        TreePanel<Project> currentProjectPanel;
        TreeObj             selectNode;
        Section             section, parentSection;
        Project             project;
        String              msg;
        boolean             b;

        Log.l.debug ( "Start" );

        try
        {
            // Взять текущий проект - TreePanel
            currentProjectPanel = Par.GM.getFrame().getCurrentProjectPanel();

            selectNode  = currentProjectPanel.getCurrentObj();
            section     = (Section) selectNode.getWTreeObj();
            Log.l.debug ( "section for delete = ", section );


            // Взять уровень выбранного элемента. Проверить - может уже последний?
            level       = selectNode.getLevel();
            // Корень НЕ допустим
            if ( level == 0 )  throw new WEditException ( "Нельзя удалить корневой элемент" );

            // todo Проверка, может книги для удаления открыты в панелях книг или текстах?

            // Диалог - Запросить имя нового обьекта
            msg     = Convert.concatObj ( getName(), " '",section.getName(),"'\nсо всеми подразделами и книжками ?" );
            inum    = DialogTools.showConfirmDialog ( Par.GM.getFrame(), msg, "Удалить", "Отменить" );
            if ( inum == JOptionPane.YES_OPTION )
            {
                // Удалить из вышестоящего сектора проекта
                parentSection   = (Section) section.getParent();
                b               = parentSection.deleteSection ( section );
                if ( ! b )
                    throw new WEditException ( null, "Не удалось удалить Раздел в родительском Разделе '", parentSection.getName(), "'." );

                // Взять текущий проект
                project         = currentProjectPanel.getObject();

                // Удалить Раздел - файловая директория -  со всеми вложениями
                deleteSection ( project, selectNode );

                // Сохранить обновление проекта - в файле project.xml
                saveProjectFile ( project );

                // Удалить из дерева - в самом конце, когда все действия прошли успешно (создание директории, перезапись project.xml и т.д.)
                currentProjectPanel.removeNode ( selectNode );

                // todo Закрыть открытые табики удаляемого раздела
            }
        } catch ( WEditException we )       {
            throw we;
        } catch ( Exception e )       {
            Log.l.error ( "Error. ", e );
        }

        Log.l.debug ( "Finish" );
    }

    private void deleteSection ( Project project, TreeObj selectNode ) throws WEditException
    {
        StringBuilder   filePath;
        File            sectionDir, projectDir;
        boolean         b;

        if ( project == null )
            throw new WEditException ( null, "Сборник не задан" );

        projectDir = project.getProjectDir();
        if ( projectDir == null )
            throw new WEditException ( null, "У Сборника '", project.getName(), "' \nотсутствует описание на корневой файл." );

        // Создать полное имя файла-директории
        filePath    = FileTools.createNodeFilePath ( project, selectNode );

        Log.l.debug ( "filePath 2 = '", filePath, "'" );

        // Удалить директорию в проекте. file.delete() - удаляет только пустые директории.
        sectionDir  = new File ( filePath.toString() );
        b           = FileTools.deleteRecursive ( sectionDir );
        // Если проблемы с физичпским удалением директории - все ранво удаляем из проекта
        if ( ! b  ) 
        {
            Log.l.error ( "Не удалось удалить директорию раздела \n'%s'.", sectionDir );
            //throw new WEditException ( null, "Не удалось удалить директорию раздела \n'", sectionDir, "'." );
        }
    }

}
