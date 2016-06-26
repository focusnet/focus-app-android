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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import eu.focusnet.app.R;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.exception.FocusNotImplementedException;
import eu.focusnet.app.model.gson.FocusObject;
import eu.focusnet.app.model.gson.FocusSample;
import eu.focusnet.app.model.widgets.Html5WidgetInstance;
import eu.focusnet.app.service.DataManager;
import eu.focusnet.app.ui.common.TouchWebView;
import eu.focusnet.app.util.ApplicationHelper;
import eu.focusnet.app.util.Constant;

/**
 * A {@code Fragment} rendering a webapp inside a {@code WebView}. We in fact use a {@link TouchWebView} to
 * overcome problems when including such a {@code View} type inside a {@code ScrollView}.
 */
public class Html5WidgetFragment extends WidgetFragment
{
	/**
	 * Global JavaScript variable that can be accessed from the webapp to interact with the
	 * application.
	 */
	private static final String JAVASCRIPT_EXPOSED_INTERFACE_OBJECT_NAME = "FocusApp";

	/**
	 * JavaScript initilization function in the webapp that will be called by the application after
	 * loading.
	 */
	private static final String JAVASCRIPT_INIT_FUNCTION = JAVASCRIPT_EXPOSED_INTERFACE_OBJECT_NAME + ".init";

	/**
	 * The context to be passed to the {@link #JAVASCRIPT_INIT_FUNCTION}.
	 */
	private String context;


	/**
	 * Create the view. In this method, we configure our custom {@link TouchWebView}, enabling all
	 * features that we may need. We also load the webapp entrypoint from the assets.
	 *
	 * @param inflater           Inherited
	 * @param container          Inherited
	 * @param savedInstanceState Inherited
	 * @return The new View.
	 */
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// setup: we leave the contained webapp decide of the height of the widget (via viewport and body height,
		// preferably expressed in vw)
		super.onCreate(savedInstanceState);
		this.setupWidget(inflater.inflate(R.layout.fragment_widget_html5_webapp, container, false));

		TouchWebView webView = (TouchWebView) this.rootView.findViewById(R.id.webview);

		// configure the WebView
		webView.setWebViewClient(new FocusAppWebViewClient());
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setDatabaseEnabled(true);

		settings.setAllowFileAccessFromFileURLs(true); //Maybe you don't need this rule
		settings.setAllowUniversalAccessFromFileURLs(true);

		settings.setDomStorageEnabled(true);
		settings.setGeolocationEnabled(true);
		settings.setUseWideViewPort(true);

		// Enable app-js communication
		webView.addJavascriptInterface(new WebAppInterface(getActivity()), JAVASCRIPT_EXPOSED_INTERFACE_OBJECT_NAME);
		webView.setWebChromeClient(new WebChromeClient());

		// and load the actual page in the browser

		this.context = ((Html5WidgetInstance) this.widgetInstance).getContext();

		webView.loadUrl(
				"file:///android_asset/"
						+ Html5WidgetInstance.ASSETS_HTML5_WEBAPPS_FOLDER + "/"
						+ ((Html5WidgetInstance) this.widgetInstance).getWebAppIdentifier() + "/"
						+ Html5WidgetInstance.ASSETS_HTML5_WEBAPPS_ENTRY_POINT
		);

