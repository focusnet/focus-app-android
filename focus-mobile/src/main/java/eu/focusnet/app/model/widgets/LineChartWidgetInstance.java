/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.model.widgets;

import java.util.ArrayList;
import java.util.Map;

import eu.focusnet.app.model.DataContext;
import eu.focusnet.app.model.TypesHelper;
import eu.focusnet.app.model.gson.WidgetTemplate;
import eu.focusnet.app.util.FocusBadTypeException;
import eu.focusnet.app.util.FocusMissingResourceException;

/**
 * WidgetInstance containing all information pertaining to a Line Chart widget.
 */
public class LineChartWidgetInstance extends WidgetInstance
{
	/**
	 * Configuration property for the caption
	 */
	final private static String CONFIG_LABEL_CAPTION = "caption";

	/**
	 * Configuration property for the x-axis
	 */
	final private static String CONFIG_LABEL_X_AXIS = "x-axis";

	/**
	 * Configuration property for the Series
	 */
	final private static String CONFIG_LABEL_SERIES = "series";

	/**
	 * Configuration property for the limits
	 */
	final private static String CONFIG_LABEL_LIMITS = "limits";

	/**
	 * Configuration property for the labels
	 */
	final private static String CONFIG_LABEL_LABEL = "label";

	/**
	 * Configuration property for the values
	 */
	final private static String CONFIG_LABEL_VALUES = "values";

	/**
	 * Configuration property for the limit value
	 */
	final private static String CONFIG_LABEL_LIMIT_VALUE = "value";

	/**
	 * Configuration property for the type of oimit
	 */
	final private static String CONFIG_LABEL_LIMIT_TYPE = "type";

	/**
	 * The caption
	 */
	private String caption;

	/**
	 * Number of series
	 */
	private int numberOfSeries;

	/**
	 * Number of max limits
	 */
	private int numberOfMaxLimits;

	/**
	 * Number of min limits
	 */
	private int numberOfMinLimits;

	/**
	 * Series labels
	 */
	private ArrayList<String> seriesLabels;

	/**
	 * Series values
	 */
	private ArrayList<ArrayList<Double>> seriesValues;

	/**
	 * Labels for max limits
	 */
	private ArrayList<String> limitsMaxLabels;

	/**
	 * Values for max limits
	 */
	private ArrayList<Double> limitsMaxValues;

	/**
	 * Labels for min limits
	 */
	private ArrayList<String> limitsMinLabels;

	/**
	 * Values for min values
	 */
	private ArrayList<Double> limitsMinValues;

	/**
	 * x-axis label
	 */
	private String xAxisLabel;

	/**
	 * x-axis values
	 */
	private ArrayList<String> xAxisValues;

	/**
	 * C'tor
	 *
	 * @param wTpl         Inherited
	 * @param layoutConfig Inherited
	 * @param dataCtx      Inherited
	 */
	public LineChartWidgetInstance(WidgetTemplate wTpl, Map<String, String> layoutConfig, DataContext dataCtx)
	{
		super(wTpl, layoutConfig, dataCtx);
	}

