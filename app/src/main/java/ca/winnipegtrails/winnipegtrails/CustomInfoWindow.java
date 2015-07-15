package ca.winnipegtrails.winnipegtrails;

import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.InfoWindow;
import com.mapbox.mapboxsdk.views.MapView;

public class CustomInfoWindow extends InfoWindow
{
    public CustomInfoWindow(MapView mapView)
    {
        super(R.layout.info_egg, mapView);

        // Add own OnTouchListener to customize handling InfoWindow touch events
        setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    close();
                }

                // Return true as we're done processing this event
                return true;
            }
        });
    }

    @Override
    public void onOpen(Marker marker)
    {
        TextView title = (TextView) mView.findViewById(R.id.info_title);
        title.setText(marker.getTitle());

        TextView snippet = (TextView) mView.findViewById(R.id.info_snippet);
        snippet.setText(marker.getDescription());
    }
}
