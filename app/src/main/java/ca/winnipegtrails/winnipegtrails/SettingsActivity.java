package ca.winnipegtrails.winnipegtrails;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.parse.ParseUser;

public class SettingsActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView modeButton = (TextView) findViewById(R.id.mode_button);
        modeButton.setText(WinnipegTrailsApplication.types[WinnipegTrailsApplication.getTransportMode()]);

        TextView notifyButton = (TextView) findViewById(R.id.notify_button);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser != null) {

            Boolean notify = currentUser.getBoolean("notifications");
            if(notify) {
                notifyButton.setText("YES");
            }
            else {
                notifyButton.setText("NO");
            }
        }
        else {
            notifyButton.setText("NO");
        }

        TextView gemButton = (TextView) findViewById(R.id.gem_button);
        gemButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                startActivity(new Intent(SettingsActivity.this, EggListActivity.class));
            }
        });

        TextView leaderboardButton = (TextView) findViewById(R.id.leaderboard_button);
        leaderboardButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                startActivity(new Intent(SettingsActivity.this, UserListActivity.class));
            }
        });

        TextView cancelButton = (TextView) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            }
        });
    }
}