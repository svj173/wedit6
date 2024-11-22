package svj.wedit.v6.tools;


import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;

import java.awt.*;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Конвертация объектов (приведение типов).
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 10.11.2010 10:46:13
 */
public class Convert
{
    /* Набор форматтеров для парсинга введенной даты (чтобы создать форматтер только один раз) */
    private static final Map<String,SimpleDateFormat> formatters = new HashMap<String,SimpleDateFormat> ();

    public enum Format
    {
        MM_SS, HH_MM_SS, DD_HH_MM_SS, DD_HH_MM_SS_MS,
        YY_MM_DD  // для очень больших значений
    }

    /* Преобразовать стек-traceroute ошибки в текст. */
    public static String trace2str ( Throwable t )
    {
        StringBuilder    result;

        result = new StringBuilder(128);

        if ( t != null )
        {
            ByteArrayOutputStream ou;
            PrintStream ps;

            ou	= new ByteArrayOutputStream (8192);
            ps	= new PrintStream ( ou );
            t.printStackTrace ( ps ) ;
            result.append ( "\tTrace:\r\n" );
            result.append ( ou.toString() );
        }

        return result.toString ();
    }

    public static String createSpace ( String text, int step )
    {
        StringBuilder result;

        result = new StringBuilder();
        if ( text != null )
        {
            if ( step > 0 )
            {
                for ( int i = 0; i<step; i++ )  result.append ( text );

            }
            //result.append ( text );
        }
        return result.toString();
    }

    public static String font2str ( Font font )
    {
        //              str = name + "-" + style.toUpperCase () + "-" + size;
        if ( font == null )
            return "";    // Monospaced-bold-14
        else
            return concatObj ( font.getName(), '-', font.getStyle(), '-', font.getSize() );
    }

    public static Font  str2font ( String fontStr )
    {
        return Font.decode ( fontStr );
    }

    /**
     * Создать фонт из строковых представлений его атрибутов.
     * @param name
     * @param style
     * @param size
     * @return
     */
    public static Font  str2font ( String name, String style, String size )
    {
         Font    result;
         String  str;
         if ( name == null || style == null || size == null )    return null;
         try
         {
             str = name + "-" + style.toUpperCase () + "-" + size;
             result  = Font.decode ( str );
         } catch ( NumberFormatException e ) {
             result  = null;
         }
         return result;
    }


    /**
     * Создать цвет.
     * <BR> Цвет может быть как шестнадцатиричный (например: 017F69),
     * так и символьный (например: white, black...)
     * @param rgbStr   Строковое представление цвета.
     * @return обьект
     */
    public static Color str2color ( String rgbStr )
    {
        Color   result;
        int     ic;

        result  = null;

        //logger.debug ( "Start. color = " + color );
        if ( rgbStr == null) return result;

        try
        {
            ic      = Integer.parseInt ( rgbStr, 16 );
            result  = new Color ( ic );
            //result  = Color.decode ( color );
        } catch ( Exception e ) {
            // Значит это не число а название цвета
            //logger.error ( "Error", e );
            result  = null;
        }

        if ( result == null )
        {
            //logger.debug ( "Not integer" );
            //result  = Color.getColor ( "1" );
            rgbStr   = rgbStr.toLowerCase ();
            if ( rgbStr.equals ( "black") )
            {
                result  = Color.BLACK;
                return result;
            }
            if ( rgbStr.equals ( "white") )
            {
                result  = Color.WHITE;
                return result;
            }
            if ( rgbStr.equals ( "blue") )
            {
                result  = Color.BLUE;
                return result;
            }
            if ( rgbStr.equals ( "cyan") )
            {
                result  = Color.CYAN;
                return result;
            }
            if ( rgbStr.equals ( "darkgray") )
            {
                result  = Color.DARK_GRAY;
                return result;
            }
            if ( rgbStr.equals ( "gray") )
            {
                result  = Color.GRAY;
                return result;
            }
            if ( rgbStr.equals ( "green") )
            {
                result  = Color.GREEN;
                return result;
            }
            if ( rgbStr.equals ( "lightgray") )
            {
                result  = Color.LIGHT_GRAY;
                return result;
            }
            if ( rgbStr.equals ( "magenta") )
            {
                result  = Color.MAGENTA;
                return result;
            }
            if ( rgbStr.equals ( "orange") )
            {
                result  = Color.ORANGE;
                return result;
            }
            if ( rgbStr.equals ( "pink") )
            {
                result  = Color.PINK;
                return result;
            }
            if ( rgbStr.equals ( "red") )
            {
                result  = Color.RED;
                return result;
            }
            if ( rgbStr.equals ( "yellow") )
            {
                result  = Color.YELLOW;
                return result;
            }
        }
        //logger.debug ( "Finish. color = " + result );

        return result;
    }

