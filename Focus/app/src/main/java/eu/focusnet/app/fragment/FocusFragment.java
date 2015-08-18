package eu.focusnet.app.fragment;

import android.app.ListFragment;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import eu.focusnet.app.adapter.StandardListAdapter;
import eu.focusnet.app.common.AbstractListItem;
import eu.focusnet.app.db.DatabaseAdapter;
import eu.focusnet.app.db.ProjectDao;
import eu.focusnet.app.model.data.Project;
import eu.focusnet.app.model.ui.HeaderListItem;
import eu.focusnet.app.model.ui.StandardListItem;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.Util;
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
        if(l.getAdapter().getItemViewType(position) != HeaderListItem.TYPE_HEADER) {
            if(position > notifHeaderPosition){
                //TODO navigate to notifications ...
            }
            else{
                Intent intent = new Intent("eu.focusnet.app.activity.ProjectActivity");
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                StandardListItem selectedItem = (StandardListItem) abstractItems.get(position);
                intent.putExtra(Constant.PROJECT_ID, selectedItem.getId());
                intent.putExtra(Constant.PROJECT_NAME, selectedItem.getTitle());
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
            AbstractListItem headerProjectsListItem = new HeaderListItem(Util.getBitmap(getActivity(), R.drawable.ic_file),
                    getString(R.string.focus_header_project),
                    Util.getBitmap(getActivity(), R.drawable.ic_filter));

            abstractItems.add(headerProjectsListItem);

            DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());
            databaseAdapter.openWritableDatabase();
            ProjectDao projectDao = new ProjectDao(databaseAdapter.getDb());
            ArrayList<Project> projects = projectDao.findAllProjects();
            databaseAdapter.close();
            for(Project p : projects){
                StandardListItem drawListItem = new StandardListItem(p.getGuid(), Util.getBitmap(getActivity(), projectIcons.getResourceId(0, -1)), p.getTitle(), p.getDescription(), Util.getBitmap(getActivity(), R.drawable.ic_star));
                abstractItems.add(drawListItem);
            }

            AbstractListItem headerNotificationListItem = new HeaderListItem(Util.getBitmap(getActivity(), R.drawable.ic_notification),
                    getString(R.string.focus_header_notification),
                    Util.getBitmap(getActivity(),  R.drawable.ic_filter));
            abstractItems.add(headerNotificationListItem);

            notifHeaderPosition = abstractItems.size() - 1;

            notificationTitels = getResources().getStringArray(R.array.focus_notification_items);
            // load icons
            notificationIcons = getResources().obtainTypedArray(R.array.focus_notification_icons);

            for(int i = 0; i < notificationTitels.length; i++){
                String notifTitle = notificationTitels[i];
                StandardListItem drawListItem = new StandardListItem(Util.getBitmap(getActivity(), notificationIcons.getResourceId(i, -1)), notifTitle, "Info notifications", null);
                abstractItems.add(drawListItem);
            }

            // Recycle the typed array
            projectIcons.recycle();
            notificationIcons.recycle();

            return new StandardListAdapter(getActivity(), abstractItems);
        }

        @Override
        protected void onPostExecute(StandardListAdapter standardListAdapter) {
            setListAdapter(standardListAdapter);
        }
    }

}
