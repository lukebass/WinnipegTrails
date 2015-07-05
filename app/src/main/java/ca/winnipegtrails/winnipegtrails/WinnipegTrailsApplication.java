package ca.winnipegtrails.winnipegtrails;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;

public class WinnipegTrailsApplication extends Application
{
    // Debugging switch
    public static final boolean APPDEBUG = false;

    // Debugging tag for the application
    public static final String APPTAG = "WinnipegTrails";

    public static final int[] modes = {75, 250, 325, 125, 200, 500};

    private static SharedPreferences preferences;

    // Key for saving the transport mode preference
    private static final String KEY_TRANSPORT_MODE = "transportMode";

    private static final int DEFAULT_TRANSPORT_MODE = 0;

    @Override
    public void onCreate()
    {
        super.onCreate();

        ParseObject.registerSubclass(Egg.class);
        ParseObject.registerSubclass(UserEggLinks.class);
        ParseObject.registerSubclass(Question.class);
        ParseObject.registerSubclass(QuestionUserLinks.class);
        Parse.initialize(this, "Bb7WJJOc7201FlMPXR7X2gg2q6kh84BkFUfo4oXk", "zSy0YCTwt13AGFm36QouRR06TT4LbB86c2q2g3uR");
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        preferences = getSharedPreferences("ca.winnipegtrails.winnipegtrails", Context.MODE_PRIVATE);
    }

    public static int getTransportMode()
    {
        return preferences.getInt(KEY_TRANSPORT_MODE, DEFAULT_TRANSPORT_MODE);
    }

    public static void setTransportMode(int value)
    {
        preferences.edit().putInt(KEY_TRANSPORT_MODE, value).apply();
    }
}