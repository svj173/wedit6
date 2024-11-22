package svj.wedit.v6.obj.book.element;


import svj.wedit.v6.util.INameNumber;


/**
 * Тип стиля текста. Зависит от названия стиля.
 * <BR/> - аннотация
 * <BR/> - element
 * <BR/> - текст (label и просто текст с измененным стилем)
 * <BR/>
 * <BR/> Здесь имя - это имя стиля данного элемента.
 * <BR/> Если в тексте имя стиля не задано или не входит в данный список - это текст.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 26.01.2012 15:58:41
 */
public enum StyleType  implements INameNumber
{
    UNKNOW      ( 0, "unknow"), 
    ELEMENT     ( 1, "element"),
    ANNOTATION  ( 2, "annotation"),
    TEXT        ( 3, "text"),
    COLOR_TEXT  ( 4, "color_text"),
    IMG         ( 4, "image" );
    //, LABEL - это тот же текст, только со своим стилем


    private int     number;
    private String  name;

    StyleType ( int number, String name )
    {
        this.number = number;
        this.name   = name;
    }

    @Override
    public String getName ()
    {
        return name;
    }

    @Override
    public int getNumber ()
    {
        return number;
    }

}
