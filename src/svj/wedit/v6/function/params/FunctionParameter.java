package svj.wedit.v6.function.params;


import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.IName;
import svj.wedit.v6.obj.XmlAvailable;
import svj.wedit.v6.tools.Convert;

import javax.swing.*;
import java.io.FileOutputStream;


/**
 * Базовый класс для параметров функций.
 * <BR/>
 * <BR/> - T - тип обьекта для value.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.07.2011 16:35:44
 */
public abstract class FunctionParameter<T>  extends XmlAvailable implements IName
{
    //private String  id     = Cons.SP;

    /** ID параметра (в латинице). Уникально в пределах функции. Обязательный элемент. */
    private String  name   = WCons.SP;
    /** Имя, выводимое в виджете. */
    private String  ruName   = WCons.SP;
    private String  desc   = WCons.SP;

    /** Иконка данного параметра. Может отсутствовать. */
    private Icon icon    = null;

    /** wedit-user-book-none - категория параметра -- пока не используется, т.к. все параметры функций сохраняются в профиле пользователя. */
    private ParameterCategory     category    = ParameterCategory.NONE;

    /** Флаг, можно ли редактировать данный параметр (т.е. выводить ссылку на
                   него в системном меню "Настройки" или нет.
     По умолчанию - можно редактировать. */
    private boolean editable    = true;

    /* Флаг - допускаются пустые значения или нет. */
    private boolean empty = false;

    /** Именованные аттрибуты данного параметра.
     *  Могут быть как 'Параметрами', так и любыми обьектами. */
    //private Map<String,Object> attributes;


    // Имена тэгов
    //private final static String NAME        = "name";
    //private final static String DESC        = "desc";
    //private final static String CATEGORY    = "category";


    // ---------------------------- abstract -------------------------------

    // чтобы абстрактно работать с данными. анпример, когад есть спсико разномастных параметров и абстрактные виджеты, связанные с ними.
    public abstract T  getValue ();                 // ???
    public abstract void    setValue ( T value );   // ???

    public abstract FunctionParameter clone ();


    public void    addValue ( T value ) {}

    /* Инициализация значения параметра из его строкового представления. */
    //public abstract void    initValue ( String value );
    // либо  initValue ( TreeObject value ) - для сложных значений (списко и т.д.)


    public FunctionParameter ()
    {
        this ( "name" );
    }

    public FunctionParameter  ( String name )
    {
        //attributes          = new HashMap<String,Object>();
        this.name           = name;
    }

    public void mergeToOther ( FunctionParameter other )
    {
        other.setDesc ( getDesc() );
        other.setRuName ( getRuName() );
        other.setName ( getName() );
        other.setValue ( getValue() );
        other.setCategory ( getCategory() );
        other.setEditable ( isEditable() );
        other.setHasEmpty ( hasEmpty() );
        other.setIcon ( getIcon() );
    }

    /**
     * Сохранить параметр в файле (в xml виде).
     * @param file  Файл сохранения. Как правило - это xml-конфиг.
     * @throws WEditException Ошибка сохранения
     */
    //public abstract void save ( FileOutputStream file )  throws WEditException;
    //public abstract void save ( FileOutputStream file, int level )  throws WEditException;
    public void save ( FileOutputStream file ) throws WEditException
    {
        toXml ( 1, file );
    }

    public void save ( FileOutputStream file, int level ) throws WEditException
    {
        toXml ( level, file );
    }

    public boolean  equals ( FunctionParameter parameter )
    {
        return name.equals ( parameter.getName () );
    }

    @Override
    public int getSize ()
    {
        // Это не используется
        return 0;
    }

    public Icon getIcon ()
    {
        return icon;
    }

    public void setIcon ( Icon icon )
    {
        this.icon = icon;
    }

    public String getDesc ()
    {
        return desc;
    }

    public void setDesc ( String descKey )
    {
        this.desc = descKey;
    }

    public ParameterCategory getCategory ()
    {
        return category;
    }

    public void setCategory ( ParameterCategory categoryName )
    {
        category    = categoryName;
    }
    public void setCategory ( String categoryName )
    {
        ParameterCategory c;
        try
        {
            category    = ParameterCategory.valueOf ( categoryName );
        } catch ( Exception e )        {
            category    = ParameterCategory.NONE;
            Log.l.error ( Convert.concatObj ( "Ошибка валидации категории параметра '", categoryName, "'" ), e );
        }
    }

    public String getName ()
    {
        return name;
    }

    public void setName ( String nameKey )
    {
        this.name = nameKey;
    }

    // по идее - только для отладки
    public String toString()
    {
        StringBuilder result;
        result  = new StringBuilder ( 64 );
        result.append ( "[ FunctionParameter: name = " );
        result.append ( getName () );
        result.append ( ", ruName = " );
        result.append ( getRuName() );
        result.append ( ", desc = " );
        result.append ( getDesc() );
        result.append ( ", category = " );
        result.append ( getCategory() );
        result.append ( ", hasEmpty = " );
        result.append ( hasEmpty() );
        result.append ( " ]" );

        return result.toString();
        //return  getName ();
    }

    /*
    public String getInfo()
    {
        StringBuilder result;
        result  = new StringBuilder ( 64 );
        result.append ( "name = " );
        result.append ( getName () );
        result.append ( ", desc = " );
        result.append ( getDesc() );
        result.append ( ", category = " );
        result.append ( getCategory() );
        result.append ( ", hasEmpty = " );
        result.append ( hasEmpty() );
        //result.append ( ", attributes = " );
        //result.append ( getAttributes() );
        return result.toString();
    }
    */

    /**
     * Опрос, принадлежит ли параметр данной категории.
     * @param category
     * @return
     */
    public boolean isCategory ( ParameterCategory category )
    {
        return this.category == category;
    }

    /*
    public void clearAttributes ()
    {
        attributes.clear ();
    }

    public int getAttributesSize ()
    {
        return attributes.size ();
    }

    public Map<String,Object> getAttributes ()
    {
        return attributes;
    }

    public Object   getAttribute ( String attrName )
    {
        return attributes.get (attrName);
    }

    public void setAttribyte ( String attrName, Object value )
    {
        attributes.put ( attrName, value );
    }
    */

    public boolean isEditable ()
    {
        return editable;
    }

    public void setEditable ( boolean editable )
    {
        this.editable = editable;
    }

    public void setEditable ( String editableStr )
    {
        if ( editableStr.equalsIgnoreCase ( WCons.YES) )
            editable = true;
        else
            editable = false;
    }

    public boolean hasEmpty ()
    {
        return empty;
    }

    public void setHasEmpty ( boolean empty )
    {
        this.empty = empty;
    }

    // переписывается
    public void setParameter ( String paramName, FunctionParameter param )    {    }

    public String getRuName ()
    {
        return ruName;
    }

    public void setRuName ( String ruName )
    {
        this.ruName = ruName;
    }
}
