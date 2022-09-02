package svj.wedit.v6.tools;


import svj.wedit.v6.Par;
import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.params.BooleanParameter;
import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.function.params.SimpleParameter;
import svj.wedit.v6.gui.WComponent;
import svj.wedit.v6.gui.button.EmptyButton;
import svj.wedit.v6.gui.button.WButton;
import svj.wedit.v6.gui.img.TabIcon;
import svj.wedit.v6.gui.listener.CloseTabListener;
import svj.wedit.v6.gui.menu.WEMenu;
import svj.wedit.v6.gui.menu.WEMenuItem;
import svj.wedit.v6.gui.panel.EditablePanel;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.gui.widget.AbstractWidget;
import svj.wedit.v6.gui.widget.BooleanWidget;
import svj.wedit.v6.gui.widget.StringFieldWidget;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.book.WEditStyle;
import svj.wedit.v6.obj.function.Function;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.*;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 27.07.2011 10:41:31
 */
public class GuiTools
{
    private static final    String EMAIL_PATTERN = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
    //private static final Icon CLOSE_TAB_ICON = new ImageIcon ( GuiTools.class.getResource ( "closeTabButton.png" ) );
    private static final    Icon CLOSE_TAB_ICON = createImageByFile ( "img/editor/close_red.png" );

    /**
     * A UIDefaults key for enabling/disabling the new high-resolution
     * gray filter globally. This setting can be overridden per component.
     */
    public static final String HI_RES_GRAY_FILTER_ENABLED_KEY = "HiResGrayFilterEnabled";

    /**
     * A client property key for components with a disabled icon
     * such as buttons, labels, and tabbed panes. Specifies whether
     * the new high resolution gray filter shall be used to compute
     * a disabled icon - if none is available.
     */
    public static final String HI_RES_DISABLED_ICON_CLIENT_KEY = "generateHiResDisabledIcon";

    public static final Collection<Integer> iconSizeList        = new LinkedList<Integer>();


    public static AbstractWidget createWidget ( FunctionParameter parameter, int titleWidth, int valueWidth )
    {
        AbstractWidget result = null;

        if ( parameter instanceof BooleanParameter )
        {
            BooleanParameter booleanParameter = (BooleanParameter) parameter;
            result = createBooleanWidget ( booleanParameter, parameter.getRuName(), BooleanWidget.Orientation.TITLE_FIRST, titleWidth, valueWidth );
            return result;
        }

        if ( parameter instanceof SimpleParameter )
        {
            SimpleParameter simpleParameter = (SimpleParameter) parameter;
            result = createStringWidget ( simpleParameter, parameter.getRuName(), parameter.hasEmpty(), titleWidth, valueWidth );
            return result;
        }

        return null;
    }

    public static AbstractWidget createBooleanWidget ( BooleanParameter booleanParameter, String ruTitle, BooleanWidget.Orientation orientation,
                                                       int titleWidth, int valueWidth )
    {
        BooleanWidget widget;

        widget = new BooleanWidget ( ruTitle, booleanParameter.getValue(), orientation );
        widget.setEditable ( false );
        widget.setTitleWidth ( titleWidth );
        widget.setValueWidth ( valueWidth );
        widget.setObject ( booleanParameter );

        return widget;
    }

    public static AbstractWidget createStringWidget ( SimpleParameter parameter, String ruTitle, boolean hasEmpty,
                                                      int titleWidth, int valueWidth )
    {
        StringFieldWidget widget;

        widget = new StringFieldWidget ( ruTitle, parameter.getValue(), hasEmpty );
        widget.setEditable ( false );
        widget.setTitleWidth ( titleWidth );
        widget.setValueWidth ( valueWidth );
        widget.setObject ( parameter );

        return widget;
    }


    public static Border createRightTitleBorder ( String title, Color color )
    {
        Border  border;
        Font    titleBorderFont;

        titleBorderFont = new Font ("Monospaced", Font.BOLD, 14 );
        border          = new TitledBorder ( BorderFactory.createEtchedBorder(), title, TitledBorder.RIGHT,
                                                TitledBorder.TOP, titleBorderFont, color );  // WCons.GREEN_1
        return border;
    }

