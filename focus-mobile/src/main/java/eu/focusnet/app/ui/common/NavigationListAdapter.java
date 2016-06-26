/*
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
 */

package eu.focusnet.app.ui.common;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import eu.focusnet.app.controller.FocusAppLogic;
import eu.focusnet.app.R;
import eu.focusnet.app.util.FocusInternalErrorException;
import eu.focusnet.app.model.UserPreferencesInstance;
import eu.focusnet.app.model.gson.Bookmark;
import eu.focusnet.app.model.gson.UserPreferences;
import eu.focusnet.app.util.ApplicationHelper;
import eu.focusnet.app.util.Constant;

/**
 * List Adapter used by the drawer menu and the listings of objects.
 */
public class NavigationListAdapter extends BaseAdapter
{

	/**
	 * Android Context
	 */
	final private Context context;

	/**
	 * Layout inflater
	 */
	final private LayoutInflater inflater;

	/**
	 * List of items to include in the list. These are {@link SimpleListItem}s (for header and
	 * dummy empty list item) or {@link FeaturedListItem} (for links).
	 */
	private ArrayList<SimpleListItem> listItems;

	/**
	 * Constructor
	 *
	 * @param context   Input value for instance variable
	 * @param listItems The list of items to be included in the list.
	 */
	public NavigationListAdapter(Context context, ArrayList<SimpleListItem> listItems)
	{
		this.context = context;
		this.listItems = listItems;
		this.inflater = LayoutInflater.from(context);
	}


	/**
	 * We have 3 types of list items: header, dummy empty element and link.
	 *
	 * @return The number of types of elements in the lsit.
	 */
	@Override
	public int getViewTypeCount()
	{
		return Constant.Navigation.LIST_TYPE_EMPTY + 1;
	}

	/**
	 * Give the type of item at the given position.
	 *
	 * @param position The position of the item to investigate within the list
	 * @return The type of the list item
	 */
	@Override
	public int getItemViewType(int position)
	{
		SimpleListItem item = listItems.get(position);
		if (item instanceof FeaturedListItem) {
			return Constant.Navigation.LIST_TYPE_LINK;
		}
		else if (item instanceof EmptyListItem) {
			return Constant.Navigation.LIST_TYPE_EMPTY;
		}
		else {
			return Constant.Navigation.LIST_TYPE_HEADER;
		}
	}

	/**
	 * Get the number of elements in the list
	 *
	 * @return The number of elements in the list
	 */
	@Override
	public int getCount()
	{
		return listItems.size();
	}

	/**
	 * Get the list item at the give position
	 *
	 * @param position Position of the item to get
	 * @return The item
	 */
	@Override
	public Object getItem(int position)
	{
		return listItems.get(position);
	}

	/**
	 * Get an item ID
	 *
	 * @param position Position of the item to get
	 * @return The ID of the item
	 */
	@Override
	public long getItemId(int position)
	{
		return position;
	}

