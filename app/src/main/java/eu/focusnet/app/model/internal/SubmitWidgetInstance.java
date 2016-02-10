package eu.focusnet.app.model.internal;

import java.util.Map;

import eu.focusnet.app.model.focus.WidgetTemplate;

/**
 * Created by admin on 28.01.2016.
 */

//TODO implement this class and its methods
public class SubmitWidgetInstance extends WidgetInstance
{
	/**
	 * C'tor
	 *
	 * @param wTpl
	 * @param layoutConfig
	 * @param dataCtx
	 */
	public SubmitWidgetInstance(WidgetTemplate wTpl, Map<String, String> layoutConfig, DataContext dataCtx)
	{
		super(wTpl, layoutConfig, dataCtx);
	}

	@Override
	public void processConfig()
	{

	}
}
