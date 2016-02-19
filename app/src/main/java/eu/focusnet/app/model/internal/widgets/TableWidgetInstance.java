package eu.focusnet.app.model.internal.widgets;

import java.util.ArrayList;
import java.util.Map;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.exception.FocusBadTypeException;
import eu.focusnet.app.model.json.WidgetTemplate;
import eu.focusnet.app.model.internal.DataContext;
import eu.focusnet.app.model.util.TypesHelper;

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
	protected void processConfig()
	{
		this.numberOfColumns = 0;
		this.maxNumberOfRows = 0;
		ArrayList<String> tmp_headers = new ArrayList<>();
		ArrayList<ArrayList<String>> tmp_values = new ArrayList<>();
		ArrayList<Map> map = (ArrayList<Map>) this.config.get(CONFIG_LABEL_COLUMNS);

		// try {
		for (Map m : map) {

			String header;
			ArrayList<String> values;

			try {
				header = TypesHelper.asString(m.get(CONFIG_LABEL_HEADER));
				values = TypesHelper.asArrayOfStrings(m.get(CONFIG_LABEL_VALUES));
			}
			catch (FocusBadTypeException e) {
				// ignore this column
				FocusApplication.reportError(e);
				continue;
			}

// FIXME TODO resolve data

			tmp_headers.add(header);
			tmp_values.add(values);
			this.maxNumberOfRows = (this.maxNumberOfRows >= values.size()) ? this.maxNumberOfRows : values.size();
			++this.numberOfColumns;
		}


		// transform the headers into the format that the TableWidgetFragment is expecting
		// an array of Strings
		this.headers = new String[tmp_headers.size()];
		this.headers = tmp_headers.toArray(this.headers);

		// transform the values into a format that the TableWidgetFragment is expecting
		// an array of array of Strings, i.e. one array per row of data
		// We must therefore transform our columns into rows
		this.values = new String[this.maxNumberOfRows][this.numberOfColumns];

		for (int i = 0; i < this.maxNumberOfRows; ++i) {
			for (int j = 0; j < this.numberOfColumns; ++j) {
				try {
					this.values[i][j] = tmp_values.get(j).get(i);
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
