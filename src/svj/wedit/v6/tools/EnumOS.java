package svj.wedit.v6.tools;


/**
 * Список операционных систем.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 27.01.2015 11:29
 */
public enum EnumOS
{
    linux, macos, solaris, unknown, windows;

    public boolean isLinux ()
    {
        return this == linux || this == solaris;
    }

    public boolean isMac ()
    {
        return this == macos;
    }

    public boolean isWindows ()
    {
        return this == windows;
    }
}
