package svj.wedit.v6.util;


import java.io.File;
import java.text.Collator;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Этот класс предназначен для сортировки списка файлов - по имени, по размеру, по дате.
 *
 * <BR/> Для поддержки различных кодировок очень удобно использовать классы Collator и Locale из пакета java.util.
 * <BR/> Метод compare класса Collator позволяет выполнить сортировку строк в соответствии с алфавитом языка, который установлен в настройках системы.
 *
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.03.2011 16:22:37
 */
public class FileSorter implements Comparator<File>
{
    // класс для работы с регулярными выражениями - для подсчета в полном имени файла кол-ва символов-разделителей
    private Pattern p = null;

    // класс для работы со строками на разных языках
    private Collator collator = null;

    // сортировка по имени файла, сортировка по размеру файла
    public enum SortType { SORT_BY_NAME, SORT_BY_SIZE, SORT_BY_DATE }
    private SortType sortType = SortType.SORT_BY_NAME; //в этой переменной сохраняем текущий тип сортировки


    public FileSorter ( SortType type )
    {
        sortType    = type;

        // определяем системный символ разделитель и создаем шаблон на его основе
        String separator = File.separator;

        //if (separator.equals("\\"))   separator = "\\";

        // создаем шаблон на основе символа-разделителя
        p = Pattern.compile ( separator, Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE );

        // получаем системные настройки (язык и страну)
        String country  = System.getProperty("user.country");
        String language = System.getProperty("user.language");

        // создаем экземпляр класса для сравнения строк на основе региональных настроек
        collator = Collator.getInstance(new Locale(country, language));
    }


    @Override
    public int compare ( File o1, File o2 )
    {
        int result;
        switch ( sortType )
        {
            default:
            case SORT_BY_NAME:               // сравнение объектов по их имени
                result = compareByName ( o1, o2 );
                break;

            case SORT_BY_SIZE:               // сравнение объектов по их размеру
                result = compareBySize ( o1, o2 );
                break;

            case SORT_BY_DATE:               // сравнение объектов по дате
                result = compareByDate ( o1, o2 );
                break;
        }
        return result;
    }

    /* По дате, в порядке убывания - самая старая дата - в конце списка */
    private int compareByDate ( File f1, File f2 )
    {
        // если объекты не равны null и имеют тип File
        if ( f1 != null && f2 != null )
        {
            // берем даты как long и сравниваем
            return Long.valueOf(f2.lastModified()).compareTo( f1.lastModified());
        }

        return 0;
    }

    private int compareBySize ( File f1, File f2 )
    {
        //если объекты не равны null и имеют тип File
        if ( f1 != null && f2 != null )
        {
            // берем длины как long и сравниваем
            return Long.valueOf(f2.length()).compareTo( f1.length());
        }

        return 0;
    }

    /**
     * Этот метод выполняет сравнение имен двух файлов.
     *
     * В первую очередь, проверяем равенство объектов (если имена файлов одинаковы, то и файлы равны).
     * Если файлы разные, определяем их глубину вложения. Как вы помните, сначала должны идти файлы с меньшей
     * глубиной вложения. Наконец, если файлы находятся на одной глубине, сравниваем сами имена файлов.
     * Для определения глубины вложения файлов нам нужно узнать количество символов-разделителей в полном
     * имени файла. Обратите внимание, определять символ-разделитель нужно с помощью переменной File.separator,
     * т.к. он зависит от операционной системы.
     *
     * Возвращает:
     *     1 если первый параметр (о1) больше второго (о2),
     *    -1 если первый параметр (о1) меньше второго (о2),
     *     0 если они равны.
     * Имя первого файла считается больше второго имени, если первый файл находится ближе к корню дерева папок.
     * Если файлы находятся в одной папке, то больше то имя, которое идет первым по алфавиту.
     *
     * @param f1 объект типа File
     * @param f2 объект типа File
     * @return результат сравнения
     */
    public int compareByName ( File f1, File f2 )
    {
        String      fullPath1, fullPath2;
        String[]    res1, res2;

        //если объекты не равны null и имеют тип File
        if ( f1 != null && f2 != null )
        {
            // получаем полный путь к имени файла
            fullPath1 = f1.getAbsolutePath();
            fullPath2 = f2.getAbsolutePath();

            // проверяем равенство имен
            if ( fullPath1.equals(fullPath2) )
            {
                //возвращаем 0, т.к. имена одинаковы
                return 0;
            }

            //определяем глубину размещения файла в дереве папок
            //для этого разбиваем полный путь к файлу на
            //лексемы, и определяем их количество
            res1 = p.split(fullPath1);
            res2 = p.split(fullPath2);
            if(res1.length > res2.length)
            {
                //возвращаем 1, если глубина вложения первого файла больше глубины вложения второго
                return 1;
            }

            if ( res1.length < res2.length )
            {
                //возвращаем "-1" в противном случае
                return -1;
            }

            if ( res1.length == res2.length )
            {
                //если файлы находятся на одинаковой глубине, сортируем их в соответствии с алфавитом
                return collator.compare(fullPath1, fullPath2);
            }
        }

        // здесь мы возвращаем 0, т.к. сравнение объектов выполнить невозможно (т.е. считаем, что объекты
        // одинаковые, во всяком случае, сортировать их нет смысла)
        return 0;
    }



    /**
     * Этот метод выполняет сортировку списка файлов
     * @param fileList не отсортированный список файлов
     * @return отсортированный список файлов
     */
    public Collection<File> sort ( List<File> fileList )
    {
        if ( fileList == null )  return new ArrayList<File>();

        // создаем список для результатов (такого же размера как и исходный список)
        ArrayList<File> res = new ArrayList<File>(fileList.size());
        // копируем список
        res.addAll ( fileList );
        // выполняем сортировку
        Collections.sort ( res, this );
        //возвращаем результат

        return res;
    }

    public Collection<File> sort ( File[] files )
    {
        if ( files == null )  return new ArrayList<File>();

        List<File> list    = Arrays.asList ( files );
        return sort ( list );
    }

}

