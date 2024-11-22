package svj.wedit.v6.function.project.edit.section;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.dialog.WValidateDialog;
import svj.wedit.v6.gui.widget.StringFieldWidget;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.Section;

import javax.swing.*;


/**
 * Диалог по созданию Раздела Сборника.
 * <BR/> todo Аннотация на раздел ?
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 09.08.2011 14:54:05
 */
public class CreateSectionDialog extends WValidateDialog<Section, Section>
{
    private StringFieldWidget   nameWidget, fileNameWidget;
    private Project project;

    public CreateSectionDialog(Project project, String title, boolean editFileName) throws WEditException
    {
        super ( title );

        this.project = project;

        JPanel  panel;
        int     width;


        width   = 220;

        panel = new JPanel();
        panel.setLayout ( new BoxLayout(panel, BoxLayout.PAGE_AXIS) );

        // boolean hasEmpty, int maxSize, int width, String titleName
        nameWidget = new StringFieldWidget ( "Название раздела", false, 128, 320 );
        nameWidget.setTitleWidth ( width );
        //nameWidget.setValue ( "none empty" );
        panel.add ( nameWidget );

        fileNameWidget = new StringFieldWidget ( "Имя файловой директории", false, 128, 320 );
        fileNameWidget.setTitleWidth ( width );
        fileNameWidget.setEditable ( editFileName );
        panel.add ( fileNameWidget );

        addToNorth ( panel );

        pack ();
    }

    @Override
    public boolean validateData ()
    {
        boolean result;
        String  str;

        result  = true;
        str     = nameWidget.getValue();
        str     = str.trim();
        if ( str.isEmpty() )
        {
            result  = false;
            addValidateErr ( "Не задано название раздела." );
        }
        else
        {
            str = fileNameWidget.getValue();
            str = str.trim();
            if ( str.isEmpty() )
            {
                result  = false;
                addValidateErr ( "Не задано имя файловой директории раздела." );
            }
        }

        return result;
    }

    protected void createDialogSize ()
    {
    }

    @Override
    public void doClose ( int closeType )
    {
    }

    @Override
    public void init ( Section initSection ) throws WEditException
    {
        nameWidget.setValue ( initSection.getName() );
        fileNameWidget.setValue ( initSection.getFileName() );
    }

    @Override
    public Section getResult () throws WEditException
    {
        Section section;

        section = new Section ( nameWidget.getValue(), project );
        section.setFileName ( fileNameWidget.getValue() );
        
        return section;
    }

}
