package eu.focusnet.app.model.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import eu.focusnet.app.model.focus.PageTemplate;

/**
 * Created by julien on 12.01.16.
 */
public class PageInstance
{

	public final static String LABEL_PAGE_ITERATOR = "$page-iterator$";
	public enum PageType
	{
		DASHBOARD,
		TOOL,
		HIDDEN
	};

	private PageTemplate template = null;
	private String guid = "";
	private PageType type = null;
	private String title = null;
	private String description = null;
	private boolean isValid = false; // <=> all widgets are valid
	LinkedHashMap<String, WidgetInstance> widgets = null; // layout + widgetdefinition, with dataContext.
	DataContext dataContext = null;


	/**
	 * C'tor
	 *
	 * @param pageTpl
	 * @param widgets
	 * @param dataCtx
	 */
	public PageInstance(PageTemplate pageTpl, PageType type, LinkedHashMap<String, WidgetInstance> widgets, DataContext dataCtx)
	{
		this.template = pageTpl;
		this.guid = pageTpl.getGuid();
		this.type = type;
		if (dataCtx.get(LABEL_PAGE_ITERATOR) != null) {
			this.guid = this.guid + "[" + dataCtx.get(LABEL_PAGE_ITERATOR).getUrl() + "]";
		}
		this.widgets = widgets;
		this.dataContext = dataCtx;

		// add page-specific data to our current data context
		this.dataContext.provideData(this.template.getData());

		this.title = pageTpl.getTitle(); // FIXME resolve?
		this.description = pageTpl.getDescription(); // FIXME resolve?
	}

	/**
	 * Get the current page instance guid.
	 *
	 * @return
	 */
	public String getGuid()
	{
		return this.guid;
	}

	/**
	 * Return the type of page (dashboards or tools or null)
	 *
	 * @return
	 */
	public PageType getType()
	{
		return this.type;
	}

	/**
	 * Get the page title (resolved within the current data context)
	 *
	 * @return
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * Get the description (resolved within the current data context)
	 *
	 * @return
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Tells whether this PageInstance is valid. Non-valid ones cannot be accessed by the
	 * end-user.
	 *
	 * @return
	 */
	public boolean isValid()
	{
		return this.isValid; // <=> all widgets are valid
	}

}
