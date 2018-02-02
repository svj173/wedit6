package svj.wedit.v6.function.project;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.SystemErrorException;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.progressBar.ResponseObject;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.msg.Msg;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.WPair;
import svj.wedit.v6.obj.function.multi.MultiActionViewMode;
import svj.wedit.v6.obj.function.multi.MultiFunction;
import svj.wedit.v6.obj.function.multi.ProgressBarType;
import svj.wedit.v6.tools.FileTools;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;


/**
 * Архивировать все открытые сборники - Акция по нажатию кнопки, т.е. диалоги переспроса допустимы.
 * <BR/> Выводит статистические данные - список сборников, размеры.
 * <BR/>
 * <BR/> Т.е. если какой-то сборник (проект) не был открыт, то он в архив НЕ попадет.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 05.11.2012 14:36:45
 */
public class ZipAllProjectsFunction extends MultiFunction //Function
{
    private StringBuilder       msg;
    private int                 ic;

    public ZipAllProjectsFunction ()
    {
        super();

        setId ( FunctionId.ZIP_ALL_PROJECTS );
        setName ( "Архивировать все Сборники" );
        setMapKey ( "Ctrl/A" );
        setIconFileName ( "zip.png" );
        setDialogSize ( new Dimension ( 800, 300 ) );

        msg = new StringBuilder(512);
    }

    protected ResponseObject backgroundMultiActionProcess ()
    {
        ResponseObject      result;
        Collection<Project> projects;
        WPair<Long,String>  zipResult;

        Thread.currentThread().setName ( "ZipAllProjects" );

        result = new ResponseObject();

        try
        {
            setMultiActionMode ( Msg.getMessage("system.gui.dialog.addfile.addnewfile"), Par.GM.getFrame().getProjects().size(), false, MultiActionViewMode.ALL );

            //msg = new StringBuilder(512);
            msg.setLength ( 0 );

            msg.append ( "<HTML><body>\n<h2>Сохраненные сборники</h2>\n" );

            // Взять все открытые сборники.
            projects   = Par.GM.getFrame().getProjects();

            msg.append ( "<table border='1' cellpadding='0' cellspacing='0'><tr bgcolor='#BBBBBB'><th>&nbsp;N&nbsp;</th><th>&nbsp;Название&nbsp;</th><th>Имя файла</th><th>ZIP файл</th><th>Размер</th></tr>\n" );
            ic          = 1;

            Log.l.debug ( "ZIP: BG Start. projects = %s", projects );

            // Настраиваем общий прогресс-бар - выставляем кол-во акций
            initTotalProgressBar ( projects.size() );

            Log.l.debug ( "ZIP: <M01>. dialog = %s", getDialog() );
            for ( Project project : projects )
            {
                Log.l.debug ( "--- ZIP: project = %s", project );
                initProcessProgressBar ( ProgressBarType.UNTIME, 0, 0, project.getName() );

                msg.append ( "<tr><td align='right'>&nbsp;" );
                msg.append ( ic );
                msg.append ( "&nbsp;</td><td><font color='green'>&nbsp;" );
                msg.append ( project.getName() );
                msg.append ( "&nbsp;</font></td><td><font color='blue'>&nbsp;" );
                // -- /home/svj/Projects/SVJ/wedit-6/test/test_sb  - здесь находится project.xml и subdir по темам
                msg.append ( project.getProjectDir().toString() );
                // - сохранить и взять размер архива
                msg.append ( "&nbsp;</font></td><td>&nbsp;" );

                zipResult = FileTools.zipProject ( project );
                msg.append ( zipResult.getParam2() );
                msg.append ( "&nbsp;</td><td align='right'>&nbsp;" );
                msg.append ( zipResult.getParam1() );
                msg.append ( "&nbsp;</td></tr>\n" );

                ic++;

                processMsg ( "File: ", zipResult.getParam2(), "; Size: ", zipResult.getParam1() );

                // Увеличиваем кол-во выполненных заданий.
                incTotalProgressBar();
                // счет времени на прогресс-баре начинаем вести с самого начала.
                setCurrentProcessCounter ( 0 );
            }

            msg.append ( "</table><br/></body></HTML>\n" );

            Log.l.debug ( "ZIP: BG Finish" );

            //label   = new JLabel ( msg.toString() );
            //DialogTools.showMessage ( Par.GM.getFrame(), label, Convert.concatObj ( "Сохранение ", (ic-1), " сборников" ) );

        } catch ( WEditException ee )  {
            Log.l.error ( "ZipAllProjectsFunction.backgroundMultiActionProcess: error", ee );
            processError ( ee );
            result.setException ( ee );
        } catch ( Exception e )  {
            Log.l.error ( "ZipAllProjectsFunction.backgroundMultiActionProcess: error", e );
            processError ( e );
            result.setException ( new SystemErrorException ( e, Msg.getMessage ( "system.gui.error.filecopyerror" ), " : ", e ) );
        }

        Log.l.debug ( "ZipAllProjectsFunction.backgroundMultiActionProcess: Finish. result = %s", result );
        return result;
    }

    protected  void afterHandle ( ActionEvent event ) throws WEditException
    {
        /*
        JLabel              label;
        label   = new JLabel ( msg.toString() );
        DialogTools.showMessage ( Par.GM.getFrame (), label, Convert.concatObj ( "Сохранение ", ( ic - 1 ), " сборников" ) );
        */
    }


    @Override
    public String getToolTipText ()
    {
        return "Архивировать все Сборники (Ctrl/A)";
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

}