package svj.wedit.v6.obj.function;


import svj.wedit.v6.WCons;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.tools.Convert;

import java.io.FileOutputStream;

/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 14.11.2013 15:19
 */
public abstract class FileWriteFunction extends SimpleBookFunction
{
    private FileOutputStream fos = null;

    public void setFos ( FileOutputStream fos )
    {
        this.fos = fos;
    }

    public FileOutputStream getFos ()
    {
        return fos;
    }

    protected void writeStr ( String str )
    {
        //if ( fos == null )  return;
        if ( fos == null )  throw new RuntimeException ( "FOS is absent!" );
        if ( str == null )  return;

        try
        {
            // todo codeType - По идее - тип кодировки файла тоже можно задавать индивидуально для данной Ковертации.
            fos.write ( str.getBytes ( WCons.CODE_PAGE ));
        } catch ( Exception e )         {
            Log.file.error ( Convert.concatObj ( "Save string error: '", str, "'" ), e);
        }
    }

    @Deprecated
    protected void writeStr ( FileOutputStream fos, String str )
    {
        try
        {
            fos.write ( str.getBytes ( WCons.CODE_PAGE ));
        } catch ( Exception e )         {
            Log.file.error ( Convert.concatObj ( "Save string error: '", str, "'" ), e);
        }
    }

}