	/**
	 * Acquire the {@code View} for a list item.
	 * 
	 * FIXME enable recycling. We have problems here, yet.
	 *
	 * @param position    The position of the view to obtain
	 * @param convertView A recycled View or null
	 * @param parent      Inherited.
	 * @return The new View for the list item
	 */
	@Override
	public View getView(final int position, final View convertView, ViewGroup parent)
	{
		View row;
		FeaturedListItemViewSet itemViewSet;
		SimpleListItem listItem = listItems.get(position);


		int itemType = this.getItemViewType(position);

		// if (convertView == null) { FIXME bug with clicklistener, let's deactivate recycling for now
		// create new view holder
		itemViewSet = new FeaturedListItemViewSet();

		// resource to inflate depends on current itemType
		int resourceToInflate;
		switch (itemType) {
			case Constant.Navigation.LIST_TYPE_EMPTY:
				resourceToInflate = R.layout.list_item_empty;
				break;
			case Constant.Navigation.LIST_TYPE_HEADER:
				resourceToInflate = R.layout.list_item_header;
				break;
			case Constant.Navigation.LIST_TYPE_LINK:
				resourceToInflate = R.layout.list_item_link;
				break;
			default:
				throw new FocusInternalErrorException("No view to inflate for list item.");
		}
		row = inflater.inflate(resourceToInflate, parent, false);

		itemViewSet.setTitle((TextView) row.findViewById(R.id.title));
		itemViewSet.setPrimaryIcon((ImageView) row.findViewById(R.id.icon));
		if (!(itemType == Constant.Navigation.LIST_TYPE_HEADER || itemType == Constant.Navigation.LIST_TYPE_EMPTY)) {
			itemViewSet.setDescription((TextView) row.findViewById(R.id.description));
			itemViewSet.setSecondaryIcon((ImageView) row.findViewById(R.id.right_icon));
		}
		row.setTag(itemViewSet);
		/* }
		 FIXME see above else {
			// recycle existing view holder
			row = convertView;
			itemViewSet = (FeaturedListItemViewSet) row.getTag();
		}*/

		// type seems to help to remember??? convertview may be per type
		// Set values to our Views
		switch (itemType) {
			case Constant.Navigation.LIST_TYPE_LINK:
				// reasonable defaults
				row.setEnabled(true);
				itemViewSet.getDescription().setVisibility(View.GONE);
				// FIXME	row.setOnClickListener(null); here we should have the default listener
				itemViewSet.getTitle().setTextColor(ApplicationHelper.getResources().getColor(android.R.color.black));
				itemViewSet.getSecondaryIcon().setOnClickListener(getOnClickBookmarkListener((FeaturedListItem) listItem)); // FIXME also be careful with this one

				String description = ((FeaturedListItem) listItem).getDescription();
				if (description != null && !description.equals("")) {
					itemViewSet.getDescription().setText(description);
					itemViewSet.getDescription().setVisibility(View.VISIBLE);
				}

				itemViewSet.getSecondaryIcon().setImageBitmap(((FeaturedListItem) listItem).getSecondaryIcon());

				if (listItem.isDisabled()) {
					row.setEnabled(false);
					row.setOnClickListener(null);
					itemViewSet.getTitle().setTextColor(ApplicationHelper.getResources().getColor(R.color.textColorDiscrete));
					itemViewSet.getSecondaryIcon().setOnClickListener(null);
				}
				else {

				}


				// no break, we share attributes with LIST_TYPE_HEADER
			case Constant.Navigation.LIST_TYPE_HEADER:
				itemViewSet.getPrimaryIcon().setImageBitmap(listItem.getPrimaryIcon());
				itemViewSet.getTitle().setText(listItem.getTitle());
				break;
		}

		switch (itemType) {
			case Constant.Navigation.LIST_TYPE_HEADER:
			case Constant.Navigation.LIST_TYPE_EMPTY:
				row.setEnabled(false);
				row.setOnClickListener(null);
				break;
		}

		return row;
	}

