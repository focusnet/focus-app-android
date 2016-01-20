package eu.focusnet.app.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import java.util.ArrayList;

import eu.focusnet.app.R;
import eu.focusnet.app.common.WidgetFragment;
import eu.focusnet.app.model.ui.ChartData;
import eu.focusnet.app.util.DataFactory;
import eu.focusnet.app.util.ViewFactory;

/**
 * Created by yandypiedra on 13.01.16.
 */
public class BarChartWidgetFragment extends WidgetFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View viewRoot = inflater.inflate(R.layout.fragment_barchart, container, false);

        //TODO uncomment this when the hardcode values are moved
        //setWidgetLayout(viewRoot);

        //TODO values hard coded
        viewRoot.getLayoutParams().height = 550;

        ArrayList<ChartData> chartDatas = new ArrayList<>(8);
        ChartData d = new ChartData("January", -10, Color.BLUE);
        chartDatas.add(d);
        d = new ChartData("February", -13,Color.BLUE);
        chartDatas.add(d);
        d = new ChartData("March", 10,Color.BLUE);
        chartDatas.add(d);
        d = new ChartData("April", 16, Color.BLUE);
        chartDatas.add(d);
        d = new ChartData("May", 20, Color.BLUE);
        chartDatas.add(d);
        d = new ChartData("June", 22, Color.RED);
        chartDatas.add(d);
        d = new ChartData("July", 28, Color.RED);
        chartDatas.add(d);
        d = new ChartData("August", 29, Color.RED);
        chartDatas.add(d);
        d = new ChartData("September", 25, Color.RED);
        chartDatas.add(d);
        d = new ChartData("October", 20, Color.BLUE);
        chartDatas.add(d);
        d = new ChartData("November", 10, Color.BLUE);
        chartDatas.add(d);
        d = new ChartData("December", 2, Color.BLUE);
        chartDatas.add(d);

        BarChart barChart = (BarChart) viewRoot.findViewById(R.id.bar_chart);

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setDescription("");

        // if more than 40 entries are displayed in the chart, no values will be
        // drawn
        barChart.setMaxVisibleValueCount(40);

        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false);

        // draw shadows for each bar that show the maximum value
        barChart.setDrawBarShadow(true);

        // barChart.setDrawXLabels(false);

        barChart.setDrawGridBackground(false);
        // barChart.setDrawYLabels(false);

        // mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf")

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);

        YAxis yAxis = barChart.getAxisLeft();
        //leftAxis.setTypeface(mTf);
        yAxis.setLabelCount(8, false);
        //leftAxis.setValueFormatter(custom);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setSpaceTop(15f);

        barChart.animateY(2500);

        yAxis.setStartAtZero(false);
        float yAxisMinValue = -15f;
        yAxis.setAxisMinValue(yAxisMinValue);

        barChart.getAxisRight().setEnabled(false);
//        YAxis rightAxis = mChart.getAxisRight();
//        rightAxis.setDrawGridLines(false);
//       // rightAxis.setTypeface(mTf);
//        rightAxis.setLabelCount(8, false);
//        //rightAxis.setValueFormatter(custom);
//        rightAxis.setSpaceTop(15f);

        barChart.setData(DataFactory.createBarData(chartDatas, "Heat"));

        Legend legend = barChart.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(9f);
        legend.setTextSize(11f);
        legend.setXEntrySpace(4f);

        return viewRoot;
    }
}
