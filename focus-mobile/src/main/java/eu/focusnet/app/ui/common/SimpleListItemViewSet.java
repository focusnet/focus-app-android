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

import android.widget.ImageView;
import android.widget.TextView;

/**
 * A View set holder for {@link SimpleListItem}s that allows recycling of list item in our
 * list adapters.
 */
public class SimpleListItemViewSet
{
	/**
	 * Primary icon view
	 */
	private ImageView primaryIcon;

	/**
	 * Title view
	 */
	private TextView title;

	/**
	 * Get primary icon View
	 *
	 * @return An ImageView
	 */
	public ImageView getPrimaryIcon()
	{
		return primaryIcon;
	}

	/**
	 * Set the primary icon View
	 *
	 * @param primaryIcon An ImageView
	 */
	public void setPrimaryIcon(ImageView primaryIcon)
	{
		this.primaryIcon = primaryIcon;
	}

	/**
	 * Get title View
	 *
	 * @return A TextView
	 */
	public TextView getTitle()
	{
		return title;
	}

	/**
	 * Set the title View
	 *
	 * @param title A TextView
	 */
	public void setTitle(TextView title)
	{
		this.title = title;
	}
}