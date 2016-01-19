package eu.focusnet.app.model.internal;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import eu.focusnet.app.model.focus.FocusSampleDataMap;
import eu.focusnet.app.model.focus.WidgetTemplate;

/**
 * Created by julien on 12.01.16.
 */
public class WidgetInstance
{

	private String guid = "";
//	private String title = null; // FIXME ?
	private String type = null;
	private FocusSampleDataMap params = null;
	private WidgetTemplate template = null;
	private Map<String, String> layoutConfig = null;
	private DataContext dataContext = null;

	private static HashMap<String, String> layoutConfigDefaults = null;
	static
	{
		layoutConfigDefaults = new HashMap<String, String>();
		layoutConfigDefaults.put("width", "1of4");
	}

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
		this.type = wTpl.getType();
		this.template = wTpl;
		this.layoutConfig = layoutConfig;
		this.dataContext = dataCtx;

	//	this.title = wTpl.getTitle(); // FIXME
	// 	this.description = wTpl.getDescription();
		this.params = wTpl.getParams(); // FIXME resolve?
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
	 * Get the current widget's type.
	 *
	 * @return
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 * Get a layout attribute.
	 *
	 * @return A string which's value corresponds to the requested attribute.
	 */
	public String getLayoutAttribute(String attribute)
	{
		String tmp = layoutConfig.get(attribute);
		if (tmp == null) {
			return this.layoutConfigDefaults.get(attribute);
		}
		return tmp;
	}

	/**
	 * Get a parameter (resolved in the current data context)
	 *
	 * @param which
	 * @return
	 */
	public Object getParam(String which)
	{
		return this.params.get(which);
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
