package ca.winnipegtrails.winnipegtrails;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;

public class VideoActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.intro);
        mediaPlayer.start();
    }
}