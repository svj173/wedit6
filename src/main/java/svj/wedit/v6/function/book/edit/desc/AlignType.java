package svj.wedit.v6.function.book.edit.desc;


import svj.wedit.v6.util.INameNumber;

import javax.swing.text.StyleConstants;

/**
 * Позиционирование (смещение) текста.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 21.08.2011 17:36:59
 */
public enum AlignType implements INameNumber
{
    LEFT        ( "Влево",          StyleConstants.ALIGN_LEFT       ),
    CENTER      ( "По центру",      StyleConstants.ALIGN_CENTER     ),
    RIGHT       ( "Вправо",         StyleConstants.ALIGN_RIGHT      ),
    JUSTIFIED   ( "Форматировать",  StyleConstants.ALIGN_JUSTIFIED  )
    ;

    private String  name;
    private int     number;

    AlignType ( String name, int number )
    {
        this.name   = name;
        this.number = number;
    }

    public String getName ()
    {
        return name;
    }

    public int getNumber ()
    {
        return number;
    }

    public static AlignType getByNumber ( int ic )
    {
        for ( AlignType align : values() )
        {
            if ( align.getNumber() == ic )  return align;
        }

        return AlignType.LEFT;
    }

}
