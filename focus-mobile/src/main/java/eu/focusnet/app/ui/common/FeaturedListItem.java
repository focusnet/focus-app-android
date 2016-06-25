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

package eu.focusnet.app.ui.common;

import android.graphics.Bitmap;


/**
 * Standard list item which contains
 * This is the standard item from a list
 * <p/>
 * FIXME used for clickable entries in navigationn lists
 */
public class FeaturedListItem extends SimpleListItem
{

	private String path;
	private String description;
	private Bitmap secondaryIcon;
	private boolean isBookmarked;
	private String typeOfBookmark;


	public FeaturedListItem(String path, Bitmap icon, String title, String description, Bitmap secondaryIcon, boolean isBookmarked, String typeOfBookmark, boolean disabled)
	{
		super(icon, title, disabled);
		this.description = description;
		this.path = path;
		this.secondaryIcon = secondaryIcon;
		this.isBookmarked = isBookmarked;
		this.typeOfBookmark = typeOfBookmark;
	}


	public Bitmap getSecondaryIcon()
	{
		return secondaryIcon;
	}

	public void setSecondaryIcon(Bitmap secondaryIcon)
	{
		this.secondaryIcon = secondaryIcon;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public boolean isExistingBookmark()
	{
		return isBookmarked;
	}

	public void setIsBookmarked(boolean isBookarmed)
	{
		this.isBookmarked = isBookarmed;
	}

	public String getTypeOfBookmark()
	{
		return typeOfBookmark;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}
}
