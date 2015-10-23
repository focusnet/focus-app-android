package eu.focusnet.app.fragment;

import android.app.ListFragment;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import eu.focusnet.app.activity.WebViewTestActivity;
import eu.focusnet.app.adapter.StandardListAdapter;
import eu.focusnet.app.common.AbstractListItem;
import eu.focusnet.app.db.BookmarkLinkDao.BOOKMARK_LINK_TYPE;
import eu.focusnet.app.db.DatabaseAdapter;
import eu.focusnet.app.manager.BookmarkLinkManager;
import eu.focusnet.app.manager.ProjectManager;
import eu.focusnet.app.model.data.Project;
import eu.focusnet.app.model.ui.HeaderListItem;
import eu.focusnet.app.model.ui.StandardListItem;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.ViewUtil;
import eu.focusnet.app.activity.R;


/**
 * Created by admin on 15.06.2015.
 */
public class FocusFragment extends ListFragment {

    private TypedArray projectIcons;
    private String[] notificationTitels;
    private TypedArray notificationIcons;
    private int notifHeaderPosition;
    private ArrayList<AbstractListItem> abstractItems;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View viewRoot = inflater.inflate(R.layout.list_fragment, container, false);
        new FocusBuilderTask().execute();
        return viewRoot;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if(l.getAdapter().getItemViewType(position) != HeaderListItem.TYPE_HEADER){
            if(position > notifHeaderPosition){
            //TODO navigate to notifications ...

                Intent intent = new Intent(getActivity(), WebViewTestActivity.class);
                if(position - 1 == notifHeaderPosition) {
                     intent = new Intent("eu.focusnet.app.activity.TestActivity");
                }
                startActivity(intent);
            }
            else{
                Intent intent = new Intent("eu.focusnet.app.activity.ProjectActivity");
                //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                StandardListItem selectedItem = (StandardListItem) abstractItems.get(position);
                intent.putExtra(Constant.PATH, selectedItem.getPath());
                intent.putExtra(Constant.TITLE, selectedItem.getTitle());
                startActivity(intent);
            }
        }
    }


    private class FocusBuilderTask extends AsyncTask<Void, Void, StandardListAdapter> {

        @Override
        protected StandardListAdapter doInBackground(Void... voids) {
            // load icons
            projectIcons = getResources().obtainTypedArray(R.array.focus_project_icons);

            abstractItems = new ArrayList<AbstractListItem>();
            AbstractListItem headerProjectsListItem = new HeaderListItem(ViewUtil.getBitmap(getActivity(), R.drawable.ic_file),
                    getString(R.string.focus_header_project),
                    ViewUtil.getBitmap(getActivity(), R.drawable.ic_filter));

            abstractItems.add(headerProjectsListItem);

            DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());

            try {
                databaseAdapter.openWritableDatabase();
                SQLiteDatabase db = databaseAdapter.getDb();
                ProjectManager projectManager = new ProjectManager(db);
                ArrayList<Project> projects = projectManager.getAllProjects();
                BookmarkLinkManager bookmarkLinkManager = new BookmarkLinkManager(db);
                for (Project p : projects) {
                    String projectId = p.getGuid();
                    String projectTitle = p.getTitle();
                    int projectOrder = p.getOrder();
                    String bookmarkLinkType = BOOKMARK_LINK_TYPE.PAGE.toString();
                    Bitmap rightIcon = ViewUtil.getBitmap(getActivity(), R.drawable.ic_star);
                    boolean isRightIconActive = true;
                    if (bookmarkLinkManager.findBookmarkLink(projectId, bookmarkLinkType) == null) {
                        rightIcon = ViewUtil.getBitmap(getActivity(), R.drawable.ic_star_o);
                        isRightIconActive = false;
                    }
                    //The project Id is the same as the path
                    StandardListItem drawListItem = new StandardListItem(projectId, ViewUtil.getBitmap(getActivity(), projectIcons.getResourceId(0, -1)), projectTitle, p.getDescription(),
                            projectOrder, rightIcon, isRightIconActive, bookmarkLinkType); //TODO see this BOOKMARK_LINK_TYPE.PAGE with Julien
                    abstractItems.add(drawListItem);
                }

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

            }
            finally {
                databaseAdapter.close();
            }

            return new StandardListAdapter(getActivity(), abstractItems);
        }

        @Override
        protected void onPostExecute(StandardListAdapter standardListAdapter) {
            setListAdapter(standardListAdapter);
        }
    }

}
