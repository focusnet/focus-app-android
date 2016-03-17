/**
 *
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package eu.focusnet.app.ui.fragment;

import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.R;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.ui.activity.PageActivity;
import eu.focusnet.app.ui.activity.ProjectActivity;
import eu.focusnet.app.ui.adapter.StandardListAdapter;
import eu.focusnet.app.ui.common.AbstractListItem;
import eu.focusnet.app.model.json.Bookmark;
import eu.focusnet.app.model.json.BookmarkLink;
import eu.focusnet.app.model.json.Preference;
import eu.focusnet.app.ui.common.HeaderListItem;
import eu.focusnet.app.ui.common.StandardListItem;
import eu.focusnet.app.ui.util.Constant;
import eu.focusnet.app.service.util.EventBus;
import eu.focusnet.app.ui.util.ViewUtil;


/**
 * Created by admin on 15.06.2015.
 */
public class BookmarkFragment extends ListFragment implements EventBus.IEventListener
{

	private static final String TAG = BookmarkFragment.class.getName();
	private ArrayList<AbstractListItem> abstractItems;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View viewRoot = inflater.inflate(R.layout.list_fragment, container, false);
		return viewRoot;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		EventBus.registerIEventListener(this);
		onBookmarksUpdated();
	}

	@Override
	public void onStop()
	{
		EventBus.unregisterIEventListener(this);
		super.onStop();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		if (l.getAdapter().getItemViewType(position) != HeaderListItem.TYPE_HEADER) {

			Intent intent = null;
			StandardListItem selectedItem = (StandardListItem) abstractItems.get(position);

			String path = selectedItem.getPath();
			String[] parts = path.split("\\|");
			switch (parts.length) {
				case 1:
					intent = new Intent(getActivity(), ProjectActivity.class);
					break;
				case 3:
					intent = new Intent(getActivity(), PageActivity.class);
					break;
			}

			intent.putExtra(Constant.UI_EXTRA_PATH, selectedItem.getPath());
			intent.putExtra(Constant.UI_EXTRA_TITLE, selectedItem.getTitle());
			startActivity(intent);
		}
	}

	@Override
	public void onBookmarksUpdated()
	{
		new BookmarkBuilderTask().execute();
	}

	private class BookmarkBuilderTask extends AsyncTask<Void, Void, StandardListAdapter>
	{

		@Override
		protected StandardListAdapter doInBackground(Void... voids)
		{
//            DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());
			StandardListAdapter adapter = null;
//            try {

//                databaseAdapter.openWritableDatabase();
//                PreferenceDao preferenceDao = new PreferenceDao(databaseAdapter.getDb());
//                //TODO get the preference's ID
//                Preference preference = preferenceDao.findPreference(new Long(123));
			Preference preference = null;
			try {
				preference = FocusApplication.getInstance().getDataManager().getUserPreferences();
			}
			catch (FocusMissingResourceException ex) {
				// FIXME TODO do something smart.
			}

			Bookmark bookmark = preference.getBookmarks();
			ArrayList<BookmarkLink> pages = bookmark.getPages();
			ArrayList<BookmarkLink> tools = bookmark.getTools();

			abstractItems = new ArrayList<AbstractListItem>();
			AbstractListItem headerProjectsListItem = new HeaderListItem(ViewUtil.getBitmap(getActivity(), R.drawable.ic_file),
					getResources().getString(R.string.bookmark_header_dashboard), ViewUtil.getBitmap(getActivity(), R.drawable.ic_filter));
			abstractItems.add(headerProjectsListItem);

			for (BookmarkLink bl : pages) {
				StandardListItem drawListItem = new StandardListItem(bl.getPath(), ViewUtil.getBitmap(getActivity(), R.drawable.ic_chevron_right),
						bl.getName(), bl.getPath());
				abstractItems.add(drawListItem);
			}

			AbstractListItem headerToolListItem = new HeaderListItem(ViewUtil.getBitmap(getActivity(), R.drawable.ic_tool),
					getString(R.string.bookmark_header_tool), ViewUtil.getBitmap(getActivity(), R.drawable.ic_filter));
			abstractItems.add(headerToolListItem);


			for (BookmarkLink bl : tools) {
				StandardListItem drawListItem = new StandardListItem(bl.getPath(), ViewUtil.getBitmap(getActivity(), R.drawable.ic_clock_o),
						bl.getName(), bl.getPath());
				abstractItems.add(drawListItem);
			}

			adapter = new StandardListAdapter(getActivity(), abstractItems);

			return adapter;
		}

		@Override
		protected void onPostExecute(StandardListAdapter adapter)
		{
			setListAdapter(adapter);
		}
	}
}

