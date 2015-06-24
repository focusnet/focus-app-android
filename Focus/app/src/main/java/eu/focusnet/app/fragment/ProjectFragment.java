package eu.focusnet.app.fragment;

import android.app.Fragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import eu.focusnet.app.adapter.StandarListAdapter;
import eu.focusnet.app.common.AbstractListItem;
import eu.focusnet.app.model.ui.HeaderListItem;
import eu.focusnet.app.model.ui.StandardListItem;
import eu.focusnet.app.util.Util;
import eu.focusnet.app.activity.R;


/**
 * Created by admin on 15.06.2015.
 */
public class ProjectFragment extends Fragment {

    private String[] projectTitels;
    private TypedArray projectIcons;
    private ListView projectList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View viewRoot = inflater.inflate(R.layout.fragment_project, container, false);

        projectList = (ListView)viewRoot.findViewById(R.id.notification_list);

        projectTitels = getResources().getStringArray(R.array.drawer_items);

        // load menu icons
        projectIcons = getResources().obtainTypedArray(R.array.drawer_icons);

        ArrayList<AbstractListItem> abstractItems = new ArrayList<AbstractListItem>();
        AbstractListItem headerProjectsListItem = new HeaderListItem(Util.getBitmap(getActivity(), projectIcons.getResourceId(0, -1)),
                                                            "My Projects",
                                                             Util.getBitmap(getActivity(), projectIcons.getResourceId(0, -1)));

        abstractItems.add(headerProjectsListItem);

        for(int i = 0; i < projectTitels.length; i++){
            String notifTitle = projectTitels[i];
            StandardListItem drawListItem = new StandardListItem(Util.getBitmap(getActivity(), projectIcons.getResourceId(i, -1)), notifTitle, "Info", Util.getBitmap(getActivity(), projectIcons.getResourceId(i, -1)));
            abstractItems.add(drawListItem);
        }

        AbstractListItem headerNotificationListItem = new HeaderListItem(Util.getBitmap(getActivity(), projectIcons.getResourceId(0, -1)),
                "New notifications",
                Util.getBitmap(getActivity(), projectIcons.getResourceId(0, -1)));
        abstractItems.add(headerNotificationListItem);

        for(int i = 0; i < projectTitels.length; i++){
            String notifTitle = projectTitels[i];
            StandardListItem drawListItem = new StandardListItem(Util.getBitmap(getActivity(), projectIcons.getResourceId(i, -1)), notifTitle, "Info", null);
            abstractItems.add(drawListItem);
        }

        // Recycle the typed array
        projectIcons.recycle();

        StandarListAdapter adapter = new StandarListAdapter(getActivity(), abstractItems);
        projectList.setAdapter(adapter);
        projectList.setOnItemClickListener(new NotificationClickListener());

        return viewRoot;
    }

    /**
     * Slide menu item click listener
     * */
    private class NotificationClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // display view for selected nav drawer item
           Util.displayToast(getActivity(), "Selected position:"+position);
        }
    }
}
