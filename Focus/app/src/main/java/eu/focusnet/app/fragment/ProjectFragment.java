package eu.focusnet.app.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
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
import eu.focusnet.app.common.FragmentInterface;
import eu.focusnet.app.model.ui.HeaderListItem;
import eu.focusnet.app.model.ui.StandardListItem;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.Util;
import eu.focusnet.app.activity.R;


/**
 * Created by admin on 15.06.2015.
 */
public class ProjectFragment extends ListFragment implements FragmentInterface {

    private String[] projectTitels;
    private TypedArray projectIcons;
    private String[] notificationTitels;
    private TypedArray notificationIcons;
    private CharSequence title;
    private int position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View viewRoot = inflater.inflate(R.layout.list_fragment, container, false);
        new ProjectBuilderTask().execute();
        return viewRoot;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //only for test purposes
        if(position != 0){
            FragmentInterface fragment = new CuttingFragment();
            fragment.setTitle(projectTitels[position-1]);
            Util.replaceFragment((Fragment)fragment, getActivity().getFragmentManager());
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
    }

    @Override
    public CharSequence getTitle() {
        return title;
    }

    @Override
    public void setPosition(int position){
        this.position = position;

    }
    @Override
    public int getPosition() {
        return position;
    }

    
    private class ProjectBuilderTask extends AsyncTask<Void, Void, Void> {
        private StandardListAdapter adapter;

        @Override
        protected Void doInBackground(Void... voids) {
            projectTitels = getResources().getStringArray(R.array.focus_project_items);
            // load icons
            projectIcons = getResources().obtainTypedArray(R.array.focus_project_icons);

            ArrayList<AbstractListItem> abstractItems = new ArrayList<AbstractListItem>();
            AbstractListItem headerProjectsListItem = new HeaderListItem(Util.getBitmap(getActivity(), R.drawable.ic_file),
                    getString(R.string.focus_header_project),
                    Util.getBitmap(getActivity(), R.drawable.ic_filter));

            abstractItems.add(headerProjectsListItem);

            for(int i = 0; i < projectTitels.length; i++){
                String notifTitle = projectTitels[i];
                StandardListItem drawListItem = new StandardListItem(Util.getBitmap(getActivity(), projectIcons.getResourceId(i, -1)), notifTitle, "Info projects", Util.getBitmap(getActivity(), R.drawable.ic_star));
                abstractItems.add(drawListItem);
            }

            AbstractListItem headerNotificationListItem = new HeaderListItem(Util.getBitmap(getActivity(), R.drawable.ic_notification),
                    getString(R.string.focus_header_notification),
                    Util.getBitmap(getActivity(),  R.drawable.ic_filter));
            abstractItems.add(headerNotificationListItem);

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

            adapter = new StandardListAdapter(getActivity(), abstractItems);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setListAdapter(adapter);
        }
    }

}
