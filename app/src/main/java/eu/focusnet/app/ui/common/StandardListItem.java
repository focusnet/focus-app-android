package eu.focusnet.app.ui.common;

import android.graphics.Bitmap;

/**
 * Standard list item which contains
 * This is the standard item from a list
 */
public class StandardListItem extends AbstractListItem
{

	public static final int TYPE_STANDARD = 1;
	private String path;
	private String info;
	private Bitmap rightIcon;
	private boolean isRightIconActive;
	private String typeOfBookmark; //no to good
	private int order; //no to good

	public StandardListItem(String absolutePath, Bitmap icon, String title, String info)
	{
		super(icon, title);
		this.info = info;
		this.path = absolutePath;
	}

	public StandardListItem(String absolutePath, Bitmap icon, String title, String info, int order, Bitmap rightIcon, boolean isRightIconActive, String typeOfBookmark)
	{
		this(absolutePath, icon, title, info);
		this.order = order;
		this.rightIcon = rightIcon;
		this.isRightIconActive = isRightIconActive;
		this.typeOfBookmark = typeOfBookmark;
	}


	public Bitmap getRightIcon()
	{
		return rightIcon;
	}

	public void setRightIcon(Bitmap rightIcon)
	{
		this.rightIcon = rightIcon;
	}

	public String getInfo()
	{
		return info;
	}

	public void setInfo(String info)
	{
		this.info = info;
	}

	public int getOrder()
	{
		return order;
	}

	public void setOrder(int order)
	{
		this.order = order;
	}

	public boolean isRightIconActive()
	{
		return isRightIconActive;
	}

	public void setIsRightIconActive(boolean isRightIconActive)
	{
		this.isRightIconActive = isRightIconActive;
	}

	public String getTypeOfBookmark()
	{
		return typeOfBookmark;
	}

	public void setTypeOfBookmark(String typeOfBookmark)
	{
		this.typeOfBookmark = typeOfBookmark;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	@Override
	public int getType()
	{
		return TYPE_STANDARD;
	}
}