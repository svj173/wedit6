package svj.wedit.v6.gui.listener;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.TreeObjType;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.WDumpTools;

import javax.swing.event.TreeSelectionEvent;


/**
 * Акция выборки объекта в дереве.
 * <BR/>  Конечная точка исключений.
 * <BR/>  Ошибки отображаются GUI окном.
 * <BR/>  Действия (см UML модель данного процесса)
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 04.08.2011 12:06:05
 */
public class TreeObjSelectionListener extends WTreeSelectionListener
{
    /* старое состояние дерева - для rollback */
    private TreeObj     obj;
    
    private TreePanel treePanel;


    public TreeObjSelectionListener ( String name )
    {
        super ( name );
        obj = null;
    }

    /**
     * Смена объекта на дереве.
     * <BR/> Вызывается при любом обращении к дереву, в том числе и программно - setSelected(0)
     * <BR/> Если ошибка открытия - должна оставаться на этом (новом) месте, но с сообщением об ошибке - на рабочей панели или в поле статуса.
     */
    @Override
    public void handleAction ( TreeSelectionEvent event ) throws WEditException
    {
        Log.l.info ( "TreeObjectSelectionListener.handleAction: Start" );

        TreeObjType objType;
        TreeObj     currentObj;

        currentObj      = null;

        try
        {
            Log.l.debug ( "TreeObjectSelectionListener.handleAction: event = ", event );

            // Взять выбранный объект
            //  - По идее - надо брать из event. Но в этом случае закрытие (свертывание) узла дерева отрабатывается неверно
            //currentObj = (TreeObj) event.getPath().getLastPathComponent();
            currentObj = getTreePanel().getCurrentObj();

            Log.l.debug ( "TreeObjectSelectionListener.handleAction: selected tree Obj = ", WDumpTools.printTreeObj ( currentObj ) );

            if ( currentObj == null ) return;

            // получить полный тип выбранного объекта - тип+подтип+версия
            objType = currentObj.getType();

            // Активировать панель из карт
            getTreePanel().goTo ( objType, currentObj );

            // сохранить значение - для возможного отката
            obj = currentObj;

            Log.l.debug ( "TreeObjectSelectionListener.handleAction: obj for rollback = ", obj );

        } catch ( Exception ex )        {
            // вернуться на старый объект  - НЕТ. Выводим что можем у нового + сообщение об ошибке.
            //rollback();
            Par.GM.setStatus ( "Ошибка перехода на объект : ", ex.getMessage() );
            Log.l.error ( Convert.concatObj ( "TreeObjectSelectionListener.handleAction: Error. currentObj = '", currentObj, "'" ), ex);
            throw new WEditException ( ex, "Ошибка обработки выбранного объекта дерева : ", ex.getMessage() );
        }
        Log.l.info ("TreeObjectSelectionListener.handleAction: Finish");
    }


    /**
     * Вернуться на предыдущий объект дерева.
     * <BR/> Осуществляется при ошибке перехода на новый объект.
     */
    protected void rollback ()
    {
        Log.l.debug ( "TreeObjectSelectionListener.rollback: Start." );

        Log.l.debug ( "TreeObjectSelectionListener.rollback: old obj for rollback = ", obj );
        if ( obj == null ) return;
        try
        {
            getTreePanel ().goTo ( obj.getType(), obj );
        } catch ( Exception e )         {
            Log.l.error ( "TreeObjectSelectionListener.rollback: err ", e );
        }

        Log.l.debug ( "TreeObjectSelectionListener.rollback: Finish" );
    }

    /* Сообщение об окончании акции */
    protected String getOkMessage ()
    {
        return Convert.concatObj ( "Выбран объект '", getTreePanel ().getCurrentObj(), "'" );
    }

    public void setTreePanel ( TreePanel treePanel )
    {
        this.treePanel = treePanel;
    }

    public TreePanel getTreePanel ()
    {
        return treePanel;
    }
    
}

