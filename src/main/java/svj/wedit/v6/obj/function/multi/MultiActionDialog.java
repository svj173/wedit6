package svj.wedit.v6.obj.function.multi;


import svj.wedit.v6.Par;
import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.dialog.WDialog;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.tools.Convert;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * мульти-акция - применяется только тогда, когад на стороне гуи в однйо акции требуется несколкьо разных обращений через сокеты к разным командерам.
 * <BR/> Здесь все эти акции собираются в один прогресс-бар, в диалог с отображением шага работы и с возможностью прерывания работы.
 * <BR/>
 * <BR/> Входные параметры:
 * <BR/> 1) Флаг - нужна ли кнопка Отмена.
 * <BR/> 2) Флаг - надо ли показывать список выполняемых работ (с затраченным временем).
 * <BR/> 3) закрывать диалог после завершения работы.
 * <BR/>
 * <BR/> Важное:
 * <BR/> 1) Изменения в гуи-компонентах - производить только в awt-потоке.
 * <BR/>
 * <BR/> Отмена
 * <BR/> 1) Отрабатывается только при закрытии диалога. В методе закрытия проверяется флаг - работа завершена или нет. Если нет - отрабатывается Прерывание работы.
 * <BR/> 2) На акцию цепляется ИД.
 * <BR/> 3) По Отмена команда уходит на сервер. Там, в пуле командеров данного типа ищутся занятые командеры. И у них спрашивается ИД.
 * <BR/> 4) Если командер найден - в сокет кидается какой-то ответ - чтобы сокет закрылся на стороне гуи и не висел в ожидании. А командеру говорится - прервись.
 * <BR/> Если сможет - прервется, если нет - поработает вхолостую, сьедая какие-то ресурсы.
 * <BR/>
 * <BR/> А есть ситуации когда по Отмене диалог должен оставаться открытым? -- в том смысле что может по Отмена безусловно закрывать диалог?
 * <BR/>
 * <BR/> todo Проблемы - на каждую Отмену остается подвешенный swingWorker-процесс, хотя по логам видно что процесс дошел
 *   до своего завершения. А почему остался висеть - непонятно. Может, перейти на свой runnable? -- надо применять свой Excecutor
 * <BR/>
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 30.08.2013 12:19
 */
public class MultiActionDialog extends WDialog<Object,Object>
{
    /* Листенер, который запустил данную мульти-акцию - для получения от него информации - текущего commandId, кол-ва задач, флаг надо ли закрывать диалог и т.д. */
    private MultiFunction           multiFunction;

    private JProgressBar            processProgressBar, totalProgressBar;
    /* Поле отображения выполняемых акций. А также - сообщения об ошибках (подкрашивать красным). */
    private JTextArea               textArea;
    /* название текущего процесса. */
    private JLabel                  currentProcessNameLabel;

    /* Флаг состояния работы мультиакции - завершена (false), в работе (true).
     * На смаом деле служит для того чтобы функция Отмены операции не дергалась второй раз -- т.е. это флаг применения операции Отмена. */
    private boolean                 worked;
    /* Background процесс - здесь ссылка на него - только для прерывания. */
    private MultiActionSwingWorker  swingWorker;

    private JScrollPane             scrollPane;

    /* Обьект хранит (и накапливает) данные, которые используются для отображения в первом прогресс-баре (ход процесса). */
    private ProgressBarDataObject   dataObject;

    /* Акция, управляемая таймером - отрисовывает данные в прогресс-барах - каждую секунду. */
    private TimerAction             timerListener;
    /* Общий  секундный таймер - на все время мульти-акции. */
    private Timer                   timer;



