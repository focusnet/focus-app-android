package eu.focusnet.app.model.internal.widgets;

import java.util.ArrayList;
import java.util.Map;

import eu.focusnet.app.exception.FocusBadTypeException;
import eu.focusnet.app.model.json.WidgetTemplate;
import eu.focusnet.app.model.internal.DataContext;
import eu.focusnet.app.model.util.TypesHelper;

/**
 * An object containing everything required to build a Pie Chart widget
 */
public class PieChartWidgetInstance extends WidgetInstance
{
	private final static String CONFIG_LABEL_CAPTION = "caption";
	private final static String CONFIG_LABEL_PARTS = "parts";
	private final static String CONFIG_LABEL_LABEL = "label";
	private final static String CONFIG_LABEL_VALUE = "value";

	private String caption;
	private int numberOfParts;
	private ArrayList<String> labels;
	private ArrayList<Double> values;


	public PieChartWidgetInstance(WidgetTemplate template, Map<String, String> layoutConfig, DataContext newCtx)
	{
		super(template, layoutConfig, newCtx);
	}

	/**
	 * PieChart configuration:
	 * - caption: String
	 * - parts[]: ArrayList
	 * - parts[]{}.label: String
	 * - parts[]{}.value: Double
	 */
	@Override
	protected void processConfig()
	{
		this.caption = "";
		this.numberOfParts = 0;
		this.labels = new ArrayList<>();
		this.values = new ArrayList<>();

		try {
			this.caption = TypesHelper.asString(this.config.get(CONFIG_LABEL_CAPTION));
		}
		catch (FocusBadTypeException e) {
			this.caption = "";
		}

		ArrayList<Map> parts = (ArrayList<Map>) this.config.get(CONFIG_LABEL_PARTS);
		for (Map m : parts) {
			String label;
			Double value;
			try {
				label = TypesHelper.asString(m.get(CONFIG_LABEL_LABEL));
				value = TypesHelper.asDouble(m.get(CONFIG_LABEL_VALUE));
			}
			catch (FocusBadTypeException e) {
				// ignore this part, go to the next
				continue;
			}
			this.labels.add(label);
			this.values.add(value);
			++this.numberOfParts;
		}
	}

	/**
	 * Get the caption. It may be the empty string.
	 *
	 * @return
	 */
	public String getCaption()
	{
		return this.caption;
	}

	/**
	 * Get the total number of parts on the pie chart.
	 *
	 * @return
	 */
	public int getNumberOfParts()
	{
		return this.numberOfParts;
	}

	public ArrayList<String> getLabels()
	{
		return this.labels;
	}

	public ArrayList<Double> getValues()
	{
		return this.values;
	}

}
