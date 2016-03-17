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

package eu.focusnet.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import eu.focusnet.app.R;
import eu.focusnet.app.ui.common.AbstractListItem;
import eu.focusnet.app.ui.common.DrawerListItem;
import eu.focusnet.app.ui.common.HeaderDrawerListItem;

/**
 * FIXME TODO JULIEN To be reviewed
 */
public class DrawerListAdapter extends BaseAdapter
{

	private LayoutInflater inflater;
	private ArrayList<AbstractListItem> drawerListItems;

	public DrawerListAdapter(Context context, ArrayList<AbstractListItem> drawerListItems)
	{
		this.inflater = LayoutInflater.from(context);
		this.drawerListItems = drawerListItems;
	}

	@Override
	public int getViewTypeCount()
	{
		return 2;
	} //return two types of views

	@Override
	public int getItemViewType(int position)
	{
		return drawerListItems.get(position).getType();
	}

	@Override
	public int getCount()
	{
		return drawerListItems.size();
	}

	@Override
	public Object getItem(int position)
	{
		return drawerListItems.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row;
		ViewHolder holder;
		int itemViewType = getItemViewType(position);

		//Test is the view is already created
		if (convertView == null) {
			holder = new ViewHolder();

			//Find out the item view type and set the corresponding layout
			if (itemViewType == HeaderDrawerListItem.TYPE_HEADER_DRAWER) {
				row = inflater.inflate(R.layout.drawer_list_item_header, parent, false);
				holder.icon = (ImageView) row.findViewById(R.id.logo);
				holder.title = (TextView) row.findViewById(R.id.user); //here is the value of title the user name
				holder.email = (TextView) row.findViewById(R.id.email);
				holder.company = (TextView) row.findViewById(R.id.company);
			}
			else {
				row = inflater.inflate(R.layout.standard_list_item, parent, false);
				//Present in R.layout.drawer_list_item layout
				holder.icon = (ImageView) row.findViewById(R.id.icon);
				holder.title = (TextView) row.findViewById(R.id.title);
				holder.info = (TextView) row.findViewById(R.id.info);
			}

			row.setTag(holder);
		}
		else {
			row = convertView;
			holder = (ViewHolder) row.getTag();
		}

		AbstractListItem drawerListItem = drawerListItems.get(position);
		if (itemViewType == HeaderDrawerListItem.TYPE_HEADER_DRAWER) {
			HeaderDrawerListItem headerDrawerListItem = (HeaderDrawerListItem) drawerListItem;
			holder.email.setText(headerDrawerListItem.getEmail());
			holder.company.setText(headerDrawerListItem.getCompany());
		}
		else {
			holder.info.setText(((DrawerListItem) drawerListItem).getInfo());
		}

		//Present in all menu items
		holder.icon.setImageBitmap(drawerListItem.getIcon());
		holder.title.setText(drawerListItem.getTitle());

		return row;
	}

	private class ViewHolder
	{

		public ImageView icon;
		public TextView title, info, email, company;
	}
}
