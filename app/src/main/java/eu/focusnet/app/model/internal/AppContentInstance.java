package eu.focusnet.app.model.internal;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import eu.focusnet.app.manager.DataManager;
import eu.focusnet.app.model.focus.AppContentTemplate;
import eu.focusnet.app.model.focus.ProjectTemplate;

/**
 * Created by julien on 12.01.16.
 */
public class AppContentInstance
{
	private DataManager dataManager = null;
	private AppContentTemplate appTemplate = null;
	private HashMap<String, ProjectInstance> projects = new HashMap<String, ProjectInstance>();
	private HashMap<String, Object> dataContext = null;

	/**
	 * C'tor
	 *
	 * @param tpl
	 */
	public AppContentInstance(AppContentTemplate tpl)
	{
		this.appTemplate = tpl;
		this.dataManager = DataManager.getInstance();
		this.build();
	}

	/**
	 * Build the instance based on the template, using the DataManager
	 *
	 * @return
	 */
	private void build()
	{
		// if some data are defined in the global level, retrieve them
		// FIXME TODO
		this.dataContext = this.retrieveData();

		// build the different projects in the application content
		ArrayList<ProjectTemplate> projectTemplates = this.appTemplate.getProjects();
		for (ProjectTemplate pTpl : projectTemplates) {

			if (pTpl.getIterator() != null) {
				// FIXME TODO
			/*	ArrayList<URL> urls = dataManager.getListOfURIs(pTpl.getIterator());
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
				HashMap<String, Object> new_ctx = new HashMap<String, Object>(this.dataContext);
				ProjectInstance p = new ProjectInstance(pTpl, new_ctx);
				this.projects.put(p.getGuid(), p);
			}
		}
	}

	/**
	 * Retrieve the data that may have been defined.
	 *
	 * @return
	 */
	public HashMap<String, Object> retrieveData()
	{
		/**
		 * this.templateApp.getData()
		 * foreach data ->
		 * v = dm.getData();
		 * key = data-identifer e.g <data:list-of-machines>
		 * value = returned value (FocusObject)
		 *
		 * FIXME TODO
		 */
		return new HashMap<String, Object>(); // put them in the dataContext
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


}
