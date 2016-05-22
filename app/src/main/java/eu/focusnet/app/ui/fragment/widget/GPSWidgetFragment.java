/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.ui.fragment.widget;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
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
import eu.focusnet.app.model.internal.widgets.GPSWidgetInstance;

/**
 */
public class GPSWidgetFragment extends WidgetFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,  LocationListener
{

	private volatile boolean positionAsked;
	private GoogleApiClient googleApiClient;
	private TextView longitudeValue, latitudeValue, accuracyValue;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View viewRoot = inflater.inflate(R.layout.fragment_gps, container, false);

		setWidgetLayout(viewRoot);

		// --------------------------------- debug FIXME DEBUG TODO
		LocationManager lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
		boolean gps_enabled	= lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


		this.widgetInstance = this.getWidgetInstance();

		// set widget title
		TextView textTitle = (TextView) viewRoot.findViewById(R.id.textTitle);
		if (this.widgetInstance.getTitle() == null) {
			((ViewGroup) textTitle.getParent()).removeView(textTitle);
		}
		else {
			textTitle.setText(this.widgetInstance.getTitle());
		}

		longitudeValue = (TextView) viewRoot.findViewById(R.id.text_longitude_value);
		latitudeValue = (TextView) viewRoot.findViewById(R.id.text_latitude_value);
		accuracyValue = (TextView) viewRoot.findViewById(R.id.text_accuracy_value);

		Button gpsPosition = (Button) viewRoot.findViewById(R.id.button_gps_position);
		gpsPosition.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				positionAsked = true;
			}
		});

		return viewRoot;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		startLocationUpdates();

	}

	@Override
	public void onPause()
	{
		stopLocationUpdates();
		super.onPause();
	}


	private void startLocationUpdates()
	{
		if (googleApiClient == null) {
			googleApiClient = new GoogleApiClient.Builder(getActivity())
					.addApi(LocationServices.API)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.build();
		}
		googleApiClient.connect();
	}

	private void stopLocationUpdates()
	{
		if (googleApiClient.isConnected()) { // !!!! important to check for service connection
			LocationServices.FusedLocationApi.removeLocationUpdates(
					googleApiClient, this);
		}

		googleApiClient.disconnect();
	}

	@Override
	public void onConnected(Bundle bundle)
	{
		LocationRequest locationRequest = new LocationRequest();
		locationRequest.setInterval(3000);
		locationRequest.setFastestInterval(1500);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
	}

	@Override
	public void onConnectionSuspended(int i)
	{
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult)
	{
	}


	@Override
	public void onLocationChanged(Location location)
	{
		if (positionAsked) {
			((GPSWidgetInstance) this.widgetInstance).saveSample(location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getAccuracy());
			longitudeValue.setText("" + location.getLongitude());
			latitudeValue.setText("" + location.getLatitude());
			accuracyValue.setText("" + location.getAccuracy());
			positionAsked = false;
		}
	}


}
