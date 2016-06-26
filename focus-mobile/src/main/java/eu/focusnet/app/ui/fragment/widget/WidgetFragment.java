/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.ui.fragment.widget;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import eu.focusnet.app.controller.FocusAppLogic;
import eu.focusnet.app.R;
import eu.focusnet.app.model.widgets.BarChartWidgetInstance;
import eu.focusnet.app.model.widgets.CameraWidgetInstance;
import eu.focusnet.app.model.widgets.ExternalAppWidgetInstance;
import eu.focusnet.app.model.widgets.FormWidgetInstance;
import eu.focusnet.app.model.widgets.GPSWidgetInstance;
import eu.focusnet.app.model.widgets.Html5WidgetInstance;
import eu.focusnet.app.model.widgets.InvalidWidgetInstance;
import eu.focusnet.app.model.widgets.LineChartWidgetInstance;
import eu.focusnet.app.model.widgets.PieChartWidgetInstance;
import eu.focusnet.app.model.widgets.SubmitWidgetInstance;
import eu.focusnet.app.model.widgets.TableWidgetInstance;
import eu.focusnet.app.model.widgets.TextWidgetInstance;
import eu.focusnet.app.model.widgets.WidgetInstance;
import eu.focusnet.app.ui.activity.ProjectsListingActivity;
import eu.focusnet.app.ui.common.UiHelper;
import eu.focusnet.app.util.ApplicationHelper;
import eu.focusnet.app.util.Constant;

/**
 * Abstract class representing an UI widget.
 * 
 * All widgets have at least an optional title.
 */
public abstract class WidgetFragment extends Fragment
{


	/**
	 * Reference height: if not 0, the widget will be set to this number of dp.
	 * This allows us to adapt the height depending on the content of the widget.
	 */
	protected int referenceHeight;

	/**
	 * The root View
	 */
	protected View rootView;

	/**
	 * Current {@link WidgetInstance} of interest, that will be used to create
	 * this {@code Fragment}.
	 */
	WidgetInstance widgetInstance;

	/**
	 * Factory function to get the WidgetFragment subtype depending on the input parameter
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

	/**
	 * Create the view. To be overriden by children classes.
	 *
	 * @param inflater           Inherited
	 * @param container          Inherited
	 * @param savedInstanceState Inherited
	 * @return The new View
	 */
	@Override
	public abstract View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

	/**
	 * Setup the new widget:
	 * - retrieve {@link WidgetInstance} of interest
	 * - apply reference height alteration
	 * - setup the layout
	 * - set the title
	 *
	 * @param rootView The root View
	 */
	protected void setupWidget(View rootView)
	{
		// assign root view
		this.rootView = rootView;

		// acquire the widget instance
		Bundle bundles = getArguments();
		String path = bundles.getString(Constant.Extra.UI_EXTRA_PATH);

		// may happen that we have no path (e.g. EmptyWidgetFragment)
		if (path != null) {
			this.widgetInstance = (WidgetInstance) FocusAppLogic.getCurrentApplicationContent().lookupByPath(path);
			if (this.widgetInstance == null) {
				// FIXME	if a page is not valid anymore and the user wants to access it, this redirection
				// FIXME	will be triggered for all widgets contained in the page, not only one.
				// FIXME	Possible solution: detect missing page when user clicks on the page link
				UiHelper.redirectTo(ProjectsListingActivity.class, ApplicationHelper.getResources().getString(R.string.page_not_found), getActivity());
				return;
			}
		}

		// alter the reference height if necessary
		this.alterReferenceHeight();

		// apply the layout properties on the root view
		this.setWidgetLayout();

		// and finally set the title
		TextView textTitle = (TextView) this.rootView.findViewById(R.id.widget_title);
		if (textTitle != null) {
			if (this.widgetInstance == null || this.widgetInstance.getTitle() == null) {
				((ViewGroup) textTitle.getParent()).removeView(textTitle);
			}
			else {
				textTitle.setText(this.widgetInstance.getTitle());
			}
		}
	}


	/**
	 * Setup widget layout. We set width, height and margins and apply the overridden
	 * {@link #referenceHeight} if necessary.
	 * 
	 * See {@link #setupWidget(View)}.
	 */
	protected void setWidgetLayout()
	{
		Bundle arguments = getArguments();
		int width = 0; // width is determined by the weight only
		int numOfCols = arguments.getInt(Constant.Extra.UI_EXTRA_LAYOUT_WEIGHT);
		int positionInRow = arguments.getInt(Constant.Extra.UI_EXTRA_LAYOUT_POSITION_IN_ROW);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT, numOfCols);

		// set a reasonable margin between fragments
		int margin = UiHelper.dpToPixels(Constant.Ui.UI_MARGIN_SIZE_DP, this.getActivity());
		params.setMargins(
				positionInRow == 0 ? margin : margin / 2,
				margin,
				positionInRow + numOfCols == Constant.Ui.LAYOUT_NUM_OF_COLUMNS ? margin : margin / 2,
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
	 * The reference height is not altered by default. In this case WRAP_CONTENT will be used.
	 * Subclasses can override this behavior.
	 * 
	 * See {@link #setupWidget(View)} and {@link #setWidgetLayout()}.
	 */
	protected void alterReferenceHeight()
	{

	}
}
