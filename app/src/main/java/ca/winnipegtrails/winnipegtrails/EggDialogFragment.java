package ca.winnipegtrails.winnipegtrails;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class EggDialogFragment extends DialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        String id = getArguments().getString("id");
        String title = getArguments().getString("title");

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
                if (e != null) {

                    if (WinnipegTrailsApplication.APPDEBUG) {
                        Log.d(WinnipegTrailsApplication.APPTAG, "An error occurred while querying for questions", e);
                    }

                    return;
                }

                //Create the questions
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle(title)
                .setView(inflater.inflate(R.layout.dialog_egg, null))
                // Add action buttons
                .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // Evaluate the form
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        EggDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}
