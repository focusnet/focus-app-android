package eu.focusnet.app.model.internal.widgets;

import java.util.Map;

import eu.focusnet.app.model.json.WidgetTemplate;
import eu.focusnet.app.model.internal.DataContext;

/**
 * Created by admin on 28.01.2016.
 */

//TODO implement this class with its methods
public class Html5WidgetInstance extends WidgetInstance
{
	/**
	 * C'tor
	 *
	 * @param wTpl
	 * @param layoutConfig
	 * @param dataCtx
	 */
	public Html5WidgetInstance(WidgetTemplate wTpl, Map<String, String> layoutConfig, DataContext dataCtx)
	{
		super(wTpl, layoutConfig, dataCtx);
	}

	@Override
	protected void processConfig()
	{

	}
}
