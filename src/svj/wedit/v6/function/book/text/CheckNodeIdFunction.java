package svj.wedit.v6.function.book.text;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.MessageException;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.gui.tabs.TabsPanel;
import svj.wedit.v6.obj.WTreeObj;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.BookTools;
import svj.wedit.v6.tools.DialogTools;

import java.awt.event.ActionEvent;
import java.util.*;


/**
 * Проверить все ИД эпизодов, и если какие-то совпадают (например, одинаково называются но находятся в
 * разных местах книги - старый функционал таким присваивал одинаковый ИД = название + время в мсек).
 * <BR/> В этом случае генерятся уникальные ИД: плюс = номер + время в наносек
 * <BR/> Работает только если все эпизоды закрыты.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 08.09.2021 17:21:07
 */
public class CheckNodeIdFunction extends Function
{
    public CheckNodeIdFunction()
    {
        setId ( FunctionId.CHECK_NODE_ID );
        setName ( "Проверка ИД эпизодов");
        setIconFileName ( "reload.png" );
    }

    @Override
    public void handle ( ActionEvent event ) throws WEditException
    {
        BookContent currentBookContent;
        TabsPanel<TextPanel> tabs;
        BookNode bookNode;

        currentBookContent  = Par.GM.getFrame().getCurrentBookContent();

        // Проверка открытых эпизодов
        tabs = Par.GM.getFrame().getTextTabsPanel();
        if ((tabs != null) && (tabs.isNotEmpty()))
        {
            throw new MessageException( "У книги '", currentBookContent.getName() + "' есть открытые тексты.\nНеобходимо их все закрыть."
                    + "\n\n" + tabs );
        }

        // рекурсивно пробегаем по эпизодам и анализируем ИД

        // ИД_эпизода = кол-во попаданий
        Map<String, Integer> maps = new HashMap<>();
        // ИД_эпизода = название_эпизода - для отчета
        Map<String, String> mapsName = new HashMap<>();
        // ИД_эпизода = List<bookNode>
        Map<String, Collection<BookNode>> mapsNode = new HashMap<>();

        bookNode            = currentBookContent.getBookNode();

        // пробегаем по всем текстам - исключаем заголовки и аннотации.
        processNode ( bookNode, maps, mapsName, mapsNode );

        StringBuilder sb = new StringBuilder(128);

        int size, count, i1;
        count = 0;
        i1 = 0;
        String newId;
        Collection<BookNode> nodes;
        // анализ - были ли изменения
        for (Map.Entry<String, Integer> entry: maps.entrySet()) {
            size = entry.getValue();
            // отчет
            if (size > 1) {
                sb.append("'");
                sb.append(mapsName.get(entry.getKey()));
                sb.append("'");
                sb.append(" = ");
                sb.append(size);
                sb.append("\n");
                count++;

                // берем эпизоды
                nodes = mapsNode.get(entry.getKey());
                for (BookNode node : nodes) {
                    i1++;
                    // генерим другой ИД
                    newId = BookTools.createBookNodeId ( node.getName(), i1 );
                    node.setId(newId);
                    sb.append(". . . . '");
                    sb.append(node.getName());
                    sb.append("' = ");
                    sb.append(newId);
                    sb.append("'");
                    sb.append("\n");
                }
            }
        }

        // отмечаем что были изменения.
        if (count > 0 ) {
            currentBookContent.setEdit(true);
        }

        DialogTools.showMessage ( getName(), "Успешно завершилась.\nДубликатных глав: "+count + "\n\n" + sb );


        //Log.l.info("[C] tabs = %s", tabs);
        //Log.l.info("[C] currentBookContent = %s", currentBookContent);
    }

    private void processNode(BookNode bookNode, Map<String, Integer> maps,
                             Map<String, String> mapsName,
                             Map<String, Collection<BookNode>> mapsNode) {

        BookNode node;
        String   type;
        Collection<WTreeObj> childs;
        Integer count;

        // берем тип обьекта.
        type = bookNode.getElementType ();
        if ( (type != null) && type.equalsIgnoreCase ( "hidden" ) )  return;

        String id = bookNode.getId();
        if (maps.containsKey(id)) {
            count = maps.get(id);
            count++;
        }
        else
        {
            count = 1;
        }
        maps.put(id, count);

        mapsName.put(id, bookNode.getName());

        Collection<BookNode> list;
        if (count > 1) {
            list = mapsNode.get(id);
            list.add(bookNode);
        } else {
            list = new ArrayList<>();
            list.add(bookNode);
            mapsNode.put(id, list);
        }

        // Проверка на вложенные обьекты
        childs = bookNode.getChildrens ();
        for ( WTreeObj wo : childs )
        {
            node = (BookNode) wo;
            processNode ( node, maps, mapsName, mapsNode );
        }
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

    @Override
    public String getToolTipText ()
    {
        return "Проверка ИД эпизодов";
    }

}
