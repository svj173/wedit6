package svj.wedit.v6.obj;


import svj.wedit.v6.exception.WEditException;

import java.io.InputStream;
import java.io.OutputStream;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 05.07.2011 15:26:05
 */
public class Address   extends XmlAvailable
{
    public void  getFromXml ( InputStream in ) throws WEditException
    {
    }

    @Override
    public void toXml ( int level, OutputStream out ) throws WEditException
    {
    }
    
    @Override
    public int getSize ()
    {
        return 0;
    }

}
