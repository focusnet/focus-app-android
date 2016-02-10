package com.example.webviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class WebViewDemoActivity extends AppCompatActivity implements WebViewDataFragment.DataSenderCallback {

	private WebViewFragment webViewFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		webViewFragment = (WebViewFragment) getFragmentManager().findFragmentById(R.id.web_view_fragment);
	}

	@Override
	public void loadView(String assetName, String context) {
		webViewFragment.loadWebView(assetName, context);
	}
}
