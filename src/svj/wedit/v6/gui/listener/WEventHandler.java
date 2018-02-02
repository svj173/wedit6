package svj.wedit.v6.gui.listener;


import svj.wedit.v6.Par;
import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.WPair;
import svj.wedit.v6.obj.WorkResult;
import svj.wedit.v6.tools.Convert;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;


/**
 * Общий класс листенеров.
 * <BR/> основная задача - ловить исключения и правильно их выводить (окно сообщения) - чтобы все остальные листенеры не мучались этой проблемой.
 * <BR/> устанавливает/сбрасывает режимы курсора - ожидание
 * <BR/> Ошибки выводит окном сообщения
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 04.08.2011 12:13:45
 */
public abstract class WEventHandler<T extends EventObject>
{
    /** Имя листенера - для различий в логах. */
    private String name;

    /** Swing компонента. Та, с которой работает конкретный листенер. */
    //private JComponent component;

    /* Флаг что акция отрабатывается. Применяется для вложенных акций, чтобы waitCursor и frame.rewrite запускались только один раз. */
    private static boolean startAction = false;

    /* Флаг - разрешено отработать акцию (FALSE) или нет (TRUE) */
    private boolean disableAction;

    /* Время начала операции */

    /* Родительская операция */

    private T event;

    /* Результат операции листенеров - для сообщения на ошибочной странице и для отображения конечной информации об операции в логах */
    private WorkResult operResult    = WorkResult.NOT_FOUND;


  // ------------------------------- abstract -------------------------------------

    // public - чтобы можно было дергать внутри других листенеров
    public abstract void handleAction ( T event ) throws WEditException;

    //protected abstract String getErrorMessage ( Exception ex );

    //protected abstract String getOkMessage ();

    //protected abstract String getStartMessage ();

    protected abstract String getCmd ( T e );



    //protected abstract Cursor waitCursorEnable ( T event );

    //protected abstract void waitCursorDisable ( T event, Cursor cursor );

    /* вернуться в исходное состояние при ошибке обработки - НЕ возвращаемся а остаемся на новом объекте.
       Иначе не сможем у него подредактирвоать Параметры.*/
    //protected abstract void rollback ();


    public WEventHandler ( String name )
    {
        this.name       = name;
        startAction     = false;
        disableAction   = false;
    }

    // Выдать сообщения о процессе работы для статус-панели
    // Эти три метода перенес сюда как перезаписываемые - тк их наличие избыточно.
    //  Т.е. возникла куча классов в которых прописаны данные методы с null выводом. - svj, 2010-10-28
    /* Ошибка акции */
    protected String getErrorMessage ( Exception ex )
    {
        return null;
    }

    /* Сообщение об окончании акции */
    protected String getOkMessage ()
    {
        return null;
    }

    /* Начало акции */
    protected String getStartMessage ()
    {
        return null;
    }

