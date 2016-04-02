/**
 *
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
 *
 */

package eu.focusnet.app.ui.fragment.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowColorizers;
import eu.focusnet.app.R;
import eu.focusnet.app.model.internal.widgets.TableWidgetInstance;

/**
 * Created by yandypiedra on 13.01.16.
 */
public class TableWidgetFragment extends WidgetFragment
{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View viewRoot = inflater.inflate(R.layout.fragment_table, container, false);

		setWidgetLayout(viewRoot);

		this.widgetInstance = (TableWidgetInstance) getWidgetInstance();

		TableView tableView = (TableView) viewRoot.findViewById(R.id.tableView);
		SimpleTableHeaderAdapter adapter = new SimpleTableHeaderAdapter(getActivity(), ((TableWidgetInstance)this.widgetInstance).getTableHeaders());
		adapter.setPaddingTop(25);
		adapter.setPaddingBottom(25);
		adapter.setTextColor(getResources().getColor(R.color.table_header_text));
		tableView.setHeaderAdapter(adapter);
		tableView.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary));
		tableView.setDataAdapter(new SimpleTableDataAdapter(getActivity(), ((TableWidgetInstance)this.widgetInstance).getTableData()));
		int colorEvenRows = getResources().getColor(R.color.table_data_row_even);
		int colorOddRows = getResources().getColor(R.color.table_data_row_odd);
		tableView.setDataRowColoriser(TableDataRowColorizers.alternatingRows(colorEvenRows, colorOddRows));

		return viewRoot;
	}

}
