package eu.focusnet.app.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.focusnet.app.activity.R;


/**
 * Created by admin on 15.06.2015.
 */
public class UserManualFragment extends Fragment {

    private CharSequence title;
    private int position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View viewRoot = inflater.inflate(R.layout.fragment_usermanual, container, false);
        return viewRoot;
    }
}
