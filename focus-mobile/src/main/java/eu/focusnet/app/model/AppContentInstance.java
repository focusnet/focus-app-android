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

import java.util.ArrayList;
import java.util.Locale;

import eu.focusnet.app.controller.DataManager;
import eu.focusnet.app.model.gson.AppContentTemplate;
import eu.focusnet.app.model.gson.ProjectTemplate;
import eu.focusnet.app.util.Constant;

/**
 * This object contains the whole content of the application (projects, pages, widgets). It is
 * based on a corresponding {@link AppContentTemplate} and the content is resolved iteratively
 * to obtain each part of the application with a proper data context.
 */
public class AppContentInstance extends AbstractInstance
{

	/**
	 * Template on which the application content instantication is based.
	 */
	private AppContentTemplate appTemplate;

	/**
	 * List of projects in this application content
	 */
	private ArrayList<ProjectInstance> projects;

	/**
	 * Title of this application content
	 */
	private String title;

	/**
	 * Language in which we should display the UI when this application content is loaded.
	 */
	private String language;

	/**
	 * C'tor.
	 *
	 * @param template    Base template
	 * @param dataManager {@link DataManager} used for retrieving data for this application content.
	 */
	public AppContentInstance(AppContentTemplate template, DataManager dataManager) throws InterruptedException
	{
		super(dataManager);

		this.appTemplate = template;
		this.projects = new ArrayList<>();
		this.dataContext = new DataContext(this.dataManager);

		// build the whole app data model
		this.build();
		this.waitForCompletion();

		// when all is done, build the paths of all instances
		// we MUST wait for completion, otherwise some parts of the application may not be ready
		// and the paths will be broken.
		this.buildPaths(null);

		// is everything valid?
		this.checkValidity();

		if (!this.isValid()) {
			//	throw new FocusInternalErrorException("Invalid Application Content. Error found while parsing widgets/pages/projects.");
			// FIXME define strategy. Be resilient.
			// statu quo is not bad. If not valid, then widget will be shown as invalid.
		}
	}


	/**
	 * Build the instance based on the template, using the DataManager
	 */
	private void build()
	{
		// Get the language if available
		this.language = this.appTemplate.getLanguage();

		// Get the global app content title
		this.title = this.appTemplate.getTitle();

		// retrieve application-wide data
		this.dataContext.provideData(this.appTemplate.getData());

		// build the different projects in the application content
		ArrayList<ProjectTemplate> projectTemplates = this.appTemplate.getProjects();

		this.projects = ProjectInstance.createProjects(projectTemplates, this.dataContext);
	}

	/**
	 * Check validity of the content and mark this instance as invalid if an error is found.
	 * <p/>
	 * This method is intended to be called after the full application content has been built.
	 */
	private void checkValidity()
	{
		boolean isValid = true;
		for (ProjectInstance pi : this.projects) {
			pi.checkValidity();
			isValid &= pi.isValid();
		}
		if (!isValid) {
			this.markAsInvalid();
		}
	}

	/**
	 * Wait for the application instance building to be terminated.
	 *
	 * @throws InterruptedException
	 */
	public void waitForCompletion() throws InterruptedException
	{
		this.dataManager.waitForCompletion();
		this.freeDataContext();
	}

	/**
	 * Return the application projects instances.
	 *
	 * @return a list of {@link ProjectInstance}s
	 */
	public ArrayList<ProjectInstance> getProjects()
	{
		return this.projects;
	}

	public String getTitle()
	{
		return title;
	}

	public String getLanguage()
	{
		if (this.language == null) {
			return Locale.ENGLISH.toString();
		}
		return this.language;
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
		for (ProjectInstance p : this.projects) {
			AbstractInstance ret = p.lookupByPath(searchedPath);
			if (ret != null) {
				return ret;
			}
		}
		return null;
	}

	/**
	 * Inherited
	 *
	 * @param parentPath The parent path on the top of which the new path must be defined.
	 */
	@Override
	public void buildPaths(String parentPath)
	{
		this.path = Constant.Navigation.PATH_ROOT;
		for (ProjectInstance pi : this.projects) {
			pi.buildPaths(this.path);
		}
	}


}