    /**
     * Преобразовать цвет в его строковое 16-ти ричное представление.
     * @param color  Цвет
     * @return       16-ти ричное значение цвета RGB.
     */
    public static String color2str ( Color color )
    {
        int     ic;
        String  str;

        ic  = color.getRGB ();
        str = Integer.toHexString ( ic );
        // - отрезаем первые два символа - альфа-составляющая цвета
        str = str.substring ( 2 );

        return str;
    }

    /**
     * Преобразвоать текст в формат, пригодный для размешения внутри xml - т.е. без использования символов '>', '<', '&'.
     * @param str  Исходная строка.
     * @return     Результирующая строка.
     */
    public static String validateXml ( String str )
    {
        if ( str == null ) return WCons.SP;

        String result;
        // Заменить символы <> на &lt; &gt;
        result = str.replace ( "&", "&amp;");   // обязательно первым - иначе поломает &lt;
        result = result.replace ( "<", "&lt;");
        result = result.replace ( ">", "&gt;");

        return result;
    }

    /**
     * Убрать из текста все специфические XML символы - заменить их на подчеркивание.
     * Применяется при создании ИД глав.
     * @param str  Исходный текст.
     * @return
     */
    public static String replaceXml ( String str )
    {
        if ( str == null ) return WCons.SP;

        String result;
        // Заменить символы <> на &lt; &gt;
        result = str.replace ( "&", WCons.PP );   // обязательно первым - иначе поломает &lt;
        result = result.replace ( "<", WCons.PP );
        result = result.replace ( ">", WCons.PP );

        return result;
    }

    /**
     * Преобразвоать текст из xml-формата. Т.е. получить правильные символы '>', '<', '&'.
     * @param str  Исходная строка.
     * @return     Результирующая строка.
     */
    public static String revalidateXml ( String str )
    {
        //Log.file.debug ( "-- Line = %s", str );
        if ( str == null ) return WCons.SP;
        String result;
        // Заменить символы <> на &lt; &gt;
        result = str.replace ( "&lt;", "<" );
        //Log.file.debug ( "---- revalidate Line 1 = %s", result );
        result = result.replace ( "&gt;", ">" );
        //Log.file.debug ( "---- revalidate Line 2 = %s", result );
        result = result.replace ( "&amp;", "&" );
        //Log.file.debug ( "---- revalidate Line result = %s", result );

        return result;
    }


    public static Date getRuDate ( String strDate ) throws WEditException
    {
        // dd.MM.yyyy HH:mm:ss.SSS
        return str2date ( strDate, "dd.MM.yyyy HH:mm:ss" );
    }


    public static int getInt ( Object strObj, int defaultValue )
    {
        if ( strObj == null ) return defaultValue;

        String strInt   = strObj.toString();
        return getInt ( strInt, defaultValue );
    }

    public static int getInt ( String strInt, int defaultValue )
    {
        int     result;

        result   = defaultValue;
        if ( (strInt == null) || (strInt.length() == 0) )  return result;

        try
        {
            result = Integer.parseInt ( strInt );
        } catch ( Exception e )            {
            result  = defaultValue;
        }
        return result;
    }

