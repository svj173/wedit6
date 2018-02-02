package svj.wedit.v6.xml;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.ConfigParam;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.obj.open.BookInfo;
import svj.wedit.v6.obj.open.OpenParamsData;
import svj.wedit.v6.obj.open.ProjectInfo;
import svj.wedit.v6.obj.open.TextInfo;
import svj.wedit.v6.tools.NumberTools;
import svj.wedit.v6.xml.functionParams.FunctionParamsStaxParser;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.InputStream;


/**
 * Парсер xml файла параметров пользoвателя.
 * Загрузить и распарсить .wedit6/user_params.xml файл.
 * <BR/> Грузится динамически. Т.е. каждый новый считанный параметр сразу же отдается в функцию - без буферизации.
 * <BR/>
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 27.07.2011 11:40:14
 */
public class UserParamsStaxParser  extends WEditStaxParser
{
    private static final QName name         = new QName(ConfigParam.NAME);
    private static final QName type         = new QName(ConfigParam.TYPE);
    private static final QName file         = new QName(ConfigParam.FILE);

    //private Section currentSection          = null;
    //private Author  author                  = null;

    private FunctionId  currentFunctionId    = null;
    //private ParameterType pType    = null;
    //private FunctionParameter fp;
    private String      paramName;

    /*  Информация об открытых Проекта, Книгах, Главах. Размерах экрана, кординатах указателя. */
    private final OpenParamsData openInfo   = new OpenParamsData ();



    public UserParamsStaxParser ()
    {
    }

    /*
    public void read ( File file, StringBuilder errMsg ) throws WEditException
    {
        InputStream in;

        try
        {
            if ( ! file.exists() )
            {
                Log.l.error ( null, "Отсутствует файл профиля пользователя '", file, "'");
                errMsg.append ( "Отсутствует файл профиля пользователя '" + file + "'.\n" );
                //throw new WEditException ( null, "Отсутствует файл профиля пользователя '", file, "'");
            }
            else
            {
                in      = new FileInputStream(file);

                read ( in, errMsg );

                Log.file.info ( "Finish. fileName = '", file, "'");
            }

        } catch ( WEditException ex )        {
            throw ex;
        } catch ( Exception e )        {
            Log.file.error ("err",e);
            throw new WEditException ( e, "Системная ошибка чтения файла опиcания проекта '", file, "' :\n", e );
        }
    }
    */