    public MultiActionDialog ( Frame parent, MultiFunction multiFunction )
    {
        super ( parent, multiFunction.getTitle() );

        this.multiFunction = multiFunction;

        Log.l.debug ( "MultiActionDialog:: Start. multiFunction = %s; parent = %s", multiFunction, parent );

        JPanel      panel;
        Border      border;

        setName ( "MultiActionDialog_"+multiFunction.getName() );
        // Отключаем модальность, чтобы после dialog.showDialog() работа пошла дальше и в конце handleMultiAction можно было решать - стоит закрывать диалог или нет.
        //setModal ( false );

        // блокируем крестик (по нажатию ничего не происходит) - чтобы выход только по Отмена, по Ошибке или по Окончанию работы.
        setDefaultCloseOperation ( WindowConstants.DO_NOTHING_ON_CLOSE );   // почему-то не отрабатывает. возможно - надо создать свой класс диалога (лишняя сущность).


        dataObject          = new ProgressBarDataObject();

        // ---------- убрать служебные - Убрать вниз, Изменить размер, Закрыть -------------
        // убирает всю рамку окна (титл и кнопки - Убрать вниз, Изменить размер, Закрыть. Остаются только созданные компоненты
        //setUndecorated(true);
        // false - убирается кнопка Изменения размера
        setResizable ( true );
        // Убираем кнопку - Принять.
        //dialog.disableOkButton();

        panel = new JPanel();
        panel.setLayout ( new BoxLayout ( panel, BoxLayout.PAGE_AXIS ) );

        // ----------------- титл --------------------------
        /*
        label = new JLabel ( title );
        label.setForeground ( Color.GREEN );
        label.setHorizontalAlignment ( SwingConstants.CENTER );
        panel.add ( label );
        */

        // ------------------ пропуск ---------------------
        panel.add ( Box.createVerticalStrut ( 15 ) );

        // -------------- название текущего процесса -------------------
        currentProcessNameLabel = new JLabel();
        panel.add ( currentProcessNameLabel );

        // -------------------- первый прогресс-бар - текущий процесс --------------------
        // может быть таймером, может быть бесконечным
        processProgressBar = new JProgressBar ( 0, 2000 );
        // рисуем бесконечный бегунок вправо-влево
        //processProgressBar.setIndeterminate ( true );
        processProgressBar.setValue ( 0 );
        processProgressBar.setStringPainted ( true );
        // бордюр с отображением времени работы
        border = BorderFactory.createTitledBorder ( "00:00:00" );
        processProgressBar.setBorder ( border );

        panel.add ( processProgressBar );

        // ------------------ пропуск ---------------------
        panel.add ( Box.createVerticalStrut ( 15 ) );

        // пустой бордюр - т.е. просто отступы по краям.
        //border = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        // бордюр вокруг бегунка - c текстом
        //border = BorderFactory.createTitledBorder ( "Ожидание..." );
        //processProgressBar.setBorder ( border );

        // -------------------- второй прогресс-бар --------------------
        // может быть только ограниченным таймером - по кол-ву акций.
        //totalProgressBar = new JProgressBar ( 0, actionSize );
        totalProgressBar = new JProgressBar ( 0, multiFunction.getActionSize() );
        // НЕ бесконечный бегунок вправо-влево
        totalProgressBar.setIndeterminate ( false );
        totalProgressBar.setValue ( 0 );
        totalProgressBar.setStringPainted ( true );
        border  = BorderFactory.createTitledBorder ( "00:00:00" );
        totalProgressBar.setBorder ( border );
        panel.add ( totalProgressBar );

        // ------------------ пропуск ---------------------
        panel.add ( Box.createVerticalStrut ( 15 ) );

        // ------------------ Сообщения и ошибки ---------------------
        textArea    = new JTextArea();
        textArea.setEditable(false);
        scrollPane  = new JScrollPane ( textArea );
        panel.add ( scrollPane );

        // ------------------ пропуск ---------------------
        panel.add ( Box.createVerticalStrut ( 15 ) );

        addToCenter ( panel );

        // --------------------- Кнопки --------------------------
        // Выключаем кнопку ОК диалога.
        disableOkButton();

        setCancelButtonText ( "Прервать" );
        //setCancelButtonTooltip ( Msg.getMessage ( "system.gui.dialog.multiaction.interrupt" ) );
        //setCancelButtonTooltip ( "" );

        // Установка режима отображения гуи-элементов - все показывать, не все и т.д.
        //setViewMode ( viewMode );
        setViewMode ( multiFunction.getViewMode() );

        worked      = true;

        setPreferredSize ( multiFunction.getDialogSize() );
        pack();
    }

