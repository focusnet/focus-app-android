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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import eu.focusnet.app.FocusAppLogic;
import eu.focusnet.app.R;
import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.internal.AppContentInstance;
import eu.focusnet.app.model.json.Bookmark;
import eu.focusnet.app.model.json.BookmarksList;
import eu.focusnet.app.model.json.UserPreferences;
import eu.focusnet.app.ui.activity.PageActivity;
import eu.focusnet.app.ui.activity.ProjectActivity;
import eu.focusnet.app.ui.adapter.NavigationListAdapter;
import eu.focusnet.app.ui.common.EmptyListItem;
import eu.focusnet.app.ui.common.FeaturedListItem;
import eu.focusnet.app.ui.common.SimpleListItem;
import eu.focusnet.app.ui.util.Constant;
import eu.focusnet.app.ui.util.UiHelper;


/**
 */
public class BookmarkFragment extends ListFragment
{

	private ArrayList<SimpleListItem> listItems;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		return inflater.inflate(R.layout.list_fragment, container, false);
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
		if (l.getAdapter().getItemViewType(position) == NavigationListAdapter.LIST_TYPE_LINK) {

			Intent intent;
			FeaturedListItem selectedItem = (FeaturedListItem) listItems.get(position);

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
				default:
					throw new FocusInternalErrorException("Invalid path format.");
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
		UserPreferences preferences = FocusAppLogic.getUserManager().getUserPreferences();

		BookmarksList bookmark = preferences.getBookmarks();
		ArrayList<Bookmark> pages = bookmark.getPages();
		ArrayList<Bookmark> tools = bookmark.getTools();

		listItems = new ArrayList<>();
		this.addListItems(pages, R.drawable.ic_category_dashboard_negative, R.string.header_dashboard_and_projects);
		this.addListItems(tools, R.drawable.ic_category_tool_negative, R.string.header_tools);

		NavigationListAdapter adapter = new NavigationListAdapter(getActivity(), listItems);

		setListAdapter(adapter);
	}


	private void addListItems(ArrayList<Bookmark> source, int icon, int label)
	{
		AppContentInstance appContentInstance = FocusAppLogic.getCurrentApplicationContent();

		ArrayList<SimpleListItem> newItems = new ArrayList<SimpleListItem>();

		// header
		SimpleListItem headerProjectsListItem = new SimpleListItem(
				UiHelper.getBitmap(getActivity(), icon),
				getResources().getString(label)
		);
		newItems.add(headerProjectsListItem);

		// links
		Bitmap rightIcon = UiHelper.getBitmap(getActivity(), R.drawable.ic_bookmark_selected);
		if (!source.isEmpty()) {
			for (Bookmark bl : source) {
				String originalTitle;
				String path = bl.getPath();
				String[] parts = path.split(eu.focusnet.app.model.util.Constant.PATH_SEPARATOR_PATTERN);
				try {
					if (parts.length >= 3) {
						originalTitle = appContentInstance.getPageFromPath(path).getTitle();
					}
					else {
						originalTitle = appContentInstance.getProjectFromPath(path).getTitle();
					}
				}
				catch (FocusMissingResourceException ex) {
					// the project/page does not exist, that may be because the data evolved and
					// this bookmark is not valid anymore. So let's skip it.
					continue;
				}

				FeaturedListItem listItem = new FeaturedListItem(
						bl.getPath(),
						UiHelper.getBitmap(getActivity(), R.drawable.ic_chevron_right),
						bl.getName(),
						originalTitle.equals(bl.getName()) ? "" : originalTitle,
						rightIcon,
						true,
						Bookmark.BookmarkLinkType.PAGE.toString()
				);
				newItems.add(listItem);
			}
		}

		// only the header? let's add an empty dummy element
		if (newItems.size() == 1) {
			newItems.add(new EmptyListItem());
		}

		listItems.addAll(newItems);
	}

}

