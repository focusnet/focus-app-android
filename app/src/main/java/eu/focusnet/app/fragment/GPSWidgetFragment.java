package eu.focusnet.app.fragment;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import eu.focusnet.app.R;
import eu.focusnet.app.common.WidgetFragment;

/**
 * Created by yandypiedra on 14.01.16.
 */
public class GPSWidgetFragment extends WidgetFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private volatile boolean positionAsked;
    private GoogleApiClient googleApiClient;
    private TextView longitudeValue, latitudeValue, accuracyValue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View viewRoot = inflater.inflate(R.layout.fragment_gps, container, false);

        longitudeValue = (TextView) viewRoot.findViewById(R.id.text_longitude_value);

        latitudeValue =  (TextView) viewRoot.findViewById(R.id.text_latitude_value);

        accuracyValue = (TextView) viewRoot.findViewById(R.id.text_accuracy_value);

        Button gpsPosition= (Button) viewRoot.findViewById(R.id.button_gps_position);
        gpsPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionAsked = true;
            }
        });

        return viewRoot;
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();

    }

    @Override
    public void onPause() {
        stopLocationUpdates();
        super.onPause();
    }


    private void startLocationUpdates() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        googleApiClient.connect();
    }

    private void stopLocationUpdates() {
        if (googleApiClient.isConnected()) { // !!!! important to check for service connection
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient, this);
        }

        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(1500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (positionAsked) {
            longitudeValue.setText(""+location.getLongitude());
            latitudeValue.setText(""+location.getLatitude());
            accuracyValue.setText(""+location.getAccuracy());
            positionAsked = false;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
}
