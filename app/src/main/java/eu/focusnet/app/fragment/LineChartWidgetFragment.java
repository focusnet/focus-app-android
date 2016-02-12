package eu.focusnet.app.fragment;

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

import java.util.ArrayList;

import eu.focusnet.app.R;
import eu.focusnet.app.common.WidgetFragment;
import eu.focusnet.app.model.internal.LineChartWidgetInstance;
import eu.focusnet.app.model.ui.ChartData;

/**
 * Created by yandypiedra on 13.01.16.
 *
 * FIXME TODO use accessor functions that are in the LineChartWidgetInstance to create the UI.
 * We may need to adapt these functions, though.
 */
public class LineChartWidgetFragment extends WidgetFragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View viewRoot = inflater.inflate(R.layout.fragment_linechart, container, false);

		setWidgetLayout(viewRoot);

		LineChartWidgetInstance lineChartWidgetInstance = (LineChartWidgetInstance) getWidgetInstance();


		TextView lineChartTitle = (TextView) viewRoot.findViewById(R.id.text_title_line_chart);
		lineChartTitle.setText(lineChartWidgetInstance.getTitle());

		LineChart lineChart = (LineChart) viewRoot.findViewById(R.id.line_chart);

		float yAxisMinValue = -35;
		float yAxisMaxValue = 50;
		float limitLineLowerYAxisValue = -27f;
		String limitLineLowerYAxisText = "Lower Limit";
		float limitLineUpperYAxisValue = 37f;
		String limitLineUpperYAxisText = "Upper Limit";


		lineChart.setDrawGridBackground(false);

		// no description text
		lineChart.setDescription("");
		//lineChart.setNoDataTextDescription("You need to provide data for the chart.");

		// enable value highlighting
		lineChart.setHighlightEnabled(true);

		// enable touch gestures
		lineChart.setTouchEnabled(true);

		// enable scaling and dragging
		lineChart.setDragEnabled(true);
		lineChart.setScaleEnabled(true);
//        lineChart.setScaleXEnabled(true);
//         lineChart.setScaleYEnabled(true);

		// if disabled, scaling can be done on x- and y-axis separately
		lineChart.setPinchZoom(true);

		// set an alternative background color
		// mChart.setBackgroundColor(Color.GRAY);

//        // create a custom MarkerView (extend MarkerView) and specify the layout
//        // to use for it
//        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
//
//        // set the marker to the chart
//        lineChart.setMarkerView(mv);

		// x-axis limit line
//        LimitLine limitLineXAxis = new LimitLine(3f, "Index 10");
//        limitLineXAxis.setLineWidth(4f);
//        limitLineXAxis.enableDashedLine(10f, 10f, 0f);
//        // llXAxis.setLabelPosition(LimitLabelPosition.RIGHT_BOTTOM);
//        limitLineXAxis.setTextSize(10f);


		XAxis xAxis = lineChart.getXAxis();
		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
		//xAxis.setXValueFormatter(new MyCustomXValueFormatter());
		xAxis.setSpaceBetweenLabels(1);
//        xAxis.addLimitLine(limitLineXAxis); // add x-axis limit line

		LimitLine limitLineUpperYAxis = new LimitLine(limitLineUpperYAxisValue, limitLineUpperYAxisText);
		limitLineUpperYAxis.setLineWidth(4f);
		limitLineUpperYAxis.enableDashedLine(10f, 10f, 0f);
		//   ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
		limitLineUpperYAxis.setTextSize(10f);

		LimitLine limitLineLowerYAxis = new LimitLine(limitLineLowerYAxisValue, limitLineLowerYAxisText);
		limitLineLowerYAxis.setLineWidth(4f);
		limitLineLowerYAxis.enableDashedLine(10f, 10f, 0f);
		//   ll2.setLabelPosition(LimitLabelPosition.RIGHT_BOTTOM);
		limitLineLowerYAxis.setTextSize(10f);

		YAxis leftAxis = lineChart.getAxisLeft();
		leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
		leftAxis.addLimitLine(limitLineUpperYAxis);
		leftAxis.addLimitLine(limitLineLowerYAxis);
		leftAxis.setAxisMaxValue(yAxisMaxValue);
		leftAxis.setYOffset(10);
		leftAxis.setStartAtZero(false);
		leftAxis.setAxisMinValue(yAxisMinValue);
		leftAxis.enableGridDashedLine(10f, 10f, 0f);

		// limit lines are drawn behind data (and not on top)
		leftAxis.setDrawLimitLinesBehindData(true);

		lineChart.getAxisRight().setEnabled(false);

//        lineChart.getViewPortHandler().setMaximumScaleY(2f);
//        lineChart.getViewPortHandler().setMaximumScaleX(2f);

		ArrayList<ChartData> lineChartData = new ArrayList<ChartData>();// FIXME use available functions ... lineChartWidgetInstance.getData();

		//x-axis
		ArrayList<String> xVals = new ArrayList<>();
		// y-axis values
		ArrayList<Entry> yVals = new ArrayList<>();

		for (int i = 0; i < lineChartData.size(); i++) {
			ChartData chartData = lineChartData.get(i);
			xVals.add(chartData.getName());
			yVals.add(new BarEntry(chartData.getValue(), i));
		}

		// create a dataset and give it a type
		LineDataSet set1 = new LineDataSet(yVals, "Heat"); //TODO description
		// set1.setFillAlpha(110);
		// set1.setFillColor(Color.RED);

		// set the line to be drawn like this "- - - - - -"
		set1.enableDashedLine(10f, 5f, 0f);
		//  set1.enableDashedHighlightLine(10f, 5f, 0f);
		set1.setColor(Color.BLACK);
		set1.setCircleColor(Color.BLACK);
		set1.setLineWidth(1f);
		set1.setCircleSize(3f);
		set1.setDrawCircleHole(false);
		set1.setValueTextSize(9f);
		set1.setFillAlpha(65);
		set1.setFillColor(Color.BLACK);
//        set1.setDrawFilled(true);
		// set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
		// Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

		ArrayList<LineDataSet> dataSets = new ArrayList<>();
		dataSets.add(set1); // add the datasets

		// create a data object with the datasets
		LineData data = new LineData(xVals, dataSets);

		lineChart.setData(data);

		lineChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
//        mChart.invalidate();

		// get the legend (only possible after setting data)
		Legend legend1 = lineChart.getLegend();

		// modify the legend ...
		legend1.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
		legend1.setForm(Legend.LegendForm.LINE);

		return viewRoot;
	}
}
