package ca.winnipegtrails.winnipegtrails;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class UserListActivity extends Activity
{
    private ParseQueryAdapter<ParseUser> userQueryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        TextView score = (TextView) findViewById(R.id.score);
        ParseUser currentUser = ParseUser.getCurrentUser();

        if(currentUser != null && currentUser.getNumber("points") != null) {

            String points = currentUser.getNumber("points").toString();
            score.setText(points);
        }
        else {
            score.setText("0");
        }

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
                if(view == null) {
                    view = View.inflate(getContext(), R.layout.list_item_user, null);
                }

                TextView title = (TextView) view.findViewById(R.id.list_item_title);
                title.setText(item.getUsername());

                TextView points = (TextView) view.findViewById(R.id.list_item_points);
                if(item.getNumber("points") == null) {
                    points.setText("0");
                }
                else {
                    points.setText(item.getNumber("points").toString());
                }

                return view;
            }
        };

        userQueryAdapter.setAutoload(false);
        userQueryAdapter.setPaginationEnabled(false);

        // Attach the query adapter to the view
        ListView postsListView = (ListView) findViewById(R.id.list);
        postsListView.setAdapter(userQueryAdapter);
    }

    /*
     * Set up a query to update the list view
     */
    private void doListQuery()
    {
        userQueryAdapter.loadObjects();
    }

    /*
     * Called when the Activity is resumed. Updates the view.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        doListQuery();
    }
}