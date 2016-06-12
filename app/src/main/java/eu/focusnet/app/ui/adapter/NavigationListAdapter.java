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

package eu.focusnet.app.ui.adapter;

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

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.R;
import eu.focusnet.app.model.json.Bookmark;
import eu.focusnet.app.model.json.UserPreferences;
import eu.focusnet.app.ui.common.EmptyListItem;
import eu.focusnet.app.ui.common.FeaturedListItem;
import eu.focusnet.app.ui.common.FeaturedListItemViewSet;
import eu.focusnet.app.ui.common.FocusDialogBuilder;
import eu.focusnet.app.ui.common.SimpleListItem;
import eu.focusnet.app.ui.util.UiHelper;

/**
 * List Adapter, used for drawer and entries in listings pages
 * <p/>
 * FIXME TODO JULIEN TO BE REVIEWED
 * <p/>
 * FIXME FIXME FIXME bookarmking for TOOLs does not work: the position value is relative to the HeaderItem. (e.g. 1 instead of count(PAGE)+1)
 * however, simple click on TOOL items redirects to the correct page.
 */
public class NavigationListAdapter extends BaseAdapter
{
	public final static int LIST_TYPE_HEADER = 1;
	public final static int LIST_TYPE_LINK = 2;
	public final static int LIST_TYPE_EMPTY = 3;

	private final Context context;
	private final LayoutInflater inflater;
	private ArrayList<SimpleListItem> listItems;

	public NavigationListAdapter(Context context, ArrayList<SimpleListItem> listItems)
	{
		this.context = context;
		this.listItems = listItems;
		this.inflater = LayoutInflater.from(context);
	}


	@Override
	public int getViewTypeCount()
	{
		return 3;
	}

	@Override
	public int getItemViewType(int position)
	{
		SimpleListItem item = listItems.get(position);
		if (item instanceof FeaturedListItem) {
			return LIST_TYPE_LINK;
		}
		else if (item instanceof EmptyListItem) {
			return LIST_TYPE_EMPTY;
		}
		else {
			return LIST_TYPE_HEADER;
		}
	}

	@Override
	public int getCount()
	{
		return listItems.size();
	}

	@Override
	public Object getItem(int position)
	{
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(final int position, final View convertView, ViewGroup parent)
	{
		View row;
		FeaturedListItemViewSet itemViewSet;
		SimpleListItem listItem = listItems.get(position);

		int itemType = this.getItemViewType(position);

		if (convertView == null) {
			// create new view holder
			itemViewSet = new FeaturedListItemViewSet();
			row = inflater.inflate(R.layout.standard_list_item, parent, false); // FIXME change inflated view depending on view type.

			itemViewSet.title = (TextView) row.findViewById(R.id.title);
			itemViewSet.icon = (ImageView) row.findViewById(R.id.icon);
			 // not for EMPTY, FIXME
			if (itemType == LIST_TYPE_HEADER || itemType == LIST_TYPE_EMPTY) {
				row.setEnabled(false);
				row.setOnClickListener(null);
			}
			else {
				itemViewSet.description = (TextView) row.findViewById(R.id.description);
				itemViewSet.rightIcon = (ImageView) row.findViewById(R.id.right_icon);
				itemViewSet.rightIcon.setOnClickListener(getClickBookmarkListener((FeaturedListItem) listItem));
			}
			row.setTag(itemViewSet);
		}
		else {
			// recycle existing view holder
			row = convertView;
			itemViewSet = (FeaturedListItemViewSet) row.getTag();
		}

		// Set values to our Views
		switch (itemType) {
			case LIST_TYPE_LINK:
				itemViewSet.description.setText(((FeaturedListItem)listItem).getDescription());
				itemViewSet.rightIcon.setImageBitmap(((FeaturedListItem)listItem).getRightIcon());
				// no break, we share attributes with LIST_TYPE_HEADER and EMPTY
			case LIST_TYPE_HEADER:
			case LIST_TYPE_EMPTY:
				itemViewSet.icon.setImageBitmap(listItem.getIcon());
				itemViewSet.title.setText(listItem.getTitle());
				break;
		}

		return row;
	}

private View.OnClickListener getClickBookmarkListener(final FeaturedListItem featuredListItem)
{

	return new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
		//	final FeaturedListItem featuredListItem = (FeaturedListItem) listItems.get(position);

			if (featuredListItem.getRightIcon() != null) {
				final ImageView imageView = (ImageView) v;

				Resources res = FocusApplication.getInstance().getResources();
				final boolean isExistingBookmark = featuredListItem.isExistingBookmark();

				// Dialog payload
				LayoutInflater inflater = LayoutInflater.from(context);
				View dialogContent = inflater.inflate(R.layout.dialog_content_bookmark, null); // FIXME context?
				final TextView bookmarkTitle = (TextView) dialogContent.findViewById(isExistingBookmark ? R.id.bookmark_field_ro : R.id.bookmark_field_rw);
				TextView alternateField = (TextView) dialogContent.findViewById(isExistingBookmark ? R.id.bookmark_field_rw : R.id.bookmark_field_ro);
				alternateField.setVisibility(View.GONE);
				bookmarkTitle.setVisibility(View.VISIBLE);
				bookmarkTitle.setText(featuredListItem.getTitle());


				// Dialog building
				FocusDialogBuilder builder = new FocusDialogBuilder(context)
						.setTitle(res.getString(isExistingBookmark ? R.string.focus_remove_bookmark_question : R.string.focus_add_bookmark_question))
						.insertContent(dialogContent)
						.removeNeutralButton()
						.setCancelable(false)
						.setPositiveButtonText(res.getString((isExistingBookmark ? R.string.delete_bookmark : R.string.ok)))
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
						UserPreferences userPreference = FocusApplication.getInstance().getDataManager().getUserPreferences();

						if (isExistingBookmark) {
							featuredListItem.setIsRightIconActive(false);
							Bitmap rightIcon = UiHelper.getBitmap(context, R.drawable.ic_bookmark_not_selected);
							featuredListItem.setRightIcon(rightIcon);
							imageView.setImageBitmap(rightIcon);
							bookmarkTitle.setEnabled(false);
							userPreference.removeBookmarkLink(featuredListItem.getPath(), featuredListItem.getTitle(), featuredListItem.getTypeOfBookmark());
						}
						else {
							featuredListItem.setIsRightIconActive(true);
							Bitmap rightIcon = UiHelper.getBitmap(context, R.drawable.ic_bookmark_selected);
							featuredListItem.setRightIcon(rightIcon);
							imageView.setImageBitmap(rightIcon);
							Bookmark bookmarkLink = new Bookmark(bookmarkTitle.getText().toString(), featuredListItem.getPath());
							userPreference.addBookmarkLink(bookmarkLink, featuredListItem.getTypeOfBookmark());
						}

						new SaveUserPreferencesTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						dialog.dismiss();
					}
				});

				dialog.show();
			}
		}
	};
}




	/**
	 * This class is used for testing if the given credential are correct
	 */
	private class SaveUserPreferencesTask extends AsyncTask<Void, Void, Void>
	{
		private Context context;

		public SaveUserPreferencesTask(Context context)
		{
			this.context = context;
		}

		@Override
		protected void onPreExecute()
		{
			UiHelper.displayToast(this.context, R.string.focus_save_user_pref_before);
		}

		@Override
		protected Void doInBackground(Void... nil)
		{
			FocusApplication.getInstance().getDataManager().saveUserPreferences();
			return null;
		}

		@Override
		protected void onPostExecute(Void nil)
		{
			UiHelper.displayToast(this.context, R.string.focus_save_user_pref_after);
		}
	}
}
