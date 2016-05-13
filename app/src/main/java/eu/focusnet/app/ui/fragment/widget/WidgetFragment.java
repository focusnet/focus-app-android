/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.ui.fragment.widget;

import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.model.internal.widgets.BarChartWidgetInstance;
import eu.focusnet.app.model.internal.widgets.CameraWidgetInstance;
import eu.focusnet.app.model.internal.widgets.ExternalAppWidgetInstance;
import eu.focusnet.app.model.internal.widgets.FormWidgetInstance;
import eu.focusnet.app.model.internal.widgets.GPSWidgetInstance;
import eu.focusnet.app.model.internal.widgets.Html5WidgetInstance;
import eu.focusnet.app.model.internal.widgets.LineChartWidgetInstance;
import eu.focusnet.app.model.internal.widgets.PieChartWidgetInstance;
import eu.focusnet.app.model.internal.widgets.SubmitWidgetInstance;
import eu.focusnet.app.model.internal.widgets.TableWidgetInstance;
import eu.focusnet.app.model.internal.widgets.TextWidgetInstance;
import eu.focusnet.app.model.internal.widgets.WidgetInstance;
import eu.focusnet.app.service.DataManager;
import eu.focusnet.app.ui.util.Constant;
import eu.focusnet.app.ui.util.UiHelpers;

/**
 * Created by yandypiedra on 13.01.16.
 */
public abstract class WidgetFragment extends Fragment
{
	/**
	 * Reference height: if not 0, the widget will be set to this number of dp.
	 * This allows us to adapt the height depending on the content of the widget.
	 */
	protected int reference_height;

	WidgetInstance widgetInstance;

	/**
	 * Get the WidgetFragment subtype depending on the input parameter
	 */
	public static WidgetFragment getWidgetFragmentByType(WidgetInstance wi)
	{
		if (wi instanceof TextWidgetInstance) {
			return new TextWidgetFragment();
		}
		else if (wi instanceof TableWidgetInstance) {
			return new TableWidgetFragment();
		}
		else if (wi instanceof PieChartWidgetInstance) {
			return new PieChartWidgetFragment();
		}
		else if (wi instanceof BarChartWidgetInstance) {
			return new BarChartWidgetFragment();
		}
		else if (wi instanceof LineChartWidgetInstance) {
			return new LineChartWidgetFragment();
		}
		else if (wi instanceof CameraWidgetInstance) {
			return new CameraWidgetFragment();
		}
		else if (wi instanceof GPSWidgetInstance) {
			return new GPSWidgetFragment();
		}
		else if (wi instanceof FormWidgetInstance) {
			return new FormWidgetFragment();
		}
		else if (wi instanceof ExternalAppWidgetInstance) {
			return new ExternalAppFragment();
		}
		else if (wi instanceof SubmitWidgetInstance) {
			return new SubmitWidgetFragment();
		}
		else if (wi instanceof Html5WidgetInstance) {
			return new Html5WidgetFragment();
		}
		return null;
	}

	@Override
	public abstract View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

	@Override
	public void onDestroyView()
	{
		FocusApplication.getInstance().getDataManager().unregisterActiveInstance(this.widgetInstance);

		super.onDestroyView();
	}

	protected void setWidgetLayout(View viewRoot)
	{
		Bundle arguments = getArguments();
		int linearLayoutWidth = arguments.getInt(Constant.UI_BUNDLE_LAYOUT_WIDTH);
		int linearLayoutHeight = arguments.getInt(Constant.UI_BUNDLE_LAYOUT_HEIGHT);
		int linearLayoutWeight = arguments.getInt(Constant.UI_BUNDLE_LAYOUT_WEIGHT);
		viewRoot.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutWidth, linearLayoutHeight, linearLayoutWeight));

		if (this.reference_height != 0) {
			ViewGroup.LayoutParams l = viewRoot.getLayoutParams();
			l.height = UiHelpers.dp_to_pixels(this.reference_height, this.getActivity());
			viewRoot.setLayoutParams(l);
		}
	}

	public WidgetInstance getWidgetInstance()
	{
		Bundle bundles = getArguments();
		String path = bundles.getString(Constant.UI_EXTRA_PATH);
		DataManager dm = FocusApplication.getInstance().getDataManager();
		this.widgetInstance = dm.getAppContentInstance().getWidgetFromPath(path);
		dm.registerActiveInstance(this.widgetInstance);
		return this.widgetInstance;
	}

	public float getDisplayHeightInDp()
	{

		Display display = getActivity().getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics ();
		display.getMetrics(outMetrics);

		float density  = getResources().getDisplayMetrics().density;
		float dpHeight = outMetrics.heightPixels / density;
		return dpHeight;
		// float dpWidth  = outMetrics.widthPixels / density;

	}

}
