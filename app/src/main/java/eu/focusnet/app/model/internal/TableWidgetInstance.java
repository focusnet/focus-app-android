package eu.focusnet.app.model.internal;

import java.util.ArrayList;
import java.util.Map;

import eu.focusnet.app.model.focus.WidgetTemplate;
import eu.focusnet.app.util.TypesHelper;

/**
 * Created by julien on 20.01.16.
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
	void processConfig()
	{
		this.numberOfColumns = 0;
		this.maxNumberOfRows = 0;
		ArrayList<String> tmp_headers = new ArrayList<String>();
		ArrayList<ArrayList<String>> tmp_values = new ArrayList<ArrayList<String>>();

		try {
			for (Map m : (ArrayList<Map>) this.config.get(CONFIG_LABEL_COLUMNS)) {
				String header = TypesHelper.asString(m.get(CONFIG_LABEL_HEADER));
				ArrayList<String> values = TypesHelper.asArrayOfStrings(m.get(CONFIG_LABEL_VALUES));

// FIXME resolve data

				tmp_headers.add(header);
				tmp_values.add(values);
				this.maxNumberOfRows = (this.maxNumberOfRows >= values.size()) ? this.maxNumberOfRows : values.size();
				++this.numberOfColumns;
			}
		}
		catch (ClassCastException e) {
			// invalid casting -> bad.
			// FIXME FIXME report error.
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
