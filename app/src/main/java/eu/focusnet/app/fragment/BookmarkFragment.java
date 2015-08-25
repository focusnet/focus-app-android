package eu.focusnet.app.fragment;

import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import eu.focusnet.app.adapter.StandardListAdapter;
import eu.focusnet.app.common.AbstractListItem;
import eu.focusnet.app.db.DatabaseAdapter;
import eu.focusnet.app.db.PreferenceDao;
import eu.focusnet.app.model.data.Bookmark;
import eu.focusnet.app.model.data.BookmarkLink;
import eu.focusnet.app.model.data.Preference;
import eu.focusnet.app.model.ui.HeaderListItem;
import eu.focusnet.app.model.ui.StandardListItem;
import eu.focusnet.app.util.GuiUtil;
import eu.focusnet.app.activity.R;


/**
 * Created by admin on 15.06.2015.
 */
public class BookmarkFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View viewRoot = inflater.inflate(R.layout.list_fragment, container, false);
        new BookmarkBuilderTask().execute();
        return viewRoot;
    }

    private class BookmarkBuilderTask extends AsyncTask<Void, Void, Void> {
        private StandardListAdapter adapter;

        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());
            databaseAdapter.openWritableDatabase();
            PreferenceDao preferenceDAO = new PreferenceDao(databaseAdapter.getDb());
            //TODO get the preference's ID
            Preference preference = preferenceDAO.findPreference(new Long(123));
            databaseAdapter.close();

            Bookmark bookmark = preference.getBookmarks();
            ArrayList<BookmarkLink> pages = bookmark.getPages();
            ArrayList<BookmarkLink> tools = bookmark.getTools();

            ArrayList<AbstractListItem> abstractItems = new ArrayList<AbstractListItem>();
            AbstractListItem headerProjectsListItem = new HeaderListItem(GuiUtil.getBitmap(getActivity(), R.drawable.ic_file),
                                                                         getResources().getString(R.string.bookmark_header_dashboard), GuiUtil.getBitmap(getActivity(), R.drawable.ic_filter));
            abstractItems.add(headerProjectsListItem);

            for(BookmarkLink bl : pages){
                //TODO set correct id (for now the name is set as id)
                StandardListItem drawListItem = new StandardListItem(bl.getName(), GuiUtil.getBitmap(getActivity(), R.drawable.ic_chevron_right),
                                                                     bl.getName(), bl.getPath());
                abstractItems.add(drawListItem);
            }

            AbstractListItem headerToolListItem = new HeaderListItem(GuiUtil.getBitmap(getActivity(), R.drawable.ic_settings),
                    getString(R.string.bookmark_header_tool), GuiUtil.getBitmap(getActivity(), R.drawable.ic_filter));
            abstractItems.add(headerToolListItem);


            for(BookmarkLink bl : tools ){
                //TODO set correct id (for now the name is set as id)
                StandardListItem drawListItem = new StandardListItem(bl.getName(), GuiUtil.getBitmap(getActivity(), R.drawable.ic_clock_o),
                                                                     bl.getName(), bl.getPath());
                abstractItems.add(drawListItem);
            }

            adapter = new StandardListAdapter(getActivity(), abstractItems);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setListAdapter(adapter);
        }
    }
}
