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
import svj.wedit.v6.tools.TreeObjTools;

import java.awt.event.ActionEvent;
import java.io.File;


/**
 * Добавить новый обьект дерева в отмеченный обьект (только Узел), первым по списку.
 * <BR/> Создает директорию, перезаписывает проект в project.xml  - т.е. без возможности отката.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 09.08.2011 14:12:24
 */
public class AddSectionInFunction extends AbstractSaveProjectFunction
{
    public AddSectionInFunction ()
    {
        setId ( FunctionId.ADD_SECTION_IN );
        setName ( "Добавить новый Раздел в...");
        //setMapKey ( "Ctrl/N" );
        setIconFileName ( "add.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        TreeObj             newNode;
        int                 inum;
        TreePanel<Project>  currentProjectPanel;
        TreeObj             selectNode;
        CreateSectionDialog dialog;
        Section             section, parentSection;
        Project             project;

        Log.l.debug ( "Start" );

        // Взять текущий проект - TreePanel
        currentProjectPanel = Par.GM.getFrame().getCurrentProjectPanel();

        selectNode  = currentProjectPanel.getCurrentObj();

        // Взять родителя отмеченного элемента
        //parentNode  = (TreeObj) selectNode.getParent();

        // Взять уровень выбранного элемента. Проверить - может уже последний?
        //level       = selectNode.getLevel();

        // Корень допустим
        //if ( level == 0 )  throw new WEditException ( "Выбран корневой элемент" );


        // Взять текущий проект
        project         = currentProjectPanel.getObject();

        // Диалог - Запросить имя нового обьекта
        dialog  = new CreateSectionDialog ( project, "Новый Раздел", true );
        dialog.showDialog();
        if ( dialog.isOK() )
        {
            section     = dialog.getResult();

            newNode     = new TreeObj();
            newNode.setUserObject ( section );
            //newNode.setType ( section.getType() );

            // Устанавливаем флаг что было изменение данных
            //currentProjectPanel.setEdit ( true );

            // Взять номер отмеченного. inum+1 - значит вставлять в следующее место, после inum.
            //inum            = parentNode.getIndex ( selectNode );

            inum    = 0;

            // Добавить в сектора проекта - внутрь отмеченного
            //selectNode.
            parentSection   = (Section) selectNode.getWTreeObj();
            parentSection.addSection ( inum, section );
            section.setParent ( parentSection );

            // Сохранить новый Раздел - новая файловая директория
            saveNewSection ( project, selectNode, section );

            // Сохранить обновление проекта - в файле project.xml
            saveProjectFile ( project );

            // Добавить в дерево после отмеченного - в самом конце, когда все действия прошли успешно (создание директории, перезапись project.xml и т.д.)
            currentProjectPanel.insertNode ( newNode, selectNode, inum );
        }

        Log.l.debug ( "Finish" );
    }

    private void saveNewSection ( Project project, TreeObj parentNode, Section section ) throws WEditException
    {
        StringBuilder   filePath;
        File            newDir, projectDir;
        boolean         b;

        if ( project == null )
            throw new WEditException ( null, "Сборник не задан" );

        projectDir = project.getProjectDir();
        if ( projectDir == null )
            throw new WEditException ( null, "У Сборника '", project.getName(), "' отсутствует описание на корневой файл." );

        // Создать полное имя файла-директории
        // - Сложить директории по парентам Разделов, исключая корневой (т.к. он уже входит в путь Проекта).
        filePath    = new StringBuilder (128);
        TreeObjTools.createFilePath ( parentNode, filePath );
        Log.l.debug ( "filePath 1 = '", filePath, "'" );

        filePath.insert ( 0, projectDir.getAbsolutePath() );

        filePath.append ( '/' );
        filePath.append ( section.getFileName() );

        Log.l.debug ( "filePath 2 = '", filePath, "'" );

        // Создать новую директорию в проекте
        newDir  = new File ( filePath.toString() );
        if ( newDir.exists() )
            throw new WEditException ( null, "Директория для нового раздела \n'", newDir, "' уже существует." );

        b   = newDir.mkdir();
        if ( ! b )
            throw new WEditException ( null, "Не удалось создать директорию для нового раздела \n'", newDir, "'." );
    }

}
