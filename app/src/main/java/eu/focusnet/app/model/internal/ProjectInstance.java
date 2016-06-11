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

package eu.focusnet.app.model.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.exception.FocusBadTypeException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.internal.widgets.WidgetInstance;
import eu.focusnet.app.model.json.PageReference;
import eu.focusnet.app.model.json.PageTemplate;
import eu.focusnet.app.model.json.ProjectTemplate;
import eu.focusnet.app.model.json.WidgetReference;
import eu.focusnet.app.model.json.WidgetTemplate;
import eu.focusnet.app.model.util.Constant;
import eu.focusnet.app.model.util.TypesHelper;

/**
 * Created by julien on 12.01.16.
 */
public class ProjectInstance extends AbstractInstance
{
	public final static String LABEL_PROJECT_ITERATOR = "$project-iterator$";
	public final static String WELCOME_PROJECT_IDENTIFIER = "__welcome__";

	private String guid;
	private String title;
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
		super();

		this.template = tpl;
		this.dataContext = dataContext;
		if (this.dataContext == null) {
			this.dataContext = new DataContext();
		}
		this.guid = tpl.getGuid();
		this.dashboards = new LinkedHashMap<String, PageInstance>();
		this.tools = new LinkedHashMap<String, PageInstance>();
		if (dataContext.get(LABEL_PROJECT_ITERATOR) != null) {
			this.guid = this.guid + Constant.PATH_SELECTOR_OPEN + dataContext.get(LABEL_PROJECT_ITERATOR) + Constant.PATH_SELECTOR_CLOSE;
		}

		this.build();
		this.freeDataContext();
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

		// Special case: __welcome__ is a fake project that may contain tools only.
		if (this.getGuid().equals(WELCOME_PROJECT_IDENTIFIER)) {
			this.title = "";
			this.description = "";
			return;
		}

		if (this.template.getDashboards() != null) {
			this.dashboards = this.createPageInstances(this.template.getDashboards(), PageInstance.PageType.DASHBOARD);
			// if any page is invalid, mark this project as invalid.
			for (LinkedHashMap.Entry<String, PageInstance> entry : this.dashboards.entrySet()) {
				if (!entry.getValue().isValid()) {
					this.markAsInvalid();
					break;
				}
			}
		}

		if (this.template.getTools() != null) {
			this.tools = this.createPageInstances(this.template.getTools(), PageInstance.PageType.TOOL);
			// if any page is invalid, mark this project as invalid.
			if (this.isValid()) {
				for (LinkedHashMap.Entry<String, PageInstance> entry : this.tools.entrySet()) {
					if (!entry.getValue().isValid()) {
						this.markAsInvalid();
						break;
					}
				}
			}
		}
	}

	/**
	 * Create a LinkedHashMap of PageInstance based on the provided source definition.
	 *
	 * @param source
	 * @return
	 */
	private LinkedHashMap<String, PageInstance> createPageInstances(ArrayList<PageReference> source, PageInstance.PageType type)
	{
		LinkedHashMap<String, PageInstance> pageInstances = new LinkedHashMap<>();

		try {
			this.title = TypesHelper.asString(this.dataContext.resolve(this.template.getTitle()));
			this.description = TypesHelper.asString(this.dataContext.resolve(this.template.getDescription()));
		}
		catch (FocusMissingResourceException | FocusBadTypeException ex) {
			FocusApplication.reportError(ex);
			return null;
		}

		if (this.description == null) {
			this.description = "";
		}

		for (PageReference s : source) {
			String pageid = s.getPageid();
			PageTemplate pageTpl = this.template.findPage(pageid);

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
					DataContext newPageCtx = new DataContext(this.dataContext);
					try {
						newPageCtx.register(PageInstance.LABEL_PAGE_ITERATOR, url);
					}
					catch (FocusMissingResourceException ex) {
						// should not happen, but let's continue silently
						FocusApplication.reportError(ex);
						continue;
					}

					// the guid is adapted in the PageInstance constructor
					PageInstance page;
					try {
						page = new PageInstance(pageTpl, type, newPageCtx);
					}
					catch (FocusMissingResourceException ex) {
						FocusApplication.reportError(ex);
						continue;
					}

					for (WidgetReference wr : pageTpl.getWidgets()) {
						WidgetTemplate wTpl = this.template.findWidget(wr.getWidgetid());

						DataContext newWidgetCtx = new DataContext(page.getDataContext());
						WidgetInstance wi = WidgetInstance.factory(wTpl, wr.getLayout(), newWidgetCtx); // FIXME we may just pass the context without creating a new one
						page.addWidget(wi.getGuid(), wi);
					}

					page.freeDataContext();

					pageInstances.put(page.getGuid(), page);
				}
			}
			else {
				// no iterator, render a simple PageInstance
				PageInstance page;
				DataContext newPageCtx = new DataContext(this.dataContext);
				try {
					page = new PageInstance(pageTpl, type, newPageCtx);
				}
				catch (FocusMissingResourceException ex) {
					FocusApplication.reportError(ex);
					continue;
				}

				for (WidgetReference wr : pageTpl.getWidgets()) {
					WidgetTemplate wTpl = this.template.findWidget(wr.getWidgetid());
					DataContext newWidgetCtx = new DataContext(page.getDataContext());
					WidgetInstance wi = WidgetInstance.factory(wTpl, wr.getLayout(), newWidgetCtx);// FIXME we may just pass the context without creating a new one
					page.addWidget(wi.getGuid(), wi);
				}

				page.freeDataContext();

				pageInstances.put(page.getGuid(), page);
			}
		}
		return pageInstances;
	}

	/**
	 * Get the page identified by the specified guid, which may contain an iterator specifier
	 * e.g.
	 * - my-project
	 * - my-project[http://www.example.org/data/123]
	 *
	 * @param expandedGuid
	 * @param type          Either PageInstance.PageType.DASHBOARD or PageInstance.PageType.TOOL
	 * @return
	 */
	public PageInstance getPageFromGuid(String expandedGuid, String type)
	{
		if (type.equals(PageInstance.PageType.DASHBOARD.toString())) {
			return this.dashboards.get(expandedGuid);
		}
		if (type.equals(PageInstance.PageType.TOOL.toString())) {
			return this.tools.get(expandedGuid);
		}
		return null;
	}

	public String getTitle()
	{
		return this.title;
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
