package ca.winnipegtrails.winnipegtrails;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Data model for a post.
 */
@ParseClassName("Egg")
public class Egg extends ParseObject
{
    public String getTitle()
    {
        return getString("title");
    }

    public void setTitle(String value)
    {
        put("title", value);
    }

    public ParseGeoPoint getLocation()
    {
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint value)
    {
        put("location", value);
    }

    public Boolean getHidden()
    {
        return getBoolean("hidden");
    }

    public void setHidden(Boolean value)
    {
        put("hidden", value);
    }

    public Number getPoints()
    {
        return getNumber("points");
    }

    public void setPoints(Number value)
    {
        put("points", value);
    }

    public Number getActionRadiusMeters()
    {
        return getNumber("action_radius_meters");
    }

    public void setActionRadiusMeters(Number value)
    {
        put("action_radius_meters", value);
    }

    public static ParseQuery<Egg> getQuery()
    {
        return ParseQuery.getQuery(Egg.class);
    }
}