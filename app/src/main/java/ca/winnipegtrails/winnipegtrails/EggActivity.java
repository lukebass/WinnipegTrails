package ca.winnipegtrails.winnipegtrails;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EggActivity extends Activity
{
    private final Map<Integer, Question> questionMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egg);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        ParseQuery<Egg> innerQuery = Egg.getQuery();
        innerQuery.whereEqualTo("objectId", id);
        ParseQuery<Question> questionQuery = Question.getQuery();
        questionQuery.whereMatchesQuery("egg", innerQuery);
        questionQuery.orderByAscending("question");

        questionQuery.findInBackground(new FindCallback<Question>()
        {
            @Override
            public void done(List<Question> objects, ParseException e)
            {
                if(e != null) {

                    if(WinnipegTrailsApplication.APPDEBUG) {
                        Log.d(WinnipegTrailsApplication.APPTAG, "An error occurred while querying for egg questions", e);
                    }

                    return;
                }

                populateQuestions(objects);
            }
        });

        // Set up the submit button click handler
        Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                submit();
            }
        });

        // Set up the sign up button click handler
        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                startActivity(new Intent(EggActivity.this, MainActivity.class));
            }
        });
    }

    private void populateQuestions(List<Question> objects)
    {
        LinearLayout questions = (LinearLayout) findViewById(R.id.questions);

        int i = 1;
        // Loop through the results of the search
        for(final Question item : objects) {

            // add text view
            TextView question = new TextView(this);
            question.setText(item.getQuestion());
            questions.addView(question);

            // add edit text
            final EditText answer = new EditText(this);
            answer.setInputType(InputType.TYPE_CLASS_TEXT);
            answer.setMaxLines(1);
            answer.setId(i);
            questionMap.put(i, item);

            ParseQuery<QuestionUserLinks> questionUserQuery = QuestionUserLinks.getQuery();
            questionUserQuery.whereEqualTo("question", item);
            questionUserQuery.whereEqualTo("user", ParseUser.getCurrentUser());

            questionUserQuery.getFirstInBackground(new GetCallback<QuestionUserLinks>()
            {
                public void done(QuestionUserLinks object, ParseException e)
                {
                    if(e != null) {

                        if(WinnipegTrailsApplication.APPDEBUG) {
                            Log.d(WinnipegTrailsApplication.APPTAG, "An error occurred while querying for user questions", e);
                        }

                        return;
                    }

                    if(object != null) {
                        answer.setText(item.getAnswer());
                        answer.setEnabled(false);
                    }
                }
            });

            questions.addView(answer);
        }
    }

    private void submit()
    {
        LinearLayout questions = (LinearLayout) findViewById(R.id.questions);
        for(int i = 0; i < questions.getChildCount(); i++) {

            View child = questions.getChildAt(i);
            if(child instanceof EditText) {

                EditText userAnswer = (EditText) child;

                if(!userAnswer.isEnabled()) {
                    continue;
                }

                Question question = questionMap.get(userAnswer.getId());
                if(question.getAnswer().toLowerCase().equals(userAnswer.getText().toString().toLowerCase().trim())) {

                    QuestionUserLinks questionUserLink = new QuestionUserLinks();
                    questionUserLink.put("question", question);
                    questionUserLink.put("user", ParseUser.getCurrentUser());
                    questionUserLink.saveInBackground();
                }
            }
        }

        //Continue
    }
}