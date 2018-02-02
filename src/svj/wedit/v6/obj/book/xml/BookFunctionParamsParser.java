package svj.wedit.v6.obj.book.xml;


import svj.wedit.v6.exception.SystemErrorException;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.ConfigParam;
import svj.wedit.v6.obj.book.BookCons;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.xml.WEditStaxParser;
import svj.wedit.v6.xml.functionParams.FunctionParamsStaxParser;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


/**
 * Распарсить из потока структуру параметров функций уровня книги.
 * <BR/> Структура книги - элементы распарсиваются своим парсером
 * <BR/>
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.10.2014 17:45:43
 */
public class BookFunctionParamsParser extends WEditStaxParser
{
    public void read ( XMLEventReader eventReader, BookContent bookContent ) throws WEditException
    {
        String          tagName, attrName, paramName, paramType;
        XMLEvent        event;
        StartElement    startElement;
        Attribute       attr;
        EndElement      endElement;
        boolean         bWork;
        FunctionId      functionId;
        StringBuilder   errMsg;
        FunctionParameter fParameter;

        tagName     = null;
        functionId  = null;
        errMsg      = new StringBuilder();

        try
        {
            bWork   = true;

            while ( bWork )
            {
                if ( ! eventReader.hasNext() ) return;

                event = eventReader.nextEvent();

                if ( event.isStartElement() )
                {
                    startElement = event.asStartElement();
                    tagName      = startElement.getName().getLocalPart();
                    Log.file.debug ( "----- BookStructure. tagName = %s", tagName );

                    if ( tagName.equals( BookCons.FUNCTION) )
                    {
                        // Название атрибута
                        attr    = startElement.getAttributeByName ( NAME );
                        if ( attr == null )
                            throw new WEditException ("Отсутствует ИД функции.");
                        attrName     = attr.getValue();
                        //attrValue    = getText ( eventReader );
                        functionId  = bookContent.getBookParams().addFunction ( attrName );
                        continue;
                    }

                    // ---------------  параметры функций  -----------------------

                    if ( tagName.equals( ConfigParam.PARAM) )
                    {
                        attr    = startElement.getAttributeByName ( NAME );
                        if ( attr == null )
                        {
                            Log.l.error ( "Ошибка инициализации параметра функции '%s'. Отсутствует имя Параметра.", functionId );
                        }
                        paramName   = attr.getValue();

                        attr    = startElement.getAttributeByName ( TYPE );
                        if ( attr == null )
                        {
                            Log.l.error ( "Отсутствует тип Параметра" );
                        }
                        else
                        {
                            // занести параметр
                            paramType   = attr.getValue();
                            // Получить параметр (индивидуальный xml-парсинг по типу параметра)
                            fParameter  = FunctionParamsStaxParser.parseFunctionParameter ( paramName, paramType, eventReader, errMsg );
                            //Log.l.debug ("--- functionId = %s; fParameter = %s", functionId, fParameter );
                            if ( fParameter != null )  bookContent.getBookParams().setParam ( functionId, paramName, fParameter );
                        }
                        continue;
                    }

                    throw new  WEditException ( null, "Ошибка загрузки параметров функций уровня книги '", bookContent.getName(), "' :\n Неизвестное имя стартового тега '", tagName, "'." );
                }

                if ( event.isEndElement() )
                {
                    endElement  = event.asEndElement();
                    tagName     = endElement.getName().getLocalPart();

                    if ( tagName.equals(BookCons.BOOK_FUNCTION_PARAMS) )
                    {
                        bWork = false;
                    }
                }
            }

        } catch ( WEditException ex ) {
            Log.file.error ( Convert.concatObj ( "error. tagName = ", tagName, "; book = ", bookContent ), ex);
            throw ex;
        } catch ( Exception e ) {
            Log.file.error ("err",e);
            throw new SystemErrorException ( e, "Системная ошибка чтения параметров функций уровня книги '", bookContent.getName(), "' :\n", e );
        }
    }

}
