/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.ui.fragment;

import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Bitmap;
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
import eu.focusnet.app.model.json.Bookmark;
import eu.focusnet.app.model.json.BookmarkLink;
import eu.focusnet.app.model.json.Preference;
import eu.focusnet.app.ui.activity.PageActivity;
import eu.focusnet.app.ui.activity.ProjectActivity;
import eu.focusnet.app.ui.adapter.StandardListAdapter;
import eu.focusnet.app.ui.common.AbstractListItem;
import eu.focusnet.app.ui.common.HeaderListItem;
import eu.focusnet.app.ui.common.StandardListItem;
import eu.focusnet.app.ui.util.Constant;
import eu.focusnet.app.ui.util.UiHelpers;


/**
 */
public class BookmarkFragment extends ListFragment
{

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
		this.updateListAdapter();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		if (l.getAdapter().getItemViewType(position) != HeaderListItem.TYPE_HEADER) {

			Intent intent = null;
			StandardListItem selectedItem = (StandardListItem) abstractItems.get(position);

			String path = selectedItem.getPath();
			String[] parts = path.split(eu.focusnet.app.model.util.Constant.PATH_SEPARATOR_PATTERN);
			switch (parts.length) {
				case 1:
					intent = new Intent(getActivity(), ProjectActivity.class);
					break;
				case 3:
					intent = new Intent(getActivity(), PageActivity.class);
					intent.putExtra(Constant.UI_EXTRA_PAGE_PATH, selectedItem.getPath());
					break;
			}

			intent.putExtra(Constant.UI_EXTRA_PROJECT_PATH, parts[0]);
			intent.putExtra(Constant.UI_EXTRA_TITLE, selectedItem.getTitle());
			startActivity(intent);
		}
	}


	/**
	 * Update the current bookmark list with data from the User Preferences
	 */
	private void updateListAdapter()
	{
		Preference preference = null;
		try {
			preference = FocusApplication.getInstance().getDataManager().getUserPreferences();
		}
		catch (FocusMissingResourceException ex) {
			// FIXME TODO do something smart.
			// create a default object?
		}

		Bookmark bookmark = preference.getBookmarks();
		ArrayList<BookmarkLink> pages = bookmark.getPages();
		ArrayList<BookmarkLink> tools = bookmark.getTools();

		abstractItems = new ArrayList<>();
		AbstractListItem headerProjectsListItem = new HeaderListItem(UiHelpers.getBitmap(getActivity(), R.drawable.ic_file),
				getResources().getString(R.string.bookmark_header_dashboard), null);
		abstractItems.add(headerProjectsListItem);

		Bitmap rightIcon = UiHelpers.getBitmap(getActivity(), R.drawable.ic_star);

		for (BookmarkLink bl : pages) {
			StandardListItem drawListItem = new StandardListItem(bl.getPath(), UiHelpers.getBitmap(getActivity(), R.drawable.ic_chevron_right),
					bl.getName(), bl.getPath(),	rightIcon, true, BookmarkLink.BOOKMARK_LINK_TYPE.PAGE.toString());
			abstractItems.add(drawListItem);
		}

		AbstractListItem headerToolListItem = new HeaderListItem(UiHelpers.getBitmap(getActivity(), R.drawable.ic_tool),
				getString(R.string.bookmark_header_tool), null);
		abstractItems.add(headerToolListItem);


		for (BookmarkLink bl : tools) {
			StandardListItem drawListItem = new StandardListItem(bl.getPath(), UiHelpers.getBitmap(getActivity(), R.drawable.ic_chevron_right),
					bl.getName(), bl.getPath(),rightIcon, true, BookmarkLink.BOOKMARK_LINK_TYPE.TOOL.toString());
			abstractItems.add(drawListItem);
		}

		StandardListAdapter adapter = new StandardListAdapter(getActivity(), abstractItems);

		setListAdapter(adapter);
	}
}

