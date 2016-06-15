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

package eu.focusnet.app.model.json;

import java.io.Serializable;
import java.util.Date;

import eu.focusnet.app.FocusAppLogic;

/**
 * Created by admin on 06.07.2015.
 * <p/>
 * There are no setters on this object properties because we don't want it to be altered by
 * the application logic.
 */
public class FocusObject implements Serializable
{

	/**
	 * Safe to use this user like this, as it will be used for data creation,
	 * and therefore this User should represent the one that is currently used by the application
	 * and not another one
	 *
	 * FIXME see if we can use a more elegant pattern.
	 */
	final private static User user = FocusAppLogic.getUserManager().getUser();
	private String type,
			url,
			owner,
			editor;
	private int version;
	private Date creationDateTime,
			editionDateTime;
	private boolean active;

	public FocusObject()
	{

	}

	/**
	 * Create a new active FocusObject belonging to the current user
	 */
	protected FocusObject(String type, String url)
	{
		this(type, url, null, null, 1, null, null, true);
	}

	/**
	 * NOTE: if the url is internal, we do not set the following fields to anything: owner, editor
	 *
	 * @param type
	 * @param url
	 * @param owner
	 * @param editor
	 * @param version
	 * @param creationDateTime
	 * @param editionDateTime
	 * @param active
	 */
	protected FocusObject(String type, String url, String owner, String editor, int version, Date creationDateTime, Date editionDateTime, boolean active)
	{
		this.type = type;
		this.url = url;
		this.owner = owner == null ? user.toString() : owner;
		this.editor = editor == null ? user.toString() : editor;
		this.version = version;
		this.creationDateTime = creationDateTime != null ? creationDateTime : new Date();
		this.editionDateTime = editionDateTime != null ? editionDateTime : new Date();
		this.active = active;
	}

	/**
	 * Create a new FocusObject, based on the provided JSON string and target class.
	 */
	public static FocusObject factory(String json, Class targetClass)
	{
		return (FocusObject) FocusAppLogic.getGson().fromJson(json, targetClass);
	}

	/**
	 * Update the current object to highlight the fact that we are providing a new version (sampe) of the same resource
	 * <p/>
	 * We do not commit() yet, as this is done once the resource is submitted for POST/PUT-ing (i.e. to the DataManager)
	 */
	public void updateToNewVersion()
	{
		++this.version;
		this.editor = user.toString();
		this.editionDateTime = new Date();
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


	@Override
	public String toString()
	{
		return FocusAppLogic.getGson().toJson(this);
	}

}
