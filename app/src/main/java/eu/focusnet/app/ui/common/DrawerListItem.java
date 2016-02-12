package eu.focusnet.app.ui.common;

import android.graphics.Bitmap;

/**
 * Drawer list item which contains an icon, a title and an info
 * This is the standard list drawer list item
 */
public class DrawerListItem extends AbstractListItem
{

	public static final int TYPE_STANDARD = 1;
	private String info;

	public DrawerListItem(Bitmap icon, String title, String info)
	{
		super(icon, title);
		this.info = info;
	}

	public String getInfo()
	{
		return info;
	}

	public void setInfo(String info)
	{
		this.info = info;
	}

	@Override
	public int getType()
	{
		return TYPE_STANDARD;
	}
}

