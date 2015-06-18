package ca.winnipegtrails.winnipegtrails;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Data model for a post.
 */
@ParseClassName("QuestionUserLinks")
public class QuestionUserLinks extends ParseObject
{
    public ParseUser getUser()
    {
        return getParseUser("user");
    }

    public void setUser(ParseUser value)
    {
        put("user", value);
    }

    public ParseObject getQuestion()
    {
        return getParseObject("question");
    }

    public void setQuestion(ParseObject value)
    {
        put("question", value);
    }

    public static ParseQuery<QuestionUserLinks> getQuery()
    {
        return ParseQuery.getQuery(QuestionUserLinks.class);
    }
}