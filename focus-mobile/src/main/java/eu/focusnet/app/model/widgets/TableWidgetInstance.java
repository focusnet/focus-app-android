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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import eu.focusnet.app.model.DataContext;
import eu.focusnet.app.model.TypesHelper;
import eu.focusnet.app.model.gson.WidgetTemplate;
import eu.focusnet.app.util.FocusBadTypeException;
import eu.focusnet.app.util.FocusMissingResourceException;

/**
 * An instance containing all information pertaining to a Table widget.
 */
public class TableWidgetInstance extends WidgetInstance
{
	/**
	 * The configuration property for columns definitions
	 */
	final private static String CONFIG_LABEL_COLUMNS = "columns";

	/**
	 * The configuration property corresponding to header definition
	 */
	final private static String CONFIG_LABEL_HEADER = "header";

	/**
	 * The configuration property corresponding to values definition
	 */
	final private static String CONFIG_LABEL_VALUES = "values";

	/**
	 * The configuration property that defines if values are an epoch timestamp
	 *
	 * @deprecated quite clumsy, was last minute addition for PC3
	 */
	final private static String CONFIG_LABEL_IS_EPOCH = "epoch";

	/**
	 * Headers
	 */
	private String[] headers;

	/**
	 * Values ([rows][cols])
	 */
	private String[][] values;

	/**
	 * Number of columns
	 */
	private int numberOfColumns;

	/**
	 * Maximum number of rows
	 */
	private int maxNumberOfRows;

	/**
	 * Constructor.
	 *
	 * @param template     Inherited
	 * @param layoutConfig Inherited
	 * @param newCtx       Inherited
	 */
	public TableWidgetInstance(WidgetTemplate template, Map<String, String> layoutConfig, DataContext newCtx)
	{
		super(template, layoutConfig, newCtx);
	}

	/**
	 * Configuration of a table widget:
	 * - columns[]
	 * - columns[].header: ArrayList<String>
	 * - columns[].values: ArrayList<String>: values for each row
	 */
	@Override
	protected void processSpecificConfig()
	{
		this.numberOfColumns = 0;
		this.maxNumberOfRows = 0;
		ArrayList<String> tmpHeaders = new ArrayList<>();
		ArrayList<ArrayList<String>> tmpValues = new ArrayList<>();
		ArrayList<Map> map = (ArrayList<Map>) this.config.get(CONFIG_LABEL_COLUMNS);

		// try {
		for (Map m : map) {

			String header;
			ArrayList<String> values;

			Object rawHeader = m.get(CONFIG_LABEL_HEADER);
			Object rawValues = m.get(CONFIG_LABEL_VALUES);
			Object rawIsEpoch = m.get(CONFIG_LABEL_IS_EPOCH);

			boolean isEpoch = false;
			if (rawIsEpoch != null && rawIsEpoch instanceof Boolean && (Boolean) rawIsEpoch) {
				isEpoch = true;
			}

			if (rawHeader == null || rawValues == null) {
				this.markAsInvalid();
				return;
			}

			try {
				rawValues = this.dataContext.resolveToObject(rawValues);
			}
			catch (FocusBadTypeException e) {
				// ok to ignore, means that we are already in an array context
			}
			catch (FocusMissingResourceException e) {
				this.markAsInvalid();
				return;
			}

			try {
				values = TypesHelper.asArrayOfStrings(rawValues);
			}
			catch (FocusBadTypeException e) {
				this.markAsInvalid();
				return;
			}

			// resolve() header and values
			try {
				header = this.dataContext.resolveToString(rawHeader);
			}
			catch (FocusMissingResourceException | FocusBadTypeException ex) {
				this.markAsInvalid();
				return;
			}

			DateFormat dateFormat = DateFormat.getDateTimeInstance();
			for (int i = 0; i < values.size(); ++i) {
				try {
					String v = this.dataContext.resolveToString(values.get(i));
					if (isEpoch) {
						Double d = Double.parseDouble(v);
						long longVal = d.longValue() * 1_000;
						v = dateFormat.format(new Date(longVal));
					}
					else {
						v += " ";
					}
					values.set(i, v);
				}
				catch (FocusMissingResourceException | FocusBadTypeException ex) {
					this.markAsInvalid();
					return;
				}
			}

			tmpHeaders.add(header);
			tmpValues.add(values);

			this.maxNumberOfRows = (this.maxNumberOfRows >= values.size()) ? this.maxNumberOfRows : values.size();
			++this.numberOfColumns;
		}


		// transform the headers into the format that the TableWidgetFragment is expecting
		// an array of Strings
		this.headers = new String[tmpHeaders.size()];
		this.headers = tmpHeaders.toArray(this.headers);

		// transform the values into a format that the TableWidgetFragment is expecting
		// an array of array of Strings, i.e. one array per row of data
		// We must therefore transform our columns into rows
		this.values = new String[this.maxNumberOfRows][this.numberOfColumns];

		for (int i = 0; i < this.maxNumberOfRows; ++i) {
			for (int j = 0; j < this.numberOfColumns; ++j) {
				try {
					this.values[i][j] = tmpValues.get(j).get(i);
				}
				catch (IndexOutOfBoundsException e) {
					this.values[i][j] = "";
				}
			}
		}
	}

	/**
	 * Get the number of columns for this Table
	 *
	 * @return Number of columns
	 */
	public int getNumberOfColumns()
	{
		return this.numberOfColumns;
	}

	/**
	 * Return the number of rows for this Table
	 *
	 * @return Number of rows
	 */
	public int getNumberOfRows()
	{
		return this.maxNumberOfRows;
	}


	/**
	 * Return the header row of the Table
	 *
	 * @return Header row
	 */
	public String[] getTableHeaders()
	{
		return this.headers;
	}

	/**
	 * Return the data of the Table as an array of array of Strings. The first dimension contains
	 * the rows, the second dimension contains the columns
	 *
	 * @return Table data as a bi-dimensional array of Strings
	 */
	public String[][] getTableData()
	{
		return this.values;
	}
}
