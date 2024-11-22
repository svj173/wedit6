package svj.wedit.v6.tools;


import svj.wedit.v6.exception.WEditException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;


/**
 * Утилиты работы со строками.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 01.02.2012 12:58:08
 */
public class StringTools
{
    /**
     * Проверка строки на пустоту. Учитываем null, и одни пробелы.
     * @param str Проверяемая строка.
     * @return    TRUE - Есть какие-то символы, кроме пробелов.
     */
    public static boolean isEmpty ( String str )
    {
        if ( str == null )  return true;
        str = str.trim();
        return str.isEmpty();
    }

    /**
     * Добавить символы в конец строки.
     * @param str   Строка
     * @param ch    Символ
     * @param size  Сколько раз.
     * @return      Результат.
     */
    public static String addSpaceLast ( String str, char ch, int size ) {
        StringBuilder sb;
        int ic;

        if (str == null) {
            str = "";
        }
        ic = str.length();
        if (size <= ic) {
            return str;
        }

        sb = new StringBuilder(64);
        sb.append(str);
        for (int i = 0; i < (size - ic); i++) {
            sb.append(ch);
        }

        return sb.toString();
    }


    public static String createFirst ( int spaceSize )
    {
        return createFirst(spaceSize, '\t');
    }

    public static String createFirst ( int spaceSize, char sep )
    {
        StringBuilder result;

        if ( spaceSize < 0 )  spaceSize = 0;

        result = new StringBuilder ( spaceSize );

        for ( int i=0; i < spaceSize; i++ )  result.append ( sep );

        return result.toString();
    }

    public static BufferedReader createReader ( String str )
    {
        StringReader sr     = new StringReader ( str );
        BufferedReader br   = new BufferedReader ( sr );
        return br;
    }

    public static String readFromConsole ( String codePage )
    {
        String result;
        byte[] bb;
        int c, i;

        i = -1;
        bb = new byte[1000];

        try
        {
            while ( ( c = System.in.read () ) != '\n' )
            {
                if ( !( c == '\r' ) )
                {
                    i++;
                    bb[ i ] = ( byte ) c;
                }
            }
            if ( i < 0 ) return "";
            if ( codePage != null )
                result = new String ( bb, 0, ( i + 1 ), codePage );
            else
                result = new String ( bb, 0, ( i + 1 ) );
        } catch ( IOException e )        {
            result = "error";
            //e.printStackTrace ();
        }
        return result;
    }

    /**
     * Преобразовать сообщения из одной русской кодировки в другую.
     * @param mess Исходное сообщение.
     * @return  Сообщение в новой кодировке.
     */
    public static String transform ( String mess, String inCode, String outCode )
            throws WEditException
    {
       String   result;
       byte[]   bb;

       try
       {
          // Преобразвоать входную строку в массив байт соответствующей кодировки.
          if ( inCode != null )
             bb       = mess.getBytes ( inCode );
          else
             bb       = mess.getBytes ();
          // Создать новую строку
          if ( outCode != null )
             result   = new String ( bb, outCode );
          else
             result   = new String ( bb );
       } catch ( Exception e )
       {
           throw new WEditException ( e, "String transform error. Source = '", mess,
                    "', charsetSorceName = '", inCode,
                    "', charsetTargetName = '", outCode,
                    "'. Error : ", e );
       }
       return result;
    }

    /**
     * Нарезать строку, вставляя разделитель.
     * @param str
     * @param size
     * @param separator
     * @return
     */
    public static String recut ( String str, int size, String separator )
    {
        return null;
    }
}
