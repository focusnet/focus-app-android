package eu.focusnet.app.ui.common;

import android.graphics.Bitmap;

/**
 * Header list item which contains a left icon, a title and a right icon
 * This is the header item from a list
 */
public class HeaderListItem extends AbstractListItem
{

	public static final int TYPE_HEADER = 0;
	private Bitmap rightIcon;

	public HeaderListItem(Bitmap icon, String title, Bitmap rightIcon)
	{
		super(icon, title);
		this.rightIcon = rightIcon;
	}

	public Bitmap getRightIcon()
	{
		return rightIcon;
	}

	public void setRightIcon(Bitmap rightIcon)
	{
		this.rightIcon = rightIcon;
	}

	public int getType()
	{
		return TYPE_HEADER;
	}
}
