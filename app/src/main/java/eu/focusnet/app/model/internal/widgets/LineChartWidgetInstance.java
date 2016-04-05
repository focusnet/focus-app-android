/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.model.internal.widgets;

import java.util.ArrayList;
import java.util.Map;

import eu.focusnet.app.exception.FocusBadTypeException;
import eu.focusnet.app.exception.FocusMissingResourceException;
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
	private ArrayList<String> seriesLabels;
	private ArrayList<ArrayList<Double>> seriesValues;
	private ArrayList<String> limitsMaxLabels;
	private ArrayList<Double> limitsMaxValues;
	private ArrayList<String> limitsMinLabels;
	private ArrayList<Double> limitsMinValues;
	private String xAxisLabel;
	private ArrayList<String> xAxisValues;

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
		try {
			this.caption = TypesHelper.asString(
					this.dataContext.resolve(
							TypesHelper.asString(this.config.get(CONFIG_LABEL_CAPTION))
					)
			);
		}
		catch (FocusMissingResourceException ex) {
			this.caption = "";
		}
		catch (FocusBadTypeException e) {
			this.caption = "";
		}

		// x-axis
		try {
			Map xMap = (Map<String, Object>) this.config.get(CONFIG_LABEL_X_AXIS);
			this.xAxisLabel = TypesHelper.asString(
					this.dataContext.resolve(
							TypesHelper.asString(xMap.get(CONFIG_LABEL_LABEL))
					)
			);
			this.xAxisValues = TypesHelper.asArrayOfStrings(xMap.get(CONFIG_LABEL_VALUES));
			for (int i = 0; i < this.xAxisValues.size(); ++i) {
				this.xAxisValues.set(i, TypesHelper.asString(this.dataContext.resolve(this.xAxisValues.get(i))));
			}
		}
		catch (FocusMissingResourceException ex) {
			return;
		}
		catch (FocusBadTypeException e) {
			// not safe to continue without label or values for x-axis
			return;
		}

		// series and limits
		ArrayList<Map> series = (ArrayList<Map>) this.config.get(CONFIG_LABEL_SERIES);
		for (Map m : series) {

			String new_label;
			ArrayList<Double> values;

			try {
				new_label = TypesHelper.asString(
						this.dataContext.resolve(
								TypesHelper.asString(m.get(CONFIG_LABEL_LABEL))
						)
				);
			}
			catch (FocusMissingResourceException ex) {
				// ignore this serie
				continue;
			}
			catch (FocusBadTypeException e) {
				// ignore this serie
				continue;
			}

			Object values_tmp;
			try {
				String values_descr = TypesHelper.asString(m.get(CONFIG_LABEL_VALUES));
				values_tmp = this.dataContext.resolve(values_descr);
			}
			catch (FocusBadTypeException ex) {
				// good, this means we have an array of double and nothing to resolve()
				values_tmp = m.get(CONFIG_LABEL_VALUES);
			}
			catch (FocusMissingResourceException ex) {
				// cannot resolve, ignore series
				continue;
			}
			try {
				values = TypesHelper.asArrayOfDoubles(values_tmp);
			}
			catch (FocusBadTypeException ex) {
				// bad values, ignore series
				continue;
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
					try {
						label = TypesHelper.asString(this.dataContext.resolve(TypesHelper.asString(m2.get(CONFIG_LABEL_LABEL))));
						value = TypesHelper.asDouble(this.dataContext.resolve(TypesHelper.asString(m2.get(CONFIG_LABEL_VALUE))));
						type = TypesHelper.asString(m2.get(CONFIG_LABEL_LIMIT_TYPE));
					}
					catch (FocusMissingResourceException ex) {
						// ignore this limit as a whole
						continue;
					}
					catch (FocusBadTypeException e) {
						// ignore this limit as a whole
						continue;
					}

					if (type != null && type.equals("max")) {
						this.limitsMaxLabels.add(label);
						this.limitsMaxValues.add(value);
						++this.numberOfMaxLimits;
					}
					else {
						this.limitsMinLabels.add(label);
						this.limitsMinValues.add(value);
						++this.numberOfMinLimits;
					}
				}
			}

			this.seriesLabels.add(new_label);
			this.seriesValues.add(values);

			++this.numberOfSeries;
		}

	}


	public String getCaption()
	{
		return this.caption;
	}

	public String getxAxisLabel()
	{
		return this.xAxisLabel;
	}

	public ArrayList<String> getxAxisValues()
	{
		return this.xAxisValues;
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
		return this.seriesLabels.get(i);
	}

	public ArrayList<Double> getSerieValues(int i)
	{
		return this.seriesValues.get(i);
	}

	public String getMaxLimitLabel(int i)
	{
		return this.limitsMaxLabels.get(i);
	}

	public Double getMaxLimitValue(int i)
	{
		return this.limitsMaxValues.get(i);
	}

	public String getMinLimitLabel(int i)
	{
		return this.limitsMinLabels.get(i);
	}

	public Double getMinLimitValue(int i)
	{
		return this.limitsMinValues.get(i);
	}

}
