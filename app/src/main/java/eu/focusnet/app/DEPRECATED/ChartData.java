package eu.focusnet.app.DEPRECATED;

/**
 * Created by yandypiedra on 23.11.15.
 */
public class ChartData
{

	private String name;
	private float value;
	private int color;

	public ChartData(String name, float value, int color)
	{
		this(name, value);
		this.color = color;
	}

	public ChartData(String name, float value)
	{
		this.value = value;
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public float getValue()
	{
		return value;
	}

	public void setValue(float value)
	{
		this.value = value;
	}

	public int getColor()
	{
		return color;
	}

	public void setColor(int color)
	{
		this.color = color;
	}
}