    /**
     * Обработать и перерисовать экран. Ошибки вывести в экране (конечная точка исключений).
     * @param e событие
     */
    public void handle ( T e )
    {
        Cursor  cursor = null;
        boolean myAction;
        String  str, actionParent;
        long    startTimeMsec;

        Log.l.info ( "\t---------->>>----- WEventHandler.handle (%s): Start [%s]  ---------->>>-----",getName(),getCmd(e));
        //Log.l.debug ( "WEventHandler.handle ("+name+"): <M-01>");

        if ( disableAction )
        {
            Log.l.info ( "\t------<<<----- WEventHandler.handle (%s): Finish by DISABLE [%s]------<<<-----",getName(),getCmd(e) );
            return;
        }
        //Log.l.debug ( "WEventHandler.handle ("+name+"): <M-05>");

        cursor          = null;
        startTimeMsec   = System.currentTimeMillis ();
        event           = e;

        //str             = getStartMessage();
        //Par.GM.setStatus ( str );

        //Log.l.debug ( "WEventHandler.handle ("+name+"): <M-08>");
        if ( ! startAction )
        {
            startAction     = true;
            myAction        = true;
            actionParent    = getName();
        }
        else
        {
            myAction        = false;
            actionParent    = "ROOT";
        }
        //Log.l.debug ( "WEventHandler.handle ("+name+"): <M-10>");
        Log.l.info ("WEventHandler.handle (%s): startAction = %s, myAction = %s",getName(), startAction, myAction );
        //Log.l.debug ("WEventHandler.handle ("+name+"): actionParent = " + actionParent );

        // запомнить исходное состояние фрейма (надо ли? есть вероятность никогда не выйти из режима редактирования)
        //GCons.SystemState state = Par.STATE; // лишнее

        try
        {
            // навесить на курсор паузу                     -----
            if ( myAction ) cursor = waitCursorEnable4(e);

            // обработать событие
            handleAction (e);

            Par.GM.setStatus ( getOkMessage() );

            operResult    = WorkResult.OK;

        } catch ( Exception ex )        {
            // -------------- Конечная точка исключений. ----------------
            //Par.STATE = state;
            str = getErrorMessage(ex);
            if ( str == null )  str = ex.getMessage();
            Par.GM.setStatus ( str );
            // установить статус результата работы - Ошибка/Инфо и вывести окно с сообщением
            parseError ( ex );
            //rollback();
        } finally {
            if ( myAction )
            {
                // в любом случае
                // - перерисовать весь фрейм
                //if ( Par.WEDIT_STARTED ) Par.GM.rewrite();
                Par.GM.rewrite();  // флаги учитываются внутри
                /*
                try
                {
                    Log.l.info ( "--------- WEventHandler.handle (%s): run REWRITE Frame ---------",getName() );
                    Par.GM.rewrite();

                } catch ( Exception ge )            {
                    Log.l.error ( Convert.concatObj (  "WEventHandler.handle (",name,"): Frame REWRITE ERROR" ), ge);
                    JOptionPane.showMessageDialog ( Par.GM.getFrame(), "Ошибка перерисовки фрейма :\n" + ge.getMessage(),
                                                    "Ошибка",  JOptionPane.ERROR_MESSAGE );
                }
                */

                // - вернуть курсор
                waitCursorDisable4 ( e, cursor );

                // сброс в последнюю очередь - тк и при rewrite могут вызываться акции (например - tree.rewrite)
                //myAction = false;
                startAction = false;
            }
            long timeMsec = System.currentTimeMillis() - startTimeMsec;
            //if ( Par.GM.getCurrentObj() != null )                str = Par.GM.getCurrentObj().getFullType();
            //else                                                 str = "object is null";
            str = "Проект/Книга/Глава: ...";
            Log.l.info (  "TIME: %s; %s; %s; msec = %d; actionParent = %s; operResult = %s", str, getName(), getCmd(e), timeMsec, actionParent, operResult );
            Par.GM.setTime ( timeMsec );
        }

        Log.l.info ( "\t------<<<----- WEventHandler.handle (%s): Finish [%s]------<<<-----",getName(),getCmd(e) );
    }

    private void parseError ( Exception ex )
    {
        if ( (ex instanceof WEditException) && ( (WEditException) ex).isInfo() )
        {
            // это информационное сообщение
            operResult    = WorkResult.INFO;
            Log.l.info ( "WEventHandler.handle(%s): handle action release info = %s",getName(), ex.getMessage() );
            JOptionPane.showMessageDialog ( Par.GM.getFrame(), ex.getMessage(), "Сообщение",  JOptionPane.INFORMATION_MESSAGE );
        }
        else
        {
            int codeError = -1;
            String msg  = Convert.concatObj ( "WEventHandler.handle (",getName(),"): handle action ERROR = ", ex.getMessage() );
            // это ошибка
            // - значение для логгера уровня TIME
            operResult    = WorkResult.ERROR;
            if  (ex instanceof WEditException)
            {
                WEditException ge = (WEditException) ex;
                codeError   = ge.getCode();
            }
            //if ( codeError == EltexException.SNMP_TIMEOUT )                Log.l.info ( msg );  // Timeout
            //else
                Log.l.error ( msg, ex );
            // - писк
            Toolkit.getDefaultToolkit().beep();
            // todo - описать глобальные параметры для вывода сообщения на экран информационной панели.
            // - окно
            JOptionPane.showMessageDialog ( Par.GM.getFrame(), ex.getMessage(), "Ошибка",  JOptionPane.ERROR_MESSAGE );
        }
    }

    protected void waitCursorDisable ( EventObject event, Cursor cursor )
    {
        Component   component;
        Object      source;

        //Log.l.debug (" -x-x-x- WEventHandler.waitCursorDisable ("+name+"): Start" );
        /*
        Par.FRAME.setCursor ( GCons.WORK_CURSOR );
        Par.GM.getTreePanel().getTree().setCursor ( GCons.WORK_CURSOR );
        Par.FRAME.repaint();
        Par.GM.getTreePanel().getTree().repaint ();
          */

        if ( cursor == null ) return;

        source = event.getSource();
        //if ( source instanceof JButton ) source = Par.GM.getWorkPanel().getCurrent();
        if ( source instanceof JButton ) source = Par.GM.getFrame();
        //Log.l.debug (" -yyy- WEventHandler.waitCursorDisable ("+getName()+"): source = " + source );

        // Если источник - итем popup меню - взять фрейм для ожидания крусора
        //if ( source instanceof JMenuItem )  source = Par.GM.getTreePanel().getTree();

        if ( source instanceof Component )
        {
            //Log.l.debug ("WEventHandler.waitCursorDisable ("+getName()+"): disable WAIT" );
            component = (Component) source;
            component.setCursor ( cursor );
            component.repaint();
            Log.l.debug (" -x-x-x- WEventHandler.waitCursorDisable (",name,"): SET. component name = ", component.getName() );
        }

        //Log.l.debug (" -x-x-x- WEventHandler.waitCursorDisable ("+name+"): Finish" );
    }

