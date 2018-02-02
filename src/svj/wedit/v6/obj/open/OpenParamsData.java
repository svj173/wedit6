package svj.wedit.v6.obj.open;


import java.util.ArrayList;
import java.util.Collection;


/**
 * Параметры Редактора, измененные пользователем.
 * Сохраняются в файле user+params.xml.
 * Информация об открытых Проекта, Книгах, Главах. Размерах экрана, кординатах указателя.
 * <BR/> Взята из конфига пользователя.
 * <BR/> Применяется при старте Редактора.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 10.08.2011 12:58:20
 */
public class OpenParamsData
{
    // список открытых Сборников
    private final Collection<ProjectInfo> openProjects;
    // размер фрейма
    private int x, y, width, height;

    // Инфа об активных открытых элементах - ID -- Сборник, Книга, Текст. Курсор - в рамках самой книги.
    private String activeProject, activeBook, activeChapter;


    public OpenParamsData ()
    {
        openProjects    = new ArrayList<ProjectInfo>();

        x       = -1;
        y       = -1;
        width   = -1;
        height  = -1;
    }

    public Collection<ProjectInfo> getOpenProjects ()
    {
        return openProjects;
    }

    public void addProject ( ProjectInfo projectInfo )
    {
        openProjects.add ( projectInfo );
    }

    public int getLocationX ()
    {
        return x;
    }

    public void setLocationX ( int x )
    {
        this.x = x;
    }

    public int getLocationY ()
    {
        return y;
    }

    public void setLocationY ( int y )
    {
        this.y = y;
    }

    public int getWidth ()
    {
        return width;
    }

    public void setWidth ( int width )
    {
        this.width = width;
    }

    public int getHeight ()
    {
        return height;
    }

    public void setHeight ( int height )
    {
        this.height = height;
    }

    public String getActiveProject ()
    {
        return activeProject;
    }

    public void setActiveProject ( String activeProject )
    {
        this.activeProject = activeProject;
    }

    public String getActiveBook ()
    {
        return activeBook;
    }

    public void setActiveBook ( String activeBook )
    {
        this.activeBook = activeBook;
    }

    public String getActiveChapter ()
    {
        return activeChapter;
    }

    public void setActiveChapter ( String activeChapter )
    {
        this.activeChapter = activeChapter;
    }

}
