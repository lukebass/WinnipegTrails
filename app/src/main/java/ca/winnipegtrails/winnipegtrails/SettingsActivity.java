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

        final TextView modeButton = (TextView) findViewById(R.id.mode_button);
        modeButton.setText(WinnipegTrailsApplication.types[WinnipegTrailsApplication.getTransportMode()]);

        modeButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                modeSelected(modeButton);
            }
        });

        final TextView notifyButton = (TextView) findViewById(R.id.notify_button);
        TextView loginButton = (TextView) findViewById(R.id.login_button);

        final ParseUser currentUser = ParseUser.getCurrentUser();
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

            notifyButton.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View view)
                {
                    notifySelected(notifyButton, currentUser);
                }
            });

            loginButton.setText("LOGOUT");
            loginButton.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View view)
                {
                    currentUser.logOut();

                    Intent intent = new Intent(SettingsActivity.this, ModeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        }
        else {

            notifyButton.setText("NO");
            notifyButton.setTextColor(Color.RED);

            loginButton.setText("LOGIN");
            loginButton.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View view)
                {
                    startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                }
            });
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

    private void modeSelected(TextView view)
    {
        CharSequence modeText = view.getText();

        int i = 1;
        String[] types = WinnipegTrailsApplication.types;
        for(String item : types) {

            if(modeText == item) {

                if(i == 6) {
                    i = 0;
                }

                view.setText(WinnipegTrailsApplication.types[i]);
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

    private void notifySelected(TextView view, ParseUser currentUser)
    {
        CharSequence notifyText = view.getText();

        if(notifyText == "NO") {

            view.setText("YES");
            currentUser.put("notifications", true);
            currentUser.saveInBackground();
        }
        else {

            view.setText("NO");
            currentUser.put("notifications", false);
            currentUser.saveInBackground();
        }
    }
}