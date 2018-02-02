package svj.wedit.v6.obj.function;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.Issue;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionGroup;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.params.ParameterCategory;
import svj.wedit.v6.function.params.SimpleParameter;
import svj.wedit.v6.gui.button.EmptyButton;
import svj.wedit.v6.gui.icon.MenuIconSizeGetter;
import svj.wedit.v6.gui.menu.WEMenuItem;
import svj.wedit.v6.gui.menu.WIconMenuItem;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.manager.KeyMapManager;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.DialogTools;
import svj.wedit.v6.tools.GuiTools;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;


/**
 * Основной класс функций.
 * На события функции можно подписываться другим функциям.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 15.07.2011 16:02:08
 */
public abstract class Function implements ActionListener
{
    /** ИД функции. */
    private FunctionId      id;

    /** названия функции. */
    private String      name;

    /** Клавиши для мапирования вызова данной функции. */
    private String      mapKey      = null;

    /** Тип функции: content, chapter, common, system. */
    private String      type;

    /** Место расположения функции в Меню Редактора. */
    //private String      menuPath    = null;
    //private String      menuGroup    = null;

    //private String      icon        = null;

    private Map<String, svj.wedit.v6.function.params.FunctionParameter> params; //      = new HashMap<String, FunctionParameter>();

    /** Событие, которое выдается подписчикам данной функции. */
    private Object      eventObject = null;


    /** Собираемый список нефатальных ошибок. */
    private Issue       error       = new Issue ( );


    /**
     * Функциональная категория функции. Например: работа с деревом оглавления.
     * Пока используется для массовых подписок, например: UndoRedo хочет
     * подписаться на события всех функций, относящихся к работе с деревом. Тогда все функции,
     * работающие с деревом, должны входить в эту группу.
     */
    private FunctionGroup functionGroup    = null;

    /**
     * Противовес для functionGroup. Указатель функции на то, на события какой группы она хочет подписаться.
     */
    private FunctionGroup listenerGroup    = null;

    /** Тип параметров функции. Чтобы знать, в какой файл сохранять. */
    private ParameterCategory paramsType   = null;

    //private String listenerCmd    = null;

    /**
     * Список слушателей, которые подписались к событиям данной функции.
     * Каждый слушатель имеет свою собственную ActionCommand.
     *  Ключ - собственно слушатель. Значение - ActionCommand.
     * ActionCommand у разных слушателей могут совпадать, в то время
     * как двух одинаковых слушателей в списке быть не может.
     * При активации слушателей им передается ActionEvent, где Source - какой-то свой обьект (любой для разных групп), а ActionCommand - тот, который передала функция, которая подписалась.
     */
    //private Hashtable<Function,String>   listenerList    = new Hashtable<Function,String>();
    private Collection<Function> listenerList    = new ArrayList<Function>();

    /* Флаг - функция поднялась.*/
    private boolean started = false;

    /* Короткое имя файла иконки. Например: save.png */
    private String iconFileName;


    /**
     * Собственно обработчик функционала.
     * Тоже в принципе можно сделать необязательным методом (как init, close).
     * @param event        Событие
     * @throws WEditException   err
     */
    public abstract void handle ( ActionEvent event )     throws WEditException;

    /**
     * Обновить функцию. Например, в связи с изменением значений ее
     *  параметров (например, надо перерисовать меню).
     */
    public abstract void rewrite();

    /**
     * Начальная установка функции на этапе инсталляции Редактора.
     *  Если ошибка - то прерывание и выход.
     * Здесь действия касаются только самой функции, т.к. еще
     * не все модули Редактора и функции установлены.
     * @throws WEditException  Ошибка инициализации функции
     */
    public abstract void init()      throws WEditException;

    /**
     * Завершение работы. Завершается без прерываний. Если все-таки есть
     *  ошибки - просто скидываются в журнал.
     */
    public abstract void close();

    public abstract String getToolTipText ();
    
    //public abstract void reinit ( String paramName, String... params );


    public void actionPerformed ( ActionEvent e )
    {
        Log.l.debug ( "\n\nStart. =========== V ============== actionPerformed.Start: function (%s) ==============V ===========",getId() );
        try
        {
            // Сбросить старый обьект события - иначе будет бесконечный цикл - processEvent
            setEventObject ( null );

            // Обработать данное событие
            handle ( e );

        } catch ( Exception ex )         {
            Log.l.error ( "Error", ex );
            // Вывести сообщение об ошибке - только если не было отмены работы функции (cancel)
            if ( ex instanceof WEditException )
            {
                WEditException we = (WEditException) ex;
                if ( ! we.isCancel() )
                {
                    // Если это не CANCEL - выдать сообщение об ошибке.
                    DialogTools.showError ( we.getMessage(), "Ошибка" );
                }
            }
            else
            {
                // Выдать на экран - системная ошибка + стэк-трэйс
                DialogTools.showError ( ex.getMessage(), "Ошибка" );
            }
        } finally {
            // Разослать подписчикам
            Log.l.debug ( "============ processEvent: function (%s) ============",getId() );
            processEvent();
            // перерисовать фрейм
            //Par.GM.rewrite();
            //if ( Par.WEDIT_STARTED ) Par.GM.rewrite();
            Par.GM.rewrite();  // флаги учитываются внутри
        }
        Log.l.debug ( "Finish. ============ ^ ============= actionPerformed.Finish: function (%s) =============== ^ ==========\n\n",getId() );
    }

