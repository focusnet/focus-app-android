package layout.fragment;

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

import eu.focusnet.app.ui.activity.R;
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


    /**
     * Send data to the html page using a javascript function defined on it
     * @param data
     */
    public void setData(String data) {
        myWebView.loadUrl("javascript:lifeData('" + data + "')");
    }

    /**
     * Create a web client, which loads internal html pages with the Webview(pages saved in the app, the url has not a host)
     * other pages will be loaded for instance with the chrome browser
     */
    private class MyAppWebViewClient extends WebViewClient {
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
            //On page loaded call this javascript function to pass
            // data to the loaded page
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

        /*
          This method can be called in the html page
         */
        @JavascriptInterface
        public void showToast(String toast) {
            ViewUtil.displayToast(mContext, toast);
        }
    }
}
