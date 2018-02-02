package svj.wedit.v6.obj;


/**
 * Типы обьектов дерева.      -- ??? не ясен смысл данного обьекта
 * <BR/> НЕ enum - т.к. типы могут динамически добавляться (главы, подглавы и т.д.).
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 04.08.2011 17:26:50
 */
public enum TreeObjType
{
    UNKNOW      ( "Неизвестный" ),
    SECTION     ( "Раздел" ),
    BOOK        ( "Книга" ),
    BOOK_NODE   ( "Часть книги" ),
    ;

    private String ruName;

    TreeObjType ( String ruName )
    {
        this.ruName = ruName;
    }

    public String getRuName ()
    {
        return ruName;
    }

}
