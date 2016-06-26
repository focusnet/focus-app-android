/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.ui.fragment.widget;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import eu.focusnet.app.R;
import eu.focusnet.app.model.widgets.GPSWidgetInstance;
import eu.focusnet.app.util.ApplicationHelper;

/**
 * A {@code Fragment} rendering the GPS coordinates fetching form widget.
 * <p/>
 * FIXME move to same code as MOTI. Much better.
 */
public class GPSWidgetFragment extends WidgetFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
{

	/**
	 * Polling interval for location updates
	 */
	final public static int LOCATION_POLLING_INTERVAL = 3_000;

	/**
	 * Fast polling interval for location updates
	 */
	final public static int LOCATION_POLLING_FAST_INTERVAL = 1_500;

	/**
	 * Google API client for GPS localization
	 */
	private GoogleApiClient googleApiClient;

	/**
	 * The different {@code View}s for displaying localization information
	 */
	private TextView longitudeView, latitudeView, accuracyView;

	/**
	 * Create the View holding location information
	 *
	 * @param inflater           Inherited
	 * @param container          Inherited
	 * @param savedInstanceState Inherited
	 * @return The new View
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setupWidget(inflater.inflate(R.layout.fragment_widget_gps, container, false));

		this.longitudeView = (TextView) this.rootView.findViewById(R.id.text_longitude_value);
		this.latitudeView = (TextView) this.rootView.findViewById(R.id.text_latitude_value);
		this.accuracyView = (TextView) this.rootView.findViewById(R.id.text_accuracy_value);

		this.setUnavailableMessage();

		return this.rootView;
	}

	/**
	 * Start localization updates
	 */
	@Override
	public void onStart()
	{
		super.onStart();
		this.startLocationUpdates();
	}

	/**
	 * Resume location updating
	 */
	@Override
	public void onResume()
	{
		super.onResume();
		this.startLocationUpdates();
	}

	/**
	 * Pause location updating
	 */
	@Override
	public void onPause()
	{
		this.stopLocationUpdates();
		super.onPause();
	}

	/**
	 * Start localization updates
	 */
	private void startLocationUpdates()
	{
		if (this.googleApiClient == null) {
			this.googleApiClient = new GoogleApiClient.Builder(getActivity())
					.addApi(LocationServices.API)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.build();
		}
		this.googleApiClient.connect();
	}

	/**
	 * Stop localization updates
	 */
	private void stopLocationUpdates()
	{
		if (this.googleApiClient.isConnected()) { // !!!! important to check for service connection
			LocationServices.FusedLocationApi.removeLocationUpdates(
					this.googleApiClient, this);
		}

		this.googleApiClient.disconnect();
	}

	/**
	 * When connected, configure localization updates.
	 *
	 * @param bundle Inherited
	 */
	@Override
	public void onConnected(Bundle bundle)
	{
		LocationRequest locationRequest = new LocationRequest();
		locationRequest.setInterval(LOCATION_POLLING_INTERVAL);
		locationRequest.setFastestInterval(LOCATION_POLLING_FAST_INTERVAL);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		if (ApplicationHelper.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
				|| ApplicationHelper.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
			LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
		}
	}

	/**
	 * When the connection is suspended
	 *
	 * @param i Inherited
	 */
	@Override
	public void onConnectionSuspended(int i)
	{
		this.setUnavailableMessage();
	}

	/**
	 * When location has changed, update the UI to show where we are.
	 *
	 * @param location The new location
	 */
	@Override
	public void onLocationChanged(Location location)
	{
		((GPSWidgetInstance) this.widgetInstance).saveSample(location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getAccuracy());
		this.longitudeView.setText("" + location.getLongitude());
		this.latitudeView.setText("" + location.getLatitude());
		this.accuracyView.setText("" + location.getAccuracy());
	}


	/**
	 * On connection failure, display an error message in place of the coordinates.
	 *
	 * @param connectionResult Inherited
	 */
	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
	{
		this.setUnavailableMessage();
	}

	/**
	 * Set dummy "non available" message to all location fields in the UI
	 */
	private void setUnavailableMessage()
	{
		((GPSWidgetInstance)this.widgetInstance).resetSample();
		this.longitudeView.setText(R.string.n_a);
		this.latitudeView.setText(R.string.n_a);
		this.accuracyView.setText(R.string.n_a);
	}
}
