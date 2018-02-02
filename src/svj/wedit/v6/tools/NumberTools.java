package svj.wedit.v6.tools;


import svj.wedit.v6.exception.WEditException;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;


/**
 * Сервисные утилиты по преобразованиям числовых значений.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 01.02.2012 13:03:44
 */
public class NumberTools
{
    /**
     * Преобразовать строку в целое.
     * @param intStr        Строковое представление целого числа.
     * @param errorMessage  Сообщение, выводимое при возникновении ошибки преобразования типа.
     * @return
     * @throws Exception
     */
    public static int getInt ( String intStr, String errorMessage )
         throws WEditException
    {
       int   result;
       try
       {
          result = Integer.parseInt ( intStr );
       } catch ( Exception e )   {
          throw new WEditException ( errorMessage + ". Source code = " + intStr );
       }
       return result;
    }

    public static int getInt ( String intStr, int def )
    {
       int   result;
       try
       {
           result = Integer.parseInt ( intStr );
       } catch ( Throwable e )      {
           result = def;
       }
       return result;
    }

    public static float getFloat ( String floatStr, float def )
    {
       float   result;
       try
       {
          result = Float.parseFloat ( floatStr );
       } catch ( Throwable e )      {
          result = def;
       }
       return result;
    }

    public static double getDouble ( String doubleStr, double def )
    {
       double   result;
       try
       {
          result = Double.parseDouble ( doubleStr );
       } catch ( Throwable e )      {
          result = def;
       }
       return result;
    }

     public static double getDouble  ( String doubleStr, String errorMessage )
          throws WEditException
     {
        double   result;
        try
        {
           result = Double.parseDouble ( doubleStr );
        } catch ( Exception e )      {
           throw new WEditException ( errorMessage + ". Source code = " + doubleStr );
        }
        return result;
     }

     public static double getDouble ( Object doubleObj, String errorMessage )
             throws WEditException
     {
         double result;
         String doubleStr;
         Double db;

         if ( doubleObj == null )
             throw new WEditException ( errorMessage + ". Source code = " + doubleObj );

         try
         {
             if ( doubleObj instanceof Double )
             {
                 db      = (Double) doubleObj;
                 result  = db.doubleValue();
             }
             else if ( doubleObj instanceof Long )
             {
                 result  = (Long) doubleObj;
             }
             else
             {
                 doubleStr   = doubleObj.toString();
                 result      = Double.parseDouble(doubleStr);
             }
         } catch ( Exception e ) {
             throw new WEditException ( e, errorMessage, ". Source code = ", doubleObj );
         }
         return result;
     }

     public static Long getLong ( Object obj, String errorMessage )
             throws WEditException
     {
         Long   result;
         String longStr;

         if ( obj == null )
             throw new WEditException ( errorMessage + ". Source code = " + obj );

         try
         {
             longStr = obj.toString();
             result  = Long.parseLong(longStr);
         } catch ( Exception e ) {
             throw new WEditException ( errorMessage + ". Source code = " + obj );
         }
         return result;
     }

     public static Integer getInteger ( Object obj, String errorMessage )
             throws WEditException
     {
         Integer result;
         String  intStr;

         if ( obj == null )
             throw new WEditException ( errorMessage + ". Source code = " + obj );

         try
         {
             intStr  = obj.toString();
             result  = Integer.parseInt(intStr);
         } catch ( Exception e ) {
             throw new WEditException ( errorMessage + ". Source code = " + obj );
         }
         return result;
     }


     /**
      * Отформатировать число под сумму. Т.е. округление (само делает) и ограничение до двух знаков
      *  после запятой, с выводом нулей - и после запятой и первым, если число меньше 1.00.
      * @param  amount
      * @return string
      */
    public static String amountFormat ( float amount )
    {
         String                  result, pattern;
         DecimalFormat formatter;
         DecimalFormatSymbols symbols;

         // Установить десятичный разделитель - точка (ставит обычно запятую)
         symbols     =   new DecimalFormatSymbols();
         symbols.setDecimalSeparator('.');
         //
         pattern     = "##0.00";
         formatter   = new DecimalFormat ( pattern, symbols );
         result      = formatter.format ( amount );
         return  result;
    }

   /**
    * Преобразовать INT в набор байтов (4 байта)
    * @param inum  Исходное целое число.
    * @return  Массив из 4 байт - байтовое представление INT
    */
    public static byte[] int2bytes ( int inum )
    {
        byte[] abyte = new byte[4];
        abyte[ 0 ] = ( byte ) ( inum % 256 );
        abyte[ 1 ] = ( byte ) ( ( inum >> 8 ) % 256 );
        abyte[ 2 ] = ( byte ) ( ( inum >> 16 ) % 256 );
        abyte[ 3 ] = ( byte ) ( ( inum >> 24 ) % 256 );

        return abyte;
    }

}
