package svj.wedit.v6.xml.functionParams;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.function.params.SimpleParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.ConfigParam;
import svj.wedit.v6.tools.Convert;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 14.10.2017 13:24
 */
public class SimpleParamParser  extends FunctionParamsStaxParser
{
    @Override
    public FunctionParameter parse ( XMLEventReader eventReader, String paramName, StringBuilder errMsg )
    {
        //errMsg.append ( "Функция 'SimpleParamParser' не реализована.\n" );
        String              tagName;
        XMLEvent            event;
        StartElement        startElement;
        EndElement          endElement;
        String              str;
        SimpleParameter     fParameter;
        boolean             bwork;
        Characters          characters;

        fParameter = null;

        try
        {
            bwork = true;

            while ( bwork && eventReader.hasNext() )
            {
                event = eventReader.nextEvent();

                if ( event.isStartElement() )
                {
                    startElement = event.asStartElement();
                    tagName      = startElement.getName().getLocalPart();

                    if ( tagName.equals( ConfigParam.VALUE) )
                    {
                        str = getText ( eventReader );
                        Log.file.info ( "---- SimpleParamParser: paramName = %s; value = '%s'",paramName, str );
                        fParameter = new SimpleParameter ( paramName, str );
                        //fParameter.setHasEmpty ( true );
                    }
                    else if ( tagName.equals( "hasEmpty") )
                    {
                        str = getText ( eventReader );
                        Log.file.debug ( "---- SimpleParamParser: paramName = %s; value = '%s'",paramName, str );
                        if ( fParameter != null )  fParameter.setHasEmpty ( Convert.getBoolean ( str, true ) );
                    }
                    else if ( tagName.equals( ConfigParam.RU_NAME ) )
                    {
                        str = getText ( eventReader );
                        if ( fParameter != null )    fParameter.setRuName ( str );
                    }
                    else
                    {
                        errMsg.append ( "Неизвестный стартовый тег простого параметра : " );
                        errMsg.append ( tagName );
                        errMsg.append ( "\n" );
                    }
                }

                else if ( event.isCharacters() )
                {
                    if ( fParameter != null )
                    {
                        // это продолжение значения параметра - собираем (но может быть и мусор в виде кучи пробелов)
                        characters  = event.asCharacters();
                        str         = characters.getData();
                        //Log.file.error ( "---- SimpleParamParser: lost text. paramName = %s; text = '%s'", paramName, str );
                        str         = str.trim();
                        fParameter.addValue ( str );
                    }
                }

                else if ( event.isEndElement() )
                {
                    endElement  = event.asEndElement();
                    tagName     = endElement.getName().getLocalPart();

                    if ( tagName.equals(ConfigParam.PARAM) )
                    {
                        // Тег закрытия опиcания параметра - завершить работы
                        bwork = false;
                        // Конвертим текст
                        fParameter.setValue ( Convert.revalidateXml ( fParameter.getValue() ) );
                    }
                }
            }

        } catch ( WEditException ex ) {
            errMsg.append ( "Ошибка чтения простого параметра : " );
            errMsg.append ( ex.getMessage() );
            errMsg.append ( "\n" );
            Log.file.error ( "err", ex );
        } catch ( Exception e ) {
            errMsg.append ( "Ошибка чтения простого параметра : " );
            errMsg.append ( e );
            errMsg.append ( "\n" );
            Log.file.error ( "err", e );
        } catch ( NoSuchMethodError ne ) {
            errMsg.append ( "Ошибка чтения простого параметра - нет такого метода : " );
            errMsg.append ( ne );
            errMsg.append ( "\n" );
            Log.file.error ( "err", ne );
        }

        return fParameter;
    }

}
