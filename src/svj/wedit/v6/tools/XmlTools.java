package svj.wedit.v6.tools;


import svj.wedit.v6.WCons;

import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.01.2013 9:56
 */
public class XmlTools
{
    public static final char    START   = '<';
    public static final String  FINISH  = "</";
    public static final char    END     = '>';
    public static final char    KAV     = '"';
    public static final char    EQ      = '=';
    public static final char    TAB     = '\t';
    public static final char    END_LINE     = '\n';

    private static String codePage  = null;


    /**
     * Проверяет на наличие в тексте символов XML - угловые скобки, &.
     * @param text Проверяемый текст.
     * @return     TRUE - есть символы XML.
     */
    public static boolean checkXmlSymbols ( String text )
    {
        if ( text == null )  return false;
        if ( text.contains ( "<") )  return true;
        if ( text.contains ( ">") )  return true;
        //if ( text.contains ( "&") )  return true; // можно, иначе как передавать спец-символы XML?
        return false;
    }

    public static void startTag ( FileOutputStream file, String name )
            throws Exception
    {
        saveChar ( file, START );
        saveWord ( file, name );
        saveChar ( file, END );
    }

    public static void startTag ( FileOutputStream file, String name, String attr1, String value1 )
            throws Exception
    {
        saveChar ( file, START );
        saveWord ( file, name );
        saveChar ( file, ' ' );
        saveWord ( file, attr1 );
        saveChar ( file, EQ );
        saveChar ( file, KAV );
        saveWord ( file, value1 );
        saveChar ( file, KAV );
        saveChar ( file, END );
    }

    /**
     * Тэг с атрибутами.
     * @param name
     * @param attr
     * @return законченная строка тэга. без последнего /.
     */
    public static void startTag ( FileOutputStream file, String name, Hashtable attr )
            throws Exception
    {
        Enumeration en;
        String      key, value;

        saveChar ( file, START );
        saveWord ( file, name );

        en  = attr.keys ();
        while ( en.hasMoreElements () )
        {
            key     = (String) en.nextElement ();
            value   = (String) attr.get ( key );
            saveChar ( file, ' ' );
            saveWord ( file, key );
            saveChar ( file, EQ );
            saveChar ( file, KAV );
            saveWord ( file, value );
            saveChar ( file, KAV );
        }

        saveChar ( file, END );
    }

    public static void endTag ( FileOutputStream file, String name )
            throws Exception
    {
        saveWord ( file, FINISH );
        saveWord ( file, name );
        saveChar ( file, END );
    }

    public static void createTag ( FileOutputStream file, String name, String value )
            throws Exception
    {
        startTag(file,name);
        saveWord ( file, value );
        endTag(file,name);
    }

    // -------------- Здесь используем внутреннюю табуляцию и перевод строки ------

    public static void startTag ( FileOutputStream file, String name, int tab )
            throws Exception
    {
        saveTab ( file, tab );
        startTag ( file, name );
        saveChar ( file, END_LINE );
    }

    public static void startTag ( FileOutputStream file, String name, String attr1, String value1, int tab )
            throws Exception
    {
        saveTab ( file, tab );
        startTag ( file, name, attr1, value1 );
        saveChar ( file, END_LINE );
    }

    public static void startTag ( FileOutputStream file, String name, Hashtable attr, int tab )
            throws Exception
    {
        saveTab ( file, tab );
        startTag ( file, name, attr );
        saveChar ( file, END_LINE );
    }

    public static void endTag ( FileOutputStream file, String name, int tab )
            throws Exception
    {
        saveTab ( file, tab );
        endTag ( file,name );
        saveChar ( file, END_LINE );
    }

    public static void createTag ( FileOutputStream file, String name, String value, int tab )
            throws Exception
    {
        saveTab ( file, tab );
        createTag ( file,name, value );
        saveChar ( file, END_LINE );
    }




    public static StringBuilder startTag ( String name )
    {
        StringBuilder    result  = new StringBuilder ( 32 );
        result.append ( START );
        result.append ( name );
        result.append ( END );
        return result;
    }