    // from UserParamsManager
    public void read ( InputStream in, StringBuilder errMsg ) throws WEditException
    {
        String          tagName;
        XMLEvent        event;
        StartElement    startElement;
        XMLEventReader  eventReader;
        String          str, vParam, paramType;
        Attribute       attr, attrType;
        XMLInputFactory inputFactory;
        Function        function;
        ProjectInfo     projectInfo;
        BookInfo        bookInfo;
        TextInfo        textInfo;
        int             ic;
        svj.wedit.v6.function.params.FunctionParameter fParameter;

        projectInfo = null;
        bookInfo    = null;
        textInfo    = null;

        try
        {
            // Read the XML document

            inputFactory = XMLInputFactory.newInstance();
            eventReader  = inputFactory.createXMLEventReader(in);
            //eventReader  = inputFactory.createXMLEventReader(in,"UTF-8");
            //eventReader  = inputFactory.createXMLEventReader(new FileReader ());

            while ( eventReader.hasNext() )
            {
                event = eventReader.nextEvent();

                if ( event.isStartElement() )
                {
                    startElement = event.asStartElement();
                    tagName      = startElement.getName().getLocalPart();

                    if ( tagName.equals(ConfigParam.FUNCTION) )
                    {
                        // Начало параметрoв функции
                        // - Взять ИД функции
                        attr    = startElement.getAttributeByName ( name );
                        if ( attr == null )
                        {
                            Log.l.error ( null, "Отсутствует имя Функции." );
                            errMsg.append ( "Отсутствует имя Функции.\n" );
                            currentFunctionId = null;
                            //errMsg.append ( "\n" );
                            //throw new WEditException ("Отсутствует имя Функции");
                        }
                        else
                        {
                            str = attr.getValue();
                            currentFunctionId   = FunctionId.valueOf ( str );
                        }
                        continue;
                    }

                    // ---------------  параметры функций  -----------------------

                    if ( tagName.equals(ConfigParam.PARAM) )
                    {
                        attr    = startElement.getAttributeByName ( name );
                        if ( attr == null )
                        {
                            Log.l.error ( "Ошибка инициализации параметра функции '%s'. Отсутствует имя Параметра.",currentFunctionId );
                            errMsg.append ( "Ошибка инициализации параметра функции '" ).append ( currentFunctionId ).append ( "'. Отсутствует имя Параметра.\n" );
                            //throw new WEditException ("Отсутствует имя Параметра");
                        }
                        paramName   = attr.getValue();
                        //*
                        attr    = startElement.getAttributeByName ( type );
                        if ( attr == null )
                        {
                            Log.l.error ("Отсутствует тип Параметра");
                            errMsg.append ("Отсутствует тип Параметра.\n");
                            //throw new WEditException ("Отсутствует тип Параметра");
                        }
                        else
                        {
                            // Взять функцию
                            if ( Par.GM == null )
                            {
                                errMsg.append ( "Отсутствует Par.GM. Функция = '" );
                                errMsg.append ( currentFunctionId );
                                //errMsg.append ( "'. Параметр = " );
                                //errMsg.append ( fParameter );
                                //errMsg.append ( "\n" );
                            }
                            else
                            {
                                function    = Par.GM.getFm().get ( currentFunctionId );
                                if ( function != null )
                                {
                                    paramType   = attr.getValue();
                                    // Получить параметр (индивидуальный xml-парсинг по типу параметра)
                                    fParameter  = FunctionParamsStaxParser.parseFunctionParameter ( paramName, paramType, eventReader, errMsg );
                                    Log.l.debug ("--- currentFunctionId = ", currentFunctionId, "; fParameter = ", fParameter );
                                    if ( fParameter != null )  function.setParameter ( paramName, fParameter );
                                }
                                else
                                {
                                    Log.l.error ( "-------- Ошибка: для параметра '%s' не найдена функция '%s' ---------.",paramName, currentFunctionId );
                                    errMsg.append ( "Ошибка: для параметра " ).append ( paramName ).append ( "' не найдена функция '" ).append ( currentFunctionId ).append ( "'.\n" );
                                }
                            }
                        }
                        continue;
                    }

                    /*
                    //  ----------------- эти уже не нужны -------------------------
                    if ( tagName.equals(ConfigParam.VALUE) )
                    {
                        str = getText ( eventReader );
                        Log.l.debug ( "--- paramName: ", paramName, ", param: ", str );
                        // Взять функцию
                        function    = Par.GM.getFm().get ( currentFunctionId );
                        try
                        {
                            function.reinit ( paramName, str );
                        } catch ( Exception e )                        {
                            //errMsg.append ( e2 );
                            errMsg.append ( "\n" );
                            Log.l.error ( e, "Ошибка инициализации параметра функции '",currentFunctionId,"'. paramName: ", paramName, ", param: ", str );
                        }
                        //fp.initValue ( str );
                        continue;
                    }

                    if ( tagName.equals(ConfigParam.ITEM) )
                    {
                        // Элемент списка значений одного параметра. Имя=Значение
                        attr    = startElement.getAttributeByName ( name );
                        if ( attr == null )
                        {
                            Log.l.error ("Отсутствует имя Параметра");
                            errMsg.append ("Отсутствует имя Параметра.\n");
                            //throw new WEditException ("Отсутствует имя Параметра");
                        }
                        else
                        {
                            str     = attr.getValue ();
                            // Имя файла
                            vParam = getText ( eventReader );
                            Log.l.debug ( "--- paramName: ", paramName, ", param1: ", str, ", param2: ", vParam );
                            // Взять функцию
                            function    = Par.GM.getFm ().get ( currentFunctionId );
                            try
                            {
                                function.reinit ( paramName, str, vParam );
                            } catch ( Exception e )                        {
                                //errMsg.append ( e2 );
                                errMsg.append ( "\n" );
                                Log.l.error ( e, "Ошибка инициализации параметра функции '",currentFunctionId,"'. paramName: ", paramName, ", param: ", str );
                            }
                        }
                        continue;
                    }

                    if ( tagName.equals(ConfigParam.LIST) )
                    {
                        continue;
                    }
                    */

                    // ------------------------------- ^ параметры функций ^ -----------------------


                    if ( tagName.equals(ConfigParam.PARAMS) )
                    {
                        // todo
                        continue;
                    }

                    if ( tagName.equals(ConfigParam.FUNCTIONS) )
                    {
                        continue;
                    }

                    if ( tagName.equals(ConfigParam.OPEN) )
                    {
                        continue;
                    }

                    if ( tagName.equals(ConfigParam.PROJECTS) )
                    {
                        continue;
                    }

                    if ( tagName.equals(ConfigParam.ACTIVE) )
                    {
                        continue;
                    }

                    if ( tagName.equals(ConfigParam.PROJECT) )
                    {
                        attr    = startElement.getAttributeByName ( file );
                        if ( attr == null )
                        {
                            Log.l.error ("Отсутствует имя файла открытого Проекта");
                            errMsg.append ("Отсутствует имя файла открытого Проекта.\n");
                            //throw new WEditException ("Отсутствует имя файла открытого Проекта");
                        }
                        else
                        {
                            str = attr.getValue();
                            projectInfo = new ProjectInfo(str);
                            openInfo.addProject ( projectInfo );
                        }
                        continue;
                    }

                    if ( tagName.equals(ConfigParam.PROJECT_FILE) )
                    {
                        // nothing
                        continue;
                    }

                    if ( tagName.equals(ConfigParam.BOOK) )
                    {
                        attr    = startElement.getAttributeByName ( name );
                        if ( attr == null )
                        {
                            Log.l.error ("Отсутствует имя файла открытой Книги");
                            errMsg.append ("Отсутствует имя файла открытой Книги.\n");
                            //throw new WEditException ("Отсутствует имя файла открытой Книги");
                        }
                        else
                        {
                            str         = attr.getValue();
                            //str         = getText ( eventReader );
                            bookInfo    = new BookInfo ( str );
                            if ( projectInfo != null )
                                projectInfo.addBook ( bookInfo );
                            else
                            {
                                Log.l.error ( "Отсутствует информация об открытом Проекте для открытой книги '$s'.", str );
                                errMsg.append ( "Отсутствует информация об открытом Проекте для открытой книги '"+ str+ "'.\n"  );
                            }
                            //    throw new WEditException ( null, "Отсутствует информация об открытом Проекте для открытой книги '", str, "'." );
                        }
                        continue;
                    }

                    if ( tagName.equals(ConfigParam.CHAPTER) )
                    {
                        //str         = getText ( eventReader );
                        attr    = startElement.getAttributeByName ( name );
                        if ( attr == null )
                        {
                            Log.l.error ("Отсутствует полный путь для открытого Текста.");
                            errMsg.append ("Отсутствует полный путь для открытого Текста.\n");
                            //throw new WEditException ("Отсутствует полный путь для открытого Текста.");
                        }
                        else
                        {
                            str         = attr.getValue();
                            textInfo    = new TextInfo ( str );
                            if ( bookInfo != null )
                                bookInfo.addText ( textInfo );
                            else
                            {
                                Log.l.error ( "Отсутствует информация об открытой Книге для открытого текста '%s'.", str );
                                errMsg.append ( "Отсутствует информация об открытой Книге для открытого текста '"+ str+ "'.\n" );
                            }
                        }
                        continue;
                    }

                    if ( tagName.equals(ConfigParam.ACTIVE_PROJECT) )
                    {
                        str         = getText ( eventReader );
                        openInfo.setActiveProject ( str );
                        continue;
                    }

                    if ( tagName.equals(ConfigParam.ACTIVE_BOOK) )
                    {
                        str         = getText ( eventReader );
                        openInfo.setActiveBook ( str );
                        continue;
                    }

                    if ( tagName.equals(ConfigParam.ACTIVE_CHAPTER) )
                    {
                        str         = getText ( eventReader );
                        openInfo.setActiveChapter ( str );
                        continue;
                    }

                    if ( tagName.equals(ConfigParam.TEXT_CURSOR) )
                    {
                        str         = getText ( eventReader );
                        if ( textInfo != null ) textInfo.setCursor ( str );
                        continue;
                    }

                    if ( tagName.equals(ConfigParam.FRAME) )
                    {
                        // nothing
                        continue;
                    }

                    if ( tagName.equals(ConfigParam.X) )
                    {
                        str = getText ( eventReader );
                        ic  = NumberTools.getInt ( str, -1 );
                        openInfo.setLocationX ( ic );
                        continue;
                    }

                    if ( tagName.equals(ConfigParam.Y) )
                    {
                        str = getText ( eventReader );
                        ic  = NumberTools.getInt ( str, -1 );
                        openInfo.setLocationY ( ic );
                        continue;
                    }

                    if ( tagName.equals(ConfigParam.WIDTH) )
                    {
                        str = getText ( eventReader );
                        ic  = NumberTools.getInt ( str, -1 );
                        openInfo.setWidth ( ic );
                        continue;
                    }

                    if ( tagName.equals(ConfigParam.HEIGHT) )
                    {
                        str = getText ( eventReader );
                        ic  = NumberTools.getInt ( str, -1 );
                        openInfo.setHeight ( ic );
                        continue;
                    }

                    // Есть много разных параметров
                    //throw new  WEditException ( null, "Ошибка загрузки файла профиля пользователя :\n Неизвестное имя стартового тега '", tagName, "'." );
                    Log.l.error ( "Ошибка загрузки файла профиля пользователя : Неизвестное имя стартового тега '%s'.", tagName );
                    errMsg.append ( "Ошибка загрузки файла профиля пользователя : Неизвестное имя стартового тега '" ).append ( tagName ).append ( "'.\n" );
                }

                if ( event.isEndElement() )
                {
                    /*
                    endElement  = event.asEndElement();
                    tagName     = endElement.getName().getLocalPart();

                    if ( tagName.equals(SECTION) )
                    {
                        // Переклюбчить сосию на родительскую
                        currentSection  = (Section) currentSection.getParent();
                        //bwork = false;
                        //Log.file.debug ("ElementStaXParser.createElement: SECTION = ", section );
                    }
                    */
                }
            }

        } catch ( WEditException ex ) {
            errMsg.append ( "Ошибка чтения файла профиля пользователя : " );
            errMsg.append ( ex.getMessage() );
            errMsg.append ( "\n" );
            throw ex;
        } catch ( Exception e ) {
            errMsg.append ( "Ошибка чтения файла профиля пользователя : " );
            errMsg.append ( e );
            errMsg.append ( "\n" );
            Log.file.error ( "err", e );
            throw new  WEditException ( e, "Ошибка загрузки файла параметров Пользователя :\n", e );
        }
    }

