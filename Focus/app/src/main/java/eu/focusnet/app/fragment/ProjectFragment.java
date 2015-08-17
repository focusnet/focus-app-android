package eu.focusnet.app.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import eu.focusnet.app.activity.R;
import eu.focusnet.app.adapter.StandardListAdapter;
import eu.focusnet.app.common.AbstractListItem;
import eu.focusnet.app.model.ui.HeaderListItem;
import eu.focusnet.app.model.ui.StandardListItem;
import eu.focusnet.app.util.Util;

/**
 * Created by admin on 29.06.2015.
 */
public class ProjectFragment extends ListFragment {

    private String[] dashboardsTitels;
    private TypedArray dashboardsIcons;
    private String[] toolsTitels;
    private TypedArray toolsIcons;
    private String[] notificationTitels;
    private TypedArray notificationIcons;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View viewRoot = inflater.inflate(R.layout.list_fragment, container, false);

        dashboardsTitels = getResources().getStringArray(R.array.cutting_dashboard_items);
        // load icons
        dashboardsIcons = getResources().obtainTypedArray(R.array.cutting_dashboard_icons);

        ArrayList<AbstractListItem> abstractItems = new ArrayList<AbstractListItem>();
        AbstractListItem headerProjectsListItem = new HeaderListItem(Util.getBitmap(getActivity(), R.drawable.ic_file),
                getString(R.string.cutting_header_dashboard),
                null);

        abstractItems.add(headerProjectsListItem);

        for(int i = 0; i < dashboardsTitels.length; i++){
            String notifTitle = dashboardsTitels[i];
            StandardListItem drawListItem = new StandardListItem(Util.getBitmap(getActivity(), dashboardsIcons.getResourceId(i, -1)), notifTitle, "Info dashboards", Util.getBitmap(getActivity(), R.drawable.ic_star));
            abstractItems.add(drawListItem);
        }

        AbstractListItem headerToolListItem = new HeaderListItem(Util.getBitmap(getActivity(), R.drawable.ic_settings),
                getString(R.string.cutting_header_tool),
                null);
        abstractItems.add(headerToolListItem);

        toolsTitels = getResources().getStringArray(R.array.cutting_tool_items);
        // load icons
        toolsIcons = getResources().obtainTypedArray(R.array.cutting_tool_icons);

        for(int i = 0; i < toolsTitels.length; i++){
            String notifTitle = toolsTitels[i];
            StandardListItem drawListItem = new StandardListItem(Util.getBitmap(getActivity(), toolsIcons.getResourceId(i, -1)), notifTitle, "Info Tools", Util.getBitmap(getActivity(), R.drawable.ic_star_o));
            abstractItems.add(drawListItem);
        }


        AbstractListItem headerNotificationListItem = new HeaderListItem(Util.getBitmap(getActivity(), R.drawable.ic_notification),
                getString(R.string.cutting_header_notification),
                Util.getBitmap(getActivity(),  R.drawable.ic_filter));
        abstractItems.add(headerNotificationListItem);


        notificationTitels = getResources().getStringArray(R.array.cutting_notification_items);
        // load icons
        notificationIcons = getResources().obtainTypedArray(R.array.cutting_notification_icons);

        for(int i = 0; i < notificationTitels.length; i++){
            String notifTitle = notificationTitels[i];
            StandardListItem drawListItem = new StandardListItem(Util.getBitmap(getActivity(), notificationIcons.getResourceId(i, -1)), notifTitle, "Info notifications", null);
            abstractItems.add(drawListItem);
        }

        // Recycle the typed array
        dashboardsIcons.recycle();
        toolsIcons.recycle();
        notificationIcons.recycle();

        StandardListAdapter adapter = new StandardListAdapter(getActivity(), abstractItems);
        setListAdapter(adapter);
        return viewRoot;
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        activity.setTitle(this.title);
//    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Util.displayToast(getActivity(), "Selected position:"+position);
    }
}
