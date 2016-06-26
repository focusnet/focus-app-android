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

import android.graphics.Bitmap;

/**
 * List item type being used for clickable entries in our listings. This class inherits the
 * features of {@link SimpleListItem}.
 */
public class FeaturedListItem extends SimpleListItem
{

	/**
	 * The path of the entity being pointed to by the list item
	 */
	private String path;

	/**
	 * An optional description to display on the list item
	 */
	private String description;

	/**
	 * A secondary icon to display on the right.
	 */
	private Bitmap secondaryIcon;

	/**
	 * Tells whether the list item has been bookmarked.
	 */
	private boolean isExistingBookmark;

	/**
	 * Gives the type of bookmark.
	 */
	private String typeOfBookmark;


	/**
	 * Constructor.
	 * @param path Input value for instance variable
	 * @param icon Input value for instance variable
	 * @param title Input value for instance variable
	 * @param description Input value for instance variable
	 * @param secondaryIcon Input value for instance variable
	 * @param isExistingBookmark Input value for instance variable
	 * @param typeOfBookmark Input value for instance variable
	 * @param disabled Input value for instance variable
	 */
	public FeaturedListItem(String path, Bitmap icon, String title, String description, Bitmap secondaryIcon, boolean isExistingBookmark, String typeOfBookmark, boolean disabled)
	{
		super(icon, title, disabled);
		this.description = description;
		this.path = path;
		this.secondaryIcon = secondaryIcon;
		this.isExistingBookmark = isExistingBookmark;
		this.typeOfBookmark = typeOfBookmark;
	}

	/**
	 * Get the secondary icon.
	 *
	 * @return The icon
	 */
	public Bitmap getSecondaryIcon()
	{
		return secondaryIcon;
	}

	/**
	 * Set the secondary icon
	 *
	 * @param secondaryIcon The icon
	 */
	public void setSecondaryIcon(Bitmap secondaryIcon)
	{
		this.secondaryIcon = secondaryIcon;
	}

	/**
	 * Get the description
	 *
	 * @return The description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Set the description
	 *
	 * @param description The description
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Tells whether the current list item has already been bookmarked.
	 *
	 * @return {@code true} if this is the case, {@code false} otherwise.
	 */
	public boolean isExistingBookmark()
	{
		return isExistingBookmark;
	}

	/**
	 * Sets the bookmark status
	 *
	 * @param isExistingBookmark The status
	 */
	public void setIsBookmarked(boolean isExistingBookmark)
	{
		this.isExistingBookmark = isExistingBookmark;
	}

	/**
	 * Get the type of bookmark as a String
	 *
	 * @return TOOL or DASHBOARD
	 */
	public String getTypeOfBookmark()
	{
		return typeOfBookmark;
	}

	/**
	 * Get the path pointed to by the list item.
	 *
	 * @return The path
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 * Set the path being pointed to by the list item
	 *
	 * @param path The path
	 */
	public void setPath(String path)
	{
		this.path = path;
	}
}
