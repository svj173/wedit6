package svj.wedit.v6.manager;


import svj.wedit.v6.tools.Convert;

/**
 * Статистика обращений к нити.
 * <BR> User: Zhiganov
 * <BR> Date: 13.09.2004
 * <BR> Time: 17:50:18
 */
public class ThreadStat
{
   /** Имя процесса. */
   private String    name;
   /** Идентификатор для связи лога с конкретным ордером. В нашем случае - это значение TranID. */
   private String    idForOrder;
   /** суммарное время работы процесса, в миллсек. */
   private long      workDate;
   /** Время начала текущего этапа работы процесса. */
   private long      startDate;
   /** Кол-во обращений к процессу. */
   private int       counter;
   /** Ссылка на сам обьект - для возможности его рестарта при зависании. */
   private  Thread   thread;
   /** Хэш-код этого процесса - для единой информации. */
   private  int      hash;


   public ThreadStat ( String name, int hash )
   {
      this.name   = name;
      this.hash   = hash;
      counter     = 0;
      workDate    = 0;
      startDate   = (new java.util.Date()).getTime ();
      thread      = Thread.currentThread ();
   }

   public String  toString()
   {
      StringBuilder   result   = new StringBuilder ( 64 );
      //
      result.append ( name );
      result.append ( "\tCount: " + counter );
      result.append ( "\tWork: " + Convert.sec2str ( (int) (workDate / 1000), "hh:mm:ss") );
      return   result.toString ();
   }

   public void setCounter ()
   {
      counter++;
      // Подсчитать время работы
      long  endDate  = (new java.util.Date()).getTime ();
      workDate += endDate - startDate;
   }

   public void setStart ()
   {
      startDate   = (new java.util.Date()).getTime ();
   }

   public int getCounter ()
   {
      return   counter;
   }

   public void setId ( String newId )
   {
      idForOrder  = newId;
   }

   public String getId ()
   {
      return   idForOrder;
   }

   public Thread  getThread ()
   {
      return thread;
   }

   public String  getName ()
   {
      return name;
   }

   public long  getStartDate ()
   {
      return startDate;
   }

   public int  getHash ()
   {
      return hash;
   }

//================================================================================

}
