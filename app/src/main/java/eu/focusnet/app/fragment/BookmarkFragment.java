package eu.focusnet.app.fragment;

import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import eu.focusnet.app.adapter.StandardListAdapter;
import eu.focusnet.app.common.AbstractListItem;
import eu.focusnet.app.db.DatabaseAdapter;
import eu.focusnet.app.db.PreferenceDao;
import eu.focusnet.app.manager.PreferenceManager;
import eu.focusnet.app.model.data.Bookmark;
import eu.focusnet.app.model.data.BookmarkLink;
import eu.focusnet.app.model.data.Preference;
import eu.focusnet.app.model.ui.HeaderListItem;
import eu.focusnet.app.model.ui.StandardListItem;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.GuiUtil;
import eu.focusnet.app.activity.R;
import eu.focusnet.app.util.NavigationUtil;

import static eu.focusnet.app.util.NavigationUtil.PathType.PROJECTID;
import static eu.focusnet.app.util.NavigationUtil.PathType.PROJECTID_BRACKETS_PAGEID;


/**
 * Created by admin on 15.06.2015.
 */
public class BookmarkFragment extends ListFragment {

    private ArrayList<AbstractListItem> abstractItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View viewRoot = inflater.inflate(R.layout.list_fragment, container, false);
        new BookmarkBuilderTask().execute();
        return viewRoot;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (l.getAdapter().getItemViewType(position) != HeaderListItem.TYPE_HEADER) {

            Intent intent = null;
            StandardListItem selectedItem = (StandardListItem) abstractItems.get(position);

            String path = selectedItem.getPath();
            switch(NavigationUtil.checkPathType(path)){
                case PROJECTID:
                    intent = new Intent("eu.focusnet.app.activity.ProjectActivity");
                    break;
                case PROJECTID_PAGEID:
                    intent = new Intent("eu.focusnet.app.activity.PageActivity");
                    break;
                case PROJECTID_BRACKETS:
                    //TODO
                    break;
                default:
                //TODO PROJECTID_BRACKETS_PAGEID
            }

            intent.putExtra(Constant.PATH, selectedItem.getPath());
            intent.putExtra(Constant.TITLE, selectedItem.getTitle());
            startActivity(intent);
        }
    }

    private class BookmarkBuilderTask extends AsyncTask<Void, Void, StandardListAdapter> {

        @Override
        protected StandardListAdapter doInBackground(Void... voids) {
            DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());
            StandardListAdapter adapter = null;
            try {

                databaseAdapter.openWritableDatabase();
                PreferenceManager preferenceManager = new PreferenceManager(databaseAdapter.getDb());
                //TODO get the preference's ID
                Preference preference = preferenceManager.findPreference(new Long(123));

                Bookmark bookmark = preference.getBookmarks();
                ArrayList<BookmarkLink> pages = bookmark.getPages();
                ArrayList<BookmarkLink> tools = bookmark.getTools();

                abstractItems = new ArrayList<AbstractListItem>();
                AbstractListItem headerProjectsListItem = new HeaderListItem(GuiUtil.getBitmap(getActivity(), R.drawable.ic_file),
                        getResources().getString(R.string.bookmark_header_dashboard), GuiUtil.getBitmap(getActivity(), R.drawable.ic_filter));
                abstractItems.add(headerProjectsListItem);

                for (BookmarkLink bl : pages) {
                    StandardListItem drawListItem = new StandardListItem(bl.getPath(), GuiUtil.getBitmap(getActivity(), R.drawable.ic_chevron_right),
                            bl.getName(), bl.getPath());
                    abstractItems.add(drawListItem);
                }

                AbstractListItem headerToolListItem = new HeaderListItem(GuiUtil.getBitmap(getActivity(), R.drawable.ic_settings),
                        getString(R.string.bookmark_header_tool), GuiUtil.getBitmap(getActivity(), R.drawable.ic_filter));
                abstractItems.add(headerToolListItem);


                for (BookmarkLink bl : tools) {
                    StandardListItem drawListItem = new StandardListItem(bl.getPath(), GuiUtil.getBitmap(getActivity(), R.drawable.ic_clock_o),
                            bl.getName(), bl.getPath());
                    abstractItems.add(drawListItem);
                }

                adapter = new StandardListAdapter(getActivity(), abstractItems);

            } finally {
                databaseAdapter.close();
            }

            return adapter;
        }

        @Override
        protected void onPostExecute(StandardListAdapter adapter) {
            setListAdapter(adapter);
        }
    }

}

