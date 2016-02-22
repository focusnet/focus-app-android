/**
 *
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
 *
 */

package eu.focusnet.app.ui.fragment;

import android.app.ListFragment;
import android.content.Intent;
import android.content.res.TypedArray;
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
import eu.focusnet.app.model.json.BookmarkLink;
import eu.focusnet.app.ui.activity.FocusActivity;
import eu.focusnet.app.ui.activity.ProjectActivity;
import eu.focusnet.app.ui.adapter.StandardListAdapter;
import eu.focusnet.app.ui.common.AbstractListItem;
import eu.focusnet.app.service.DataManager;
import eu.focusnet.app.model.internal.ProjectInstance;
import eu.focusnet.app.ui.common.HeaderListItem;
import eu.focusnet.app.ui.common.StandardListItem;
import eu.focusnet.app.ui.util.Constant;
import eu.focusnet.app.ui.util.ViewUtil;


/**
 * This fragment is the start point of the application
 * after the user logged in.
 */
public class FocusFragment extends ListFragment
{

	private TypedArray projectIcons;
	private String[] notificationTitels;
	private TypedArray notificationIcons;
	private int notifHeaderPosition;
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
			if (position > notifHeaderPosition) {
				//TODO navigate to notifications ...
		/*		Intent cronServiceIntent = new Intent(getActivity(), WebViewTestActivity.class);
				if (position - 1 == notifHeaderPosition) {
					cronServiceIntent = new Intent(getActivity(), TestActivity.class);
				}
				startActivity(cronServiceIntent);
				*/
			}
			else {
				Intent intent = new Intent(getActivity(), ProjectActivity.class);
				//cronServiceIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				StandardListItem selectedItem = (StandardListItem) abstractItems.get(position);
				intent.putExtra(Constant.UI_EXTRA_PROJECT_PATH, selectedItem.getPath());
				intent.putExtra(Constant.UI_EXTRA_TITLE, selectedItem.getTitle());
				startActivity(intent);
			}
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
			// load icons
			projectIcons = getResources().obtainTypedArray(R.array.focus_project_icons);

			abstractItems = new ArrayList<AbstractListItem>();
			AbstractListItem headerProjectsListItem = new HeaderListItem(ViewUtil.getBitmap(getActivity(), R.drawable.ic_file),
					getString(R.string.focus_header_project),
					ViewUtil.getBitmap(getActivity(), R.drawable.ic_filter));

			abstractItems.add(headerProjectsListItem);

//            DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());

//            try {
//                databaseAdapter.openWritableDatabase();
//                SQLiteDatabase db = databaseAdapter.getDb();
//                ProjectDao projectDao = new ProjectDao(db);
//                ArrayList<ProjectTemplate> projects = projectDao.findAllProjects();
//                BookmarkLinkDao bookmarkLinkManager = new BookmarkLinkDao(db);

			DataManager dm = FocusApplication.getInstance().getDataManager();
			LinkedHashMap<String, ProjectInstance> projects = dm.getAppContentInstance().getProjects();

			for (Map.Entry<String, ProjectInstance> entry : projects.entrySet()) {
				ProjectInstance p = entry.getValue();
				String projectId = dm.getAppContentInstance().buildPath(p);
				String projectTitle = p.getTitle();
				String projectDesc = p.getDescription();

				//TODO add the bookmarks to the dataManager
				int projectOrder = 0;
				String bookmarkLinkType = BookmarkLink.BOOKMARK_LINK_TYPE.PAGE.toString();
				Bitmap rightIcon = ViewUtil.getBitmap(getActivity(), R.drawable.ic_star_o);
				boolean isRightIconActive = false;

				//END///

				StandardListItem drawListItem = new StandardListItem(projectId, ViewUtil.getBitmap(getActivity(), projectIcons.getResourceId(0, -1)), projectTitle, projectDesc,
						projectOrder, rightIcon, isRightIconActive, bookmarkLinkType); //TODO see this BOOKMARK_LINK_TYPE.PAGE with Julien
				abstractItems.add(drawListItem);
			}

//                for (ProjectTemplate p : projects) {
//                    String projectId = p.getGuid();
//                    String projectTitle = p.getTitle();
//                    int projectOrder = p.getOrder();
//                    String bookmarkLinkType = BOOKMARK_LINK_TYPE.PAGE.toString();
//                    Bitmap rightIcon = ViewUtil.getBitmap(getActivity(), R.drawable.ic_star);
//                    boolean isRightIconActive = true;
//                    if (bookmarkLinkManager.findBookmarkLink(projectId, bookmarkLinkType) == null) {
//                        rightIcon = ViewUtil.getBitmap(getActivity(), R.drawable.ic_star_o);
//                        isRightIconActive = false;
//                    }
//                    //The project Id is the same as the path
//                    StandardListItem drawListItem = new StandardListItem(projectId, ViewUtil.getBitmap(getActivity(), projectIcons.getResourceId(0, -1)), projectTitle, p.getDescription(),
//                            projectOrder, rightIcon, isRightIconActive, bookmarkLinkType); //TODO see this BOOKMARK_LINK_TYPE.PAGE with Julien
//                    abstractItems.add(drawListItem);
//                }

			AbstractListItem headerNotificationListItem = new HeaderListItem(ViewUtil.getBitmap(getActivity(), R.drawable.ic_notification),
					getString(R.string.focus_header_notification),
					ViewUtil.getBitmap(getActivity(), R.drawable.ic_filter));
			abstractItems.add(headerNotificationListItem);

			notifHeaderPosition = abstractItems.size() - 1;

			notificationTitels = getResources().getStringArray(R.array.focus_notification_items);
			// load icons
			notificationIcons = getResources().obtainTypedArray(R.array.focus_notification_icons);

			for (int i = 0; i < notificationTitels.length; i++) {
				String notifTitle = notificationTitels[i];
				//TODO set correct path (for now the title is set as the path)
				StandardListItem drawListItem = new StandardListItem(notifTitle, ViewUtil.getBitmap(getActivity(), notificationIcons.getResourceId(i, -1)), notifTitle, "Info notifications");
				abstractItems.add(drawListItem);
			}

			// Recycle the typed array
			projectIcons.recycle();
			notificationIcons.recycle();

//            }
//            finally {
//              databaseAdapter.close();
//            }

			return new StandardListAdapter(getActivity(), abstractItems);
		}

		@Override
		protected void onPostExecute(StandardListAdapter standardListAdapter)
		{
			setListAdapter(standardListAdapter);
		}
	}

}
