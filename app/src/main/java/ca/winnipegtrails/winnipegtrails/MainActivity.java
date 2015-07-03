package ca.winnipegtrails.winnipegtrails;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener
{
    private GoogleMap googleMap;
    private Map<String, Marker> mapMarkers = new HashMap<>();
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView score = (TextView) findViewById(R.id.score);
        ParseUser currentUser = ParseUser.getCurrentUser();

        if(currentUser != null && currentUser.getNumber("points") != null) {

            int points = currentUser.getNumber("points").intValue();
            int length = String.valueOf(points).length();

            if(length == 1) {
                score.setText("SCORE: 000" + points);
            }
            else if(length == 2) {
                score.setText("SCORE: 00" + points);
            }
            else if(length == 3) {
                score.setText("SCORE: 0" + points);
            }
            else {
                score.setText(points);
            }
        }
        else {
            score.setText("SCORE: 0000");
        }

        ImageView centerButton = (ImageView) findViewById(R.id.center_button);
        centerButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                centerMap();
            }
        });

        ImageView scanButton = (ImageView) findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                eggQuery(true);
            }
        });

        ImageView bagButton = (ImageView) findViewById(R.id.bag_button);
        bagButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        buildGoogleApiClient();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        googleMap = map;

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(true);

        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
    }

    private void centerMap()
    {
        Location currentLocation = getLocation();
        if(currentLocation != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15));
        }
    }

    private void findEggs(List<Egg> objects)
    {
        Location currentLocation = getLocation();
        if(currentLocation == null) {
            return;
        }

        Boolean found = false;
        // Loop through the results of the search
        for(final Egg item : objects) {

            float[] results = new float[1];
            Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), item.getLocation().getLatitude(), item.getLocation().getLongitude(), results);

            if(results[0] < item.getActionRadiusMeters().floatValue()) {

                found = true;

                final ParseUser currentUser = ParseUser.getCurrentUser();
                if(currentUser != null) {

                    ParseQuery<UserEggLinks> userEggQuery = UserEggLinks.getQuery();
                    userEggQuery.whereEqualTo("user", currentUser);
                    userEggQuery.whereEqualTo("egg", item);

                    userEggQuery.getFirstInBackground(new GetCallback<UserEggLinks>()
                    {
                        public void done(UserEggLinks object, ParseException e)
                        {
                            if(e != null) {

                                if(WinnipegTrailsApplication.APPDEBUG) {
                                    Log.d(WinnipegTrailsApplication.APPTAG, "An error occurred while querying for user eggs", e);
                                }

                                if(e.getCode() == 101) {

                                    UserEggLinks userEggLink = new UserEggLinks();
                                    userEggLink.put("user", currentUser);
                                    userEggLink.put("egg", item);
                                    userEggLink.saveInBackground();

                                    if(currentUser.getNumber("points") == null) {
                                        currentUser.put("points", item.getPoints().intValue());
                                    }
                                    else {
                                        currentUser.put("points", currentUser.getNumber("points").intValue() + item.getPoints().intValue());
                                    }

                                    // Save the user's new point value
                                    currentUser.saveInBackground();
                                }
                            }
                        }
                    });

                    // Launch the egg activity
                    Intent intent = new Intent(this, EggActivity.class);
                    intent.putExtra("id", item.getObjectId());
                    startActivity(intent);
                }
                else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
            }
        }

        if(!found) {
            Toast.makeText(this, R.string.no_eggs_found, Toast.LENGTH_LONG).show();
        }
    }

    private void placeEggs(List<Egg> objects)
    {
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
                    .title(item.getTitle())
                    .snippet(item.getPoints().toString())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon));

            // Add a new marker
            Marker marker = googleMap.addMarker(markerOpts);
            mapMarkers.put(item.getObjectId(), marker);
        }
    }

    /*
     * Set up the query to update the map view
     */
    private void eggQuery(final Boolean find)
    {
        ParseQuery<Egg> eggQuery = Egg.getQuery();
        eggQuery.orderByDescending("title");

        eggQuery.findInBackground(new FindCallback<Egg>()
        {
            @Override
            public void done(List<Egg> objects, ParseException e)
            {
                if(e != null) {

                    if(WinnipegTrailsApplication.APPDEBUG) {
                        Log.d(WinnipegTrailsApplication.APPTAG, "An error occurred while querying for eggs", e);
                    }

                    return;
                }

                if(find) {
                    findEggs(objects);
                }
                else {
                    placeEggs(objects);
                }
            }
        });
    }

    /*
     * Get the current location
     */
    private Location getLocation()
    {
        if(googleApiClient.isConnected()) {
            return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }
        else {
            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
            return null;
        }
    }

    /*
     * Called when the Activity is resumed. Updates the view.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        eggQuery(false);
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if(googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint)
    {
        if(WinnipegTrailsApplication.APPDEBUG) {
            // In debug mode, log the status
            Log.d(WinnipegTrailsApplication.APPTAG, "Connected to location services");
        }

        Location currentLocation = getLocation();
        if(currentLocation != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15));
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result)
    {
        if(WinnipegTrailsApplication.APPDEBUG) {
            // In debug mode, log the status
            Log.d(WinnipegTrailsApplication.APPTAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
        }
    }

    @Override
    public void onConnectionSuspended(int cause)
    {
        if(WinnipegTrailsApplication.APPDEBUG) {
            // In debug mode, log the status
            Log.d(WinnipegTrailsApplication.APPTAG, "Connection suspended: Cause = " + cause);
        }

        googleApiClient.connect();
    }
}