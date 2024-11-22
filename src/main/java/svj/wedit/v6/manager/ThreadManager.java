package svj.wedit.v6.manager;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 05.05.2011 9:48:37
 */

import java.util.Hashtable;
import java.util.Enumeration;

/**
 * Класс мапирует у себя имена потоков и ИД к ним, а также попутно ведет
 *  статистику работы потоков.
 * <BR> 1.	Считает кол-во обращений
 * <BR> 2.	Считает затраченное потоком общее время.
 * <BR>
 * <BR> Обращения к этому классу.
 * <BR> 1.	Потоки, в момент перехода в режим ожидания - смена ИД (сам генерит ИД).
 * <BR> 2.	Syslog - в момент вывода сообщения.
 * <BR>
 * <BR>
 * <BR> User: Zhiganov
 * <BR> Date: 13.09.2004
 * <BR> Time: 16:57:07
 * <BR> Версия: $Revision $
 */
public class ThreadManager
{
   /** Массив работающих в системе потоков, имеющих метод логгера START.
    * Хранит обьекты типа ThreadStat. Ключ - хэш-код потока. */
   private  Hashtable   threadTable;

   /** Сам обьект. */
   private static ThreadManager instance = new ThreadManager ();

//-----------------------------------------------------------------------------
   private ThreadManager ()
   {
      threadTable = new Hashtable ( );
   }

   public static ThreadManager getInstance ()
   {
      return instance;
   }

   /**
    * Установить новое ИД. Заодно пересчитать статистику обращений.
    */
   public void setEnd ( )
   {
      ThreadStat   stat  = getThreadStat ( Thread.currentThread ()  );
      // Сгенерить новое ИД. Подсчитать время работы.
      stat.setCounter();
      stat.setId ( "-1" );
   }

   /**
    * Получить обьект описания процесса.
    * @param thread
    * @return
    */
   private ThreadStat getThreadStat ( Thread thread )
   {
      ThreadStat  result;
      // Взять хэш-код потока.
      int   hash  = thread.hashCode ();
      String   str   = ""+hash;
      // Получить статистический обьект
      result  = (ThreadStat) threadTable.get ( str );
      // Если такого процесса нет в нашем массиве - создать новый обьект описания процесса.
      if (result == null)
      {
         result  = new ThreadStat ( thread.getName(), hash );
         threadTable.put ( str, result );
      }
      return result;
   }

   /**
    * Процесс проснулся. Установить время начала работы.
    */
   public void setStart ( )
   {
      ThreadStat  stat  = getThreadStat ( Thread.currentThread ()  );
      // Сгенерить новое ИД. Подсчитать время работы.
      stat.setStart();
      stat.setId ( "-1" );
   }

   public String  toString ()
   {
      StringBuilder   result   = new StringBuilder ( 256 );
      int            i;
      ThreadStat     stat;

      result.append ( "Threads: " );
      result.append ( threadTable.size() );
      result.append ( "\n" );

      i  = 1;
      Enumeration element  = threadTable.elements ();
      while ( element.hasMoreElements () )
      {
         stat = ( ThreadStat ) element.nextElement ();
         //result.append ( ", { ");
         result.append ( i );
         result.append ( ". " );
         result.append ( stat.toString () );
         //result.append ( " } " );
         result.append ( "\n" );
         i++;
      }
      return result.toString ();
   }

   public int getId ()
   {
      int   result;
      ThreadStat  stat  = getThreadStat ( Thread.currentThread ()  );
      result   = stat.getCounter();
      return result;
   }

   /**
    * Установить уникальный идентификатор, по которому запись лога привязывется к ордеру.
    * @param newId
    */
   public void setUid ( String newId )
   {
      ThreadStat  stat  = getThreadStat ( Thread.currentThread ()  );
      stat.setId ( newId );
   }

   public String getUid ()
   {
      ThreadStat  stat  = getThreadStat ( Thread.currentThread ()  );
      return   stat.getId ();
   }

   public Hashtable getThreadTable ()
   {
      return   threadTable;
   }


//================================================================================

}