    public static Border createTitleBorder ( String title, Color color, int align )
    {
        Border  border;
        Font    titleBorderFont;

        titleBorderFont = new Font ("Monospaced", Font.BOLD, 14 );
        border          = new TitledBorder ( BorderFactory.createEtchedBorder(), title, align,      // align: TitledBorder.RIGHT
                                                TitledBorder.TOP, titleBorderFont, color );         // color: WCons.GREEN_1
        return border;
    }


    /**
     * Добавить новый табик. В табике кроме титла прописать две иконки - титульная иконка (слева) и иконка для закрытия табика (справа).
     * @param tabbedPane   Панель табиков.
     * @param c            Новая Панель, которую добавят как табик.
     * @param title        Заголовок для нового табика.
     * @param icon         Титульная иконка нового табика.
     */
    public static JLabel addClosableTab ( JTabbedPane tabbedPane, JComponent c, String title, Icon icon, Function closeFunction )
    {
        int             pos, titleIndex;
        String          titleRelease;
        boolean         notOk;
        FlowLayout      f;
        JPanel          pnlTab;  // Панель которая расположится в титле табика.
        JLabel          lblTitle;
        JButton         btnClose;
        final ActionListener  listener;

        titleIndex  = 2;
        notOk       = true;
        titleRelease= title;
        // Смотрим заголовки  среди существующих таб панелей. Если такой заголовок уже есть (совпадение) - добавить к нашему заголовку индекс. И снова проверить на совпадение.
        while ( notOk )
        {
            if ( contentTitle(tabbedPane, titleRelease) )
            {
                // Есть такой заголовок. Изменяем заголовок - добавляя индекс
                titleRelease = Convert.concatObj ( title, '(', titleIndex, ')' );
                titleIndex++;
            }
            else
            {
                notOk = false;
            }
        }
        // Изменим имя панели табика
        c.setName ( titleRelease );

        Log.l.debug ( "=========================== ADD Tab - START : %s", title );
        // Добавить табик - пока без заголовка.
        // -- Здесь сразу же дергается акция -- WChangeListener.stateChanged с последующим глобальным rewrite.
        tabbedPane.addTab ( null, c );
        Log.l.debug ( "=========================== ADD Tab - FINISH : %s", title );

        pos = tabbedPane.indexOfComponent ( c );

        f   = new FlowLayout ( FlowLayout.CENTER, 5, 0 );

        // Make a small JPanel with the layout and make it non-opaque
        pnlTab = new JPanel ( f );
        pnlTab.setOpaque ( false );

        // Add a JLabel with title and the left-side tab icon
        lblTitle = new JLabel ( titleRelease );
        lblTitle.setIcon ( icon );

        // Create a JButton for the close tab button
        btnClose = new JButton();
        btnClose.setOpaque ( false );

        // Configure icon and rollover icon for button
        btnClose.setRolloverIcon ( CLOSE_TAB_ICON );
        btnClose.setRolloverEnabled ( true );
        btnClose.setIcon ( RGBGrayFilter.getDisabledIcon ( btnClose, CLOSE_TAB_ICON ) );
        //btnClose.setIcon ( GrayFilter.createDisabledImage ( CLOSE_TAB_ICON ) );

        // Set border null so the button doesn't make the tab too big
        btnClose.setBorder ( null );

        // Make sure the button can't get focus, otherwise it looks funny
        btnClose.setFocusable ( false );

        // Put the panel together
        pnlTab.add ( lblTitle );
        pnlTab.add ( btnClose );

        // Add a thin border to keep the image below the top edge of the tab when the tab is selected
        pnlTab.setBorder ( BorderFactory.createEmptyBorder ( 2, 0, 0, 0 ) );

        // Now assign the component for the tab
        tabbedPane.setTabComponentAt ( pos, pnlTab );

        // Add the listener that removes the tab
        listener = new CloseTabListener ( closeFunction );
        btnClose.addActionListener ( listener );

        // Optionally bring the new tab to the front
        tabbedPane.setSelectedComponent ( c );

        tabbedPane.revalidate();

        // ----------------------- Мапим сочетание клавиш ---------------------
        /* НЕТ. иначе запутаемся с закрытием текстов, книг, сборников.
        //-------------------------------------------------------------
        // Bonus: Adding a <Ctrl-W> keystroke binding to close the tab
        //-------------------------------------------------------------
        closeTabAction = new AbstractAction ()
        {
            @Override
            public void actionPerformed ( ActionEvent e )
            {
                //tabbedPane.remove ( c );
                listener.actionPerformed ( e );
            }
        };

        // Create a keystroke
        controlW = KeyStroke.getKeyStroke ( "control W" );

        // Get the appropriate input map using the JComponent constants.
        // This one works well when the component is a container.
        inputMap = c.getInputMap ( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );

        // Add the key binding for the keystroke to the action name
        inputMap.put ( controlW, "closeTab" );

        // Now add a single binding for the action name to the anonymous action
        c.getActionMap().put ( "closeTab", closeTabAction );
        */

        return lblTitle;
    }

