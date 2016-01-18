package eu.focusnet.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.focusnet.app.R;
import eu.focusnet.app.common.WidgetFragment;

/**
 * Created by admin on 18.01.2016.
 */
public class TextWidgetFragment extends WidgetFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View viewRoot;
        viewRoot = inflater.inflate(R.layout.fragment_text, container, false);

        return viewRoot;
    }
}
