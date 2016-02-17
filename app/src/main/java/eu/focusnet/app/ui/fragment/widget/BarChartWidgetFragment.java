package eu.focusnet.app.ui.fragment.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.focusnet.app.R;

/**
 * Created by yandypiedra on 13.01.16.
 */
public class BarChartWidgetFragment extends WidgetFragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View viewRoot = inflater.inflate(R.layout.fragment_barchart, container, false);

		setWidgetLayout(viewRoot);

//        BarChartWidgetInstance barChartWidgetInstance = (BarChartWidgetInstance) getWidgetInstance();
//
//        TextView barChartTitle = (TextView) viewRoot.findViewById(R.id.text_bar_chart_title);
//        barChartTitle.setText(barChartWidgetInstance.getTitle());
//
//        BarChart barChart = (BarChart) viewRoot.findViewById(R.id.bar_chart);
//
//        barChart.setDrawBarShadow(false);
//        barChart.setDrawValueAboveBar(true);
//        barChart.setDescription("");
//
//        // if more than 40 entries are displayed in the chart, no values will be
//        // drawn
//        barChart.setMaxVisibleValueCount(40);
//
//        // scaling can now only be done on x- and y-axis separately
//        barChart.setPinchZoom(false);
//
//        // draw shadows for each bar that show the maximum value
//        barChart.setDrawBarShadow(true);
//
//        // barChart.setDrawXLabels(false);
//
//        barChart.setDrawGridBackground(false);
//        // barChart.setDrawYLabels(false);
//
//        // mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf")
//
//        XAxis xAxis = barChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setDrawGridLines(false);
//        xAxis.setSpaceBetweenLabels(2);
//
//        YAxis yAxis = barChart.getAxisLeft();
//        //leftAxis.setTypeface(mTf);
//        yAxis.setLabelCount(8, false);
//        //leftAxis.setValueFormatter(custom);
//        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
//        yAxis.setSpaceTop(15f);
//
//        barChart.animateY(2500);
//
//        yAxis.setStartAtZero(false);
//        float yAxisMinValue = -15f;
//        yAxis.setAxisMinValue(yAxisMinValue);
//
//        barChart.getAxisRight().setEnabled(false);
////        YAxis rightAxis = mChart.getAxisRight();
////        rightAxis.setDrawGridLines(false);
////       // rightAxis.setTypeface(mTf);
////        rightAxis.setLabelCount(8, false);
////        //rightAxis.setValueFormatter(custom);
////        rightAxis.setSpaceTop(15f);
//
//
//        ////x-axis
//        ArrayList<String> xVals = new ArrayList<>();
//        // y-axis values
//        ArrayList<BarEntry> yVals = new ArrayList<>();
//
//        // add colors
//        ArrayList<Integer> colors = new ArrayList<Integer>();
//
//        ArrayList<ChartData> chartDatas = barChartWidgetInstance.getData();
//
//        for(int i = 0; i < chartDatas.size(); i++) {
//            ChartData chartData = chartDatas.get(i);
//            xVals.add(chartData.getName());
//            yVals.add(new BarEntry(chartData.getValue(), i));
//            colors.add(chartData.getColor());
//        }
//
//        BarDataSet set1 = new BarDataSet(yVals, "Heat"); //TODO description
//        set1.setBarSpacePercent(35f);
//        set1.setColors(colors);
//
//        ArrayList<BarDataSet> dataSets = new ArrayList<>();
//        dataSets.add(set1);
//
//
//        BarData data = new BarData(xVals, dataSets);
//        // data.setValueFormatter(new MyValueFormatter());
//        data.setValueTextSize(10f);
//        //data.setValueTypeface(mTf);
//
//        barChart.setContext(data);
//
//        Legend legend = barChart.getLegend();
//        legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
//        legend.setForm(Legend.LegendForm.SQUARE);
//        legend.setFormSize(9f);
//        legend.setTextSize(11f);
//        legend.setXEntrySpace(4f);

		return viewRoot;
	}
}
