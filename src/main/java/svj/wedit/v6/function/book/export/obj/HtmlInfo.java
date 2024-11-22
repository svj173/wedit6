package svj.wedit.v6.function.book.export.obj;


import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Обьект описания правил конвертации текста в HTML.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 25.04.2013 14:15
 */
public class HtmlInfo
{
    private String codeType = "UTF-8"; //"Windows-1251";  KOI8-R

    /* Нумерация для элементов (для Глав - своя). */
    private final Map<Integer,Integer>    numbers;

    /* Префиксы для элементов. Например: Часть, Глава... */
    private final Map<Integer,String>     prefix;
    private final Map<Integer,Boolean>    useTitle;
    private final Map<Integer,Boolean>    useZvezd;
    private final Collection<String>      ignoreElement;
    private final Map<Integer,Integer>    spaceBefore;  // Кол-во пропусков-строк перед заголовком.
    private final Map<Integer,Integer>    spaceAfter;   // Кол-во пропусков-строк после заголовка.

    /* Надо ли собрать Оглавление книги - для печати в конце. */
    private boolean useContent  = true;
    private String BR ;


    public HtmlInfo ( String BR )
    {
        this.BR     = BR;
        numbers     = new HashMap<Integer,Integer>();
        spaceBefore = new HashMap<Integer,Integer>();
        spaceAfter  = new HashMap<Integer,Integer>();
        prefix      = new HashMap<Integer,String>();
        useTitle    = new HashMap<Integer,Boolean>();
        useZvezd        = new HashMap<Integer,Boolean>();
        ignoreElement   = new LinkedList<String> ();
    }

    /**
     *
     * @param elementType
     * @return
     */
    public boolean ignoreElement ( String elementType )
    {
        boolean result;

        result = ignoreElement.contains ( elementType );
        //Log.file.debug ( "------ elementType = '", elementType, "'; ignore result = ", result );

        return result;
    }

    /* Выводить титл данного уровня или нет */
    public boolean useElementTitle ( int nodeLevel )
    {
        boolean result;
        Boolean bb;

        result  = false;
        //result  = true;

        // Ограничения на вывод титлов
        //if ( (elementName.equals("book")) || (elementName.equals("part")) || (elementName.equals("chapter"))  )
        //if ( (elementName.equals("book")) || (elementName.equals("part"))  )    result  = true;

        bb = useTitle.get ( nodeLevel );
        if ( bb != null )  result = bb;

        return result;
    }

    public boolean useZv ( int nodeLevel )
    {
        Boolean result;
        //result  = false;
        result  = useZvezd.get(nodeLevel);
        if ( result == null )  result = false;
        //if ( elementName.equals("chapter") )      result  = true;
        return result;
    }

    /* Выводить префикс к титлу или нет - часть, глава... */
    public boolean usePrefix ( int nodeLevel )
    {
        //return true;
        return prefix.get ( nodeLevel ) != null;
    }

    public String getPrefix ( int nodeLevel )
    {
        String result;
        result  = prefix.get ( nodeLevel );
        if ( result == null )   result  = "";
        return result;
    }

    public boolean useNumber ( int nodeLevel )
    {
        return numbers.get ( nodeLevel ) != null;
    }

    public String getNumber ( int nodeLevel )
    {
        StringBuilder   result;
        Integer         i1;

        result  = new StringBuilder (8);
        i1      = numbers.get (nodeLevel);
        if ( i1 != null )
        {
            result.append ( " " );
            result.append ( i1 );
            result.append ( ". " );

            i1  = i1 + 1;
            numbers.put ( nodeLevel, i1 );
        }

        return result.toString();
    }

    public boolean isCreateContent ()
    {
        return useContent;
    }

    /* Нет пропуска - пустая строка. */
    public String getVkBeforeAsHtml ( int nodeLevel )
    {
        Integer ic;

        ic = spaceBefore.get ( nodeLevel );
        if ( ic == null )  ic = 0;

        return createSpaceLine ( ic );
    }

    public String getVkAfterAsHtml ( int nodeLevel )
    {
        Integer ic;

        ic = spaceAfter.get ( nodeLevel );
        if ( ic == null )  ic = 0;

        return createSpaceLine ( ic );
    }

    public String createSpaceLine ( int size )
    {
        StringBuilder   result;

        result  = new StringBuilder (16);

        if ( size > 0 )
        {
            for ( int i=0; i<size; i++ )
            {
                result.append ( BR );
                result.append ( "\n&nbsp;&nbsp;&nbsp;" );
            }
        }

        return result.toString();
    }

    public String getCodeType ()
    {
        return codeType;
    }

    public void addPrefix ( int nodeLevel, String name )
    {
        prefix.put ( nodeLevel, name);
    }

    public int getContentWidth ()
    {
       return 3;
    }

    public void setUseTitle ( int nodeLevel, boolean isUseTitle )
    {
        useTitle.put ( nodeLevel, isUseTitle );
    }

    public void setNumbers ( int nodeLevel, int startNumber )
    {
        numbers.put ( nodeLevel, startNumber );
    }

    public void setIgnoreElement ( String elementType )
    {
        ignoreElement.add ( elementType );
    }

    public void setSpaceBefore ( int nodeLevel, int space )
    {
        spaceBefore.put ( nodeLevel, space );
    }

    public void setSpaceAfter ( int nodeLevel, int space )
    {
        spaceAfter.put ( nodeLevel, space );
    }

    public void setUseContent ( boolean isUseContent )
    {
        useContent = isUseContent;
    }

}