    public static StringBuilder startTag ( String name, String attr1, String value1 )
    {
        StringBuilder    result  = new StringBuilder ( 32 );
        result.append ( START );
        result.append ( name );
        result.append ( ' ' );
        result.append ( attr1 );
        result.append ( "=" );
        result.append ( KAV );
        result.append ( value1 );
        result.append ( KAV );
        result.append ( END );
        return result;
    }

    /**
     * Тэг с атрибутами.
     * @param name
     * @param attr
     * @return законченная строка тэга. без последнего /.
     */
    public static StringBuilder startTag ( String name, Hashtable attr )
    {
        Enumeration en;
        String  key, value;
        StringBuilder    result  = new StringBuilder ( 32 );

        result.append ( START );
        result.append ( name );

        en  = attr.keys ();
        while ( en.hasMoreElements () )
        {
            key = (String) en.nextElement ();
            value   = (String) attr.get ( key );
            result.append ( ' ' );
            result.append ( key );
            result.append ( "=" );
            result.append ( KAV );
            result.append ( value );
            result.append ( KAV );
        }

        result.append ( END );
        return result;
    }

    public static StringBuilder endTag ( String name )
    {
        StringBuilder    result  = new StringBuilder ( 32 );
        result.append ( FINISH );
        result.append ( name );
        result.append ( END );
        return result;
    }

    public static StringBuilder createTag ( String name, String value )
    {
        StringBuilder    result  = new StringBuilder ( 32 );
        result.append ( startTag(name) );
        result.append ( value );
        result.append ( endTag(name) );
        return result;
    }

    // -------------- Здесь используем внутреннюю табуляцию и перевод строки ------

    public static StringBuilder startTag ( String name, int tab )
    {
        StringBuilder    result  = new StringBuilder ( 32 );
        int i;
        for ( i=0; i<tab; i++ )   result.append ( WCons.TAB );
        result.append ( startTag ( name ) );
        result.append ( WCons.END_LINE );
        return result;
    }

    public static StringBuilder startTag ( String name, String attr1, String value1, int tab )
    {
        StringBuilder    result  = new StringBuilder ( 32 );
        int i;
        for ( i=0; i<tab; i++ )   result.append ( WCons.TAB );
        result.append ( startTag ( name, attr1, value1 ) );
        result.append ( WCons.END_LINE );
        return result;
    }

    public static StringBuilder startTag ( String name, Hashtable attr, int tab )
    {
        StringBuilder    result  = new StringBuilder ( 32 );
        int i;
        for ( i=0; i<tab; i++ )   result.append ( WCons.TAB );
        result.append ( startTag ( name, attr ) );
        result.append ( WCons.END_LINE );
        return result;
    }

    public static StringBuilder endTag ( String name, int tab )
    {
        StringBuilder    result  = new StringBuilder ( 32 );
        int i;
        for ( i=0; i<tab; i++ )   result.append ( WCons.TAB );
        result.append ( endTag ( name ) );
        result.append ( WCons.END_LINE );
        return result;
    }

    public static StringBuilder createTag ( String name, String value, int tab )
    {
        StringBuilder    result  = new StringBuilder ( 32 );
        int i;
        for ( i=0; i<tab; i++ )   result.append ( WCons.TAB );
        result.append ( createTag ( name, value ) );
        result.append ( WCons.END_LINE );
        return result;
    }

    public static void saveWord ( FileOutputStream file, String str ) throws Exception
    {
        byte[] bb;
        if ( str == null )  return;
        if ( codePage == null )
            bb  = str.getBytes();
        else
            bb  = str.getBytes(codePage);
        file.write ( bb );
    }

    public static void saveLine ( FileOutputStream file, String str ) throws Exception
    {
        if ( str == null )  return;
        saveWord ( file, str );
        saveChar ( file, '\n' );
    }

    public static void saveChar ( FileOutputStream file, char ch ) throws Exception
    {
        file.write ( ch );
    }

    public static void saveTab ( FileOutputStream file, int ic ) throws Exception
    {
        for ( int i=0; i<ic; i++ )  saveChar ( file, TAB );
    }

    public static void setCodePage ( String codePage )
    {
        XmlTools.codePage = codePage;
    }
}
