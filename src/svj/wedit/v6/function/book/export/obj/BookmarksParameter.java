package svj.wedit.v6.function.book.export.obj;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.function.params.ParameterType;
import svj.wedit.v6.function.params.SimpleParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.WType;
import svj.wedit.v6.obj.book.element.WBookElement;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.DumpTools;

import java.io.*;
import java.util.*;

/**
 * Обьект описания всех закладок.
 * <BR/> Применяется в функциях конвертации - как запомненные измененные параметры пользователя.
 * <BR/>
 * <BR/> Содержит в себе массив параметров ConvertParameter, которые уже в свою очередь хранят информацию о конвертации - какие
 * элементы выводить, какие не выводить, оглавление и прочее.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 10.10.2013 15:56
 */
public class BookmarksParameter extends FunctionParameter<Object>
{
    // Список bookmarks
    private final Collection<ConvertParameter>  list    = new TreeSet<ConvertParameter>();
    private String currentBookmark = null;


    public BookmarksParameter ( String paramName )
    {
        super ( paramName );
    }


    @Override
    public BookmarksParameter clone ()
    {
        BookmarksParameter result;

        result = new BookmarksParameter ( getName() );

        super.mergeToOther ( result );

        for ( ConvertParameter p : getList() )
        {
            result.addNew ( p.clone() );
        }

        result.setCurrentBookmark ( getCurrentBookmark() );

        return result;
    }


    public void addNew ( ConvertParameter cp )
    {
        list.add ( cp );
    }

    /**
     * При парсинге XML файла user_params.xml
     * @param paramName
     * @param param
     */
    public void setParameter ( String paramName, FunctionParameter param )
    {
        if ( param != null )
        {
            if ( param instanceof ConvertParameter )
            {
                list.add ( (ConvertParameter) param );
            }
        }
    }

    @Override
    public void toXml ( int level, OutputStream out ) throws WEditException
    {
        int    ic1, ic2;

        try
        {
            ic1 = level+1;
            ic2 = ic1+1;

            outString ( level, Convert.concatObj ( "<param name=\"", getName(), "\" type=\"", ParameterType.CONVERT_BOOKMARKS, "\">\n" ), out );
            //outString ( level, Convert.concatObj ( "<param name=\"", getName (), "\" >\n" ), out );
            outString ( ic1, "<bookmarks>\n", out );

            for ( ConvertParameter cp : list )
            {
                cp.toXml ( ic2, out );
            }
            outString ( ic1, "</bookmarks>\n", out );

            // Дефолтное значение закладки - с которым последним работали
            if ( currentBookmark != null )
                outTag ( ic1, "selected", currentBookmark, out );

            outString ( level, "</param>\n", out );

        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML представления Параметра '", getName(), "' в поток :\n", e );
        }
    }

    public Collection<ConvertParameter> getList ()
    {
        return list;
    }

    public void setCurrentBookmark ( String currentBookmark )
    {
        this.currentBookmark = currentBookmark;
    }