    private static boolean contentTitle ( JTabbedPane tabbedPane, String title )
    {
        int         ic;
        boolean     result;
        Component   c;
        String      name;

        result  = false;
        ic      = tabbedPane.getTabCount();

        if ( ic > 0 )
        {
            for ( int i = 0; i < ic; i++)
            {
                c = tabbedPane.getComponentAt ( i );
                Log.l.debug ( "if contentTitle -- title = %s; tab comp = %s", title, c );
                // Взять имя табика
                name = c.getName();
                Log.l.debug ( "if contentTitle -- title = %s; tab name = %s", title, name );
                if ( (name != null) && name.equalsIgnoreCase ( title ) )
                {
                    // нашли похожее имя
                    result = true;
                    break;
                }
            }
        }
        Log.l.debug ( "Finish. if contentTitle -- title = '%s'; result = %b", title, result );
        return result;
    }


    public static EmptyButton createIconButton ( Function function, int componentHeight ) //throws WEditException
    {
        EmptyButton result;
        ImageIcon   icon;
        int         width, height;
        Dimension   size;
        String      iconFileName;
        Image       image;

        iconFileName    = function.getIcon ( componentHeight );

        Log.l.debug ( "---------------- iconFileName = %s", iconFileName );

        if ( iconFileName == null )  iconFileName = WCons.DEFAULT_ICON_PATH;

        icon            = GuiTools.createImageByFile ( iconFileName );

        Log.l.debug ( "icon = %s", icon ); // Файл иконки не найден по пути, но здесь какой-то обьект все ранво есть.
        Log.l.debug ( "icon image = %s", icon.getImage() ); // Файл иконки не найден по пути, но здесь какой-то обьект все ранво есть.
        Log.l.debug ( "icon width = %s", icon.getIconWidth() ); // Файл иконки не найден по пути, но здесь какой-то обьект все ранво есть.

        if ( icon.getIconWidth() < 0 )
        {
            // Иконка не загрузилась. Грузим дефолтную.
            icon            = GuiTools.createImageByFile ( WCons.DEFAULT_ICON_PATH );
            // Привести дефолтную иконку к заданным размерам
            width       = icon.getIconWidth();
            image       = icon.getImage();
            icon        = new ImageIcon ( image.getScaledInstance ( componentHeight, -1, Image.SCALE_DEFAULT ) );
        }

        width       = icon.getIconWidth();
        // Привести иконку к заданным размерам  -- уже НЕ делаем - иконки портятся.
        //image       = icon.getImage();
        //icon        = new ImageIcon ( image.getScaledInstance ( componentHeight, -1, Image.SCALE_DEFAULT ) );

        // размер кнопки приводим к квадрату
        height  = icon.getIconHeight();
        width   = height    = Math.max ( width, height ) + 5;
        size    = new Dimension ( width, height );

        result  = new EmptyButton();
        result.setFunctionId ( function.getId() );
        result.setPreferredSize ( size );
        result.setSize ( size );
        result.setMinimumSize ( size );
        result.addActionListener ( function );
        result.setIcon ( icon );
        result.setToolTipText ( function.getToolTipText() );
        result.setFocusable ( false );

        return result;
    }

