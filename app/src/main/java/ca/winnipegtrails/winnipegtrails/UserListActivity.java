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
        setContentView(R.layout.activity_list);

        // Set up a customized query
        ParseQueryAdapter.QueryFactory<ParseUser> factory = new ParseQueryAdapter.QueryFactory<ParseUser>()
        {
            public ParseQuery<ParseUser> create()
            {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.orderByDescending("username");
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
                    view = View.inflate(getContext(), R.layout.list_item, null);
                }

                TextView titleView = (TextView) view.findViewById(R.id.list_item_title);
                TextView pointsView = (TextView) view.findViewById(R.id.list_item_points);
                titleView.setText(item.getUsername());

                if(item.getNumber("points") == null) {
                    pointsView.setText("0");
                }
                else {
                    pointsView.setText(item.getNumber("points").toString());
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