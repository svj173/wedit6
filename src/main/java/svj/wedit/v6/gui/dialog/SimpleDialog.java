package svj.wedit.v6.gui.dialog;


import svj.wedit.v6.exception.WEditException;

/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 28.12.2015 16:49
 */
public class SimpleDialog  extends WDialog<Object, Object>
{
    public SimpleDialog ( String title )
    {
        super ( title );
    }

    @Override
    public void init ( Object initObject ) throws WEditException
    {
    }

    @Override
    public Object getResult () throws WEditException
    {
        return null;
    }

}
