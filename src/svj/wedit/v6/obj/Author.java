package svj.wedit.v6.obj;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;

import java.io.InputStream;
import java.io.OutputStream;


/**
 * <BR/>
 * <BR/> Пример:
 *     < author>
        < name>< /name>
        < last_name>< /last_name>
        < address>
            < e_mail>< /e_mail>
            < icq>< /icq>
        < /address>
    < /author>
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 05.07.2011 15:24:33
 */
public class Author   extends XmlAvailable
{
    private String  firstName, secondName, lastName, email;
    //private Address address;

    public void getFromXml ( InputStream in ) throws WEditException
    {
    }

    @Override
    public int getSize ()
    {
        return getSize(firstName) + getSize(secondName) + getSize(lastName) + getSize(email);
    }

    @Override
    public void  toXml ( int level, OutputStream out ) throws WEditException
    {
        int    ic;

        try
        {
            ic  = level + 1;

            outString ( level, "<author>\n", out );

            outTag ( ic, "name", getFirstName(), out );
            outTag ( ic, "last_name", getLastName(), out );
            outTag ( ic, "e_mail", getEmail(), out );

            outString ( level, "</author>\n", out );

        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML представления Автора в поток :\n", e );
        }
    }

    public void setFirstName ( String firstName )
    {
        this.firstName = firstName;
    }

    public void setLastName ( String lastName )
    {
        this.lastName = lastName;
    }

    public void setEmail ( String email )
    {
        //if ( address != null ) address.setEmail ( email );
        this.email = email;
    }

    public String getFirstName ()
    {
        return firstName;
    }

    public String getSecondName ()
    {
        return secondName;
    }

    public String getLastName ()
    {
        return lastName;
    }

    public String getEmail ()
    {
        return email;
    }

    public String getFullName ()
    {
        return getFirstName() + " " + getLastName();
    }

}
