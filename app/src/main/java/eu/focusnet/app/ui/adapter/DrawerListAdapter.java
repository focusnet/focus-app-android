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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import eu.focusnet.app.R;
import eu.focusnet.app.ui.common.SimpleListItem;
import eu.focusnet.app.ui.common.SimpleListItemViewSet;

/**
 * ok
 */
public class DrawerListAdapter extends BaseAdapter
{

	private LayoutInflater inflater;
	private ArrayList<SimpleListItem> listItems;

	public DrawerListAdapter(Context context, ArrayList<SimpleListItem> listItems)
	{
		this.inflater = LayoutInflater.from(context);
		this.listItems = listItems;
	}

	@Override
	public int getCount()
	{
		return this.listItems.size();
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
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row;
		SimpleListItemViewSet itemViewSet;
		SimpleListItem listItem = listItems.get(position);

		// Test is the view is already created. If this is the case retrieve Views from
		// the tag. Otherwise create it.
		if (convertView == null) {
			itemViewSet = new SimpleListItemViewSet();
			row = inflater.inflate(R.layout.list_item_drawer, parent, false);
			itemViewSet.setPrimaryIcon((ImageView) row.findViewById(R.id.icon));
			itemViewSet.setTitle((TextView) row.findViewById(R.id.title));
			row.setTag(itemViewSet);
		}
		else {
			row = convertView;
			itemViewSet = (SimpleListItemViewSet) row.getTag();
		}

		// Set values to our Views
		itemViewSet.getPrimaryIcon().setImageBitmap(listItem.getPrimaryIcon());
		itemViewSet.getTitle().setText(listItem.getTitle());

		return row;
	}


}
