package eu.focusnet.app.model.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import eu.focusnet.app.exception.BadTypeException;
import eu.focusnet.app.manager.DataManager;
import eu.focusnet.app.model.focus.Linker;
import eu.focusnet.app.model.focus.PageTemplate;
import eu.focusnet.app.model.focus.ProjectTemplate;
import eu.focusnet.app.model.focus.WidgetLinker;
import eu.focusnet.app.model.focus.WidgetTemplate;
import eu.focusnet.app.util.TypesHelper;

/**
 * Created by julien on 12.01.16.
 */
public class ProjectInstance
{
	public final static String LABEL_PROJECT_ITERATOR = "$project-iterator$";
	public final static String WELCOME_PROJECT_IDENTIFIER = "__welcome__";

	private String guid;
	private String title; // should be SmartString -> resolved on toString() call
	private String description;

	private LinkedHashMap<String, PageInstance> dashboards;
	private LinkedHashMap<String, PageInstance> tools;

	private ProjectTemplate template;
	private DataContext dataContext;

	private boolean isValid;

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
		this.dashboards = new LinkedHashMap<String, PageInstance>();
		this.tools = new LinkedHashMap<String, PageInstance>();
		this.isValid = false;
		if (dataContext.get(LABEL_PROJECT_ITERATOR) != null) {
			this.guid = this.guid + "[" + dataContext.get(LABEL_PROJECT_ITERATOR).getUrl() + "]";
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

		// FIXME TODO special case ?: guid == __welcome__ == WELCOME_PROJECT_IDENTIFIER
		if (this.getGuid().equals(WELCOME_PROJECT_IDENTIFIER)) {
			this.title = ""; // FIXME i18n string
			this.description = ""; // FIXME i18n string.
			this.isValid = true;
			return;
		}

		if (this.template.getDashboards() != null) {
			this.dashboards = this.createPageInstances(this.template.getDashboards(), PageInstance.PageType.DASHBOARD);
		}
		if (this.template.getTools() != null) {
			this.tools = this.createPageInstances(this.template.getTools(), PageInstance.PageType.TOOL);
		}
		// FIXME should we add a 'hidden' category?

		this.isValid = true; // FIXME may not be true inthe end
		return;
	}

	/**
	 * Create a LinkedHashMap of PageInstance based on the provided source definition.
	 *
	 * @param source
	 * @return
	 */
	private LinkedHashMap<String, PageInstance> createPageInstances(ArrayList<Linker> source, PageInstance.PageType type)
	{
		LinkedHashMap<String, PageInstance> ret = new LinkedHashMap<String, PageInstance>();

		for(Linker s : source) {
			String page_id = s.getPageid();
			PageTemplate pageTpl = this.template.findPage(page_id);

			// if we have an iterator, this means that we must construct multiple times the same page,
			// but with a different data context each time
			if (pageTpl.getIterator() != null) {
				ArrayList<String> urls = null;
				try {
					urls = TypesHelper.asArrayOfUrls(
							this.dataContext.resolve(pageTpl.getIterator())
					);
				}
				catch (BadTypeException e) {
					// invalid iterator. ignore but log?
					// FIXME
					continue;
				}
				for (String url : urls) {
					DataManager.getInstance().getSample(url);
					DataContext new_ctx = new DataContext(this.dataContext);
					new_ctx.put(PageInstance.LABEL_PAGE_ITERATOR, url);
					LinkedHashMap<String, WidgetInstance> widgets = new LinkedHashMap<String, WidgetInstance>();
					for (WidgetLinker wl : pageTpl.getWidgets()) {
						WidgetTemplate wTpl = this.template.findWidget(wl.getWidgetid());
						WidgetInstance wi = WidgetInstance.factory(wTpl, wl.getLayout(), new_ctx);
						widgets.put(wi.getGuid(), wi); // FIXME if widget-id is same as a previous widget, we won't have 2x the widget.
					}
					// the guid is adapted in the PageInstance constructor
					PageInstance p = new PageInstance(pageTpl, type, widgets, new_ctx);
					ret.put(p.getGuid(), p);
				}
			}
			else {
				// no iterator, render a simple PageInstance
				DataContext new_ctx = new DataContext(this.dataContext);
				LinkedHashMap<String, WidgetInstance> widgets = new LinkedHashMap<String, WidgetInstance>();
				for (WidgetLinker wl : pageTpl.getWidgets()) {
					WidgetTemplate wTpl = this.template.findWidget(wl.getWidgetid());
					WidgetInstance wi = WidgetInstance.factory(wTpl, wl.getLayout(), new_ctx);
					widgets.put(wi.getGuid(), wi);
				}
				PageInstance p = new PageInstance(pageTpl, type, widgets, new_ctx);
				ret.put(p.getGuid(), p);
			}

		}
		return ret;
	}

	/**
	 * Get the page identified by the specified guid, which may contain an iterator specifier
	 * e.g.
	 * - my-project
	 * - my-project[http://www.example.org/data/123]
	 *
	 * @param expanded_guid
	 * @param type Either PageInstance.PageType.DASHBOARD or PageInstance.PageType.TOOL
	 * @return
	 */
	public PageInstance getPageFromGuid(String expanded_guid, String type)
	{
		PageInstance.PageType p1 = PageInstance.PageType.DASHBOARD;
		String t = type;
		String t2 = p1.toString();
		if (type.equals(PageInstance.PageType.DASHBOARD.toString())) {
			PageInstance p = this.dashboards.get(expanded_guid);
			return p;
		}
		if (type.equals(PageInstance.PageType.TOOL.toString())) {
			return this.tools.get(expanded_guid);
		}
		return null;
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

		// if cannot retrieve, then set the current object to invalid. this will prevent from creating new objects.

	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public LinkedHashMap<String, PageInstance> getDashboards() {
		return dashboards;
	}

	public LinkedHashMap<String, PageInstance> getTools() {
		return tools;
	}
}
