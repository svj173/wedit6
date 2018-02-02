package svj.wedit.v6.gui;


/**
 * Интерфейс перерисовки объектов
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 04.08.2011 11:45:12
 */
public interface InitObjectComponent<T>
{
    public void init ( T obj );

    public T getObj ();

}