    public static Double getDouble ( String strValue, double defaultValue )
    {
        Double     result;

        result   = defaultValue;
        if ( (strValue == null) || (strValue.length() == 0) )  return result;

        try
        {
            result = Double.parseDouble ( strValue );
        } catch ( Exception e )            {
            result  = defaultValue;
        }
        return result;
    }

    /* Преобразовать строку в дату */
    public static Date str2date ( String strDate, String outTemplate ) throws WEditException
    {
        Date                result;
        SimpleDateFormat    formatter;

        try
        {
            formatter   = getFormatter ( outTemplate );
            result      = formatter.parse ( strDate );
        } catch ( ParseException pe )        {
            throw new WEditException ( pe, "Неверный формат даты!\nНужно вводить в формате '",outTemplate,"'" );
        } catch ( Exception e )        {
            throw new WEditException ( e, "Ошибка преобразоыания даты :\n", e );
        }


        return result;
    }

    public static String getDateAsStr ( Date date, String outTemplate )
    {
        SimpleDateFormat    formatter;
        String              dateStr;

        dateStr     = WCons.SP;
        if ( date == null ) return dateStr;
        if ( outTemplate == null ) return date.toString();

        formatter   = getFormatter (outTemplate);
        dateStr     = formatter.format(date);

        return dateStr;
    }

    private static SimpleDateFormat getFormatter ( String outTemplate )
    {
        SimpleDateFormat    formatter;

        formatter   = formatters.get(outTemplate);
        if ( formatter == null )
        {
            formatter = new SimpleDateFormat ( outTemplate );
            formatters.put ( outTemplate, formatter );
        }
        return formatter;
    }

    public static String getRussianDateTime ( Date date )
    {
        return getDateAsStr ( date, "dd.MM.yyyy HH:mm:ss" );
    }

    public static String getEnDateTime ( Date date )
    {
        return getDateAsStr ( date, "yyyy-MM-dd HH:mm:ss" );
    }

    public static String getFullDate ( Date date )
    {
        return getDateAsStr ( date, "yyyy_MM_dd_HH_mm_ss_SSS" );
    }


    public static String concatObj ( Object ... mess )
    {
        StringBuilder result;

        result = new StringBuilder (512);
        for ( Object mes : mess )
        {
            //if ( mes != null ) 
            result.append ( mes );
        }

        return result.toString();
    }

    /* Преобразовать строку вида 'par1=v1\npar2=v2' в обьект пропертей. Перевод строки в качестве разделителя параметрoв обязателен. */
    public static Properties str2props ( String text ) throws WEditException
    {
        Properties      result;
        StringReader    sr;
        BufferedReader  br;

        try
        {
            sr      = new StringReader ( text );
            br      = new BufferedReader ( sr );
            result  = new Properties();
            result.load ( br );
        } catch ( Exception e )        {
            throw new WEditException ( e, "Ошибка конвертации строки в набор параметров :\n", e );
        }

        return result;
    }

    /**
     * Перечислить содержимое  массива через символ CH.
     * В конце строки символа CH - нет
     * @param array Коллекция обьектов для преобразования
     * @param sep    Разделитель между обьектами
     * @param kv     Символ обрамления данных. Например, для выделения текстовой информации
     * @return       Строковое представление коллекции.
     */
    public static String collectionToString ( Collection array, char sep, char kv )
    {
        String          result;
        StringBuilder   sb;

        result  = WCons.SP;
        if ( (array == null) || array.isEmpty() ) return result;

        sb      = new StringBuilder ( 128 );
        for ( Object obj : array )
        {
            //result = result + array[i] + ch + " ";
            sb.append ( kv );
            sb.append ( obj );
            sb.append ( kv );
            sb.append ( sep );
            sb.append ( " " );
        }
        // удалить последнюю запятую (символ SEP)
        result = sb.toString();
        result = result.substring ( 0, result.length() - 2 );

        return result;
    }

    public static String collectionToString ( Collection array, char sep )
    {
        return collectionToString ( array, sep, ' ' );
    }

