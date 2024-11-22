package svj.wedit.v6.obj.function.multi;


/**
 * Обьект хранит (и накапливает) данные, которые используются для отображения в первом прогресс-баре (ход процесса).
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 28.08.2013 14:38
 */
public class ProgressBarDataObject
{
    /* Номер передаваемого пакета. Размер пакета, в байт. */
    private int             packetNumber, stepSize;
    /* Размер файла. Счетчик передаваемых данных. */
    private long            totalSize, count, startTime;
    private ProgressBarType progressBarType;


    public ProgressBarDataObject ()
    {
        clear();
    }

    public String toString()
    {
        StringBuilder result;

        result = new StringBuilder ( 128 );
        result.append ( "[ ProgressBarDataObject : progressBarType = " );
        result.append ( getProgressBarType() );
        result.append ( "; count = " );
        result.append ( getCount() );
        result.append ( "; startTime = " );
        result.append ( getStartTime() );
        result.append ( "; packetNumber = " );
        result.append ( getPacketNumber() );
        result.append ( "; totalSize = " );
        result.append ( getTotalSize() );
        result.append ( "; stepSize = " );
        result.append ( getStepSize() );
        result.append ( " ]" );

        return result.toString ();
    }

    public void addData ( int sendSize )
    {
        count = count + sendSize;
    }

    public void clear ()
    {
        count           = 0l;
        packetNumber    = 0;
        totalSize       = 0;
        startTime       = 0;
        stepSize        = 0;
        progressBarType = ProgressBarType.UNTIME;

        startTime       = System.currentTimeMillis();
    }

    public int getPacketNumber ()
    {
        return packetNumber;
    }

    public void setPacketNumber ( int packetNumber )
    {
        this.packetNumber = packetNumber;
    }

    public int getStepSize ()
    {
        return stepSize;
    }

    public void setStepSize ( int stepSize )
    {
        this.stepSize = stepSize;
    }

    public long getTotalSize ()
    {
        return totalSize;
    }

    public void setTotalSize ( long totalSize )
    {
        this.totalSize = totalSize;
    }

    public long getCount ()
    {
        return count;
    }

    public void setCount ( long count )
    {
        this.count = count;
    }

    public long getStartTime ()
    {
        return startTime;
    }

    public ProgressBarType getProgressBarType ()
    {
        return progressBarType;
    }

    public void setProgressBarType ( ProgressBarType progressBarType )
    {
        this.progressBarType = progressBarType;
    }

}
