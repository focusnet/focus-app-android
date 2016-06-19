package eu.focusnet.app.ui.fragment.widget;

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

import eu.focusnet.app.FocusAppLogic;
import eu.focusnet.app.R;
import eu.focusnet.app.model.internal.widgets.ExternalAppWidgetInstance;
import eu.focusnet.app.model.json.FocusSample;
import eu.focusnet.app.ui.util.Constant;
import eu.focusnet.app.ui.util.UiHelper;

/**
 * An application launcher.
 */
public class ExternalAppFragment extends WidgetFragment
{
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
						intent.putExtra(Constant.UI_EXTRA_EXTERNAL_APP_INPUT, jsonInput);
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == ((ExternalAppWidgetInstance) widgetInstance).getRequestCode()) {
			if (resultCode == Activity.RESULT_OK) {
				String response = data.getStringExtra(Constant.UI_EXTRA_EXTERNAL_APP_OUTPUT);
				if (response != null && !response.equals("")) {
					((ExternalAppWidgetInstance) widgetInstance).saveResponse(response);
				}
				UiHelper.displayToast(getActivity(), getActivity().getResources().getString(R.string.appreturned));
			}
		}
	}

}
