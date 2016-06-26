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

import android.widget.ImageView;
import android.widget.TextView;

/**
 * A View set holder for {@link FeaturedListItem}s that allows recycling of list item in our
 * list adapters.
 */
public class FeaturedListItemViewSet extends SimpleListItemViewSet
{
	/**
	 * Description view
	 */
	private TextView description;

	/**
	 * Secondary icon view
	 */
	private ImageView secondaryIcon;

	/**
	 * Get the description View
	 *
	 * @return A TextView
	 */
	public TextView getDescription()
	{
		return description;
	}

	/**
	 * Set the description View
	 *
	 * @param description A TextView
	 */
	public void setDescription(TextView description)
	{
		this.description = description;
	}

	/**
	 * Get the secondary icon View
	 *
	 * @return An ImageView
	 */
	public ImageView getSecondaryIcon()
	{
		return secondaryIcon;
	}

	/**
	 * Set the secondary icon View
	 *
	 * @param secondaryIcon An ImageView
	 */
	public void setSecondaryIcon(ImageView secondaryIcon)
	{
		this.secondaryIcon = secondaryIcon;
	}
}
