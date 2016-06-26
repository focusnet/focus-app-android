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

import android.graphics.Bitmap;

/**
 * List item which contains an icon and a title.
 * This is the standard list item, and other list item types inherit it.
 * <p/>
 * This list item type is used for list items styled as headers and the drawer menu items.
 */
public class SimpleListItem
{
	/**
	 * Title of the list item
	 */
	private final String title;

	/**
	 * Main icon of the list item
	 */
	private final Bitmap primaryIcon;

	/**
	 * Tells whether this list item is enabled (clickable)
	 */
	private final boolean disabled;

	/**
	 * Constructor
	 *
	 * @param primaryIcon Input value for instance variable
	 * @param title       Input value for instance variable
	 */
	public SimpleListItem(Bitmap primaryIcon, String title)
	{
		this(primaryIcon, title, false);
	}

	/**
	 * Constructor
	 *
	 * @param primaryIcon Input value for instance variable
	 * @param title       Input value for instance variable
	 * @param disabled    Input value for instance variable
	 */
	public SimpleListItem(Bitmap primaryIcon, String title, boolean disabled)
	{
		this.primaryIcon = primaryIcon;
		this.title = title;
		this.disabled = disabled;
	}

	/**
	 * Get the main icon
	 *
	 * @return The icon
	 */
	public Bitmap getPrimaryIcon()
	{
		return this.primaryIcon;
	}

	/**
	 * Get the list item title
	 *
	 * @return The title
	 */
	public String getTitle()
	{
		return this.title;
	}

	/**
	 * Tells whether the list item is disabled or not
	 *
	 * @return {@code true} if this is the case, {@code false} otherwise.
	 */
	public boolean isDisabled()
	{
		return this.disabled;
	}
}

