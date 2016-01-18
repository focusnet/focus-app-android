package eu.focusnet.app.model.internal;

import java.util.HashMap;
import java.util.Map;

import eu.focusnet.app.model.focus.WidgetTemplate;

/**
 * Created by julien on 12.01.16.
 */
public class WidgetInstance
{

	private String guid = "";
	private WidgetTemplate template = null;
	private Map<String, String> layoutConfig = null;
	private DataContext dataContext = null;

	/**
	 * C'tor
	 *
	 * @param wTpl
	 * @param layoutConfig
	 * @param dataCtx
	 */
	public WidgetInstance(WidgetTemplate wTpl, Map<String, String> layoutConfig, DataContext dataCtx)
	{
		this.guid = wTpl.getGuid();
		this.template = wTpl;
		this.layoutConfig = layoutConfig;
		this.dataContext = dataCtx;

		// FIXME params? title, description, etc.

		this.build();
	}

	/**
	 * Build the current WidgetInstance based on the input parameters that were provided in the
	 * constructor.
	 */
	private void build()
	{
// something to do here.? regarding params? perhaps.
	}

	/**
	 * Return this WidgetInstance's guid.
	 *
	 * @return
	 */
	public String getGuid()
	{
		return this.guid;
	}

	/**
	 * Get a layout attribute.
	 *
	 * @return A string which's value corresponds to the requested attribute.
	 */
	public String getLayoutAttribute(String attribute)
	{
		return layoutConfig.get(attribute);
	}

	/**
	 * Tells whether this object is valid.
	 *
	 * @return
	 */
	public boolean isValid()
	{
		return true;
	}
}
