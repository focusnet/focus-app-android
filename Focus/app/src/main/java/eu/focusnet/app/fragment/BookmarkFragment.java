package eu.focusnet.app.fragment;

import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import eu.focusnet.app.activity.MainActivity;
import eu.focusnet.app.adapter.StandardListAdapter;
import eu.focusnet.app.common.AbstractListItem;
import eu.focusnet.app.common.FragmentInterface;
import eu.focusnet.app.db.DatabaseAdapter;
import eu.focusnet.app.db.PreferenceDAO;
import eu.focusnet.app.model.data.Bookmark;
import eu.focusnet.app.model.data.BookmarkLink;
import eu.focusnet.app.model.data.Preference;
import eu.focusnet.app.model.ui.HeaderListItem;
import eu.focusnet.app.model.ui.StandardListItem;
import eu.focusnet.app.util.Util;
import eu.focusnet.app.activity.R;


/**
 * Created by admin on 15.06.2015.
 */
public class BookmarkFragment extends ListFragment implements FragmentInterface {

    private String[] httpMethods;
    private String selectedHttpMethod;
    private CharSequence title;
    private int position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View viewRoot = inflater.inflate(R.layout.list_fragment, container, false);
        new BookmarkBuilderTask().execute();
        return viewRoot;
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


    private class BookmarkBuilderTask extends AsyncTask<Void, Void, Void> {
        private StandardListAdapter adapter;

        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseAdapter databaseAdapter = ((MainActivity)getActivity()).getDatabaseAdapter();
            databaseAdapter.open();
            PreferenceDAO preferenceDAO = new PreferenceDAO(databaseAdapter.getDb());
            Preference preference = preferenceDAO.findPreference(new Long(1));
            databaseAdapter.close();

            Bookmark bookmark = preference.getBookmarks();
            ArrayList<BookmarkLink> pages = bookmark.getPages();
            ArrayList<BookmarkLink> tools = bookmark.getTools();

            ArrayList<AbstractListItem> abstractItems = new ArrayList<AbstractListItem>();
            AbstractListItem headerProjectsListItem = new HeaderListItem(Util.getBitmap(getActivity(), R.drawable.ic_file),
                    getResources().getString(R.string.bookmark_header_dashboard), Util.getBitmap(getActivity(), R.drawable.ic_filter));
            abstractItems.add(headerProjectsListItem);

            for(BookmarkLink bl : pages){
                StandardListItem drawListItem = new StandardListItem(Util.getBitmap(getActivity(), R.drawable.ic_chevron_right), bl.getName(),
                        bl.getPath(), Util.getBitmap(getActivity(), R.drawable.ic_user_manual));    //TODO order by item_order when retrieving from db
                abstractItems.add(drawListItem);
            }


            AbstractListItem headerToolListItem = new HeaderListItem(Util.getBitmap(getActivity(), R.drawable.ic_settings),
                    getString(R.string.bookmark_header_tool), Util.getBitmap(getActivity(), R.drawable.ic_filter));
            abstractItems.add(headerToolListItem);


            for(BookmarkLink bl : tools ){
                StandardListItem drawListItem = new StandardListItem(Util.getBitmap(getActivity(), R.drawable.ic_clock_o), bl.getName(),
                        bl.getPath(),  Util.getBitmap(getActivity(), R.drawable.ic_user_manual));  //TODO order by item_order when retrieving from db
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
