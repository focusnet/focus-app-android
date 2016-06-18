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

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

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
 */
public class ProjectInstance extends AbstractInstance
{
	public final static String LABEL_PROJECT_ITERATOR = "$project-iterator$";

	private String guid;
	private String title;
	private String description;

	private LinkedHashMap<String, PageInstance> dashboards;
	private LinkedHashMap<String, PageInstance> tools;
	private LinkedHashMap<String, ProjectInstance> projects;

	private ProjectTemplate template;

	/**
	 * C'tor
	 *
	 * @param projectTemplate
	 * @param dataContext     we use the datamanager of this context as we will build the new instance on top of the old context, so using the same data manager makes sense
	 */
	public ProjectInstance(ProjectTemplate projectTemplate, @NonNull DataContext dataContext)
	{
		super(dataContext.getDataManager());

		this.template = projectTemplate;
		this.dataContext = dataContext;
		this.guid = projectTemplate.getGuid();
		this.dashboards = new LinkedHashMap<>();
		this.tools = new LinkedHashMap<>();
		this.projects = new LinkedHashMap<>();

		this.build();
	}

	public static LinkedHashMap<String, ProjectInstance> createProjects(ArrayList<ProjectTemplate> projectTemplates, DataContext baseContext)
	{
		ArrayList<ProjectInstance> projInstancesTemp = new ArrayList<>();
		for (ProjectTemplate projTpl : projectTemplates) {

			// Iterators use application-level data context list of urls
			// we cannot postpone fetching these ones. Let's do it now.
			if (projTpl.getIterator() != null) {
				ArrayList<String> urls;
				try {
					urls = TypesHelper.asArrayOfUrls(
							baseContext.resolve(projTpl.getIterator())
					);
				}
				catch (FocusMissingResourceException | FocusBadTypeException e) {
					// Resource not found or invalid iterator.
					// should not happen, but let's continue silently
					FocusApplication.reportError(e);
					continue;
				}

				for (String url : urls) {
					DataContext newCtx = new DataContext(baseContext);
					newCtx.register(ProjectInstance.LABEL_PROJECT_ITERATOR, url);

					// the guid is adapted in the ProjectInstance constructor
					ProjectInstance p = new ProjectInstance(projTpl, newCtx);
					projInstancesTemp.add(p);
				}
			}
			else {
				DataContext newCtx = new DataContext(baseContext);
				ProjectInstance p = new ProjectInstance(projTpl, newCtx);
				projInstancesTemp.add(p);
			}
		}


		// fill projects with real data
		// FIXME we could imagine putting this part in a separate thread, too, such that
		// we don't block the main thread.
		// Let's first see how are performance.
		// in the present state, it should process all projects in parallel before continuing to
		// the pages
		LinkedHashMap<String, ProjectInstance> projects = new LinkedHashMap<>();
		ArrayList<Future> futures = new ArrayList<>();
		for (ProjectInstance pi : projInstancesTemp) {
			Future f = pi.fillWithRealData();
			futures.add(f);
		}
		// wait for the future to return such that we have a valid guid.
		for (int i = 0; i < projInstancesTemp.size(); ++i) {
			ProjectInstance selectedProject = projInstancesTemp.get(i);
			try {
				futures.get(i).get();
			}
			catch (InterruptedException | ExecutionException e) {
				// application requested interruption, let's not take it personnally.
				continue; // FIXME check if this is sufficient
			}
			projects.put(selectedProject.getGuid(), selectedProject);
		}
		return projects;
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
		this.dataContext.provideData(this.template.getData()); // FIXME

		if (this.description == null) {
			this.description = "";
		}



		// 2x same same FIXME modularize better
		if (this.template.getDashboards() != null) {
			this.dashboards = this.createPageInstances(this.template.getDashboards(), PageInstance.PageType.DASHBOARD);
			if (this.dashboards == null) {
				this.markAsInvalid();
			}
			else {
				// if any page is invalid, mark this project as invalid.
				for (LinkedHashMap.Entry<String, PageInstance> entry : this.dashboards.entrySet()) {
					PageInstance pi = entry.getValue();
					if (!pi.isValid()) {
						this.markAsInvalid();
					}
				}
			}
		}

		if (this.template.getTools() != null) {
			this.tools = this.createPageInstances(this.template.getTools(), PageInstance.PageType.TOOL);
			if (this.tools == null) {
				this.markAsInvalid();
			}
			else {
				// if any page is invalid, mark this project as invalid.
				for (LinkedHashMap.Entry<String, PageInstance> entry : this.tools.entrySet()) {
					PageInstance pi = entry.getValue();
					if (!pi.isValid()) {
						this.markAsInvalid();
					}
				}
			}
		}

		// possible to have a project within a project
		if (this.template.getProjects() != null) {
			this.projects = ProjectInstance.createProjects(this.template.getProjects(), this.dataContext);
			for (Map.Entry e : this.projects.entrySet()) {
				ProjectInstance pi = (ProjectInstance) e.getValue();
				if (!pi.isValid()) {
					this.markAsInvalid();
				}
			}
		}

	}

