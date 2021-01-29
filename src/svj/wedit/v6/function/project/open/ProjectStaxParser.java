package svj.wedit.v6.function.project.open;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.Author;
import svj.wedit.v6.obj.book.BookStatus;
import svj.wedit.v6.obj.book.BookTitle;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.Section;
import svj.wedit.v6.tools.Convert;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


/**
 * загрузить и распарсить project.xml файл.
 * <BR/>
 * <BR/>
 * < ?xml version="1.0" encoding="UTF-8"? >

< project name="rrrr">
	< author>
		< name>< /name>
		< last_name>< /last_name>
		< e_mail>< /e_mail>
	< /author>
	< create_date>05.08.2011 17:58:33< /create_date>
	< section name="Miniatur">
		< section name="UrfinDjus">
			< book name="urfin_djus.book">/home/svj/projects/SVJ/WEdit-6/test/Miniatur/UrfinDjus/urfin_djus.book< /book>
		< /section>
		< section name="Dream">
			< book name="dream.book">/home/svj/projects/SVJ/WEdit-6/test/Miniatur/Dream/dream.book< /book>
			< book name="relikt.book">/home/svj/projects/SVJ/WEdit-6/test/Miniatur/Dream/relikt.book< /book>
		< /section>
		< section name="ZS">
			< book name="zs5.book">/home/svj/projects/SVJ/WEdit-6/test/Miniatur/ZS/zs5.book< /book>
			< book name="zs4.book">/home/svj/projects/SVJ/WEdit-6/test/Miniatur/ZS/zs4.book< /book>
		< /section>
	< /section>
< /project>

 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 08.08.2011 17:27:18
 */
public class ProjectStaxParser
{
    private static final String PROJECT     = "project";
    private static final String SECTION     = "section";
    
    private static final String AUTHOR      = "author";
    private static final String CREATE_DATE = "create_date";
    private static final String BOOK        = "book";
    private static final String ID          = "id";

    // author
    private static final String NAME        = "name";
    private static final String LAST_NAME   = "last_name";
    private static final String E_MAIL      = "e_mail";

    private static final QName name         = new QName(NAME);
    private static final QName dirName      = new QName("dirName");
    private static final QName status       = new QName("status");

    private Section currentSection          = null;
    private Author  author                  = null;


    /**
     *
     * @param file   Это файл 'project.xml'
     * @return
     * @throws WEditException
     */
    public Project read ( File file ) throws WEditException
    {
        Project result;
        InputStream in;

        try
        {
            if ( ! file.exists() )
                throw new WEditException ( null, "Отсутствует файл  '", file, "'");

            in      = new FileInputStream ( file );

            result  = new Project ( file.getParentFile() );
            //result.setProjectDir ( file.getParentFile() );   // Заносим директорию
            read ( in, result );

            Log.file.info ( "Finish. fileName = '", file, "'");

        } catch ( WEditException ex )        {
            throw ex;
        } catch ( Exception e )        {
            Log.file.error ("err",e);
            throw new WEditException ( e, "Системная ошибка чтения файла опиcания проекта '", file, "' :\n", e );
        }

        return result;
    }

