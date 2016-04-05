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

package eu.focusnet.app.ui.fragment.widget;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import eu.focusnet.app.R;
import eu.focusnet.app.model.internal.widgets.LineChartWidgetInstance;

/**
 * Created by yandypiedra on 13.01.16.
 * <p/>
 * We may need to adapt these functions, though.
 *
 * NOTE: the caption is not rendered. We use this UI location to display the x-axis label instead.
 */
public class LineChartWidgetFragment extends WidgetFragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View viewRoot = inflater.inflate(R.layout.fragment_linechart, container, false);

		this.reference_height = 800;
		setWidgetLayout(viewRoot);

		this.widgetInstance = getWidgetInstance();

		// set widget title
		TextView lineChartTitle = (TextView) viewRoot.findViewById(R.id.text_title_line_chart);
		lineChartTitle.setText(this.widgetInstance.getTitle());

		LineChart lineChart = (LineChart) viewRoot.findViewById(R.id.line_chart);

		// Caption
		lineChart.setDescription(((LineChartWidgetInstance) this.widgetInstance).getxAxisLabel()); // we use the caption location to display the x-axis label

		// chart configuration
		lineChart.setDrawGridBackground(false);
		lineChart.setHighlightEnabled(true);
		lineChart.setTouchEnabled(true);
		lineChart.setDragEnabled(true);
		lineChart.setScaleEnabled(true);
		lineChart.setPinchZoom(true);
		lineChart.getAxisRight().setEnabled(false);

		// x-axis configuration
		XAxis x_axis = lineChart.getXAxis();
		x_axis.setPosition(XAxis.XAxisPosition.BOTTOM);
		x_axis.setSpaceBetweenLabels(1);
		ArrayList<String> x_vals = ((LineChartWidgetInstance) this.widgetInstance).getxAxisValues();

		// limits
		float yAxisMinValue = 0;
		float yAxisMaxValue = 0;

		int[] rainbow = ColorTemplate.COLORFUL_COLORS;
		int current_color = 0;

		// series
		ArrayList<LineDataSet> datasets = new ArrayList<>();
		for (int i = 0; i < ((LineChartWidgetInstance) this.widgetInstance).getNumberOfSeries(); ++i) {
			ArrayList<Double> values = ((LineChartWidgetInstance) this.widgetInstance).getSerieValues(i);
			ArrayList<Entry> vals = new ArrayList<>();
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
			LineDataSet set = new LineDataSet(vals, ((LineChartWidgetInstance) this.widgetInstance).getSerieLabel(i));

			set.enableDashedLine(10f, 5f, 0f);
			set.setColor(rainbow[current_color]);
			set.setCircleColor(rainbow[current_color]);
			set.setLineWidth(1f);
			set.setCircleSize(3f);
			set.setDrawCircleHole(false);
			set.setValueTextSize(9f);
			set.setFillAlpha(65);
			set.setFillColor(rainbow[current_color]);
			set.setDrawValues(false);
			datasets.add(set);

			current_color = (current_color + 1) % rainbow.length;
		}

		// Yaxis and limits
		YAxis leftAxis = lineChart.getAxisLeft();
		leftAxis.removeAllLimitLines();
		// leftAxis.setAxisMinValue(yAxisMinValue);
		// leftAxis.setAxisMaxValue(yAxisMaxValue);
		leftAxis.setYOffset(20);
		leftAxis.setStartAtZero(false);
		leftAxis.enableGridDashedLine(10f, 10f, 0f);
		leftAxis.setDrawLimitLinesBehindData(true);

		for (int i = 0; i < ((LineChartWidgetInstance) this.widgetInstance).getNumberOfMinLimits(); ++i) {
			LimitLine limitLineLowerYAxis = new LimitLine(((LineChartWidgetInstance) this.widgetInstance).getMinLimitValue(i).floatValue(), ((LineChartWidgetInstance) this.widgetInstance).getMinLimitLabel(i));
			limitLineLowerYAxis.setLineWidth(4f);
			limitLineLowerYAxis.setLineColor(getResources().getColor(R.color.green));
			limitLineLowerYAxis.enableDashedLine(10f, 10f, 0f);
			limitLineLowerYAxis.setTextSize(10f);
			leftAxis.addLimitLine(limitLineLowerYAxis);
		}

		for (int i = 0; i < ((LineChartWidgetInstance) this.widgetInstance).getNumberOfMaxLimits(); ++i) {
			LimitLine limitLineUpperYAxis = new LimitLine(((LineChartWidgetInstance) this.widgetInstance).getMaxLimitValue(i).floatValue(), ((LineChartWidgetInstance) this.widgetInstance).getMaxLimitLabel(i));
			limitLineUpperYAxis.setLineWidth(4f);
			limitLineUpperYAxis.setLineColor(getResources().getColor(R.color.red));
			limitLineUpperYAxis.enableDashedLine(10f, 10f, 0f);
			limitLineUpperYAxis.setTextSize(10f);
			leftAxis.addLimitLine(limitLineUpperYAxis);
		}

		// create a data object with the datasets
		LineData data = new LineData(x_vals, datasets);
		lineChart.setData(data);

		//	lineChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);

		// get the legend (only possible after setting data)
		Legend legend = lineChart.getLegend();

		// modify the legend ...
		legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
		legend.setForm(Legend.LegendForm.LINE);

		return viewRoot;
	}
}
