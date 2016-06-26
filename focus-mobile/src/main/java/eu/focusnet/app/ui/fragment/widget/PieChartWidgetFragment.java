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

package eu.focusnet.app.ui.fragment.widget;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import eu.focusnet.app.R;
import eu.focusnet.app.model.widgets.PieChartWidgetInstance;
import eu.focusnet.app.ui.common.UiHelper;
import eu.focusnet.app.util.Constant;

/**
 * A {@code Fragment} rendering a Pie Chart widget, using MPAndroidChart
 */
public class PieChartWidgetFragment extends WidgetFragment
{

	/**
	 * The height of the widget if it takes the full width of the screen.
	 */
	private final static int HEIGHT_DP_FOR_FULL_WIDTH = 500;

	/**
	 * Create the view.
	 *
	 * @param inflater           Inherited
	 * @param container          Inherited
	 * @param savedInstanceState Inherited
	 * @return The new View
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// setup
		super.onCreate(savedInstanceState);
		this.setupWidget(inflater.inflate(R.layout.fragment_widget_piechart, container, false));

		ArrayList<Entry> yVals = new ArrayList<>();

		for (int i = 0; i < ((PieChartWidgetInstance) this.widgetInstance).getValues().size(); i++) {
			float d = ((PieChartWidgetInstance) this.widgetInstance).getValues().get(i).floatValue();
			yVals.add(new Entry(d, i));
		}

		PieDataSet dataSet = new PieDataSet(yVals, "");
		dataSet.setSliceSpace(3f);
		dataSet.setSelectionShift(5f);
		dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

		PieData data = new PieData(((PieChartWidgetInstance) this.widgetInstance).getLabels(), dataSet);
		data.setValueTextSize(11f);
		data.setValueTextColor(Color.WHITE);

		PieChart pieChart = (PieChart) this.rootView.findViewById(R.id.pie_chart);
		pieChart.setUsePercentValues(true);
		pieChart.setDescription(((PieChartWidgetInstance) this.widgetInstance).getCaption());
		pieChart.setDragDecelerationFrictionCoef(0.95f);
		pieChart.setDrawHoleEnabled(false);
		pieChart.setHoleRadius(58f);
		pieChart.setTransparentCircleColor(Color.WHITE);
		pieChart.setTransparentCircleAlpha(110);
		pieChart.setTransparentCircleRadius(61f);
		pieChart.setRotationAngle(0);
		pieChart.setRotationEnabled(false);
		pieChart.setDrawCenterText(false);
		pieChart.highlightValues(null);
		pieChart.invalidate();

		pieChart.setData(data);

		Legend legend = pieChart.getLegend();
		legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
		legend.setXEntrySpace(7f);
		legend.setYEntrySpace(0f);
		legend.setYOffset(0f);

		return this.rootView;
	}

	/**
	 * Alter the default height
	 */
	@Override
	protected void alterReferenceHeight()
	{
		int widthCols = this.widgetInstance.getNumberOfColumnsInUi();
		this.referenceHeight = UiHelper.dpToPixels(HEIGHT_DP_FOR_FULL_WIDTH * widthCols / Constant.Ui.LAYOUT_NUM_OF_COLUMNS, this.getActivity());
	}
}
