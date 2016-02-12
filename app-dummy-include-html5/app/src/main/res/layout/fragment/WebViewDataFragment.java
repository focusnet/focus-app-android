package layout.fragment;

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

import eu.focusnet.app.ui.activity.R;
import eu.focusnet.app.util.ViewFactory;

/**
 * Created by yandypiedra on 20.10.15.
 */
public class WebViewDataFragment extends Fragment {

    private DataSenderCallback mCallback;
    private boolean islifeDataLayoutCreated;

    public interface DataSenderCallback {
        void loadView(String url, String data);
        void sendLifeData(String data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View viewRoot = inflater.inflate(R.layout.fragment_webview_data, container, false);

        final EditText data = (EditText) viewRoot.findViewById(R.id.data);
        Button button = (Button) viewRoot.findViewById(R.id.loadWebViewButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.loadView("index", data.getText().toString());
                if(!islifeDataLayoutCreated) {
                    LinearLayout linearLayout = (LinearLayout) viewRoot.findViewById(R.id.data_layout);
                    TextView title = ViewFactory.createTextView(getActivity(), R.style.TitleAppearance,
                            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), "Pass some text to the index.html page after it was loaded");
                    final EditText editTextData = ViewFactory.createEditText(getActivity(), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    Button buttonSendData = ViewFactory.createButton(getActivity(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), "Send data");
                    buttonSendData.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCallback.sendLifeData(editTextData.getText().toString());
                        }
                    });
                    linearLayout.addView(ViewFactory.createEmptyView(getActivity(), LinearLayout.LayoutParams.MATCH_PARENT, 20, 1));
                    linearLayout.addView(title);
                    linearLayout.addView(editTextData);
                    linearLayout.addView(buttonSendData);
                    islifeDataLayoutCreated = true;
                }
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

