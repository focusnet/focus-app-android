package eu.focusnet.app.model.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.exception.FocusBadTypeException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.util.Constant;
import eu.focusnet.app.service.DataManager;
import eu.focusnet.app.model.json.Linker;
import eu.focusnet.app.model.json.PageTemplate;
import eu.focusnet.app.model.json.ProjectTemplate;
import eu.focusnet.app.model.json.WidgetLinker;
import eu.focusnet.app.model.json.WidgetTemplate;
import eu.focusnet.app.model.internal.widgets.WidgetInstance;
import eu.focusnet.app.model.util.TypesHelper;

/**
 * Created by julien on 12.01.16.
 */
public class ProjectInstance extends AbstractInstance
{
	public final static String LABEL_PROJECT_ITERATOR = "$project-iterator$";
	public final static String WELCOME_PROJECT_IDENTIFIER = "__welcome__";

	private String guid;
	private String title; // should be SmartString -> resolved on toString() call
	private String description;

	private LinkedHashMap<String, PageInstance> dashboards;
	private LinkedHashMap<String, PageInstance> tools;

	private ProjectTemplate template;

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
			this.guid = this.guid + Constant.PATH_SELECTOR_OPEN + dataContext.get(LABEL_PROJECT_ITERATOR).getUrl() + Constant.PATH_SELECTOR_CLOSE;
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

		this.dataContext.setIsOptimisticFillMode(true);

		this.isValid = true; // FIXME may not be true inthe end
	}

	/**
	 * Create a LinkedHashMap of PageInstance based on the provided source definition.
	 *
	 * @param source
	 * @return
	 */
	private LinkedHashMap<String, PageInstance> createPageInstances(ArrayList<Linker> source, PageInstance.PageType type)
	{
		LinkedHashMap<String, PageInstance> ret = new LinkedHashMap<>();

		for (Linker s : source) {
			String page_id = s.getPageid();
			PageTemplate pageTpl = this.template.findPage(page_id);

			// if we have an iterator, this means that we must construct multiple times the same page,
			// but with a different data context each time
			if (pageTpl.getIterator() != null) {
				ArrayList<String> urls;
				try {
					urls = TypesHelper.asArrayOfUrls(
							this.dataContext.resolve(pageTpl.getIterator())
					);
				}
				catch (FocusBadTypeException e) {
					// invalid iterator. survive by ignoring, but log the event
					FocusApplication.reportError(e);
					continue;
				}
				for (String url : urls) {
					DataContext new_page_ctx = new DataContext(this.dataContext);
					try {
						if (new_page_ctx.isOptimisticFillMode()) {
							DataManager.getInstance().getSample(url);
						}
						else {
							DataManager.getInstance().getSampleForSync(url);
						}
					}
					catch (FocusMissingResourceException ex) {
						continue;
					}
					new_page_ctx.put(PageInstance.LABEL_PAGE_ITERATOR, url);
					LinkedHashMap<String, WidgetInstance> widgets = new LinkedHashMap<String, WidgetInstance>();
					for (WidgetLinker wl : pageTpl.getWidgets()) {
						WidgetTemplate wTpl = this.template.findWidget(wl.getWidgetid());
						DataContext new_widget_ctx = new DataContext(new_page_ctx);
						WidgetInstance wi = WidgetInstance.factory(wTpl, wl.getLayout(), new_widget_ctx);
						widgets.put(wi.getGuid(), wi); // FIXME if widget-id is same as a previous widget, we won't have 2x the widget.
					}
					// the guid is adapted in the PageInstance constructor
					PageInstance p = new PageInstance(pageTpl, type, widgets, new_page_ctx);
					new_page_ctx.setIsOptimisticFillMode(true);
					ret.put(p.getGuid(), p);
				}
			}
			else {
				// no iterator, render a simple PageInstance
				DataContext new_page_ctx = new DataContext(this.dataContext);
				LinkedHashMap<String, WidgetInstance> widgets = new LinkedHashMap<String, WidgetInstance>();
				for (WidgetLinker wl : pageTpl.getWidgets()) {
					WidgetTemplate wTpl = this.template.findWidget(wl.getWidgetid());
					DataContext new_widget_ctx = new DataContext(new_page_ctx);
					WidgetInstance wi = WidgetInstance.factory(wTpl, wl.getLayout(), new_widget_ctx);
					widgets.put(wi.getGuid(), wi);
				}
				PageInstance p = new PageInstance(pageTpl, type, widgets, new_page_ctx);
				new_page_ctx.setIsOptimisticFillMode(true);
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
	 * @param type          Either PageInstance.PageType.DASHBOARD or PageInstance.PageType.TOOL
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

	public String getTitle()
	{
		return title;
	}

	public String getDescription()
	{
		return this.description;
	}

	public LinkedHashMap<String, PageInstance> getDashboards()
	{
		return this.dashboards;
	}

	public LinkedHashMap<String, PageInstance> getTools()
	{
		return this.tools;
	}
}
