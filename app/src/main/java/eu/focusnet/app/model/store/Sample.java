package eu.focusnet.app.model.store;

import java.util.Date;

import eu.focusnet.app.model.json.FocusObject;

/**
 * Created by admin on 21.01.2016.
 */
public class Sample
{

	private Long id;
	private String url;
	private String context;
	private int version;
	private String type;
	private String owner;
	private Date creationDateTime;
	private Date editionDateTime;
	private String editor;
	private boolean active;
	private String data;
	private boolean toDelete;
	private boolean toPush;
	private boolean toPost;

	public Sample(Long id, String url, String context, int version,
				  String type, String owner,
				  Date creationDateTime, Date editionDateTime,
				  String editor, boolean active,
				  String data, boolean toDelete, boolean toPush, boolean toPost)
	{
		this.id = id;
		this.url = url;
		this.context = context;
		this.version = version;
		this.type = type;
		this.owner = owner;
		this.creationDateTime = creationDateTime;
		this.editionDateTime = editionDateTime;
		this.editor = editor;
		this.active = active;
		this.data = data;
		this.toDelete = toDelete;
		this.toPush = toPush;
		this.toPost = toPost;
	}

	public Sample()
	{
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public int getVersion()
	{
		return version;
	}

	public void setVersion(int version)
	{
		this.version = version;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getOwner()
	{
		return owner;
	}

	public void setOwner(String owner)
	{
		this.owner = owner;
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

	public String getEditor()
	{
		return editor;
	}

	public void setEditor(String editor)
	{
		this.editor = editor;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public String getData()
	{
		return data;
	}

	public void setData(String data)
	{
		this.data = data;
	}

	public boolean isToDelete()
	{
		return toDelete;
	}

	public void setToDelete(boolean toDelete)
	{
		this.toDelete = toDelete;
	}

	public boolean isToPush()
	{
		return toPush;
	}

	public void setToPush(boolean toPush)
	{
		this.toPush = toPush;
	}

	public void cloneFromFocusObject(FocusObject fo, String raw_data)
	{

		this.type = fo.getType();
		this.url = fo.getUrl();
		this.context = fo.getContext();
		this.owner = fo.getOwner();
		this.editor = fo.getEditor();
		this.version = fo.getVersion();
		this.creationDateTime = fo.getCreationDateTime();
		this.editionDateTime = fo.getEditionDateTime();
		this.active = fo.isActive();

		this.data = raw_data;

		this.toDelete = false;
		this.toPush = false;
	}

	public String getContext()
	{
		return context;
	}

	public boolean isToPost()
	{
		return this.toPost;
	}

	public void setToPost(boolean toPost)
	{
		this.toPost = toPost;
	}
}