    /**
     * Установить курсор в режим ожидания, предварительно сохранив исходное состояние курсора на данном объекте.
     * <BR/> todo Может здесь необходимо установить wait на все видимые Панели? И ссылки на них сохранить в буфере, чтобы потом скинуть?
     * @param event  Событие, из которого берется компонент, курсором которого и будем манипулировать.
     * @return       Исходный курсор
     */
    protected Cursor waitCursorEnable ( final EventObject event )
    {
        Cursor result = null;
        final  Component component;
        Object source;

        //Log.l.debug ("WEventHandler.waitCursorEnable ("+name+"): Start" );
        /*
        Par.GM.getTreePanel().getTree().setCursor ( GCons.WAIT_CURSOR );
        Par.FRAME.setCursor ( GCons.WAIT_CURSOR );
        Par.FRAME.repaint();
        Par.GM.getTreePanel().getTree().repaint ();
    
        result = GCons.WORK_CURSOR;
        */

        // console - jTree + Frame
        source = event.getSource();   // wait исчезает при работе с кнопками панели - перейти на ОНТ, обновить и т.д.
        //source = Par.GM.getTreePanel().getTree();  // wait не устанавливеатся при рабоет  с деревом и с кнопками.
        //source = Par.GM.getTreePanel();  // wait висит на дереве - и после операции
        //source = Par.GM.getFrame();   // тогда wait висит после выполнения операции (выборка в дереве)
        Log.l.debug (" -xxx- WEventHandler.waitCursorEnable (",name,"): Start. source = ", source.getClass().getName() );
        if ( source instanceof JButton ) source = Par.GM.getFrame();

        // Если источник - итем popup меню - взять фрейм для ожидания крусора   -- getCurrentPanel
        //if ( source instanceof JMenuItem )  source = Par.GM.getTreePanel().getTree();

        if ( source instanceof Component )
        {
            //Log.l.debug ("WEventHandler.waitCursorEnable ("+name+"): SET Wait cursor" );
            //Log.l.debug (" -xxx- WEventHandler.waitCursorEnable ("+name+"): source 2 = " + source.getClass().getName() );
            component   = (Component) source;
            result      = component.getCursor();
            component.setCursor ( WCons.WAIT_CURSOR );
            component.repaint();
            Log.l.debug (" -xxx- WEventHandler.waitCursorEnable (",name,"): SET. component name = ", component.getName() );
        }

        //Log.l.debug (" -xxx- WEventHandler.waitCursorEnable ("+name+"): Finish" );
        return result;
    }



    private Collection<WPair<Component,Cursor>> waitComponents    = new ArrayList<WPair<Component,Cursor>> ();

    private void setWait ( Component comp )
    {
        if ( comp == null ) return;

        Cursor cursor = comp.getCursor();
        //if ( cursor)
        waitComponents.add ( new WPair<Component,Cursor>(comp, cursor ) );
        comp.setCursor ( WCons.WAIT_CURSOR );
        comp.repaint();
    }


    protected void waitCursorDisable4 ( EventObject event, Cursor cursor )
    {
        Component   component;
        Object      source;

        for ( WPair<Component,Cursor> pair : waitComponents )
        {
            //pair.getParam1().setCursor ( pair.getParam2() );
            component = pair.getParam1 ();
            component.setCursor ( WCons.WORK_CURSOR );    // HAND_CURSOR
            component.repaint();
        }
        waitComponents.clear();
    }

    protected Cursor waitCursorEnable4 ( final EventObject event )
    {
        Cursor result = null;
        Object source;

        waitComponents.clear();
        setWait ( Par.GM.getFrame() );
        /* todo
        setWait ( Par.GM.getMenuBar() );
        setWait ( Par.GM.getToolBarPanel() );
        setWait ( Par.GM.getObjectHeaderPanel() );
        setWait ( Par.GM.getSearchPanel() );
        setWait ( Par.GM.getWorkPanel().getCurrent() );
        setWait ( Par.GM.getStatusPanel() );
        */

        source = event.getSource();
        if ( source instanceof Component )  setWait ( (Component) source );

        //Log.l.debug (" -xxx- WEventHandler.waitCursorEnable4 ("+name+"): Finish" );
        return result;
    }

    public String getName ()
    {
        return name;
    }

    public void setDisableAction ()
    {
        disableAction = true;
    }

    public void setEnableAction ()
    {
        disableAction = false;
    }

    public boolean isDisableAction()
    {
        return disableAction;
    }

    public T getEvent ()
    {
        return event;
    }
    
}