    /**
     * Если есть слушатели - сгенерить для всех событие.
     */
    private void processEvent ()
    {
        ActionEvent     event;
        int             id;
        FunctionGroup   cmd;
        Object          objEvent;

        objEvent    = getEventObject();
        if ( objEvent == null )
        {
            //Log.l.error ( "This EventObject is NULL" );
            return;
        }

        cmd = getFunctionGroup();

        for ( Function listenerFunction : listenerList )
        {
            id     = (int) System.currentTimeMillis ();
            event  = new ActionEvent ( objEvent, id, cmd.toString() );
            try
            {
                // кидает на самый верхний уровень - чтобы переподписка выполнялась сколько нужно раз а не только один (переподписка - только в акции actionPerformed)
                listenerFunction.actionPerformed ( event );
            } catch ( Exception e )      {
                // Это внутренние проблемы слушателя
                Log.l.error ( Convert.concatObj ( "Event listener error. Listener = ", listenerFunction, ", event = ", event.getSource(),
                        "/", event.getActionCommand() ), e );
            }
        }
    }

    public String getName ()
    {
        return name;
    }

    public void setName ( String nameKey )
    {
        this.name = nameKey;
    }

    public String getType ()
    {
        return type;
    }

    public void setType ( String type )
    {
        this.type = type;
    }

    public String toString()
    {
        StringBuilder result;
        result  = new StringBuilder ( 64 );
        result.append ( "[ Function: id = " );
        result.append ( id );
        result.append ( ", name = " );
        result.append ( getName() );
        result.append ( ", type = " );
        result.append ( type );
        //result.append ( ", params = " );
        //result.append ( params );
        result.append ( " ] " );
        return result.toString ();
    }

    /**
     * Стандартный элемент Главного меню.
     * <BR> Метод переписывается, если требуется создать что-либо нестандартное.
     * @param cmd  Команда, передаваемая извне.
     * @return  Обьект меню (JComponent).
     */
    public JComponent getMenuObject ( String cmd )
    {
        String      str;
        WEMenuItem  item;

        item    = new WIconMenuItem ( this, new MenuIconSizeGetter() );     // иконка
        //item    = new WEMenuItem ( getName(), getIcon ( Par.MENU_ICON_SIZE ) );     // иконка
        //item.addActionListener ( this );
        //item.setActionCommand ( cmd );
        // map key
        // Обработать клавиши мапирования
        str     = getMapKey();
        if ( str != null )
        {
            KeyMapManager.getInstance().treatKeyMap ( item, str, this );
        }

        return item;
    }

    public JComponent getToolBarObject()
    {
        return GuiTools.createIconButton ( this, Par.TOOLBAR_ICON_SIZE );
    }

    public void addListener ( Function listenerFunction )
    {
        listenerList.add ( listenerFunction );
    }

    /**
     * Используется только если данная функция генерит события
     *  слушателям, которые на нее подписались. Иначе - NULL.
     * @return
     */
    public Object getEventObject ()
    {
        return eventObject;
    }

    public void setEventObject ( Object eventObject )
    {
        this.eventObject = eventObject;
    }

    public FunctionGroup getFunctionGroup ()
    {
        return functionGroup;
    }

    public void setFunctionGroup ( FunctionGroup mode )
    {
        if ( mode != null ) functionGroup = mode;
    }

    public FunctionGroup getListenerGroup ()
    {
        return listenerGroup;
    }

    public void setListenerGroup ( FunctionGroup listenerGroup )
    {
        if ( listenerGroup != null ) this.listenerGroup = listenerGroup;
    }

    public boolean isStarted ()
    {
        return started;
    }

    public void setStarted ( boolean started )
    {
        this.started = started;
    }

    /**
     * Выдать список имен функций от которых зависит инициализация данной функции.
     * Если имен нет - список должен быть пустым.
     * Имена тримятся. Если имя пустое - не заносить в список.
     * @return
     */
    public List getFirstStartFunctions ()
    {
        //if ( firstStartFunctions != null ) return firstStartFunctions;
        //else 
            return new ArrayList();
    }

    public FunctionId getId ()
    {
        return id;
    }

    public void setId ( FunctionId id )
    {
        this.id = id;
    }

    public String getMapKey ()
    {
        return mapKey;
    }

    public void setMapKey ( String mapKey )
    {
        this.mapKey = mapKey;
    }

    public Issue getError ()
    {
        return error;
    }

    public void addIssue ( Issue issue )
    {
        //Issue
        error.add ( issue );
    }

