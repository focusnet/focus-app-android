package eu.focusnet.app.model.internal;

import java.util.ArrayList;
import java.util.HashMap;

import eu.focusnet.app.model.focus.PageTemplate;

/**
 * Created by julien on 12.01.16.
 */
public class PageInstance
{

	public final static String LABEL_PAGE_ITERATOR = "$page-iterator$";

	private String guid = "";
	private boolean isValid = false; // <=> all widgets are valid
	ArrayList<WidgetInstance> widgets = null; // layout + widgetdefinition, with dataContext.
	HashMap<String, Object> dataContext = null;


	/**
	 * C'tor
	 *
	 * @param pageTpl
	 * @param widgets
	 * @param dataCtx
	 */
	public PageInstance(PageTemplate pageTpl, ArrayList<WidgetInstance> widgets, HashMap<String, Object> dataCtx)
	{
		this.guid = pageTpl.getGuid();
		if (dataCtx.get(LABEL_PAGE_ITERATOR) != null) {
			this.guid = this.guid + "[" + dataCtx.get(LABEL_PAGE_ITERATOR) +"]";
		}
		this.widgets = widgets;
		this.dataContext = dataCtx;

		// set validity depending on widgets validity and dataContext resolution?
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
