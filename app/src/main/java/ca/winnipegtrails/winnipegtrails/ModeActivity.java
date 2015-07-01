package ca.winnipegtrails.winnipegtrails;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ModeActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);

        // Set up the submit button click handler
        TextView startButton = (TextView) findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                getSelected();
            }
        });
    }

    public void modeSelected(View view)
    {
        TextView selected = (TextView) view;
        CharSequence selectedText = selected.getText();

        LinearLayout modes = (LinearLayout) findViewById(R.id.modes);
        for(int i = 0; i < modes.getChildCount(); i++) {

            View child = modes.getChildAt(i);
            if(child instanceof TextView) {

                TextView mode = (TextView) child;

                if(selectedText == mode.getText()) {
                    mode.setTextColor(Color.GREEN);
                }
                else {
                    mode.setTextColor(Color.BLACK);
                }
            }
        }
    }

    private void getSelected()
    {
        String selectedText = null;

        LinearLayout modes = (LinearLayout) findViewById(R.id.modes);
        for(int i = 0; i < modes.getChildCount(); i++) {

            View child = modes.getChildAt(i);
            if(child instanceof TextView) {

                TextView mode = (TextView) child;

                if(Color.GREEN == mode.getCurrentTextColor()) {
                    selectedText = mode.getText().toString();
                }
            }
        }

        if(selectedText != null) {

            // Launch the main activity
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("mode", selectedText);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, R.string.no_mode_selected, Toast.LENGTH_LONG).show();
        }
    }
}