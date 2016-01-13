package eu.focusnet.app.model.focus;

import java.util.ArrayList;

/**
 * Created by admin on 03.08.2015.
 */
public class PageTemplate
{

	private String guid,
			title,
			description;

	private String iterator; //TODO define this object

	private ArrayList<WidgetLinker> widgets;

	public PageTemplate(String guid, String title, String description, ArrayList<WidgetLinker> widgets)
	{
		this.guid = guid;
		this.title = title;
		this.description = description;
		this.widgets = widgets;
	}

	public PageTemplate()
	{
	}

	public String getGuid()
	{
		return guid;
	}

	public void setGuid(String guid)
	{
		this.guid = guid;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public ArrayList<WidgetLinker> getWidgets()
	{
		return widgets;
	}

	public void setWidgets(ArrayList<WidgetLinker> widgets)
	{
		this.widgets = widgets;
	}

	public String getIterator()
	{
		return iterator;
	}
}
