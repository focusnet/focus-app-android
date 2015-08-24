package eu.focusnet.app.fragment;

import android.app.ListFragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import eu.focusnet.app.activity.R;
import eu.focusnet.app.adapter.StandardListAdapter;
import eu.focusnet.app.common.AbstractListItem;
import eu.focusnet.app.db.BookmarkLinkDao;
import eu.focusnet.app.db.BookmarkLinkDao.BOOKMARK_LINK_TYPE;
import eu.focusnet.app.db.DatabaseAdapter;
import eu.focusnet.app.db.ProjectDao;
import eu.focusnet.app.model.data.Linker;
import eu.focusnet.app.model.data.Notification;
import eu.focusnet.app.model.data.Project;
import eu.focusnet.app.model.ui.HeaderListItem;
import eu.focusnet.app.model.ui.StandardListItem;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.GuiUtil;
import eu.focusnet.app.util.NavigationUtil;

/**
 * Created by admin on 29.06.2015.
 */
public class ProjectFragment extends ListFragment {

    private TypedArray dashboardsIcons;
    private TypedArray toolsIcons;
    private TypedArray notificationIcons;
    private int toolsHeaderPosition, notificationsHeaderPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View viewRoot = inflater.inflate(R.layout.list_fragment, container, false);
        Bundle bundle = getArguments();
        String projectId = (String)bundle.get(Constant.PROJECT_ID);
        new ProjectBuilderTask(getActivity(), projectId).execute();

        return viewRoot;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if(l.getAdapter().getItemViewType(position) != HeaderListItem.TYPE_HEADER) {
            if(position > notificationsHeaderPosition){
                GuiUtil.displayToast(getActivity(), "Notification selected");
            }
            else if(position > toolsHeaderPosition){
                GuiUtil.displayToast(getActivity(), "Tools selected");
            }
            else{
                GuiUtil.displayToast(getActivity(), "Dashboard selected");
            }
        }
    }


    private class ProjectBuilderTask extends AsyncTask<Void, Void, StandardListAdapter> {

        private DatabaseAdapter databaseAdapter;
        private String projectId;

        public ProjectBuilderTask(Context context, String projectId){
            databaseAdapter = new DatabaseAdapter(context);
            this.projectId = projectId;
        }

        @Override
        protected StandardListAdapter doInBackground(Void... params) {

            databaseAdapter.openWritableDatabase();
            BookmarkLinkDao bookmarkLinkDao = new BookmarkLinkDao(databaseAdapter.getDb());
            ProjectDao projectDao = new ProjectDao(databaseAdapter.getDb());
            Project project = projectDao.findProject(projectId);

            ArrayList<Linker> dashboards = project.getDashboards();

            // load icons
            dashboardsIcons = getResources().obtainTypedArray(R.array.cutting_dashboard_icons);

            ArrayList<AbstractListItem> abstractItems = new ArrayList<AbstractListItem>();

            AbstractListItem headerProjectsListItem = new HeaderListItem(GuiUtil.getBitmap(getActivity(), R.drawable.ic_file),
                    getString(R.string.cutting_header_dashboard),
                    null);

            abstractItems.add(headerProjectsListItem);

            for(Linker dashboard : dashboards){
                String pageId = dashboard.getPageid();
                int dashboardOrder = dashboard.getOrder();
                String bookmarkLinkType = BOOKMARK_LINK_TYPE.PAGE.toString();
                Bitmap rightIcon = GuiUtil.getBitmap(getActivity(), R.drawable.ic_star);
                boolean isRightIconActive = true;
                String path = projectId+"/"+pageId;
                if(bookmarkLinkDao.findBookmarkLink(pageId, path, bookmarkLinkType, dashboardOrder) == null){
                    rightIcon = GuiUtil.getBitmap(getActivity(), R.drawable.ic_star_o);
                    isRightIconActive = false;
                }
                StandardListItem drawListItem = new StandardListItem(path, GuiUtil.getBitmap(getActivity(), dashboardsIcons.getResourceId(0, -1)),
                                                                     pageId, null, dashboardOrder, rightIcon, isRightIconActive, bookmarkLinkType); //TODO see this BOOKMARK_LINK_TYPE.PAGE with Julien
                abstractItems.add(drawListItem);
            }

            AbstractListItem headerToolListItem = new HeaderListItem(GuiUtil.getBitmap(getActivity(), R.drawable.ic_settings),
                    getString(R.string.cutting_header_tool),
                    null);

            abstractItems.add(headerToolListItem);
            toolsHeaderPosition = abstractItems.size() -1;

            // load icons
            toolsIcons = getResources().obtainTypedArray(R.array.cutting_tool_icons);
            ArrayList<Linker> tools = project.getTools();

            for(Linker tool : tools){
                String pageId = tool.getPageid();
                int toolOrder = tool.getOrder();
                String bookmarkLinkType = BOOKMARK_LINK_TYPE.TOOL.toString();
                Bitmap rightIcon = GuiUtil.getBitmap(getActivity(), R.drawable.ic_star);
                boolean isRightIconActive = true;

                String path = projectId+"/"+pageId;

                if(bookmarkLinkDao.findBookmarkLink(pageId, path, bookmarkLinkType, toolOrder) == null){
                    rightIcon = GuiUtil.getBitmap(getActivity(), R.drawable.ic_star_o);
                    isRightIconActive = false;
                }
                StandardListItem drawListItem = new StandardListItem(path, GuiUtil.getBitmap(getActivity(), toolsIcons.getResourceId(0, -1)), pageId, null,
                                                                     toolOrder ,rightIcon, isRightIconActive, BOOKMARK_LINK_TYPE.TOOL.toString());
                abstractItems.add(drawListItem);
            }

            databaseAdapter.close();

            AbstractListItem headerNotificationListItem = new HeaderListItem(GuiUtil.getBitmap(getActivity(), R.drawable.ic_notification),
                    getString(R.string.cutting_header_notification),
                    GuiUtil.getBitmap(getActivity(), R.drawable.ic_filter));
            abstractItems.add(headerNotificationListItem);

            notificationsHeaderPosition  = abstractItems.size() - 1;

            // load icons
            notificationIcons = getResources().obtainTypedArray(R.array.cutting_notification_icons);

            ArrayList<Notification> notifications = project.getNotifications();
            //TODO
//            for(Notification notification : notifications){
//                StandardListItem drawListItem = new StandardListItem(Util.getBitmap(getActivity(), notificationIcons.getResourceId(0, -1)), notification.toString(), "Info notifications", null);
//                abstractItems.add(drawListItem);
//            }

            // Recycle the typed array
            dashboardsIcons.recycle();
            toolsIcons.recycle();
            notificationIcons.recycle();

            return new StandardListAdapter(getActivity(), abstractItems);
        }

        @Override
        protected void onPostExecute(StandardListAdapter standardListAdapter) {
            setListAdapter(standardListAdapter);
        }
    }
}
