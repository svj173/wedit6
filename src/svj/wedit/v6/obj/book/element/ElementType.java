package svj.wedit.v6.obj.book.element;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.WClone;
import svj.wedit.v6.obj.XmlAvailable;
import svj.wedit.v6.tools.Utils;

import java.io.OutputStream;


/**
 * Тип элемента (work, hidden)      -- НЕ исп - т.к. сильно громоздско.   -- WType.
 <type name="hidden">
     <ruName>Скрытый</ruName>
     <desc>Материал, используемый в книге</desc>
 </type>
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 25.01.2012 17:57:40
 */
public class ElementType  extends XmlAvailable implements Comparable<ElementType>, WClone<ElementType>
{
    private String name, ruName, desc;

    @Override
    public int compareTo ( ElementType element )
    {
        if ( element == null )
            return -1;
        else
            return Utils.compareToWithNull ( getName(), element.getName() );
    }

    @Override
    public void toXml ( int outLevel, OutputStream out ) throws WEditException
    {
        int ic;

        try
        {
            ic  = outLevel + 1;

            outTitle ( outLevel, "type", "name", getName(), out );

            outTag ( ic, "ruName",      getRuName(),  out );
            outTag ( ic, "desc",        getDesc(),    out );

            endTag ( outLevel, "type", out );

        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML представления типа\n Элемента Книги '", getName(), "' в поток :\n", e );
        }
    }

    @Override
    public int getSize ()
    {
        // Это не используется
        return 0;
    }

    public ElementType cloneObj ()
    {
        ElementType result;

        result  = new ElementType ();

        result.setName ( getName() );
        result.setRuName ( getRuName() );
        result.setDesc ( getDesc () );

        return result;
    }

    public String getName ()
    {
        return name;
    }

    public void setName ( String name )
    {
        this.name = name;
    }

    public String getRuName ()
    {
        return ruName;
    }

    public void setRuName ( String ruName )
    {
        this.ruName = ruName;
    }

    public String getDesc ()
    {
        return desc;
    }

    public void setDesc ( String desc )
    {
        this.desc = desc;
    }

}
