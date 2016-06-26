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

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import eu.focusnet.app.controller.FocusAppLogic;
import eu.focusnet.app.R;
import eu.focusnet.app.model.gson.FocusSample;
import eu.focusnet.app.model.widgets.ExternalAppWidgetInstance;
import eu.focusnet.app.ui.common.UiHelper;
import eu.focusnet.app.util.Constant;

/**
 * {@code Fragment} rendering an external application launcher widget
 */
public class ExternalAppFragment extends WidgetFragment
{
	/**
	 * Create the View. The launch listener button is modified depending if the external
	 * application is installed or not. If it is not, then the button triggers the installation
	 * in Google Play. If it is, then the external application is launched.
	 * <p/>
	 * The external application has an optional stringified {@link FocusSample}
	 * input parameter passed in
	 * the {@link eu.focusnet.app.util.Constant.Extra#UI_EXTRA_EXTERNAL_APP_INPUT} extra.
	 *
	 * @param inflater           Inherited
	 * @param container          Inherited
	 * @param savedInstanceState Inherited
	 * @return The new View
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// setup
		super.onCreate(savedInstanceState);
		this.setupWidget(inflater.inflate(R.layout.fragment_widget_external_app, container, false));

		TextView textIdentifier = (TextView) this.rootView.findViewById(R.id.external_app_value_identifier);
		textIdentifier.setText(((ExternalAppWidgetInstance) widgetInstance).getAppIdentifier());

		TextView textStatus = (TextView) this.rootView.findViewById(R.id.external_app_value_status);

		Button button = (Button) this.rootView.findViewById(R.id.launch_button);
		if (((ExternalAppWidgetInstance) widgetInstance).updateAppAvailability()) {
			textStatus.setText(
					String.format(getResources().getString(R.string.installed_ready_to_be_used),
							((ExternalAppWidgetInstance) widgetInstance).getInstalledVersion())
			);
			button.setText(((ExternalAppWidgetInstance) this.widgetInstance).getButtonLabel());
		}
		else {
			textStatus.setText(R.string.not_installed);
			textStatus.setTextColor(getResources().getColor(R.color.red));
			button.setText(R.string.install_via_google_play);
		}

		/**
		 * When the action button is clicked, do launch the app or redirect to Google Play
		 */
		button.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Resources r = getActivity().getResources();
				if (((ExternalAppWidgetInstance) widgetInstance).updateAppAvailability()) {
					Intent intent = new Intent(((ExternalAppWidgetInstance) widgetInstance).getAppIdentifier());

					FocusSample inputObject = ((ExternalAppWidgetInstance) widgetInstance).getInputObject();
					if (inputObject != null) {
						String jsonInput = FocusAppLogic.getGson().toJson(inputObject);
						intent.putExtra(Constant.Extra.UI_EXTRA_EXTERNAL_APP_INPUT, jsonInput);
					}
					UiHelper.displayToast(getActivity(), r.getString(R.string.launching_ext_app));
					startActivityForResult(intent, ((ExternalAppWidgetInstance) widgetInstance).getRequestCode());
				}
				else {
					Uri uri = Uri.parse("market://details?id=" + ((ExternalAppWidgetInstance) widgetInstance).getAppIdentifier());
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					try {
						startActivity(intent);
					}
					catch (ActivityNotFoundException ex) {
						// market is not installed
						UiHelper.displayToast(getActivity(), r.getString(R.string.market_not_installed));
					}
				}


			}
		});

		return this.rootView;
	}

	/**
	 * When the external application returns, retrieve the result from the
	 * {@link Constant.Extra#UI_EXTRA_EXTERNAL_APP_OUTPUT} extra, which comes as a stringified
	 * {@link FocusSample}.
	 *
	 * @param requestCode Must be the same as the one that has been set when launching the
	 *                    external app.
	 * @param resultCode  Must be {@code Activity.RESULT_OK}
	 * @param intent      The returning {@code Intent}
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		if (requestCode == ((ExternalAppWidgetInstance) widgetInstance).getRequestCode()) {
			if (resultCode == Activity.RESULT_OK) {
				String response = intent.getStringExtra(Constant.Extra.UI_EXTRA_EXTERNAL_APP_OUTPUT);
				if (response != null && !response.equals("")) {
					((ExternalAppWidgetInstance) widgetInstance).saveResponse(response);
				}
				UiHelper.displayToast(getActivity(), getActivity().getResources().getString(R.string.appreturned));
			}
		}
	}

}
