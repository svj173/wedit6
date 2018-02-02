package svj.wedit.v6.xml;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.ConfigParam;
import svj.wedit.v6.tools.Convert;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;


/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 20.08.2011 9:37:33
 */
public class WEditStaxParser
{
    protected static final QName  NAME         = new QName("name");
    protected static final QName  TYPE         = new QName( ConfigParam.TYPE);

    protected String getText ( XMLEventReader eventReader ) throws WEditException
    {
        String      result;
        XMLEvent    event;
        Characters  characters;

        result = null;

        try
        {
            event = eventReader.nextEvent();
            //Log.file.debug ("getText: event = ", event );

            if ( event.isCharacters() )
            {
                characters = event.asCharacters();
                //Log.file.debug ("WEditStaxParser.getText: characters = ", characters );
                if ( characters != null )
                {
                    //result = characters.getData().trim();
                    result = characters.getData();
                    //if ( result.length() == 0 ) result = null;
                    //*
                    // Что это ??? - проверка что нельзя убирать крайние пробелы?
                    // - characters.isWhiteSpace() - true - значит вся стркоа состоит из пробелов.
                    // - characters.isIgnorableWhiteSpace()
                    if ( (result != null) && (!characters.isIgnorableWhiteSpace()) && (!characters.isWhiteSpace()) )
                    {
                        //result = characters.getData();
                        //result  = result.trim();  -- нельзя, иначе пропадут разделительные пробелы между текстами разных шрифтов.
                        // преобразуем символы html - >< и т.д. -- Рано еще - т.к. такие строки почему-то парсер разбивает на отдельные текстовые лексемы (&.. gt;..).
                        //     Их сначала надо как-то собирать.
                        //result  = Convert.revalidateXml ( result );
                        if ( result.length() == 0 ) result = null;
                    }
                    //*/
                }
            }
            // Иначе - это тег закрытия - при отсутствии данных - например: <object_class></object_class>

        } catch ( Exception e )        {
            Log.file.error ( Convert.concatObj ( "error. result = ", result ), e);
            throw new  WEditException (  e, "Ошибка получения текстовых данных тега :\n", e );
        }

        return result;
    }

}