	/**
	 * Process the configuration that is specific to LineCharts. In JSON, the structure is well
	 * organized, but here we flatten everything such that it can be used to render by the Chart
	 * library.
	 */
	@Override
	public void processSpecificConfig()
	{
		this.numberOfSeries = 0;
		this.numberOfMaxLimits = 0;
		this.numberOfMinLimits = 0;

		this.caption = "";
		this.seriesLabels = new ArrayList<>();
		this.seriesValues = new ArrayList<>();
		this.limitsMaxLabels = new ArrayList<>();
		this.limitsMinLabels = new ArrayList<>();
		this.limitsMaxValues = new ArrayList<>();
		this.limitsMinValues = new ArrayList<>();
		this.xAxisLabel = "";
		this.xAxisValues = new ArrayList<>();


		// caption
		Object rawCaption = this.config.get(CONFIG_LABEL_CAPTION);
		if (rawCaption == null) {
			this.caption = "";
		}
		else {
			try {
				this.caption = this.dataContext.resolveToString(rawCaption);
			}
			catch (FocusMissingResourceException | FocusBadTypeException ex) {
				this.markAsInvalid();
				return;
			}
		}

		// x-axis
		try {
			Map xMap = (Map) this.config.get(CONFIG_LABEL_X_AXIS);
			if (xMap == null) {
				this.markAsInvalid();
				return;
			}
			Object rawLabel = xMap.get(CONFIG_LABEL_LABEL);
			if (rawLabel == null) {
				this.markAsInvalid();
				return;
			}

			this.xAxisLabel = this.dataContext.resolveToString(rawLabel);
			Object rawValues = xMap.get(CONFIG_LABEL_VALUES);
			if (rawValues == null) {
				this.markAsInvalid();
				return;
			}
			this.xAxisValues = TypesHelper.asArrayOfStrings(rawValues);
			for (int i = 0; i < this.xAxisValues.size(); ++i) {
				this.xAxisValues.set(i, this.dataContext.resolveToString(this.xAxisValues.get(i)));
			}
		}
		catch (FocusMissingResourceException | FocusBadTypeException ex) {
			this.markAsInvalid();
			return;
		}

		// series and limits
		ArrayList<Map> series = (ArrayList<Map>) this.config.get(CONFIG_LABEL_SERIES);
		if (series == null) {
			this.markAsInvalid();
			return;
		}
		for (Map m : series) {

			String newLabel;
			ArrayList<Double> values;

			Object rawLabel = m.get(CONFIG_LABEL_LABEL);
			if (rawLabel == null) {
				this.markAsInvalid();
				return;
			}
			try {
				newLabel = this.dataContext.resolveToString(rawLabel);
			}
			catch (FocusMissingResourceException | FocusBadTypeException ex) {
				this.markAsInvalid();
				return;
			}

			Object valuesTmp;
			Object rawValues = m.get(CONFIG_LABEL_VALUES);
			if (rawValues == null) {
				this.markAsInvalid();
				return;
			}
			try {
				valuesTmp = this.dataContext.resolveToObject(rawValues);
			}
			catch (FocusBadTypeException ex) {
				// good, this means we have an array of double and nothing to resolve()
				valuesTmp = rawValues;
			}
			catch (FocusMissingResourceException ex) {
				this.markAsInvalid();
				return;
			}
			try {
				values = TypesHelper.asArrayOfDoubles(valuesTmp);
			}
			catch (FocusBadTypeException ex) {
				// bad values, ignore series
				this.markAsInvalid();
				return;
			}

			// if the array of values does not match the number of values that is expecteed, e.g.
			// xAxis size, then fill/remove elements. We always keep the first elements and fill/
			// remove last elements
			int vsize = values.size();
			int xsize = xAxisValues.size();
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
					Object rawLimitLabel = m2.get(CONFIG_LABEL_LABEL);
					Object rawLimitValue = m2.get(CONFIG_LABEL_LIMIT_VALUE);
					if (rawLimitLabel == null || rawLimitValue == null) {
						this.markAsInvalid();
						return;
					}
					try {
						label = this.dataContext.resolveToString(rawLimitLabel);
						value = this.dataContext.resolveToDouble(rawLimitValue);
						Object rawType = m2.get(CONFIG_LABEL_LIMIT_TYPE);
						if (rawType == null) {
							// default is "min"
							type = "min";
						}
						else {
							type = TypesHelper.asString(rawType);
						}
					}
					catch (FocusMissingResourceException | FocusBadTypeException ex) {
						this.markAsInvalid();
						return;
					}

					if (type.equals("max")) {
						this.limitsMaxLabels.add(label);
						this.limitsMaxValues.add(value);
						++this.numberOfMaxLimits;
					}
					else if (type.equals("min")) {
						this.limitsMinLabels.add(label);
						this.limitsMinValues.add(value);
						++this.numberOfMinLimits;
					}
					else {
						this.markAsInvalid();
						return;
					}
				}
			}

			this.seriesLabels.add(newLabel);
			this.seriesValues.add(values);

			++this.numberOfSeries;
		}

	}


	/**
	 * Get the caption
	 *
	 * @return The caption
	 */
	public String getCaption()
	{
		return this.caption;
	}

	/**
	 * Returns the x-axis label
	 *
	 * @return The x-axis label
	 */
	public String getxAxisLabel()
	{
		return this.xAxisLabel;
	}

	/**
	 * Get the x-axis values
	 *
	 * @return The x-axis values
	 */
	public ArrayList<String> getxAxisValues()
	{
		return this.xAxisValues;
	}

	/**
	 * Get the number of series
	 *
	 * @return The number of series
	 */
	public int getNumberOfSeries()
	{
		return this.numberOfSeries;
	}

	/**
	 * Get the number of max limits
	 *
	 * @return Number of max limits
	 */
	public int getNumberOfMaxLimits()
	{
		return this.numberOfMaxLimits;
	}

	/**
	 * Get the number of min limits
	 *
	 * @return Number of min limits
	 */
	public int getNumberOfMinLimits()
	{
		return this.numberOfMinLimits;
	}

	/**
	 * Get label of a specific serie
	 *
	 * @param i Which serie to get the label from (0-based index)
	 * @return The label
	 */
	public String getSerieLabel(int i)
	{
		return this.seriesLabels.get(i);
	}

	/**
	 * Get values for a specific serie
	 *
	 * @param i Which serie to get the label from (0-based index)
	 * @return The values
	 */
	public ArrayList<Double> getSerieValues(int i)
	{
		return this.seriesValues.get(i);
	}

	/**
	 * Get a specific max limit label
	 *
	 * @param i Which label to get (0-based index)
	 * @return The label
	 */
	public String getMaxLimitLabel(int i)
	{
		return this.limitsMaxLabels.get(i);
	}

	/**
	 * Get a specific max limit value
	 *
	 * @param i Which value to get (0-based index)
	 * @return The value
	 */
	public Double getMaxLimitValue(int i)
	{
		return this.limitsMaxValues.get(i);
	}

	/**
	 * Get a specific min limit label
	 *
	 * @param i Which label to get (0-based index)
	 * @return The label
	 */
	public String getMinLimitLabel(int i)
	{
		return this.limitsMinLabels.get(i);
	}

	/**
	 * Get a specific min limit value
	 *
	 * @param i Which value to get (0-based index)
	 * @return The value
	 */
	public Double getMinLimitValue(int i)
	{
		return this.limitsMinValues.get(i);
	}

}
