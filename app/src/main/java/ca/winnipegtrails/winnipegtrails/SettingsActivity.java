package ca.winnipegtrails.winnipegtrails;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SettingsActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView gemButton = (TextView) findViewById(R.id.gem_button);
        gemButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
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
    }
}