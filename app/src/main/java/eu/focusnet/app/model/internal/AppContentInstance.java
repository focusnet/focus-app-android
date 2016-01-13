package eu.focusnet.app.model.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import eu.focusnet.app.exception.NotImplementedException;
import eu.focusnet.app.manager.DataManager;
import eu.focusnet.app.model.focus.AppContentTemplate;
import eu.focusnet.app.model.focus.FocusSample;
import eu.focusnet.app.model.focus.ProjectTemplate;

/**
 * Created by julien on 12.01.16.
 */
public class AppContentInstance
{
	private DataManager dataManager = null;
	private AppContentTemplate appTemplate = null;
	private LinkedHashMap<String, ProjectInstance> projects = new LinkedHashMap<String, ProjectInstance>();
	private DataContext dataContext = null;

	/**
	 * C'tor
	 *
	 * @param tpl
	 */
	public AppContentInstance(AppContentTemplate tpl)
	{
		this.appTemplate = tpl;
		this.dataManager = DataManager.getInstance();
		this.dataContext = new DataContext();
		this.build();
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

			if (projTpl.getIterator() != null) {
				// FIXME TODO
				// the iterator uses some of the data that is accessible at application-level (current data-context).
			/*	ArrayList<URL> urls = dataManager.getListOfUrls(pTpl.getIterator().toString(this.dataContext)); // more or less
				for (URL url : urls) {
					FocusSample s = dataManager.get(url);
					HashMap<String, Object> new_ctx = new HashMap<String, Object>(this.dataContext);
					new_ctx.put(ProjectInstance.LABEL_PROJECT_ITERATOR, url);
					ProjectInstance p = new ProjectInstance(pTpl, new_ctx);
					this.projects.put(p.getGuid(), p);
				}
			*/
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
	public HashMap<String, ProjectInstance> getProjects()
	{
		return this.projects;
	}


	/**
	 * Get the project identified by the specified path
	 *
	 * @param path
	 * @return
	 */
	public ProjectInstance getProjectFromPath(String path)
	{
		return this.projects.get(path);
	}

	/**
	 * Get the page identified by the specified path.
	 *
	 * @param path
	 * @return
	 */
	public PageInstance getPageFromPath(String path)
	{
		// disect path
		// project[uri]|dashboard|page[uri]
		throw new NotImplementedException("AppContentInstance.getPageFromPath()");
	}


}
