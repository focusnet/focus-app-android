package eu.focusnet.app.model.internal;

import java.util.ArrayList;
import java.util.Map;

import eu.focusnet.app.model.focus.WidgetTemplate;
import eu.focusnet.app.util.TypesHelper;

/**
 * Created by julien on 20.01.16.
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
	void processConfig()
	{
		if (1==1) {
			return;
		}

		this.caption = "";
		this.numberOfParts = 0;

		this.caption = TypesHelper.asString(this.config.get(CONFIG_LABEL_CAPTION));
		ArrayList a = (ArrayList) this.config.get(CONFIG_LABEL_PARTS);
		for (Map m : (ArrayList<Map>)a) {
			String l = TypesHelper.asString(m.get(CONFIG_LABEL_LABEL));
			Double d = 1.0; // FIXME TypesHelper.asDouble(m.get(CONFIG_LABEL_VALUE));

			this.labels.add(TypesHelper.asString(m.get(CONFIG_LABEL_LABEL)));
			this.values.add(TypesHelper.asDouble(m.get(CONFIG_LABEL_VALUE)));
			++this.numberOfParts;
		}
		return;
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
	 * @return
	 */
	public int getNumberOfParts()
	{
		return this.numberOfParts;
	}

}
