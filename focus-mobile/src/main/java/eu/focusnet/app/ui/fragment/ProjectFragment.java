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

import eu.focusnet.app.controller.FocusAppLogic;
import eu.focusnet.app.R;
import eu.focusnet.app.model.PageInstance;
import eu.focusnet.app.model.ProjectInstance;
import eu.focusnet.app.model.UserPreferencesInstance;
import eu.focusnet.app.model.gson.Bookmark;
import eu.focusnet.app.ui.activity.PageActivity;
import eu.focusnet.app.ui.activity.ProjectInProjectActivity;
import eu.focusnet.app.ui.activity.ProjectsListingActivity;
import eu.focusnet.app.ui.common.FeaturedListItem;
import eu.focusnet.app.ui.common.NavigationListAdapter;
import eu.focusnet.app.ui.common.SimpleListItem;
import eu.focusnet.app.ui.common.UiHelper;
import eu.focusnet.app.util.ApplicationHelper;
import eu.focusnet.app.util.Constant;

/**
 * This fragment will be loaded from {@link eu.focusnet.app.ui.activity.ProjectActivity}
 * or {@link ProjectInProjectActivity} and displays the different dashboards, tools and
 * inner projects of {@link ProjectInstance}.
 */
public class ProjectFragment extends ListFragment
{
	/**
	 * List of items to render in the {@code ListView}
	 */
	private ArrayList<SimpleListItem> listItems;

	/**
	 * The path of the currently displayed {@link ProjectInstance}
	 */
	private String path;

	/**
	 * Create the view and load items via a background task.
	 *
	 * @param inflater           Inherited
	 * @param container          Inherited
	 * @param savedInstanceState Inherited
	 * @return The new View
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		View viewRoot = inflater.inflate(R.layout.list_fragment, container, false);
		Bundle bundle = getArguments();
		path = bundle.getString(Constant.Extra.UI_EXTRA_PATH);
		new ProjectBuilderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		return viewRoot;
	}


	/**
	 * Click listener for when we click an item. We redirect to a new {@code Activity}. Its type
	 * depends on the target of the item link ({@link PageActivity}
	 * or {@link ProjectInProjectActivity}).
	 *
	 * @param l        Inherited
	 * @param v        Inherited
	 * @param position Position of the item being clicked
	 * @param id       Inherited
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		if (l.getAdapter().getItemViewType(position) == Constant.Navigation.LIST_TYPE_LINK) {
			// same as in bookmark fragment, modularize
			Intent intent;
			FeaturedListItem selectedItem = (FeaturedListItem) listItems.get(position);

			String path = selectedItem.getPath();
			String[] parts = path.split(Constant.Navigation.PATH_SEPARATOR_PATTERN);
			String category = parts[parts.length - 2];
			intent = new Intent(
					getActivity(),
					(category.equals(PageInstance.PageType.TOOL.toString()) || category.equals(PageInstance.PageType.DASHBOARD.toString()))
							? PageActivity.class : ProjectInProjectActivity.class
			);
			intent.putExtra(Constant.Extra.UI_EXTRA_PATH, path);
			intent.putExtra(Constant.Extra.UI_EXTRA_TITLE, selectedItem.getTitle());
			startActivity(intent);
			// FIXME not finish()?
		}
	}

	/**
	 * This task loads the list items for the {@link ProjectInstance} being represented in the
	 * current {@code Activity}.
	 */
	private class ProjectBuilderTask extends AsyncTask<String, Void, NavigationListAdapter>
	{

		/**
		 * Tells whether the {@link ProjectInstance} has been found or not.
		 * 
		 * Used for delaying redirection to another page to postExecute(), which runs on the
		 * UI thread and therefore can display the associated toast
		 */
		private boolean projectNotFound;

