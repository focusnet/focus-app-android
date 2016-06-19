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

package eu.focusnet.focus_mobile.ui.fragment.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.codecrafters.tableview.colorizers.TableDataRowColorizer;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowColorizers;
import eu.focusnet.focus_mobile.R;
import eu.focusnet.focus_mobile.model.internal.widgets.TableWidgetInstance;
import eu.focusnet.focus_mobile.ui.common.TouchTableView;
import eu.focusnet.focus_mobile.ui.util.UiHelper;

/**
 * Uses TouchTableView instead of TableView to overcome scrolling issues
 */
public class TableWidgetFragment extends WidgetFragment
{
	private final static int HEIGHT_DP_PER_ROW = 60;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// setup
		super.onCreate(savedInstanceState);
		this.setupWidget(inflater.inflate(R.layout.fragment_widget_table, container, false));

		TouchTableView tableView = (TouchTableView) this.rootView.findViewById(R.id.tableView);
		SimpleTableHeaderAdapter adapter = new SimpleTableHeaderAdapter(getActivity(), ((TableWidgetInstance) this.widgetInstance).getTableHeaders());
		adapter.setPaddingTop(25);
		adapter.setPaddingBottom(25);
		adapter.setTextColor(getResources().getColor(R.color.defaultTextColorNegative));
		tableView.setHeaderAdapter(adapter);
		tableView.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary));
		TableDataRowColorizer<Object> colorizer = TableDataRowColorizers.alternatingRows(
				getResources().getColor(R.color.defaultBackground),
				getResources().getColor(R.color.lightGreenForSelectedElement)
		);
		tableView.setDataRowColorizer(colorizer);
		tableView.setDataAdapter(new SimpleTableDataAdapter(getActivity(), ((TableWidgetInstance) this.widgetInstance).getTableData()));
		return this.rootView;
	}

	@Override
	protected void alterReferenceHeight()
	{
		int computed = UiHelper.dpToPixels((int) (1.1f + (float) ((TableWidgetInstance) this.widgetInstance).getNumberOfRows()) * HEIGHT_DP_PER_ROW, this.getActivity());
		int max = UiHelper.dpToPixels((int) this.getDisplayHeightInDp(), this.getActivity());
		this.referenceHeight = Math.min(computed, max);
	}


}
