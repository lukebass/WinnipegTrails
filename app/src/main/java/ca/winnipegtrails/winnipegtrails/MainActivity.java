package ca.winnipegtrails.winnipegtrails;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity implements OnMapReadyCallback
{
    private MapFragment mapFragment;
    private final Map<String, Marker> mapMarkers = new HashMap<String, Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        map.setMyLocationEnabled(true);
        doMapQuery();
    }

    /*
    * Set up the query to update the map view
    */
    private void doMapQuery()
    {
        ParseQuery<Egg> mapQuery = Egg.getQuery();
        mapQuery.orderByDescending("createdAt");

        mapQuery.findInBackground(new FindCallback<Egg>()
        {
            @Override
            public void done(List<Egg> objects, ParseException e)
            {
                if(e != null) {

                    if(WinnipegTrailsApplication.APPDEBUG) {
                        Log.d(WinnipegTrailsApplication.APPTAG, "An error occurred while querying for map eggs.", e);
                    }

                    return;
                }

                // Loop through the results of the search
                for(Egg item : objects) {

                    // Check for an existing marker for this item
                    Marker oldMarker = mapMarkers.get(item.getObjectId());
                    if(oldMarker != null) {
                        // In range marker already exists, skip adding it
                        continue;
                    }

                    // Set up the map marker's location
                    // Display a green marker with the item information
                    MarkerOptions markerOpts = new MarkerOptions()
                            .position(new LatLng(item.getLocation().getLatitude(), item.getLocation().getLongitude()))
                            .title(item.getTitle()).snippet(item.getPoints().toString())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                    // Add a new marker
                    Marker marker = mapFragment.getMap().addMarker(markerOpts);
                    mapMarkers.put(item.getObjectId(), marker);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_help:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}