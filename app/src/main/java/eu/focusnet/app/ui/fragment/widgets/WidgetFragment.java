package eu.focusnet.app.ui.fragment.widgets;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import eu.focusnet.app.service.DataManager;
import eu.focusnet.app.model.internal.widgets.WidgetInstance;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.ui.util.ViewUtil;

/**
 * Created by yandypiedra on 13.01.16.
 */
public abstract class WidgetFragment extends Fragment
{

	//TODO

	@Override
	public abstract View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

	public void setWidgetLayout(View viewRoot)
	{
		Bundle arguments = getArguments();
		int linearLayoutWidth = arguments.getInt(ViewUtil.LAYOUT_WIDTH);
		int linearLayoutHeight = arguments.getInt(ViewUtil.LAYOUT_HEIGHT);
		int linearLayoutWeight = arguments.getInt(ViewUtil.LAYOUT_WEIGHT);
		viewRoot.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutWidth, linearLayoutHeight, linearLayoutWeight));
	}

	public WidgetInstance getWidgetInstance()
	{
		Bundle bundles = getArguments();
		String path = bundles.getString(Constant.PATH);
		return DataManager.getInstance().getAppContentInstance().getWidgetFromPath(path);
	}

}