    public static ImageIcon createIcon ( EmptyButton iconButton, String iconFileName, Function function ) //throws WEditException
    {
        ImageIcon   icon;
        int         width, height;
        Dimension   size;

        Log.l.debug ( "---------------- iconFileName = ", iconFileName );
        icon            = GuiTools.createImageByFile ( iconFileName );
        Log.l.debug ( "icon = ", icon ); // Файл иконки не найден по пути, но здесь какой-то обьект все ранво есть.
        Log.l.debug ( "icon image = ", icon.getImage() ); // Файл иконки не найден по пути, но здесь какой-то обьект все ранво есть.
        Log.l.debug ( "icon width = ", icon.getIconWidth() ); // Файл иконки не найден по пути, но здесь какой-то обьект все ранво есть.

        if ( icon.getIconWidth() < 0 )
        {
            // Иконка не загрузилась. Грузим дефолтную.
            icon            = GuiTools.createImageByFile ( WCons.DEFAULT_ICON_PATH );
        }

        // размер приводим к круглому
        width   = icon.getIconWidth();
        height  = icon.getIconHeight();
        width   = height    = Math.max ( width, height ) + 5;
        size    = new Dimension ( width, height );
        iconButton.setPreferredSize ( size );
        iconButton.setSize ( size );
        iconButton.setMinimumSize ( size );

        return icon;
    }


    public static WButton createButton ( String title, String toolTip, String iconFileName )
    {
        // - iconPath - именно только имя иконки. Сформировать относительный путь = img/button/img_button_size/имя_иконки
        String iconPath;

        iconPath = Convert.concatObj ( "img/button/", Par.BUTTONS_ICON_SIZE, '/', iconFileName );
        WButton result = new WButton (title, toolTip, iconPath );

        //result.setEnabled(false);
        //result.setActionCommand ( command );
        //result.addActionListener ( actionListener );

        return result;
    }

    public static WButton createButton ( String title, String toolTip, String iconFileName, int width, ActionListener actionListener, String command )
    {
        String iconPath;

        iconPath = Convert.concatObj ( "img/button/", Par.BUTTONS_ICON_SIZE, '/', iconFileName );
        WButton result = new WButton (title, toolTip, iconPath );

        result.setPreferredSize ( new Dimension ( width, WCons.BUTTON_HEIGHT ) );
        result.setSize ( new Dimension ( width, WCons.BUTTON_HEIGHT ) );
        //result.setEnabled(false);
        result.setActionCommand ( command );
        result.addActionListener ( actionListener );

        return result;
    }

    /* Returns an ImageIcon, or WEditException if the path was invalid. */
    public static Icon createImageIcon ( String path, String description ) throws WEditException
    {
        return createImage ( path, description );
    }

    /* Взять иконку из jar*/
    public static ImageIcon createImage ( String path, String description ) throws WEditException
    {
        ImageIcon   result;
        URL imgURL;

        imgURL = WEditException.class.getResource(path);
        if ( imgURL == null )
            throw new WEditException ( null, "Невозможно найти файл иконки '", path, "' для объекта '", description, "'." );
        else
            result = new ImageIcon ( imgURL, description );

        return result;
    }

    /**
     *  Взять иконку из локальной директории.
     * <BR/> Если файл не будет найден - обьект все равно создастся. И Image тоже будет не null. Только width=-1.
     * <BR/>
     * @param path  Путь до файла (относительный, либо абсолютный).
     * @return  Иконка.
     */
    public static ImageIcon createImageByFile ( String path )
    {
        String      imgLocation;
        ImageIcon   icon;

        //Log.l.debug ( "--- icon path = ", path );
        // Загрузить иконку
        imgLocation = FileTools.createFileName ( Par.MODULE_HOME, path );
        //Log.l.debug ( "--- icon imgLocation = ", imgLocation );
        icon        = new ImageIcon ( imgLocation );
        //Log.l.debug ( "--- icon  = ", icon );

        return icon;
    }

