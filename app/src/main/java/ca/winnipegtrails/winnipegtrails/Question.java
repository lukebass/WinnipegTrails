package ca.winnipegtrails.winnipegtrails;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Data model for a post.
 */
@ParseClassName("Question")
public class Question extends ParseObject
{
    public String getQuestion()
    {
        return getString("question");
    }

    public void setQuestion(String value)
    {
        put("question", value);
    }

    public String getAnswer()
    {
        return getString("answer");
    }

    public void setAnswer(String value)
    {
        put("answer", value);
    }

    public ParseObject getEgg()
    {
        return getParseObject("egg");
    }

    public void setEgg(ParseObject value)
    {
        put("egg", value);
    }

    public Number getPoints()
    {
        return getNumber("points");
    }

    public void setPoints(Number value)
    {
        put("points", value);
    }

    public static ParseQuery<Question> getQuery()
    {
        return ParseQuery.getQuery(Question.class);
    }
}