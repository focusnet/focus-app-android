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

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.PercentFormatter;

import java.util.ArrayList;

import eu.focusnet.app.R;
import eu.focusnet.app.model.internal.widgets.PieChartWidgetInstance;

/**
 * Created by yandypiedra on 13.01.16.
 */
public class PieChartWidgetFragment extends WidgetFragment
{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View viewRoot = inflater.inflate(R.layout.fragment_piechart, container, false);

		setWidgetLayout(viewRoot);

		PieChartWidgetInstance pieChartWidgetInstance = (PieChartWidgetInstance) getWidgetInstance();

		TextView piechartTitle = (TextView) viewRoot.findViewById(R.id.text_piechart_title);
		piechartTitle.setText(pieChartWidgetInstance.getTitle());

		ArrayList<Entry> yVals = new ArrayList<>();

		for (int i = 0; i < pieChartWidgetInstance.getValues().size(); i++) {
			float d = pieChartWidgetInstance.getValues().get(i).floatValue();
			yVals.add(new Entry(d, i));
			// FIXME?	colors.register(chartData.getColor());
		}


		PieDataSet dataSet = new PieDataSet(yVals, pieChartWidgetInstance.getCaption());
		dataSet.setSliceSpace(3f);
		dataSet.setSelectionShift(5f);

		// FIXME?	dataSet.setColors(colors);

		PieData data = new PieData(pieChartWidgetInstance.getLabels(), dataSet); // names, values=new PieDataSet(values, legend)
		data.setValueFormatter(new PercentFormatter());
		data.setValueTextSize(11f);
		data.setValueTextColor(Color.WHITE);


		PieChart pieChart = (PieChart) viewRoot.findViewById(R.id.pie_chart);

		pieChart.setUsePercentValues(true);
		pieChart.setDescription("");
		pieChart.setDragDecelerationFrictionCoef(0.95f);
		pieChart.setDrawHoleEnabled(true);
		pieChart.setHoleColorTransparent(true);
		pieChart.setHoleRadius(58f);
		pieChart.setTransparentCircleColor(Color.WHITE);
		pieChart.setTransparentCircleAlpha(110);
		pieChart.setTransparentCircleRadius(61f);
		pieChart.setRotationAngle(0);
		// enable rotation of the chart by touch
		pieChart.setRotationEnabled(true);
		pieChart.setDrawCenterText(true);
		pieChart.setCenterText("This is the center text");
		// undo all highlights
		pieChart.highlightValues(null);
		pieChart.invalidate();
		pieChart.animateY(1500, Easing.EasingOption.EaseInOutQuad);

		pieChart.setData(data);

		Legend legend = pieChart.getLegend();
		legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
		legend.setXEntrySpace(7f);
		legend.setYEntrySpace(0f);
		legend.setYOffset(0f);

		return viewRoot;
	}
}
