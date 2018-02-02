package svj.wedit.v6.function.text;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.MessageException;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.dialog.WidgetsDialog;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.gui.tabs.TabsPanel;
import svj.wedit.v6.gui.widget.StringFieldWidget;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.book.TextObject;
import svj.wedit.v6.obj.function.SimpleFunction;
import svj.wedit.v6.tools.BookTools;
import svj.wedit.v6.tools.GuiTools;

import java.awt.event.ActionEvent;

/**
 * Заменить текст в текущей книге.
 *
 * <BR>  Внимание! Проблемы:
 * <BR/> 1) Кусок заменяемого текста может, например первой половиной находиться в одном TextObject, а второй - в другой.
 * И в этом случае он не будет найден.
 * <BR> 2) todo Если заменяем в обьекте, то обьект может быть открыт в виде текста, и тогда как производить замену?
 * <BR>
 * <BR>
 * <BR/> todo В undo-redo необходимо сохранять единым блоком, а не по одной замене.
 * <BR>
 * <BR> User: Zhiganov
 * <BR> Date: 08.04.2016
 * <BR> Time: 14:25:16
 */
public class ReplaceTextFunction extends SimpleFunction
{

    public ReplaceTextFunction ()
    {
        setId ( FunctionId.REPLACE_BOOK_TEXT );
        setName ( "Заменить текст" );
        //setIconFileName ( "trim_text.png" );
        setIconFileName ( "replace.png" );    // бинокль, как в Офисе

        //setName ( "Найти и заменить" );
        //setMapKey ( "Ctrl/R" );      // переносим в
        //setIconFileName ( "replace.png" );    // бинокль, как в Офисе
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        BookContent             currentBookContent;
        BookNode                bookNode;
        TabsPanel<TextPanel>    tabsPanel;
        BookNode                node;
        WidgetsDialog           dialog;

        Log.l.debug ( "Start" );

        // Взять текущую книгу - TreePanel
        //currentBookContentPanel = Par.GM.getFrame().getCurrentBookContentPanel();
        currentBookContent  = Par.GM.getFrame().getCurrentBookContent ();
        bookNode            = currentBookContent.getBookNode();

        // Выяснить, есть ли для данной книги открытые тексты.
        // - Берем все тексты
        tabsPanel = Par.GM.getFrame().getTextTabsPanel ();
        if ( tabsPanel != null )
        {
            for ( TextPanel tp : tabsPanel.getPanels() )
            {
                node = tp.getBookNode ();
                // - node открыта в составе bookNode? Если ДА - исключение.
                try
                {
                    BookTools.checkContainInNode ( node, bookNode );
                } catch ( WEditException e )   {
                    // Есть открытые главы. Предлагем их предварительно закрыть.
                    throw new MessageException ( "У книги '", currentBookContent.getName() + "' есть открытые тексты.\nНеобходимо их все закрыть." );
                }
            }
        }

        // Диалог запроса.
        // - Диалог ввода текста для замены. todo Здесь можно выхватывать из текста текущее слово - под указателем.
        dialog = createRequestDialog ();
        dialog.showDialog ();

        if ( dialog.isOK() )
        {
            String from, to;
            int ic;
            // Берем заданные строки
            from = dialog.getValue ( "From" ).toString();
            to   = dialog.getValue ( "To" ).toString();
            // Все ОК. Рекурсивный прогон по текстам.
            ic   = replaceText ( from, to, bookNode, 0 );

            //DialogTools.showMessage ( "Замена", "From: "+from+"\nTo:"+to );

            /*
            // Пока не знаю как вести подсчет кол-ва замен простым способом.
            if ( ic > 0 )
            {
                // Отметить что было изменение
                currentBookContent.setEdit ( true );
                DialogTools.showMessage ( "Замена", "Было произведено "+ic+" замен." );
            }
            else
            {
                DialogTools.showMessage ( "Замена", "Не было произведено замен." );
            }
            */

            // Отметить что было изменение - всегда, т.к. пока нет возможности выяснить, были ли вообще изменения, и каков их обьем.
            currentBookContent.setEdit ( true );
        }

        Log.l.debug ( "Finish" );
    }

    private int replaceText ( String fromStr, String toStr, BookNode bookNode, int count )
    {
        int result;

        result = count;
        if ( bookNode == null ) return result;

        for ( TextObject to : bookNode.getText() )
        {
            // replace text
            result = result + to.replace ( fromStr, toStr );
        }

        for ( BookNode node : bookNode.getNodes() )
        {
            result = replaceText ( fromStr, toStr, node, result );
        }

        return result;
    }

    private WidgetsDialog createRequestDialog () throws WEditException
    {
        WidgetsDialog     dialog;
        int               titleSize, ic;
        StringFieldWidget widget;

        titleSize   = 0;
        widget      = null;

        dialog      = new WidgetsDialog ( "Заменить текст." );

        widget = new StringFieldWidget ( "Что", "" );    // Что
        widget.setName ( "From" );
        dialog.addWidget ( widget );

        widget = new StringFieldWidget ( "На что", "" );         // На что
        widget.setName ( "To" );
        dialog.addWidget ( widget );

        // перевести размер в символах в размер в пикселях
        ic = GuiTools.getFontSize ( widget.getGuiComponent().getFont(), titleSize );
        //Log.l.debug ( "--- titleSize 2 = %d; ic = %d", titleSize, ic );

        dialog.setTitleWidth ( ic );
        dialog.pack();

        return dialog;
    }

    @Override
    public void rewrite ()      {    }

    @Override
    public void init () throws WEditException    {    }

    @Override
    public void close ()    {    }

    @Override
    public String getToolTipText ()
    {
        return "Заменить текст в рамках текущей книги. Требует чтобы все текстовые панели книги были закрыты.";
    }

}
