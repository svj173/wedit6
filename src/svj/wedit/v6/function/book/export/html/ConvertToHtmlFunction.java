package svj.wedit.v6.function.book.export.html;


import svj.wedit.v6.function.FunctionId;

/**
 * Преобразовать книгу в файл формата HTML.
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
 * <BR/> Варианты конвертации должны формироваться внутри этой функции.
 * А пользователь уже выбирает вариант из списка, либо создает новый.
 * <BR/>
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 26.09.2013 12:56
 */
public class ConvertToHtmlFunction extends AToHtml2Commander // AToHtmlCommander
{
    public ConvertToHtmlFunction ()
    {
        super ( FunctionId.CONVERT_TO_HTML, "Преобразовать книгу в HTML", "to_html.png", false );

        /*
        setId ( FunctionId.CONVERT_TO_HTML );
        setName ( "Преобразовать книгу в HTML" );
        setIconFileName ( "to_html.png" );
        setParamsType ( ParameterCategory.BOOK );
        */
    }

    /*
    @Override
    protected BookNode[] getSelectedNodes ( BookNode bookNode )  throws WEditException
    {
        BookNode[] result;
        // выдаем корень книги
        result      = new BookNode[1];
        result[0]   = bookNode;
        return result;
    }
    */
}
