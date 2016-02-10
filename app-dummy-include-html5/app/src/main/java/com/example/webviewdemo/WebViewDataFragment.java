package com.example.webviewdemo;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WebViewDataFragment extends Fragment {

	private DataSenderCallback mCallback;

	public interface DataSenderCallback {
		void loadView(String url, String context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View viewRoot = inflater.inflate(R.layout.fragment_webview_data, container, false);

		final EditText data = (EditText) viewRoot.findViewById(R.id.data);
		Button button = (Button) viewRoot.findViewById(R.id.loadWebViewButton);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCallback.loadView("dummy", data.getText().toString());
			}
		});

		return viewRoot;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (DataSenderCallback) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement DataSenderCallback");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallback = null;
	}
}

