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
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.internal.AppContentInstance;
import eu.focusnet.app.model.internal.ProjectInstance;
import eu.focusnet.app.model.json.BookmarkLink;
import eu.focusnet.app.model.json.Preference;
import eu.focusnet.app.service.DataManager;
import eu.focusnet.app.ui.activity.ProjectActivity;
import eu.focusnet.app.ui.adapter.StandardListAdapter;
import eu.focusnet.app.ui.common.AbstractListItem;
import eu.focusnet.app.ui.common.HeaderListItem;
import eu.focusnet.app.ui.common.StandardListItem;
import eu.focusnet.app.ui.util.Constant;
import eu.focusnet.app.ui.util.UiHelper;


/**
 * This fragment is the start point of the application
 * after the user logged in.
 */
public class ProjectsListingFragment extends ListFragment
{

	private ArrayList<AbstractListItem> abstractItems;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View viewRoot = inflater.inflate(R.layout.list_fragment, container, false);
		new FocusBuilderTask().execute();
		return viewRoot;
	}


	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		//Test where the user has clicked and navigate to this project or notifications
		if (l.getAdapter().getItemViewType(position) != HeaderListItem.TYPE_HEADER) {
			/* if (position > notifHeaderPosition) {
				//TODO navigate to notifications ...
			}

			else {*/
			Intent intent = new Intent(getActivity(), ProjectActivity.class);
			StandardListItem selectedItem = (StandardListItem) abstractItems.get(position);
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
	private class FocusBuilderTask extends AsyncTask<Void, Void, StandardListAdapter>
	{

		@Override
		protected StandardListAdapter doInBackground(Void... voids)
		{
			abstractItems = new ArrayList<>();
			AbstractListItem headerProjectsListItem = new HeaderListItem(UiHelper.getBitmap(getActivity(), R.drawable.picto_category_project_negative),
					getString(R.string.focus_header_project),
					null); // filter icon: ViewUtil.getBitmap(getActivity(), R.drawable.ic_filter)

			abstractItems.add(headerProjectsListItem);
			DataManager dm = FocusApplication.getInstance().getDataManager();
			Preference preference = null;
			try {
				preference = dm.getUserPreferences();
			}
			catch (FocusMissingResourceException ex) {
				// ignore? or crash? FIXME TODO
			}

			LinkedHashMap<String, ProjectInstance> projects = dm.getAppContentInstance().getProjects();
			Bitmap rightIconNotActive = UiHelper.getBitmap(getActivity(), R.drawable.picto_bookmark_not_selected);
			Bitmap rightIconActive = UiHelper.getBitmap(getActivity(), R.drawable.picto_bookmark_selected);

			for (Map.Entry<String, ProjectInstance> entry : projects.entrySet()) {
				ProjectInstance p = entry.getValue();

				// FIXME for now simply ignore the special __welcome__ project that may contain tool-pages.
				if (p.getGuid().equals(ProjectInstance.WELCOME_PROJECT_IDENTIFIER)) {
					continue;
				}

				String projectId = AppContentInstance.buildPath(p);
				String projectTitle = p.getTitle();
				String projectDesc = p.getDescription();

				String bookmarkLinkType = BookmarkLink.BOOKMARK_LINK_TYPE.PAGE.toString(); // useless
				// FIXME bug: displayed title is not the title of the bookmark but the title of the original project
				boolean checkedBookmark = (preference != null) && (-1 != preference.findBookmarkLinkInSpecificSet(projectId, projectTitle, BookmarkLink.BOOKMARK_LINK_TYPE.PAGE.toString()));

				StandardListItem drawListItem = new StandardListItem(projectId, UiHelper.getBitmap(getActivity(), R.drawable.picto_project), projectTitle, projectDesc,
						checkedBookmark ? rightIconActive : rightIconNotActive, checkedBookmark, bookmarkLinkType);
				abstractItems.add(drawListItem);
			}

			// FIXME TOOLS of __welcome__ should be here.; for now not implemented.

			/*
			FIXME TODO notifications
			*/
			/*
			AbstractListItem headerNotificationListItem = new HeaderListItem(UiHelper.getBitm(ap(getActivity(), R.drawable.ic_notification),
					getString(R.string.focus_header_notification),
					null);
			abstractItems.add(headerNotificationListItem);

			notifHeaderPosition = abstractItems.size() - 1;

			notificationTitels = getResources().getStringArray(R.array.focus_notification_items);
			// load icons
			notificationIcons = getResources().obtainTypedArray(R.array.focus_notification_icons);

			for (int i = 0; i < notificationTitels.length; i++) {
				String notifTitle = notificationTitels[i];
				//TODO set correct path (for now the title is set as the path)
				StandardListItem drawListItem = new StandardListItem(notifTitle, UiHelper.getBitmap(getActivity(), notificationIcons.getResourceId(i, -1)), notifTitle, "N/A");
				abstractItems.add(drawListItem);
			}

*/

			return new StandardListAdapter(getActivity(), abstractItems);
		}

		@Override
		protected void onPostExecute(StandardListAdapter standardListAdapter)
		{
			setListAdapter(standardListAdapter);
		}
	}

}
