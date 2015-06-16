package ca.winnipegtrails.winnipegtrails;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener
{
    private GoogleMap googleMap;
    private final Map<String, Marker> mapMarkers = new HashMap<>();
    private GoogleApiClient googleApiClient;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();
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
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(49.8994, -97.1392), 10));
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
                            .title(item.getTitle())
                            .snippet(item.getPoints().toString())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                    // Add a new marker
                    Marker marker = googleMap.addMarker(markerOpts);
                    mapMarkers.put(item.getObjectId(), marker);
                }
            }
        });
    }

    /*
     * Get the current location
     */
    private Location getLocation() {

        if(servicesConnected()) {
            return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }
        else {
            return null;
        }
    }

    /*
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected()
    {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if(ConnectionResult.SUCCESS == resultCode) {

            if(WinnipegTrailsApplication.APPDEBUG) {
                // In debug mode, log the status
                Log.d(WinnipegTrailsApplication.APPTAG, "Google play services available");
            }
            // Continue
            return true;
        }
        // Google Play services was not available for some reason
        else {

            Toast.makeText(this, R.string.error_google_play, Toast.LENGTH_LONG).show();
            return false;
        }
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

    /*
     * Called when the Activity is resumed. Updates the view.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        doMapQuery();
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
            Log.d(WinnipegTrailsApplication.APPTAG, "Connected to location services");
        }

        currentLocation = getLocation();
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
            Log.d(WinnipegTrailsApplication.APPTAG, "Connection suspended");
        }

        googleApiClient.connect();
    }
}