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

package eu.focusnet.app.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import eu.focusnet.app.controller.PriorityTask;
import eu.focusnet.app.model.gson.ProjectTemplate;
import eu.focusnet.app.ui.FocusApplication;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.FocusBadTypeException;
import eu.focusnet.app.util.FocusMissingResourceException;

/**
 * This object instantiates a project, out of a {@link ProjectTemplate}.
 */
public class ProjectInstance extends AbstractInstance
{

	/**
	 * Unique identifier for the project. If an iterator is defined for this project, it will be
	 * altered such that we are able to distinguish between the different versions of the project.
	 * <p/>
	 * See {@link eu.focusnet.app.util.Constant.Navigation},
	 * {@link ProjectInstance#createProjects(ArrayList, DataContext)}
	 * and {@link #fillWithAcquiredData()}
	 */
	private String guid;

	/**
	 * The title of the project.
	 */
	private String title;

	/**
	 * The description of the project
	 */
	private String description;

	/**
	 * The list of {@link PageInstance}s in the "dashboards" library of the project.
	 */
	private ArrayList<PageInstance> dashboards;

	/**
	 * The list of {@link PageInstance}s in the "tools" library of the project.
	 */
	private ArrayList<PageInstance> tools;

	/**
	 * The list of inner {@link ProjectInstance}s of the project: a project can contain projects.
	 */
	private ArrayList<ProjectInstance> projects;

	/**
	 * The template used to build the current project instance.
	 */
	private ProjectTemplate template;

	/**
	 * Tells whether this project is disabled and should consequently not be accessible.
	 */
	private boolean disabled;

	/**
	 * C'tor
	 *
	 * @param projectTemplate Template to use to contruct this instance.
	 * @param dataContext     The {@link DataContext} of this instance
	 */
	public ProjectInstance(ProjectTemplate projectTemplate, @NonNull DataContext dataContext)
	{
		super(dataContext.getDataManager());

		this.template = projectTemplate;
		this.dataContext = dataContext;
		this.guid = null;
		this.dashboards = new ArrayList<>();
		this.tools = new ArrayList<>();
		this.projects = new ArrayList<>();
		this.disabled = this.template.isDisabled();

		this.build();
	}

	/**
	 * Factory function for creating a set of projects out of a template.
	 *
	 * @param projectTemplates The template used to build the current project instances.
	 * @param parentContext    Parent context on the top of which we will define a new {@link DataContext} for created instances
	 * @return A list of new {@link ProjectInstance}s
	 */
	public static ArrayList<ProjectInstance> createProjects(ArrayList<ProjectTemplate> projectTemplates, DataContext parentContext)
	{
		ArrayList<ProjectInstance> projInstancesTemp = new ArrayList<>();
		for (ProjectTemplate projTpl : projectTemplates) {

			// Iterators use application-level data context list of urls
			// we cannot postpone fetching these ones. Let's do it now.
			if (projTpl.getIterator() != null) {
				ArrayList<String> urls;
				try {
					urls = parentContext.resolveToArrayOfUrls(projTpl.getIterator());
				}
				catch (FocusMissingResourceException | FocusBadTypeException e) {
					// Resource not found or invalid iterator.
					// continue silently
					continue;
				}

				ArrayList<DataContext> contexts = new ArrayList<>();
				for (String url : urls) {
					DataContext newCtx = new DataContext(parentContext);
					newCtx.registerIterator(projTpl.getGuid(), url);
					contexts.add(newCtx);
				}

				for (DataContext newCtx : contexts) {
					// the guid is adapted in the ProjectInstance constructor
					ProjectInstance p = new ProjectInstance(projTpl, newCtx);
					projInstancesTemp.add(p);
				}
			}
			else {
				DataContext newCtx = new DataContext(parentContext);
				ProjectInstance p = new ProjectInstance(projTpl, newCtx);
				projInstancesTemp.add(p);
			}
		}

		// fill projects with real data
		// this is done as parallel tasks
		// See similar logic in PageInstance
		for (ProjectInstance pi : projInstancesTemp) {
			pi.fillWithAcquiredData();
		}
		return projInstancesTemp;
	}

	/**
	 * Build the PageInstance's for this project.
	 */
	private void build()
	{
		// register the project-specific data to our data context
		// provide data called AFTER projects set iterator
		this.dataContext.provideData(this.template.getData()); // I don't wait for iterator here, and that's not good. SO iterators should ALWAYS have higher priority! FIXME

		if (this.description == null) {
			this.description = "";
		}

		// build the content of the project
		this.dashboards = PageInstance.createPageInstances(this.template, PageInstance.PageType.DASHBOARD, this.dataContext);
		this.tools = PageInstance.createPageInstances(this.template, PageInstance.PageType.TOOL, this.dataContext);
		this.projects = ProjectInstance.createProjects(this.template.getProjects(), this.dataContext);
	}

