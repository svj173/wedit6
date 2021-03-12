package svj.wedit.v6.obj.book;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;

import java.io.OutputStream;
import java.util.Properties;


/**
 * Таблица.
 * <BR/> Могут содержаться и обьекты - картинки, другие таблицы, цветной текст, TextArea
 * <BR/>
 * <BR/> XML Структура
 *
 * table rowSize=1 columnSize=5
 *   columnNames
 *     column - name1 - /column
 *     column - name2 - /column
 *   /columnNames
 *   values
 *     1
 *       value - 123 - /value
 *       value - 223 - /value
 *       value - 323 - /value
 *     /1
 *     2
 *       value - 123 - /value
 *       value - 223 - /value
 *       value - 323 - /value
 *     /2
 *
 *   /values
 * /table
 *
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.12.2017 16:50:12
 */
public class TableTextObject extends TextObject
{
    /** Имена колонок */
    private final String[] columnNames;

    private final int rowSize;
    //private final int columnSize;

    /** Данные */
    //private final Collection<Collection<String>> values;
    private final String[][] values;

    public TableTextObject ( int row, int column )
    {
        super();
        //setText ( tableName );      ???

        rowSize     = row;
        //columnSize  = column;

        columnNames = new String[column];
        //values      = new ArrayList<Collection<String>>();
        values      = new String[row][column];
    }

    public TableTextObject clone ()
    {
        TableTextObject result;

        result  = new TableTextObject ( getRowCount(), getColumnCount() );
        result.setStyle ( getStyle() );

        for ( int i=0; i<getColumnCount(); i++ )  result.addColumnName ( i, columnNames[i] );

        for ( int i=0; i<getRowCount(); i++ )
        {
            for ( int k=0; k<getColumnCount(); k++ )
            {
                result.setValue ( i, k, values[i][k] );
            }
        }

        return result;
    }

    /**
     * Структура
     * <pre>
 * table rowSize=1 columnSize=5
 *   columnNames
 *     column - name1 - /column
 *     column - name2 - /column
 *   /columnNames
 *   values
 *     1
 *       value - 123 - /value
 *       value - 223 - /value
 *       value - 323 - /value
 *     /1
 *     2
 *       value - 123 - /value
 *       value - 223 - /value
 *       value - 323 - /value
 *     /2
 *
 *   /values
 * /table
     </pre>
     */
    // todo
    @Override
    public void toXml ( int level, OutputStream out ) throws WEditException
    {
        int ic;
        String tagName;
        Properties attr;

        tagName = "table";
        ic  = level + 1;
        try
        {
            //outTag ( level+1, "img", getText(), out );

            // - table rowSize=1 columnSize=5
            attr = new Properties ();
            attr.put ( "rowSize", getRowCount() );
            attr.put ( "columnSize", getColumnCount() );
            outTitle ( ic, tagName, attr, out );

        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML тега TABLE в поток :\n", e );
        }
    }

    public int getColumnCount ()
    {
        return columnNames.length;
        //return columnSize;
    }

    public int getRowCount ()
    {
        return rowSize;
    }


    public void addColumnName ( int column, String columnName )
    {
        columnNames[column] = columnName;
    }

    public void setValue ( int row, int column, Object value )
    {
        if ( value == null )
            values[row][column] = null;
        else
            values[row][column] = value.toString();
    }

    public TextObjectType getType()
    {
        return TextObjectType.TABLE;
    }

}
