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

import java.util.HashMap;
import java.util.Map;

public class UserListActivity extends Activity
{
    private Map<String, Integer> userRankMap = new HashMap<>();
    private int rank = 1;

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
        ParseQueryAdapter<ParseUser> userQueryAdapter = new ParseQueryAdapter<ParseUser>(this, factory)
        {
            @Override
            public View getItemView(ParseUser item, View view, ViewGroup parent)
            {
                if (view == null) {
                    view = View.inflate(getContext(), R.layout.list_item_user, null);
                }

                TextView title = (TextView) view.findViewById(R.id.list_item_title);
                title.setText(rank + ". " + item.getUsername());

                TextView points = (TextView) view.findViewById(R.id.list_item_points);
                if (item.getNumber("points") == null) {
                    points.setText("0");
                } else {
                    points.setText(item.getNumber("points").toString());
                }

                rank++;
                return view;
            }
        };

        userQueryAdapter.setPaginationEnabled(false);

        // Attach the query adapter to the view
        ListView userListView = (ListView) findViewById(R.id.list);
        userListView.setAdapter(userQueryAdapter);
    }
}