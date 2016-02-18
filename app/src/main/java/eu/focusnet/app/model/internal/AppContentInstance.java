package eu.focusnet.app.model.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import eu.focusnet.app.exception.BadTypeException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.service.DataManager;
import eu.focusnet.app.model.json.AppContentTemplate;
import eu.focusnet.app.model.json.ProjectTemplate;
import eu.focusnet.app.model.internal.widgets.WidgetInstance;
import eu.focusnet.app.model.util.TypesHelper;

/**
 * Created by julien on 12.01.16.
 */
public class AppContentInstance
{
	private DataManager dataManager;
	private AppContentTemplate appTemplate;
	private LinkedHashMap<String, ProjectInstance> projects;
	private DataContext dataContext;

	/**
	 * C'tor
	 *
	 * @param tpl
	 */
	public AppContentInstance(AppContentTemplate tpl)
	{
		this.appTemplate = tpl;
		this.projects = new LinkedHashMap<String, ProjectInstance>();
		this.dataManager = DataManager.getInstance();
		this.dataContext = new DataContext();
		this.build();
		return;
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
		return buildPath(project) + "|" + page.getType() + "|" + page.getGuid();
	}

	/**
	 * Build a string used as a path identifier, using the specified instance objects.
	 */
	public static String buildPath(ProjectInstance project, PageInstance page, WidgetInstance w)
	{
		return buildPath(project, page) + "|" + w.getGuid();
	}

	/**
	 * Build the instance based on the template, using the DataManager
	 *
	 * @return
	 */
	private void build()
	{
		// retrieve application-wide data
		this.dataContext.provideData(this.appTemplate.getData());

		// build the different projects in the application content
		ArrayList<ProjectTemplate> projectTemplates = this.appTemplate.getProjects();
		for (ProjectTemplate projTpl : projectTemplates) {

			// Iterators use application-level data context list of urls
			String x = projTpl.getIterator();

			if (projTpl.getIterator() != null) {
				ArrayList<String> urls = null;
				try {
					urls = TypesHelper.asArrayOfUrls(this.dataContext.resolve(projTpl.getIterator()));
				}
				catch (BadTypeException e) {
					// invalid iterator. ignore and log?
					// FIXME
					continue;
				}

				for (String url : urls) {
					try {
						this.dataManager.getSample(url);
					}
					catch (FocusMissingResourceException ex) {
						continue;
					}
					DataContext new_ctx = new DataContext(this.dataContext);
					new_ctx.put(ProjectInstance.LABEL_PROJECT_ITERATOR, url);
					// the guid is adapted in the ProjectInstance constructor
					ProjectInstance p = new ProjectInstance(projTpl, new_ctx);
					this.projects.put(p.getGuid(), p);
				}
			}
			else {
				DataContext new_ctx = new DataContext(this.dataContext);
				ProjectInstance p = new ProjectInstance(projTpl, new_ctx);
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
	public ProjectInstance getProjectFromPath(String path)
	{
		String[] parts = path.split("\\|");
		if (parts.length >= 1) {
			return this.projects.get(parts[0]);
		}
		return null;// exception instead ?
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
	public PageInstance getPageFromPath(String path)
	{
		ProjectInstance pr = this.getProjectFromPath(path);
		if (pr == null) {
			return null;
		}
		String[] parts = path.split("\\|");
		if (parts.length >= 3) {
			return pr.getPageFromGuid(parts[2], parts[1]);
		}
		return null;// exception instead ?
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
	public WidgetInstance getWidgetFromPath(String path)
	{
		PageInstance p = this.getPageFromPath(path);
		if (p == null) {
			return null;
		}
		String[] parts = path.split("\\|");
		if (parts.length >= 4) {
			return p.widgets.get(parts[3]);
		}
		return null; // exception instead ?
	}


}
