package svj.wedit.v6.tree;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.book.export.obj.ConvertParameter;
import svj.wedit.v6.function.book.export.obj.TitleViewMode;
import svj.wedit.v6.obj.book.TextObject;

import java.util.Collection;

/**
 * Обработчик обьектов дерева.
 * <BR/> Менеджер дерева пробегает по дереву от заданного элемента (имеет опции - список типов обьектов, которые игнорируем).
 * <BR/> При попадании - титл, аннтотация, текст, он дергает данный обработчик
 * <BR/>
 * <BR/>
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 09.08.2011 14:09:42
 * @deprecated Т.к. есть унвиерсальный механизм AbstractConvertFunction
 */
public interface TreeNodeProcessor
{
    /** В самом конце обработки - заключительный пинок. Для тех кто на самом деле только собирал данные. И теперь только скинет их в выходнйо поток.
     * @param cp*/
    void finished ( ConvertParameter cp ) throws WEditException;

    /**
     * @param titleViewMode     Режим отображения данного узла - не выводить, выводить только звездочки, выводить.
     * @param title             Заголовок.
     * @param elementParamName  Название элемента узла (Часть, Глава...)
     * @param number            Номер данного элемента от самого начала
     * @param level
     * @param cp
     */
    void title ( TitleViewMode titleViewMode, String title, String elementParamName, String number, int level, ConvertParameter cp );

    void text ( Collection<TextObject> text );
}
