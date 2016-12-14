package model;

/**
 * Created by aljon on 19/10/2016.
 */

public class User {

    private String firstName;
    private String lastName;
    private String eMail;

    public User(String fname, String lname, String email)
    {
        firstName = fname;
        lastName = lname;
        eMail = email;
    }


    public User(String fname, String lname)
    {
        firstName = fname;
        lastName = lname;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getEmail()
    {
        return eMail;
    }
}
