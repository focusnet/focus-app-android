package eu.focusnet.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.focusnet.app.R;
import eu.focusnet.app.common.WidgetFragment;

/**
 * Created by yandypiedra on 14.01.16.
 */
public class FormWidgetFragment extends WidgetFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View viewRoot = inflater.inflate(R.layout.fragment_form, container, false);

        //TODO find widgets by id and add listeners to buttons

        return viewRoot;
    }
}
