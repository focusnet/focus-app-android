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

import eu.focusnet.app.FocusAppLogic;
import eu.focusnet.app.R;
import eu.focusnet.app.model.ProjectInstance;
import eu.focusnet.app.model.gson.Bookmark;
import eu.focusnet.app.model.gson.UserPreferences;
import eu.focusnet.app.ui.activity.ProjectActivity;
import eu.focusnet.app.ui.common.EmptyListItem;
import eu.focusnet.app.ui.common.FeaturedListItem;
import eu.focusnet.app.ui.common.NavigationListAdapter;
import eu.focusnet.app.ui.common.SimpleListItem;
import eu.focusnet.app.ui.common.UiHelper;
import eu.focusnet.app.util.Constant;


/**
 * This fragment is the start point of the application
 * after the user logged in.
 */
public class ProjectsListingFragment extends ListFragment
{

	/**
	 * Items to be inserted in the {@link NavigationListAdapter} that is responsible for
	 * handling the {@link ListView} of this page. I.e. list of projects.
	 */
	private ArrayList<SimpleListItem> listItems;


	/**
	 * Create the view and build the content of the {@link ListView} in a background task.
	 *
	 * @param inflater Inherited
	 * @param container Inherited
	 * @param savedInstanceState Inherited
	 * @return The new View
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View viewRoot = inflater.inflate(R.layout.list_fragment, container, false);
		new ProjectsListingBuilderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		return viewRoot;
	}


	/**
	 * Click listener for when a list item is clicked.
	 *
	 * @param l Inherited
	 * @param v Inherited
	 * @param position Position of the clicked item
	 * @param id Inherited
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		if (l.getAdapter().getItemViewType(position) != Constant.Navigation.LIST_TYPE_HEADER) {
			Intent intent = new Intent(getActivity(), ProjectActivity.class);
			FeaturedListItem selectedItem = (FeaturedListItem) listItems.get(position);
			intent.putExtra(Constant.Extra.UI_EXTRA_PATH, selectedItem.getPath());
			intent.putExtra(Constant.Extra.UI_EXTRA_TITLE, selectedItem.getTitle());
			startActivity(intent);
		}
	}


	/**
	 * This task loads all projects a renders them using a {@link NavigationListAdapter}
	 */
	private class ProjectsListingBuilderTask extends AsyncTask<Void, Void, NavigationListAdapter>
	{

		/**
		 * Acquire the list items and save them in a {@link NavigationListAdapter}.
		 *
		 * @param voids Nothing
		 * @return A new {@link NavigationListAdapter}
		 */
		@Override
		protected NavigationListAdapter doInBackground(Void... voids)
		{
			listItems = new ArrayList<>();
			SimpleListItem headerProjectsListItem = new SimpleListItem(UiHelper.getBitmap(getActivity(), R.drawable.ic_category_project_negative),
					getString(R.string.focus_header_project));

			listItems.add(headerProjectsListItem);
			UserPreferences preference = FocusAppLogic.getUserManager().getUserPreferences();

			ArrayList<ProjectInstance> projects = FocusAppLogic.getCurrentApplicationContent().getProjects();
			Bitmap rightIconIfNotActive = UiHelper.getBitmap(getActivity(), R.drawable.ic_bookmark_not_selected);
			Bitmap rightIconIfActive = UiHelper.getBitmap(getActivity(), R.drawable.ic_bookmark_selected);

			if (!projects.isEmpty()) {
				for (ProjectInstance p : projects) {

					String projectPath = p.getPath();
					String projectTitle = p.getTitle();
					String projectDesc = p.getDescription();
					boolean disabled = p.isDisabled();

					String bookmarkLinkType = Bookmark.BookmarkLinkType.PAGE.toString();
					boolean checkedBookmark = (preference != null) && (-1 != preference.findBookmarkLinkInSpecificSet(projectPath, projectTitle, Bookmark.BookmarkLinkType.PAGE.toString()));

					FeaturedListItem drawListItem = new FeaturedListItem(
							projectPath,
							UiHelper.getBitmap(getActivity(), R.drawable.ic_project),
							projectTitle,
							projectDesc,
							checkedBookmark ? rightIconIfActive : rightIconIfNotActive,
							checkedBookmark,
							bookmarkLinkType,
							disabled
					);
					listItems.add(drawListItem);
				}
			}
			else {
				listItems.add(new EmptyListItem());
			}

			return new NavigationListAdapter(getActivity(), listItems);
		}

		/**
		 * Set the list adapter
		 *
		 * @param navigationListAdapter Adapter to use
		 */
		@Override
		protected void onPostExecute(NavigationListAdapter navigationListAdapter)
		{
			setListAdapter(navigationListAdapter);
		}
	}

}