		return this.rootView;
	}


	/**
	 * Create a web client, which loads internal HTML pages with the custom {@link TouchWebView}.
	 * Other pages will be loaded for instance with the chrome browser
	 */
	private class FocusAppWebViewClient extends WebViewClient
	{
		/**
		 * If remote hosts being directly accessed by the webapp do not have a valid certificate,
		 * this overridden function will grant access to all resource without further checking.
		 *
		 * @deprecated This is bad. Don't enable it.
		 */
		/*
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
		{
			handler.proceed(); // if set, I get 403, but i should get 200
		}
		*/

		/**
		 * Override URL loading. External pages are loaded in the browser.
		 *
		 * @param view Inherited
		 * @param url  Inherited
		 * @return Inherited
		 */
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			//for internal pages
			if (Uri.parse(url).getHost().length() == 0) {
				return false;
			}

			//For other pages
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			view.getContext().startActivity(intent);
			return true;
		}

		/**
		 * When the page has finished loading, call the initialization function if it exists.
		 *
		 * @param view Inherited
		 * @param url  Inherited
		 */
		@Override
		public void onPageFinished(WebView view, String url)
		{
			// On page loaded call this javascript function to pass
			// data to the loaded page
			// pass some param to init the HTML5 app here, eg. data
			view.loadUrl(
					String.format("javascript: %s = (typeof %s !== 'undefined') ? %s : function(ctx) { console.log('No %s implemented.'); }; %s(\"%s\");",
							JAVASCRIPT_INIT_FUNCTION,
							JAVASCRIPT_INIT_FUNCTION,
							JAVASCRIPT_INIT_FUNCTION,
							JAVASCRIPT_INIT_FUNCTION,
							JAVASCRIPT_INIT_FUNCTION,
							context
					)
			);
		}
	}

	/**
	 * JavaScript interface definition.
	 * <p/>
	 * Using this interface grants access to all facilities provided by the application to the
	 * webapp, such as access control tokens, offline usage of data, etc.
	 * <p/>
	 * The browser imposes a maximum of 6 concurrent downloads. This can be overcome by using the
	 * below interface.
	 * <p/>
	 * In the webapp, these calls are blocking. Therefore the webapp must implement Web Workers.
	 * <p/>
	 * FIXME implement non-blocking methods with {@code Future}s
	 */
	private class WebAppInterface
	{
		/**
		 * Android Context
		 */
		Context context;

		/**
		 * Current application {@link DataManager}
		 */
		DataManager dataManager;

		/**
		 * Instantiate the interface and set the context
		 */
		WebAppInterface(Context c)
		{
			this.dataManager = widgetInstance.getDataManager();
			this.context = c;
		}

		/**
		 * Get a {@link FocusSample} as a String
		 *
		 * @param url the URL of the resource to fetch
		 * @return The String representation of the returned object, or {@code null}.
		 */
		@JavascriptInterface
		public String getFocusData(String url)
		{
			try {
				FocusSample fs = this.dataManager.getSample(url);
				return fs.toString();
			}
			catch (FocusMissingResourceException ex) {
				return null;
			}
		}

		/**
		 * Create a new {@link FocusSample}
		 *
		 * @param url      URL of the resource to create
		 * @param jsonData The stringified JSON data to create
		 * @return The return status of the operation
		 */
		@JavascriptInterface
		public DataManager.ResourceOperationStatus postFocusData(String url, String jsonData)
		{
			FocusSample fs = (FocusSample) FocusObject.factory(jsonData, FocusSample.class);
			return this.dataManager.create(fs);
		}

		/**
		 * Update a {@link FocusSample}
		 *
		 * @param url      URL of the resource to update
		 * @param jsonData The stringified JSON data to update
		 * @return The return status of the operation
		 */
		@JavascriptInterface
		public DataManager.ResourceOperationStatus putFocusData(String url, String jsonData)
		{
			FocusSample fs = (FocusSample) FocusObject.factory(jsonData, FocusSample.class);
			return this.dataManager.update(fs);
		}

		/**
		 * Delete a {@link FocusSample}
		 *
		 * @param url URL of the resource to delete
		 * @return The return status of the operation
		 */
		@JavascriptInterface
		public DataManager.ResourceOperationStatus deleteFocusData(String url)
		{
			return this.dataManager.delete(url);
		}

		/**
		 * Get a non-FOCUS resource
		 *
		 * @param url The resource to get
		 * @return A base64 String containing the fetched resource.
		 * <p/>
		 * FIXME mimetype?
		 */
		@JavascriptInterface
		public String getResource(String url)
		{
			throw new FocusNotImplementedException("Html5WidgetFragment.WebAppInterface.getResource()");
		}

		/**
		 * Get an access control token directly from the application
		 *
		 * FIXME this information should come from the UserManager
		 */
		@JavascriptInterface
		public String getAccessControlToken(String which)
		{
			String header = ApplicationHelper.getProperty(Constant.AppConfig.PROPERTY_HTTP_REQUEST_MODIFIER_PREFIX + which);
			if (header != null) {
				return header;
			}
			else {
				return "";
			}
		}


	}

}
