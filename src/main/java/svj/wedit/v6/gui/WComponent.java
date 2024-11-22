package svj.wedit.v6.gui;


/**
 * Интерфейс перерисовки объектов
 * <BR/> Ошибки здесь не нужны - т.к. неизвестно, что делать дальше.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 04.08.2011 11:46:57
 */
public interface WComponent
{
    /* Обновить экран, учитывая смену языка.  */
    void rewrite();// throws WEditException;

    // возможно сюда и init(obj)
}
