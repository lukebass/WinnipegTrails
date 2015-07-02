package ca.winnipegtrails.winnipegtrails;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class EggListActivity extends Activity
{
    private ParseQueryAdapter<Egg> eggQueryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egg_list);

        // Set up a customized query
        ParseQueryAdapter.QueryFactory<Egg> factory = new ParseQueryAdapter.QueryFactory<Egg>()
        {
            public ParseQuery<Egg> create()
            {
                ParseQuery<Egg> query = Egg.getQuery();
                query.orderByAscending("title");
                return query;
            }
        };

        // Set up the query adapter
        eggQueryAdapter = new ParseQueryAdapter<Egg>(this, factory)
        {
            @Override
            public View getItemView(Egg item, View view, ViewGroup parent)
            {
                if(view == null) {
                    view = View.inflate(getContext(), R.layout.list_item_egg, null);
                }

                ParseImageView image = (ParseImageView) findViewById(R.id.list_item_image);
                ParseFile imageFile = item.getLargeImage();

                if(imageFile != null) {
                    image.setParseFile(imageFile);
                    image.loadInBackground();
                }
                else {
                    image.setImageResource(R.drawable.icon);
                }

                TextView title = (TextView) view.findViewById(R.id.list_item_title);
                title.setText(item.getTitle());

                return view;
            }
        };

        eggQueryAdapter.setAutoload(false);
        eggQueryAdapter.setPaginationEnabled(false);

        // Attach the query adapter to the view
        ListView postsListView = (ListView) findViewById(R.id.list);
        postsListView.setAdapter(eggQueryAdapter);
    }

    /*
     * Set up a query to update the list view
     */
    private void doListQuery()
    {
        eggQueryAdapter.loadObjects();
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