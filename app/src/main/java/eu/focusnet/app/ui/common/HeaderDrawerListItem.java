package eu.focusnet.app.ui.common;

import android.graphics.Bitmap;

/**
 * Drawer header item which contains an icon, the username, email and the company, the user belongs to
 * This is the header drawer item
 */
public class HeaderDrawerListItem extends AbstractListItem
{

	public static final int TYPE_HEADER_DRAWER = 0;
	private String email, company;

	public HeaderDrawerListItem(Bitmap logo, String user, String email, String company)
	{
		super(logo, user);
		this.email = email;
		this.company = company;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getCompany()
	{
		return company;
	}

	public void setCompany(String company)
	{
		this.company = company;
	}

	@Override
	public int getType()
	{
		return TYPE_HEADER_DRAWER;
	}
}
