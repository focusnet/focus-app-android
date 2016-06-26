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

package eu.focusnet.app.model.gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * A project template
 *
 * Refer to JSON Schema for further documentation.
 * See https://github.com/focusnet/focus-data-mode
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
	 * @param pageId The id of the page to find.
	 * @return The {@link PageTemplate} of interest or {@code null} if none is found.
	 */
	public PageTemplate findPage(String pageId)
	{
		for (PageTemplate p : this.pages) {
			if (p.getGuid().equals(pageId)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Find the WidgetTemplate identified by the specified widgetid
	 *
	 * @param widgetId The id of the widget to find.
	 * @return The {@link WidgetTemplate} of interest or {@code null} if none is found.
	 */
	public WidgetTemplate findWidget(String widgetId)
	{
		for (WidgetTemplate w : this.widgets) {
			if (w.getGuid().equals(widgetId)) {
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
