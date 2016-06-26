/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.model.widgets;

import java.util.ArrayList;
import java.util.Map;

import eu.focusnet.app.exception.FocusBadTypeException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.DataContext;
import eu.focusnet.app.model.TypesHelper;
import eu.focusnet.app.model.gson.WidgetTemplate;

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
	protected void processSpecificConfig()
	{
		this.caption = "";
		this.numberOfParts = 0;
		this.labels = new ArrayList<>();
		this.values = new ArrayList<>();

		Object caption = this.config.get(CONFIG_LABEL_CAPTION);
		if (caption == null) {
			this.caption = "";
		}

		try {
			this.caption = this.dataContext.resolveToString(caption);
		}
		catch (FocusMissingResourceException | FocusBadTypeException ex) {
			this.markAsInvalid();
			return;
		}

		ArrayList<Map> parts = (ArrayList<Map>) this.config.get(CONFIG_LABEL_PARTS);
		for (Map m : parts) {
			Double value;
			String label;
			try {
				Object valueRaw = m.get(CONFIG_LABEL_VALUE);
				if (valueRaw == null) {
					this.markAsInvalid();
					return;
				}
				value = this.dataContext.resolveToDouble(valueRaw);

				Object labelRaw = m.get(CONFIG_LABEL_LABEL);
				if (labelRaw == null) {
					label = "";
				}
				else {
					label = this.dataContext.resolveToString(labelRaw);
				}
			}
			catch (FocusMissingResourceException | FocusBadTypeException ex) {
				this.markAsInvalid();
				return;
			}

			this.labels.add(label);
			this.values.add(value);
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

	public ArrayList<String> getLabels()
	{
		return this.labels;
	}

	public ArrayList<Double> getValues()
	{
		return this.values;
	}

}
