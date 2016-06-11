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

package eu.focusnet.app.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.LinkedHashMap;
import java.util.Map;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.R;
import eu.focusnet.app.model.internal.AppContentInstance;
import eu.focusnet.app.model.internal.PageInstance;
import eu.focusnet.app.model.internal.ProjectInstance;
import eu.focusnet.app.model.internal.widgets.WidgetInstance;
import eu.focusnet.app.service.DataManager;
import eu.focusnet.app.ui.fragment.widget.EmptyWidgetFragment;
import eu.focusnet.app.ui.fragment.widget.WidgetFragment;
import eu.focusnet.app.ui.util.Constant;
import eu.focusnet.app.ui.util.FragmentManager;
import eu.focusnet.app.ui.util.UiHelper;

/**
 * This fragment will be loaded from the PageActivity and displays
 * the characteristics of a page
 */
public class PageFragment extends Fragment
{

	private PageInstance pageInstance;
	private ProjectInstance projectInstance;
	private View viewRoot;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);

		// Inflate the layout for this fragment
		this.viewRoot = inflater.inflate(R.layout.fragment_page_page, container, false);
		Bundle bundle = getArguments();
		String projectPath = (String) bundle.get(Constant.UI_EXTRA_PROJECT_PATH);
		String pagePath = (String) bundle.get(Constant.UI_EXTRA_PAGE_PATH);

		DataManager dm = FocusApplication.getInstance().getDataManager();
		AppContentInstance appContentInstance = dm.getAppContentInstance();
		this.projectInstance = appContentInstance.getProjectFromPath(projectPath);
		this.pageInstance = appContentInstance.getPageFromPath(pagePath);

		// useful for our custom garbage collection in DataManager
		dm.registerActiveInstance(this.pageInstance);


		//	ViewUtil.buildPageView(this.projectInstance, this.pageInstance, linearLayoutPageInfo, getActivity());
		this.buildPage();

		// Keyboard should disappear when the user clicks outside of it
		UiHelper.setupHidableKeyboard(this.viewRoot, this.getActivity());

		return viewRoot;
	}

	@Override
	public void onDestroyView()
	{
		// useful for our custom garbage collection in DataManager
		FocusApplication.getInstance().getDataManager().unregisterActiveInstance(this.pageInstance);

		super.onDestroyView();
	}


	/**
	 * Setup the horizontal layout containers that contain the different widgets for this page.
	 */
	private void buildPage()
	{
		LinkedHashMap<String, WidgetInstance> widgetInstances = this.pageInstance.getWidgets();

		int currentayoutId = View.generateViewId();

		LinearLayout verticalContainerLayout = (LinearLayout) this.viewRoot.findViewById(R.id.page_info);

		LinearLayout containerLayout = new LinearLayout(this.getActivity());
		containerLayout.setId(currentayoutId);
		containerLayout.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, 0, 0, 0);
		containerLayout.setLayoutParams(layoutParams);

		int spaceLeft = Constant.LAYOUT_NUM_OF_COLUMNS;

		for (Map.Entry<String, WidgetInstance> entry : widgetInstances.entrySet()) {
			WidgetInstance widgetInstance = entry.getValue();

			//Get the width of the widget (e.g. 2of4)
			int requiredSpace = widgetInstance.getNumberOfColumnsInUi();

			if (requiredSpace > spaceLeft) {

				// Fill remaining space with empty fragment
				if (spaceLeft != 0) {
					WidgetFragment emptyWidgetFragment = new EmptyWidgetFragment();
					Bundle widgetBundle = new Bundle();
					widgetBundle.putInt(Constant.UI_EXTRA_LAYOUT_HEIGHT, LinearLayout.LayoutParams.WRAP_CONTENT);
					widgetBundle.putInt(Constant.UI_EXTRA_LAYOUT_WEIGHT, spaceLeft);
					emptyWidgetFragment.setArguments(widgetBundle);
					FragmentManager.addFragment(containerLayout.getId(), emptyWidgetFragment, this.getActivity().getFragmentManager());
				}

				// commit the current line
				verticalContainerLayout.addView(containerLayout);

				// and create a new container line
				containerLayout = new LinearLayout(this.getActivity());
				containerLayout.setOrientation(LinearLayout.HORIZONTAL);
				containerLayout.setId(++currentayoutId);
				containerLayout.setLayoutParams(layoutParams);
				spaceLeft = Constant.LAYOUT_NUM_OF_COLUMNS;
			}

			// add the new widget fragment
			WidgetFragment widgetFragment = WidgetFragment.getWidgetFragmentByType(widgetInstance);
			Bundle widgetBundle = new Bundle();
			widgetBundle.putString(Constant.UI_EXTRA_PATH, AppContentInstance.buildPath(this.projectInstance, this.pageInstance, widgetInstance));
			widgetBundle.putInt(Constant.UI_EXTRA_LAYOUT_HEIGHT, LinearLayout.LayoutParams.WRAP_CONTENT);
			widgetBundle.putInt(Constant.UI_EXTRA_LAYOUT_WEIGHT, requiredSpace);
			widgetBundle.putInt(Constant.UI_EXTRA_LAYOUT_POSITION_IN_ROW, Constant.LAYOUT_NUM_OF_COLUMNS - spaceLeft);
			widgetFragment.setArguments(widgetBundle);

			FragmentManager.addFragment(containerLayout.getId(), widgetFragment, this.getActivity().getFragmentManager());

			spaceLeft -= requiredSpace;
		}

		// fill remaining space with empty fragment
		if (spaceLeft != 0) {
			WidgetFragment emptyWidgetFragment = new EmptyWidgetFragment();
			Bundle widgetBundle = new Bundle();
			widgetBundle.putInt(Constant.UI_EXTRA_LAYOUT_HEIGHT, LinearLayout.LayoutParams.WRAP_CONTENT);
			widgetBundle.putInt(Constant.UI_EXTRA_LAYOUT_WEIGHT, spaceLeft);
			emptyWidgetFragment.setArguments(widgetBundle);
			FragmentManager.addFragment(containerLayout.getId(), emptyWidgetFragment, this.getActivity().getFragmentManager());
			//	containerLayout.addView(emptyWidgetFragment.getView()); // FIXME??
		}

		verticalContainerLayout.addView(containerLayout);


	}

}
