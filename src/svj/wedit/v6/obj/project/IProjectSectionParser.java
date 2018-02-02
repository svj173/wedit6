package svj.wedit.v6.obj.project;


import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.Section;
import svj.wedit.v6.obj.book.BookTitle;


/**
 * Обработчик событий пробега по дереву октрытых элементво от Сборника до открытых текстов.
 * <BR/> Для всевозможных обработок:
 * <BR/> - Формирование userParams по открытым частям.
 * <BR/> - 
 * <BR/>
 * <BR/> T - результирующий обьект.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 06.04.2012 23:01:28
 */
public interface IProjectSectionParser<T>
{
    T getResult ();

    void startDocument ();
    void endDocument ();

    void startProject ( Project project );
    void endProject ( Project project, int booksCount );

    void startSection ( Section section, int level );
    void endSection ( Section section, int level );

    void startBook ( BookTitle book, int level );
    void endBook ( BookTitle book, int level );

}
