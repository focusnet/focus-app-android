package eu.focusnet.focus.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import eu.focusnet.focus.activity.BaseActivity;
import eu.focusnet.focus.activity.R;


/**
 * Created by admin on 15.06.2015.
 */
public class UserManualFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View viewRoot = inflater.inflate(R.layout.fragment_usermanual, container, false);
        return viewRoot;
    }
}
