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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.MapView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener
{
    private MapView mapView;
    private Map<Marker, Egg> mapMarkers = new HashMap<>();
    private GoogleApiClient googleApiClient;
    private Location currentLocation;
    private LocationRequest locationRequest;
    public static final int UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    public static final int FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 2500;
    private Marker currentLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView score = (TextView) findViewById(R.id.score);
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null && currentUser.getNumber("points") != null) {

            String points = currentUser.getNumber("points").toString();
            int length = points.length();

            if (length == 1) {
                score.setText("SCORE: 0000000" + points);
            } else if (length == 2) {
                score.setText("SCORE: 000000" + points);
            } else if (length == 3) {
                score.setText("SCORE: 00000" + points);
            } else if (length == 4) {
                score.setText("SCORE: 0000" + points);
            } else if (length == 5) {
                score.setText("SCORE: 000" + points);
            } else if (length == 6) {
                score.setText("SCORE: 00" + points);
            } else if (length == 7) {
                score.setText("SCORE: 0" + points);
            } else {
                score.setText(points);
            }
        } else {
            score.setText("SCORE: 00000000");
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

        mapView = (MapView) findViewById(R.id.map);
    }

    private void centerMap()
    {
        if (currentLocation != null) {
            mapView.setCenter(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
            mapView.setZoom(15);
        }
    }

    private void findEggs(List<Egg> objects)
    {
        if (currentLocation == null) {
            return;
        }

        float distance = 0;
        Egg closest = null;

        // Loop through the results of the search
        for (final Egg item : objects) {

            float[] results = new float[1];
            Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), item.getLocation().getLatitude(), item.getLocation().getLongitude(), results);

            if (results[0] < item.getActionRadiusMeters().floatValue()) {

                if (closest == null) {

                    distance = results[0];
                    closest = item;
                } else if (results[0] < distance) {

                    distance = results[0];
                    closest = item;
                }

                final ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {

                    ParseQuery<UserEggLinks> userEggQuery = UserEggLinks.getQuery();
                    userEggQuery.whereEqualTo("user", currentUser);
                    userEggQuery.whereEqualTo("egg", item);

                    userEggQuery.getFirstInBackground(new GetCallback<UserEggLinks>()
                    {
                        public void done(UserEggLinks object, ParseException e)
                        {
                            if (e != null) {

                                if (WinnipegTrailsApplication.APPDEBUG) {
                                    Log.d(WinnipegTrailsApplication.APPTAG, "An error occurred while querying for user eggs", e);
                                }

                                if (e.getCode() == 101) {

                                    UserEggLinks userEggLink = new UserEggLinks();
                                    userEggLink.put("user", currentUser);
                                    userEggLink.put("egg", item);
                                    userEggLink.saveInBackground();

                                    if (currentUser.getNumber("points") == null) {
                                        currentUser.put("points", item.getPoints().intValue());
                                    } else {
                                        currentUser.put("points", currentUser.getNumber("points").intValue() + item.getPoints().intValue());
                                    }

                                    // Save the user's new point value
                                    currentUser.saveInBackground();
                                }
                            }
                        }
                    });
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
            }
        }

        if (closest != null) {

            // Launch the egg activity
            Intent intent = new Intent(this, EggActivity.class);
            intent.putExtra("id", closest.getObjectId());
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.no_eggs_found, Toast.LENGTH_LONG).show();
        }
    }

    private void placeEggs(List<Egg> objects)
    {
        int avg = WinnipegTrailsApplication.modes[WinnipegTrailsApplication.getTransportMode()];

        // Loop through the results of the search
        for (Egg item : objects) {

            // Check for an existing marker for this item
            if (mapMarkers.containsValue(item)) {
                // In range marker already exists, skip adding it
                continue;
            }

            // Set up the map marker's location
            // Display a green marker with the item information
            Marker marker = new Marker(mapView, item.getTitle(), "", new LatLng(item.getLocation().getLatitude(), item.getLocation().getLongitude()));
            marker.setMarker(getResources().getDrawable(R.drawable.icon));
            marker.setImage(getResources().getDrawable(R.drawable.icon));

            if (currentLocation != null) {

                float[] results = new float[1];
                Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), item.getLocation().getLatitude(), item.getLocation().getLongitude(), results);

                int time = Math.round(results[0] / avg);
                marker.setDescription("Travel: " + String.valueOf(time) + " mins");
            }

            // Add a new marker
            mapView.addMarker(marker);
            mapMarkers.put(marker, item);
        }
    }

    /*
     * Set up the query to update the map view
     */
    private void eggQuery(final Boolean find)
    {
        ParseQuery<Egg> eggQuery = Egg.getQuery();
        eggQuery.orderByAscending("title");

        eggQuery.findInBackground(new FindCallback<Egg>()
        {
            @Override
            public void done(List<Egg> objects, ParseException e)
            {
                if (e != null) {

                    if (WinnipegTrailsApplication.APPDEBUG) {
                        Log.d(WinnipegTrailsApplication.APPTAG, "An error occurred while querying for eggs", e);
                    }

                    return;
                }

                if (find) {
                    findEggs(objects);
                } else {
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
        if (googleApiClient.isConnected()) {
            return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        } else {
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

        if (googleApiClient.isConnected()) {
            startLocationUpdates();
        }

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

        createLocationRequest();
    }

    protected void createLocationRequest()
    {
        locationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (googleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop()
    {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }

        super.onStop();
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates()
    {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint)
    {
        if (WinnipegTrailsApplication.APPDEBUG) {
            // In debug mode, log the status
            Log.d(WinnipegTrailsApplication.APPTAG, "Connected to location services");
        }

        currentLocation = getLocation();
        if (currentLocation != null) {

            mapView.setCenter(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
            mapView.setZoom(15);
            updateCurrentLocation();
        }

        startLocationUpdates();
    }

    protected void startLocationUpdates()
    {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location)
    {
        currentLocation = location;
        updateCurrentLocation();
    }

    private void updateCurrentLocation()
    {
        if (currentLocation != null) {

            if (currentLocationMarker != null) {
                mapView.removeMarker(currentLocationMarker);
            }

            currentLocationMarker = new Marker(mapView, "", "", new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
            currentLocationMarker.setMarker(getResources().getDrawable(R.drawable.dude));
            mapView.addMarker(currentLocationMarker);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result)
    {
        if (WinnipegTrailsApplication.APPDEBUG) {
            // In debug mode, log the status
            Log.d(WinnipegTrailsApplication.APPTAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
        }
    }

    @Override
    public void onConnectionSuspended(int cause)
    {
        if (WinnipegTrailsApplication.APPDEBUG) {
            // In debug mode, log the status
            Log.d(WinnipegTrailsApplication.APPTAG, "Connection suspended: Cause = " + cause);
        }

        googleApiClient.connect();
    }
}