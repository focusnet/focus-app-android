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
import eu.focusnet.app.model.internal.widgets.InvalidWidgetInstance;
import eu.focusnet.app.model.internal.widgets.LineChartWidgetInstance;
import eu.focusnet.app.model.internal.widgets.PieChartWidgetInstance;
import eu.focusnet.app.model.internal.widgets.SubmitWidgetInstance;
import eu.focusnet.app.model.internal.widgets.TableWidgetInstance;
import eu.focusnet.app.model.internal.widgets.TextWidgetInstance;
import eu.focusnet.app.model.internal.widgets.WidgetInstance;
import eu.focusnet.app.service.DataManager;
import eu.focusnet.app.ui.util.Constant;
import eu.focusnet.app.ui.util.UiHelper;

/**
 */
public abstract class WidgetFragment extends Fragment
{

	private final static int UI_MARGIN_SIZE = 22;
	/**
	 * Reference height: if not 0, the widget will be set to this number of dp.
	 * This allows us to adapt the height depending on the content of the widget.
	 */
	protected int referenceHeight;
	protected View rootView;
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
		else if (wi instanceof InvalidWidgetInstance) {
			return new InvalidWidgetFragment();
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

	protected void setWidgetLayout()
	{
		Bundle arguments = getArguments();
		int width = 0; // width is determined by the weight only
		int height = arguments.getInt(Constant.UI_EXTRA_LAYOUT_HEIGHT);
		int num_of_cols = arguments.getInt(Constant.UI_EXTRA_LAYOUT_WEIGHT);
		int position_in_row = arguments.getInt(Constant.UI_EXTRA_LAYOUT_POSITION_IN_ROW);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height, num_of_cols);

		// set a reasonable margin between fragments
		int margin = UiHelper.dpToPixels(UI_MARGIN_SIZE, this.getActivity());
		params.setMargins(
				position_in_row == 0 ? margin : margin / 2,
				margin,
				position_in_row + num_of_cols == Constant.LAYOUT_NUM_OF_COLUMNS ? margin : margin / 2,
				margin
		);

		this.rootView.setLayoutParams(params);

		if (this.referenceHeight != 0) {
			ViewGroup.LayoutParams l = this.rootView.getLayoutParams();
			l.height = UiHelper.dpToPixels(this.referenceHeight, this.getActivity());
			this.rootView.setLayoutParams(l);
		}
	}

	/**
	 * This function also sets the rootView instance variable, which must be returned by onCreateView
	 *
	 * @param rootView
	 */
	protected void setupWidget(View rootView)
	{
		// assign root view
		this.rootView = rootView;

		// acquire the widget instance
		Bundle bundles = getArguments();
		String path = bundles.getString(Constant.UI_EXTRA_PATH);

		// may happen that we have no path (e.g. EmptyWidgetFragment)
		if (path != null) {
			DataManager dm = FocusApplication.getInstance().getDataManager();
			this.widgetInstance = dm.getAppContentInstance().getWidgetFromPath(path);
			dm.registerActiveInstance(this.widgetInstance);
		}

		// set margins / paddings
		// FIXME TODO
		// preferabily margin, such that we can revert webview width to 56.25vw

		// alter the reference height if necessary
		this.alterReferenceHeight();

		// and finally apply the layout properties on the root view
		this.setWidgetLayout();
	}

	/**
	 * The reference height is not altered by default. Subclasses can override this behavior.
	 */
	protected void alterReferenceHeight()
	{

	}

	public float getDisplayHeightInDp()
	{
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);

		float density = getResources().getDisplayMetrics().density;
		float dpHeight = outMetrics.heightPixels / density;
		return dpHeight;
	}

}
