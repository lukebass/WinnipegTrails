package ca.winnipegtrails.winnipegtrails;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

public class VideoActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        VideoView videoView = (VideoView) findViewById(R.id.video);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.intro));

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                // Start an intent for the dispatch activity
                Intent intent = new Intent(VideoActivity.this, ModeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        videoView.start();
    }
}