    public static ImageIcon createSmallImageByFile ( String path, int size )
    {
        String      imgLocation;
        ImageIcon   icon;

        //Log.l.debug ( "--- icon path = ", path );
        // Загрузить иконку
        //imgLocation = FileTools.createFileName ( Par.MODULE_HOME, path );
        //Log.l.debug ( "--- icon imgLocation = ", imgLocation );
        //icon        = new ImageIcon ( imgLocation );
        //Log.l.debug ( "--- icon  = ", icon );

        ImageIcon tmpIcon = new ImageIcon ( path );

        int k = -1, width = 1, height = 1;
        if ( tmpIcon.getIconWidth() > tmpIcon.getIconHeight() )   {
            // уменьшаем по горизонтали
            if (tmpIcon.getIconWidth() > size) {
                k = tmpIcon.getIconWidth() / size;
                width = tmpIcon.getIconWidth() / k;
                height = tmpIcon.getIconHeight() / k;
            }

        } else {
            // уменьшаем по вертикали
            if (tmpIcon.getIconHeight() > size) {
                k = tmpIcon.getIconHeight() / size;
                width = tmpIcon.getIconWidth() / k;
                height = tmpIcon.getIconHeight() / k;
            }
        }

        Image image = tmpIcon.getImage ();
        if (k > 0) {
            //icon = new ImageIcon ( image.getScaledInstance ( 90, height, Image.SCALE_REPLICATE ) );
            //icon = new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_DEFAULT));

            icon = new ImageIcon(getScaledImage(image, width, height));
        } else {
            icon = tmpIcon;
        }

        return icon;
    }

