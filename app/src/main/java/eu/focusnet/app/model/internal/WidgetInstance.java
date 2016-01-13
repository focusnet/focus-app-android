package eu.focusnet.app.model.internal;

import java.util.HashMap;
import java.util.Map;

import eu.focusnet.app.model.focus.WidgetTemplate;

/**
 * Created by julien on 12.01.16.
 */
public class WidgetInstance
{

	private WidgetTemplate template = null;
	private Map<String, String> layoutConfig = null;
	private HashMap<String, Object> dataContext = null;

	/**
	 * C'tor
	 *
	 * @param wTpl
	 * @param layoutConfig
	 * @param dataCtx
	 */
	public WidgetInstance(WidgetTemplate wTpl, Map<String, String> layoutConfig, HashMap<String, Object> dataCtx)
	{
		this.template = wTpl;
		this.layoutConfig = layoutConfig;
		this.dataContext = dataCtx;
	}
}
