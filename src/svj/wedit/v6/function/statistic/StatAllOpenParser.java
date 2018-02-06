package svj.wedit.v6.function.statistic;


import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.project.IProjectParser;


/**
 * Сборщик информации обо всем открытом.
 * <BR/> перевести на SwingWorker - быстрее работать.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 07.04.2012 8:42:04
 */
public class StatAllOpenParser implements IProjectParser<String>
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
    }

    @Override
    public void endProject ( Project project )
    {
    }

    @Override
    public void startBook ( BookContent bookContent )
    {
        text.append ( sp );
        text.append ( "<B><font color='green'>" );
        text.append ( bookContent.getName() );
        text.append ( "</font></B>" );
        text.append ( "<br/>\n" );

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
    }

    @Override
    public void endBook ( BookContent bookContent )
    {
    }

    @Override
    public void startText ( BookNode bookNode, int textCursor )
    {
        text.append ( sp );
        text.append ( sp );
        text.append ( "<B><font color='blue'>" );
        text.append ( bookNode.getName() );
        text.append ( "</font></B>" );
        text.append ( "<br/>\n" );

        text.append ( sp );
        text.append ( sp );
        text.append ( sp );
        text.append ( "ИД : " );
        text.append ( bookNode.getId() );
        text.append ( "<br/>\n" );

        text.append ( sp );
        text.append ( sp );
        text.append ( sp );
        text.append ( "Путь : " );
        text.append ( bookNode.getFullPath() );
        text.append ( "<br/>\n" );

        text.append ( sp );
        text.append ( sp );
        text.append ( sp );
        text.append ( "Курсор : " );
        text.append ( textCursor );
        text.append ( "<br/>\n" );
    }

}
