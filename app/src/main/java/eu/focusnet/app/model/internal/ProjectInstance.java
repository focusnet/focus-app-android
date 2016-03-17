/**
 *
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
 *
 */

package eu.focusnet.app.model.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.exception.FocusBadTypeException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.util.Constant;
import eu.focusnet.app.model.json.Linker;
import eu.focusnet.app.model.json.PageTemplate;
import eu.focusnet.app.model.json.ProjectTemplate;
import eu.focusnet.app.model.json.WidgetLinker;
import eu.focusnet.app.model.json.WidgetTemplate;
import eu.focusnet.app.model.internal.widgets.WidgetInstance;
import eu.focusnet.app.model.util.TypesHelper;

/**
 * Created by julien on 12.01.16.
 */
public class ProjectInstance extends AbstractInstance
{
	public final static String LABEL_PROJECT_ITERATOR = "$project-iterator$";
	public final static String WELCOME_PROJECT_IDENTIFIER = "__welcome__";

	private String guid;
	private String title; // should be SmartString -> resolved on toString() call
	private String description;

	private LinkedHashMap<String, PageInstance> dashboards;
	private LinkedHashMap<String, PageInstance> tools;

	private ProjectTemplate template;

	/**
	 * C'tor
	 *
	 * @param tpl
	 * @param dataContext
	 */
	public ProjectInstance(ProjectTemplate tpl, DataContext dataContext)
	{
		this.template = tpl;
		this.dataContext = dataContext;
		this.guid = tpl.getGuid();
		this.dashboards = new LinkedHashMap<String, PageInstance>();
		this.tools = new LinkedHashMap<String, PageInstance>();
		if (dataContext.get(LABEL_PROJECT_ITERATOR) != null) {
			this.guid = this.guid + Constant.PATH_SELECTOR_OPEN + dataContext.get(LABEL_PROJECT_ITERATOR) + Constant.PATH_SELECTOR_CLOSE;
		}

		this.build();
	}

	/**
	 * Get the GUID for this project, in the context of the app
	 */
	public String getGuid()
	{
		return this.guid;
	}

	/**
	 * Build the PageInstance's for this project.
	 */
	private void build()
	{
		// register the project-specific data to our data context
		try {
			this.dataContext.provideData(this.template.getData());
		}
		catch (FocusMissingResourceException ex) {
			// should not happen, but let's continue silently
			FocusApplication.reportError(ex);
			return;
		}

		// FIXME TODO special case ?: guid == __welcome__ == WELCOME_PROJECT_IDENTIFIER
		if (this.getGuid().equals(WELCOME_PROJECT_IDENTIFIER)) {
			this.title = ""; // FIXME i18n string
			this.description = ""; // FIXME i18n string.
			return;
		}

		if (this.template.getDashboards() != null) {
			this.dashboards = this.createPageInstances(this.template.getDashboards(), PageInstance.PageType.DASHBOARD);
		}
		if (this.template.getTools() != null) {
			this.tools = this.createPageInstances(this.template.getTools(), PageInstance.PageType.TOOL);
		}
	}

	/**
	 * Create a LinkedHashMap of PageInstance based on the provided source definition.
	 *
	 * @param source
	 * @return
	 */
	private LinkedHashMap<String, PageInstance> createPageInstances(ArrayList<Linker> source, PageInstance.PageType type)
	{
		LinkedHashMap<String, PageInstance> ret = new LinkedHashMap<>();

		for (Linker s : source) {
			String page_id = s.getPageid();
			PageTemplate pageTpl = this.template.findPage(page_id);

			// if we have an iterator, this means that we must construct multiple times the same page,
			// but with a different data context each time
			if (pageTpl.getIterator() != null) {
				ArrayList<String> urls;
				try {
					urls = TypesHelper.asArrayOfUrls(
							this.dataContext.resolve(pageTpl.getIterator())
					);
				}
				catch (FocusMissingResourceException e) {
					// should not happen, but let's continue silently
					FocusApplication.reportError(e);
					continue;
				}
				catch (FocusBadTypeException e) {
					// invalid iterator. survive by ignoring, but log the event
					FocusApplication.reportError(e);
					continue;
				}
				for (String url : urls) {
					DataContext new_page_ctx = new DataContext(this.dataContext);
					try {
						new_page_ctx.register(PageInstance.LABEL_PAGE_ITERATOR, url);
					}
					catch (FocusMissingResourceException ex) {
						// should not happen, but let's continue silently
						FocusApplication.reportError(ex);
						continue;
					}

					LinkedHashMap<String, WidgetInstance> widgets = new LinkedHashMap<String, WidgetInstance>();
					for (WidgetLinker wl : pageTpl.getWidgets()) {
						WidgetTemplate wTpl = this.template.findWidget(wl.getWidgetid());
						DataContext new_widget_ctx = new DataContext(new_page_ctx);
						WidgetInstance wi = WidgetInstance.factory(wTpl, wl.getLayout(), new_widget_ctx); // FIXME TODO will throw exception if misconfiguration of widget -> do not include in widgets list -> do not include PageInstance at all
						widgets.put(wi.getGuid(), wi); // FIXME if widget-id is same as a previous widget, we won't have 2x the widget.
					}
					// the guid is adapted in the PageInstance constructor
					PageInstance p;
					try {
						p = new PageInstance(pageTpl, type, widgets, new_page_ctx);
					}
					catch (FocusMissingResourceException ex) {
						FocusApplication.reportError(ex);
						continue;
					}
					ret.put(p.getGuid(), p);
				}
			}
			else {
				// no iterator, render a simple PageInstance
				DataContext new_page_ctx = new DataContext(this.dataContext);
				LinkedHashMap<String, WidgetInstance> widgets = new LinkedHashMap<String, WidgetInstance>();
				for (WidgetLinker wl : pageTpl.getWidgets()) {
					WidgetTemplate wTpl = this.template.findWidget(wl.getWidgetid());
					DataContext new_widget_ctx = new DataContext(new_page_ctx);
					WidgetInstance wi = WidgetInstance.factory(wTpl, wl.getLayout(), new_widget_ctx);
					widgets.put(wi.getGuid(), wi);
				}
				PageInstance p;
				try {
					p = new PageInstance(pageTpl, type, widgets, new_page_ctx);
				}
				catch (FocusMissingResourceException ex) {
					FocusApplication.reportError(ex);
					continue;
				}
				ret.put(p.getGuid(), p);
			}
		}
		return ret;
	}

	/**
	 * Get the page identified by the specified guid, which may contain an iterator specifier
	 * e.g.
	 * - my-project
	 * - my-project[http://www.example.org/data/123]
	 *
	 * @param expanded_guid
	 * @param type          Either PageInstance.PageType.DASHBOARD or PageInstance.PageType.TOOL
	 * @return
	 */
	public PageInstance getPageFromGuid(String expanded_guid, String type)
	{
		PageInstance.PageType p1 = PageInstance.PageType.DASHBOARD;
		String t = type;
		String t2 = p1.toString();
		if (type.equals(PageInstance.PageType.DASHBOARD.toString())) {
			PageInstance p = this.dashboards.get(expanded_guid);
			return p;
		}
		if (type.equals(PageInstance.PageType.TOOL.toString())) {
			return this.tools.get(expanded_guid);
		}
		return null;
	}

	public String getTitle()
	{
		return title;
	}

	public String getDescription()
	{
		return this.description;
	}

	public LinkedHashMap<String, PageInstance> getDashboards()
	{
		return this.dashboards;
	}

	public LinkedHashMap<String, PageInstance> getTools()
	{
		return this.tools;
	}
}
