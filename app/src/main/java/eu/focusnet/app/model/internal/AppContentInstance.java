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
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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

	private final static String PATH_ROOT = "FOCUS";
	private AppContentTemplate appTemplate;
	private ArrayList<ProjectInstance> projects;
	private String title;
	private String language;

	/**
	 * C'tor
	 *
	 * @param tpl
	 */
	public AppContentInstance(AppContentTemplate tpl, DataManager dm)
	{
		super(dm);

		// set the root path

		this.appTemplate = tpl;
		this.projects = new ArrayList<>();
		this.dataContext = new DataContext(this.dataManager);

		// build the whole app data model
		this.build();

		// when all is done, build the paths of all instances
		this.buildPaths(null);

		if (!this.isValid()) {
			throw new FocusInternalErrorException("Invalid Application Content. Error found while parsing widgets/pages/projects.");
		}
	}


	/**
	 * Build the instance based on the template, using the DataManager
	 *
	 * @return
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
		for(ProjectInstance pi : this.projects) {
			if (!pi.isValid()) {
				this.markAsInvalid();
				break;
			}
		}
	}

	/**
	 * Return the application projects instances.
	 *
	 * @return
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

	@Override
	protected AbstractInstance propagatePathLookup(String searchedPath)
	{
		for(ProjectInstance p : this.projects) {
			AbstractInstance ret = p.lookupByPath(searchedPath);
			if (ret != null) {
				return ret;
			}
		}
		return null;
	}


	@Override
	public void buildPaths(String parentPath)
	{
		this.path = PATH_ROOT;
		for(ProjectInstance pi : this.projects) {
			pi.buildPaths(this.path);
		}
	}

	public void waitForCompletion() throws InterruptedException
	{
		this.dataManager.getDataRetrievingExecutor().shutdown();
		// Wait for everything to finish.
		//noinspection StatementWithEmptyBody
		while (!this.dataManager.getDataRetrievingExecutor().awaitTermination(5, TimeUnit.SECONDS)) {
				// wait silently
		}
		this.freeDataContext();
	}

}
