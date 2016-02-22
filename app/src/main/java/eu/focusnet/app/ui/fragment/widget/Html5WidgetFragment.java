/**
 *
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
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

import java.io.IOException;

import eu.focusnet.app.R;
import eu.focusnet.app.model.internal.widgets.Html5WidgetInstance;
import eu.focusnet.app.model.json.FocusObject;
import eu.focusnet.app.model.json.FocusSample;
import eu.focusnet.app.network.HttpResponse;
import eu.focusnet.app.network.NetworkManager;
import eu.focusnet.app.service.DataManager;

// FIXME should use DataManager instead of NetworkManager!
public class Html5WidgetFragment extends WidgetFragment
{

	private WebView myWebView;
	private String context;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View viewRoot = inflater.inflate(R.layout.fragment_webview, container, false);
		setWidgetLayout(viewRoot);

		Html5WidgetInstance html5WidgetInstance = (Html5WidgetInstance) getWidgetInstance();
		//TODO do something with this

		myWebView = (WebView) viewRoot.findViewById(R.id.webview);

		// configure the WebView
		myWebView.setWebViewClient(new FocusAppWebViewClient());
		WebSettings settings = myWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setDatabaseEnabled(true);
		settings.setDomStorageEnabled(true);
		settings.setGeolocationEnabled(true);
		// TODO should we activate more features?

		// Enable app-js communication
		myWebView.addJavascriptInterface(new WebAppInterface(getActivity()), "FocusApp");
		myWebView.setWebChromeClient(new WebChromeClient());

		return viewRoot;
	}

	public void loadWebView(String assetName, String context)
	{
		this.context = context;
		myWebView.loadUrl("file:///android_asset/www/" + assetName + "/index.html");
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
			view.loadUrl("javascript:init('" + context + "')");
		}
	}

	private class WebAppInterface
	{
		Context mContext;

		/**
		 * Instantiate the interface and set the context
		 */
		WebAppInterface(Context c)
		{
			mContext = c;
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
			NetworkManager net = NetworkManager.getInstance();
			try {
				HttpResponse response = net.get(url);
				return response.getData();
			}
			catch (IOException e) {
				return null;
			}
		}

		/**
		 * POST Focus data
		 * <p/>
		 * post data to localstore (will later be pushed to server)
		 */
		@JavascriptInterface
		public boolean postFocusData(String url, String data)
		{
			NetworkManager net = NetworkManager.getInstance();
			try {
				FocusSample fs = (FocusSample) FocusObject.factory(data, FocusSample.class);
				return net.post(url, fs).isSuccessful();
			}
			catch (IOException e) {
				return false;
			}
		}

		/**
		 * PUT Focus data
		 * <p/>
		 * put data to local store (will later be pushed to server)
		 */
		@JavascriptInterface
		public boolean putFocusData(String url, String data)
		{
			NetworkManager net = NetworkManager.getInstance();
			try {
				FocusSample fs = (FocusSample) FocusObject.factory(data, FocusSample.class);
				return net.put(url, fs).isSuccessful();
			}
			catch (IOException e) {
				return false;
			}
		}

		/**
		 * DELETE Focus data
		 * <p/>
		 * announce deletion in local store (will later be deleted on server)
		 */
		@JavascriptInterface
		public boolean deleteFocusData(String url)
		{
			NetworkManager net = NetworkManager.getInstance();
			try {
				return net.delete(url).isSuccessful();
			}
			catch (IOException e) {
				return false;
			}
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
			NetworkManager net = NetworkManager.getInstance();
			try {
				HttpResponse response = net.get(url);
				return response.getData();
			}
			catch (IOException e) {
				return null;
			}

			//      return "{\"mimetype\": \"image/png\", \"data\":\"base64encodeddata\"}"; // or exception
		}


	}

}
