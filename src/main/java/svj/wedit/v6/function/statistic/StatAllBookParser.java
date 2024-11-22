package svj.wedit.v6.function.statistic;


import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.Section;
import svj.wedit.v6.obj.book.BookTitle;
import svj.wedit.v6.obj.project.IProjectSectionParser;
import svj.wedit.v6.tools.Convert;


/**
 * Сборщик информации обо всем открытом.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 07.04.2012 8:42:04
 */
public class StatAllBookParser implements IProjectSectionParser<String>
{
    private String sp   = "&nbsp;&nbsp;&nbsp;";
    private final StringBuilder text    = new StringBuilder();

    @Override
    public String getResult ()
    {
        return text.toString();
    }

    @Override
    public void startDocument ()
    {
        text.append ( "<html>\n" );
    }

    @Override
    public void endDocument ()
    {
        text.append ( "</html>\n" );
    }

    @Override
    public void startProject ( Project project )
    {
        text.append ( "<B><font color='red'>" );
        text.append ( project.getName() );
        text.append ( "</font></B>" );
        text.append ( "<br/>\n" );
        /*
        text.append ( sp );
        text.append ( "ИД : " );
        text.append ( project.getId() );
        text.append ( "<br/>\n" );

        text.append ( sp );
        text.append ( "Директория : " );
        text.append ( project.getProjectDir() );
        text.append ( "<br/>\n" );

        text.append ( sp );
        text.append ( "Размер : " );
        text.append ( project.getSize() );
        text.append ( "<br/>\n" );
        */
    }

    @Override
    public void endProject ( Project project, int booksCount )
    {
        text.append ( "<B>== Всего книг: " );
        text.append ( booksCount );
        text.append ( "</B><br/><br/>\n" );
    }

    @Override
    public void startSection ( Section section, int level )
    {
        if ( level != 1 )
        {
            text.append ( Convert.createSpace ( sp, level) );
            text.append ( "<B><font color='green'>" );
            text.append ( section.getName() );
            text.append ( "</font></B>" );
            text.append ( "<br/>\n" );
        }
    }

    @Override
    public void endSection ( Section section, int level )
    {
    }

    @Override
    public void startBook ( BookTitle book, int level )
    {
        text.append ( Convert.createSpace ( sp, level) );
        text.append ( "<B><font color='blue'>" );
        text.append ( book.getName() );
        text.append ( "</font></B>" );
        //text.append ( "<br/>\n" );

        /*
        text.append ( sp );
        text.append ( sp );
        text.append ( "ИД : " );
        text.append ( bookContent.getId() );
        text.append ( "<br/>\n" );

        text.append ( sp );
        text.append ( sp );
        text.append ( "Файл : " );
        text.append ( bookContent.getFileName() );
        text.append ( "<br/>\n" );
        */
        //text.append ( sp );
        //text.append ( sp );

        //text.append ( "           -- " );
        //text.append ( bookContent.getSize() );

    }

    @Override
    public void endBook ( BookTitle book, int level )
    {
        text.append ( "<br/>\n" );
    }

}
