package svj.wedit.v6.manager;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.obj.Project;


/**
 * Управляет проектами.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 22.09.2011 15:40:40
 */
public class ProjectManager
{
    public void addProject ( Project project ) throws WEditException
    {
        Par.GM.getFrame().addProject ( project );
    }

    public boolean containProject ( String projectId )
    {
        return Par.GM.getFrame().containProject ( projectId );
    }

}