    public static byte[] getAddress ( int address )
    {
        byte[] addr = new byte[4];

        addr[ 0 ] = ( byte ) ( ( address >>> 24 ) & 0xFF );
        addr[ 1 ] = ( byte ) ( ( address >>> 16 ) & 0xFF );
        addr[ 2 ] = ( byte ) ( ( address >>> 8 ) & 0xFF );
        addr[ 3 ] = ( byte ) ( address & 0xFF );

        return addr;
    }

    /**
     * Перевод русских букв в корявицу.
     * <BR/> В яве руские символы всегда хранятся в Unicode - к ним и надо привязаться.
     *
     * @param   ruText  Текст с русскими буквами.
     * @return  Текст в корявице (транслит).
     */
    public static String translit ( String ruText )
    {
      StringBuilder  result;
      int           ic;
      char          ch;


      ic      = ruText.length();
      result  = new StringBuilder();

      for ( int i=0; i<ic; i++ )
      {
        ch  = ruText.charAt ( i );
        switch ( ch )
        {
          case 1040:  // А
            result.append ( 'A' );
            break;
          case 1041:  // Б
            result.append ( 'B' );
            break;
          case 1042:  // В
            result.append ( 'V' );
            break;
          case 1043:  // Г
            result.append ( 'G' );
            break;
          case 1044:  // Д
            result.append ( 'D' );
            break;
          case 1045:  // E
            result.append ( 'E' );
            break;
          case 1025:  // Ё
            result.append ( 'E' );
            break;
          case 1046:  // Ж
            result.append ( "ZH" );
            break;
          case 1047:  // З
            result.append ( 'Z' );
            break;
          case 1048:  // И
            result.append ( 'I' );
            break;
          case 1049:  // Й
            result.append ( 'I' );
            break;
          case 1050:  // K
            result.append ( 'K' );
            break;
          case 1051:  // Л
            result.append ( 'L' );
            break;
          case 1052:  // M
            result.append ( 'M' );
            break;
          case 1053:  // Н
            result.append ( 'N' );
            break;
          case 1054:  // O
            result.append ( 'O' );
            break;
          case 1055:  // П
            result.append ( 'P' );
            break;
          case 1056:  // P
            result.append ( 'R' );
            break;
          case 1057:  // С
            result.append ( 'S' );
            break;
          case 1058:  // T
            result.append ( 'T' );
            break;
          case 1059:  // У
            result.append ( 'U' );
            break;
          case 1060:  // Ф
            result.append ( 'F' );
            break;
          case 1061:  // Х
            result.append ( 'H' );
            break;
          case 1062:  // Ц
            result.append ( 'C' );
            break;
          case 1063:  // Ч
            result.append ( "CH" );
            break;
          case 1064:  // Ш
            result.append ( "SH" );
            break;
          case 1065:  // Щ
            result.append ( "SH" );
            break;
          case 1066:  // Ъ
            result.append ( '\'' );
            break;
          case 1067:  // Ы
            result.append ( 'Y' );
            break;
          case 1068:  // Ь
            result.append ( '\'' );
            break;
          case 1069:  // Э
            result.append ( 'E' );
            break;
          case 1070:  // Ю
            result.append ( 'U' );
            break;
          case 1071:  // Я
            result.append ( "YA" );
            break;

          case 1072:  // а
            result.append ( 'a' );
            break;
          case 1073:  // б
            result.append ( 'b' );
            break;
          case 1074:  // в
            result.append ( 'v' );
            break;
          case 1075:  // г
            result.append ( 'g' );
            break;
          case 1076:  // д
            result.append ( 'd' );
            break;
          case 1077:  // е
            result.append ( 'e' );
            break;
          case 1105:  // ё
            result.append ( 'e' );
            break;
          case 1078:  // ж
            result.append ( "zh" );
            break;
          case 1079:  // з
            result.append ( 'z' );
            break;
          case 1080:  // и
            result.append ( 'i' );
            break;
          case 1081:  // й
            result.append ( 'i' );
            break;
          case 1082:  // к
            result.append ( 'k' );
            break;
          case 1083:  // л
            result.append ( 'l' );
            break;
          case 1084:  // м
            result.append ( 'm' );
            break;
          case 1085:  // н
            result.append ( 'n' );
            break;
          case 1086:  // о
            result.append ( 'o' );
            break;
          case 1087:  // п
            result.append ( 'p' );
            break;
          case 1088:  // р
            result.append ( 'r' );
            break;
          case 1089:  // с
            result.append ( 's' );
            break;
          case 1090:  // т
            result.append ( 't' );
            break;
          case 1091:  // у
            result.append ( 'u' );
            break;
          case 1092:  // ф
            result.append ( 'f' );
            break;
          case 1093:  // х
            result.append ( 'h' );
            break;
          case 1094:  // ц
            result.append ( 'c' );
            break;
          case 1095:  // ч
            result.append ( "ch" );
            break;
          case 1096:  // ш
            result.append ( "sh" );
            break;
          case 1097:  // щ
            result.append ( "sh" );
            break;
          case 1098:  // ъ
            result.append ( '\'' );
            break;
          case 1099:  // ы
            result.append ( 'y' );
            break;
          case 1100:  // ь
            result.append ( '\'' );
            break;
          case 1101:  // э
            result.append ( 'e' );
            break;
          case 1102:  // ю
            result.append ( 'u' );
            break;
          case 1103:  // я
            result.append ( "ya" );
            break;

          default:
            result.append ( ch );

        }
      }

      return result.toString();
    }

