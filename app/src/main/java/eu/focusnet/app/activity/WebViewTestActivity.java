package eu.focusnet.app.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import eu.focusnet.app.R;
import eu.focusnet.app.common.BaseActivity;
import eu.focusnet.app.fragment.WebViewDataFragment;
import eu.focusnet.app.fragment.WebViewFragment;

/**
 * This activity is only for testing the iteration between android and the webview {@link eu.focusnet.app.fragment.WebViewDataFragment}
 * {@link eu.focusnet.app.fragment.WebViewFragment}
 */
public class WebViewTestActivity extends BaseActivity implements WebViewDataFragment.DataSenderCallback {

    private WebViewFragment webViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Webview test activity");
        webViewFragment = (WebViewFragment) getFragmentManager().findFragmentById(R.id.web_view_fragment);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_web_view_test;
    }

    @Override
    public void loadView(String url, String data) {
        webViewFragment.loadWebView(url, data);
    }

    @Override
    public void sendLifeData(String data) {
        webViewFragment.setData(data);
    }
}
