package eu.focusnet.app.model.focus;

/**
 * Created by admin on 03.08.2015.
 */
public class WidgetTemplate
{

	private String guid,
			type,
			title;
	private Object config;

	public WidgetTemplate(String guid, String type, String title, Object config)
	{
		this.guid = guid;
		this.type = type;
		this.title = title;
		this.config = config;
	}

	public WidgetTemplate()
	{
	}

	public String getGuid()
	{
		return guid;
	}

	public void setGuid(String guid)
	{
		this.guid = guid;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public Object getConfig()
	{
		return config;
	}

	public void setConfig(Object config)
	{
		this.config = config;
	}

	public String getTitle()
	{
		return title;
	}
}