    public void addError ( Object ... msg )
    {
        Issue  issue;
        issue   = new Issue ( Convert.concatObj ( msg ) );
        error.add ( issue );
    }

    /*
    public void initParams ()
    {
        setParams ( Par.GM.getConfig().getFunctionData ( getId() ) );
    }
    */


    public Map<String, svj.wedit.v6.function.params.FunctionParameter> getParams ()
    {
        return params;
    }

    public void setParams ( Map<String, svj.wedit.v6.function.params.FunctionParameter> params )
    {
        this.params = params;
    }

    public svj.wedit.v6.function.params.FunctionParameter getParameter ( String name )
    {
        if ( params != null )
            return params.get ( name );
        else
            return null;
    }

    public void setParameter ( String name, svj.wedit.v6.function.params.FunctionParameter param )
    {
        if ( params == null )   params = new HashMap<String, svj.wedit.v6.function.params.FunctionParameter> ();
        params.put ( name, param );
    }

    public void setParameter ( svj.wedit.v6.function.params.FunctionParameter param )
    {
        if ( params == null )   params = new HashMap<String, svj.wedit.v6.function.params.FunctionParameter> ();
        params.put ( param.getName(), param );
    }

    protected SimpleParameter getSimpleParameter ( String paramName, String defaultValue )
    {
        SimpleParameter sp;

        sp  = (SimpleParameter) getParameter ( paramName );
        if ( sp == null )
        {
            sp  = new SimpleParameter ( paramName, defaultValue ); // дефолтное значение
            sp.setHasEmpty ( false );
            setParameter ( paramName, sp );
        }

        return sp;
    }


    public boolean containGroup ( FunctionGroup fGroup )
    {
        if ( functionGroup == null )
            return false;
        else
            return functionGroup == fGroup;
    }

    /**
     * Последняя стадия инсталляции Редактора.
     * <BR> Здесь функции осуществляют действия со сторонними обьектами - другими функциями и модулями
     *  Редактора, т.к. все они к этому моменту уже проинициализированы, и находятся в боевом состоянии.
     * <BR> Загружают данные из файлов, загружаются динамические параметры и заносятся новые значения в функции и т.д.
     * <BR> При ошибке генерит исключение, т.к. ошибки на этой стадии существенно влияют на работу Редактора.
     * @throws WEditException  Ошибка старта функции
     */
    public void start()      throws WEditException     {}


    /**
     * Выдать ГУи компоненту, отображающую данную функцию.
     * @param height  Обязательная высота компоненты.
     * @return  Компонента для гуи-отображения (компоненты, расположенные над панелями).
     */
    public JComponent getGuiComponent ( int height )
    {
        String      iconFileName;
        EmptyButton iconButton;

        iconFileName    = getIcon(height);
        if ( iconFileName != null )
        {
            // создать иконку согласно заданному размеру
            iconButton  = GuiTools.createIconButton ( this, height );

            /*
            ImageIcon   icon;
            iconButton      = new  EmptyButton();
            icon    = GuiTools.createIcon ( iconButton, iconFileName, this );

            iconButton.addActionListener ( this );

            iconButton.setIcon ( icon );

            iconButton.setToolTipText ( getToolTipText() );
            iconButton.setFocusable ( false );
            */
        }
        else
        {
            iconButton  = null;
        }

        return iconButton;
    }

    public String getIcon ( int iconSize )
    {
        return Convert.concatObj ( "img/function/",iconSize,"/", getIconFileName() );
    }

    public String getIconFileName ()
    {
        return iconFileName;
    }

    /**
     * Выдать путь до иконки данной функции - указанного размера (для оперативной смены набора иконок одного размера на другой.
     * <br/>
     * <br/> Изменение размеров:
     * <br/> imageIcon = new ImageIcon ( imageOld.getScaledInstance ( iNewSize, -1, Image.SCALE_DEFAULT ) );
     * <br/>
     * @param iconFileName  Короткое имя файла иконки.
     */
    public void setIconFileName ( String iconFileName )
    {
        this.iconFileName = iconFileName;
    }

    public boolean checkParams ( SimpleParameter sp, String paramName, String[] params )
    {
        if ( paramName == null )  return false;
        if ( params    == null )  return false;
        if ( params.length == 0 )  return false;
        if ( params[0] == null )  return false;
        if ( sp == null )  return false;

        return true;
    }

    /**
     * Выключить функцию.
     * <br/> Сделать ее неактивной, но отображаемой.
     * <br/> Применяется, например, когда при иснталляции редактора функции валидируются и запускаются -- в случае ошибок.
     * <br/> TODO Проблема в том что все меню и кнопки, ссылающиеся на данную фцункцию уже созданы, а здесь их  надо как-то найти и выключить.
     * <br/> Либо глобальный rewrite в конце инсталляции Редактора.
     * <br/>
     */
    public void disable ()
    {
    }

    public ParameterCategory getParamsType ()
    {
        return paramsType;
    }

    public void setParamsType ( ParameterCategory paramsType )
    {
        this.paramsType = paramsType;
    }

}
