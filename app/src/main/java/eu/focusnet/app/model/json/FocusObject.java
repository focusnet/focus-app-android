package eu.focusnet.app.model.json;

import java.io.Serializable;
import java.util.Date;
import java.util.MissingResourceException;

import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.service.DataManager;

/**
 * Created by admin on 06.07.2015.
 *
 * There are no setters on this object properties because we don't want it to be altered by
 * the application logic.
 */
public class FocusObject implements Serializable
{

	private String type,
			url,
			context,
			owner,
			editor;
	private String originalData;
	private int version;
	private Date creationDateTime,
			editionDateTime;
	private boolean active;

	protected FocusObject(String type, String url, String context, String owner, String editor, int version, Date creationDateTime, Date editionDateTime, boolean active)
	{

		User user;
		try {
			user = DataManager.getInstance().getUser();
		}
		catch (FocusMissingResourceException e) {
			throw new FocusInternalErrorException("Not allowed to not have a User at this stage.");
		}

		this.type = type;
		this.url = url;
		this.context = context;
		this.owner = owner == null ? user.toString() : owner;
		this.editor = editor == null ? user.toString() : editor;
		this.version = version;
		this.creationDateTime = creationDateTime != null ? creationDateTime : new Date();
		this.editionDateTime = editionDateTime != null ? editionDateTime : new Date();
		this.active = active;
	}

	public FocusObject()
	{
	}

	/**
	 * Create a new FocusObject, based on the provided JSON string and target class.
	 */
	public static FocusObject factory(String json, Class targetClass)
	{
		FocusObject fo = (FocusObject) DataManager.getInstance().getGson().fromJson(json, targetClass);
		fo.setOriginalData(json);
		return fo;
	}

	public void updateToNewVersion()
	{
		++this.version;
		// update editor, editiontime, etc.

		// FIXME FIFXME TODO when we modify an object, we should update its original_data string
	}

	public String getType()
	{
		return type;
	}

	public String getUrl()
	{
		return url;
	}

	public String getOwner()
	{
		return owner;
	}

	public String getEditor()
	{
		return editor;
	}

	public int getVersion()
	{
		return version;
	}

	public Date getCreationDateTime()
	{
		return creationDateTime;
	}

	public Date getEditionDateTime()
	{
		return editionDateTime;
	}

	public boolean isActive()
	{
		return active;
	}

	public String getContext()
	{
		return context;
	}

	public String getOriginalData()
	{
		return originalData;
	}

	private void setOriginalData(String original_data)
	{
		this.originalData = original_data;
	}
}
