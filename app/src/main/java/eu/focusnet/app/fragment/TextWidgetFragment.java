package eu.focusnet.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import eu.focusnet.app.R;
import eu.focusnet.app.common.WidgetFragment;
import eu.focusnet.app.manager.DataManager;
import eu.focusnet.app.model.internal.TextWidgetInstance;
import eu.focusnet.app.util.Constant;

/**
 * Created by admin on 18.01.2016.
 */
public class TextWidgetFragment extends WidgetFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View viewRoot = inflater.inflate(R.layout.fragment_text, container, false);
        setWidgetLayout(viewRoot);

        Bundle bundles = getArguments();
        String path = bundles.getString(Constant.PATH);
        TextWidgetInstance textWidgetInstance = (TextWidgetInstance) DataManager.getInstance().getAppContentInstance().getWidgetFromPath(path);

        TextView textTitle = (TextView) viewRoot.findViewById(R.id.text_title);
        textTitle.setText(textWidgetInstance.getTitle());

        TextView textContent = (TextView) viewRoot.findViewById(R.id.text_content);
        textContent.setText(textWidgetInstance.getContent());

        return viewRoot;
    }
}
