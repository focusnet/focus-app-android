package eu.focusnet.app.model.internal;

import java.util.ArrayList;
import java.util.Map;

import eu.focusnet.app.model.focus.WidgetTemplate;
import eu.focusnet.app.model.ui.ChartData;

/**
 * Created by admin on 28.01.2016.
 */

//TODO implement this class with its methods
public class LineChartWidgetInstance extends WidgetInstance
{


	/**
	 * C'tor
	 *
	 * @param wTpl
	 * @param layoutConfig
	 * @param dataCtx
	 */
	public LineChartWidgetInstance(WidgetTemplate wTpl, Map<String, String> layoutConfig, DataContext dataCtx)
	{
		super(wTpl, layoutConfig, dataCtx);
	}

	@Override
	public void processConfig()
	{

	}

	public ArrayList<ChartData> getData()
	{
		return new ArrayList<>();
	}

}
