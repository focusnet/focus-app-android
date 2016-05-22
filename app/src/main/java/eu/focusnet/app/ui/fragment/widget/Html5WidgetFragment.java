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
import android.widget.TextView;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.R;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.internal.widgets.Html5WidgetInstance;
import eu.focusnet.app.model.json.FocusObject;
import eu.focusnet.app.model.json.FocusSample;
import eu.focusnet.app.service.DataManager;

// FIXME should use DataManager instead of NetworkManager!
// FIXME FIXME FIXME TODO

public class Html5WidgetFragment extends WidgetFragment
{

	private static final String JAVASCRIPT_EXPOSED_INTERFACE_OBJECT_NAME = "FocusApp";
	private static final String JAVASCRIPT_INIT_FUNCTION = "init";

	private WebView myWebView;
	private String context;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		this.widgetInstance = getWidgetInstance();

		// FIXME better to let the webapp take the space it wants.
		// this.reference_height= 200;

		View viewRoot = inflater.inflate(R.layout.fragment_webview, container, false);
		setWidgetLayout(viewRoot);

		// set widget title
		TextView textTitle = (TextView) viewRoot.findViewById(R.id.text_title_webapp);
		if (this.widgetInstance.getTitle() == null) {
			((ViewGroup) textTitle.getParent()).removeView(textTitle);
		}
		else {
			textTitle.setText(this.widgetInstance.getTitle());
		}

		myWebView = (WebView) viewRoot.findViewById(R.id.webview);

		// configure the WebView
		myWebView.setWebViewClient(new FocusAppWebViewClient());
		WebSettings settings = myWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setDatabaseEnabled(true);
		settings.setDomStorageEnabled(true);
		settings.setGeolocationEnabled(true);
		settings.setUseWideViewPort(true);

		// Enable app-js communication
		myWebView.addJavascriptInterface(new WebAppInterface(getActivity()), JAVASCRIPT_EXPOSED_INTERFACE_OBJECT_NAME);
		myWebView.setWebChromeClient(new WebChromeClient());

		// and load the actual page in the browser

		this.context = ((Html5WidgetInstance) this.widgetInstance).getContext();
		myWebView.loadUrl("file:///android_asset/webapps/" + ((Html5WidgetInstance) this.widgetInstance).getWebAppIdentifier() + "/index.html");

		return viewRoot;
	}


	/**
	 * Create a web client, which loads internal html pages with the Webview(pages saved in the app, the url has not host)
	 * other pages will be loaded for instance with the chrome browser
	 */
	private class FocusAppWebViewClient extends WebViewClient
	{

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
					String.format("javascript: %s = (typeof %s !== 'undefined') ? %s : function(ctx) { }; %s(\"%s\");",
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
		 * <p>
		 * advantages of going through app:
		 * - take benefit of auth/access control
		 * - permanent storage of accessed data
		 * - not relying on buggy/unstable local storage
		 * <p>
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
		 * <p>
		 * post data to localstore (will later be pushed to server)
		 */
		@JavascriptInterface
		public DataManager.ResourceOperationStatus postFocusData(String url, String data)
		{
			FocusSample fs = (FocusSample) FocusObject.factory(data, FocusSample.class);
			return this.dm.post(url, fs);
		}

		/**
		 * PUT Focus data
		 * <p>
		 * register data to local store (will later be pushed to server)
		 */
		@JavascriptInterface
		public DataManager.ResourceOperationStatus putFocusData(String url, String data)
		{
			FocusSample fs = (FocusSample) FocusObject.factory(data, FocusSample.class);
			return this.dm.put(url, fs);
		}

		/**
		 * DELETE Focus data
		 * <p>
		 * announce deletion in local store (will later be deleted on server)
		 */
		@JavascriptInterface
		public DataManager.ResourceOperationStatus deleteFocusData(String url)
		{
			return this.dm.delete(url);
		}

		/**
		 * GET a non-FOCUS resource
		 * <p>
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


	}

}