    /**
     * todo Мержим структуру книги с имеющимися данными (во всех закладках) - вдруг расхождения. Если ДА - отмечаем, данные сбрасываем в дефолт.
     * <br/> Сейчас в данном параметре - находится структура элементов, полученная их файла '.wedit6/user_property.xml'.
     * <br/>
     * <br/>
     * <br/> Положения мержинга:
     * <br/> 1) Исходим из того что главное - это структура, взятая из книги.
     * <br/> 2) Мержим только соответствие уровней.
     * <br/> 3) Необходимо из структуры книги занести все названия элементов, их типы и т.д. (цвета и шрифты не нужны, т.к. они прописаны только для гуи-отображения).
     * <br/> - название элемента
     * <br/> - тип элемента
     * <br/>
     * <br/> Алгоритм мержинга:
     * <br/>
     * @param bookElementList  Структура элементов, полученная из обьекта Книги.
     * @param types            Список всех типов элементов.
     */
    public void merge ( Collection<WBookElement> bookElementList, Map<String, WType> types )
    {
        Collection<ElementConvertParameter> oldElementList, elementList, forDeleteList;
        Collection<SimpleParameter>         oldTypeList, typeList;
        ElementConvertParameter             element;
        SimpleParameter                     spType;
        WBookElement                        wbElement;

        // Цикл по всем закладкам.
        for ( ConvertParameter cp : list )
        {
            elementList     = new LinkedList<ElementConvertParameter> ();

            // Взять список элементов из закладки -- должен быть TreeSet - упорядоченный
            oldElementList  = cp.getElementList();

            /*
            // Анализ на удаленные элементы
            forDeleteList   = new ArrayList<ElementConvertParameter>();
            for ( ElementConvertParameter param : oldElementList )
            {
                Log.l.info ( "- param = %s", param );
                // - Ищем подобный элемент среди реальных
                wbElement = findElement ( param, bookElementList );
                Log.l.info ( "-- find wbElement = %s", wbElement );
                if ( wbElement == null )
                {
                    forDeleteList.add ( param );
                }
            }
            Log.l.info ( "forDeleteList = %s", forDeleteList );
            if ( ! forDeleteList.isEmpty() )
            {
                oldElementList.removeAll ( forDeleteList );
            }
            */

            // Цикл по всем реальным элементам
            for ( WBookElement bookElement : bookElementList )
            {
                // - Ищем подобный элемент среди прописанных в закладках - по уровню.
                element = findElement ( bookElement, oldElementList );
                Log.file.debug ( "--- bookElement = %s; find element = %s", bookElement, element );
                if ( element == null )
                {
                    // Нет такого элемента в нашем списке - создать и добавить в список
                    element = createElement ( bookElement );
                }
                else
                {
                    // мержить параметры -- надо на случай если пользователь изменил что-то в элементе. Например, уровень 1 был Часть, а стал Глава.
                    // --- лишнее, т.к. параметры в описании элемента книги и в описании конвертации - различаются. ???
                    element.setValue ( bookElement );
                }
                Log.file.debug ( "--- merge element = %s", element );
                // Если в параметрах есть элемент, но его нет в описании книги - удалить.
                elementList.add ( element );
            }
            Log.file.debug ( "--- new elementList = \n%s", DumpTools.printCollection ( elementList ) );
            cp.setElementList ( elementList );

            typeList     = new TreeSet<SimpleParameter>();
            oldTypeList  = cp.getTypes();
            Log.l.debug ( "--- oldTypeList = %s", oldTypeList );
            // Цикл по всем реальным типам элементов
            for ( WType type : types.values() )
            {
                // - Ищем подобный тип среди прописанных в закладках
                spType = findType ( type, oldTypeList );
                Log.l.debug ( "----- find type = %s; for type = %s", spType, type );
                if ( spType == null )
                {
                    // создать и добавить в список типов
                    spType = createType ( type );
                    //oldTypeList.add ( spType );
                }
                else
                {
                    // todo мержить параметры -- надо на случай если пользователь изменил что-то в элементе.
                }
                typeList.add ( spType );
            }
            cp.setTypeList ( typeList );
        }
    }

    // Поиск по русскому названию - толкьо оно пропсиано в обоих элементах.
    private SimpleParameter findType ( WType type, Collection<SimpleParameter> oldTypeList )
    {
        String name;

        //name = type.getEnName();
        name = type.getRuName();

        for ( SimpleParameter element : oldTypeList )
        {
            if ( element.getName().equals ( name ) )  return element;
        }
        return null;
    }

    public SimpleParameter createType ( WType type )
    {
        SimpleParameter result;

        result  = new SimpleParameter ( type.getRuName(), TypeHandleType.NOTHING.toString() );
        result.setValue ( TypeHandleType.NOTHING.toString () );

        return result;
    }

    public ElementConvertParameter createElement ( WBookElement bookElement )
    {
        HtmlElementConvertParameter result;

        result  = new HtmlElementConvertParameter();
        result.setName ( bookElement.getName() );
        result.setLevel ( bookElement.getElementLevel () );

        return result;
    }

    /**
     * Поиск элемента в описании закладок.
     * Критерии поиска:
     * - по уровню
     * - по типу
     * @param bookElement
     * @param oldElementList
     * @return
     */
    public ElementConvertParameter findElement ( WBookElement bookElement, Collection<ElementConvertParameter> oldElementList )
    {
        int level;

        level = bookElement.getElementLevel();

        for ( ElementConvertParameter element : oldElementList )
        {
            if ( element.getLevel() == level )  return element;
        }
        return null;
    }

    /**
     * Ищем по титлу.
     * @param param
     * @param bookElementList
     * @return
     */
    private WBookElement findElement ( ElementConvertParameter param, Collection<WBookElement> bookElementList )
    {
        String name;

        name = param.getName();

        for ( WBookElement element : bookElementList )
        {
            //Log.l.info ( "--- name = %s; element = %s", name, element );
            if ( element.getName().equals ( name ) )  return element;
        }
        return null;
    }


    public String getCurrentBookmark ()
    {
        return currentBookmark;
    }

    public ConvertParameter getConvertParameter ( String name )
    {
        if ( name == null ) return null;
        for ( ConvertParameter cp : getList() )
        {
            if ( cp.getName().equals ( name ) ) return cp;
        }
        return null;
    }

    public String toString()
    {
        ByteArrayOutputStream ou;

        ou	= new ByteArrayOutputStream (8192);
        try
        {
            toXml ( 1, ou );
            ou.flush();
        } catch ( Exception e )         {
            Log.l.error ( "error", e );
        }
        return ou.toString();
    }

    public void delete ( ConvertParameter cp )
    {
        list.remove ( cp );
    }

    @Override
    public Object getValue ()
     {
         return null;
     }

    @Override
    public void setValue ( Object value )    {    }

    public boolean hasLocaleParams ()
    {
        for ( ConvertParameter cp : getList() )  if ( cp.hasLocaleParams() ) return true;
        return false;
    }
    
}
