/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.model.gson;

import java.io.Serializable;
import java.util.Date;

import eu.focusnet.app.controller.FocusAppLogic;
import eu.focusnet.app.model.UserInstance;

/**
 * The basic object for all FOCUS-compliant objects.
 *
 * Refer to JSON Schema for further documentation.
 * See https://github.com/focusnet/focus-data-mode
 *
 * FIXME we use a static reference to the current {@link UserInstance}. Would it be possible to use
 * FIXME a better pattern?
 */
public class FocusObject implements Serializable
{

	protected String owner,
			editor;
	private String type,
			url;
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
	 * if a user exists, use it. otherwise, just leave the owner and editor
	 *
	 * @param type The FOCUS type (i.e. the URL of the JSON schema against which the resource
	 *             validates)
	 * @param url The URL identifying this resource
	 * @param owner If this field is set, use it for specifying the owner of the resource. If not,
	 *              then use the current {@link UserInstance} of the application. If there is
	 *              no {@link UserInstance}, then leave this field empty ({@code ""}).
	 * @param editor Same logic as for the {@link #owner} applies.
	 * @param version Current version number of the resource
	 * @param creationDateTime Creation date time as a {@code Date}. If none set, use the current
	 *                         date and time.
	 * @param editionDateTime Edition date time as a {@code Date}. If none set, use the current
	 *                         date and time.
	 * @param active Tells whether the resource is active (=accessible) or not.
	 */
	protected FocusObject(String type, String url, String owner, String editor, int version, Date creationDateTime, Date editionDateTime, boolean active)
	{
		UserInstance user = FocusAppLogic.getUserManager().getUserAsIs();
		this.type = type;
		this.url = url;
		this.owner = (owner != null ? owner : (user != null ? user.toString() : ""));
		this.editor = (editor != null ? editor : (user != null ? user.toString() : ""));
		this.version = version;
		this.creationDateTime = creationDateTime != null ? creationDateTime : new Date();
		this.editionDateTime = editionDateTime != null ? editionDateTime : new Date();
		this.active = active;
	}

	/**
	 * Create a new FocusObject, based on the provided JSON string and target class.
	 *
	 * @param json Raw JSON String representation of the object
	 * @param targetClass The target POJO class to convert the {@code json} input into
	 */
	public static FocusObject factory(String json, Class targetClass)
	{
		return (FocusObject) FocusAppLogic.getGson().fromJson(json, targetClass);
	}

	/**
	 * Update the current object to highlight the fact that we are providing a new version of
	 * the same resource
	 *
	 * The user is the current {@link UserInstance} of the application.
	 */
	public void updateToNewVersion()
	{
		UserInstance user = FocusAppLogic.getUserManager().getUserAsIs();
		++this.version;
		if (user != null) {
			this.editor = user.toString();
		}
		else {
			this.editor = "";
		}
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

	/**
	 * Stringify the object: return its serizalization by GSON.
	 *
	 * @return A JSON string
	 */
	@Override
	public String toString()
	{
		return FocusAppLogic.getGson().toJson(this);
	}

}
