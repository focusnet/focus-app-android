package eu.focusnet.app.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import eu.focusnet.app.activity.R;
import eu.focusnet.app.util.ViewUtil;

/**
 * Created by yandypiedra on 20.10.15.
 */
public class WebViewFragment extends Fragment {

    private WebView myWebView;
    private String data;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_webview, container, false);
        myWebView = (WebView) viewRoot.findViewById(R.id.webview);
        myWebView.setWebViewClient(new MyAppWebViewClient());
        WebSettings settings = myWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);

        myWebView.addJavascriptInterface(new WebAppInterface(getActivity()), "Android");

        myWebView.setWebChromeClient(new WebChromeClient());

        return viewRoot;
    }

    public void loadWebView(String url, String data) {
        this.data = data;
        myWebView.loadUrl("file:///android_asset/www/" + url + ".html");
    }


    public void setData(String data) {
        myWebView.loadUrl("javascript:lifeData('" + data + "')");
    }


    private class MyAppWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().length() == 0) {
                return false;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            view.getContext().startActivity(intent);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            view.loadUrl("javascript:load('" + data + "')");
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

        @JavascriptInterface
        public void showToast(String toast) {
            ViewUtil.displayToast(mContext, toast);
        }
    }
}
