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

package eu.focusnet.app.ui.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import eu.focusnet.app.R;

/**
 * List adapter used by the drawer menu
 */
public class DrawerListAdapter extends BaseAdapter
{

	/**
	 * Layout inflater
	 */
	private LayoutInflater inflater;

	/**
	 * List items of the drawer menu
	 */
	private ArrayList<SimpleListItem> listItems;

	/**
	 * Constuctor
	 *
	 * @param context Inherited
	 * @param listItems Items to put in the drawer menu
	 */
	public DrawerListAdapter(Context context, ArrayList<SimpleListItem> listItems)
	{
		this.inflater = LayoutInflater.from(context);
		this.listItems = listItems;
	}

	/**
	 * Inherited. Dummy function.
	 *
	 * @return Inherited.
	 */
	@Override
	public int getCount()
	{
		return this.listItems.size();
	}

	/**
	 * Inherited. Dummy function.
	 *
	 * @param position Inherited.
	 * @return Inherited.
	 */
	@Override
	public Object getItem(int position)
	{
		return listItems.get(position);
	}

	/**
	 * Inherited. Dummy function.
	 *
	 * @param position Inherited.
	 * @return Inherited.
	 */
	@Override
	public long getItemId(int position)
	{
		return position;
	}

	/**
	 * Create the view for a menu item.
	 *
	 * @param position 0-based position of the menu item
	 * @param convertView Recycled view.
	 * @param parent Inherited.
	 * @return A {@code View} for the menu item.
	 */
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