    // уменьшить раземр картинки
    private static Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }
    
    public static Image createImage ( String path )
    {
        String      imgLocation;
        Image   image;

        // Загрузить иконку
        imgLocation = FileTools.createFileName ( Par.MODULE_HOME, path );
        image = Toolkit.getDefaultToolkit().getImage(imgLocation);

        return image;
    }

    /**
     * Метод помещает указанный диалог в центр экрана
     * @param dlg диалог для размещения по центру
     */
    public static void setDialogScreenCenterPosition ( JDialog dlg )
    {
        if (dlg == null)             return;

        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        dlg.setLocation(screenSize.width / 2 - (dlg.getSize().width / 2),
                screenSize.height / 2 - (dlg.getSize().height / 2));
    }

    public static void setDialogScreenCenterPosition ( JDialog dlg, Container container )
    {
        if (dlg == null)             return;

        if (container != null)
            dlg.setLocationRelativeTo(container);
        else
            setDialogScreenCenterPosition ( dlg );
    }


    /**
      * Добавляет в указанный объект дочерний, с автоматической сортировкой по имени среди "детей".
      * <BR/> Без акций.
      * @param parent родительский объект
      * @param child ребёнок для добавления
      * @throws WEditException ош
      */
     public static void addChild ( TreeObj parent, TreeObj child ) throws WEditException
     {
         /*
         Collection<WTreeObj> v;
         TreeSet<WTreeObj> sorter;

         try
         {
             parent.add ( child );
             child.setParent(parent);
             // для автоматической сортировки "детей" в списке используется TreeSet
             v       = parent.getChildrens();
             sorter  = new TreeSet<WTreeObj>(v);
             // удалить все
             parent.removeAllChildren();
             // добавить заново, уже отсортированные в коллекции TreeSet по имени
             for ( WTreeObj obj : sorter )
             {
                 parent.add ( obj );
             }
         } catch (Exception e) {
             Log.l.error ( e, "Error add child to ", parent, " cause: ", e.getMessage() );
             throw new WEditException ( e, "Системная ошибка добавления нового объекта '", child.toString(), "' к объекту '", parent.toString (), "'." );
         }
         */
     }

    /* Контекстное меню, которое вызывается по правой кнопке мышки на панели дерева (но не на объектах).
       Не используется так как лишнее. Не удалять - мало ли что. */
    public static JPopupMenu createDefaultTreePopupMenu ( final TreePanel treePanel )
    {
        JPopupMenu  result;
        JMenuItem   menuItem;
        JLabel      title;
        JPanel      titlePanel;

        result   = new JPopupMenu();

        // title
        titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder ( BorderFactory.createEtchedBorder() );
        titlePanel.setBackground ( WCons.BLUE_1 );
        title = new JLabel("     Объект :  Дерево   ");
        title.setForeground ( Color.WHITE );
        titlePanel.add ( title, BorderLayout.CENTER );
        title.setBackground ( WCons.BLUE_1  );
        result.add(titlePanel);

        menuItem = new JMenuItem();
        menuItem.setText ("Схлопнуть все");
        menuItem.addActionListener ( new ActionListener()
        {
                        @Override
            public void actionPerformed ( ActionEvent evt )
            {
                treePanel.collapsNode ( null );
            }
        });
        result.add(menuItem);

        menuItem = new JMenuItem();
        menuItem.setText ("Перерисовать");
        menuItem.addActionListener ( new ActionListener()
        {
                        @Override
            public void actionPerformed ( ActionEvent evt )
            {
                treePanel.setRepaintTreeMode ( TreePanel.RepaintTree.ALL );
                try
                {
                    treePanel.rewrite();
                } catch ( Exception e )                 {
                    Log.l.error ( "SystemCreator.createDefaultTreePopupMenu / ActionListener.actionPerformed: err", e );
                } finally                {
                    treePanel.setRepaintTreeMode ( TreePanel.RepaintTree.NO );
                }
            }
        });
        result.add(menuItem);

        return result;
    }

    public static Icon createTabIcon ( EditablePanel editPanel )
    {
        TabIcon result;
        Image         greenImage, redImage;

        greenImage  = createImage ( "img/editor/close_green.png" );
        redImage    = createImage ( "img/editor/close_red.png" );
        result      = new TabIcon ( editPanel, greenImage, redImage );

        return result;
    }

    /**
     * Выдать допутсимые размеры иконок.
     * <br/> Пока забито в коде.
     * <br/>
     * @return  Массив размеров.
     */
    public static Collection<Integer> getIconSizeList ()
    {
        if ( iconSizeList.isEmpty() )
        {
            iconSizeList.add ( 16 );
            iconSizeList.add ( 24 );
        }

        return iconSizeList;
    }

    public static void rewriteComponents ( Container container )
    {
        WComponent  ec;
        Container   c;

        Log.l.debug ( "GuiTools.rewriteComponents: Start. container = %s (%s)", container.getName(), container.getClass().getName() );

        for ( Component component : container.getComponents() )
        {
            Log.l.debug ( "GuiTools.rewriteComponents: component = %s (%s)", component.getName(), component.getClass().getName() );
            if ( component instanceof WComponent )
            {
                Log.l.debug ( "--- GuiTools.rewriteComponents: rewrite = %s (%s)", component.getName(), component.getClass().getName() );
                ec   = (WComponent ) component;
                ec.rewrite();
            }
            else if ( component instanceof Container )
            {
                c    = (Container) component;
                rewriteComponents ( c );
            }
        }
        Log.l.debug ( "GuiTools.rewriteComponents: Finish. container = %s (%s)", container.getName(), container.getClass().getName() );
    }

    public static void rewriteComponents_old_menu ( Container container )
    {
        boolean         nodeAllow, allowed;
        JMenu           menu;
        WEMenuItem      menuItem;
        WEMenu          eltexMenu;
        Component[]     menuComponents;
        WComponent      rewriteComp;

        //Logger.getInstance().debug ( "GuiTools.rewriteMenuComponents(",getText(),") : enabled = ", isAllowed() );

        // Открываем пункт - если хотя бы один из подпунктов разрешен (открыт).
        nodeAllow = false;

        // начальная установка
        if ( container instanceof WComponent )
        {
            rewriteComp   = (WComponent) container;
            rewriteComp.rewrite();    //
        }

        // Надо перерисовать все вложенные элементы этого меню
        try
        {
            if ( container instanceof JMenu )
            {
                menu = (JMenu) container;
                menuComponents = menu.getMenuComponents();   // for JMenu
            }
            else
            {
                menuComponents = container.getComponents();         // for JPopupMenu
            }
            //Logger.getInstance().debug ( "- GuiTools.rewriteMenuComponents(", getText (), "): menuComponents = ", menuComponents );

            if ( menuComponents != null )
            {
                // Включить все разделители - на случай, если были отключены ранее.
                //visibleAllSeparator ( menuComponents );

                for ( Component comp : menuComponents )
                {
                    // Перерисовать парентов
                    if ( comp instanceof Container )
                    {
                        rewriteComponents ( ( Container ) comp );
                    }
                }
                /*
                for ( Component comp : menuComponents )
                {
                    //Logger.getInstance().debug ( "-- GuiTools.rewriteMenuComponents(", getText (), "): comp = ", comp );

                    if ( comp instanceof JMenu )
                    {
                        // instanceof - тк здесь присутствует Separator и могут и др gui-обьекты.
                        menu = ( JMenu ) comp;
                        menu.setEnabled ( true );
                        nodeAllow = true;
                        //Logger.getInstance().debug ( "--- GuiTools.rewriteMenuComponents(", getText(), "): JMenu = ", menu );
                    }

                    else if ( comp instanceof WEMenuItem )
                    {
                        menuItem = ( WEMenuItem ) comp;
                        menuItem.rewrite ();
                        if ( menuItem.isEnabled() ) nodeAllow = true;
                        //Logger.getInstance().debug ( "--- GuiTools.rewriteMenuComponents(", getText(), "): nodeAllow = ", nodeAllow, "; EltexMenuItem = ", menuItem );
                    }

                    else if ( comp instanceof WEMenu )
                    {
                        eltexMenu = ( WEMenu ) comp;
                        eltexMenu.rewrite();
                        //GuiTools.rewriteMenuComponents ( eltexMenu );
                        if ( eltexMenu.isAllowed() ) nodeAllow = true;
                        //Logger.getInstance().debug ( "--- GuiTools.rewriteMenuComponents(", getText(), "): nodeAllow = ", nodeAllow, "; EltexMenu = ", eltexMenu );
                    }

                    else if ( comp instanceof WComponent )
                    {
                        rewriteComp   = (WComponent ) comp;
                        rewriteComp.rewrite ();
                    }

                    // Перерисовать парентов
                    if ( comp instanceof Container )
                    {
                        rewriteComponents ( ( Container ) comp );
                    }
                }
                */

                // Прогнать список и удалить лишние разделители - т.е. первый, последний, несколько подряд
                //checkSeparator ( menuComponents );
            }

            //Logger.getInstance().debug ( "----- GuiTools.rewriteMenuComponents(", getText(), "): nodeAllow = ", nodeAllow );

            /*
            // Если все вложения выключены - то отключаем (скрываем) и сам пункт меню
            if ( nodeAllow )
                container.setVisible ( true );
            else
                container.setVisible ( false );
            */

        } catch ( Exception ex )         {
            Log.l.error ( Convert.concatObj ( "GuiTools.rewriteComponents (", container.getName (), "): err" ), ex);
        }
    }

    public static com.lowagie.text.Font createRtfFont ( AttributeSet swing )
    {
        com.lowagie.text.Font result;
        int     family, style, ic;
        float   size;
        Color   color;
        String  str;

        // style
        style   = com.lowagie.text.Font.NORMAL;
        if ( StyleConstants.isBold ( swing ) )
        {
            if ( StyleConstants.isItalic (swing) )
                style  = com.lowagie.text.Font.BOLDITALIC;
            else
                style  = com.lowagie.text.Font.BOLD;
        }
        else if ( StyleConstants.isItalic (swing) ) style   = com.lowagie.text.Font.ITALIC;

        // size
        ic = StyleConstants.getFontSize (swing);
        if ( ic <= 0 )   ic  = 10;
        size    = ic;

        // color
        color   = Color.BLACK;
        //color = StyleConstants.getForeground ( swing );
        //if ( color == null )    color   = Color.BLACK;

        // family
        family  = com.lowagie.text.Font.UNDEFINED;
        // Ищем в библиотеке iText название фонта, который прописан в нашем стиле.
        str = StyleConstants.getFontFamily ( swing );
        if ( str != null )   family  = com.lowagie.text.Font.getFamilyIndex ( str );

        // Если нет в библиотеке iText такого фонта - ищем дальше
        if ( family == com.lowagie.text.Font.UNDEFINED ) family  = com.lowagie.text.Font.getFamilyIndex ( "CMU Sans Serif" );
        if ( family == com.lowagie.text.Font.UNDEFINED ) family  = com.lowagie.text.Font.getFamilyIndex ( "Arial" );
        if ( family == com.lowagie.text.Font.UNDEFINED ) family  = com.lowagie.text.Font.TIMES_ROMAN; // Ничего нет - ставим распространенный фонт. Если же не найдет и его, то сама поставит дефолтный фонт.

        result  = new com.lowagie.text.Font ( family, size, style, color );

        return result;
    }

    /**
     * Тщательное сравнение текстовых стилей.
     * <br/> Сравниваем все атрибуты стиля:
     * <br/> 1) Цвет символа
     * <br/> 2) Размер
     * <br/> 3) Цвет фона
     * <br/> 4) Смещение
     * <br/> 5) Family
     * <br/> 6) Alignment
     * <br/>
     * <br/> size=10,Alignment=0,styleName=text,foreground=java.awt.Color[r=0,g=255,b=0],bold=true,FirstLineIndent=10.0,family=CMU Serif
     * <br/>
     * @param style       Стиль, полученный из свинг-редактора.
     * @param textStyle   Стиль текста данной книги.
     * @return            TRUE  - стили полностью равны.
     */
    public static boolean compareTextStyle ( AttributeSet style, WEditStyle textStyle )
    {
        boolean result;
        String strEditor, strBook;
        int    iEditorStyle, iBookStyle;
        float  fEditor, fBook;
        Color  editorColor, bookColor;

        result = false;

        // size
        iEditorStyle    = StyleConstants.getFontSize ( style );
        iBookStyle      = StyleConstants.getFontSize ( textStyle );

        if ( iBookStyle == iEditorStyle )
        {
            // Размеры совпали. Сравниваем цвет символа.
            editorColor = StyleConstants.getForeground ( style );
            bookColor   = StyleConstants.getForeground ( textStyle );
            if ( bookColor.equals ( editorColor ) )
            {
                // Цвета символов совпали. Сравниваем цвета фона -- его может и не быть. Сравниваем Alignment
                iEditorStyle    = StyleConstants.getAlignment ( style );
                iBookStyle      = StyleConstants.getAlignment ( textStyle );
                if ( iBookStyle == iEditorStyle )
                {
                    // Alignment совпали. Сравниваем название шрифта.
                    strEditor    = StyleConstants.getFontFamily ( style );
                    strBook      = StyleConstants.getFontFamily ( textStyle );
                    if ( strBook.equals ( strEditor ) )
                    {
                        // Названия шрифтов совпали. Сравниваем смещение.
                        fEditor    = StyleConstants.getFirstLineIndent ( style );
                        fBook      = StyleConstants.getFirstLineIndent ( textStyle );

                        if ( fBook == fEditor )
                        {
                            // Смещения совпали. Сравниваем тип шрифта (bold, italic...).
                            if ( StyleConstants.isItalic (style) == StyleConstants.isItalic (textStyle) )
                            {
                                if ( StyleConstants.isBold ( style ) == StyleConstants.isBold ( textStyle ) )
                                {
                                    if ( StyleConstants.isUnderline ( style ) == StyleConstants.isUnderline ( textStyle ) )  result = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    public static int getFontSize ( Font font, int titleSize )
    {
        return font.getSize() * titleSize;
    }

    public static EnumOS getOs ()
    {
        String s = System.getProperty ( "os.name" ).toLowerCase ();

        /*
        -- Параметры доступны для любой ОС.
        file.encoding=UTF-8
        java.version=1.6.0_21-ea
        os.name=Linux                        -- Linux, Windows,..
        os.version=2.6.31.5-0.1-desktop
        path.separator=:
        user.country=RU
        user.dir=/home/svj/projects/SVJ/JavaSample
        user.home=/home/svj
        user.language=ru
        user.name=svj
        user.timezone=
         */

        if ( s.contains ( "win" ) )         return EnumOS.windows;
        if ( s.contains ( "mac" ) )         return EnumOS.macos;
        if ( s.contains ( "solaris" ) )     return EnumOS.solaris;
        if ( s.contains ( "sunos" ) )       return EnumOS.solaris;
        if ( s.contains ( "linux" ) )       return EnumOS.linux;
        if ( s.contains ( "unix" ) )        return EnumOS.linux;

        return EnumOS.unknown;
    }

}
