package svj.wedit.v6.function.book.view;

import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.obj.WTreeObj;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.book.TextObject;
import svj.wedit.v6.obj.function.SimpleFunction;
import svj.wedit.v6.tools.DialogTools;

import java.awt.event.ActionEvent;
import java.util.*;

/**
 * Подсчет статистики файла-книги:
 * - Чистый текст
 * - Скрытый текст
 * - Общий размер (берется из размера файла)
 * - Остаток (то что осталось при вычете из размера файла)
 *
 * Потом
 * - Частей
 * - Глав
 * - пр...
 */
public class BookStatisticFunction extends SimpleFunction {

    private int WORK_SIZE = 1;
    private int HIDDEN_SIZE = 2;
    private int TOTAL_SIZE = 3;
    //private int WRONG_SIZE = 4;

    public BookStatisticFunction()
    {
        setId ( FunctionId.BOOK_STATISTIC );
        setName ( "Статистика текущей книги");
        setIconFileName ( "edit.png" );
    }

    @Override
    public void handle(ActionEvent event) throws WEditException
    {

        BookContent bookContent;
        Map<Integer, Long> statInfo = new HashMap<>();

        bookContent  = Par.GM.getFrame().getCurrentBookContent ();

        // пробегаем по всем текстам - исключаем заголовки и аннотации.
        processNode ( bookContent.getBookNode(), statInfo, WORK_SIZE );

        long size = bookContent.getFileSize();
        Long workSize = statInfo.get(WORK_SIZE);
        Long hiddenSize = statInfo.get(HIDDEN_SIZE);

        StringBuilder sb = new StringBuilder(512);

        // Сообщение о завершении работы.
        sb.append ( "Книга : " );
        sb.append ( bookContent.getName() );
        sb.append ( "\n   Файл: " );
        sb.append ( bookContent.getFileName() );
        sb.append ( "\n   Чистый текст: " );
        sb.append ( workSize );
        sb.append ( "\n   Скрытый текст: " );
        sb.append ( hiddenSize );
        sb.append ( "\n   Общий размер: " );
        sb.append ( size );
        sb.append ( "\n   Остаток: \n" );
        sb.append ( size - workSize - hiddenSize );

        DialogTools.showMessage ( "Статистика", sb.toString () );
    }

    private void processNode(BookNode bookNode, Map<Integer, Long> statInfo, int parentSizeType) {
        BookNode node;
        Collection<WTreeObj> childs;


        int sizeType = processSize ( bookNode, statInfo, parentSizeType );

        // Проверка на вложенные обьекты
        childs = bookNode.getChildrens ();
        for ( WTreeObj wo : childs )
        {
            node = (BookNode) wo;
            processNode ( node, statInfo, sizeType );
        }
    }

    private int processSize(BookNode bookNode, Map<Integer, Long> statInfo, int parentSizeType) {
        String                  str;
        Collection<TextObject>  text;
        String   type;
        int newSizeType;
        long ic = 0L;

        if (parentSizeType == HIDDEN_SIZE) {
            newSizeType = HIDDEN_SIZE;
        }
        else {
            // берем тип обьекта.
            type = bookNode.getElementType ();
            if ((type != null) && type.equalsIgnoreCase("hidden")) {
                // Обработка скрытого текста
                // Внимание!!! Тип Скрытый имеется толкьо у головного элемента.
                // У подэлементов тип обычный.
                // Необходимо передавать тип Скрытый вглубь
                newSizeType = HIDDEN_SIZE;
            } else {
                newSizeType = WORK_SIZE;
            }
        }

        // Взять текст
        text = bookNode.getText();
        if ((text != null) && (!text.isEmpty())) {
            for (TextObject textObj : text) {
                str = textObj.getText();
                ic = ic + str.length();
            }
        }

        addStatInfo(statInfo, newSizeType, ic);

        return newSizeType;
    }

    private void addStatInfo(Map<Integer, Long> statInfo, int sizeType, long ic) {
        Long value = statInfo.get(sizeType);
        if (value == null) value = 0L;
        value = value + ic;
        statInfo.put(sizeType, value);
    }

    @Override
    public void rewrite() {

    }

    @Override
    public String getToolTipText ()
    {
        return "Подсчет статистики файла-книги";
    }

}
