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
import eu.focusnet.app.model.internal.AppContentInstance;
import eu.focusnet.app.model.json.BookmarkLink;
import eu.focusnet.app.ui.activity.PageActivity;
import eu.focusnet.app.ui.adapter.StandardListAdapter;
import eu.focusnet.app.ui.common.AbstractListItem;
import eu.focusnet.app.model.store.DatabaseAdapter;
import eu.focusnet.app.service.DataManager;
import eu.focusnet.app.model.internal.PageInstance;
import eu.focusnet.app.model.internal.ProjectInstance;
import eu.focusnet.app.ui.common.HeaderListItem;
import eu.focusnet.app.ui.common.StandardListItem;
import eu.focusnet.app.ui.util.Constant;
import eu.focusnet.app.ui.util.ViewUtil;

/**
 * This fragment will be loaded from the ProjectActivity and displays
 * the characteristics of a project
 */
public class ProjectFragment extends ListFragment
{

	private TypedArray dashboardsIcons;
	private TypedArray toolsIcons;
	private TypedArray notificationIcons;
	private int toolsHeaderPosition, notificationsHeaderPosition;
	private ArrayList<AbstractListItem> abstractItems;
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
		new ProjectBuilderTask().execute();

		return viewRoot;
	}

	@Override
	public void onDestroyView()
	{
		// useful for our custom garbage collection in DataManager
		FocusApplication.getInstance().getDataManager().unregisterActiveInstance(this.projectInstance);
	}


	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		if (l.getAdapter().getItemViewType(position) != HeaderListItem.TYPE_HEADER) {
			if (position > notificationsHeaderPosition) {
				ViewUtil.displayToast(getActivity(), "Notification selected");
			}
//            else if(position > toolsHeaderPosition){
//                GuiUtil.displayToast(getActivity(), "Tools selected");
//            }
			//User has selected either Tools or Dashboard
			else {
				// GuiUtil.displayToast(getActivity(), "Dashboard selected");
				Intent intent = new Intent(getActivity(), PageActivity.class);
				StandardListItem selectedItem = (StandardListItem) abstractItems.get(position);
				intent.putExtra(Constant.UI_EXTRA_PROJECT_PATH, projectId);
				intent.putExtra(Constant.UI_EXTRA_PAGE_PATH, selectedItem.getPath());
				intent.putExtra(Constant.UI_EXTRA_TITLE, selectedItem.getTitle());
				startActivity(intent);
			}
		}
	}

	/**
	 * This class loads the project characteristics from the database
	 *
	 * FIXME TODO is that useful to have it in async now that we do things from RAM?
	 */
	private class ProjectBuilderTask extends AsyncTask<String, Void, StandardListAdapter>
	{

		@Override
		protected StandardListAdapter doInBackground(String... params)
		{
			DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());

			// load icons
			dashboardsIcons = getResources().obtainTypedArray(R.array.cutting_dashboard_icons);

			abstractItems = new ArrayList<>();

			AbstractListItem headerProjectsListItem = new HeaderListItem(ViewUtil.getBitmap(getActivity(), R.drawable.ic_file),
					getString(R.string.cutting_header_dashboard),
					null);

			abstractItems.add(headerProjectsListItem);

			// load icons
			toolsIcons = getResources().obtainTypedArray(R.array.cutting_tool_icons);

			DataManager dm = FocusApplication.getInstance().getDataManager();
			projectInstance = dm.getAppContentInstance().getProjectFromPath(projectId);
			// FIXME FIXME TODO YANDY: check that you don't get NULL, if that's the case, go back to FocusActivity
			//Answer: Why it's the projectInstance null, then if the project is showed in the FocusActivity, it can not be null, the user
			//can select it and the information about it should be displayed or??
			// FIXME FIXME TODO YANDY this is also true for other call such as this one (for page, widget, etc.)

			// useful for our custom garbage collection in DataManager
			dm.registerActiveInstance(projectInstance);

			LinkedHashMap<String, PageInstance> dashboards = projectInstance.getDashboards();

			for (Map.Entry<String, PageInstance> entry : dashboards.entrySet()) {
				PageInstance dashboard = entry.getValue();
				String dashboardId = AppContentInstance.buildPath(projectInstance, dashboard);
				int dashboardOrder = 0;

				//TODO register the bookmarks to the dataManager
				Bitmap rightIcon = ViewUtil.getBitmap(getActivity(), R.drawable.ic_star_o);
				boolean isRightIconActive = false;
				//END

				StandardListItem drawListItem = new StandardListItem(dashboardId, ViewUtil.getBitmap(getActivity(), dashboardsIcons.getResourceId(0, -1)),
						dashboard.getTitle(), dashboard.getDescription(), dashboardOrder, rightIcon, isRightIconActive, BookmarkLink.BOOKMARK_LINK_TYPE.PAGE.toString());
				abstractItems.add(drawListItem);
			}

			AbstractListItem headerToolListItem = new HeaderListItem(ViewUtil.getBitmap(getActivity(), R.drawable.ic_tool),
					getString(R.string.cutting_header_tool),
					null);

			abstractItems.add(headerToolListItem);

			LinkedHashMap<String, PageInstance> tools = projectInstance.getTools();
			for (Map.Entry<String, PageInstance> entry : tools.entrySet()) {
				PageInstance tool = entry.getValue();
				String toolId = AppContentInstance.buildPath(projectInstance, tool);

				//TODO register the bookmarks to the dataManager
				Bitmap rightIcon = ViewUtil.getBitmap(getActivity(), R.drawable.ic_star_o);
				boolean isRightIconActive = false;
				//END

				int dashboardOrder = 0;

				StandardListItem drawListItem = new StandardListItem(toolId, ViewUtil.getBitmap(getActivity(), toolsIcons.getResourceId(0, -1)), tool.getTitle(), tool.getDescription(),
						dashboardOrder, rightIcon, isRightIconActive, BookmarkLink.BOOKMARK_LINK_TYPE.TOOL.toString());
				abstractItems.add(drawListItem);

			}

			AbstractListItem headerNotificationListItem = new HeaderListItem(ViewUtil.getBitmap(getActivity(), R.drawable.ic_notification),
					getString(R.string.cutting_header_notification),
					ViewUtil.getBitmap(getActivity(), R.drawable.ic_filter));
			abstractItems.add(headerNotificationListItem);

			notificationsHeaderPosition = abstractItems.size() - 1;

			// load icons
			notificationIcons = getResources().obtainTypedArray(R.array.cutting_notification_icons);

			//TODO
			//   ArrayList<Notification> notifications = notificationDao.findNotification(projectId) ;

//            for(Notification notification : notifications){
//                StandardListItem drawListItem = new StandardListItem(Util.getBitmap(getActivity(), notificationIcons.getResourceId(0, -1)), notification.toString(), "Info notifications", null);
//                abstractItems.register(drawListItem);
//            }

			// Recycle the typed array
			dashboardsIcons.recycle();
			toolsIcons.recycle();
			notificationIcons.recycle();


//            try {
//                databaseAdapter.openWritableDatabase();
//                SQLiteDatabase db = databaseAdapter.getDb();
//
//                BookmarkLinkDao bookmarkLinkDao = new BookmarkLinkDao(db);
//                PageDao pageDao = new PageDao(db);
//                LinkerDao linkerDao = new LinkerDao(db);
//
//                // load icons
//                dashboardsIcons = getResources().obtainTypedArray(R.array.cutting_dashboard_icons);
//
//                abstractItems = new ArrayList<>();
//
//                AbstractListItem headerProjectsListItem = new HeaderListItem(ViewUtil.getBitmap(getActivity(), R.drawable.ic_file),
//                        getString(R.string.cutting_header_dashboard),
//                        null);
//
//                abstractItems.register(headerProjectsListItem);
//
//                ArrayList<Linker> dashboards = linkerDao.findLinkers(projectId, LinkerDao.LINKER_TYPE.DASHBOARD);
//
//
//                for (Linker dashboard : dashboards) {
//                    String pageId = dashboard.getPageid();
//                    int dashboardOrder = dashboard.getOrder();
//                    PageTemplate page = pageDao.findPage(pageId);
//                    String bookmarkLinkType = BOOKMARK_LINK_TYPE.PAGE.toString();
//                    Bitmap rightIcon = ViewUtil.getBitmap(getActivity(), R.drawable.ic_star);
//                    boolean isRightIconActive = true;
//                    String path = projectId + "/" + pageId;
//                    if (bookmarkLinkDao.findBookmarkLink(path, bookmarkLinkType) == null) {
//                        rightIcon = ViewUtil.getBitmap(getActivity(), R.drawable.ic_star_o);
//                        isRightIconActive = false;
//                    }
//                    StandardListItem drawListItem = new StandardListItem(path, ViewUtil.getBitmap(getActivity(), dashboardsIcons.getResourceId(0, -1)),
//                            page.getTitle(), page.getDescription(), dashboardOrder, rightIcon, isRightIconActive, bookmarkLinkType); //TODO see this BOOKMARK_LINK_TYPE.PAGE with Julien
//                    abstractItems.register(drawListItem);
//                }
//
//                AbstractListItem headerToolListItem = new HeaderListItem(ViewUtil.getBitmap(getActivity(), R.drawable.ic_tool),
//                        getString(R.string.cutting_header_tool),
//                        null);
//
//                abstractItems.register(headerToolListItem);
//               // toolsHeaderPosition = abstractItems.size() - 1;
//
//                // load icons
//                toolsIcons = getResources().obtainTypedArray(R.array.cutting_tool_icons);
//
//                ArrayList<Linker> tools = linkerDao.findLinkers(projectId, LinkerDao.LINKER_TYPE.TOOL);
//                for (Linker tool : tools) {
//                    String pageId = tool.getPageid();
//                    int toolOrder = tool.getOrder();
//                    PageTemplate page = pageDao.findPage(pageId);
//                    String bookmarkLinkType = BOOKMARK_LINK_TYPE.TOOL.toString();
//                    Bitmap rightIcon = ViewUtil.getBitmap(getActivity(), R.drawable.ic_star);
//                    boolean isRightIconActive = true;
//                    String path = projectId + "/" + pageId;
//                    if (bookmarkLinkDao.findBookmarkLink(path, bookmarkLinkType) == null) {
//                        rightIcon = ViewUtil.getBitmap(getActivity(), R.drawable.ic_star_o);
//                        isRightIconActive = false;
//                    }
//                    StandardListItem drawListItem = new StandardListItem(path, ViewUtil.getBitmap(getActivity(), toolsIcons.getResourceId(0, -1)), page.getTitle(), page.getDescription(),
//                            toolOrder, rightIcon, isRightIconActive, BOOKMARK_LINK_TYPE.TOOL.toString());
//                    abstractItems.register(drawListItem);
//                }
//
//                AbstractListItem headerNotificationListItem = new HeaderListItem(ViewUtil.getBitmap(getActivity(), R.drawable.ic_notification),
//                        getString(R.string.cutting_header_notification),
//                        ViewUtil.getBitmap(getActivity(), R.drawable.ic_filter));
//                abstractItems.register(headerNotificationListItem);
//
//                notificationsHeaderPosition = abstractItems.size() - 1;
//
//                // load icons
//                notificationIcons = getResources().obtainTypedArray(R.array.cutting_notification_icons);

			//TODO
			//   ArrayList<Notification> notifications = notificationDao.findNotification(projectId) ;

//            for(Notification notification : notifications){
//                StandardListItem drawListItem = new StandardListItem(Util.getBitmap(getActivity(), notificationIcons.getResourceId(0, -1)), notification.toString(), "Info notifications", null);
//                abstractItems.register(drawListItem);
//            }

//                // Recycle the typed array
//                dashboardsIcons.recycle();
//                toolsIcons.recycle();
//                notificationIcons.recycle();
//            }
//            finally {
//                databaseAdapter.close();
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
