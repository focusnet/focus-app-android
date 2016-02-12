package eu.focusnet.app.model.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by admin on 03.08.2015.
 */
public class ProjectTemplate implements Serializable
{

	private String guid;
	private String iterator; //TODO define this object
	private String title;
	private String description;

	private LinkedHashMap<String, String> data = null;

	private int order;

	private ArrayList<WidgetTemplate> widgets;
	private ArrayList<PageTemplate> pages;

	private ArrayList<Linker> dashboards;
	private ArrayList<Linker> tools;

	private ArrayList<Notification> notifications;

	public String getGuid()
	{
		return guid;
	}

	public void setGuid(String guid)
	{
		this.guid = guid;
	}

	public String getIterator()
	{
		return iterator;
	}

	public void setIterator(String iterator)
	{
		this.iterator = iterator;
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

	public int getOrder()
	{
		return order;
	}

	public void setOrder(int order)
	{
		this.order = order;
	}

	public ArrayList<WidgetTemplate> getWidgets()
	{
		return widgets;
	}

	public void setWidgets(ArrayList<WidgetTemplate> widgets)
	{
		this.widgets = widgets;
	}

	public ArrayList<PageTemplate> getPages()
	{
		return pages;
	}

	public void setPages(ArrayList<PageTemplate> pages)
	{
		this.pages = pages;
	}

	public ArrayList<Linker> getDashboards()
	{
		return dashboards;
	}

	public void setDashboards(ArrayList<Linker> dashboards)
	{
		this.dashboards = dashboards;
	}

	public ArrayList<Linker> getTools()
	{
		return tools;
	}

	public void setTools(ArrayList<Linker> tools)
	{
		this.tools = tools;
	}

	public ArrayList<Notification> getNotifications()
	{
		return notifications;
	}

	public void setNotifications(ArrayList<Notification> notifications)
	{
		this.notifications = notifications;
	}

	/**
	 * Find the PageTemplate that is contained within this ProjectTemplate and that
	 * is identified by page_id.
	 *
	 * @param page_id
	 * @return
	 */
	public PageTemplate findPage(String page_id)
	{
		for (PageTemplate p : this.pages) {
			if (p.getGuid().equals(page_id)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Find the WidgetTemplate identified by the specified widgetid
	 *
	 * @param widgetid
	 * @return
	 */
	public WidgetTemplate findWidget(String widgetid)
	{
		for (WidgetTemplate w : this.widgets) {
			if (w.getGuid().equals(widgetid)) {
				return w;
			}
		}
		return null;
	}

	public LinkedHashMap<String, String> getData()
	{
		return data;
	}
}
