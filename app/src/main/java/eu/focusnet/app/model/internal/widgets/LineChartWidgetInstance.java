package eu.focusnet.app.model.internal.widgets;

import java.util.ArrayList;
import java.util.Map;

import eu.focusnet.app.exception.FocusBadTypeException;
import eu.focusnet.app.model.internal.DataContext;
import eu.focusnet.app.model.json.WidgetTemplate;
import eu.focusnet.app.model.util.TypesHelper;

/**
 * WidgetInstance containing all information pertaining to a Line Chart widget.
 */
public class LineChartWidgetInstance extends WidgetInstance
{
	private static final String CONFIG_LABEL_CAPTION = "caption";
	private static final String CONFIG_LABEL_X_AXIS = "x-axis";
	private static final String CONFIG_LABEL_SERIES = "series";
	private static final String CONFIG_LABEL_LIMITS = "limits";
	private static final String CONFIG_LABEL_LABEL = "label";
	private static final String CONFIG_LABEL_VALUES = "values";
	private static final String CONFIG_LABEL_VALUE = "value";
	private static final String CONFIG_LABEL_LIMIT_TYPE = "type";

	private String caption;
	private int numberOfSeries;
	private int numberOfMaxLimits;
	private int numberOfMinLimits;
	private ArrayList<String> series_labels;
	private ArrayList<ArrayList<Double>> series_values;
	private ArrayList<String> limits_max_labels;
	private ArrayList<Double> limits_max_values;
	private ArrayList<String> limits_min_labels;
	private ArrayList<Double> limits_min_values;
	private String xAxis_label;
	private ArrayList<String> xAxis_values;

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

	/**
	 * Process the configuration that is specific to LineCharts. In JSON, the structure is well
	 * organized, but here we flatten everything such that it can be used to render by the Chart
	 * library. That could be improved, but will work for now.
	 */
	@Override
	public void processConfig()
	{
		this.numberOfSeries = 0;
		this.numberOfMaxLimits = 0;
		this.numberOfMinLimits = 0;

		this.caption = "";
		this.series_labels = new ArrayList<>();
		this.series_values = new ArrayList<>();
		this.limits_max_labels = new ArrayList<>();
		this.limits_min_labels = new ArrayList<>();
		this.limits_max_values = new ArrayList<>();
		this.limits_min_values = new ArrayList<>();
		this.xAxis_label = "";
		this.xAxis_values = new ArrayList<>();


		// caption
		try {
			// caption
			this.caption = TypesHelper.asString(this.config.get(CONFIG_LABEL_CAPTION));
		}
		catch (FocusBadTypeException e) {
			this.caption = "";
		}

		// x-axis
		try {
			Map xMap = (Map<String, Object>) this.config.get(CONFIG_LABEL_X_AXIS);
			this.xAxis_label = TypesHelper.asString(xMap.get(CONFIG_LABEL_LABEL));
			this.xAxis_values = TypesHelper.asArrayOfStrings(xMap.get(CONFIG_LABEL_VALUES));
		}
		catch (FocusBadTypeException e) {
			// not safe to continue without label or values for x-axis
			return;
		}

		// series and limits
		ArrayList<Map> a = (ArrayList<Map>) this.config.get(CONFIG_LABEL_SERIES);
		for (Map m : a) {

			String new_label;
			ArrayList<Double> values;

			try {
				new_label = TypesHelper.asString(m.get(CONFIG_LABEL_LABEL));
				values = TypesHelper.asArrayOfDoubles(m.get(CONFIG_LABEL_VALUES));
			}
			catch (FocusBadTypeException e) {
				// ignore this serie
				continue;
			}

			// if the array of values does not match the number of values that is expecteed, e.g.
			// xAxis size, then fill/remove elements. We always keep the first elements and fill/
			// remove last elements
			int vsize = values.size();
			int xsize = xAxis_values.size();
			if (vsize < xsize) {
				for (int i = vsize; i < xsize; ++i) {
					values.add(0.0);
				}
			}
			else if (vsize > xsize) {
				values = new ArrayList<>(values.subList(0, xsize));
			}


			ArrayList<Map> limits = (ArrayList<Map>) m.get(CONFIG_LABEL_LIMITS);
			if (limits != null) {
				for (Map m2 : limits) {
					String label;
					Double value;
					String type;
					try {
						label = TypesHelper.asString(m2.get(CONFIG_LABEL_LABEL));
						value = TypesHelper.asDouble(m2.get(CONFIG_LABEL_VALUE));
						type = TypesHelper.asString(m2.get(CONFIG_LABEL_LIMIT_TYPE));
					}
					catch (FocusBadTypeException e) {
						// ignore this limit as a whole
						continue;
					}

					if (type != null && type.equals("max")) {
						this.limits_max_labels.add(label);
						this.limits_max_values.add(value);
						++this.numberOfMaxLimits;
					}
					else {
						this.limits_min_labels.add(label);
						this.limits_min_values.add(value);
						++this.numberOfMinLimits;
					}
				}
			}

			this.series_labels.add(new_label);
			this.series_values.add(values);

			++this.numberOfSeries;
		}

	}


	// FIXME TODO add documentation
	public String getCaption()
	{
		return this.caption;
	}

	public String getxAxisLabel()
	{
		return this.xAxis_label;
	}

	public ArrayList<String> getxAxisValue()
	{
		return this.xAxis_values;
	}

	public int getNumberOfSeries()
	{
		return this.numberOfSeries;
	}

	public int getNumberOfMaxLimits()
	{
		return this.numberOfMaxLimits;
	}

	public int getNumberOfMinLimits()
	{
		return this.numberOfMinLimits;
	}

	public String getSerieLabel(int i)
	{
		return this.series_labels.get(i);
	}

	public ArrayList<Double> getSerieValues(int i)
	{
		return this.series_values.get(i);
	}

	public String getMaxLimitLabel(int i)
	{
		return this.limits_max_labels.get(i);
	}

	public double getMaxLimitValue(int i)
	{
		return this.limits_max_values.get(i);
	}

	public String getMinLimitLabel(int i)
	{
		return this.limits_min_labels.get(i);
	}

	public double getMinLimitValue(int i)
	{
		return this.limits_min_values.get(i);
	}

}