    /* Обработать строку под имя файла - переименовать некоторые символы, перекодировать русские буквы в корявицу */
    public static String processFileName ( String name )
    {
        String result;

        if ( name == null )
        {
            result  = null;
        }
        else
        {
            // Заменяем некоторые символы на подчеркивание
            result  = name.replace   ( ' ', '_' );
            result  = result.replace ( '/', '_' );
            result  = result.replace ( '\\', '_' );
            result  = result.replace ( ':', '_' );
            result  = result.replace ( '.', '_' );

            // перекодируем русские буквы в корявицу
            result  = translit ( result );
        }

        return result;
    }

    /**
     * Перевести секунды в минуты и часы (hh:mm:ss) без округления секунд - для числа.
     * Используется при формивраонии статистических данных о работе процессов.
     *
     *	@param   type     Тип вывода времени - dd:hh:mm:ss, hh:mm:ss или mm:ss
     */
       public static String sec2str ( int lps1, String	type ) {

          int lps2, lps3, lps4, lps5;

          lps4 = lps5	= 0;
          lps2 = lps1 % 60;	// Секунды
          lps3 = lps1 / 60;	// Минуты

          if ( type.equals("mm:ss") )
          {
             return	setFirstZero(lps3) + ":" + setFirstZero(lps2);
          }
          if ( type.equals("hh:mm:ss") )
          {
             if (lps3 > 59) {
                // Если минуты больше 60, то выделить часы
                lps4 = lps3 / 60;
                lps3 = lps3 % 60;
             }
             return	setFirstZero(lps4) + ":" + setFirstZero(lps3) + ":" + setFirstZero(lps2);
          }
          if ( type.equals("dd:hh:mm:ss") )
          {
             // Выделить дни
             if (lps4 > 23) {
                // Если часы больше 23, то выделить дни
                lps5 = lps4 / 24;
                lps4 = lps4 % 24;
             }
             return	setFirstZero(lps5) + ":" + setFirstZero(lps4) + ":" +
                   setFirstZero(lps3) + ":" + setFirstZero(lps2);
          }
          return setFirstZero(lps3) + ":" + setFirstZero(lps2);
     }
    /**
     * Перевести секунды в минуты и часы (hh:mm:ss) без округления секунд - для числа
     * Тип вывода - либо dd:hh:mm:ss, hh:mm:ss, mm:ss
     *
     * @param timeSec время в сек
     * @param type    тип формата вывода сообщения ( @see Format - mm:ss, hh:mm:ss, dd:hh:mm:ss )
     * @return строковое представление времени согласно формату
     */
    public static String sec2str ( long timeSec, Format type )
    {
        String  result;
        long    lps2, lps3, lps4, lps5;

        lps4 = lps5 = 0;
        lps2 = timeSec % 60;  // Секунды
        lps3 = timeSec / 60;  // Минуты

        switch ( type )
        {
            default:
            case MM_SS:
                result = Convert.concatObj ( setFirstZero ( lps3 ), WCons.SEP_COLON, setFirstZero ( lps2 ) );
                break;

            case HH_MM_SS:
                if ( lps3 > 59 )
                {
                    // Если минуты больше 60, то выделить часы
                    lps4 = lps3 / 60;
                    lps3 = lps3 % 60;
                }
                result = Convert.concatObj ( setFirstZero ( lps4 ), WCons.SEP_COLON, setFirstZero ( lps3 ), WCons.SEP_COLON, setFirstZero ( lps2 ) );
                break;

            case DD_HH_MM_SS:
                if ( lps3 > 59 )
                {
                    // Если минуты больше 60, то выделить часы
                    lps4 = lps3 / 60;
                    lps3 = lps3 % 60;
                }
                // Выделить дни
                if ( lps4 > 23 )
                {
                    // Если часы больше 23, то выделить дни
                    lps5 = lps4 / 24;
                    lps4 = lps4 % 24;
                }
                result = Convert.concatObj ( setFirstZero ( lps5 ), WCons.SEP_COLON, setFirstZero ( lps4 ), WCons.SEP_COLON, setFirstZero ( lps3 ), WCons.SEP_COLON, setFirstZero ( lps2 ) );
                break;
        }

        return result;
    }


//----------------------------------------------------------------------------
    /**
     *    Если число меньше десяти - то ставит 0 впереди в выходной строке - 020398.txt
     */
    public static String setFirstZero ( int iprv ) {
       String result;
       if (iprv < 10) result = "0" + iprv;
       else result = WCons.SP + iprv;
       return result;
    }

