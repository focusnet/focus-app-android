package com.example.webviewdemo;

import android.app.Fragment;
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
import android.widget.Toast;

import com.example.webviewdemo.network.HttpResponse;
import com.example.webviewdemo.network.NetworkManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

// FIXME should use DataManager instead of NetworkManager!
public class WebViewFragment extends Fragment {

	private WebView myWebView;
	private String context;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View viewRoot = inflater.inflate(R.layout.fragment_webview, container, false);
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

	public void loadWebView(String assetName, String context) {
		this.context = context;
		myWebView.loadUrl("file:///android_asset/www/" + assetName + "/index.html");
	}

	/**
	 * Create a web client, which loads internal html pages with the Webview(pages saved in the app, the url has not host)
	 * other pages will be loaded for instance with the chrome browser
	 */
	private class FocusAppWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
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
		public void onPageFinished(WebView view, String url) {
			// On page loaded call this javascript function to pass
			// data to the loaded page
			// pass some param to init the HTML5 app here, eg. data
			view.loadUrl("javascript:init('" + context + "')");
		}
	}

	private class WebAppInterface {
		Context mContext;

		/**
		 * Instantiate the interface and set the context
		 */
		WebAppInterface(Context c) {
			mContext = c;
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
		public String getFocusData(String url) {
			NetworkManager net = NetworkManager.getInstance();
			try {
				HttpResponse response = net.get(url);
				return response.getData();
			} catch (IOException e) {
				return null;
			}
		}

		/**
		 * POST Focus data
		 * <p>
		 * post data to localstore (will later be pushed to server)
		 */
		@JavascriptInterface
		public boolean postFocusData(String url, String data) {
			NetworkManager net = NetworkManager.getInstance();
			try {
				return net.post(url, data).isSuccessful();
			} catch (IOException e) {
				return false;
			}
		}

		/**
		 * PUT Focus data
		 * <p>
		 * put data to local store (will later be pushed to server)
		 */
		@JavascriptInterface
		public boolean putFocusData(String url, String data) {
			NetworkManager net = NetworkManager.getInstance();
			try {
				return net.put(url, data).isSuccessful();
			} catch (IOException e) {
				return false;
			}
		}

		/**
		 * DELETE Focus data
		 * <p>
		 * announce deletion in local store (will later be deleted on server)
		 */
		@JavascriptInterface
		public boolean deleteFocusData(String url) {
			NetworkManager net = NetworkManager.getInstance();
			try {
				return net.delete(url).isSuccessful();
			} catch (IOException e) {
				return false;
			}
		}

		/**
		 * GET a non-FOCUS resource
		 * <p>
		 * get from local resources store
		 * otherwise from network
		 * if no network -> FALSE (should have been detected by HTML5)
		 */
		@JavascriptInterface
		public String getResource(String url) {
			NetworkManager net = NetworkManager.getInstance();
			try {
				HttpResponse response = net.get(url);
				return response.getData();
			} catch (IOException e) {
				return null;
			}

			//      return "{\"mimetype\": \"image/png\", \"data\":\"base64encodeddata\"}"; // or exception
		}


	}

}
