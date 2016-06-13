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
import android.net.http.SslError;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.R;
import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.internal.widgets.Html5WidgetInstance;
import eu.focusnet.app.model.json.FocusObject;
import eu.focusnet.app.model.json.FocusSample;
import eu.focusnet.app.network.HttpRequest;
import eu.focusnet.app.service.DataManager;
import eu.focusnet.app.ui.common.TouchWebView;
import eu.focusnet.app.util.ConfigurationHelper;




public class Html5WidgetFragment extends WidgetFragment
{


	private static final String JAVASCRIPT_EXPOSED_INTERFACE_OBJECT_NAME = "FocusApp";
	private static final String JAVASCRIPT_INIT_FUNCTION = JAVASCRIPT_EXPOSED_INTERFACE_OBJECT_NAME + ".init";
	private String context;


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
	 * Create a web client, which loads internal html pages with the Webview(pages saved in the app, the url has not host)
	 * other pages will be loaded for instance with the chrome browser
	 */
	private class FocusAppWebViewClient extends WebViewClient
	{
		/**
		 * FIXME FIXME DEBUG TODO
		 * remove this if all certificates have a proper CA. Or handle errors properly!! BIG SECURITY ISSUE
		 * @param view
		 * @param handler
		 * @param error
		 */
	 @Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
		{
			handler.proceed(); // if set, I get 403, but i should get 200
		}

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
	 * JavaScript interface definition
	 */
	private class WebAppInterface
	{
		Context context;
		DataManager dm;

		/**
		 * Instantiate the interface and set the context
		 */
		WebAppInterface(Context c)
		{
			this.dm = FocusApplication.getInstance().getDataManager();
			this.context = c;
		}

		/**
		 * Get FOCUS data
		 * <p/>
		 * advantages of going through app:
		 * - take benefit of auth/access control
		 * - permanent storage of accessed data
		 * - not relying on buggy/unstable local storage
		 * <p/>
		 * disadvantages:
		 * - no browser security (e.g. CORS) - but we only consume data, no scripts.
		 */
		@JavascriptInterface
		public String getFocusData(String url)
		{
			try {
				FocusSample fs = this.dm.getSample(url);
				return fs.toString();
			}
			catch (FocusMissingResourceException ex) {
				return null;
			}
		}

		/**
		 * POST Focus data
		 * <p/>
		 * create data to localstore (will later be pushed to server)
		 */
		@JavascriptInterface
		public DataManager.ResourceOperationStatus postFocusData(String url, String data)
		{
			FocusSample fs = (FocusSample) FocusObject.factory(data, FocusSample.class);
			return this.dm.create(fs);
		}

		/**
		 * PUT Focus data
		 * <p/>
		 * register data to local store (will later be pushed to server)
		 */
		@JavascriptInterface
		public DataManager.ResourceOperationStatus putFocusData(String url, String jsonData)
		{
			FocusSample fs = (FocusSample) FocusObject.factory(jsonData, FocusSample.class);
			return this.dm.update(fs);
		}

		/**
		 * DELETE Focus data
		 * <p/>
		 * announce deletion in local store (will later be deleted on server)
		 */
		@JavascriptInterface
		public DataManager.ResourceOperationStatus deleteFocusData(String url)
		{
			return this.dm.delete(url);
		}

		/**
		 * GET a non-FOCUS resource
		 * <p/>
		 * get from local resources store
		 * otherwise from network
		 * if no network -> FALSE (should have been detected by HTML5)
		 */
		@JavascriptInterface
		public String getResource(String url)
		{
			/*
			try {
				HttpResponse response = this.dm.get(url);
				return response.getData();
			}
			catch (IOException e) {
				return null;
			}
			*/
			return "bb";

			//      return "{\"mimetype\": \"image/png\", \"data\":\"base64encodeddata\"}"; // or exception
		}

		/**
		 * Get an access control token directly from the application
		 * <p/>
		 * FIXME - in the end, this information should come from an Access Control manager, and not
		 * from a simple hard-coded property
		 */
		@JavascriptInterface
		public String getAccessControlToken(String which)
		{
			String header = null;
			try {
				header = ConfigurationHelper.getProperty(HttpRequest.PROPERTY_HTTP_REQUEST_MODIFIER_PREFIX + which, FocusApplication.getInstance());
			}
			catch (IOException e) {
				throw new FocusInternalErrorException("Impossible to retrieve access token in properties.");
			}
			if (header != null) {
				return header;
			}
			else {
				return "";
			}
		}


	}

}