    private void setViewMode ( MultiActionViewMode viewMode )
    {
        int         width, height;
        Dimension   dim;

        switch ( viewMode )
        {
            case ONLY_ONE_PROGRESS_BAR:
                // Показать только один прогресс-бар - текущего процесса.
                totalProgressBar.setVisible ( false );
                currentProcessNameLabel.setVisible ( false );
                textArea.setVisible ( false );
                //Log.l.debug ( "MultiActionDialog.setViewMode: ONLY_ONE_PROGRESS_BAR -- textArea.getParent() = ", textArea.getParent() );  // parent is JViewPort
                //textArea.getParent().setVisible ( false );
                //textArea.getParent().validate();
                //textArea.getParent().repaint();
                scrollPane.setVisible ( false );
                //scrollPane.revalidate();
                //scrollPane.repaint();
                width   = 400;
                height  = 150;
                break;

            case ONLY_TOTAL_PROGRESS_BAR:
                processProgressBar.setVisible( false );

            default:
            case ALL :
                // Расчитать размер диалога исходя из размеров родительского фрейма
                dim     = Par.GM.getFrame().getPreferredSize();
                Log.l.debug ( "MultiActionDialog: frame PreferredSize = %s", dim );
                width   = (int) (dim.getWidth()  * 0.7);
                height  = (int) (dim.getHeight() * 0.7);
                Log.l.debug ( "MultiActionDialog: width = %d; height = %d", width, height );
                //setPreferredSize ( new Dimension ( width, height ) );
                break;
        }

        setPreferredSize ( new Dimension ( width, height ) );
    }

    @Override
    public void init ( Object initObject ) throws WEditException
    {
    }

    @Override
    public Object getResult () throws WEditException
    {
        return null;
    }

    public void addMsg ( String msg )
    {
        //textArea.setVisible ( true );    // Открываем поле, а то ошибку не увидят.
        //textArea.append ( "Ошибка : " );
        textArea.append ( msg );
        textArea.append ( "\n" );
        scrollPane.setVisible ( true );
        scrollPane.revalidate();
        scrollPane.repaint();
        textArea.repaint();
    }

    public void setErrorMsg ( String errorMsg )
    {
        processProgressBar.setFont ( WCons.ERROR_FONT );
        processProgressBar.setForeground ( Color.RED );
        processProgressBar.setString ( "Ошибка" );

        // Попытка навесить стиль (другой цвет текста) - но здесь документ без поддержки стилей -- можно применить JEditorPane вместо textArea.
        //textDocument    = textArea.getDocument();
        //Log.l.debug ( "MultiActionDialog.setErrorMsg: textDocument = ", textDocument );
        //iStart          = textDocument.getLength();

        //Log.l.debug ( "MultiActionDialog.setErrorMsg: processTitle = ", processTitle );
        //Log.l.debug ( "MultiActionDialog.setErrorMsg: textArea before size = ", textArea.getPreferredSize() );
        //Log.l.debug ( "MultiActionDialog.setErrorMsg: scrollPane before size = ", scrollPane.getPreferredSize() );
        // Текст ошибки -- делаем видимыми панель сообщения об ошибке - на случай если она была выключениа.
        textArea.setVisible ( true );    // Открываем поле, а то ошибку не увидят.
        textArea.append ( "Ошибка : " );
        textArea.append ( errorMsg );
        textArea.append ( "\n" );
        scrollPane.setVisible ( true );
        scrollPane.revalidate();
        scrollPane.repaint();
        textArea.repaint();

        // Подкрашиваем в красный цвет -- только для StyledDocument
        //textDocument.set

        // Меняем текст на кнопке закрытия диалога.
        setCancelButtonText ( "Закрыть" );

        // здесь видно что размеры увеличились.
        //Log.l.debug ( "MultiActionDialog.setErrorMsg: textArea after size = ", textArea.getPreferredSize() );
        //Log.l.debug ( "MultiActionDialog.setErrorMsg: scrollPane after size = ", scrollPane.getPreferredSize() );

        // меняем размер диалога - т.к. появился большой текст об ошибке. -- почему-то диалог не меняется по pack()..
        // - хотим чтобы применился естественный размер - что-то не проходит изменение по pack() без setPreferredSize().
        Dimension ps = getPreferredSize();
        //Log.l.debug ( "MultiActionDialog.setErrorMsg: dialog size before pack = ", getSize() );
        setPreferredSize ( new Dimension ( ps.width+ 50, ps.height + 100 ) );
        pack();
        Log.l.debug ( "MultiActionDialog.setErrorMsg: Finish. dialog size after pack = %d", getSize() );
    }

