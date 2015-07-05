package ca.winnipegtrails.winnipegtrails;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
                notifyButton.setTextColor(Color.GREEN);
            }
            else {
                notifyButton.setText("NO");
                notifyButton.setTextColor(Color.RED);
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

    public void modeSelected(View view)
    {
        TextView mode = (TextView) view;
        CharSequence modeText = mode.getText();

        int i = 1;
        String[] types = WinnipegTrailsApplication.types;
        for(String item : types) {

            if(modeText == item) {

                if(i == 6) {
                    i = 0;
                }

                mode.setText(WinnipegTrailsApplication.types[i]);
                WinnipegTrailsApplication.setTransportMode(i);

                ParseUser currentUser = ParseUser.getCurrentUser();
                if(currentUser != null) {

                    currentUser.put("transport_mode", i);
                    currentUser.saveInBackground();
                }

                break;
            }

            i++;
        }
    }

    public void notifySelected(View view)
    {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser != null) {

            TextView notify = (TextView) view;
            CharSequence notifyText = notify.getText();

            if(notifyText == "NO") {

                notify.setText("YES");
                currentUser.put("notifications", true);
                currentUser.saveInBackground();
            }
            else {

                notify.setText("NO");
                currentUser.put("notifications", false);
                currentUser.saveInBackground();
            }
        }
    }
}