		/**
		 * Fetch the items and put them in a new {@link NavigationListAdapter}.
		 *
		 * @param ignored Nothing
		 * @return A new {@link NavigationListAdapter}
		 */
		@Override
		protected NavigationListAdapter doInBackground(String... ignored)
		{
			this.projectNotFound = false;
			ProjectInstance projectInstance = (ProjectInstance) FocusAppLogic.getCurrentApplicationContent().lookupByPath(path);
			if (projectInstance == null) {
				this.projectNotFound = true;
				return null;
			}

			UserPreferencesInstance preference = FocusAppLogic.getUserManager().getUserPreferences();

			listItems = new ArrayList<>();

			// FIXME TODO modularize
			ArrayList<PageInstance> dashboards = projectInstance.getDashboards();

			Bitmap rightIconNotActive = UiHelper.getBitmap(getActivity(), R.drawable.ic_bookmark_not_selected);
			Bitmap rightIconActive = UiHelper.getBitmap(getActivity(), R.drawable.ic_bookmark_selected);

			if (!dashboards.isEmpty()) {
				SimpleListItem headerDashboardsListItem = new SimpleListItem(
						UiHelper.getBitmap(getActivity(), R.drawable.ic_category_dashboard_negative),
						getString(R.string.header_dashboards)
				);
				listItems.add(headerDashboardsListItem);

				for (PageInstance dashboard : dashboards) {
					String path = dashboard.getPath();
					boolean disabled = dashboard.isDisabled();

					boolean checkedBookmark = (preference != null) && (-1 != preference.findBookmarkLinkInSpecificSet(path, dashboard.getTitle(), Bookmark.BookmarkLinkType.PAGE.toString()));

					FeaturedListItem drawListItem = new FeaturedListItem(
							path,
							UiHelper.getBitmap(getActivity(),
									R.drawable.ic_chevron_right),
							dashboard.getTitle(),
							dashboard.getDescription(),
							checkedBookmark ? rightIconActive : rightIconNotActive,
							checkedBookmark,
							Bookmark.BookmarkLinkType.PAGE.toString(),
							disabled
					);
					listItems.add(drawListItem);
				}
			}

			ArrayList<PageInstance> tools = projectInstance.getTools();
			if (!tools.isEmpty()) {
				SimpleListItem headerToolListItem = new SimpleListItem(
						UiHelper.getBitmap(getActivity(), R.drawable.ic_category_tool_negative),
						getString(R.string.header_tools)
				);
				listItems.add(headerToolListItem);

				for (PageInstance tool : tools) {
					String path = tool.getPath();
					boolean disabled = tool.isDisabled();

					boolean checkedBookmark = (preference != null) && (-1 != preference.findBookmarkLinkInSpecificSet(path, tool.getTitle(), Bookmark.BookmarkLinkType.PAGE.toString()));

					FeaturedListItem drawListItem = new FeaturedListItem(
							path,
							UiHelper.getBitmap(getActivity(),
									R.drawable.ic_chevron_right),
							tool.getTitle(),
							tool.getDescription(),
							checkedBookmark ? rightIconActive : rightIconNotActive,
							checkedBookmark,
							Bookmark.BookmarkLinkType.TOOL.toString(),
							disabled);
					listItems.add(drawListItem);
				}
			}

			ArrayList<ProjectInstance> projects = projectInstance.getProjects();
			if (!projects.isEmpty()) {
				SimpleListItem headerProjectsListItem = new SimpleListItem(UiHelper.getBitmap(getActivity(), R.drawable.ic_category_project_negative),
						getString(R.string.focus_related_projects));

				listItems.add(headerProjectsListItem);

				Bitmap rightIconIfNotActive = UiHelper.getBitmap(getActivity(), R.drawable.ic_bookmark_not_selected);
				Bitmap rightIconIfActive = UiHelper.getBitmap(getActivity(), R.drawable.ic_bookmark_selected);

				for (ProjectInstance p : projects) {

					String projectPath = p.getPath();
					String projectTitle = p.getTitle();
					String projectDesc = p.getDescription();
					boolean disabled = p.isDisabled();

					String bookmarkLinkType = Bookmark.BookmarkLinkType.PAGE.toString();
					boolean checkedBookmark = (preference != null) && (-1 != preference.findBookmarkLinkInSpecificSet(projectPath, projectTitle, Bookmark.BookmarkLinkType.PAGE.toString()));

					FeaturedListItem drawListItem = new FeaturedListItem(
							projectPath,
							UiHelper.getBitmap(getActivity(), R.drawable.ic_project),
							projectTitle,
							projectDesc,
							checkedBookmark ? rightIconIfActive : rightIconIfNotActive,
							checkedBookmark,
							bookmarkLinkType,
							disabled
					);
					listItems.add(drawListItem);
				}
			}

			return new NavigationListAdapter(getActivity(), listItems);
		}

		/**
		 * When the project could not be found, redirect to the {@link ProjectsListingActivity}.
		 * Otherwise, set the list adapter
		 *
		 * @param navigationListAdapter The list adapter to set.
		 */
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
