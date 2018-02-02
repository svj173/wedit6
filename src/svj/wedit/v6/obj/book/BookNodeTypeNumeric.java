package svj.wedit.v6.obj.book;


/**
 * Тип нумерации элемента - сквозной от начала, либо в пределах парента.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 21.08.2011 17:36:59
 */
public enum BookNodeTypeNumeric
{
    ALL_BOOK ("Сквозной от начала"), ONLY_PARENT ("В пределах парента");

    private String name;

    BookNodeTypeNumeric ( String name )
    {
        this.name = name;
    }

    public String getName ()
    {
        return name;
    }

    /*
    public String toString ()
    {
        return getName();
    }
    */

}
