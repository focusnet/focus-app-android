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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import eu.focusnet.app.R;
import eu.focusnet.app.model.internal.widgets.ExternalAppWidgetInstance;
import eu.focusnet.app.ui.util.Constant;
import eu.focusnet.app.ui.util.UiHelpers;

/**
 * An application launcher.
 */
public class ExternalAppFragment extends WidgetFragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View viewRoot = inflater.inflate(R.layout.fragment_external_app, container, false);
		setWidgetLayout(viewRoot);

		this.widgetInstance = getWidgetInstance();

		TextView textTitle = (TextView) viewRoot.findViewById(R.id.text_title_external_app);
		textTitle.setText(this.widgetInstance.getTitle());

		Button button = (Button) viewRoot.findViewById(R.id.launch_button);
		if (((ExternalAppWidgetInstance) widgetInstance).isAppAvailable()) {
			button.setText(((ExternalAppWidgetInstance) this.widgetInstance).getButtonLabel());
		}
		else {
			button.setText("Install " + ((ExternalAppWidgetInstance) widgetInstance).getAppIdentifier()); // FIXME request to install
		}

		// FIXME TODO

		button.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				if (((ExternalAppWidgetInstance) widgetInstance).isAppAvailable()) {
					Intent intent = new Intent(((ExternalAppWidgetInstance) widgetInstance).getAppIdentifier());
					intent.putExtra(Constant.UI_EXTRA_EXTERNAL_APP_INPUT, "FIXME");
					startActivityForResult(intent, ((ExternalAppWidgetInstance) widgetInstance).getRequestCode());
				}
				else {
					Intent intent = new Intent(Intent.ACTION_VIEW)
							.setData(Uri.parse("market://details?id=" + ((ExternalAppWidgetInstance) widgetInstance).getAppIdentifier()));
					startActivity(intent);
				}

				UiHelpers.displayToast(getActivity(), "Button clicked" + ((ExternalAppWidgetInstance) widgetInstance).getAppIdentifier());
			}
		});

		return viewRoot;
	}
}