	/**
	 * Check that all the content is valid, and if this is not the case, mark this project instance
	 * as invalid.
	 * <p/>
	 * This method is intended to be called after the full application content has been built.
	 */
	public void checkValidity()
	{
		boolean isValid = true;
		for (PageInstance pi : this.dashboards) {
			isValid &= pi.isValid();
		}
		for (PageInstance pi : this.tools) {
			isValid &= pi.isValid();
		}
		for (ProjectInstance pi : this.projects) {
			pi.checkValidity();
			isValid &= pi.isValid();
		}
		if (!isValid) {
			this.markAsInvalid();
		}
	}


	/**
	 * Fill instance with data that have been acquired via {@link DataContext#register(String, String)}
	 *
	 * @return a {@code Future} on which we may listen to know if the operation is finished.
	 */
	private Future fillWithAcquiredData()
	{
		// post-pone setting information after having fetched all resources related to this object
		Callable todo = new Callable()
		{
			@Override
			public Boolean call() throws Exception
			{
				guid = template.getGuid();
				if (template.getIterator() != null) {
					guid = guid +
							Constant.Navigation.PATH_SELECTOR_OPEN +
							dataContext.getIteratorValue() +
							Constant.Navigation.PATH_SELECTOR_CLOSE;
				}

				try {
					title = dataContext.resolveToString(template.getTitle());
					description = dataContext.resolveToString(template.getDescription());
				}
				catch (FocusMissingResourceException | FocusBadTypeException ex) {
					// silent skipping
					return false;
				}

				freeDataContext();
				return true;
			}
		};

		// priority: just a little bit less than the current data context priority, such that is executed
		// just after all data from the data context have been retrieved
		PriorityTask<Object> future = new PriorityTask<>(this.getDataContext().getPriority() - Constant.AppConfig.PRIORITY_SMALL_DELTA, todo);
		this.dataManager.executeOnAppBuilderPool(future);
		return future;
	}

	/**
	 * Get this project title
	 *
	 * @return The title
	 */
	public String getTitle()
	{
		return this.title;
	}

	/**
	 * Get this project description
	 *
	 * @return The description or the empty String
	 */
	public String getDescription()
	{
		return this.description;
	}

	/**
	 * Get the list of dashboards pages
	 *
	 * @return A list of {@link PageInstance}s
	 */
	public ArrayList<PageInstance> getDashboards()
	{
		return this.dashboards;
	}

	/**
	 * Get the list of tools pages
	 *
	 * @return A list of {@link PageInstance}s
	 */
	public ArrayList<PageInstance> getTools()
	{
		return this.tools;
	}

	/**
	 * Return the application projects instances.
	 *
	 * @return A list of {@link ProjectInstance}s.
	 */
	public ArrayList<ProjectInstance> getProjects()
	{
		return this.projects;
	}

	/**
	 * Inherited.
	 *
	 * @param searchedPath The path to look after.
	 * @return Inherited.
	 */
	@Override
	protected AbstractInstance propagatePathLookup(String searchedPath)
	{
		ArrayList<ArrayList> sources = new ArrayList<>();
		sources.add(this.dashboards);
		sources.add(this.tools);
		sources.add(this.projects);
		for (ArrayList<AbstractInstance> array : sources) {
			for (AbstractInstance i : array) {
				AbstractInstance ret = i.lookupByPath(searchedPath);
				if (ret != null) {
					return ret;
				}
			}
		}
		return null;
	}

	/**
	 * Inherited.
	 *
	 * @param parentPath The parent path on the top of which the new path must be defined.
	 */
	@Override
	public void buildPaths(String parentPath)
	{
		this.path = parentPath + Constant.Navigation.PATH_SEPARATOR + this.guid;

		ArrayList<ArrayList> sources = new ArrayList<>();
		sources.add(this.dashboards);
		sources.add(this.tools);
		sources.add(this.projects);


		for (ArrayList<AbstractInstance> ar : sources) {
			for (AbstractInstance i : ar) {
				i.buildPaths(this.path);
			}
		}
	}

	/**
	 * Tells whether the current instance is disabled.
	 *
	 * @return {@code true} if this is the case, {@code false} otherwise.
	 */
	public boolean isDisabled()
	{
		return this.disabled;
	}
}
