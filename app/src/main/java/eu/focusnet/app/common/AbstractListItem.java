package eu.focusnet.app.common;

import android.graphics.Bitmap;

/**
 * Created by admin on 17.06.2015.
 */
public abstract class AbstractListItem
{

	protected String title;
	protected Bitmap icon;

	public AbstractListItem(Bitmap icon, String title)
	{
		this.icon = icon;
		this.title = title;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public Bitmap getIcon()
	{
		return icon;
	}

	public void setIcon(Bitmap icon)
	{
		this.icon = icon;
	}

	public abstract int getType();
}
