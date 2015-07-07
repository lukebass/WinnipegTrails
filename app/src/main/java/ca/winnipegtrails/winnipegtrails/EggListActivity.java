package ca.winnipegtrails.winnipegtrails;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.HashSet;
import java.util.List;

public class EggListActivity extends Activity
{
    private HashSet<String> eggMap = new HashSet<>();
    private ParseQueryAdapter<Egg> eggQueryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egg_list);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser != null) {

            ParseQuery<UserEggLinks> userEggQuery = UserEggLinks.getQuery();
            userEggQuery.whereEqualTo("user", currentUser);

            userEggQuery.findInBackground(new FindCallback<UserEggLinks>()
            {
                @Override
                public void done(List<UserEggLinks> objects, ParseException e)
                {
                    if(e != null) {

                        if(WinnipegTrailsApplication.APPDEBUG) {
                            Log.d(WinnipegTrailsApplication.APPTAG, "An error occurred while querying for user eggs", e);
                        }

                        return;
                    }

                    createUserEggLinksMap(objects);
                }
            });
        }
    }

    private void createUserEggLinksMap(List<UserEggLinks> objects)
    {
        for(UserEggLinks item : objects) {
            eggMap.add(item.getEgg().getObjectId());
        }

        setupQueryAdapter();
    }

    private void setupQueryAdapter()
    {
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

                ParseImageView image = (ParseImageView) view.findViewById(R.id.list_item_image);

                TextView title = (TextView) view.findViewById(R.id.list_item_title);
                title.setText(item.getTitle());

                if(eggMap.contains(item.getObjectId())) {

                    ParseFile imageFile = item.getLargeImage();

                    if(imageFile != null) {
                        image.setParseFile(imageFile);
                        image.loadInBackground();
                    }
                    else {
                        image.setImageResource(R.drawable.icon);
                    }

                    title.setTextColor(Color.BLACK);
                }
                else {
                    image.setImageResource(R.drawable.hidden);
                    title.setTextColor(Color.GRAY);
                }

                return view;
            }
        };

        eggQueryAdapter.setPaginationEnabled(false);

        // Attach the query adapter to the view
        ListView postsListView = (ListView) findViewById(R.id.list);
        postsListView.setAdapter(eggQueryAdapter);
    }
}