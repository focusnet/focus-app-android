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
import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.internal.widgets.WidgetInstance;
import eu.focusnet.app.model.json.AppContentTemplate;
import eu.focusnet.app.model.json.ProjectTemplate;
import eu.focusnet.app.model.util.Constant;
import eu.focusnet.app.model.util.TypesHelper;
import eu.focusnet.app.service.DataManager;

/**
 * An Application content instance, i.e. the application template has been resolved and real
 * instances of the different contained objects are accessible through the present object.
 */
public class AppContentInstance extends AbstractInstance
{
	private DataManager dataManager;
	private AppContentTemplate appTemplate;
	private LinkedHashMap<String, ProjectInstance> projects;
	private String title;

	/**
	 * C'tor
	 *
	 * @param tpl
	 */
	public AppContentInstance(AppContentTemplate tpl)
	{
		super();

		this.appTemplate = tpl;
		this.projects = new LinkedHashMap<>();
		this.dataManager = FocusApplication.getInstance().getDataManager();
		this.dataContext = new DataContext();
		this.build();
		this.freeDataContext();

		if (!this.isValid()) {
			throw new FocusInternalErrorException("Invalid Application Content. Error found while parsing widgets/pages/projects.");
		}
	}

	/**
	 * Build a string used as a path identifier, using the specified instance objects.
	 */
	public static String buildPath(ProjectInstance project)
	{
		return project.getGuid();
	}

	/**
	 * Build a string used as a path identifier, using the specified instance objects.
	 */
	public static String buildPath(ProjectInstance project, PageInstance page)
	{
		return buildPath(project) + Constant.PATH_SEPARATOR + page.getType() + Constant.PATH_SEPARATOR + page.getGuid();
	}

	/**
	 * Build a string used as a path identifier, using the specified instance objects.
	 */
	public static String buildPath(ProjectInstance project, PageInstance page, WidgetInstance w)
	{
		return buildPath(project, page) + Constant.PATH_SEPARATOR + w.getGuid();
	}

	/**
	 * Build the instance based on the template, using the DataManager
	 *
	 * @return
	 */
	private void build()
	{
		// Get the global app content title
		this.title = this.appTemplate.getTitle();

		// retrieve application-wide data
		try {
			this.dataContext.provideData(this.appTemplate.getData());
		}
		catch (FocusMissingResourceException ex) {
			return;
		}

		// build the different projects in the application content
		ArrayList<ProjectTemplate> projectTemplates = this.appTemplate.getProjects();
		for (ProjectTemplate projTpl : projectTemplates) {

			// Iterators use application-level data context list of urls
			if (projTpl.getIterator() != null) {
				ArrayList<String> urls;
				try {
					urls = TypesHelper.asArrayOfUrls(
							this.dataContext.resolve(projTpl.getIterator())
					);
				}
				catch (FocusMissingResourceException e) {
					// should not happen, but let's continue silently
					FocusApplication.reportError(e);
					continue;
				}
				catch (FocusBadTypeException e) {
					// invalid iterator. survive by ignoring, but log the event
					// should not happen, but let's continue silently
					FocusApplication.reportError(e);
					continue;
				}

				for (String url : urls) {
					DataContext newCtx = new DataContext(this.dataContext);
					try {
						this.dataManager.getSample(url);
					}
					catch (FocusMissingResourceException ex) {
						// should not happen, but let's continue silently
						FocusApplication.reportError(ex);
						continue;
					}

					try {
						newCtx.register(ProjectInstance.LABEL_PROJECT_ITERATOR, url);
					}
					catch (FocusMissingResourceException ex) {
						// should not happen, but let's continue silently
						FocusApplication.reportError(ex);
						continue;
					}
					// the guid is adapted in the ProjectInstance constructor
					ProjectInstance p = new ProjectInstance(projTpl, newCtx);
					if (!p.isValid()) {
						this.markAsInvalid();
					}
					this.projects.put(p.getGuid(), p);
				}
			}
			else {
				DataContext newCtx = new DataContext(this.dataContext);
				ProjectInstance p = new ProjectInstance(projTpl, newCtx);
				if (!p.isValid()) {
					this.markAsInvalid();
				}
				this.projects.put(p.getGuid(), p);
			}
		}
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

	/**
	 * Get the project identified by the specified path, which may contain an iterator specifier
	 * e.g.
	 * - my-project
	 * - my-project[http://www.example.org/data/123]
	 *
	 * @param path
	 * @return
	 */
	public ProjectInstance getProjectFromPath(String path) throws FocusMissingResourceException
	{
		String[] parts = path.split(Constant.PATH_SEPARATOR_PATTERN);
		if (parts.length >= 1) {
			return this.projects.get(parts[0]);
		}
		throw new FocusMissingResourceException("Invalid project path");
	}

	/**
	 * Get the page identified by the specified path, which may contain iterators specifiers, e.g.
	 * - my-project|dashboards|my-page
	 * - my-project[http://www.example.org/data/123]|dashboards|my-page
	 * - my-project|tools|my-page[http://www.example.org/data/456]
	 * - my-project[http://www.example.org/data/123]|tools|my-page[http://www.example.org/data/456]
	 *
	 * @param path
	 * @return
	 */
	public PageInstance getPageFromPath(String path) throws FocusMissingResourceException
	{
		ProjectInstance pr = this.getProjectFromPath(path);
		if (pr == null) {
			return null;
		}
		String[] parts = path.split(Constant.PATH_SEPARATOR_PATTERN);
		if (parts.length >= 3) {
			return pr.getPageFromGuid(parts[2], parts[1]);
		}
		throw new FocusMissingResourceException("Invalid page path");
	}


	/**
	 * Get the widget object identified by the specified path. It may contain iterators specifiers
	 * for the project and page parts, e.g.
	 * - my-project|dashboards|my-page|widget-id
	 * - my-project[http://www.example.org/data/123]|dashboards|my-page|widget-id
	 * - my-project|tools|my-page[http://www.example.org/data/456]|widget-id
	 * - my-project[http://www.example.org/data/123]|tools|my-page[http://www.example.org/data/456]|widget-id
	 *
	 * @param path
	 * @return
	 */
	public WidgetInstance getWidgetFromPath(String path) throws FocusMissingResourceException
	{
		PageInstance p = this.getPageFromPath(path);
		if (p == null) {
			return null;
		}
		String[] parts = path.split(Constant.PATH_SEPARATOR_PATTERN);
		if (parts.length >= 4) {
			return p.widgets.get(parts[3]);
		}
		throw new FocusMissingResourceException("Invalid widget path");
	}


	public String getTitle()
	{
		return title;
	}
}