    /**
     * Метод дергается ВСЕГДА при нажатии на крестик диалога (в прикрученном CloseWindowListener).
     */
    @Override
    public void doClose ( int retStatus )
    {
        Log.l.debug ( "MultiActionDialog.doClose: Start. worked = %b; retStatus = %d", worked, retStatus );

        // Закрываем диалог
        super.doClose ( retStatus );

        switch ( retStatus )
        {
            case WCons.RET_CANCEL:    // Нажата Отмена или Крестик диалога -- 1
                // Смотрим состояние работы.
                if ( worked )
                {
                    // Процесс находится в работе - прерываем
                    // -- Переспрос -- Убрал
                    //ic = DialogTools.showConfirmDialog ( this, "Вы действительно желаете превать работу ?", "Прерывание работы" );
                    //if ( ic == 0 )
                    //{
                        // Прерываем работу рабочего процесса
                        //swingWorker.cancel ( true );
                        // отправить на сервер команду - прервать команду с ИД=commandId   -- лишнее. в другом месте отрабатывается
                        //sendCancelCommand ( maListener.getCommandId() );
                    //}
                    stopProcess();
                    //sendCancelCommand ( multiFunction.getCommandId() );
                }
                worked = false; // чтобы второй раз не дернулась Отмена операции.
                break;

            case WCons.RET_OK :  // Каким-то образом прилетело состояние ОК (Принять) -- 0 -- ничего не делаем.
            default:
                break;
        }
    }


    /**
     * Отправить на сервер команду - прервать команду с ИД=commandId
     * @ param commandId  ID текущей выполняемой команды.
     */
    /*
    private void sendCancelCommand ( String commandId )
    {
        Command  command;
        Function function;

        Log.l.debug ("MultiActionDialog.sendCancelCommand: Start. commandId = ", commandId );

        if ( commandId == null )
        {
            Log.l.error ( null, "MultiActionDialog.sendCancelCommand: error. commandId is NULL. Nothing do." );
        }
        else
        {
            // только для сокетного обработчика версии 2. Для 4 - все реализовано на уровне абстракции (кроме прерываний в конретных командерах сервера)
            if ( Par.SOCKET_VERSION == 2 )
            {
                function = new Function();
                // Заодно будет и ИД данного процесса.
                function.setProcessId ( commandId );

                command  = new Command ( ActionId.INTERRUPT_COMMAND, EmsConst.SYSTEM_FULL_OBJECT_TYPE, Par.USER, Par.SESSION_ID );
                command.setFunction ( function );

                try
                {
                    DataExchangeManager.getInstance().send ( command );
                } catch ( Exception ex ) {
                    Log.l.error ( ex, "MultiActionDialog.sendCancelCommand: error. commandId =  ", commandId );
                }
            }
            else
            {
                Log.l.error ( null, "MultiActionDialog.sendCancelCommand: interrupt error for commandId: ",commandId," - socket version is not 2 (", Par.SOCKET_VERSION, ")." );
            }
        }
        Log.l.debug ("MultiActionDialog.sendCancelCommand: Finish. commandId = ", commandId );
    }
    */

