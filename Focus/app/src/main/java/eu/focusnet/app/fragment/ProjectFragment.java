package eu.focusnet.app.fragment;

import android.app.ListFragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.media.CamcorderProfile;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import eu.focusnet.app.activity.R;
import eu.focusnet.app.adapter.StandardListAdapter;
import eu.focusnet.app.common.AbstractListItem;
import eu.focusnet.app.db.DatabaseAdapter;
import eu.focusnet.app.db.ProjectDao;
import eu.focusnet.app.model.data.Linker;
import eu.focusnet.app.model.data.Notification;
import eu.focusnet.app.model.data.Project;
import eu.focusnet.app.model.ui.HeaderListItem;
import eu.focusnet.app.model.ui.StandardListItem;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.Util;

/**
 * Created by admin on 29.06.2015.
 */
public class ProjectFragment extends ListFragment {

    private String pageId;
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
                Util.displayToast(getActivity(), "Notification selected");
            }
            else if(position > toolsHeaderPosition){
                Util.displayToast(getActivity(), "Tools selected");
            }
            else{
                Util.displayToast(getActivity(), "Dashboard selected");
                ImageView remember = (ImageView)v.findViewById(R.id.right_icon);
                remember.setImageBitmap(Util.getBitmap(getActivity(), R.drawable.ic_star_o));
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
            SQLiteDatabase database = databaseAdapter.getDb();
            ProjectDao projectDao = new ProjectDao(database);

            Project project = projectDao.findProject(projectId);
            databaseAdapter.close();

            ArrayList<Linker> dashboards = project.getDashboards();

            // load icons
            dashboardsIcons = getResources().obtainTypedArray(R.array.cutting_dashboard_icons);

            ArrayList<AbstractListItem> abstractItems = new ArrayList<AbstractListItem>();

            AbstractListItem headerProjectsListItem = new HeaderListItem(Util.getBitmap(getActivity(), R.drawable.ic_file),
                    getString(R.string.cutting_header_dashboard),
                    null);

            abstractItems.add(headerProjectsListItem);

            for(Linker dashboard : dashboards){
                StandardListItem drawListItem = new StandardListItem(Util.getBitmap(getActivity(), dashboardsIcons.getResourceId(0, -1)), dashboard.getPageid(), null, Util.getBitmap(getActivity(), R.drawable.ic_star));
                abstractItems.add(drawListItem);
            }

            AbstractListItem headerToolListItem = new HeaderListItem(Util.getBitmap(getActivity(), R.drawable.ic_settings),
                    getString(R.string.cutting_header_tool),
                    null);

            abstractItems.add(headerToolListItem);
            toolsHeaderPosition = abstractItems.size() -1;

            // load icons
            toolsIcons = getResources().obtainTypedArray(R.array.cutting_tool_icons);
            ArrayList<Linker> tools = project.getTools();

            for(Linker tool : tools){
                StandardListItem drawListItem = new StandardListItem(Util.getBitmap(getActivity(), toolsIcons.getResourceId(0, -1)), tool.getPageid(), null, Util.getBitmap(getActivity(), R.drawable.ic_star_o));
                abstractItems.add(drawListItem);
            }

            AbstractListItem headerNotificationListItem = new HeaderListItem(Util.getBitmap(getActivity(), R.drawable.ic_notification),
                    getString(R.string.cutting_header_notification),
                    Util.getBitmap(getActivity(),  R.drawable.ic_filter));
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
