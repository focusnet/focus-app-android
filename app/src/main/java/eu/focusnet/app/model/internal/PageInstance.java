package eu.focusnet.app.model.internal;

import java.util.ArrayList;
import java.util.HashMap;

import eu.focusnet.app.model.focus.PageTemplate;

/**
 * Created by julien on 12.01.16.
 */
public class PageInstance
{
	private boolean isValid = false; // <=> all widgets are valid
	ArrayList<WidgetInstance> widgets = null; // layout + widgetdefinition, with dataContext.

	/**
	 * C'tor
	 *
	 * @param pageTpl
	 * @param widgetsDefinition
	 * @param dataCtx
	 */
	public PageInstance(PageTemplate pageTpl, ArrayList<WidgetInstance> widgetsDefinition, HashMap<String, Object> dataCtx)
	{
// this.guid = ...;
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
