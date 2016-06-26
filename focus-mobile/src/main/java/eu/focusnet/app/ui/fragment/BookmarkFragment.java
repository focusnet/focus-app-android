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

import eu.focusnet.app.controller.FocusAppLogic;
import eu.focusnet.app.R;
import eu.focusnet.app.model.AbstractInstance;
import eu.focusnet.app.model.AppContentInstance;
import eu.focusnet.app.model.PageInstance;
import eu.focusnet.app.model.ProjectInstance;
import eu.focusnet.app.model.UserPreferencesInstance;
import eu.focusnet.app.model.gson.Bookmark;
import eu.focusnet.app.model.gson.BookmarksList;
import eu.focusnet.app.ui.activity.PageActivity;
import eu.focusnet.app.ui.activity.ProjectActivity;
import eu.focusnet.app.ui.common.EmptyListItem;
import eu.focusnet.app.ui.common.FeaturedListItem;
import eu.focusnet.app.ui.common.NavigationListAdapter;
import eu.focusnet.app.ui.common.SimpleListItem;
import eu.focusnet.app.ui.common.UiHelper;
import eu.focusnet.app.util.Constant;

import static eu.focusnet.app.model.PageInstance.PageType;


/**
 * This fragment is inserted in the {@link eu.focusnet.app.ui.activity.ProjectsListingActivity}
 * when the user clicks the "Bookmarks" entry in the Drawer menu.
 */
public class BookmarkFragment extends ListFragment
{

	/**
	 * Items to include in the ListView
	 */
	private ArrayList<SimpleListItem> listItems;

	/**
	 * Create the view.
	 *
	 * @param inflater           Inherited
	 * @param container          Inherited
	 * @param savedInstanceState Inherited
	 * @return
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		return inflater.inflate(R.layout.list_fragment, container, false);
	}

	/**
	 * Start the fragment. Retrieve the content of the ListView.
	 */
	@Override
	public void onStart()
	{
		super.onStart();
		this.fillListAdapter(); // FIXME why like this and not in background task like in ProjectFragment? to check.
	}

	/**
	 * Click listener for the bookmark list
	 *
	 * @param l        Inherited
	 * @param v        Inherited
	 * @param position Position of the clicked item
	 * @param id       Inherited
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		if (l.getAdapter().getItemViewType(position) == Constant.Navigation.LIST_TYPE_LINK) {

			Intent intent;
			FeaturedListItem selectedItem = (FeaturedListItem) listItems.get(position);

			String path = selectedItem.getPath();
			String[] parts = path.split(Constant.Navigation.PATH_SEPARATOR_PATTERN);
			String category = parts[parts.length - 2];
			intent = new Intent(
					getActivity(),
					(category.equals(PageType.TOOL.toString()) || category.equals(PageType.DASHBOARD.toString())) ? PageActivity.class : ProjectActivity.class
			);
			intent.putExtra(Constant.Extra.UI_EXTRA_PATH, path);
			intent.putExtra(Constant.Extra.UI_EXTRA_TITLE, selectedItem.getTitle());
			startActivity(intent);
		}
	}

	/**
	 * Fill the ListView with data from the User Preferences and using
	 * a {@link NavigationListAdapter}.
	 */
	private void fillListAdapter()
	{
		UserPreferencesInstance preferences = FocusAppLogic.getUserManager().getUserPreferences();

		BookmarksList bookmark = preferences.getBookmarks();
		ArrayList<Bookmark> pages = bookmark.getPages();
		ArrayList<Bookmark> tools = bookmark.getTools();

		listItems = new ArrayList<>();
		this.addListItems(pages, R.drawable.ic_category_dashboard_negative, R.string.header_dashboard_and_projects);
		this.addListItems(tools, R.drawable.ic_category_tool_negative, R.string.header_tools);

		NavigationListAdapter adapter = new NavigationListAdapter(getActivity(), listItems);

		setListAdapter(adapter);
	}


	/**
	 * Add list items from a give source, also adding a user-friendly header.
	 *
	 * @param source      The source of items
	 * @param headerIcon  The icon of the header
	 * @param headerLabel The label of the header
	 */
	private void addListItems(ArrayList<Bookmark> source, int headerIcon, int headerLabel)
	{
		AppContentInstance appContentInstance = FocusAppLogic.getCurrentApplicationContent();

		ArrayList<SimpleListItem> newItems = new ArrayList<SimpleListItem>();

		// header
		SimpleListItem headerProjectsListItem = new SimpleListItem(
				UiHelper.getBitmap(getActivity(), headerIcon),
				getResources().getString(headerLabel)
		);
		newItems.add(headerProjectsListItem);

		// links
		Bitmap rightIcon = UiHelper.getBitmap(getActivity(), R.drawable.ic_bookmark_selected);
		if (!source.isEmpty()) {
			for (Bookmark bl : source) {
				String originalTitle;
				String path = bl.getPath();
				AbstractInstance instance = appContentInstance.lookupByPath(path);
				if (instance == null) {
					continue;
				}
				if (instance instanceof PageInstance) {
					originalTitle = ((PageInstance) instance).getTitle();
				}
				else if (instance instanceof ProjectInstance) {
					originalTitle = ((ProjectInstance) instance).getTitle();
				}
				else {
					continue;
				}

				FeaturedListItem listItem = new FeaturedListItem(
						bl.getPath(),
						UiHelper.getBitmap(getActivity(), R.drawable.ic_chevron_right),
						bl.getName(),
						originalTitle.equals(bl.getName()) ? "" : originalTitle,
						rightIcon,
						true,
						Bookmark.BookmarkLinkType.PAGE.toString(),
						false
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

