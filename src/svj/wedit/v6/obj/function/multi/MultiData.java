package svj.wedit.v6.obj.function.multi;


/**
 * Обьект передачи данных из background-потока в awt-поток - внутри работы MultiActionSwingWorker.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 28.08.2013 12:12
 */
public class MultiData
{
    private MultiActionCmd  cmd;
    private ProgressBarType progressBarType;
    private boolean         error;
    private int             sendSize;
    private int             packetNumber;
    private long            totalSize;
    private int             stepSize;
    private String          processTitle;
    private Exception       exception;


    public MultiData ( MultiActionCmd cmd )
    {
        this.cmd        = cmd;
        progressBarType = ProgressBarType.DATA;
        error           = false;
        sendSize        = -1;
        packetNumber    = -1;
        totalSize       = -1;
        stepSize        = -1;
        processTitle    = null;
        exception       = null;
    }

    public String toString()
    {
        StringBuilder result;

        result = new StringBuilder ( 128 );
        result.append ( "[ MultiData : cmd = " );
        result.append ( getCmd() );
        result.append ( "; processTitle = " );
        result.append ( getProcessTitle() );
        result.append ( "; progressBarType = " );
        result.append ( getProgressBarType() );
        result.append ( "; error = " );
        result.append ( isError() );
        result.append ( "; sendSize = " );
        result.append ( getSendSize() );
        result.append ( "; packetNumber = " );
        result.append ( getPacketNumber() );
        result.append ( "; totalSize = " );
        result.append ( getTotalSize() );
        result.append ( "; stepSize = " );
        result.append ( getStepSize() );
        result.append ( "; exception = " );
        result.append ( getException() );
        result.append ( " ]" );

        return result.toString ();
    }

    public void setCmd ( MultiActionCmd cmd )
    {
        this.cmd = cmd;
    }

    public void setProgressBarType ( ProgressBarType progressBarType )
    {
        this.progressBarType = progressBarType;
    }

    public ProgressBarType getProgressBarType ()
    {
        return progressBarType;
    }

    public MultiActionCmd getCmd ()
    {
        return cmd;
    }

    public boolean isError ()
    {
        return error;
    }

    public void setError ( boolean error )
    {
        this.error = error;
    }

    public int getSendSize ()
    {
        return sendSize;
    }

    public void setSendSize ( int sendSize )
    {
        this.sendSize = sendSize;
    }

    public void setPacketNumber ( int packetNumber )
    {
        this.packetNumber = packetNumber;
    }

    public int getPacketNumber ()
    {
        return packetNumber;
    }

    public void setTotalSize ( long totalSize )
    {
        this.totalSize = totalSize;
    }

    public void setStepSize ( int stepSize )
    {
        this.stepSize = stepSize;
    }

    public long getTotalSize ()
    {
        return totalSize;
    }

    public int getStepSize ()
    {
        return stepSize;
    }

    public void setProcessTitle ( String processTitle )
    {
        this.processTitle = processTitle;
    }

    public String getProcessTitle ()
    {
        return processTitle;
    }

    public void setException ( Exception exception )
    {
        this.exception = exception;
    }

    public Exception getException ()
    {
        return exception;
    }

}