	// FIXME abstract method?
	public Future fillWithRealData()
	{
		// post-pone setting information after having fetched all resources related to this object
		Callable todo = new Callable()
		{
			@Override
			public Boolean call() throws Exception
			{
				if (template.getIterator() != null) {
					guid = guid + Constant.PATH_SELECTOR_OPEN + dataContext.get(LABEL_PROJECT_ITERATOR) + Constant.PATH_SELECTOR_CLOSE;
				}

				try { // FIXME
					title = TypesHelper.asString(dataContext.resolve(template.getTitle()));
					description = TypesHelper.asString(dataContext.resolve(template.getDescription()));
				}
				catch (FocusMissingResourceException | FocusBadTypeException ex) {
					// silent skipping
					FocusApplication.reportError(ex);
					return false;
				}

				freeDataContext();
				return true;
			}
		};

		FutureTask future = new FutureTask<>(todo);
		this.dataContext.toExecuteWhenReady(future);
		return future;
	}

	/**
	 * Create a LinkedHashMap of PageInstance based on the provided source definition.
	 *
	 * @param source
	 * @return FIXME a bit strange to have it
	 */
	private LinkedHashMap<String, PageInstance> createPageInstances(ArrayList<PageReference> source, PageInstance.PageType type)
	{
		ArrayList<PageInstance> pageInstancesTmp = new ArrayList<>();

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
				catch (FocusMissingResourceException | FocusBadTypeException e) {
					// should not happen, but let's continue silently
					FocusApplication.reportError(e);
					continue;
				}
				for (String url : urls) {
					DataContext newPageCtx = new DataContext(this.dataContext);
					newPageCtx.register(PageInstance.LABEL_PAGE_ITERATOR, url);

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

						// we can simply pass the same DataContext as widgets do not augment it (or alter it)
						WidgetInstance wi = WidgetInstance.factory(wTpl, wr.getLayout(), newPageCtx);
						page.addWidget(wi.getGuid(), wi);
					}

					pageInstancesTmp.add(page);
				}
			}
			else {
				// no iterator, render a simple PageInstance
				// FIXME almost the same, modularize
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

					// we can simply pass the same DataContext as widgets do not augment it (or alter it)
					WidgetInstance wi = WidgetInstance.factory(wTpl, wr.getLayout(), newPageCtx);
					page.addWidget(wi.getGuid(), wi);
				}

				pageInstancesTmp.add(page);
			}
		}

		// All template information have been gathered, let's know build the actual
		// objects with real data.
		// We do that AFTER having filled the basic information in the contructor to avoid
		// sequences of datacontext.register() / datacontext.get() that would end up in
		// sequential loading of resources. Also, this function is called outside of the loops
		// for the same reason.
		// Ideally, no datacontext.get() (or resolve()) should occure before this point.
		LinkedHashMap<String, PageInstance> pageInstances = new LinkedHashMap<>();
		ArrayList<Future> futures = new ArrayList<>();
		for (PageInstance pi : pageInstancesTmp) {
			Future f = pi.fillWithRealData();
			futures.add(f);
		}
		// wait for the future to return such that we have a valid guid.
		for (int i = 0; i < pageInstancesTmp.size(); ++i) {
			try {
				futures.get(i).get();
			}
			catch (InterruptedException | ExecutionException e) {
				// application requested interruption, let's not take it personnally.
				continue; // FIXME check if this is sufficient
			}
			pageInstances.put(pageInstancesTmp.get(i).getGuid(), pageInstancesTmp.get(i));
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
	 * @param type         Either PageInstance.PageType.DASHBOARD or PageInstance.PageType.TOOL
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

	/**
	 * Return the application projects instances.
	 *
	 * @return
	 */
	public LinkedHashMap<String, ProjectInstance> getProjects()
	{
		return this.projects;
	}

	@Override
	protected AbstractInstance propagatePathLookup(String searchedPath)
	{
		ArrayList<LinkedHashMap> sources = new ArrayList<>();
		sources.add(this.dashboards);
		sources.add(this.tools);
		sources.add(this.projects);
		for (LinkedHashMap<String, AbstractInstance> source : sources) {
			for (Map.Entry e : source.entrySet()) {
				AbstractInstance i = (AbstractInstance) e.getValue();
				AbstractInstance ret = i.lookupByPath(searchedPath);
				if (ret != null) {
					return ret;
				}
			}
		}
		return null;
	}


	@Override
	public void buildPaths(String parentPath)
	{
		this.path = parentPath + Constant.PATH_SEPARATOR + this.guid;
		ArrayList<LinkedHashMap> sources = new ArrayList<>();
		sources.add(this.dashboards);
		sources.add(this.tools);
		sources.add(this.projects);
		for (LinkedHashMap<String, AbstractInstance> source : sources) {
			for (Map.Entry e : source.entrySet()) {
				AbstractInstance i = (AbstractInstance) e.getValue();
				i.buildPaths(this.path);
			}
		}
	}
}
