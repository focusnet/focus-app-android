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

import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import eu.focusnet.app.FocusAppLogic;
import eu.focusnet.app.R;
import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.internal.AppContentInstance;
import eu.focusnet.app.model.internal.PageInstance;
import eu.focusnet.app.model.internal.ProjectInstance;
import eu.focusnet.app.model.json.Bookmark;
import eu.focusnet.app.model.json.UserPreferences;
import eu.focusnet.app.ui.activity.PageActivity;
import eu.focusnet.app.ui.activity.ProjectsListingActivity;
import eu.focusnet.app.ui.adapter.NavigationListAdapter;
import eu.focusnet.app.ui.common.EmptyListItem;
import eu.focusnet.app.ui.common.FeaturedListItem;
import eu.focusnet.app.ui.common.SimpleListItem;
import eu.focusnet.app.ui.util.Constant;
import eu.focusnet.app.ui.util.UiHelper;
import eu.focusnet.app.util.ApplicationHelper;

/**
 * This fragment will be loaded from the ProjectActivity and displays
 * the characteristics of a project
 */
public class ProjectFragment extends ListFragment
{
	private ArrayList<SimpleListItem> listItems;
	private String projectId;

	private ProjectInstance projectInstance;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		View viewRoot = inflater.inflate(R.layout.list_fragment, container, false);
		Bundle bundle = getArguments();
		//Path is the same as projectId
		projectId = bundle.getString(Constant.UI_EXTRA_PROJECT_PATH);
		new ProjectBuilderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		return viewRoot;
	}

	@Override
	public void onDestroyView()
	{
		// useful for our custom garbage collection in DataManager
// FIXME useful ?????		FocusApplication.getInstance().getDataManager().unregisterActiveInstance(this.projectInstance);

		super.onDestroyView();
	}


	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		if (l.getAdapter().getItemViewType(position) == NavigationListAdapter.LIST_TYPE_LINK) {
			Intent intent = new Intent(getActivity(), PageActivity.class);
			FeaturedListItem selectedItem = (FeaturedListItem) listItems.get(position);
			intent.putExtra(Constant.UI_EXTRA_PROJECT_PATH, projectId);
			intent.putExtra(Constant.UI_EXTRA_PAGE_PATH, selectedItem.getPath());
			intent.putExtra(Constant.UI_EXTRA_TITLE, selectedItem.getTitle());
			startActivity(intent);
		}
	}

	/**
	 * This class loads the project characteristics from the database
	 */
	private class ProjectBuilderTask extends AsyncTask<String, Void, NavigationListAdapter>
	{

		// used for delaying redirection to another page to postExecute(), which runs on the
		// UI thread and therefore can display the associated toast
		private boolean projectNotFound;

		@Override
		protected NavigationListAdapter doInBackground(String... params)
		{
			this.projectNotFound = false;
			try {
				projectInstance = FocusAppLogic.getCurrentApplicationContent().getProjectFromPath(projectId);
			}
			catch (FocusMissingResourceException ex) {
				this.projectNotFound = true;
				return null;
			}

			// useful for our custom garbage collection in DataManager
			// FIXME uesless?		dm.registerActiveInstance(projectInstance);
			UserPreferences preference = FocusAppLogic.getUserManager().getUserPreferences();

			listItems = new ArrayList<>();

			SimpleListItem headerProjectsListItem = new SimpleListItem(
					UiHelper.getBitmap(getActivity(), R.drawable.ic_category_dashboard_negative),
					getString(R.string.header_dashboards)
			);
			listItems.add(headerProjectsListItem);

			LinkedHashMap<String, PageInstance> dashboards = projectInstance.getDashboards();

			Bitmap rightIconNotActive = UiHelper.getBitmap(getActivity(), R.drawable.ic_bookmark_not_selected);
			Bitmap rightIconActive = UiHelper.getBitmap(getActivity(), R.drawable.ic_bookmark_selected);

			if (!dashboards.isEmpty()) {
				for (Map.Entry<String, PageInstance> entry : dashboards.entrySet()) {
					PageInstance dashboard = entry.getValue();
					String dashboardId = AppContentInstance.buildPath(projectInstance, dashboard);

					boolean checkedBookmark = (preference != null) && (-1 != preference.findBookmarkLinkInSpecificSet(dashboardId, dashboard.getTitle(), Bookmark.BookmarkLinkType.PAGE.toString()));

					FeaturedListItem drawListItem = new FeaturedListItem(dashboardId, UiHelper.getBitmap(getActivity(), R.drawable.ic_chevron_right), dashboard.getTitle(), dashboard.getDescription(),
							checkedBookmark ? rightIconActive : rightIconNotActive, checkedBookmark, Bookmark.BookmarkLinkType.PAGE.toString());
					listItems.add(drawListItem);
				}
			}
			else {
				listItems.add(new EmptyListItem());
			}


			SimpleListItem headerToolListItem = new SimpleListItem(
					UiHelper.getBitmap(getActivity(), R.drawable.ic_category_tool_negative),
					getString(R.string.header_tools)
			);
			listItems.add(headerToolListItem);

			LinkedHashMap<String, PageInstance> tools = projectInstance.getTools();
			if (!tools.isEmpty()) {
				for (Map.Entry<String, PageInstance> entry : tools.entrySet()) {
					PageInstance tool = entry.getValue();
					String toolId = AppContentInstance.buildPath(projectInstance, tool);

					boolean checkedBookmark = (preference != null) && (-1 != preference.findBookmarkLinkInSpecificSet(toolId, tool.getTitle(), Bookmark.BookmarkLinkType.PAGE.toString()));

					FeaturedListItem drawListItem = new FeaturedListItem(toolId, UiHelper.getBitmap(getActivity(), R.drawable.ic_chevron_right), tool.getTitle(), tool.getDescription(),
							checkedBookmark ? rightIconActive : rightIconNotActive, checkedBookmark, Bookmark.BookmarkLinkType.TOOL.toString());
					listItems.add(drawListItem);
				}
			}
			else {
				listItems.add(new EmptyListItem());
			}


			return new NavigationListAdapter(getActivity(), listItems);
		}

		@Override
		protected void onPostExecute(NavigationListAdapter navigationListAdapter)
		{
			if (this.projectNotFound) {
				UiHelper.redirectTo(ProjectsListingActivity.class, ApplicationHelper.getResources().getString(R.string.project_not_found), getActivity());
			}
			else {
				setListAdapter(navigationListAdapter);
			}
		}
	}
}
