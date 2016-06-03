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

package eu.focusnet.app.model.store;

import java.util.Date;

import eu.focusnet.app.model.json.FocusObject;

/**
 * A Sample is the object representation of the rows in the "samples" database table
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
	private boolean toPut;
	private boolean toPost;

	/**
	 * Constructor
	 */
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
		this.toPut = toPush;
		this.toPost = toPost;
	}

	/**
	 * Dummy empty ctor
	 */
	public Sample()
	{
	}


	/**
	 * Clone a FocusObject into the present Sample
	 *
	 * @param fo the FocusObject to get inspiration from
	 *
	 */
	public static Sample cloneFromFocusObject(FocusObject fo)
	{
		return new Sample(
				null,
				fo.getUrl(),
				fo.getContext(),
				fo.getVersion(),
				fo.getType(),
				fo.getOwner(),
				fo.getCreationDateTime(),
				fo.getEditionDateTime(),
				fo.getEditor(),
				fo.isActive(),
				fo.getOriginalData(),
				false,
				false,
				false
		);
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

	public boolean isToPut()
	{
		return toPut;
	}

	public void setToPut(boolean toPut)
	{
		this.toPut = toPut;
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