    public void read ( InputStream in, Project project ) throws WEditException
    {
        String          tagName, str, str2;
        XMLEvent        event;
        StartElement    startElement;
        XMLEventReader  eventReader;
        Section         section;
        Attribute       attr, statusAttr;
        XMLInputFactory inputFactory;
        BookTitle       bookTitle  = null;
        EndElement      endElement;

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

                    if ( tagName.equals(PROJECT) )
                    {
                        // Начало документа
                        attr    = startElement.getAttributeByName ( name );
                        if ( attr == null )
                            throw new WEditException ("Отсутствует имя Проекта");
                        str = attr.getValue();
                        project.setName ( str );
                        //currentSection  = project.getRootSection();
                        continue;
                    }

                    if ( tagName.equals(BOOK) )
                    {
                        attr    = startElement.getAttributeByName ( name );
                        if ( attr == null )
                            throw new WEditException ("Отсутствует имя Книги");
                        str         = getText ( eventReader );
                        bookTitle   = new BookTitle ( project, currentSection, attr.getValue(), str );
                        currentSection.addBook( bookTitle );
                        statusAttr    = startElement.getAttributeByName ( status );
                        if ( statusAttr != null )
                        {
                            bookTitle.setBookStatus ( BookStatus.getStatus ( statusAttr.getValue() ) );
                            //    throw new WEditException ("Отсутствует статус Книги");
                        }
                        continue;
                    }


                    if ( tagName.equals ( ID ) )
                    {
                        str = getText ( eventReader );
                        if ( bookTitle != null )  bookTitle.setId ( str );
                        continue;
                    }

                    if ( tagName.equals ( AUTHOR ) )
                    {
                        // Начало блока
                        author = new Author();
                        project.setAuthor ( author );
                        continue;
                    }

                    if ( tagName.equals(NAME) )
                    {
                        str = getText ( eventReader );
                        author.setFirstName(str);
                        continue;
                    }

                    if ( tagName.equals(LAST_NAME) )
                    {
                        str = getText ( eventReader );
                        author.setLastName(str);
                        continue;
                    }

                    if ( tagName.equals(E_MAIL) )
                    {
                        str = getText ( eventReader );
                        author.setEmail(str);
                        continue;
                    }

                    if ( tagName.equals(CREATE_DATE) )
                    {
                        str = getText ( eventReader );
                        // 05.08.2011 17:58:33
                        project.setCreateDate ( Convert.getRuDate ( str ) );
                        continue;
                    }

                    if ( tagName.equals(SECTION) )
                    {
                        // Название раздела
                        attr    = startElement.getAttributeByName ( name );
                        if ( attr == null )
                            throw new WEditException ("Отсутствует имя Раздела");
                        str     = attr.getValue();
                        // Имя файла-директории Раздела
                        attr    = startElement.getAttributeByName ( dirName );
                        if ( attr == null )
                            throw new WEditException ("Отсутствует имя Директории Раздела");
                        str2    = attr.getValue();
                        section = new Section ( str, str2, project );
                        if ( currentSection == null )
                        {
                            // это root
                            project.setRootSection ( section );
                        }
                        else
                        {
                            currentSection.addSection ( section );
                            section.setParent ( currentSection );
                        }
                        currentSection  = section;
                        continue;
                    }

                    throw new  WEditException ( null, "Ошибка загрузки файла описания Проекта :\n Неизвестное имя стартового тега '", tagName, "'." );
                }

                if ( event.isEndElement() )
                {
                    endElement  = event.asEndElement();
                    tagName     = endElement.getName().getLocalPart();

                    if ( tagName.equals(SECTION) )
                    {
                        // Переклюбчить сосию на родительскую
                        currentSection  = (Section) currentSection.getParent();
                        //bwork = false;
                        //Log.file.debug ("ElementStaXParser.createElement: SECTION = ", section );
                    }
                }
            }

        } catch (WEditException ex) {
            throw ex;
        } catch (Exception e) {
            Log.file.error ("err",e);
            throw new  WEditException ( e, "Ошибка загрузки файла описания Проекта :\n", e );
        }
    }

    /*
	<section name="Miniatur">
		<section name="UrfinDjus">
			<book name="urfin_djus.book">/home/svj/projects/SVJ/WEdit-6/test/Miniatur/UrfinDjus/urfin_djus.book</book>
		</section>

     */

    private String getText ( XMLEventReader eventReader ) throws WEditException
    {
        String      result;
        XMLEvent    event;
        Characters  characters;

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
                    // данные сборника из xml файла - чистим крайние пробелы - на всякий случай.
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

    // test
    public static void main ( String[] args )
    {
        ProjectStaxParser   parser;
        Project             project;

        try
        {
            Par.MODULE_HOME = "/home/svj/programm/ems_server";
            //Par.MODULE_HOME = "/home/svj/projects/Eltex/eltex-web-ems";
            System.setProperty ( "module.home", Par.MODULE_HOME );

            //Log.init ( Par.MODULE_HOME + "/conf/logger.cfg" );
            //Log.init ( "/home/svj/programm/ems_server/conf/logger.cfg" );

            parser   = new ProjectStaxParser();
            project  = parser.read( new File("/Projects/SVJ/wedit-6/test/Miniatur/project.xml"));

            Log.file.debug ( "project = ", project );

        } catch ( Exception e )        {
            Log.file.error ("err",e);
        }
    }
    
}
