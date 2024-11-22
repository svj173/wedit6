package svj.wedit.v6.manager;


/**
 * Управляет файлами конфигураций - Редактора, Пользователя.
 * <BR/> Конфигурация книги хранится в самой книге.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.07.2011 16:21:57
 */
public class ConfigManager
{
    public void init ()
    {
        // Загрузить конфиг Пользователя

        // Загрузить конфиг Редактора. Имя конфиг файла - фиксированно. Здесь хранится дефолтная структура книги.
        
    }

    /*
    public Map<String, FunctionParameter> getFunctionData ( FunctionId functionId )
    {
        Map<String, FunctionParameter> result;
        SimpleParameter sp;

        Log.l.debug ( "Start. functionId = ", functionId );
        
        result  = null;
        
        // todo TEST
        if ( functionId == FunctionId.REOPEN_PROJECT )
        {
            result  = new HashMap<String, FunctionParameter>();
            sp      = new SimpleParameter();
            sp.setCategory ( ParameterCategory.USER );
            sp.setValue ( "project-01" );
            sp.setDesc ( "/home/svj/pp/project_01.txt" );
            result.put ( "1", sp );
            sp  = new SimpleParameter();
            sp.setCategory ( ParameterCategory.USER );
            sp.setValue ( "project-02" );                // todo отображается первым - выстроить по дате или по номеру
            sp.setDesc ( "/home/svj/temp/ggggg/project_02.txt" );
            result.put ( "2", sp );
        }

        return result;
    }
    */
}