    /*
    private FunctionParameter createParam ( ParameterType pType, String paramName )
    {
        FunctionParameter result;

        result  = null;
        switch ( pType )
        {
            case list_item:
                result = new OrderListParameter ( paramName );
                break;
            case simple:
                result = new SimpleParameter ( paramName, null );
                break;
        }
        return result;
    }
    */

    /*
	<section name="Miniatur">
		<section name="UrfinDjus">
			<book name="urfin_djus.book">/home/svj/projects/SVJ/WEdit-6/test/Miniatur/UrfinDjus/urfin_djus.book</book>
		</section>

     */

    /*
    private String getText ( XMLEventReader eventReader ) throws WEditException
    {
        String      result;
        XMLEvent    event;
        Characters characters;

        result = null;

        try
        {
            event = eventReader.nextEvent();
            //Log.file.debug ("ElementStaXParser.getText: event = " + event );

            if ( event.isCharacters() )
            {
                characters = event.asCharacters();
                if ( characters != null )
                {
                    result = characters.getData().trim();
                    if ( result.length() == 0 ) result = null;
                }
            }
            // Иначе - это тег закрытия - при отсутствии данных - например: <object_class></object_class>

        } catch ( Exception e )        {
            Log.file.error ("err",e);
            throw new  WEditException (  e, "Ошибка получения текстовых данных во время загрузки файла со свойствами элементов :\n", e );
        }

        return result;
    }
    */

