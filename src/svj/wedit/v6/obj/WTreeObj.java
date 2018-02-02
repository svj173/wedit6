package svj.wedit.v6.obj;


import java.util.Collection;


/**
 * Информационный обьект. Хранится в обьекте дерева (как носитель - в userObject).
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 27.07.2011 15:22:28
 */
public abstract class WTreeObj  extends XmlAvailable   implements IName
{
    /* Имя обьекта (русское) */
    private String   name;

    /* Аннотация */
    private String   annotation = null;

    private WTreeObj parent;

    /* Номер обьекта среди дочерних обьектов данного уровня. От 0. - Пока не исп. */
    private int      index;

    /* Уровень от корня (чтобы не бегать по парентам) - необходим для редактирования gui-описания элемента. TODO - от 0 или от 1 ???
     * И именно по уровню определяется элемент описания данного заголовка. Здесь хранится только тип (work, hidden). */
    //private int   level;

    
    public abstract Collection<WTreeObj> getChildrens ();

    /* Тип обьекта (enum) */
    public abstract TreeObjType getType ();

    /* Иконка для отображения в дереве. */
    public abstract String getTreeIconFilePath ();

    public abstract WTreeObj clone ();



    public String toString()
    {
        StringBuilder result;
        result  = new StringBuilder();

        result.append ( "[ WTreeObj: name=" );
        result.append ( getName() );
        result.append ( "; annotation='" );
        result.append ( getAnnotation() );
        result.append ( "'; index=" );
        result.append ( getIndex() );
        result.append ( "'; type=" );
        result.append ( getType() );

        //result.append ( super.toString() );
        result.append ( " ]" );

        return result.toString();
    }

    public int getLevel ()
    {
        int result;

        result  = 0;
        if ( getParent() != null )
        {
            // счетчик по парентам до рута
            result  = getParent().getLevel() + 1;
        }
        return result;
    }

    public void clear ()
    {
        name        = null;
        annotation  = null;
    }

    public void setParent ( WTreeObj parent )
    {
        this.parent = parent;
    }

    public WTreeObj getParent ()
    {
        return parent;
    }
    
    public String getName ()
    {
        return name;
    }

    public void setName ( String name )
    {
        this.name = name;
    }

    public String getAnnotation ()
    {
        return annotation;
    }

    public void setAnnotation ( String annotation )
    {
        this.annotation = annotation;
    }

    public void addAnnotation ( String text )
    {
        if ( getAnnotation() == null )
        {
            setAnnotation ( text );
        }
        else
        {
            /*
            // Для многострочных аннтоаций: Почему-то начинают добавляться переносы строк и в конце концов анотация расползается на несколкьо страниц.
            if ( getAnnotation().endsWith ( WCons.END_LINE ) )
                setAnnotation ( getAnnotation() + text );
            else
                setAnnotation ( getAnnotation() + WCons.END_LINE + text );
            */
            setAnnotation ( getAnnotation() + text );
        }
    }

    public int getIndex ()
    {
        return index;
    }

    public void setIndex ( int index )
    {
        this.index = index;
    }

}
