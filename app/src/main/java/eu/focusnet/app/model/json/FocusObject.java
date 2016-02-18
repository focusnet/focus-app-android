package eu.focusnet.app.model.json;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by admin on 06.07.2015.
 */
public class FocusObject implements Serializable
{

	private String type,
			url,
			context,
			owner,
			editor;

	private int version;

	private Date creationDateTime,
			editionDateTime;

	private boolean active;

	// this field is not in the database, but is used for manual garbage collection of our cache.
	private Date lastUsage;

	public FocusObject(String type, String url, String context, String owner, String editor, int version, Date creationDateTime, Date editionDateTime, boolean active)
	{
		this.type = type;
		this.url = url;
		this.context = context;
		this.owner = owner;
		this.editor = editor;
		this.version = version;
		this.creationDateTime = creationDateTime != null ? creationDateTime : new Date();
		this.editionDateTime = editionDateTime != null ? editionDateTime : new Date();
		this.active = active;

		this.lastUsage = new Date();
	}

	public FocusObject()
	{
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getOwner()
	{
		return owner;
	}

	public void setOwner(String owner)
	{
		this.owner = owner;
	}

	public String getEditor()
	{
		return editor;
	}

	public void setEditor(String editor)
	{
		this.editor = editor;
	}

	public int getVersion()
	{
		return version;
	}

	public void setVersion(int version)
	{
		this.version = version;
	}

	public Date getCreationDateTime()
	{
		return creationDateTime;
	}

	public void setCreationDateTime(Date creationDateTime)
	{
		this.creationDateTime = creationDateTime;
	}

	public Date getEditionDateTime()
	{
		return editionDateTime;
	}

	public void setEditionDateTime(Date editionDateTime)
	{
		this.editionDateTime = editionDateTime;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public String getContext()
	{
		return context;
	}

	public void setContext(String context)
	{
		this.context = context;
	}

	public Date getLastUsage()
	{
		return lastUsage;
	}

	public void updateLastUsage()
	{
		this.lastUsage = new Date();
	}
}