    // test
    public static void main ( String[] args )
    {
        UserParamsStaxParser    parser;
        StringBuilder           errMsg;
        String                  fileName;
        FileInputStream         fis;

        try
        {
            Par.MODULE_HOME = "/home/svj/projects/SVJ/WEdit-6/project";
            //Par.MODULE_HOME = "/home/svj/projects/Eltex/eltex-web-ems";
            System.setProperty ( "module.home", Par.MODULE_HOME );

            //Log.init ( Par.MODULE_HOME + "/conf/logger_console.txt" );
            //Log.init ( "/home/svj/programm/ems_server/conf/logger.cfg" );

            //fileName    = "/home/svj/.wedit6/user_params.xml";
            fileName    = "/home/svj/.wedit6/" + ConfigParam.USER_PARAMS_FILE;
            fis        = new FileInputStream ( fileName );

            errMsg   = new StringBuilder ( 128 );
            parser   = new UserParamsStaxParser();
            parser.read ( fis, errMsg );

            Log.file.debug ( "errMsg :\n ", errMsg );

        } catch ( Exception e )        {
            Log.file.error ("err",e);
        }
    }

    // Сформировать информацию об открытых Проекта, Книгах, Главах.
    public OpenParamsData getOpenInfo ()
    {
        return openInfo;
    }

}
