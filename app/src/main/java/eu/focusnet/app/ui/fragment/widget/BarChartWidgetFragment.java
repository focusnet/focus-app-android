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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import eu.focusnet.app.R;
import eu.focusnet.app.model.internal.widgets.BarChartWidgetInstance;
import eu.focusnet.app.model.internal.widgets.WidgetInstance;
import eu.focusnet.app.ui.util.UiHelper;

/**
 * Created by yandypiedra on 13.01.16.
 */
public class BarChartWidgetFragment extends WidgetFragment
{
	final private static int HEIGHT_DP_FOR_FULL_WIDTH = 350;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// setup
		super.onCreate(savedInstanceState);
		this.setupWidget(inflater.inflate(R.layout.fragment_widget_barchart, container, false));

		BarChart mChart = (BarChart) this.rootView.findViewById(R.id.bar_chart);

		mChart.setDrawBarShadow(false);
		mChart.setDrawValueAboveBar(true);

		// We use the description location for displaying the x-axis label
		mChart.setDescription(((BarChartWidgetInstance) this.widgetInstance).getxAxisLabel());

		// if more than 60 entries are displayed in the chart, no values will be drawn
		mChart.setMaxVisibleValueCount(60);

		// scaling can now only be done on x- and y-axis separately
		mChart.setPinchZoom(false);
		mChart.setDrawGridBackground(true);

		XAxis xAxis = mChart.getXAxis();
		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
		xAxis.setDrawGridLines(false);
		xAxis.setSpaceBetweenLabels(2);

		YAxis leftAxis = mChart.getAxisLeft();
		leftAxis.setLabelCount(8, false);
		leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
		leftAxis.setSpaceTop(15f);
		leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

		mChart.getAxisRight().setEnabled(false);

		Legend l = mChart.getLegend();
		l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
		l.setForm(Legend.LegendForm.SQUARE);
		l.setFormSize(9f);
		l.setTextSize(11f);
		l.setXEntrySpace(4f);

		// x-axis configuration
		ArrayList<String> xVals = ((BarChartWidgetInstance) this.widgetInstance).getxAxisValues();

		// limits
		float yAxisMinValue = 0;
		float yAxisMaxValue = 0;

		int[] rainbow = ColorTemplate.COLORFUL_COLORS;
		int currentColor = 0;

		// series
		ArrayList<IBarDataSet> datasets = new ArrayList<>();
		for (int i = 0; i < ((BarChartWidgetInstance) this.widgetInstance).getNumberOfSeries(); ++i) {
			ArrayList<Double> values = ((BarChartWidgetInstance) this.widgetInstance).getSerieValues(i);
			ArrayList<BarEntry> vals = new ArrayList<>();
			for (int j = 0; j < values.size(); ++j) {
				// BarEntry(value, position on x axis)
				// we have made sure that the number of entries in xAxis and on values of series
				// are the same in the WidgetInstance implementation
				float v = values.get(j).floatValue();
				if (v < yAxisMinValue) {
					yAxisMinValue = v;
				}
				if (v > yAxisMaxValue) {
					yAxisMaxValue = v;
				}
				vals.add(new BarEntry(v, j));
			}
			BarDataSet set = new BarDataSet(vals, ((BarChartWidgetInstance) this.widgetInstance).getSerieLabel(i));

			set.setColor(rainbow[currentColor]);
			set.setValueTextSize(9f);
			set.setDrawValues(true);
			datasets.add(set);

			currentColor = (currentColor + 1) % rainbow.length;
		}


		// Yaxis and limits
		leftAxis.removeAllLimitLines();
		// leftAxis.setAxisMaxValue(yAxisMaxValue);
		// leftAxis.setAxisMinValue(yAxisMinValue);
		leftAxis.setYOffset(20);
		leftAxis.setStartAtZero(false);
		leftAxis.enableGridDashedLine(10f, 10f, 0f);
		leftAxis.setDrawLimitLinesBehindData(true);

		for (int i = 0; i < ((BarChartWidgetInstance) this.widgetInstance).getNumberOfMinLimits(); ++i) {
			LimitLine limitLineLowerYAxis = new LimitLine(((BarChartWidgetInstance) this.widgetInstance).getMinLimitValue(i).floatValue(), ((BarChartWidgetInstance) this.widgetInstance).getMinLimitLabel(i));
			limitLineLowerYAxis.setLineWidth(4f);
			limitLineLowerYAxis.setLineColor(getResources().getColor(R.color.green));
			limitLineLowerYAxis.enableDashedLine(10f, 10f, 0f);
			limitLineLowerYAxis.setTextSize(10f);
			leftAxis.addLimitLine(limitLineLowerYAxis);
		}

		for (int i = 0; i < ((BarChartWidgetInstance) this.widgetInstance).getNumberOfMaxLimits(); ++i) {
			LimitLine limitLineUpperYAxis = new LimitLine(((BarChartWidgetInstance) this.widgetInstance).getMaxLimitValue(i).floatValue(), ((BarChartWidgetInstance) this.widgetInstance).getMaxLimitLabel(i));
			limitLineUpperYAxis.setLineWidth(4f);
			limitLineUpperYAxis.setLineColor(getResources().getColor(R.color.red));
			limitLineUpperYAxis.enableDashedLine(10f, 10f, 0f);
			limitLineUpperYAxis.setTextSize(10f);
			leftAxis.addLimitLine(limitLineUpperYAxis);
		}

		// create a data object with the datasets
		BarData data = new BarData(xVals, datasets);
		mChart.setData(data);

		//	mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);

		return this.rootView;
	}

	@Override
	protected void alterReferenceHeight()
	{
		int widthCols = this.widgetInstance.getNumberOfColumnsInUi();
		this.referenceHeight = UiHelper.dpToPixels(HEIGHT_DP_FOR_FULL_WIDTH * widthCols / WidgetInstance.WIDGET_LAYOUT_TOTAL_NUMBER_OF_COLS, this.getActivity());
	}
}