    public JProgressBar getProcessProgressBar ()
    {
        return processProgressBar;
    }

    public JProgressBar getTotalProgressBar ()
    {
        return totalProgressBar;
    }

    /**
     * Настроить прогресс-бар отображения работы текущего процесса.
     * <br/> Работа в awt-потоке. Получили из none-awt-потока. Работаем с gui-компонентами. Начальная настройка.
     * <br/> Дергается в bg-потоке в bg-методе swingWorker'a - process ( List<MultiData> list ).
     * <br/> здесь мы накапливаем инфу из List<MultiData> list в обьекте dataObject.
     * <br/>
     * @param data  Данные для настройки.
     */
    public void setupProcessProgressBar ( MultiData data )
    {
        Log.l.debug ( "--- MultiActionWaitingListener.setupProcessProgressBar: Start. data = %s", data );

        //timer.stop ();
        // скинуть все значения в прогресс-баре в исходное состояние
        switch ( data.getProgressBarType() )
        {
            case DATA:
            case PACKET:
                // Устанавливаем счет прогресс-бара - с 0.
                timerListener.setCurrentProcessCounter ( 0 );
                //processProgressBar.setIndeterminate ( false );    // false - конечный бегунок
                break;
            case UNTIME:
                //processProgressBar.setIndeterminate ( true );    // true - бесконечный бегунок
                // Привязываем бесконечный таймер. Запускаем.
                //timer.start();
                break;
        }


        dataObject.clear();
        dataObject.setProgressBarType ( data.getProgressBarType() );
        dataObject.setTotalSize ( data.getTotalSize() );
        dataObject.setStepSize ( data.getStepSize() );

        // Настроить прогресс-бар отображения работы текущего процесса - это начало работы процесса.
        // <br/> Работа в awt-потоке. Получили из none-awt-потока. Работаем с gui-компонентами. Начальная настройка.
        // Запоминаем ИД текущей команды.
        //setCommandId ( data.getCommandId() );

        //timer.stop ();
        // скинуть все значения в прогресс-баре в исходное состояние
        switch ( data.getProgressBarType() )
        {
            case DATA:
            case PACKET:
                // Устанавливаем счет прогресс-бара - с 0.
                //timerListener.setCurrentProcessCounter ( 0 );        // делается в листенере
                processProgressBar.setIndeterminate ( false );    // false - конечный бегунок
                break;
            case UNTIME:
                processProgressBar.setIndeterminate ( true );    // true - бесконечный бегунок
                // Привязываем бесконечный таймер. Запускаем.
                //timer.start();
                break;
        }

        processProgressBar.setValue ( 0 );
        processProgressBar.setString ( "" );  // очищаем текст бегунка от старых значений
        processProgressBar.setStringPainted ( true );
        processProgressBar.setMinimum ( 0 );
        processProgressBar.setMaximum ( 100 );  // т.к. обьем данных в прогресс-бар уже передаем в процентах.

        // заголовок процесса
        currentProcessNameLabel.setText ( data.getProcessTitle() );

        // скинуть заголовок и в текстовую область
        textArea.append ( Integer.toString ( totalProgressBar.getValue()+1 ) );   // Нумерация действия - от 0
        textArea.append ( ") " );
        textArea.append ( data.getProcessTitle() );
        textArea.append ( "\n" );
    }


    /**
     * Работа в awt-потоке. Получили из none-awt-потока. Работаем с gui-компонентами.
     * <br/> Дергается в bg-потоке в bg-методе swingWorker'a - process ( List<MultiData> list ).
     *
     * @param data
     */
    public void setupTotalProgressBar ( MultiData data )
    {
        totalProgressBar.setIndeterminate ( false );    // false - конечный бегунок
        totalProgressBar.setMinimum ( 0 );
        totalProgressBar.setMaximum ( data.getStepSize() );
        totalProgressBar.setValue ( 0 );

        /*
        // бордюр с отображением суммарного времени работы
        Border border;
        border = BorderFactory.createTitledBorder ( "00:00:00" );
        totalProgressBar.setBorder ( border );
        */
    }

