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

import java.net.MalformedURLException;
import java.net.URL;

public class WebViewFragment extends Fragment {

    private WebView myWebView;
    private String data;


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

        // Enable app-js communication
        myWebView.addJavascriptInterface(new WebAppInterface(getActivity()), "FocusApp");
        myWebView.setWebChromeClient(new WebChromeClient());

        return viewRoot;
    }

    public void loadWebView(String url, String data) {
        this.data = data;
        myWebView.loadUrl("file:///android_asset/www/" + url + ".html");
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
            view.loadUrl("javascript:init('" + data + "')");
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
         * from local db if available
         * otherwise from netwokr
         * if no network -> FALSE (HTML5 should have detected that)
         * <p>
         * FIXME
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
            //This is only for demo purposes
            String data = null;
            try {
               data =  NetworkUtil.makeHttpRequest(new URL(url), NetworkUtil.HTTP_METHOD_GET);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return data;
        }

        /**
         * POST Focus data
         * <p>
         * post data to localstore (will later be pushed to server)
         */
        @JavascriptInterface
        public boolean postFocusData(String url, String data) {
            return true; // or false, or Exception if bad data
        }

        /**
         * PUT Focus data
         * <p>
         * put data to local store (will later be pushed to server)
         */
        @JavascriptInterface
        public boolean putFocusData(String url, String data) {

            return true; // ...
        }

        /**
         * DELETE Focus data
         * <p>
         * announce deletion in local store (will later be deleted on server)
         */
        @JavascriptInterface
        public boolean deleteFocusData(String url) {
            return true; // false
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
            return "{\"mimetype\": \"image/png\", \"data\":\"base64encodeddata\"}"; // or exception
        }


    }

}
