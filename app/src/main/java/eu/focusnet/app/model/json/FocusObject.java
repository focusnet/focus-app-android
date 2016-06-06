/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.model.json;

import java.io.Serializable;
import java.util.Date;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.exception.FocusMissingResourceException;

/**
 * Created by admin on 06.07.2015.
 * <p/>
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

	public FocusObject()
	{

	}

	protected FocusObject(String type, String url, String context, String owner, String editor, int version, Date creationDateTime, Date editionDateTime, boolean active)
	{

		User user;
		try {
			user = FocusApplication.getInstance().getDataManager().getUser();
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

	/**
	 * Create a new FocusObject, based on the provided JSON string and target class.
	 */
	public static FocusObject factory(String json, Class targetClass)
	{
		FocusObject fo = (FocusObject) FocusApplication.getInstance().getDataManager().getGson().fromJson(json, targetClass);
		fo.setOriginalData(json);
		return fo;
	}

	/**
	 * Update the current object to highlight the fact that we are providing a new version (sampe) of the same resource
	 * <p/>
	 * We do not commit() yet, as this is done once the resource is submitted for POST/PUT-ing (i.e. to the DataManager)
	 */
	public void updateToNewVersion()
	{
		++this.version;
		User user;
		try {
			user = FocusApplication.getInstance().getDataManager().getUser();
		}
		catch (FocusMissingResourceException e) {
			throw new FocusInternalErrorException("Not allowed to not have a User at this stage.");
		}
		this.editor = user.toString();
		this.editionDateTime = new Date();
		this.commit();
	}

	/**
	 * Committing means that the current object is considered a valid entity in the FOCUS context,
	 * so we update the original_data to reflect this new state.
	 */
	public void commit()
	{
		this.originalData = ""; // avoid to store useless data
		this.originalData = FocusApplication.getInstance().getDataManager().getGson().toJson(this);
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

	@Override
	public String toString()
	{
		// avoid to store useless data
		String tmp = this.originalData;
		this.originalData = "";
		String ret = FocusApplication.getInstance().getDataManager().getGson().toJson(this);
		this.originalData = tmp;
		return ret;
	}

}
