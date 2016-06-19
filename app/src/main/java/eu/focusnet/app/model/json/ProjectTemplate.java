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
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by admin on 03.08.2015.
 */

@SuppressWarnings("unused")
public class ProjectTemplate implements Serializable
{

	private String guid;
	private String iterator;
	private String title;
	private String description;

	private LinkedHashMap<String, String> data = new LinkedHashMap<>();

	private ArrayList<WidgetTemplate> widgets;
	private ArrayList<PageTemplate> pages;

	private ArrayList<PageReference> dashboards;
	private ArrayList<PageReference> tools;
	private ArrayList<ProjectTemplate> projects = new ArrayList<>();
	private boolean disabled;

	public String getGuid()
	{
		return guid;
	}

	public String getIterator()
	{
		return iterator;
	}

	public String getTitle()
	{
		return title;
	}

	public String getDescription()
	{
		return description;
	}

	public ArrayList<WidgetTemplate> getWidgets()
	{
		return widgets;
	}

	public ArrayList<PageReference> getDashboards()
	{
		return dashboards;
	}

	public ArrayList<PageReference> getTools()
	{
		return tools;
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

	public ArrayList<ProjectTemplate> getProjects()
	{
		return this.projects;
	}

	public boolean isDisabled()
	{
		return this.disabled;
	}
}
