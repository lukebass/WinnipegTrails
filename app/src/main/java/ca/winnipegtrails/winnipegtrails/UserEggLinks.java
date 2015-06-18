package ca.winnipegtrails.winnipegtrails;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Data model for a post.
 */
@ParseClassName("UserEggLinks")
public class UserEggLinks extends ParseObject
{
    public ParseUser getUser()
    {
        return getParseUser("user");
    }

    public void setUser(ParseUser value)
    {
        put("user", value);
    }

    public ParseObject getEgg()
    {
        return getParseObject("egg");
    }

    public void setEgg(ParseObject value)
    {
        put("egg", value);
    }

    public static ParseQuery<UserEggLinks> getQuery()
    {
        return ParseQuery.getQuery(UserEggLinks.class);
    }
}