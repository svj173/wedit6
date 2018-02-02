package svj.wedit.v6.obj.book;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.XmlAvailable;
import svj.wedit.v6.tools.Convert;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * todo Список значений параметров функций, измененных в рамках данной книги.
 * <BR/>
 * <BR/> Необходим пример таких функций  - уровня книги.
 * <BR/> - Имя файла преобразования в RTF, PDF.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.08.2011 16:09:35
 */
public class BookParams    extends XmlAvailable
{
    // лучше перебор всех функций на type=book
    private final Map<FunctionId,Map<String,FunctionParameter>> functions;  // Имя функции / Параметры для нее

    public BookParams ()
    {
        functions   = new HashMap<FunctionId,Map<String,FunctionParameter>>();
    }

    public void toXml ( int level, OutputStream out )   throws WEditException
    {
        int                             ic, ic2;
        Map<String, FunctionParameter>  params;
        FunctionId                      functionId;

        try
        {
            ic  = level + 1;
            ic2 = level + 2;

            outTitle ( level, BookCons.BOOK_FUNCTION_PARAMS, out );

            for ( Map.Entry<FunctionId, Map<String, FunctionParameter>> functionEntry : functions.entrySet () )
            {
                functionId  = functionEntry.getKey();
                params      = functionEntry.getValue();
                if ( (params != null) && ( ! params.isEmpty() ) )
                {
                    outTitle ( ic, BookCons.FUNCTION, functionId.toString(), out );
                    for ( FunctionParameter fp : params.values() )
                    {
                        fp.toXml ( ic2, out );
                    }
                    endTag ( ic, BookCons.FUNCTION, out );
                }
            }

            endTag ( level, BookCons.BOOK_FUNCTION_PARAMS, out );

        } catch ( WEditException we )        {
            throw we;
        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML представления Структуры параметра функции для книги в поток :\n", e );
        }
    }

    /**
     * Выдать данные по реальному обьему книги.
     * Для реальных значений книги обьем параметров функций не нужен.
     */
    @Override
    public int getSize ()
    {
        return 0;
    }

    public boolean isEmpty ()
    {
        return functions.isEmpty();
    }

    public FunctionParameter getParam ( FunctionId functionId, String paramName )
    {
        FunctionParameter             result;
        Map<String,FunctionParameter> functionParams;

        functionParams = functions.get ( functionId );
        if ( functionParams != null )
            result = functionParams.get ( paramName );
        else
            result = null;
        return result;
    }

    public void setParam ( FunctionId functionId, String paramName, FunctionParameter parameter )
    {
        Map<String,FunctionParameter> functionParams;

        if ( (functionId == null) || (paramName == null) || (parameter == null) )  return;

        functionParams = functions.get ( functionId );
        if ( functionParams == null )
        {
            functionParams = new HashMap<String,FunctionParameter>();
            functions.put ( functionId, functionParams );
        }
        functionParams.put ( paramName, parameter );
    }

    public FunctionId addFunction ( String fId )
    {
        FunctionId functionId;
        Map<String,FunctionParameter> functionParams;

        functionId = null;
        try
        {
            functionId = FunctionId.valueOf ( fId );
            if ( ! functions.containsKey ( functionId ) )
            {
                functionParams = new HashMap<String,FunctionParameter>();
                functions.put ( functionId, functionParams );
            }

        } catch ( Exception e )     {
            Log.file.error ( Convert.concatObj ("Error. fId = ", fId ), e );
        }
        return functionId;
    }

}
