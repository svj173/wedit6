package svj.wedit.v6.function.book.text;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.MessageException;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.gui.tabs.TabsPanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.WTreeObj;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.book.TextObject;
import svj.wedit.v6.obj.function.SimpleFunction;
import svj.wedit.v6.tools.BookTools;
import svj.wedit.v6.tools.DialogTools;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Заменить текст в книге - как подготовка к печати.
 * <BR/> Меняем:
 * <BR/> 1) два пробела на один
 * <BR/> 2) тире с пробелом на дефис с пробелом
 * <BR/> 3) кавычки на стандартные
 * <BR/>
 * Замены:
 1) двойной пробел
 2) тире (с пробелом)
 -
 на
 –
 3) кавычки
 ”   - 02DD
 «
 »
 ‘
 ’
 две запятых вверху, направленные вверх - 201C, вниз - 201D
 две запятых внизу - 201E
 на
 "

 * <BR/> Можно правила замены хранить в виде параметров и добавлять новые, удалять старые.
 * <BR/>
 * <BR/> Применяется, толкьо если все тексты закрыты.
 * <BR/> todo В undo-redo необходимо сохранять единым блоком, а не по одной замене.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 31.10.2016 10:57
 */
public class ReplaceBlockTextFunction extends SimpleFunction
{
    // что меняем и на что.
    private final Map<Character,Character>  mapChars    = new HashMap<Character,Character> ();
    private final Map<String,String>        mapText     = new HashMap<String,String> ();

    public ReplaceBlockTextFunction ()
    {
        setId ( FunctionId.BLOCK_REPLACE );
        setName ( "Блочная замена" );
        //setMapKey ( "Ctrl/R" );      // переносим в
        setIconFileName ( "replace.png" );    // бинокль, как в Офисе

        Character ch = '"';

        // кавычки
        mapChars.put ( (char) 0x02DD, ch );      // ”
        mapChars.put ( '«', ch );
        mapChars.put ( '»', ch );
        mapChars.put ( '‘', ch );
        mapChars.put ( '’', ch );
        mapChars.put ( (char) 0x201C, ch );
        mapChars.put ( (char) 0x201D, ch );
        mapChars.put ( (char) 0x201E, ch );

        // двойной пробел
        mapText.put ( "  ", " " );
        // тире
        mapText.put ( "- ", "– " );
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

        // пробегаем по всем текстам - исключаем заголовки и аннотации.
        processNode ( bookNode );

        // отмечаем что были изменения.
        currentBookContent.setEdit ( true );

        DialogTools.showMessage ( getName(), "Успешно завершилась." );
    }

    private void processNode ( BookNode bookNode )
    {
        BookNode node;
        String   type;
        Collection<WTreeObj> childs;

        // берем тип обьекта.
        type = bookNode.getElementType ();
        if ( (type != null) && type.equalsIgnoreCase ( "hidden" ) )  return;

        replace ( bookNode );

        // Проверка на вложенные обьекты
        childs = bookNode.getChildrens ();
        for ( WTreeObj wo : childs )
        {
            node = (BookNode) wo;
            processNode ( node );
        }
    }

    /**
     * todo В undo-redo необходимо сохранять единым блоком, а не по одной замене.
     * @param bookNode  Обьект книги.
     */
    private void replace ( BookNode bookNode )
    {
        String                  str;
        Collection<TextObject>  text;

        // замены только в тексте
        // Взять текст
        text = bookNode.getText();
        if ( ( text != null ) && ( !text.isEmpty () ) )
        {
            for ( TextObject textObj : text )
            {
                str     = textObj.getText();
                for ( Map.Entry<Character,Character> entry : mapChars.entrySet() )
                {
                    str = str.replace ( entry.getKey(), entry.getValue() );
                }
                for ( Map.Entry<String,String> entry : mapText.entrySet() )
                {
                    str = str.replaceAll ( entry.getKey(), entry.getValue() );
                }
                // Занести изменения обратно в обьект
                textObj.setText ( str );
            }
        }
    }

    @Override
    public void rewrite ()     {    }

}
