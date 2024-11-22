package svj.wedit.v6.function.statistic;


import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.DatePeriod;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.Section;
import svj.wedit.v6.obj.book.BookCons;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookTitle;
import svj.wedit.v6.obj.book.xml.BookContentOnlyAttrStaxParser;
import svj.wedit.v6.obj.project.IProjectSectionParser;
import svj.wedit.v6.tools.BookTools;
import svj.wedit.v6.tools.Convert;

import java.io.FileInputStream;
import java.util.Date;
import java.util.Map;


/**
 * Сборщик информации обо всех редактируемых книгах за указанный Период.
 * <BR/> Парсит каждую книгу (в методе startBook) на предмет получения данных - атрибуты книги last_change_date и create_date.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 02.02.2018 17:00:04
 */
public class StatEditBookInfoParser implements IProjectSectionParser<String>
{
    private String sp   = "&nbsp;&nbsp;&nbsp;";
    private final StringBuilder text    = new StringBuilder();

    private final Date startDate, endDate;
    private final DatePeriod datePeriod;
    private Project currentProject = null;
    private Section currentSection = null;

    private final BookContentOnlyAttrStaxParser bookParser;

    public StatEditBookInfoParser ( DatePeriod datePeriod )
    {
        this.datePeriod = datePeriod;

        bookParser = new BookContentOnlyAttrStaxParser();

        // Получить дату Начала периода и дату Окончания.
        endDate     = new Date();

        long l = endDate.getTime() - (datePeriod.getTime() * 1000);
        startDate   = new Date(l);
    }

    @Override
    public String getResult ()
    {
        return text.toString();
    }

    @Override
    public void startDocument ()
    {
        text.append ( "<html>\n" );

        text.append ( "<B><font color='black'>" );
        text.append ( datePeriod.getName() + ": "+Convert.getEnDateTime ( startDate ) + " -- "+Convert.getEnDateTime ( endDate ) );
        text.append ( "</font></B>" );
        text.append ( "<br/><br/>\n" );
    }

    @Override
    public void endDocument ()
    {
        text.append ( "</html>\n" );
    }

    @Override
    public void startProject ( Project project )
    {
        currentProject = project;

        text.append ( "<B><font color='red'>" );
        text.append ( project.getName() );
        text.append ( "</font></B>" );
        text.append ( "<br/>\n" );
    }

    @Override
    public void endProject ( Project project, int booksCount )
    {
        /*
        text.append ( "<B>== Всего книг: " );
        text.append ( booksCount );
        text.append ( "</B><br/><br/>\n" );
        */
    }

    @Override
    public void startSection ( Section section, int level )
    {
        currentSection = section;

        /*
        if ( level != 1 )
        {
            text.append ( Convert.createSpace ( sp, level) );
            text.append ( "<B><font color='green'>" );
            text.append ( section.getName() );
            text.append ( "</font></B>" );
            text.append ( "<br/>\n" );
        }
        */
    }

    @Override
    public void endSection ( Section section, int level )
    {
        // Секция закончилась, значит надо ставить текущей вышестоящую.
        currentSection = (Section ) section.getParent();
    }

    @Override
    public void startBook ( BookTitle book, int level )
    {
        String fileName = null;

        /*
        text.append ( Convert.createSpace ( sp, level) );
        text.append ( "<B><font color='blue'>" );
        text.append ( book.getName() );
        text.append ( "</font></B>" );
        //text.append ( "<br/>\n" );
        */

        //text.append ( book.getFileName() );    // короткое имя файла

        try
        {
            if ( (currentSection != null) && (currentProject != null ) )
            {
                fileName = BookTools.createFilePath ( currentProject, currentSection, book );

                //text.append ( sp );
                //text.append ( fileName );    // полное имя файла

                // Запускаем парсер книги и выкусываем два атрибута дат.
                // - Отчет по книге складываем в Итог только если Период даты устраивает.
                BookContent bookContent;
                Map<String, String> attrs;
                String str;
                
                bookContent = new BookContent ( book.getName(), fileName );
                bookParser.read ( new FileInputStream ( fileName ), bookContent );
                // Читаем атрибуты
                attrs   = bookContent.getBookAttrs();
                str     = checkDate ( attrs );            // + флаг - создана-редакция
                if ( str != null )
                {
                    //text.append ( Convert.createSpace ( sp, level ) );
                    text.append ( sp );
                    text.append ( "<B><font color='blue'>" );
                    text.append ( book.getName() );
                    text.append ( "</font></B>&nbsp;&nbsp;&nbsp;- " );
                    text.append ( str );
                    text.append ( "<br/>" );
                }
            }
            else
            {
                text.append ( book.getName() );
                text.append ( ": Error - cannot create Book filePath.<br/>" );
            }

        } catch ( Exception e )        {
            Log.file.error ( "Error. currentProject = "+currentProject+"; currentSection = "+currentSection+"; book = "+book, e );
            text.append ( "Error. " );
            text.append ( book.getName() );
            text.append ( ": " );
            text.append ( e.getMessage() );
            text.append ( "<br/>" );
        }
    }

    /**
     * + флаг - создана-редакция
     * @param attrs   Атрибуты книги с датами создания и редактирования.
     * @return        Текст в виде - тип и дата, либо Null если дата не попадается в указанный период.
     */
    private String checkDate ( Map<String, String> attrs )
    {
        String str, result;
        Date   date;

        result = null;

        // формат даты - 2014-08-27 22:08:37
        str = attrs.get ( BookCons.ATTR_NAME_CREATE_DATE );
        if ( str != null )
        {
            try
            {
                date = Convert.str2date ( str, "yyyy-MM-dd HH:mm:ss" );
                //if ( date.after ( startDate ) || date.equals (  ))
                int ic = date.compareTo ( startDate );
                if ( ic >= 0 )
                {
                    result = "Создана: " + str;
                }
            } catch ( Exception e )      {
                result = str + ": "+e.getMessage();
            }
        }

        str = attrs.get ( BookCons.ATTR_NAME_LAST_CHANGE_DATE );
        if ( str != null )
        {
            try
            {
                date = Convert.str2date ( str, "yyyy-MM-dd HH:mm:ss" );
                //if ( date.after ( startDate ) || date.equals (  ))
                int ic = date.compareTo ( startDate );
                if ( ic >= 0 )
                {
                    result = "Редакция: " + str;
                }
            } catch ( Exception e )     {
                result = str + ": "+e.getMessage();
            }
        }

        return result;
    }

    @Override
    public void endBook ( BookTitle book, int level )
    {
        //text.append ( "<br/>\n" );
    }

}
