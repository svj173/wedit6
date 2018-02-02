package svj.wedit.v6.obj;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.IId;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.BookTitle;
import svj.wedit.v6.tools.Convert;

import java.io.File;
import java.io.OutputStream;
import java.util.Date;


/**
 * Проект - Книжный Сборник 
 * <BR/>
 * <BR/> ID - это его имя файловой директории. Абс путь, но в XML-файле, для гибкости
 * (возможность копирования Сборника на другой комп), хранятся относительные пути.
 * <BR/>
 *
 <project name="Название проекта">
    <author>
        <name></name>
        <last_name></last_name>
        <address>
            <e_mail></e_mail>
            <icq></icq>
        </address>
    </author>

    <create_date></create_date>

    <section name="Имя раздела (здесь - название проекта)">
        <section name="">
            <section name="">
                <book name="Имя книги">book file</book>
            </section>
            <book name="Имя книги">book file</book>
        </section>

        <book name="Имя книги">book file</book>
    </section>

</project>

 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 05.07.2011 15:15:31
 */
public class Project   extends XmlAvailable   implements IId, Editable
{
    /** Русское название сборника. */
    private String  name;
    private Author  author;
    private Section rootSection = null;
    private Date    createDate;

    /* Директория проекта. Абс путь. Заносится при загрузке Проекта. Зачем? Как тогда переносить проекты на другой комп?
    * -- В xml файле хранятся относительные пути, а абс путь необходим чтобы добавлять, удалять разделы Сборника.
    * Английское название папки Сборника. Это также ИД Сборника. */
    private final File    projectDir;

    /* Аннотация */
    private String   annotation;
    private boolean  editMode;


    public Project ( File projectDir )
    {
        this.projectDir = projectDir;
    }

    public String toString()
    {
        StringBuilder result;

        result  = new StringBuilder();

        result.append ( "[ Project : name = '" );
        result.append ( getName() );
        result.append ( "'; folderName = '" );
        result.append ( getFolderName() );
        result.append ( "'; file = '" );
        result.append ( getProjectDir() );
        result.append ( "'; id = '" );
        result.append ( getId() );
        result.append ( "' ]" );

        return result.toString();
    } 

    @Override
    public int getSize ()
    {
        return getSize ( getName() ) + getSize(getAuthor()) + getSize(getRootSection()) + getSize(getAnnotation());
    }

    /* Преобразовать в XML для сохранения. И скинуть в поток.  */
    @Override
    public void toXml ( int level, OutputStream out ) throws WEditException
    {
        String str;
        int    ic;

        try
        {
            ic  = level + 1;

            outTitle ( level, "project", getName(), out );

            getAuthor().toXml ( ic, out );

            str = Convert.getRussianDateTime ( getCreateDate() );
            outTag ( ic, "create_date", str, out );

            if ( getRootSection() != null )
                getRootSection().toXml ( ic, out );

            endTag ( level, "project", out );

        } catch ( WEditException we )        {
            throw we;
        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML представления Проекта '", getName(), "' в поток :\n", e );
        }
    }

    public void setName ( String projectName )
    {
        // убираем двойные кавычки, т.к. этот текст заносится в xml-файле в атрибут name="текст" - т.е. поломается структура.
        if ( (projectName != null) &&  projectName.contains ( "\"" ) )  projectName = projectName.replace ( '"', '\'' );
        name    = projectName;
    }

    public Author getAuthor ()
    {
        return author;
    }

    public void setAuthor ( Author author )
    {
        this.author = author;
    }

    public String getName ()
    {
        return name;
    }

    public Section getRootSection ()
    {
        return rootSection;
    }

    public void setRootSection ( Section rootSection )
    {
        this.rootSection = rootSection;
    }

    public Date getCreateDate ()
    {
        return createDate;
    }

    public void setCreateDate ( Date createDate )
    {
        this.createDate = createDate;
    }

    public File getProjectDir ()
    {
        return projectDir;
    }

    public String getAnnotation ()
    {
        return annotation;
    }

    public void setAnnotation ( String annotation )
    {
        this.annotation = annotation;
    }

    @Override
    public String getId ()
    {
        if ( projectDir == null )
            return "";
        else
            return projectDir.getAbsolutePath();
    }

    @Override
    public void setEdit ( boolean editMode )
    {
        this.editMode = editMode;
    }

    @Override
    public boolean isEdit ()
    {
        return editMode;
    }

    /**
     * Рекурсивно пробегаем по секциям и ищем в них bookTitle
     * @param fileName
     * @return
     */
    public BookTitle getBookTitle ( String fileName )
    {
        BookTitle result;

        result = null;
        if ( getRootSection() != null )   result = getRootSection().getBookTitle ( fileName );

        return result;
    }

    public String getFolderName ()
    {
        if ( projectDir != null )
            return projectDir.getName();
        else
            return null;
    }

}
