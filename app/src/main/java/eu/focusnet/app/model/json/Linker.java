package eu.focusnet.app.model.json;

/**
 * Created by admin on 03.08.2015.
 */
public class Linker
{

	private String pageid;
	private int order;

	public Linker()
	{
	}

	public Linker(int order, String pageid)
	{
		this.order = order;
		this.pageid = pageid;
	}

	public String getPageid()
	{
		return pageid;
	}

	public void setPageid(String pageid)
	{
		this.pageid = pageid;
	}

	public int getOrder()
	{
		return order;
	}

	public void setOrder(int order)
	{
		this.order = order;
	}
}
