package ca.winnipegtrails.winnipegtrails;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class EggActivity extends Activity
{
    private ProgressDialog dialog;
    private ParseUser currentUser;
    private HashSet<String> questionUserMap = new HashSet<>();
    private Egg egg;
    private Map<Integer, Question> questionMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egg);

        // Set up a progress dialog
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.progress_egg));
        dialog.show();

        currentUser = ParseUser.getCurrentUser();

        ParseQuery<QuestionUserLinks> questionUserQuery = QuestionUserLinks.getQuery();
        questionUserQuery.whereEqualTo("user", currentUser);

        questionUserQuery.findInBackground(new FindCallback<QuestionUserLinks>()
        {
            public void done(List<QuestionUserLinks> objects, ParseException e)
            {
                if (e != null) {

                    if (WinnipegTrailsApplication.APPDEBUG) {
                        Log.d(WinnipegTrailsApplication.APPTAG, "An error occurred while querying for user questions", e);
                    }

                    return;
                }

                createQuestionUserLinksMap(objects);
            }
        });

        Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                submit();
            }
        });

        TextView cancelButton = (TextView) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                startActivity(new Intent(EggActivity.this, MainActivity.class));
            }
        });
    }

    private void createQuestionUserLinksMap(List<QuestionUserLinks> objects)
    {
        for (QuestionUserLinks item : objects) {
            questionUserMap.add(item.getQuestion().getObjectId());
        }

        getEgg();
    }

    private void getEgg()
    {
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        // Get egg object
        ParseQuery<Egg> eggQuery = Egg.getQuery();
        eggQuery.getInBackground(id, new GetCallback<Egg>()
        {
            public void done(Egg object, ParseException e)
            {
                if (e != null) {

                    if (WinnipegTrailsApplication.APPDEBUG) {
                        Log.d(WinnipegTrailsApplication.APPTAG, "An error occurred while querying for an egg", e);
                    }

                    return;
                }

                egg = object;
                getQuestions();
            }
        });
    }

    private void getQuestions()
    {
        ParseQuery<Question> questionQuery = Question.getQuery();
        questionQuery.whereEqualTo("egg", egg);
        questionQuery.orderByAscending("question");

        questionQuery.findInBackground(new FindCallback<Question>()
        {
            @Override
            public void done(List<Question> objects, ParseException e)
            {
                if (e != null) {

                    if (WinnipegTrailsApplication.APPDEBUG) {
                        Log.d(WinnipegTrailsApplication.APPTAG, "An error occurred while querying for egg questions", e);
                    }

                    return;
                }

                populateEggView(objects);
            }
        });
    }

    private void populateEggView(List<Question> objects)
    {
        // add text view
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(egg.getTitle());

        ParseImageView image = (ParseImageView) findViewById(R.id.image);
        ParseFile imageFile = egg.getLargeImage();

        if (imageFile != null) {
            image.setParseFile(imageFile);
            image.loadInBackground();
        } else {
            image.setImageResource(R.drawable.icon);
        }

        LinearLayout questions = (LinearLayout) findViewById(R.id.questions);

        int score = 1;
        int i = 1;

        for (final Question item : objects) {

            // add text view
            TextView question = new TextView(this);
            question.setText(item.getQuestion());
            question.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            question.setTypeface(null, Typeface.BOLD);

            LinearLayout.LayoutParams questionParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            if (i == 1) {
                question.setLayoutParams(questionParams);
            } else {
                questionParams.topMargin = convertToPixels(10);
                question.setLayoutParams(questionParams);
            }

            // add edit text
            final EditText answer = new EditText(this);
            answer.setId(i);
            answer.setInputType(InputType.TYPE_CLASS_TEXT);
            answer.setMaxLines(1);

            LinearLayout.LayoutParams answerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            answer.setLayoutParams(answerParams);

            if (questionUserMap.contains(item.getObjectId())) {

                answer.setText(item.getAnswer());
                answer.setEnabled(false);
                score++;
            }

            questions.addView(question);
            questions.addView(answer);
            questionMap.put(i, item);

            i++;
        }

        TextView totalGems = (TextView) findViewById(R.id.total);
        totalGems.setText("You have " + score + " out of " + i + " gems at this location");

        dialog.dismiss();
    }

    private void submit()
    {
        LinearLayout questions = (LinearLayout) findViewById(R.id.questions);
        for (int i = 0; i < questions.getChildCount(); i++) {

            View child = questions.getChildAt(i);
            if (child instanceof EditText) {

                EditText userAnswer = (EditText) child;

                if (!userAnswer.isEnabled()) {
                    continue;
                }

                Question question = questionMap.get(userAnswer.getId());
                if (question.getAnswer().toLowerCase().equals(userAnswer.getText().toString().toLowerCase().trim())) {

                    QuestionUserLinks questionUserLink = new QuestionUserLinks();
                    questionUserLink.put("question", question);
                    questionUserLink.put("user", currentUser);
                    questionUserLink.saveInBackground();

                    if (currentUser.getNumber("points") == null) {
                        currentUser.put("points", question.getPoints().intValue());
                    } else {
                        currentUser.put("points", currentUser.getNumber("points").intValue() + question.getPoints().intValue());
                    }

                    // Save the user's new point value
                    currentUser.saveInBackground();
                }
            }
        }

        // Launch the egg activity
        Intent intent = new Intent(this, EggActivity.class);
        intent.putExtra("id", egg.getObjectId());
        startActivity(intent);
    }

    private int convertToPixels(int dp)
    {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}