package svj.wedit.v6.function.text;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.MessageException;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.gui.tabs.TabsPanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.book.TextObject;
import svj.wedit.v6.obj.book.WEditStyle;
import svj.wedit.v6.obj.function.SimpleFunction;
import svj.wedit.v6.tools.BookTools;
import svj.wedit.v6.tools.DialogTools;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import java.awt.event.ActionEvent;

/**
 * В текущей книге скинуть весь текст в тип текста - По умолчанию (Простой).
 *
 * <BR>
 * <BR> User: Zhiganov
 * <BR> Date: 27.12.2017
 * <BR> Time: 11:25:16
 */
public class SetAllTextAsSimpleFunction extends SimpleFunction
{
    public SetAllTextAsSimpleFunction ()
    {
        setId ( FunctionId.SET_ALL_TEXT_AS_SIMPLE );
        setName ( "Стиль всего текста - по умолчанию." );
        setIconFileName ( "replace.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        BookContent             currentBookContent;
        BookNode                bookNode;
        TabsPanel<TextPanel>    tabsPanel;
        BookNode                node;

        Log.l.debug ( "Start" );

        // Взять текущую книгу - TreePanel
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

        replaceText ( bookNode );

        // Отметить что было изменение - всегда, т.к. пока нет возможности выяснить, были ли вообще изменения, и каков их обьем.
        currentBookContent.setEdit ( true );

        DialogTools.showMessage ( getName(), "Успешно завершилась." );

        Log.l.debug ( "Finish" );
    }

    private void replaceText ( BookNode bookNode )  throws WEditException
    {
        if ( bookNode == null ) return;

        AttributeSet        style;
        SimpleAttributeSet  sStyle;
        WEditStyle          wStyle;

        for ( TextObject to : bookNode.getText() )
        {
            // Вобще удаляем тип стиля текста - т.е. сбрасываем его к типу по-умолчанию.
            to.setStyle ( null );
            /*
            // replace text
            style = to.getStyle();
            if ( style instanceof SimpleAttributeSet )
            {
                sStyle = (SimpleAttributeSet) style;
                sStyle.removeAttribute ( StyleName.STYLE_NAME );
            }
            if ( style instanceof WEditStyle )
            {
                wStyle = (WEditStyle) style;
                wStyle.setStyleName ( null );
            }
            */
        }

        for ( BookNode node : bookNode.getNodes() )
        {
            replaceText ( node );
        }
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
        return "Скинуть стиль текста в состояние 'по-умолчанию' в рамках текущей книги. Требует чтобы все текстовые панели книги были закрыты.";
    }

}
