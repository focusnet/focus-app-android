package eu.focusnet.app.model.focus;

import java.util.Map;

/**
 * Created by admin on 03.08.2015.
 */
public class WidgetLinker
{

	private String widgetid;
	private int order;
	private Map<String, String> layout;

	public WidgetLinker(String widgetid, int order, Map<String, String> layout)
	{
		this.widgetid = widgetid;
		this.order = order;
		this.layout = layout;
	}

	public WidgetLinker()
	{
	}

	public String getWidgetid()
	{
		return widgetid;
	}

	public void setWidgetid(String widgetid)
	{
		this.widgetid = widgetid;
	}

	public int getOrder()
	{
		return order;
	}

	public void setOrder(int order)
	{
		this.order = order;
	}

	public Map<String, String> getLayout()
	{
		return layout;
	}

	public void setLayout(Map<String, String> layout)
	{
		this.layout = layout;
	}
}
