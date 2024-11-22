package svj.wedit.v6.function.book.export.html;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.params.ParameterCategory;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.tools.BookTools;

/**
 * Преобразовать выделенные элементы книги в файл формата HTML.
 * <BR/> Если выделены разноуровневые элементы, то работа ведется только с элементами самого высокого уровня.
 * <BR/>
 * <BR/> Внимание! Речь идет имено об элементах однйо книги, а не о нескольких книгах.
 * <BR/>
 * <BR/>
 * <BR/>  Параметры, которые необходимо задавать (в диалоге) - берутся из описания элемента.  -- Варианты конвертации
 * <BR/>
 * <BR/> 1) Межстрочный интервал, в пикселях. (10)
 * <BR/> 2) Header - текст вверху слева и справа от нумерации страниц. (пусто)  -- RTF
 * <BR/> 3) Фонт простого текста. И других элементов текста.
 * <BR/> 4) Начальный номер страницы.
 * <BR/> 5) Вид форматирования текста (JUSTIFIED)
 * <BR/> 6) Красная строка (в пробелах)
 * <BR/>
 * <BR/> Парсинг параметров из xml-файла - UserParamsStaxParser
 * <BR/>
 * <BR/> Варианты конвертации должны формироваться внутри этой функции. А пользователь уже выбирает вариант из списка, либо создает новый.
 * <BR/>
 * <BR/> todo  Перевести на AToHtml2Commander
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 26.09.2013 12:56
 */
public class SelectedToHtmlFunction extends AToHtmlCommander
{
    public SelectedToHtmlFunction ()
    {
        setId ( FunctionId.CONVERT_SELECTED_TO_HTML );
        setName ( "Преобразовать выделенное в HTML" );
        setIconFileName ( "to_html.png" );
        setParamsType ( ParameterCategory.BOOK );
    }

    @Override
    protected BookNode[] getSelectedNodes ( BookNode bookNode )   throws WEditException
    {
        BookNode[]  result;
        TreeObj[]   to;
        int         ic;

        to      = BookTools.getSelectedNodesForCut ( true );
        if ( to == null )  throw new WEditException ( "Нет выбранных элементов книги." );

        result  = new BookNode[to.length];
        ic      = 0;
        for ( TreeObj node : to )
        {
            result[ic] = ( BookNode ) node.getUserObject();
            ic++;
        }
        return result;
    }

    // todo общего пользвоания
    protected TreeObj[] getNodesToConvert ( BookContent bookContent ) throws WEditException
    {
        TreeObj[]   to;

        to      = BookTools.getSelectedNodesForCut ( true );
        if ( to == null )
        {
            throw new WEditException ( "Нет выбранных элементов книги." );
        }

        return to;
    }

}
