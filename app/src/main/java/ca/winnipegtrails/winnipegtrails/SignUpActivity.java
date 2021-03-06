package ca.winnipegtrails.winnipegtrails;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends Activity
{
    private EditText usernameEdit;
    private EditText emailEdit;
    private EditText passwordEdit;
    private EditText passwordAgainEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        if (getActionBar() != null) {

            getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getActionBar().setCustomView(R.layout.actionbar);
            getActionBar().setDisplayHomeAsUpEnabled(true);

            TextView title = (TextView) findViewById(R.id.title);
            title.setText(R.string.app_name);
        }

        // Set up the sign up form
        usernameEdit = (EditText) findViewById(R.id.username);
        emailEdit = (EditText) findViewById(R.id.email);
        passwordEdit = (EditText) findViewById(R.id.password);
        passwordAgainEdit = (EditText) findViewById(R.id.password_again);

        // Set up the submit button click handler
        Button signUpButton = (Button) findViewById(R.id.signup_button);
        signUpButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                signup();
            }
        });
    }

    private void signup()
    {
        String username = usernameEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();
        String passwordAgain = passwordAgainEdit.getText().toString().trim();

        // Validate the sign up data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));

        if (username.length() == 0) {

            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_username));
        }

        if (email.length() == 0) {

            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            } else {
                validationError = true;
            }

            validationErrorMessage.append(getString(R.string.error_blank_email));
        }

        if (password.length() == 0) {

            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            } else {
                validationError = true;
            }

            validationErrorMessage.append(getString(R.string.error_blank_password));
        }

        if (!password.equals(passwordAgain)) {

            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            } else {
                validationError = true;
            }

            validationErrorMessage.append(getString(R.string.error_mismatched_passwords));
        }

        validationErrorMessage.append(getString(R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {

            Toast.makeText(SignUpActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
            return;
        }

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.progress_signup));
        dialog.show();

        // Set up a new Parse user
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);

        // Call the Parse signup method
        user.signUpInBackground(new SignUpCallback()
        {
            @Override
            public void done(ParseException e)
            {
                dialog.dismiss();
                if (e != null) {
                    // Show the error message
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    // Start an intent for the dispatch activity
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}