/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.ui.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.R;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.json.Bookmark;
import eu.focusnet.app.model.json.BookmarkLink;
import eu.focusnet.app.model.json.Preference;
import eu.focusnet.app.service.DataManager;
import eu.focusnet.app.ui.activity.EntryPointActivity;
import eu.focusnet.app.ui.common.AbstractListItem;
import eu.focusnet.app.ui.common.HeaderListItem;
import eu.focusnet.app.ui.common.StandardListItem;
import eu.focusnet.app.ui.util.UiHelpers;

/**
 * List Adapter, used for
 *
 * FIXME TODO JULIEN TO BE REVIEWED
 */
public class StandardListAdapter extends BaseAdapter
{
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<AbstractListItem> abstractListItems;

	public StandardListAdapter(Context context, ArrayList<AbstractListItem> abstractListItems)
	{
		this.context = context;
		this.inflater = android.view.LayoutInflater.from(this.context);
		this.abstractListItems = abstractListItems;
	}

	@Override
	public int getViewTypeCount()
	{
		return 2;
	} //two types of views

	@Override
	public int getItemViewType(int position)
	{
		return abstractListItems.get(position).getType();
	}

	@Override
	public int getCount()
	{
		return abstractListItems.size();
	}

	@Override
	public Object getItem(int position)
	{
		return abstractListItems.get(position);
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
		ViewHolder holder;
		int itemViewType = getItemViewType(position);

		//Test is the imageView is already created
		if (convertView == null) {
			holder = new ViewHolder();

			//Find out the item imageView type and set the corresponding layout
			if (itemViewType == HeaderListItem.TYPE_HEADER) {
				row = inflater.inflate(R.layout.list_item_header, parent, false);
				holder.leftIcon = (ImageView) row.findViewById(R.id.header_left_icon);
				holder.title = (TextView) row.findViewById(R.id.header_title);
				holder.rightIcon = (ImageView) row.findViewById(R.id.header_right_icon);
			}
			else {
				row = inflater.inflate(R.layout.standard_list_item, parent, false);
				holder.leftIcon = (ImageView) row.findViewById(R.id.icon);
				holder.title = (TextView) row.findViewById(R.id.title);
				holder.info = (TextView) row.findViewById(R.id.info);
				holder.rightIcon = (ImageView) row.findViewById(R.id.right_icon);
				holder.rightIcon.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						final StandardListItem standardListItem = (StandardListItem) abstractListItems.get(position);

						if (standardListItem.getRightIcon() != null) {
							final ImageView imageView = (ImageView) v;

							AlertDialog.Builder builder = new AlertDialog.Builder(context);
							View dialogView = inflater.inflate(R.layout.custom_alert_dialog_layout, null);
							builder.setView(dialogView);
							final Dialog dialog = builder.create();

							TextView dialogTitle = ((TextView) dialogView.findViewById(R.id.dialog_title));


							final boolean isRightIconActive = standardListItem.isRightIconActive();

							if (isRightIconActive) {
								dialogTitle.setText(R.string.focus_remove_bookmark_question);
							}
							else {
								dialogTitle.setText(R.string.focus_add_bookmark_question);
							}


							Button ok = (Button) dialogView.findViewById(R.id.ok);
							Button cancel = (Button) dialogView.findViewById(R.id.cancel);
							cancel.setOnClickListener(new View.OnClickListener()
							{
								@Override
								public void onClick(View v)
								{
									dialog.dismiss();
								}
							});

							final EditText selectedContext = (EditText) dialogView.findViewById(R.id.edit_text_name);
							selectedContext.setText(standardListItem.getTitle());
							if (isRightIconActive) {
								selectedContext.setEnabled(false);
							}

							ok.setOnClickListener(new View.OnClickListener()
							{
								@Override
								public void onClick(View view)
								{
									Preference userPreference = null;
									try {
										userPreference = FocusApplication.getInstance().getDataManager().getUserPreferences();
									}
									catch (FocusMissingResourceException ex) {
										// FIXME FIXME TODO
										// create a new one? or crash
									}

									if (isRightIconActive) {
										standardListItem.setIsRightIconActive(false);
										Bitmap rightIcon = UiHelpers.getBitmap(context, R.drawable.ic_star_o);
										standardListItem.setRightIcon(rightIcon);
										imageView.setImageBitmap(rightIcon);
										selectedContext.setEnabled(false);

										userPreference.removeBookmarkLink(standardListItem.getPath(), standardListItem.getTitle(), standardListItem.getTypeOfBookmark());
									}
									else {
										standardListItem.setIsRightIconActive(true);
										Bitmap rightIcon = UiHelpers.getBitmap(context, R.drawable.ic_star);
										standardListItem.setRightIcon(rightIcon);
										imageView.setImageBitmap(rightIcon);

										BookmarkLink bookmarkLink = new BookmarkLink(selectedContext.getText().toString(), standardListItem.getPath(), 0);
										userPreference.addBookmarkLink(bookmarkLink, standardListItem.getTypeOfBookmark());
									}

									new SaveUserPreferencesTask(context).execute();
/*
									// permanently save
									final Thread save_user_preferences_thread = new Thread()
									{
										public void run()
										{
											UiHelpers.displayToast(context, "raaaaa");
											FocusApplication.getInstance().getDataManager().saveUserPreferences();
										}
									};
									save_user_preferences_thread.start();*/

									dialog.dismiss();
								}
							});
							dialog.show();
						}
					}
				});
			}

			row.setTag(holder);
		}
		else {
			row = convertView;
			holder = (ViewHolder) row.getTag();
		}

		AbstractListItem abstractListItem = abstractListItems.get(position);
		if (itemViewType == HeaderListItem.TYPE_HEADER) {
			HeaderListItem headerListItem = (HeaderListItem) abstractListItem;
			holder.rightIcon.setImageBitmap(headerListItem.getRightIcon());
		}
		else {
			holder.info.setText(((StandardListItem) abstractListItem).getInfo());
			holder.rightIcon.setImageBitmap(((StandardListItem) abstractListItem).getRightIcon());
		}

		//Present in all menu items
		holder.leftIcon.setImageBitmap(abstractListItem.getIcon());
		holder.title.setText(abstractListItem.getTitle());

		return row;
	}


	private class ViewHolder
	{
		public ImageView leftIcon;
		public TextView title;
		public TextView info;
		public ImageView rightIcon;
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
			UiHelpers.displayToast(this.context, R.string.focus_save_user_pref_before);
		}

		@Override
		protected Void doInBackground(Void ... nil)
		{
			FocusApplication.getInstance().getDataManager().saveUserPreferences();
			return null;
		}

		@Override
		protected void onPostExecute(Void nil)
		{
			UiHelpers.displayToast(this.context, R.string.focus_save_user_pref_after);
		}
	}
}
