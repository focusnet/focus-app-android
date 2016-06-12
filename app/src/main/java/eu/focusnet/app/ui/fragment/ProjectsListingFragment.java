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

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.R;
import eu.focusnet.app.model.internal.AppContentInstance;
import eu.focusnet.app.model.internal.ProjectInstance;
import eu.focusnet.app.model.json.Bookmark;
import eu.focusnet.app.model.json.UserPreferences;
import eu.focusnet.app.service.DataManager;
import eu.focusnet.app.ui.activity.ProjectActivity;
import eu.focusnet.app.ui.adapter.NavigationListAdapter;
import eu.focusnet.app.ui.common.EmptyListItem;
import eu.focusnet.app.ui.common.FeaturedListItem;
import eu.focusnet.app.ui.common.SimpleListItem;
import eu.focusnet.app.ui.util.Constant;
import eu.focusnet.app.ui.util.UiHelper;


/**
 * This fragment is the start point of the application
 * after the user logged in.
 */
public class ProjectsListingFragment extends ListFragment
{

	private ArrayList<SimpleListItem> listItems;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View viewRoot = inflater.inflate(R.layout.list_fragment, container, false);
		new ProjectsListingBuilderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		return viewRoot;
	}


	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		//Test where the user has clicked and navigate to this project or notifications
		if (l.getAdapter().getItemViewType(position) != NavigationListAdapter.LIST_TYPE_HEADER) {
			/* if (position > notifHeaderPosition) {
				//TODO navigate to notifications ...
			}

			else {*/
			Intent intent = new Intent(getActivity(), ProjectActivity.class);
			FeaturedListItem selectedItem = (FeaturedListItem) listItems.get(position);
			intent.putExtra(Constant.UI_EXTRA_PROJECT_PATH, selectedItem.getPath());
			intent.putExtra(Constant.UI_EXTRA_TITLE, selectedItem.getTitle());
			startActivity(intent);
			// }
		}
	}


	/**
	 * This class loads all projects from the database belonging to the logged user and displays
	 * the project title and a small description of the project
	 */
	private class ProjectsListingBuilderTask extends AsyncTask<Void, Void, NavigationListAdapter>
	{

		@Override
		protected NavigationListAdapter doInBackground(Void... voids)
		{
			listItems = new ArrayList<>();
			SimpleListItem headerProjectsListItem = new SimpleListItem(UiHelper.getBitmap(getActivity(), R.drawable.ic_category_project_negative),
					getString(R.string.focus_header_project));

			listItems.add(headerProjectsListItem);
			DataManager dm = FocusApplication.getInstance().getDataManager();
			UserPreferences preference = dm.getUserPreferences();

			LinkedHashMap<String, ProjectInstance> projects = dm.getAppContentInstance().getProjects();
			Bitmap rightIconIfNotActive = UiHelper.getBitmap(getActivity(), R.drawable.ic_bookmark_not_selected);
			Bitmap rightIconIfActive = UiHelper.getBitmap(getActivity(), R.drawable.ic_bookmark_selected);

			if (!projects.isEmpty()) {
				for (Map.Entry<String, ProjectInstance> entry : projects.entrySet()) {
					ProjectInstance p = entry.getValue();

					// FIXME for now simply ignore the special __welcome__ project that may contain tool-pages.
					if (p.getGuid().equals(ProjectInstance.WELCOME_PROJECT_IDENTIFIER)) {
						continue;
					}

					String projectPath = AppContentInstance.buildPath(p);
					String projectTitle = p.getTitle();
					String projectDesc = p.getDescription();

					String bookmarkLinkType = Bookmark.BOOKMARK_LINK_TYPE.PAGE.toString(); // useless
					// FIXME bug: displayed title is not the title of the bookmark but the title of the original project
					boolean checkedBookmark = (preference != null) && (-1 != preference.findBookmarkLinkInSpecificSet(projectPath, projectTitle, Bookmark.BOOKMARK_LINK_TYPE.PAGE.toString()));

					FeaturedListItem drawListItem = new FeaturedListItem(
							projectPath,
							UiHelper.getBitmap(getActivity(), R.drawable.ic_project),
							projectTitle,
							projectDesc,
							checkedBookmark ? rightIconIfActive : rightIconIfNotActive,
							checkedBookmark,
							bookmarkLinkType
					);
					listItems.add(drawListItem);
				}
			}
			else {
				listItems.add(new EmptyListItem());
			}

			// FIXME TOOLS of __welcome__ should be here.; for now not implemented.

			/*
			FIXME TODO notifications
			*/
			/*
			AbstractListItem headerNotificationListItem = new HeaderListItem(UiHelper.getBitm(ap(getActivity(), R.drawable.ic_notification),
					getString(R.string.focus_header_notification),
					null);
			listItems.add(headerNotificationListItem);

			notifHeaderPosition = listItems.size() - 1;

			notificationTitels = getResources().getStringArray(R.array.focus_notification_items);
			// load icons
			notificationIcons = getResources().obtainTypedArray(R.array.focus_notification_icons);

			for (int i = 0; i < notificationTitels.length; i++) {
				String notifTitle = notificationTitels[i];
				//TODO set correct path (for now the title is set as the path)
				FeaturedListItem drawListItem = new FeaturedListItem(notifTitle, UiHelper.getBitmap(getActivity(), notificationIcons.getResourceId(i, -1)), notifTitle, "N/A");
				listItems.add(drawListItem);
			}

*/

			return new NavigationListAdapter(getActivity(), listItems);
		}

		@Override
		protected void onPostExecute(NavigationListAdapter navigationListAdapter)
		{
			setListAdapter(navigationListAdapter);
		}
	}

}