	/**
	 * Create a click listener for the secondary icon, which triggers bookmarking or unbookmarking.
	 *
	 * @param featuredListItem The item on which the listener will be registered.
	 * @return A click listener.
	 */
	private View.OnClickListener getOnClickBookmarkListener(final FeaturedListItem featuredListItem)
	{

		return new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (featuredListItem.getSecondaryIcon() != null) {
					final ImageView imageView = (ImageView) v;

					Resources res = ApplicationHelper.getResources();
					final boolean isExistingBookmark = featuredListItem.isExistingBookmark();

					// Dialog payload
					LayoutInflater inflater = LayoutInflater.from(context);
					// FIXME better context than null
					View dialogContent = inflater.inflate(R.layout.dialog_content_bookmark, null);
					final TextView bookmarkTitle = (TextView) dialogContent.findViewById(isExistingBookmark ? R.id.bookmark_field_ro : R.id.bookmark_field_rw);
					TextView alternateField = (TextView) dialogContent.findViewById(isExistingBookmark ? R.id.bookmark_field_rw : R.id.bookmark_field_ro);
					alternateField.setVisibility(View.GONE);
					bookmarkTitle.setVisibility(View.VISIBLE);
					bookmarkTitle.setText("");
					bookmarkTitle.append(featuredListItem.getTitle());

					// Dialog building
					FocusDialogBuilder builder = new FocusDialogBuilder(context)
							.setTitle(res.getString(isExistingBookmark ? R.string.focus_remove_bookmark_question : R.string.focus_add_bookmark_question))
							.insertContent(dialogContent)
							.removeNeutralButton()
							.setCancelable(false)
							.setPositiveButtonText(res.getString((isExistingBookmark ? R.string.delete : R.string.ok)))
							.setNegativeButtonText(res.getString(R.string.cancel));

					// instantiate
					final AlertDialog dialog = builder.create();

					// Add click listeners on buttons
					builder.getNegativeButton().setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							dialog.dismiss();
						}
					});

					builder.getPositiveButton().setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View view)
						{
							UserPreferencesInstance userPreferences = FocusAppLogic.getUserManager().getUserPreferences();

							if (isExistingBookmark) {
								featuredListItem.setIsBookmarked(false);
								Bitmap rightIcon = UiHelper.getBitmap(context, R.drawable.ic_bookmark_not_selected);
								featuredListItem.setSecondaryIcon(rightIcon);
								imageView.setImageBitmap(rightIcon);
								bookmarkTitle.setEnabled(false);
								userPreferences.removeBookmarkLink(featuredListItem.getPath(), featuredListItem.getTitle(), featuredListItem.getTypeOfBookmark());
							}
							else {
								featuredListItem.setIsBookmarked(true);
								Bitmap rightIcon = UiHelper.getBitmap(context, R.drawable.ic_bookmark_selected);
								featuredListItem.setSecondaryIcon(rightIcon);
								imageView.setImageBitmap(rightIcon);
								Bookmark bookmarkLink = new Bookmark(bookmarkTitle.getText().toString(), featuredListItem.getPath());
								userPreferences.addBookmarkLink(bookmarkLink, featuredListItem.getTypeOfBookmark());
							}

							new SaveUserPreferencesTask(userPreferences, context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							dialog.dismiss();
						}
					});

					dialog.show();
				}
			}
		};
	}


	/**
	 * Task to save user preferences
	 * 
	 * FIXME what happens if we save user preferences when user preference saving is not finished, yet. To check.
	 */
	private class SaveUserPreferencesTask extends AsyncTask<Void, Void, Void>
	{
		/**
		 * User preference object
		 */
		private final UserPreferences userPreferences;

		/**
		 * Android Context
		 */
		private Context context;

		/**
		 * Constructor
		 *
		 * @param userPreferences Input value for instance variables
		 * @param context         Input value for instance variable
		 */
		public SaveUserPreferencesTask(UserPreferences userPreferences, Context context)
		{
			this.context = context;
			this.userPreferences = userPreferences;
		}

		/**
		 * Announce that we are going to do something
		 */
		@Override
		protected void onPreExecute()
		{
			UiHelper.displayToast(this.context, R.string.focus_save_user_pref_before);
		}

		/**
		 * Save the new preferences
		 *
		 * @param nil Nothing
		 * @return Nothing
		 */
		@Override
		protected Void doInBackground(Void... nil)
		{
			FocusAppLogic.getDataManager().update(userPreferences);
			return null;
		}

		/**
		 * Announce that we successfully saved the preferences
		 *
		 * @param nil Nothing
		 */
		@Override
		protected void onPostExecute(Void nil)
		{
			UiHelper.displayToast(this.context, R.string.focus_save_user_pref_after);
		}
	}
}
