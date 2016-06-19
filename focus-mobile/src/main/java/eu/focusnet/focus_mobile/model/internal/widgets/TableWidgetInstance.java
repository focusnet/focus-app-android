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

package eu.focusnet.focus_mobile.model.internal.widgets;

import java.util.ArrayList;
import java.util.Map;

import eu.focusnet.focus_mobile.exception.FocusBadTypeException;
import eu.focusnet.focus_mobile.exception.FocusMissingResourceException;
import eu.focusnet.focus_mobile.model.internal.DataContext;
import eu.focusnet.focus_mobile.model.json.WidgetTemplate;
import eu.focusnet.focus_mobile.model.util.TypesHelper;

/**
 * An instance containing all information pertaining to a Table widget.
 */
public class TableWidgetInstance extends WidgetInstance
{
	private final static String CONFIG_LABEL_COLUMNS = "columns";
	private final static String CONFIG_LABEL_HEADER = "header";
	private final static String CONFIG_LABEL_VALUES = "values";

	private String[] headers;
	private String[][] values;
	private int numberOfColumns;
	private int maxNumberOfRows;

	/**
	 * Constructor.
	 *
	 * @param template
	 * @param layoutConfig
	 * @param newCtx
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
			if (rawHeader == null || rawValues == null) {
				this.markAsInvalid();
				return;
			}
			try {
				header = TypesHelper.asString(rawHeader);
				values = TypesHelper.asArrayOfStrings(rawValues);
			}
			catch (FocusBadTypeException e) {
				this.markAsInvalid();
				return;
			}

			// resolve() header and values
			try {
				header = TypesHelper.asString(this.dataContext.resolve(header));
			}
			catch (FocusMissingResourceException | FocusBadTypeException ex) {
				this.markAsInvalid();
				return;
			}

			for (int i = 0; i < values.size(); ++i) {
				try {
					values.set(i, TypesHelper.asString(this.dataContext.resolve(values.get(i))));
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
	 * @return
	 */
	public int getNumberOfColumns()
	{
		return this.numberOfColumns;
	}

	/**
	 * Return the number of rows for this Table
	 *
	 * @return
	 */
	public int getNumberOfRows()
	{
		return this.maxNumberOfRows;
	}


	/**
	 * Return the header row of the Table
	 *
	 * @return
	 */
	public String[] getTableHeaders()
	{
		return this.headers;
	}

	/**
	 * Return the data of the Table as an array of array of Strings. The first dimension contains
	 * the rows, the second dimension contains the columns
	 *
	 * @return
	 */
	public String[][] getTableData()
	{
		return this.values;
	}
}
