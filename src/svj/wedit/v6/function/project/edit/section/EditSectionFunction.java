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

import java.awt.event.ActionEvent;


/**
 * Редактировать выбранный Раздел.
 * <BR/> Допускается изменять только Русское название Раздела. Имя директории - нельзя.
 * <BR/> Перезаписывает проект в project.xml  - т.е. без возможности отката.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 18.08.2011 10:52:24
 */
public class EditSectionFunction extends AbstractSaveProjectFunction
{
    public EditSectionFunction ()
    {
        setId ( FunctionId.EDIT_SECTION );
        setName ( "Редактировать Раздел");
        setIconFileName ( "edit.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        TreePanel<Project>  currentProjectPanel;
        TreeObj             selectNode;
        CreateSectionDialog dialog;
        Section             newSection, editSection;
        Project             project;
        String              sectionName;

        Log.l.debug ( "Start" );

        // Взять текущий проект - TreePanel
        currentProjectPanel = Par.GM.getFrame().getCurrentProjectPanel();
        if ( currentProjectPanel == null ) throw  new WEditException ( "Текущий Сборник не установлен." );

        // Взять текущий узел
        selectNode  = currentProjectPanel.getCurrentObj();
        if ( selectNode == null ) throw  new WEditException ( "Раздел не выбран." );

        editSection = (Section) selectNode.getWTreeObj();

        // Взять текущий проект
        project         = currentProjectPanel.getObject();

        // Диалог
        dialog      = new CreateSectionDialog (project, getName(), false );
        dialog.init ( editSection );
        dialog.showDialog();
        if ( dialog.isOK() )
        {
            newSection     = dialog.getResult();
            // Взять имя
            sectionName    = newSection.getName();
            // Сравнить имена
            if ( sectionName.equals ( editSection.getName() ) )
                throw new WEditException ( null, "Имя раздела не изменилось" );

            // Изменяем имя
            editSection.setName ( sectionName );

            // Сохранить обновление проекта - в файле project.xml
            saveProjectFile ( project );

            // Обновить название в дереве
            currentProjectPanel.getTreeModel().nodeChanged ( selectNode );
        }

        Log.l.debug ( "Finish" );
    }

}
