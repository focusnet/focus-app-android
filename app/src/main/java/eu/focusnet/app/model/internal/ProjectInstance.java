package eu.focusnet.app.model.internal;

import java.util.HashMap;

import eu.focusnet.app.model.focus.ProjectTemplate;

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

	private HashMap<String, PageInstance> dashboards = new HashMap<String, PageInstance>();
	private HashMap<String, PageInstance> tools = new HashMap<String, PageInstance>();

	private ProjectTemplate template = null;
	private HashMap<String, Object> dataCtx = null;

	private boolean isValid = false;

	/**
	 * C'tor
	 *
	 * @param tpl
	 * @param dataCtx
	 */
	public ProjectInstance(ProjectTemplate tpl, HashMap<String, Object> dataCtx)
	{
		this.template = tpl;
		this.dataCtx = dataCtx;
		this.guid = tpl.getGuid();
		if (dataCtx.get("$project-iterator$") != null) {
			this.guid = this.guid + "[" + dataCtx.get(LABEL_PROJECT_ITERATOR) +"]";
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
		/*

		this.retrieveData();

		foreach tpl.getDashboards() -> get list of pages ->
			if (page.getIterator()) {

			}
			else {
				new PageInstance(pageTpl, widgetDef, dataCtx);
			}

		---------

		idem for tools

		 */

		this.isValid = true;
		return;
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
}