    /**
     * <br/> Дергается в bg-потоке в bg-методе swingWorker'a - process ( List<MultiData> list ).
     *
     */
    public void paintIncTotalProgressBar ()
    {
        int ic = totalProgressBar.getValue();
        ic++;
        totalProgressBar.setValue ( ic );
    }

    /**
     * Рисовать данные во втором прогресс-баре - на основе полученных данных dataObject.
     * Также здесь отметить и первый прогресс бар - на сколько продвинулось суммарное время - printTotalTime.
     * <br/> Дергается в bg-потоке в bg-методе swingWorker'a - process ( List<MultiData> list ).
     */
    public void paintFirstProgressBarData ()
    {
        Log.l.debug ( "--- MultiActionWaitingListener.paintFirstProgressBarData: Start. dataObject = %s", dataObject );

        paintFirstProgressBarData ( dataObject );

        // Счет и отображение суммарного времени
        timerListener.printTotalTime ( null, System.currentTimeMillis() );
    }

    /**
     * Пришли данные из backgroundProcessпроцесса для отрисовки в гуи-диалоге.
     * Рисовать данные во втором прогресс-баре - на основе полученных данных dataObject.
     * Также здесь отметить и первый прогресс бар - на сколько продвинулось суммарное время - printTotalTime.
     */
    private void paintFirstProgressBarData ( ProgressBarDataObject dataObject )
    {
        int         procent;
        String      msg;

        Log.l.debug ( "MultiActionDialog (",getName(),").paintFirstProgressBarData: Start. dataObject = %s", dataObject );

        // вычислить процент выполненной работы
        procent = (int) ( (((double)dataObject.getCount())/dataObject.getTotalSize()) * 100 );

        // Отрисовка первого прогресс-бара - согласно установленному типу
        switch ( dataObject.getProgressBarType() )
        {
            case UNTIME : // бесконечный бегунок - показываем: В титле - прошедшие секунды (таймером). В бегунке - ничего.
                //procent = -1;
                //msg     = "";
                break;

            case PACKET : // передача пакетных данных - показываем: В титле - кол-во пакетов, размер пакета, прошедшие секунды. В бегунке - проценты от передачи.
                //msg = Convert.concatObj ( "packet: ", dataObject.getPacketNumber(), " (by ", dataObject.getStepSize(), " byte); time : ", timeStr, " [", time,"]" );
                msg = Convert.concatObj ( "packet: ", dataObject.getPacketNumber(), " (by ", dataObject.getStepSize(), " byte)", " ", procent, "%" );
                processProgressBar.setValue ( procent );
                processProgressBar.setString ( msg );
                break;

            case DATA : // передача простых данных - показываем: В титле - прошедшие секунды. В бегунке - проценты от передачи.
                //msg = timeStr;
                //msg     = "";
                processProgressBar.setValue ( procent );
                break;
        }
    }

    public void startProcess ( long startTime )
    {
        Log.l.debug ( "MultiActionDialog (%s).startProcess: Start. startTime = %d; multiFunction = %s",getName(), startTime, multiFunction );

        timerListener = new TimerAction ( startTime, this );
        timer         = new Timer ( WCons.ONE_SECOND, timerListener );
        timerListener.setTimer ( timer );

        // - создаем swingWorker - T, D.  -- T - что возвращает doInBackground. D - что передается в publish-process.
        swingWorker   = new MultiActionSwingWorker ( multiFunction );
        // Задаем имя потока. Нет - swingWorker не позволяет этого делать - менять имя.
        //swingWorker.setName ( Convert.concatObj ( "Sync_", str ) );

        timer.start();

        // TEST
        // - SwingWorker.getWorkersExecutorService ();
        //final AppContext appContext = AppContext.getAppContext();
        //ExecutorService executorService = (ExecutorService) appContext.get ( SwingWorker.class );

        // - запускаем  backgroundProcess
        //  там вызывается static getWorkersExecutorService().execute(this); - т.е. какой-то общий. Все exceotorObj хранятся в мапе на класс. appContext.put(SwingWorker.class, executorService)
        //  -- т.е. разные обьекты одного класса запускаются одним и тем же Executor.
        //swingWorker.execute();   // зесь запуск через ThreadPoolExecutor, который в свою очередь не завершает завершенные процессы - толкьо если запутсится нвоая задача и вытеснит кого-нибудь.
        Par.EXECUTOR.execute ( swingWorker );
    }

