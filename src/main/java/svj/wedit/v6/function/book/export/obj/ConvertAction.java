package svj.wedit.v6.function.book.export.obj;


/**
 * Акции в диалоге конвертации книги.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 10.10.2013 19:12
 */
public enum ConvertAction
{
    CREATE,               // создать нвоую закладку
    EDIT,
    SAVE,
    CANCEL,
    DELETE,
    SELECT_NEW_BOOKMARK,   // изменился обьект в списке закладок
    CONVERT_LIST_EDIT,      // вызов диалога редаткивроания списко вариантов конвертации.
    COPY,
    PASTE
}
