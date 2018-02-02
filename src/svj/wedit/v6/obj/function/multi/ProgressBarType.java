package svj.wedit.v6.obj.function.multi;


/**
 * Тип отображения информации в прогресс-баре.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 28.08.2013 15:25
 */
public enum ProgressBarType
{
    UNTIME,     // бесконечный бегунок - показываем: В титле - ничего. В бегунке - только прошедшие секунды -- или в титле?
    DATA,       // передача простых данных - показываем: В титле - прошедшие секунды. В бегунке - проценты от передачи.
    PACKET      // передача пакетных данных - показываем: В титле - кол-во пакетов, размер пакета, прошедшие секунды. В бегунке - проценты от передачи.
}
