package ca.winnipegtrails.winnipegtrails;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserListActivity extends Activity
{
    private Map<String, Integer> userRankMap = new HashMap<>();
    private ParseQueryAdapter<ParseUser> userQueryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        TextView score = (TextView) findViewById(R.id.score);
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null && currentUser.getNumber("points") != null) {

            String points = currentUser.getNumber("points").toString();
            score.setText(points);
        } else {
            score.setText("0");
        }

        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.orderByDescending("points");

        userQuery.findInBackground(new FindCallback<ParseUser>()
        {
            @Override
            public void done(List<ParseUser> objects, ParseException e)
            {
                if (e != null) {

                    if (WinnipegTrailsApplication.APPDEBUG) {
                        Log.d(WinnipegTrailsApplication.APPTAG, "An error occurred while querying for user eggs", e);
                    }

                    return;
                }

                createUserRankMap(objects);
            }
        });
    }

    private void createUserRankMap(List<ParseUser> objects)
    {
        int i = 1;
        for (ParseUser item : objects) {

            userRankMap.put(item.getObjectId(), i);
            i++;
        }

        setupQueryAdapter();
    }

    private void setupQueryAdapter()
    {
        // Set up a customized query
        ParseQueryAdapter.QueryFactory<ParseUser> factory = new ParseQueryAdapter.QueryFactory<ParseUser>()
        {
            public ParseQuery<ParseUser> create()
            {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.orderByDescending("points");
                return query;
            }
        };

        // Set up the query adapter
        userQueryAdapter = new ParseQueryAdapter<ParseUser>(this, factory)
        {
            @Override
            public View getItemView(ParseUser item, View view, ViewGroup parent)
            {
                if (view == null) {
                    view = View.inflate(getContext(), R.layout.list_item_user, null);
                }

                TextView title = (TextView) view.findViewById(R.id.list_item_title);
                title.setText(userRankMap.get(item.getObjectId()) + ".  " + item.getUsername());

                TextView points = (TextView) view.findViewById(R.id.list_item_points);
                if (item.getNumber("points") == null) {
                    points.setText("0");
                } else {
                    points.setText(item.getNumber("points").toString());
                }

                return view;
            }
        };

        userQueryAdapter.setPaginationEnabled(false);

        // Attach the query adapter to the view
        ListView postsListView = (ListView) findViewById(R.id.list);
        postsListView.setAdapter(userQueryAdapter);
    }
}