package eu.focusnet.app.model.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import eu.focusnet.app.model.focus.Linker;
import eu.focusnet.app.model.focus.PageTemplate;
import eu.focusnet.app.model.focus.ProjectTemplate;
import eu.focusnet.app.model.focus.WidgetLinker;
import eu.focusnet.app.model.focus.WidgetTemplate;

/**
 * Created by julien on 12.01.16.
 */
public class ProjectInstance
{

	public final static String LABEL_PROJECT_ITERATOR = "$project-iterator$";

	private String guid = "";
	private String iterator;
	private String title; // should be SmartString -> resolved on toString() call
	private String description;

	private LinkedHashMap<String, PageInstance> dashboards = new LinkedHashMap<String, PageInstance>();
	private LinkedHashMap<String, PageInstance> tools = new LinkedHashMap<String, PageInstance>();

	private ProjectTemplate template = null;
	private DataContext dataContext = null;

	private boolean isValid = false;

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
		if (dataContext.get(LABEL_PROJECT_ITERATOR) != null) {
			this.guid = this.guid + "[" + dataContext.get(LABEL_PROJECT_ITERATOR) +"]";
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
		// add the project-specific data to our data context
		this.dataContext.provideData(this.template.getData());

		// FIXME TODO special case: guid == __welcome__

		if (this.template.getDashboards() != null) {
			this.dashboards = this.createPageInstances(this.template.getDashboards());
		}
		if (this.template.getTools() != null) {
			this.tools = this.createPageInstances(this.template.getTools());
		}

		this.isValid = true;
		return;
	}

	/**
	 * Create a HashMap of PageInstance based on the provided source definition.
	 *
	 * @param source
	 * @return
	 */
	private LinkedHashMap<String, PageInstance> createPageInstances(ArrayList<Linker> source)
	{
		LinkedHashMap<String, PageInstance> ret = new LinkedHashMap<String, PageInstance>();

		for(Linker s : source) {
			String page_id = s.getPageid();
			PageTemplate pageTpl = this.template.findPage(page_id);

			ArrayList<WidgetInstance> widgets = new ArrayList<WidgetInstance>();

			if (pageTpl.getIterator() != null) {
				// iterator
				// FIXME TODO something like this:
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
				// no iterator, render a simple PageInstance
				DataContext new_ctx = new DataContext(this.dataContext);
				//	ArrayList<WidgetInstance> widgets = new ArrayList<WidgetInstance>();
				for (WidgetLinker wl : pageTpl.getWidgets()) {
					WidgetTemplate wTpl = this.template.findWidget(wl.getWidgetid());
					widgets.add(new WidgetInstance(wTpl, wl.getLayout(), new_ctx));
				}
				PageInstance p = new PageInstance(pageTpl, widgets, new_ctx);
				ret.put(p.getGuid(), p);
			}

		}
		return ret;
	}

	/**
	 * Tells whether this ProjectInstance is valid. Non-valid ones cannot be accessed by the
	 * end-user.
	 *
	 * @return
	 */
	public boolean isValid()
	{
		return this.isValid;
	}

	/**
	 * Retrieve data that are described in the 'data' property.
	 */
	private void retrieveData()
	{
		//this.dataContext.put(); // .. augment existing data context.
	}
}
