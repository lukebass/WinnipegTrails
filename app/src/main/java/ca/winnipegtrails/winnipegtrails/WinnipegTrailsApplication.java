package ca.winnipegtrails.winnipegtrails;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

public class WinnipegTrailsApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        //ParseObject.registerSubclass(Meal.class);
        Parse.initialize(this, "Bb7WJJOc7201FlMPXR7X2gg2q6kh84BkFUfo4oXk", "zSy0YCTwt13AGFm36QouRR06TT4LbB86c2q2g3uR");
        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);
    }

}