    public static String setFirstZero ( long iprv )
    {
        String result;
        if ( iprv < 10 ) result = Convert.concatObj ( "0", iprv );
        else result = Long.toString ( iprv );
        return result;
    }

    /**
     * Удалить в строке все символы переноса строк (\n, \r).
     * @param   str     Исходная строка.
     * @return
     */
    public static String delLine ( String str )
    {
       String   result;
       result   = str.replace ( '\n', ' ' );
       result   = result.replace ( '\r', ' ' );
       return result;
    }

    public static boolean str2boolean ( String booleanValue, boolean defaultValue  )
    {
        boolean     result;

        if ( (booleanValue == null) || (booleanValue.length() == 0) )  return defaultValue;

        try
        {
            // здесь только проверка на True в любом регистре
            //result = Boolean.parseBoolean ( strValue );
            // Более общая проверка
            result =  ( booleanValue.equalsIgnoreCase("true") || booleanValue.equalsIgnoreCase("yes") || booleanValue.equalsIgnoreCase("y"));
        } catch ( Exception e )            {
            result  = defaultValue;
        }
        return result;
    }
    
    public static String boolean2str ( boolean value  )
    {
        return Boolean.toString ( value );
    }

    public static Boolean getBoolean ( String strValue, boolean defaultValue )
    {
        Boolean     result;

        if ( (strValue == null) || (strValue.length() == 0) )  return defaultValue;

        try
        {
            // здесь только проверка на True в любом регистре
            //result = Boolean.parseBoolean ( strValue );
            // Более общая проверка
            result =  ( strValue.equalsIgnoreCase("true") || strValue.equalsIgnoreCase("yes") || strValue.equalsIgnoreCase("y"));
        } catch ( Exception e )            {
            result  = defaultValue;
        }
        return result;
    }

}