    public MultiActionSwingWorker getSwingWorker ()
    {
        return swingWorker;
    }

    public void stopProcess ()
    {
        Log.l.debug ( "MultiActionDialog.stopProcess: Start. swingWorker = %s", swingWorker );

        // Если по крестику - тогда worker еще работает - прервать его от имени гуи, чтобы worker смог послать на сервер команду на прерывание.
        if ( ( ! swingWorker.isCancelled()) && (! swingWorker.isDone()) )
        {
            swingWorker.cancel ( true );  // говорим ему чтобы прервался самостоятельно -- в коде в методе background должна быть периодическая проверка на статус 'Прервано'.
        }

        Toolkit.getDefaultToolkit().beep();
        //dialog.setCursor ( null ); //turn off the wait cursor

        timer.stop();

        multiFunction.clear();

        /*
        // ждем завершения работы -- не здесь
        try
        {
            Log.l.debug ( "MultiActionDialog.stopProcess: Waiting swingWorker GET. swingWorker = ", swingWorker );
            swingWorker.get();
        } catch ( Exception e )        {
            Log.l.error ( e, "MultiActionDialog.stopProcess: get swingWorker result error" );
        }
        */

        // Сам диалог закроется далее - своим чередом.
        Log.l.debug ( "MultiActionDialog.stopProcess: Finish. swingWorker = %s", swingWorker );

        swingWorker = null;
    }

    public void setCurrentProcessCounter ( int ic )
    {
        timerListener.setCurrentProcessCounter ( ic );
    }

    /**
     * Работа успешно завершена - закрыть диалог если задано.
     * <br/> Дергается в AWT-потоке в методе swingWorker'a - process ( List<MultiData> list ).
     * Анализ - надо ли закрывать диалог.
     */
    public void closeDialog ()
    {
        // Остановить все запущенные процессы - если они еще не были остановлены.
        stopProcess();

        if ( multiFunction.isNeedCloseDialog() )
        {
            setVisible ( false );
            dispose();  // Очистить все используемые ресурсы (вернуть их в ОС)
        }
        else
        {
            // Останавливаем бегунок последнего процесса - если он находился в бесконечном режиме вправо-влево.
            processProgressBar.setIndeterminate ( false );   // false - конечный бегунок
            // Довести сумарный прогресс-бар до 100%.
            totalProgressBar.setValue ( totalProgressBar.getMaximum() );
            setCancelButtonText ( "Закрыть" );
            textArea.append ( "\n--------------- Работа завершена --------------\n" );
            // Очистить первый титл от названия последней выполненной задачи
            currentProcessNameLabel.setText ( "" );
            // Перерисовываем диалог, т.к. скорее всего там выскочило сообщение об ошибке и его надо показать.
            pack();
        }
    }

    /**
     * Динамический подсчет переданных данных - для отображения в первом прогресс-баре.
     * <br/> Дергается в bg-потоке в bg-методе swingWorker'a - process ( List<MultiData> list ).
     * @param sendSize      Кол-во переданных данных.
     * @param packetNumber  Номер переданного пакета, либо -1.
     */
    public void addData ( int sendSize, int packetNumber )
    {
        dataObject.addData ( sendSize );
        dataObject.setPacketNumber ( packetNumber );
    }

    protected void createDialogSize ()
    {
    }

}
